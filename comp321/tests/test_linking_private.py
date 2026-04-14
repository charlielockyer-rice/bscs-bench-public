"""
Tests for Project 3: Linking (readjcf)

Tests the following functionality:
- Reading Java Class File format
- Extracting dependencies (-d flag)
- Extracting exports (-e flag)
- Error handling for malformed class files

Test cases derived from grading feedback.
"""

import os
import struct
import sys
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, run_program, compile_c_program,
    create_temp_file, cleanup_temp_path
)

# Point values for each test
TEST_POINTS = {
    # Basic class structure tests
    "test_empty_class": 3,
    "test_single_field": 3,
    "test_single_method": 3,
    "test_single_interface_method": 3,
    "test_export_single_field": 3,
    "test_exporting_single_method": 3,
    "test_private_methods": 3,
    "test_protected_methods": 3,
    "test_depends": 3,
    "test_exports": 3,
    "test_no_flags": 3,
    # Data type tests
    "test_ints_and_floats": 3,
    "test_doubles_and_longs": 3,
    "test_empty_strings": 1,
    # Complex class tests
    "test_implement_interface": 2,
    "test_large_class": 2,
    # Error handling tests
    "test_empty_file": 1,
    "test_bad_magic": 1,
    "test_extra_bytes": 1,
    "test_truncated": 1,
    "test_pool_error": 1,
    "test_bad_tag": 1,
    "test_bad_indices": 1,
    "test_format_strings": 1,
    # Additional error handling tests
    "test_truncated_body": 1,
    "test_bad_tag_in_ref": 1,
    "test_bad_indices_out_of_range": 1,
}


# Java Class File constants
JCF_MAGIC = 0xCAFEBABE
CONSTANT_Class = 7
CONSTANT_Fieldref = 9
CONSTANT_Methodref = 10
CONSTANT_InterfaceMethodref = 11
CONSTANT_String = 8
CONSTANT_Integer = 3
CONSTANT_Float = 4
CONSTANT_Long = 5
CONSTANT_Double = 6
CONSTANT_NameAndType = 12
CONSTANT_Utf8 = 1
CONSTANT_MethodHandle = 15
CONSTANT_MethodType = 16
CONSTANT_InvokeDynamic = 18

ACC_PUBLIC = 0x0001
ACC_PRIVATE = 0x0002
ACC_PROTECTED = 0x0004


def create_minimal_class_file(
    class_name: str = "Test",
    fields: list = None,
    methods: list = None,
    dependencies: list = None,
) -> bytes:
    """
    Create a minimal valid Java class file.

    Args:
        class_name: Name of the class
        fields: List of (name, descriptor, access_flags) tuples
        methods: List of (name, descriptor, access_flags) tuples
        dependencies: List of (class, name, descriptor) tuples for refs

    Returns:
        Bytes representing a valid class file
    """
    fields = fields or []
    methods = methods or []
    dependencies = dependencies or []

    # Build constant pool
    cp = []

    # Add class name
    cp.append((CONSTANT_Utf8, class_name.encode()))
    class_name_idx = len(cp)

    # Add class constant
    cp.append((CONSTANT_Class, struct.pack(">H", class_name_idx)))
    this_class_idx = len(cp)

    # Add java/lang/Object as superclass
    cp.append((CONSTANT_Utf8, b"java/lang/Object"))
    object_name_idx = len(cp)
    cp.append((CONSTANT_Class, struct.pack(">H", object_name_idx)))
    super_class_idx = len(cp)

    # Add fields
    field_infos = []
    for fname, fdesc, faccess in fields:
        cp.append((CONSTANT_Utf8, fname.encode()))
        name_idx = len(cp)
        cp.append((CONSTANT_Utf8, fdesc.encode()))
        desc_idx = len(cp)
        field_infos.append((faccess, name_idx, desc_idx))

    # Add methods
    method_infos = []
    for mname, mdesc, maccess in methods:
        cp.append((CONSTANT_Utf8, mname.encode()))
        name_idx = len(cp)
        cp.append((CONSTANT_Utf8, mdesc.encode()))
        desc_idx = len(cp)
        method_infos.append((maccess, name_idx, desc_idx))

    # Add dependencies (references to other classes/methods)
    for dep_class, dep_name, dep_desc in dependencies:
        cp.append((CONSTANT_Utf8, dep_class.encode()))
        dep_class_name_idx = len(cp)
        cp.append((CONSTANT_Class, struct.pack(">H", dep_class_name_idx)))
        dep_class_idx = len(cp)

        cp.append((CONSTANT_Utf8, dep_name.encode()))
        dep_name_idx = len(cp)
        cp.append((CONSTANT_Utf8, dep_desc.encode()))
        dep_desc_idx = len(cp)

        cp.append((CONSTANT_NameAndType, struct.pack(">HH", dep_name_idx, dep_desc_idx)))
        name_and_type_idx = len(cp)

        cp.append((CONSTANT_Methodref, struct.pack(">HH", dep_class_idx, name_and_type_idx)))

    # Build class file bytes
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))

    # Version (Java 8 = 52.0)
    data.extend(struct.pack(">HH", 0, 52))

    # Constant pool count (1-indexed)
    data.extend(struct.pack(">H", len(cp) + 1))

    # Constant pool entries
    for tag, info in cp:
        data.append(tag)
        if tag == CONSTANT_Utf8:
            data.extend(struct.pack(">H", len(info)))
            data.extend(info)
        else:
            data.extend(info)

    # Access flags
    data.extend(struct.pack(">H", ACC_PUBLIC))

    # This class
    data.extend(struct.pack(">H", this_class_idx))

    # Super class
    data.extend(struct.pack(">H", super_class_idx))

    # Interfaces count
    data.extend(struct.pack(">H", 0))

    # Fields count and fields
    data.extend(struct.pack(">H", len(field_infos)))
    for faccess, name_idx, desc_idx in field_infos:
        data.extend(struct.pack(">HHHH", faccess, name_idx, desc_idx, 0))

    # Methods count and methods
    data.extend(struct.pack(">H", len(method_infos)))
    for maccess, name_idx, desc_idx in method_infos:
        data.extend(struct.pack(">HHHH", maccess, name_idx, desc_idx, 0))

    # Attributes count
    data.extend(struct.pack(">H", 0))

    return bytes(data)


def _run_readjcf_test(
    workspace: Path,
    class_file_data: bytes,
    flags: list = None,
    expected_output: str = None,
    expected_error: str = None,
) -> 'TestResult':
    """
    Run readjcf test with given class file data.

    Args:
        workspace: Path to workspace
        class_file_data: Raw bytes of class file
        flags: Command flags like ["-d", "-e"]
        expected_output: Expected stdout substring
        expected_error: Expected stderr substring (must be graceful, not crash)
    """
    executable = workspace / "readjcf" / "readjcf"

    # Compile if needed
    if not executable.exists():
        readjcf_dir = workspace / "readjcf"
        if readjcf_dir.exists():
            success, error = compile_c_program(readjcf_dir)
            if not success:
                return make_test_result(False, "compiled", f"compilation failed: {error}", None)
        else:
            return make_test_result(False, "compiled", "readjcf directory not found", None)

    # Create temp class file
    import tempfile
    fd, class_path = tempfile.mkstemp(suffix=".class")
    try:
        with os.fdopen(fd, 'wb') as f:
            f.write(class_file_data)

        # Run readjcf
        args = list(flags or []) + [class_path]
        ret_code, stdout, stderr = run_program(executable, args, timeout=30)

        # Check for expected error
        if expected_error:
            # For error handling tests, the implementation must:
            # 1. Print "ERROR" to stderr (graceful handling)
            # 2. Exit with non-zero code (1)
            # A crash (SIGSEGV=-11, SIGBUS=-10, etc.) is NOT proper error handling
            # and should be marked as FAIL

            # Check for crash (negative return code = killed by signal)
            if ret_code < 0:
                signal_num = -ret_code
                signal_names = {11: "SIGSEGV", 10: "SIGBUS", 6: "SIGABRT", 8: "SIGFPE"}
                signal_name = signal_names.get(signal_num, f"signal {signal_num}")
                return make_test_result(
                    False,
                    f"graceful error ({expected_error})",
                    f"CRASH: {signal_name} (exit code {ret_code})",
                    "Program crashed instead of handling error gracefully"
                )

            # Check for proper error message
            if expected_error in stderr and ret_code != 0:
                return make_test_result(True, expected_error, stderr.strip(), None)
            elif ret_code == 0:
                return make_test_result(
                    False,
                    f"{expected_error} with non-zero exit",
                    f"exit 0, stdout: {stdout.strip()[:100]}",
                    "Program should have detected error"
                )
            else:
                # Non-zero exit but wrong/missing error message
                return make_test_result(
                    False,
                    expected_error,
                    stderr.strip() or f"exit {ret_code} (no error message)",
                    None
                )

        # Check for expected output
        if expected_output:
            if expected_output in stdout:
                return make_test_result(True, expected_output, stdout.strip(), None)
            else:
                return make_test_result(False, expected_output, stdout.strip(), None)

        # Default: check for successful execution
        if ret_code == 0:
            return make_test_result(True, "exit 0", stdout.strip(), None)
        else:
            return make_test_result(False, "exit 0", f"exit {ret_code}", stderr)

    finally:
        os.unlink(class_path)


# ============================================================================
# Basic Class Structure Tests
# ============================================================================

def test_private_methods(workspace):
    """Test that private methods are not exported."""
    class_data = create_minimal_class_file(
        "PrivateMethods",
        methods=[
            ("publicMethod", "()V", ACC_PUBLIC),
            ("privateMethod", "()V", ACC_PRIVATE),
        ]
    )
    result = _run_readjcf_test(workspace, class_data, ["-e"])

    # Should export public but not private
    if result.passed:
        if "privateMethod" in result.actual:
            return make_test_result(False, "no private exports", result.actual, None)
    return result

test_private_methods.input_description = "class with private methods"


def test_protected_methods(workspace):
    """Test that protected methods are not exported."""
    class_data = create_minimal_class_file(
        "ProtectedMethods",
        methods=[
            ("publicMethod", "()V", ACC_PUBLIC),
            ("protectedMethod", "()V", ACC_PROTECTED),
        ]
    )
    result = _run_readjcf_test(workspace, class_data, ["-e"])

    if result.passed:
        if "protectedMethod" in result.actual:
            return make_test_result(False, "no protected exports", result.actual, None)
    return result

test_protected_methods.input_description = "class with protected methods"


def test_depends(workspace):
    """Test dependency extraction."""
    class_data = create_minimal_class_file(
        "Depends",
        dependencies=[
            ("java/lang/Object", "equals", "(Ljava/lang/Object;)Z"),
            ("java/lang/Object", "hashCode", "()I"),
        ]
    )
    return _run_readjcf_test(workspace, class_data, ["-d"], "Dependency")

test_depends.input_description = "class with dependencies"


def test_exports(workspace):
    """Test export extraction with multiple exports."""
    class_data = create_minimal_class_file(
        "Exports",
        methods=[
            ("toString", "()Ljava/lang/String;", ACC_PUBLIC),
            ("equals", "(Ljava/lang/Object;)Z", ACC_PUBLIC),
            ("hashCode", "()I", ACC_PUBLIC),
        ]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"], "Export")

test_exports.input_description = "class with multiple exports"


def test_no_flags(workspace):
    """Test running without -d or -e flags."""
    class_data = create_minimal_class_file("NoFlags")
    return _run_readjcf_test(workspace, class_data, [])

test_no_flags.input_description = "run without flags"


# ============================================================================
# Data Type Tests
# ============================================================================

def test_ints_and_floats(workspace):
    """Test class with int and float fields."""
    class_data = create_minimal_class_file(
        "IntsAndFloats",
        fields=[
            ("intField", "I", ACC_PUBLIC),
            ("floatField", "F", ACC_PUBLIC),
        ]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"])

test_ints_and_floats.input_description = "class with int and float fields"


def test_doubles_and_longs(workspace):
    """Test class with double and long fields."""
    class_data = create_minimal_class_file(
        "DoublesAndLongs",
        fields=[
            ("doubleField", "D", ACC_PUBLIC),
            ("longField", "J", ACC_PUBLIC),
        ]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"])

test_doubles_and_longs.input_description = "class with double and long fields"


def test_empty_strings(workspace):
    """Test class with empty string field."""
    class_data = create_minimal_class_file(
        "EmptyStrings",
        fields=[("emptyStr", "Ljava/lang/String;", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"])

test_empty_strings.input_description = "class with empty string handling"


# ============================================================================
# Complex Class Tests
# ============================================================================

def test_implement_interface(workspace):
    """Test class that implements an interface."""
    # This would require more complex class file generation
    class_data = create_minimal_class_file(
        "ImplementsRunnable",
        methods=[("run", "()V", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-d", "-e"])

test_implement_interface.input_description = "class implementing interface"


def test_large_class(workspace):
    """Test class with many fields and methods."""
    fields = [(f"field{i}", "I", ACC_PUBLIC) for i in range(20)]
    methods = [(f"method{i}", "()V", ACC_PUBLIC) for i in range(20)]
    class_data = create_minimal_class_file("LargeClass", fields, methods)
    return _run_readjcf_test(workspace, class_data, ["-e"])

test_large_class.input_description = "class with many fields and methods"


# ============================================================================
# Error Handling Tests
# ============================================================================

def test_empty_file(workspace):
    """Test handling of empty file."""
    return _run_readjcf_test(workspace, b"", ["-d", "-e"], expected_error="ERROR")

test_empty_file.input_description = "empty class file"


def test_bad_magic(workspace):
    """Test handling of incorrect magic number."""
    bad_data = struct.pack(">I", 0xDEADBEEF) + b"\x00" * 100
    return _run_readjcf_test(workspace, bad_data, ["-d", "-e"], expected_error="ERROR")

test_bad_magic.input_description = "incorrect magic number"


def test_extra_bytes(workspace):
    """Test handling of extra bytes at end of file."""
    class_data = create_minimal_class_file("Extra")
    class_data_with_extra = class_data + b"\xFF\xFF\xFF\xFF"
    return _run_readjcf_test(workspace, class_data_with_extra, ["-d", "-e"], expected_error="ERROR")

test_extra_bytes.input_description = "extra bytes after class data"


def test_truncated(workspace):
    """Test handling of truncated file - truncate in middle of constant pool."""
    # Create a class file that truncates mid-read of a constant
    # This should fail during process_jcf_constant_pool
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))
    # Constant pool count = 5 (means 4 entries)
    data.extend(struct.pack(">H", 5))

    # Entry 1: Start a UTF8 with length 100 but only give 10 bytes
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 100))  # Says 100 bytes follow
    data.extend(b"truncated!")  # Only 10 bytes - will fail reading rest

    return _run_readjcf_test(workspace, bytes(data), ["-d", "-e"], expected_error="ERROR")

test_truncated.input_description = "truncated in middle of UTF8 constant"


def test_pool_error(workspace):
    """Test handling of invalid constant pool."""
    # Create file with valid magic but invalid constant pool count
    bad_data = struct.pack(">I", JCF_MAGIC)  # Magic
    bad_data += struct.pack(">HH", 0, 52)  # Version
    bad_data += struct.pack(">H", 1000)  # Invalid pool count
    return _run_readjcf_test(workspace, bad_data, ["-d", "-e"], expected_error="ERROR")

test_pool_error.input_description = "invalid constant pool"


def test_bad_tag(workspace):
    """Test handling of invalid constant pool tag."""
    data = bytearray()
    # Magic
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))
    # Pool count = 3 (two entries expected)
    data.extend(struct.pack(">H", 3))
    # Entry 1: Valid UTF8
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 4))
    data.extend(b"Test")
    # Entry 2: Invalid tag (99 is not a valid constant tag)
    data.append(99)
    data.extend(b"\x00\x00\x00\x00")  # Some padding

    return _run_readjcf_test(workspace, bytes(data), ["-d", "-e"], expected_error="ERROR")

test_bad_tag.input_description = "invalid constant pool tag"


def test_bad_indices(workspace):
    """
    Test handling of invalid constant pool indices.

    This creates a Methodref that references index 0 in the constant pool.
    Index 0 is never initialized (constant pool entries start at 1),
    so accessing pool[0]->tag will dereference NULL and crash.
    """
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))

    # Constant pool count = 6 (5 entries)
    data.extend(struct.pack(">H", 6))

    # Entry 1: UTF8 "Test"
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 4))
    data.extend(b"Test")

    # Entry 2: Class pointing to entry 1 (valid)
    data.append(CONSTANT_Class)
    data.extend(struct.pack(">H", 1))

    # Entry 3: UTF8 "foo"
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 3))
    data.extend(b"foo")

    # Entry 4: NameAndType pointing to entry 3 and 3
    data.append(CONSTANT_NameAndType)
    data.extend(struct.pack(">HH", 3, 3))

    # Entry 5: Methodref with class_index=0 (INVALID - index 0 is never initialized!)
    # When -d flag is used, this will try to print pool[0]->tag which is NULL
    data.append(CONSTANT_Methodref)
    data.extend(struct.pack(">HH", 0, 4))  # class_index=0 is invalid!

    # Rest of the class file structure
    # Access flags
    data.extend(struct.pack(">H", ACC_PUBLIC))
    # this_class (index 2)
    data.extend(struct.pack(">H", 2))
    # super_class (index 2)
    data.extend(struct.pack(">H", 2))
    # Interfaces count
    data.extend(struct.pack(">H", 0))
    # Fields count
    data.extend(struct.pack(">H", 0))
    # Methods count
    data.extend(struct.pack(">H", 0))
    # Attributes count
    data.extend(struct.pack(">H", 0))

    return _run_readjcf_test(workspace, bytes(data), ["-d"], expected_error="ERROR")

test_bad_indices.input_description = "Methodref with index 0 (uninitialized)"


def test_format_strings(workspace):
    """Test proper handling of format strings in output."""
    # Class name with format string characters
    class_data = create_minimal_class_file("Test%s%n")
    return _run_readjcf_test(workspace, class_data, ["-d", "-e"])

test_format_strings.input_description = "format string characters in names"


# ============================================================================
# Additional Error Handling Tests (to catch edge cases)
# ============================================================================

def test_truncated_body(workspace):
    """Test handling of truncated file in class body section."""
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))
    # Constant pool count = 3 (two entries)
    data.extend(struct.pack(">H", 3))

    # Entry 1: UTF8 "Test"
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 4))
    data.extend(b"Test")

    # Entry 2: Class pointing to entry 1
    data.append(CONSTANT_Class)
    data.extend(struct.pack(">H", 1))

    # Truncate here - no body, interfaces, fields, methods, or attributes
    # This should fail when trying to read access_flags

    return _run_readjcf_test(workspace, bytes(data), ["-d", "-e"], expected_error="ERROR")

test_truncated_body.input_description = "truncated before class body"


def test_bad_tag_in_ref(workspace):
    """Test handling of Methodref pointing to wrong tag type."""
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))
    # Constant pool count = 5 (four entries)
    data.extend(struct.pack(">H", 5))

    # Entry 1: UTF8 "Test"
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 4))
    data.extend(b"Test")

    # Entry 2: Class pointing to entry 1
    data.append(CONSTANT_Class)
    data.extend(struct.pack(">H", 1))

    # Entry 3: NameAndType (name=1, descriptor=1)
    data.append(CONSTANT_NameAndType)
    data.extend(struct.pack(">HH", 1, 1))

    # Entry 4: Methodref - but class_index points to UTF8 instead of Class!
    data.append(CONSTANT_Methodref)
    data.extend(struct.pack(">HH", 1, 3))  # index 1 is UTF8, not Class

    # Rest of class file
    data.extend(struct.pack(">H", ACC_PUBLIC))  # access flags
    data.extend(struct.pack(">H", 2))  # this_class
    data.extend(struct.pack(">H", 2))  # super_class
    data.extend(struct.pack(">H", 0))  # interfaces count
    data.extend(struct.pack(">H", 0))  # fields count
    data.extend(struct.pack(">H", 0))  # methods count
    data.extend(struct.pack(">H", 0))  # attributes count

    return _run_readjcf_test(workspace, bytes(data), ["-d"], expected_error="ERROR")

test_bad_tag_in_ref.input_description = "Methodref.class_index points to UTF8 not Class"


def test_bad_indices_out_of_range(workspace):
    """Test handling of constant pool index beyond pool size."""
    data = bytearray()

    # Magic number
    data.extend(struct.pack(">I", JCF_MAGIC))
    # Version
    data.extend(struct.pack(">HH", 0, 52))
    # Constant pool count = 5 (four entries)
    data.extend(struct.pack(">H", 5))

    # Entry 1: UTF8 "Test"
    data.append(CONSTANT_Utf8)
    data.extend(struct.pack(">H", 4))
    data.extend(b"Test")

    # Entry 2: Class pointing to entry 1
    data.append(CONSTANT_Class)
    data.extend(struct.pack(">H", 1))

    # Entry 3: NameAndType
    data.append(CONSTANT_NameAndType)
    data.extend(struct.pack(">HH", 1, 1))

    # Entry 4: Methodref with class_index=100 (way out of bounds)
    data.append(CONSTANT_Methodref)
    data.extend(struct.pack(">HH", 100, 3))

    # Rest of class file
    data.extend(struct.pack(">H", ACC_PUBLIC))
    data.extend(struct.pack(">H", 2))
    data.extend(struct.pack(">H", 2))
    data.extend(struct.pack(">H", 0))
    data.extend(struct.pack(">H", 0))
    data.extend(struct.pack(">H", 0))
    data.extend(struct.pack(">H", 0))

    return _run_readjcf_test(workspace, bytes(data), ["-d"], expected_error="ERROR")

test_bad_indices_out_of_range.input_description = "Methodref with index beyond pool size"

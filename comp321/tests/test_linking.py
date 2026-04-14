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

def test_empty_class(workspace):
    """Test reading a minimal empty class."""
    class_data = create_minimal_class_file("EmptyClass")
    return _run_readjcf_test(workspace, class_data, ["-d", "-e"])

test_empty_class.input_description = "minimal empty class file"


def test_single_field(workspace):
    """Test class with a single public field."""
    class_data = create_minimal_class_file(
        "SingleField",
        fields=[("value", "I", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-d", "-e"], "Export - value I")

test_single_field.input_description = "class with one public field"


def test_single_method(workspace):
    """Test class with a single public method."""
    class_data = create_minimal_class_file(
        "SingleMethod",
        methods=[("getValue", "()I", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-d", "-e"], "Export - getValue ()I")

test_single_method.input_description = "class with one public method"


def test_single_interface_method(workspace):
    """Test class with interface method reference."""
    class_data = create_minimal_class_file(
        "SingleInterfaceMethod",
        dependencies=[("java/lang/Runnable", "run", "()V")]
    )
    return _run_readjcf_test(workspace, class_data, ["-d"], "Dependency")

test_single_interface_method.input_description = "class with interface method ref"


def test_export_single_field(workspace):
    """Test exporting a single field."""
    class_data = create_minimal_class_file(
        "ExportField",
        fields=[("data", "Ljava/lang/String;", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"], "Export - data")

test_export_single_field.input_description = "export single field"


def test_exporting_single_method(workspace):
    """Test exporting a single method."""
    class_data = create_minimal_class_file(
        "ExportMethod",
        methods=[("process", "(Ljava/lang/String;)V", ACC_PUBLIC)]
    )
    return _run_readjcf_test(workspace, class_data, ["-e"], "Export - process")

test_exporting_single_method.input_description = "export single method"



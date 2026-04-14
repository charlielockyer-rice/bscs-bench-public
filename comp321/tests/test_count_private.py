"""
Tests for Project 2: Word Count

Tests the following functionality:
- Character counting (-c flag)
- Word counting (-w flag)
- Line counting (-l flag)
- Multiple file processing with ASCIIbetical sorting
- Error handling for missing/unreadable files

Test cases derived from grading feedback.
"""

import os
import sys
import tempfile
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, run_program, compile_c_program,
    create_temp_file, cleanup_temp_path, count_words_standard
)

# Point values for each test
TEST_POINTS = {
    # File type tests
    "test_blankfile_small": 2,
    "test_blankfile_large": 2,
    "test_alphanumeric_only": 2,
    "test_special_char_only": 2,
    "test_special_char_with_spaces": 2,
    "test_huge_line": 2,
    "test_long_misspelled_word": 2,
    "test_lots_of_unique_misspelled": 2,
    "test_small_dict_big_input": 2,
    "test_large_dict": 2,
    "test_lotsa_misspelled": 2,
    "test_emptyfile": 2,
    # Error handling tests
    "test_read_not_allowed": 2,
    "test_file_does_not_exist": 2,
    # Sorting tests
    "test_sort_test": 3,
    "test_sort_test_with_error": 3,
    "test_sort_test_only_errors": 3,
    # Flag combination tests
    "test_args_c": 0.5,
    "test_args_l": 0.5,
    "test_args_w": 0.5,
    "test_args_cw": 0.5,
    "test_args_cl": 0.5,
    "test_args_wl": 0.5,
    "test_args_cwl": 0.5,
    # Numbered tests
    "test_1": 2,
    "test_2": 2,
    "test_3": 2,
    "test_4": 2,
    "test_5": 2,
    "test_6": 2,
    "test_7": 2,
    "test_8": 2,
    "test_9": 2,
    # Multiple file test
    "test_open_files": 3,
}


class CountTestContext:
    """Context manager for count tests with temporary files."""

    def __init__(self, workspace_path: Path):
        self.workspace_path = workspace_path
        self.temp_files = []
        self.executable = workspace_path / "count"

    def __enter__(self):
        # Compile if needed
        if not self.executable.exists():
            success, error = compile_c_program(self.workspace_path)
            if not success:
                raise RuntimeError(f"Compilation failed: {error}")
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        # Cleanup temp files
        for tf in self.temp_files:
            cleanup_temp_path(tf)

    def create_file(self, content: str, name: str = None) -> Path:
        """Create a temporary test file."""
        if name:
            path = self.workspace_path / name
            with open(path, 'w') as f:
                f.write(content)
        else:
            path = create_temp_file(content)
        self.temp_files.append(path)
        return path

    def run_count(self, files: list, flags: list = None) -> tuple:
        """Run count program with given files and flags."""
        args = []
        if flags:
            args.extend(flags)
        args.extend([str(f) for f in files])

        return run_program(self.executable, args, timeout=60)


def _parse_count_output(output: str) -> dict:
    """
    Parse count output into structured format.

    Returns dict mapping filename (basename) to counts list.
    Also returns original order of filenames for sorting check.
    """
    import os
    results = {}
    order = []  # Track original order for sorting check
    for line in output.strip().split('\n'):
        if not line.strip():
            continue
        parts = line.split()
        if len(parts) >= 2:
            # Last part is filename (may be full path), rest are counts
            filepath = parts[-1]
            filename = os.path.basename(filepath)
            counts = [int(p) for p in parts[:-1] if p.lstrip('-').isdigit()]
            results[filename] = counts
            if filename != "total":
                order.append(filename)
    results['_order'] = order
    return results


def _run_count_test(
    workspace: Path,
    file_contents: dict,  # {filename: content}
    flags: list = None,
    expected_counts: dict = None,  # {filename: [lines, words, chars]}
    expected_error: str = None,
    check_sorting: bool = True
) -> 'TestResult':
    """
    Run a count test with given parameters.

    Args:
        workspace: Path to workspace
        file_contents: Dict mapping filename to content
        flags: Command flags like ["-c", "-w", "-l"]
        expected_counts: Expected output counts per file
        expected_error: Expected error message substring
        check_sorting: Whether to verify ASCIIbetical sorting
    """
    try:
        with CountTestContext(workspace) as ctx:
            # Create test files
            files = []
            for name, content in file_contents.items():
                path = ctx.create_file(content, name)
                files.append(path)

            # Run count
            ret_code, stdout, stderr = ctx.run_count(files, flags)

            # Check for expected error
            if expected_error:
                if expected_error in stderr:
                    return make_test_result(True, expected_error, stderr.strip(), None)
                else:
                    return make_test_result(False, expected_error, stderr.strip(), None)

            # Parse output
            results = _parse_count_output(stdout)

            # Verify counts if provided
            if expected_counts:
                for filename, expected in expected_counts.items():
                    actual = results.get(filename, [])
                    if list(actual) != list(expected):
                        return make_test_result(
                            False,
                            f"{filename}: {expected}",
                            f"{filename}: {actual}",
                            None
                        )

            # Verify ASCIIbetical sorting
            if check_sorting:
                filenames = results.get('_order', [])
                if len(filenames) > 1 and filenames != sorted(filenames):
                    return make_test_result(
                        False,
                        f"sorted: {sorted(filenames)}",
                        f"actual: {filenames}",
                        "Output not in ASCIIbetical order"
                    )

            return make_test_result(True, "correct output", stdout.strip(), None)

    except RuntimeError as e:
        return make_test_result(False, "compiled", str(e), None)


# ============================================================================
# File Type Tests
# ============================================================================

def test_lots_of_unique_misspelled(workspace):
    """Test file with many unique words."""
    words = [f"unique{i:05d}" for i in range(1000)]
    content = "\n".join(words) + "\n"
    return _run_count_test(
        workspace,
        {"unique_words.txt": content},
        ["-c", "-w", "-l"]
    )

test_lots_of_unique_misspelled.input_description = "1000 unique words"


def test_small_dict_big_input(workspace):
    """Test large input file."""
    content = "the quick brown fox jumps over the lazy dog\n" * 1000
    return _run_count_test(
        workspace,
        {"big_input.txt": content},
        ["-c", "-w", "-l"]
    )

test_small_dict_big_input.input_description = "1000 repeated lines"


def test_large_dict(workspace):
    """Test file simulating dictionary-like content."""
    words = [f"word{i:06d}" for i in range(5000)]
    content = "\n".join(words) + "\n"
    return _run_count_test(
        workspace,
        {"large_dict.txt": content},
        ["-c", "-w", "-l"]
    )

test_large_dict.input_description = "5000 dictionary entries"


def test_lotsa_misspelled(workspace):
    """Test file with many repeated words."""
    content = ("misspelled " * 100 + "\n") * 100
    return _run_count_test(
        workspace,
        {"lotsa.txt": content},
        ["-c", "-w", "-l"]
    )

test_lotsa_misspelled.input_description = "10000 repeated words"


def test_emptyfile(workspace):
    """Test empty file."""
    return _run_count_test(
        workspace,
        {"empty.txt": ""},
        ["-c", "-w", "-l"],
        {"empty.txt": [0, 0, 0]}
    )

test_emptyfile.input_description = "empty file"


# ============================================================================
# Error Handling Tests
# ============================================================================

def test_read_not_allowed(workspace):
    """Test file without read permission."""
    try:
        with CountTestContext(workspace) as ctx:
            # Create file and remove read permission
            path = ctx.create_file("test content\n", "noread.txt")
            os.chmod(path, 0o000)

            ret_code, stdout, stderr = ctx.run_count([path], ["-c", "-w", "-l"])

            # Restore permissions for cleanup
            os.chmod(path, 0o644)

            if "ERROR" in stderr and "cannot open" in stderr:
                return make_test_result(True, "error message", stderr.strip(), None)
            else:
                return make_test_result(
                    False,
                    "ERROR: cannot open file",
                    stderr.strip() or stdout.strip(),
                    None
                )
    except RuntimeError as e:
        return make_test_result(False, "compiled", str(e), None)

test_read_not_allowed.input_description = "file without read permission"


def test_file_does_not_exist(workspace):
    """Test non-existent file."""
    try:
        with CountTestContext(workspace) as ctx:
            ret_code, stdout, stderr = ctx.run_count(
                ["/nonexistent/path/file.txt"],
                ["-c", "-w", "-l"]
            )

            if "ERROR" in stderr and "cannot open" in stderr:
                return make_test_result(True, "error message", stderr.strip(), None)
            else:
                return make_test_result(
                    False,
                    "ERROR: cannot open file",
                    stderr.strip() or stdout.strip(),
                    None
                )
    except RuntimeError as e:
        return make_test_result(False, "compiled", str(e), None)

test_file_does_not_exist.input_description = "non-existent file"


# ============================================================================
# Sorting Tests
# ============================================================================

def test_sort_test(workspace):
    """Test ASCIIbetical sorting of output."""
    return _run_count_test(
        workspace,
        {
            "zebra.txt": "one\n",
            "apple.txt": "two\n",
            "Banana.txt": "three\n",  # Capital B comes before lowercase
        },
        ["-c", "-w", "-l"],
        check_sorting=True
    )

test_sort_test.input_description = "three files requiring sorting"


def test_sort_test_with_error(workspace):
    """Test sorting with one file causing an error."""
    try:
        with CountTestContext(workspace) as ctx:
            f1 = ctx.create_file("content one\n", "aaa.txt")
            f2 = ctx.create_file("content two\n", "zzz.txt")

            # Include a non-existent file
            ret_code, stdout, stderr = ctx.run_count(
                [f1, "/nonexistent.txt", f2],
                ["-c", "-w", "-l"]
            )

            # Check that valid files are still processed
            if "aaa.txt" in stdout and "zzz.txt" in stdout:
                return make_test_result(True, "valid files processed", stdout.strip(), None)
            else:
                return make_test_result(False, "both valid files in output", stdout.strip(), None)

    except RuntimeError as e:
        return make_test_result(False, "compiled", str(e), None)

test_sort_test_with_error.input_description = "mixed valid and invalid files"


def test_sort_test_only_errors(workspace):
    """Test when all files cause errors."""
    try:
        with CountTestContext(workspace) as ctx:
            ret_code, stdout, stderr = ctx.run_count(
                ["/nonexistent1.txt", "/nonexistent2.txt", "/nonexistent3.txt"],
                ["-c", "-w", "-l"]
            )

            # Should still print total line with zeros
            if "total" in stdout:
                return make_test_result(True, "total line present", stdout.strip(), None)
            else:
                return make_test_result(False, "total line expected", stdout.strip(), None)

    except RuntimeError as e:
        return make_test_result(False, "compiled", str(e), None)

test_sort_test_only_errors.input_description = "all files non-existent"


# ============================================================================
# Flag Combination Tests
# ============================================================================

def test_args_c(workspace):
    """Test -c flag only (character count)."""
    content = "hello world\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-c"]
    )

test_args_c.input_description = "single file with -c flag"


def test_args_l(workspace):
    """Test -l flag only (line count)."""
    content = "line one\nline two\nline three\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-l"]
    )

test_args_l.input_description = "single file with -l flag"


def test_args_w(workspace):
    """Test -w flag only (word count)."""
    content = "one two three four five\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-w"]
    )

test_args_w.input_description = "single file with -w flag"


def test_args_cw(workspace):
    """Test -c -w flags (chars and words)."""
    content = "hello world test\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-c", "-w"]
    )

test_args_cw.input_description = "single file with -c -w flags"


def test_args_cl(workspace):
    """Test -c -l flags (chars and lines)."""
    content = "line one\nline two\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-c", "-l"]
    )

test_args_cl.input_description = "single file with -c -l flags"


def test_args_wl(workspace):
    """Test -w -l flags (words and lines)."""
    content = "one two\nthree four\nfive\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-w", "-l"]
    )

test_args_wl.input_description = "single file with -w -l flags"


def test_args_cwl(workspace):
    """Test -c -w -l flags (all counts)."""
    content = "This is a test.\n"
    return _run_count_test(
        workspace,
        {"test.txt": content},
        ["-c", "-w", "-l"]
    )

test_args_cwl.input_description = "single file with all flags"


# ============================================================================
# Numbered Tests (general functionality)
# ============================================================================

def test_1(workspace):
    """General test 1: simple file."""
    content = "Hello, World!\n"
    return _run_count_test(
        workspace,
        {"test1.txt": content},
        ["-c", "-w", "-l"]
    )

test_1.input_description = "simple greeting"


def test_2(workspace):
    """General test 2: multiple lines."""
    content = "Line 1\nLine 2\nLine 3\n"
    return _run_count_test(
        workspace,
        {"test2.txt": content},
        ["-c", "-w", "-l"]
    )

test_2.input_description = "three lines"


def test_3(workspace):
    """General test 3: tabs and spaces."""
    content = "word1\tword2\tword3\nword4  word5\n"
    return _run_count_test(
        workspace,
        {"test3.txt": content},
        ["-c", "-w", "-l"]
    )

test_3.input_description = "tabs and multiple spaces"


def test_4(workspace):
    """General test 4: leading/trailing whitespace."""
    content = "  leading spaces\ntrailing spaces  \n"
    return _run_count_test(
        workspace,
        {"test4.txt": content},
        ["-c", "-w", "-l"]
    )

test_4.input_description = "leading and trailing whitespace"


def test_5(workspace):
    """General test 5: single word per line."""
    content = "one\ntwo\nthree\nfour\nfive\n"
    return _run_count_test(
        workspace,
        {"test5.txt": content},
        ["-c", "-w", "-l"]
    )

test_5.input_description = "single word per line"


def test_6(workspace):
    """General test 6: no trailing newline."""
    content = "no newline at end"
    return _run_count_test(
        workspace,
        {"test6.txt": content},
        ["-c", "-w", "-l"]
    )

test_6.input_description = "no trailing newline"


def test_7(workspace):
    """General test 7: multiple consecutive newlines."""
    content = "text\n\n\nmore text\n"
    return _run_count_test(
        workspace,
        {"test7.txt": content},
        ["-c", "-w", "-l"]
    )

test_7.input_description = "multiple blank lines"


def test_8(workspace):
    """General test 8: numbers as words."""
    content = "123 456 789\n1 2 3 4 5\n"
    return _run_count_test(
        workspace,
        {"test8.txt": content},
        ["-c", "-w", "-l"]
    )

test_8.input_description = "numbers as words"


def test_9(workspace):
    """General test 9: mixed content."""
    content = "The quick brown fox\njumps over 42 lazy dogs!\n"
    return _run_count_test(
        workspace,
        {"test9.txt": content},
        ["-c", "-w", "-l"]
    )

test_9.input_description = "mixed alphanumeric content"


# ============================================================================
# Multiple File Tests
# ============================================================================

def test_open_files(workspace):
    """Test processing multiple files."""
    return _run_count_test(
        workspace,
        {
            "file1.txt": "content one\n",
            "file2.txt": "content two three\n",
            "file3.txt": "content four five six\n",
            "file4.txt": "content seven eight nine ten\n",
            "file5.txt": "content\n",
            "file6.txt": "more content here\n",
        },
        ["-c", "-w", "-l"],
        check_sorting=True
    )

test_open_files.input_description = "six files to process"

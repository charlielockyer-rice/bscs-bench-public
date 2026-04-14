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
        # Use basenames only: files are created in the workspace directory,
        # and the program runs with cwd set to the workspace (Docker: /workspace,
        # host: workspace_path). Absolute host paths break inside Docker.
        args.extend([Path(f).name for f in files])

        return run_program(self.executable, args, timeout=60,
                           cwd=self.workspace_path)


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

def test_blankfile_small(workspace):
    """Test counting blank lines only (small file)."""
    content = "\n" * 14  # 14 blank lines
    return _run_count_test(
        workspace,
        {"blankfile_small.txt": content},
        ["-c", "-w", "-l"],
        {"blankfile_small.txt": [14, 0, 14]}
    )

test_blankfile_small.input_description = "14 blank lines"


def test_blankfile_large(workspace):
    """Test counting blank lines only (large file)."""
    content = "\n" * 999  # 999 blank lines
    return _run_count_test(
        workspace,
        {"blankfile_large.txt": content},
        ["-c", "-w", "-l"],
        {"blankfile_large.txt": [999, 0, 999]}
    )

test_blankfile_large.input_description = "999 blank lines"


def test_alphanumeric_only(workspace):
    """Test file with only alphanumeric characters."""
    content = "abc123 def456\nghi789 jkl012\n"
    return _run_count_test(
        workspace,
        {"alphanum.txt": content},
        ["-c", "-w", "-l"]
    )

test_alphanumeric_only.input_description = "alphanumeric text only"


def test_special_char_only(workspace):
    """Test file with only special characters."""
    content = "!@#$%^&*()_+-=[]{}|;':\",./<>?\n"
    return _run_count_test(
        workspace,
        {"special.txt": content},
        ["-c", "-w", "-l"]
    )

test_special_char_only.input_description = "special characters only"


def test_special_char_with_spaces(workspace):
    """Test file with special characters and spaces."""
    content = "!@# $%^ &*()\n_+- =[] {}\n"
    return _run_count_test(
        workspace,
        {"special_spaces.txt": content},
        ["-c", "-w", "-l"]
    )

test_special_char_with_spaces.input_description = "special chars with spaces"


def test_huge_line(workspace):
    """Test file with a very long line."""
    # Create a line with many words
    words = ["word" + str(i) for i in range(10000)]
    content = " ".join(words) + "\n"
    return _run_count_test(
        workspace,
        {"huge_line.txt": content},
        ["-c", "-w", "-l"]
    )

test_huge_line.input_description = "single line with 10000 words"


def test_long_misspelled_word(workspace):
    """Test file with very long words."""
    content = "\n".join(["a" * 1000 for _ in range(100)]) + "\n"
    return _run_count_test(
        workspace,
        {"long_words.txt": content},
        ["-c", "-w", "-l"]
    )

test_long_misspelled_word.input_description = "100 lines with 1000-char words"



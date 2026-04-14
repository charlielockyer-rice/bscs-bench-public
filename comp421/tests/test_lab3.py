"""
COMP 421 Lab 3 (Yalnix FS) Test Definitions

Test definitions map test program names to expected output patterns.
The runner executes each test under Yalnix and checks TTYLOG output.

Each test program source is bundled from lab3/samples/ and compiled on CLEAR.
and run under the Yalnix kernel with the student's YFS server. Yalnix
redirects all process stdout/stderr to TTYLOG.N files. The YFS server is
TTYLOG.0, the test program is typically TTYLOG.1.
"""

import re
from typing import Tuple

# Common error patterns that indicate kernel/process failure
ERROR_PATTERNS = ["TRAP", "Segfault", "BUS ERROR", "KILL"]


def _has_errors(output: str) -> str:
    """Check for common error patterns. Returns error description or empty string."""
    for pattern in ERROR_PATTERNS:
        if pattern in output:
            return f"Found error pattern: {pattern}"
    return ""


# ---------------------------------------------------------------------------
# Check functions
# Each returns (passed: bool, error_msg: str)
# ---------------------------------------------------------------------------

def check_sample1(output: str) -> Tuple[bool, str]:
    """
    sample1: Creates files a/b/c, dir, /dir/x/y/z. No printf output.
    Pass if no error patterns found (silent success).
    """
    err = _has_errors(output)
    if err:
        return (False, err)
    return (True, "")


def check_sample2(output: str) -> Tuple[bool, str]:
    """
    sample2: Creates 32 empty files file00-file31. No printf output.
    Pass if no error patterns found (silent success).
    """
    err = _has_errors(output)
    if err:
        return (False, err)
    return (True, "")


def check_writeread(output: str) -> Tuple[bool, str]:
    """
    writeread: Creates /xxxxxx, writes 26+10 chars, reads back one at a time.
    Checks for key output lines.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    # Check create succeeded (fd should be >= 0)
    if "Create fd" not in output:
        return (False, "Missing 'Create fd' output")

    # Check writes
    if "Write nch 26" not in output:
        return (False, "Missing 'Write nch 26'")
    if "Write nch 10" not in output:
        return (False, "Missing 'Write nch 10'")

    # Check close after write
    if "Close status 0" not in output:
        return (False, "Missing 'Close status 0'")

    # Check open succeeded
    if "Open fd" not in output:
        return (False, "Missing 'Open fd' output")

    # Check first char read back correctly
    if "ch 0x61 'a'" not in output:
        return (False, "Missing first read-back char 'a' (0x61)")

    # Check last alpha char
    if "ch 0x7a 'z'" not in output:
        return (False, "Missing read-back char 'z' (0x7a)")

    # Check first digit char
    if "ch 0x30 '0'" not in output:
        return (False, "Missing read-back char '0' (0x30)")

    # Check last digit char
    if "ch 0x39 '9'" not in output:
        return (False, "Missing read-back char '9' (0x39)")

    # Check final read returns 0 (EOF)
    if "Read nch 0" not in output:
        return (False, "Missing 'Read nch 0' (EOF)")

    return (True, "")


def check_tcreate(output: str) -> Tuple[bool, str]:
    """
    tcreate: Creates /foo, prints to stderr. Does NOT call Shutdown().
    Output goes to TTYLOG since stderr is also redirected.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    if "Create returned 0" not in output:
        return (False, "Missing 'Create returned 0'")

    return (True, "")


def check_tcreate2(output: str) -> Tuple[bool, str]:
    """
    tcreate2: Creates /foo (should succeed), /bar (should succeed),
    /foo again (should succeed, truncates), /foo/zzz (should fail,
    /foo is a regular file not a directory).

    Output format per create: newline, fd number, two newlines.
    First three creates should return fd >= 0.
    Fourth create (/foo/zzz) should return -1 (ERROR).
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    # Parse the fd values from the output. The format is:
    #   \n<fd>\n\n  repeated 4 times
    # We look for lines that contain just a number (possibly with whitespace)
    lines = [line.strip() for line in output.split("\n") if line.strip()]
    fds = []
    for line in lines:
        try:
            fds.append(int(line))
        except ValueError:
            continue

    if len(fds) < 4:
        return (False, f"Expected 4 fd values, found {len(fds)}: {fds}")

    # First three creates should succeed (fd >= 0)
    for i in range(3):
        if fds[i] < 0:
            return (False, f"Create #{i+1} returned {fds[i]}, expected >= 0")

    # Fourth create (/foo/zzz) should fail since /foo is not a directory
    if fds[3] != -1:
        return (False, f"Create /foo/zzz returned {fds[3]}, expected -1 (ERROR)")

    return (True, "")


def check_tlink(output: str) -> Tuple[bool, str]:
    """
    tlink: Creates /a, links /a to /b. Both should succeed with status 0.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    if "Create status 0" not in output:
        return (False, "Missing 'Create status 0'")
    if "Link status 0" not in output:
        return (False, "Missing 'Link status 0'")

    return (True, "")


def check_topen2(output: str) -> Tuple[bool, str]:
    """
    topen2: Opens /foo, /bar, /foo, /foo/zzz on a fresh filesystem.
    All four opens should fail (return -1) since no files exist.

    Output format: newline, status number, two newlines per Open call.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    lines = [line.strip() for line in output.split("\n") if line.strip()]
    statuses = []
    for line in lines:
        try:
            statuses.append(int(line))
        except ValueError:
            continue

    if len(statuses) < 4:
        return (False, f"Expected 4 status values, found {len(statuses)}: {statuses}")

    # All opens on fresh filesystem should return -1 (ERROR)
    for i in range(4):
        if statuses[i] != -1:
            return (False, f"Open #{i+1} returned {statuses[i]}, expected -1 (no such file)")

    return (True, "")


def check_tunlink2(output: str) -> Tuple[bool, str]:
    """
    tunlink2: Unlinks /bar, /bar again, /foo/abc, /foo on a fresh filesystem.
    All four unlinks should fail (return -1) since no files exist.

    Output format: newline, status number, two newlines per Unlink call.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    lines = [line.strip() for line in output.split("\n") if line.strip()]
    statuses = []
    for line in lines:
        try:
            statuses.append(int(line))
        except ValueError:
            continue

    if len(statuses) < 4:
        return (False, f"Expected 4 status values, found {len(statuses)}: {statuses}")

    # All unlinks on fresh filesystem should return -1 (ERROR)
    for i in range(4):
        if statuses[i] != -1:
            return (False, f"Unlink #{i+1} returned {statuses[i]}, expected -1 (no such file)")

    return (True, "")


def check_tsymlink(output: str) -> Tuple[bool, str]:
    """
    tsymlink: Creates /a, symlinks /a to /b, reads link, stats both,
    creates symlinks with long paths, writes via /a, reads via b.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    # Create /a should succeed
    if "Create status 0" not in output:
        return (False, "Missing 'Create status 0'")

    # SymLink /a -> /b should succeed
    if "SymLink status 0" not in output:
        return (False, "Missing 'SymLink status 0'")

    # ReadLink should succeed and return "/a"
    if "ReadLink status" not in output:
        return (False, "Missing ReadLink status")
    if "link = '/a'" not in output:
        return (False, "Missing or wrong ReadLink result, expected \"link = '/a'\"")

    # Stat /a should succeed
    if "Stat status 0" not in output:
        return (False, "Missing 'Stat status 0'")

    # Both Stat outputs should be present
    if "/a: inum" not in output:
        return (False, "Missing Stat output for /a")
    if "/b: inum" not in output:
        return (False, "Missing Stat output for /b")

    # Write through /a should succeed
    if "Write /a status 27" not in output:
        return (False, "Missing 'Write /a status 27'")

    # Read through symlink b should return the written data
    if "Read b status 27" not in output:
        return (False, "Missing 'Read b status 27'")
    if "ABCDEFGHIJKLMNOPQRSTUVWXYZ" not in output:
        return (False, "Missing read-back data through symlink")

    return (True, "")


def check_tls(output: str) -> Tuple[bool, str]:
    """
    tls: Lists directory contents of root. On a fresh filesystem,
    should show at least "." and ".." entries with correct types.
    """
    err = _has_errors(output)
    if err:
        return (False, err)

    # Should not have any error messages
    if "Can't ChDir" in output:
        return (False, "ChDir failed")
    if "Can't Open" in output:
        return (False, "Open '.' failed")
    if "ERROR Reading" in output:
        return (False, "Error reading directory")

    # Root directory must have . and .. entries (both are inode 1, type directory)
    # Match ". " or "." at end of line to avoid matching periods in other text
    if not re.search(r'\.\s*$', output, re.MULTILINE):
        return (False, "Missing '.' entry in directory listing")

    # Should have at least some inum/type output (format: "  N d  M  SIZE name")
    if " d " not in output:
        return (False, "No directory-type entries found (expected '.' and '..')")

    return (True, "")


# ---------------------------------------------------------------------------
# Generic checkers for assert-style tests
# ---------------------------------------------------------------------------

def _check_all_tests_passed(output: str) -> Tuple[bool, str]:
    """Generic checker for tests that print 'All tests passed' on success."""
    err = _has_errors(output)
    if err:
        return (False, err)
    if "All tests passed" not in output:
        return (False, "Missing 'All tests passed' — test likely failed an assertion")
    return (True, "")


def _check_test_passed(output: str) -> Tuple[bool, str]:
    """Generic checker for tests that print 'Test passed' on success."""
    err = _has_errors(output)
    if err:
        return (False, err)
    if "Test passed" not in output:
        return (False, "Missing 'Test passed' — test likely failed an assertion")
    return (True, "")


def _check_no_errors(output: str) -> Tuple[bool, str]:
    """Generic checker for tests with no stdout — pass if no error patterns."""
    err = _has_errors(output)
    if err:
        return (False, err)
    return (True, "")


# ---------------------------------------------------------------------------
# Check functions for new tests
# ---------------------------------------------------------------------------

def check_create_close(output: str) -> Tuple[bool, str]:
    """create_close: Create/Close edge cases, long names, NULL, dirs."""
    return _check_all_tests_passed(output)


def check_chdir(output: str) -> Tuple[bool, str]:
    """chdir: MkDir + ChDir with absolute, relative, '.', '..', edge cases."""
    return _check_all_tests_passed(output)


def check_mkdir_rmdir(output: str) -> Tuple[bool, str]:
    """mkdir_rmdir: MkDir + RmDir nested directories, edge cases, NULL."""
    return _check_all_tests_passed(output)


def check_link_unlink(output: str) -> Tuple[bool, str]:
    """link_unlink: Hard links, unlink, error cases."""
    return _check_all_tests_passed(output)


def check_symlink_readlink(output: str) -> Tuple[bool, str]:
    """symlink_readlink: SymLink, ReadLink, open through symlinks."""
    return _check_all_tests_passed(output)


def check_read_write_seek_stat(output: str) -> Tuple[bool, str]:
    """read_write_seek_stat: Indirect blocks, seek past EOF, gap blocks, stat."""
    return _check_all_tests_passed(output)


def check_testall(output: str) -> Tuple[bool, str]:
    """testall: Combined create, unlink, write, read, seek, max FDs."""
    return _check_all_tests_passed(output)


def check_lorem(output: str) -> Tuple[bool, str]:
    """lorem: Write and read back a large (~1KB) lorem ipsum buffer."""
    return _check_test_passed(output)


def check_holeindirect(output: str) -> Tuple[bool, str]:
    """holeindirect: Write with seeks creating holes spanning indirect blocks."""
    return _check_test_passed(output)


def check_test_create_read_write(output: str) -> Tuple[bool, str]:
    """test_create_read_write: Create 10 files, write/read 'Hello world' to each."""
    err = _has_errors(output)
    if err:
        return (False, err)
    if "Wrote to all files" not in output:
        return (False, "Missing 'Wrote to all files'")
    if "Something went wrong" in output:
        return (False, "Error during file operations")
    return (True, "")


def check_test_create_read_write_subdir(output: str) -> Tuple[bool, str]:
    """test_create_read_write_subdir: Create 10 files in /dir/, write/read each."""
    err = _has_errors(output)
    if err:
        return (False, err)
    if "Wrote to all files" not in output:
        return (False, "Missing 'Wrote to all files'")
    if "Something went wrong" in output:
        return (False, "Error during file operations")
    return (True, "")


def check_test_mkdir_rmdir(output: str) -> Tuple[bool, str]:
    """test_mkdir_rmdir: Create 10 nested dirs, open deep path, rmdir in reverse."""
    err = _has_errors(output)
    if err:
        return (False, err)
    if "Created all directories" not in output:
        return (False, "Missing 'Created all directories'")
    if "Something went wrong" in output:
        return (False, "Error during directory operations")
    return (True, "")


def check_stattest(output: str) -> Tuple[bool, str]:
    """stattest: MkDir, Create, Create in subdir — prints return values."""
    err = _has_errors(output)
    if err:
        return (False, err)
    # MkDir("/foo") should return 0, Create("/bar") should return fd >= 0,
    # Create("/foo/zoo") should return fd >= 0
    lines = [line.strip() for line in output.split("\n") if line.strip()]
    values = []
    for line in lines:
        try:
            values.append(int(line))
        except ValueError:
            continue
    if len(values) < 3:
        return (False, f"Expected 3 return values, found {len(values)}: {values}")
    if values[0] != 0:
        return (False, f"MkDir('/foo') returned {values[0]}, expected 0")
    if values[1] < 0:
        return (False, f"Create('/bar') returned {values[1]}, expected >= 0")
    if values[2] < 0:
        return (False, f"Create('/foo/zoo') returned {values[2]}, expected >= 0")
    return (True, "")


def check_fillfile(output: str) -> Tuple[bool, str]:
    """fillfile: Write to a file until disk is full (fills all blocks)."""
    return _check_no_errors(output)


def check_holetest(output: str) -> Tuple[bool, str]:
    """holetest: Write with seeks creating holes, read back and verify."""
    return _check_no_errors(output)


def check_indirect(output: str) -> Tuple[bool, str]:
    """indirect: Write large file spanning indirect blocks."""
    return _check_no_errors(output)


def check_init(output: str) -> Tuple[bool, str]:
    """init: Basic create, write, seek, read back — output via TracePrintf."""
    return _check_no_errors(output)


# ---------------------------------------------------------------------------
# Test definitions
# ---------------------------------------------------------------------------

TEST_DEFINITIONS = {
    "test_sample1": {
        "program": "sample1",
        "points": 10,
        "check": check_sample1,
        "needs_timeout": False,
        "description": "Create files and directories, write data, shutdown cleanly",
    },
    "test_sample2": {
        "program": "sample2",
        "points": 10,
        "check": check_sample2,
        "needs_timeout": False,
        "description": "Create 32 empty files (file00-file31), shutdown cleanly",
    },
    "test_writeread": {
        "program": "writeread",
        "points": 15,
        "check": check_writeread,
        "needs_timeout": False,
        "description": "Create file, write 36 chars, read back one at a time",
    },
    "test_tcreate": {
        "program": "tcreate",
        "points": 8,
        "check": check_tcreate,
        "needs_timeout": True,
        "description": "Create /foo, verify fd returned (no Shutdown call)",
    },
    "test_tcreate2": {
        "program": "tcreate2",
        "points": 10,
        "check": check_tcreate2,
        "needs_timeout": False,
        "description": "Create /foo, /bar, reopen /foo, fail on /foo/zzz",
    },
    "test_tlink": {
        "program": "tlink",
        "points": 10,
        "check": check_tlink,
        "needs_timeout": False,
        "description": "Create /a and hard link /b to /a",
    },
    "test_topen2": {
        "program": "topen2",
        "points": 8,
        "check": check_topen2,
        "needs_timeout": False,
        "description": "Open nonexistent files (all should fail)",
    },
    "test_tunlink2": {
        "program": "tunlink2",
        "points": 8,
        "check": check_tunlink2,
        "needs_timeout": False,
        "description": "Unlink nonexistent files (all should fail)",
    },
    "test_tsymlink": {
        "program": "tsymlink",
        "points": 12,
        "check": check_tsymlink,
        "needs_timeout": False,
        "description": "SymLink, ReadLink, Stat, and read/write through symlinks",
    },
    "test_tls": {
        "program": "tls",
        "points": 9,
        "check": check_tls,
        "needs_timeout": False,
        "description": "List root directory contents (ls-style)",
    },
    "test_create_close": {
        "program": "create_close",
        "points": 5,
        "check": check_create_close,
        "needs_timeout": False,
        "description": "Create/Close edge cases: long names, NULL, dirs, reopen",
    },
    "test_chdir": {
        "program": "chdir",
        "points": 5,
        "check": check_chdir,
        "needs_timeout": False,
        "description": "MkDir + ChDir: absolute, relative, '.', '..', complex paths",
    },
    "test_mkdir_rmdir": {
        "program": "mkdir_rmdir",
        "points": 5,
        "check": check_mkdir_rmdir,
        "needs_timeout": False,
        "description": "MkDir + RmDir: nested dirs, edge cases, NULL, long names",
    },
    "test_link_unlink": {
        "program": "link_unlink",
        "points": 5,
        "check": check_link_unlink,
        "needs_timeout": False,
        "description": "Hard links, unlink original, unlink through link, error cases",
    },
    "test_symlink_readlink": {
        "program": "symlink_readlink",
        "points": 5,
        "check": check_symlink_readlink,
        "needs_timeout": False,
        "description": "SymLink + ReadLink: files, dirs, errors, read through symlink",
    },
    "test_read_write_seek_stat": {
        "program": "read_write_seek_stat",
        "points": 8,
        "check": check_read_write_seek_stat,
        "needs_timeout": False,
        "description": "Indirect blocks, seek past EOF, gap blocks, stat, dir read/write",
    },
    "test_create_read_write": {
        "program": "test_create_read_write",
        "points": 4,
        "check": check_test_create_read_write,
        "needs_timeout": False,
        "description": "Create 10 files, write/read 'Hello world' to each",
    },
    "test_mkdir_rmdir_nested": {
        "program": "test_mkdir_rmdir",
        "points": 4,
        "check": check_test_mkdir_rmdir,
        "needs_timeout": False,
        "description": "Create 10 nested dirs, open deep path, rmdir in reverse",
    },
}


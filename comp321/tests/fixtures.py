"""
Shared test fixtures and utilities for COMP 321 C program tests.

This module provides helper functions for:
- Compiling C programs
- Running executables and capturing output
- Comparing outputs with expected values
- Creating test input files

Docker Support:
- Set USE_DOCKER=true environment variable to run programs in Docker
- Docker-compiled binaries are Linux ELF files that require Docker to run on macOS
"""

import os
import platform
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Optional, Tuple

# Add framework to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent.parent))
from framework.docker_utils import (
    compile_in_docker as _compile_in_docker,
    run_program_in_docker as _run_program_in_docker,
)

# Docker configuration
DOCKER_IMAGE = "comp321-runner"

# Check if Docker mode is enabled
# Default to True on non-Linux systems where Docker-compiled binaries won't run
_is_linux = platform.system() == "Linux"
USE_DOCKER = os.environ.get("USE_DOCKER", "false" if _is_linux else "true").lower() == "true"


@dataclass
class TestResult:
    """Result of a single test."""
    passed: bool
    expected: str
    actual: str
    error_message: Optional[str] = None


def make_test_result(passed: bool, expected: str, actual: str, error_msg: str = None) -> TestResult:
    """Create a test result."""
    return TestResult(passed=passed, expected=expected, actual=actual, error_message=error_msg)


def compile_c_program(
    source_dir: Path,
    target: str = None,
    makefile: bool = True,
    extra_flags: List[str] = None
) -> Tuple[bool, str]:
    """
    Compile a C program using make or gcc.

    Args:
        source_dir: Directory containing source files
        target: Make target or output executable name
        makefile: Whether to use make (True) or gcc directly (False)
        extra_flags: Additional compiler flags

    Returns:
        Tuple of (success, error_message)
    """
    try:
        if makefile and (source_dir / "Makefile").exists():
            cmd = ["make", "-C", str(source_dir)]
            if target:
                cmd.append(target)
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=60
            )
        else:
            # Direct gcc compilation
            c_files = list(source_dir.glob("*.c"))
            if not c_files:
                return False, f"No C files found in {source_dir}"

            output = target or "a.out"
            cmd = ["gcc", "-Wall", "-Wextra", "-o", str(source_dir / output)]
            if extra_flags:
                cmd.extend(extra_flags)
            cmd.extend([str(f) for f in c_files])

            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=60
            )

        if result.returncode != 0:
            return False, result.stderr
        return True, ""

    except subprocess.TimeoutExpired:
        return False, "Compilation timed out"
    except Exception as e:
        return False, str(e)


def compile_c_program_in_docker(
    source_dir: Path,
    subdir: str = None,
    network: bool = False
) -> Tuple[bool, str]:
    """
    Compile a C program inside Docker container.

    Args:
        source_dir: Directory containing source files (mounted as /workspace)
        subdir: Subdirectory to cd into before make (e.g., "readjcf")
        network: Whether to enable network access

    Returns:
        Tuple of (success, error_message)
    """
    if subdir:
        build_cmd = f"cd {subdir} && make"
    else:
        build_cmd = "make"

    return _compile_in_docker(
        image=DOCKER_IMAGE,
        workspace=source_dir,
        build_cmd=build_cmd,
        timeout=120,
        memory="4g",
        cpus="2",
        network=network
    )


def run_program_in_docker(
    workspace: Path,
    executable: str,
    args: List[str] = None,
    stdin_input: str = None,
    timeout: int = 60,
    network: bool = False
) -> Tuple[int, str, str]:
    """
    Run an executable inside Docker container.

    Args:
        workspace: Directory containing the executable (mounted as /workspace)
        executable: Name of executable relative to workspace
        args: Command-line arguments
        stdin_input: Input to provide on stdin
        timeout: Timeout in seconds
        network: Whether to enable network access

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    return _run_program_in_docker(
        workspace=workspace,
        executable=executable,
        image=DOCKER_IMAGE,
        args=args,
        stdin_input=stdin_input,
        timeout=timeout,
        network=network,
        memory="4g",
        cpus="2"
    )


def run_program(
    executable: Path,
    args: List[str] = None,
    stdin_input: str = None,
    timeout: int = 60,
    env: Dict[str, str] = None,
    cwd: Path = None,
    use_docker: bool = None,
    network: bool = False
) -> Tuple[int, str, str]:
    """
    Run an executable and capture output.

    Args:
        executable: Path to the executable
        args: Command-line arguments
        stdin_input: Input to provide on stdin
        timeout: Timeout in seconds
        env: Environment variables (ignored in Docker mode)
        cwd: Working directory (ignored in Docker mode, workspace is inferred)
        use_docker: Override USE_DOCKER setting
        network: Whether to enable network access (Docker only)

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    should_use_docker = use_docker if use_docker is not None else USE_DOCKER

    if should_use_docker:
        # Infer workspace from executable path
        workspace = executable.parent
        exe_name = executable.name
        return run_program_in_docker(workspace, exe_name, args, stdin_input, timeout, network)

    # Host execution
    cmd = [str(executable)]
    if args:
        cmd.extend(args)

    run_env = os.environ.copy()
    if env:
        run_env.update(env)

    try:
        result = subprocess.run(
            cmd,
            input=stdin_input,
            capture_output=True,
            text=True,
            timeout=timeout,
            env=run_env,
            cwd=str(cwd) if cwd else None
        )
        return result.returncode, result.stdout, result.stderr

    except subprocess.TimeoutExpired:
        return -1, "", "Program timed out"
    except Exception as e:
        return -1, "", str(e)


def normalize_output(output: str, ignore_pids: bool = False) -> str:
    """
    Normalize output for comparison.

    Args:
        output: Raw output string
        ignore_pids: Whether to normalize process IDs

    Returns:
        Normalized string
    """
    # Normalize line endings
    output = output.replace('\r\n', '\n')

    # Remove trailing whitespace from each line
    lines = [line.rstrip() for line in output.split('\n')]
    output = '\n'.join(lines)

    # Optionally normalize PIDs
    if ignore_pids:
        # Replace process IDs with placeholder
        output = re.sub(r'\(\d+\)', '(PID)', output)
        output = re.sub(r'\b\d{4,}\b', 'PID', output)

    return output.strip()


def compare_outputs(expected: str, actual: str, ignore_pids: bool = False) -> bool:
    """
    Compare expected and actual outputs.

    Args:
        expected: Expected output
        actual: Actual output
        ignore_pids: Whether to ignore PID differences

    Returns:
        True if outputs match
    """
    expected_norm = normalize_output(expected, ignore_pids)
    actual_norm = normalize_output(actual, ignore_pids)
    return expected_norm == actual_norm


def create_temp_file(content: str, suffix: str = ".txt") -> Path:
    """
    Create a temporary file with given content.

    Args:
        content: File content
        suffix: File extension

    Returns:
        Path to temporary file
    """
    fd, path = tempfile.mkstemp(suffix=suffix)
    with os.fdopen(fd, 'w') as f:
        f.write(content)
    return Path(path)


def create_temp_dir() -> Path:
    """Create a temporary directory."""
    return Path(tempfile.mkdtemp())


def cleanup_temp_path(path: Path):
    """Remove a temporary file or directory."""
    try:
        if path.is_dir():
            shutil.rmtree(path)
        else:
            path.unlink()
    except Exception:
        pass


# Prime factorization helpers
def count_prime_factors(n: int) -> int:
    """Count total prime factors of n."""
    if n < 2:
        return 0
    count = 0
    d = 2
    while d * d <= n:
        while n % d == 0:
            count += 1
            n //= d
        d += 1
    if n > 1:
        count += 1
    return count


def count_distinct_prime_factors(n: int) -> int:
    """Count distinct prime factors of n."""
    if n < 2:
        return 0
    factors = set()
    d = 2
    while d * d <= n:
        while n % d == 0:
            factors.add(d)
            n //= d
        d += 1
    if n > 1:
        factors.add(n)
    return len(factors)


# Word count helpers
def count_file_stats(content: str) -> Tuple[int, int, int]:
    """
    Count characters, words, and lines in content.

    Returns:
        Tuple of (chars, words, lines)
    """
    chars = len(content)
    words = len(content.split())
    lines = content.count('\n')
    return chars, words, lines


def is_whitespace(c: str) -> bool:
    """Check if character is whitespace (matching isspace behavior)."""
    return c in ' \t\n\r\f\v'


def count_words_standard(content: str) -> int:
    """
    Count words using standard definition.
    A word is a maximal sequence of non-whitespace characters.
    """
    in_word = False
    count = 0
    for c in content:
        if is_whitespace(c):
            in_word = False
        elif not in_word:
            in_word = True
            count += 1
    return count


# Shell test helpers
def parse_trace_file(trace_path: Path) -> List[str]:
    """Parse a shell trace file and extract commands."""
    commands = []
    with open(trace_path) as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#'):
                commands.append(line)
    return commands


def strip_ansi_codes(text: str) -> str:
    """Remove ANSI escape codes from text."""
    ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
    return ansi_escape.sub('', text)

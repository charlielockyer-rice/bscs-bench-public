#!/usr/bin/env python3
"""
COMP 421 SSH-based Test Runner

Compiles and runs COMP 421 lab tests on Rice CLEAR servers via SSH.
Supports multiple labs with different execution models:

- Lab 1 (MonTTY): Regular executables, stdout/stderr output, Xvfb for X11
- Lab 2 (Yalnix Kernel): Yalnix simulator, TTYLOG output, no filesystem
- Lab 3 (Yalnix FS): Yalnix simulator, TTYLOG output, mkyfs disk setup

The Yalnix OS simulator, kernel, and linker are only available on CLEAR at
/clear/courses/comp421/pub/.

Usage:
    python runner.py <project> <workspace> [options]

Examples:
    python runner.py lab1 ./workspaces/comp421_lab1_agent --verbose
    python runner.py lab3 ./workspaces/comp421_lab3_agent
    python runner.py lab3 ./workspaces/comp421_lab3_agent --json --netid $CLEAR_NETID
"""

import argparse
import hashlib
import json
import os
import subprocess
import sys
from pathlib import Path
from typing import List, Optional, Tuple

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.yaml_utils import parse_simple_yaml
from framework.config_compat import (
    normalize_config,
    get_assignment_number,
    get_display_name,
)
from framework.test_loader import load_python_module_from_file, load_python_test_modules
from framework.suite_manifest import load_suite_manifest, source_from_manifest_path
from shared.results import TestResult, GradeResult

# SSH constants
SSH_HOST = "ssh.clear.rice.edu"
YALNIX_PUB = "/clear/courses/comp421/pub"
YALNIX_BIN = f"{YALNIX_PUB}/bin/yalnix"
LINK_USER_GCC = f"{YALNIX_PUB}/bin/link-user-gcc"

# Lab 1
LAB1_TEST_TIMEOUT = 15

# Timeouts (seconds)
SSH_TIMEOUT = 60
TEST_TIMEOUT = 30
COMPILE_TIMEOUT = 120


class SSHRunner:
    """Compiles and runs COMP 421 lab tests on CLEAR via SSH/SCP."""

    def __init__(self, comp421_dir: Path, netid: Optional[str] = None):
        self.comp421_dir = comp421_dir
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        self.tests_dir = Path(suite_override).resolve() if suite_override else (comp421_dir / "tests")
        self.configs_dir = comp421_dir / "configs"

        # Resolve net ID: param > env var (required)
        self.netid = netid or os.environ.get("CLEAR_NETID")
        if not self.netid:
            raise ValueError(
                "Rice NetID required. Set CLEAR_NETID environment variable or pass --netid"
            )
        self.ssh_target = f"{self.netid}@{SSH_HOST}"

    def _get_samples_dir(self, project_name: str) -> Path:
        """Get samples directory for a project."""
        return self.comp421_dir / project_name / "samples"

    def _remote_dir(self, workspace: Path) -> str:
        """Generate unique remote directory path.

        Uses .bscs_bench/ (relative to home dir) instead of /tmp/ because
        CLEAR load-balances SSH across multiple nodes, each with a separate
        /tmp. The home directory is NFS-shared across all nodes. A relative
        path (no leading /) is resolved from $HOME by both SSH and SCP.
        """
        workspace_hash = hashlib.sha256(str(workspace).encode()).hexdigest()[:12]
        return f".bscs_bench/{workspace_hash}"

    def _ssh_cmd(self, command: str, timeout: int = SSH_TIMEOUT) -> Tuple[int, str, str]:
        """Execute a command on CLEAR via SSH.

        Wraps in bash -c since CLEAR defaults to tcsh.
        """
        # Use bash explicitly and source profile for PATH
        wrapped = f"bash -c {_shell_quote(command)}"
        cmd = ["ssh", "-o", "BatchMode=yes", "-o", "StrictHostKeyChecking=accept-new",
               self.ssh_target, wrapped]
        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=timeout,
            )
            return result.returncode, result.stdout, result.stderr
        except subprocess.TimeoutExpired:
            return -1, "", f"SSH command timed out after {timeout}s"

    def _scp_to(self, local_path: str, remote_path: str, timeout: int = SSH_TIMEOUT) -> Tuple[bool, str]:
        """Upload a file or directory to CLEAR via SCP."""
        cmd = ["scp", "-o", "BatchMode=yes", "-o", "StrictHostKeyChecking=accept-new",
               "-r", local_path, f"{self.ssh_target}:{remote_path}"]
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
            if result.returncode != 0:
                return False, result.stderr
            return True, ""
        except subprocess.TimeoutExpired:
            return False, f"SCP upload timed out after {timeout}s"

    def _scp_from(self, remote_path: str, local_path: str, timeout: int = SSH_TIMEOUT) -> Tuple[bool, str]:
        """Download a file from CLEAR via SCP."""
        cmd = ["scp", "-o", "BatchMode=yes", "-o", "StrictHostKeyChecking=accept-new",
               f"{self.ssh_target}:{remote_path}", local_path]
        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=timeout)
            if result.returncode != 0:
                return False, result.stderr
            return True, ""
        except subprocess.TimeoutExpired:
            return False, f"SCP download timed out after {timeout}s"

    def load_config(self, project_name: str) -> dict:
        """Load project configuration from YAML."""
        config_path = self.configs_dir / f"{project_name}.yaml"
        if not config_path.exists():
            raise FileNotFoundError(f"Config not found: {config_path}")
        with open(config_path) as f:
            raw_config = parse_simple_yaml(f.read())
        return normalize_config(raw_config)

    def load_test_module(self, project_name: str):
        """Load the test definitions module."""
        test_file = self.tests_dir / f"test_{project_name}.py"
        return load_python_module_from_file(test_file, f"test_{project_name}")

    def load_test_modules(self, project_name: str) -> List[object]:
        """Load one or more test definition modules.

        Default behavior loads only the canonical public module for the project.
        Suite override behavior loads all test_*.py modules to include agent tests.
        """
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        suite_dir = self.tests_dir if suite_override else None
        return load_python_test_modules(
            canonical_test_file=self.tests_dir / f"test_{project_name}.py",
            canonical_module_name=f"test_{project_name}",
            suite_dir=suite_dir,
            suite_namespace="bscs_suite",
        )

    def _upload_files(
        self, project_name: str, workspace: Path, remote_dir: str,
        source_files: List[str], test_programs: List[str],
        extra_programs: Optional[List[str]] = None,
        verbose: bool = False,
    ) -> Tuple[bool, str]:
        """Upload student source files and test programs to CLEAR.

        Uses tar to bundle everything into a single SCP transfer.
        Lab 1: uploads all files from samples/ (test programs, utils, Makefile).
        Lab 2: uploads test program .c files + idle.c + exec targets from samples/.
        Lab 3: uploads test program .c files + mkyfs.c from samples/.
        """
        import tempfile
        import tarfile

        if extra_programs is None:
            extra_programs = []
        samples_dir = self._get_samples_dir(project_name)
        is_lab1 = project_name == "lab1"
        is_lab2 = project_name == "lab2"

        with tempfile.NamedTemporaryFile(suffix=".tar.gz", delete=False) as tmp:
            tar_path = tmp.name

        try:
            with tarfile.open(tar_path, "w:gz") as tar:
                # Add student source files from workspace
                for fname in source_files:
                    fpath = workspace / fname
                    if fpath.exists():
                        tar.add(str(fpath), arcname=fname)
                    elif verbose:
                        print(f"  Warning: {fname} not found in workspace")

                if is_lab1:
                    # Lab 1: upload all files from samples/ (test programs, utils,
                    # Makefile). The samples Makefile intentionally replaces the
                    # student's skeleton Makefile since it has all test targets.
                    for fpath in samples_dir.iterdir():
                        if fpath.is_file():
                            tar.add(str(fpath), arcname=fpath.name)
                elif is_lab2:
                    # Lab 2: upload test program .c files + idle.c + exec targets
                    for prog in test_programs:
                        src_file = samples_dir / f"{prog}.c"
                        if src_file.exists():
                            tar.add(str(src_file), arcname=f"{prog}.c")

                    # Upload idle.c (always needed)
                    idle_src = samples_dir / "idle.c"
                    if idle_src.exists():
                        tar.add(str(idle_src), arcname="idle.c")

                    # Upload extra programs (exec targets)
                    for prog in extra_programs:
                        src_file = samples_dir / f"{prog}.c"
                        if src_file.exists():
                            tar.add(str(src_file), arcname=f"{prog}.c")
                else:
                    # Lab 3: upload test program .c files + mkyfs.c
                    for prog in test_programs:
                        src_file = samples_dir / f"{prog}.c"
                        if src_file.exists():
                            tar.add(str(src_file), arcname=f"{prog}.c")

                    mkyfs_src = samples_dir / "mkyfs.c"
                    if mkyfs_src.exists():
                        tar.add(str(mkyfs_src), arcname="mkyfs.c")

            # Create remote dir and upload tar
            rc, _, stderr = self._ssh_cmd(f"mkdir -p {remote_dir}")
            if rc != 0:
                return False, f"Failed to create remote dir: {stderr}"

            success, err = self._scp_to(tar_path, f"{remote_dir}/upload.tar.gz")
            if not success:
                return False, f"SCP upload failed: {err}"

            # Extract on remote
            rc, _, stderr = self._ssh_cmd(
                f"cd {remote_dir} && tar xzf upload.tar.gz && rm upload.tar.gz",
                timeout=COMPILE_TIMEOUT,
            )
            if rc != 0:
                return False, f"Failed to extract upload: {stderr}"

            return True, ""
        finally:
            os.unlink(tar_path)

    def _compile_all(self, remote_dir: str, test_programs: List[str],
                     verbose: bool = False) -> Tuple[bool, str]:
        """Compile YFS server, iolib.a, mkyfs, and all test programs on CLEAR.

        Single SSH command to minimize connections.
        """
        # Build compile script as a single bash command
        compile_cmds = [
            f"cd {remote_dir}",
            # Compile YFS server and iolib via Makefile
            "make clean || true && make all",
            # Compile mkyfs (Unix program, not Yalnix)
            f"gcc -I{YALNIX_PUB}/include -o mkyfs mkyfs.c",
        ]

        # Compile each test program: compile .c -> .o, then link with iolib.a
        for prog in test_programs:
            compile_cmds.append(
                f"gcc -fcommon -I{YALNIX_PUB}/include -c {prog}.c -o {prog}.o && "
                f"{LINK_USER_GCC} -o {prog} {prog}.o iolib.a"
            )

        full_cmd = " && ".join(compile_cmds)
        rc, stdout, stderr = self._ssh_cmd(full_cmd, timeout=COMPILE_TIMEOUT)

        output = (stdout + "\n" + stderr).strip()
        if rc != 0:
            return False, output
        return True, output

    def _compile_lab1(self, remote_dir: str, verbose: bool = False) -> Tuple[bool, str]:
        """Compile Lab 1 on CLEAR using the uploaded Makefile.

        The Makefile from samples/ handles everything: compiles montty.o,
        utils.o, and all test programs.
        """
        compile_cmd = f"cd {remote_dir} && make clean 2>&1; make all 2>&1"
        rc, stdout, stderr = self._ssh_cmd(compile_cmd, timeout=COMPILE_TIMEOUT)

        output = (stdout + "\n" + stderr).strip()
        if rc != 0:
            return False, output
        return True, output

    def _compile_lab2(self, remote_dir: str, test_programs: List[str],
                      extra_programs: Optional[List[str]] = None,
                      verbose: bool = False) -> Tuple[bool, str]:
        """Compile Lab 2 kernel, idle, and all test programs on CLEAR.

        Lab 2 (Yalnix Kernel) differs from Lab 3 (Yalnix FS):
        - No iolib.a (no filesystem)
        - Kernel binary is 'yalnix' (not 'yfs')
        - Test programs are linked directly (no iolib.a)
        """
        if extra_programs is None:
            extra_programs = []
        compile_cmds = [
            f"cd {remote_dir}",
            # Compile kernel via Makefile → ./yalnix
            "make clean 2>&1; make all 2>&1",
            # Compile idle as user program
            f"gcc -fcommon -I{YALNIX_PUB}/include -c idle.c -o idle.o && "
            f"{LINK_USER_GCC} -o idle idle.o",
        ]

        # Compile each test program (no iolib.a — just compile and link)
        all_programs = list(test_programs) + list(extra_programs)
        for prog in all_programs:
            compile_cmds.append(
                f"gcc -fcommon -I{YALNIX_PUB}/include -c {prog}.c -o {prog}.o && "
                f"{LINK_USER_GCC} -o {prog} {prog}.o"
            )

        full_cmd = " && ".join(compile_cmds)
        rc, stdout, stderr = self._ssh_cmd(full_cmd, timeout=COMPILE_TIMEOUT)

        output = (stdout + "\n" + stderr).strip()
        if rc != 0:
            return False, output
        return True, output

    def _run_single_test(
        self, remote_dir: str, test_name: str, program: str,
        needs_timeout: bool, verbose: bool = False,
    ) -> Tuple[bool, str, str]:
        """Run a single test under Yalnix on CLEAR.

        Returns (success, ttylog_output, error_message).
        Each test gets a fresh DISK and clean TTYLOG state.
        """
        # Clean state, format disk, run yalnix with test program
        # Capture yalnix exit code separately so TTYLOG extraction doesn't mask it
        test_cmd = (
            f"cd {remote_dir} && "
            f"rm -f DISK TTYLOG.* TRACE && "
            f"./mkyfs && "
            f"timeout {TEST_TIMEOUT} {YALNIX_BIN} yfs {program} 2>&1; "
            "yalnix_rc=$?; "
            # Always read TTYLOG files regardless of exit code
            "echo '===TTYLOG_START===' && "
            'for f in TTYLOG.*; do '
            '  if [ -f "$f" ]; then echo "==$f==" && cat "$f"; fi; '
            'done && '
            "echo '===TTYLOG_END===' && "
            "echo '===YALNIX_RC===' && echo $yalnix_rc"
        )

        # Tests that don't call Shutdown() will hang until timeout
        timeout = TEST_TIMEOUT + 15 if needs_timeout else TEST_TIMEOUT + 10
        rc, stdout, stderr = self._ssh_cmd(test_cmd, timeout=timeout)

        # Parse TTYLOG output from the combined stdout
        ttylog_output = ""
        if "===TTYLOG_START===" in stdout:
            ttylog_section = stdout.split("===TTYLOG_START===")[1]
            if "===TTYLOG_END===" in ttylog_section:
                ttylog_section = ttylog_section.split("===TTYLOG_END===")[0]
            ttylog_output = ttylog_section.strip()

        # Extract yalnix exit code from output
        yalnix_rc = None
        if "===YALNIX_RC===" in stdout:
            rc_section = stdout.split("===YALNIX_RC===")[1].strip().split("\n")[0].strip()
            try:
                yalnix_rc = int(rc_section)
            except ValueError:
                pass

        # For verbose mode, also capture yalnix stderr/stdout before TTYLOG
        yalnix_output = ""
        if "===TTYLOG_START===" in stdout:
            yalnix_output = stdout.split("===TTYLOG_START===")[0].strip()

        if verbose:
            if yalnix_output:
                print(f"    Yalnix output: {yalnix_output[:200]}")
            if yalnix_rc is not None and yalnix_rc != 0:
                print(f"    Yalnix exit code: {yalnix_rc}")
            if ttylog_output:
                print(f"    TTYLOG: {ttylog_output[:300]}")

        # Check for catastrophic failure (couldn't even start yalnix)
        if rc == -1:
            return False, ttylog_output, "SSH timed out"

        # Extract just the test program output (typically TTYLOG.1)
        # TTYLOG.0 is the YFS server, TTYLOG.1+ are user processes
        test_output = _extract_ttylog(ttylog_output, target="TTYLOG.1")

        # If no TTYLOG.1, try combining all TTYLOGs (some tests only have TTYLOG.0)
        if not test_output:
            test_output = _extract_all_ttylogs(ttylog_output)

        # If TTYLOG content is still empty, fall back to yalnix stdout/stderr.
        # Some Yalnix kernels write user process output to their own stdout
        # rather than (or in addition to) TTYLOG files.
        if not test_output and yalnix_output:
            # Strip the "Booting..." line and kernel shutdown messages
            lines = yalnix_output.split("\n")
            filtered = []
            for line in lines:
                stripped = line.strip()
                # Skip Yalnix boot/shutdown banner lines
                if stripped.startswith("Booting") or stripped.startswith("** "):
                    continue
                filtered.append(line)
            test_output = "\n".join(filtered).strip()

        return True, test_output, ""

    def _run_single_test_lab1(
        self, remote_dir: str, test_name: str, program: str,
        timeout_secs: int = LAB1_TEST_TIMEOUT, verbose: bool = False,
    ) -> Tuple[bool, str, int, str]:
        """Run a single Lab 1 test on CLEAR.

        Returns (ran_ok, output, exit_code, error_message).
        Lab 1 tests are regular executables (not Yalnix programs).
        They need DISPLAY=:99 for the hardware emulator's X11 window.
        """
        # Run test under xvfb-run (auto-allocates display, cleans up after).
        # Each test gets its own Xvfb instance — no shared state issues.
        test_cmd = (
            f"cd {remote_dir} && "
            f"xvfb-run -a timeout {timeout_secs} ./{program} 2>&1; "
            f"echo '===EXIT_CODE===' && echo $?"
        )

        ssh_timeout = timeout_secs + 15
        rc, stdout, stderr = self._ssh_cmd(test_cmd, timeout=ssh_timeout)

        if rc == -1:
            return False, "", -1, "SSH timed out"

        # Parse exit code from output
        output = stdout
        exit_code = -1
        if "===EXIT_CODE===" in stdout:
            parts = stdout.split("===EXIT_CODE===")
            output = parts[0].strip()
            code_str = parts[1].strip().split("\n")[0].strip()
            try:
                exit_code = int(code_str)
            except ValueError:
                pass

        if verbose:
            if output:
                print(f"    Output: {output[:300]}")
            print(f"    Exit code: {exit_code}")

        return True, output, exit_code, ""

    def _run_single_test_lab2(
        self, remote_dir: str, test_name: str, program: str,
        timeout_secs: int = TEST_TIMEOUT, verbose: bool = False,
    ) -> Tuple[bool, str, str]:
        """Run a single Lab 2 test under Yalnix on CLEAR.

        Returns (success, output, error_message).
        Lab 2 (Yalnix Kernel) differs from Lab 3 (Yalnix FS):
        - No DISK/mkyfs (no filesystem)
        - Test program IS the init process (no separate kernel name arg)
        - printf output goes to yalnix stdout
        - TracePrintf output goes to TRACE file (enabled with -lk 0 -lu 0)
        - Combine stdout + TRACE + TTYLOG for check functions
        """
        # Clean state, run yalnix with test program
        # -lk 0 -lu 0: enable kernel+user TracePrintf to TRACE file
        # Start Xvfb for X11 terminal simulation, use script for pty
        # (Yalnix needs DISPLAY for terminals, and tcgetattr needs a tty)
        test_cmd = (
            f"cd {remote_dir} && "
            f"rm -f TTYLOG.* TRACE && "
            f"XDISP=:$$; Xvfb $XDISP -screen 0 1024x768x24 -ac &>/dev/null & "
            f"XPID=$!; sleep 0.5; "
            f'DISPLAY=$XDISP script -qc "timeout {timeout_secs} ./yalnix -lk 0 -lu 0 {program}" /dev/null 2>&1; '
            "yalnix_rc=$?; "
            "kill $XPID 2>/dev/null; wait $XPID 2>/dev/null; "
            # Read TRACE file (TracePrintf output)
            "echo '===TRACE_START===' && "
            "cat TRACE 2>/dev/null && "
            "echo '===TRACE_END===' && "
            # Read TTYLOG files
            "echo '===TTYLOG_START===' && "
            'for f in TTYLOG.*; do '
            '  if [ -f "$f" ]; then echo "==$f==" && cat "$f"; fi; '
            'done && '
            "echo '===TTYLOG_END===' && "
            "echo '===YALNIX_RC===' && echo $yalnix_rc"
        )

        ssh_timeout = timeout_secs + 15
        rc, stdout, stderr = self._ssh_cmd(test_cmd, timeout=ssh_timeout)

        # Parse TRACE file output (TracePrintf)
        trace_output = ""
        if "===TRACE_START===" in stdout:
            trace_section = stdout.split("===TRACE_START===")[1]
            if "===TRACE_END===" in trace_section:
                trace_section = trace_section.split("===TRACE_END===")[0]
            trace_output = trace_section.strip()

        # Parse TTYLOG output
        ttylog_output = ""
        if "===TTYLOG_START===" in stdout:
            ttylog_section = stdout.split("===TTYLOG_START===")[1]
            if "===TTYLOG_END===" in ttylog_section:
                ttylog_section = ttylog_section.split("===TTYLOG_END===")[0]
            ttylog_output = ttylog_section.strip()

        # Extract yalnix exit code
        yalnix_rc = None
        if "===YALNIX_RC===" in stdout:
            rc_section = stdout.split("===YALNIX_RC===")[1].strip().split("\n")[0].strip()
            try:
                yalnix_rc = int(rc_section)
            except ValueError:
                pass

        # Yalnix stdout before TRACE (printf output from test programs)
        yalnix_output = ""
        if "===TRACE_START===" in stdout:
            yalnix_output = stdout.split("===TRACE_START===")[0].strip()

        if verbose:
            if yalnix_output:
                print(f"    Yalnix output: {yalnix_output[:200]}")
            if yalnix_rc is not None and yalnix_rc != 0:
                print(f"    Yalnix exit code: {yalnix_rc}")
            if trace_output:
                print(f"    TRACE: {trace_output[:300]}")
            if ttylog_output:
                print(f"    TTYLOG: {ttylog_output[:300]}")

        if rc == -1:
            return False, "", "SSH timed out"

        # Build combined output from all sources:
        # 1. Yalnix stdout (printf from test programs)
        # 2. TRACE file (TracePrintf from kernel/user)
        # 3. TTYLOG files (TtyWrite/TtyPrintf)
        parts = []

        # Filter yalnix stdout: strip boot/shutdown banners
        if yalnix_output:
            lines = yalnix_output.split("\n")
            filtered = [
                line for line in lines
                if not line.strip().startswith("Booting")
                and not line.strip().startswith("** ")
            ]
            filtered_text = "\n".join(filtered).strip()
            if filtered_text:
                parts.append(filtered_text)

        # Filter TRACE: strip simulator boilerplate, keep user content.
        # These prefixes come from the Yalnix simulator's internal tracing,
        # not from student/test TracePrintf calls.
        _TRACE_BOILERPLATE = (
            "** ",              # Simulator banners (** Machine Halted **, etc.)
            "KernelStart:",     # Simulator KernelStart logging
            "cmd_args[",        # Simulator cmd_args dump
            "addr ",            # Simulator ExceptionInfo dump
            "info: vector",     # Simulator ExceptionInfo dump
            "regs[",            # Simulator register dump
            "Before ",          # Simulator phase markers
            "After ",           # Simulator phase markers
            "Done with ",       # Simulator phase markers
            ">>> ",             # Simulator phase markers
            "SetKernelBrk:",    # Simulator SetKernelBrk logging
            "InitMemory:",      # Simulator memory init logging
            "FreePage ",        # Simulator free page list dump
            "Done freeing",     # Simulator free page list dump
            "Kernel text ",     # Simulator page table dump
            "Kernel heap ",     # Simulator page table dump
            "Kernel stack ",    # Simulator page table dump
            "New Thread",       # Simulator thread creation
            "Init page_table",  # Simulator page table init
        )
        if trace_output:
            lines = trace_output.split("\n")
            filtered = []
            for line in lines:
                # Strip "Kernel   " or "User     " prefix from TracePrintf
                stripped = line.strip()
                if stripped.startswith("Kernel   "):
                    stripped = stripped[len("Kernel   "):]
                elif stripped.startswith("User     "):
                    stripped = stripped[len("User     "):]
                if any(stripped.startswith(p) for p in _TRACE_BOILERPLATE):
                    continue
                if stripped:
                    filtered.append(stripped)
            trace_text = "\n".join(filtered).strip()
            if trace_text:
                parts.append(trace_text)

        # TTYLOG.0 (test output from TtyWrite/TtyPrintf)
        ttylog_text = _extract_ttylog(ttylog_output, target="TTYLOG.0")
        if not ttylog_text:
            ttylog_text = _extract_all_ttylogs(ttylog_output)
        if ttylog_text:
            parts.append(ttylog_text)

        test_output = "\n".join(parts)
        return True, test_output, ""

    def run_tests(self, project_name: str, workspace_path: Path, verbose: bool = False) -> GradeResult:
        """Run all tests for a project via SSH to CLEAR."""
        config = self.load_config(project_name)

        result = GradeResult(
            project_name=project_name,
            display_name=get_display_name(config) or project_name.replace("_", " ").title(),
            course_code="comp421",
            project_number=get_assignment_number(config) or 0,
        )

        # Load test definitions from one or more modules.
        try:
            test_modules = self.load_test_modules(project_name)
        except FileNotFoundError as e:
            result.compilation_error = str(e)
            return result

        suite_manifest = load_suite_manifest(suite_dir=self.tests_dir if os.environ.get("BSCS_TEST_SUITE_DIR", "").strip() else None)
        test_defs: dict = {}
        test_sources: dict[str, str] = {}
        extra_programs: List[str] = []
        for test_module in test_modules:
            module_defs = getattr(test_module, "TEST_DEFINITIONS", {})
            module_file = getattr(test_module, "__file__", "")
            rel_path = ""
            if module_file:
                try:
                    rel_path = str(Path(module_file).resolve().relative_to(self.tests_dir.resolve()).as_posix())
                except Exception:
                    rel_path = Path(module_file).name
            module_source = source_from_manifest_path(suite_manifest, rel_path) if rel_path else None
            if not module_source:
                module_name = getattr(test_module, "__name__", "")
                if "_private" in module_name:
                    module_source = "private"
                elif "_agent" in module_name or "test_my_" in module_name:
                    module_source = "agent"
                else:
                    module_source = "public"
            if isinstance(module_defs, dict):
                for name, test_def in module_defs.items():
                    if name not in test_defs:
                        test_defs[name] = test_def
                        test_sources[name] = module_source
            module_extras = getattr(test_module, "EXTRA_PROGRAMS", [])
            if isinstance(module_extras, list):
                extra_programs.extend(str(p) for p in module_extras)

        if not test_defs:
            result.compilation_error = "No test definitions found"
            return result

        # Discover source files from workspace (agent creates all files from scratch)
        source_files = _discover_source_files(workspace_path)
        test_programs = sorted(set(d["program"] for d in test_defs.values()))
        extra_programs = sorted(set(extra_programs))

        remote_dir = self._remote_dir(workspace_path)
        is_lab1 = project_name == "lab1"
        is_lab2 = project_name == "lab2"

        if verbose:
            print(f"SSH target: {self.ssh_target}")
            print(f"Remote dir: {remote_dir}")
            print(f"Project: {project_name}" + (" (Lab 1 mode)" if is_lab1 else " (Lab 2 mode)" if is_lab2 else ""))
            print()

        # Step 1: Upload files
        if verbose:
            print("Uploading files to CLEAR...")
        success, error = self._upload_files(
            project_name, workspace_path, remote_dir, source_files, test_programs,
            extra_programs=extra_programs, verbose=verbose,
        )
        if not success:
            result.compilation_error = f"Upload failed: {error}"
            self._cleanup(remote_dir, verbose)
            return result

        # Step 2: Compile
        if verbose:
            print("Compiling on CLEAR...")
        if is_lab1:
            success, error = self._compile_lab1(remote_dir, verbose)
        elif is_lab2:
            success, error = self._compile_lab2(remote_dir, test_programs, extra_programs, verbose)
        else:
            success, error = self._compile_all(remote_dir, test_programs, verbose)
        if not success:
            result.compilation_error = error
            self._cleanup(remote_dir, verbose)
            return result

        if verbose:
            print("Compilation successful.")
            print()

        # Step 3: Run each test
        for test_name, test_def in sorted(test_defs.items()):
            program = test_def["program"]
            points = test_def["points"]
            check_fn = test_def["check"]
            description = test_def.get("description", "")
            test_source = test_sources.get(test_name, "public")

            if verbose:
                print(f"Running {test_name} ({program})...")

            if is_lab1:
                # Lab 1: run directly, check with (output, exit_code)
                test_timeout = test_def.get("timeout", LAB1_TEST_TIMEOUT)
                ran_ok, test_output, exit_code, ssh_error = self._run_single_test_lab1(
                    remote_dir, test_name, program,
                    timeout_secs=test_timeout, verbose=verbose,
                )

                if not ran_ok:
                    result.test_results.append(TestResult(
                        name=test_name,
                        passed=False,
                        points=0,
                        max_points=points,
                        error_message=ssh_error or "Failed to run test",
                        input_description=description,
                        source=test_source,
                    ))
                    continue

                passed, error_msg = check_fn(test_output, exit_code)
            elif is_lab2:
                # Lab 2: run under Yalnix, check with (output,)
                test_timeout = test_def.get("timeout", TEST_TIMEOUT)
                ran_ok, test_output, ssh_error = self._run_single_test_lab2(
                    remote_dir, test_name, program,
                    timeout_secs=test_timeout, verbose=verbose,
                )

                if not ran_ok:
                    result.test_results.append(TestResult(
                        name=test_name,
                        passed=False,
                        points=0,
                        max_points=points,
                        error_message=ssh_error or "Failed to run test",
                        input_description=description,
                        source=test_source,
                    ))
                    continue

                passed, error_msg = check_fn(test_output)
            else:
                # Lab 3: run under Yalnix, check with (output,)
                needs_timeout = test_def.get("needs_timeout", False)
                ran_ok, test_output, ssh_error = self._run_single_test(
                    remote_dir, test_name, program, needs_timeout, verbose,
                )

                if not ran_ok:
                    result.test_results.append(TestResult(
                        name=test_name,
                        passed=False,
                        points=0,
                        max_points=points,
                        error_message=ssh_error or "Failed to run test",
                        input_description=description,
                        source=test_source,
                    ))
                    continue

                passed, error_msg = check_fn(test_output)

            result.test_results.append(TestResult(
                name=test_name,
                passed=passed,
                points=points if passed else 0,
                max_points=points,
                actual=test_output[:200] if not passed else "",
                error_message=error_msg if not passed else None,
                input_description=description,
                source=test_source,
            ))

            if verbose:
                status = "PASS" if passed else "FAIL"
                print(f"  [{status}] {test_name}")
                if not passed and error_msg:
                    print(f"    Error: {error_msg}")
                print()
        # Step 4: Cleanup
        self._cleanup(remote_dir, verbose)

        return result

    def _cleanup(self, remote_dir: str, verbose: bool = False):
        """Remove remote temp directory."""
        rc, _, stderr = self._ssh_cmd(f"rm -rf {remote_dir}", timeout=30)
        if rc != 0 and verbose:
            print(f"  Warning: cleanup failed for {remote_dir}: {stderr}", file=sys.stderr)
        # Remove parent .bscs_bench/ if now empty
        self._ssh_cmd("rmdir .bscs_bench 2>/dev/null || true", timeout=10)


def _discover_source_files(workspace: Path) -> List[str]:
    """Find all uploadable source files in the workspace.

    The agent creates all .c, .h, and Makefile files from scratch (no hardcoded
    list), so we glob the workspace for anything relevant.
    """
    patterns = ["*.c", "*.h", "Makefile"]
    result = []
    for pattern in patterns:
        for fpath in workspace.glob(pattern):
            result.append(fpath.name)
    return sorted(set(result))


def _shell_quote(s: str) -> str:
    """Quote a string for safe use in shell command."""
    return "'" + s.replace("'", "'\"'\"'") + "'"


def _extract_ttylog(ttylog_output: str, target: str = "TTYLOG.1") -> str:
    """Extract content from a specific TTYLOG file in the combined output."""
    marker = f"=={target}=="
    if marker not in ttylog_output:
        return ""

    parts = ttylog_output.split(marker)
    if len(parts) < 2:
        return ""

    content = parts[1]
    # Find the next TTYLOG marker or end
    for i, line in enumerate(content.split("\n")):
        if line.startswith("==TTYLOG.") and line.endswith("=="):
            content = "\n".join(content.split("\n")[:i])
            break

    return content.strip()


def _extract_all_ttylogs(ttylog_output: str) -> str:
    """Extract all TTYLOG content, combining all files."""
    lines = []
    in_ttylog = False
    for line in ttylog_output.split("\n"):
        if line.startswith("==TTYLOG.") and line.endswith("=="):
            in_ttylog = True
            continue
        if in_ttylog:
            lines.append(line)
    return "\n".join(lines).strip()


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(description="COMP 421 SSH-based Autograder")
    parser.add_argument("project", type=str, help="Project name (e.g., lab3)")
    parser.add_argument("workspace", type=str, help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true", help="Show detailed output")
    parser.add_argument("--json", action="store_true", help="Output as JSON")
    parser.add_argument("--course", type=str, default=None, help="Path to course directory")
    parser.add_argument("--netid", type=str, default=None, help="Rice NetID for SSH (or set CLEAR_NETID env var)")

    args = parser.parse_args()

    # Find comp421 directory
    if args.course:
        comp421_dir = Path(args.course).resolve()
    else:
        comp421_dir = Path(__file__).parent

    runner = SSHRunner(comp421_dir, netid=args.netid)

    workspace_path = Path(args.workspace).resolve()
    if not workspace_path.exists():
        print(f"Error: Workspace not found: {workspace_path}", file=sys.stderr)
        sys.exit(1)

    result = runner.run_tests(args.project, workspace_path, args.verbose)

    if args.json:
        print(json.dumps(result.to_dict(), indent=2))
    else:
        print(result.format_output(args.verbose))

    # Exit with error code if tests failed
    if result.compilation_error or result.passed_count < result.total_count:
        sys.exit(1)


if __name__ == "__main__":
    main()

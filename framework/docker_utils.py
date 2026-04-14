"""
Shared Docker utilities for course test runners.

Provides common Docker operations used by COMP 140, COMP 215, COMP 321, and COMP 322 runners.
"""

import os
import platform
import shlex
import subprocess
import sys
import uuid
from pathlib import Path
from typing import Dict, List, Optional, Tuple


def is_in_container() -> bool:
    """Check if running inside a pre-provisioned benchmark container.

    When BSCS_IN_CONTAINER=1 is set, test runners should execute commands
    locally via subprocess instead of launching Docker containers.
    """
    return os.environ.get("BSCS_IN_CONTAINER") == "1"


def docker_image_exists(image_name: str) -> bool:
    """
    Check if a Docker image already exists.

    Args:
        image_name: Name of the Docker image to check

    Returns:
        True if image exists, False otherwise
    """
    try:
        result = subprocess.run(
            ["docker", "images", "-q", image_name],
            capture_output=True,
            text=True,
            timeout=10
        )
        return bool(result.stdout.strip())
    except subprocess.TimeoutExpired:
        return False  # Timeout checking - assume doesn't exist
    except FileNotFoundError:
        return False  # Docker not installed
    except Exception as e:
        print(f"Warning: Error checking Docker image: {e}", file=sys.stderr)
        return False


def run_local(
    cmd: List[str],
    timeout: int = 300,
    cwd: Optional[Path] = None,
    env: Optional[Dict[str, str]] = None,
) -> Tuple[int, str, str]:
    """
    Run a command locally via subprocess.

    Mirrors the return signature of run_in_container() for easy substitution
    when running inside a pre-provisioned benchmark container.

    Args:
        cmd: Command and arguments to run
        timeout: Timeout in seconds
        cwd: Working directory (optional)
        env: Environment variables (optional, defaults to os.environ)

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=timeout,
            cwd=cwd,
            env=env,
        )
        return result.returncode, result.stdout, result.stderr
    except subprocess.TimeoutExpired:
        return -1, "", f"Execution timed out after {timeout}s"
    except Exception as e:
        return -1, "", f"Execution error: {str(e)}"


def ensure_docker_image(
    image_name: str,
    dockerfile_dir: Path,
    build_timeout: int = 300,
    force_rebuild: bool = False
) -> Tuple[bool, str]:
    """
    Build Docker image if not already built.

    When running inside a pre-provisioned benchmark container (BSCS_IN_CONTAINER=1),
    Docker is not available so this returns success immediately.

    Args:
        image_name: Name/tag for the Docker image
        dockerfile_dir: Directory containing the Dockerfile
        build_timeout: Timeout in seconds for docker build (default 5 minutes)
        force_rebuild: Force rebuild even if image exists

    Returns:
        Tuple of (success, error_message)
    """
    if is_in_container():
        return True, ""

    if not force_rebuild and docker_image_exists(image_name):
        return True, ""

    dockerfile = dockerfile_dir / "Dockerfile"
    if not dockerfile.exists():
        return False, f"Dockerfile not found: {dockerfile}"

    print(f"Building Docker image {image_name}...", file=sys.stderr)
    cmd = [
        "docker", "build",
        "-t", image_name,
        "-f", str(dockerfile),
        str(dockerfile_dir)
    ]

    try:
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=build_timeout
        )
        if result.returncode != 0:
            return False, f"Docker build failed: {result.stderr}"
        return True, ""
    except subprocess.TimeoutExpired:
        return False, "Docker build timed out"
    except FileNotFoundError:
        return False, "Docker not found. Please install Docker or use --no-docker flag."
    except Exception as e:
        return False, f"Docker build error: {str(e)}"


def run_in_container(
    image: str,
    command: List[str],
    workspace: Path,
    timeout: int = 300,
    memory: str = "2g",
    cpus: str = "2",
    network: str = "none",
    extra_volumes: Optional[Dict[str, str]] = None,
    env: Optional[Dict[str, str]] = None,
    stdin_input: Optional[str] = None,
    workdir: str = "/workspace",
    entrypoint: Optional[str] = None,
) -> Tuple[int, str, str]:
    """
    Run a command inside a Docker container.

    Args:
        image: Docker image name
        command: Command and arguments to run
        workspace: Host path to mount as /workspace
        timeout: Timeout in seconds
        memory: Memory limit (e.g., "2g", "4g")
        cpus: CPU limit (e.g., "2")
        network: Network mode ("none", "bridge", "host")
        extra_volumes: Additional volume mounts {host_path: container_path}
        env: Environment variables to set
        stdin_input: Input to provide on stdin
        workdir: Working directory inside container
        entrypoint: Optional entrypoint override

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    cmd = [
        "docker", "run", "--rm",
        "--memory", memory,
        "--cpus", cpus,
        f"--network={network}",
        "-v", f"{workspace.resolve()}:{workdir}",
        "-w", workdir,
    ]

    # On macOS, /var/folders (used by tempfile) resolves through /private/var.
    # Mount just /private/var/folders so test temp files are accessible inside the container.
    if platform.system() == "Darwin":
        cmd.extend(["-v", "/private/var/folders:/var/folders"])

    # Add stdin flag if input provided
    if stdin_input is not None:
        cmd.insert(2, "-i")

    # Add extra volumes
    if extra_volumes:
        for host_path, container_path in extra_volumes.items():
            cmd.extend(["-v", f"{host_path}:{container_path}"])

    # Override entrypoint when requested
    if entrypoint:
        cmd.extend(["--entrypoint", entrypoint])

    # Add environment variables
    if env:
        for key, value in env.items():
            cmd.extend(["-e", f"{key}={value}"])

    container_name = f"bscs-{uuid.uuid4().hex[:12]}"
    cmd.extend(["--name", container_name])
    cmd.append(image)
    cmd.extend(command)

    proc = subprocess.Popen(
        cmd,
        stdin=subprocess.PIPE if stdin_input is not None else subprocess.DEVNULL,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )
    try:
        stdout, stderr = proc.communicate(input=stdin_input, timeout=timeout)
        return proc.returncode, stdout, stderr
    except subprocess.TimeoutExpired:
        subprocess.run(
            ["docker", "stop", "--time=5", container_name],
            capture_output=True, timeout=15
        )
        proc.kill()
        proc.communicate()
        return -1, "", f"Container execution timed out after {timeout}s"
    except Exception as e:
        proc.kill()
        proc.communicate()
        return -1, "", f"Docker error: {str(e)}"


def run_bash_in_container(
    image: str,
    script: str,
    workspace: Path,
    timeout: int = 300,
    memory: str = "2g",
    cpus: str = "2",
    network: str = "none",
    extra_volumes: Optional[Dict[str, str]] = None,
    env: Optional[Dict[str, str]] = None,
    stdin_input: Optional[str] = None,
    override_entrypoint: bool = False,
) -> Tuple[int, str, str]:
    """
    Run a bash script inside a Docker container.

    Convenience wrapper around run_in_container for bash scripts.

    Args:
        image: Docker image name
        script: Bash script to execute
        workspace: Host path to mount as /workspace
        timeout: Timeout in seconds
        memory: Memory limit
        cpus: CPU limit
        network: Network mode
        extra_volumes: Additional volume mounts
        env: Environment variables
        stdin_input: Input to provide on stdin
        override_entrypoint: If true, force container entrypoint to bash

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    command = ["-c", script] if override_entrypoint else ["bash", "-c", script]

    return run_in_container(
        image=image,
        command=command,
        workspace=workspace,
        timeout=timeout,
        memory=memory,
        cpus=cpus,
        network=network,
        extra_volumes=extra_volumes,
        env=env,
        stdin_input=stdin_input,
        entrypoint="bash" if override_entrypoint else None,
    )


def run_program_in_docker(
    workspace: Path,
    executable: str,
    image: str,
    args: Optional[List[str]] = None,
    stdin_input: Optional[str] = None,
    timeout: int = 60,
    network: bool = False,
    memory: str = "4g",
    cpus: str = "2"
) -> Tuple[int, str, str]:
    """
    Run an executable inside Docker container.

    Args:
        workspace: Directory containing the executable (mounted as /workspace)
        executable: Name of executable relative to workspace
        image: Docker image to use
        args: Command-line arguments
        stdin_input: Input to provide on stdin
        timeout: Timeout in seconds
        network: Whether to enable network access
        memory: Memory limit
        cpus: CPU limit

    Returns:
        Tuple of (return_code, stdout, stderr)
    """
    # Build the command to run inside container
    exe_cmd = f"./{executable}"
    if args:
        # Use shlex.quote for proper shell escaping
        exe_cmd += " " + " ".join(shlex.quote(arg) for arg in args)

    return run_bash_in_container(
        image=image,
        script=exe_cmd,
        workspace=workspace,
        timeout=timeout,
        memory=memory,
        cpus=cpus,
        network="bridge" if network else "none",
        stdin_input=stdin_input
    )


def compile_in_docker(
    image: str,
    workspace: Path,
    build_cmd: str = "make",
    timeout: int = 120,
    memory: str = "4g",
    cpus: str = "2",
    network: bool = False,
    extra_volumes: Optional[Dict[str, str]] = None
) -> Tuple[bool, str]:
    """
    Compile a project inside Docker container using make or custom command.

    Args:
        image: Docker image name
        workspace: Directory containing source files (mounted as /workspace)
        build_cmd: Build command to run (default: "make")
        timeout: Compilation timeout in seconds
        memory: Memory limit
        cpus: CPU limit
        network: Whether to enable network access
        extra_volumes: Additional volume mounts {host_path: container_path}

    Returns:
        Tuple of (success, error_message)
    """
    returncode, stdout, stderr = run_bash_in_container(
        image=image,
        script=build_cmd,
        workspace=workspace,
        timeout=timeout,
        memory=memory,
        cpus=cpus,
        network="bridge" if network else "none",
        extra_volumes=extra_volumes
    )

    if returncode != 0:
        return False, stderr or stdout
    return True, ""


def run_python_tests_in_docker(
    workspace: Path,
    course_dir: Path,
    docker_image: str,
    build_timeout: int = 120,
    test_timeout: int = 300,
    verbose: bool = False,
    cli_args: Optional[List[str]] = None,
) -> Dict[str, object]:
    """
    Run framework CLI tests inside Docker (or locally if in container).

    Shared implementation for Python course docker runners (COMP 140, COMP 182, etc.).

    Args:
        workspace: Path to the student workspace directory
        course_dir: Path to the course directory (e.g. comp140/, comp182/)
        docker_image: Docker image name (e.g. "comp140-runner")
        build_timeout: Timeout for Docker image build in seconds
        test_timeout: Timeout for test execution in seconds
        verbose: Whether to show detailed output
        cli_args: Additional CLI arguments for framework.cli

    Returns:
        Dict with keys: success, stdout, stderr, error
    """
    project_root = course_dir.parent

    if is_in_container():
        # Already inside a pre-provisioned benchmark container — run framework CLI directly.
        command = ["python3", "-m", "framework.cli", str(workspace.resolve())]
        if cli_args:
            command.extend(cli_args)

        env = os.environ.copy()
        env["PYTHONPATH"] = str(project_root.resolve())

        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        if suite_override:
            env["BSCS_TEST_SUITE_DIR"] = suite_override

        returncode, stdout, stderr = run_local(
            command, timeout=test_timeout, cwd=workspace, env=env,
        )

        if verbose:
            if stdout:
                print("STDOUT:", stdout, file=sys.stderr)
            if stderr:
                print("STDERR:", stderr, file=sys.stderr)

        return {
            "success": returncode == 0,
            "stdout": stdout,
            "stderr": stderr,
            "error": None if returncode == 0 else f"Exit code: {returncode}",
        }

    # Ensure Docker image exists
    success, error = ensure_docker_image(docker_image, course_dir, build_timeout)
    if not success:
        return {"success": False, "error": error, "stdout": "", "stderr": ""}

    # Run framework CLI inside container
    command = ["python3", "-m", "framework.cli", "/workspace"]
    if cli_args:
        command.extend(cli_args)

    extra_volumes: Dict[str, str] = {
        str(project_root.resolve()): "/project:ro",
    }

    suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
    env: Dict[str, str] = {"PYTHONPATH": "/project"}
    if suite_override:
        suite_path = Path(suite_override).resolve()
        if str(suite_path).startswith(str(workspace.resolve())):
            rel = suite_path.relative_to(workspace.resolve())
            env["BSCS_TEST_SUITE_DIR"] = f"/workspace/{rel.as_posix()}"
        else:
            extra_volumes[str(suite_path)] = "/suite_tests:ro"
            env["BSCS_TEST_SUITE_DIR"] = "/suite_tests"

    returncode, stdout, stderr = run_in_container(
        image=docker_image,
        command=command,
        workspace=workspace,
        timeout=test_timeout,
        memory="2g",
        cpus="2",
        network="none",
        extra_volumes=extra_volumes,
        env=env,
    )

    if verbose:
        if stdout:
            print("STDOUT:", stdout, file=sys.stderr)
        if stderr:
            print("STDERR:", stderr, file=sys.stderr)

    return {
        "success": returncode == 0,
        "stdout": stdout,
        "stderr": stderr,
        "error": None if returncode == 0 else f"Exit code: {returncode}",
    }

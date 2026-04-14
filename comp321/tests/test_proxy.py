"""
Tests for Project 6: Web Proxy

Tests the following functionality:
- Basic proxy functionality (forwarding requests)
- Logging to proxy.log
- Error handling for unreachable servers
- Concurrent request handling

Test cases derived from grading feedback.
"""

import os
import re
import socket
import sys
import subprocess
import threading
import time
import uuid
from pathlib import Path
from urllib.parse import urlparse

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, run_program, compile_c_program,
    USE_DOCKER, DOCKER_IMAGE
)

# Point values for each test
TEST_POINTS = {
    # Basic functionality tests
    "test_neverssl": 5,
    "test_cmu_conglonl": 5,
    "test_httpforever": 5,
    "test_csapp": 5,
    "test_squid_cache": 5,
    "test_openoffice": 5,
    "test_unm": 5,
    "test_washington": 5,
    # Error handling
    "test_nonexistent_domain": 5,
    # Logging tests
    "test_log_format": 5,
    "test_log_size": 5,
    # Concurrency tests
    "test_concurrent_basic": 10,
    "test_concurrent_multiple": 10,
}

# Test URLs (HTTP only - proxy doesn't support HTTPS)
TEST_URLS = {
    "neverssl": "http://neverssl.com/",
    "cmu_conglonl": "http://www.cs.cmu.edu/~conglonl/",
    "httpforever": "http://httpforever.com/",
    "csapp": "http://csapp.cs.cmu.edu/",
    "squid_cache": "http://www.squid-cache.org/",
    "openoffice": "http://www.openoffice.org/",
    "unm": "http://www.unm.edu/",
    "washington": "http://www.washington.edu/",
    "nonexistent": "http://www.ashfjasdhfdlasfkljdas.com/",
}


def find_free_port() -> int:
    """Find an available port for the proxy."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind(('', 0))
        s.listen(1)
        port = s.getsockname()[1]
    return port


class DockerProxyProcess:
    """Wraps a Docker container running the proxy, mimicking subprocess.Popen interface."""

    def __init__(self, container_name: str):
        self.container_name = container_name

    def poll(self):
        """Check if container is still running. Returns None if running, 1 if stopped."""
        try:
            result = subprocess.run(
                ["docker", "inspect", "-f", "{{.State.Running}}", self.container_name],
                capture_output=True, text=True, timeout=5
            )
            if "true" in result.stdout:
                return None
            return 1
        except Exception:
            return 1

    def terminate(self):
        try:
            subprocess.run(["docker", "stop", "-t", "2", self.container_name],
                           capture_output=True, timeout=10)
        except Exception:
            pass

    def wait(self, timeout=None):
        try:
            subprocess.run(["docker", "rm", "-f", self.container_name],
                           capture_output=True, timeout=timeout or 10)
        except Exception:
            pass

    def kill(self):
        try:
            subprocess.run(["docker", "rm", "-f", self.container_name],
                           capture_output=True, timeout=10)
        except Exception:
            pass


def start_proxy(workspace: Path, port: int):
    """
    Start the proxy server.

    Args:
        workspace: Path to workspace containing proxy executable
        port: Port to listen on

    Returns:
        Popen-like object for the proxy process
    """
    proxy_exe = workspace / "proxy"
    if not proxy_exe.exists():
        compile_c_program(workspace)

    if USE_DOCKER:
        # Start proxy in Docker with port forwarding
        container_name = f"proxy-test-{uuid.uuid4().hex[:8]}"
        cmd = [
            "docker", "run", "-d",
            "--name", container_name,
            "--memory", "4g",
            "--cpus", "2",
            "--network=bridge",
            "-p", f"{port}:{port}",
            "-v", f"{workspace.resolve()}:/workspace",
            "-w", "/workspace",
            DOCKER_IMAGE,
            "./proxy", str(port)
        ]
        result = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
        if result.returncode != 0:
            raise RuntimeError(f"Failed to start proxy in Docker: {result.stderr}")

        # Give it time to start
        time.sleep(1.0)
        return DockerProxyProcess(container_name)
    else:
        # Host execution
        proc = subprocess.Popen(
            [str(proxy_exe), str(port)],
            cwd=str(workspace),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )

        # Give it time to start
        time.sleep(0.5)
        return proc


def fetch_through_proxy(url: str, proxy_host: str, proxy_port: int, timeout: int = 30) -> tuple:
    """
    Fetch a URL through the proxy.

    Args:
        url: URL to fetch
        proxy_host: Proxy host
        proxy_port: Proxy port
        timeout: Request timeout

    Returns:
        Tuple of (success, response_body, response_size)
    """
    try:
        parsed = urlparse(url)
        host = parsed.hostname
        path = parsed.path or "/"
        if parsed.query:
            path += "?" + parsed.query

        # Connect to proxy
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout)
        sock.connect((proxy_host, proxy_port))

        # Send HTTP request
        request = f"GET {url} HTTP/1.0\r\nHost: {host}\r\n\r\n"
        sock.sendall(request.encode())

        # Receive response
        response = b""
        while True:
            try:
                data = sock.recv(4096)
                if not data:
                    break
                response += data
            except socket.timeout:
                break

        sock.close()

        # Parse response
        if b"\r\n\r\n" in response:
            headers, body = response.split(b"\r\n\r\n", 1)
            return True, body, len(response)
        else:
            return True, response, len(response)

    except Exception as e:
        return False, str(e).encode(), 0


def _run_proxy_test(workspace: Path, url: str, check_log: bool = True) -> 'TestResult':
    """
    Run a proxy test for a given URL.

    Args:
        workspace: Path to workspace
        url: URL to fetch through proxy
        check_log: Whether to verify log entry

    Returns:
        TestResult
    """
    proxy_exe = workspace / "proxy"

    # Compile if needed
    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    # Find free port
    port = find_free_port()

    # Start proxy
    proxy_proc = None
    try:
        proxy_proc = start_proxy(workspace, port)

        # Check if proxy started
        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        # Fetch through proxy
        success, body, size = fetch_through_proxy(url, "localhost", port)

        if not success:
            return make_test_result(False, "fetch succeeded", body.decode('utf-8', errors='replace'), None)

        # Check log if requested
        if check_log:
            log_path = workspace / "proxy.log"
            time.sleep(0.5)  # Give time for log write

            if log_path.exists():
                log_content = log_path.read_text()
                if url not in log_content:
                    return make_test_result(
                        False,
                        f"URL in log: {url}",
                        f"Log content: {log_content[:200]}",
                        None
                    )
            else:
                return make_test_result(False, "proxy.log exists", "no log file", None)

        # Success
        return make_test_result(
            True,
            f"fetched {len(body)} bytes",
            f"fetched {len(body)} bytes, total {size}",
            None
        )

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()


# ============================================================================
# Basic Functionality Tests
# ============================================================================

def test_neverssl(workspace):
    """Test fetching neverssl.com through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["neverssl"])

test_neverssl.input_description = "http://neverssl.com/"


def test_cmu_conglonl(workspace):
    """Test fetching CMU page through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["cmu_conglonl"])

test_cmu_conglonl.input_description = "http://www.cs.cmu.edu/~conglonl/"


def test_httpforever(workspace):
    """Test fetching httpforever.com through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["httpforever"])

test_httpforever.input_description = "http://httpforever.com/"



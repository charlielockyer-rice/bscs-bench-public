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

def test_csapp(workspace):
    """Test fetching CS:APP book site through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["csapp"])

test_csapp.input_description = "http://csapp.cs.cmu.edu/"


def test_squid_cache(workspace):
    """Test fetching squid-cache.org through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["squid_cache"])

test_squid_cache.input_description = "http://www.squid-cache.org/"


def test_openoffice(workspace):
    """Test fetching openoffice.org through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["openoffice"])

test_openoffice.input_description = "http://www.openoffice.org/"


def test_unm(workspace):
    """Test fetching UNM site through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["unm"])

test_unm.input_description = "http://www.unm.edu/"


def test_washington(workspace):
    """Test fetching UW site through proxy."""
    return _run_proxy_test(workspace, TEST_URLS["washington"])

test_washington.input_description = "http://www.washington.edu/"


# ============================================================================
# Error Handling Tests
# ============================================================================

def test_nonexistent_domain(workspace):
    """Test handling of non-existent domain."""
    proxy_exe = workspace / "proxy"

    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    port = find_free_port()
    proxy_proc = None

    try:
        proxy_proc = start_proxy(workspace, port)

        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        # Try to fetch non-existent domain
        success, body, size = fetch_through_proxy(
            TEST_URLS["nonexistent"],
            "localhost",
            port
        )

        # Proxy should return an error page, not crash
        if proxy_proc.poll() is None:
            # Proxy still running - good
            return make_test_result(True, "proxy handles error", f"returned {size} bytes", None)
        else:
            return make_test_result(False, "proxy still running", "proxy crashed", None)

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()

test_nonexistent_domain.input_description = "non-existent domain"


# ============================================================================
# Logging Tests
# ============================================================================

def test_log_format(workspace):
    """Test log entry format."""
    proxy_exe = workspace / "proxy"

    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    port = find_free_port()
    proxy_proc = None
    log_path = workspace / "proxy.log"

    # Remove old log
    if log_path.exists():
        log_path.unlink()

    try:
        proxy_proc = start_proxy(workspace, port)

        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        # Fetch a URL
        success, body, size = fetch_through_proxy(
            TEST_URLS["neverssl"],
            "localhost",
            port
        )

        time.sleep(0.5)

        # Check log format
        if not log_path.exists():
            return make_test_result(False, "log exists", "no proxy.log", None)

        log_content = log_path.read_text().strip()

        # Expected format: "Date: browserIP URL size"
        # Example: "Thu 16 Apr 2020 20:06:08 CDT: 127.0.0.1 http://neverssl.com/ 2584"

        if ":" not in log_content or "http" not in log_content:
            return make_test_result(
                False,
                "proper log format",
                log_content[:200],
                None
            )

        return make_test_result(True, "log format correct", log_content[:200], None)

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()

test_log_format.input_description = "log entry format verification"


def test_log_size(workspace):
    """Test that log records correct response size."""
    proxy_exe = workspace / "proxy"

    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    port = find_free_port()
    proxy_proc = None
    log_path = workspace / "proxy.log"

    if log_path.exists():
        log_path.unlink()

    try:
        proxy_proc = start_proxy(workspace, port)

        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        # Fetch a URL
        success, body, size = fetch_through_proxy(
            TEST_URLS["neverssl"],
            "localhost",
            port
        )

        time.sleep(0.5)

        if not log_path.exists():
            return make_test_result(False, "log exists", "no proxy.log", None)

        log_content = log_path.read_text().strip()

        # Extract size from log (last number on line)
        match = re.search(r'(\d+)\s*$', log_content)
        if match:
            logged_size = int(match.group(1))
            # Size should be reasonable (within tolerance)
            if abs(logged_size - size) < 100:  # Allow small difference
                return make_test_result(True, f"size ~{size}", f"logged {logged_size}", None)
            else:
                return make_test_result(False, f"size ~{size}", f"logged {logged_size}", None)
        else:
            return make_test_result(False, "size in log", log_content[:200], None)

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()

test_log_size.input_description = "log size accuracy"


# ============================================================================
# Concurrency Tests
# ============================================================================

def test_concurrent_basic(workspace):
    """Test basic concurrent handling."""
    proxy_exe = workspace / "proxy"

    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    port = find_free_port()
    proxy_proc = None

    try:
        proxy_proc = start_proxy(workspace, port)

        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        # Open a connection but don't send request
        blocking_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        blocking_sock.connect(("localhost", port))

        # Now try to fetch through a second connection
        success, body, size = fetch_through_proxy(
            TEST_URLS["neverssl"],
            "localhost",
            port,
            timeout=10
        )

        blocking_sock.close()

        if success and size > 0:
            return make_test_result(True, "concurrent fetch succeeded", f"{size} bytes", None)
        else:
            return make_test_result(False, "concurrent fetch", "blocked or failed", None)

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()

test_concurrent_basic.input_description = "basic concurrency"


def test_concurrent_multiple(workspace):
    """Test multiple concurrent requests."""
    proxy_exe = workspace / "proxy"

    if not proxy_exe.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    port = find_free_port()
    proxy_proc = None

    try:
        proxy_proc = start_proxy(workspace, port)

        if proxy_proc.poll() is not None:
            return make_test_result(False, "proxy running", "proxy failed to start", None)

        results = []

        def fetch_url(url):
            try:
                success, body, size = fetch_through_proxy(url, "localhost", port, timeout=30)
                results.append((url, success, size))
            except Exception as e:
                results.append((url, False, 0))

        # Start multiple fetches
        threads = []
        urls = [TEST_URLS["neverssl"], TEST_URLS["cmu_conglonl"]]
        for url in urls:
            t = threading.Thread(target=fetch_url, args=(url,))
            t.start()
            threads.append(t)

        # Wait for all
        for t in threads:
            t.join(timeout=60)

        # Check results
        successes = sum(1 for _, success, _ in results if success)
        if successes >= 2:
            return make_test_result(True, "all concurrent fetches", f"{successes}/{len(urls)} succeeded", None)
        else:
            return make_test_result(False, "all concurrent fetches", f"{successes}/{len(urls)} succeeded", None)

    except Exception as e:
        return make_test_result(False, "test completed", str(e), None)

    finally:
        if proxy_proc:
            proxy_proc.terminate()
            try:
                proxy_proc.wait(timeout=5)
            except subprocess.TimeoutExpired:
                proxy_proc.kill()

test_concurrent_multiple.input_description = "multiple concurrent requests"

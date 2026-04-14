"""
Sandboxed module importing for the COMP 140 evaluation framework.

Provides safe importing of student code without allowing access to:
- Test implementations
- Solution files
- Other sensitive paths
"""

import sys
import os
import importlib.util
import types
import threading
import logging
from pathlib import Path
from typing import Optional, Dict, Any
import signal
from contextlib import contextmanager


logger = logging.getLogger(__name__)


class ExecutionTimeoutError(Exception):
    """Raised when code execution times out."""
    pass


class ModuleImportError(Exception):
    """Raised when module import fails."""
    pass


@contextmanager
def timeout_context(seconds: float):
    """
    Context manager for timing out code execution.

    Args:
        seconds: Maximum execution time in seconds

    Raises:
        ExecutionTimeoutError: If execution exceeds the timeout

    Note:
        On Unix systems, uses SIGALRM for precise timeout control.
        On Windows, uses a thread-based timeout which cannot interrupt
        blocking operations but will raise after the timeout period.
    """
    # Use signal-based timeout on Unix systems (more reliable)
    if hasattr(signal, 'SIGALRM'):
        def timeout_handler(signum, frame):
            raise ExecutionTimeoutError(f"Execution timed out after {seconds} seconds")

        old_handler = signal.signal(signal.SIGALRM, timeout_handler)
        signal.setitimer(signal.ITIMER_REAL, seconds)
        try:
            yield
        finally:
            signal.setitimer(signal.ITIMER_REAL, 0)
            signal.signal(signal.SIGALRM, old_handler)
    else:
        # Windows: use thread-based timeout
        # This approach cannot interrupt blocking C calls or I/O operations,
        # but it will raise after the timeout period completes.
        timeout_occurred = threading.Event()
        result_container = {"exception": None, "completed": False}

        def check_timeout():
            if not result_container["completed"]:
                timeout_occurred.set()

        timer = threading.Timer(seconds, check_timeout)
        timer.daemon = True
        timer.start()

        try:
            yield
            result_container["completed"] = True
        finally:
            timer.cancel()

        if timeout_occurred.is_set():
            raise ExecutionTimeoutError(f"Execution timed out after {seconds} seconds")


class SafeImporter:
    """
    Safely imports student code while preventing access to test implementations.

    This class:
    1. Loads student code from a specific path
    2. Sets up the lib path for helper modules
    3. Blocks imports from eval/tests/ directory
    4. Provides timeout protection for imports
    """

    # Paths that should be blocked from import
    BLOCKED_PATHS = [
        "eval/tests",
        "eval/rubrics",
        "solutions",
    ]

    def __init__(self, workspace_path: Path, lib_path: Optional[Path] = None):
        """
        Initialize the safe importer.

        Args:
            workspace_path: Path to the agent's workspace
            lib_path: Path to the lib directory (defaults to workspace/lib or project/lib)
        """
        self.workspace_path = Path(workspace_path)

        # Determine lib path
        if lib_path:
            self.lib_path = Path(lib_path)
        elif (self.workspace_path / "lib").exists():
            self.lib_path = self.workspace_path / "lib"
        else:
            # Look for lib in the project root
            project_root = Path(__file__).parent.parent
            self.lib_path = project_root / "lib"

        self._original_meta_path = None
        self._loaded_modules: Dict[str, types.ModuleType] = {}

    def _is_blocked_path(self, path: str) -> bool:
        """Check if a path should be blocked from import."""
        path_lower = path.lower().replace("\\", "/")
        for blocked in self.BLOCKED_PATHS:
            if blocked.lower() in path_lower:
                return True
        return False

    def _setup_import_hooks(self):
        """Install import hooks to block access to test implementations."""
        class BlockingFinder:
            def __init__(self, importer):
                self.importer = importer

            def find_module(self, fullname, path=None):
                # Block imports from test directories
                if path:
                    for p in path:
                        if self.importer._is_blocked_path(p):
                            return BlockingLoader()
                return None

        class BlockingLoader:
            def load_module(self, fullname):
                raise ModuleImportError(f"Import of '{fullname}' is blocked in evaluation context")

        self._original_meta_path = sys.meta_path.copy()
        sys.meta_path.insert(0, BlockingFinder(self))

    def _restore_import_hooks(self):
        """Restore original import hooks."""
        if self._original_meta_path is not None:
            sys.meta_path = self._original_meta_path
            self._original_meta_path = None

    def load_student_module(
        self,
        module_path: Path,
        module_name: str = "student_submission",
        timeout_seconds: float = 30.0,
    ) -> types.ModuleType:
        """
        Load a student's module from file.

        Args:
            module_path: Path to the student's Python file
            module_name: Name to give the loaded module
            timeout_seconds: Maximum time allowed for module loading

        Returns:
            The loaded module object

        Raises:
            FileNotFoundError: If the module file doesn't exist
            ModuleImportError: If the module fails to import
            ExecutionTimeoutError: If loading times out
        """
        module_path = Path(module_path)

        if not module_path.exists():
            raise FileNotFoundError(f"Module not found: {module_path}")

        # Add lib path to sys.path if not already there
        lib_path_str = str(self.lib_path)
        if lib_path_str not in sys.path:
            sys.path.insert(0, lib_path_str)

        # Also add the module's directory to sys.path for relative imports
        module_dir = str(module_path.parent)
        if module_dir not in sys.path:
            sys.path.insert(0, module_dir)

        try:
            self._setup_import_hooks()

            with timeout_context(timeout_seconds):
                # Load the module from file
                spec = importlib.util.spec_from_file_location(
                    module_name, str(module_path)
                )
                if spec is None or spec.loader is None:
                    raise ModuleImportError(f"Could not create spec for {module_path}")

                module = importlib.util.module_from_spec(spec)
                sys.modules[module_name] = module

                try:
                    spec.loader.exec_module(module)
                except Exception as e:
                    # Remove from sys.modules if loading failed
                    sys.modules.pop(module_name, None)
                    raise ModuleImportError(f"Failed to execute module: {e}") from e

                self._loaded_modules[module_name] = module
                return module

        finally:
            self._restore_import_hooks()

    def get_function(
        self,
        module: types.ModuleType,
        function_name: str,
    ) -> Optional[Any]:
        """
        Get a function from a loaded module.

        Args:
            module: The loaded module
            function_name: Name of the function to get

        Returns:
            The function object, or None if not found
        """
        return getattr(module, function_name, None)

    def get_class(
        self,
        module: types.ModuleType,
        class_name: str,
    ) -> Optional[type]:
        """
        Get a class from a loaded module.

        Args:
            module: The loaded module
            class_name: Name of the class to get

        Returns:
            The class object, or None if not found
        """
        obj = getattr(module, class_name, None)
        if obj is not None and isinstance(obj, type):
            return obj
        return None

    def cleanup(self):
        """Clean up loaded modules from sys.modules."""
        for name in self._loaded_modules:
            sys.modules.pop(name, None)
        self._loaded_modules.clear()

    def __enter__(self):
        """Context manager entry."""
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Context manager exit - cleanup loaded modules."""
        self.cleanup()
        return False

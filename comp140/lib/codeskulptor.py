"""
Stub module for CodeSkulptor utilities.
This provides mock implementations for testing/evaluation purposes.
"""

import os
from pathlib import Path


# Base URL for CodeSkulptor files (not actually accessible outside CodeSkulptor)
_BASE_URL = "http://codeskulptor-assets.commondatastorage.googleapis.com/"


def file2url(filename):
    """
    Convert a CodeSkulptor filename to a URL or local file path.

    Checks for local files in the following order:
    1. comp140/data/ directory (relative to this module)
    2. Current working directory
    3. Falls back to CodeSkulptor URL (won't work outside CodeSkulptor)
    """
    # Check in the data directory relative to lib/
    data_dir = Path(__file__).parent.parent / "data"
    data_path = data_dir / filename
    if data_path.exists():
        return str(data_path)

    # Check current directory
    if os.path.exists(filename):
        return str(Path(filename).resolve())

    # Otherwise return a CodeSkulptor URL (won't work outside CodeSkulptor)
    return _BASE_URL + filename


def set_timeout(timeout):
    """
    Set the maximum runtime for a program.

    In CodeSkulptor, this sets the timeout in seconds.
    In this stub, it's a no-op.
    """
    print(f"[codeskulptor stub] Timeout set to {timeout} seconds (ignored)")


def get_timeout():
    """
    Get the current timeout setting.

    Returns a default value in this stub.
    """
    return 300  # 5 minutes default


class RandomNumber:
    """
    Random number generator compatible with CodeSkulptor.
    """

    def __init__(self, seed=None):
        import random
        self._rng = random.Random(seed)

    def random(self):
        """Return a random float in [0, 1)."""
        return self._rng.random()

    def randint(self, low, high):
        """Return a random integer in [low, high]."""
        return self._rng.randint(low, high)

    def choice(self, seq):
        """Return a random element from seq."""
        return self._rng.choice(seq)

    def shuffle(self, seq):
        """Shuffle seq in place."""
        self._rng.shuffle(seq)


def randomize():
    """
    Create a new RandomNumber generator.
    """
    return RandomNumber()

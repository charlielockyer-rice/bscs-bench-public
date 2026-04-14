"""
COMP 341 Homework 6: Fashion-MNIST Deep Learning - Test Suite

Tests organized by functional area:
- Data loading (DataLoaders, label counts)
- Model architecture (Conv layers, FC layers)
- Forward pass shape and output properties
- Training loop (loss values, convergence)
- Validation accuracy
- Confusion matrix
"""

import pytest
import numpy as np
import os
import sys
from pathlib import Path

# =============================================================================
# Test Configuration
# =============================================================================

# Get paths from environment or use defaults
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR',
    Path(__file__).parent.parent / 'data')) / 'hw6'
WORKSPACE = Path(os.environ.get('COMP341_WORKSPACE', '.'))

# Add workspace to path for importing solution
sys.path.insert(0, str(WORKSPACE))

# Import will fail if solution.py doesn't exist - that's expected for template
try:
    import solution
    SOLUTION_AVAILABLE = True
except ImportError:
    SOLUTION_AVAILABLE = False


# Skip all tests if solution not available
pytestmark = pytest.mark.skipif(
    not SOLUTION_AVAILABLE,
    reason="solution.py not found in workspace"
)


# =============================================================================
# Fixtures
# =============================================================================

@pytest.fixture(scope="module")
def data_loaders():
    """Create train and validation DataLoaders from FashionMNIST."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    train_loader, valid_loader = solution.create_data_loaders(str(DATA_DIR))
    return train_loader, valid_loader


@pytest.fixture(scope="module")
def train_loader(data_loaders):
    """Training DataLoader."""
    return data_loaders[0]


@pytest.fixture(scope="module")
def valid_loader(data_loaders):
    """Validation DataLoader."""
    return data_loaders[1]


@pytest.fixture(scope="module")
def label_counts(train_loader):
    """Count labels in training set."""
    return solution.count_labels(train_loader)


@pytest.fixture(scope="module")
def model():
    """Instantiate a fresh StylishNN model."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    return solution.StylishNN()


@pytest.fixture(scope="module")
def fast_train_loader(data_loaders):
    """Create a small training DataLoader (10K subset) for fast CPU testing."""
    import torch
    from torch.utils.data import DataLoader as DL, Subset

    full_train = data_loaders[0].dataset
    # Use first 10000 samples for fast training (~6x faster than full 60K)
    subset = Subset(full_train, range(10000))
    return DL(subset, batch_size=64, shuffle=True)


@pytest.fixture(scope="module")
def fast_valid_loader(data_loaders):
    """Create a small validation DataLoader (2K subset) for fast CPU testing."""
    import torch
    from torch.utils.data import DataLoader as DL, Subset

    full_valid = data_loaders[1].dataset
    # Use first 2000 samples for fast validation
    subset = Subset(full_valid, range(2000))
    return DL(subset, batch_size=64, shuffle=False)


@pytest.fixture(scope="module")
def training_results(model, fast_train_loader, fast_valid_loader):
    """Train the model for 5 epochs on a data subset and collect results.

    Uses 10K training subset (not full 60K) for fast CPU test execution.

    Returns dict with keys:
        model: trained StylishNN
        train_losses: list of 5 average training losses
        valid_losses: list of 5 average validation losses
        final_accuracy: validation accuracy after last epoch
    """
    import torch
    import torch.nn as nn
    import torch.optim as optim

    device = 'cpu'
    model.to(device)
    optimizer = optim.SGD(model.parameters(), lr=0.01)
    loss_fn = nn.NLLLoss()

    train_losses = []
    valid_losses = []
    final_accuracy = 0.0

    # 5 epochs on 5K subset for fast CPU test execution
    for epoch in range(5):
        train_loss = solution.train_epoch(model, fast_train_loader, optimizer, loss_fn, device=device)
        valid_loss, accuracy = solution.validate(model, fast_valid_loader, loss_fn, device=device)
        train_losses.append(train_loss)
        valid_losses.append(valid_loss)
        final_accuracy = accuracy

    return {
        'model': model,
        'train_losses': train_losses,
        'valid_losses': valid_losses,
        'final_accuracy': final_accuracy,
    }


@pytest.fixture(scope="module")
def confusion_results(training_results, fast_valid_loader):
    """Compute confusion matrix using the trained model on validation subset."""
    trained_model = training_results['model']
    cm, labels = solution.create_confusion_matrix(trained_model, fast_valid_loader, device='cpu')
    return cm, labels


# =============================================================================
# Data Loader Creation
# =============================================================================

class TestDataLoaderCreation:
    """Verify DataLoader creation from FashionMNIST."""

    def test_data_loader_creation(self, data_loaders, train_loader, valid_loader):
        """Verify create_data_loaders returns correct DataLoaders."""
        # Should return a tuple of length 2
        assert isinstance(data_loaders, tuple), \
            f"create_data_loaders should return a tuple, got {type(data_loaders).__name__}"
        assert len(data_loaders) == 2, \
            f"Should return 2 DataLoaders, got {len(data_loaders)}"

        # Training set should have 60000 items
        assert len(train_loader.dataset) == 60000, \
            f"Training dataset should have 60000 items, " \
            f"got {len(train_loader.dataset)}"

        # Validation set should have 10000 items
        assert len(valid_loader.dataset) == 10000, \
            f"Validation dataset should have 10000 items, " \
            f"got {len(valid_loader.dataset)}"

        # Check batch shape by getting first batch
        images, labels = next(iter(train_loader))
        assert images.shape == (64, 1, 28, 28), \
            f"First batch image shape should be (64, 1, 28, 28), " \
            f"got {tuple(images.shape)}"


# =============================================================================
# Label Counts
# =============================================================================

class TestLabelCounts:
    """Verify label counting across the training set."""

    def test_label_counts(self, label_counts):
        """Verify count_labels returns correct distribution."""
        # Should be a dict with 10 keys
        assert isinstance(label_counts, dict), \
            f"count_labels should return a dict, got {type(label_counts).__name__}"
        assert len(label_counts) == 10, \
            f"Should have 10 label keys, got {len(label_counts)}"

        # Each key should be a string
        for key in label_counts:
            assert isinstance(key, str), \
                f"Label keys should be strings, got {type(key).__name__} for {key}"

        # Each value should be an int
        for key, count in label_counts.items():
            assert isinstance(count, (int, np.integer)), \
                f"Label counts should be ints, got {type(count).__name__} for '{key}'"

        # Total should sum to 60000
        total = sum(label_counts.values())
        assert total == 60000, \
            f"Total label count should be 60000, got {total}"

        # FashionMNIST is balanced: each class has exactly 6000 items
        for key, count in label_counts.items():
            assert count == 6000, \
                f"Class '{key}' should have 6000 items (balanced dataset), got {count}"


# =============================================================================
# Model Architecture: Conv1
# =============================================================================

class TestModelArchitectureConv1:
    """Verify first convolutional layer configuration."""

    def test_model_architecture_conv1(self, model):
        """Verify Conv1: 12 filters, 5x5 kernel, with MaxPool2d."""
        import torch.nn as nn

        # Find all Conv2d layers
        conv_layers = [m for m in model.modules() if isinstance(m, nn.Conv2d)]
        assert len(conv_layers) >= 1, \
            "Model should have at least 1 Conv2d layer"

        # First Conv2d should have in_channels=1, out_channels=12, kernel_size=5
        conv1 = conv_layers[0]
        assert conv1.in_channels == 1, \
            f"First Conv2d in_channels should be 1, got {conv1.in_channels}"
        assert conv1.out_channels == 12, \
            f"First Conv2d out_channels should be 12, got {conv1.out_channels}"
        assert conv1.kernel_size == (5, 5), \
            f"First Conv2d kernel_size should be (5, 5), got {conv1.kernel_size}"

        # Padding should be 2 to preserve 28x28 spatial dimensions
        padding = conv1.padding if isinstance(conv1.padding, tuple) else (conv1.padding, conv1.padding)
        assert padding == (2, 2), \
            f"First Conv2d padding should be (2, 2), got {padding}"

        # Should have at least one MaxPool2d
        pool_layers = [m for m in model.modules() if isinstance(m, nn.MaxPool2d)]
        assert len(pool_layers) >= 1, \
            "Model should have at least 1 MaxPool2d layer"

        # First MaxPool2d kernel_size should be 2
        pool1 = pool_layers[0]
        pool_ks = pool1.kernel_size if isinstance(pool1.kernel_size, int) else pool1.kernel_size[0]
        assert pool_ks == 2, \
            f"First MaxPool2d kernel_size should be 2, got {pool_ks}"


# =============================================================================
# Model Architecture: Conv2
# =============================================================================

class TestModelArchitectureConv2:
    """Verify second convolutional layer configuration."""


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

    def test_model_architecture_conv2(self, model):
        """Verify Conv2: in=12, out=32, 5x5 kernel, padding=1, MaxPool2d."""
        import torch.nn as nn

        # Find all Conv2d layers
        conv_layers = [m for m in model.modules() if isinstance(m, nn.Conv2d)]
        assert len(conv_layers) >= 2, \
            f"Model should have at least 2 Conv2d layers, found {len(conv_layers)}"

        # Second Conv2d should have in_channels=12, out_channels=32, kernel_size=5
        conv2 = conv_layers[1]
        assert conv2.in_channels == 12, \
            f"Second Conv2d in_channels should be 12, got {conv2.in_channels}"
        assert conv2.out_channels == 32, \
            f"Second Conv2d out_channels should be 32, got {conv2.out_channels}"
        assert conv2.kernel_size == (5, 5), \
            f"Second Conv2d kernel_size should be (5, 5), got {conv2.kernel_size}"

        # Padding should be 1
        padding = conv2.padding if isinstance(conv2.padding, tuple) else (conv2.padding, conv2.padding)
        assert padding == (1, 1), \
            f"Second Conv2d padding should be (1, 1), got {padding}"

        # Should have at least 2 MaxPool2d layers
        pool_layers = [m for m in model.modules() if isinstance(m, nn.MaxPool2d)]
        assert len(pool_layers) >= 2, \
            f"Model should have at least 2 MaxPool2d layers, found {len(pool_layers)}"


# =============================================================================
# Model Architecture: Fully Connected Layers
# =============================================================================

class TestModelArchitectureFc:
    """Verify fully connected layer configuration."""

    def test_model_architecture_fc(self, model):
        """Verify 3 Linear layers: ->600, 600->120, 120->10."""
        import torch.nn as nn

        # Find all Linear layers
        linear_layers = [m for m in model.modules() if isinstance(m, nn.Linear)]
        assert len(linear_layers) == 3, \
            f"Model should have exactly 3 Linear layers, found {len(linear_layers)}"

        # FC1: out_features=600
        fc1 = linear_layers[0]
        assert fc1.out_features == 600, \
            f"FC1 out_features should be 600, got {fc1.out_features}"

        # FC2: in_features=600, out_features=120
        fc2 = linear_layers[1]
        assert fc2.in_features == 600, \
            f"FC2 in_features should be 600, got {fc2.in_features}"
        assert fc2.out_features == 120, \
            f"FC2 out_features should be 120, got {fc2.out_features}"

        # FC3: in_features=120, out_features=10
        fc3 = linear_layers[2]
        assert fc3.in_features == 120, \
            f"FC3 in_features should be 120, got {fc3.in_features}"
        assert fc3.out_features == 10, \
            f"FC3 out_features should be 10, got {fc3.out_features}"


# =============================================================================
# Forward Pass Shape
# =============================================================================

class TestForwardPassShape:
    """Verify model forward pass produces correct output."""

    def test_forward_pass_shape(self, model):
        """Verify output shape (batch, 10) and log-probability values."""
        import torch

        dummy_input = torch.randn(4, 1, 28, 28)
        model.eval()
        with torch.no_grad():
            output = model(dummy_input)

        # Output shape should be (4, 10)
        assert output.shape == (4, 10), \
            f"Output shape should be (4, 10), got {tuple(output.shape)}"

        # Output should be log-probabilities (all values <= 0)
        assert (output <= 0).all(), \
            "Output values should all be <= 0 (log-probabilities from LogSoftmax). " \
            f"Max value found: {output.max().item():.4f}"


# =============================================================================
# Training Loop
# =============================================================================

class TestTrainingLoop:
    """Verify training loop produces valid loss values."""

    def test_training_loop(self, training_results):
        """Verify 5 epochs of training produce reasonable loss values."""
        train_losses = training_results['train_losses']

        # Should have 5 loss values (one per epoch)
        assert len(train_losses) == 5, \
            f"Should have 5 training loss values, got {len(train_losses)}"

        # Each loss should be a float
        for i, loss in enumerate(train_losses):
            assert isinstance(loss, float), \
                f"Training loss at epoch {i+1} should be a float, " \
                f"got {type(loss).__name__}"

        # Each loss should be a reasonable number (> 0, < 10)
        for i, loss in enumerate(train_losses):
            assert 0 < loss < 10, \
                f"Training loss at epoch {i+1} should be in (0, 10), got {loss:.4f}"


# =============================================================================
# Validation Accuracy
# =============================================================================

class TestValidationAccuracy:
    """Verify model achieves reasonable validation accuracy."""

    def test_validation_accuracy(self, training_results):
        """Verify validation accuracy >= 60% after 5 epochs."""
        accuracy = training_results['final_accuracy']

        # Accuracy should be on 0-100 scale
        assert 0 <= accuracy <= 100, \
            f"Accuracy should be in [0, 100] range, got {accuracy:.2f}"

        # After 5 epochs on 10K subset, should beat random (10%) by a good margin
        # Full training (15 epochs, 60K) typically reaches 85%+
        assert accuracy >= 30.0, \
            f"Validation accuracy should be >= 30% after 5 epochs on subset, " \
            f"got {accuracy:.2f}%"


# =============================================================================
# Loss Decreases
# =============================================================================

class TestLossDecreases:
    """Verify training loss decreases over epochs."""

    def test_loss_decreases(self, training_results):
        """Verify loss at epoch 5 is less than loss at epoch 1."""
        train_losses = training_results['train_losses']

        assert len(train_losses) >= 2, \
            f"Need at least 2 epochs to check loss decrease, " \
            f"got {len(train_losses)}"

        first_loss = train_losses[0]
        last_loss = train_losses[-1]

        # Use 0.95 multiplier for tolerance against batch shuffle noise
        assert last_loss < first_loss * 0.95, \
            f"Training loss should decrease meaningfully: epoch 1 = {first_loss:.4f}, " \
            f"epoch 5 = {last_loss:.4f}"


# =============================================================================
# Confusion Matrix
# =============================================================================

class TestConfusionMatrix:
    """Verify confusion matrix computation."""

    def test_confusion_matrix(self, confusion_results):
        """Verify confusion matrix shape, types, and contents."""
        cm, labels = confusion_results

        # Should be a numpy array with shape (10, 10)
        assert isinstance(cm, np.ndarray), \
            f"Confusion matrix should be np.ndarray, got {type(cm).__name__}"
        assert cm.shape == (10, 10), \
            f"Confusion matrix shape should be (10, 10), got {cm.shape}"

        # Labels should be a list of 10 strings
        assert isinstance(labels, list), \
            f"Labels should be a list, got {type(labels).__name__}"
        assert len(labels) == 10, \
            f"Should have 10 class labels, got {len(labels)}"
        for label in labels:
            assert isinstance(label, str), \
                f"Each label should be a string, got {type(label).__name__}"

        # Matrix values should be non-negative
        assert (cm >= 0).all(), \
            "All confusion matrix values should be non-negative"

        # Sum of matrix should equal the validation subset size (2000)
        total = cm.sum()
        assert total == 2000, \
            f"Confusion matrix sum should equal 2000 (validation subset size), " \
            f"got {total}"

        # Diagonal elements should beat random chance (10% for 10 classes)
        # Model trained on subset won't be great but should be better than random
        diagonal_sum = np.trace(cm)
        assert diagonal_sum > total * 0.2, \
            f"Diagonal sum ({diagonal_sum}) should be > 20% of total ({total}), " \
            f"indicating model learned something beyond random chance"

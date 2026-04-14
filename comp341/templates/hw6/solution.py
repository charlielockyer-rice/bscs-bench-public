"""
COMP 341 Homework 6: Fashion-MNIST Deep Learning

Build a CNN to classify Fashion-MNIST images into 10 clothing categories
using PyTorch with 2 convolution layers and 3 fully connected layers.

Functions/classes to implement:
- create_data_loaders: Load FashionMNIST and create DataLoaders
- count_labels: Count observations per label in a DataLoader
- StylishNN: CNN model class (nn.Module)
- train_epoch: Train model for one epoch
- validate: Evaluate model on validation set
- plot_loss_curves: Prepare loss curve data
- create_confusion_matrix: Compute confusion matrix on validation set
"""

import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torchvision
import torchvision.transforms as transforms
from torch.utils.data import DataLoader
from sklearn.metrics import confusion_matrix
from typing import Dict, List, Tuple, Union
from pathlib import Path


# Label mapping (provided - do not modify)
LABEL_NAMES = {
    0: "T-shirt/Top",
    1: "Trouser",
    2: "Pullover",
    3: "Dress",
    4: "Coat",
    5: "Sandal",
    6: "Shirt",
    7: "Sneaker",
    8: "Bag",
    9: "Ankle Boot",
}


# =============================================================================
# Data Loading
# =============================================================================

def create_data_loaders(data_dir: Union[str, Path], batch_size: int = 64) -> Tuple[DataLoader, DataLoader]:
    """Create train and validation DataLoaders for FashionMNIST.

    Loads FashionMNIST from data_dir with download=False (data is pre-downloaded).
    Applies transforms.ToTensor() transform. Training loader uses shuffle=True.

    Args:
        data_dir: Path to directory containing FashionMNIST data
        batch_size: Batch size for DataLoaders (default: 64)

    Returns:
        Tuple of (train_loader, valid_loader)
    """
    # TODO: Implement this function
    # Hint: Use torchvision.datasets.FashionMNIST(data_dir, train=True/False, download=False, transform=transforms.ToTensor())
    # Hint: Then wrap in DataLoader(dataset, batch_size=batch_size, shuffle=True/False)
    pass


def count_labels(data_loader: DataLoader) -> Dict[str, int]:
    """Count observations per label in a DataLoader.

    Iterates through the DataLoader and counts how many observations
    exist for each label. Returns counts using human-readable label names.

    Args:
        data_loader: DataLoader to iterate over

    Returns:
        Dict mapping label name (str) to count (int).
        Uses LABEL_NAMES mapping for human-readable names.
    """
    # TODO: Implement this function
    # Hint: Loop through data_loader, count labels, map to LABEL_NAMES
    pass


# =============================================================================
# Model Architecture
# =============================================================================

class StylishNN(nn.Module):
    """CNN for Fashion-MNIST classification.

    Architecture:
    - Conv1: nn.Conv2d(1, 12, kernel_size=5, padding=2) + MaxPool2d(2, 2)
      Input: (batch, 1, 28, 28) -> Conv -> (batch, 12, 28, 28) -> Pool -> (batch, 12, 14, 14)
    - Conv2: nn.Conv2d(12, 32, kernel_size=5, padding=1) + MaxPool2d(2, 2)
      (batch, 12, 14, 14) -> Conv -> (batch, 32, 12, 12) -> Pool -> (batch, 32, 6, 6)
    - Flatten: 32 * 6 * 6 = 1152
    - FC1: nn.Linear(1152, 600) + ReLU
    - FC2: nn.Linear(600, 120) + ReLU
    - FC3: nn.Linear(120, 10) + LogSoftmax(dim=1)

    Input: (batch, 1, 28, 28) grayscale images
    Output: (batch, 10) log-probabilities
    """

    def __init__(self, num_classes: int = 10):
        """Initialize layers.

        Args:
            num_classes: Number of output classes (default: 10)
        """
        # TODO: Implement this method
        # Hint: Call super().__init__() first
        # Hint: Define conv1, pool, conv2, fc1, fc2, fc3 layers
        pass

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        """Forward pass through the network.

        Args:
            x: Input tensor of shape (batch, 1, 28, 28)

        Returns:
            Log-probabilities tensor of shape (batch, num_classes)
        """
        # TODO: Implement this method
        # Hint: Conv1 -> ReLU -> Pool -> Conv2 -> ReLU -> Pool -> Flatten -> FC1 -> ReLU -> FC2 -> ReLU -> FC3 -> LogSoftmax
        pass


# =============================================================================
# Training
# =============================================================================

def train_epoch(model: nn.Module, train_loader: DataLoader, optimizer, loss_fn, device: str = 'cpu') -> float:
    """Train the model for one epoch.

    Performs forward pass, loss computation, backward pass, and weight update
    for each batch in the training DataLoader.

    Args:
        model: StylishNN model
        train_loader: Training DataLoader
        optimizer: SGD optimizer
        loss_fn: Loss function (e.g., nn.NLLLoss for LogSoftmax output)
        device: Device string ('cpu' or 'cuda')

    Returns:
        Average training loss for the epoch (float)
    """
    # TODO: Implement this function
    # Hint: model.train(), loop batches, forward -> loss -> backward -> step -> zero_grad
    pass


def validate(model: nn.Module, valid_loader: DataLoader, loss_fn, device: str = 'cpu') -> Tuple[float, float]:
    """Evaluate model on the validation set.

    Args:
        model: StylishNN model
        valid_loader: Validation DataLoader
        loss_fn: Loss function (e.g., nn.NLLLoss)
        device: Device string ('cpu' or 'cuda')

    Returns:
        Tuple of (average_validation_loss, accuracy_percentage)
        accuracy_percentage is on 0-100 scale
    """
    # TODO: Implement this function
    # Hint: model.eval(), torch.no_grad(), compute loss and accuracy
    pass


# =============================================================================
# Evaluation
# =============================================================================

def plot_loss_curves(train_losses: List[float], valid_losses: List[float]) -> Dict:
    """Prepare loss curve data for plotting.

    Args:
        train_losses: List of average training losses per epoch
        valid_losses: List of average validation losses per epoch

    Returns:
        Dict with keys:
        - 'epochs': list of ints from 1 to len(train_losses)
        - 'train_losses': the input train_losses list
        - 'valid_losses': the input valid_losses list
    """
    # TODO: Implement this function
    # Hint: Simple dict construction with epoch numbers
    pass


def create_confusion_matrix(model: nn.Module, valid_loader: DataLoader, device: str = 'cpu') -> Tuple[np.ndarray, List[str]]:
    """Compute confusion matrix on the validation set.

    Uses sklearn.metrics.confusion_matrix to compute the matrix with
    human-readable class labels.

    Args:
        model: Trained StylishNN model
        valid_loader: Validation DataLoader
        device: Device string ('cpu' or 'cuda')

    Returns:
        Tuple of:
        - Confusion matrix as numpy array, shape (10, 10)
        - List of 10 class label strings in label order (0-9)
    """
    # TODO: Implement this function
    # Hint: model.eval(), torch.no_grad(), collect all predictions and labels,
    # Hint: then use sklearn confusion_matrix and LABEL_NAMES for labels
    pass

# Homework 6: Fashion-MNIST Deep Learning

## Overview

Build a Convolutional Neural Network (CNN) to classify Fashion-MNIST images into 10 clothing categories using PyTorch. The model uses 2 convolution layers and 3 fully connected layers, trained with SGD and NLLLoss for 15 epochs. The dataset consists of 60,000 training images and 10,000 validation images, each 28x28 grayscale.

**Data:**
- FashionMNIST dataset loaded via `torchvision.datasets.FashionMNIST`
- Location: `data/hw6/` (mounted at `/data/hw6` in Docker)
- Must use `download=False` since Docker has no network access

**10 Classes (label mapping):**

| Label | Name |
|-------|------|
| 0 | T-shirt/Top |
| 1 | Trouser |
| 2 | Pullover |
| 3 | Dress |
| 4 | Coat |
| 5 | Sandal |
| 6 | Shirt |
| 7 | Sneaker |
| 8 | Bag |
| 9 | Ankle Boot |

## Tasks

### Part 1: Load and Explore the Data (10 pts)

#### Step 1: Create Data Loaders

Load the FashionMNIST train and test sets using `create_data_loaders(data_dir, batch_size=64)`. The function loads both splits from `data_dir` using `torchvision.datasets.FashionMNIST`, applies `transforms.ToTensor()`, and creates DataLoaders with the given batch_size. Shuffle the training loader but not the validation loader.

#### Step 2: Count Labels

Implement `count_labels(data_loader)` to count how many observations exist per label across all batches. Return a dict mapping label NAME (string) to count (int). Use the `LABEL_NAMES` mapping defined in the imports block to convert numeric labels to human-readable names.

### Part 2: Define the Model (40 pts)

#### Step 3: StylishNN Architecture

Implement `class StylishNN(nn.Module)` with `__init__` and `forward` methods.

**Layer definitions (in `__init__`):**

| Layer | Specification | Notes |
|-------|--------------|-------|
| conv1 | `nn.Conv2d(1, 12, kernel_size=5, padding=2)` | 12 filters, 5x5, padding=2 keeps 28x28 |
| pool | `nn.MaxPool2d(2, 2)` | Shared pooling layer, reduces spatial dims by half |
| conv2 | `nn.Conv2d(12, 32, kernel_size=5, padding=1)` | 32 filters, 5x5, padding=1 |
| fc1 | `nn.Linear(32 * 6 * 6, 600)` | 1152 input features to 600 |
| fc2 | `nn.Linear(600, 120)` | 600 to 120 |
| fc3 | `nn.Linear(120, 10)` | 120 to 10 output classes |

**Spatial dimension walkthrough:**
1. Input: (batch, 1, 28, 28)
2. Conv1 (padding=2, kernel=5): output = (28 + 2*2 - 5)/1 + 1 = 28 -> (batch, 12, 28, 28)
3. MaxPool(2, 2): 28/2 = 14 -> (batch, 12, 14, 14)
4. Conv2 (padding=1, kernel=5): output = (14 + 2*1 - 5)/1 + 1 = 12 -> (batch, 32, 12, 12)
5. MaxPool(2, 2): 12/2 = 6 -> (batch, 32, 6, 6)
6. Flatten: 32 * 6 * 6 = 1152

**Forward pass (in `forward`):**
1. x -> conv1 -> ReLU -> pool
2. x -> conv2 -> ReLU -> pool
3. Flatten: `x = x.view(x.size(0), -1)` (or `x.reshape(x.size(0), -1)`)
4. x -> fc1 -> ReLU
5. x -> fc2 -> ReLU
6. x -> fc3 -> LogSoftmax(dim=1)

**Output**: (batch, 10) log-probabilities

### Part 3: Model Training (30 pts)

#### Step 4: Training Loop

Implement `train_epoch(model, train_loader, optimizer, loss_fn, device='cpu')` to run one epoch of training:
1. Set model to training mode: `model.train()`
2. For each batch: move images and labels to device, forward pass, compute loss, backward pass, optimizer step, zero gradients
3. Track running loss across all batches
4. Return average training loss for the epoch (total loss / number of batches)

#### Step 5: Validation

Implement `validate(model, valid_loader, loss_fn, device='cpu')` to evaluate the model:
1. Set model to eval mode: `model.eval()`
2. Use `torch.no_grad()` context
3. For each batch: move images and labels to device, forward pass, compute loss, count correct predictions
4. Return tuple of (average_validation_loss, accuracy_percentage)
5. accuracy_percentage should be on 0-100 scale (e.g., 85.5 means 85.5%)

#### Step 6: Full Training Workflow

The complete training procedure (used in tests):
1. Create DataLoaders with `batch_size=64`
2. Initialize model: `StylishNN().to(device)`
3. Initialize optimizer: `torch.optim.SGD(model.parameters(), lr=0.01)`
4. Initialize loss function: `nn.NLLLoss()` (pairs with LogSoftmax output)
5. Train for 15 epochs, calling `train_epoch()` and `validate()` each epoch
6. Collect `train_losses` and `valid_losses` lists (one value per epoch)

### Part 4: Checking Performance (20 pts)

#### Step 7: Loss Curves

Implement `plot_loss_curves(train_losses, valid_losses)` to prepare loss curve data:
1. Takes lists of per-epoch training and validation losses
2. Returns dict with keys:
   - `'epochs'`: list of ints from 1 to N (where N = len(train_losses))
   - `'train_losses'`: the input train_losses list
   - `'valid_losses'`: the input valid_losses list

**Written Question:** Q1: Based on your plot, does it seem like our model converged within 15 epochs? Would you add more epochs? Or stop earlier? Explain.

#### Step 8: Confusion Matrix

Implement `create_confusion_matrix(model, valid_loader, device='cpu')`:
1. Set model to eval mode, use `torch.no_grad()`
2. Collect all predictions and true labels from the validation loader
3. Use `sklearn.metrics.confusion_matrix` to compute the matrix
4. Use the 10 human-readable class labels from `LABEL_NAMES` as the label list
5. Return tuple of (confusion_matrix as np.ndarray, list of label name strings)

**Written Question:** Q2: Does our model have any trouble distinguishing any items of clothing? If so, which ones does it tend to mix up?

**Written Question:** Q3: In this homework we tried one model architecture (a CNN with 2 convolutions and 3 fully connected layers) and achieved reasonable performance on the validation set. Could we instead use the output of the CNN with a classical machine learning algorithm? Explain your answer.

## Functions to Implement

```python
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

LABEL_NAMES = {
    0: "T-shirt/Top", 1: "Trouser", 2: "Pullover", 3: "Dress", 4: "Coat",
    5: "Sandal", 6: "Shirt", 7: "Sneaker", 8: "Bag", 9: "Ankle Boot"
}


def create_data_loaders(data_dir: Union[str, Path], batch_size: int = 64) -> Tuple[DataLoader, DataLoader]:
    """Load FashionMNIST and create train/validation DataLoaders.

    Uses torchvision.datasets.FashionMNIST with download=False (data is
    pre-downloaded). Applies transforms.ToTensor() to convert images to
    tensors. Training loader is shuffled; validation loader is not.

    Args:
        data_dir: Path to directory containing FashionMNIST data
        batch_size: Batch size for both loaders (default 64)

    Returns:
        Tuple of (train_loader, valid_loader)
    """


def count_labels(data_loader: DataLoader) -> Dict[str, int]:
    """Count the number of observations per label in a DataLoader.

    Iterates through all batches and counts how many images belong to
    each class. Returns counts keyed by human-readable label names
    using the LABEL_NAMES mapping.

    Args:
        data_loader: DataLoader to count labels from

    Returns:
        Dict mapping label name (str) to count (int),
        e.g. {"T-shirt/Top": 6000, "Trouser": 6000, ...}
    """


class StylishNN(nn.Module):
    """CNN for Fashion-MNIST classification.

    Architecture:
        Conv1(1->12, 5x5, pad=2) -> ReLU -> MaxPool(2)
        Conv2(12->32, 5x5, pad=1) -> ReLU -> MaxPool(2)
        Flatten(32*6*6=1152)
        FC1(1152->600) -> ReLU
        FC2(600->120) -> ReLU
        FC3(120->10) -> LogSoftmax(dim=1)

    Input: (batch, 1, 28, 28) grayscale images
    Output: (batch, 10) log-probabilities
    """

    def __init__(self, num_classes: int = 10):
        """Initialize layers: conv1, conv2, pool, fc1, fc2, fc3."""

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        """Forward pass through the network.

        Args:
            x: Input tensor of shape (batch, 1, 28, 28)

        Returns:
            Log-probabilities of shape (batch, 10)
        """


def train_epoch(model: nn.Module, train_loader: DataLoader, optimizer, loss_fn, device: str = 'cpu') -> float:
    """Train the model for one epoch.

    Sets model to train mode. For each batch: moves data to device,
    runs forward pass, computes loss, runs backward pass, steps
    optimizer, and zeros gradients. Tracks running loss.

    Args:
        model: The neural network model
        train_loader: DataLoader for training data
        optimizer: Optimizer (e.g., SGD)
        loss_fn: Loss function (e.g., nn.NLLLoss)
        device: Device to run on ('cpu' or 'cuda')

    Returns:
        Average training loss for the epoch (total loss / num batches)
    """


def validate(model: nn.Module, valid_loader: DataLoader, loss_fn, device: str = 'cpu') -> Tuple[float, float]:
    """Evaluate the model on validation data.

    Sets model to eval mode and uses torch.no_grad(). For each batch:
    moves data to device, runs forward pass, computes loss, counts
    correct predictions.

    Args:
        model: The neural network model
        valid_loader: DataLoader for validation data
        loss_fn: Loss function (e.g., nn.NLLLoss)
        device: Device to run on ('cpu' or 'cuda')

    Returns:
        Tuple of (average_validation_loss, accuracy_percentage)
        where accuracy_percentage is on 0-100 scale (e.g., 85.5)
    """


def plot_loss_curves(train_losses: List[float], valid_losses: List[float]) -> Dict:
    """Prepare loss curve data for plotting.

    Args:
        train_losses: List of per-epoch average training losses
        valid_losses: List of per-epoch average validation losses

    Returns:
        Dict with keys:
        - 'epochs': list of ints [1, 2, ..., N]
        - 'train_losses': the input train_losses
        - 'valid_losses': the input valid_losses
    """


def create_confusion_matrix(model: nn.Module, valid_loader: DataLoader, device: str = 'cpu') -> Tuple[np.ndarray, List[str]]:
    """Compute confusion matrix on validation data.

    Sets model to eval mode with torch.no_grad(). Collects all
    predictions and true labels, then computes the confusion matrix
    using sklearn.metrics.confusion_matrix with human-readable
    class labels from LABEL_NAMES.

    Args:
        model: Trained neural network model
        valid_loader: DataLoader for validation data
        device: Device to run on ('cpu' or 'cuda')

    Returns:
        Tuple of:
        - Confusion matrix as np.ndarray of shape (10, 10)
        - List of 10 label name strings in label order (0-9)
    """
```

## Hints

- **download=False**: Docker has no network access. Load data with `torchvision.datasets.FashionMNIST(data_dir, train=True, download=False, transform=transforms.ToTensor())`.
- **Train vs test split**: Use `train=True` for the training set (60,000 images) and `train=False` for the validation set (10,000 images).
- **Padding math**: Conv1 with padding=2 on 28x28 keeps the spatial size at 28x28. Conv2 with padding=1 on 14x14 with kernel_size=5 gives (14 + 2*1 - 5 + 1) = 12x12. After both MaxPool(2, 2) stages: 28 -> 14 -> 12 -> 6. Final feature map is 32 x 6 x 6 = 1152.
- **Flatten before FC layers**: Use `x.view(x.size(0), -1)` or `x.reshape(x.size(0), -1)` to flatten the conv output before feeding into fc1.
- **LogSoftmax + NLLLoss**: Since the architecture uses `F.log_softmax` (or `nn.LogSoftmax`) as the final activation, use `nn.NLLLoss()` as the loss function. Do NOT use `nn.CrossEntropyLoss` with LogSoftmax -- CrossEntropyLoss applies softmax internally and would double-apply it.
- **Device handling**: Move images and labels to the device with `.to(device)` inside the train/validate loops.
- **Label names for confusion matrix**: Use the `LABEL_NAMES` dict to build the list of label strings in order (index 0 through 9).
- **SGD optimizer**: Use `torch.optim.SGD(model.parameters(), lr=0.01)`.
- **15 epochs**: Train for 15 epochs to allow convergence.
- **Counting correct predictions**: In `validate`, get predicted class with `torch.argmax(output, dim=1)` (works for both log-probabilities and raw logits) and compare to true labels.
- **No GPU needed**: CPU training works fine for Fashion-MNIST. Each epoch takes about 30-60 seconds on CPU.

## Grading

| Part | Points |
|------|--------|
| Part 1: Load and Explore Data | 10 |
| Part 2: Define the Model | 40 |
| Part 3: Model Training | 30 |
| Part 4: Checking Performance | 20 |
| **Total** | **100** |

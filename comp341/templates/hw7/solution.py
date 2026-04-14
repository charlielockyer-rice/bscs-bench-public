"""
COMP 341 Homework 7: Is a Picture Worth 1000 Words?

Hybrid deep learning assignment combining house listing images (128x128 RGB)
with tabular features to predict listing prices. Implements three model
variants (hybrid CNN+MLP, image-only CNN, features-only MLP) and a custom
PyTorch Dataset.

Functions/classes to implement:
- display_image: Load and return a house listing image
- reduce_dimensions: Flatten images and apply PCA to 2D
- detect_schematics: Identify schematic/floorplan images in 2D space
- HouseImagesDataset: Custom Dataset for images + tabular features
- HybridHouseNN: CNN branch + MLP branch combined model
- HouseImageOnly: CNN-only model
- HouseFeatsOnly: MLP-only model
- train_model: One epoch of training (supports all three model modes)
- validate_model: Validation pass with RMSE
- prepare_kaggle_submission: Generate test set predictions
"""

import pandas as pd
import numpy as np
from PIL import Image
import os
from typing import Dict, List, Optional, Tuple, Union

import torch
from torch.utils.data import Dataset, DataLoader
from torchvision import transforms
import torch.nn as nn
import torch.optim as optim
from torchmetrics import MeanSquaredError
from sklearn.decomposition import PCA


# =============================================================================
# Constants (provided - do not modify)
# =============================================================================

# Image normalization constants for house listing images
HOUSE_MEAN = [0.5230, 0.5416, 0.4989]
HOUSE_SD = [1, 1, 1]

TRANSFORM = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(HOUSE_MEAN, HOUSE_SD)
])

INV_NORMALIZE = transforms.Normalize(
    mean=[-m / s for m, s in zip(HOUSE_MEAN, HOUSE_SD)],
    std=[1 / s for s in HOUSE_SD]
)


# =============================================================================
# Part 1: Exploring the Home Images
# =============================================================================

def display_image(house_id, image_dir: str):
    """Load and return the PIL Image for the given house listing.

    Constructs the image path as {image_dir}/{house_id}.jpg and opens it.

    Args:
        house_id: House ID (str or int). Will be converted to str for
            the filename lookup.
        image_dir: Path to the directory containing house images
            (e.g., 'house_imgs/').

    Returns:
        PIL.Image.Image: The loaded RGB image (128x128).
    """
    # TODO: Implement this function
    # Hint: Use os.path.join(image_dir, f"{house_id}.jpg")
    # Hint: Use Image.open() from PIL
    pass


def reduce_dimensions(image_dir: str, house_ids: list) -> np.ndarray:
    """Flatten all images for given house IDs into pixel vectors and apply PCA to 2D.

    For each house_id, loads the image from {image_dir}/{house_id}.jpg,
    converts it to a numpy array, and flattens it into a 1D pixel vector
    (128 * 128 * 3 = 49152 features). Then applies PCA with n_components=2
    to reduce the collection to 2 dimensions.

    Args:
        image_dir: Path to the directory containing house images.
        house_ids: List of house ID strings to include.

    Returns:
        np.ndarray of shape (n_images, 2) containing the 2D PCA
        coordinates for each image.
    """
    # TODO: Implement this function
    # Hint: Loop through house_ids, load each image with Image.open()
    # Hint: Convert to numpy array, flatten with .flatten()
    # Hint: Stack all flattened vectors into a matrix (n_images, 49152)
    # Hint: Fit PCA(n_components=2) and transform
    pass


def detect_schematics(reduced_2d: np.ndarray, house_ids: list, known_ids: Optional[list] = None) -> list:
    """Identify schematic/floorplan images using the 2D reduced space.

    Uses the PCA-reduced 2D coordinates to find images that are schematics
    or architectural drawings rather than photographs. Known schematic IDs
    serve as seed points to identify the cluster of similar images.

    Args:
        reduced_2d: np.ndarray of shape (n_images, 2) from reduce_dimensions.
        house_ids: List of house ID strings corresponding to rows of reduced_2d.
        known_ids: List of house ID strings known to be schematics.
            Defaults to ['4112', '7758'] if None.

    Returns:
        List of house ID strings classified as schematics. Must include
        all IDs from known_ids plus any additional detected schematics.
    """
    # TODO: Implement this function
    # Hint: Find the 2D coordinates of the known schematic IDs
    # Hint: Compute distances or use clustering to find nearby points
    # Hint: Apply a threshold or use a clustering algorithm (e.g., KMeans)
    # Hint: Return all house_ids in the schematic cluster
    pass


# =============================================================================
# Part 2a: Dataset
# =============================================================================

class HouseImagesDataset(Dataset):
    """Custom PyTorch Dataset for house listing images with tabular features.

    Loads a CSV annotation file and corresponding images. Preprocesses tabular
    features (cleaning, scaling, encoding) and transforms images using the
    global TRANSFORM pipeline.

    The dataset returns a dict per sample with keys:
    - 'image': Tensor of shape (3, 128, 128) - transformed RGB image
    - 'id': str - the house ID
    - 'features': FloatTensor - processed tabular feature vector
    - 'price': FloatTensor - list_price target (only when train=True)
    """

    def __init__(self, annot_file: str, image_dir: str, train: bool = True):
        """Initialize the dataset by loading and preprocessing the CSV data.

        Preprocessing steps to implement:
        1. Load the CSV from annot_file
        2. Handle missing values (NaN) - fill or drop as appropriate
        3. Scale numeric features (beds, baths, sqft, lot_size, year_built, etc.)
        4. One-hot encode categorical features (property_type, zipcode, etc.)
        5. Store the list of feature column names in self.feature_cols

        Args:
            annot_file: Path to the CSV file (home_data_train.csv or
                home_data_test.csv).
            image_dir: Path to the directory containing house images.
            train: If True, dataset includes 'price' (list_price) in
                each sample. If False, 'price' key is omitted.
        """
        # TODO: Implement this method
        # Hint: self.df = pd.read_csv(annot_file)
        # Hint: self.image_dir = image_dir
        # Hint: self.train = train
        # Hint: Clean and preprocess features, store column names in self.feature_cols
        pass

    def __len__(self) -> int:
        """Return the number of samples in the dataset.

        Returns:
            int: Number of rows in the annotation CSV.
        """
        # TODO: Implement this method
        pass

    def __getitem__(self, idx: int) -> Dict:
        """Return a single sample as a dict.

        Loads the image for the given index, applies TRANSFORM, and packages
        the tabular features and (optionally) the target price into a dict.

        Args:
            idx: Integer index into the dataset.

        Returns:
            Dict with keys:
            - 'image': FloatTensor of shape (3, 128, 128)
            - 'id': str, the house ID
            - 'features': FloatTensor of processed tabular features
            - 'price': FloatTensor of list_price (only when self.train=True)
        """
        # TODO: Implement this method
        # Hint: Get the row from self.df using idx
        # Hint: Build image path from house ID, load with Image.open(), apply TRANSFORM
        # Hint: Extract feature values using self.feature_cols
        # Hint: Return dict; include 'price' only if self.train is True
        pass


# =============================================================================
# Part 2b: Model Architectures
# =============================================================================

class HybridHouseNN(nn.Module):
    """Hybrid model combining a CNN branch for images with an MLP branch for
    tabular features.

    Architecture:
    - CNN branch: Convolutional layers processing (3, 128, 128) images,
      producing a learned image embedding.
    - MLP branch: Fully connected layers processing the tabular feature vector.
    - Fusion: Concatenate CNN and MLP embeddings, pass through final FC
      layers to produce a single price prediction.

    Output should be non-negative (e.g., use ReLU on the final layer)
    since house prices cannot be negative.
    """

    def __init__(self, num_features: int):
        """Initialize the hybrid model layers.

        Args:
            num_features: Number of tabular feature columns from the dataset.
                This determines the input size of the MLP branch.
        """
        # TODO: Implement this method
        # Hint: super().__init__()
        # Hint: Define CNN branch (Conv2d layers + pooling + flatten)
        # Hint: Define MLP branch (Linear layers for tabular features)
        # Hint: Define final fusion layers (Linear layers after concatenation)
        # Hint: Ensure final output is a single value per sample
        pass

    def forward(self, ximg: torch.Tensor, xfeats: torch.Tensor) -> torch.Tensor:
        """Forward pass through both branches and fusion layers.

        Args:
            ximg: Image tensor of shape (batch_size, 3, 128, 128).
            xfeats: Tabular feature tensor of shape (batch_size, num_features).

        Returns:
            Tensor of shape (batch_size, 1) with predicted prices.
            Values should be non-negative.
        """
        # TODO: Implement this method
        # Hint: Pass ximg through CNN branch
        # Hint: Pass xfeats through MLP branch
        # Hint: Concatenate the two embeddings
        # Hint: Pass through fusion layers
        pass


class HouseImageOnly(nn.Module):
    """CNN-only model that predicts house price from listing images alone.

    Architecture:
    - Convolutional layers processing (3, 128, 128) RGB images
    - Fully connected layers producing a single price prediction

    Output should be non-negative since house prices cannot be negative.
    """

    def __init__(self):
        """Initialize CNN and fully connected layers."""
        # TODO: Implement this method
        # Hint: super().__init__()
        # Hint: Define Conv2d layers + pooling + flatten + Linear layers
        # Hint: Final layer outputs a single value
        pass

    def forward(self, ximg: torch.Tensor) -> torch.Tensor:
        """Forward pass through the image-only model.

        Args:
            ximg: Image tensor of shape (batch_size, 3, 128, 128).

        Returns:
            Tensor of shape (batch_size, 1) with predicted prices.
            Values should be non-negative.
        """
        # TODO: Implement this method
        pass


class HouseFeatsOnly(nn.Module):
    """MLP-only model that predicts house price from tabular features alone.

    Architecture:
    - Fully connected layers processing the tabular feature vector
    - Single output for price prediction

    Output should be non-negative since house prices cannot be negative.
    """

    def __init__(self, num_features: int):
        """Initialize fully connected layers.

        Args:
            num_features: Number of tabular feature columns from the dataset.
        """
        # TODO: Implement this method
        # Hint: super().__init__()
        # Hint: Define Linear layers with ReLU activations
        # Hint: Final layer outputs a single value
        pass

    def forward(self, xfeats: torch.Tensor) -> torch.Tensor:
        """Forward pass through the features-only model.

        Args:
            xfeats: Tabular feature tensor of shape (batch_size, num_features).

        Returns:
            Tensor of shape (batch_size, 1) with predicted prices.
            Values should be non-negative.
        """
        # TODO: Implement this method
        pass


# =============================================================================
# Part 2c: Training and Evaluation
# =============================================================================

def train_model(model: nn.Module, train_loader: DataLoader, optimizer,
                loss_fn, device: str = 'cpu', mode: str = 'both') -> float:
    """Train the model for one epoch.

    Iterates through the training DataLoader, performing forward pass, loss
    computation, backward pass, and weight update for each batch. Supports
    three modes corresponding to the three model architectures.

    Args:
        model: One of HybridHouseNN, HouseImageOnly, or HouseFeatsOnly.
        train_loader: DataLoader yielding dicts with keys 'image', 'features',
            'price'.
        optimizer: PyTorch optimizer (e.g., Adam).
        loss_fn: Loss function (e.g., torchmetrics.MeanSquaredError).
        device: Device string ('cpu' or 'cuda').
        mode: One of:
            - 'both': Pass both image and features to model (HybridHouseNN)
            - 'image': Pass only image to model (HouseImageOnly)
            - 'features': Pass only features to model (HouseFeatsOnly)

    Returns:
        Average training loss for the epoch (float).
    """
    # TODO: Implement this function
    # Hint: model.train()
    # Hint: Loop over train_loader batches
    # Hint: Extract 'image', 'features', 'price' from batch dict
    # Hint: Move tensors to device
    # Hint: Forward pass depends on mode ('both', 'image', 'features')
    # Hint: Compute loss, backward, optimizer.step(), optimizer.zero_grad()
    # Hint: Accumulate and return average loss
    pass


def validate_model(model: nn.Module, valid_loader: DataLoader, loss_fn,
                   device: str = 'cpu', mode: str = 'both') -> Tuple[float, float]:
    """Evaluate model on the validation set.

    Runs a forward pass on all validation batches without computing gradients.
    Computes both the average loss and the RMSE across the entire validation set.

    Args:
        model: One of HybridHouseNN, HouseImageOnly, or HouseFeatsOnly.
        valid_loader: Validation DataLoader yielding dicts with keys
            'image', 'features', 'price'.
        loss_fn: Loss function (e.g., torchmetrics.MeanSquaredError).
        device: Device string ('cpu' or 'cuda').
        mode: One of 'both', 'image', or 'features'.

    Returns:
        Tuple of (avg_valid_loss, rmse):
        - avg_valid_loss: float, average loss over all validation batches
        - rmse: float, root mean squared error over the validation set
    """
    # TODO: Implement this function
    # Hint: model.eval(), torch.no_grad()
    # Hint: Loop over valid_loader, accumulate loss
    # Hint: Use torchmetrics.MeanSquaredError(squared=False) for RMSE
    #       or compute manually: sqrt(mean((pred - true)^2))
    # Hint: Return (avg_loss, rmse)
    pass


# =============================================================================
# Part 3: Kaggle Submission
# =============================================================================

def prepare_kaggle_submission(model: nn.Module, test_loader: DataLoader,
                              device: str = 'cpu', mode: str = 'both') -> pd.DataFrame:
    """Generate predictions for the test set in Kaggle submission format.

    Runs the model on the test DataLoader (which has no 'price' key) and
    collects predictions alongside house IDs.

    Args:
        model: Trained model (any of the three variants).
        test_loader: Test DataLoader yielding dicts with keys 'image',
            'features', 'id' (no 'price').
        device: Device string ('cpu' or 'cuda').
        mode: One of 'both', 'image', or 'features'.

    Returns:
        pd.DataFrame with columns ['houseid', 'price'] containing
        predicted prices for each house in the test set.
    """
    # TODO: Implement this function
    # Hint: model.eval(), torch.no_grad()
    # Hint: Loop over test_loader, collect predictions and IDs
    # Hint: Build DataFrame with columns 'houseid' and 'price'
    pass

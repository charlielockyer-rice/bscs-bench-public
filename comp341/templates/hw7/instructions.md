# Homework 7: Is a Picture Worth 1000 Words? (Hybrid Deep Learning)

## Overview

Build three neural network variants to predict house listing prices from Redfin Houston data: a hybrid model combining house images with tabular features, an image-only CNN, and a features-only MLP. Before modeling, explore the image data using PCA dimensionality reduction and detect schematic/floorplan images. The dataset contains ~7152 house images (128x128 RGB JPEGs) paired with tabular listing features across 4000 training rows and 3152 test rows.

**Data Files:**
- `home_data_train.csv` - Training data (4000 rows x 11 columns)
- `home_data_test.csv` - Test data (3152 rows x 10 columns, no list_price)
- `house_imgs/` - House listing images (~7152 JPG files, 128x128 RGB), named `{houseid}.jpg`
- Location: `data/hw7/` (mounted at `/data/hw7` in Docker)

**Training CSV Columns:**

| Column | Type | Notes |
|--------|------|-------|
| houseid | int | Unique identifier, matches image filename |
| property_type | str | Categorical (e.g., "Single Family Residential", "Condo/Co-op", "Townhouse") |
| beds | float | Number of bedrooms (may contain NaN) |
| baths | float | Number of bathrooms (may contain NaN) |
| sqft | float | Square footage (may contain NaN) |
| lot_size | float | Lot size in sqft (may contain NaN) |
| year_built | float | Year built (may contain NaN) |
| zipcode | int | ZIP code |
| latitude | float | Latitude coordinate |
| longitude | float | Longitude coordinate |
| list_price | float | Target variable (training only) |

**Test CSV:** Same columns as training, minus `list_price`.

## Tasks

### Part 1: Exploring Home Images (25 pts)

#### Step 1: Display an Image

Implement `display_image(house_id, image_dir)` to load and return a house listing image. Open the file `{house_id}.jpg` from the `image_dir` directory using `PIL.Image.open()` and return the PIL Image object. Do NOT close the image before returning.

```python
def display_image(house_id, image_dir) -> PIL.Image.Image:
```

- `house_id`: int or string house ID
- `image_dir`: path to directory containing the JPG files
- Returns: PIL Image object (128x128 RGB)

#### Step 2: Dimensionality Reduction on Images

Implement `reduce_dimensions(image_dir, house_ids)` to flatten images into pixel vectors and reduce to 2D with PCA.

```python
def reduce_dimensions(image_dir: str, house_ids: list) -> np.ndarray:
```

- `image_dir`: Path to directory containing house JPG images
- `house_ids`: List of house ID strings to include
- Returns: np.ndarray of shape (n_images, 2)

**Workflow:**
1. For each house_id in `house_ids`, load `{image_dir}/{house_id}.jpg` with PIL
2. Convert to numpy array, flatten to 1D vector (128 * 128 * 3 = 49152 features)
3. Stack all flattened vectors into a 2D array (shape: n_images x 49152)
4. Apply `sklearn.decomposition.PCA(n_components=2)` to reduce to 2D
5. Return the 2D coordinates as a numpy array of shape (n_images, 2)

#### Step 3: Detect Schematics

Implement `detect_schematics(reduced_2d, house_ids, known_ids=['4112', '7758'])` to identify houses whose primary image is a schematic or floorplan rather than a real photograph. Houses '4112' and '7758' are known schematics that should appear in the output.

```python
def detect_schematics(reduced_2d: np.ndarray, house_ids: list, known_ids: list = None) -> list:
```

- `reduced_2d`: 2D PCA-reduced coordinates from `reduce_dimensions`, shape (N, 2)
- `house_ids`: List of house ID strings corresponding to rows of `reduced_2d`
- `known_ids`: List of known schematic house ID strings (defaults to `['4112', '7758']`)
- Returns: List of house ID strings classified as schematics (must include '4112' and '7758')

**Approach:** Use the 2D PCA space to identify outlier clusters. Schematics tend to cluster separately from real photos because their pixel distributions are very different (mostly white/line drawings vs. colorful house photos). You can use K-means clustering, distance-based thresholding from the known IDs, or any reasonable method.

### Part 2: Predicting List Price (70 pts)

#### Step 4: Image Normalization Constants

Use these exact normalization values for all image transforms:

```python
HOUSE_MEAN = [0.5230, 0.5416, 0.4989]
HOUSE_SD = [1, 1, 1]

TRANSFORM = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(HOUSE_MEAN, HOUSE_SD)
])
```

This subtracts the channel means but does not divide by standard deviation (since `HOUSE_SD = [1, 1, 1]`). These constants are already provided in the solution template.

#### Step 5: Custom Dataset

Implement `class HouseImagesDataset(torch.utils.data.Dataset)` with three methods:

```python
class HouseImagesDataset(Dataset):
    def __init__(self, annot_file, image_dir, train=True):
    def __len__(self):
    def __getitem__(self, idx):
```

**`__init__(self, annot_file, image_dir, train=True)`:**
1. Load the CSV from `annot_file` using pandas
2. Store `image_dir` and `train` flag
3. Set up the image transform (see Step 4)
4. Preprocess tabular features:
   - Handle NaN values (fill with column median, mean, or 0)
   - Scale numeric features (StandardScaler or MinMaxScaler recommended)
   - One-hot encode categorical columns (e.g., `property_type` using `pd.get_dummies`)
5. Store the preprocessed feature column names in `self.feature_cols` (list of strings)

**`__len__(self)`:** Return the number of samples (rows in the CSV).

**`__getitem__(self, idx)`:**
Return a dict with these keys:
- `'image'`: Transformed image tensor of shape `(3, 128, 128)` -- load `{houseid}.jpg`, apply `image_transform`
- `'id'`: The houseid as a string
- `'features'`: `torch.FloatTensor` of preprocessed tabular features
- `'price'` (train only): `list_price` as a `torch.FloatTensor` scalar

When `train=False`, omit the `'price'` key from the returned dict.

#### Step 6: Three Model Architectures

Implement three `nn.Module` subclasses. You have freedom to design the exact layer sizes and architecture, but each must follow the specified interface.

**Model 1: HybridHouseNN (CNN + MLP)**

```python
class HybridHouseNN(nn.Module):
    def __init__(self, num_features):
    def forward(self, ximg, xfeats):
```

- `__init__`: Takes `num_features` (number of tabular feature columns). Builds a CNN branch for images and an MLP branch for tabular features.
- `forward(self, ximg, xfeats)`: Takes image tensor `(batch, 3, 128, 128)` and features tensor `(batch, num_features)`. Processes images through the CNN branch, processes features through the MLP branch, concatenates both outputs, passes through final FC layers.
- Output: Tensor of shape `(batch, 1)` -- a single non-negative price prediction per sample. Use `torch.relu()`, `torch.abs()`, or `F.softplus()` on the final output to ensure non-negative predictions.

**Suggested CNN branch architecture (not required, just a reasonable starting point):**
- Conv2d(3, 16, 3, padding=1) -> ReLU -> MaxPool(2)
- Conv2d(16, 32, 3, padding=1) -> ReLU -> MaxPool(2)
- Conv2d(32, 64, 3, padding=1) -> ReLU -> MaxPool(2)
- Flatten -> FC(64 * 16 * 16, 128) -> ReLU

**Suggested MLP branch architecture:**
- FC(num_features, 64) -> ReLU
- FC(64, 32) -> ReLU

**Suggested final layers after concatenation:**
- FC(128 + 32, 64) -> ReLU
- FC(64, 1)

**Model 2: HouseImageOnly (CNN only)**

```python
class HouseImageOnly(nn.Module):
    def __init__(self):
    def forward(self, ximg):
```

- `forward(self, ximg)`: Takes only image tensor `(batch, 3, 128, 128)`.
- Output: Tensor of shape `(batch, 1)` -- non-negative price prediction.
- Use a CNN architecture similar to the CNN branch of HybridHouseNN, followed by FC layers to produce a single output.

**Model 3: HouseFeatsOnly (MLP only)**

```python
class HouseFeatsOnly(nn.Module):
    def __init__(self, num_features):
    def forward(self, xfeats):
```

- `forward(self, xfeats)`: Takes only features tensor `(batch, num_features)`.
- Output: Tensor of shape `(batch, 1)` -- non-negative price prediction.
- Use an MLP architecture similar to the MLP branch of HybridHouseNN, followed by FC layers to produce a single output.

#### Step 7: Training Function

Implement `train_model(model, train_loader, optimizer, loss_fn, device, mode="both")`:

```python
def train_model(model, train_loader, optimizer, loss_fn, device, mode="both") -> float:
```

- `mode="both"`: Use HybridHouseNN -- pass both `batch['image']` and `batch['features']` to `model(ximg, xfeats)`
- `mode="image"`: Use HouseImageOnly -- pass only `batch['image']` to `model(ximg)`
- `mode="features"`: Use HouseFeatsOnly -- pass only `batch['features']` to `model(xfeats)`

**Training loop (one epoch):**
1. Set model to training mode: `model.train()`
2. For each batch from `train_loader`:
   - Move tensors to device: `image = batch['image'].to(device)`, `features = batch['features'].to(device)`, `price = batch['price'].to(device)`
   - Zero gradients: `optimizer.zero_grad()`
   - Forward pass based on mode
   - Reshape predictions/targets as needed for loss computation (ensure matching shapes)
   - Compute loss: `loss = loss_fn(predictions, price)`
   - Backward pass: `loss.backward()`
   - Optimizer step: `optimizer.step()`
   - Accumulate running loss
3. Return average training loss (total loss / number of batches) as a float

#### Step 8: Validation Function

Implement `validate_model(model, valid_loader, loss_fn, device, mode="both")`:

```python
def validate_model(model, valid_loader, loss_fn, device, mode="both") -> Tuple[float, float]:
```

- Same mode logic as `train_model`
- Use `model.eval()` and `torch.no_grad()`
- Compute both average validation loss and RMSE

**Validation loop:**
1. Set model to eval mode: `model.eval()`
2. Use `torch.no_grad()` context
3. For each batch: forward pass, accumulate loss and squared errors
4. Return tuple of `(average_validation_loss, rmse_value)` -- both floats

**Loss function:** The `loss_fn` is passed in by the caller. Use it directly as `loss = loss_fn(preds, targets)`. The tests pass `torchmetrics.MeanSquaredError(squared=False)` which computes RMSE directly.

#### Step 9: Training Configuration

The full training workflow (used when running end-to-end, not strictly required by tests):

1. Create dataset: `HouseImagesDataset(annot_file, image_dir, train=True)`
2. Split 75/25 train/val: `torch.utils.data.random_split(dataset, [0.75, 0.25])`
3. Create DataLoaders with `batch_size=64`
4. Initialize model, optimizer (e.g., `Adam`), and loss function
5. Train for 30 epochs (tests may use fewer), calling `train_model()` and `validate_model()` each epoch

### Part 3: Test Set Predictions (5 pts)

#### Step 10: Generate Predictions

Implement `prepare_kaggle_submission(model, test_loader, device, mode="both")`:

```python
def prepare_kaggle_submission(model, test_loader, device, mode="both") -> pd.DataFrame:
```

1. Set model to eval mode, use `torch.no_grad()`
2. For each batch in `test_loader`, run forward pass (based on mode) to get predictions
3. Collect all house IDs and corresponding predictions
4. Return a DataFrame with columns `['houseid', 'price']`
   - `houseid` values must be strings
   - `price` values are the predicted listing prices (floats)

### Part 4: Extra Credit -- TensorBoard (skip)

TensorBoard logging is extra credit and is not tested by the autograder. Skip this section.

## Functions to Implement

```python
import numpy as np
import pandas as pd
import torch
import torch.nn as nn
import torch.nn.functional as F
import torchvision.transforms as transforms
from torch.utils.data import Dataset, DataLoader
from sklearn.decomposition import PCA
from PIL import Image
from pathlib import Path
from typing import Union, Dict, List, Tuple


# Image normalization constants (provided in template)
HOUSE_MEAN = [0.5230, 0.5416, 0.4989]
HOUSE_SD = [1, 1, 1]

TRANSFORM = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(HOUSE_MEAN, HOUSE_SD)
])


def display_image(house_id, image_dir) -> Image.Image:
    """Load and return a house listing image.

    Opens {house_id}.jpg from image_dir using PIL.Image.open().

    Args:
        house_id: House ID (int or str) matching the image filename
        image_dir: Path to directory containing house JPG images

    Returns:
        PIL Image object (128x128 RGB)
    """


def reduce_dimensions(image_dir: str, house_ids: list) -> np.ndarray:
    """Flatten images into pixel vectors and reduce to 2D with PCA.

    Args:
        image_dir: Path to directory containing house JPG images
        house_ids: List of house ID strings to include

    Returns:
        np.ndarray of shape (n_images, 2) with 2D PCA coordinates
    """


def detect_schematics(reduced_2d: np.ndarray, house_ids: list, known_ids=None) -> list:
    """Identify house IDs whose images are schematics/floorplans.

    Uses the 2D PCA-reduced space to find outlier images that cluster
    separately from real photographs. Known schematic IDs '4112' and '7758'
    are provided as reference points.

    Args:
        reduced_2d: 2D PCA-reduced coordinates, shape (N, 2)
        house_ids: List of house ID strings corresponding to rows of reduced_2d
        known_ids: List of known schematic house ID strings

    Returns:
        List of str house IDs classified as schematics (must include '4112' and '7758')
    """


class HouseImagesDataset(Dataset):
    """Custom PyTorch Dataset for house images + tabular features.

    Loads house listing images and preprocessed tabular features.
    For training data, also provides the target list_price.
    """

    def __init__(self, annot_file: Union[str, Path], image_dir: Union[str, Path], train: bool = True):
        """Initialize the dataset.

        Loads CSV, preprocesses tabular features (handle NaN, scale
        numeric columns, one-hot encode categoricals like property_type).
        Stores feature column names in self.feature_cols.

        Args:
            annot_file: Path to CSV file (home_data_train.csv or home_data_test.csv)
            image_dir: Path to directory containing house JPG images
            train: If True, CSV contains list_price column
        """

    def __len__(self) -> int:
        """Return number of samples in the dataset."""

    def __getitem__(self, idx: int) -> Dict[str, torch.Tensor]:
        """Get a single sample by index.

        Returns:
            Dict with keys:
            - 'image': FloatTensor of shape (3, 128, 128)
            - 'id': houseid as string
            - 'features': FloatTensor of preprocessed tabular features
            - 'price' (train only): list_price as FloatTensor scalar
        """


class HybridHouseNN(nn.Module):
    """Hybrid model combining CNN for images with MLP for tabular features.

    CNN branch processes (batch, 3, 128, 128) images.
    MLP branch processes (batch, num_features) tabular data.
    Concatenated outputs pass through final FC layers to predict price.
    """

    def __init__(self, num_features: int):
        """Initialize CNN branch, MLP branch, and final layers.

        Args:
            num_features: Number of tabular feature columns
        """

    def forward(self, ximg: torch.Tensor, xfeats: torch.Tensor) -> torch.Tensor:
        """Forward pass through both branches.

        Args:
            ximg: Image tensor of shape (batch, 3, 128, 128)
            xfeats: Features tensor of shape (batch, num_features)

        Returns:
            Non-negative price predictions of shape (batch, 1)
        """


class HouseImageOnly(nn.Module):
    """CNN-only model for predicting house prices from images.

    Processes (batch, 3, 128, 128) images through conv layers
    and FC layers to predict a single price value.
    """

    def __init__(self):
        """Initialize CNN layers and FC head."""

    def forward(self, ximg: torch.Tensor) -> torch.Tensor:
        """Forward pass through CNN.

        Args:
            ximg: Image tensor of shape (batch, 3, 128, 128)

        Returns:
            Non-negative price predictions of shape (batch, 1)
        """


class HouseFeatsOnly(nn.Module):
    """MLP-only model for predicting house prices from tabular features.

    Processes (batch, num_features) tabular data through FC layers
    to predict a single price value.
    """

    def __init__(self, num_features: int):
        """Initialize MLP layers.

        Args:
            num_features: Number of tabular feature columns
        """

    def forward(self, xfeats: torch.Tensor) -> torch.Tensor:
        """Forward pass through MLP.

        Args:
            xfeats: Features tensor of shape (batch, num_features)

        Returns:
            Non-negative price predictions of shape (batch, 1)
        """


def train_model(model: nn.Module, train_loader: DataLoader, optimizer, loss_fn, device: str, mode: str = "both") -> float:
    """Train the model for one epoch.

    Sets model to train mode. For each batch: moves data to device,
    runs forward pass based on mode, computes loss, runs backward
    pass, steps optimizer.

    Args:
        model: Neural network model (HybridHouseNN, HouseImageOnly, or HouseFeatsOnly)
        train_loader: DataLoader yielding dicts with 'image', 'features', 'price' keys
        optimizer: Optimizer (e.g., Adam)
        loss_fn: Loss function (e.g., torchmetrics.MeanSquaredError or nn.MSELoss)
        device: Device to run on ('cpu' or 'cuda')
        mode: "both" (hybrid), "image" (image-only), "features" (features-only)

    Returns:
        Average training loss for the epoch (total loss / num batches)
    """


def validate_model(model: nn.Module, valid_loader: DataLoader, loss_fn, device: str, mode: str = "both") -> Tuple[float, float]:
    """Evaluate the model on validation data.

    Sets model to eval mode with torch.no_grad(). For each batch:
    runs forward pass based on mode, computes loss, accumulates
    squared errors for RMSE calculation.

    Args:
        model: Neural network model
        valid_loader: DataLoader for validation data
        loss_fn: Loss function
        device: Device to run on ('cpu' or 'cuda')
        mode: "both" (hybrid), "image" (image-only), "features" (features-only)

    Returns:
        Tuple of (average_validation_loss, rmse_value) -- both floats
    """


def prepare_kaggle_submission(model: nn.Module, test_loader: DataLoader, device: str, mode: str = "both") -> pd.DataFrame:
    """Generate price predictions for the test set.

    Sets model to eval mode with torch.no_grad(). Runs forward pass
    on each test batch, collects house IDs and predictions.

    Args:
        model: Trained neural network model
        test_loader: DataLoader for test data (no 'price' key in batches)
        device: Device to run on ('cpu' or 'cuda')
        mode: "both" (hybrid), "image" (image-only), "features" (features-only)

    Returns:
        DataFrame with columns ['houseid', 'price'] where:
        - houseid values are strings
        - price values are predicted listing prices (floats)
    """
```

## Hints

- **Image loading**: Use `PIL.Image.open(os.path.join(image_dir, f"{house_id}.jpg"))` to load images. Images are 128x128 RGB (3 channels).
- **Flattening images for PCA**: Convert each PIL image to a numpy array with `np.array(img)`, then flatten with `.flatten()`. This produces a vector of length 128 * 128 * 3 = 49152.
- **Normalization constants**: `HOUSE_MEAN = [0.5230, 0.5416, 0.4989]`, `HOUSE_SD = [1, 1, 1]`. The `transforms.Normalize(mean, std)` formula is `(x - mean) / std`, so with std=1 this only subtracts the mean.
- **Handling NaN in tabular features**: Numeric columns like `beds`, `baths`, `sqft`, `lot_size`, `year_built` may have missing values. Fill with column median or mean before scaling.
- **One-hot encoding**: Use `pd.get_dummies(df, columns=['property_type'])` to one-hot encode the categorical column. The resulting feature count depends on the number of unique property types.
- **Feature scaling**: Apply `sklearn.preprocessing.StandardScaler` or `MinMaxScaler` to numeric columns. Store the scaler in `self` so the same transform applies at test time.
- **Dataset `'price'` key**: Use `'price'` as the dictionary key for the target value in `__getitem__`, not `'object'`. The training and validation functions access `batch['price']`.
- **Non-negative output**: House prices cannot be negative. Apply `torch.relu()`, `torch.abs()`, or `F.softplus()` to the final layer output of all three models.
- **Output shape**: All models should return shape `(batch, 1)`. If your final layer is `nn.Linear(..., 1)`, the output is already `(batch, 1)`.
- **Loss function**: The `loss_fn` parameter is passed in by the caller. Use it directly as `loss = loss_fn(predictions, targets)` — do NOT wrap it in `sqrt()`. The tests pass `torchmetrics.MeanSquaredError(squared=False)` which already computes RMSE.
- **train/val split**: Use `torch.utils.data.random_split(dataset, [0.75, 0.25])` to split the training dataset into 75% train and 25% validation.
- **Batch size**: Use `batch_size=64` for DataLoaders.
- **CPU only**: All training runs on CPU in Docker. No GPU available. Keep architectures small enough to train within the timeout.
- **Device handling**: Move images, features, and prices to the device with `.to(device)` inside the training/validation loops.
- **Schematic detection**: Schematics (floorplans, drawings) have distinctive pixel patterns compared to real house photos. In the 2D PCA space, they typically form an outlier cluster. Use the known IDs (4112, 7758) to locate this cluster, then find other points nearby.
- **Submission format**: The `houseid` column in the output DataFrame must contain strings, not integers. Use `str(houseid)` when collecting IDs.
- **No GPU needed**: CPU training works for this assignment. Each epoch takes 2-5 minutes on CPU depending on model complexity.

## Grading

| Part | Points |
|------|--------|
| Part 1: Exploring Home Images | 25 |
| Part 2: Predicting List Price | 70 |
| Part 3: Test Set Predictions | 5 |
| Part 4: TensorBoard (extra credit, skip) | 0 |
| **Total** | **100** |

**Test breakdown:**

| Test | Points | What it checks |
|------|--------|----------------|
| test_display_image | 5 | Returns PIL Image with correct size (128x128) |
| test_dimensionality_reduction | 10 | PCA reduces pixel vectors to 2D, correct shape |
| test_schematic_detection | 10 | Returns list containing known schematics (4112, 7758) |
| test_dataset_len | 5 | Dataset length matches number of rows in CSV |
| test_dataset_getitem | 10 | Returns dict with correct keys and tensor shapes |
| test_hybrid_model_forward | 15 | Forward pass produces (batch, 1) non-negative output |
| test_image_only_forward | 10 | Forward pass produces (batch, 1) non-negative output |
| test_features_only_forward | 10 | Forward pass produces (batch, 1) non-negative output |
| test_training_loop | 10 | train_model returns valid float loss |
| test_validation_rmse | 10 | validate_model returns valid (loss, rmse) tuple |
| test_submission_format | 5 | DataFrame has columns ['houseid', 'price'] with correct row count |

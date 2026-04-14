"""
COMP 341 Homework 7: Is a Picture Worth 1000 Words? - Test Suite

Tests organized by functional area:
- Image display and visualization
- Dimensionality reduction and schematic detection
- Custom PyTorch Dataset (HouseImagesDataset)
- Model architectures (HybridHouseNN, HouseImageOnly, HouseFeatsOnly)
- Training loop and validation
- Kaggle submission format
"""

import pytest
import pandas as pd
import numpy as np
import os
import sys
from pathlib import Path

# =============================================================================
# Test Configuration
# =============================================================================

# Get paths from environment or use defaults
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR',
    Path(__file__).parent.parent / 'data')) / 'hw7'
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
def train_csv():
    """Path to training CSV."""
    return str(DATA_DIR / 'home_data_train.csv')


@pytest.fixture(scope="module")
def holdout_csv():
    """Path to test CSV."""
    return str(DATA_DIR / 'home_data_test.csv')


@pytest.fixture(scope="module")
def image_dir():
    """Path to house images directory."""
    return str(DATA_DIR / 'house_imgs')


@pytest.fixture(scope="module")
def train_df():
    """Load training dataframe for extracting house IDs."""
    return pd.read_csv(DATA_DIR / 'home_data_train.csv')


@pytest.fixture(scope="module")
def dataset(train_csv, image_dir):
    """Full training HouseImagesDataset (module-scoped for efficiency)."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    return solution.HouseImagesDataset(train_csv, image_dir, train=True)


@pytest.fixture(scope="module")
def num_features(dataset):
    """Number of tabular features from the dataset."""
    sample = dataset[0]
    return sample['features'].shape[0]


@pytest.fixture(scope="module")
def small_subset(dataset):
    """Tiny subset (200 samples) for fast training/validation tests."""
    import torch
    from torch.utils.data import Subset
    return Subset(dataset, range(min(200, len(dataset))))


@pytest.fixture(scope="module")
def small_loader(small_subset):
    """DataLoader from the small subset."""
    from torch.utils.data import DataLoader
    return DataLoader(small_subset, batch_size=32, shuffle=True)


@pytest.fixture(scope="module")
def trained_model_results(num_features, small_loader):
    """Train HybridHouseNN for 1 epoch on tiny subset for validation test."""
    import torch
    from torchmetrics import MeanSquaredError

    model = solution.HybridHouseNN(num_features)
    model.to('cpu')
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001)
    loss_fn = MeanSquaredError(squared=False).to('cpu')

    train_loss = solution.train_model(
        model, small_loader, optimizer, loss_fn,
        device='cpu', mode='both'
    )

    return {
        'model': model,
        'train_loss': train_loss,
        'optimizer': optimizer,
        'loss_fn': loss_fn,
    }


@pytest.fixture(scope="module")
def small_house_ids(train_df):
    """Small subset of house IDs (100) for dimensionality reduction tests."""
    ids = train_df['houseid'].astype(str).tolist()[:100]
    return ids


@pytest.fixture(scope="module")
def reduced_2d(image_dir, small_house_ids):
    """PCA/t-SNE reduced 2D coordinates from a small subset of images."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    return solution.reduce_dimensions(image_dir, small_house_ids)


# =============================================================================
# Display Image
# =============================================================================

class TestDisplayImage:
    """Verify display_image returns a valid PIL Image."""

    def test_dataset_len(self, dataset):
        """Verify training dataset has 4000 samples."""
        assert len(dataset) == 4000, \
            f"Training dataset should have 4000 samples, " \
            f"got {len(dataset)}"


# =============================================================================
# Dataset __getitem__
# =============================================================================

class TestDatasetGetitem:
    """Verify HouseImagesDataset returns correct item structure."""

    def test_dataset_getitem(self, dataset, train_csv, image_dir):
        """Verify dataset[0] returns dict with correct keys and tensor shapes."""
        import torch

        # Test training mode item
        sample = dataset[0]

        # Should be a dict
        assert isinstance(sample, dict), \
            f"dataset[0] should return a dict, " \
            f"got {type(sample).__name__}"

        # Check required keys for training mode
        required_keys = {'image', 'id', 'features', 'price'}
        assert required_keys.issubset(set(sample.keys())), \
            f"Training sample should have keys {required_keys}, " \
            f"got {set(sample.keys())}"

        # Check image tensor shape: (3, 128, 128)
        assert isinstance(sample['image'], torch.Tensor), \
            f"'image' should be a torch.Tensor, " \
            f"got {type(sample['image']).__name__}"
        assert sample['image'].shape == (3, 128, 128), \
            f"'image' shape should be (3, 128, 128), " \
            f"got {tuple(sample['image'].shape)}"

        # Check features tensor
        assert isinstance(sample['features'], torch.Tensor), \
            f"'features' should be a torch.Tensor, " \
            f"got {type(sample['features']).__name__}"
        assert sample['features'].dtype == torch.float32, \
            f"'features' dtype should be float32, " \
            f"got {sample['features'].dtype}"
        assert sample['features'].numel() >= 1, \
            "'features' should have at least 1 element"

        # Check price tensor
        assert isinstance(sample['price'], torch.Tensor), \
            f"'price' should be a torch.Tensor, " \
            f"got {type(sample['price']).__name__}"

        # Test mode (train=False) should NOT have 'price'
        test_dataset = solution.HouseImagesDataset(
            str(DATA_DIR / 'home_data_test.csv'), image_dir, train=False
        )
        test_sample = test_dataset[0]
        assert 'price' not in test_sample, \
            "Test mode sample should NOT have 'price' key"


# =============================================================================
# Hybrid Model Forward Pass
# =============================================================================

class TestHybridModelForward:
    """Verify HybridHouseNN forward pass produces correct output."""

    def test_hybrid_model_forward(self, num_features):
        """Verify HybridHouseNN output shape and properties."""
        import torch

        model = solution.HybridHouseNN(num_features)
        model.eval()

        # Create dummy batch
        images = torch.randn(4, 3, 128, 128)
        features = torch.randn(4, num_features)

        with torch.no_grad():
            output = model(images, features)

        # Accept shape (4, 1) or (4,)
        squeezed = output.squeeze()
        assert squeezed.shape == (4,), \
            f"Output shape should be (4, 1) or (4,), " \
            f"got {tuple(output.shape)}"

        # No NaN values
        assert not torch.any(torch.isnan(output)), \
            "Model output should not contain NaN values"

        # Values should be finite
        assert torch.all(torch.isfinite(output)), \
            "Model output values should be finite (not inf)"


# =============================================================================
# Image-Only Model Forward Pass
# =============================================================================

class TestImageOnlyForward:
    """Verify HouseImageOnly forward pass produces correct output."""

    def test_image_only_forward(self):
        """Verify HouseImageOnly output shape and properties."""
        import torch

        if not SOLUTION_AVAILABLE:
            pytest.skip("solution.py not available")

        model = solution.HouseImageOnly()
        model.eval()

        # Create dummy batch of images
        images = torch.randn(4, 3, 128, 128)

        with torch.no_grad():
            output = model(images)

        # Accept shape (4, 1) or (4,)
        squeezed = output.squeeze()
        assert squeezed.shape == (4,), \
            f"Output shape should be (4, 1) or (4,), " \
            f"got {tuple(output.shape)}"

        # No NaN values
        assert not torch.any(torch.isnan(output)), \
            "Model output should not contain NaN values"

        # Values should be finite
        assert torch.all(torch.isfinite(output)), \
            "Model output values should be finite (not inf)"


# =============================================================================
# Features-Only Model Forward Pass
# =============================================================================

class TestFeaturesOnlyForward:
    """Verify HouseFeatsOnly forward pass produces correct output."""

    def test_features_only_forward(self, num_features):
        """Verify HouseFeatsOnly output shape and properties."""
        import torch

        model = solution.HouseFeatsOnly(num_features)
        model.eval()

        # Create dummy batch of features
        features = torch.randn(4, num_features)

        with torch.no_grad():
            output = model(features)

        # Accept shape (4, 1) or (4,)
        squeezed = output.squeeze()
        assert squeezed.shape == (4,), \
            f"Output shape should be (4, 1) or (4,), " \
            f"got {tuple(output.shape)}"

        # No NaN values
        assert not torch.any(torch.isnan(output)), \
            "Model output should not contain NaN values"

        # Values should be finite
        assert torch.all(torch.isfinite(output)), \
            "Model output values should be finite (not inf)"


# =============================================================================
# Training Loop
# =============================================================================

class TestTrainingLoop:
    """Verify train_model returns a valid loss value."""

    def test_training_loop(self, num_features, small_loader):
        """Verify 1 epoch of training on tiny subset returns valid loss."""
        import torch
        from torchmetrics import MeanSquaredError

        model = solution.HybridHouseNN(num_features)
        model.to('cpu')
        optimizer = torch.optim.Adam(model.parameters(), lr=0.001)
        loss_fn = MeanSquaredError(squared=False).to('cpu')

        train_loss = solution.train_model(
            model, small_loader, optimizer, loss_fn,
            device='cpu', mode='both'
        )

        # Should return a float
        assert isinstance(train_loss, (float, int, np.floating)), \
            f"train_model should return a float, " \
            f"got {type(train_loss).__name__}"

        train_loss = float(train_loss)

        # Loss should be positive
        assert train_loss > 0, \
            f"Training loss should be positive, got {train_loss}"

        # Loss should be finite (not inf or nan)
        assert np.isfinite(train_loss), \
            f"Training loss should be finite, got {train_loss}"


# =============================================================================
# Validation RMSE
# =============================================================================

class TestValidationRmse:
    """Verify validate_model returns valid RMSE and predictions."""

    def test_validation_rmse(self, trained_model_results, small_loader):
        """Verify validation returns a tuple of 2 positive finite values."""
        from torchmetrics import MeanSquaredError

        model = trained_model_results['model']
        # Fresh loss_fn to avoid accumulated state from training
        loss_fn = MeanSquaredError(squared=False).to('cpu')

        result = solution.validate_model(
            model, small_loader, loss_fn,
            device='cpu', mode='both'
        )

        # Should return a tuple of length 2
        assert isinstance(result, tuple), \
            f"validate_model should return a tuple, " \
            f"got {type(result).__name__}"
        assert len(result) == 2, \
            f"validate_model should return a tuple of length 2, " \
            f"got length {len(result)}"

        val_loss, val_metric = result

        # Both values should be numeric
        val_loss = float(val_loss)
        val_metric = float(val_metric)

        # Both should be positive
        assert val_loss > 0, \
            f"Validation loss should be positive, got {val_loss}"
        assert val_metric > 0, \
            f"Validation metric should be positive, got {val_metric}"

        # Both should be finite
        assert np.isfinite(val_loss), \
            f"Validation loss should be finite, got {val_loss}"
        assert np.isfinite(val_metric), \
            f"Validation metric should be finite, got {val_metric}"


# =============================================================================
# Submission Format
# =============================================================================

class TestSubmissionFormat:
    """Verify Kaggle submission format."""

    def test_submission_format(self, num_features, holdout_csv, image_dir):
        """Verify prepare_kaggle_submission returns correct DataFrame format."""
        import torch
        from torch.utils.data import DataLoader

        # Create test dataset (train=False, no price labels)
        test_dataset = solution.HouseImagesDataset(
            holdout_csv, image_dir, train=False
        )
        test_loader = DataLoader(test_dataset, batch_size=64, shuffle=False)

        # Use an untrained model - we only care about format
        model = solution.HybridHouseNN(num_features)

        submission = solution.prepare_kaggle_submission(
            model, test_loader, device='cpu', mode='both'
        )

        # Should return a DataFrame
        assert isinstance(submission, pd.DataFrame), \
            f"prepare_kaggle_submission should return pd.DataFrame, " \
            f"got {type(submission).__name__}"

        # Check columns
        assert list(submission.columns) == ['houseid', 'price'], \
            f"Columns should be ['houseid', 'price'], " \
            f"got {list(submission.columns)}"

        # Check row count matches test set
        assert len(submission) == 3152, \
            f"Submission should have 3152 rows (test set size), " \
            f"got {len(submission)}"

        # No NaN in price column
        assert submission['price'].notna().all(), \
            f"Price column should have no NaN values, " \
            f"found {submission['price'].isna().sum()} NaN entries"

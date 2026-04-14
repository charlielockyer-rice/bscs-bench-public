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

    def test_display_image(self, image_dir, train_df):
        """Verify display_image returns a 128x128 PIL Image."""
        from PIL import Image

        # Use a known house_id from the training data
        house_id = str(train_df['houseid'].iloc[0])
        result = solution.display_image(house_id, image_dir)

        # Should return a PIL Image
        assert isinstance(result, Image.Image), \
            f"display_image should return PIL.Image.Image, " \
            f"got {type(result).__name__}"

        # Image should be 128x128
        assert result.size == (128, 128), \
            f"Image size should be (128, 128), got {result.size}"


# =============================================================================
# Dimensionality Reduction
# =============================================================================

class TestDimensionalityReduction:
    """Verify reduce_dimensions returns valid 2D coordinates."""

    def test_dimensionality_reduction(self, reduced_2d, small_house_ids):
        """Verify reduce_dimensions returns (n, 2) array with no NaN."""
        n_ids = len(small_house_ids)

        # Should return numpy array
        assert isinstance(reduced_2d, np.ndarray), \
            f"reduce_dimensions should return np.ndarray, " \
            f"got {type(reduced_2d).__name__}"

        # Shape should be (n_ids, 2)
        assert reduced_2d.shape == (n_ids, 2), \
            f"Shape should be ({n_ids}, 2), got {reduced_2d.shape}"

        # No NaN values
        assert not np.any(np.isnan(reduced_2d)), \
            "Reduced coordinates should not contain NaN values"


# =============================================================================
# Schematic Detection
# =============================================================================

class TestSchematicDetection:
    """Verify detect_schematics identifies schematic images."""

    def test_schematic_detection(self, image_dir, train_df):
        """Verify detect_schematics returns a list containing known schematics."""
        if not SOLUTION_AVAILABLE:
            pytest.skip("solution.py not available")

        # Use a subset that includes known schematic IDs
        # Build a list of 100 house IDs that includes the known schematics
        all_ids = train_df['houseid'].astype(str).tolist()

        # Ensure known schematics are in our subset
        known_schematics = ['4112', '7758']
        subset_ids = []
        for kid in known_schematics:
            if kid in all_ids:
                subset_ids.append(kid)

        # Fill up to 100 with other IDs
        for hid in all_ids:
            if hid not in subset_ids:
                subset_ids.append(hid)
            if len(subset_ids) >= 100:
                break

        # First reduce dimensions on this subset
        reduced_2d = solution.reduce_dimensions(image_dir, subset_ids)

        # Detect schematics
        result = solution.detect_schematics(
            reduced_2d, subset_ids, known_ids=known_schematics
        )

        # Should return a list
        assert isinstance(result, list), \
            f"detect_schematics should return a list, " \
            f"got {type(result).__name__}"

        # Known schematics should be in the result
        for kid in known_schematics:
            if kid in subset_ids:
                assert kid in result, \
                    f"Known schematic '{kid}' should be in detected list"

        # Length should be reasonable (at least the known ones, at most half the input)
        assert 2 <= len(result) <= len(subset_ids) // 2, \
            f"Number of detected schematics should be between 2 and {len(subset_ids) // 2}, " \
            f"got {len(result)}"


# =============================================================================
# Dataset Length
# =============================================================================

class TestDatasetLen:
    """Verify HouseImagesDataset has correct length."""


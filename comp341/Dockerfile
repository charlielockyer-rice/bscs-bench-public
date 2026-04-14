# COMP 341: Practical Machine Learning
# Python 3.11 + ML stack (sklearn, torch, plotnine)

FROM python:3.11-slim

WORKDIR /workspace

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    && rm -rf /var/lib/apt/lists/*

# Install Python ML dependencies
RUN pip install --no-cache-dir \
    numpy==1.26.4 \
    pandas==2.2.0 \
    scikit-learn==1.4.0 \
    plotnine==0.13.3 \
    matplotlib==3.8.2 \
    seaborn==0.13.2 \
    pillow==10.2.0

# Install PyTorch (CPU version for smaller image)
RUN pip install --no-cache-dir \
    torch==2.2.0 torchvision==0.17.0 \
    --index-url https://download.pytorch.org/whl/cpu && \
    pip install --no-cache-dir torchmetrics==1.6.0

# Install testing dependencies
RUN pip install --no-cache-dir \
    pytest==8.0.0 \
    pytest-timeout==2.2.0

# Set environment
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

CMD ["bash"]

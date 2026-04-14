#!/usr/bin/env python3
"""
Generate synthetic stock price data using geometric Brownian motion.

This creates realistic-looking stock price data for testing the Module 3
stock prediction algorithms without requiring real market data.

Usage:
    python generate_stock_data.py

Output files:
    comp140_module3_DJIA.txt  - Dow Jones Industrial Average (low volatility)
    comp140_module3_GOOG.txt  - Google stock (medium volatility)
    comp140_module3_FSLR.txt  - First Solar (high volatility)
"""

import math
import random
from pathlib import Path


def generate_gbm_prices(
    initial_price: float,
    num_days: int,
    annual_drift: float = 0.08,
    annual_volatility: float = 0.20,
    seed: int = None
) -> list[float]:
    """
    Generate stock prices using geometric Brownian motion.

    The model: S(t+1) = S(t) * exp((μ - σ²/2)Δt + σ√Δt * Z)

    Args:
        initial_price: Starting stock price
        num_days: Number of trading days to generate
        annual_drift: Expected annual return (μ), typically 0.05-0.15
        annual_volatility: Annual volatility (σ), typically 0.15-0.50
        seed: Random seed for reproducibility

    Returns:
        List of daily closing prices
    """
    if seed is not None:
        random.seed(seed)

    # Convert annual parameters to daily
    trading_days_per_year = 252
    dt = 1.0 / trading_days_per_year
    daily_drift = (annual_drift - 0.5 * annual_volatility ** 2) * dt
    daily_vol = annual_volatility * math.sqrt(dt)

    prices = [initial_price]
    for _ in range(num_days - 1):
        # Standard normal random variable
        z = random.gauss(0, 1)
        # Geometric Brownian motion step
        growth = math.exp(daily_drift + daily_vol * z)
        new_price = prices[-1] * growth
        prices.append(round(new_price, 2))

    return prices


def main():
    """Generate all stock data files."""
    output_dir = Path(__file__).parent

    # Configuration for each stock
    stocks = [
        {
            "symbol": "DJIA",
            "initial_price": 13000.0,
            "volatility": 0.15,
            "drift": 0.06,
            "days": 500,
            "seed": 42,
        },
        {
            "symbol": "GOOG",
            "initial_price": 700.0,
            "volatility": 0.25,
            "drift": 0.10,
            "days": 500,
            "seed": 123,
        },
        {
            "symbol": "FSLR",
            "initial_price": 30.0,
            "volatility": 0.45,
            "drift": 0.05,
            "days": 500,
            "seed": 456,
        },
    ]

    for stock in stocks:
        print(f"Generating {stock['symbol']} data...")

        prices = generate_gbm_prices(
            initial_price=stock["initial_price"],
            num_days=stock["days"],
            annual_drift=stock["drift"],
            annual_volatility=stock["volatility"],
            seed=stock["seed"],
        )

        # Write space-separated prices to file
        filename = f"comp140_module3_{stock['symbol']}.txt"
        filepath = output_dir / filename

        with open(filepath, "w") as f:
            f.write(" ".join(str(p) for p in prices))

        # Print summary statistics
        print(f"  File: {filename}")
        print(f"  Days: {len(prices)}")
        print(f"  Range: {min(prices):.2f} - {max(prices):.2f}")
        print(f"  Start: {prices[0]:.2f}, End: {prices[-1]:.2f}")
        print()


if __name__ == "__main__":
    main()

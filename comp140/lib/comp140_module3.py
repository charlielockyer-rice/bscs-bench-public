"""
Support code for analyzing stocks.
"""

import urllib.request
import codeskulptor
import simpleplot
from collections import defaultdict

# Boundaries for the bins, as the amount of change
BINS = (-.01, 0, .01)

# Supported stock symbols
SYMBOLS = ("FSLR", "GOOG", "DJIA")

def get_supported_symbols():
    """
    Return a tuple of the supported stock symbols.
    """
    return SYMBOLS

def get_historical_prices(symbol):
    """
    Get stock prices for the supported stock symbols.
    """
    if symbol not in SYMBOLS:
        msg = "No data is available for "
        msg += str(symbol) + " try one of " + str(SYMBOLS)
        raise ValueError(msg)
    name = "comp140_module3_" + symbol + ".txt"
    filepath = codeskulptor.file2url(name)

    # Handle both local files and URLs
    if filepath.startswith(('http://', 'https://')):
        stockf = urllib.request.urlopen(filepath)
        stockd = stockf.read().decode('ascii')
    else:
        with open(filepath, 'r') as f:
            stockd = f.read()

    return [float(item) for item in stockd.split()]

DJIA_JAN2013 = [13412.55, 13391.36, 13435.21,
                13384.29, 13328.85, 13390.51,
                13471.22, 13488.43, 13507.32,
                13534.89, 13511.23, 13596.02,
                13649.7, 13712.21, 13779.33,
                13825.33, 13895.98, 13881.93]

GOOG_JAN2013 = [723.25, 723.67, 737.97,
                734.75, 733.3, 738.12,
                741.48, 739.99, 723.25,
                724.93, 715.19, 711.32,
                704.51, 702.87, 741.5,
                754.21, 753.67, 750.73]

FSLR_JAN2013 = [31.99, 34.41, 33.59,
                31.37, 31.02, 31.91,
                31.97, 32.01, 31.21,
                31.62, 30.72, 30.53,
                30.08, 31.58, 31.15,
                30.3, 30.21, 29.7]

def get_test_prices(symbol):
    """
    Get stock prices to be used for predictions.
    """
    if symbol not in SYMBOLS:
        msg = "No data is available for "
        msg += str(symbol) + " try one of " + str(SYMBOLS)
        raise ValueError(msg)
    if symbol == "DJIA":
        return DJIA_JAN2013
    elif symbol == "GOOG":
        return GOOG_JAN2013
    elif symbol == "FSLR":
        return FSLR_JAN2013
    # Error.  Shouldn't reach here.
    return []

def compute_daily_change(price_data):
    """
    Return a list of day-to-day deltas given a list of stock prices
    """
    return [(price_data[i] - price_data[i-1]) / float(price_data[i-1])
            for i in range(1, len(price_data))]

def bin_daily_changes(deltas):
    """
    Return a list of bins, given a list of deltas and a sequence of
    boundaries for the bins.
    """
    binned = []
    for val in deltas:
        newbin = -1
        for idx, binval in enumerate(BINS):
            if val < binval:
                newbin = idx
                break
        if newbin == -1:
            newbin = len(BINS)
        binned.append(newbin)
    return binned

def create_histogram(data):
    """
    Create a histogram from the given list of data.
    """
    hist = defaultdict(int)
    for item in data:
        hist[item] += 1
    return hist

def plot_daily_change(changes):
    """
    Plot daily change in stock price.
    """
    symbols = list(changes.keys())
    changedata = [list(enumerate(changes[symbol])) for symbol in symbols]
    simpleplot.plot_lines("Daily Change", 800, 400, "Day", "Change",
                          changedata, False, symbols)

def plot_bin_histogram(bins):
    """
    Plot a histogram of the binned data.
    """
    hist = []
    symbols = []
    for symbol, data in bins.items():
        symbols.append(symbol)
        hist.append(create_histogram(data))
    simpleplot.plot_bars("Bin Histogram", 800, 400, "Bin", "Number of Days", hist, symbols)

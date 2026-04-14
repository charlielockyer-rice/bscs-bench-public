"""
Stub module for CodeSkulptor's simpleplot.
This provides mock implementations for testing/evaluation purposes.
The actual plotting functionality is not available outside CodeSkulptor,
but this can output data that could be plotted with matplotlib or similar.
"""

import json


class Plot:
    """
    Mock Plot class that records plot data.
    Can be used to export data for plotting with other libraries.
    """

    def __init__(self, title=""):
        self._title = title
        self._datasets = []
        self._xlabel = ""
        self._ylabel = ""

    def add_dataset(self, data, color=None, label=None):
        """Add a dataset to the plot."""
        self._datasets.append({
            'data': list(data),
            'color': color,
            'label': label
        })

    def set_xlabel(self, label):
        """Set the x-axis label."""
        self._xlabel = label

    def set_ylabel(self, label):
        """Set the y-axis label."""
        self._ylabel = label

    def get_data(self):
        """Get all plot data as a dictionary."""
        return {
            'title': self._title,
            'xlabel': self._xlabel,
            'ylabel': self._ylabel,
            'datasets': self._datasets
        }

    def to_json(self):
        """Export plot data as JSON."""
        return json.dumps(self.get_data(), indent=2)


def plot_lines(title, width, height, xlabel, ylabel, datasets, legends=None):
    """
    Create a line plot.

    In CodeSkulptor, this creates an interactive plot.
    In this stub, it prints the data and returns a Plot object.

    inputs:
        - title: the plot title
        - width, height: dimensions (ignored in stub)
        - xlabel, ylabel: axis labels
        - datasets: list of [(x1, y1), (x2, y2), ...] data points
        - legends: optional list of legend labels

    Returns: a Plot object containing the data
    """
    print(f"[simpleplot stub] Creating line plot: {title}")
    print(f"  X-axis: {xlabel}")
    print(f"  Y-axis: {ylabel}")
    print(f"  Datasets: {len(datasets)}")

    plot = Plot(title)
    plot.set_xlabel(xlabel)
    plot.set_ylabel(ylabel)

    for i, data in enumerate(datasets):
        label = legends[i] if legends and i < len(legends) else None
        plot.add_dataset(data, label=label)

    return plot


def plot_bars(title, width, height, xlabel, ylabel, datasets, legends=None):
    """
    Create a bar plot.

    In CodeSkulptor, this creates an interactive bar chart.
    In this stub, it prints the data and returns a Plot object.

    inputs:
        - title: the plot title
        - width, height: dimensions (ignored in stub)
        - xlabel, ylabel: axis labels
        - datasets: list of [(label1, value1), (label2, value2), ...] data
        - legends: optional list of legend labels

    Returns: a Plot object containing the data
    """
    print(f"[simpleplot stub] Creating bar plot: {title}")
    print(f"  X-axis: {xlabel}")
    print(f"  Y-axis: {ylabel}")
    print(f"  Datasets: {len(datasets)}")

    plot = Plot(title)
    plot.set_xlabel(xlabel)
    plot.set_ylabel(ylabel)

    for i, data in enumerate(datasets):
        label = legends[i] if legends and i < len(legends) else None
        plot.add_dataset(data, label=label)

    return plot


def plot_scatter(title, width, height, xlabel, ylabel, datasets, legends=None):
    """
    Create a scatter plot.

    In CodeSkulptor, this creates an interactive scatter plot.
    In this stub, it prints the data and returns a Plot object.

    inputs:
        - title: the plot title
        - width, height: dimensions (ignored in stub)
        - xlabel, ylabel: axis labels
        - datasets: list of [(x1, y1), (x2, y2), ...] data points
        - legends: optional list of legend labels

    Returns: a Plot object containing the data
    """
    print(f"[simpleplot stub] Creating scatter plot: {title}")
    print(f"  X-axis: {xlabel}")
    print(f"  Y-axis: {ylabel}")
    print(f"  Datasets: {len(datasets)}")

    plot = Plot(title)
    plot.set_xlabel(xlabel)
    plot.set_ylabel(ylabel)

    for i, data in enumerate(datasets):
        label = legends[i] if legends and i < len(legends) else None
        plot.add_dataset(data, label=label)

    return plot

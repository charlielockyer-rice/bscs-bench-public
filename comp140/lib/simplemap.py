"""
Stub module for CodeSkulptor's simplemap.
This provides mock implementations for testing/evaluation purposes.
The actual Google Maps functionality is not available outside CodeSkulptor.
"""

# Location constants (as used in the course)
Rice = (29.7174, -95.4018)  # Rice University coordinates
Houston = (29.7604, -95.3698)
NewYork = (40.7128, -74.0060)
LosAngeles = (34.0522, -118.2437)


class Map:
    """
    Mock Map class that simulates CodeSkulptor's simplemap.Map.
    """

    def __init__(self, title, location, width, height, control_width):
        self._title = title
        self._location = location
        self._width = width
        self._height = height
        self._control_width = control_width
        self._markers = {}
        self._lines = []
        print(f"[simplemap stub] Created map: {title} at {location}")

    def add_marker(self, description, marker_id, icon, location, click_handler=None):
        """Add a marker to the map."""
        marker = Marker(description, marker_id, icon, location, click_handler)
        self._markers[marker_id] = marker
        return marker

    def add_button(self, text, handler, width=100):
        """Add a button to the control panel."""
        print(f"[simplemap stub] Added button: {text}")

    def add_break(self):
        """Add a line break in the control panel."""
        pass

    def add_label(self, text, width=100):
        """Add a label to the control panel."""
        print(f"[simplemap stub] Added label: {text}")

    def draw_line(self, start_marker, stop_marker, path=None):
        """Draw a line between two markers."""
        line = Line(start_marker, stop_marker, path)
        self._lines.append(line)
        return line

    def clear_lines(self):
        """Clear all lines from the map."""
        self._lines = []


class Marker:
    """
    Mock Marker class for map markers.
    """

    def __init__(self, description, marker_id, icon, location, click_handler=None):
        self._description = description
        self._id = marker_id
        self._icon = icon
        self._location = location
        self._click_handler = click_handler

    def get_description(self):
        """Get the marker description."""
        return self._description

    def get_id(self):
        """Get the marker ID."""
        return self._id

    def get_location(self):
        """Get the marker location."""
        return self._location

    def set_icon(self, icon):
        """Set the marker icon."""
        self._icon = icon

    def click(self):
        """Simulate a click on the marker."""
        if self._click_handler:
            self._click_handler(self)


class Line:
    """
    Mock Line class for map lines.
    """

    def __init__(self, start_marker, stop_marker, path=None):
        self._start = start_marker
        self._stop = stop_marker
        self._path = path
        self._color = 'Blue'

    def set_color(self, color):
        """Set the line color."""
        self._color = color

    def get_color(self):
        """Get the line color."""
        return self._color


def create_map(title, location, width, height, control_width=200):
    """Create a new map."""
    return Map(title, location, width, height, control_width)

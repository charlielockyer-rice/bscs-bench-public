"""
Stub module for CodeSkulptor's simplegui.
This provides mock implementations for testing/evaluation purposes.
The actual GUI functionality is not available outside CodeSkulptor.
"""

# Default canvas dimensions
DEFAULT_WIDTH = 400
DEFAULT_HEIGHT = 400


class Frame:
    """
    Mock Frame class that simulates CodeSkulptor's simplegui.Frame.
    """

    def __init__(self, title, width, height, control_width=200):
        self._title = title
        self._width = width
        self._height = height
        self._control_width = control_width
        self._draw_handler = None
        self._mouseclick_handler = None
        self._keydown_handler = None
        self._keyup_handler = None
        self._canvas_background = "white"
        self._running = False
        print(f"[simplegui stub] Created frame: {title} ({width}x{height})")

    def start(self):
        """Start the frame (no-op in stub)."""
        self._running = True
        print(f"[simplegui stub] Frame started: {self._title}")

    def stop(self):
        """Stop the frame (no-op in stub)."""
        self._running = False
        print(f"[simplegui stub] Frame stopped: {self._title}")

    def set_draw_handler(self, handler):
        """Set the draw handler function."""
        self._draw_handler = handler

    def set_mouseclick_handler(self, handler):
        """Set the mouse click handler function."""
        self._mouseclick_handler = handler

    def set_keydown_handler(self, handler):
        """Set the key down handler function."""
        self._keydown_handler = handler

    def set_keyup_handler(self, handler):
        """Set the key up handler function."""
        self._keyup_handler = handler

    def set_canvas_background(self, color):
        """Set the canvas background color."""
        self._canvas_background = color

    def add_button(self, text, handler, width=100):
        """Add a button to the control panel."""
        print(f"[simplegui stub] Added button: {text}")
        return MockButton(text, handler)

    def add_label(self, text, width=100):
        """Add a label to the control panel."""
        print(f"[simplegui stub] Added label: {text}")
        return MockLabel(text)

    def add_input(self, text, handler, width=100):
        """Add an input field to the control panel."""
        print(f"[simplegui stub] Added input: {text}")
        return MockInput(text, handler)


class MockButton:
    """Mock button for control panel."""
    def __init__(self, text, handler):
        self._text = text
        self._handler = handler


class MockLabel:
    """Mock label for control panel."""
    def __init__(self, text):
        self._text = text

    def set_text(self, text):
        self._text = text


class MockInput:
    """Mock input field for control panel."""
    def __init__(self, text, handler):
        self._text = text
        self._handler = handler


class Canvas:
    """
    Mock Canvas class for drawing operations.
    Records draw calls for testing/verification.
    """

    def __init__(self):
        self._operations = []

    def draw_circle(self, center, radius, line_width, line_color, fill_color=None):
        """Draw a circle."""
        self._operations.append(('circle', center, radius, line_width, line_color, fill_color))

    def draw_line(self, point1, point2, line_width, line_color):
        """Draw a line."""
        self._operations.append(('line', point1, point2, line_width, line_color))

    def draw_polyline(self, points, line_width, line_color):
        """Draw a polyline."""
        self._operations.append(('polyline', points, line_width, line_color))

    def draw_polygon(self, points, line_width, line_color, fill_color=None):
        """Draw a polygon."""
        self._operations.append(('polygon', points, line_width, line_color, fill_color))

    def draw_text(self, text, point, font_size, font_color, font_face="serif"):
        """Draw text."""
        self._operations.append(('text', text, point, font_size, font_color, font_face))

    def draw_image(self, image, center_source, width_height_source, center_dest, width_height_dest, rotation=0):
        """Draw an image."""
        self._operations.append(('image', image, center_source, width_height_source, center_dest, width_height_dest, rotation))

    def clear(self):
        """Clear recorded operations."""
        self._operations = []


class Timer:
    """Mock Timer class."""

    def __init__(self, interval, handler):
        self._interval = interval
        self._handler = handler
        self._running = False

    def start(self):
        self._running = True

    def stop(self):
        self._running = False

    def is_running(self):
        return self._running


class Sound:
    """Mock Sound class."""

    def __init__(self, url):
        self._url = url
        self._volume = 1.0

    def play(self):
        pass

    def pause(self):
        pass

    def rewind(self):
        pass

    def set_volume(self, volume):
        self._volume = volume


class Image:
    """Mock Image class."""

    def __init__(self, url):
        self._url = url
        self._width = 100
        self._height = 100

    def get_width(self):
        return self._width

    def get_height(self):
        return self._height


def create_frame(title, canvas_width, canvas_height, control_width=200):
    """Create a new frame."""
    return Frame(title, canvas_width, canvas_height, control_width)


def create_timer(interval, handler):
    """Create a new timer."""
    return Timer(interval, handler)


def load_sound(url):
    """Load a sound from a URL."""
    return Sound(url)


def load_image(url):
    """Load an image from a URL."""
    return Image(url)


# Key constants
KEY_MAP = {
    'left': 37, 'up': 38, 'right': 39, 'down': 40,
    'space': 32, 'a': 65, 'b': 66, 'c': 67, 'd': 68, 'e': 69,
    'f': 70, 'g': 71, 'h': 72, 'i': 73, 'j': 74, 'k': 75,
    'l': 76, 'm': 77, 'n': 78, 'o': 79, 'p': 80, 'q': 81,
    'r': 82, 's': 83, 't': 84, 'u': 85, 'v': 86, 'w': 87,
    'x': 88, 'y': 89, 'z': 90,
    '0': 48, '1': 49, '2': 50, '3': 51, '4': 52,
    '5': 53, '6': 54, '7': 55, '8': 56, '9': 57,
}

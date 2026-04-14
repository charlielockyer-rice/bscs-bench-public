'''
Support code for Module 1

Use this code by calling start with a "make_circle" function,
which has the following signature:

def make_circle(x1, y1, x2, y2, x3, y3):
    """
    Given three points, return the center and radius
    of a circle that intersects them
    """
    <code here>
    return center_x, center_y, radius
'''

import simplegui

HEIGHT = 400
WIDTH = 400

class Points:
    """
    Container to hold up to three points.
    """
    def __init__(self):
        self._points = []

    def __str__(self):
        result = ""
        for i in range(len(self._points)):
            result += "p" + str(i) + ": ("
            result += str(self._points[i][0])
            result += ", "
            result += str(self._points[i][1])
            result += ") "
        return result

    def num_points(self):
        """
        Return the current number of points being held.
        """
        return len(self._points)

    def add_point(self, new_point):
        """
        Add a new point.
        Ensures that no two points have the same x or y value and that
        the three points are not co-linear.
        """
        if len(self._points) == 3:
            self._points.pop(0)
        for point in self._points:
            if point[0] == new_point[0]:
                errmsg = "New point has same x value ("
                errmsg += str(new_point[0])
                errmsg += ") as another point!"
                print(errmsg)
                return
            if point[1] == new_point[1]:
                errmsg = "New point has same y value ("
                errmsg += str(new_point[1])
                errmsg += ") as another point!"
                print(errmsg)
                return
        if len(self._points) == 2:
            slope1 = (float(self._points[0][1] - new_point[1])
                      / float(self._points[0][0] - new_point[0]))
            slope2 = (float(self._points[1][1] - new_point[1])
                      / float(self._points[1][0] - new_point[0]))
            if slope1 == slope2:
                print("New point is collinear with existing points!")
                return
        self._points.append(tuple(new_point))

    def get_point(self, index):
        """
        Returns the index'th point.
        """
        if index < 0 or index >= len(self._points):
            print("No point", index)
            return
        return self._points[index][0], self._points[index][1]

    def draw(self, canvas):
        """
        Draw the point on the given canvas.
        """
        for point in self._points:
            canvas.draw_circle(point, 2, 1, "black", "black")


class Circle:
    """
    Class to represent a circle.
    """
    def __init__(self):
        self.reset()

    def __str__(self):
        result = ""
        if self._radius:
            result += "center x: " + str(self._center[0])
            result += " center y: " + str(self._center[1])
            result += " radius: " + str(self._radius)
        return result

    def reset(self):
        """
        Reset the circle to nothing.
        """
        self._center = (0, 0)
        self._radius = 0

    def set_center(self, centerx, centery):
        """
        Set the x, y coordinates of the center of the circle.
        """
        self._center = (centerx, centery)

    def set_radius(self, radius):
        """
        Set the radius of the circle.
        """
        self._radius = radius

    def draw(self, canvas):
        """
        Draw the circle on the given canvas.
        """
        if self._radius:
            canvas.draw_circle(self._center, self._radius, 1, "red")


def check_number(val):
    """
    Helper function to check that val is a number.
    Used for error checking purposes.
    """
    if type(val) == type(1):
        return True
    if type(val) == type(1.5):
        return True
    return False


class App:
    """
    Application class to run the circle GUI.
    """
    def __init__(self, make_circle):
        # Initialize data structures
        self._points = Points()
        self._circle = Circle()
        self._make_circle = make_circle

        # Create a frame and assign callbacks to event handlers
        self._frame = simplegui.create_frame("Circles", WIDTH, HEIGHT)
        self._frame.set_draw_handler(self.draw)
        self._frame.set_mouseclick_handler(self.click)
        self._frame.add_button("Circle", self.button)
        self._frame.set_canvas_background("white")

    def start(self):
        """
        Start the GUI.
        """
        self._frame.start()

    def stop(self):
        """
        Stop the GUI.
        """
        self._frame.stop()

    def button(self):
        """
        Handler for circle button
        """
        print(self._points)
        if self._points.num_points() < 3:
            print("Not enough points")
            return
        p0x, p0y = self._points.get_point(0)
        p1x, p1y = self._points.get_point(1)
        p2x, p2y = self._points.get_point(2)
        res = self._make_circle(p0x, p0y,
                               p1x, p1y,
                               p2x, p2y)
        # must return a tuple with 3 items
        if (type(res) != type(())) or (len(res) != 3):
            raise TypeError("make_circle must return 3 values (x, y, radius)")
        center_x, center_y, radius = res
        # check that values are numbers
        if not check_number(center_x):
            raise TypeError("x value must be a number")
        if not check_number(center_y):
            raise TypeError("y value must be a number")
        if not check_number(radius):
            raise TypeError("radius must be a number")
        self._circle.set_center(center_x, center_y)
        self._circle.set_radius(radius)

    def click(self, pos):
        """
        Handler for mouse click on the canvas.
        """
        self._points.add_point(pos)
        self._circle.reset()

    def draw(self, canvas):
        """
        Handler to draw on canvas.
        """
        self._points.draw(canvas)
        self._circle.draw(canvas)


def start(make_circle):
    """
    Module interface: start the application.
    """
    # Application class
    app = App(make_circle)
    app.start()

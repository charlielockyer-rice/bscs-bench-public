"""
Support code for the game of Spot It!
http://www.blueorangegames.com/spotit/
"""

import simplegui
import codeskulptor
import random
import math

WIDTH = 700
HEIGHT = 425
CARDRADIUS = 170
HITSIZE = 30
PLACESIZE = 40
PCARDLOC = (525, 200)
DCARDLOC = (175, 200)
PTEXTLOC = (490, 415)
DTEXTLOC = (140, 415)

# Helper functions

def format_time(num):
    """
    Given a number representing seconds, return a string formated as
    MM:SS.
    """
    secs = num % 60
    mins = num // 60
    result = ""
    if mins < 10:
        result += " "
    result += str(mins) + ":"
    if secs < 10:
        result += "0"
    result += str(secs)
    return result

def angle_to_vector(ang):
    """
    Convert an angle in radians to a unit vector in that direction.
    """
    return [math.cos(ang), math.sin(ang)]

def distance(pt1, pt2):
    """
    Return the distance between two points.
    """
    return math.sqrt((pt1[0]-pt2[0])**2+(pt1[1]-pt2[1])**2)

def overlap(newp, locs):
    """
    Compute whether the point newp overlaps with any points in
    locs.
    """
    for loc in locs:
        if distance(newp, loc) < 2 * PLACESIZE:
            return True
    return False

def centers(imgs_per_card, radius):
    """
    Generate a list of random image centers.
    """
    locs = []
    n_tries = 0
    while len(locs) < imgs_per_card:
        # If we've tried many times and failed to find appropriate
        # locations, try bumping the locations into a corner to find
        # more room.
        if n_tries in [100, 500, 1000, 2000, 3000, 4000, 5000]:
            locs = bump(locs, radius)
        mag = random.random() * (radius - PLACESIZE)
        theta = random.random() * 2 * math.pi
        loc = angle_to_vector(theta)
        loc[0] *= mag
        loc[1] *= mag
        if not overlap(loc, locs):
            locs.append(loc)
        n_tries += 1
    return locs

def dot(vector1, vector2):
    """
    Returns the dot product of two (x, y) vectors.
    """
    return vector1[0] * vector2[0] + vector1[1] * vector2[1]

def bump(locs, radius):
    """
    Nudges each location in a list closer to the edge they are closest
    to.
    """
    new_locs = []
    # Find theta
    x_from_center = sum([loc[0] for loc in locs])
    y_from_center = sum([loc[1] for loc in locs])
    bump_vector = angle_to_vector(math.atan2(y_from_center, x_from_center))
    # Move each point (in order from the farthest out)
    visited = []
    for loc in sorted(locs, key=lambda loc: -1 * dot(loc, bump_vector)):
        # Check how far each location can move without hitting another image
        possible_dist = float('inf')
        for other_loc in visited:
            dist = float('inf')
            intersection = find_moving_circle_intersection(other_loc,
                loc, PLACESIZE, bump_vector)
            if intersection:
                dist = distance(loc, intersection)
            if dist < possible_dist:
                possible_dist = dist
        # Don't go outside the card, either
        max_dist_to_edge = solve_quadratic(1, 2 * dot(loc, bump_vector),
            loc[0]**2 + loc[1]**2 - (radius - PLACESIZE)**2)
        # Move the maximum possible distance; that is, farthest possible out of
        # (1) cardradius/10, (2) distance to any other card,
        # (3) distance to the edge of the card.
        shift_by = min([radius / 10, possible_dist, max_dist_to_edge])
        new_locs.append([loc[0] + bump_vector[0] * shift_by,
                         loc[1] + bump_vector[1] * shift_by])
        visited.append(loc)
    return new_locs

def solve_quadratic(coeff_a, coeff_b, coeff_c):
    """
    Returns the maximum solution x to a quadratic equation in the form
    ax^2 + bx + c = 0.
    """
    sqrt_inside = coeff_b**2 - (4.0 * coeff_a * coeff_c)
    if sqrt_inside < 0:
        raise ValueError("Quadratic never equals zero: %sx + %sy + %s" %
            (str(coeff_a), str(coeff_b), str(coeff_c)))
    plus = (-1.0 * coeff_b + math.sqrt(sqrt_inside)) / (2 * coeff_a)
    minus = (-1.0 * coeff_b - math.sqrt(sqrt_inside)) / (2 * coeff_a)
    return max(plus, minus)

def dist_pt_to_line(point, line_a, line_b, line_c):
    """
    Calculates the shortest perpendicular distance from the provided
    point to the line defined by ax + by + c = 0.
    """
    numerator = math.fabs(line_a * point[0] + line_b * point[1] + line_c)
    denominator = math.sqrt(line_a ** 2 + line_b ** 2)
    return numerator / denominator

def find_moving_circle_intersection(center, new_ctr, radius, bump_vector):
    """
    Returns either the point at which a circle moving along the line
    described by its center and the given slope would intersect the
    original circle, or None if it would never do so.
    """
    # Find the closest distance the circles could possibly reach
    if bump_vector[0] == 0:
        # Vertical -- get close
        slope = 99999999999999.0
    else:
        slope = float(bump_vector[1]) / bump_vector[0]
    closest_dist = dist_pt_to_line(center, slope, -1,
        new_ctr[1] - slope * new_ctr[0])
    if closest_dist > radius * 2:
        return None
    else:
        # There must be some point at which the cicles are 2*radius
        # distance apart
        dist_backward = math.sqrt((2 * radius) ** 2 - closest_dist ** 2)
        perpendicular_angle = math.atan2(-1 * bump_vector[0], bump_vector[1])
        center_at_intersection = (
            center[0] - math.sin(perpendicular_angle) * closest_dist,
            center[1] - math.cos(perpendicular_angle) * closest_dist
        )
        backed_up = (
            center_at_intersection[0] - bump_vector[0] * dist_backward,
            center_at_intersection[1] - bump_vector[1] * dist_backward
        )
        return backed_up


class TiledImage:
    """
    Hold a tiled image with the given parameters.
    """
    def __init__(self, name, rowlength, width, height, padding):
        self._name = name
        self._rowlen = rowlength
        self._width = width
        self._height = height
        self._padding = padding
        url = codeskulptor.file2url(self._name)
        self._image = simplegui.load_image(url)

    def get_subimage_coords(self, num):
        """
        Return center and size (as a tuple of tuples) of the given
        subimage.
        """
        row_offset = num % 10
        col_offset = num // 10
        center_x = self._padding + self._width / 2
        center_x += (self._width + 2 * self._padding) * row_offset
        center_y = self._padding + self._height / 2
        center_y += (self._height + 2 * self._padding) * col_offset
        return (center_x, center_y), (self._width, self._height)

    def get_image(self):
        """
        Return actual image
        """
        return self._image


class Game:
    """
    Machinery to run the game.
    """
    def __init__(self, deck, images):
        self._message = "Click the matching image"
        self._deck = list(deck)
        self._images = images
        self._timer = simplegui.create_timer(1000, self.tick)
        self.reset()

    def setup(self, frame):
        """
        Setup the game with the given frame.
        """
        self._frame = frame
        msgwidth = self._frame.get_canvas_textwidth(self._message, 30)
        self._msgloc = ((WIDTH / 2.0) - (msgwidth / 2.0), (HEIGHT / 2.0))
        txtwidth = self._frame.get_canvas_textwidth("00:00", 20, "monospace")
        self._txtloc = WIDTH - txtwidth - 20

    def reset(self):
        """
        Reset the game so that you can play again.
        """
        self._elapsed = 0
        self._timestr = format_time(self._elapsed)
        self._inprogress = False
        self._curdeck = list(self._deck)
        random.shuffle(self._curdeck)

    def tick(self):
        """
        Timer tick.
        """
        self._elapsed += 1
        self._timestr = format_time(self._elapsed)

    def new_game(self):
        """
        Start a new game.
        """
        self.reset()
        self._timer.stop()
        self._timer.start()
        self._player = self._curdeck.pop()
        self._pcenters = centers(len(self._deck[0]), CARDRADIUS)
        self._dcenters = centers(len(self._deck[0]), CARDRADIUS)
        self._inprogress = True

    def click(self, pos):
        """
        Handle click anywhere.
        """
        def click_idx(idx):
            """
            Handle click on an image.
            """
            val = self._player[idx]
            if val in self._curdeck[-1]:
                self._player = self._curdeck.pop()
                self._pcenters = self._dcenters
                self._dcenters = centers(len(self._deck[0]), CARDRADIUS)
                if not self._curdeck:
                    self._inprogress = False
                    self._timer.stop()
                    self._message = "Your time was: " + self._timestr
                    self.setup(self._frame)
        if self._inprogress:
            adjpos = list(pos)
            adjpos[0] -= PCARDLOC[0]
            adjpos[1] -= PCARDLOC[1]
            for i in range(len(self._pcenters)):
                if distance(adjpos, self._pcenters[i]) < HITSIZE:
                    # assume images do not overlap
                    click_idx(i)
                    break

    def draw(self, canvas):
        """
        Draw game on canvas.
        """
        def draw_card(card, loc, imgcenters):
            """
            Draw a card on the canvas.
            """
            nimgs = len(card)
            for i in range(nimgs):
                img = card[i]
                locx = imgcenters[i][0] + loc[0]
                locy = imgcenters[i][1] + loc[1]
                pos, size = self._images.get_subimage_coords(img)
                image = self._images.get_image()
                canvas.draw_image(image, pos, size, (locx, locy), size)
        canvas.draw_text(self._timestr, (self._txtloc, 20),
                         20, "Red", "monospace")
        if self._inprogress:
            # draw card outlines
            canvas.draw_circle(DCARDLOC, CARDRADIUS, 2, "Red")
            canvas.draw_circle(PCARDLOC, CARDRADIUS, 2, "Red")
            # draw deck card
            draw_card(self._curdeck[-1], DCARDLOC, self._dcenters)
            canvas.draw_text("Deck", DTEXTLOC, 30, "Red")
            # draw player card
            draw_card(self._player, PCARDLOC, self._pcenters)
            canvas.draw_text("Player", PTEXTLOC, 30, "Red")
        else:
            canvas.draw_text(self._message, self._msgloc, 30, "Blue")


def check_cards(card1, card2):
    """
    Check that each card has one and only one match with every other card.
    """
    matches = 0
    for item1 in card1:
        for item2 in card2:
            if item1 == item2:
                matches += 1
    return matches == 1


def check_deck(cards):
    """
    Check that the deck is valid.
    """
    for i, card1 in enumerate(cards):
        for card2 in cards[i+1:]:
            if not check_cards(card1, card2):
                return False
    return True


def start(deck):
    """
    Module interface: start the application.
    """
    # Check input
    if type(deck) != type([]):
        raise ValueError("Deck must be a list")
    if len(deck) < 2:
        raise ValueError("Deck must have at least 2 cards")
    imgs = -1
    for card in deck:
        if type(card) != type([]):
            raise ValueError("Each card must be a list")
        if imgs == -1:
            imgs = len(card)
        if len(card) != imgs:
            raise ValueError("All cards must have the same number of images")
        for img in card:
            if type(img) != type(1):
                raise ValueError("All images must be integers")
        if len(set(card)) != len(card):
            raise ValueError("A card may not have duplicate images")
    if not check_deck(deck):
        msg = "Invalid deck: all pairs of cards must have exactly 1 match"
        raise ValueError(msg)
    print("Valid deck with", len(deck), "cards.")
    # Setup game
    images = TiledImage("comp140_module2_images.png", 10, 60, 60, 1)
    game = Game(deck, images)
    # Create a frame and assign callbacks to event handlers
    frame = simplegui.create_frame("Spot It!", WIDTH, HEIGHT)
    frame.add_button("Start", game.new_game)
    frame.set_draw_handler(game.draw)
    frame.set_mouseclick_handler(game.click)
    frame.set_canvas_background('white')
    game.setup(frame)
    # Start the frame animation
    frame.start()

package comp310.tests.hw02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import hw02.ball.ABall;
import hw02.ball.BounceUpBall;
import hw02.ball.DVDLogoBall;
import hw02.ball.SineSpeedBall;
import hw02.ball.ErrorBall;
import provided.utils.displayModel.IDimension;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;

/**
 * JUnit 5 tests for the HW02 ball hierarchy.
 * Tests ABall movement, bouncing, and concrete ball types
 * without requiring a GUI.
 */
class BallTestPrivate {

    /** Simple IDimension mock. */
    private IDimension mockDim(int w, int h) {
        return new IDimension() {
            public int getWidth() { return w; }
            public int getHeight() { return h; }
        };
    }

    /** Create a Graphics object backed by an off-screen BufferedImage. */
    private Graphics getGraphics() {
        return new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).createGraphics();
    }

    /**
     * A minimal concrete ABall subclass that exposes protected fields via
     * public getters, so tests in this package can inspect ball state.
     * updateState is a no-op (like StraightStrategy behavior).
     */
    static class TestBall extends ABall {
        public TestBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
            super(color, position, velocity, diameter, dimension);
        }

        @Override
        public void updateState(IDispatcher<Graphics> disp) {
            // no-op
        }

        // Expose protected fields for testing
        public Point getPosition() { return this.position; }
        public Point getVelocity() { return this.velocity; }
        public int getDiameter() { return this.diameter; }
        public Color getColor() { return this.color; }
    }

    /**
     * Expose protected fields from BounceUpBall for testing.
     */
    static class TestBounceUpBall extends BounceUpBall {
        public TestBounceUpBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
            super(color, position, velocity, diameter, dimension);
        }

        public Point getPosition() { return this.position; }
        public Point getVelocity() { return this.velocity; }
        public int getDiameter() { return this.diameter; }
        public Color getColor() { return this.color; }
    }

    /**
     * Expose protected fields from DVDLogoBall for testing.
     */
    static class TestDVDLogoBall extends DVDLogoBall {
        public TestDVDLogoBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
            super(color, position, velocity, diameter, dimension);
        }

        public Point getPosition() { return this.position; }
        public Point getVelocity() { return this.velocity; }
        public int getDiameter() { return this.diameter; }
        public Color getColor() { return this.color; }
    }

    // ---------------------------------------------------------------
    // ABall.move() tests
    // ---------------------------------------------------------------

    @Test
    void testABallBounceLeft() {
        // Ball past left boundary: position.x < 0
        TestBall ball = new TestBall(Color.RED, new Point(-5, 200), new Point(-3, 2), 20, mockDim(800, 600));
        boolean bounced = ball.bounce();
        assertTrue(bounced, "Ball past left boundary should bounce");
        assertEquals(3, ball.getVelocity().x, "Velocity x should flip on left bounce");
    }

    @Test
    void testABallBounceBottom() {
        // Ball past bottom boundary: position.y > dimension.height - diameter
        // dimension height=600, diameter=20, so boundary at y=580
        TestBall ball = new TestBall(Color.RED, new Point(200, 590), new Point(3, 5), 20, mockDim(800, 600));
        boolean bounced = ball.bounce();
        assertTrue(bounced, "Ball past bottom boundary should bounce");
        assertEquals(-5, ball.getVelocity().y, "Velocity y should flip on bottom bounce");
    }

    @Test
    void testABallBounceTop() {
        // Ball past top boundary: position.y < 0
        TestBall ball = new TestBall(Color.RED, new Point(200, -3), new Point(3, -5), 20, mockDim(800, 600));
        boolean bounced = ball.bounce();
        assertTrue(bounced, "Ball past top boundary should bounce");
        assertEquals(5, ball.getVelocity().y, "Velocity y should flip on top bounce");
    }

    // ---------------------------------------------------------------
    // ABall.paint() test
    // ---------------------------------------------------------------

    @Test
    void testABallPaintDoesNotThrow() {
        TestBall ball = new TestBall(Color.RED, new Point(100, 100), new Point(5, 3), 20, mockDim(800, 600));
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> ball.paint(g), "ABall.paint should not throw");
    }

    // ---------------------------------------------------------------
    // ABall.update() test (calls updateState + move + bounce + paint)
    // ---------------------------------------------------------------

    @Test
    void testABallUpdate() {
        IDispatcher<Graphics> disp = new SequentialDispatcher<>();
        TestBall ball = new TestBall(Color.RED, new Point(100, 100), new Point(5, 3), 20, mockDim(800, 600));
        Graphics g = getGraphics();
        ball.update(disp, g);
        // After update: move should have been called
        assertEquals(105, ball.getPosition().x, "After update, x should reflect move");
        assertEquals(103, ball.getPosition().y, "After update, y should reflect move");
    }

    // ---------------------------------------------------------------
    // BounceUpBall tests
    // ---------------------------------------------------------------

    @Test
    void testBounceUpBallUpdateStateLargeX() {
        // When x >= 50: diameter = x / 5
        IDispatcher<Graphics> disp = new SequentialDispatcher<>();
        TestBounceUpBall ball = new TestBounceUpBall(Color.RED, new Point(200, 100), new Point(0, 0), 20, mockDim(800, 600));
        ball.updateState(disp);
        assertEquals(200 / 5, ball.getDiameter(), "BounceUpBall diameter should be position.x / 5 when x >= 50");
    }

    @Test
    void testBounceUpBallUpdateStateSmallX() {
        // When x < 50: diameter = 5
        IDispatcher<Graphics> disp = new SequentialDispatcher<>();
        TestBounceUpBall ball = new TestBounceUpBall(Color.RED, new Point(30, 100), new Point(0, 0), 20, mockDim(800, 600));
        ball.updateState(disp);
        assertEquals(5, ball.getDiameter(), "BounceUpBall diameter should be 5 when x < 50");
    }

    @Test
    void testBounceUpBallUpdateStateBoundaryX() {
        // When x == 50: diameter = 50 / 5 = 10
        IDispatcher<Graphics> disp = new SequentialDispatcher<>();
        TestBounceUpBall ball = new TestBounceUpBall(Color.RED, new Point(50, 100), new Point(0, 0), 20, mockDim(800, 600));
        ball.updateState(disp);
        assertEquals(10, ball.getDiameter(), "BounceUpBall diameter should be 10 when x == 50");
    }

    // ---------------------------------------------------------------
    // DVDLogoBall tests
    // ---------------------------------------------------------------

    @Test
    void testDVDLogoBallColorChangeOnBounce() {
        // Place ball past right boundary so it bounces
        TestDVDLogoBall ball = new TestDVDLogoBall(Color.RED, new Point(790, 200), new Point(5, 3), 20, mockDim(800, 600));
        Color originalColor = ball.getColor();
        // Run bounce many times to ensure color changes (Randomizer-based, probabilistic)
        boolean colorChanged = false;
        for (int i = 0; i < 50; i++) {
            // Reset position to trigger bounce each time
            ball.getPosition().setLocation(790, 200);
            ball.getVelocity().setLocation(5, 3);
            ball.bounce();
            if (!ball.getColor().equals(originalColor)) {
                colorChanged = true;
                break;
            }
        }
        assertTrue(colorChanged, "DVDLogoBall should change color when bouncing");
    }

    @Test
    void testDVDLogoBallNoColorChangeWithoutBounce() {
        // Ball in center - no bounce should occur, color should stay the same
        TestDVDLogoBall ball = new TestDVDLogoBall(Color.RED, new Point(200, 200), new Point(3, 3), 20, mockDim(800, 600));
        Color originalColor = ball.getColor();
        ball.bounce();
        assertEquals(originalColor, ball.getColor(), "DVDLogoBall should not change color without bouncing");
    }

    // ---------------------------------------------------------------
    // SineSpeedBall tests
    // ---------------------------------------------------------------

    @Test
    void testSineSpeedBallConstruction() {
        assertDoesNotThrow(() ->
            new SineSpeedBall(Color.BLUE, new Point(100, 100), new Point(5, 5), 20, mockDim(800, 600)),
            "SineSpeedBall construction should not throw");
    }

    // ---------------------------------------------------------------
    // ErrorBall tests
    // ---------------------------------------------------------------

    @Test
    void testErrorBallConstruction() {
        IDimension dim = mockDim(800, 600);
        ErrorBall eb = new ErrorBall(dim);
        assertNotNull(eb, "ErrorBall should be constructable");
    }

    @Test
    void testErrorBallUpdateDoesNotThrow() {
        IDimension dim = mockDim(800, 600);
        ErrorBall eb = new ErrorBall(dim);
        IDispatcher<Graphics> disp = new SequentialDispatcher<>();
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> eb.update(disp, g), "ErrorBall.update should not throw");
    }
}

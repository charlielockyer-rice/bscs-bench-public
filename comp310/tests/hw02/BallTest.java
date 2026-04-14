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
class BallTest {

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
    void testABallMove() {
        TestBall ball = new TestBall(Color.RED, new Point(100, 100), new Point(5, 3), 20, mockDim(800, 600));
        ball.move();
        assertEquals(105, ball.getPosition().x, "After move, x should increase by velocity.x");
        assertEquals(103, ball.getPosition().y, "After move, y should increase by velocity.y");
    }

    @Test
    void testABallMoveNegativeVelocity() {
        TestBall ball = new TestBall(Color.RED, new Point(100, 100), new Point(-3, -7), 20, mockDim(800, 600));
        ball.move();
        assertEquals(97, ball.getPosition().x);
        assertEquals(93, ball.getPosition().y);
    }

    @Test
    void testMultipleMoveSteps() {
        TestBall ball = new TestBall(Color.RED, new Point(0, 0), new Point(10, 5), 20, mockDim(800, 600));
        for (int i = 0; i < 5; i++) {
            ball.move();
        }
        assertEquals(50, ball.getPosition().x, "After 5 moves, x should be 50");
        assertEquals(25, ball.getPosition().y, "After 5 moves, y should be 25");
    }

    // ---------------------------------------------------------------
    // ABall.bounce() tests
    // ---------------------------------------------------------------

    @Test
    void testABallNoBounce() {
        TestBall ball = new TestBall(Color.RED, new Point(200, 200), new Point(5, 3), 20, mockDim(800, 600));
        boolean bounced = ball.bounce();
        assertFalse(bounced, "Ball in center should not bounce");
        assertEquals(5, ball.getVelocity().x, "Velocity x should be unchanged");
        assertEquals(3, ball.getVelocity().y, "Velocity y should be unchanged");
    }

    @Test
    void testABallBounceRight() {
        // Ball past right boundary: position.x > dimension.width - diameter
        // dimension width=800, diameter=20, so boundary at x=780
        TestBall ball = new TestBall(Color.RED, new Point(790, 200), new Point(5, 3), 20, mockDim(800, 600));
        boolean bounced = ball.bounce();
        assertTrue(bounced, "Ball past right boundary should bounce");
        assertEquals(-5, ball.getVelocity().x, "Velocity x should flip on right bounce");
    }

}

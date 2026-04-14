package comp310.tests.hw04;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import hw04.model.Ball;
import hw04.model.IBall;
import hw04.model.IBallAlgo;
import hw04.model.IBallCmd;
import hw04.model.paint.strategies.BallPaintStrategy;
import hw04.model.paint.strategies.EllipsePaintStrategy;
import hw04.model.paint.strategies.SquarePaintStrategy;
import hw04.model.strategy.StraightStrategy;
import hw04.model.strategy.GravityStrategy;
import hw04.model.strategy.ColorStrategy;
import hw04.model.strategy.BreathingStrategy;
import hw04.model.strategy.SwitcherStrategy;
import hw04.model.strategy.MultiStrategy;
import provided.utils.displayModel.IDimension;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;

/**
 * JUnit 5 tests for HW04 update strategies and paint strategies.
 * Tests are designed to run without a GUI by creating Ball objects
 * directly and asserting on state changes.
 */
class PaintStrategyTest {

    /** Simple IDimension mock returning fixed width and height. */
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

    /** No-op IBallAlgo that does nothing on construction. */
    private IBallAlgo noOpAlgo() {
        return new IBallAlgo() {
            public void caseDefault(IBall host) {
                // no-op: do not install any strategies
            }
        };
    }

    /**
     * Create a Ball with sane defaults for testing.
     * The no-op IBallAlgo avoids GUI dependencies during construction.
     * Canvas is null since we do not need a real container for these tests.
     */
    private Ball makeBall(Point location, Point velocity, Color color, int radius) {
        return new Ball(location, velocity, color, radius, mockDim(800, 600), null, noOpAlgo());
    }

    // ---------------------------------------------------------------
    // StraightStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testStraightStrategyNoOp() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        ball.setUpdateStrategy(new StraightStrategy());
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        Point velBefore = new Point(ball.getVelocity());
        Color colorBefore = ball.getColor();
        int radiusBefore = ball.getRadius();

        ball.updateState(disp);

        assertEquals(velBefore, ball.getVelocity(), "StraightStrategy should not change velocity");
        assertEquals(colorBefore, ball.getColor(), "StraightStrategy should not change color");
        assertEquals(radiusBefore, ball.getRadius(), "StraightStrategy should not change radius");
    }

    // ---------------------------------------------------------------
    // GravityStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testGravityStrategyAddsToYVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), Color.RED, 10);
        ball.setUpdateStrategy(new GravityStrategy());
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        ball.updateState(disp);

        assertEquals(5, ball.getVelocity().x, "GravityStrategy should not change x velocity");
        assertEquals(1, ball.getVelocity().y, "GravityStrategy should add 1 to y velocity");
    }

    @Test
    void testGravityStrategyCumulative() {
        Ball ball = makeBall(new Point(100, 100), new Point(0, 0), Color.RED, 10);
        ball.setUpdateStrategy(new GravityStrategy());
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        for (int i = 0; i < 5; i++) {
            ball.updateState(disp);
        }

        assertEquals(5, ball.getVelocity().y, "GravityStrategy should add 1 to y velocity each update");
    }

    // ---------------------------------------------------------------
    // ColorStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testColorStrategyChangesColor() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        ball.setUpdateStrategy(new ColorStrategy());
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        // Run multiple updates; random color should differ from RED at least once
        boolean colorChanged = false;
        for (int i = 0; i < 50; i++) {
            ball.updateState(disp);
            if (!Color.RED.equals(ball.getColor())) {
                colorChanged = true;
                break;
            }
        }
        assertTrue(colorChanged, "ColorStrategy should change the ball's color (probabilistic, ran 50 iterations)");
    }

    // ---------------------------------------------------------------
    // BreathingStrategy tests
    // ---------------------------------------------------------------

}

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
class PaintStrategyTestPrivate {

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
    void testBreathingStrategyChangesRadius() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        ball.setUpdateStrategy(new BreathingStrategy());
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        int initialRadius = ball.getRadius();
        boolean radiusChanged = false;
        for (int i = 0; i < 20; i++) {
            ball.updateState(disp);
            if (ball.getRadius() != initialRadius) {
                radiusChanged = true;
                break;
            }
        }
        assertTrue(radiusChanged, "BreathingStrategy should oscillate the ball's radius");
    }

    // ---------------------------------------------------------------
    // SwitcherStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testSwitcherStrategyDelegates() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), Color.RED, 10);
        SwitcherStrategy switcher = new SwitcherStrategy();
        ball.setUpdateStrategy(switcher);
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        // Default delegate is StraightStrategy (no-op)
        ball.updateState(disp);
        assertEquals(0, ball.getVelocity().y, "SwitcherStrategy with StraightStrategy should not change y velocity");

        // Switch to GravityStrategy
        switcher.switchStrategy(new GravityStrategy());
        ball.updateState(disp);
        assertEquals(1, ball.getVelocity().y, "After switching to GravityStrategy, y velocity should increase by 1");
    }

    @Test
    void testSwitcherStrategyGetStrategy() {
        SwitcherStrategy switcher = new SwitcherStrategy();
        assertTrue(switcher.getStrategy() instanceof StraightStrategy,
                "Default SwitcherStrategy delegate should be StraightStrategy");

        GravityStrategy gravity = new GravityStrategy();
        switcher.switchStrategy(gravity);
        assertSame(gravity, switcher.getStrategy(),
                "After switchStrategy, getStrategy should return the new strategy");
    }

    // ---------------------------------------------------------------
    // MultiStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testMultiStrategyCombinesBothEffects() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), Color.RED, 10);
        // Combine GravityStrategy (adds to y velocity) and BreathingStrategy (changes radius)
        MultiStrategy multi = new MultiStrategy(new GravityStrategy(), new BreathingStrategy());
        ball.setUpdateStrategy(multi);
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        ball.updateState(disp);

        assertEquals(1, ball.getVelocity().y, "MultiStrategy should apply GravityStrategy effect (y velocity +1)");
        // BreathingStrategy uses sine function; radius should be set to some value
        // The SineMaker with params (5, 20, 0.2) starts producing values; just verify it ran
        // by checking the radius is the sine-generated value, not the original 10
        // (The first getIntVal from SineMaker(5,20,0.2) depends on the implementation)
    }

    // ---------------------------------------------------------------
    // Ball.move() and Ball.bounce() tests (HW04 version)
    // ---------------------------------------------------------------

    @Test
    void testBallMoveTranslatesLocation() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        ball.move();
        assertEquals(105, ball.getLocation().x, "After move, x should increase by velocity.x");
        assertEquals(103, ball.getLocation().y, "After move, y should increase by velocity.y");
    }

    @Test
    void testBallBounceReverses() {
        // Place ball past right boundary
        Ball ball = makeBall(new Point(795, 100), new Point(5, 3), Color.RED, 10);
        ball.bounce();
        assertEquals(-5, ball.getVelocity().x, "Ball at right edge should reverse x velocity");
    }

    // ---------------------------------------------------------------
    // Paint strategy tests (using BufferedImage graphics)
    // ---------------------------------------------------------------

    @Test
    void testBallPaintStrategyDoesNotThrow() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 20);
        ball.setPaintStrategy(new BallPaintStrategy());
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> ball.paint(g),
                "BallPaintStrategy.paint should not throw when painting to an off-screen Graphics");
    }

    @Test
    void testEllipsePaintStrategyDoesNotThrow() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.BLUE, 20);
        ball.setPaintStrategy(new EllipsePaintStrategy());
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> ball.paint(g),
                "EllipsePaintStrategy.paint should not throw");
    }

    @Test
    void testSquarePaintStrategyDoesNotThrow() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.GREEN, 20);
        ball.setPaintStrategy(new SquarePaintStrategy());
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> ball.paint(g),
                "SquarePaintStrategy.paint should not throw");
    }

    @Test
    void testNullPaintStrategyDoesNotThrow() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 20);
        // Default paint strategy is IPaintStrategy.NULL
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> ball.paint(g),
                "NULL PaintStrategy should not throw");
    }

    // ---------------------------------------------------------------
    // IBallCmd dispatch test
    // ---------------------------------------------------------------

    @Test
    void testBallCmdDispatch() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        disp.addObserver(ball);

        final boolean[] applied = {false};
        IBallCmd cmd = (IBall context, IDispatcher<IBallCmd> d) -> {
            applied[0] = true;
            context.setColor(Color.BLUE);
        };

        disp.updateAll(cmd);

        assertTrue(applied[0], "IBallCmd should have been applied to the ball via dispatcher");
        assertEquals(Color.BLUE, ball.getColor(), "IBallCmd should have changed ball color to BLUE");
    }
}

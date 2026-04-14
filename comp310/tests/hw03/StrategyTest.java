package comp310.tests.hw03;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import hw03.model.strategy.StraightStrategy;
import hw03.model.strategy.GravityStrategy;
import hw03.model.strategy.ColorStrategy;
import hw03.model.strategy.ReverseStrategy;
import hw03.model.strategy.ExpandingStrategy;
import hw03.model.strategy.TeleportingStrategy;
import hw03.model.strategy.RandomWalkStrategy;
import hw03.model.strategy.SwitcherStrategy;
import hw03.model.strategy.ExplodingStrategy;
import provided.utils.displayModel.IDimension;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;

/**
 * JUnit 5 tests for the HW03 strategy hierarchy.
 * Tests each IUpdateStrategy implementation and the SwitcherStrategy decorator
 * without requiring a GUI.
 */
class StrategyTest {

    /** Fixed-size mock dimension (800x600). */
    private final IDimension mockDim = new IDimension() {
        public int getWidth() { return 800; }
        public int getHeight() { return 600; }
    };

    /** A dispatcher needed by strategy.updateState(). */
    private final IDispatcher<Graphics> disp = new SequentialDispatcher<>();

    /**
     * Helper to create a Ball with the given state and a StraightStrategy.
     * StraightStrategy is a no-op, so it won't interfere with tests that
     * apply a different strategy manually.
     */
    private Ball makeBall(Point loc, Point vel, int radius) {
        return new Ball(loc, vel, Color.RED, radius, mockDim, new StraightStrategy());
    }

    // ---------------------------------------------------------------
    // StraightStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testStraightStrategyNoChangeToVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        Point velBefore = new Point(ball.getVelocity());
        new StraightStrategy().updateState(disp, ball);
        assertEquals(velBefore, ball.getVelocity(), "StraightStrategy should not change velocity");
    }

    @Test
    void testStraightStrategyNoChangeToColor() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        Color colorBefore = ball.getColor();
        new StraightStrategy().updateState(disp, ball);
        assertEquals(colorBefore, ball.getColor(), "StraightStrategy should not change color");
    }

    @Test
    void testStraightStrategyNoChangeToRadius() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        int radiusBefore = ball.getRadius();
        new StraightStrategy().updateState(disp, ball);
        assertEquals(radiusBefore, ball.getRadius(), "StraightStrategy should not change radius");
    }

    // ---------------------------------------------------------------
    // GravityStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testGravityAddsToYVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), 10);
        new GravityStrategy().updateState(disp, ball);
        assertEquals(1, ball.getVelocity().y, "Gravity should add 1 to y velocity");
    }

    @Test
    void testGravityPreservesXVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(7, 0), 10);
        new GravityStrategy().updateState(disp, ball);
        assertEquals(7, ball.getVelocity().x, "Gravity should not change x velocity");
    }

    @Test
    void testGravityMultipleTicks() {
        Ball ball = makeBall(new Point(100, 100), new Point(0, 0), 10);
        GravityStrategy g = new GravityStrategy();
        for (int i = 0; i < 10; i++) {
            g.updateState(disp, ball);
        }
        assertEquals(10, ball.getVelocity().y, "After 10 gravity ticks, y velocity should be 10");
    }

    @Test
    void testGravityAccumulates() {
        Ball ball = makeBall(new Point(100, 100), new Point(0, 5), 10);
        GravityStrategy g = new GravityStrategy();
        g.updateState(disp, ball);
        assertEquals(6, ball.getVelocity().y, "Gravity should accumulate on existing y velocity");
    }

    // ---------------------------------------------------------------
    // ColorStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testColorStrategyChangesColor() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        Color original = ball.getColor();
        ColorStrategy cs = new ColorStrategy();
        // ColorStrategy uses random hue, so run many times to ensure change
        boolean changed = false;
        for (int i = 0; i < 100; i++) {
            cs.updateState(disp, ball);
            if (!ball.getColor().equals(original)) {
                changed = true;
                break;
            }
        }
        assertTrue(changed, "ColorStrategy should change ball color");
    }

}

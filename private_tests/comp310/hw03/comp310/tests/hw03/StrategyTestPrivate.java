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
class StrategyTestPrivate {

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
    void testColorStrategyDoesNotChangeRadius() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        int radiusBefore = ball.getRadius();
        new ColorStrategy().updateState(disp, ball);
        assertEquals(radiusBefore, ball.getRadius(), "ColorStrategy should not change radius");
    }

    // ---------------------------------------------------------------
    // ReverseStrategy tests
    // ---------------------------------------------------------------
    // Implementation: ticks starts at 0. Each call checks (ticks > 9).
    // If true: reset ticks=0, reverse velocity, then ticks++.
    // So reversal happens on the 11th call (when ticks == 10).

    @Test
    void testReverseStrategyNoChangeAtTick10() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), 10);
        ReverseStrategy r = new ReverseStrategy();
        for (int i = 0; i < 10; i++) {
            r.updateState(disp, ball);
        }
        // After 10 calls, ticks went 0->1->...->10, but reversal check is > 9
        // Call 1: ticks=0, 0>9 no, ticks=1
        // ...
        // Call 10: ticks=9, 9>9 no, ticks=10
        // So velocity should still be original at this point
        assertEquals(5, ball.getVelocity().x, "Velocity should not reverse before the 11th tick");
        assertEquals(3, ball.getVelocity().y);
    }

    @Test
    void testReverseStrategyReversesAtTick11() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), 10);
        ReverseStrategy r = new ReverseStrategy();
        for (int i = 0; i < 11; i++) {
            r.updateState(disp, ball);
        }
        // Call 11: ticks=10, 10>9 yes, reverse. Velocity becomes (-5, -3).
        assertEquals(-5, ball.getVelocity().x, "Velocity x should reverse on 11th tick");
        assertEquals(-3, ball.getVelocity().y, "Velocity y should reverse on 11th tick");
    }

    @Test
    void testReverseStrategyCyclesBack() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), 10);
        ReverseStrategy r = new ReverseStrategy();
        // 11 ticks -> reversed to (-5,-3)
        // 11 more ticks -> reversed back to (5,3)
        for (int i = 0; i < 22; i++) {
            r.updateState(disp, ball);
        }
        assertEquals(5, ball.getVelocity().x, "After two reversal cycles, velocity x should be original");
        assertEquals(3, ball.getVelocity().y, "After two reversal cycles, velocity y should be original");
    }

    // ---------------------------------------------------------------
    // ExpandingStrategy tests
    // ---------------------------------------------------------------
    // Implementation: if (radius < 60 && expand) -> radius += 2
    //                 else if (radius < 5) -> expand = true
    //                 else -> radius -= 2, expand = false

    @Test
    void testExpandingStrategyExpands() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        ExpandingStrategy e = new ExpandingStrategy();
        e.updateState(disp, ball);
        assertEquals(12, ball.getRadius(), "Expanding from radius 10 should yield 12");
    }

    @Test
    void testExpandingStrategyExpandsMultiple() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        ExpandingStrategy e = new ExpandingStrategy();
        // Expand from 10: 12, 14, 16, 18, 20
        for (int i = 0; i < 5; i++) {
            e.updateState(disp, ball);
        }
        assertEquals(20, ball.getRadius(), "After 5 expansions from radius 10, radius should be 20");
    }

    @Test
    void testExpandingStrategyContractsAtMax() {
        // Start at radius 60 (the boundary): 60 < 60 is false, enters else branch
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 60);
        ExpandingStrategy e = new ExpandingStrategy();
        e.updateState(disp, ball);
        assertEquals(58, ball.getRadius(), "At radius 60, should contract to 58");
    }

    @Test
    void testExpandingStrategyContractsThenExpands() {
        // Start at radius 58 with a fresh ExpandingStrategy (expand=true)
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 58);
        ExpandingStrategy e = new ExpandingStrategy();
        // Tick 1: 58 < 60 && expand(true) -> radius=60
        e.updateState(disp, ball);
        assertEquals(60, ball.getRadius());
        // Tick 2: 60 < 60 false, 60 < 5 false, else: radius=58, expand=false
        e.updateState(disp, ball);
        assertEquals(58, ball.getRadius());
        // Tick 3: 58 < 60 && expand(false) -> false. 58 < 5 false. else: radius=56
        e.updateState(disp, ball);
        assertEquals(56, ball.getRadius());
    }

    @Test
    void testExpandingStrategyDoesNotChangeVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        Point velBefore = new Point(ball.getVelocity());
        new ExpandingStrategy().updateState(disp, ball);
        assertEquals(velBefore, ball.getVelocity(), "ExpandingStrategy should not change velocity");
    }

    // ---------------------------------------------------------------
    // TeleportingStrategy tests
    // ---------------------------------------------------------------
    // Implementation: ticks starts at 0. Each call checks (ticks > 19).
    // If true: teleport to random location, reset ticks=0, then ticks++.
    // So teleport happens on the 21st call.

    @Test
    void testTeleportingNoChangeBefore21() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        TeleportingStrategy t = new TeleportingStrategy();
        for (int i = 0; i < 20; i++) {
            t.updateState(disp, ball);
        }
        // After 20 calls: ticks went 0->1->...->20, but teleport check is > 19
        // Call 20: ticks=19, 19>19 no, ticks=20
        assertEquals(100, ball.getLocation().x, "Location should not change before teleport tick");
        assertEquals(100, ball.getLocation().y);
    }

    @Test
    void testTeleportingChangesAt21() {
        // After 21 calls, teleport should have occurred (probabilistic but very reliable)
        Ball ball = makeBall(new Point(400, 300), new Point(5, 5), 10);
        TeleportingStrategy t = new TeleportingStrategy();
        for (int i = 0; i < 21; i++) {
            t.updateState(disp, ball);
        }
        // Random teleport to somewhere in [0, 800) x [0, 600)
        // Probability of landing on exact (400, 300) is vanishingly small
        boolean moved = ball.getLocation().x != 400 || ball.getLocation().y != 300;
        assertTrue(moved, "Ball should teleport after 21 ticks");
    }

    @Test
    void testTeleportingStaysInBounds() {
        Ball ball = makeBall(new Point(400, 300), new Point(5, 5), 10);
        TeleportingStrategy t = new TeleportingStrategy();
        // Trigger several teleports
        for (int i = 0; i < 100; i++) {
            t.updateState(disp, ball);
        }
        // Ball should be within dimension bounds
        assertTrue(ball.getLocation().x >= 0 && ball.getLocation().x < 800,
                "Teleported x should be within [0, 800)");
        assertTrue(ball.getLocation().y >= 0 && ball.getLocation().y < 600,
                "Teleported y should be within [0, 600)");
    }

    // ---------------------------------------------------------------
    // RandomWalkStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testRandomWalkChangesVelocity() {
        Ball ball = makeBall(new Point(100, 100), new Point(0, 0), 10);
        RandomWalkStrategy rw = new RandomWalkStrategy();
        // Each tick adds or subtracts 1 from each component
        // After many ticks, velocity should have changed from (0,0)
        for (int i = 0; i < 100; i++) {
            rw.updateState(disp, ball);
        }
        boolean changed = ball.getVelocity().x != 0 || ball.getVelocity().y != 0;
        assertTrue(changed, "RandomWalk should change velocity over 100 ticks");
    }

    @Test
    void testRandomWalkSingleTickChangesBy1() {
        Ball ball = makeBall(new Point(100, 100), new Point(0, 0), 10);
        new RandomWalkStrategy().updateState(disp, ball);
        // Each component should be exactly +1 or -1
        assertTrue(ball.getVelocity().x == 1 || ball.getVelocity().x == -1,
                "RandomWalk should change x velocity by exactly 1");
        assertTrue(ball.getVelocity().y == 1 || ball.getVelocity().y == -1,
                "RandomWalk should change y velocity by exactly 1");
    }

    @Test
    void testRandomWalkDoesNotChangeRadius() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        int radiusBefore = ball.getRadius();
        new RandomWalkStrategy().updateState(disp, ball);
        assertEquals(radiusBefore, ball.getRadius(), "RandomWalk should not change radius");
    }

    // ---------------------------------------------------------------
    // SwitcherStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testSwitcherDefaultIsStraight() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), 10);
        SwitcherStrategy sw = new SwitcherStrategy();
        // Default internal strategy is StraightStrategy (no-op)
        sw.updateState(disp, ball);
        assertEquals(0, ball.getVelocity().y, "SwitcherStrategy with default should behave as StraightStrategy");
        assertEquals(5, ball.getVelocity().x);
    }

    @Test
    void testSwitcherCanSwitchToGravity() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), 10);
        SwitcherStrategy sw = new SwitcherStrategy();
        sw.switchStrategy(new GravityStrategy());
        sw.updateState(disp, ball);
        assertEquals(1, ball.getVelocity().y, "After switching to Gravity, y velocity should increase");
    }

    @Test
    void testSwitcherGetStrategy() {
        SwitcherStrategy sw = new SwitcherStrategy();
        assertNotNull(sw.getStrategy(), "Default strategy should not be null");
        assertTrue(sw.getStrategy() instanceof StraightStrategy,
                "Default strategy should be StraightStrategy");
    }

    @Test
    void testSwitcherGetStrategyAfterSwitch() {
        SwitcherStrategy sw = new SwitcherStrategy();
        GravityStrategy g = new GravityStrategy();
        sw.switchStrategy(g);
        assertSame(g, sw.getStrategy(), "getStrategy should return the switched-to strategy");
    }

    @Test
    void testSwitcherMultipleSwitches() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), 10);
        SwitcherStrategy sw = new SwitcherStrategy();

        // Switch to Gravity
        sw.switchStrategy(new GravityStrategy());
        sw.updateState(disp, ball);
        assertEquals(1, ball.getVelocity().y);

        // Switch to Straight (no-op) -- y velocity stays at 1
        sw.switchStrategy(new StraightStrategy());
        sw.updateState(disp, ball);
        assertEquals(1, ball.getVelocity().y, "After switching to Straight, velocity should not change");
    }

    // ---------------------------------------------------------------
    // ExplodingStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testExplodingStrategyExists() {
        assertDoesNotThrow(() -> new ExplodingStrategy(),
                "ExplodingStrategy should be constructable");
    }

    @Test
    void testExplodingStrategyNoBounceNoOp() {
        // Ball that hasn't bounced -- ExplodingStrategy should be a no-op
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 20);
        ExplodingStrategy es = new ExplodingStrategy();
        int radiusBefore = ball.getRadius();
        es.updateState(disp, ball);
        assertEquals(radiusBefore, ball.getRadius(), "ExplodingStrategy should not change radius when no bounce");
    }

    // ---------------------------------------------------------------
    // Ball construction and basic methods
    // ---------------------------------------------------------------

    @Test
    void testBallConstruction() {
        Ball ball = new Ball(new Point(50, 75), new Point(3, 4), Color.BLUE, 15, mockDim, new StraightStrategy());
        assertEquals(50, ball.getLocation().x);
        assertEquals(75, ball.getLocation().y);
        assertEquals(3, ball.getVelocity().x);
        assertEquals(4, ball.getVelocity().y);
        assertEquals(Color.BLUE, ball.getColor());
        assertEquals(15, ball.getRadius());
    }

    @Test
    void testBallMove() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 3), 10);
        ball.move();
        assertEquals(105, ball.getLocation().x, "Ball.move should add velocity to location");
        assertEquals(103, ball.getLocation().y);
    }

    @Test
    void testBallBounceAtRightWall() {
        // Ball at right edge: location.x + radius >= dimension.width
        // 795 + 10 = 805 >= 800 -> bounce
        Ball ball = makeBall(new Point(795, 300), new Point(5, 0), 10);
        ball.bounce();
        assertEquals(-5, ball.getVelocity().x, "Ball should reverse x velocity at right wall");
    }

    @Test
    void testBallBounceAtBottomWall() {
        // Ball at bottom edge: location.y + radius >= dimension.height
        // 595 + 10 = 605 >= 600 -> bounce
        Ball ball = makeBall(new Point(400, 595), new Point(0, 5), 10);
        ball.bounce();
        assertEquals(-5, ball.getVelocity().y, "Ball should reverse y velocity at bottom wall");
    }

    @Test
    void testBallSettersAndGetters() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);

        ball.setLocation(new Point(200, 300));
        assertEquals(200, ball.getLocation().x);
        assertEquals(300, ball.getLocation().y);

        ball.setVelocity(new Point(-3, 7));
        assertEquals(-3, ball.getVelocity().x);
        assertEquals(7, ball.getVelocity().y);

        ball.setRadius(25);
        assertEquals(25, ball.getRadius());

        ball.setColor(Color.GREEN);
        assertEquals(Color.GREEN, ball.getColor());
    }

    @Test
    void testBallPaintDoesNotThrow() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 5), 10);
        Graphics g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).createGraphics();
        assertDoesNotThrow(() -> ball.paint(g), "Ball.paint should not throw");
    }
}

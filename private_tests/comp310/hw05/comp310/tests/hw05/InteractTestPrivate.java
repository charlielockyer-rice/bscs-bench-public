package comp310.tests.hw05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Point;

import hw05.model.Ball;
import hw05.model.IBall;
import hw05.model.IBallAlgo;
import hw05.model.IBallCmd;
import hw05.model.interactStrategies.IInteractStrategy;
import hw05.model.interactStrategies.CollideStrategy;
import hw05.model.interactStrategies.FreezeStrategy;
import hw05.model.interactStrategies.AttractStrategy;
import hw05.model.interactStrategies.CloneTraitsStrategy;
import hw05.model.updateStrategies.CollideCriteriaStrategy;
import hw05.model.updateStrategies.SameColorCriteriaStrategy;
import hw05.model.updateStrategies.MultiStrategy;
import hw05.model.updateStrategies.GravityStrategy;
import hw05.model.updateStrategies.StraightStrategy;
import provided.utils.displayModel.IDimension;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;

/**
 * JUnit 5 tests for HW05 interaction strategies and criteria strategies.
 * Tests are designed to run without a GUI by creating Ball objects
 * directly and asserting on state changes after interactions.
 */
class InteractTestPrivate {

    /** Simple IDimension mock. */
    private IDimension mockDim(int w, int h) {
        return new IDimension() {
            public int getWidth() { return w; }
            public int getHeight() { return h; }
        };
    }

    /** No-op IBallAlgo. */
    private IBallAlgo noOpAlgo() {
        return new IBallAlgo() {
            public void caseDefault(IBall host) {
                // no-op
            }
        };
    }

    /**
     * Create a Ball for testing. Canvas is null since we do not need a real container.
     */
    private Ball makeBall(Point location, Point velocity, Color color, int radius) {
        return new Ball(location, velocity, color, radius, mockDim(800, 600), null, noOpAlgo());
    }

    // ---------------------------------------------------------------
    // FreezeStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testCloneTraitsStrategyCopiesRadiusAndColor() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 25);
        CloneTraitsStrategy clone = new CloneTraitsStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = clone.interactWith(source, target, disp);
        cmd.apply(source, disp);

        // CloneTraitsStrategy sets the applied ball's radius and color to the target's
        assertEquals(25, source.getRadius(), "CloneTraitsStrategy should copy target's radius to the applied ball");
        assertEquals(Color.BLUE, source.getColor(), "CloneTraitsStrategy should copy target's color to the applied ball");
    }

    @Test
    void testCloneTraitsStrategyDoesNotAffectVelocity() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 25);
        CloneTraitsStrategy clone = new CloneTraitsStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = clone.interactWith(source, target, disp);
        cmd.apply(source, disp);

        assertEquals(5, source.getVelocity().x, "CloneTraitsStrategy should not change velocity x");
        assertEquals(3, source.getVelocity().y, "CloneTraitsStrategy should not change velocity y");
    }

    // ---------------------------------------------------------------
    // CollideStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testCollideStrategyReturnsCommand() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 0), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-5, 0), Color.BLUE, 10);
        CollideStrategy collide = new CollideStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = collide.interactWith(source, target, disp);
        assertNotNull(cmd, "CollideStrategy should return a non-null IBallCmd");
    }

    @Test
    void testCollideStrategyModifiesVelocity() {
        // Two balls approaching each other head-on along the x-axis
        Ball source = makeBall(new Point(100, 100), new Point(10, 0), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-10, 0), Color.BLUE, 10);
        CollideStrategy collide = new CollideStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        int origVx = source.getVelocity().x;
        IBallCmd cmd = collide.interactWith(source, target, disp);
        cmd.apply(source, disp);

        // After collision, source's velocity should have been modified by the impulse
        assertNotEquals(origVx, source.getVelocity().x,
                "CollideStrategy should modify source's x velocity after head-on collision");
    }

    @Test
    void testCollideStrategyReducedMassInfiniteMass() throws Exception {
        CollideStrategy collide = new CollideStrategy();
        java.lang.reflect.Method m = CollideStrategy.class.getDeclaredMethod("reducedMass", double.class, double.class);
        m.setAccessible(true);
        // When source mass is infinite, reduced mass = target mass
        double result = (double) m.invoke(collide, Double.POSITIVE_INFINITY, 5.0);
        assertEquals(5.0, result, "Reduced mass with infinite source should return target mass");

        // When target mass is infinite, reduced mass = source mass
        double result2 = (double) m.invoke(collide, 3.0, Double.POSITIVE_INFINITY);
        assertEquals(3.0, result2, "Reduced mass with infinite target should return source mass");
    }

    @Test
    void testCollideStrategyReducedMassFinite() throws Exception {
        CollideStrategy collide = new CollideStrategy();
        java.lang.reflect.Method m = CollideStrategy.class.getDeclaredMethod("reducedMass", double.class, double.class);
        m.setAccessible(true);
        // reducedMass(4, 4) = (4*4)/(4+4) = 16/8 = 2.0
        double result = (double) m.invoke(collide, 4.0, 4.0);
        assertEquals(2.0, result, 0.001, "Reduced mass of equal masses should be half of either mass");
    }

    // ---------------------------------------------------------------
    // IInteractStrategy.NULL tests
    // ---------------------------------------------------------------

    @Test
    void testNullInteractStrategyReturnsNoOpCommand() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 10);
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = IInteractStrategy.NULL.interactWith(source, target, disp);
        assertNotNull(cmd, "NULL interact strategy should return a non-null command");

        // Applying the null command should be a no-op
        Point velBefore = new Point(source.getVelocity());
        cmd.apply(source, disp);
        assertEquals(velBefore, source.getVelocity(), "NULL interact strategy command should not change velocity");
    }

    // ---------------------------------------------------------------
    // CollideCriteriaStrategy tests (criteria update strategy)
    // ---------------------------------------------------------------

    @Test
    void testCollideCriteriaDetectsOverlap() {
        // Two overlapping balls
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 15);
        Ball target = makeBall(new Point(110, 100), new Point(-3, 2), Color.BLUE, 15);

        // Set interact strategy on both so the interaction behavior triggers
        source.setInteractStrategy(new FreezeStrategy());
        target.setInteractStrategy(new FreezeStrategy());

        CollideCriteriaStrategy criteria = new CollideCriteriaStrategy();

        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        disp.addObserver(source);
        disp.addObserver(target);

        // Source(100,100) and Target(110,100) are 10 apart.
        // minDistance = 15+15 = 30 > 10, so collision should be detected.
        // Both balls should get frozen (velocity set to 0) by FreezeStrategy.
        criteria.updateState(source, disp);

        assertEquals(0, source.getVelocity().x,
                "Source should be frozen after collide criteria detects overlap");
        assertEquals(0, source.getVelocity().y,
                "Source should be frozen after collide criteria detects overlap");
    }

    @Test
    void testCollideCriteriaNoOverlap() {
        // Two balls far apart - should not interact
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(500, 500), new Point(-3, 2), Color.BLUE, 10);

        source.setInteractStrategy(new FreezeStrategy());
        target.setInteractStrategy(new FreezeStrategy());

        CollideCriteriaStrategy criteria = new CollideCriteriaStrategy();

        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        disp.addObserver(source);
        disp.addObserver(target);

        criteria.updateState(source, disp);

        assertEquals(5, source.getVelocity().x, "Source velocity should be unchanged when no overlap");
        assertEquals(3, source.getVelocity().y, "Source velocity should be unchanged when no overlap");
    }

    // ---------------------------------------------------------------
    // SameColorCriteriaStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testSameColorCriteriaSameColor() {
        // Two balls with the same color - should trigger interaction
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.RED, 10);

        source.setInteractStrategy(new FreezeStrategy());
        target.setInteractStrategy(new FreezeStrategy());

        SameColorCriteriaStrategy criteria = new SameColorCriteriaStrategy();

        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        disp.addObserver(source);
        disp.addObserver(target);

        criteria.updateState(source, disp);

        assertEquals(0, source.getVelocity().x,
                "Same color balls should trigger interaction and freeze source");
    }

    @Test
    void testSameColorCriteriaDifferentColor() {
        // Two balls with very different colors - should NOT trigger interaction
        // Color distance threshold is 45
        // RED=(255,0,0) vs BLUE=(0,0,255), distance = sqrt(255^2+0+255^2) = ~360 >> 45
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 10);

        source.setInteractStrategy(new FreezeStrategy());
        target.setInteractStrategy(new FreezeStrategy());

        SameColorCriteriaStrategy criteria = new SameColorCriteriaStrategy();

        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        disp.addObserver(source);
        disp.addObserver(target);

        criteria.updateState(source, disp);

        assertEquals(5, source.getVelocity().x,
                "Different color balls should not trigger interaction (velocity unchanged)");
        assertEquals(3, source.getVelocity().y,
                "Different color balls should not trigger interaction (velocity unchanged)");
    }

    // ---------------------------------------------------------------
    // MultiStrategy (combining update strategies) test
    // ---------------------------------------------------------------

    @Test
    void testMultiStrategyCombinesUpdateStrategies() {
        Ball ball = makeBall(new Point(100, 100), new Point(5, 0), Color.RED, 10);
        // Combine gravity (adds to y velocity) with straight (no-op)
        GravityStrategy gravity = new GravityStrategy();
        StraightStrategy straight = new StraightStrategy();
        MultiStrategy multi = new MultiStrategy(gravity, straight);

        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();
        multi.updateState(ball, disp);

        assertEquals(1, ball.getVelocity().y,
                "MultiStrategy combining gravity + straight should add 1 to y velocity");
        assertEquals(5, ball.getVelocity().x,
                "MultiStrategy combining gravity + straight should not change x velocity");
    }
}

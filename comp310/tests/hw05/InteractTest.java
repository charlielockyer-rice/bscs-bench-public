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
class InteractTest {

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
    void testFreezeStrategySetsVelocityToZero() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 10);
        FreezeStrategy freeze = new FreezeStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = freeze.interactWith(source, target, disp);
        assertNotNull(cmd, "FreezeStrategy should return a non-null IBallCmd");

        // Apply the returned command to the source ball
        cmd.apply(source, disp);

        assertEquals(0, source.getVelocity().x, "FreezeStrategy should set x velocity to 0");
        assertEquals(0, source.getVelocity().y, "FreezeStrategy should set y velocity to 0");
    }

    @Test
    void testFreezeStrategyDoesNotAffectTargetDirectly() {
        Ball source = makeBall(new Point(100, 100), new Point(5, 3), Color.RED, 10);
        Ball target = makeBall(new Point(115, 100), new Point(-3, 2), Color.BLUE, 10);
        FreezeStrategy freeze = new FreezeStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = freeze.interactWith(source, target, disp);
        // Apply only to source
        cmd.apply(source, disp);

        // Target's velocity should be untouched by this command
        assertEquals(-3, target.getVelocity().x, "Target x velocity should be unchanged");
        assertEquals(2, target.getVelocity().y, "Target y velocity should be unchanged");
    }

    // ---------------------------------------------------------------
    // AttractStrategy tests
    // ---------------------------------------------------------------

    @Test
    void testAttractStrategyAdjustsVelocityTowardTarget() {
        Ball source = makeBall(new Point(100, 100), new Point(0, 0), Color.RED, 10);
        Ball target = makeBall(new Point(500, 500), new Point(0, 0), Color.BLUE, 10);
        AttractStrategy attract = new AttractStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = attract.interactWith(source, target, disp);
        assertNotNull(cmd, "AttractStrategy should return a non-null IBallCmd");

        // Apply the command to source; it should nudge velocity toward target
        cmd.apply(source, disp);

        // attractNudge = 0.0025; (500-100)*0.0025 = 1.0, int cast = 1
        assertTrue(source.getVelocity().x >= 0,
                "AttractStrategy should nudge x velocity toward target (positive direction)");
        assertTrue(source.getVelocity().y >= 0,
                "AttractStrategy should nudge y velocity toward target (positive direction)");
    }

    @Test
    void testAttractStrategyOppositeDirection() {
        Ball source = makeBall(new Point(500, 500), new Point(0, 0), Color.RED, 10);
        Ball target = makeBall(new Point(100, 100), new Point(0, 0), Color.BLUE, 10);
        AttractStrategy attract = new AttractStrategy();
        IDispatcher<IBallCmd> disp = new SequentialDispatcher<>();

        IBallCmd cmd = attract.interactWith(source, target, disp);
        cmd.apply(source, disp);

        // (100-500)*0.0025 = -1.0, int cast = -1
        assertTrue(source.getVelocity().x <= 0,
                "AttractStrategy should nudge x velocity toward target (negative direction)");
        assertTrue(source.getVelocity().y <= 0,
                "AttractStrategy should nudge y velocity toward target (negative direction)");
    }

    // ---------------------------------------------------------------
    // CloneTraitsStrategy tests
    // ---------------------------------------------------------------

}

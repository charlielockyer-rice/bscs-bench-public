package comp310.tests.hw06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import javax.swing.JPanel;

import hw06.model.BallHosts.IBall;
import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.DynamicBall;
import hw06.model.BallVisitors.IBallAlgo;
import hw06.model.BallVisitors.BallAlgo;
import hw06.model.BallVisitors.ABallAlgoCmd;
import provided.ballworld.extVisitors.IBallHostID;

/**
 * JUnit 5 tests for HW06 extended visitor framework and typed ball hosts.
 * These tests verify that the extended visitor pattern dispatches correctly
 * across ball host types and that basic ball operations work without a GUI.
 */
class ExtVisitorTestPrivate {

    /**
     * A minimal Component for testing. JPanel provides a real AWT component
     * without requiring a visible window.
     */
    private Component mockContainer() {
        JPanel panel = new JPanel();
        panel.setSize(800, 600);
        return panel;
    }

    /** No-op install algo that does not configure any strategies. */
    private IBallAlgo<Void, Void> noOpInstallAlgo() {
        return new BallAlgo<>(new ABallAlgoCmd<Void, Void>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Void apply(IBallHostID index, IBall host, Void... params) {
                // no-op: do not install any strategies
                return null;
            }
        });
    }

    // ---------------------------------------------------------------
    // DefaultBall construction and basic operations
    // ---------------------------------------------------------------

    @Test
    void testDynamicBallConstruction() {
        Component container = mockContainer();
        DynamicBall ball = new DynamicBall(
                new Point(200, 200), 15, new Point(-3, 4), Color.BLUE,
                container, noOpInstallAlgo());

        assertNotNull(ball, "DynamicBall should be constructable");
        assertEquals(200, ball.getLocation().x, "Initial x should be 200");
        assertEquals(15, ball.getRadius(), "Initial radius should be 15");
    }

    @Test
    void testDynamicBallSetHostID() {
        Component container = mockContainer();
        DynamicBall ball = new DynamicBall(
                new Point(200, 200), 15, new Point(-3, 4), Color.BLUE,
                container, noOpInstallAlgo());

        // Should be able to change host ID to DefaultBall's ID
        assertDoesNotThrow(() -> ball.setHostID(DefaultBall.ID),
                "DynamicBall should allow changing its host ID");
    }

    // ---------------------------------------------------------------
    // Extended visitor dispatch (BallAlgo)
    // ---------------------------------------------------------------

    @Test
    void testBallAlgoDefaultCaseDispatch() {
        Component container = mockContainer();
        final boolean[] defaultCalled = {false};

        BallAlgo<String, Void> algo = new BallAlgo<>(new ABallAlgoCmd<String, Void>() {
            private static final long serialVersionUID = 1L;
            @Override
            public String apply(IBallHostID index, IBall host, Void... params) {
                defaultCalled[0] = true;
                return "default";
            }
        });

        DefaultBall ball = new DefaultBall(
                new Point(100, 100), 10, new Point(5, 3), Color.RED,
                container, noOpInstallAlgo());

        String result = ball.execute(algo);
        assertTrue(defaultCalled[0], "BallAlgo default case should be called for DefaultBall");
        assertEquals("default", result, "BallAlgo should return the default case result");
    }

    @Test
    void testBallAlgoWithSpecificHostCase() {
        Component container = mockContainer();

        BallAlgo<String, Void> algo = new BallAlgo<>(new ABallAlgoCmd<String, Void>() {
            private static final long serialVersionUID = 1L;
            @Override
            public String apply(IBallHostID index, IBall host, Void... params) {
                return "default";
            }
        });

        // Add a specific case for DefaultBall
        algo.setCmd(DefaultBall.ID, new ABallAlgoCmd<String, Void>() {
            private static final long serialVersionUID = 1L;
            @Override
            public String apply(IBallHostID index, IBall host, Void... params) {
                return "defaultBall-specific";
            }
        });

        DefaultBall ball = new DefaultBall(
                new Point(100, 100), 10, new Point(5, 3), Color.RED,
                container, noOpInstallAlgo());

        String result = ball.execute(algo);
        assertEquals("defaultBall-specific", result,
                "BallAlgo should dispatch to the specific case for DefaultBall's host ID");
    }
}

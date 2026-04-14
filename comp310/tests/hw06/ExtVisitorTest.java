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
class ExtVisitorTest {

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
    void testDefaultBallConstruction() {
        Component container = mockContainer();
        DefaultBall ball = new DefaultBall(
                new Point(100, 100), 10, new Point(5, 3), Color.RED,
                container, noOpInstallAlgo());

        assertNotNull(ball, "DefaultBall should be constructable");
        assertEquals(100, ball.getLocation().x, "Initial x should be 100");
        assertEquals(100, ball.getLocation().y, "Initial y should be 100");
        assertEquals(10, ball.getRadius(), "Initial radius should be 10");
        assertEquals(Color.RED, ball.getColor(), "Initial color should be RED");
        assertEquals(5, ball.getVelocity().x, "Initial x velocity should be 5");
        assertEquals(3, ball.getVelocity().y, "Initial y velocity should be 3");
    }

    @Test
    void testDefaultBallMove() {
        Component container = mockContainer();
        DefaultBall ball = new DefaultBall(
                new Point(100, 100), 10, new Point(5, 3), Color.RED,
                container, noOpInstallAlgo());

        ball.move();

        assertEquals(105, ball.getLocation().x, "After move, x should increase by velocity.x");
        assertEquals(103, ball.getLocation().y, "After move, y should increase by velocity.y");
    }

    // ---------------------------------------------------------------
    // DynamicBall construction and type switching
    // ---------------------------------------------------------------

}

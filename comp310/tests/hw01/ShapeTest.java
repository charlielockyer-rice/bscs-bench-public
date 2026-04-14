package comp310.tests.hw01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import hw01.shape.AShape;
import hw01.shape.Rectangle;
import hw01.shape.Ellipse;
import hw01.shape.Circle;
import hw01.shape.CompositeShape;

/**
 * JUnit 5 tests for the HW01 shape hierarchy.
 * Tests construction, position, color delegation, and painting
 * without requiring a GUI.
 */
class ShapeTest {

    /**
     * Create a Graphics object backed by an off-screen BufferedImage.
     */
    private Graphics getGraphics() {
        return new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).createGraphics();
    }

    // ---------------------------------------------------------------
    // Rectangle tests
    // ---------------------------------------------------------------

    @Test
    void testRectangleConstruction() {
        Rectangle r = new Rectangle(10, 20, 100, 50, Color.RED);
        assertEquals(10, r.getXPos(), "Rectangle xPos should match constructor arg");
        assertEquals(20, r.getYPos(), "Rectangle yPos should match constructor arg");
    }

    @Test
    void testRectangleSetPosition() {
        Rectangle r = new Rectangle(0, 0, 100, 50, Color.BLUE);
        r.setXPos(42);
        r.setYPos(99);
        assertEquals(42, r.getXPos());
        assertEquals(99, r.getYPos());
    }

    @Test
    void testRectanglePaintDoesNotThrow() {
        Rectangle r = new Rectangle(10, 20, 100, 50, Color.RED);
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> r.paint(g), "Rectangle.paint should not throw");
    }

    @Test
    void testRectangleSetColor() {
        Rectangle r = new Rectangle(0, 0, 50, 50, Color.RED);
        // setColor should not throw; we verify by painting afterwards
        assertDoesNotThrow(() -> r.setColor(Color.GREEN));
        assertDoesNotThrow(() -> r.paint(getGraphics()));
    }

    // ---------------------------------------------------------------
    // Ellipse tests
    // ---------------------------------------------------------------

    @Test
    void testEllipseConstruction() {
        Ellipse e = new Ellipse(30, 40, 80, 60, Color.BLUE);
        assertEquals(30, e.getXPos(), "Ellipse xPos should match constructor arg");
        assertEquals(40, e.getYPos(), "Ellipse yPos should match constructor arg");
    }

}

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
class ShapeTestPrivate {

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
    void testEllipsePaintDoesNotThrow() {
        Ellipse e = new Ellipse(30, 40, 80, 60, Color.BLUE);
        Graphics g = getGraphics();
        assertDoesNotThrow(() -> e.paint(g), "Ellipse.paint should not throw");
    }

    // ---------------------------------------------------------------
    // Circle tests
    // ---------------------------------------------------------------

    @Test
    void testCircleIsEllipse() {
        Circle c = new Circle(50, 60, 30, Color.YELLOW);
        assertTrue(c instanceof Ellipse, "Circle should extend Ellipse");
    }

    @Test
    void testCircleConstruction() {
        Circle c = new Circle(50, 60, 30, Color.YELLOW);
        assertEquals(50, c.getXPos(), "Circle xPos should match constructor arg");
        assertEquals(60, c.getYPos(), "Circle yPos should match constructor arg");
    }

    @Test
    void testCirclePaintDoesNotThrow() {
        Circle c = new Circle(50, 60, 30, Color.YELLOW);
        assertDoesNotThrow(() -> c.paint(getGraphics()), "Circle.paint should not throw");
    }

    // ---------------------------------------------------------------
    // CompositeShape tests
    // ---------------------------------------------------------------

    @Test
    void testCompositeShapePosition() {
        Rectangle r1 = new Rectangle(10, 50, 100, 50, Color.RED);
        Rectangle r2 = new Rectangle(30, 20, 100, 50, Color.BLUE);
        CompositeShape cs = new CompositeShape(r1, r2);
        assertEquals(10, cs.getXPos(), "Composite xPos should be min of children (10, 30)");
        assertEquals(20, cs.getYPos(), "Composite yPos should be min of children (50, 20)");
    }

    @Test
    void testCompositeShapeSetColorDelegatesToChildren() {
        Rectangle r1 = new Rectangle(0, 0, 50, 50, Color.RED);
        Ellipse e1 = new Ellipse(10, 10, 40, 40, Color.BLUE);
        CompositeShape cs = new CompositeShape(r1, e1);
        // setColor should delegate to both children without throwing
        assertDoesNotThrow(() -> cs.setColor(Color.GREEN));
        // Painting after color change should also work
        assertDoesNotThrow(() -> cs.paint(getGraphics()));
    }

    @Test
    void testCompositeShapePaintDoesNotThrow() {
        Rectangle r1 = new Rectangle(0, 0, 50, 50, Color.RED);
        Circle c1 = new Circle(25, 25, 20, Color.BLUE);
        CompositeShape cs = new CompositeShape(r1, c1);
        assertDoesNotThrow(() -> cs.paint(getGraphics()),
                "CompositeShape.paint should delegate to both children without throwing");
    }

    @Test
    void testCompositeShapeNestedComposite() {
        Rectangle r1 = new Rectangle(100, 200, 50, 50, Color.RED);
        Ellipse e1 = new Ellipse(150, 100, 30, 30, Color.BLUE);
        CompositeShape inner = new CompositeShape(r1, e1);
        // inner position: min(100,150)=100, min(200,100)=100

        Circle c1 = new Circle(50, 300, 20, Color.GREEN);
        CompositeShape outer = new CompositeShape(inner, c1);
        // outer position: min(100,50)=50, min(100,300)=100
        assertEquals(50, outer.getXPos(), "Nested composite xPos should be min across all");
        assertEquals(100, outer.getYPos(), "Nested composite yPos should be min across all");
        assertDoesNotThrow(() -> outer.paint(getGraphics()));
    }

    // ---------------------------------------------------------------
    // General AShape tests
    // ---------------------------------------------------------------

    @Test
    void testSetPositionOnAnyShape() {
        Ellipse e = new Ellipse(0, 0, 60, 40, Color.CYAN);
        e.setXPos(123);
        e.setYPos(456);
        assertEquals(123, e.getXPos());
        assertEquals(456, e.getYPos());
    }
}

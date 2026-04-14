package test.rice.obj;

import main.rice.obj.PyBoolObj;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyBoolObj class.
 */
class PyBoolObjTest {

    /**
     * Two distinct PyBoolObjs representing the Python value True.
     */
    private static final PyBoolObj true1 = new PyBoolObj(true);
    private static final PyBoolObj true2 = new PyBoolObj(true);

    /**
     * Two distinct PyBoolObjs representing the Python value False.
     */
    private static final PyBoolObj false1 = new PyBoolObj(false);
    private static final PyBoolObj false2 = new PyBoolObj(false);

    /**
     * Tests that getValue() returns the expected result for a PyBoolObj representing
     * True.
     */
    @Test
    void testGetValueTrue() {
        assertEquals(true, true1.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyBoolObj representing
     * False.
     */
    @Test
    void testGetValueFalse() {
        assertEquals(false, false1.getValue());
    }

    /**
     * Tests that toString() returns the expected result for a PyBoolObj representing
     * True.
     */
    @Test
    void testToStringTrue() {
        assertEquals("True", true1.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyBoolObj representing
     * False.
     */
    @Test
    void testToStringFalse() {
        assertEquals("False", false1.toString());
    }

    /**
     * Tests that two distinct PyBoolObjs with the same value (true) are considered
     * equivalent by equals().
     */
    @Test
    void testEqualsTrue() {
        assertEquals(true1, true2);
    }

    /**
     * Tests that two distinct PyBoolObjs with the same value (false) are considered
     * equivalent by equals().
     */
    @Test
    void testEqualsFalse() {
        assertEquals(false1, false2);
    }

    /**
     * Tests that a PyBoolObj is not considered to be equivalent to a non-APyObj.
     */
    @Test
    void testNotEqualNonAPyObj() {
        assertNotEquals(true1, true);
    }

    /**
     * Tests that two PyBoolObjs with different values are not considered equivalent by
     * equals().
     */
    @Test
    void testNotEqual() {
        assertNotEquals(true1, false1);
    }

    /**
     * Tests that two distinct PyBoolObjs with the same value (true) return the same value
     * for hashCode().
     */
    @Test
    void testHashCodeTrue() {
        assertEquals(true1.hashCode(), true2.hashCode());
    }

    /**
     * Tests that two distinct PyBoolObjs with the same value (false) return the same
     * value for hashCode().
     */
    @Test
    void testHashCodeFalse() {
        assertEquals(false1.hashCode(), false2.hashCode());
    }

    /**
     * Tests that two distinct PyBoolObjs with different values return different values
     * for hashCode().
     */
    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(true1.hashCode(), false1.hashCode());
    }
}
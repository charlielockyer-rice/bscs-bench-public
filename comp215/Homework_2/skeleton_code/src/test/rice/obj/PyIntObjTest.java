package test.rice.obj;

import main.rice.obj.PyIntObj;
import org.junit.jupiter.api.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyIntObj class.
 */
class PyIntObjTest {

    /**
     * A PyIntObj representing the Python integer value 1.
     */
    private static final PyIntObj posInt = new PyIntObj(1);

    /**
     * A PyIntObjs representing the Python integer value -1.
     */
    private static final PyIntObj negInt = new PyIntObj(-1);

    /**
     * Two distinct PyIntObjs representing the Python integer value 123456789.
     */
    private static final PyIntObj largeInt = new PyIntObj(123456789);
    private static final PyIntObj largeInt2 = new PyIntObj(123456789);

    /**
     * A random integer (between 0 and Integer.MAX_VALUE), and a PyIntObj representing
     * that value.
     */
    private static final int randVal = new Random().nextInt(Integer.MAX_VALUE);
    private static final PyIntObj randInt = new PyIntObj(randVal);

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * positive integer value.
     */
    @Test
    void testGetValuePositive() {
        assertEquals(1, posInt.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * negative integer value.
     */
    @Test
    void testGetValueNegative() {
        assertEquals(-1, negInt.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * random integer value.
     */
    @Test
    void testGetValueRandom() {
        assertEquals(randVal, randInt.getValue());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * positive integer value.
     */
    @Test
    void testToStringPositive() {
        assertEquals("1", posInt.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * negative integer value.
     */
    @Test
    void testToStringNegative() {
        assertEquals("-1", negInt.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * large positive integer value.
     */
    @Test
    void testToStringLarge() {
        assertEquals("123456789", largeInt.toString());
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the same integer value are
     * considered equivalent by equals().
     */
    @Test
    void testEqualsSimple() {
        assertEquals(largeInt, largeInt2);
    }

    /**
     * Tests that two distinct PyIntObjs constructed with different values are not
     * considered equivalent by equals().
     */
    @Test
    void testNotEqual() {
        assertNotEquals(posInt, largeInt);
    }

    /**
     * Tests that a PyIntObj is not considered to be equivalent to a non-APyObj.
     */
    @Test
    void testNotEqualNonAPyObj() {
        assertNotEquals(posInt, 1);
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the same integer value return
     * the same value for hashCode().
     */
    @Test
    void testHashCodeSimple() {
        assertEquals(largeInt.hashCode(), largeInt2.hashCode());
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the different values return
     * different values for hashCode().
     */
    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(posInt.hashCode(), largeInt.hashCode());
    }
}
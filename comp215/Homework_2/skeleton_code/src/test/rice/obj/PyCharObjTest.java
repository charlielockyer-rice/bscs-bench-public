package test.rice.obj;

import main.rice.obj.PyCharObj;
import org.junit.jupiter.api.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyCharObj class.
 */
class PyCharObjTest {

    /**
     * Two distinct PyCharObjs representing the Python value 'a'.
     */
    private static final PyCharObj lower1 = new PyCharObj('a');
    private static final PyCharObj lower2 = new PyCharObj('a');

    /**
     * A PyCharObj representing the Python value 'B'.
     */
    private static final PyCharObj upper = new PyCharObj('B');

    /**
     * Two distinct PyCharObjs representing the Python value '1'.
     */
    private static final PyCharObj num1 = new PyCharObj('1');
    private static final PyCharObj num2 = new PyCharObj('1');

    /**
     * A random integer (between 0 and 256, and a PyCharObj representing the corresponding
     * character.
     */
    private static int randInt;
    private static PyCharObj randVal;

    /**
     * Ensures that randVal gets initialized to a non-alphanumeric character.
     */
    @BeforeAll
    static void setUp() {
        String alphanumeric =
            "abcdefghijklmnopqrstuvqxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        do {
            randInt = new Random().nextInt(256);
            randVal = new PyCharObj((char) randInt);
        } while (alphanumeric.indexOf(randInt) > -1);
    }

    /**
     * Tests that getValue() returns the expected result for a PyCharObj representing a
     * lowercase letter.
     */
    @Test
    void testGetValueLower() {
        assertEquals('a', lower1.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyCharObj representing an
     * uppercase letter.
     */
    @Test
    void testGetValueUpper() {
        assertEquals('B', upper.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyCharObj representing a
     * numeric character.
     */
    @Test
    void testGetValueNumber() {
        assertEquals('1', num1.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyCharObj representing a
     * random character.
     */
    @Test
    void testGetValueRandom() {
        assertEquals((char) randInt, randVal.getValue());
    }

    /**
     * Tests that toString() returns the expected result for a PyCharObj representing a
     * lowercase letter.
     */
    @Test
    void testToStringLower() {
        assertEquals("'a'", lower1.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyCharObj representing an
     * uppercase letter.
     */
    @Test
    void testToStringUpper() {
        assertEquals("'B'", upper.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyCharObj representing a
     * numeric character.
     */
    @Test
    void testToStringNumber() {
        assertEquals("'1'", num1.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyCharObj representing a
     * random character.
     */
    @Test
    void testToStringRandom() {
        assertEquals("'" + (char) randInt + "'", randVal.toString());
    }

    /**
     * Tests that two distinct PyCharObjs with the same value ('a') are considered
     * equivalent by equals().
     */
    @Test
    void testEqualsSimple() {
        assertEquals(lower1, lower2);
    }

    /**
     * Tests that two distinct PyCharObjs with different values are not considered
     * equivalent by equals().
     */
    @Test
    void testNotEqual() {
        assertNotEquals(lower1, upper);
    }

    /**
     * Tests that a PyCharObj is not considered to be equivalent to a non-APyObj.
     */
    @Test
    void testNotEqualNonAPyObj() {
        assertNotEquals(lower1, 'a');
    }

    /**
     * Tests that two distinct PyCharObjs with the same value ('a') return the same value
     * for hashCode().
     */
    @Test
    void testHashCodeTrue1() {
        assertEquals(lower1.hashCode(), lower2.hashCode());
    }

    /**
     * Tests that two distinct PyCharObjs with the same value ('1') return the same value
     * for hashCode().
     */
    @Test
    void testHashCodeTrue2() {
        assertEquals(num1.hashCode(), num2.hashCode());
    }

    /**
     * Tests that two distinct PyCharObjs with different values return different values
     * for hashCode().
     */
    @Test
    void testHashCodeFalse() {
        assertNotEquals(lower1.hashCode(), upper.hashCode());
    }
}
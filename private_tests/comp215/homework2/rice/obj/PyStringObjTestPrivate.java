package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyStringObj class.
 */
class PyStringObjTestPrivate {

    // Empty string
    private static final PyStringObj emptyStr = new PyStringObj("");

    // Single char string
    private static final PyStringObj singleStr = new PyStringObj("a");

    // Multiple char string
    private static final PyStringObj multiStr = new PyStringObj("hello");
    private static final PyStringObj multiStr2 = new PyStringObj("hello");

    // String with special characters
    private static final PyStringObj specialStr = new PyStringObj("a\tb\nc");

    // Subset string
    private static final PyStringObj subsetStr = new PyStringObj("hel");

    @Test
    void testToStringSingle() {
        assertEquals("'a'", singleStr.toString());
    }

    @Test
    void testToStringMultiple() {
        assertEquals("'hello'", multiStr.toString());
    }

    @Test
    void testToStringSpecialChars() {
        // Special chars should be preserved literally
        assertEquals("'a\tb\nc'", specialStr.toString());
    }

    @Test
    void testEqualsSingle() {
        PyStringObj other = new PyStringObj("a");
        assertEquals(singleStr, other);
    }

    @Test
    void testEqualsMultiple() {
        assertEquals(multiStr, multiStr2);
    }

    @Test
    void testNotEqual() {
        assertNotEquals(singleStr, multiStr);
    }

    @Test
    void testNotEqualSubset1() {
        assertNotEquals(multiStr, subsetStr);
    }

    @Test
    void testNotEqualSubset2() {
        assertNotEquals(subsetStr, multiStr);
    }

    @Test
    void testNotEqualNonAPyObj() {
        assertNotEquals(multiStr, "hello");
    }

    @Test
    void testHashCodeEqual() {
        assertEquals(multiStr.hashCode(), multiStr2.hashCode());
    }

    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(singleStr.hashCode(), multiStr.hashCode());
    }
}

package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyStringObj class.
 */
class PyStringObjTest {

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
    void testGetValue() {
        List<PyCharObj> value = multiStr.getValue();
        assertEquals(5, value.size());
        assertEquals('h', value.get(0).getValue());
        assertEquals('e', value.get(1).getValue());
        assertEquals('l', value.get(2).getValue());
        assertEquals('l', value.get(3).getValue());
        assertEquals('o', value.get(4).getValue());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("''", emptyStr.toString());
    }

}

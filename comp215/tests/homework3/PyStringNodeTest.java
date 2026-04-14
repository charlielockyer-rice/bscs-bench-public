package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyStringNode class.
 * Tests exhaustive and random value generation for Python string nodes.
 * PyStringNode is unique because it takes a String of allowed characters in its constructor
 * rather than a left child node.
 */
class PyStringNodeTest {

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetExDomain() {
        PyStringNode stringNode = new PyStringNode("abc");
        List<Integer> exDomain = Arrays.asList(0, 1, 2);
        stringNode.setExDomain(new ArrayList<>(exDomain));
        assertEquals(exDomain, stringNode.getExDomain());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsOneLenOne() {
        // Domain has one character option, length 1
        PyStringNode stringNode = new PyStringNode("x");
        stringNode.setExDomain(Arrays.asList(1));

        Set<PyStringObj> result = stringNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyStringObj("x")));
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValEmpty() {
        // Random domain only contains 0
        PyStringNode stringNode = new PyStringNode("abc");
        stringNode.setRanDomain(Arrays.asList(0));

        PyStringObj result = stringNode.genRandVal();
        assertEquals(0, result.getValue().size());
        assertEquals(new PyStringObj(""), result);
    }
}

package test.rice.node;

import main.rice.node.PyFloatNode;
import main.rice.obj.PyFloatObj;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyFloatNode class.
 */
class PyFloatNodeTest {

    /**
     * A sample PyFloatNode.
     */
    private static PyFloatNode node;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        node = new PyFloatNode();
        List<Double> exDomain = List.of(1.0, 2.0);
        node.setExDomain(exDomain);
        node.setRanDomain(exDomain);
    }

    /**
     * Tests that getExDomain() works.
     */
    @Test
    void testGetExDomain() {
        assertEquals(List.of(1.0, 2.0), node.getExDomain());
    }

    /**
     * Tests that getRanDomain() works.
     */
    @Test
    void testGetRanDomain() {
        assertEquals(List.of(1.0, 2.0), node.getRanDomain());
    }

    /**
     * Tests that getLeftChild() returns null.
     */
    @Test
    void testGetLeftChild() {
        assertNull(node.getLeftChild());
    }

    /**
     * Tests that getRightChild() returns null.
     */
    @Test
    void testGetRightChild() {
        assertNull(node.getRightChild());
    }

    /**
     * Tests genExVals().
     */
    @Test
    void testGenExVals() {
        Set<PyFloatObj> actual = node.genExVals();
        Set<PyFloatObj> expected = Set.of(new PyFloatObj(1.0),
                new PyFloatObj(2.0));
        assertEquals(expected, actual);
    }
}
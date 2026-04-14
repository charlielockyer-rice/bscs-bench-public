package test.rice.node;

import main.rice.node.PyBoolNode;
import main.rice.obj.PyBoolObj;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyBoolNode class.
 */
class PyBoolNodeTest {

    /**
     * A sample PyBoolNode.
     */
    private static PyBoolNode node;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        node = new PyBoolNode();
        List<Integer> exDomain = List.of(0, 1);
        node.setExDomain(exDomain);
        node.setRanDomain(exDomain);
    }

    /**
     * Tests that getExDomain() works.
     */
    @Test
    void testGetExDomain() {
        assertEquals(List.of(0, 1), node.getExDomain());
    }

    /**
     * Tests that getRanDomain() works.
     */
    @Test
    void testGetRanDomain() {
        assertEquals(List.of(0, 1), node.getRanDomain());
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
        Set<PyBoolObj> actual = node.genExVals();
        Set<PyBoolObj> expected = Set.of(new PyBoolObj(true),
                new PyBoolObj(false));
        assertEquals(expected, actual);
    }
}
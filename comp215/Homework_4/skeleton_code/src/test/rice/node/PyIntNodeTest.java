package test.rice.node;

import main.rice.node.PyIntNode;
import main.rice.obj.PyIntObj;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyIntNode class.
 */
class PyIntNodeTest {

    /**
     * A sample PyIntNode.
     */
    private static PyIntNode node;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        node = new PyIntNode();
        List<Integer> exDomain = List.of(1, 2);
        node.setExDomain(exDomain);
        node.setRanDomain(exDomain);
    }

    /**
     * Tests that getExDomain() works.
     */
    @Test
    void testGetExDomain() {
        assertEquals(List.of(1, 2), node.getExDomain());
    }

    /**
     * Tests that getRanDomain() works.
     */
    @Test
    void testGetRanDomain() {
        assertEquals(List.of(1, 2), node.getRanDomain());
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
        Set<PyIntObj> actual = node.genExVals();
        Set<PyIntObj> expected = Set.of(new PyIntObj(1),
                new PyIntObj(2));
        assertEquals(expected, actual);
    }
}
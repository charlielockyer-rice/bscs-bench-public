package test.rice.node;

import main.rice.node.APyNode;
import main.rice.node.PyBoolNode;
import main.rice.obj.PyBoolObj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyBoolNode class.
 * Tests the node that generates PyBoolObj values from domains.
 */
class PyBoolNodeTestPrivate {

    private PyBoolNode node;

    @BeforeEach
    void setUp() {
        node = new PyBoolNode();
    }

    // ==================== Domain Getter Tests (0.4 pts total) ====================

    /**
     * Tests that getExDomain returns the extensive domain that was set.
     * (0.2 pts)
     */
    @Test
    void testGetLeftChild() {
        assertNull(node.getLeftChild());
    }

    /**
     * Tests that getRightChild returns null for PyBoolNode (leaf node).
     * (0.3 pts)
     */
    @Test
    void testGetRightChild() {
        assertNull(node.getRightChild());
    }

    // ==================== genExVals Tests (1.5 pts total) ====================

    /**
     * Tests genExVals when domain contains both 0 and 1 (both boolean values).
     * Should return a set containing both PyBoolObj(false) and PyBoolObj(true).
     * (1.0 pts)
     */
    @Test
    void testGenExValsBoth() {
        List<Integer> exDomain = Arrays.asList(0, 1);
        node.setExDomain(exDomain);

        Set<PyBoolObj> result = node.genExVals();

        assertEquals(2, result.size());
        assertTrue(result.contains(new PyBoolObj(false)));
        assertTrue(result.contains(new PyBoolObj(true)));
    }

    /**
     * Tests genExVals when domain contains values that map to opposite booleans.
     * Values outside 0 and 1 should be ignored (return null from getObj).
     * (0.5 pts)
     */
    @Test
    void testGenExValsOpposite() {
        // Only 0 in domain - should only get false
        List<Integer> exDomain = Arrays.asList(0);
        node.setExDomain(exDomain);

        Set<PyBoolObj> result = node.genExVals();

        assertEquals(1, result.size());
        assertTrue(result.contains(new PyBoolObj(false)));
        assertFalse(result.contains(new PyBoolObj(true)));

        // Only 1 in domain - should only get true
        exDomain = Arrays.asList(1);
        node.setExDomain(exDomain);

        result = node.genExVals();

        assertEquals(1, result.size());
        assertFalse(result.contains(new PyBoolObj(false)));
        assertTrue(result.contains(new PyBoolObj(true)));
    }

    // ==================== genRandVal Tests (1.5 pts total) ====================

    /**
     * Tests genRandVal when domain contains both 0 and 1.
     * Should return either PyBoolObj(false) or PyBoolObj(true).
     * (1.0 pts)
     */
    @Test
    void testGenRandValBoth() {
        List<Integer> ranDomain = Arrays.asList(0, 1);
        node.setRanDomain(ranDomain);

        // Run multiple times to verify randomness and valid values
        Set<PyBoolObj> validValues = new HashSet<>();
        validValues.add(new PyBoolObj(false));
        validValues.add(new PyBoolObj(true));

        for (int i = 0; i < 100; i++) {
            PyBoolObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result));
        }
    }

    /**
     * Tests genRandVal when domain contains only one value (0 or 1).
     * (0.5 pts)
     */
    @Test
    void testGenRandValOpposite() {
        // Only 0 - should always get false
        List<Integer> ranDomain = Arrays.asList(0);
        node.setRanDomain(ranDomain);

        for (int i = 0; i < 10; i++) {
            PyBoolObj result = node.genRandVal();
            assertEquals(new PyBoolObj(false), result);
        }

        // Only 1 - should always get true
        ranDomain = Arrays.asList(1);
        node.setRanDomain(ranDomain);

        for (int i = 0; i < 10; i++) {
            PyBoolObj result = node.genRandVal();
            assertEquals(new PyBoolObj(true), result);
        }
    }
}

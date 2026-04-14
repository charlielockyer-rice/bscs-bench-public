package test.rice.node;

import main.rice.node.APyNode;
import main.rice.node.PyIntNode;
import main.rice.obj.PyIntObj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyIntNode class.
 * Tests the node that generates PyIntObj values from domains.
 */
class PyIntNodeTestPrivate {

    private PyIntNode node;

    @BeforeEach
    void setUp() {
        node = new PyIntNode();
    }

    // ==================== Domain Getter Tests (0.6 pts total) ====================

    /**
     * Tests that getExDomain returns the extensive domain that was set.
     * (0.3 pts)
     */
    @Test
    void testGetLeftChild() {
        assertNull(node.getLeftChild());
    }

    /**
     * Tests that getRightChild returns null for PyIntNode (leaf node).
     * (0.3 pts)
     */
    @Test
    void testGetRightChild() {
        assertNull(node.getRightChild());
    }

    // ==================== genExVals Tests (1.4 pts total) ====================

    /**
     * Tests genExVals with positive integers in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenExValsPos() {
        List<Integer> exDomain = Arrays.asList(1, 2, 3);
        node.setExDomain(exDomain);

        Set<PyIntObj> result = node.genExVals();

        assertEquals(3, result.size());
        assertTrue(result.contains(new PyIntObj(1)));
        assertTrue(result.contains(new PyIntObj(2)));
        assertTrue(result.contains(new PyIntObj(3)));
    }

    /**
     * Tests genExVals with negative integers in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenExValsNeg() {
        List<Integer> exDomain = Arrays.asList(-3, -2, -1);
        node.setExDomain(exDomain);

        Set<PyIntObj> result = node.genExVals();

        assertEquals(3, result.size());
        assertTrue(result.contains(new PyIntObj(-3)));
        assertTrue(result.contains(new PyIntObj(-2)));
        assertTrue(result.contains(new PyIntObj(-1)));
    }

    /**
     * Tests genExVals with a mix of positive, negative, and zero values.
     * (1.0 pts)
     */
    @Test
    void testGenExValsMultiple() {
        List<Integer> exDomain = Arrays.asList(-5, -2, 0, 3, 7, 10);
        node.setExDomain(exDomain);

        Set<PyIntObj> result = node.genExVals();

        assertEquals(6, result.size());
        assertTrue(result.contains(new PyIntObj(-5)));
        assertTrue(result.contains(new PyIntObj(-2)));
        assertTrue(result.contains(new PyIntObj(0)));
        assertTrue(result.contains(new PyIntObj(3)));
        assertTrue(result.contains(new PyIntObj(7)));
        assertTrue(result.contains(new PyIntObj(10)));
    }

    // ==================== genRandVal Tests (1.4 pts total) ====================

    /**
     * Tests genRandVal with positive integers in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenRandValPos() {
        List<Integer> ranDomain = Arrays.asList(1, 2, 3, 4, 5);
        node.setRanDomain(ranDomain);

        Set<PyIntObj> validValues = new HashSet<>();
        for (int val : ranDomain) {
            validValues.add(new PyIntObj(val));
        }

        for (int i = 0; i < 100; i++) {
            PyIntObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result),
                "Random value " + result + " not in valid set");
        }
    }

    /**
     * Tests genRandVal with negative integers in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenRandValNeg() {
        List<Integer> ranDomain = Arrays.asList(-5, -4, -3, -2, -1);
        node.setRanDomain(ranDomain);

        Set<PyIntObj> validValues = new HashSet<>();
        for (int val : ranDomain) {
            validValues.add(new PyIntObj(val));
        }

        for (int i = 0; i < 100; i++) {
            PyIntObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result),
                "Random value " + result + " not in valid set");
        }
    }

    /**
     * Tests genRandVal with a mix of positive, negative, and zero values.
     * (1.0 pts)
     */
    @Test
    void testGenRandValMultiple() {
        List<Integer> ranDomain = Arrays.asList(-10, -5, 0, 5, 10);
        node.setRanDomain(ranDomain);

        Set<PyIntObj> validValues = new HashSet<>();
        for (int val : ranDomain) {
            validValues.add(new PyIntObj(val));
        }

        // Run multiple times to verify randomness and valid values
        for (int i = 0; i < 100; i++) {
            PyIntObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result),
                "Random value " + result + " not in valid set");
        }
    }
}

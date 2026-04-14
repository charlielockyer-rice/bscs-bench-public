package test.rice.node;

import main.rice.node.APyNode;
import main.rice.node.PyFloatNode;
import main.rice.obj.PyFloatObj;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyFloatNode class.
 * Tests the node that generates PyFloatObj values from domains.
 */
class PyFloatNodeTestPrivate {

    private PyFloatNode node;

    @BeforeEach
    void setUp() {
        node = new PyFloatNode();
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
     * Tests that getRightChild returns null for PyFloatNode (leaf node).
     * (0.3 pts)
     */
    @Test
    void testGetRightChild() {
        assertNull(node.getRightChild());
    }

    // ==================== genExVals Tests (1.4 pts total) ====================

    /**
     * Tests genExVals with positive floats in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenExValsPos() {
        List<Double> exDomain = Arrays.asList(1.5, 2.25, 3.75);
        node.setExDomain(exDomain);

        Set<PyFloatObj> result = node.genExVals();

        assertEquals(3, result.size());
        assertTrue(result.contains(new PyFloatObj(1.5)));
        assertTrue(result.contains(new PyFloatObj(2.25)));
        assertTrue(result.contains(new PyFloatObj(3.75)));
    }

    /**
     * Tests genExVals with negative floats in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenExValsNeg() {
        List<Double> exDomain = Arrays.asList(-3.5, -2.25, -1.125);
        node.setExDomain(exDomain);

        Set<PyFloatObj> result = node.genExVals();

        assertEquals(3, result.size());
        assertTrue(result.contains(new PyFloatObj(-3.5)));
        assertTrue(result.contains(new PyFloatObj(-2.25)));
        assertTrue(result.contains(new PyFloatObj(-1.125)));
    }

    /**
     * Tests genExVals with a mix of positive, negative, and zero values.
     * (1.0 pts)
     */
    @Test
    void testGenExValsMultiple() {
        List<Double> exDomain = Arrays.asList(-5.5, -2.25, 0.0, 3.75, 7.125, 10.5);
        node.setExDomain(exDomain);

        Set<PyFloatObj> result = node.genExVals();

        assertEquals(6, result.size());
        assertTrue(result.contains(new PyFloatObj(-5.5)));
        assertTrue(result.contains(new PyFloatObj(-2.25)));
        assertTrue(result.contains(new PyFloatObj(0.0)));
        assertTrue(result.contains(new PyFloatObj(3.75)));
        assertTrue(result.contains(new PyFloatObj(7.125)));
        assertTrue(result.contains(new PyFloatObj(10.5)));
    }

    // ==================== genRandVal Tests (1.4 pts total) ====================

    /**
     * Tests genRandVal with positive floats in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenRandValPos() {
        List<Double> ranDomain = Arrays.asList(1.5, 2.25, 3.75, 4.125, 5.5);
        node.setRanDomain(ranDomain);

        Set<PyFloatObj> validValues = new HashSet<>();
        for (double val : ranDomain) {
            validValues.add(new PyFloatObj(val));
        }

        for (int i = 0; i < 100; i++) {
            PyFloatObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result),
                "Random value " + result + " not in valid set");
        }
    }

    /**
     * Tests genRandVal with negative floats in the domain.
     * (0.2 pts)
     */
    @Test
    void testGenRandValNeg() {
        List<Double> ranDomain = Arrays.asList(-5.5, -4.25, -3.125, -2.75, -1.5);
        node.setRanDomain(ranDomain);

        Set<PyFloatObj> validValues = new HashSet<>();
        for (double val : ranDomain) {
            validValues.add(new PyFloatObj(val));
        }

        for (int i = 0; i < 100; i++) {
            PyFloatObj result = node.genRandVal();
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
        List<Double> ranDomain = Arrays.asList(-10.5, -5.25, 0.0, 5.75, 10.125);
        node.setRanDomain(ranDomain);

        Set<PyFloatObj> validValues = new HashSet<>();
        for (double val : ranDomain) {
            validValues.add(new PyFloatObj(val));
        }

        // Run multiple times to verify randomness and valid values
        for (int i = 0; i < 100; i++) {
            PyFloatObj result = node.genRandVal();
            assertNotNull(result);
            assertTrue(validValues.contains(result),
                "Random value " + result + " not in valid set");
        }
    }
}

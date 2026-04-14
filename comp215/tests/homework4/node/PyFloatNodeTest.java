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
class PyFloatNodeTest {

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
    void testGetExDomain() {
        List<Double> exDomain = Arrays.asList(-5.5, 0.0, 5.5, 10.25);
        node.setExDomain(exDomain);
        assertEquals(exDomain, node.getExDomain());
    }

    /**
     * Tests that getRanDomain returns the random domain that was set.
     * (0.3 pts)
     */
    @Test
    void testGetRanDomain() {
        List<Double> ranDomain = Arrays.asList(-10.5, -5.25, 0.0, 5.75, 10.125);
        node.setRanDomain(ranDomain);
        assertEquals(ranDomain, node.getRanDomain());
    }

    // ==================== Child Node Tests (0.6 pts total) ====================

    /**
     * Tests that getLeftChild returns null for PyFloatNode (leaf node).
     * (0.3 pts)
     */
}

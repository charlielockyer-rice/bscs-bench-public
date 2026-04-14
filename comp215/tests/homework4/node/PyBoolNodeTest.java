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
class PyBoolNodeTest {

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
    void testGetExDomain() {
        List<Integer> exDomain = Arrays.asList(0, 1);
        node.setExDomain(exDomain);
        assertEquals(exDomain, node.getExDomain());
    }

    /**
     * Tests that getRanDomain returns the random domain that was set.
     * (0.2 pts)
     */
    @Test
    void testGetRanDomain() {
        List<Integer> ranDomain = Arrays.asList(0, 1);
        node.setRanDomain(ranDomain);
        assertEquals(ranDomain, node.getRanDomain());
    }

    // ==================== Child Node Tests (0.6 pts total) ====================

    /**
     * Tests that getLeftChild returns null for PyBoolNode (leaf node).
     * (0.3 pts)
     */
}

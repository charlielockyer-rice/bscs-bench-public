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
class PyIntNodeTest {

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
    void testGetExDomain() {
        List<Integer> exDomain = Arrays.asList(-5, 0, 5, 10);
        node.setExDomain(exDomain);
        assertEquals(exDomain, node.getExDomain());
    }

    /**
     * Tests that getRanDomain returns the random domain that was set.
     * (0.3 pts)
     */
    @Test
    void testGetRanDomain() {
        List<Integer> ranDomain = Arrays.asList(-10, -5, 0, 5, 10);
        node.setRanDomain(ranDomain);
        assertEquals(ranDomain, node.getRanDomain());
    }

    // ==================== Child Node Tests (0.6 pts total) ====================

    /**
     * Tests that getLeftChild returns null for PyIntNode (leaf node).
     * (0.3 pts)
     */
}

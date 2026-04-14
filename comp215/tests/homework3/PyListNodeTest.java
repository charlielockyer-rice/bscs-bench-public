package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyListNode class.
 * Tests exhaustive and random value generation for Python list nodes.
 */
class PyListNodeTest {

    // Helper method to create a PyIntNode with given exhaustive domain
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetExDomain() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        List<Integer> exDomain = Arrays.asList(0, 1, 2);
        listNode.setExDomain(new ArrayList<>(exDomain));
        assertEquals(exDomain, listNode.getExDomain());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsTwoLenOne() {
        // Domain has two element options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(1));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        assertEquals(2, result.size());
        assertTrue(result.contains(new PyListObj<>(Arrays.asList(new PyIntObj(1)))));
        assertTrue(result.contains(new PyListObj<>(Arrays.asList(new PyIntObj(2)))));
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValFourLenTwo() {
        // Random domain has two element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(2));

        PyListObj<PyIntObj> result = listNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }
}

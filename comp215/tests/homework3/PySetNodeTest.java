package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PySetNode class.
 * Tests exhaustive and random value generation for Python set nodes.
 * Sets differ from lists in that they don't allow duplicates.
 */
class PySetNodeTest {

    // Helper method to create a PyIntNode with given domains
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetLeftChild() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        assertSame(intNode, setNode.getLeftChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsOneLenOne() {
        // Domain has one element option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(5), Arrays.asList(5));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(1));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        assertEquals(1, result.size());
        Set<PyIntObj> expected = new HashSet<>();
        expected.add(new PyIntObj(5));
        assertTrue(result.contains(new PySetObj<>(expected)));
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValEmpty() {
        // Random domain only contains 0
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(0));

        PySetObj<PyIntObj> result = setNode.genRandVal();
        assertEquals(0, result.getValue().size());
    }
}

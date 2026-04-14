package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyTupleNode class.
 * Tests exhaustive and random value generation for Python tuple nodes.
 * Tuples are similar to lists in that they allow duplicates and maintain order.
 */
class PyTupleNodeTest {

    // Helper method to create a PyIntNode with given domains
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
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        List<Integer> exDomain = Arrays.asList(0, 1, 2);
        tupleNode.setExDomain(new ArrayList<>(exDomain));
        assertEquals(exDomain, tupleNode.getExDomain());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsEmptyOnly() {
        // Domain contains only 0 (empty tuple)
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(0));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyTupleObj<>(new ArrayList<>())));
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValOneLenOne() {
        // Random domain has one option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(5));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(1));

        PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
        assertEquals(1, result.getValue().size());
        assertTrue(result.getValue().contains(new PyIntObj(5)));
    }
}

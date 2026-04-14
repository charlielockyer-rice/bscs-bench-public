package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for mixed/nested iterable PyNode structures.
 * Tests complex scenarios with different combinations of iterable types.
 */
class MixedIterablePyNodeTest {

    // Helper method to create a PyIntNode with given domains
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    /**
     * Test a complex nested structure: List of Tuples of Ints
     * This tests that different iterable types can be properly nested.
     */
    @Test
    void testListOfTuplesExhaustive() {
        // Create int node
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));

        // Create tuple node containing ints
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(1)); // Tuples of length 1

        // Create list node containing tuples
        PyListNode<PyTupleObj<PyIntObj>> listNode = new PyListNode<>(tupleNode);
        listNode.setExDomain(Arrays.asList(2)); // Lists of length 2

        Set<PyListObj<PyTupleObj<PyIntObj>>> result = listNode.genExVals();

        // Inner tuples: (1,) or (2,) = 2 options
        // Outer list has length 2, allowing duplicates
        // So: [[1], [1]], [[1], [2]], [[2], [1]], [[2], [2]] = 4 results
        assertEquals(4, result.size());

        // Verify structure
        for (PyListObj<PyTupleObj<PyIntObj>> list : result) {
            assertEquals(2, list.getValue().size());
            for (PyTupleObj<PyIntObj> tuple : list.getValue()) {
                assertEquals(1, tuple.getValue().size());
            }
        }
    }
}

package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for mixed/nested iterable PyNode structures.
 * Tests complex scenarios with different combinations of iterable types.
 */
class MixedIterablePyNodeTestPrivate {

    // Helper method to create a PyIntNode with given domains
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    /**
     * Test a complex nested structure with randomness: Dict with List values
     * This verifies that nested random generation works correctly.
     */
    @Test
    void testDictWithListValuesRandom() {
        // Create key node (ints)
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));

        // Create inner int node for list elements
        PyIntNode innerIntNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20, 30));

        // Create list node for values
        PyListNode<PyIntObj> listValNode = new PyListNode<>(innerIntNode);
        listValNode.setRanDomain(Arrays.asList(0, 1, 2)); // Lists of length 0-2

        // Create dict node
        PyDictNode<PyIntObj, PyListObj<PyIntObj>> dictNode = new PyDictNode<>(keyNode, listValNode);
        dictNode.setRanDomain(Arrays.asList(1, 2)); // Dicts of size 1-2

        // Generate multiple random values and verify structure
        for (int i = 0; i < 50; i++) {
            PyDictObj<PyIntObj, PyListObj<PyIntObj>> result = dictNode.genRandVal();

            // Dict size should be 1 or 2
            int dictSize = result.getValue().size();
            assertTrue(dictSize >= 1 && dictSize <= 2,
                "Dict size should be 1 or 2, got: " + dictSize);

            // All keys should be valid ints from domain
            for (PyIntObj key : result.getValue().keySet()) {
                int keyVal = key.getValue();
                assertTrue(keyVal >= 1 && keyVal <= 3,
                    "Key should be 1, 2, or 3, got: " + keyVal);
            }

            // All values should be lists of valid length
            for (PyListObj<PyIntObj> value : result.getValue().values()) {
                int listSize = value.getValue().size();
                assertTrue(listSize >= 0 && listSize <= 2,
                    "List size should be 0-2, got: " + listSize);
            }
        }
    }
}

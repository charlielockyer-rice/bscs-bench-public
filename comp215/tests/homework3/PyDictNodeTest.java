package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyDictNode class.
 * Tests exhaustive and random value generation for Python dictionary nodes.
 * PyDictNode is unique because it has both a left child (key generator) and
 * a right child (value generator).
 */
class PyDictNodeTest {

    // Helper method to create a PyIntNode with given domains
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetRightChild() {
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);

        assertSame(valNode, dictNode.getRightChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsOneLenOne() {
        // One key option, one value option, length 1
        PyIntNode keyNode = createIntNode(Arrays.asList(1), Arrays.asList(1));
        PyIntNode valNode = createIntNode(Arrays.asList(10), Arrays.asList(10));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(1));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        assertEquals(1, result.size());

        Map<PyIntObj, PyIntObj> expected = new HashMap<>();
        expected.put(new PyIntObj(1), new PyIntObj(10));
        assertTrue(result.contains(new PyDictObj<>(expected)));
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValOneLenOne() {
        // Random domain has one option for key and value, length 1
        PyIntNode keyNode = createIntNode(Arrays.asList(1), Arrays.asList(1));
        PyIntNode valNode = createIntNode(Arrays.asList(10), Arrays.asList(10));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(1));

        PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
        assertEquals(1, result.getValue().size());
        assertEquals(new PyIntObj(10), result.getValue().get(new PyIntObj(1)));
    }
}

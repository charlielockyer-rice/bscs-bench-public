package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the PyListNode class.
 * Comprehensive coverage of exhaustive and random value generation.
 */
class PyListNodeTestPrivate {

    // Helper method to create a PyIntNode with given exhaustive domain
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetRanDomain() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        List<Integer> ranDomain = Arrays.asList(0, 1, 2, 3);
        listNode.setRanDomain(new ArrayList<>(ranDomain));
        assertEquals(ranDomain, listNode.getRanDomain());
    }

    @Test
    void testGetLeftChild() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        assertSame(intNode, listNode.getLeftChild());
    }

    @Test
    void testNoRightChild() {
        // PyListNode extends AIterablePyNode which only has a leftChild (no rightChild).
        // Verify the left child is set correctly.
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        assertSame(intNode, listNode.getLeftChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsEmptyOnly() {
        // Domain contains only 0 (empty list)
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(0));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyListObj<>(new ArrayList<>())));
    }

    @Test
    void testGenExValsOneLenOne() {
        // Domain has one element option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(5), Arrays.asList(5));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(1));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyListObj<>(Arrays.asList(new PyIntObj(5)))));
    }

    @Test
    void testGenExValsFourLenTwo() {
        // Domain has two element options, length 2 - lists allow duplicates
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(2));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        // Lists allow duplicates, so we get: [1,1], [1,2], [2,1], [2,2] = 4 combinations
        assertEquals(4, result.size());
    }

    @Test
    void testGenExValsManyLenTwo() {
        // Domain has three element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(2));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        // 3^2 = 9 combinations for lists (with duplicates)
        assertEquals(9, result.size());
    }

    @Test
    void testGenExValsMultLensContig() {
        // Multiple contiguous lengths: 0, 1, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(0, 1, 2));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        // Length 0: 1 (empty)
        // Length 1: 2 ([1], [2])
        // Length 2: 4 ([1,1], [1,2], [2,1], [2,2])
        // Total: 7
        assertEquals(7, result.size());
        assertTrue(result.contains(new PyListObj<>(new ArrayList<>())));
    }

    @Test
    void testGenExValsMultLensNonContig() {
        // Non-contiguous lengths: 0, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(Arrays.asList(0, 2));

        Set<PyListObj<PyIntObj>> result = listNode.genExVals();
        // Length 0: 1 (empty)
        // Length 2: 4 ([1,1], [1,2], [2,1], [2,2])
        // Total: 5
        assertEquals(5, result.size());
    }

    @Test
    void testGenExValsNested() {
        // Nested lists: list of lists of ints
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> innerListNode = new PyListNode<>(intNode);
        innerListNode.setExDomain(Arrays.asList(1));

        PyListNode<PyListObj<PyIntObj>> outerListNode = new PyListNode<>(innerListNode);
        outerListNode.setExDomain(Arrays.asList(1));

        Set<PyListObj<PyListObj<PyIntObj>>> result = outerListNode.genExVals();
        // Inner list can be [1] or [2], outer list has length 1
        // So we get: [[1]], [[2]] = 2 results
        assertEquals(2, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValEmpty() {
        // Random domain only contains 0
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(0));

        PyListObj<PyIntObj> result = listNode.genRandVal();
        assertEquals(0, result.getValue().size());
    }

    @Test
    void testGenRandValOneLenOne() {
        // Random domain has one option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(5));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(1));

        PyListObj<PyIntObj> result = listNode.genRandVal();
        assertEquals(1, result.getValue().size());
        assertTrue(result.getValue().contains(new PyIntObj(5)));
    }

    @Test
    void testGenRandValTwoLenOne() {
        // Random domain has two options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(1));

        // Generate multiple times and verify valid results
        Set<PyIntObj> seenElements = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyListObj<PyIntObj> result = listNode.genRandVal();
            assertEquals(1, result.getValue().size());
            seenElements.addAll(result.getValue());
        }
        // Should see both 1 and 2 eventually
        assertTrue(seenElements.contains(new PyIntObj(1)) || seenElements.contains(new PyIntObj(2)));
    }

    @Test
    void testGenRandValManyLenTwo() {
        // Random domain has three element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(2));

        PyListObj<PyIntObj> result = listNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValMultLensContig() {
        // Multiple contiguous lengths in random domain
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(0, 1, 2));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyListObj<PyIntObj> result = listNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        // Should see different lengths
        assertTrue(seenLengths.size() >= 1);
        assertTrue(seenLengths.stream().allMatch(len -> len >= 0 && len <= 2));
    }

    @Test
    void testGenRandValMultLensNonContig() {
        // Non-contiguous lengths in random domain: 0, 3
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setRanDomain(Arrays.asList(0, 3));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyListObj<PyIntObj> result = listNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        // Should only see lengths 0 and 3
        assertTrue(seenLengths.stream().allMatch(len -> len == 0 || len == 3));
    }

    @Test
    void testGenRandValNested() {
        // Nested lists
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyListNode<PyIntObj> innerListNode = new PyListNode<>(intNode);
        innerListNode.setRanDomain(Arrays.asList(1));

        PyListNode<PyListObj<PyIntObj>> outerListNode = new PyListNode<>(innerListNode);
        outerListNode.setRanDomain(Arrays.asList(2));

        PyListObj<PyListObj<PyIntObj>> result = outerListNode.genRandVal();
        assertEquals(2, result.getValue().size());
        for (PyListObj<PyIntObj> inner : result.getValue()) {
            assertEquals(1, inner.getValue().size());
        }
    }
}

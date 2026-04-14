package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the PyTupleNode class.
 * Comprehensive coverage of exhaustive and random value generation for tuples.
 */
class PyTupleNodeTestPrivate {

    // Helper method to create a PyIntNode with given domains
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
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        List<Integer> ranDomain = Arrays.asList(0, 1, 2, 3);
        tupleNode.setRanDomain(new ArrayList<>(ranDomain));
        assertEquals(ranDomain, tupleNode.getRanDomain());
    }

    @Test
    void testGetLeftChild() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        assertSame(intNode, tupleNode.getLeftChild());
    }

    @Test
    void testNoRightChild() {
        // PyTupleNode extends AIterablePyNode which only has a leftChild (no rightChild).
        // Verify the left child is set correctly.
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        assertSame(intNode, tupleNode.getLeftChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsOneLenOne() {
        // Domain has one element option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(5), Arrays.asList(5));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(1));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyTupleObj<>(Arrays.asList(new PyIntObj(5)))));
    }

    @Test
    void testGenExValsTwoLenOne() {
        // Domain has two element options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(1));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        assertEquals(2, result.size());
        assertTrue(result.contains(new PyTupleObj<>(Arrays.asList(new PyIntObj(1)))));
        assertTrue(result.contains(new PyTupleObj<>(Arrays.asList(new PyIntObj(2)))));
    }

    @Test
    void testGenExValsFourLenTwo() {
        // Domain has two element options, length 2 - tuples allow duplicates
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(2));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        // Tuples allow duplicates: (1,1), (1,2), (2,1), (2,2) = 4 combinations
        assertEquals(4, result.size());
    }

    @Test
    void testGenExValsManyLenTwo() {
        // Domain has three element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(2));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        // 3^2 = 9 combinations for tuples (with duplicates)
        assertEquals(9, result.size());
    }

    @Test
    void testGenExValsMultLensContig() {
        // Multiple contiguous lengths: 0, 1, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(0, 1, 2));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        // Length 0: 1 (empty)
        // Length 1: 2 ((1,), (2,))
        // Length 2: 4 ((1,1), (1,2), (2,1), (2,2))
        // Total: 7
        assertEquals(7, result.size());
        assertTrue(result.contains(new PyTupleObj<>(new ArrayList<>())));
    }

    @Test
    void testGenExValsMultLensNonContig() {
        // Non-contiguous lengths: 0, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setExDomain(Arrays.asList(0, 2));

        Set<PyTupleObj<PyIntObj>> result = tupleNode.genExVals();
        // Length 0: 1 (empty)
        // Length 2: 4
        // Total: 5
        assertEquals(5, result.size());
    }

    @Test
    void testGenExValsNested() {
        // Nested tuples: tuple of tuples of ints
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> innerTupleNode = new PyTupleNode<>(intNode);
        innerTupleNode.setExDomain(Arrays.asList(1));

        PyTupleNode<PyTupleObj<PyIntObj>> outerTupleNode = new PyTupleNode<>(innerTupleNode);
        outerTupleNode.setExDomain(Arrays.asList(1));

        Set<PyTupleObj<PyTupleObj<PyIntObj>>> result = outerTupleNode.genExVals();
        // Inner tuple can be (1,) or (2,), outer tuple has length 1
        // So we get: ((1,),), ((2,),) = 2 results
        assertEquals(2, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValEmpty() {
        // Random domain only contains 0
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(0));

        PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
        assertEquals(0, result.getValue().size());
    }

    @Test
    void testGenRandValTwoLenOne() {
        // Random domain has two options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(1));

        Set<PyIntObj> seenElements = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
            assertEquals(1, result.getValue().size());
            seenElements.addAll(result.getValue());
        }
        assertTrue(seenElements.contains(new PyIntObj(1)) || seenElements.contains(new PyIntObj(2)));
    }

    @Test
    void testGenRandValFourLenTwo() {
        // Random domain has two element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(2));

        PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValManyLenTwo() {
        // Random domain has three element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(2));

        PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValMultLensContig() {
        // Multiple contiguous lengths in random domain
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(0, 1, 2));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.size() >= 1);
        assertTrue(seenLengths.stream().allMatch(len -> len >= 0 && len <= 2));
    }

    @Test
    void testGenRandValMultLensNonContig() {
        // Non-contiguous lengths in random domain: 0, 3
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> tupleNode = new PyTupleNode<>(intNode);
        tupleNode.setRanDomain(Arrays.asList(0, 3));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyTupleObj<PyIntObj> result = tupleNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.stream().allMatch(len -> len == 0 || len == 3));
    }

    @Test
    void testGenRandValNested() {
        // Nested tuples
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyTupleNode<PyIntObj> innerTupleNode = new PyTupleNode<>(intNode);
        innerTupleNode.setRanDomain(Arrays.asList(1));

        PyTupleNode<PyTupleObj<PyIntObj>> outerTupleNode = new PyTupleNode<>(innerTupleNode);
        outerTupleNode.setRanDomain(Arrays.asList(2));

        PyTupleObj<PyTupleObj<PyIntObj>> result = outerTupleNode.genRandVal();
        assertEquals(2, result.getValue().size());
        for (PyTupleObj<PyIntObj> inner : result.getValue()) {
            assertEquals(1, inner.getValue().size());
        }
    }
}

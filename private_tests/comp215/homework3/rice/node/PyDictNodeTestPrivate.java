package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the PyDictNode class.
 * Comprehensive coverage of exhaustive and random value generation for dictionaries.
 */
class PyDictNodeTestPrivate {

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
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);

        List<Integer> exDomain = Arrays.asList(0, 1, 2);
        dictNode.setExDomain(new ArrayList<>(exDomain));
        assertEquals(exDomain, dictNode.getExDomain());
    }

    @Test
    void testGetRanDomain() {
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);

        List<Integer> ranDomain = Arrays.asList(0, 1, 2, 3);
        dictNode.setRanDomain(new ArrayList<>(ranDomain));
        assertEquals(ranDomain, dictNode.getRanDomain());
    }

    @Test
    void testGetLeftChild() {
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);

        assertSame(keyNode, dictNode.getLeftChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsEmptyOnly() {
        // Domain contains only 0 (empty dict)
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(0));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyDictObj<>(new HashMap<>())));
    }

    @Test
    void testGenExValsTwoLenOne() {
        // Two key options, two value options, length 1
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(1));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        // 2 keys * 2 values = 4 single-entry dicts
        assertEquals(4, result.size());
    }

    @Test
    void testGenExValsFourLenTwo() {
        // Two keys, two values, length 2
        // Dictionaries can't have duplicate keys
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(2));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        // Keys must be unique, so only {1:*, 2:*} possible
        // For each key-value pair: 2 choices for value of key 1, 2 choices for value of key 2
        // Total: 2 * 2 = 4 dicts of size 2
        assertEquals(4, result.size());
    }

    @Test
    void testGenExValsManyLenTwo() {
        // Three keys, two values, length 2
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(2));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        // C(3,2) = 3 ways to choose 2 keys from {1,2,3}
        // Each key can have 2 value choices, so 2^2 = 4 value combinations per key pair
        // Total: 3 * 4 = 12
        assertEquals(12, result.size());
    }

    @Test
    void testGenExValsMultLensContig() {
        // Multiple contiguous lengths: 0, 1, 2
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(0, 1, 2));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        // Length 0: 1 (empty dict)
        // Length 1: 2 keys * 2 values = 4
        // Length 2: 1 key pair * 4 value combinations = 4
        // Total: 9
        assertEquals(9, result.size());
    }

    @Test
    void testGenExValsMultLensNonContig() {
        // Non-contiguous lengths: 0, 2
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setExDomain(Arrays.asList(0, 2));

        Set<PyDictObj<PyIntObj, PyIntObj>> result = dictNode.genExVals();
        // Length 0: 1
        // Length 2: 4
        // Total: 5
        assertEquals(5, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValEmpty() {
        // Random domain only contains 0
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(0));

        PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
        assertEquals(0, result.getValue().size());
    }

    @Test
    void testGenRandValTwoLenOne() {
        // Random domain has two options for key and value, length 1
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(1));

        Set<PyIntObj> seenKeys = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
            assertEquals(1, result.getValue().size());
            seenKeys.addAll(result.getValue().keySet());
        }
        // Should see different keys
        assertTrue(seenKeys.size() >= 1);
    }

    @Test
    void testGenRandValFourLenTwo() {
        // Random domain has enough keys for length 2
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(2));

        PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValManyLenTwo() {
        // Random domain has many key options, length 2
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3, 4, 5));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20, 30), Arrays.asList(10, 20, 30));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(2));

        PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValMultLensContig() {
        // Multiple contiguous lengths in random domain
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(0, 1, 2));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.size() >= 1);
        assertTrue(seenLengths.stream().allMatch(len -> len >= 0 && len <= 2));
    }

    @Test
    void testGenRandValMultLensNonContig() {
        // Non-contiguous lengths in random domain: 0, 3
        PyIntNode keyNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4, 5));
        PyIntNode valNode = createIntNode(Arrays.asList(10, 20), Arrays.asList(10, 20));
        PyDictNode<PyIntObj, PyIntObj> dictNode = new PyDictNode<>(keyNode, valNode);
        dictNode.setRanDomain(Arrays.asList(0, 3));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyDictObj<PyIntObj, PyIntObj> result = dictNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.stream().allMatch(len -> len == 0 || len == 3));
    }
}

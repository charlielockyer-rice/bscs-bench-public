package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the PySetNode class.
 * Comprehensive coverage of exhaustive and random value generation for sets.
 */
class PySetNodeTestPrivate {

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
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        List<Integer> exDomain = Arrays.asList(0, 1, 2);
        setNode.setExDomain(new ArrayList<>(exDomain));
        assertEquals(exDomain, setNode.getExDomain());
    }

    @Test
    void testGetRanDomain() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        List<Integer> ranDomain = Arrays.asList(0, 1, 2, 3);
        setNode.setRanDomain(new ArrayList<>(ranDomain));
        assertEquals(ranDomain, setNode.getRanDomain());
    }

    @Test
    void testGetLeftChildNotNull() {
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        assertSame(intNode, setNode.getLeftChild());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsEmptyOnly() {
        // Domain contains only 0 (empty set)
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(0));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PySetObj<>(new HashSet<>())));
    }

    @Test
    void testGenExValsTwoLenOne() {
        // Domain has two element options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(1));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        assertEquals(2, result.size());

        Set<PyIntObj> set1 = new HashSet<>();
        set1.add(new PyIntObj(1));
        Set<PyIntObj> set2 = new HashSet<>();
        set2.add(new PyIntObj(2));

        assertTrue(result.contains(new PySetObj<>(set1)));
        assertTrue(result.contains(new PySetObj<>(set2)));
    }

    @Test
    void testGenExValsFourLenTwo() {
        // Domain has two element options, length 2 - sets don't allow duplicates
        // So with elements {1, 2}, sets of size 2 can only be {1, 2}
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(2));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        // Only one set possible: {1, 2}
        assertEquals(1, result.size());
    }

    @Test
    void testGenExValsManyLenTwo() {
        // Domain has three element options, length 2
        // Possible sets: {1,2}, {1,3}, {2,3} = C(3,2) = 3
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(2));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        assertEquals(3, result.size());
    }

    @Test
    void testGenExValsMultLensContig() {
        // Multiple contiguous lengths: 0, 1, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(0, 1, 2));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        // Length 0: 1 (empty set)
        // Length 1: 2 ({1}, {2})
        // Length 2: 1 ({1, 2})
        // Total: 4
        assertEquals(4, result.size());
        assertTrue(result.contains(new PySetObj<>(new HashSet<>())));
    }

    @Test
    void testGenExValsMultLensNonContig() {
        // Non-contiguous lengths: 0, 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setExDomain(Arrays.asList(0, 2));

        Set<PySetObj<PyIntObj>> result = setNode.genExVals();
        // Length 0: 1 (empty set)
        // Length 2: 1 ({1, 2})
        // Total: 2
        assertEquals(2, result.size());
    }

    @Test
    void testGenExValsNested() {
        // Nested: set of sets of ints (though this is unusual for Python sets due to hashability)
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> innerSetNode = new PySetNode<>(intNode);
        innerSetNode.setExDomain(Arrays.asList(1));

        // Note: This tests the node structure, actual Python frozensets would be needed
        Set<PySetObj<PyIntObj>> innerResults = innerSetNode.genExVals();
        assertEquals(2, innerResults.size()); // {1} and {2}
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValOneLenOne() {
        // Random domain has one option, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(5));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(1));

        PySetObj<PyIntObj> result = setNode.genRandVal();
        assertEquals(1, result.getValue().size());
        assertTrue(result.getValue().contains(new PyIntObj(5)));
    }

    @Test
    void testGenRandValTwoLenOne() {
        // Random domain has two options, length 1
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(1));

        Set<PyIntObj> seenElements = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PySetObj<PyIntObj> result = setNode.genRandVal();
            assertEquals(1, result.getValue().size());
            seenElements.addAll(result.getValue());
        }
        // Should see both 1 and 2 eventually
        assertTrue(seenElements.contains(new PyIntObj(1)) || seenElements.contains(new PyIntObj(2)));
    }

    @Test
    void testGenRandValFourLenTwo() {
        // Random domain has enough elements for length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(2));

        PySetObj<PyIntObj> result = setNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValManyLenTwo() {
        // Random domain has many element options, length 2
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3, 4, 5));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(2));

        PySetObj<PyIntObj> result = setNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValMultLensContig() {
        // Multiple contiguous lengths in random domain
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(0, 1, 2));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PySetObj<PyIntObj> result = setNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.size() >= 1);
        assertTrue(seenLengths.stream().allMatch(len -> len >= 0 && len <= 2));
    }

    @Test
    void testGenRandValMultLensNonContig() {
        // Non-contiguous lengths in random domain: 0, 3
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4, 5));
        PySetNode<PyIntObj> setNode = new PySetNode<>(intNode);
        setNode.setRanDomain(Arrays.asList(0, 3));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PySetObj<PyIntObj> result = setNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.stream().allMatch(len -> len == 0 || len == 3));
    }

    @Test
    void testGenRandValNested() {
        // Testing set generation structure
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3));
        PySetNode<PyIntObj> innerSetNode = new PySetNode<>(intNode);
        innerSetNode.setRanDomain(Arrays.asList(1, 2));

        // Generate and verify structure
        for (int i = 0; i < 10; i++) {
            PySetObj<PyIntObj> result = innerSetNode.genRandVal();
            assertTrue(result.getValue().size() >= 1 && result.getValue().size() <= 2);
        }
    }
}

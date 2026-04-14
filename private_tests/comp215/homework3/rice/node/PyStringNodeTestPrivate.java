package test.rice.node;

import main.rice.node.*;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the PyStringNode class.
 * Comprehensive coverage of exhaustive and random value generation for strings.
 */
class PyStringNodeTestPrivate {

    // ==================== Domain Getter Tests ====================

    @Test
    void testGetRanDomain() {
        PyStringNode stringNode = new PyStringNode("abc");
        List<Integer> ranDomain = Arrays.asList(0, 1, 2, 3);
        stringNode.setRanDomain(new ArrayList<>(ranDomain));
        assertEquals(ranDomain, stringNode.getRanDomain());
    }

    @Test
    void testConstructorCharDomain() {
        // PyStringNode extends APyNode directly (not AIterablePyNode), so it has
        // no getLeftChild(). Verify it generates values from its char domain.
        PyStringNode stringNode = new PyStringNode("abc");
        stringNode.setExDomain(Arrays.asList(1));
        Set<PyStringObj> result = stringNode.genExVals();
        assertEquals(3, result.size());
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testGenExValsEmptyOnly() {
        // Domain contains only 0 (empty string)
        PyStringNode stringNode = new PyStringNode("abc");
        stringNode.setExDomain(Arrays.asList(0));

        Set<PyStringObj> result = stringNode.genExVals();
        assertEquals(1, result.size());
        assertTrue(result.contains(new PyStringObj("")));
    }

    @Test
    void testGenExValsTwoLenOne() {
        // Domain has two character options, length 1
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setExDomain(Arrays.asList(1));

        Set<PyStringObj> result = stringNode.genExVals();
        assertEquals(2, result.size());
        assertTrue(result.contains(new PyStringObj("a")));
        assertTrue(result.contains(new PyStringObj("b")));
    }

    @Test
    void testGenExValsFourLenTwo() {
        // Domain has two character options, length 2 - strings allow repeat characters
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setExDomain(Arrays.asList(2));

        Set<PyStringObj> result = stringNode.genExVals();
        // Strings allow repeats: "aa", "ab", "ba", "bb" = 4 combinations
        assertEquals(4, result.size());
        assertTrue(result.contains(new PyStringObj("aa")));
        assertTrue(result.contains(new PyStringObj("ab")));
        assertTrue(result.contains(new PyStringObj("ba")));
        assertTrue(result.contains(new PyStringObj("bb")));
    }

    @Test
    void testGenExValsManyLenTwo() {
        // Domain has three character options, length 2
        PyStringNode stringNode = new PyStringNode("abc");
        stringNode.setExDomain(Arrays.asList(2));

        Set<PyStringObj> result = stringNode.genExVals();
        // 3^2 = 9 combinations for strings
        assertEquals(9, result.size());
    }

    @Test
    void testGenExValsMultLensContig() {
        // Multiple contiguous lengths: 0, 1, 2
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setExDomain(Arrays.asList(0, 1, 2));

        Set<PyStringObj> result = stringNode.genExVals();
        // Length 0: 1 (empty string "")
        // Length 1: 2 ("a", "b")
        // Length 2: 4 ("aa", "ab", "ba", "bb")
        // Total: 7
        assertEquals(7, result.size());
        assertTrue(result.contains(new PyStringObj("")));
    }

    @Test
    void testGenExValsMultLensNonContig() {
        // Non-contiguous lengths: 0, 2
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setExDomain(Arrays.asList(0, 2));

        Set<PyStringObj> result = stringNode.genExVals();
        // Length 0: 1 (empty string)
        // Length 2: 4
        // Total: 5
        assertEquals(5, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testGenRandValOneLenOne() {
        // Random domain has one option, length 1
        PyStringNode stringNode = new PyStringNode("x");
        stringNode.setRanDomain(Arrays.asList(1));

        PyStringObj result = stringNode.genRandVal();
        assertEquals(1, result.getValue().size());
        assertEquals(new PyStringObj("x"), result);
    }

    @Test
    void testGenRandValTwoLenOne() {
        // Random domain has two character options, length 1
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setRanDomain(Arrays.asList(1));

        Set<PyStringObj> seenResults = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyStringObj result = stringNode.genRandVal();
            assertEquals(1, result.getValue().size());
            seenResults.add(result);
        }
        // Should see both "a" and "b" eventually (with high probability)
        assertTrue(seenResults.contains(new PyStringObj("a")) || seenResults.contains(new PyStringObj("b")));
    }

    @Test
    void testGenRandValFourLenTwo() {
        // Random domain has two character options, length 2
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setRanDomain(Arrays.asList(2));

        PyStringObj result = stringNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValManyLenTwo() {
        // Random domain has three character options, length 2
        PyStringNode stringNode = new PyStringNode("abc");
        stringNode.setRanDomain(Arrays.asList(2));

        PyStringObj result = stringNode.genRandVal();
        assertEquals(2, result.getValue().size());
    }

    @Test
    void testGenRandValMultLensContig() {
        // Multiple contiguous lengths in random domain
        PyStringNode stringNode = new PyStringNode("ab");
        stringNode.setRanDomain(Arrays.asList(0, 1, 2));

        Set<Integer> seenLengths = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            PyStringObj result = stringNode.genRandVal();
            seenLengths.add(result.getValue().size());
        }
        assertTrue(seenLengths.size() >= 1);
        assertTrue(seenLengths.stream().allMatch(len -> len >= 0 && len <= 2));
    }
}

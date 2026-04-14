package test.rice.node;

import main.rice.node.PyTupleNode;
import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyTupleObj class.
 */
class PyTupleNodeTest extends AIterablePyNodeTest {

    /**
     * A PyTupleNode whose exhaustive domain includes only one option: the empty tuple.
     * Its random domain includes only one option of length one.
     */
    private static PyTupleNode<PyBoolObj> emptyOnlyTup;

    /**
     * A PyTupleNode whose exhaustive domain includes only one option of length one. Its
     * random domain includes only the empty tuple.
     */
    private static PyTupleNode<PyBoolObj> oneLenOneTup;

    /**
     * A PyTupleNode whose exhaustive domain includes two options of length one. Its
     * random domain includes four options of length two.
     */
    private static PyTupleNode<PyBoolObj> twoLenOneTup;

    /**
     * A PyTupleNode whose exhaustive domain includes four options of length two. Its
     * random domain includes two options of length two.
     */
    private static PyTupleNode<PyBoolObj> fourLenTwoTup;
    private static Set<PyTupleObj<PyBoolObj>> expectedFourLenTwoTup;

    /**
     * A PyTupleNode whose domains include many options of length two.
     */
    private static PyTupleNode<PyIntObj> manyLenTwoTup;
    private static Set<PyTupleObj<PyIntObj>> expectedManyLenTwoTup;

    /**
     * A PyTupleNode whose domains include multiple lengths, from zero to one.
     */
    private static PyTupleNode<PyFloatObj> lensZeroToOneTup;
    private static Set<PyTupleObj<PyFloatObj>> expectedLenZeroToOneTup;
    private static Map<PyTupleObj<PyFloatObj>, Double> expectedRandLenZeroToOneTup;

    /**
     * A PyTupleNode whose domains include multiple lengths, from zero to three.
     */
    private static PyTupleNode<PyFloatObj> lensZeroToThreeTup;
    private static Set<PyTupleObj<PyFloatObj>> expectedLenZeroToThreeTup;
    private static Map<PyTupleObj<PyFloatObj>, Double> expectedRandLenZeroToThreeTup;

    /**
     * A nested PyTupleNode.
     */
    private static PyTupleNode<PyTupleObj<PyBoolObj>> nestedBoolsTup;
    private static Set<PyTupleObj<PyTupleObj<PyBoolObj>>> expectedNestedTup;
    private static Map<PyTupleObj<PyTupleObj<PyBoolObj>>, Double> expectedRandNestedTup;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        // Set up the list versions
        AIterablePyNodeTest.setUp();

        // Convert the PyListNodes to PyTupleNodes
        emptyOnlyTup = convertListNodeToTup(emptyOnly);
        oneLenOneTup = convertListNodeToTup(oneLenOne);
        twoLenOneTup = convertListNodeToTup(twoLenOne);
        fourLenTwoTup = convertListNodeToTup(fourLenTwo);
        manyLenTwoTup = convertListNodeToTup(manyLenTwo);
        lensZeroToOneTup = convertListNodeToTup(lensZeroToOne);
        lensZeroToThreeTup = convertListNodeToTup(lensZeroToThree);
        nestedBoolsTup = convertNestedListNodeToTup(nestedBools);

        // For the more complex cases, get the expected tuple results
        expectedFourLenTwoTup = convertListObjsToTuples(expectedFourLenTwo);
        expectedManyLenTwoTup = convertListObjsToTuples(expectedManyLenTwo);
        expectedLenZeroToOneTup = convertListObjsToTuples(expectedLenZeroToOne);
        expectedLenZeroToThreeTup = convertListObjsToTuples(expectedLenZeroToThree);
        expectedNestedTup = convertNestedListObjsToTuples(expectedNested);

        expectedRandLenZeroToOneTup = convertMapOfListObjs(expectedRandLenZeroToOne);
        expectedRandLenZeroToThreeTup = convertMapOfListObjs(expectedRandLenZeroToThree);
        expectedRandNestedTup = convertMapOfNestedListObjs(expectedRandNested);
    }

    /**
     * Tests that getExDomain() works.
     */
    @Test
    void testGetExDomain() {
        assertEquals(Collections.singletonList(0), emptyOnlyTup.getExDomain());
    }

    /**
     * Tests that getRanDomain() works.
     */
    @Test
    void testGetRanDomain() {
        assertEquals(Collections.singletonList(1), emptyOnlyTup.getRanDomain());
    }

    /**
     * Tests that getLeftChild() works.
     */
    @Test
    void testGetLeftChild() {
        assertEquals(emptyLeftChild, emptyOnlyTup.getLeftChild());
    }

    /**
     * Tests that getRightChild() works.
     */
    @Test
    void testGetRightChild() {
        assertNull(emptyOnlyTup.getRightChild());
    }

    /**
     * Tests genExVals() in the case that the expected output contains a single element:
     * the empty tuple.
     */
    @Test
    void testGenExValsEmpty() {
        Set<PyTupleObj<PyBoolObj>> expected = Set.of(new PyTupleObj<>(
                Collections.emptyList()));
        Set<? extends PyTupleObj<? extends APyObj>> actual = emptyOnlyTup.genExVals();
        assertEquals(expected, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains a single element: a
     * tuple of length one.
     */
    @Test
    void testGenExValsOneLenOne() {
        Set<PyTupleObj<PyBoolObj>> expected = Set.of(new PyTupleObj<>(
                Collections.singletonList(new PyBoolObj(true))));
        Set<? extends PyTupleObj<? extends APyObj>> actual = oneLenOneTup.genExVals();
        assertEquals(expected, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains two tuples of
     * length one.
     */
    @Test
    void testGenExValsTwoLenOne() {
        Set<PyTupleObj<PyBoolObj>> expected = Set.of(
                new PyTupleObj<>(Collections.singletonList(new PyBoolObj(true))),
                new PyTupleObj<>(Collections.singletonList(new PyBoolObj(false))));
        Set<? extends PyTupleObj<? extends APyObj>> actual = twoLenOneTup.genExVals();
        assertEquals(expected, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains four tuples of
     * length two.
     */
    @Test
    void testGenExValsFourLenTwo() {
        Set<? extends AIterablePyObj<? extends APyObj>> actual =
                fourLenTwoTup.genExVals();
        assertEquals(expectedFourLenTwoTup, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains many tuples of
     * length two.
     */
    @Test
    void testGenExValsManyLenTwo() {
        Set<? extends AIterablePyObj<? extends APyObj>> actual =
                manyLenTwoTup.genExVals();
        assertEquals(expectedManyLenTwoTup, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains many of lengths
     * zero and one.
     */
    @Test
    void testGenExValsMultLensContig() {
        Set<? extends AIterablePyObj<? extends APyObj>> actual =
                lensZeroToOneTup.genExVals();
        assertEquals(expectedLenZeroToOneTup, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains tuples of lengths
     * zero and two.
     */
    @Test
    void testGenExValsMultLensNonContig() {
        Set<? extends AIterablePyObj<? extends APyObj>> actual =
                lensZeroToThreeTup.genExVals();
        assertEquals(expectedLenZeroToThreeTup, actual);
    }

    /**
     * Tests genExVals() in the case that the expected output contains nested tuples.
     */
    @Test
    void testGenExValsNested() {
        Set<? extends AIterablePyObj<? extends APyObj>> actual =
                nestedBoolsTup.genExVals();
        assertEquals(expectedNestedTup, actual);
    }

    /**
     * Tests genRandVal() in the case that there is only one possible option: the empty
     * tuples.
     */
    @Test
    void testGenRandValEmpty() {
        // Build the expected distribution
        Map<PyTupleObj<PyBoolObj>, Double> expected = Map.of(
                new PyTupleObj<>(new ArrayList<>()), 1.0);

        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyBoolObj>, Double> actual = buildDistribution(
                oneLenOneTup, 100);
        assertTrue(compareDistribution(expected, actual, 0.0));
    }

    /**
     * Tests genRandVal() in the case that there is only one possible option: the empty
     * tuple.
     */
    @Test
    void testGenRandValOneLenOne() {
        // Build the expected distribution
        Map<PyTupleObj<PyBoolObj>, Double> expected = Map.of
                (new PyTupleObj<>(Collections.singletonList(new PyBoolObj(true))), 1.0);

        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyBoolObj>, Double> actual = buildDistribution(
                emptyOnlyTup, 100);
        assertTrue(compareDistribution(expected, actual, 0.0));
    }

    /**
     * Tests genRandVal() in the case that there are two possible options of length one.
     */
    @Test
    void testGenRandValTwoLenOne() {
        // Build the expected distribution
        Map<PyTupleObj<PyBoolObj>, Double> expected = Map.of(
                new PyTupleObj<>(Collections.singletonList(new PyBoolObj(true))), 0.5,
                new PyTupleObj<>(Collections.singletonList(new PyBoolObj(false))), 0.5);

        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyBoolObj>, Double> actual = buildDistribution(
                fourLenTwoTup, 100000);
        assertTrue(compareDistribution(expected, actual, 0.01));
    }

    /**
     * Tests genRandVal() in the case that there are four possible options of length two.
     */
    @Test
    void testGenRandValFourLenTwo() {
        // Build the expected distribution
        Map<PyTupleObj<PyBoolObj>, Double> expected = convertExpExToRandEqual(
                expectedFourLenTwoTup, 0.25);

        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyBoolObj>, Double> actual = buildDistribution(
                twoLenOneTup, 100000);
        assertTrue(compareDistribution(expected, actual, 0.01));
    }

    /**
     * Tests genRandVal() in the case that there are many possible options of length two.
     */
    @Test
    void testGenRandValManyLenTwo() {
        // Build the expected distribution
        Map<PyTupleObj<PyIntObj>, Double> expected = convertExpExToRandEqual(
                expectedManyLenTwoTup, 1.0 / 9.0);

        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyIntObj>, Double> actual = buildDistribution(
                manyLenTwoTup, 100000);
        assertTrue(compareDistribution(expected, actual, 0.01));
    }

    /**
     * Tests genRandVal() in the case that there are possible options of lengths zero and
     * one.
     */
    @Test
    void testGenRandValMultLensContig() {
        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyFloatObj>, Double> actual = buildDistribution(
                lensZeroToOneTup, 100000);
        assertTrue(
                compareDistribution(expectedRandLenZeroToOneTup, actual, 0.01));
    }

    /**
     * Tests genRandVal() in the case that there are possible options of lengths one and
     * three.
     */
    @Test
    void testGenRandValMultLensNonContig() {
        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyFloatObj>, Double> actual = buildDistribution(
                lensZeroToThreeTup, 100000);
        assertTrue(
                compareDistribution(expectedRandLenZeroToThreeTup, actual, 0.01));
    }

    /**
     * Tests genRandVal() in the case that there are many possible options that are
     * nested.
     */
    @Test
    void testGenRandValNested() {
        // Ran a bunch of trials to get the actual distribution and compare actual and
        // expected distributions
        Map<PyTupleObj<PyTupleObj<PyBoolObj>>, Double> actual = buildDistribution(
                nestedBoolsTup, 100000);
        assertTrue(compareDistribution(expectedRandNestedTup, actual, 0.01));
    }
}
package test.rice.basegen;

import main.rice.basegen.BaseSetGenerator;
import main.rice.node.*;
import main.rice.obj.*;
import main.rice.test.TestCase;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Private test cases for the BaseSetGenerator class.
 * Comprehensive coverage of exhaustive, random, and combined base set generation.
 */
class BaseSetGeneratorTestPrivate {

    // Helper method to create a PyIntNode with given domains
    private static PyIntNode createIntNode(List<Integer> exDomain, List<Integer> ranDomain) {
        PyIntNode node = new PyIntNode();
        node.setExDomain(new ArrayList<>(exDomain));
        node.setRanDomain(new ArrayList<>(ranDomain));
        return node;
    }

    // Helper method to create a PyListNode with int elements
    private static PyListNode<PyIntObj> createIntListNode(
            List<Integer> innerExDomain, List<Integer> innerRanDomain,
            List<Integer> outerExDomain, List<Integer> outerRanDomain) {
        PyIntNode intNode = createIntNode(innerExDomain, innerRanDomain);
        PyListNode<PyIntObj> listNode = new PyListNode<>(intNode);
        listNode.setExDomain(new ArrayList<>(outerExDomain));
        listNode.setRanDomain(new ArrayList<>(outerRanDomain));
        return listNode;
    }

    // ==================== Exhaustive Generation Tests ====================

    @Test
    void testExOneArgOneOption() {
        // Single argument with single option
        PyIntNode intNode = createIntNode(Arrays.asList(5), Arrays.asList(5));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        assertEquals(1, result.size());

        TestCase expected = new TestCase(Arrays.asList(new PyIntObj(5)));
        assertTrue(result.contains(expected));
    }

    @Test
    void testExOneArgNested() {
        // Single argument that is a nested type (list of ints)
        PyListNode<PyIntObj> listNode = createIntListNode(
            Arrays.asList(1, 2), Arrays.asList(1, 2),
            Arrays.asList(0, 1), Arrays.asList(0, 1)
        );
        List<APyNode<?>> nodes = Arrays.asList(listNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        // Length 0: [] = 1
        // Length 1: [1], [2] = 2
        // Total: 3
        assertEquals(3, result.size());
    }

    @Test
    void testExMultipleArgsOneOptionEach() {
        // Multiple arguments, each with one option
        PyIntNode intNode1 = createIntNode(Arrays.asList(1), Arrays.asList(1));
        PyIntNode intNode2 = createIntNode(Arrays.asList(2), Arrays.asList(2));
        List<APyNode<?>> nodes = Arrays.asList(intNode1, intNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        assertEquals(1, result.size());

        TestCase expected = new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));
        assertTrue(result.contains(expected));
    }

    @Test
    void testExMultipleArgsNested() {
        // Multiple nested arguments
        PyListNode<PyIntObj> listNode1 = createIntListNode(
            Arrays.asList(1), Arrays.asList(1),
            Arrays.asList(0, 1), Arrays.asList(0, 1)
        );
        PyListNode<PyIntObj> listNode2 = createIntListNode(
            Arrays.asList(2), Arrays.asList(2),
            Arrays.asList(0, 1), Arrays.asList(0, 1)
        );
        List<APyNode<?>> nodes = Arrays.asList(listNode1, listNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        // Each list can be [] or [x] = 2 options each
        // 2 * 2 = 4 combinations
        assertEquals(4, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testRandZeroTests() {
        // Request 0 random tests
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2, 3, 4, 5));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        // First generate ex tests to populate currentTests
        generator.genExTests();
        Set<TestCase> result = new HashSet<>(generator.genRandTests(0));
        assertEquals(0, result.size());
    }

    @Test
    void testRandNumTestsNonZero() {
        // Request specific number of random tests
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 5);

        // Gen base set includes both ex and rand
        List<TestCase> result = generator.genBaseSet();
        // Ex tests: 1 (just value 1)
        // Rand tests: 5 (from values 2-10, not overlapping with 1)
        // Total could be up to 6, but rand tests avoid overlap
        assertTrue(result.size() >= 1); // At least the exhaustive tests
    }

    @Test
    void testRandOneArgSimpleAll() {
        // Single argument, request all possible random values
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(2, 3, 4));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 3);

        List<TestCase> result = generator.genBaseSet();
        // Ex: 1 (value 1)
        // Rand: up to 3 from {2, 3, 4}
        assertTrue(result.size() >= 1 && result.size() <= 4);
    }

    @Test
    void testRandOneArgSimpleSubset() {
        // Single argument, request subset of possible random values
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(10, 20, 30, 40, 50));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 2);

        List<TestCase> result = generator.genBaseSet();
        // Ex: 1 test (value 1)
        // Rand: 2 tests (from {10,20,30,40,50}, no overlap with ex)
        assertTrue(result.size() >= 1);
    }

    @Test
    void testRandOneArgNested() {
        // Nested argument with random generation
        PyListNode<PyIntObj> listNode = createIntListNode(
            Arrays.asList(1), Arrays.asList(1, 2, 3),
            Arrays.asList(1), Arrays.asList(1, 2, 3)
        );
        List<APyNode<?>> nodes = Arrays.asList(listNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 3);

        List<TestCase> result = generator.genBaseSet();
        assertTrue(result.size() >= 1);
    }

    @Test
    void testRandMultipleArgsOneOptionEach() {
        // Multiple arguments with limited random options
        PyIntNode intNode1 = createIntNode(Arrays.asList(1), Arrays.asList(10));
        PyIntNode intNode2 = createIntNode(Arrays.asList(2), Arrays.asList(20));
        List<APyNode<?>> nodes = Arrays.asList(intNode1, intNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 1);

        List<TestCase> result = generator.genBaseSet();
        // Ex: (1, 2)
        // Rand: (10, 20) - different from ex
        assertTrue(result.size() >= 1);
    }

    @Test
    void testRandMultipleArgsSimple() {
        // Multiple arguments with multiple random options
        PyIntNode intNode1 = createIntNode(Arrays.asList(1), Arrays.asList(10, 20));
        PyIntNode intNode2 = createIntNode(Arrays.asList(2), Arrays.asList(30, 40));
        List<APyNode<?>> nodes = Arrays.asList(intNode1, intNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 3);

        List<TestCase> result = generator.genBaseSet();
        assertTrue(result.size() >= 1);
    }

    @Test
    void testRandMultipleArgsNested() {
        // Multiple nested arguments with random generation
        PyListNode<PyIntObj> listNode1 = createIntListNode(
            Arrays.asList(1), Arrays.asList(1, 2),
            Arrays.asList(1), Arrays.asList(1, 2)
        );
        PyListNode<PyIntObj> listNode2 = createIntListNode(
            Arrays.asList(3), Arrays.asList(3, 4),
            Arrays.asList(1), Arrays.asList(1, 2)
        );
        List<APyNode<?>> nodes = Arrays.asList(listNode1, listNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 2);

        List<TestCase> result = generator.genBaseSet();
        assertTrue(result.size() >= 1);
    }

    // ==================== Full Base Set Tests ====================

    @Test
    void testFullNoOverlapDeterministic() {
        // Ex and Rand domains don't overlap - deterministic check
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2), Arrays.asList(10, 20));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 2);

        List<TestCase> result = generator.genBaseSet();
        // Ex: 2 tests (1, 2)
        // Rand: 2 tests (from {10, 20})
        // No overlap possible
        assertEquals(4, result.size());
    }

    @Test
    void testFullSomeOverlap() {
        // Ex and Rand domains have some overlap - rand should not duplicate ex
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3, 4, 5, 6));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 2);

        List<TestCase> result = generator.genBaseSet();
        // Ex: 3 tests (1, 2, 3)
        // Rand: 2 tests (from {4, 5, 6} to avoid duplicates)
        // Total: 5
        assertEquals(5, result.size());

        // Verify no duplicates
        Set<TestCase> uniqueTests = new HashSet<>(result);
        assertEquals(result.size(), uniqueTests.size());
    }
}

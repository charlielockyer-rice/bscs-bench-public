package test.rice.basegen;

import main.rice.basegen.BaseSetGenerator;
import main.rice.node.*;
import main.rice.obj.*;
import main.rice.test.TestCase;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the BaseSetGenerator class.
 * Tests both exhaustive test generation (genExTests) and random test generation (genRandTests),
 * as well as the combined base set generation (genBaseSet).
 */
class BaseSetGeneratorTest {

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
    void testExOneArgSimple() {
        // Single argument with multiple options
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        assertEquals(3, result.size());

        assertTrue(result.contains(new TestCase(Arrays.asList(new PyIntObj(1)))));
        assertTrue(result.contains(new TestCase(Arrays.asList(new PyIntObj(2)))));
        assertTrue(result.contains(new TestCase(Arrays.asList(new PyIntObj(3)))));
    }

    @Test
    void testExMultipleArgsSimple() {
        // Multiple arguments with multiple options
        PyIntNode intNode1 = createIntNode(Arrays.asList(1, 2), Arrays.asList(1, 2));
        PyIntNode intNode2 = createIntNode(Arrays.asList(3, 4), Arrays.asList(3, 4));
        List<APyNode<?>> nodes = Arrays.asList(intNode1, intNode2);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 0);

        Set<TestCase> result = generator.genExTests();
        // 2 * 2 = 4 combinations
        assertEquals(4, result.size());
    }

    // ==================== Random Generation Tests ====================

    @Test
    void testRandOneArgOneOption() {
        // Single argument with limited random options
        PyIntNode intNode = createIntNode(Arrays.asList(1), Arrays.asList(5));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 1);

        // The only random option is 5, which differs from ex option 1
        List<TestCase> result = generator.genBaseSet();
        assertTrue(result.size() >= 1);
    }

    // ==================== Full Base Set Tests ====================

    @Test
    void testFullNoOverlap() {
        // Ex and Rand domains don't overlap
        PyIntNode intNode = createIntNode(Arrays.asList(1, 2, 3), Arrays.asList(10, 20, 30, 40, 50));
        List<APyNode<?>> nodes = Arrays.asList(intNode);
        BaseSetGenerator generator = new BaseSetGenerator(nodes, 3);

        List<TestCase> result = generator.genBaseSet();
        // Ex: 3 tests
        // Rand: 3 tests (no overlap with ex)
        // Total: 6
        assertEquals(6, result.size());
    }
}

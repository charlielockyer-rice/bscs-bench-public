package test.rice.concisegen;

import main.rice.concisegen.ConciseSetGenerator;
import main.rice.obj.APyObj;
import main.rice.obj.PyIntObj;
import main.rice.obj.PyFloatObj;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the ConciseSetGenerator class.
 * Tests the setCover method which finds a minimal set of test cases
 * that covers all failing implementations.
 */
class ConciseSetGeneratorTestPrivate {

    // ==================== Helper Methods ====================

    /**
     * Creates a TestCase with the given integer arguments.
     */
    private TestCase createTestCase(int... args) {
        List<APyObj> objArgs = new ArrayList<>();
        for (int arg : args) {
            objArgs.add(new PyIntObj(arg));
        }
        return new TestCase(objArgs);
    }

    /**
     * Creates a TestCase with the given float arguments.
     */
    private TestCase createFloatTestCase(double... args) {
        List<APyObj> objArgs = new ArrayList<>();
        for (double arg : args) {
            objArgs.add(new PyFloatObj(arg));
        }
        return new TestCase(objArgs);
    }

    /**
     * Creates a deep copy of a list of test cases.
     */
    private List<TestCase> copyAllCases(List<TestCase> allCases) {
        List<TestCase> copy = new ArrayList<>();
        for (TestCase tc : allCases) {
            List<APyObj> argsCopy = new ArrayList<>(tc.getArgs());
            copy.add(new TestCase(argsCopy));
        }
        return copy;
    }

    /**
     * Creates a deep copy of caseToFiles.
     */
    private List<Set<Integer>> copyCaseToFiles(List<Set<Integer>> caseToFiles) {
        List<Set<Integer>> copy = new ArrayList<>();
        for (Set<Integer> set : caseToFiles) {
            copy.add(new HashSet<>(set));
        }
        return copy;
    }

    /**
     * Creates a deep copy of wrongSet.
     */
    private Set<Integer> copyWrongSet(Set<Integer> wrongSet) {
        return new HashSet<>(wrongSet);
    }

    // ==================== Mutation Tests (4.0 pts total) ====================

    /**
     * Tests that setCover does not mutate the allCases list.
     * (1.0 pt)
     */
    @Test
    void testDoesNotMutateWrongSet() {
        // Setup: 2 test cases, 2 files
        List<TestCase> allCases = Arrays.asList(
            createTestCase(1),
            createTestCase(2)
        );
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0)),
            new HashSet<>(Arrays.asList(1))
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1));

        // Make copy for comparison
        Set<Integer> originalWrongSet = copyWrongSet(wrongSet);

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        ConciseSetGenerator.setCover(results);

        // Verify wrongSet was not mutated
        assertEquals(originalWrongSet, wrongSet);
    }

    // ==================== Need All Tests (4.0 pts total) ====================

    /**
     * Tests the case where all test cases are needed (each catches exactly one unique file).
     * (1.0 pt)
     */
    @Test
    void testNeedAll() {
        // 3 test cases, each catches exactly one unique file
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);
        TestCase tc3 = createTestCase(3);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0)),  // tc1 catches file 0
            new HashSet<>(Arrays.asList(1)),  // tc2 catches file 1
            new HashSet<>(Arrays.asList(2))   // tc3 catches file 2
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // All 3 test cases should be in the cover
        assertEquals(3, cover.size());
        assertTrue(cover.contains(tc1));
        assertTrue(cover.contains(tc2));
        assertTrue(cover.contains(tc3));
    }

    /**
     * Tests the case where all test cases are needed with non-integer (float) test cases.
     * (2.0 pts)
     */
    @Test
    void testNotIntegersNeedAll() {
        // 3 float test cases, each catches exactly one unique file
        TestCase tc1 = createFloatTestCase(1.5);
        TestCase tc2 = createFloatTestCase(2.5);
        TestCase tc3 = createFloatTestCase(3.5);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0)),
            new HashSet<>(Arrays.asList(1)),
            new HashSet<>(Arrays.asList(2))
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        assertEquals(3, cover.size());
        assertTrue(cover.contains(tc1));
        assertTrue(cover.contains(tc2));
        assertTrue(cover.contains(tc3));
    }

    // ==================== Need One Tests (2.0 pts total) ====================

    /**
     * Tests the case where only one test case is needed and all files fail all tests.
     * (1.0 pt)
     */
    @Test
    void testNeedOneAllWrong() {
        // All test cases catch all files - only one needed
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);
        TestCase tc3 = createTestCase(3);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        Set<Integer> allFiles = new HashSet<>(Arrays.asList(0, 1, 2));
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(allFiles),  // tc1 catches all
            new HashSet<>(allFiles),  // tc2 catches all
            new HashSet<>(allFiles)   // tc3 catches all
        );
        Set<Integer> wrongSet = new HashSet<>(allFiles);

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Only one test case should be needed
        assertEquals(1, cover.size());
    }

    /**
     * Tests the case where only one test case is needed but not all files fail all tests.
     * (1.0 pt)
     */
    @Test
    void testNeedOneSomeWrong() {
        // One test catches all failing files
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);

        List<TestCase> allCases = Arrays.asList(tc1, tc2);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1, 2)),  // tc1 catches files 0, 1, 2
            new HashSet<>(Arrays.asList(0))         // tc2 catches only file 0
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Only tc1 should be needed (catches all)
        assertEquals(1, cover.size());
        assertTrue(cover.contains(tc1));
    }

    // ==================== Overlap/Coverage Tests (4.0 pts total) ====================

    /**
     * Tests that each file in wrongSet fails at least one test in the cover.
     * (1.0 pt)
     */
    @Test
    void testEachFileFailsOneTest() {
        // Setup with overlapping coverage
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);
        TestCase tc3 = createTestCase(3);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1)),    // tc1 catches 0, 1
            new HashSet<>(Arrays.asList(1, 2)),    // tc2 catches 1, 2
            new HashSet<>(Arrays.asList(2, 3))     // tc3 catches 2, 3
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2, 3));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Verify that all files in wrongSet are covered
        Set<Integer> coveredFiles = new HashSet<>();
        for (TestCase tc : cover) {
            int idx = allCases.indexOf(tc);
            coveredFiles.addAll(caseToFiles.get(idx));
        }
        assertTrue(coveredFiles.containsAll(wrongSet));
    }

    /**
     * Tests overlap case where greedy algorithm finds the optimal solution.
     * (1.0 pt)
     */
    @Test
    void testOverlap() {
        // tc1 catches many, tc2 and tc3 catch few overlapping
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);
        TestCase tc3 = createTestCase(3);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1, 2)),  // tc1 catches 0, 1, 2
            new HashSet<>(Arrays.asList(3)),        // tc2 catches 3
            new HashSet<>(Arrays.asList(3))         // tc3 catches 3
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2, 3));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Should need tc1 (for 0,1,2) and one of tc2 or tc3 (for 3)
        assertEquals(2, cover.size());
        assertTrue(cover.contains(tc1));
    }

    /**
     * Tests overlap case with non-integer (float) test cases.
     * (2.0 pts)
     */
    @Test
    void testNotIntegersOverlap() {
        TestCase tc1 = createFloatTestCase(1.1, 2.2);
        TestCase tc2 = createFloatTestCase(3.3, 4.4);
        TestCase tc3 = createFloatTestCase(5.5, 6.6);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1)),    // tc1 catches 0, 1
            new HashSet<>(Arrays.asList(1, 2)),    // tc2 catches 1, 2
            new HashSet<>(Arrays.asList(2, 3))     // tc3 catches 2, 3
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2, 3));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // All files should be covered
        Set<Integer> coveredFiles = new HashSet<>();
        for (TestCase tc : cover) {
            int idx = allCases.indexOf(tc);
            coveredFiles.addAll(caseToFiles.get(idx));
        }
        assertTrue(coveredFiles.containsAll(wrongSet));
    }

    /**
     * Tests a scenario where the greedy algorithm does not find the optimal solution.
     * The greedy algorithm picks based on maximum coverage at each step, which may
     * not always yield the smallest set.
     * (2.0 pts)
     */
    @Test
    void testGreedyNotOptimal() {
        // Classic set cover example where greedy is suboptimal
        // Optimal: {tc2, tc3} covers all (2 test cases)
        // Greedy picks tc1 first (covers 3), then needs tc2 and tc3 (3 test cases total)
        TestCase tc1 = createTestCase(1);
        TestCase tc2 = createTestCase(2);
        TestCase tc3 = createTestCase(3);

        List<TestCase> allCases = Arrays.asList(tc1, tc2, tc3);
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1, 2)),  // tc1 catches 0, 1, 2
            new HashSet<>(Arrays.asList(0, 3, 4)),  // tc2 catches 0, 3, 4
            new HashSet<>(Arrays.asList(1, 2, 5))   // tc3 catches 1, 2, 5
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Greedy will pick tc1 first (covers 3 files: 0,1,2)
        // Then needs tc2 (covers 3,4) and tc3 (covers 5) - but wait, tc2 also covers 3,4
        // So greedy picks tc1, then tc2 (covers 3,4), then tc3 (covers 5)
        // Result: 3 test cases (not optimal, optimal would be tc2 + tc3 = 2)

        // Verify cover is complete
        Set<Integer> coveredFiles = new HashSet<>();
        for (TestCase tc : cover) {
            int idx = allCases.indexOf(tc);
            coveredFiles.addAll(caseToFiles.get(idx));
        }
        assertTrue(coveredFiles.containsAll(wrongSet));

        // Greedy picks 3 (tc1 first), optimal would be 2
        // The greedy algorithm should return 3 test cases
        assertEquals(3, cover.size());
    }
}

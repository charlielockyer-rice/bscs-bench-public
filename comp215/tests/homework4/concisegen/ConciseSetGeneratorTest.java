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
class ConciseSetGeneratorTest {

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
    void testDoesNotMutateAllCases() {
        // Setup: 3 test cases, 3 files, each test catches one file
        List<TestCase> allCases = Arrays.asList(
            createTestCase(1),
            createTestCase(2),
            createTestCase(3)
        );
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0)),
            new HashSet<>(Arrays.asList(1)),
            new HashSet<>(Arrays.asList(2))
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        // Make copies for comparison
        List<TestCase> originalAllCases = copyAllCases(allCases);

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        ConciseSetGenerator.setCover(results);

        // Verify allCases was not mutated
        assertEquals(originalAllCases.size(), allCases.size());
        for (int i = 0; i < allCases.size(); i++) {
            assertEquals(originalAllCases.get(i), allCases.get(i));
        }
    }

    /**
     * Tests that setCover does not mutate the caseToFiles list.
     * (1.0 pt)
     */
    @Test
    void testDoesNotMutateCaseToFiles() {
        // Setup: 3 test cases, 3 files
        List<TestCase> allCases = Arrays.asList(
            createTestCase(1),
            createTestCase(2),
            createTestCase(3)
        );
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1)),
            new HashSet<>(Arrays.asList(1, 2)),
            new HashSet<>(Arrays.asList(0, 2))
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        // Make copies for comparison
        List<Set<Integer>> originalCaseToFiles = copyCaseToFiles(caseToFiles);

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        ConciseSetGenerator.setCover(results);

        // Verify caseToFiles was not mutated
        assertEquals(originalCaseToFiles.size(), caseToFiles.size());
        for (int i = 0; i < caseToFiles.size(); i++) {
            assertEquals(originalCaseToFiles.get(i), caseToFiles.get(i));
        }
    }

    /**
     * Tests that setCover does not mutate the wrongSet.
     * (2.0 pts)
     */

    /**
     * Tests that setCover returns a valid covering set.
     * (1.0 pt)
     */
    @Test
    void testSetCoverReturnsValidCover() {
        // Setup: 2 test cases, case 0 catches files {0,1}, case 1 catches files {1,2}
        List<TestCase> allCases = Arrays.asList(
            createTestCase(1),
            createTestCase(2)
        );
        List<Set<Integer>> caseToFiles = Arrays.asList(
            new HashSet<>(Arrays.asList(0, 1)),
            new HashSet<>(Arrays.asList(1, 2))
        );
        Set<Integer> wrongSet = new HashSet<>(Arrays.asList(0, 1, 2));

        TestResults results = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> cover = ConciseSetGenerator.setCover(results);

        // Both test cases needed to cover all 3 files
        assertEquals(2, cover.size());
    }
}

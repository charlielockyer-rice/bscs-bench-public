package test.rice.test;

import main.rice.obj.*;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the TestResults class.
 */
class TestResultsTestPrivate {

    // ========== Test data for allCases ==========
    private static final TestCase case0 =
        new TestCase(Arrays.asList(new PyIntObj(1)));
    private static final TestCase case1 =
        new TestCase(Arrays.asList(new PyIntObj(2)));
    private static final TestCase case2 =
        new TestCase(Arrays.asList(new PyIntObj(3)));
    private static final List<TestCase> threeCases =
        Arrays.asList(case0, case1, case2);

    // ========== Test data for caseToFiles ==========
    // Empty: no files caught by any test case
    private static final List<Set<Integer>> emptyCaseToFiles =
        Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>());

    // All pass: same as empty, no files caught
    private static final List<Set<Integer>> allPassCaseToFiles =
        Arrays.asList(new HashSet<>(), new HashSet<>(), new HashSet<>());

    // NonEmpty: some files caught by test cases
    // case0 catches files 0, 2
    // case1 catches file 1
    // case2 catches files 0, 1, 2
    private static final List<Set<Integer>> nonEmptyCaseToFiles =
        Arrays.asList(
            new HashSet<>(Arrays.asList(0, 2)),
            new HashSet<>(Arrays.asList(1)),
            new HashSet<>(Arrays.asList(0, 1, 2)));

    // ========== Test data for wrongSet ==========
    private static final Set<Integer> emptyWrongSet = new HashSet<>();
    private static final Set<Integer> nonEmptyWrongSet =
        new HashSet<>(Arrays.asList(0, 1, 2));

    // ========== TestResults objects ==========
    private static final TestResults resultsWithCases =
        new TestResults(threeCases, emptyCaseToFiles, emptyWrongSet);
    private static final TestResults emptyResults =
        new TestResults(new ArrayList<>(), new ArrayList<>(), new HashSet<>());
    private static final TestResults allPassResults =
        new TestResults(threeCases, allPassCaseToFiles, emptyWrongSet);
    private static final TestResults nonEmptyResults =
        new TestResults(threeCases, nonEmptyCaseToFiles, nonEmptyWrongSet);

    // =====================================================
    // Tests for getTestCase()
    // =====================================================

    @Test
    void testGetTestCaseOutOfBoundsNeg() {
        // Test negative index - should return null
        TestCase result = resultsWithCases.getTestCase(-1);
        assertNull(result);
    }

    @Test
    void testGetTestCaseOutOfBoundsPos() {
        // Test index >= size - should return null
        TestCase result = resultsWithCases.getTestCase(3);
        assertNull(result);
    }

    // =====================================================
    // Tests for getWrongSet()
    // =====================================================

    @Test
    void testGetWrongSetEmpty() {
        Set<Integer> wrongSet = allPassResults.getWrongSet();
        assertNotNull(wrongSet);
        assertTrue(wrongSet.isEmpty());
    }

    @Test
    void testGetWrongSetNonEmpty() {
        Set<Integer> wrongSet = nonEmptyResults.getWrongSet();
        assertNotNull(wrongSet);
        assertEquals(3, wrongSet.size());
        assertTrue(wrongSet.contains(0));
        assertTrue(wrongSet.contains(1));
        assertTrue(wrongSet.contains(2));
    }

    // =====================================================
    // Tests for getCaseToFiles()
    // =====================================================

    @Test
    void testGetCaseToFilesEmpty() {
        // When there are no test cases
        List<Set<Integer>> caseToFiles = emptyResults.getCaseToFiles();
        assertNotNull(caseToFiles);
        assertTrue(caseToFiles.isEmpty());
    }

    @Test
    void testGetCaseToFilesAllPass() {
        // When all files pass all tests (each set in caseToFiles is empty)
        List<Set<Integer>> caseToFiles = allPassResults.getCaseToFiles();
        assertNotNull(caseToFiles);
        assertEquals(3, caseToFiles.size());
        for (Set<Integer> filesForCase : caseToFiles) {
            assertTrue(filesForCase.isEmpty());
        }
    }

    @Test
    void testGetCaseToFilesNonEmpty() {
        // When some files fail some tests
        List<Set<Integer>> caseToFiles = nonEmptyResults.getCaseToFiles();
        assertNotNull(caseToFiles);
        assertEquals(3, caseToFiles.size());

        // case0 catches files 0, 2
        assertEquals(new HashSet<>(Arrays.asList(0, 2)), caseToFiles.get(0));

        // case1 catches file 1
        assertEquals(new HashSet<>(Arrays.asList(1)), caseToFiles.get(1));

        // case2 catches files 0, 1, 2
        assertEquals(new HashSet<>(Arrays.asList(0, 1, 2)), caseToFiles.get(2));
    }
}

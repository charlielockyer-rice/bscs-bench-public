package test.rice.test;

import main.rice.obj.*;
import main.rice.test.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the TestResults class.
 */
class TestResultsTest {

    /**
     * A list of test cases, to be shared across the different tests.
     */
    private static List<TestCase> testCases;

    /**
     * A TestResults object and its corresponding caseToFiles and wrongSet.
     */
    private static TestResults testResults;
    private static List<Set<Integer>> caseToFiles;
    private static Set<Integer> wrongSet;

    /**
     * Sets up all of the static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        // Set up 10 test cases
        testCases = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testCases.add(new TestCase(List.of(new PyBoolObj(true), new PyIntObj(i),
                    new PyStringObj(String.valueOf((char) i)))));
        }

        // Create a caseToFiles and wrongSet
        caseToFiles = new ArrayList<>();
        wrongSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            caseToFiles.add(Set.of(i));
            wrongSet.add(i);
        }

        // Create TestResults
        testResults = new TestResults(testCases, caseToFiles, wrongSet);
    }

    /**
     * Tests getTestCase() using a valid index.
     */
    @Test
    void testGetTestCaseValid() {
        assertEquals(testCases.get(3), testResults.getTestCase(3));
    }

    /**
     * Tests getTestCase() using a negative index; should return null.
     */
    @Test
    void testGetTestCaseOutOfBoundsNeg() {
        assertNull(testResults.getTestCase(-1));
    }

    /**
     * Tests getTestCase() using a positive index that's out of bounds; should return null.
     */
    @Test
    void testGetTestCaseOutOfBoundsPos() {
        assertNull(testResults.getTestCase(10));
    }

    /**
     * Tests getWrongSet().
     */
    @Test
    void testGetWrongSet() {
        assertEquals(new HashSet<>(wrongSet), testResults.getWrongSet());
    }

    /**
     * Tests getCaseToFiles().
     */
    @Test
    void testGetCaseToFiles() {
        assertEquals(new ArrayList<>(caseToFiles), testResults.getCaseToFiles());
    }
}
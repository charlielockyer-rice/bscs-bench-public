package test.rice.test;

import main.rice.obj.*;
import main.rice.test.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the Tester class.
 */
class TesterTest {

    /**
     * The absolute path to this project directory, which we'll use to find the provided
     * pyfiles.
     */
    private static final String userDir = System.getProperty("user.dir");

    /**
     * List of test cases for the function under test (function definitions can be found
     * in the test.rice.test.pyfiles package).
     */
    private static final List<TestCase> f0Tests = new ArrayList<>();

    /**
     * Contents of solution file.
     */
    private static final String[] solContentsArray = new String[]{"def func0(intval):\n    return intval"};

    /**
     * Sets up all test cases for all functions under test.
     */
    @BeforeAll
    static void setUp() {
        setUpF0Test();
    }

    /**
     * Tests computeExpectedResults() on the provided function.
     */
    @Test
    void testGetExpectedResults() {
        expectedHelper("func0", f0Tests,
                "func0sol.py", List.of("0"));
    }

    /**
     * Tests runTests() for the provided function on a buggy implementation; checks wrongSet.
     */
    @Test
    void testRunTestsWrongSetWrong() {
        runTestsHelper("func0", f0Tests, "f0oneWrong",
                "results = [0]", Set.of(0), List.of(Set.of(0)), true);
    }

    /**
     * Tests runTests() for the provided function on a buggy implementation; checks caseToFiles.
     */
    @Test
    void testRunTestsCaseToFilesWrong() {
        runTestsHelper("func0", f0Tests, "f0oneWrong",
                "results = [0]", Set.of(0), List.of(Set.of(0)), false);
    }

    /**
     * Tests runTests() for the provided function on a non-buggy implementation; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(17)
    void testRunTestsWrongSetRight() {
        runTestsHelper("func0", f0Tests, "f0oneRight",
                "results = [0]", Set.of(), List.of(Set.of()), true);
    }

    /**
     * Tests runTests() for the provided function on a non-buggy implementation; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(18)
    void testRunTestsCaseToFilesRight() {
        runTestsHelper("func0", f0Tests, "f0oneRight",
                "results = [0]", Set.of(), List.of(Set.of()), false);
    }


    /**
     * Sets up a test case for function f0.
     */
    private static void setUpF0Test() {
        f0Tests.add(new TestCase(Collections.singletonList(new PyIntObj(0))));
    }

    /**
     * Helper function for testing the computeExpectedResults() function; instantiates a
     * Tester object, computes the expected results, and compares those to the
     * manually-created expected results.
     *
     * @param funcName the name of the function under test
     * @param tests    the set of tests to be run
     * @param solName  the filename of the reference solution, which can be found in * the
     *                 test.rice.test.pyfiles.sols package
     * @param expected the expected (expected) results
     */
    private void expectedHelper(String funcName, List<TestCase> tests, String solName,
                                List<String> expected) {
        int solNum = Integer.parseInt(String.valueOf(funcName.charAt(funcName.length() - 1)));
        Tester tester = new Tester(funcName, userDir +
                "/src/test/rice/test/pyfiles/sols/" + solName, userDir +
                "/src/test/rice/test/pyfiles/f0oneWrong", tests);
        try {
            // Compute the actual results and compare to the expected
            writeSolContents(solNum);
            List<String> actual = tester.computeExpectedResults();
            assertEquals(expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Helper function for writing the contents of a solution file from scratch.
     *
     * @param solNum the number of the solution to be written; valid numbers are {0, 1, 2, 3}
     * @throws IOException if something goes wrong
     */
    private void writeSolContents(int solNum) throws IOException {
        // Get path and contents
        String solPath = userDir + "/src/test/rice/test/pyfiles/sols/func" + solNum + "sol.py";
        String solContents = solContentsArray[solNum];

        // Write solution file from scratch
        FileWriter writer = new FileWriter(solPath);
        writer.write(solContents);
        writer.close();
    }

    /**
     * Helper function for testing the runTests() function; instantiates a Tester object,
     * fakes computation of the expected results, gets the actual results, and compares
     * both the wrongSet and caseToFile mappings between the actual and expected results.
     *
     * @param funcName      name of the function under test
     * @param tests         the set of tests to be run
     * @param implDir       the path to the directory containing the buggy implementations
     * @param solResults    the expected contents of expected.py, assuming
     *                      computeExpectedResults() is correct
     * @param expWrongSet   the expected wrongSet
     * @param expResults    the expected caseToFile list
     * @param checkWrongSet a boolean indicating which output to check
     */
    private void runTestsHelper(String funcName, List<TestCase> tests, String implDir,
                                String solResults, Set<Integer> expWrongSet, List<Set<Integer>> expResults,
                                boolean checkWrongSet) {
        Tester tester = new Tester(funcName, null,
                userDir + "/src/test/rice/test/pyfiles/" + implDir, tests);
        try {
            // Generate the expected.py file (to fake computing the expected results
            // without creating a dependency on computeExpectedResults())
            FileWriter writer = new FileWriter(userDir +
                    "/src/test/rice/test/pyfiles/" + implDir + "/expected.py");
            writer.write(solResults);
            writer.close();

            // Run the tester
            TestResults results = tester.runTests();
            if (checkWrongSet) {
                assertEquals(expWrongSet, results.getWrongSet());
            } else {
                assertEquals(expResults, results.getCaseToFiles());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            // Clean up, so that we don't artificially make it look like
            // computeExpectedResults() works
            deletedExpected(implDir);
        }
    }

    /**
     * Deletes the file containing the expected results.
     *
     * @param implDir the path to the directory containing the expected results
     */
    private void deletedExpected(String implDir) {
        File expFile = new File(userDir + "/src/test/rice/test/pyfiles/" +
                implDir + "/expected.py");
        expFile.delete();
    }
}
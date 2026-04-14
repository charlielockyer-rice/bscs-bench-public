package test.rice.test;

import main.rice.obj.*;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import main.rice.test.Tester;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the Tester class.
 */
class TesterTestPrivate {

    @TempDir
    Path tempDir;

    private Path solutionDir;
    private Path implDir;

    @BeforeEach
    void setUp() throws IOException {
        solutionDir = tempDir.resolve("solution");
        implDir = tempDir.resolve("impl");
        Files.createDirectories(solutionDir);
        Files.createDirectories(implDir);
    }

    // =====================================================
    // Helper methods
    // =====================================================

    /**
     * Creates a simple solution Python file that defines a function.
     */
    private Path createSolutionFile(String funcName, String funcBody) throws IOException {
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = String.format("def %s(*args):\n    %s\n", funcName, funcBody);
        Files.writeString(solutionFile, content);
        return solutionFile;
    }

    /**
     * Creates a buggy implementation file in the impl directory.
     */
    private Path createImplFile(String filename, String funcName, String funcBody) throws IOException {
        Path implFile = implDir.resolve(filename);
        String content = String.format("def %s(*args):\n    %s\n", funcName, funcBody);
        Files.writeString(implFile, content);
        return implFile;
    }

    /**
     * Creates a simple add function solution: returns sum of two args.
     */
    private Path createAddSolution() throws IOException {
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def add(a, b):\n    return a + b\n";
        Files.writeString(solutionFile, content);
        return solutionFile;
    }

    /**
     * Creates an identity function solution: returns the first arg.
     */
    private Path createIdentitySolution() throws IOException {
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def identity(x):\n    return x\n";
        Files.writeString(solutionFile, content);
        return solutionFile;
    }

    /**
     * Creates a correct implementation of add.
     */
    private void createCorrectAddImpl(String filename) throws IOException {
        String content = "def add(a, b):\n    return a + b\n";
        Files.writeString(implDir.resolve(filename), content);
    }

    /**
     * Creates a buggy implementation of add (always returns 0).
     */
    private void createBuggyAddImpl(String filename) throws IOException {
        String content = "def add(a, b):\n    return 0\n";
        Files.writeString(implDir.resolve(filename), content);
    }

    /**
     * Creates a partially buggy implementation of add (works for some cases).
     */
    private void createPartiallyBuggyAddImpl(String filename) throws IOException {
        // Works when a == 1, fails otherwise
        String content = "def add(a, b):\n    if a == 1:\n        return a + b\n    return 0\n";
        Files.writeString(implDir.resolve(filename), content);
    }

    /**
     * Creates test cases with simple integer arguments for add function.
     */
    private List<TestCase> createAddTestCases() {
        return Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new TestCase(Arrays.asList(new PyIntObj(3), new PyIntObj(4))),
            new TestCase(Arrays.asList(new PyIntObj(0), new PyIntObj(0)))
        );
    }

    /**
     * Creates a single test case with one integer argument.
     */
    private List<TestCase> createSingleIntTestCase(int value) {
        return Arrays.asList(new TestCase(Arrays.asList(new PyIntObj(value))));
    }

    /**
     * Creates a single test case with one string argument.
     */
    private List<TestCase> createSingleStringTestCase(String value) {
        return Arrays.asList(new TestCase(Arrays.asList(new PyStringObj(value))));
    }

    /**
     * Creates a test case with one nested argument (list of ints).
     */
    private List<TestCase> createNestedListTestCase() {
        PyListObj<PyIntObj> nestedList = new PyListObj<>(
            Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));
        return Arrays.asList(new TestCase(Arrays.asList(nestedList)));
    }

    /**
     * Creates test cases with multiple simple args.
     */
    private List<TestCase> createMultipleArgsSimpleTestCases() {
        return Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyStringObj("a"), new PyFloatObj(1.5))),
            new TestCase(Arrays.asList(new PyIntObj(2), new PyStringObj("b"), new PyFloatObj(2.5)))
        );
    }

    /**
     * Creates test cases with multiple nested args.
     */
    private List<TestCase> createMultipleArgsNestedTestCases() {
        PyListObj<PyIntObj> list1 = new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));
        PyTupleObj<PyStringObj> tuple1 = new PyTupleObj<>(Arrays.asList(new PyStringObj("a")));
        return Arrays.asList(
            new TestCase(Arrays.asList(list1, tuple1)),
            new TestCase(Arrays.asList(
                new PyListObj<>(Arrays.asList(new PyIntObj(3))),
                new PyTupleObj<>(Arrays.asList(new PyStringObj("b"), new PyStringObj("c")))
            ))
        );
    }

    // =====================================================
    // Tests for invalid paths
    // =====================================================

    @Test
    void testGetExpectedResultsMultipleArgsNested() throws IOException, InterruptedException {
        // Create a function that returns first arg (a list)
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def first(a, b):\n    return a\n";
        Files.writeString(solutionFile, content);

        List<TestCase> tests = createMultipleArgsNestedTestCases();
        Tester tester = new Tester("first", solutionFile.toString(), implDir.toString(), tests);

        List<String> results = tester.computeExpectedResults();

        assertEquals(2, results.size());
        assertEquals("[1, 2]", results.get(0));
        assertEquals("[3]", results.get(1));
    }

    // =====================================================
    // Tests for runTests - one file, one test
    // =====================================================

    @Test
    void testRunTestsOneFileOneTestFails() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(1, results.getWrongSet().size());
        assertTrue(results.getWrongSet().contains(0));
    }

    @Test
    void testRunTestsOneFileOneTestFails2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(5), new PyIntObj(5))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // caseToFiles should show that case 0 catches file 0
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        assertEquals(1, caseToFiles.size());
        assertTrue(caseToFiles.get(0).contains(0));
    }

    @Test
    void testRunTestsReturnsTests() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Verify that getTestCase returns the correct test cases
        assertEquals(tests.get(0), results.getTestCase(0));
        assertEquals(tests.get(1), results.getTestCase(1));
        assertEquals(tests.get(2), results.getTestCase(2));
    }

    @Test
    void testRunTestsOneFileOneTestPasses() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsOneFileOneTestPasses2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(10), new PyIntObj(20))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // caseToFiles should be empty (no files caught)
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        assertEquals(1, caseToFiles.size());
        assertTrue(caseToFiles.get(0).isEmpty());
    }

    @Test
    void testRunTestsOneFileOneTestPrints() throws IOException, InterruptedException {
        // Create a solution that prints and returns
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def add(a, b):\n    print('debug')\n    return a + b\n";
        Files.writeString(solutionFile, content);

        // Create impl that also prints
        String implContent = "def add(a, b):\n    print('impl debug')\n    return a + b\n";
        Files.writeString(implDir.resolve("impl1.py"), implContent);

        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Should still pass despite print statements
        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsOneFileOneTestPrints2() throws IOException, InterruptedException {
        // Create a solution that prints multiple lines and returns
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def add(a, b):\n    print('line1')\n    print('line2')\n    return a + b\n";
        Files.writeString(solutionFile, content);

        // Create impl that prints different things but returns correct result
        String implContent = "def add(a, b):\n    print('other')\n    return a + b\n";
        Files.writeString(implDir.resolve("impl1.py"), implContent);

        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(5), new PyIntObj(3))));
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
    }

    // =====================================================
    // Tests for runTests - skip non-py files
    // =====================================================

    @Test
    void testRunTestsSkipNonPy() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        // Create a non-.py file that should be skipped
        Files.writeString(implDir.resolve("readme.txt"), "This is not a Python file");

        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Should only process the .py file
        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsSkipNonPy2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        createBuggyAddImpl("impl2.py");
        // Create multiple non-.py files
        Files.writeString(implDir.resolve("readme.txt"), "Text file");
        Files.writeString(implDir.resolve("data.json"), "{}");
        Files.writeString(implDir.resolve("script.sh"), "#!/bin/bash");

        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Should process only the 2 .py files
        // impl1 passes, impl2 fails
        assertEquals(1, results.getWrongSet().size());
    }

    // =====================================================
    // Tests for runTests - one file, all fail/pass/mixed
    // =====================================================

    @Test
    void testRunTestsOneFileFailsAll() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(1, results.getWrongSet().size());
    }

    @Test
    void testRunTestsOneFileFailsAll2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // caseToFiles should have 3 entries (one per test case)
        // Tests (1,2) and (3,4) catch file 0; test (0,0) does not (buggy returns 0 = correct)
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        assertEquals(3, caseToFiles.size());
        assertTrue(caseToFiles.get(0).contains(0));  // (1,2): buggy returns 0, expected 3
        assertTrue(caseToFiles.get(1).contains(0));  // (3,4): buggy returns 0, expected 7
        assertTrue(caseToFiles.get(2).isEmpty());     // (0,0): buggy returns 0, expected 0 (match)
    }

    @Test
    void testRunTestsOneFilePassesAll() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsOneFilePassesAll2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // No test case should catch any file
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        for (Set<Integer> caught : caseToFiles) {
            assertTrue(caught.isEmpty());
        }
    }

    @Test
    void testRunTestsOneFileMixed() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createPartiallyBuggyAddImpl("impl1.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // File should be in wrongSet (fails at least one test)
        assertEquals(1, results.getWrongSet().size());
    }

    @Test
    void testRunTestsOneFileMixed2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createPartiallyBuggyAddImpl("impl1.py");
        // Tests: (1,2), (3,4), (0,0)
        // Partially buggy: if a==1: return a+b; else: return 0
        // (1,2): a==1, returns 3, expected 3 -> passes
        // (3,4): a!=1, returns 0, expected 7 -> fails
        // (0,0): a!=1, returns 0, expected 0 -> passes (buggy returns correct answer)
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        // Test 0 (1,2): passes -> empty set
        assertTrue(caseToFiles.get(0).isEmpty());
        // Test 1 (3,4): fails -> catches file 0
        assertTrue(caseToFiles.get(1).contains(0));
        // Test 2 (0,0): passes (buggy returns 0, expected 0) -> empty set
        assertTrue(caseToFiles.get(2).isEmpty());
    }

    // =====================================================
    // Tests for runTests - multiple files
    // =====================================================

    @Test
    void testRunTestsMultipleFilesFailAll() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        createBuggyAddImpl("impl2.py");
        createBuggyAddImpl("impl3.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(3, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMultipleFilesFailAll2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("impl1.py");
        createBuggyAddImpl("impl2.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Both files caught by tests (1,2) and (3,4); test (0,0) not caught
        // (buggy returns 0 for add(0,0), which is the correct answer)
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        assertEquals(3, caseToFiles.size());
        assertEquals(2, caseToFiles.get(0).size()); // (1,2): both fail
        assertEquals(2, caseToFiles.get(1).size()); // (3,4): both fail
        assertTrue(caseToFiles.get(2).isEmpty());    // (0,0): both "pass" (return correct 0)
    }

    @Test
    void testRunTestsMultipleFilesPassAll() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        createCorrectAddImpl("impl2.py");
        createCorrectAddImpl("impl3.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsMultipleFilesPassAll2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        createCorrectAddImpl("impl2.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // No file should be caught
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        for (Set<Integer> caught : caseToFiles) {
            assertTrue(caught.isEmpty());
        }
    }

    @Test
    void testRunTestsMultipleFilesMixed() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        createBuggyAddImpl("impl2.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(1, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMultipleFilesMixed2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("impl1.py");
        createBuggyAddImpl("impl2.py");
        createPartiallyBuggyAddImpl("impl3.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // impl1 passes all, impl2 fails all, impl3 fails some
        assertEquals(2, results.getWrongSet().size());
        assertTrue(results.getWrongSet().contains(1)); // impl2
        assertTrue(results.getWrongSet().contains(2)); // impl3
    }

    @Test
    void testRunTestsMultipleFilesMixedCorrectness() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("a_impl.py");
        createBuggyAddImpl("b_impl.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // File 0 (a_impl) should pass, File 1 (b_impl) should fail
        assertFalse(results.getWrongSet().contains(0));
        assertTrue(results.getWrongSet().contains(1));
    }

    @Test
    void testRunTestsMultipleFilesMixedCorrectness2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("a_impl.py");
        createCorrectAddImpl("b_impl.py");
        createPartiallyBuggyAddImpl("c_impl.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // File 0 (a_impl) fails, File 1 (b_impl) passes, File 2 (c_impl) fails
        assertTrue(results.getWrongSet().contains(0));
        assertFalse(results.getWrongSet().contains(1));
        assertTrue(results.getWrongSet().contains(2));
    }

    @Test
    void testRunTestsMultipleFilesNotNumbered() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createCorrectAddImpl("alpha.py");
        createBuggyAddImpl("beta.py");
        createCorrectAddImpl("gamma.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // alpha and gamma pass, beta fails
        assertEquals(1, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMultipleFilesNotNumbered2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        createBuggyAddImpl("zebra.py");
        createCorrectAddImpl("apple.py");
        createBuggyAddImpl("mango.py");
        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Files are sorted: apple (0), mango (1), zebra (2)
        // apple passes, mango fails, zebra fails
        assertEquals(2, results.getWrongSet().size());
        assertFalse(results.getWrongSet().contains(0)); // apple
        assertTrue(results.getWrongSet().contains(1));  // mango
        assertTrue(results.getWrongSet().contains(2));  // zebra
    }

    // =====================================================
    // Tests for runTests - complex cases
    // =====================================================

    @Test
    void testRunTestsMultipleFilesFailAllComplex() throws IOException, InterruptedException {
        // Create a more complex function
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def concat(lst, s):\n    return lst + [s]\n";
        Files.writeString(solutionFile, content);

        // Buggy implementations
        String buggy = "def concat(lst, s):\n    return lst\n";
        Files.writeString(implDir.resolve("impl1.py"), buggy);
        Files.writeString(implDir.resolve("impl2.py"), buggy);

        // Test with nested args
        PyListObj<PyIntObj> list = new PyListObj<>(Arrays.asList(new PyIntObj(1)));
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(list, new PyStringObj("x"))));
        Tester tester = new Tester("concat", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(2, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMultipleFilesFailAllComplex2() throws IOException, InterruptedException {
        // Create function that returns a tuple
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def make_pair(a, b):\n    return (a, b)\n";
        Files.writeString(solutionFile, content);

        // Buggy implementations return list instead of tuple
        String buggy = "def make_pair(a, b):\n    return [a, b]\n";
        Files.writeString(implDir.resolve("impl1.py"), buggy);
        Files.writeString(implDir.resolve("impl2.py"), buggy);

        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new TestCase(Arrays.asList(new PyStringObj("a"), new PyStringObj("b"))));
        Tester tester = new Tester("make_pair", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // All test cases catch all files
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        for (Set<Integer> caught : caseToFiles) {
            assertEquals(2, caught.size());
        }
    }

    @Test
    void testRunTestsMultipleFilesPassAllComplex() throws IOException, InterruptedException {
        // Create function that processes nested structure
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def flatten(lst):\n    result = []\n    for item in lst:\n        if isinstance(item, list):\n            result.extend(item)\n        else:\n            result.append(item)\n    return result\n";
        Files.writeString(solutionFile, content);

        // Correct implementations
        Files.writeString(implDir.resolve("impl1.py"), content);
        Files.writeString(implDir.resolve("impl2.py"), content);

        PyListObj<PyIntObj> inner = new PyListObj<>(Arrays.asList(new PyIntObj(2), new PyIntObj(3)));
        PyListObj<APyObj> outer = new PyListObj<>(Arrays.asList(new PyIntObj(1), inner));
        List<TestCase> tests = Arrays.asList(new TestCase(Arrays.asList(outer)));
        Tester tester = new Tester("flatten", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
    }

    @Test
    void testRunTestsMultipleFilesPassAllComplex2() throws IOException, InterruptedException {
        // Create function that returns dict-like structure
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def double_list(lst):\n    return [x * 2 for x in lst]\n";
        Files.writeString(solutionFile, content);

        Files.writeString(implDir.resolve("impl1.py"), content);
        Files.writeString(implDir.resolve("impl2.py"), content);
        Files.writeString(implDir.resolve("impl3.py"), content);

        PyListObj<PyIntObj> testList = new PyListObj<>(
            Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));
        List<TestCase> tests = Arrays.asList(new TestCase(Arrays.asList(testList)));
        Tester tester = new Tester("double_list", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertTrue(results.getWrongSet().isEmpty());
        List<Set<Integer>> caseToFiles = results.getCaseToFiles();
        assertTrue(caseToFiles.get(0).isEmpty());
    }

    @Test
    void testRunTestsMultipleFilesMixedComplex() throws IOException, InterruptedException {
        // Create function with nested lists
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def sum_nested(lst):\n    total = 0\n    for item in lst:\n        if isinstance(item, list):\n            total += sum(item)\n        else:\n            total += item\n    return total\n";
        Files.writeString(solutionFile, content);

        // Correct implementation
        Files.writeString(implDir.resolve("correct.py"), content);

        // Buggy implementation (doesn't handle nested lists)
        String buggy = "def sum_nested(lst):\n    return sum(lst)\n";
        Files.writeString(implDir.resolve("wrong.py"), buggy);

        PyListObj<PyIntObj> inner = new PyListObj<>(Arrays.asList(new PyIntObj(2), new PyIntObj(3)));
        PyListObj<APyObj> outer = new PyListObj<>(Arrays.asList(new PyIntObj(1), inner));
        List<TestCase> tests = Arrays.asList(new TestCase(Arrays.asList(outer)));
        Tester tester = new Tester("sum_nested", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        assertEquals(1, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMultipleFilesMixedComplex2() throws IOException, InterruptedException {
        // More complex scenario with multiple test cases and implementations
        Path solutionFile = solutionDir.resolve("solution.py");
        String content = "def process(x, y):\n    if isinstance(x, list):\n        return x + [y]\n    return [x, y]\n";
        Files.writeString(solutionFile, content);

        // Correct
        Files.writeString(implDir.resolve("a_correct.py"), content);

        // Partially buggy (works for non-list x)
        String partial = "def process(x, y):\n    return [x, y]\n";
        Files.writeString(implDir.resolve("b_partial.py"), partial);

        // Totally buggy
        String buggy = "def process(x, y):\n    return x\n";
        Files.writeString(implDir.resolve("c_wrong.py"), buggy);

        PyListObj<PyIntObj> listArg = new PyListObj<>(Arrays.asList(new PyIntObj(1)));
        List<TestCase> tests = Arrays.asList(
            new TestCase(Arrays.asList(listArg, new PyIntObj(2))),
            new TestCase(Arrays.asList(new PyIntObj(3), new PyIntObj(4))));
        Tester tester = new Tester("process", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // a_correct passes all, b_partial fails test 0, c_wrong fails both
        assertFalse(results.getWrongSet().contains(0)); // a_correct
        assertTrue(results.getWrongSet().contains(1));  // b_partial
        assertTrue(results.getWrongSet().contains(2));  // c_wrong
    }

    // =====================================================
    // Tests for malformed files
    // =====================================================

    @Test
    void testRunTestsMalformedFilesFailAll() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        // Create files with syntax errors or missing function
        String malformed1 = "def wrong_name(a, b):\n    return a + b\n";
        Files.writeString(implDir.resolve("impl1.py"), malformed1);

        String malformed2 = "def add(a, b):\n    return a +\n"; // syntax error
        Files.writeString(implDir.resolve("impl2.py"), malformed2);

        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Both malformed files should fail
        assertEquals(2, results.getWrongSet().size());
    }

    @Test
    void testRunTestsMalformedFilesFailAll2() throws IOException, InterruptedException {
        Path solutionFile = createAddSolution();
        // Create file with runtime error
        String runtime = "def add(a, b):\n    return a / 0\n";
        Files.writeString(implDir.resolve("impl1.py"), runtime);

        // Create file with different function signature
        String wrongSig = "def add(x):\n    return x\n";
        Files.writeString(implDir.resolve("impl2.py"), wrongSig);

        List<TestCase> tests = createAddTestCases();
        Tester tester = new Tester("add", solutionFile.toString(), implDir.toString(), tests);

        tester.computeExpectedResults();
        TestResults results = tester.runTests();

        // Both should be caught
        assertEquals(2, results.getWrongSet().size());
        assertTrue(results.getWrongSet().contains(0));
        assertTrue(results.getWrongSet().contains(1));
    }
}

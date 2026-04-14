package test.rice;

import main.rice.Main;
import main.rice.parse.InvalidConfigException;
import main.rice.test.TestCase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the Main class.
 * Total: 12.0 points (6 tests)
 */
class MainTestPrivate {

    @TempDir
    Path tempDir;

    private Path configPath;
    private Path solutionPath;
    private Path implDirPath;

    @BeforeEach
    void setUp() throws IOException {
        configPath = tempDir.resolve("config.json");
        solutionPath = tempDir.resolve("solution.py");
        implDirPath = tempDir.resolve("impls");
        Files.createDirectory(implDirPath);
    }

    /**
     * Helper to create a config file.
     */
    private void createConfigFile(String content) throws IOException {
        Files.writeString(configPath, content);
    }

    /**
     * Helper to create a solution file.
     */
    private void createSolutionFile(String content) throws IOException {
        Files.writeString(solutionPath, content);
    }

    /**
     * Helper to create an implementation file.
     */
    private void createImplFile(String filename, String content) throws IOException {
        Files.writeString(implDirPath.resolve(filename), content);
    }

    /**
     * Tests that Main.generateTests works correctly when all files are valid.
     * (1.0 pts)
     */
    @Test
    void testMultipleCasesDeterministic() throws IOException, InvalidConfigException, InterruptedException {
        // Create a config
        String config = """
            {
                "fname": "square",
                "types": ["int"],
                "exhaustive domain": ["[0, 1, 2]"],
                "random domain": ["[0, 1, 2]"],
                "num random": 0
            }
            """;
        createConfigFile(config);

        // Create a solution
        String solution = """
            def square(x):
                return x * x
            """;
        createSolutionFile(solution);

        // Create one buggy implementation that fails only on input 2
        createImplFile("buggy.py", """
            def square(x):
                if x == 2:
                    return 5
                return x * x
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        Set<TestCase> result = Main.generateTests(args);
        // Should find exactly one test case that catches the bug
        assertEquals(1, result.size());
    }

    /**
     * Tests Main.generateTests with random test cases to ensure randomness works.
     * (3.0 pts)
     */
    @Test
    void testMultipleCasesRandomness() throws IOException, InvalidConfigException, InterruptedException {
        // Create a config with random tests
        String config = """
            {
                "fname": "increment",
                "types": ["int"],
                "exhaustive domain": ["[1]"],
                "random domain": ["1~100"],
                "num random": 10
            }
            """;
        createConfigFile(config);

        // Create a solution
        String solution = """
            def increment(x):
                return x + 1
            """;
        createSolutionFile(solution);

        // Create a buggy implementation that fails on values > 50
        createImplFile("buggy.py", """
            def increment(x):
                if x > 50:
                    return x
                return x + 1
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        // Run multiple times to test that random tests can catch the bug
        boolean caughtBug = false;
        for (int i = 0; i < 5; i++) {
            Set<TestCase> result = Main.generateTests(args);
            if (!result.isEmpty()) {
                caughtBug = true;
                break;
            }
        }
        // With 10 random tests in range 1-100, should have good chance to catch
        // the bug that manifests for x > 50
        // Note: This test may be flaky due to randomness
        assertTrue(caughtBug, "Random tests should eventually catch the bug");
    }

    /**
     * Tests Main.generateTests with complex nested types.
     * (3.0 pts)
     */
    @Test
    void testMultipleCasesComplex() throws IOException, InvalidConfigException, InterruptedException {
        // Create a config with list type
        String config = """
            {
                "fname": "sum_list",
                "types": ["list(int)"],
                "exhaustive domain": ["0~2(1~2)"],
                "random domain": ["0~3(1~3)"],
                "num random": 0
            }
            """;
        createConfigFile(config);

        // Create a solution
        String solution = """
            def sum_list(lst):
                return sum(lst)
            """;
        createSolutionFile(solution);

        // Create implementations - one correct, one buggy
        createImplFile("correct.py", """
            def sum_list(lst):
                return sum(lst)
            """);

        createImplFile("buggy.py", """
            def sum_list(lst):
                if len(lst) == 0:
                    return 1
                return sum(lst)
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        Set<TestCase> result = Main.generateTests(args);
        // Should find at least one test case that catches the empty list bug
        assertTrue(result.size() >= 1);
        // Verify that the test case that catches the bug involves an empty list
        boolean foundEmptyListTest = false;
        for (TestCase tc : result) {
            String tcString = tc.toString();
            if (tcString.contains("[]")) {
                foundEmptyListTest = true;
                break;
            }
        }
        assertTrue(foundEmptyListTest, "Should find a test case with empty list");
    }
}

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
class MainTest {

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
    void testAllFilesCorrect() throws IOException, InvalidConfigException, InterruptedException {
        // Create a simple config
        String config = """
            {
                "fname": "add_one",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 0
            }
            """;
        createConfigFile(config);

        // Create a simple solution that adds 1 to the input
        String solution = """
            def add_one(x):
                return x + 1
            """;
        createSolutionFile(solution);

        // Create a correct implementation
        createImplFile("correct.py", """
            def add_one(x):
                return x + 1
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        Set<TestCase> result = Main.generateTests(args);
        // All implementations are correct, so the concise set should be empty
        // (no test cases needed to catch bugs since there are no bugs)
        assertTrue(result.isEmpty());
    }

    /**
     * Tests that Main.generateTests works correctly with only one test case.
     * (1.0 pts)
     */
    @Test
    void testOnlyOneCase() throws IOException, InvalidConfigException, InterruptedException {
        // Create a config with only one possible test case
        String config = """
            {
                "fname": "identity",
                "types": ["int"],
                "exhaustive domain": ["[1]"],
                "random domain": ["[1]"],
                "num random": 0
            }
            """;
        createConfigFile(config);

        // Create a solution
        String solution = """
            def identity(x):
                return x
            """;
        createSolutionFile(solution);

        // Create a buggy implementation
        createImplFile("buggy.py", """
            def identity(x):
                return x + 1
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        Set<TestCase> result = Main.generateTests(args);
        // There's one buggy implementation, and only one test case can catch it
        assertEquals(1, result.size());
    }

    /**
     * Tests Main.generateTests with multiple test cases for non-deterministic coverage.
     * (2.0 pts)
     */
    @Test
    void testMultipleCasesNonDeterministic() throws IOException, InvalidConfigException, InterruptedException {
        // Create a config
        String config = """
            {
                "fname": "double",
                "types": ["int"],
                "exhaustive domain": ["1~3"],
                "random domain": ["1~5"],
                "num random": 0
            }
            """;
        createConfigFile(config);

        // Create a solution
        String solution = """
            def double(x):
                return x * 2
            """;
        createSolutionFile(solution);

        // Create multiple buggy implementations that fail on different inputs
        createImplFile("buggy1.py", """
            def double(x):
                if x == 1:
                    return 0
                return x * 2
            """);

        createImplFile("buggy2.py", """
            def double(x):
                if x == 2:
                    return 0
                return x * 2
            """);

        String[] args = {
            configPath.toString(),
            solutionPath.toString(),
            implDirPath.toString()
        };

        Set<TestCase> result = Main.generateTests(args);
        // Should find a minimal set that catches both buggy implementations
        // Could be 1 or 2 test cases depending on set cover algorithm
        assertTrue(result.size() >= 1 && result.size() <= 2);
    }

    /**
     * Tests Main.generateTests with multiple test cases for deterministic coverage.
     * (2.0 pts)
     */
}

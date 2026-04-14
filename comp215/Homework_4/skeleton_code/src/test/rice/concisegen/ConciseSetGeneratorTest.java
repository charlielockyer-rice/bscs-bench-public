package test.rice.concisegen;

import main.rice.concisegen.ConciseSetGenerator;
import main.rice.obj.*;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for the ConciseSetGenerator class.
 */
class ConciseSetGeneratorTest {

    /**
     * Tests setCover().
     */
    @Test
    void testNeedOneAllWrong() {
        // Generate wrongSet
        Set<Integer> wrongSet = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            wrongSet.add(i);
        }

        // Generate allCases
        List<TestCase> allCases = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<APyObj> elem = Collections.singletonList(new PyIntObj(i));
            allCases.add(new TestCase(elem));
        }

        // Generate caseToFiles
        List<Set<Integer>> caseToFiles = new ArrayList<>();
        caseToFiles.add(Set.of(0, 1));
        caseToFiles.add(Set.of(2, 3));
        caseToFiles.add(Set.of(4, 5));
        caseToFiles.add(Set.of(6, 7));

        // Generate expected results
        Set<TestCase> expected = new HashSet<>();
        for (int i = 0; i < 4; i++) {
            expected.add(new TestCase(List.of(new PyIntObj(i))));
        }

        // Compute actual results and compare to expected
        TestResults input = new TestResults(allCases, caseToFiles, wrongSet);
        Set<TestCase> actual = ConciseSetGenerator.setCover(input);
        assertEquals(expected, actual);
    }
}

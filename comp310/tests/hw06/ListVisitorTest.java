package comp310.tests.hw06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import provided.listFW.IList;
import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;
import provided.listFW.visitors.FoldLAlgo;

import visitorDemoExercises.listFWVisitorExercises.model.visitors.LargestFwdAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.LastFwdAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.ContainFwdAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.FromNthAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.RemoveSmallestRevAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.LargestRevAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.SearchRevAlgo;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.LargestAcc;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.LastAcc;
import visitorDemoExercises.listFWVisitorExercises.model.visitors.ContainsAcc;

/**
 * JUnit 5 tests for the list visitor algorithms in HW06.
 * The list framework uses the Visitor design pattern:
 *   - MTList.Singleton is the empty list
 *   - new NEList(first, rest) is a non-empty list (cons cell)
 *   - list.execute(algo, params...) calls algo.emptyCase or algo.nonEmptyCase
 */
class ListVisitorTest {

    /**
     * Helper to build a list of integers from varargs.
     * Builds right-to-left: list(3, 1, 4) => NEList(3, NEList(1, NEList(4, MTList)))
     */
    private IList list(Integer... vals) {
        IList result = MTList.Singleton;
        for (int i = vals.length - 1; i >= 0; i--) {
            result = new NEList(vals[i], result);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // LargestFwdAlgo tests (forward accumulation to find maximum)
    // ---------------------------------------------------------------

    @Test
    void testLargestFwdSingleElement() {
        assertEquals(5, list(5).execute(new LargestFwdAlgo()),
                "Largest of [5] should be 5");
    }

    @Test
    void testLargestFwdMultipleElements() {
        assertEquals(9, list(3, 9, 1, 7).execute(new LargestFwdAlgo()),
                "Largest of [3, 9, 1, 7] should be 9");
    }

    @Test
    void testLargestFwdNegativeElements() {
        assertEquals(-1, list(-3, -1, -5).execute(new LargestFwdAlgo()),
                "Largest of [-3, -1, -5] should be -1");
    }

    @Test
    void testLargestFwdLargestAtEnd() {
        assertEquals(10, list(1, 2, 3, 10).execute(new LargestFwdAlgo()),
                "Largest of [1, 2, 3, 10] should be 10 (at end)");
    }

    // ---------------------------------------------------------------
    // LastFwdAlgo tests (forward accumulation to find last element)
    // ---------------------------------------------------------------

    @Test
    void testLastFwdSingleElement() {
        assertEquals(5, list(5).execute(new LastFwdAlgo()),
                "Last of [5] should be 5");
    }

    @Test
    void testLastFwdMultipleElements() {
        assertEquals(7, list(3, 9, 1, 7).execute(new LastFwdAlgo()),
                "Last of [3, 9, 1, 7] should be 7");
    }

}

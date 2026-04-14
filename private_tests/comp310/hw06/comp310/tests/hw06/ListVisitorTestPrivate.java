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
class ListVisitorTestPrivate {

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
    void testLastFwdEmptyList() {
        assertNull(MTList.Singleton.execute(new LastFwdAlgo()),
                "Last of empty list should be null");
    }

    // ---------------------------------------------------------------
    // ContainFwdAlgo tests (forward search for containment)
    // The parameter is passed as a String that gets parsed to int.
    // ---------------------------------------------------------------

    @Test
    void testContainFwdFound() {
        assertTrue((Boolean) list(3, 1, 4).execute(new ContainFwdAlgo(), "1"),
                "ContainFwd should find 1 in [3, 1, 4]");
    }

    @Test
    void testContainFwdNotFound() {
        assertFalse((Boolean) list(3, 1, 4).execute(new ContainFwdAlgo(), "9"),
                "ContainFwd should not find 9 in [3, 1, 4]");
    }

    @Test
    void testContainFwdFirstElement() {
        assertTrue((Boolean) list(3, 1, 4).execute(new ContainFwdAlgo(), "3"),
                "ContainFwd should find 3 at the beginning of [3, 1, 4]");
    }

    @Test
    void testContainFwdLastElement() {
        assertTrue((Boolean) list(3, 1, 4).execute(new ContainFwdAlgo(), "4"),
                "ContainFwd should find 4 at the end of [3, 1, 4]");
    }

    // ---------------------------------------------------------------
    // FromNthAlgo tests (sum elements starting from the Nth position)
    // The parameter is a 1-based position passed as a String.
    // When param reaches 0, elements start being included.
    // ---------------------------------------------------------------

    @Test
    void testFromNthSumAll() {
        // Starting from position 1 (first element), sum all: 3+1+4 = 8
        assertEquals(8, list(3, 1, 4).execute(new FromNthAlgo(), "1"),
                "FromNth with param=1 should sum all elements: 3+1+4=8");
    }

    @Test
    void testFromNthSumPartial() {
        // Starting from position 2: skip first, sum 1+4 = 5
        assertEquals(5, list(3, 1, 4).execute(new FromNthAlgo(), "2"),
                "FromNth with param=2 should sum from 2nd element: 1+4=5");
    }

    @Test
    void testFromNthSumLast() {
        // Starting from position 3: skip first two, sum 4 = 4
        assertEquals(4, list(3, 1, 4).execute(new FromNthAlgo(), "3"),
                "FromNth with param=3 should sum only last element: 4");
    }

    @Test
    void testFromNthBeyondList() {
        // Starting from position 5: no elements qualify, sum = 0
        assertEquals(0, list(3, 1, 4).execute(new FromNthAlgo(), "5"),
                "FromNth with param beyond list length should return 0");
    }

    // ---------------------------------------------------------------
    // RemoveSmallestRevAlgo tests (remove smallest element, single pass)
    // ---------------------------------------------------------------

    @Test
    void testRemoveSmallestBasic() {
        IList result = (IList) list(3, 1, 4).execute(new RemoveSmallestRevAlgo());
        // Smallest is 1, should be removed. Result should contain 3 and 4.
        assertNotNull(result, "Result should not be null");
        assertTrue(result instanceof NEList, "Result should be non-empty");
        NEList ne = (NEList) result;
        assertEquals(3, ne.getFirst(), "First element after removing smallest should be 3");
    }

    @Test
    void testRemoveSmallestFromTwoElements() {
        IList result = (IList) list(5, 2).execute(new RemoveSmallestRevAlgo());
        // Smallest is 2, should be removed. Result should be [5].
        assertTrue(result instanceof NEList, "Result should be non-empty");
        assertEquals(5, ((NEList) result).getFirst(), "After removing 2 from [5,2], first should be 5");
    }

    // ---------------------------------------------------------------
    // LargestRevAlgo tests (reverse accumulation to find maximum)
    // ---------------------------------------------------------------

    @Test
    void testLargestRevSingleElement() {
        assertEquals(5, list(5).execute(new LargestRevAlgo()),
                "Largest of [5] should be 5 (reverse algo)");
    }

    @Test
    void testLargestRevMultipleElements() {
        assertEquals(9, list(3, 9, 1).execute(new LargestRevAlgo()),
                "Largest of [3, 9, 1] should be 9 (reverse algo)");
    }

    @Test
    void testLargestRevEmpty() {
        assertEquals(Integer.MIN_VALUE, MTList.Singleton.execute(new LargestRevAlgo()),
                "Largest of empty list should be MIN_VALUE (reverse algo)");
    }

    // ---------------------------------------------------------------
    // SearchRevAlgo tests (reverse search for containment)
    // The parameter is passed as a String.
    // ---------------------------------------------------------------

    @Test
    void testSearchRevFound() {
        assertTrue((Boolean) list(3, 1, 4).execute(new SearchRevAlgo(), "1"),
                "SearchRev should find 1 in [3, 1, 4]");
    }

    @Test
    void testSearchRevNotFound() {
        assertFalse((Boolean) list(3, 1, 4).execute(new SearchRevAlgo(), "9"),
                "SearchRev should not find 9 in [3, 1, 4]");
    }

    @Test
    void testSearchRevFirstElement() {
        assertTrue((Boolean) list(3, 1, 4).execute(new SearchRevAlgo(), "3"),
                "SearchRev should find 3 in [3, 1, 4]");
    }

    @Test
    void testSearchRevEmpty() {
        assertFalse((Boolean) MTList.Singleton.execute(new SearchRevAlgo(), "1"),
                "SearchRev should return false for empty list");
    }

    // ---------------------------------------------------------------
    // Accumulator-based algorithms (used with FoldLAlgo)
    // ---------------------------------------------------------------

    @Test
    void testLargestAccViaFoldL() {
        LargestAcc acc = new LargestAcc();
        list(3, 9, 1, 7).execute(new FoldLAlgo(), acc);
        assertEquals("9", acc.toString(), "LargestAcc via FoldL should find 9 in [3, 9, 1, 7]");
    }

    @Test
    void testLastAccViaFoldL() {
        LastAcc acc = new LastAcc();
        list(3, 9, 1, 7).execute(new FoldLAlgo(), acc);
        assertEquals("7", acc.toString(), "LastAcc via FoldL should find 7 (last) in [3, 9, 1, 7]");
    }

    @Test
    void testContainsAccViaFoldLFound() {
        ContainsAcc acc = new ContainsAcc("9");
        list(3, 9, 1, 7).execute(new FoldLAlgo(), acc);
        assertEquals("true", acc.toString(), "ContainsAcc via FoldL should find 9 in [3, 9, 1, 7]");
    }

    @Test
    void testContainsAccViaFoldLNotFound() {
        ContainsAcc acc = new ContainsAcc("5");
        list(3, 9, 1, 7).execute(new FoldLAlgo(), acc);
        assertEquals("false", acc.toString(), "ContainsAcc via FoldL should not find 5 in [3, 9, 1, 7]");
    }
}

package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for mixed iterable PyObj types - cross-type comparisons,
 * deeply nested structures, and various string representations.
 */
class MixedIterablePyObjTestPrivate {

    // Simple list [1, 2]
    private static final PyListObj<PyIntObj> simpleList =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    // Another simple list with same values
    private static final PyListObj<PyIntObj> simpleList2 =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    // Simple tuple (1, 2)
    private static final PyTupleObj<PyIntObj> simpleTuple =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    // Another simple tuple with same values
    private static final PyTupleObj<PyIntObj> simpleTuple2 =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    // Simple set {1, 2}
    private static final PySetObj<PyIntObj> simpleSet;
    private static final PySetObj<PyIntObj> simpleSet2;

    // Simple dict {1: 2}
    private static final PyDictObj<PyIntObj, PyIntObj> simpleDict;
    private static final PyDictObj<PyIntObj, PyIntObj> simpleDict2;

    // Deeply nested: [[{1: (2, 3)}]]
    private static final PyListObj<PyListObj<PyDictObj<PyIntObj, PyTupleObj<PyIntObj>>>> deeplyNested;
    private static final PyListObj<PyListObj<PyDictObj<PyIntObj, PyTupleObj<PyIntObj>>>> deeplyNested2;

    // List containing strings
    private static final PyListObj<PyStringObj> stringList =
        new PyListObj<>(Arrays.asList(new PyStringObj("a"), new PyStringObj("b")));

    // Tuple containing strings
    private static final PyTupleObj<PyStringObj> stringTuple =
        new PyTupleObj<>(Arrays.asList(new PyStringObj("a"), new PyStringObj("b")));

    // Set containing strings
    private static final PySetObj<PyStringObj> stringSet;

    // Dict with string values
    private static final PyDictObj<PyIntObj, PyStringObj> stringDict;

    static {
        // Initialize simple set
        Set<PyIntObj> s1 = new LinkedHashSet<>();
        s1.add(new PyIntObj(1));
        s1.add(new PyIntObj(2));
        simpleSet = new PySetObj<>(s1);

        Set<PyIntObj> s2 = new LinkedHashSet<>();
        s2.add(new PyIntObj(1));
        s2.add(new PyIntObj(2));
        simpleSet2 = new PySetObj<>(s2);

        // Initialize simple dict
        Map<PyIntObj, PyIntObj> d1 = new LinkedHashMap<>();
        d1.put(new PyIntObj(1), new PyIntObj(2));
        simpleDict = new PyDictObj<>(d1);

        Map<PyIntObj, PyIntObj> d2 = new LinkedHashMap<>();
        d2.put(new PyIntObj(1), new PyIntObj(2));
        simpleDict2 = new PyDictObj<>(d2);

        // Initialize deeply nested: [[{1: (2, 3)}]]
        PyTupleObj<PyIntObj> innerTuple = new PyTupleObj<>(
            Arrays.asList(new PyIntObj(2), new PyIntObj(3)));
        Map<PyIntObj, PyTupleObj<PyIntObj>> innerMap = new LinkedHashMap<>();
        innerMap.put(new PyIntObj(1), innerTuple);
        PyDictObj<PyIntObj, PyTupleObj<PyIntObj>> innerDict = new PyDictObj<>(innerMap);
        PyListObj<PyDictObj<PyIntObj, PyTupleObj<PyIntObj>>> middleList =
            new PyListObj<>(Arrays.asList(innerDict));
        deeplyNested = new PyListObj<>(Arrays.asList(middleList));

        // Second deeply nested (same structure)
        PyTupleObj<PyIntObj> innerTuple2 = new PyTupleObj<>(
            Arrays.asList(new PyIntObj(2), new PyIntObj(3)));
        Map<PyIntObj, PyTupleObj<PyIntObj>> innerMap2 = new LinkedHashMap<>();
        innerMap2.put(new PyIntObj(1), innerTuple2);
        PyDictObj<PyIntObj, PyTupleObj<PyIntObj>> innerDict2 = new PyDictObj<>(innerMap2);
        PyListObj<PyDictObj<PyIntObj, PyTupleObj<PyIntObj>>> middleList2 =
            new PyListObj<>(Arrays.asList(innerDict2));
        deeplyNested2 = new PyListObj<>(Arrays.asList(middleList2));

        // String set
        Set<PyStringObj> ss = new LinkedHashSet<>();
        ss.add(new PyStringObj("a"));
        ss.add(new PyStringObj("b"));
        stringSet = new PySetObj<>(ss);

        // String dict
        Map<PyIntObj, PyStringObj> sd = new LinkedHashMap<>();
        sd.put(new PyIntObj(1), new PyStringObj("a"));
        sd.put(new PyIntObj(2), new PyStringObj("b"));
        stringDict = new PyDictObj<>(sd);
    }

    @Test
    void testToStringSquareBracketsSet() {
        // Test that sets use curly braces, not square brackets
        String str = simpleSet.toString();
        assertTrue(str.startsWith("{"));
        assertTrue(str.endsWith("}"));
        assertFalse(str.startsWith("["));
    }

    @Test
    void testToStringDict() {
        assertEquals("{1: 'a', 2: 'b'}", stringDict.toString());
    }

    @Test
    void testToStringSquareBrackets() {
        // Test that lists use square brackets, tuples use parentheses
        assertEquals("[1, 2]", simpleList.toString());
        assertEquals("(1, 2)", simpleTuple.toString());
    }

    @Test
    void testToStringDeeplyNested() {
        assertEquals("[[{1: (2, 3)}]]", deeplyNested.toString());
    }

    @Test
    void testEqualsList() {
        assertEquals(simpleList, simpleList2);
    }

    @Test
    void testEqualsTup() {
        assertEquals(simpleTuple, simpleTuple2);
    }

    @Test
    void testEqualsSet() {
        assertEquals(simpleSet, simpleSet2);
    }

    @Test
    void testEqualsDict() {
        assertEquals(simpleDict, simpleDict2);
    }

    @Test
    void testEqualsDeeplyNested() {
        assertEquals(deeplyNested, deeplyNested2);
    }

    @Test
    void testNotEqualListTup() {
        // List and Tuple with same elements should NOT be equal
        assertNotEquals(simpleList, simpleTuple);
    }

    @Test
    void testNotEqualTupList() {
        // Tuple and List with same elements should NOT be equal
        assertNotEquals(simpleTuple, simpleList);
    }

    @Test
    void testNotEqualListSet() {
        // List and Set with same elements should NOT be equal
        assertNotEquals(simpleList, simpleSet);
    }

    @Test
    void testNotEqualSetList() {
        // Set and List with same elements should NOT be equal
        assertNotEquals(simpleSet, simpleList);
    }

    @Test
    void testNotEqualTupSet() {
        // Tuple and Set with same elements should NOT be equal
        assertNotEquals(simpleTuple, simpleSet);
    }

    @Test
    void testNotEqualSetTup() {
        // Set and Tuple with same elements should NOT be equal
        assertNotEquals(simpleSet, simpleTuple);
    }

    @Test
    void testHashCodeDeeplyNested() {
        assertEquals(deeplyNested.hashCode(), deeplyNested2.hashCode());
    }
}

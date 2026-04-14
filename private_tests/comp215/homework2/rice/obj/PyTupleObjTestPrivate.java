package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyTupleObj class.
 */
class PyTupleObjTestPrivate {

    // Empty tuple
    private static final PyTupleObj<PyIntObj> emptyTuple =
        new PyTupleObj<>(new ArrayList<>());

    // Another empty tuple (distinct object)
    private static final PyTupleObj<PyIntObj> emptyTuple2 =
        new PyTupleObj<>(new ArrayList<>());

    // Single element tuple
    private static final PyTupleObj<PyIntObj> singleTuple =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1)));

    // Multiple element tuple
    private static final PyTupleObj<PyIntObj> multiTuple =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));

    // Another multiTuple with same values (distinct object)
    private static final PyTupleObj<PyIntObj> multiTuple2 =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));

    // Nested tuple: ((1, 2), (3, 4))
    private static final PyTupleObj<PyTupleObj<PyIntObj>> nestedTuple =
        new PyTupleObj<>(Arrays.asList(
            new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new PyTupleObj<>(Arrays.asList(new PyIntObj(3), new PyIntObj(4)))
        ));

    // Another nested tuple with same values
    private static final PyTupleObj<PyTupleObj<PyIntObj>> nestedTuple2 =
        new PyTupleObj<>(Arrays.asList(
            new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new PyTupleObj<>(Arrays.asList(new PyIntObj(3), new PyIntObj(4)))
        ));

    // Subset tuple
    private static final PyTupleObj<PyIntObj> subsetTuple =
        new PyTupleObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    @Test
    void testToStringMultiple() {
        assertEquals("(1, 2, 3)", multiTuple.toString());
    }

    @Test
    void testToStringNested() {
        assertEquals("((1, 2), (3, 4))", nestedTuple.toString());
    }

    @Test
    void testEqualsEmpty() {
        assertEquals(emptyTuple, emptyTuple2);
    }

    @Test
    void testEqualsMultiple() {
        assertEquals(multiTuple, multiTuple2);
    }

    @Test
    void testNotEqual() {
        assertNotEquals(singleTuple, multiTuple);
    }

    @Test
    void testNotEqualSubset1() {
        assertNotEquals(multiTuple, subsetTuple);
    }

    @Test
    void testNotEqualSubset2() {
        assertNotEquals(subsetTuple, multiTuple);
    }

    @Test
    void testNotEqualNonAPyObj() {
        assertNotEquals(multiTuple, Arrays.asList(1, 2, 3));
    }

    @Test
    void testEqualsNested() {
        assertEquals(nestedTuple, nestedTuple2);
    }

    @Test
    void testHashCodeEqual() {
        assertEquals(multiTuple.hashCode(), multiTuple2.hashCode());
    }

    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(singleTuple.hashCode(), multiTuple.hashCode());
    }
}

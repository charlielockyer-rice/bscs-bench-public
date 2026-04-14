package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyTupleObj class.
 */
class PyTupleObjTest {

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
    void testGetValueNested() {
        List<PyTupleObj<PyIntObj>> value = nestedTuple.getValue();
        assertEquals(2, value.size());
        assertEquals(2, value.get(0).getValue().size());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("()", emptyTuple.toString());
    }

    @Test
    void testToStringSingle() {
        // Python single-element tuples have trailing comma: (1,)
        assertEquals("(1,)", singleTuple.toString());
    }

}

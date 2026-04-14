package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyListObj class.
 */
class PyListObjTest {

    // Empty list
    private static final PyListObj<PyIntObj> emptyList =
        new PyListObj<>(new ArrayList<>());

    // Another empty list (distinct object)
    private static final PyListObj<PyIntObj> emptyList2 =
        new PyListObj<>(new ArrayList<>());

    // Simple list with single element
    private static final PyListObj<PyIntObj> singleList =
        new PyListObj<>(Arrays.asList(new PyIntObj(1)));

    // Simple list with multiple elements
    private static final PyListObj<PyIntObj> multiList =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));

    // Another multiList with same values (distinct object)
    private static final PyListObj<PyIntObj> multiList2 =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));

    // Nested list: [[1, 2], [3, 4]]
    private static final PyListObj<PyListObj<PyIntObj>> nestedList =
        new PyListObj<>(Arrays.asList(
            new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new PyListObj<>(Arrays.asList(new PyIntObj(3), new PyIntObj(4)))
        ));

    // Another nested list with same values
    private static final PyListObj<PyListObj<PyIntObj>> nestedList2 =
        new PyListObj<>(Arrays.asList(
            new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2))),
            new PyListObj<>(Arrays.asList(new PyIntObj(3), new PyIntObj(4)))
        ));

    // Subset list [1, 2]
    private static final PyListObj<PyIntObj> subsetList =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2)));

    @Test
    void testGetValueNested() {
        List<PyListObj<PyIntObj>> value = nestedList.getValue();
        assertEquals(2, value.size());
        assertEquals(2, value.get(0).getValue().size());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("[]", emptyList.toString());
    }

    @Test
    void testToStringSimpleSingle() {
        assertEquals("[1]", singleList.toString());
    }

}

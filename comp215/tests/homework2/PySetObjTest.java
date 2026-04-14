package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PySetObj class.
 */
class PySetObjTest {

    // Empty set
    private static final PySetObj<PyIntObj> emptySet =
        new PySetObj<>(new HashSet<>());

    // Another empty set (distinct object)
    private static final PySetObj<PyIntObj> emptySet2 =
        new PySetObj<>(new HashSet<>());

    // Single element set
    private static final PySetObj<PyIntObj> singleSet =
        new PySetObj<>(new HashSet<>(Arrays.asList(new PyIntObj(1))));

    // Multiple element set - use LinkedHashSet for predictable order
    private static final PySetObj<PyIntObj> multiSet;
    private static final PySetObj<PyIntObj> multiSet2;

    static {
        Set<PyIntObj> s1 = new LinkedHashSet<>();
        s1.add(new PyIntObj(1));
        s1.add(new PyIntObj(2));
        s1.add(new PyIntObj(3));
        multiSet = new PySetObj<>(s1);

        Set<PyIntObj> s2 = new LinkedHashSet<>();
        s2.add(new PyIntObj(1));
        s2.add(new PyIntObj(2));
        s2.add(new PyIntObj(3));
        multiSet2 = new PySetObj<>(s2);
    }

    // Subset
    private static final PySetObj<PyIntObj> subsetSet =
        new PySetObj<>(new HashSet<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2))));

    @Test
    void testGetValue() {
        Set<PyIntObj> value = multiSet.getValue();
        assertEquals(3, value.size());
    }

    @Test
    void testToStringEmpty() {
        // Python's empty set is set(), not {} (which is an empty dict)
        assertEquals("set()", emptySet.toString());
    }

    @Test
    void testToStringSingle() {
        assertEquals("{1}", singleSet.toString());
    }

}

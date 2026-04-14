package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyDictObj class.
 */
class PyDictObjTest {

    // Empty dict
    private static final PyDictObj<PyIntObj, PyIntObj> emptyDict =
        new PyDictObj<>(new LinkedHashMap<>());

    // Another empty dict (distinct object)
    private static final PyDictObj<PyIntObj, PyIntObj> emptyDict2 =
        new PyDictObj<>(new LinkedHashMap<>());

    // Single entry dict
    private static final PyDictObj<PyIntObj, PyStringObj> singleDict;

    // Multiple entry dict - use LinkedHashMap for predictable order
    private static final PyDictObj<PyIntObj, PyStringObj> multiDict;
    private static final PyDictObj<PyIntObj, PyStringObj> multiDict2;

    // Nested dict
    private static final PyDictObj<PyIntObj, PyDictObj<PyIntObj, PyIntObj>> nestedDict;
    private static final PyDictObj<PyIntObj, PyDictObj<PyIntObj, PyIntObj>> nestedDict2;

    // Subset dict
    private static final PyDictObj<PyIntObj, PyStringObj> subsetDict;

    static {
        // Single dict: {1: 'a'}
        Map<PyIntObj, PyStringObj> s1 = new LinkedHashMap<>();
        s1.put(new PyIntObj(1), new PyStringObj("a"));
        singleDict = new PyDictObj<>(s1);

        // Multi dict: {1: 'a', 2: 'b'}
        Map<PyIntObj, PyStringObj> m1 = new LinkedHashMap<>();
        m1.put(new PyIntObj(1), new PyStringObj("a"));
        m1.put(new PyIntObj(2), new PyStringObj("b"));
        multiDict = new PyDictObj<>(m1);

        Map<PyIntObj, PyStringObj> m2 = new LinkedHashMap<>();
        m2.put(new PyIntObj(1), new PyStringObj("a"));
        m2.put(new PyIntObj(2), new PyStringObj("b"));
        multiDict2 = new PyDictObj<>(m2);

        // Subset dict: {1: 'a'}
        Map<PyIntObj, PyStringObj> sub = new LinkedHashMap<>();
        sub.put(new PyIntObj(1), new PyStringObj("a"));
        subsetDict = new PyDictObj<>(sub);

        // Nested dict: {1: {2: 3}}
        Map<PyIntObj, PyIntObj> inner = new LinkedHashMap<>();
        inner.put(new PyIntObj(2), new PyIntObj(3));
        Map<PyIntObj, PyDictObj<PyIntObj, PyIntObj>> n1 = new LinkedHashMap<>();
        n1.put(new PyIntObj(1), new PyDictObj<>(inner));
        nestedDict = new PyDictObj<>(n1);

        Map<PyIntObj, PyIntObj> inner2 = new LinkedHashMap<>();
        inner2.put(new PyIntObj(2), new PyIntObj(3));
        Map<PyIntObj, PyDictObj<PyIntObj, PyIntObj>> n2 = new LinkedHashMap<>();
        n2.put(new PyIntObj(1), new PyDictObj<>(inner2));
        nestedDict2 = new PyDictObj<>(n2);
    }

    @Test
    void testGetValueNested() {
        Map<PyIntObj, PyDictObj<PyIntObj, PyIntObj>> value = nestedDict.getValue();
        assertEquals(1, value.size());
    }

    @Test
    void testToStringEmpty() {
        assertEquals("{}", emptyDict.toString());
    }

    @Test
    void testToStringSingle() {
        assertEquals("{1: 'a'}", singleDict.toString());
    }

}

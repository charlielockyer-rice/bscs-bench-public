package test.rice.test;

import main.rice.obj.*;
import main.rice.test.TestCase;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the TestCase class.
 */
class TestCaseTestPrivate {

    // ========== Test data: No args ==========
    private static final List<APyObj> emptyArgs = new ArrayList<>();
    private static final TestCase noArgsCase = new TestCase(emptyArgs);
    private static final TestCase noArgsCase2 = new TestCase(new ArrayList<>());

    // ========== Test data: One arg simple (primitive) ==========
    private static final List<APyObj> oneArgSimple =
        Arrays.asList(new PyIntObj(42));
    private static final TestCase oneArgSimpleCase = new TestCase(oneArgSimple);
    private static final TestCase oneArgSimpleCase2 =
        new TestCase(Arrays.asList(new PyIntObj(42)));

    // ========== Test data: One arg nested (list of ints) ==========
    private static final PyListObj<PyIntObj> nestedList =
        new PyListObj<>(Arrays.asList(new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)));
    private static final List<APyObj> oneArgNested = Arrays.asList(nestedList);
    private static final TestCase oneArgNestedCase = new TestCase(oneArgNested);
    private static final TestCase oneArgNestedCase2 = new TestCase(
        Arrays.asList(new PyListObj<>(Arrays.asList(
            new PyIntObj(1), new PyIntObj(2), new PyIntObj(3)))));

    // ========== Test data: Multiple args simple (primitives) ==========
    private static final List<APyObj> multiArgsSimple =
        Arrays.asList(new PyIntObj(1), new PyStringObj("hello"), new PyFloatObj(3.14));
    private static final TestCase multiArgsSimpleCase = new TestCase(multiArgsSimple);
    private static final TestCase multiArgsSimpleCase2 = new TestCase(
        Arrays.asList(new PyIntObj(1), new PyStringObj("hello"), new PyFloatObj(3.14)));

    // ========== Test data: Multiple args nested (lists and tuples) ==========
    private static final PyListObj<PyIntObj> nestedList1 =
        new PyListObj<>(Arrays.asList(new PyIntObj(10), new PyIntObj(20)));
    private static final PyTupleObj<PyStringObj> nestedTuple =
        new PyTupleObj<>(Arrays.asList(new PyStringObj("a"), new PyStringObj("b")));
    private static final List<APyObj> multiArgsNested =
        Arrays.asList(nestedList1, nestedTuple, new PyIntObj(99));
    private static final TestCase multiArgsNestedCase = new TestCase(multiArgsNested);
    private static final TestCase multiArgsNestedCase2 = new TestCase(Arrays.asList(
        new PyListObj<>(Arrays.asList(new PyIntObj(10), new PyIntObj(20))),
        new PyTupleObj<>(Arrays.asList(new PyStringObj("a"), new PyStringObj("b"))),
        new PyIntObj(99)));

    // ========== Test data: For inequality tests ==========
    private static final TestCase differentCase =
        new TestCase(Arrays.asList(new PyIntObj(100)));
    private static final TestCase subsetCase1 =
        new TestCase(Arrays.asList(new PyIntObj(1)));
    private static final TestCase subsetCase2 =
        new TestCase(Arrays.asList(new PyIntObj(1), new PyStringObj("hello")));

    // =====================================================
    // Tests for getArgs()
    // =====================================================

    @Test
    void testToStringOneArgSimple() {
        String str = oneArgSimpleCase.toString();
        assertEquals("[42]", str);
    }

    @Test
    void testToStringOneArgNested() {
        String str = oneArgNestedCase.toString();
        assertEquals("[[1, 2, 3]]", str);
    }

    @Test
    void testToStringMultipleArgsSimple() {
        String str = multiArgsSimpleCase.toString();
        assertEquals("[1, 'hello', 3.14]", str);
    }

    @Test
    void testToStringMultipleArgsNested() {
        String str = multiArgsNestedCase.toString();
        assertEquals("[[10, 20], ('a', 'b'), 99]", str);
    }

    // =====================================================
    // Tests for equals()
    // =====================================================

    @Test
    void testEqualsNoArgs() {
        assertEquals(noArgsCase, noArgsCase2);
    }

    @Test
    void testEqualsOneArgSimple() {
        assertEquals(oneArgSimpleCase, oneArgSimpleCase2);
    }

    @Test
    void testEqualsOneArgNested() {
        assertEquals(oneArgNestedCase, oneArgNestedCase2);
    }

    @Test
    void testEqualsMultipleArgsSimple() {
        assertEquals(multiArgsSimpleCase, multiArgsSimpleCase2);
    }

    @Test
    void testEqualsMultipleArgsNested() {
        assertEquals(multiArgsNestedCase, multiArgsNestedCase2);
    }

    @Test
    void testNotEqual() {
        assertNotEquals(oneArgSimpleCase, differentCase);
    }

    @Test
    void testNotEqualSubset1() {
        // subsetCase1 has 1 arg, multiArgsSimpleCase has 3
        assertNotEquals(multiArgsSimpleCase, subsetCase1);
    }

    @Test
    void testNotEqualSubset2() {
        // subsetCase2 has 2 args, multiArgsSimpleCase has 3
        assertNotEquals(subsetCase2, multiArgsSimpleCase);
    }

    // =====================================================
    // Tests for hashCode()
    // =====================================================

    @Test
    void testHashCodeNoArgs() {
        assertEquals(noArgsCase.hashCode(), noArgsCase2.hashCode());
    }

    @Test
    void testHashCodeOneArgSimple() {
        assertEquals(oneArgSimpleCase.hashCode(), oneArgSimpleCase2.hashCode());
    }

    @Test
    void testHashCodeOneArgNested() {
        assertEquals(oneArgNestedCase.hashCode(), oneArgNestedCase2.hashCode());
    }

    @Test
    void testHashCodeMultipleArgsSimple() {
        assertEquals(multiArgsSimpleCase.hashCode(), multiArgsSimpleCase2.hashCode());
    }

    @Test
    void testHashCodeMultipleArgsNested() {
        assertEquals(multiArgsNestedCase.hashCode(), multiArgsNestedCase2.hashCode());
    }

    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(oneArgSimpleCase.hashCode(), differentCase.hashCode());
    }

    @Test
    void testHashCodeNotEqualSubset() {
        assertNotEquals(multiArgsSimpleCase.hashCode(), subsetCase1.hashCode());
    }
}

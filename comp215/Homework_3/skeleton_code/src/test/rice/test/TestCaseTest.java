package test.rice.test;

import main.rice.obj.*;
import main.rice.test.TestCase;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the TestCase class.
 */
class TestCaseTest {

    /**
     * Two identical test cases.
     */
    private static TestCase testCase;
    private static TestCase testCase2;

    /**
     * A test case that is not equivalent to testCase and testCase2.
     */
    private static TestCase testCase3;

    /**
     * A list of args comprising a test case; will ultimately correspond to testCase3.
     */
    private static List<APyObj> args;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        args = List.of(new PyIntObj(17), new PyBoolObj(true), new PyFloatObj(-183.381));
        testCase = new TestCase(args);
        testCase2 = new TestCase(new ArrayList<>(args));
        args = List.of(new PyIntObj(3), new PyBoolObj(false), new PyFloatObj(101.5));
        testCase3 = new TestCase(args);
    }

    /**
     * Tests the getArgs() method.
     */
    @Test
    void testGetArgs() {
        assertEquals(new ArrayList<>(args), testCase3.getArgs());
    }

    /**
     * Tests the toString() method.
     */
    @Test
    void testToString() {
        assertEquals("[17, True, -183.381]", testCase.toString());
    }

    /**
     * Tests the equals() method on two identical test cases.
     */
    @Test
    void testEquals() {
        assertEquals(testCase, testCase2);
    }

    /**
     * Tests the equals() method on two non-equivalent test cases.
     */
    @Test
    void testNotEqual() {
        assertNotEquals(testCase, testCase3);
    }

    /**
     * Tests the hashCode() method on two identical test cases.
     */
    @Test
    void testHashCodeEqual() {
        assertEquals(testCase.hashCode(), testCase2.hashCode());
    }

    /**
     * Tests the hashCode() method on two non-equivalent test cases.
     */
    @Test
    void testHashCodeNotEqual() {
        assertNotEquals(testCase.hashCode(), testCase3.hashCode());
    }
}
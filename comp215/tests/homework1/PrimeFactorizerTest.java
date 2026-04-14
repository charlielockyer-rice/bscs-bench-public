package test.rice;

import main.rice.PrimeFactorizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Complete test suite for the PrimeFactorizer class.
 * Tests based on COMP 215 grading rubric.
 */
public class PrimeFactorizerTest {

    /**
     * A prime factorizer with a small upper bound of 100.
     */
    private static final PrimeFactorizer smallFactorizer = new PrimeFactorizer(100);

    /**
     * A prime factorizer with a large upper bound.
     */
    private static final PrimeFactorizer largeFactorizer = new PrimeFactorizer(50000000);

    // ==================== Small Max Tests (max = 100) ====================

    /**
     * Tests that factorizing a negative number returns null.
     */
    @Test
    void testSmallMaxFactorizeNegative() {
        int[] actual = smallFactorizer.computePrimeFactorization(-1);
        assertNull(actual);
    }

    /**
     * Tests that factorizing 0 returns null.
     */
    @Test
    void testSmallMaxFactorize0() {
        int[] actual = smallFactorizer.computePrimeFactorization(0);
        assertNull(actual);
    }

    /**
     * Tests that factorizing a number greater than max returns null.
     * 101 > 100, so should return null.
     */
    @Test
    void testSmallMaxFactorize101() {
        int[] actual = smallFactorizer.computePrimeFactorization(101);
        assertNull(actual);
    }

    /**
     * Tests that factorizing a number greater than max returns null.
     * 102 > 100, so should return null.
     */
}

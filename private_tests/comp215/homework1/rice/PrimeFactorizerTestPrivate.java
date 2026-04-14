package test.rice;

import main.rice.PrimeFactorizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Complete test suite for the PrimeFactorizer class.
 * Tests based on COMP 215 grading rubric.
 */
public class PrimeFactorizerTestPrivate {

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
    void testSmallMaxFactorize102() {
        int[] actual = smallFactorizer.computePrimeFactorization(102);
        assertNull(actual);
    }

    /**
     * Tests that factorizing 1 returns null (1 has no prime factors).
     */
    @Test
    void testSmallMaxFactorize1() {
        int[] actual = smallFactorizer.computePrimeFactorization(1);
        assertNull(actual);
    }

    /**
     * Tests factorization of a small prime number.
     * 7 is prime, so [7].
     */
    @Test
    void testSmallMaxFactorize7() {
        int[] actual = smallFactorizer.computePrimeFactorization(7);
        int[] expected = new int[]{7};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of a larger prime number.
     * 71 is prime, so [71].
     */
    @Test
    void testSmallMaxFactorize71() {
        int[] actual = smallFactorizer.computePrimeFactorization(71);
        int[] expected = new int[]{71};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of a composite number.
     * 30 = 2 * 3 * 5
     */
    @Test
    void testSmallMaxFactorize30() {
        int[] actual = smallFactorizer.computePrimeFactorization(30);
        int[] expected = new int[]{2, 3, 5};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of a prime power.
     * 81 = 3^4 = 3 * 3 * 3 * 3
     */
    @Test
    void testSmallMaxFactorize81() {
        int[] actual = smallFactorizer.computePrimeFactorization(81);
        int[] expected = new int[]{3, 3, 3, 3};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of 100.
     * 100 = 2^2 * 5^2 = 2 * 2 * 5 * 5
     */
    @Test
    void testSmallMaxFactorize100() {
        int[] actual = smallFactorizer.computePrimeFactorization(100);
        int[] expected = new int[]{2, 2, 5, 5};
        assertArrayEquals(expected, actual);
    }

    // ==================== Large Max Tests ====================

    /**
     * Tests factorization of 11777 with large factorizer.
     * 11777 = 11 * 1071 = 11 * 3 * 357 = 11 * 3 * 3 * 119 = 11 * 3 * 3 * 7 * 17
     * Actually: 11777 is prime
     */
    @Test
    void testLargeMaxFactorize11777() {
        int[] actual = largeFactorizer.computePrimeFactorization(11777);
        // 11777 = 11777 (prime)
        int[] expected = new int[]{11777};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of 34534 with large factorizer.
     * 34534 = 2 * 31 * 557
     */
    @Test
    void testLargeMaxFactorize34534() {
        int[] actual = largeFactorizer.computePrimeFactorization(34534);
        // 34534 = 2 * 31 * 557
        int[] expected = new int[]{2, 31, 557};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of a power of 2.
     * 65536 = 2^16
     */
    @Test
    void testLargeMaxFactorize65536() {
        int[] actual = largeFactorizer.computePrimeFactorization(65536);
        int[] expected = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests factorization of a large composite number.
     * 42398432 = 2^5 * 1324951 = 32 * 1324951
     * 1324951 = 1324951 (prime)
     */
    @Test
    void testLargeMaxFactorize42398432() {
        int[] actual = largeFactorizer.computePrimeFactorization(42398432);
        // 42398432 = 2^5 * 1324951 = 2*2*2*2*2 * 1324951
        int[] expected = new int[]{2, 2, 2, 2, 2, 1324951};
        assertArrayEquals(expected, actual);
    }
}

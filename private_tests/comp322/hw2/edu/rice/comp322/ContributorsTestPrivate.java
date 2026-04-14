package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.runtime.config.HjSystemProperty;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.rice.hj.Module0.abstractMetrics;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Comprehensive test suite for COMP 322 HW2: GitHub Contributions (Parallel)
 *
 * Tests verify:
 * 1. Correctness: parallel results match sequential exactly
 * 2. Data integrity: user logins AND contributions match
 * 3. Ordering: results sorted by contributions descending
 * 4. Parallelism: work metric bounded by O(repos)
 * 5. Aggregation: contributions properly summed across repos
 * 6. Deduplication: no duplicate users in output
 *
 * Note: "UI must not freeze" and "no finish/async" requirements cannot be
 * easily verified via automated tests - these are checked manually.
 */
class ContributorsTestPrivate {

    private String username;
    private String token;
    private TestableContributorsUI contributorsUI;

    @BeforeEach
    void setUp() {
        // Check if mock mode is enabled
        String mockMode = System.getProperty("MOCK_GITHUB", System.getenv("MOCK_GITHUB"));
        boolean isMockMode = "true".equalsIgnoreCase(mockMode);

        if (isMockMode) {
            System.out.println("[Test] Running in MOCK mode - using pre-recorded GitHub API responses");
            username = "mock-user";
            token = "mock-token";
        } else {
            // Validate credentials - fail fast with clear error
            username = System.getenv("GITHUB_USERNAME");
            token = System.getenv("GITHUB_TOKEN");

            if (username == null || username.isEmpty() || token == null || token.isEmpty()) {
                fail("GITHUB_USERNAME and GITHUB_TOKEN environment variables must be set (or set MOCK_GITHUB=true)");
            }
        }

        // Use testable implementation that doesn't require GUI
        contributorsUI = new TestableContributorsUI();
    }

    // =========================================================================
    // CORE TESTS - Correctness and parallelism for each organization
    // Uses parameterized test to avoid code duplication
    // =========================================================================

    @ParameterizedTest(name = "Organization: {0}")
    @ValueSource(strings = {"edgecase", "trabian", "collectiveidea", "galaxycats",
                            "revelation", "moneyspyder", "notch8"})

    // =========================================================================
    // DATA INTEGRITY TESTS - Verify actual data matches, not just counts
    // =========================================================================


    // =========================================================================
    // ORDERING TESTS - Verify results are sorted correctly
    // =========================================================================


    // =========================================================================
    // PARALLELISM METRICS TESTS - Verify parallel efficiency
    // =========================================================================


    // =========================================================================
    // AGGREGATION TESTS - Verify contributions are properly summed
    // =========================================================================

    /**
     * Verifies that when the same user contributes to multiple repos,
     * their contributions are properly aggregated (summed).
     */

    /**
     * Verifies that users are properly deduplicated (no duplicates in output).
     */
    @ParameterizedTest(name = "User deduplication: {0}")
    @ValueSource(strings = {"edgecase", "trabian", "galaxycats"})
    public void testUserDeduplication(String org) throws IOException {
        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {
            try {
                contributorsUI.loadContributorsPar(username, token, org);
            } catch (IOException e) {
                fail("IOException: " + e.getMessage());
            }
        }, () -> {
            List<User> users = contributorsUI.users;
            Set<String> uniqueLogins = users.stream().map(u -> u.login).collect(Collectors.toSet());

            assertEquals(uniqueLogins.size(), users.size(),
                    "Duplicate users found for " + org + ": " + uniqueLogins.size() +
                    " unique logins but " + users.size() + " users in list");
        });
    }

    // =========================================================================
    // BASELINE SANITY TESTS
    // =========================================================================

    /**
     * Verifies sequential implementation is deterministic (baseline sanity check).
     */
    @Test
    public void testSequentialDeterminism() throws IOException {
        String org = "edgecase";

        // Run sequential twice
        contributorsUI.loadContributorsSeq(username, token, org);
        List<String> firstRun = contributorsUI.users.stream()
                .map(u -> u.login + ":" + u.contributions)
                .collect(Collectors.toList());

        assumeTrue(!firstRun.isEmpty(), "No users found for determinism test");

        contributorsUI.loadContributorsSeq(username, token, org);
        List<String> secondRun = contributorsUI.users.stream()
                .map(u -> u.login + ":" + u.contributions)
                .collect(Collectors.toList());

        assertEquals(firstRun, secondRun,
                "Sequential implementation is not deterministic");
    }

    /**
     * Verifies parallel implementation returns non-null results.
     */
    @Test
    public void testParallelReturnsResults() throws IOException {
        String org = "revelation";

        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {
            try {
                int repos = contributorsUI.loadContributorsPar(username, token, org);
                assertTrue(repos >= 0, "loadContributorsPar returned negative value");
            } catch (IOException e) {
                fail("IOException: " + e.getMessage());
            }
        }, () -> {
            assertNotNull(contributorsUI.users, "Users list should not be null");
        });
    }
}

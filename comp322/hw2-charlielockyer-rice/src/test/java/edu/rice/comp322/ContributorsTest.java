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
class ContributorsTest {

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
    public void testOrganizationCorrectnessAndParallelism(String org) throws IOException {
        // Run sequential version first
        double reposSize = contributorsUI.loadContributorsSeq(username, token, org);
        List<User> usersSeq = new ArrayList<>(contributorsUI.users);

        assumeTrue(reposSize > 0, "No repositories found for " + org);

        HjSystemProperty.abstractMetrics.setProperty(true);
        HjSystemProperty.asyncSpawnCostMetrics.setProperty(1);

        launchHabaneroApp(() -> {
            try {
                int reposSizePar = contributorsUI.loadContributorsPar(username, token, org);
                assertTrue(reposSizePar != 0,
                    "No parallel implementation or repos found for " + org);
            } catch (IOException e) {
                fail("IOException during parallel load for " + org + ": " + e.getMessage());
            }
        }, () -> {
            HjMetrics metrics = abstractMetrics();
            double work = metrics.totalWork();
            List<User> usersPar = contributorsUI.users;

            System.out.println("Test for organization " + org + " achieved total work " +
                work + " with goal work of " + (reposSize + 1));

            // Verify basic metrics
            assertTrue(reposSize != 0 && work != 0,
                "Invalid metrics for " + org + ": reposSize=" + reposSize + ", work=" + work);

            // Verify user count matches
            assertEquals(usersSeq.size(), usersPar.size(),
                "User count mismatch for " + org + ": seq=" + usersSeq.size() + ", par=" + usersPar.size());

            // Verify work is bounded (parallel efficiency)
            assertTrue(work <= (reposSize + 1),
                "Work (" + work + ") for " + org + " exceeds goal (" + (reposSize + 1) + ")");
        });
    }

    // =========================================================================
    // DATA INTEGRITY TESTS - Verify actual data matches, not just counts
    // =========================================================================

    @ParameterizedTest(name = "Data integrity: {0}")
    @ValueSource(strings = {"edgecase", "trabian", "collectiveidea"})
    public void testDataIntegrity(String org) throws IOException {
        // Run sequential
        contributorsUI.loadContributorsSeq(username, token, org);
        Map<String, Integer> seqContributions = contributorsUI.users.stream()
                .collect(Collectors.toMap(u -> u.login, u -> u.contributions));

        assumeTrue(!seqContributions.isEmpty(),
            "Skipping data integrity test for " + org + " - no users found");

        // Run parallel
        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {
            try {
                contributorsUI.loadContributorsPar(username, token, org);
            } catch (IOException e) {
                fail("IOException for " + org + ": " + e.getMessage());
            }
        }, () -> {
            Map<String, Integer> parContributions = contributorsUI.users.stream()
                    .collect(Collectors.toMap(u -> u.login, u -> u.contributions));

            // Verify same users exist
            assertEquals(seqContributions.keySet(), parContributions.keySet(),
                    "User sets differ between sequential and parallel for " + org);

            // Verify contribution counts match for each user
            for (String login : seqContributions.keySet()) {
                assertEquals(seqContributions.get(login), parContributions.get(login),
                        "Contribution count mismatch for user '" + login + "' in " + org +
                        ": seq=" + seqContributions.get(login) + ", par=" + parContributions.get(login));
            }
        });
    }

    // =========================================================================
    // ORDERING TESTS - Verify results are sorted correctly
    // =========================================================================

    @ParameterizedTest(name = "Result ordering: {0}")
    @ValueSource(strings = {"collectiveidea", "edgecase", "trabian"})
    public void testResultOrdering(String org) throws IOException {
        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {
            try {
                int repos = contributorsUI.loadContributorsPar(username, token, org);
                assumeTrue(repos > 0, "No repos found for " + org);
            } catch (IOException e) {
                fail("IOException for " + org + ": " + e.getMessage());
            }
        }, () -> {
            List<User> users = contributorsUI.users;
            assumeTrue(users.size() >= 2, "Fewer than 2 users for " + org);

            // Verify descending order by contributions
            for (int i = 0; i < users.size() - 1; i++) {
                int curr = users.get(i).contributions;
                int next = users.get(i + 1).contributions;
                assertTrue(curr >= next,
                        "Results not sorted for " + org + ": user[" + i + "].contributions=" +
                        curr + " < user[" + (i + 1) + "].contributions=" + next);
            }
        });
    }

    // =========================================================================
    // PARALLELISM METRICS TESTS - Verify parallel efficiency
    // =========================================================================

    @ParameterizedTest(name = "Parallelism metrics: {0}")
    @ValueSource(strings = {"collectiveidea", "trabian"})
    public void testParallelismMetrics(String org) throws IOException {
        double reposSize = contributorsUI.loadContributorsSeq(username, token, org);
        int totalUsers = contributorsUI.users.size();

        assumeTrue(reposSize >= 2 && totalUsers >= 3,
            "Org " + org + " too small for parallelism test");

        HjSystemProperty.abstractMetrics.setProperty(true);
        HjSystemProperty.asyncSpawnCostMetrics.setProperty(1);

        launchHabaneroApp(() -> {
            try {
                contributorsUI.loadContributorsPar(username, token, org);
            } catch (IOException e) {
                fail("IOException: " + e.getMessage());
            }
        }, () -> {
            HjMetrics metrics = abstractMetrics();
            double work = metrics.totalWork();
            double cpl = metrics.criticalPathLength();

            System.out.printf("Parallelism metrics for '%s': work=%.0f, CPL=%.0f, parallelism=%.2f%n",
                    org, work, cpl, work / Math.max(cpl, 1));

            assertTrue(work <= (reposSize + 1),
                    "Work (" + work + ") suggests sequential execution for " + org +
                    ", expected <= " + (reposSize + 1));
        });
    }

    // =========================================================================
    // AGGREGATION TESTS - Verify contributions are properly summed
    // =========================================================================

    /**
     * Verifies that when the same user contributes to multiple repos,
     * their contributions are properly aggregated (summed).
     */
    @Test
    public void testContributionAggregation() throws IOException {
        String org = "collectiveidea";

        // Get sequential results
        contributorsUI.loadContributorsSeq(username, token, org);
        Map<String, Integer> seqContributions = new HashMap<>();
        for (User u : contributorsUI.users) {
            seqContributions.put(u.login, u.contributions);
        }

        assumeTrue(!seqContributions.isEmpty(), "No users found for aggregation test");

        // Get parallel results
        HjSystemProperty.abstractMetrics.setProperty(true);
        launchHabaneroApp(() -> {
            try {
                contributorsUI.loadContributorsPar(username, token, org);
            } catch (IOException e) {
                fail("IOException: " + e.getMessage());
            }
        }, () -> {
            // Verify total contributions across all users match
            int seqTotal = seqContributions.values().stream().mapToInt(Integer::intValue).sum();
            int parTotal = contributorsUI.users.stream().mapToInt(u -> u.contributions).sum();

            assertEquals(seqTotal, parTotal,
                    "Total contributions mismatch: seq=" + seqTotal + ", par=" + parTotal);

            // Verify no duplicate users (proper deduplication)
            Set<String> logins = new HashSet<>();
            for (User u : contributorsUI.users) {
                assertFalse(logins.contains(u.login),
                        "Duplicate user found: " + u.login + " - aggregation failed");
                logins.add(u.login);
            }
        });
    }

    /**
     * Verifies that users are properly deduplicated (no duplicates in output).
     */

    // =========================================================================
    // BASELINE SANITY TESTS
    // =========================================================================

    /**
     * Verifies sequential implementation is deterministic (baseline sanity check).
     */

    /**
     * Verifies parallel implementation returns non-null results.
     */
}

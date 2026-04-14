package edu.rice.comp322;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.SuspendableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.rice.hj.Module1.future;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Loads the contributors using the GitHub service.
 */
public interface LoadContributors {

    /**
     * Performs requests to GitHub and loads and aggregates the contributors for all
     * repositories under the given organization.
     */
    default int loadContributorsSeq(String username, String password, String org)
        throws IOException {

        //Create the service to make the requests
        GitHubService service = createGitHubService(username, password);

        //Get all the repos under the given organization
        List<Repo> repos = service.getOrgReposCall(org).execute().body();
        if (repos == null) {
            System.err.println("Error making request to GitHub. Make sure token and organization name are correct.");
            return 0;
        } else if (repos.size() == 0) {
            System.out.println("0 repositories found in " + org + " organization, make sure your token is correct.");
        } else {
            System.out.println("Found " + repos.size() + " repositories in " + org + " organization.");
        }

        //Get the contributors for each repo
        List<User> users = new ArrayList<>();
        for (Repo repo : repos) {
            List<User> tempUsers = service.getRepContributorsCall(org, repo.name).execute().body();
            if (tempUsers != null) {
                users.addAll(tempUsers);
                System.out.println("Found " + tempUsers.size() + " users in " + repo.name + " repository.");
            } else {
                System.err.println("Error making request to GitHub for repository " + repo.name);
            }
        }

        //Aggregate the number of contributions for each user
        System.out.println("Aggregating Results");
        List<User> aggregatedUsers = new ArrayList<>();
        for (User user: users) {
            if (aggregatedUsers.contains(user)) {
                aggregatedUsers.get(aggregatedUsers.indexOf(user)).contributions += user.contributions;
            } else {
                aggregatedUsers.add(user);
            }
        }

        //Sort the users in descending order of contributions
        aggregatedUsers.sort((o1, o2) -> o2.contributions - o1.contributions);
        System.out.println("Displaying Results");
        updateContributors(aggregatedUsers);
        return repos.size();
    }

    /**
     * Performs requests to GitHub and loads and aggregates the contributors for all
     * repositories under the given organization in parallel using futures.
     * <p>
     * Implementation uses HJlib futures to fetch contributors for each repository
     * concurrently, then aggregates results using streams.
     * </p>
     * <p>
     * Requirements (from assignment):
     * Use futures, data-driven tasks, and streams (not finish or async).
     * UI must not freeze when loading data.
     * Work metric should be bounded by O(repos).
     * Must produce same results as loadContributorsSeq.
     * </p>
     */
    default int loadContributorsPar(String username, String password, String org)
        throws IOException {

        // Create the service to make the requests
        GitHubService service = createGitHubService(username, password);

        // Get all the repos under the given organization (sequential - one API call)
        List<Repo> repos = service.getOrgReposCall(org).execute().body();
        if (repos == null) {
            System.err.println("Error making request to GitHub. Make sure token and organization name are correct.");
            return 0;
        } else if (repos.size() == 0) {
            System.out.println("0 repositories found in " + org + " organization, make sure your token is correct.");
            updateContributors(new ArrayList<>());
            return 0;
        } else {
            System.out.println("Found " + repos.size() + " repositories in " + org + " organization.");
        }

        // Create futures for each repo's contributors (parallel fetch)
        List<HjFuture<List<User>>> futures = new ArrayList<>();
        for (Repo repo : repos) {
            final String repoName = repo.name;
            futures.add(future(() -> {
                try {
                    List<User> users = service.getRepContributorsCall(org, repoName).execute().body();
                    if (users != null) {
                        System.out.println("Found " + users.size() + " users in " + repoName + " repository.");
                    }
                    return users;
                } catch (IOException e) {
                    System.err.println("Error making request to GitHub for repository " + repoName);
                    return null;
                }
            }));
        }

        // Collect results from all futures
        // Note: f.get() may throw SuspendableException, so we wrap it
        System.out.println("Aggregating Results");
        List<User> allUsers = new ArrayList<>();
        for (HjFuture<List<User>> f : futures) {
            try {
                List<User> users = f.get();
                if (users != null) {
                    allUsers.addAll(users);
                }
            } catch (SuspendableException e) {
                // Wrap in RuntimeException since method signature only allows IOException
                throw new RuntimeException("Failed to get future result", e);
            }
        }

        // Aggregate contributions per user using streams
        Map<String, Integer> aggregatedMap = allUsers.stream()
            .collect(Collectors.groupingBy(
                u -> u.login,
                Collectors.summingInt(u -> u.contributions)
            ));

        // Convert map back to list of Users
        List<User> aggregatedUsers = aggregatedMap.entrySet().stream()
            .map(entry -> new User(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        // Sort by contributions descending
        aggregatedUsers.sort((o1, o2) -> o2.contributions - o1.contributions);
        
        System.out.println("Displaying Results");
        updateContributors(aggregatedUsers);
        return repos.size();
    }

    /**
     * Creates the GitHub service with correct authorization.
     * <p>
     * If the system property "MOCK_GITHUB" is set to "true", uses pre-recorded
     * mock responses instead of making real API calls. This enables testing
     * without GitHub credentials.
     * </p>
     */
    default GitHubService createGitHubService(String username, String password) {
        String authToken = "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes()), UTF_8);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        // Check for mock mode (set via -DMOCK_GITHUB=true or environment variable)
        String mockMode = System.getProperty("MOCK_GITHUB", System.getenv("MOCK_GITHUB"));
        if ("true".equalsIgnoreCase(mockMode)) {
            clientBuilder.addInterceptor(new MockGitHubInterceptor());
            System.out.println("[MockMode] Using pre-recorded GitHub API responses");
        }

        // Add auth header interceptor (still needed for request structure, even if mocked)
        clientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", authToken);
            Request request = builder.build();
            return chain.proceed(request);
        });

        OkHttpClient httpClient = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
        return retrofit.create(GitHubService.class);
    }

    /**
     * Updates the contributors list displayed on the user-interface.
     * @param users a list of Users
     */
    void updateContributors(List<User> users);

}

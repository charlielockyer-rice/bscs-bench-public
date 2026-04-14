package edu.rice.comp322;

import okhttp3.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

/**
 * OkHttp Interceptor that returns pre-recorded GitHub API responses.
 * <p>
 * Intercepts requests to api.github.com and returns mock data from
 * src/main/resources/mock-responses/ directory.
 * </p>
 * <p>
 * Supported endpoints: GET /orgs/{org}/repos returns {org}_repos.json,
 * GET /repos/{org}/{repo}/contributors returns {org}/{repo}_contributors.json.
 * </p>
 */
public class MockGitHubInterceptor implements Interceptor {

    private static final Pattern REPOS_PATTERN =
        Pattern.compile("/orgs/([^/]+)/repos");
    private static final Pattern CONTRIBUTORS_PATTERN =
        Pattern.compile("/repos/([^/]+)/([^/]+)/contributors");

    private final String mockDataDir;

    public MockGitHubInterceptor() {
        // Default to classpath resources
        this.mockDataDir = null;
    }

    public MockGitHubInterceptor(String mockDataDir) {
        this.mockDataDir = mockDataDir;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();

        // Try to match repos endpoint
        Matcher reposMatcher = REPOS_PATTERN.matcher(path);
        if (reposMatcher.matches()) {
            String org = reposMatcher.group(1);
            String json = loadMockData(org + "_repos.json");
            if (json != null) {
                return createMockResponse(request, json);
            }
        }

        // Try to match contributors endpoint
        Matcher contribMatcher = CONTRIBUTORS_PATTERN.matcher(path);
        if (contribMatcher.matches()) {
            String org = contribMatcher.group(1);
            String repo = contribMatcher.group(2);
            String json = loadMockData(org + "/" + repo + "_contributors.json");
            if (json != null) {
                return createMockResponse(request, json);
            }
        }

        // No mock data found - return empty array
        System.err.println("MockGitHubInterceptor: No mock data for " + path);
        return createMockResponse(request, "[]");
    }

    private String loadMockData(String filename) {
        try (InputStream is = openMockDataStream(filename)) {
            if (is == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            System.err.println("MockGitHubInterceptor: Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }

    private InputStream openMockDataStream(String filename) throws IOException {
        if (mockDataDir != null) {
            File file = new File(mockDataDir, filename);
            if (!file.exists()) {
                return null;
            }
            return new FileInputStream(file);
        } else {
            return getClass().getResourceAsStream("/mock-responses/" + filename);
        }
    }

    private Response createMockResponse(Request request, String json) {
        return new Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(ResponseBody.create(MediaType.parse("application/json"), json))
            .build();
    }
}

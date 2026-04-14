package edu.rice.comp322;

import java.util.ArrayList;
import java.util.List;

/**
 * Test-only implementation of LoadContributors that doesn't require a GUI.
 * <p>
 * This class implements the same interface as ContributorsUI but without
 * extending JFrame, allowing tests to run in headless Docker environments.
 * </p>
 */
public class TestableContributorsUI implements LoadContributors {

    public List<User> users = new ArrayList<>();

    @Override
    public void updateContributors(List<User> users) {
        this.users = new ArrayList<>(users);
    }
}

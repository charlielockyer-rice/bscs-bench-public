package main.rice.test;

import java.io.File;
import java.io.IOException;

/**
 * A class for running a test suite. Encapsulates the ability to run the test suite on a
 * reference implementation to generate the expected results, the ability to create a
 * "wrapper" file for comparing actual results to these expected results, and the ability
 * to run the test suite on a set of files and identify which test cases each file fails
 * on.
 */
public class Tester {
    // TODO: complete the definition of the Tester class here

    /**
     * The absolute path to the directory containing the student implementations.
     */
    private String implDirPath;

    /**
     * Helper function for deleting all cached Python files, so that an old cached version
     * of expected.pyc doesn't accidentally get invoked.
     *
     * @throws IOException if the path to the pycache is invalid or a deletion operation
     *                     fails
     */
    private void deletePyCache() throws IOException {
        // Get the list of all files in the pycache
        File pyCacheDir = new File(this.implDirPath + "/__pycache__/");
        String[] filepaths = pyCacheDir.list();

        if (filepaths != null) {
            for (String filepath : filepaths) {
                // Only delete .pyc files
                if (filepath.contains(".pyc")) {
                    File cachedFile =
                            new File(this.implDirPath + "/__pycache__/" + filepath);
                    if (!cachedFile.delete()) {
                        throw new IOException("could not delete cached file " + filepath);
                    }
                }
            }
        }
    }
}
package edu.rice.comp322;

import junit.framework.TestCase;

import java.util.Random;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * This is a test class for your homework and should not be modified.
 *
 * @author Vivek Sarkar (vsarkar@rice.edu)
 */
public class Homework3Checkpoint2CorrectnessTestPrivate extends TestCase {


    public void testUsefulParScoring3() {
        final int xLength = 100;
        final int yLength = 120;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }

    public void testUsefulParScoring4() {
        final int xLength = 120;
        final int yLength = 100;

        final IScoringConstructor seqConstructor = TestSeqScoring::new;
        final IScoringConstructor parConstructor = UsefulParScoring::new;

        Homework3Checkpoint1CorrectnessTest.kernel(xLength, yLength, seqConstructor, parConstructor, false);
    }
}

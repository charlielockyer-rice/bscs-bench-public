package provided.config.test;

import provided.config.impl.AppConfigChooser;
import provided.config.impl.AppConfigRun;


/**
 * Simple test of AppConfigRun's 
 * @author swong
 *
 */
public class AppConfigRunTest {

	/**
	 * Constructor for the class
	 */
	public AppConfigRunTest() {
	}

	/**
	 * Test function that pops up an AppConfig chooser dialog and then runs the chosen app, defaulting to 
	 * the first app (index=0) if the dialog is cancelled.
	 * Run this test with a run/launch configuration with command line parameters to see the extra parameters 
	 * being appended to the configured parameters.  
	 * @param args  Additional arguments passed to the chosen configuration when it is run.
	 */
	public static void main(String[] args) {
		try {
			// Creating a pop-up chooser.  The AppConfigRun instantiations need to be done inside of the try-catch.
			AppConfigChooser<AppConfigRun> appChooser =  new AppConfigChooser<AppConfigRun>(0,  // Defaults to the first app
					new AppConfigRun("TestApp1", TestApp1.class, "a", "b"),   // The command line parameters can be customized per app
					new AppConfigRun("TestApp2", TestApp2.class, "x", "y", "z")
				);
	
			appChooser.choose().run(args); // Run the chosen app's main() method with its configured command line parameters plus this app's command line parameters appended to them.
		} catch (Exception e) {  // Catches when main() doesn't exist in the AppConfigRun's apps or if main() has problems executing.
			System.err.println("[AppConfigRunTest.main()] Exception = "+e);
			e.printStackTrace();
		}
	}

}


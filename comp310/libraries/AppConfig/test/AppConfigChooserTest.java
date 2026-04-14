package provided.config.test;

import provided.config.AppConfig;
import provided.config.impl.AppConfigChooser;

/**
 * Simple test of AppConfigChooser
 * @author swong
 *
 */
public class AppConfigChooserTest {

	/**
	 * Constructor for the class
	 */
	public AppConfigChooserTest() {
	}

	/**
	 * Test function to demonstrate using the chooser.  Run as a Java application. 
	 * In a real application, the code in this method would not necessarily be in main() but 
	 * rather in a location that gave the app configs any necessary access to required entities, 
	 * e.g. in the the controller's constructor and/or start() method.  
	 * @param args Not used
	 */
	public static void main(String[] args) {
		// The generic typing would normally be set to the specific AppConfig subclass being used.  
		// Create an app config chooser.  This is typically a field somewhere.
		AppConfigChooser<AppConfig> appChooser =  new AppConfigChooser<AppConfig>( // Can add default choice index parameter here if desired
				new AppConfig("Choice 1") {},	// no-op AppConfig subclasses being used for demonstration purposes only.
				new AppConfig("Choice 2") {},
				new AppConfig("Choice 3") {},
				new AppConfig("Choice 4") {});
		
		AppConfig chosenConfig = appChooser.choose();
		System.out.println("choice = "+chosenConfig);
		
	}

}

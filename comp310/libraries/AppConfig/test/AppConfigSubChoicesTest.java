package provided.config.test;

import provided.config.AppConfig;
import provided.config.impl.AppConfigChooser;
import provided.config.impl.AppConfigSubChoices;

/**
 * Just an example AppConfig subclass
 * @author swong
 *
 */
class MyAppConfig extends AppConfig {

	/**
	 * Constructor for the class
	 * @param name A name for this app config
	 */
	public MyAppConfig(String name) {
		super(name);
	}

}

/**
 * Simple test of AppConfigSubChoices
 * @author swong
 *
 */
public class AppConfigSubChoicesTest {

	/**
	 * Constructor for the class
	 */
	public AppConfigSubChoicesTest() {
	}

	/**
	 * Test function to demonstrate using AppConfigSubChoices with an AppConfigChooser.  Run as a Java application. 
	 * In a real application, the code in this method would not necessarily be in main() but 
	 * rather in a location that gave the app configs any necessary access to required entities, 
	 * e.g. in the the controller's constructor and/or start() method.  
	 * @param args Not used
	 */
	public static void main(String[] args) {

		AppConfigChooser<AppConfigSubChoices<MyAppConfig>> appChooser =  new AppConfigChooser<>(
				new AppConfigSubChoices<>("Set #1",
						new MyAppConfig("Set #1: Choice A"),
						new MyAppConfig("Set #1: Choice B")),
				new AppConfigSubChoices<>("Set #2",
						new MyAppConfig("Set #2: Choice A"),
						new MyAppConfig("Set #2: Choice B"),
						new MyAppConfig("Set #2: Choice C")),
				new AppConfigSubChoices<>("Set #3", 1, // Second choice is default if chooser is cancelled  
						new MyAppConfig("Set #3: Choice A"),
						new MyAppConfig("Set #3: Choice B"))
				);
		
		MyAppConfig chosenConfig = appChooser.choose().choose();
		System.out.println("choice = "+chosenConfig);
		
	}

}

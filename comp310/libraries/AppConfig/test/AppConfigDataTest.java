package provided.config.test;

import provided.config.impl.AppConfigChooser;
import provided.config.impl.AppConfigData;
import provided.mixedData.MixedDataKey;
import provided.rmiUtils.IRMI_Defs;

/**
 * Simple test of AppConfigData's
 * @author swong
 *
 */
public class AppConfigDataTest {
	
	/**
	 * 
	 */
	// Define the key names used to avoid "magic values":

	/**
	 * Key name for the String username
	 */
	public static final String USERNAME_KEYNAME = "Username";
	
	/**
	 * Key name for the integer stub port
	 */
	public static final String STUB_PORT_KEYNAME = "StubPort";
	
	/**
	 * Key name for the integer class server port
	 */
	public static final String CLASS_SERVER_PORT_KEYNAME = "ClassServerPort";
	
	/**
	 * Key name for the Runnable function
	 */
	public static final String RUN_FN_KEYNAME = "RunFn";

	/**
	 * Constructor for the class.
	 */
	public AppConfigDataTest() {
	}

	/**
	 * Test method to demonstrate the use of AppConfigData's
	 * In a real application, the code in this method would not necessarily be in main() but 
	 * rather in a location that gave the app configs any necessary access to required entities, 
	 * e.g. in the the controller's constructor and/or start() method. 
	 * @param args Not used.
	 */
	public static void main(String[] args) {

		
		// Make an app config chooser.  This is typically a field somewhere.
		AppConfigChooser<AppConfigData> appChooser =  new AppConfigChooser<AppConfigData>(
				
				// Note: the individual AppConfigData's could hold completely different key-value pairs if desired.
				
				new AppConfigData("Data 1") {
					/**
					 * For serialization
					 */
					private static final long serialVersionUID = -6479001662848948503L;
		
					{
						// Load the key-values in this initializer block using this config's UUID
						put(makeStrKey(USERNAME_KEYNAME), "User_X" );
						put(makeIntKey(STUB_PORT_KEYNAME), IRMI_Defs.STUB_PORT_CLIENT );
						put(makeIntKey(CLASS_SERVER_PORT_KEYNAME), IRMI_Defs.CLASS_SERVER_PORT_CLIENT );
						put(new MixedDataKey<Runnable>(this.getUUID(),RUN_FN_KEYNAME, Runnable.class), ()->{
							System.out.println("[Data 1 RunFn] Running!");
						});
					}
				},
				new AppConfigData("Data 2") {
					/**
					 * For serialization
					 */
					private static final long serialVersionUID = 1614284594675970639L;
		
					{
						// Load the key-values in this initializer block, using the config's UUID
						put(makeStrKey(USERNAME_KEYNAME), "User_Y" );
						put(makeIntKey(STUB_PORT_KEYNAME), IRMI_Defs.STUB_PORT_SERVER );
						put(makeIntKey(CLASS_SERVER_PORT_KEYNAME), IRMI_Defs.CLASS_SERVER_PORT_SERVER );
						put(new MixedDataKey<Runnable>(this.getUUID(),RUN_FN_KEYNAME, Runnable.class), ()->{
							System.out.println("[Data 2 RunFn] Running!");
						});
					}
				}
			);
		
			AppConfigData chosenConfig = appChooser.choose();  // Choose an app config

			System.out.println(USERNAME_KEYNAME +" = "+ chosenConfig.get(chosenConfig.makeStrKey(USERNAME_KEYNAME)));
			
			System.out.println(STUB_PORT_KEYNAME +" = "+ chosenConfig.get(chosenConfig.makeIntKey(STUB_PORT_KEYNAME)));
			
			System.out.println(CLASS_SERVER_PORT_KEYNAME + " = "+ chosenConfig.get(chosenConfig.makeIntKey(CLASS_SERVER_PORT_KEYNAME)));
			
			chosenConfig.get(new MixedDataKey<Runnable>(chosenConfig.getUUID(),RUN_FN_KEYNAME, Runnable.class)).run();
			
	}

}

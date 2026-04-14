package provided.rmiUtils.examples.hello_discovery.server.controller;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.rmiUtils.examples.hello_common.IHello;
import provided.config.impl.AppConfigChooser;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryModelPubOnly;
import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import provided.rmiUtils.examples.hello_discovery.server.model.HelloModelServer;
import provided.rmiUtils.examples.hello_discovery.server.model.IModel2ViewAdapterServer;
import provided.rmiUtils.examples.hello_discovery.server.view.HelloViewServer;
import provided.rmiUtils.examples.hello_discovery.server.view.IView2ModelAdapterServer;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * @author swong
 *
 */
public class HelloAppServer {
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();

	/**
	 * The Discovery server UI panel for the view
	 */
	private DiscoveryPanel<IEndPointData> discPnl;

	/**
	 * A self-contained model to handle the discovery server.   MUST be started AFTER the main model as it needs the IRMIUtils from the main model! 
	 */
	private DiscoveryModelPubOnly<IHello> discModel;  // Replace "IHello" with the appropriate for the application, i.e. the Remote type of stub in Registry)  

	/**
	 * The MVC's view module
	 */
	private HelloViewServer view;

	/**
	 * The MVC's model object
	 */
	private HelloModelServer model;
	
	/**
	 * The selected app configuration holding the configuration-dependent information
	 */
	private RMIPortConfigWithBoundName currentConfig;
	
	/**
	 * 3 possible app configs with different config names, port numbers and bound names.
	 */
	AppConfigChooser<RMIPortConfigWithBoundName> appChooser =  new AppConfigChooser<RMIPortConfigWithBoundName>( // Can add default choice index parameter here if desired
			new RMIPortConfigWithBoundName("RMIHello_Demo_Server-Server_port", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER, "Hello_1"),	
			new RMIPortConfigWithBoundName("RMIHello_Demo_Server-Client_port", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT, "Hello_2"),
			new RMIPortConfigWithBoundName("RMIHello_Demo_Server-Extra_port", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA, "Hello_3")
	);	


	/**
	 * Constructor of the class.   Instantiates and connects the model and the view plus the discovery panel and model.
	 */
	public HelloAppServer() {
		sysLogger.setLogLevel(LogLevel.DEBUG);  // For debugging purposes.   Default is LogLevel.INFO

		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = appChooser.choose(); // Have the user select a configuration.
		sysLogger.log(LogLevel.INFO, "Selected app config: "+currentConfig);

		discPnl = new DiscoveryPanel<IEndPointData>( new IDiscoveryPanelAdapter<IEndPointData>() {

			/**
			 * watchOnly parameter is ignored because DiscoveryModelPubOnly assumes watchOnly = false.
			 */
			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// Ask the discovery model to connect to the discovery server on the given category and use the given updateFn to update the endpoints list in the discovery panel.
				discModel.connectToDiscoveryServer(category, endPtsUpdateFn);

			}

			/**
			 * This method is never called in "Server" usage mode
			 */
			@Override
			public void connectToEndPoint(IEndPointData selectedValue) {
				System.err.println("[IDiscoveryPanelAdapter.connectToEndPoint()] DiscoveryModelPubOnly does not support connecting to endpoints!");
			}

		}, true, false);  // "Server" usage mode

		discModel = new DiscoveryModelPubOnly<IHello>(sysLogger);  // No adapter needed b/c endpoint connection not supported

		model = new HelloModelServer(sysLogger, currentConfig, new IModel2ViewAdapterServer() {

			@Override
			public void displayMsg(String msg) {
				view.append(msg);
			}
			
		});
		view = new HelloViewServer(new IView2ModelAdapterServer() {

			@Override
			public void quit() {
				model.quit(0); // normal exit code
			}

			
		});
		
	}

	/**
	 * Starts the view then the model plus the discovery panel and model.  The view needs to be started first so that it can display 
	 * the model status updates as it starts.   The discovery panel is added to the main view after the discovery model starts. 
	 */
	public void start() {
		// start the main view and model. THE MODEL MUST BE STARTED _BEFORE_  model.getRMIUtils() IS CALLED!!
		model.start();   // starts the internal IRMIUtils instance too.

		discPnl.start();  // start the discovery panel
		// Start the discovery model with info about the published endpoint, substituting the appropriate values for the application's friendly name (to appear in discovery server) and bound name for the stub in the local Registry.
		discModel.start(model.getRMIUtils(), currentConfig.name, currentConfig.boundName); 
		view.addCtrlComponent(discPnl);  // Add the discovery panel to the view's "control" panel.

		// start the main view.  Starting the view here will keep the view from showing before the discovery panel is installed.
		view.start();
	}
	
	/**
	 * Run the app.
	 * @param args Not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new HelloAppServer()).start();
			}
		});
	}
}

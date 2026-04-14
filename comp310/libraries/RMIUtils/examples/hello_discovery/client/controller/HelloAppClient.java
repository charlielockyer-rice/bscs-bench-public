package provided.rmiUtils.examples.hello_discovery.client.controller;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.examples.hello_common.IHello;
import provided.config.impl.AppConfigChooser;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryModelWatchOnly;
import provided.discovery.impl.model.IDiscoveryModelToViewAdapter;
import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import provided.rmiUtils.examples.hello_discovery.client.model.HelloModelClient;
import provided.rmiUtils.examples.hello_discovery.client.model.IModel2ViewAdapterClient;
import provided.rmiUtils.examples.hello_discovery.client.view.HelloViewClient;
import provided.rmiUtils.examples.hello_discovery.client.view.IView2ModelAdapterClient;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;


/**
 * @author swong
 *
 */
public class HelloAppClient {
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	
	/**
	 * The model in use
	 */
	private HelloModelClient model;
	
	/**
	 * The view in use
	 */
	private HelloViewClient view;

	/**
	 * The Discovery server UI panel for the view
	 */
	private DiscoveryPanel<IEndPointData> discPnl;
	
	/**
	 * A self-contained model to handle the discovery server.   MUST be started AFTER the main model as it needs the IRMIUtils from the main model! 
	 */
	private DiscoveryModelWatchOnly<IHello> discModel;  // Replace "IRemoteStubType" with the appropriate for the application, i.e. the Remote type of stub in Registry)  

	/**
	 * The selected app configuration holding the configuration-dependent information.
	 * Using the simpler RMIPortConfig because this client is watch-only and not binding 
	 * anything into the local Registry and thus doesn't need a bound name to be defined.
	 * Technically, this client doesn't need the stub port either.
	 */
	private RMIPortConfig currentConfig;
	
	/**
	 * 3 possible app configs with different config names and port numbers.
	 */
	AppConfigChooser<RMIPortConfig> appChooser =  new AppConfigChooser<RMIPortConfig>( // Can add default choice index parameter here if desired
			new RMIPortConfig("RMIHello_Demo_Client-Server_port", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER),	
			new RMIPortConfig("RMIHello_Demo_Client-Client_port", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT),
			new RMIPortConfig("RMIHello_Demo_Client-Extra_port", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA)
	);	
	
	/**
	 * Constructor of the class.   Instantiates and connects the model and the view plus the discovery panel and model.
	 */
	public HelloAppClient() {
		
		sysLogger.setLogLevel(LogLevel.DEBUG);  // For debugging purposes.   Default is LogLevel.INFO
		
		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = appChooser.choose(); // Have the user select a configuration.
		sysLogger.log(LogLevel.INFO, "Selected app config: "+currentConfig);
		
		discPnl = new DiscoveryPanel<IEndPointData>( new IDiscoveryPanelAdapter<IEndPointData>() {

			/**
			 * watchOnly is ignored b/c discovery model configured for watchOnly = true
			 */
			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// Ask the discovery model to connect to the discovery server on the given category and use the given updateFn to update the endpoints list in the discovery panel.
				discModel.connectToDiscoveryServer(category, endPtsUpdateFn);
			}

			@Override
			public void connectToEndPoint(IEndPointData selectedEndPt) {
				// Ask the discovery model to obtain a stub from a remote Registry using the info from the given endpoint 
				discModel.connectToEndPoint(selectedEndPt);
			}
			
		}, false, true);  // "Client" usage mode
		
		discModel = new DiscoveryModelWatchOnly<IHello>(sysLogger, new IDiscoveryModelToViewAdapter<IHello>() {

			@Override
			public void addStub(IHello stub) {
				model.connectToStub(stub);   // Give the stub obtained from a remote Registry to the model to process
			}
			
		});

		model = new HelloModelClient(sysLogger, currentConfig, new IModel2ViewAdapterClient() {

			@Override
			public void displayMsg(String msg) {
				view.append(msg);
			}
			
		});
		view = new HelloViewClient(new IView2ModelAdapterClient() {

			@Override
			public String connectTo(String remoteIP) {
				return model.connectTo(remoteIP);
			}

			@Override
			public void sayHello() {
				model.sayHello();
			}

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
		// start the main model.  THE MODEL MUST BE STARTED _BEFORE_  model.getRMIUtils() IS CALLED!!
		model.start();   // starts the internal IRMIUtils instance too.
		
		discPnl.start();  // start the discovery panel
		discModel.start(model.getRMIUtils());   // start the discovery model using the already started IRMIUtils instance.
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
				(new HelloAppClient()).start();
			}
		});
	}

}

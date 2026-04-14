package provided.rmiUtils.logger.central_logger.server.controller;

import javax.swing.SwingUtilities;

import provided.config.impl.AppConfigChooser;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.logger.central_logger.server.model.IModel2ViewAdapter;
import provided.rmiUtils.logger.central_logger.server.model.MainModel;
import provided.rmiUtils.logger.central_logger.server.view.IView2ModelAdapter;
import provided.rmiUtils.logger.central_logger.server.view.MainFrame;

/**
 * A  stand-alone application providing remote logging services.   An IRemoteLogService stub is bound to the local RMI Registry
 * under the name given by IRemoteLogService.DEFAULT_BOUND_NAME 
 * @author swong
 *
 */
public class CentralLoggerApp {
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	
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
			new RMIPortConfig("CentralLoggerApp-Server_port", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER),	
			new RMIPortConfig("CentralLoggerApp-Client_port", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT),
			new RMIPortConfig("CentralLoggerApp-Extra_port", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA)
	);	
	
	/**
	 * The view in use
	 */
	private MainFrame view;
	
	/**
	 * The model in use.
	 */
	private MainModel model;
	
	/**
	 * Constructor for the class
	 */
	
	public CentralLoggerApp() {
		
		sysLogger.setLogLevel(LogLevel.DEBUG);  // For debugging purposes.   Default is LogLevel.INFO
		
		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = appChooser.choose(); // Have the user select a configuration.
		sysLogger.log(LogLevel.INFO, "Selected app config: "+currentConfig);	
		
		
		view = new MainFrame(new IView2ModelAdapter() {

			@Override
			public void test() {
				model.localTest();
			}

			@Override
			public void setLogLevel(LogLevel logLevel) {
				model.setLogLevel(logLevel);
			}});
		
		model = new MainModel(currentConfig, new IModel2ViewAdapter() {

			@Override
			public ILogEntryProcessor getLogEntryProcessor() {
				return view.getLogEntryProcessor();
			}

			@Override
			public void setIP_Ports(String ipAddr, int stubPort, int classServerPort) {
				view.setIP_Port(ipAddr, stubPort, classServerPort);
			}


			
		});
	}
	
	/**
	 * Start the app
	 */
	public void start() {
		view.start();
		model.start();
	}
	
	/**
	 * Run the application 
	 * @param args Not used.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new CentralLoggerApp()).start();
			}
		});
	}

}

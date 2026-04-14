package provided.rmiUtils.logger.central_logger.client.controller;

import javax.swing.SwingUtilities;

import provided.config.impl.AppConfigChooser;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.logger.IRemoteLogService;
import provided.rmiUtils.logger.central_logger.client.model.IModel2ViewAdapter;
import provided.rmiUtils.logger.central_logger.client.model.MainModel;
import provided.rmiUtils.logger.central_logger.client.view.IView2ModelAdapter;
import provided.rmiUtils.logger.central_logger.client.view.MainFrame;

/**
 * Test client to test a remote logging service provided by an IRemoteLogService implementation.
 * @author swong
 *
 */
public class RemoteLoggingServiceTestClientApp {
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
			new RMIPortConfig("RemoteLoggingServiceTestClient-Server_port", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER),	
			new RMIPortConfig("RemoteLoggingServiceTestClient-Client_port", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT),
			new RMIPortConfig("RemoteLoggingServiceTestClient-Extra_port", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA)
	);	
	
	/**
	 * The view in use
	 */
	private MainFrame view;
	
	/**
	 * The model in use
	 */
	private MainModel model;
	
	/**
	 * Constructor for the class
	 * @param boundName The bound name of the remote central log service to use
	 */
	public RemoteLoggingServiceTestClientApp(String boundName) {
		
		sysLogger.setLogLevel(LogLevel.DEBUG);  // For debugging purposes.   Default is LogLevel.INFO
		
		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = appChooser.choose(); // Have the user select a configuration.
		sysLogger.log(LogLevel.INFO, "Selected app config: "+currentConfig);	
		
		
		view = new MainFrame(new IView2ModelAdapter() {
			@Override
			public void connectTo(String ipAddr) {
				model.connectTo(ipAddr);
			}

			@Override
			public void logMsg(LogLevel logLevel, String msg) {
				model.logMsg(logLevel, msg);
			}});
		
		model = new MainModel(boundName, currentConfig, new IModel2ViewAdapter() {

			@Override
			public ILogEntryProcessor getLogEntryProcessor() {
				return view.getLogEntryProcessor();
			}

			@Override
			public void setAddr(String addr) {
				view.setAddr(addr);
			}

			@Override
			public void enableSendMsg() {
				view.enableSendMsg();
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
	 * Run the app
	 * @param args If present, args[0] is the RMI Registry bound name to use to locate the remote log service.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new RemoteLoggingServiceTestClientApp( args.length>0? args[0]: IRemoteLogService.DEFAULT_BOUND_NAME)).start();
			}
		});
	}

}

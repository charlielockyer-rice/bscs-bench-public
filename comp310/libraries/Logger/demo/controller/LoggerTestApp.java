package provided.logger.demo.controller;

import javax.swing.SwingUtilities;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.logger.demo.model.IModel2ViewAdapter;
import provided.logger.demo.model.MainModel;
import provided.logger.demo.view.IView2ModelAdapter;
import provided.logger.demo.view.MainFrame;
import provided.logger.util.LoggerPanel;

/**
 * The controller for the demo app 
 * @author swong
 *
 */
public class LoggerTestApp {
	/**
	 * The global singleton shared logger, obtained from ILoggerControl.   Note that this logger defaults 
	 * to a LogLevel.INFO minimum level.  Rather than have the model and view modules directly access this logger,
	 * the controller is the sole manager of it and hands it to the various modules of the system.   This is a 
	 * much more extensible method than having the modules directly accessing the shared logger because the constructor
	 * can change how the logger is handled without disturbing the modules.
	 */
	private ILogger sharedSystemLogger = ILoggerControl.getSharedLogger();

	/**
	 * A logger that is not the globally shared logger that the controller will chain after the global logger.  
	 * This logger is being set to a minimum log level of LogLevel.DEBUG so it will naturally show more log entries than sharedSystemLogger.
	 * Note the use of explicit positional arguments in the format string (e.g. "%2$") that enable total control 
	 * of exactly which values are used and where they are placed in the output string: 
	 * "[Non-Shared Logger] LogLevel = "+log_level+": "+ log_msg+" @ "+ code_loc  
	 */
	private ILogger nonSharedLogger = ILoggerControl.makeLogger(
			ILogEntryProcessor
					.MakeDefault(ILogEntryFormatter.MakeFormatter("[Non-Shared Logger] LogLevel = %1$s: %2$s @ %3$s")),
			LogLevel.DEBUG);

	/**
	 * The view
	 */
	private MainFrame view;

	/**
	 * The model
	 */
	private MainModel model;

	/**
	 * The logger panel that will be embedded in the view
	 */
	private LoggerPanel pnlLog;

	/**
	 * Construct the application
	 */
	public LoggerTestApp() {

		/** 
		 * Note that it is a better practice to pass the system logger to ALL the modules in the system,
		 * such as is done with the model below.  To illustrate that the global, singleton logger (systemLogger, here) 
		 * can be accessed statically, it is not being passed here and the MainFrame will access it directly through 
		 * ILoggerControl.getSharedLogger().  Architecturally, it would be better practice to pass the system logger to the 
		 * view here.   
		 */
		view = new MainFrame(new IView2ModelAdapter() {

			@Override
			public void sendErrorLogMsg(String msg) {
				model.makeLog(LogLevel.ERROR, msg); // We're simulating a log entry being created in the model.
			}

			@Override
			public void sendCriticalLogMsg(String msg) {
				model.makeLog(LogLevel.CRITICAL, msg); // We're simulating a log entry being created in the model.
			}

			@Override
			public void sendInfoLogMsg(String msg) {
				model.makeLog(LogLevel.INFO, msg); // We're simulating a log entry being created in the model.
			}

			@Override
			public void sendDebugLogMsg(String msg) {
				model.makeLog(LogLevel.DEBUG, msg); // We're simulating a log entry being created in the model.
			}

			@Override
			public void setLogLevel(LogLevel logLevel) {
				sharedSystemLogger.setLogLevel(logLevel); // Since the controller has control of the system-wide logger, the minimum log level for it can be set here.
			}

		});

		pnlLog = new LoggerPanel("System Logger");

		view.addCenterPanel(pnlLog); // Add the logger panel to the view.

		/**
		 * This uses the better practice of letting the controller decide what the system logger is and 
		 * passing it to the modules in the systems such as the model here.
		 */
		model = new MainModel(sharedSystemLogger, new IModel2ViewAdapter() {

			@Override
			public void displayMsg(String msg) {
				view.displayMsg(msg);
			}

		});
	}

	/**
	 * Start the application.   This includes chaining another logger (nonSharedLogger) to the end of the system logger.
	 */
	private void start() {
		view.start();

		//nonSharedLogger has NOT yet been added to the sharedSystemLogger chain, so log entries in the system logger do not go to it yet.
		sharedSystemLogger.log(LogLevel.INFO, "Info test msg BEFORE adding logger panel as log entry processor.");
		// Dynamically add a log entry processor to the shared logger
		sharedSystemLogger.addLogProcessor(pnlLog); // Can do this because LoggerPanel is an ILogEntryProcessor. 
		sharedSystemLogger.log(LogLevel.INFO, "Info test msg AFTER adding logger panel as log entry processor.");

		// Chain nonShareLogger to the end of sharedSystemLogger --> Now all log entries going to the sharedSystemLogger will also go to nonSharedLogger
		sharedSystemLogger.append(nonSharedLogger);

		model.start();
	}

	/**
	 * Run the application
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			(new LoggerTestApp()).start();
		});
	}

}

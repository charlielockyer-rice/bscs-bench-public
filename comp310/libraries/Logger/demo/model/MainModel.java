package provided.logger.demo.model;

import provided.logger.ILogEntryFormatter;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * The model for the demo
 * @author swong
 *
 */
public class MainModel {
	/**
	 * The adapter to the view
	 */
	private IModel2ViewAdapter m2vAdpt;

	/**
	 * The system-wide logger
	 */
	private ILogger systemLogger;

	/**
	 * The log entry formatter used by the local model logger
	 * Note the use of the indexed positional arguments ("%X$") in the format string 
	 * to use only the desired elements of the log entry to be formatted.
	 * See the ILogEntryFormatter Javadocs
	 */
	private ILogEntryFormatter leFormatter = ILogEntryFormatter.MakeFormatter("[%1$s] %2$s (from model-only logger)"); // "[level] msg (from model-only logger)"

	/**
	 * The local model logger
	 */
	private ILogger modelOnlyLogger = ILoggerControl.makeLogger((logEntry) -> {
		m2vAdpt.displayMsg(leFormatter.apply(logEntry));
	}, LogLevel.CRITICAL);

	/**
	 * Construct the model
	 * @param systemLogger  The global system-wide logger to use as determined by the controller
	 * @param m2vAdpt The adapter to the view
	 */
	public MainModel(ILogger systemLogger, IModel2ViewAdapter m2vAdpt) {
		this.systemLogger = systemLogger;
		this.m2vAdpt = m2vAdpt;

	}

	/**
	 * Make submit a log entry with the given log level and message
	 * @param level The log level to use
	 * @param msg The message to use
	 */
	public void makeLog(LogLevel level, String msg) {
		modelOnlyLogger.log(level, msg); // This is the code location that the resultant log entry will identify 
	}

	/**
	 * Start the model, including chaining the system logger to the end of the local model logger.
	 */
	public void start() {
		modelOnlyLogger.append(systemLogger);
	}

}

package provided.logger;

import provided.logger.impl.Logger;

/**
 * 
 * Top-level interface with static methods to instantiate ILoggers or access 
 * a simple shared default logger.   
 * 
 * ILoggers should ALWAYS be created via this interface, ILoggers should NEVER instantiated directly! 
 * @author swong
 *
 */
public interface ILoggerControl {

	/**
	 * Instantiate a new instance of an ILogger with the ILogEntryProcessor.DEFAULT 
	 * log entry processor and a minimum logging level of LogLevel.INFO.
	 * @return A new ILogger instance
	 */
	public static ILogger makeLogger() {
		return new Logger();
	}

	/**
	 * Instantiate a new instance of an ILogger with the ILogEntryProcessor.DEFAULT 
	 * log entry processor and the given  minimum logging level.
	 * @param level  The minimum log level to use
	 * @return A new ILogger instance
	 */
	public static ILogger makeLogger(LogLevel level) {
		return new Logger(level);
	}

	/**
	 * Instantiate a new instance of an ILogger with the given log entry processor
	 * and a minimum logging level of LogLevel.INFO.
	 * @param logProcFn  The log entry processor to use
	 * @return A new ILogger instance
	 */
	public static ILogger makeLogger(ILogEntryProcessor logProcFn) {
		return new Logger(logProcFn);
	}

	/**
	 * Instantiate a new instance of an ILogger with the given log entry processor
	 * and the given minimum logging level.
	 * @param logProcFn The log entry processor to use
	 * @param logLevel The minimum log level to use
	 * @return A new ILogger instance
	 */
	public static ILogger makeLogger(ILogEntryProcessor logProcFn, LogLevel logLevel) {
		return new Logger(logProcFn, logLevel);
	}

	/**
	 * Return a singleton shared ILogger instance.  
	 * Repeated calls to this static method will return 
	 * a reference to the same ILogger instance. 
	 * The shared ILogger instance is typically used for implementing a 
	 * single common logger across decoupled parts of an entire system.  
	 * This singleton shared instance is NOT useful for when 
	 * multiple, separate shared loggers are required. 
	 * @return The shared ILogger instance
	 */
	public static ILogger getSharedLogger() {
		return Logger.SHARED;
	}

}

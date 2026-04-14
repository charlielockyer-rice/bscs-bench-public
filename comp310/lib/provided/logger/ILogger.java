package provided.logger;

/**
 * Represents a logger object that is the head node of a chain of logger objects.
 * @author swong
 *
 */
public interface ILogger {

	/**
	 * Null logger instance that is a no-op or return false except for its append() method which returns the given ILogger object.
	 */
	public static final ILogger NULL = new ILogger() {

		@Override
		public void log(LogLevel level, String msg) {
		}

		@Override
		public void log(ILogEntry logEntry) {
		}

		@Override
		public void setLogLevel(LogLevel logLevel) {
		}

		@Override
		public ILogger append(ILogger logger) {
			return logger;
		}

		@Override
		public ILogger remove(ILogger logger) {
			return this;
		}

		@Override
		public void setLogProcessor(ILogEntryProcessor logDisplayFn) {
		}

		@Override
		public boolean addLogProcessor(ILogEntryProcessor logDisplayFn) {
			return false;
		}

		@Override
		public boolean removeLogProcessor(ILogEntryProcessor logDisplayFn) {
			return false;
		}

		@Override
		public boolean find(ILogger logger) {
			return false;
		}

	};

	/**
	 * Log the given log entry where the code location is
	 * being automatically calculate. 
	 * 
	 * @param level  The log level of the entry
	 * @param msg The message of the entry
	 */
	public void log(LogLevel level, String msg);

	/**
	 * TYPICAL DEVELOPER CODE NEVER CALLS THIS METHOD!
	 * Log the given log entry using a completed ILogEntry object
	 * where the code location is being explicitly specified. 
	 * This method is used by ILoggers during a chained log.
	 * @param logEntry The log entry to process
	 */
	public void log(ILogEntry logEntry);

	/**
	 * Sets the minimum log level to be displayed.
	 * @param logLevel The minimum log level to be displayed.
	 */
	public void setLogLevel(LogLevel logLevel);

	/**
	 * Set the log entry processor being used.  
	 * This processor will replace ALL processors currently in use.
	 * @param logProcFn  The new log processor function.
	 */
	public void setLogProcessor(ILogEntryProcessor logProcFn);

	/**
	 * Add the given log entry processor to the set of processors being used.  
	 * Duplicate processors will not be added and will return a False value.
	 * @param logProcFn  The new log processor function to add.
	 * @return true if the processor was successfully added, false if the processor was already in the current set of processors or could not be added.
	 */
	public boolean addLogProcessor(ILogEntryProcessor logProcFn);

	/**
	 * Remove the given log entry processor from the set of processors being used.  
	 * The last processor cannot be removed (returns false).
	 * @param logProcFn  The new log processor function to remove.
	 * @return true if the processor was found and removed, false otherwise.
	 */
	public boolean removeLogProcessor(ILogEntryProcessor logProcFn);

	/**
	 * Appends the chain represented by the given logger into this logger chain.
	 * @param logger The logger and the rest of its chain to append to the end of this logger's chain.
	 * @return The logger at the head of the new chain. A non-empty chain returns itself while an empty chain returns the given logger.
	 * @throws IllegalStateException if a circular chain would be created.
	 */
	public ILogger append(ILogger logger);

	/**
	 * Remove the given logger from anywhere in the logger chain that begins with this logger.
	 * @param logger The logger to remove from the chain.
	 * @return Returns the head of the new logger chain, typically this ILogger unless this logger is the one being removed, when ILogger.NULL is returned.
	 */
	public ILogger remove(ILogger logger);

	/**
	 * TYPICAL DEVELOPER CODE NEVER CALLS THIS METHOD!
	 * Searches the this logger chain to find any occurrence of the given ILogger.
	 * This method is used between ILoggers to check for potential loops in chains.
	 * 	  
	 * @param logger  The logger chain to search for
	 * @return true if found, false otherwise
	 */
	public boolean find(ILogger logger);

}

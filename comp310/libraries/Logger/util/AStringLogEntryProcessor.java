package provided.logger.util;

import java.util.Objects;

import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.LogLevel;

/**
 * A abstract mutable IStringLogEntryProcessor class that enables the string formatter in use to be both accessed and changed.
 * This class provides the internal storage for the ILogEntryFormatter in use.   Note: The use of this class is not always feasible
 * in some situations, particularly due to the multiple inheritance limitations of Java.
 * @author swong
 *
 */
public abstract class AStringLogEntryProcessor implements IStringLogEntryProcessor {

	/**
	 * The log entry formatter currently in use.
	 */
	private ILogEntryFormatter logEntryFormatter;

	/**
	 * Construct an instance using the ILogEntryFormatter.DEFAULT formatter.
	 */
	public AStringLogEntryProcessor() {
		this(ILogEntryFormatter.DEFAULT);
	}

	/**
	 * Construct an instance that uses the given log entry formatter.
	 * @param leFormatter The initial log entry formatter to use.
	 */
	public AStringLogEntryProcessor(ILogEntryFormatter leFormatter) {
		this.logEntryFormatter = leFormatter;
	}

	/**
	 * Set the log entry formatter in use to be the given formatter
	 * @param leFormatter The new log entry formatter to use 
	 */
	@Override
	public void setLogEntryFormatter(ILogEntryFormatter leFormatter) {
		Objects.requireNonNull(leFormatter,
				"[AStringLogEntryProcessor.setLogEntryFormatter()] The log entry formatter must be non-null.");
		this.logEntryFormatter = leFormatter;
	}

	/**
	 * Get the current log entry formatter in use
	 * @return The log entry formatter in use 
	 */
	@Override
	public ILogEntryFormatter getLogEntryFormatter() {
		return this.logEntryFormatter;
	}

	/**
	 * Static factory to instantiate an AStringLogEntryProcessor instance using the  
	 * ILogEntryFormatter.DEFAULT formatter.  Output goes to the console where 
	 * LogLevel.ERROR level is printed to stderr and other levels are printed to 
	 * stdout. 
	 * 
	 * @return An IStringLogEntryFormatter instance initially
	 */
	public static final IStringLogEntryProcessor MakeDefault() {
		return MakeDefault(ILogEntryFormatter.DEFAULT);
	}

	/**
	 * Static factory to instantiate an AStringLogEntryProcessor instance using the  
	 * given ILogEntryFormatter.  Output goes to the console where 
	 * LogLevel.ERROR level is printed to stderr and other levels are printed to 
	 * stdout.  
	 * @param leFormatter The log entry formatter to use initially
	 * @return An IStringLogEntryFormatter instance
	 */
	public static final IStringLogEntryProcessor MakeDefault(ILogEntryFormatter leFormatter) {
		return new AStringLogEntryProcessor(leFormatter) {

			@Override
			public void accept(ILogEntry logEntry) {
				String outputStr = getLogEntryFormatter().apply(logEntry);
				if (0 >= LogLevel.ERROR.compareTo(logEntry.getLevel())) {
					System.err.println(outputStr);
				} else {
					System.out.println(outputStr);
				}
			}

		};
	}

}
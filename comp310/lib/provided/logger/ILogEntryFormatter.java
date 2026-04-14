package provided.logger;

/**
 * A functional which will convert an ILogEntry into a String representation.
 * @author swong
 *
 */
@FunctionalInterface
public interface ILogEntryFormatter {

	/**
	 * The string format used by the DEFAULT formatter used to convert a log entry into a single String.
	 * The format string parameters are in this order: (level, msg, loc)  
	 * Using indexed positional arguments ("%X$") enables the parameters to be used in any order as well
	 * as to use only some of the parameters.
	 */
	public static final String DEFAULT_FORMAT = "%1$s [%3$s] %2$s"; // level+ " ["+loc+"] "+msg

	/**
	 * Format the given log entry as a String
	 * @param logEntry The log entry to format
	 * @return A String representation of the given log entry
	 */
	public String apply(ILogEntry logEntry);

	/**
	 * A default log entry formatter that uses the default string format.
	 */
	public static final ILogEntryFormatter DEFAULT = MakeFormatter(DEFAULT_FORMAT);

	/**
	 * A default log entry formatter that uses the given string format.  See the DEFAULT_FORMAT for an example.
	 * Use of argument positional indices ("%x$") in the format string will enable the log entry values to be 
	 * placed anywhere desired in the output string, including ignoring a value if desired.
	 * @param formatStr A format string that takes 3 values in the order: (LogLevel, log_msg_str, code_loc_str) though not all 3 need to be used.
	 * @return An ILogEntryFormatter instance
	 */
	public static ILogEntryFormatter MakeFormatter(final String formatStr) {
		return (logEntry) -> {
			String output = String.format(formatStr, logEntry.getLevel(), logEntry.getMsg(), logEntry.getLoc());
			return output;
		};
	}
}

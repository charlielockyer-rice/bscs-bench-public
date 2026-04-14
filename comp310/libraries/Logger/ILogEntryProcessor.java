package provided.logger;

import java.util.function.BiConsumer;
import provided.logger.util.AStringLogEntryProcessor;

/**
 * Represents an object that processes a log entry.   This could include any
 * of a number of processes, including but not limited to printing, GUI display, 
 * storage, transmission, etc.
 * @author swong
 *
 */
@FunctionalInterface
public interface ILogEntryProcessor {

	/**
	 * Process the given log entry	
	 * @param logEntry The log entry to process
	 */
	public void accept(ILogEntry logEntry);

	/**
	 * Instantiate a log entry processor that prints to the console 
	 * using the ILogEntryFormatter.DEFAULT formatter. Convenience method 
	 * that simply delegates to Make(ILogEntryFormatter.DEFAULT).
	 * @return A new ILogEntryProcessor instance
	 */
	public static ILogEntryProcessor MakeDefault() {
		return MakeDefault(ILogEntryFormatter.DEFAULT);
	}

	/**
	 * Instantiate a log entry processor that prints to the console 
	 * using the given log entry formatter.
	 * LogLevel.ERROR entries are 
	 * printed to stderr while others are printed to stdout.
	 * @param leFormatter The ILogEntryFormatter to use
	 * @return A new ILogEntryProcessor instance
	 */
	public static ILogEntryProcessor MakeDefault(final ILogEntryFormatter leFormatter) {
		return AStringLogEntryProcessor.MakeDefault(leFormatter);
	}

	/**
	 * Convenience factory to wrap a Consumer&lt;LogEntry, String&gt; in an ILogEntryProcessor so that it can be used in an ILogger.
	 * Note that if the Consumer is delegating to a logger, that logger will NOT display the code location of the Consumer call, 
	 * rather it will display the code location of the logger's log() call.
	 * The appendLocation parameter enables the location of ILogEntryProcessor's ILogEntry parameter to be appended to the log message so
	 * that it will appear in Consumer's resultant output.
	 * @param logConsumer The Consumer to wrap
	 * @param appendLocation If true, "[@ code_location]" will be appended to the message.  
	 * @return An ILogEntryProcessor instance
	 */
	public static ILogEntryProcessor Wrap(BiConsumer<LogLevel, String> logConsumer, boolean appendLocation) {
		return new ILogEntryProcessor() {

			@Override
			public void accept(ILogEntry logEntry) {
				logConsumer.accept(logEntry.getLevel(),
						logEntry.getMsg() + (appendLocation ? "[@ " + logEntry.getLoc() + "]" : ""));
			}

		};
	}

}

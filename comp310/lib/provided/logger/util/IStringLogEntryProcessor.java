package provided.logger.util;

import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;

/**
 * An abstract log entry processor that converts a log entry into a String that is then displayed, stored, transmitted, etc.
 * This mutable processor enable the internal ILogEntryFormatter in use to be accessed and mutated.
 * @author swong
 *
 */
public interface IStringLogEntryProcessor extends ILogEntryProcessor {

	/**
	 * Set the log entry formatter in use to be the given formatter
	 * @param leFormatter The new log entry formatter to use 
	 */
	public void setLogEntryFormatter(ILogEntryFormatter leFormatter);

	/**
	 * Get the current log entry formatter in use
	 * @return The log entry formatter in use 
	 */
	public ILogEntryFormatter getLogEntryFormatter();

}
package provided.rmiUtils.logger.central_logger.client.view;

import provided.logger.LogLevel;

/**
 * The view to model adapter
 * @author swong
 *
 */
public interface IView2ModelAdapter {

	/**
	 * Connect to and retrieve the remote log service stub from the Registry at the given IP address.
	 * @param ipAddr The IP address of the remote Registry
	 */
	void connectTo(String ipAddr);

	/**
	 * Create and log a log entry using the given log level and message.
	 * @param logLevel The log level to use
	 * @param msg The message to use
	 */
	void logMsg(LogLevel logLevel, String msg);

}

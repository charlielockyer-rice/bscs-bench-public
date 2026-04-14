package provided.logger.demo.view;

import provided.logger.LogLevel;

/**
 * The view to model adapter
 * @author swong
 *
 */
public interface IView2ModelAdapter {

	/**
	 * Send a error message to the model
	 * @param msg The message to send
	 */
	void sendErrorLogMsg(String msg);

	/**
	 * Send a debug message to the model
	 * @param msg The message to send
	 */
	void sendCriticalLogMsg(String msg);

	/**
	 * Send a debug message to the model
	 * @param msg The message to send
	 */
	void sendInfoLogMsg(String msg);

	/**
	 * Send a debug message to the model
	 * @param msg The message to send
	 */
	void sendDebugLogMsg(String msg);

	/**
	 * Set the log level of the model's logger.
	 * @param level The new minimum log level to use
	 */
	void setLogLevel(LogLevel level);

}

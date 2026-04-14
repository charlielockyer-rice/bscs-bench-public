package provided.rmiUtils.logger.central_logger.server.view;

import provided.logger.LogLevel;

/**
 * The view to model adapter
 * @author swong
 *
 */
public interface IView2ModelAdapter {

	/**
	 * Perform a test of the locally published logger
	 */
	void test();

	/**
	 * Set the log level of logger
	 * @param logLevel The log level for the model to use
	 */
	void setLogLevel(LogLevel logLevel);

}

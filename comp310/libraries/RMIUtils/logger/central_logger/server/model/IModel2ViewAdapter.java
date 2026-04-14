package provided.rmiUtils.logger.central_logger.server.model;

import provided.logger.ILogEntryProcessor;

/**
 * The model to view adapter
 * @author swong
 *
 */
public interface IModel2ViewAdapter {

	/**
	 * Get a log entry processor from the view
	 * @return A log entry processor
	 */
	ILogEntryProcessor getLogEntryProcessor();

	/**
	 * Display the IP address and port information
	 * @param ipAddr The IP address in use
	 * @param stubPort The stub port in use
	 * @param classServerPort The class server port in use
	 */
	void setIP_Ports(String ipAddr, int stubPort, int classServerPort);

}

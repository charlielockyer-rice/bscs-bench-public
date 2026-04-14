package provided.rmiUtils.logger.central_logger.client.model;

import provided.logger.ILogEntryProcessor;

/**
 * The adapter from the model to the view
 * @author swong
 *
 */
public interface IModel2ViewAdapter {

	/**
	 * Get a log entry processor that will display log entries on the view.
	 * @return A log entry processor
	 */
	ILogEntryProcessor getLogEntryProcessor();

	/**
	 * Set the displayed local IP address on the view.
	 * @param addr  The local IP address
	 */
	void setAddr(String addr);

	/**
	 * Enable the view to send a log entry message to the connected remote log service.
	 */
	void enableSendMsg();

}

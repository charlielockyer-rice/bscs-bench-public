package provided.rmiUtils.examples.hello_discovery.server.model;

/**
 * Adapter from the model to the view
 * @author swong
 *
 */
public interface IModel2ViewAdapterServer {

	/**
	 * Display the given message on the view
	 * @param msg The message to display
	 */
	void displayMsg(String msg);

}

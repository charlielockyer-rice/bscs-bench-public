package provided.rmiUtils.examples.hello_discovery.client.model;

/**
 * Adapter from the model to the view
 * @author swong
 *
 */
public interface IModel2ViewAdapterClient {

	/**
	 * Display the given message on the view
	 * @param msg The message to display
	 */
	void displayMsg(String msg);

}

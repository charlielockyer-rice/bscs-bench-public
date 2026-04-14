package provided.rmiUtils.examples.hello_discovery.client.view;

/**
 * Adapter from the view to the model
 * @author swong
 *
 */
public interface IView2ModelAdapterClient {

	/**
	 * Manually connect to the remote system at the given IP address
	 * @param remoteIP The IP address to connect to
	 * @return A connection status message
	 */
	String connectTo(String remoteIP);

	/**
	 * Invoke the sayHello() method on the remote IHello RMI Server object.
	 */
	void sayHello();

	/**
	 * Shut down the RMI and quit the application
	 */
	void quit();

}

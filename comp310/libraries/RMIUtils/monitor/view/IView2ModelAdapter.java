package provided.rmiUtils.monitor.view;

/**
 * Adapter from the view to the model
 * @author swong
 *
 */
public interface IView2ModelAdapter {

	/**
	 * Unbind the given name from the Registry
	 * @param name The name to unbind.
	 */
	void unbind(String name);

}

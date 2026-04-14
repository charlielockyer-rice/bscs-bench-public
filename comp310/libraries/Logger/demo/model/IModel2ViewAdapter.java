package provided.logger.demo.model;

/**
 * The adapter from the model to the view
 * @author swong
 *
 */
public interface IModel2ViewAdapter {

	/**
	 * Display the given message on the view somewhere
	 * @param msg  The message to display
	 */
	void displayMsg(String msg);

}

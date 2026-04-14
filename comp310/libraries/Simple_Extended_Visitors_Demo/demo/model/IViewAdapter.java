package provided.simpleExtVisitorsDemo.demo.model;

/**
 * Adapter from the model to the view.
 * No methods yet, included to make future expansion easier.
 * @author swong
 *
 */
public interface IViewAdapter {

	/**
	 * Append the given message to the view's output display.
	 * @param msg The message to append.
	 */
	public void appendMsg(String msg);
}

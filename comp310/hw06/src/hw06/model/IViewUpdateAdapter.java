package hw06.model;

/**
 * Interface that goes from the model to the view that enables the model to talk to the view 
 * @author Megan Xiao and Son Nguyen.
 */
public interface IViewUpdateAdapter {

	/**
	 * Updates the view
	 */
	public void update();

	/**
	 * No-op "null" adapter
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IViewUpdateAdapter NULL_OBJECT = new IViewUpdateAdapter() {
		public void update() {
		}
	};
}

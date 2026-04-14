package hw03.model;

/**
 * @author Christina
 * An adapter to update the view side
 */
public interface IViewUpdateAdapter {
	
	/**
	 * The method that tells the view to update
	 */
	public void repaint();
	
	/**
	 * No-op "null" adapter
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IViewUpdateAdapter NULL_OBJECT = new IViewUpdateAdapter() {
		public void repaint() {
		}
	};
} 

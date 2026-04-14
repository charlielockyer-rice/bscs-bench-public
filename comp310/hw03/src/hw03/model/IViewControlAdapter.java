package hw03.model;

import provided.utils.displayModel.IDimension;

/**
 * @author Christina
 * This is an adapter to assister with the view sides control
 */
public interface IViewControlAdapter {
		
	/**
	 * No-op "null" adapter
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IViewControlAdapter NULL_OBJECT = new IViewControlAdapter() {
		@Override
		public IDimension getCanvasDim() {
			// TODO Auto-generated method stub
			return null;
		}

	};
	
	/**
	 * Get an IDimension object that reflects the current dimensions of the ball's canvas component in the view.
	 * @return an IDimension instance
	 */
	public IDimension getCanvasDim();

} // TODO is this right


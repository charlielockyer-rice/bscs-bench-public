package hw02.ball;

import provided.utils.displayModel.IDimension;

/**
 * Adapter allowing the model to communicate to the view
 * @author rlmse
 */
public interface IModel2ViewAdapter {
	/**
	 * Tells the view to update what it displays to reflect changes in the model
	 */
	public void update();

	/**
	 * No-op "null" adapter
	 */
	public static final IModel2ViewAdapter NULL_OBJECT = new IModel2ViewAdapter() {
		public void update() {
		}

		public IDimension getDimension() {
			return null;
		}
	};

	/**
	 * @return The dimensions of the Graphics object which contains the balls in the view
	 */
	public IDimension getDimension();

}

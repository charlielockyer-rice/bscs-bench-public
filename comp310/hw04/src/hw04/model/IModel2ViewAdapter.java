package hw04.model;

import provided.utils.displayModel.IDimension;

/**
 * An adapter from the model to the view
 * @author charlielockyer
 *
 */
public interface IModel2ViewAdapter {
	
	/**
	 * Defines the update method that updates the view based on model
	 */
	public void update();
	
	/**
	 * Gets the canvas dimensions
	 * @return the canvas dimensions
	 */
	public IDimension getCanvasDim();
	
	/**
	 * Instantiates the null object version of the adapter
	 */
	public static final IModel2ViewAdapter NULL = new IModel2ViewAdapter() {
		
		@Override
		public void update() {
			
		}
		
		public IDimension getCanvasDim() {
			return new IDimension() {
				
				@Override
				public int getWidth() {
					return 0;
				}
				
				@Override
				public int getHeight() {
					return 0;
				}
			};
			
		};
	};
}

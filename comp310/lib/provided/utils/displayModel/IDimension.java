package provided.utils.displayModel;

/**
 * Abstract representation of an entity with an integer width and height.
 * This interface is useful for representing components the model needs that 
 * have a width and height, such as a drawing canvas but without exposing the 
 * model to the full GUI-specific objects such as JPanels.   Also, unlike 
 * java.awt.Dimension, this interface is able to represent a dynamically changing
 * width and height, not just a static value.
 * <br><br>
 * Typical usage: Have the controller wrap a GUI element from the view with an 
 * instance of this interface that is then given to the model to use.
 * @author swong
 *
 */
public interface IDimension {
	/**
	 * The width of the entity
	 * @return An integer width
	 */
	public int getWidth();

	/**
	 * The height of the entity
	 * @return An integer height
	 */
	public int getHeight();
	
	/**
	 * Null object with zero width and height.
	 */
	public static final IDimension NULL = new IDimension() {

		@Override
		public int getWidth() {
			return 0;
		}

		@Override
		public int getHeight() {
			return 0;
		}};

}

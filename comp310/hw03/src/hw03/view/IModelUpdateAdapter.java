package hw03.view;

import java.awt.Graphics;

/**
 * @author Christina
 * @param <IStrategyFac> Interface for strategy factory
 * This is an adapter to speak and update the Model
 */
public interface IModelUpdateAdapter<IStrategyFac> {
	
	/**
	 * No-op singleton implementation of IView2ModelAdapter 
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	@SuppressWarnings("rawtypes")
	public static final IModelUpdateAdapter NULL_OBJECT = new IModelUpdateAdapter () {	
		
	    public void update(Graphics g) {
	    	// Implement in controller
	    }
	};
	

	/**
	 * Method to update the graphics for the balls. This will paint balls
	 * @param g inputed graphics
	 */
	public void update(Graphics g);

}

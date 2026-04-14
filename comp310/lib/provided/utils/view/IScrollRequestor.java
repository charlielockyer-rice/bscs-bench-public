package provided.utils.view;

import java.util.function.Consumer;

/**
 * Interface that represents an entity that is capable of requesting that its parent scroll 
 * the display of this entity to the end of the scrolling range in a specified direction. 
 */
public interface IScrollRequestor {

	/**
	 * Enum that define the requested scroll direction.   These values are the only
	 * accepted input parameter values allowed when this entity invokes the scrolling request strategy
	 * supplied by the setScrollRequest() method. 
	 */
	static enum ScrollDir {
		/**
		 * Scroll to the top 
		 */
		TOP,
		
		/**
		 * Scroll to the bottom
		 */
		BOTTOM,
		
		/**
		 * Scroll to the left
		 */
		LEFT,
		
		/**
		 * Scroll to the right
		 */
		RIGHT;
	}
	
	/**
	 * Sets the scroll request strategy.   This method would be called by the parent of this entity.  
	 * This entity should NOT make any assumptions that the parent of this entity can or will actually honor 
	 * the scrolling request if invoked.
	 * @param scrollReq  A functional that takes a scroll direction and requests that parent attempt 
	 * to scroll this component to the end of the scrolling range in the the given direction.
	 */
	void setScrollRequest(Consumer<ScrollDir> scrollReq);
	

}




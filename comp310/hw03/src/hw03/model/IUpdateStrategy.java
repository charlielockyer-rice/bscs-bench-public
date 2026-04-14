package hw03.model;

import provided.utils.dispatcher.IDispatcher;
import java.awt.Graphics;

/**
 * @author Christina
 * Interface that will update strategy.
 */
public interface IUpdateStrategy {
	
	
	/**
	 * @param disp the display for view perspective
	 * @param ball the ball object
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball);
	
	/**
	 * 
	 */
	public static IUpdateStrategy errorStrategy = new IUpdateStrategy() {
		private int count = 0;
		public void updateState(IDispatcher<Graphics> disp, Ball ball) {
			if(25 < count++){
		        java.awt.Toolkit.getDefaultToolkit().beep(); 
		        count = 0;
		    }
		}
	};
}

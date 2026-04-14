package hw04.model;

import provided.utils.dispatcher.IDispatcher;

/**
 * @author charlielockyer
 * Interface that will update strategy.
 */
public interface IUpdateStrategy {
	
	
	/**
	 * @param disp the display for view perspective
	 * @param ball the ball object
	 */
	public abstract void updateState(IBall context, IDispatcher<IBallCmd> disp);
	
	/**
	 * Initializes the ball
	 * @param context the ball to initialize
	 */
	public void init(IBall context);
	
	/**
	 * The null object version of an IUpdateStrategy
	 */
	public static final IUpdateStrategy NULL = new IUpdateStrategy() {
		public void init(IBall context) {
			
		}
		
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
			
		}
	};
	
	/**
	 * The error strategy version of an IUpdateStrategy
	 */
	public static IUpdateStrategy errorStrategy = new IUpdateStrategy() {
		private int count = 0;
		
		public void init(IBall context) {
			
		}
		
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
			if(25 < count++){
		        java.awt.Toolkit.getDefaultToolkit().beep(); 
		        count = 0;
		    }
		}
	};
}

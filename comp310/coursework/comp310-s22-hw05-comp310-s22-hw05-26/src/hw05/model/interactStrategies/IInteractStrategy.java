package hw05.model.interactStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * IInteractStrategy performs the interaction behavior of balls after certain interaction criteria is satisfied
 * @author cindy
 *
 */
public interface IInteractStrategy {
	
		/**
		 * Performs a directed interaction between the context ball and the target Ball from the 
		 * perspective of the context Ball.
		 * @param context  The Ball from whose perspective the interaction 
		 * processing takes place.
		 * @param target  The Ball that is the "other ball" in the perspective of this processing.
		 * @param disp  The Dispatcher that is to be used if desired.
		 * @return A command to be executed after both ball's interaction behaviors have completed.   
		 */
		public abstract IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp);
		
		/**
		 * initialization for IInteractStrategy
		 * @param context the ball in interaction
		 */
		public void init(IBall context);
		
		/**
		 * No-opt for null strategy
		 */
		public static final IInteractStrategy NULL = new IInteractStrategy() {

			@Override
			public IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
				return new IBallCmd() {

					@Override
					public void apply(IBall context, IDispatcher<IBallCmd> disp) {
						// TODO Auto-generated method stub
						
					}
					
				};
			}

 
			@Override
			public void init(IBall context) {
			}

		};
		
		/**
		 * The error strategy
		 */
		public static final IInteractStrategy ERROR = new IInteractStrategy() {

			@Override
			public IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
				return null;
			}

 
			@Override
			public void init(IBall context) {
			}

		};

}
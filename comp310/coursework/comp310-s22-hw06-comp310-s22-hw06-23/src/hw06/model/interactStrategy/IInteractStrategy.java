package hw06.model.interactStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Interface for an interaction strategy
 * @author Son Nguyen and Smit Viroja
 */
public interface IInteractStrategy {
	
	/**
	 * Performs a directed interaction between the context ball and the target Ball from the 
	 * perspective of the context Ball.
	 * @param context -> The Ball from whose perspective the interaction 
	 * processing takes place.
	 * @param other -> The Ball that is the "other ball" in the perspective of this processing.
	 * @param disp -> The IDispatcher that is to be used if desired.
	 * @return A command to be executed after both balls' interaction behaviors have completed.   
	 */
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp);
	
	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host);
	
	/**
	 * Null strategy with no-op behavior.
	 */
	public static final IInteractStrategy NULL_OBJECT = new IInteractStrategy() {

		@Override
		public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
			return new IBallCmd() {

				@Override
				public void apply(IBall context, IDispatcher<IBallCmd> disp) {
				
				}
			};
		}

		@Override
		public void init(IBall host) {
		}
		
	};	
	
	
	
	/**
	 * An error strategy to beep.
	 */
	public static final IInteractStrategy ERROR = new IInteractStrategy() {

		@Override
		public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
			return new IBallCmd() {
				@Override
				public void apply(IBall context, IDispatcher<IBallCmd> disp) {					
				}	
			};
		}

		@Override
		public void init(IBall host) {
			System.err.println("IIteractStrategy Error!!!");
		}
	};
	
}

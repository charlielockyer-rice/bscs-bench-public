package hw04.model.strategy;

import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
import provided.utils.displayModel.IDimension;

/**
 * An energy strategy is a strategy by which the ball gains speed by a factor of 50% 
 * when contacting the top and left boundaries of the GUI panel but loses speed 
 * by a factor of ~33% when contacting the bottom and right boundaries of the GUI panel.
 * @author akashkaranam
 */
public class EnergyStrategy implements IUpdateStrategy {
	
	/**
	 * float that determines how much speed increases on contact with left and top boundaries
	 */
	private float powerUp = 1.5f;
	/**
	 * float that determines how much speed decreases on contact with right and bottom boundaries
	 */
	private float powerDown = .66f;
	
	/**
	 * Method to change the speed of the ball based on contact with boundaries
	 * @param disp the dispatcher that makes calls to update the balls
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		
	}


}

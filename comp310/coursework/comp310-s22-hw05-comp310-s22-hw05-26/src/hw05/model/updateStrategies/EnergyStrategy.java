package hw05.model.updateStrategies;

import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import hw05.model.IBall;
import hw05.model.IBallCmd;
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
		IDimension canvasDim = context.getDimension();
		int canvasWidth = canvasDim.getWidth();
		int canvasHeight = canvasDim.getHeight();
		
		int xLoc = context.getLocation().x + context.getVelocity().x;
		int yLoc = context.getLocation().y + context.getVelocity().y;
		
		// Case where the ball hits the left wall.
		if (xLoc - context.getRadius() < 0) {
			context.setVelocity(new Point(Math.round(context.getVelocity().x*this.powerUp), 
					Math.round(context.getVelocity().y * this.powerUp)));
		}
		
		// Case where the ball hits the right wall.
		if (xLoc + context.getRadius() > canvasWidth) {
			context.setVelocity(new Point(Math.round(context.getVelocity().x*this.powerDown), 
					Math.round(context.getVelocity().y * this.powerDown)));
		}
		
		// Case where the ball hits the upper wall.
		if (yLoc - context.getRadius() < 0) {
			context.setVelocity(new Point(Math.round(context.getVelocity().x * this.powerUp), 
					Math.round(context.getVelocity().y * this.powerUp)));
		}
		
		// Case where the ball hits the lower wall.
		if (yLoc + context.getRadius() > canvasHeight) {
			context.setVelocity(new Point(Math.round(context.getVelocity().x * this.powerDown), 
					Math.round(context.getVelocity().y * this.powerDown)));
		}
		

	}
	
	@Override
	public void init(IBall context) {
		
	}


}

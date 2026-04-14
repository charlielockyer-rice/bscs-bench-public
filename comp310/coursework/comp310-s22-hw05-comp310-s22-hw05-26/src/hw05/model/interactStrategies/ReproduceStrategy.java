package hw05.model.interactStrategies;

import java.awt.Color;
import java.awt.Graphics;

import hw05.model.Ball;
import hw05.model.IBall;
import hw05.model.IBallAlgo;
import hw05.model.IBallCmd;
import hw05.model.paintStrategies.IPaintStrategy;
import hw05.model.updateStrategies.MultiStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * The interact strategy that generates a new ball with the average radius and color of the two balls that interacted
 *
 */
public class ReproduceStrategy implements IInteractStrategy {

	/**
	 * Generates a new ball with the average radius and color of the two balls that interacted
	 * NOTE TO INSTRUCTORS: THIS IS BROKEN, DO NOT COUNT AS ONE OF OUR STRATEGIES OR GRADE
	 */
	public IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
		// TODO Auto-generated method stub
		System.out.println("infinity here too");
		float[] contextColor = context.getColor().getRGBColorComponents(new float[3]);
		float[] targetColor = context.getColor().getRGBColorComponents(new float[3]);
		float[] newColor = new float[3];
		for (int i = 0; i < 3; i++) {
			newColor[i] = (contextColor[i] + targetColor[i]) / 2;
		}
		Color childColor = new Color((int) newColor[0], (int) newColor[1], (int) newColor[2]);
		
		IBallAlgo childAlgo = new IBallAlgo(){

			public void caseDefault(IBall host) {
				System.out.println("in case default");
				host.setUpdateStrategy(new MultiStrategy(context.getUpdateStrategy(), target.getUpdateStrategy()));
				
				host.setPaintStrategy(new IPaintStrategy() {
					IPaintStrategy paintStrategy1 = context.getPaintStrategy();
					IPaintStrategy paintStrategy2 = target.getPaintStrategy();
				
					@Override
					public void paint(Graphics g, IBall host) {
						paintStrategy1.paint(g, host);
						paintStrategy2.paint(g, host);
					}
					
					@Override
					public void init(IBall host) {
						paintStrategy1.init(host);
						paintStrategy2.init(host);
				}});
				
				host.setInteractStrategy(context.getInteractStrategy());
			};
			
		};
		
		/**
		 * Adding the observer
		 */
		
		return new IBallCmd() {
			@Override
			public void apply(IBall ball, IDispatcher<IBallCmd> disp) {
				disp.addObserver(new Ball(context.getLocation(), target.getVelocity(), childColor, 
						(context.getRadius()+target.getRadius()) / 2, context.getDimension(), context.getCanvas(), childAlgo));
				ball.getLocation().translate((int)(-1 * ball.getVelocity().x * 0.1),(int) (-1 * ball.getVelocity().y * 0.1));
			}
		};
	}

	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}

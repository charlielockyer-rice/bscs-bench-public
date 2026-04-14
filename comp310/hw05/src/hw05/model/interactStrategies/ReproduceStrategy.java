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
		// TODO: Implement this method
		return (ball, d) -> {};
	}

	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}

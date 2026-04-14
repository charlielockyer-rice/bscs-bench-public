package hw04.model.paint.strategies;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import hw04.model.IBall;

/**
 * An interface for describing a paint strategy
 * @author charlielockyer
 *
 */
public interface IPaintStrategy {
	/**
	 * Initialize given a ball
	 * @param ball the ball to use this with
	 */
	public abstract void init(IBall ball);
	
	/**
	 * Paints with the strategy
	 * @param g the graphics to paint on
	 * @param ball the ball to paint
	 */
	public void paint(Graphics g, IBall ball);
	
	/**
	 * Instantiate the null object version of the strategy
	 */
	public static final IPaintStrategy NULL = new IPaintStrategy() {
		public void init(IBall ball) {
			
		}
		
		public void paint(Graphics g, IBall ball) {
			
		}
	};
	
	/**
	 * Instantiate the error strategy version of the strategy
	 */
	public static final IPaintStrategy ERROR = new IPaintStrategy() {
		private int count = 100;
		
		public void init(IBall context) {
			
		}
		
		public void paint(Graphics g, IBall host) {
			host.setColor(Color.WHITE);
			host.setVelocity(new Point(0, 0));
			host.setRadius(0);
			host.setLocation(new Point(0, 0));
			
			if(count >= 0 && 0 == (count--) % 25) {
				java.awt.Toolkit.getDefaultToolkit().beep();
				System.err.println("IUpdateStrategy.ERROR: beep!");
			}
		}
	};
}

package hw05.model.paintStrategies;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw05.model.IBall;

/**
 * A multi paint strategy that combines two paint strategies
 * @author charlielockyer
 *
 */
public class MultiPaintStrategy extends APaintStrategy {
	/**
	 * The array of paint strategies
	 */
	private APaintStrategy[] paintStrategies;
	/**
	 * Constructs a multi paint strategy
	 * @param paintStrategies the input paint strategies
	 */
	public MultiPaintStrategy(APaintStrategy... paintStrategies) {
		this(new AffineTransform(), paintStrategies);
	}
	
	/**
	 * Constructs a multi paint strategy given an affine as well
	 * @param at affine transform to use
	 * @param paintStrategies the input paint strategies
	 */
	public MultiPaintStrategy(AffineTransform at, APaintStrategy... paintStrategies) {
		super(at);
		this.paintStrategies = paintStrategies;
	}
	/**
	 * Runs the init() of each strategy input into this
	 */
	public void init(IBall ball) {
		for(IPaintStrategy strategy: this.paintStrategies) {
			strategy.init(ball);
		}
	}
	
	@Override
	public void paintCfg(Graphics g, IBall ball) {
		for (APaintStrategy strategy: this.paintStrategies) {
			strategy.paintCfg(g,  ball);
		}
	}
	
	@Override
	public void paintXfrm(Graphics g, IBall ball, AffineTransform at) {
		for(APaintStrategy strategy : this.paintStrategies) {
			strategy.paintXfrm(g, ball, at);
		}
	}
	
}

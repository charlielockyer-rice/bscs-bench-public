package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;

/**
 * The paint strategy that paints a fungi
 *
 */
public class FungiPaintStrategy extends MultiPaintStrategy {

	/**
	 * @param at AffineTransform parameter
	 * @param paintStrategies The paint strategies that compose the fungi paint strategy
	 */
	public FungiPaintStrategy(AffineTransform at, APaintStrategy... paintStrategies) {
		super(at, paintStrategies);
	}

	/**
	 * Default constructor
	 */
	public FungiPaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform());
	}

}

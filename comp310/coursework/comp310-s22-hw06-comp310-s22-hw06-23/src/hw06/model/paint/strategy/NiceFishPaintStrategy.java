package hw06.model.paint.strategy;

import java.awt.geom.AffineTransform;

import hw06.model.paint.ADecoratorPaintStrategy;
import hw06.model.paint.UprightMultiPaintStrategy;

/**
 * A concrete Paint Strategy to draw a composite shape of Fish with upright implementation.
 * @author Son Nguyen and Cole Rabson
 */
public class NiceFishPaintStrategy extends UprightMultiPaintStrategy {
	/**
	 * A constructor for a class that paints a face that is always smiling. 
	 */
	public NiceFishPaintStrategy() {
		super(new EllipsePaintStrategy(),
				new ADecoratorPaintStrategy(new EllipsePaintStrategy(new AffineTransform(), .6, -.25, .18, .18)));
	}

}

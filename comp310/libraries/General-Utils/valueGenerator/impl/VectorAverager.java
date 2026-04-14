package provided.utils.valueGenerator.impl;

import java.awt.geom.Point2D;

import provided.utils.valueGenerator.IAverager;

/**
 * An averager for vectors, i.e. the input is a Point2D 
 * which could be either a Point or a Point2D.Double.
 * The average value is always a Point2D.Double
 * @author swong
 *
 */
public class VectorAverager implements IAverager<Point2D, Point2D.Double> {
	/**
	 * The accumulated weight so far
	 */
	private double accWeight = 0.0;
	
	/**
	 * The accumulated value so far
	 */
	private Point2D.Double accValue = new Point2D.Double(0.0, 0.0);

	/**
	 * Instantiate the averager in a reset state.
	 */
	public VectorAverager() {
		reset();
	}

	@Override
	public void reset() {
		accWeight = 0.0;
		accValue.setLocation(0.0, 0.0);
	}
	
	@Override
	public boolean hasValidAvg() {
		return 0.0 < accWeight ;
	}

	@Override
	public Point2D.Double getAvg() {
		if(hasValidAvg()) {
			return new Point2D.Double(accValue.getX()/accWeight, accValue.getY()/accWeight);
		}
		else {
			throw new IllegalStateException("[VectorAverager.getAvg()] A valid average has not yet been accumulated!");		
		}
	}

	@Override
	public void addTo(double weight, Point2D value) {
		if(0.0 > weight) {
			throw new IllegalArgumentException( "[VectorAverager.addTo()] Negative weights are not allowed: "+weight);
		}
		else {
			accValue.x += value.getX()*weight;
			accValue.y += value.getY()*weight;
			accWeight += weight;
		}
	}

}

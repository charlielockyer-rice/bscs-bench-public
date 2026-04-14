package provided.utils.valueGenerator.impl;

import provided.utils.valueGenerator.IAverager;

/**
 * An averager for numbers, which could be doubles, ints or floats or any combination of them.
 * The averaged result is always a double.
 * @author swong
 *
 */
public class ScalarAverager implements IAverager<Number, Double> {
	/**
	 * The accumulated weight so far
	 */
	private double accWeight = 0.0;
	
	/**
	 * The accumulated value so far
	 */
	private double accValue = 0.0;

	/**
	 * Instantiate the averager in a reset state.
	 */
	public ScalarAverager() {
		reset();
	}

	@Override
	public void reset() {
		accWeight = 0.0;
		accValue = 0.0;
	}
	

	@Override
	public boolean hasValidAvg() {
		return 0.0 < accWeight ;
	}
	

	@Override
	public Double getAvg() {
		if(hasValidAvg()) {
			return accValue/accWeight;
		}
		else {
			throw new IllegalStateException("[ScalarAverager.getAvg()] A valid average has not yet been accumulated!");		
		}
	}

	@Override
	public void addTo(double weight, Number value) {
		if(0.0 > weight) {
			throw new IllegalArgumentException( "[ScalarAverager.addTo()] Negative weights are not allowed: "+weight);
		}
		else {
			accValue += value.doubleValue()*weight;
			accWeight += weight;
		}
	}


}

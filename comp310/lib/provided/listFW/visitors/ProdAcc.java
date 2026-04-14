package provided.listFW.visitors;

/**
 * Accumulates the product of the values, starting with a total of 1.
 * @author swong
 *
 */
public class ProdAcc extends AAccumulator {

	/**
	 * Constructor for the class
	 */
	public ProdAcc() {
		super(1);
	}

	@Override
	/**
	 * Add the given value to the stored value.
	 */
	public void accumulate(Object x) {
		value = (int) value * (int) x;
	}

}

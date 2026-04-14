/**
 * 
 */
package provided.utils.struct.impl;

import java.io.Serializable;

import provided.utils.struct.IDyad;

/**
 * A class that represents a dyad (pair) of elements of possibly different types.
 * This class is a substitute for javafx.Pair since the JavaFX project is no longer an
 * official part of Java.
 * This is a Serializable IDyad implementation.
 * @author swong
 * @param <F> The type of the first element.  Must be Serializable.
 * @param <S> The type of the second element.  Must be Serializable.
 *
 */
public class DyadSerializable<F extends Serializable, S extends Serializable> implements IDyad<F, S>, Serializable{
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -7897434136470828894L;
	/**
	 * The first element
	 */
	private F first;
	/**
	 * The second element
	 */
	private S second;

	/**
	 * Construct a dyad of the two given elements.
	 * @param first  The first element
	 * @param second The second element
	 */
	public DyadSerializable(F first, S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public F getFirst() {
		return this.first;
	}

	@Override
	public S getSecond() {
		return this.second;
	}

}

package provided.extvisitor;

import java.io.Serializable;

/**
 * Interface that defines a command used by AExtVisitor.
 * @param <R> The type of the return value
 * @param <I> The type of the index value
 * @param <P> The type of the input parameters
 * @param <H> The type of the host
 * @author Stephen Wong (c) 2010
 */
public abstract interface IExtVisitorCmd<R, I, P, H extends IExtVisitorHost<I, ? extends H>> extends Serializable {
	/**
	 * The method that is run by AExtVisitor when the case associated with this
	 * command is executed.
	 * @param <T> The type of the host
	 * @param index The index value for the case
	 * @param host The host for the visitor
	 * @param params Vararg input parameters
	 * @return The value returned by running this command.
	 */
	public abstract <T extends IExtVisitorHost<I, ? extends H>> R apply(I index, T host,
			@SuppressWarnings("unchecked") P... params);
}

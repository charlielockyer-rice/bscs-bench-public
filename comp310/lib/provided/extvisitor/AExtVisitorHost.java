package provided.extvisitor;

/**
 * Abstract implementation of IExtVisitorHost that defines a host by an index value and thus,
 * a concrete execute method.
 *
 * <br>Usage:<pre>
 * public class MyExtVisitorHost extends AExtVisitorHost&lt;MyIndex, MyExtVisitorHost&gt; {...}
 * </pre>
 * @param <I>  The type of the index value that is being used.
 * @param <H>  The type of the concrete SUBCLASS that extends this class.
 * @author Stephen Wong (c) 2010
 */
public abstract class AExtVisitorHost<I, H extends IExtVisitorHost<I, ? extends H>> implements IExtVisitorHost<I, H> {

	private static final long serialVersionUID = -7702178780705773404L;

	private I idx;

	public AExtVisitorHost(I idx) {
		this.idx = idx;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public <R, P> R execute(IExtVisitor<R, I, P, ? extends H> algo, @SuppressWarnings("unchecked") P... params) {
		return (R) ((IExtVisitor) algo).caseAt(idx, this, params);
	}

}

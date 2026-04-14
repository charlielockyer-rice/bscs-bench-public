package provided.extvisitor;

import java.util.*;

/**
 * Abstract implementation of IExtVisitor that adds an invariant implementation of
 * storing commands associated with each case in a dictionary indexed by the
 * case's associated index value.
 * @param <R> The type of the return value
 * @param <I> The type of the index value
 * @param <P> The type of the input parameters
 * @param <H> The type of the host
 * @author Stephen Wong (c) 2010
 */
public abstract class AExtVisitor<R, I, P, H extends IExtVisitorHost<I, ? extends H>> implements IExtVisitor<R, I, P, H> {

	private static final long serialVersionUID = 4445948668748598430L;

	private Map<I, IExtVisitorCmd<R, I, P, H>> cmds = new Hashtable<I, IExtVisitorCmd<R, I, P, H>>();

	private IExtVisitorCmd<R, I, P, H> defaultCmd;

	public AExtVisitor(IExtVisitorCmd<R, I, P, H> defaultCmd) {
		this.defaultCmd = defaultCmd;
	}

	public AExtVisitor(final R noOpResult) {
		this(new IExtVisitorCmd<R, I, P, H>() {

			private static final long serialVersionUID = -3773477471593844489L;

			public <T extends IExtVisitorHost<I, ? extends H>> R apply(I index, T host,
					@SuppressWarnings("unchecked") P... params) {
				return noOpResult;
			}
		});
	}

	public void setCmd(I idx, IExtVisitorCmd<R, I, P, H> cmd) {
		cmds.put(idx, cmd);
	}

	public IExtVisitorCmd<R, I, P, H> getCmd(I idx) {
		return cmds.get(idx);
	}

	public IExtVisitorCmd<R, I, P, H> removeCmd(I idx) {
		return cmds.remove(idx);
	}

	public IExtVisitorCmd<R, I, P, H> getDefaultCmd() {
		return defaultCmd;
	}

	public void setDefaultCmd(IExtVisitorCmd<R, I, P, H> defaultCmd) {
		this.defaultCmd = defaultCmd;
	}

	@Override
	public <T extends IExtVisitorHost<I, ? extends H>> R caseAt(I idx, T host,
			@SuppressWarnings("unchecked") P... params) {
		IExtVisitorCmd<R, I, P, H> cmd = cmds.get(idx);
		if (cmd == null)
			return defaultCmd.apply(idx, host, params);
		else
			return cmd.apply(idx, host, params);
	}

	public Set<I> getAllIndices() {
		return cmds.keySet();
	}

}

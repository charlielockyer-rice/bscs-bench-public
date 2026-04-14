package provided.datapacket;

import java.util.ArrayList;
import java.util.List;

/**
 * A data type that represents an Iterable of data packets
 * @author Stephen Wong (c) 2018
 *
 */
public interface IVDataPacket extends IDataPacketData, Iterable<ADataPacket> {


	/**
	 * This method allows one to get the ID value directly from the interface.
	 * 
	 * The only difference between this code and any other data type's getID() code is the value of the 
	 * Class object being passed to the DataPacketIDFactory's makeID() method.    This has to be 
	 * specified here because this is the only place where the proper Class object is unequivocally known.
	 * 
	 * @return The ID value associated with this data type.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IVDataPacket.class);
	}
	
	@Override
	public default IDataPacketID getID() {
		return IVDataPacket.GetID();
	}
	
	
	/**
	 * Convenience method that creates a command that maps a visitor over the vector of data packets.
	 * <br>
	 * Since the result returned by the returned command is an Iterable of R, 
	 * the returned command must be wrapped in another command
	 * before it can be used in a recursive algorithm, which would require a return of type R.
	 * @param <R>  The return type of the given visitor.
	 * @param <P>  The vararg input parameter type of the original visitor. 
	 * @param <A> The type of the adapter to the local system.
	 * @param <S> The type of the sender
	 * @param algo The visitor to be mapped over all the stored data packets.
	 * @return An ADataPacketAlgoCmd whose results are a vector of results from applying the given visitor to each data packet element.
	 */
	public static <R, P, A, S> ADataPacketAlgoCmd<List<R>, IVDataPacket, P, A, DataPacket<IVDataPacket, S> > makeMapCmd(final DataPacketAlgo<R,P> algo) {
		return new ADataPacketAlgoCmd<List<R>, IVDataPacket, P, A, DataPacket<IVDataPacket, S>>(){

			private static final long serialVersionUID = -5855856243603215928L;

			@Override
			public List<R> apply(IDataPacketID id, DataPacket<IVDataPacket, S> host,  @SuppressWarnings("unchecked") P... params) {
				List<R> results = new ArrayList<R>();
				for(ADataPacket d: host.getData()){
					results.add(d.execute(algo, params));
				}
				return results;
			}
			
			@Override
			public void setCmd2ModelAdpt(A cmd2ModelAdpt) {
				// not used.
			}

		};
	}
}
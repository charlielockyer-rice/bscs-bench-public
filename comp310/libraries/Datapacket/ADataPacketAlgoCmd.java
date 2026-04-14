package provided.datapacket;

import provided.extvisitor.*;

/**
 * A DataPacketAlgo command that is designed to work on a DataPacket&lt;D&gt; host.
 * <br>
 * This convenience class both simplifies the command code but also increase type safety by restricting the host type.
 * <br>
 * Usage:
 * <pre>
 * myDataPacketAlgo.addCmd(IMyData.GetID(), new ADataPacketAlgoCmd&lt;IMyReturn, IMyData, IMyParam&gt;(){
 *     private static final long serialVersionUID = aGeneratedUIDvalue;
 *     
 *     public IMyReturn apply(DataPacket&lt;IMyData&gt; host, IMyParam... params){
 *         // your code here
 *     }
 * }
 * </pre>
 * Note:  In Eclipse, the auto-generation of the implemented methods of this class does not work properly.
 * The concrete apply method below is replicated by the automatic method generator because it doesn't 
 * recognize that the method already exists and is final.  Luckily, a compiler error message gets generated
 * in the attempt to override a final method.   Simply delete the extraneous auto-generated method.
 * 
 * @author Stephen Wong (c) 2018
 *
 * @param <R> The return type
 * @param <D> The data type held by the host
 * @param <P> The input parameter type 
 * @param <A> The type of the adapter to the local model
 * @param <H> The type of datapacket host the command should coerce the given host into when dispatching to the abstract apply() method.
 * * ----------------------------------------------
 * Restricts command to hosts of type ADataPacket
 */
public abstract class ADataPacketAlgoCmd<R, D extends IDataPacketData, P, A, H extends ADataPacket>
		implements IExtVisitorCmd<R, IDataPacketID, P, ADataPacket> {

	/**
	 * Version number for serialization
	 */
	private static final long serialVersionUID = -5627902537609466988L;
	
	/**
	 * The adapter used to communicate with the local system.  
	 * Type-narrowed implementations of this class should default this field 
	 * to a NULL adapter. 
	 */
	transient private A cmd2ModelAdpt;

	/**
	 * The actual method called by the host visitor when the associated case is invoked.   
	 * This method simply forwards the call to the abstract apply method, performing 
	 * an unchecked cast of the host to the required DataPacket type.
	 * @param id  The IDataPacketID value used to identify the host
	 * @param host The host calling the visitor
	 * @param params Vararg input parameters to be used for processing the host
	 * @return The result of this case.
	 */
	@SuppressWarnings("unchecked")
	final public <T extends IExtVisitorHost<IDataPacketID, ? super ADataPacket>> R apply(IDataPacketID id, T host,
			P... params) {
		return apply(id, (H) host, params);
	}

	/**
	 * Abstract method that actually performs the processing of the case.   
	 * Here, the host is strongly typed to be the DataPacket type appropriate for the case (D).
	 * @param index The host ID identifying the host
	 * @param host  The DataPacket host calling the visitor
	 * @param params  Vararg input parameter to be used for processing the host
	 * @return  The result of this case.
	 */
	abstract public R apply(IDataPacketID index, H host, @SuppressWarnings("unchecked") P... params);

	/**
	 * Sets the ICmd2ModelAdapter for this command to use to communicate with the
	 * local ChatApp host system. A system should use this method to set the local 
	 * adapter IMMEDIATELY upon reception of or instantiation of a command instance.
	 * @param cmd2ModelAdpt  The adapter to the ChatApp model.
	 */
	 public void setCmd2ModelAdpt(A cmd2ModelAdpt) {
		 this.cmd2ModelAdpt = cmd2ModelAdpt;
	 }

	/**
	 * Gets the ICmd2ModelAdapter this command to uses to communicate with the
	 * local ChatApp host system. 
	 * @return cmd2ModelAdpt  The adapter to the ChatApp model.
	 */
	 public A getCmd2ModelAdpt() {
		 return this.cmd2ModelAdpt;
	 }
}

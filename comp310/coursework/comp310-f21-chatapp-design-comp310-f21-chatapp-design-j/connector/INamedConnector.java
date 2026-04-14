package common.connector;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * A dyad that allows a ChatApp to get the instance name associated with another ChatApp without making a costly network call.
 * 
 * @author Design Group J
 */
public interface INamedConnector extends Serializable {
	
	/**
	 * Send a data packet to the connector by delegating to the stub.
	 * 
	 * @param packet The data packet to be sent.
	 * @throws RemoteException For any network errors.
	 */
	public default void sendMessage(ConnectorDataPacket<?> packet) throws RemoteException{
		this.getStub().sendMessage(packet);
	}
	
	/**
	 * Get the name of the connector.
	 * 
	 * @return The name of the connector.
	 */
	public String getName();
	
	/**
	 * Get the contained stub.
	 * 
	 * @return The contained stub.
	 */
	public IConnector getStub();
	
	/**
	 * Returns true if obj is an INamedConnector with an equivalent stub, and false otherwise.
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @param obj An arbitrary object.
	 * @return The result of the comparison.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * Returns the hash code of the stub contained within.
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode();
	
	/**
	 * Returns a human-friendly String representation of the named connector.
	 * 
	 * We suggest simply delegating to this.getName().
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The String representation of the named connector.
	 */
	@Override
	public String toString();
	
}

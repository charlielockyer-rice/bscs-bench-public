package common.receiver;

import java.io.Serializable;
import java.rmi.RemoteException;

import common.connector.INamedConnector;

/**
 * Represents a wrapper for an IReceiver which includes methods to get the user name and corresponding connection-level
 * INamedConnector named stub.
 * 
 * @author Design Group J
 */
public interface INamedReceiver extends Serializable {
	
	/**
	 * Send a data packet to the receiver by delegating to the stub.
	 * 
	 * @param packet The data packet to be sent.
	 * @throws RemoteException For any network errors.
	 */
	public default void sendMessage(ReceiverDataPacket<?> packet) throws RemoteException {
		this.getStub().sendMessage(packet);
	}
	
	/**
	 * Get the name of the receiver. Note that this is stored locally, and thus does not involve a network call.
	 * 
	 * @return The name of the receiver.
	 */
	public String getName();
	
	/**
	 * Get the INamedConnector of the ChatApp to which this receiver corresponds.
	 * 
	 * This allows an implementation to get the name of a receiver's ChatApp instance,
	 * and also allows for more sophisticated connection features to be implemented in the future.
	 * 
	 * @return The INamedConnector stub.
	 */
	public INamedConnector getConnector();
	
	/**
	 * Get the IReceiver stub of the ChatApp (in the current chat room) to which this receiver corresponds.
	 * 
	 * @return the IReceiver stub of the ChatApp (in the current chat room) to which this receiver corresponds.
	 */
	public IReceiver getStub();
	
	/**
	 * Returns true if obj is an INamedReceiver with an equivalent stub, and false otherwise.
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
	 * Returns a human-friendly String representation of the named receiver.
	 * 
	 * We suggest simply delegating to this.getName().
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The String representation of the named receiver.
	 */
	@Override
	public String toString();
	
}

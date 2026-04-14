package common.receiver;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents one receiver to which a chat room message can be sent.
 * 
 * @author Design Group J
 *
 */
public interface IReceiver extends Remote {
	
	/**
	 * Sends a data packet to the receiver.
	 * 
	 * The ChatApp implementation is responsible for iterating over all other receivers
	 * in the chat room to send the message to everyone.
	 * 
	 * @param packet The data packet to be sent.
	 * @throws RemoteException For any network errors.
	 */
	void sendMessage(ReceiverDataPacket<?> packet) throws RemoteException;
	
}

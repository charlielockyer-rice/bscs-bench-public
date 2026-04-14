package common.connector;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.connector.messageType.IConnectionMsg;

/** 
 * The underlying remote (stub) of INamedConnection. This is the actual receiver of messages.
 *  
 * @author Group B
 *
 */
public interface IConnection extends Remote {
	
	/**
	 * Method for the connection (stub) to receive messages.
	 * @param packet data packet the message that's received
	 * @throws RemoteException if something goes wrong with the remote connection
	 */
	public void receiveMessage(ConnectionDataPacket<? extends IConnectionMsg> packet) throws RemoteException;
	
}

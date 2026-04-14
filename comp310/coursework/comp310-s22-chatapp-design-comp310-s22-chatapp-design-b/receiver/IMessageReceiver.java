package common.receiver;

import java.rmi.Remote;
import java.rmi.RemoteException;

import common.receiver.messageType.ICommunicationMsg;

/**
 * The underlying remote (stub) of INamedMessageReceiver. This is the actual receiver of messages.
 *  
 * @author Group B
 *
 */
public interface IMessageReceiver extends Remote {

	/**
	 * Method for the MessageReceiver (stub) to receive messages.
	 * @param packet data packet the message that's received
	 * @throws RemoteException if something goes wrong with the remote connection
	 */
	public void receiveMessage(MessageDataPacket<? extends ICommunicationMsg> packet) throws RemoteException;
}

package common.connector;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines how one ChatApp instance can complete a connection to another and invite
 * another to a chat room.
 * 
 * To use this interface, each ChatApp instance must house a single IConnector RMI server object (NOT
 * a stub).
 * 
 * @author Design Group J
 */
public interface IConnector extends Remote {
	
	/**
	 * Send a data packet to the connector.
	 * 
	 * @param packet The data packet to be sent.
	 * @throws RemoteException For any network errors.
	 */
	public void sendMessage(ConnectorDataPacket<?> packet) throws RemoteException;
	
	/**
	 * A factory method to make a NamedConnector from a IConnector.
	 * 
	 * Note that in our design, the INamedConnector is obtained through
	 * this factory method on the IConnector, not directly from the discovery
	 * server and registry. This is because the registry can only hold stubs, and
	 * the INamedConnector is not a stub; it is a wrapper around a stub, following
	 * the dyad pattern discussed in class.
	 * 
	 * @return A NamedConnector that of wrapping a connector.
	 * @throws RemoteException For any network errors.
	 */
	public INamedConnector makeNamedConnector() throws RemoteException;
	
}

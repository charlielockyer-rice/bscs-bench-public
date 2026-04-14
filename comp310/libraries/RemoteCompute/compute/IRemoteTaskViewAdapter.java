package provided.remoteCompute.compute;

import java.rmi.*;

/**
 * A remote view adapter that enables a task or any other part of a system to display
 * a text message on the user interface of a remote system.<br>
 * <b>This adapter IS an RMI Server object!</b>
 * Instantiations of this interface should NEVER be sent to a remote machine, <em>only
 * STUBs made from this Remote object should ever be transmitted.</em>
 * @author swong
 *
 */
public interface IRemoteTaskViewAdapter extends Remote {

	/**
	 * Append the given string to the remote view's display
	 * @param s the string to display
	 * @throws RemoteException thrown if a network error occurs
	 */
	public void append(String s) throws RemoteException; 

	/**
	 * Null adapter object that only prints the given string to the standard err output.
	 */
	public static final IRemoteTaskViewAdapter NULL_ADAPTER = new IRemoteTaskViewAdapter() {

		@Override
		public void append(String s) throws RemoteException {
			System.err.println("[IRemoteTaskViewAdapter.NULL_ADAPTER.append()] The following message was NOT transmitted: "+s);
		}
	};

}

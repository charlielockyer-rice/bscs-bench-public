package provided.rmiUtils.logger;

import java.rmi.Remote;
import java.rmi.RemoteException;

import provided.logger.ILogEntry;

/**
 * An RMI Remote interface that provides a remote logging service.   This interface is
 * the Remote equivalent of an ILogEntryProcessor.   This interface is NOT equivalent to ILogger!
 * In particular, this interface does NOT provide LogLevel filtering as an ILogger does.
 * @author swong
 *
 */
public interface IRemoteLogService extends Remote {
	
	/**
	 * A default RMI Registry bound name to use if no other name is needed.
	 */
	public static final String DEFAULT_BOUND_NAME = "RemoteLogService";

	/**
	 * Log the given ILogEntry to an implementation-specific ILogEntryProcessor or ILogger.
	 * @param logEntry The ILogEntry to log
	 * @throws RemoteException upon network error.
	 */
	public void accept(ILogEntry logEntry) throws RemoteException;
}

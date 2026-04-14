package provided.rmiUtils.logger.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;

import provided.logger.ILogEntry;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.rmiUtils.logger.IRemoteLogService;

/**
 * An RMI server implementation of the IRemoteLogService
 * @author swong
 *
 */
public class RemoteLogService implements IRemoteLogService {
	
	/**
	 * The log entry processor that this service delegates its logging to.
	 */
	private ILogEntryProcessor logEntryProc;
	
	/**
	 * It true, automatically add the local datetime to all incoming log entries.
	 */
	private boolean addDateTime = true;

	/**
	 * Constructor for the class
	 * @param logEntryProc The log entry processor that this service delegates its logging to.
	 */
	public RemoteLogService(ILogEntryProcessor logEntryProc) {
		this(logEntryProc, true);
	}

	/**
	 * Constructor for the class
	 * @param logEntryProc The log entry processor that this service delegates its logging to.
	 * @param addDateTime If true, automatically adds the local date-time to all incoming log entry messages.
	 */
	public RemoteLogService(ILogEntryProcessor logEntryProc, boolean addDateTime) {
		this.logEntryProc = logEntryProc;
		this.addDateTime = addDateTime;
	}
	
	/**
	 * Constructor for the class
	 * @param logger The logger that this service delegates its logging to.
	 */
	public RemoteLogService(ILogger logger) {
		this(logger, true);
	}
	
	/**
	 * Constructor for the class
	 * @param logger The logger that this service delegates its logging to.
	 * @param addDateTime If true, automatically add the local date-time to the log entry.
	 */
	public RemoteLogService(ILogger logger, boolean addDateTime) {
		this(new ILogEntryProcessor() {

			@Override
			public void accept(ILogEntry logEntry) {
				logger.log(logEntry);
			}}, addDateTime);
	}

	@Override
	public void accept(ILogEntry logEntry) throws RemoteException {
		logEntryProc.accept(augmentLogEntry(logEntry));
	}
	
	/**
	 * Convenience method to create the RMI stub from this RMI Server object instance.
	 * @param stubPort The port for the stub to use.   This should be the same as that used by the rest of the application.
	 * @return An RMI Stub object
	 * @throws RemoteException if stub creation fails.
	 */
	public IRemoteLogService makeStub(int stubPort) throws RemoteException  {
		
		try {
			return (IRemoteLogService) UnicastRemoteObject.exportObject(this, stubPort);
		} catch (RemoteException e) {
			String errMsg = "Exception while creating stub: "+e;
			logEntryProc.accept(ILogEntry.make(LogLevel.ERROR, getCodeLoc(e.getStackTrace()), errMsg));
			System.err.println("[RemoteLogService.makeStub()] "+errMsg);
			e.printStackTrace();
			throw e;
		}
	}
	

	/**
	 * Return a string describing the location of the code where the event of interest occurred, given a stack trace.  
	 * The desired code location is assumed to be the caller of the caller of this method.
	 * @param stackTraces  A stack trace
	 * @return A description of the code location 
	 */
	private String getCodeLoc(StackTraceElement[] stackTraces) {
		return getCodeLoc(stackTraces, 1);
	}	
	
	/**
	 * Return a string describing the location of the code where the event of interest occurred, given a stack trace.  
	 * The level parameter enables flexibility in adjusting the described code location relative to the calling of this method.
	 * @param stackTraces An array of the lines of the stack trace
	 * @param level 0 = caller of this method, 1 = caller of caller, etc.
	 * @return A description of the code location 
	 */
	private String getCodeLoc(StackTraceElement[] stackTraces, int level) {
		StackTraceElement stackLoc = stackTraces[level+2]; //Thread.currentThread().getStackTrace()[level+2];
		String classNameLoc = stackLoc.getClassName();
		String methodNameLoc = stackLoc.getMethodName();

		String codeLoc = stackLoc.getFileName() + ":" + stackLoc.getLineNumber();

		String loc = stackLoc.getModuleName() + "/" + classNameLoc + "." + methodNameLoc + "(" + codeLoc + ")"; 
		return loc;
	}
	
	/**
	 * Create a new ILogEntry from the given log entry by adding the local date-time to the log entry's message value.
	 * @param logEntry The log entry to augment.
	 * @return A new ILogEntry instance
	 */
	private ILogEntry augmentLogEntry(ILogEntry logEntry) {
		return ILogEntry.make(logEntry.getLevel(), logEntry.getLoc(), (addDateTime ? "("+LocalDateTime.now()+") " : "")+logEntry.getMsg());
	}

}

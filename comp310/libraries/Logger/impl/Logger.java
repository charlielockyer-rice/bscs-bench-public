package provided.logger.impl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import provided.logger.ILogEntry;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.LogLevel;

/**
 * DEVELOPER CODE SHOULD NOT INSTANTIATE THIS CLASS DIRECTLY!   USE ILoggerControl TO INSTANTIATE
 * A BASIC ILogger INSTANCE.
 * A basic utility ILogger implementation that should work in most scenarios.
 * @author swong
 *
 */
public class Logger implements ILogger {

	/**
	 * The ILogger implementation to be shared on an application-wide level.
	 */
	public static final Logger SHARED = new Logger();

	/**
	 * The next logger in the chain.
	 */
	private ILogger nextLogger = ILogger.NULL;

	/**
	 * The current set of log entry processors to use
	 */
	private Set<ILogEntryProcessor> leProcessors = new HashSet<ILogEntryProcessor>();

	/**
	 * The current minimum logging level
	 */
	private LogLevel currentLogLevel = LogLevel.INFO;

	/**
	 * Instantiate this logger with a single 
	 * log entry processor = ILogEntryProcessor.DEFAULT
	 * and a minimum logging level of LogLevel.INFO
	 */
	public Logger() {
		this(ILogEntryProcessor.MakeDefault());
	}

	/**
	 * Instantiate this logger with a single 
	 * log entry processor = ILogEntryProcessor.DEFAULT
	 * and a minimum logging level of LogLevel.INFO
	 * @param level The minimum log level to use.
	 */
	public Logger(LogLevel level) {
		this(ILogEntryProcessor.MakeDefault(), level);
	}

	/**
	 * Instantiate this logger with the given log entry processor
	 * and a minimum logging level of LogLevel.INFO
	 * @param logDisplayFn The log entry processor to use.
	 */
	public Logger(ILogEntryProcessor logDisplayFn) {
		this(logDisplayFn, LogLevel.INFO);
	}

	/**
	 * Instantiate this logger with the given log entry processor
	 * and given minimum logging level
	 * @param logDisplayFn The log entry processor to use.
	 * @param logLevel The minimum logging level to process.
	 */
	public Logger(ILogEntryProcessor logDisplayFn, LogLevel logLevel) {
		this.setLogProcessor(logDisplayFn);
		this.currentLogLevel = logLevel;
	}

	@Override
	public void log(LogLevel level, String msg) {
		StackTraceElement stackLoc = Thread.currentThread().getStackTrace()[2];
		String classNameLoc = stackLoc.getClassName();
		String methodNameLoc = stackLoc.getMethodName();

		String codeLoc = stackLoc.getFileName() + ":" + stackLoc.getLineNumber();

		String loc = stackLoc.getModuleName() + "/" + classNameLoc + "." + methodNameLoc + "(" + codeLoc + ")"; //stackLoc.toString();
		this.log(ILogEntry.make(level, loc, msg));

	}

	@Override
	public void log(ILogEntry logEntry) {
		//		if(level.getRank() <= currentLogLevel.getRank()) {
		if (0 >= currentLogLevel.compareTo(logEntry.getLevel())) {
			leProcessors.forEach((fn) -> {
				fn.accept(logEntry);
			});
		}
		this.nextLogger.log(logEntry); // recurse with known code location
	}

	@Override
	public void setLogProcessor(ILogEntryProcessor logProcFn) {
		Objects.requireNonNull(logProcFn, "[Logger.setLogProcessor()] logProcFn cannot be null");
		leProcessors.clear();
		leProcessors.add(logProcFn);
	}

	@Override
	public boolean addLogProcessor(ILogEntryProcessor logProcFn) {
		Objects.requireNonNull(logProcFn, "[Logger.addLogProcessor()] logProcFn cannot be null");
		if (leProcessors.contains(logProcFn)) {
			return false;
		} else {
			leProcessors.add(logProcFn);
			return true;
		}
	}

	@Override
	public boolean removeLogProcessor(ILogEntryProcessor logProcFn) {
		Objects.requireNonNull(logProcFn, "[Logger.removeLogProcessor()] logProcFn cannot be null");
		if (1 == leProcessors.size()) {
			return false;
		} else {
			return leProcessors.remove(logProcFn);
		}
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.currentLogLevel = logLevel;
	}

	@Override
	public ILogger append(ILogger logger) {
		// check for loops!
		if (logger.find(this)) {
			throw new IllegalStateException("[Logger.append()] Possible circular chain detected! Individual logger, "
					+ this + ", found in logger chain, " + logger);
		} else {
			nextLogger = nextLogger.append(logger);
			return this;
		}
	}

	@Override
	public ILogger remove(ILogger logger) {
		if (this.equals(logger)) {
			return ILogger.NULL;
		} else {
			nextLogger = nextLogger.remove(logger);
			return this;
		}
	}

	@Override
	public boolean find(ILogger logger) {
		return this.equals(logger) ? true : nextLogger.find(logger);
	}

}

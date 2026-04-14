package provided.logger;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a complete log entry with a log level, code location and a message.
 * @author swong
 *
 */
public interface ILogEntry extends Serializable {

	/**
	 * Get this log entry's log level
	 * @return The log level
	 */
	public LogLevel getLevel();

	/**
	 * Get this log entry's code location
	 * @return The code location of the logging event as a String
	 */
	public String getLoc();

	/**
	 * Get this log entry's message
	 * @return The message
	 */
	public String getMsg();

	/**
	 * Instantiate a new ILogEntry instance with the given non-null characteristics.
	 * @param level The log level of the entry
	 * @param loc The code location of the entry as a String
	 * @param msg The message of the entry
	 * @return A new ILogEntry instance
	 */
	public static ILogEntry make(final LogLevel level, final String loc, final String msg) {
		Objects.requireNonNull(level, "[ILogEntry.make()] A log entry must have a non-null log level.");
		Objects.requireNonNull(loc, "[ILogEntry.make()] A log entry must have a non-null code location.");
		Objects.requireNonNull(msg, "[ILogEntry.make()] A log entry must have a non-null message.");

		return new ILogEntry() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = -62537418468150468L;

			@Override
			public LogLevel getLevel() {
				return level;
			}

			@Override
			public String getLoc() {
				return loc;
			}

			@Override
			public String getMsg() {
				return msg;
			}

		};
	}

}

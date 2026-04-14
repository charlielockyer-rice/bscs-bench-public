package provided.logger;

/**
 * Enumeration values for classifying log messages.
 * Note that all enums (java.lang.Enum) derivatives implement the Comparable
 * interface, so to compare them, use their inherited compareTo() method.
 * The relative "size" of the log levels is defined by their order of declaration 
 * with the first declaration having the "smallest" value.   
 * 
 * 
 * @author swong
 *
 */
public enum LogLevel {
	/**
	 * Detailed information for debugging purposes. 
	 */
	DEBUG,

	/**
	 * Information about general program operation. 
	 */
	INFO,

	/**
	 * Critical program operation information 
	 */
	CRITICAL,

	/**
	 * Error condition information 
	 */
	ERROR;

}

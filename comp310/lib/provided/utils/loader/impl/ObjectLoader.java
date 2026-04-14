package provided.utils.loader.impl;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.function.Function;
import java.util.function.BiFunction;

import provided.logger.LogLevel;

/**
 * Factory that dynamically class loads and instantiates an object of type ReturnT
 * This is a non-recursive object loader that should be used when the fully qualified name of the desired class can be determined BEFORE the 
 * object loader is invoked.
 * @author Stephen Wong
 *
 * @param <ReturnT>  The type of object to be created.
 */
public class ObjectLoader<ReturnT> extends AObjectLoader<ReturnT> {

	/**
	 * Lambda function to return an error object of type ReturnT
	 */
	private BiFunction<String, Object[], ReturnT> errorFac;
//	private Function<Object[], ReturnT> errorFac;
	

	/**
	 * Constructor for the class.   The given errorFac is used to generate instances when the loadInstance() method
	 * is otherwise unable to do so because of a processing error.
	 * @param errorFac A factory method that takes the same array of input parameters that loadInstance() 
	 * takes and returns an instance of ReturnT.
	 */
	@Deprecated
	public ObjectLoader(Function<Object[], ReturnT> errorFac) {
		// Wrap errorFac in a BiFunction and delegate to the other constructor.
		this((classname, args) -> { 
			// How to use logger here?  Compiler disallows reference to logger in constructor.
			System.err.println("WARNING!! [ObjectLoader] Using deprecated error factory type that discards the attempted classname:  "+classname);
			return errorFac.apply(args); 
		});
	}
	
	/**
	 * Constructor for the class.   The given errorFac is used to generate instances when the loadInstance() method
	 * is otherwise unable to do so because of a processing error.
	 * @param errorFac A factory method that takes 2 params, the same as loadInstance(): 
	 * The attempted classname and the array of input parameters.  
	 * The errorfFac function returns an instance of ReturnT.  
	 */
	public ObjectLoader(BiFunction<String, Object[], ReturnT> errorFac) {
		super();
		this.errorFac = errorFac;
	}
	

	/**
	 * Prints the given Exception to stderr and then invokes the stored errorFac to generate an error object.
	 */
	@Override
	protected ReturnT errorHandler(Exception ex, String className, Object... args) {
	
		logger.log(LogLevel.ERROR, "\nObjectLoader.loadInstance(" + className + ", "
				+ (new ArrayList<Object>(Arrays.asList(args))) + "):\n   Exception = " + ex);
		ex.printStackTrace();
		return errorFac.apply(className, args); // Make the error object
	}
}

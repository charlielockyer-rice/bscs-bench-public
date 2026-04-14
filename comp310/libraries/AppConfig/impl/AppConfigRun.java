package provided.config.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import provided.config.AppConfig;

/**
 * An AppConfig that is used to invoke another application's main() method.  
 * This is used when a single application is used to launch other applications, 
 * e.g. a "master" application launching a server vs. one of several clients.   
 * The application being launched can use internal AppConfigs to perform further 
 * customizations based on the value of the command line parameters passed to its 
 * main() method.  Command line parameters are specified at construction time and
 * additional command line parameters can be appended at invocation time as well.
 * @author swong
 *
 */
public class AppConfigRun extends AppConfig {
	
	/**
	 * The main method of the given class
	 */
	private Method mainMethod;
	/**
	 * The command line arguments to use when invoking the given class's main method. 
	 */
	private String[] args;

	/**
	 * Construct an instance of AppConfigRun with the given friendly name, class with a main() method, and command line parameters for invoking the given class's main() method.
	 * @param name  Friendly name for this AppConfig
	 * @param runClass The class object of the desired class to run.   This class MUST have a public static void main(String[] args) method!
	 * @param args  A varargs of the command line parameters used when invoking the given class's main() method.
	 * @throws NoSuchMethodException  If the main() method does not exist in the given class.
	 * @throws SecurityException If a security manager, s, is present and the caller's class loader is not the same as or an ancestor of the class loader for the current class and invocation of s.checkPackageAccess() denies access to the package of this class.
	 * 
	 */
	public AppConfigRun(String name, Class<?> runClass, String... args) throws NoSuchMethodException, SecurityException {
		super(name);
		mainMethod = runClass.getMethod("main", String[].class);
		this.args = args;
	}
	
	/**
	 * Run the configured class's main() method with the configured command line parameters.
	 * @param extraArgs Vararg of additional command line parameters that are appended to the parameters configured during construction.
	 * @throws IllegalAccessException if this Method object is enforcing Java language access control and the underlying method is inaccessible
	 * @throws IllegalArgumentException if the method is an instance method and the specified object argument is not an instance of the class or interface declaring the underlying method (or of a subclassor implementor thereof); if the number of actualand formal parameters differ; if an unwrapping conversion for primitive arguments fails; or if,after possible unwrapping, a parameter valuecannot be converted to the corresponding formal parameter type by a method invocation conversion
	 * @throws InvocationTargetException if the underlying method throws an exception.
	 */
	public void run(String... extraArgs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String[] allArgs = Arrays.copyOf(args, args.length+extraArgs.length); // copy the original args into a larger array
		System.arraycopy(extraArgs, 0, allArgs, args.length, extraArgs.length);  // Add the extra args into the larger array
		mainMethod.invoke(null, (Object) allArgs);  // Cast to Object not Object[] due to vararg issues.
	}
}





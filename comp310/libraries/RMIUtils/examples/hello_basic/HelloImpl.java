package provided.rmiUtils.examples.hello_basic;

import java.rmi.RemoteException;

import provided.rmiUtils.examples.hello_common.IHello;

/**
 * RMI "Server" object implementation.
 * Demo now uses anonymous inner class implementation IHello
 * @author Stephen Wong
 *
 */
@Deprecated
public class HelloImpl implements IHello {

	@Override
	/**
	 * Concrete implementation of the method defined by the Hello interface.
	 */
	public String sayHello() throws RemoteException {
		return "Hello RMI World!";
	}

}

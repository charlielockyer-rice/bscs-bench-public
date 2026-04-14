package common.connector;

import java.io.Serializable;

/**
 * 
 * A wrapper (dyad) of IConnection together with its name.
 * 
 * @author Group B
 *
 */
public interface INamedConnection extends Serializable {
	
	/**
	 * Get the name of the stub.
	 * @return the name of the stub
	 */
	public String getName();
	
	/**
	 * Get the stub it wraps around.
	 * 
	 * @return stub.
	 */
	public IConnection getConnectionStub();
	
}

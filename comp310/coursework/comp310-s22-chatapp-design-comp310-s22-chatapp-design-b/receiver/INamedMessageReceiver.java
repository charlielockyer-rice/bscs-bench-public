package common.receiver;

import java.io.Serializable;

/**
 * 
 * A wrapper (dyad) of IMessageReceiver together with its name.
 * 
 * @author Group B
 *
 */
public interface INamedMessageReceiver extends Serializable {
	
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
	public IMessageReceiver getMessageReceiverStub();
	
}

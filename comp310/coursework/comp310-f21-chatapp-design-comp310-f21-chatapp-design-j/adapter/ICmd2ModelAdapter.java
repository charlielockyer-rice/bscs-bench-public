package common.adapter;

import java.io.File;

import common.receiver.INamedReceiver;
import common.receiver.messages.IReceiverMsg;
import provided.logger.ILogger;
import provided.mixedData.MixedDataKey;

/**
 * The adapter given to an unknown command by the ChatApp system.
 * 
 * @author Design Group J
 *
 */
public interface ICmd2ModelAdapter {
	
	/**
	 * Given a component factory, builds a component and installs it in a fixed pane. The component must not overwrite
	 * any previously added components.
	 * 
	 * @param fac The component factory.
	 * @param name The name of the component to install.
	 */
	public void buildFixedComponent(IComponentFactory fac, String name);
	
	/**
	 * Given a component factory, builds a component and installs it in the scrolling pane. The component must not overwrite
	 * any previously added components.
	 * 
	 * @param fac The component factory.
	 * @param name The name of the component to install.
	 */
	public void buildScrollingComponent(IComponentFactory fac, String name);
	
	/**
	 * Put an item in the ChatApp instance's mixed data dictionary.
	 * 
	 * @param <T> The type of the data.
	 * @param key The MixedDataKey that will be used to look up the data.
	 * @param value The data to be stored.
	 * @return The previous value associated with the key, or null if there was none.
	 */
	public <T> T put(MixedDataKey<T> key, T value);
	
	/**
	 * Get an item from the ChatApp instance's mixed data dictionary.
	 * 
	 * @param <T> The type of the data.
	 * @param key The MixedDataKey with which to look up the data.
	 * @return The data associated with the key, or null if there is none.
	 */
	public <T> T get(MixedDataKey<T> key);
	
	/**
	 * Check whether or not a given key is in the ChatApp instance's mixed data dictionary.
	 * 
	 * @param key The MixedDataKey to examine.
	 * @return True if the key is in the dictionary, and false otherwise.
	 */
	public boolean containsKey(MixedDataKey<?> key);
	
	/**
	 * Get the human-friendly name of the chat room within which the command is installed.
	 * 
	 * @return The chat room name.
	 */
	public String getRoomName();
	
	/**
	 * Get the human-friendly name of the ChatApp instance within which the command is installed.
	 * 
	 * Depending on the implementation, this could be a user name, or a server name.
	 * 
	 * @return The instance name.
	 */
	public String getInstanceName();
	
	/**
	 * Get the human-friendly name of the INamedReceiver associated with the ChatApp and chat room within
	 * which the command is installed.
	 * 
	 * @return The receiver name.
	 */
	public String getReceiverName();
	
	/**
	 * Send a message to every receiver currently in the chat room.
	 * 
	 * The ChatApp implementation is responsible for constructing the DataPacket and sending it to each receiver in the chat room.
	 * 
	 * This is equivalent to calling send() on each receiver in the chat room, but in our design the command does not have direct access
	 * to the room roster.
	 * 
	 * @param <T> The type of the message to send.
	 * @param msg The message to send.
	 */
	public <T extends IReceiverMsg> void broadcast(T msg);
	
	/**
	 * Send a piece of data as a message to the specified receiver. 
	 * 
	 * The ChatApp implementation is responsible for constructing the DataPacket and sending it to the receiver. It should also verify
	 * that the given receiver is in the chat room within which the command is installed. 
	 * 
	 * A reference to an IReciever stub can be obtained from local storage (via the mixed-data dictionary) or from an incoming DataPacket
	 * via the getSender() method.
	 * 
	 * Note that such a method is necessary to allow a command to communicate over the network, per the highlighted guidelines immediately
	 * below "Staff Feedback" <a href="https://canvas.rice.edu/courses/41326/pages/chatapp-api-design-proposals">here</a>.
	 * 
	 * From a security perspective, the unknown command might be able to send a message directly to the IReceiver object, bypassing the
	 * ChatApp implementation. Eliminating this vulnerability (which really just comes down to an unknown command being able to forge the
	 * sender of a DataPacket, something that could be done by a malicious ChatApp regardless) would require a complicated layer of indirection
	 * that does not seem worth it for our purposes. 
	 * 
	 * @param <T> The type of the message to send.
	 * @param data The message to send.
	 * @param recv The named receiver to send the data to.
	 */
	public <T extends IReceiverMsg> void send(T data, INamedReceiver recv);
	
	/**
	 * Pop up a file chooser to select a file, which is returned as an immutable File object for further reading or writing.
	 * 
	 * Implementation hints can be found <a href="https://www.codejava.net/java-se/swing/show-simple-open-file-dialog-using-jfilechooser">here</a>.
	 * 
	 * @return The chosen File, or null if cancel is selected.
	 */
	public File getFile();
	
	/**
	 * Pop up a file chooser to save a file, which is returned as an immutable File object for further reading or writing.
	 * 
	 * Implementation hints can be found <a href="https://www.codejava.net/java-se/swing/show-save-file-dialog-using-jfilechooser">here</a>.
	 * 
	 * @param defaultName The default name of the file to save in the file chooser.
	 * @return The chosen File, or null if cancel is selected.
	 */
	public File saveFile(String defaultName);
	
	/**
	 * Get a logger that can be used by the command to output status information.
	 * 
	 * This must return a new instance of ILogger, so that the command cannot mutate the system logger.
	 * 
	 * @return The logger.
	 */
	public ILogger getLogger();
	
}

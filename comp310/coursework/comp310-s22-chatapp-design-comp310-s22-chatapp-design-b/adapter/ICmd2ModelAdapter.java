package common.adapter;

import java.util.function.Supplier;

import javax.swing.JComponent;

import common.receiver.INamedMessageReceiver;
import common.receiver.messageType.ICommunicationMsg;

/**
 * An adapter that defines the "sandbox" in which a foreign (externally sourced) command 
 * is allowed to operate in the local system.
 * 
 * @author Group B.
 */
public interface ICmd2ModelAdapter {

	/**
	 * Default no-op adapter.
	 */
	public static final ICmd2ModelAdapter DEFAULT_ADAPTER = new ICmd2ModelAdapter() {

		@Override
		public void displayText(String text) {
			
		}
		
		@Override
		public void displayComponent(String label, Supplier<JComponent> fac) {
			
		}	
		
		@Override
		public void displayDebugInfo(String info) {
			
		}

		@Override
		public String getLocalMessageReceiverName() {
			return null;
		}

		@Override
		public String getLocalRoomName() {
			return null;
		}

		@Override
		public <T extends ICommunicationMsg> void sendMsgToRoom(T data) {
			
		}

		@Override
		public <T extends ICommunicationMsg> void sendMsgToReceiver(T data, INamedMessageReceiver namedReceiver) {
			
		}

	};
	
	/**
	 * Display text messages on the chatroom view.
	 * @param text the text message to display
	 */
	public void displayText(String text);
	
	/**
	 * Add a component to the chat room view.
	 * NOTE: DO NOT OVERWRITE EXISTING COMPONENTS WHEN 
	 * ADDING NEW COMPONENTS!!!!!
	 * @param label the label of the component
	 * @param fac a factory of the component to add 
	 */
	public void displayComponent(String label, Supplier<JComponent> fac);
	
	/**
	 * Display debug info on the chatroom view. 
	 * @param info the information to display
	 */
	public void displayDebugInfo(String info);
	
	/**
	 * Get the name of the local message receiver.
	 * @return the name of the local message receiver.
	 */
	public String getLocalMessageReceiverName();
	
	/**
	 * Get the name of the local room.
	 * @return the name of the local room.
	 */
	public String getLocalRoomName();
	
	/**
	 * Send a message to the current room.
	 * @param <T> the type of data to send to the current room.
	 * @param data the data in the data packet to be sent to the current room.
	 */
	public <T extends ICommunicationMsg> void sendMsgToRoom(T data);
	
	/**
	 * Send a message to a specific receiver in the current room.
	 * @param <T> the type of data to send to the current room.
	 * @param data the data in the data packet to be sent to the current room.
	 * @param namedReceiver an INamedMessageReceiver object to receive the data.
	 */
	public <T extends ICommunicationMsg> void sendMsgToReceiver(T data, INamedMessageReceiver namedReceiver);
	
}

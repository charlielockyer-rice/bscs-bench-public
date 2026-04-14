package mcx1_cwl6.miniMVC.controller;

import java.util.HashSet;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.connector.INamedConnection;
import common.receiver.INamedMessageReceiver;
import mcx1_cwl6.ChatRoom;
import mcx1_cwl6.miniMVC.view.IMiniView2ModelAdapter;
import mcx1_cwl6.miniMVC.model.ChatRoomModel;
import mcx1_cwl6.miniMVC.model.IMini2MainAdapter;
import mcx1_cwl6.miniMVC.model.IMiniModel2ViewAdapter;
import mcx1_cwl6.miniMVC.view.ChatRoomView;
import provided.rmiUtils.RMIPortConfigWithBoundName;

/**
 * Controller of the mini MVC
 */
public class ChatRoomController {

	/**
	 * System logger to use
	 */
	//private ILogger sysLogger = ILoggerControl.getSharedLogger();
	
	/**
	 * Model of chatroom
	 */
	private ChatRoomModel model;
	 
	/**
	 * GUI of chatroom
	 */
	private ChatRoomView view;

	/**
	 * Constructor for the ChatRoomController.
	 * @param chatroom ChatRoom representation of this chatroom
	 * @param config info
	 * @param userConnector of parent ChatApp
	 * @param m2MAdpt adapter from mini-MVC to main MVC
	 */
	public ChatRoomController(ChatRoom chatroom, RMIPortConfigWithBoundName config, INamedConnection userConnector, IMini2MainAdapter m2MAdpt) {
		
		view = new ChatRoomView(new IMiniView2ModelAdapter() {

			@Override
			public void sendText(String textMsg) {
				model.sendText(textMsg);
				
			}

			@Override
			public void sendUnknown() {
				model.sendUnknown();
				
			}

			@Override
			public void leaveRoom() {
				model.leaveRoom();
				
			}

			@Override
			public void removeRoomFromList() {
				model.removeRoomFromList();
				
			}

			
			
		});

		model = new ChatRoomModel(chatroom, config, userConnector, m2MAdpt, new IMiniModel2ViewAdapter() {

			@Override
			public void displayText(String str) {
				view.displayText(str);
			}

			@Override
			public void displayComponent(String label, Supplier<JComponent> fac) {
				view.displayComponent(label, fac);
			}

			@Override
			public void updateUserSet(HashSet<INamedMessageReceiver> userSet) {
				view.updateUserSet(userSet);
				
			}
		});
	}
	
	/**
	 * Get the miniModel
	 * @return The miniModel
	 */
	public ChatRoomModel getMiniModel() { return model;}
	
	/**
	 * Get the miniView
	 * @return The miniView
	 */
	public ChatRoomView getMiniView() { 
		System.out.println("ChatRoomGUI: " + view);
		return view;}
	
	/**
	 * Start the mini model and mini view.
	 */
	public void start() {
		System.out.println("start");
		model.start();
		view.start();
	}
	
}























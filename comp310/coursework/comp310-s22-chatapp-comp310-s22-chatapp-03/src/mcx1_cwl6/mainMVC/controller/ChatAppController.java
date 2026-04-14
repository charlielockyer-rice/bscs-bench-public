package mcx1_cwl6.mainMVC.controller;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import common.connector.IInitialConnection;
import common.connector.INamedConnection;
import mcx1_cwl6.miniMVC.controller.ChatRoomController;
import mcx1_cwl6.miniMVC.model.ChatRoomModel;
import mcx1_cwl6.miniMVC.model.IMini2MainAdapter;
import mcx1_cwl6.miniMVC.view.ChatRoomView;
import mcx1_cwl6.ChatRoom;
import mcx1_cwl6.mainMVC.model.ChatAppModel;
import mcx1_cwl6.mainMVC.model.IMain2MiniAdapter;
import mcx1_cwl6.mainMVC.model.IMainModel2ViewAdapter;
import mcx1_cwl6.mainMVC.view.ChatAppView;
import mcx1_cwl6.mainMVC.view.IMainView2ModelAdapter;
import provided.config.impl.AppConfigMap;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryModel;
import provided.discovery.impl.model.IDiscoveryModelToViewAdapter;
import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.pubsubsync.IPubSubSyncManager;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfigWithBoundName;

/**
 * The ChatApp Controller
 */
public class ChatAppController {
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();

	/**
	 * The model in use
	 */
	private ChatAppModel model;

	/**
	 * The view in use
	 */
	private ChatAppView<INamedConnection, ChatRoom> view; 

	/**
	 * The Discovery server UI panel for the view
	 */
	private DiscoveryPanel<IEndPointData> discPnl;

	/**
	 * A self-contained model to handle the discovery server.   MUST be started AFTER the main model as it needs the IRMIUtils from the main model! 
	 */
	private DiscoveryModel<IInitialConnection> discModel; // Replace "IRemoteStubType" with the appropriate for the application, i.e. the Remote type of stub in Registry)  

	/**
	 * App configuration
	 */
	private RMIPortConfigWithBoundName config;
	
	/**
	 * Default bound name
	 */
	private final static String BOUND_NAME = "User";
	
	/**
	 * Map used to map strings to config info.
	 */
	private static AppConfigMap<RMIPortConfigWithBoundName> configMap = new AppConfigMap<RMIPortConfigWithBoundName>(	    
			new RMIPortConfigWithBoundName("UserA", IRMI_Defs.STUB_PORT_SERVER,
					IRMI_Defs.CLASS_SERVER_PORT_SERVER, BOUND_NAME + "A"), 
			new RMIPortConfigWithBoundName("UserB", IRMI_Defs.STUB_PORT_SERVER + 1,
							IRMI_Defs.CLASS_SERVER_PORT_SERVER + 1, BOUND_NAME + "B"),
			new RMIPortConfigWithBoundName("UserC", IRMI_Defs.STUB_PORT_SERVER + 2,
					IRMI_Defs.CLASS_SERVER_PORT_SERVER + 2, BOUND_NAME + "C"));

	
	/**
	 * Constructor of the class.   Instantiates and connects the model and the view plus the discovery panel and model.
	 * @param username The username of the user
	 */
	public ChatAppController(String username) {
		
		this.config = configMap.getConfig(username);
		System.out.println(username);
		System.out.println(this.config);

		//sysLogger.setLogLevel(LogLevel.DEBUG); // For debugging purposes.   Default is LogLevel.INFO

		
		discPnl = new DiscoveryPanel<IEndPointData>(new IDiscoveryPanelAdapter<IEndPointData>() {

			/**
			 * watchOnly is ignored b/c discovery model configured for watchOnly = true
			 */
			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly,
					Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// Ask the discovery model to connect to the discovery server on the given category and use the given updateFn to update the endpoints list in the discovery panel.
				discModel.connectToDiscoveryServer(category, endPtsUpdateFn);
			}

			@Override
			public void connectToEndPoint(IEndPointData selectedEndPt) {
				// Ask the discovery model to obtain a stub from a remote Registry using the info from the given endpoint 
				discModel.connectToEndPoint(selectedEndPt);
			}

		}, true, true); 

		discModel = new DiscoveryModel<IInitialConnection>(sysLogger, new IDiscoveryModelToViewAdapter<IInitialConnection>() {

			@Override
			public void addStub(IInitialConnection stub) {
				model.connectToStub(stub); // Give the stub obtained from a remote Registry to the model to process
			}

		});

		model = new ChatAppModel(sysLogger, config, new IMainModel2ViewAdapter() {


			@Override
			public void displayStatus(String str) {
				view.displayStatus(str);
			}

			@Override
			public void addPeers(Set<INamedConnection> userStubSet) {
				for (INamedConnection user : userStubSet) {
					//System.out.println(user + "-> " + user.getName());
					ChatAppController.this.view.addPeer(user);
				}
				
			}

			@Override
			public void addChatRoom(ChatRoom room) {
				view.addRoomToList(room);
			}

			@Override
			public IMain2MiniAdapter makeMiniMVC(ChatRoom room, INamedConnection user) {
				ChatRoomController miniController;
				miniController = new ChatRoomController(room, config, user, new IMini2MainAdapter() {

					@Override
					public void removeRoomView(ChatRoom room) {
						view.removeRoom(room);
					}
					
					@Override
					public void addRoomUser(ChatRoom room) {
						view.addRoomToList(room);
					}
					
					@Override
					public void removeRoomList(ChatRoom room) {
						view.removeRoomFromList(room);
					}
					
				});
				
				ChatRoomView miniView = miniController.getMiniView();
				System.out.println("miniGUI -> " + miniView);
				view.addChatRoom(room.getRoomName(), miniView);

				ChatRoomModel miniModel = miniController.getMiniModel();
				
				miniController.start();
				
				return new IMain2MiniAdapter() {

					@Override
					public void joinChannel(IPubSubSyncManager manager) {
						miniModel.joinChannel(manager);
					}

					@Override
					public void createChannel(IPubSubSyncManager manager, ChatRoom room) {
						miniModel.createChannel(manager, room);
					}

					@Override
					public ChatRoom getChatRoom() {
						return miniController.getMiniModel().getRoom();
					}

					@Override
					public void appendString(String str) {
						miniView.displayText(str);
					}

					@Override
					public void addComponent(String label, Supplier<JComponent> compFac) {
						miniView.displayComponent(label, compFac);
						
					}

					@Override
					public String getFriendlyName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void leaveRoom() {
						// TODO Auto-generated method stub
						miniModel.leaveRoom();
					}
					
				};
			}

			@Override
			public void removeRoom(ChatRoom room) {
				view.removeRoom(room);
				
			}

			@Override
			public void removeAllRooms() {
				view.removeAllRooms();
				
			}

			@Override
			public void removePeer(INamedConnection userStub) {
				view.removeUser(userStub);
				
			}

		});

		view = new ChatAppView<INamedConnection, ChatRoom>(config, new IMainView2ModelAdapter<INamedConnection, ChatRoom>() {

			@Override
			public void quit() {
				model.quit();
			}

			@Override
			public void send(String text) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String connectUser(String IP) {
				//return chatAppModel.connectTo(IP);
				return null;
			}


			@Override
			public ChatRoom createRoom(String name) {
				return model.createChatRoom(name);
			}

			@Override
			public void leaveRoom(ChatRoom room) {
				
			}


			@Override
			public List<ChatRoom> getSelectedUserRooms(INamedConnection stub) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void inviteUser(INamedConnection connectedUser, ChatRoom yourRoom) {
				model.inviteUser(connectedUser, yourRoom);
				
			}
		});

	}

	/**
	 * Starts the view then the model plus the discovery panel and model.  The view needs to be started first so that it can display 
	 * the model status updates as it starts.   The discovery panel is added to the main view after the discovery model starts. 
	 */
	public void start() {
		// start the main model.  THE MODEL MUST BE STARTED _BEFORE_  model.getRMIUtils() IS CALLED!!
		model.start(); // starts the internal IRMIUtils instance too.

		discPnl.start(); // start the discovery panel
		discModel.start(model.getRMIUtils(), this.config.name, this.config.boundName); // start the discovery model using the already started IRMIUtils instance.
		view.addCtrlComponent(discPnl); // Add the discovery panel to the view's "control" panel.

		// start the main view.  Starting the view here will keep the view from showing before the discovery panel is installed.
		view.start();

	}

	/**
	 * Run the app.
	 * @param args Not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new ChatAppController(args[0])).start();
			}
		});
	}

}

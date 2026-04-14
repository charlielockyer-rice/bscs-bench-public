package mcx1_cwl6.mainMVC.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import common.connector.AConnectionDataPacketAlgoCmd;
import common.connector.ConnectionDataPacket;
import common.connector.ConnectionDataPacketAlgo;
import common.connector.IConnection;
import common.connector.IInitialConnection;
import common.connector.INamedConnection;
import common.connector.IRoom;
import common.connector.messageType.exception.IConnectionExceptionErrorMsg;
import common.connector.messageType.exception.IConnectionExceptionRejectionMsg;
import common.connector.messageType.IConnectionMsg;
import common.connector.messageType.IInviteMsg;
import common.connector.messageType.IQuitMsg;
import common.connector.messageType.ISendConnectionsMsg;
import mcx1_cwl6.ChatRoom;
import mcx1_cwl6.mainMVC.model.messageType.InviteMsg;
import mcx1_cwl6.mainMVC.model.messageType.SendConnectionsMsg;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncConnection;
import provided.pubsubsync.IPubSubSyncManager;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.rmiUtils.RMIUtils;

/**
 * Model for the main MVC
 */
public class ChatAppModel {

	/**
	 * The system logger in use
	 */
	private ILogger sysLogger;

	/**
	 * A logger that logs to the view and the system logger
	 */
	private ILogger viewLogger;

	/**
	 * The model to view adapter for the chatapp
	 */
	private IMainModel2ViewAdapter m2vAdpt;

	/**
	 * The RMIUtils in use
	 */
	private IRMIUtils rmiUtils;

	/**
	 * Local Registry
	 */
	private Registry registry;

	/**
	 * Stub of local user
	 */
	private IConnection localStub;

	/**
	 * Stub of remote user
	 */
	private INamedConnection remoteStub;
	
	/**
	 * Remote initial connection 
	 */
	private IInitialConnection remoteInitialConnection;

	/**
	 * Local remote
	 */
	private IConnection localRemote;

	/**
	 * Connector dyad for remote user's stub
	 */
	private INamedConnection connector;

	/**
	 * Stub for users connection
	 */
	private INamedConnection user;
	
	/**
	 * User initial connection
	 */
	private IInitialConnection userInitialConnection;
	
	/**
	 * User initial connection stub
	 */
	private IInitialConnection userInitialConnectionStub;
	

	/**
	 * Set of remote users' stubs.
	 */
	private HashSet<INamedConnection> knownConnections = new HashSet<INamedConnection>();
	
	//private HashSet<INamedConnection> peersAndMyself = new HashSet<INamedConnection>();

	/**
	 * Config info.
	 */
	private RMIPortConfigWithBoundName config;

	/**
	 * Collection of algorithms (commands) to handle message processing.
	 */
	private ConnectionDataPacketAlgo visitor;

	/**
	 * PubSubSync manager.
	 */
	private IPubSubSyncManager pubSubManager;

	/**
	 * Map of chatrooms and their UUIDs.
	 */
	private HashMap<UUID, IMain2MiniAdapter> mapRooms = new HashMap<UUID, IMain2MiniAdapter>();

	/** 
	 * Constructor for the chat app model
	 * 
	 * @param logger  The system logger
	 * @param config The app config
	 * @param m2vAdpt The adapter to the view
	 */
	public ChatAppModel(ILogger logger, RMIPortConfigWithBoundName config, IMainModel2ViewAdapter m2vAdpt) {
		this.sysLogger = logger;
		this.config = config;
		this.m2vAdpt = m2vAdpt;

		this.visitor = new ConnectionDataPacketAlgo((AConnectionDataPacketAlgoCmd<? extends IConnectionExceptionRejectionMsg>)new AConnectionDataPacketAlgoCmd<IConnectionExceptionRejectionMsg>() {


			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 6873454406406891063L;

			@Override
			public Void apply(IDataPacketID index, ConnectionDataPacket<IConnectionExceptionRejectionMsg> host, Void... params) {
				return null;
			}

		});

		viewLogger = ILoggerControl.makeLogger(new ILogEntryProcessor() {
			ILogEntryFormatter formatter = ILogEntryFormatter.MakeFormatter("[%1s] %2s"); // custom log entry formatting
																							// "[level] msg"

			@Override
			public void accept(ILogEntry logEntry) {
				ChatAppModel.this.m2vAdpt.displayStatus(formatter.apply(logEntry));
			}

		}, LogLevel.INFO);
		viewLogger.append(sysLogger);
	}

	/**
	 * Get the internal IRMIUtils instance being used. The discovery model start
	 * method needs the main model's IRMIUtils. ONLY call the method AFTER the
	 * model, i.e. the internal IRMIUtils, has been started!
	 * 
	 * @return The internal IRMIUtils instance
	 */
	public IRMIUtils getRMIUtils() {
		return this.rmiUtils;
	}

	/**
	 * Start the model
	 */
	public void start() {
		try {
			// Initialize RMIUtils
			rmiUtils = new RMIUtils(sysLogger);

			// Start RMI system
			rmiUtils.startRMI(this.config.classServerPort);

			this.pubSubManager = IPubSubSyncConnection.getPubSubSyncManager(sysLogger, rmiUtils,
			 this.config.stubPort);

			// Instantiate user RMI IConnector
			localRemote = new IConnection() {

				@Override
				public void receiveMessage(ConnectionDataPacket<?> packet) throws RemoteException {
					packet.execute(ChatAppModel.this.visitor);
				}


			};

			try {
				this.localStub = (IConnection) UnicastRemoteObject.exportObject(localRemote, this.config.stubPort);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Instantiate the user RMI INamedConnector
			user = new NamedConnection(ChatAppModel.this.config.name, localStub);
			this.userInitialConnection = new InitialConnection(user);
			this.userInitialConnectionStub = (IInitialConnection) UnicastRemoteObject.exportObject(userInitialConnection, this.config.stubPort);
			
			knownConnections.add(user);

			// Get local registry with IRMIUtils
			registry = rmiUtils.getLocalRegistry();

			// Bing localUserStub to registry
			registry.rebind(this.config.boundName, userInitialConnectionStub);
			viewLogger.log(LogLevel.INFO, "ChatAppModel successfully started!");

		} catch (Exception e) {
			// Send error message
			viewLogger.log(LogLevel.ERROR, "ChatAppModel Error Starting: " + e.toString());
			e.printStackTrace();
		}

		this.visitor.setCmd(ISendConnectionsMsg.GetID(), new AConnectionDataPacketAlgoCmd<ISendConnectionsMsg>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 6746376800405769245L;
			

			@Override
			public Void apply(IDataPacketID index, ConnectionDataPacket<ISendConnectionsMsg> host, Void... params) {
				ISendConnectionsMsg msg = host.getData();
				//Set<INamedConnection> savedConnections = Set.copyOf(knownConnections);

				
				
				for (INamedConnection newConnection : msg.getKnownConnections()) {
					try {
						
						
						
						if (!knownConnections.contains(newConnection)) {
							
							// adding message connections to current set of connections
							knownConnections.add(newConnection);
							
							// forwarding known connections to new connections
							newConnection.getConnectionStub().receiveMessage(new ConnectionDataPacket<ISendConnectionsMsg>(
									new SendConnectionsMsg(knownConnections), user));
							
						}
						
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				
				
				m2vAdpt.addPeers(knownConnections);
				

				return null;
			}

		});

		this.visitor.setCmd(IInviteMsg.GetID(), new AConnectionDataPacketAlgoCmd<IInviteMsg>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 8211663781539296203L;

			@Override
			public Void apply(IDataPacketID index, ConnectionDataPacket<IInviteMsg> host, Void... params) {
				
				var rooms = host.getData().getRooms();
				
				for (var room : rooms) {
					ChatRoom chatroom = new ChatRoom(room.getRoomName());
					chatroom.setUUID(room.getRoomID());
					IMain2MiniAdapter m2mAdpt = m2vAdpt.makeMiniMVC(chatroom, ChatAppModel.this.user);
					m2mAdpt.joinChannel(ChatAppModel.this.pubSubManager);
					ChatAppModel.this.mapRooms.put(chatroom.getRoomID(), m2mAdpt);
					ChatAppModel.this.m2vAdpt.addChatRoom(chatroom);
				}
				
				

				
				return null;
			}

		});

		this.visitor.setCmd(IQuitMsg.GetID(), new AConnectionDataPacketAlgoCmd<IConnectionMsg>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = -106161080968934126L;

			@Override
			public Void apply(IDataPacketID index, ConnectionDataPacket<IConnectionMsg> host, Void... params) {
				ChatAppModel.this.knownConnections.remove(host.getSender());
				ChatAppModel.this.m2vAdpt.removePeer(host.getSender());
				return null;
			}

		});

		this.visitor.setCmd(IConnectionExceptionErrorMsg.GetID(), new AConnectionDataPacketAlgoCmd<IConnectionMsg>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 670337733106212059L;

			@Override
			public Void apply(IDataPacketID index, ConnectionDataPacket<IConnectionMsg> host, Void... params) {
				ChatAppModel.this.viewLogger.log(LogLevel.ERROR, ((IConnectionExceptionErrorMsg) host.getData()).getErrorMsg());
				return null;
			}

		});


	}

	/**
	 * Leave all chat rooms, stop the RMI system and quit the app with the given
	 * exit code.
	 */
	public void quit() {
		// Leave all chat rooms
		//System.out.println("Hashmap: " + mapRooms);
		//System.out.println("\n Size: " + mapRooms.size());
		if (mapRooms.size() > 1) {
			for (HashMap.Entry<UUID, IMain2MiniAdapter> entry : mapRooms.entrySet()) {
				IMain2MiniAdapter m2mAdpt = entry.getValue();
				m2mAdpt.leaveRoom();
				mapRooms.remove(entry.getKey());
				
			}
		}
		

		// Stop RMI system and quit app
		try {
			registry.unbind(this.config.name);
			rmiUtils.stopRMI();
		} catch (Exception e) {
			viewLogger.log(LogLevel.ERROR, "ChatAppModel Stopping Error: " + e.toString());
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Connect to remote host using an IP Address and get their
	 * 
	 * @param remoteHost the remote host
	 * @return a String indicating an established connection
	 */
	public String connectTo(String remoteHost) {

		try {
			// Locate registry
			sysLogger.log(LogLevel.INFO, "Locating registry at " + remoteHost + "...");
			Registry registry = rmiUtils.getRemoteRegistry(remoteHost);

			// Get IConnector from remote user and make INamedConnector
			sysLogger.log(LogLevel.INFO, "Found registry: " + registry);
			remoteInitialConnection = (IInitialConnection) registry.lookup(this.config.boundName);
			remoteStub = remoteInitialConnection.getNamedConnection();
			connector = user;

			// Update set of connected users
			sysLogger.log(LogLevel.INFO, "Found remote stub: " + remoteStub);
			knownConnections.add(connector);
			sysLogger.log(LogLevel.INFO, "Successfully connected to " + remoteHost);

			// Sync peer msgs
			remoteStub.getConnectionStub().receiveMessage(new ConnectionDataPacket<ISendConnectionsMsg>(
					new SendConnectionsMsg(knownConnections), connector));

		} catch (Exception e) {
			sysLogger.log(LogLevel.ERROR, "Exception connecting to " + remoteHost + ": " + e);
			e.printStackTrace();
			return "No connection established!";
		}
		return "Connection to " + remoteHost + " established!";
	}

	/**
	 * Process the newly acquired stub. This is the method that the discovery model
	 * uses in "Client" or "Client + Server" usage modes
	 * 
	 * @param newStub The newly acquired stub
	 */
	public void connectToStub(IInitialConnection newStub) { // Replace "IRemoteStubType" with the appropriate for the
													// application, i.e. the Remote type of stub in Registry)
		try {
			this.remoteStub = newStub.getNamedConnection();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			connector = user;

			// Update set of connected users
			sysLogger.log(LogLevel.INFO, "Found remote stub: " + remoteStub);
			
			sysLogger.log(LogLevel.INFO, "Successfully connected to " + connector.getName());

			// Sync peer msgs
			remoteStub.getConnectionStub().receiveMessage(new ConnectionDataPacket<ISendConnectionsMsg>(
					new SendConnectionsMsg(knownConnections), user));
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		m2vAdpt.addPeers(knownConnections);

		this.viewLogger.log(LogLevel.INFO, "Connected to engine via discovery server!");
	}
	
	/**
	 * Create a new chatroom (MVC, GUI tab)
	 * @param roomName name of new room
	 * @return a new chatroom object representing this room
	 */
	public ChatRoom createChatRoom(String roomName) {
		ChatRoom room = new ChatRoom(roomName);
		IMain2MiniAdapter m2mAdpt = m2vAdpt.makeMiniMVC(room, this.user);
		
		Thread t = new Thread(() -> { 
			m2mAdpt.createChannel(this.pubSubManager, room);
		});
		t.start();
		this.mapRooms.put(room.getRoomID(), m2mAdpt);
		return room;
	}
	
	/**
	 * Invite user to existing chatroom
	 * @param userInviting user being invited to new chatroom
	 * @param room the info for the room to join
	 */
	public void inviteUser(INamedConnection userInviting, IRoom room) {
		try {
			Set<IRoom> rooms = Set.of(room);
			userInviting.getConnectionStub().receiveMessage(new ConnectionDataPacket<IInviteMsg>(new InviteMsg(rooms), user));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}

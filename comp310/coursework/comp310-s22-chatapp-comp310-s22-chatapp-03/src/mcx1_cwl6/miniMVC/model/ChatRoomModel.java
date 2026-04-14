package mcx1_cwl6.miniMVC.model;

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.adapter.ICmd2ModelAdapter;
import common.connector.INamedConnection;
import common.receiver.AMessageDataPacketAlgoCmd;
import common.receiver.IMessageReceiver;
import common.receiver.INamedMessageReceiver;
import common.receiver.MessageDataPacket;
import common.receiver.MessageDataPacketAlgo;
import common.receiver.messageType.exception.ICommunicationExceptionErrorMsg;
import common.receiver.messageType.exception.ICommunicationExceptionFailureMsg;
import common.receiver.messageType.exception.ICommunicationExceptionRejectionMsg;
import common.receiver.messageType.ICommunicationMsg;
import common.receiver.messageType.IRequestCmdMsg;
import common.receiver.messageType.ISendCmdMsg;
import common.receiver.messageType.ITextMsg;
import mcx1_cwl6.ChatRoom;
import mcx1_cwl6.miniMVC.model.messageType.CommunicationExceptionErrorMsg;
import mcx1_cwl6.miniMVC.model.messageType.HelloWorldMsg;
import mcx1_cwl6.miniMVC.model.messageType.RequestCmdMsg;
import mcx1_cwl6.miniMVC.model.messageType.SendCmdMsg;
import mcx1_cwl6.miniMVC.model.messageType.TextMsg;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncChannelUpdate;
import provided.pubsubsync.IPubSubSyncData;
import provided.pubsubsync.IPubSubSyncManager;
import provided.pubsubsync.IPubSubSyncUpdater;
import provided.rmiUtils.RMIPortConfigWithBoundName;

/**
 * Model of the mini MVC
 */
public class ChatRoomModel {

//	/**
//	 * The system logger in use
//	 */
//	private ILogger sysLogger;
	
	/**
	 * A logger that logs to the view and the system logger
	 */
	private ILogger viewLogger;
	
	/**
	 * The chat room
	 */
	private ChatRoom chatroom;
	
	/**
	 * Mini MVC to Main MVC adapter
	 */
	private IMini2MainAdapter m2MAdpt;

	/**
	 * The model 2 view adapter for the chat room
	 */
	private IMiniModel2ViewAdapter m2vAdpt;

	/**
	 * Stub of local user
	 */
	private IMessageReceiver localStub;


	/**
	 * Local remote
	 */
	private IMessageReceiver localRemote;
	
	/**
	 * Stub of user
	 */
	private INamedMessageReceiver userStub;
	
	
	/**
	 * Named connection
	 */
	private INamedConnection connection;
	
	/**
	 * Visitor to process cmds
	 */
	private MessageDataPacketAlgo visitor;
	
	/**
	 * Config info wrapper.
	 */
	private RMIPortConfigWithBoundName config;

	/**
	 * Channel for this PubSub room.
	 */
	private IPubSubSyncChannelUpdate<HashSet<INamedMessageReceiver>> channel;
	
	/**
	 * Function to handle PubSubSync data messages.
	 */
	private Consumer<IPubSubSyncData<HashSet<INamedMessageReceiver>>> consumer;
	
	/**
	 * Set of users in room
	 */
	HashSet<INamedMessageReceiver> userSet = new HashSet<INamedMessageReceiver>();
	
	/**
	 * Map of IDs to commands
	 */
	private Map<IDataPacketID, AMessageDataPacketAlgoCmd<ICommunicationMsg>> cmds = new ConcurrentHashMap<>();
	/**
	 * Map of IDs message data packets
	 */
	private Map<IDataPacketID, List<MessageDataPacket<?>>> msgCache = new ConcurrentHashMap<>();
	
	/**
	 * Command to model adapter.
	 */
	private ICmd2ModelAdapter c2mAdpt = new ICmd2ModelAdapter() {

		@Override
		public void displayText(String text) {
			m2vAdpt.displayText(text);
			
		}

		@Override
		public void displayComponent(String label, Supplier<JComponent> fac) {
			m2vAdpt.displayComponent("New Component", fac);
			
		}

		@Override
		public void displayDebugInfo(String info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getLocalMessageReceiverName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalRoomName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends ICommunicationMsg> void sendMsgToRoom(T data) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T extends ICommunicationMsg> void sendMsgToReceiver(T data, INamedMessageReceiver namedReceiver) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	/**
	 * Constructor for the chat room model
	 * @param chatroom The chatroom
	 * @param config The app config
	 * @param userConnector The connection for the user
	 * @param m2vAdpt The model to view adapter
	 * @param m2MAdpt The miniMVC to mainMVC adapter
	 */
	public ChatRoomModel(ChatRoom chatroom, RMIPortConfigWithBoundName config, INamedConnection userConnector, IMini2MainAdapter m2MAdpt, IMiniModel2ViewAdapter m2vAdpt) {
		this.m2vAdpt = m2vAdpt;
		this.m2MAdpt = m2MAdpt;
		this.chatroom = chatroom;
		this.config = config;
		this.connection = userConnector;
		
		this.visitor = new MessageDataPacketAlgo((AMessageDataPacketAlgoCmd<? extends ICommunicationMsg>) new AMessageDataPacketAlgoCmd<ICommunicationMsg>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = -319806321850888444L;

			@Override
			public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationMsg> host, Void... params) {
				List<MessageDataPacket<?>> msgs = msgCache.get(host.getData().getID());
				if (msgs == null) {
					msgs = new ArrayList<>();
					msgCache.put(host.getData().getID(), msgs);
				}
				msgs.add(host);
			
			
				for (MessageDataPacket<?> dp : msgs) {
					if (dp.getData().getID() == ITextMsg.GetID()) {
						try {
							dp.getSender().getMessageReceiverStub().receiveMessage(new MessageDataPacket<ICommunicationExceptionErrorMsg>(new CommunicationExceptionErrorMsg(dp, "This message had a null ID"), userStub));
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				
				try {
					host.getSender().getMessageReceiverStub().receiveMessage(new MessageDataPacket<IRequestCmdMsg> (new RequestCmdMsg(host.getData().getID()), userStub));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;
			}

			
		});
		
		this.viewLogger = ILoggerControl.makeLogger(new ILogEntryProcessor() {
			ILogEntryFormatter formatter = ILogEntryFormatter.MakeFormatter("[%1s] %2s");   // custom log entry formatting  "[level] msg"
			
			@Override
			public void accept(ILogEntry logEntry) {
				ChatRoomModel.this.m2vAdpt.displayText(formatter.apply(logEntry));
			}
			
		}, LogLevel.INFO);
		//viewLogger.append(sysLogger);
		
	
	}

	/**
	 * Start the model
	 */
	
	public void start() {
		try {
			localRemote = new IMessageReceiver() {

				@Override
				public void receiveMessage(MessageDataPacket<? extends ICommunicationMsg> packet)
						throws RemoteException {
					packet.execute(ChatRoomModel.this.visitor);
					
				}

			};

			try {
				this.localStub = (IMessageReceiver) UnicastRemoteObject.exportObject(localRemote, this.config.stubPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			userStub = new NamedMessageReceiver(ChatRoomModel.this.config.name, connection, localStub);
			
			//System.out.println("got user stub: " + userStub);
			
			this.visitor.setCmd(ITextMsg.GetID(), new AMessageDataPacketAlgoCmd<ICommunicationMsg>() {

				/**
				 * For serialization
				 */
				private static final long serialVersionUID = 6318337614429162924L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationMsg> host, Void... params) {
					// TODO Auto-generated method stub
					//System.out.println(((ITextMsg) host.getData()).getText());
					//ChatRoomModel.this.m2vAdpt.appendString(((ITextMsg) host.getData()).getText(), host.getSender().getName());
					
					String text = ((ITextMsg)host.getData()).getText();
					String user = host.getSender().getName();
					String finalMessage = "\n" + user + ": " + text;
					ChatRoomModel.this.m2vAdpt.displayText(finalMessage);
					return null;
				}
				
			});
			
			this.visitor.setCmd(HelloWorldMsg.GetID(), new AMessageDataPacketAlgoCmd<ICommunicationMsg>() {

				/**
				 * For serialization
				 */
				private static final long serialVersionUID = 6318337614429162924L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationMsg> host, Void... params) {
					// TODO Auto-generated method stub
					
					ChatRoomModel.this.m2vAdpt.displayComponent("Hello World", new Supplier<JComponent>() {

						@Override
						public JComponent get() {
							JPanel panel = new JPanel();
							panel.setBackground(Color.BLUE);
			                JLabel helloText = new JLabel("Hello World!!!!!!!!");
			                helloText.setForeground(Color.WHITE);
			                panel.add(helloText);
			                return panel;
						}
						
					});
					return null;
				}
				
			});
			
			
			visitor.setCmd(IRequestCmdMsg.GetID(), new AMessageDataPacketAlgoCmd<IRequestCmdMsg>() {

				/**
				 * For serialization
				 */
				private static final long serialVersionUID = -1749197413034324663L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<IRequestCmdMsg> host, Void... params) {
					IRequestCmdMsg msg = host.getData();
					IDataPacketID cmdID = msg.getID();
					if (cmdID == null || msg == null) {
						try {
							host.getSender().getMessageReceiverStub().receiveMessage(new MessageDataPacket<ICommunicationExceptionErrorMsg>(new CommunicationExceptionErrorMsg(host, "error"), userStub));
						} catch(RemoteException e) {
							e.printStackTrace();
						}
					}
					else {
						try {
							System.out.println("Command we tryna send: "+ cmds.get(cmdID) + "and " + cmdID);
							host.getSender().getMessageReceiverStub().receiveMessage(new MessageDataPacket<ISendCmdMsg>(new SendCmdMsg(cmds.get(cmdID), cmdID), userStub));
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
					return null;
				}


			});
			
			visitor.setCmd(ISendCmdMsg.GetID(), new AMessageDataPacketAlgoCmd<ISendCmdMsg>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -82373559964238691L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ISendCmdMsg> host, Void... params) {
					ISendCmdMsg msg = host.getData();
					msg.getCmd().setCmd2ModelAdpt(c2mAdpt);
					visitor.setCmd(msg.getCmdID(), msg.getCmd());
					
					if (msg.getCmdID() == null || msg == null || msg.getCmd() == null) {
						try {
							host.getSender().getMessageReceiverStub().receiveMessage(new MessageDataPacket<ICommunicationExceptionErrorMsg>(new CommunicationExceptionErrorMsg(host, "This packet had a null message."), userStub));
						} catch (RemoteException e) {
							//model2ViewMiniAdapter.logText("Error when sending error message.");
							e.printStackTrace();
						}
					}

					List<MessageDataPacket<?>> packets = msgCache.get(msg.getCmdID());
					for (MessageDataPacket<?> packet : Set.copyOf(packets)) {
						packet.execute(visitor);
						packets.remove(packet);
					}
					return null;
				}

				
			});
			
			visitor.setCmd(ICommunicationExceptionErrorMsg.GetID(), new AMessageDataPacketAlgoCmd<ICommunicationExceptionErrorMsg>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -8043866656893919341L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationExceptionErrorMsg> host, Void... params) {
					ICommunicationExceptionErrorMsg msg = host.getData();
					viewLogger.log(LogLevel.ERROR, "This data packet failed to process: " + msg.getErrorDataPacket());
					viewLogger.log(LogLevel.ERROR, msg.getErrorMsg());
					return null;
				}

				
			});
			
			visitor.setCmd(ICommunicationExceptionFailureMsg.GetID(), new AMessageDataPacketAlgoCmd<ICommunicationExceptionFailureMsg>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -8043866656893919341L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationExceptionFailureMsg> host, Void... params) {
					ICommunicationExceptionFailureMsg msg = host.getData();
					viewLogger.log(LogLevel.ERROR, "This data packet failed to process: " + msg.getErrorDataPacket());
					viewLogger.log(LogLevel.ERROR, msg.getErrorMsg());
					return null;
				}

				
			});
			
			visitor.setCmd(ICommunicationExceptionRejectionMsg.GetID(), new AMessageDataPacketAlgoCmd<ICommunicationExceptionRejectionMsg>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -8043866656893919341L;

				@Override
				public Void apply(IDataPacketID index, MessageDataPacket<ICommunicationExceptionRejectionMsg> host, Void... params) {
					ICommunicationExceptionRejectionMsg msg = host.getData();
					viewLogger.log(LogLevel.ERROR, "This data packet failed to process: " + msg.getErrorDataPacket());
					viewLogger.log(LogLevel.ERROR, msg.getErrorMsg());
					return null;
				}

				
			});
			
			
			//cmds.put(HelloWorldMsg.GetID(), (AMessageDataPacketAlgoCmd) new HelloWorldDataPacketAlgoCmd());

			viewLogger.log(LogLevel.INFO, "ChatAppModel successfully started!");

		} catch (Exception e) {
			// Send error message
			viewLogger.log(LogLevel.ERROR, "ChatAppModel Error Starting: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Create new PubSub channel.
	 * @param manager PubSub manager
	 * @param room ChatRoom representation of this room
	 */
	public void createChannel(IPubSubSyncManager manager, ChatRoom room) {
		//System.out.println("Members: " + chatroom.getMembers());
		userSet.add(userStub);
		
		this.consumer = new Consumer<IPubSubSyncData<HashSet<INamedMessageReceiver>>>() {

			@Override
			public void accept(IPubSubSyncData<HashSet<INamedMessageReceiver>> t) {
				// TODO Auto-generated method stub
				userSet = t.getData();
				ChatRoomModel.this.chatroom.setMembers(userSet);
				m2vAdpt.updateUserSet(userSet);
			}
	
		};
		
		this.channel = manager.createChannel(room.getRoomName(), userSet, 
				this.consumer, (statusMsg)->{});

		 room.setUUID(this.channel.getChannelID());
	}

	/**
	 * Join existing PubSub channel.
	 * @param manager PubSub manager.
	 */
	public void joinChannel(IPubSubSyncManager manager) {
		this.channel = manager.subscribeToUpdateChannel(this.chatroom.getRoomID(),
		 (pubSubSyncData)->{ this.chatroom.getMembers().clear(); 
		 this.chatroom.getMembers().addAll(pubSubSyncData.getData()); },
		 (statusMsg)->{});
		 addThisToRoster();
		 this.chatroom.getMembers().add(this.userStub);
		 m2vAdpt.updateUserSet(chatroom.getMembers());
	}
	
	/**
	 * Add this user to the PubSub roster.
	 */
	private void addThisToRoster() {
		this.channel.update(IPubSubSyncUpdater.makeSetAddFn(this.userStub));
	}

	/**
	 * Send message in chat room
	 * @param msg The message to send
	 */
	public void sendText(String msg) {
		System.out.println("Users: " + chatroom.getMembers());
		for (INamedMessageReceiver receiver : chatroom.getMembers()) {
			try {
				receiver.getMessageReceiverStub().receiveMessage(new MessageDataPacket<ITextMsg>(new TextMsg(msg), this.userStub));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Leave this room.
	 */
	public void leaveRoom() {
		//ChatRoomModel.this.sendMsg("User " + this.userStub.toString() + " has left the chat");
		//System.out.println("Members before: " + chatroom.getMembers());
	
		this.channel.update(IPubSubSyncUpdater.makeSetRemoveFn(userStub));
		this.channel.unsubscribe();
		//System.out.println("Members after: " + chatroom.getMembers());
		
		m2MAdpt.removeRoomList(chatroom);
		m2MAdpt.removeRoomView(chatroom);
	}
	
	
	/**
	 * Remove room from view list.
	 */
	public void removeRoomFromList() {
		m2MAdpt.removeRoomList(chatroom);
	}
	

	
	/**
	 * Send unknown message.
	 */
	public void sendUnknown() {
		for (INamedMessageReceiver receiver : chatroom.getMembers()) {
			try {
				receiver.getMessageReceiverStub().receiveMessage(new MessageDataPacket<HelloWorldMsg>(new HelloWorldMsg(), this.userStub));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the room you are in.
	 * @return The chat room
	 */
	public ChatRoom getRoom() {return chatroom;}
	
	
}

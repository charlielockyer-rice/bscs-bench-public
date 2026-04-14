package mcx1_cwl6.mainMVC.view;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import mcx1_cwl6.miniMVC.view.ChatRoomView;
import provided.rmiUtils.RMIPortConfigWithBoundName;

import java.awt.GridLayout;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;

/**
 * The main view for the app.
 * Adapter from the model to the view
 * @param <TUserDropListItem> other chatapp connections
 * @param <TRoomDropListItem> other room connections
 */
public class ChatAppView<TUserDropListItem, TRoomDropListItem> extends JFrame{
	
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 8349074315951325998L;

	/**
	 * Adapter to the main model
	 */
	private IMainView2ModelAdapter<TUserDropListItem, TRoomDropListItem> v2mAdpt;
	
	/**
	 * The control panel
	 */
	private final JPanel upperPanel = new JPanel();
	
	/**
	 * The startup control panel
	 */
	private final JPanel startupPanel = new JPanel();
	
	/**
	 * Panel with startup buttons
	 */
	private final JPanel startButtonPanel = new JPanel();
	
	/**
	 * Panel to make a chatroom
	 */
	private final JPanel makeChatroomPanel = new JPanel();
	
	/**
	 * Panel of remote hosts
	 */
	private final JPanel remoteHostPanel = new JPanel();
	
	/**
	 * Panel to connect to others
	 */
	private final JPanel connectPanel = new JPanel();
	
	/**
	 * The connect to other controller panel
	 */
	private final JPanel connectToPanel = new JPanel();
	
	/**
	 * The connect to host controller panel
	 */
	private final JPanel connectHostPanel = new JPanel();
	
	/**
	 * The invite user to chatroom panel
	 */
	private final JPanel inviteRequestPanel = new JPanel();
	
	/**
	 * The display panel
	 */
	private final JPanel displayPanel = new JPanel();
	
	/**
	 * Status panel
	 */
	private final JPanel statusPanel = new JPanel();
	
	/**
	 * Text field for username selection
	 */
	private final JTextField appstartupTextField = new JTextField();
	
	/**
	 * Name of chatroom to create 
	 */
	private final JTextField chatroomnameTextField = new JTextField();
	
	/**
	 * IP address of host to connect
	 */
	private final JTextField ipTextField = new JTextField();
	
	/**
	 * Button to quit the application
	 */
	private final JButton quitBtn = new JButton("Quit");
	
	/**
	 * Button to start the application
	 */
	private final JButton startBtn = new JButton("Start");
	
	/**
	 * Button to make a chatroom
	 */
	private final JButton makeChatroomBtn = new JButton("Make");

	/**
	 * Button to connect to a host
	 */
	private final JButton connectBtnNewButton = new JButton("Connect!");
	
	/**
	 * Button to invite user to chatroom
	 */
	private final JButton inviteBtnNewButton = new JButton("Invite");
	
	/**
	 * Drop list of hosts you are connected to
	 */
	private final JComboBox<TUserDropListItem> connectedHostsComboBox = new JComboBox<>();
	
	/**
	 * Drop list of chatroom connected to
	 */
	private final JComboBox<TRoomDropListItem> chatRoomsComboBox = new JComboBox<>();
	
	/**
	 * Tabbed panel for the chatroom mini MVCs
	 */
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * Status scroll panel
	 */
	private final JScrollPane statusScrollPane = new JScrollPane();

	/**
	 * Text area for system status
	 */
	private final JTextArea statusTextArea = new JTextArea();
	
	/**
	 * Set of hosts user is connected to
	 */
	private HashSet<TUserDropListItem> users = new HashSet<>();
	
	/**
	 * Name of user
	 */
	private RMIPortConfigWithBoundName config;
	
	/**
	 * Create the frame
	 * @param config  The app config
	 * @param v2mAdpt The adapter to the model
	 */
	public ChatAppView(RMIPortConfigWithBoundName config, IMainView2ModelAdapter<TUserDropListItem, TRoomDropListItem> v2mAdpt) {
		this.v2mAdpt = v2mAdpt;
		this.config = config;
		initGUI();
	}
	
	/**
	 * Sets the GUI to be visible.
	 */
	public void start() {
		setVisible(true);
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("Server GUI");
		setBounds(100, 100, 677, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ipTextField.setToolTipText("ip of host to connect to");
		ipTextField.setColumns(10);
		chatroomnameTextField.setToolTipText("name of chatroom to make");
		chatroomnameTextField.setColumns(10);
		appstartupTextField.setText(config.name);
		appstartupTextField.setToolTipText("username to use");
		appstartupTextField.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Username:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		appstartupTextField.setColumns(10);
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		startupPanel.setBorder(new TitledBorder(null, "App Startup", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		upperPanel.add(startupPanel);
		startupPanel.setLayout(new GridLayout(3, 1, 0, 0));
		
		startupPanel.add(appstartupTextField);
		
		startupPanel.add(startButtonPanel);
		startBtn.setToolTipText("start chatapp");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TRoomDropListItem defaultRoom = v2mAdpt.createRoom("Default Chatroom");
				chatRoomsComboBox.addItem(defaultRoom);
			}
		}); 
		
		startButtonPanel.add(startBtn);
		quitBtn.setToolTipText("Quit chatapp");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.quit();
			}
		});
		
		startButtonPanel.add(quitBtn);
		makeChatroomPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Create Chat Room", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		upperPanel.add(makeChatroomPanel);
		makeChatroomPanel.setLayout(new GridLayout(2, 1, 0, 0));
		
		makeChatroomPanel.add(chatroomnameTextField);
		makeChatroomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TRoomDropListItem newRoom = v2mAdpt.createRoom(chatroomnameTextField.getText());
				chatRoomsComboBox.addItem(newRoom);
			}
		});
		makeChatroomBtn.setToolTipText("Make a chatroom");
		
		makeChatroomPanel.add(makeChatroomBtn);
		upperPanel.add(chatRoomsComboBox);
		chatRoomsComboBox.setToolTipText("List of chat rooms");
		chatRoomsComboBox.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Chat Rooms For Invite", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		remoteHostPanel.setBorder(new TitledBorder(null, "Remote Hosts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		upperPanel.add(remoteHostPanel);
			
		remoteHostPanel.add(connectPanel);
		connectPanel.setLayout(new GridLayout(2, 1, 0, 0));
		connectToPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Connect To...", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		connectPanel.add(connectToPanel);
		
		connectToPanel.add(ipTextField);
		connectBtnNewButton.setToolTipText("connect to host");
		connectBtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		connectToPanel.add(connectBtnNewButton);
		connectHostPanel.setBorder(new TitledBorder(null, "Connected Hosts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		connectPanel.add(connectHostPanel);
		connectHostPanel.setLayout(new GridLayout(0, 1, 0, 0));
		connectedHostsComboBox.setToolTipText("list of connected hosts");
		
		connectHostPanel.add(connectedHostsComboBox);
		
		connectHostPanel.add(inviteRequestPanel);
		inviteBtnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.inviteUser(connectedHostsComboBox.getItemAt(connectedHostsComboBox.getSelectedIndex()), chatRoomsComboBox.getItemAt(chatRoomsComboBox.getSelectedIndex()));
			}
		});
		inviteBtnNewButton.setToolTipText("Invite user to a chatroom");
		
		inviteRequestPanel.add(inviteBtnNewButton);
		
		getContentPane().add(displayPanel, BorderLayout.CENTER);
		displayPanel.setLayout(new BorderLayout(0, 0));
		tabbedPane.setToolTipText("chatrooms");
		
		displayPanel.add(tabbedPane);
		
		displayPanel.add(statusScrollPane, BorderLayout.SOUTH);
		
		statusScrollPane.setViewportView(statusPanel);
		statusTextArea.setToolTipText("Info about chatroom/connections/etc.");
		
		statusPanel.add(statusTextArea);
	}
	
	/**
	 * Display status on screen.
	 * @param text status text to display
	 */
	public void displayStatus(String text) {
		statusTextArea.append(text);
	}
	
	/**
	 * Add Chatroom tab to view
	 * @param roomName name of new room
	 * @param miniView to insert onto main view
	 */
	public void addChatRoom(String roomName, ChatRoomView miniView) {
		System.out.println(miniView);
		tabbedPane.addTab(roomName, miniView);
		repaint();
	}
	
	/**
	 * Remove chatroom from view.
	 * @param room to be removed
	 */
	public void removeRoom(TRoomDropListItem room) {
		tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
	}
	
	/**
	 * Remove all chatrooms currently connected.
	 */
	public void removeAllRooms() {
		tabbedPane.removeAll();
	}
	
	
	/**
	 * Add room to list of rooms.
	 * @param room to be added
	 */
	public void addRoomToList(TRoomDropListItem room) {
		chatRoomsComboBox.addItem(room);
	}
	
	/**
	 * Remove room from list of rooms.
	 * @param room to be removed
	 */
	public void removeRoomFromList(TRoomDropListItem room) {
		chatRoomsComboBox.removeItem(room);

	}

	
	/**
	 * Add peer to list of peers connected.
	 * @param userStub of peer 
	 */
	public void addPeer(TUserDropListItem userStub) {
		if (!users.contains(userStub)) {
			connectedHostsComboBox.addItem(userStub);
			users.add(userStub);
		}
	} 

	/**
	 * Remove peer (disconnection)
	 * @param userStub to be removed
	 */
	public void removeUser(TUserDropListItem userStub) {
		connectedHostsComboBox.removeItem(userStub);
	}

	/**
	 * Add the given component to the control panel,  then revalidating and packing the frame.
	 * @param comp The component to add
	 */
	public void addCtrlComponent(JComponent comp) {
		upperPanel.add(comp);  // Add the component to the control panel
		validate();  // re-runs the frame's layout manager to account for the newly added component 
		pack(); // resizes the frame and panels to make sure the newly added component is visible.  Note that this may adversely affect empty text displays without a preferred size setting.
	}

	/**
	 * Append the given message with a linefeed to the text area
	 * @param msg The message to display
	 */
	public void append(String msg) {
		//lowerTextArea.append(msg+"\n");
	}

}

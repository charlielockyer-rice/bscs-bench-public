package mcx1_cwl6.miniMVC.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import common.receiver.INamedMessageReceiver;


/**
 * View for the chat room
 */
public class ChatRoomView extends JPanel {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 7822436832079216116L;
	
	/**
	 * Adapter for View2Model of MiniMVC
	 */
	private IMiniView2ModelAdapter v2mAdpt = IMiniView2ModelAdapter.NULL_OBJECT();
	
	// === PANES and PANELS ===
	
	/**
	 * TabbedPane for anything within a Chatroom
	 */
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	
	/**
	 * Panel to Hold Users
	 */
	private final JPanel userPanel = new JPanel();
	
	/**
	 * Split Pane
	 */
	private final JSplitPane splitPane = new JSplitPane();
	
	/**
	 * Panel where all Buttons are held
	 */
	private final JPanel buttonPanel = new JPanel();
	
	/**
	 * ScrollPane for Chatroom
	 */
	private final JScrollPane chatroomScrollPane = new JScrollPane();
	
	/**
	 * Scroll Pane for TextInput
	 */
	private final JScrollPane textInputScrollPane = new JScrollPane();
	
	/**
	 * Pane for Users
	 */
	private final JScrollPane userScrollPane = new JScrollPane();
	
	// === TEXT FIELDS === //
	
	/**
	 * Where we write the text
	 */
	private final JTextField messageField = new JTextField();
	
	/**
	 * TextArea for Messages
	 */
	private final JTextArea textArea = new JTextArea();
	
	/**
	 * TextArea where Users are listed
	 */
	private final JTextArea textAreaUsers = new JTextArea();

	// === BUTTONS === //
	/**
	 * Button for Sending Text
	 */
	private final JButton sendTxtBtn = new JButton("Send Text");
	
	/**
	 * Button for Sending BallWorld
	 */
	private final JButton sendUnknownMsg = new JButton("Send Unknown");
	
	/**
	 * Button to leave a chat room
	 */
	private final JButton leaveRoomButton = new JButton("Leave Room");
	
	// === LABELS === //
	
	/**
	 * Label Basic
	 */
	private final JLabel usersLabel = new JLabel("Users in Chatroom:");
	
	/**
	 * Constructor for MiniView
	 * @param iMiniView2ModelAdapter Adapter for Mini V2M
	 */
	public ChatRoomView(IMiniView2ModelAdapter iMiniView2ModelAdapter) {
		v2mAdpt = iMiniView2ModelAdapter;
		initGUI();
	}
	
	/**
	 * Initialiaze GUI
	 */
	private void initGUI() {
		setAlignmentX(0.1f);
		
		setBounds(100, 100, 500, 300);
		this.setMaximumSize(new Dimension(this.getWidth(), 500));

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		tabbedPane.setForeground(Color.BLACK);
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Messaging Log", chatroomScrollPane);
		textArea.setEditable(false);
		textAreaUsers.setEditable(false);
		chatroomScrollPane.setViewportView(textArea);
		userPanel.setToolTipText("where Users are listed");
		add(userPanel, BorderLayout.WEST);
		userPanel.setLayout(new BorderLayout(0, 0));
		
		userPanel.add(usersLabel, BorderLayout.NORTH);
		userScrollPane.setToolTipText("Users in this chat room");
		
		userPanel.add(userScrollPane, BorderLayout.CENTER);
		
		userScrollPane.setViewportView(textAreaUsers);
		
		splitPane.setToolTipText("input text, select files and emojis");
		
		add(splitPane, BorderLayout.SOUTH);
		textInputScrollPane.setToolTipText("");
		
		splitPane.setLeftComponent(textInputScrollPane);
		messageField.setToolTipText("Write a message here");
		
		textInputScrollPane.setViewportView(messageField);
		
		splitPane.setRightComponent(buttonPanel);
		splitPane.setResizeWeight(1);
		
		messageField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendText(messageField.getText());
				messageField.setText(null);
			}
		});
		
		sendTxtBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendText(messageField.getText());
				messageField.setText(null);
			}
		});
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		sendTxtBtn.setToolTipText("Send Message to Users in Chatroom");
		
		buttonPanel.add(sendTxtBtn);
		sendUnknownMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendUnknown();
			}
		});
		sendUnknownMsg.setToolTipText("Send Unknown Command that is a message");
		
		buttonPanel.add(sendUnknownMsg);
		
		leaveRoomButton.setToolTipText("Leave chat room");
		leaveRoomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.leaveRoom();
				v2mAdpt.removeRoomFromList();
			}
		});
		
		buttonPanel.add(leaveRoomButton);
	}
	
	/**
	 * start the GUI.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Create new TabbedPane and add comp to it.
	 * @param label The label of the component
	 * @param compFac Component to add
	 */
	public void displayComponent(String label, Supplier<JComponent> compFac) {
		tabbedPane.addTab(label, compFac.get());
		return;
	}

	/**
	 * Add Message to 
	 * @param finalMessage Message to add to textArea
	 */
	public void displayText(String finalMessage) {
		textArea.append(finalMessage);
	}
	
	/**
	 * Update list of users in Chatroom
	 * @param userSet users in a chatroom
	 */
	public void updateUserSet(HashSet<INamedMessageReceiver> userSet) {
		textAreaUsers.setText(null);
		for (INamedMessageReceiver user : userSet){
			textAreaUsers.append(user.getName() + "\n");
		}
	}

}

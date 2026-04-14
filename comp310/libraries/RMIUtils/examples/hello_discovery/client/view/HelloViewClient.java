package provided.rmiUtils.examples.hello_discovery.client.view;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;



/**
 * The main view for the app
 * @author swong
 *
 */
public class HelloViewClient extends JFrame {
	

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 3765107108282474039L;

	/**
	 * The default remote host reference.
	 */
	private static final String DEFAULT_REMOTE_HOST = "localhost"; 
	
	/**
	 * Main panel with app controls
	 */
	private final JPanel pnlControl = new JPanel();
	
	/**
	 * The remote server's IP address info input text field.
	 */
	private JTextField tfRemoteHost; 
	/**
	 * Panel for remote host config
	 */
	private JPanel remoteHostPnl;	
	
	
	/**
	 * The connect button
	 */
	private JButton btnConnect;
	
	/**
	 * The scrollbars for the text area
	 */
	private final JScrollPane spDisplay = new JScrollPane();
	
	/**
	 * Message display text area
	 */
	private final JTextArea taDisplay = new JTextArea();

	/**
	 * Adapter to the model
	 */
	private IView2ModelAdapterClient v2mAdpt;
	
	/**
	 * Button to manually call the remote method
	 */
	private final JButton btnSayHello = new JButton("Say Hello");
	
	/**
	 * Button to quit the application
	 */
	private final JButton btnQuit = new JButton("Quit");

	/**
	 * Create the frame.
	 * @param v2mAdpt The adapter to the model
	 */
	public HelloViewClient(IView2ModelAdapterClient v2mAdpt) {
		this.v2mAdpt = v2mAdpt;

		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("RMI Hello World Demo -- Client");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.quit();
			}
		});
		
		pnlControl.add(btnQuit);

		remoteHostPnl = new JPanel();
		remoteHostPnl.setBorder(new TitledBorder(null, "Remote Host:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlControl.add(remoteHostPnl);
		remoteHostPnl.setLayout(new GridLayout(2, 1, 0, 0));
		tfRemoteHost = new JTextField(DEFAULT_REMOTE_HOST);
		remoteHostPnl.add(tfRemoteHost);
		tfRemoteHost.setToolTipText("The IP address of the remote Compute Engine");
		tfRemoteHost.setPreferredSize(new Dimension(100,25));
		{
			btnConnect = new JButton();
			remoteHostPnl.add(btnConnect);
			btnConnect.setToolTipText("Click to connect to the remote Compute Engine");
			btnConnect.setText("Connect");
			btnConnect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					connect();
				}
			});
		}
		tfRemoteHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		
		getContentPane().add(pnlControl, BorderLayout.NORTH);
		btnSayHello.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sayHello();
			}
		});
		
		pnlControl.add(btnSayHello);
		
		getContentPane().add(spDisplay, BorderLayout.CENTER);
		taDisplay.setRows(20);
		
		spDisplay.setViewportView(taDisplay);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		setVisible(true);
	}
	
	/**
	 * Add the given component to the control panel,  then revalidating and packing the frame.
	 * @param comp The component to add
	 */
	public void addCtrlComponent(JComponent comp) {
		pnlControl.add(comp);  // Add the component to the control panel
		validate();  // re-runs the frame's layout manager to account for the newly added component 
		pack(); // resizes the frame and panels to make sure the newly added component is visible.  Note that this may adversely affect empty text displays without a preferred size setting.
	}
	
	/**
	 * Have the model connect to the remote server.
	 */
	private void connect() {
		String remoteIP = tfRemoteHost.getText();
		append("Attempting to connect to "+remoteIP+"...\n");
		append("[Connection status] "+ v2mAdpt.connectTo(remoteIP)+"\n");
	}
	
	/**
	 * Append the given message with a linefeed to the text area
	 * @param msg The message to display
	 */
	public void append(String msg) {
		taDisplay.append(msg+"\n");
	}


}

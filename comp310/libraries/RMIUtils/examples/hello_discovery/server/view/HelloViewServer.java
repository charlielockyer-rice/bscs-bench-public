package provided.rmiUtils.examples.hello_discovery.server.view;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/**
 * The main view for the app
 * @author swong
 *
 */
public class HelloViewServer extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -2624779318776585900L;

	
	/**
	 * Main panel with app controls
	 */
	private final JPanel pnlControl = new JPanel();
	
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
	private IView2ModelAdapterServer v2mAdpt;
	
	/**
	 * Button to manually call the remote method
	 */
	private final JButton btnQuit = new JButton("Quit");

	/**
	 * Create the frame.
	 * @param v2mAdpt The adapter to the model
	 */
	public HelloViewServer(IView2ModelAdapterServer v2mAdpt) {
		this.v2mAdpt = v2mAdpt;

		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("RMI Hello World Demo -- Server");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(pnlControl, BorderLayout.NORTH);
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.quit();
			}
		});
		
		pnlControl.add(btnQuit);
		
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
	 * Append the given message with a linefeed to the text area
	 * @param msg The message to display
	 */
	public void append(String msg) {
		taDisplay.append(msg+"\n");
	}


}

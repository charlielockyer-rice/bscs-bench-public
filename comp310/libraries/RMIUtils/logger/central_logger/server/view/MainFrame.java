package provided.rmiUtils.logger.central_logger.server.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import provided.logger.LogLevel;
import provided.logger.util.IStringLogEntryProcessor;
import provided.logger.util.LoggerPanel;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

/**
 * The main server view
 * @author swong
 *
 */
public class MainFrame extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -7948030709531286139L;
	/**
	 * main content pane
	 */
	private JPanel contentPane;
	/**
	 * top control panel
	 */
	private final JPanel pnlControl = new JPanel();
	/**
	 * the panel to display logs
	 */
	private final LoggerPanel pnlLog = new LoggerPanel();
	/**
	 * Button for local testing
	 */
	private final JButton btnTest = new JButton("Test");
	/**
	 * The view to model adapter
	 */
	private IView2ModelAdapter v2mAdpt;
	/**
	 * drop list of log level choices
	 */
	private final JComboBox<LogLevel> cbxLogLevel = new JComboBox<LogLevel>();

	/**
	 * Create the frame.
	 * @param v2mAdpt The adapter to the view
	 */
	public MainFrame(IView2ModelAdapter v2mAdpt) {
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("Central Logger App");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
				setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(pnlControl, BorderLayout.NORTH);
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.test();
			}
		});

		
		cbxLogLevel.setBorder(new TitledBorder(null, "LogLevel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		cbxLogLevel.setModel(new DefaultComboBoxModel<LogLevel>(LogLevel.values()));
		cbxLogLevel.setSelectedItem(LogLevel.INFO);
		
		pnlControl.add(cbxLogLevel);
		btnTest.setToolTipText("Test the local remote logging service");
		
		pnlControl.add(btnTest);
		
		contentPane.add(pnlLog, BorderLayout.CENTER);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		cbxLogLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.setLogLevel(cbxLogLevel.getItemAt(cbxLogLevel.getSelectedIndex()));
			}
		});
		this.setVisible(true);
	}

	
	/**
	 * Get the log entry processor for the logger panel.
	 * @return A log entry processor
	 */
	public IStringLogEntryProcessor getLogEntryProcessor() {
		return pnlLog;
	}
	
	/**
	 * Displays the given IP address and port on the frame's title
	 * @param ipAddr The IP address to display
	 * @param stubPort The stub port number to display.
	 * @param classServerPort  The class server port number to display.
	 */
	public void setIP_Port(String ipAddr, int stubPort, int classServerPort) {
		this.setTitle(this.getTitle()+" ("+ipAddr+" : "+stubPort+", "+classServerPort+")");
	}
}

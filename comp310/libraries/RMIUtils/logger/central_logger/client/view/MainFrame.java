package provided.rmiUtils.logger.central_logger.client.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import provided.logger.LogLevel;
import provided.logger.util.IStringLogEntryProcessor;
import provided.logger.util.LoggerPanel;

import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

/**
 * The main view of the application
 * @author swong
 *
 */
public class MainFrame extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 8584790733581917708L;
	/**
	 * Main content pane
	 */
	private JPanel contentPane;
	/**
	 * Top control panel
	 */
	private final JPanel pnlControl = new JPanel();
	/**
	 * Panel to display log entries
	 */
	private final LoggerPanel pnlLog = new LoggerPanel();
	/**
	 * View to model adapter
	 */
	private IView2ModelAdapter v2mAdpt;
	/**
	 * Panel for remote connections
	 */
	private final JPanel pnlConnect = new JPanel();
	/**
	 * Remote address textfield
	 */
	private final JTextField tfAddr = new JTextField();
	/**
	 * Remote connection button
	 */
	private final JButton btnConnect = new JButton("Connect");
	/**
	 * Input for test log message
	 */
	private final JTextField tfMsg = new JTextField();
	/**
	 * Panel for sending test logs
	 */
	private final JPanel pnlSendMsg = new JPanel();
	/**
	 * Droplist of log level choices
	 */
	private final JComboBox<LogLevel> cbxLogLevel = new JComboBox<LogLevel>();

	/**
	 * Create the frame.
	 * @param v2mAdpt The view to model adapter
	 * 
	 */
	public MainFrame(IView2ModelAdapter v2mAdpt) {

		tfMsg.setColumns(10);
		tfAddr.setColumns(10);
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("Remote Logging Service Test Client App");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 708, 304);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		cbxLogLevel.setBorder(new TitledBorder(null, "LogLevel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		cbxLogLevel.setModel(new DefaultComboBoxModel<LogLevel>(LogLevel.values()));
		
		cbxLogLevel.setSelectedItem(LogLevel.INFO);
		pnlSendMsg.add(cbxLogLevel);
		pnlSendMsg.add(tfMsg);
		tfMsg.setToolTipText("Type Enter to send.");
		tfMsg.setBorder(new TitledBorder(null, "Test log message:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tfMsg.setEnabled(false);
		tfMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.logMsg(cbxLogLevel.getItemAt(cbxLogLevel.getSelectedIndex()), tfMsg.getText());
			}
		});
		contentPane.add(pnlControl, BorderLayout.NORTH);
		pnlConnect.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Connect To Address:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		pnlControl.add(pnlConnect);
		
		pnlConnect.add(tfAddr);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.connectTo(tfAddr.getText());
			}
		});
		
		pnlConnect.add(btnConnect);
		
		pnlControl.add(pnlSendMsg);
		
		contentPane.add(pnlLog, BorderLayout.CENTER);
	}
	
	/**
	 * Start the view
	 */
	public void start() {
		this.setVisible(true);
	}

	/**
	 * The view's log entry processor to display log entries on the view.
	 * @return A log entry processor
	 */
	public IStringLogEntryProcessor getLogEntryProcessor() {
		return pnlLog;
	}
	
	/**
	 * Set teh displayed local IP address
	 * @param addr The local IP address
	 */
	public void setAddr(String addr) {
		tfAddr.setText(addr);
		this.setTitle(this.getTitle()+" ("+addr+")");
	}
	
	/**
	 * Enable the input for log messages.
	 */
	public void enableSendMsg() {
		tfMsg.setEnabled(true);
	}
}

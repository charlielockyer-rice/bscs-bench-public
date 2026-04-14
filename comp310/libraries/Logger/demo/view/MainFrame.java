package provided.logger.demo.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import provided.logger.ILogEntryFormatter;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Example view class that has the ability to add a LoggerPanel.
 * @author swong
 *
 */
public class MainFrame extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 4959622601021830941L;

	/**
	 * The log entry formatter used by the local view logger.
	 * Notice how indexed positional arguments ("%X$") are used to control where the various log entry values are placed in the output string.d
	 */
	private ILogEntryFormatter leFormatter = ILogEntryFormatter
			.MakeFormatter("[%1$s @ %3$s] %2$s  (from view-only logger)"); // "[level @ loc] msg"

	/**
	 * The local view logger that uses a custom (lambda expression implemented) ILogEntryProcessor which shows a formatted log entry 
	 * on a pop-up dialog window.
	 */
	private ILogger viewOnlyLogger = ILoggerControl.makeLogger((logEntry) -> {
		JOptionPane.showMessageDialog(this, leFormatter.apply(logEntry));
	}, LogLevel.DEBUG);

	/**
	 * The adapter to the model
	 */
	private IView2ModelAdapter v2mAdpt;

	/**
	 * The frame's content pane
	 */
	private JPanel contentPane;

	/**
	 * The panel holding the control components at the top of the frame
	 */
	private final JPanel pnlControl = new JPanel();

	/**
	 * The button to make an error log entry in the model
	 */
	private final JButton btnMakeError = new JButton("Make Error");

	/**
	 * The button to make a debug log entry in the model
	 */
	private final JButton btnMakeDebug = new JButton("Make Debug");

	/**
	 * The drop list that sets the system logger's minimum log level
	 */
	private final JComboBox<LogLevel> cbxSetLogLevel = new JComboBox<LogLevel>();

	/**
	 * The panel that holds the controls for the system logger.
	 */
	private final JPanel pnlLogControl = new JPanel();
	/** 
	 * The panel to hold the controls to ask the model to make a log entry
	 */
	private final JPanel pnlCreateLog = new JPanel();

	/**
	 * The text field to enter a log message for the model
	 */
	private final JTextField tfLogMsg = new JTextField();

	/**
	 * The button to make a critical log entry in the model
	 */
	private final JButton btnMakeCritical = new JButton("Make Critical");

	/**
	 * The button to make an info log entry in the model
	 */
	private final JButton btnMakeInfo = new JButton("Make Info");

	/**
	 * The label used to display messages on the view
	 */
	private final JLabel lblMsgDisplay = new JLabel("");

	/**
	 * The button to make a log entry using the local view logger
	 */
	private final JButton btnMakeViewLogMsg = new JButton("Make View-only Log Msg");

	/**
	 * The panel that holds the controls for the local view logger
	 */
	private final JPanel pnlViewOnly = new JPanel();

	/**
	 * The text field to enter a message for a local view log entry.
	 */
	private final JTextField tfViewOnlyMsg = new JTextField();

	/**
	 * Create the frame.   The system-wide logger is deliberately not being passed in here as it is for the model 
	 * to illustrate how the view can directly access the global, singleton logger (which is the same logger as 
	 * the system logger here) through ILoggerControl.
	 * @param v2mAdpt The adapter to the model 
	 */
	public MainFrame(IView2ModelAdapter v2mAdpt) {
		tfViewOnlyMsg.setToolTipText("The message for the log entry");
		tfViewOnlyMsg.setText("An internal view log!");
		tfViewOnlyMsg.setColumns(12);
		tfLogMsg.setToolTipText("The message for the log entry");
		tfLogMsg.setText("A log message");
		tfLogMsg.setColumns(12);
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}

	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("Logger Library Demo");
		setSize(new Dimension(1100, 300));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(pnlControl, BorderLayout.NORTH);
		pnlLogControl.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"System Log Level", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		pnlControl.add(pnlLogControl);
		cbxSetLogLevel.setToolTipText("Set the minumum logging level of the global system-wide logger");
		pnlLogControl.add(cbxSetLogLevel);
		cbxSetLogLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.setLogLevel(cbxSetLogLevel.getItemAt(cbxSetLogLevel.getSelectedIndex()));
			}
		});
		cbxSetLogLevel.setModel(new DefaultComboBoxModel<LogLevel>(LogLevel.values()));
		cbxSetLogLevel.setSelectedIndex(1);
		pnlCreateLog.setToolTipText(
				"Controls to tell the model to create a specific log entry using hte model's local logger.");
		pnlCreateLog.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Send Log Msg to Model", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		pnlControl.add(pnlCreateLog);

		pnlCreateLog.add(tfLogMsg);
		btnMakeError.setToolTipText("Make an ERROR level log");
		pnlCreateLog.add(btnMakeError);
		btnMakeCritical.setToolTipText("Make an CRITICAL level log");
		btnMakeCritical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendCriticalLogMsg(tfLogMsg.getText());
			}
		});

		pnlCreateLog.add(btnMakeCritical);
		btnMakeInfo.setToolTipText("Make an INFO level log");
		btnMakeInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendInfoLogMsg(tfLogMsg.getText());
			}
		});

		pnlCreateLog.add(btnMakeInfo);
		btnMakeDebug.setToolTipText("Make an DEBUG level log");
		pnlCreateLog.add(btnMakeDebug);
		pnlViewOnly.setToolTipText("Controls to create a log entry in the view's local logger");
		pnlViewOnly.setBorder(new TitledBorder(null, "Make Critical Log to View-Only Logger", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		pnlControl.add(pnlViewOnly);

		pnlViewOnly.add(tfViewOnlyMsg);
		btnMakeViewLogMsg.setToolTipText("Make a log entry in the view's local logger");
		pnlViewOnly.add(btnMakeViewLogMsg);
		btnMakeViewLogMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewOnlyLogger.log(LogLevel.INFO, tfViewOnlyMsg.getText());
			}
		});
		btnMakeDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendDebugLogMsg(tfLogMsg.getText());
			}
		});
		btnMakeError.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				v2mAdpt.sendErrorLogMsg(tfLogMsg.getText());
			}
		});
		lblMsgDisplay.setToolTipText("Display for a string sent from the model");
		lblMsgDisplay.setBorder(
				new TitledBorder(null, "Message Display", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		contentPane.add(lblMsgDisplay, BorderLayout.SOUTH);
	}

	/**
	 * Start the view.
	 * This includes adding the global singleton logger to the local view logger chain (viewOnlyLogger). 
	 */
	public void start() {
		viewOnlyLogger.append(ILoggerControl.getSharedLogger()); // The global, singleton logger is chained to the end of the local view logger
		setVisible(true);
	}

	/**
	 * Add a panel to the center of the view.   The logger panel will be put here. 
	 * @param pnlCenter The panel to add to the center of the view
	 */
	public void addCenterPanel(JPanel pnlCenter) {
		contentPane.add(pnlCenter, BorderLayout.CENTER);
	}

	/**
	 * Display the given string on the display label
	 * @param msg The message to display
	 */
	public void displayMsg(String msg) {
		lblMsgDisplay.setText(msg);
	}
}

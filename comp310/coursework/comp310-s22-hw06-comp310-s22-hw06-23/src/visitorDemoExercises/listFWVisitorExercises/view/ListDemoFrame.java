package visitorDemoExercises.listFWVisitorExercises.view;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.Color;

import javax.swing.JScrollPane;

import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.GridLayout;

/**
 * The view for the ListFW demo app
 * @author swong
 * @param <THost> The type of host objects
 *
 */
public class ListDemoFrame<THost> extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147647062119454221L;

	/**
	 * Control panel
	 */
	private JPanel pnlControl = new JPanel();
	/**
	 * 
	 */
	private JTextField tfClassname = new JTextField();
	/**
	 * Scroll pane for the visitor output
	 */
	private JScrollPane scrollPaneOutput = new JScrollPane();
	/**
	 * Text area for visitor output
	 */
	private JTextArea taOutput = new JTextArea();
	/**
	 * Label for visitor class names
	 */
	private JLabel lblVisitorClassname = new JLabel("Visitor Classname:");
	/**
	 * Label for visitor input parameter
	 */
	private JLabel lblInputParameter = new JLabel("Input parameter:");
	/**
	 * Input for visitor parameter
	 */
	private JTextField tfParam = new JTextField();
	/**
	 * Label for host list
	 */
	private JLabel lblRunVisitorOn = new JLabel("List:");

	/**
	 * Adapter to the model
	 */
	private IModelAdapter<THost> model;
	/**
	 * Drop list of available hosts
	 */
	private JComboBox<THost> cbHosts = new JComboBox<THost>();
	/**
	 * Button to run the visitor
	 */
	private JButton btnRun = new JButton("Run");
	/**
	 * Label for the accumulators
	 */
	private JLabel lblAccumulator = new JLabel("Accumulator Classname:");
	/**
	 * Input for accumulator class name
	 */
	private JTextField tfAccClassname = new JTextField();
	/**
	 * Button to run Fold Right
	 */
	private JButton btnFoldr = new JButton("FoldR");
	/**
	 * Button to run Fold Left
	 */
	private JButton btnFoldL = new JButton("FoldL");
	/**
	 * Panel for visitor control
	 */
	private JPanel pnlVisitorCtrl = new JPanel();
	/**
	 * Panel host list control
	 */
	private JPanel pnlListCtrl = new JPanel();
	/**
	 * Panel for controlling folding
	 */
	private JPanel pnlFoldCtrl = new JPanel();
	/**
	 * Control sub-panel
	 */
	private JPanel pnlControlSub = new JPanel();

	/**
	 * Constructor for the view
	 * @param model The adapter to the model
	 * @param closeAction The window closing action to use.  Should be either WindowConstants.EXIT_ON_CLOSE or WindowConstants.HIDE_ON_CLOSE
	 */
	public ListDemoFrame(IModelAdapter<THost> model, int closeAction) {

		this.model = model;
		setDefaultCloseOperation(closeAction);
		initGUI();
	}

	/**
	 * Initialize the GUI components
	 */
	private void initGUI() {
		Container contentPane = this.getContentPane();
		setBounds(100, 100, 500, 427);

		pnlControl.setBackground(new Color(153, 255, 255));

		contentPane.add(pnlControl, BorderLayout.NORTH);

		pnlControl.add(pnlControlSub);
		pnlControlSub.setLayout(new GridLayout(0, 1, 0, 0));
		tfParam.setColumns(10);
		tfClassname.setColumns(10);
		pnlControlSub.add(pnlVisitorCtrl);
		pnlVisitorCtrl.add(lblVisitorClassname);
		pnlVisitorCtrl.add(tfClassname);
		pnlVisitorCtrl.add(lblInputParameter);
		pnlVisitorCtrl.add(tfParam);
		pnlControlSub.add(pnlListCtrl);
		pnlListCtrl.add(lblRunVisitorOn);
		pnlListCtrl.add(cbHosts);
		pnlListCtrl.add(btnRun);
		tfAccClassname.setColumns(10);
		pnlControlSub.add(pnlFoldCtrl);
		pnlFoldCtrl.add(lblAccumulator);
		pnlFoldCtrl.add(tfAccClassname);
		pnlFoldCtrl.add(btnFoldr);
		pnlFoldCtrl.add(btnFoldL);
		btnFoldL.addActionListener((e) -> {
				String result = model.runFoldL(
						cbHosts.getItemAt(cbHosts.getSelectedIndex()),
						tfAccClassname.getText(), tfParam.getText());
				taOutput.append(result + "\n");
		});

		btnFoldr.addActionListener((e) -> {
				String result = model.runFoldR(
						cbHosts.getItemAt(cbHosts.getSelectedIndex()),
						tfAccClassname.getText(), tfParam.getText());
				taOutput.append(result + "\n");
		});
		btnRun.addActionListener((e) -> {
				String result = model.runAlgo(
						cbHosts.getItemAt(cbHosts.getSelectedIndex()),
						tfClassname.getText(), tfParam.getText());
				taOutput.append(result + "\n");
		});

		contentPane.add(scrollPaneOutput, BorderLayout.CENTER);

		scrollPaneOutput.setViewportView(taOutput);
	}

	/**
	 * Start the view, i.e. make it visible.
	 */
	public void start() {
		setVisible(true);

	}

	/**
	 * Set drop list (cbHosts) to hold the given host objects.
	 * @param hosts  A vararg of THosts.
	 */
	public void setHosts(@SuppressWarnings("unchecked") THost... hosts) {
		for (THost host : hosts) {
			cbHosts.addItem(host);
		}

	}

}

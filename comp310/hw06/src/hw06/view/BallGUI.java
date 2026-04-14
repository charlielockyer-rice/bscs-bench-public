package hw06.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.function.Supplier;
import java.awt.event.ActionEvent;
import java.awt.Component;
import javax.swing.border.TitledBorder;

import provided.utils.view.TabbedPanel;

/**
 * The GUI of the Ball World Application.
 * @author Son Nguyen and Charlie Lockyer
 * @param <TDropListItem> The Factory type to make the ball
 */
public class BallGUI<TDropListItem> extends JFrame {

	/**
	 * Unique serial UID.
	 */
	private static final long serialVersionUID = 5311115233911321154L;
	
	/**
	 * The tab panel for the configurations.
	 */
	private TabbedPanel tabPanel = new TabbedPanel("Configuration");
	/**
	 * The adapter is initialized to a no-op to insure that system always has well-defined behavior.
	 */
	private IModelUpdateAdapter modelUpdateAdpt = IModelUpdateAdapter.NULL;

	/**
	 * Adapter back to the model for control tasks.
	 */
	private IModelCtrlAdapter<TDropListItem> modelCtrlAdpt;

	/**
	 * The content Panel that will contains everything of the Ball World Application.
	 */
	private JPanel contentPane;

	/**
	 * The Control Panel where all the control buttons, text fields, and drop lists are located.
	 */
	private final JPanel panelControl = new JPanel();

	/**
	 * The panel containing button and text field to do pain strategies.
	 */
	private final JPanel panelAddPaint = new JPanel();
	/**
	 * The text field for paint strategy input.
	 */
	private final JTextField textFieldPaint = new JTextField();
	/**
	 * This button add a paint strategy from textfieldpaint to the lists. 
	 */
	private final JButton btnAddPaint = new JButton("Add to list");

	/**
	 * The panel that contains make and combine button, two dropdown lists to make the ball.
	 */
	private final JPanel panelMake = new JPanel();
	/**
	 * This button make the choose strategy ball from the comboBoxMake list.
	 */
	private final JButton btnMakeBall = new JButton("Make Selected Ball");
	/**
	 * The top drop list, used to select what strategy to use in a new ball and
	 * to switch the switcher to.
	 */
	private JComboBox<TDropListItem> comboBoxMake = new JComboBox<TDropListItem>();
	/**
	 * Bottom drop list, used for combining with the top list selection.
	 */
	private JComboBox<TDropListItem> comboBoxCombine = new JComboBox<TDropListItem>();
	/**
	 * This button combines the two strategy selected from comboBoxMake and comboBoxCombine
	 */
	private final JButton btnCombine = new JButton("Combine!");

	/**
	 * The panel that contains make switcher button and switch button.
	 */
	private final JPanel panelSwitcher = new JPanel();
	/**
	 * This button make a switcher ball on the canvas.
	 */
	private final JButton btnMakeSwitcher = new JButton("Make Switcher");
	/**
	 * This button switch the switcher ball to a new strategy.
	 */
	private final JButton btnSwitch = new JButton("Switch!");

	/**
	 * This button clears up all of the balls on the canvas.
	 */
	private final JButton btnClear = new JButton("Clear All");

	/**
	 * The Canvas where the ball will be painted on.
	 */
	private final JPanel panelCanvas = new JPanel() {
		/**
		 * The Unique serial UID
		 */
		private static final long serialVersionUID = 8560409096153556181L;

		public void paintComponent(Graphics g) {
			super.paintComponent(g); // clear the panel and redo the background
			modelUpdateAdpt.paint(g); // call back to the model to paint the sprites
		}
	};
	/**
	 * The panel that contains add strategies functionalities: textfield and button
	 */
	private final JPanel panelAddUpdate = new JPanel();
	/**
	 * The textfield contains the new update strategy to be added to the lists.
	 */
	private final JTextField textFieldUpdate = new JTextField();
	/**
	 * The button to add the Update Strategy from textfield to the lists.
	 */
	private final JButton btnAddUpdate = new JButton("Add to list");
	/**
	 * The panel that contains interaction strategies functionalities: textfield and button.
	 */
	private final JPanel panelInteract = new JPanel();
	/**
	 * The textfield interact 
	 */
	private final JTextField txtFiledInteract = new JTextField();
	/**
	 * The button to add the Interaction Strategy from textfield to the lists.
	 */
	private final JButton btnAddInteract = new JButton("Add to list");
	/**
	 * The ball type panel.
	 */
	private final JPanel panelBallType = new JPanel();
	/**
	 * The text field ball type to input ball type.
	 */
	private final JTextField textFieldBallType = new JTextField();
	/**
	 * The button to add ball type to the drop list.
	 */
	private final JButton btnAddBallType = new JButton("Add New Type");
	/**
	 * The Drop down list for all the ball types added.
	 */
	private final JComboBox<String> comboBoxBallType = new JComboBox<String>();
	/**
	 * The Configuration algos panel.
	 */
	private final JPanel panelConfig = new JPanel();
	/**
	 * The text field to enter the configuration algo.
	 */
	private final JTextField textFieldConfig = new JTextField();
	/**
	 * The add configuration button for 
	 */
	private final JButton btnAddConfig = new JButton("Add to list");

	/**
	 * Create the frame.
	 * @param modelCtrlAdpt The interface for the button and functionalities of the GUI.
	 * @param modelUpdateAdpt The interface for whatever dynamic objects needed to be update (paint) on GUI.
	 */
	public BallGUI(IModelCtrlAdapter<TDropListItem> modelCtrlAdpt, IModelUpdateAdapter modelUpdateAdpt) {
		textFieldBallType.setToolTipText("Text the type of ball here to add.");
		textFieldBallType.setText("Default");
		textFieldBallType.setColumns(10);
		txtFiledInteract.setText("Collide");
		txtFiledInteract.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtFiledInteract.setToolTipText("Text the desired interaction strategy.");
		txtFiledInteract.setColumns(7);
		textFieldUpdate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldUpdate.setText("Overlap");
		textFieldUpdate.setToolTipText("Text one of the Update strategies here to add to the list: ");
		textFieldUpdate.setColumns(7);
		this.modelCtrlAdpt = modelCtrlAdpt;
		this.modelUpdateAdpt = modelUpdateAdpt;
		textFieldPaint.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldPaint.setText("Ball");
		textFieldPaint.setToolTipText("Text one of the Paint Strategies here to add to the list: ");
		textFieldPaint.setColumns(7);
		try {
			initGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the GUI components but do not start the frame.  
	 */
	private void initGUI() {
		setTitle("Ball World Application. - By Son Nguyen and Charlie Lockyer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1600, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		panelControl.setToolTipText("Control panel where control features are located.");
		panelControl.setBackground(Color.GREEN);

		contentPane.add(panelControl, BorderLayout.NORTH);
		panelBallType.setBorder(new TitledBorder(null, "Ball Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelBallType.setLayout(new BoxLayout(panelBallType, BoxLayout.Y_AXIS));
		panelControl.add(panelBallType);
		
		panelBallType.add(textFieldBallType);
		btnAddBallType.setToolTipText("Click to add the ball type to the drop list below.");
		btnAddBallType.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//ADD BALL TYPE BUTTON
		btnAddBallType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (null == textFieldBallType.getText())
					return; // just in case
				comboBoxBallType.insertItemAt(textFieldBallType.getText(), 0);
				comboBoxBallType.setSelectedIndex(0);
			}
		});
		
		panelBallType.add(btnAddBallType);
		comboBoxBallType.setToolTipText("This drop list contains the ball types loaded");
		comboBoxBallType.setFont(new Font("Dialog", Font.PLAIN, 12));
		
		panelBallType.add(comboBoxBallType);
		panelAddUpdate.setBorder(
				new TitledBorder(null, "Update Strategy", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAddUpdate.setBackground(Color.YELLOW);

		panelControl.add(panelAddUpdate);

		panelAddUpdate.setLayout(new BoxLayout(panelAddUpdate, BoxLayout.Y_AXIS));
		panelAddUpdate.add(textFieldUpdate);
		btnAddUpdate.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelInteract.setBorder(new TitledBorder(null, "Interact Strategy", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInteract.setBackground(Color.ORANGE);
		
		panelInteract.setLayout(new BoxLayout(panelInteract, BoxLayout.Y_AXIS));

		//ADD UPDATE BUTTON ACTION
		btnAddUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TDropListItem o = modelCtrlAdpt.addUpdateStrategy(textFieldUpdate.getText());
				if (null == o)
					return; // just in case
				addToMakeDropLists(o);
			}
		});
		btnAddUpdate
				.setToolTipText("Click this button to add the new Update strategy from the Text field to both list.");
		btnAddUpdate.setFont(new Font("Dialog", Font.PLAIN, 12));

		panelAddUpdate.add(btnAddUpdate);
		panelAddPaint.setBorder(
				new TitledBorder(null, "Paint Strategy", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAddPaint.setBackground(Color.PINK);

		panelControl.add(panelAddPaint);
		panelAddPaint.setLayout(new BoxLayout(panelAddPaint, BoxLayout.Y_AXIS));
		panelAddPaint.add(textFieldPaint);
		btnAddPaint.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAddPaint.setToolTipText("Click this button to add the new paint strategy from the Text field to both list.");

		//ADD PAINT BUTTON ACTION
		btnAddPaint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TDropListItem o = modelCtrlAdpt.addPaintStrategy(textFieldPaint.getText());
				if (null == o)
					return; // just in case
				addToMakeDropLists(o);
			}
		});

		btnAddPaint.setFont(new Font("Dialog", Font.PLAIN, 12));

		panelAddPaint.add(btnAddPaint);
		
		panelControl.add(panelInteract);
		
		panelInteract.add(txtFiledInteract);
		
		//ADD INTERACTION STRATEGY BUTTON.
		btnAddInteract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TDropListItem o = modelCtrlAdpt.addInteractStrategy(txtFiledInteract.getText());
				if (null == o)
					return; // just in case
				addToMakeDropLists(o);
			}
		});
		btnAddInteract.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnAddInteract.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnAddInteract.setToolTipText("Click to add Interaction Strategy to the drop lists.");
		
		panelInteract.add(btnAddInteract);
		panelConfig.setBorder(new TitledBorder(null, "Config Algos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelControl.add(panelConfig);
		panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.Y_AXIS));
		textFieldConfig.setToolTipText("Text the desired configuration algorithm.");
		textFieldConfig.setText("Lead");
		textFieldConfig.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldConfig.setColumns(7);
		
		panelConfig.add(textFieldConfig);
		
		//ADD CONFIGURATION STRATEGY BUTTON ACTION
		btnAddConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TDropListItem o = modelCtrlAdpt.addConfigAlgo(textFieldConfig.getText());
				if (null == o)
					return; // just in case
				addToMakeDropLists(o);
			}
		});
		btnAddConfig.setToolTipText("Click this button to add the new paint strategy from the Text field to both list.");
		btnAddConfig.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnAddConfig.setAlignmentX(0.5f);
		
		panelConfig.add(btnAddConfig);
		panelMake.setBorder(new TitledBorder(null, "Make Panel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelMake.setBackground(Color.WHITE);

		panelControl.add(panelMake);
		panelMake.setLayout(new BoxLayout(panelMake, BoxLayout.Y_AXIS));
		btnMakeBall.setToolTipText("Click to make the selected ball in the first drop box list on the canvas.");

		//MAKE BALL BUTTON ACTION
		btnMakeBall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBoxMake.getItemAt(comboBoxMake.getSelectedIndex()) == null) {
					System.err.println("Empty Box");
				} else {
					modelCtrlAdpt.makeBall(comboBoxMake.getItemAt(comboBoxMake.getSelectedIndex()), 
							comboBoxBallType.getItemAt(comboBoxBallType.getSelectedIndex()));
				}

			}
		});

		btnMakeBall.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnMakeBall.setAlignmentX(Component.CENTER_ALIGNMENT);

		panelMake.add(btnMakeBall);
		comboBoxMake.setToolTipText("Selected Strategy will be made by Make button or switch to by switch button.");
		comboBoxMake.setFont(new Font("Dialog", Font.PLAIN, 12));

		panelMake.add(comboBoxMake);
		comboBoxCombine.setToolTipText("This drop list combine with the top to help create combinational Strategy.");
		comboBoxCombine.setFont(new Font("Dialog", Font.PLAIN, 12));

		panelMake.add(comboBoxCombine);
		btnCombine.setToolTipText("Click to combine the two strategies from top drop list and bottom drop list.");

		//COMBINE STRATEGY BUTTON ACTION
		btnCombine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (comboBoxMake.getItemAt(comboBoxMake.getSelectedIndex()) == null
						|| comboBoxCombine.getItemAt(comboBoxCombine.getSelectedIndex()) == null) {
					System.err.println("Empty Box");
				} else {
					TDropListItem o = modelCtrlAdpt.combineStrategies(
							comboBoxMake.getItemAt(comboBoxMake.getSelectedIndex()),
							comboBoxCombine.getItemAt(comboBoxCombine.getSelectedIndex()));
					if (null == o)
						return; // just in case
					addToMakeDropLists(o);
				}
			}
		});
		btnCombine.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnCombine.setAlignmentX(Component.CENTER_ALIGNMENT);

		panelMake.add(btnCombine);
		panelSwitcher.setForeground(Color.WHITE);
		panelSwitcher.setBorder(
				new TitledBorder(null, "Switcher Panel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSwitcher.setBackground(Color.CYAN);

		panelControl.add(panelSwitcher);
		panelSwitcher.setLayout(new BoxLayout(panelSwitcher, BoxLayout.Y_AXIS));
		btnMakeSwitcher.setToolTipText(
				"Click to make a switcher ball with strategy default to the last switched strategy ( or straight by default).");

		//MAKE SWITCHER BALL BUTTON ACTION
		btnMakeSwitcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelCtrlAdpt.makeSwitcherBall();
			}
		});
		btnMakeSwitcher.setFont(new Font("Dialog", Font.PLAIN, 12));
		btnMakeSwitcher.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelSwitcher.add(btnMakeSwitcher);
		btnSwitch.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSwitch.setToolTipText(
				"Click to switch the strategy of a switcher ball to that currently on the top drop list.");

		//SWITCH BUTTON ACTION
		btnSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelCtrlAdpt.switchStrategy(comboBoxMake.getItemAt(comboBoxMake.getSelectedIndex()));
			}
		});
		btnSwitch.setFont(new Font("Dialog", Font.PLAIN, 12));

		panelSwitcher.add(btnSwitch);
		btnClear.setToolTipText("Click to clear all the balls off the screen.");

		//CLEAR BALL BUTTON ACTION
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelCtrlAdpt.clearBalls();
			}
		});
		btnClear.setFont(new Font("Dialog", Font.PLAIN, 12));
		panelControl.add(btnClear);
		panelCanvas.setToolTipText("The canvas where the ball will be painted on.");
		panelCanvas.setBackground(new Color(240, 248, 255));

		contentPane.add(panelCanvas, BorderLayout.CENTER);
		contentPane.add(tabPanel, BorderLayout.WEST);
	}

	/**
	 * Add the object to the two make panel drop lists.
	 * @param o the object to be added
	 */
	public void addToMakeDropLists(TDropListItem o) {
		comboBoxMake.insertItemAt(o, 0);
		comboBoxMake.setSelectedIndex(0);
		comboBoxCombine.insertItemAt(o, 0);
		comboBoxCombine.setSelectedIndex(0);
	}
	
	/**
	 * Starts the already initialized frame, making it 
	 * visible and ready to interact with the user. 
	 */
	public void start() {
		this.setVisible(true);
	}

	/**
	 * Updates the view by repainting the canvas
	 */
	public void update() {
		panelCanvas.repaint();
	}

	/**
	 * Get the Canvas panel
	 * @return the Canvas that the balls will be drawn on
	 */
	public JPanel getCanvas() {
		return this.panelCanvas;
	}

	/**
	 * @param label The string label to be put on the tab.
	 * @param compFac The actual component that is put on there.
	 */
	public void addComponentFac(String label, Supplier<JComponent> compFac) {
		tabPanel.addComponentFac(label, compFac);
	}

}

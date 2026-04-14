package hw05.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author charlielockyer
 * Ball view allows control of the user side
 * @param <TDropListItem> drop down list representing selection
 */
public class BallView<TDropListItem> extends JFrame {
	// TODO update based on link sent!
	
	/**
	 * Serial number
	 */
	private static final long serialVersionUID = 5986703344036397083L;
	
	/**
	 * Main frame content
	 */
	private JPanel contentPane;
	
	private IView2ModelAdapter<TDropListItem> modelAdapter = IView2ModelAdapter.NULL;
	
	private final JPanel pnlControl = new JPanel();
			
	
	/**
	 * Main viewing panel for the balls
	 */
	private JPanel pnlDisplay = new JPanel() {
		
		/**
		 * Serial number
		 */
		private static final long serialVersionUID = 7383794964235116372L;
		
		// paint component with the graphics
        public void paintComponent(Graphics g) {
            super.paintComponent(g);  // clear the panel and redo the background
            modelAdapter.paint(g);  // call back to the model to paint the sprites
         }
		
	};
	
	/**
	 * Instatiating all the buttons, panels, textfields, combo boxes in the view
	 */
	
	private final JTextField ballName = new JTextField();
	
	private final JButton btnAddBall = new JButton("Make Selected Ball");
	
	private final JButton btnClearAll = new JButton("Clear All Balls");
	
	private final JButton btnAddToLists = new JButton("Add to Lists");
	
	private final JButton btnMakeSwitcher = new JButton("Make Switcher");
	
	private final JButton btnSwitch = new JButton("Switch");
	
	private final JButton btnCombine = new JButton("Combine");
	
	private final JComboBox<TDropListItem> list1 = new JComboBox<TDropListItem>();
	
	private final JComboBox<TDropListItem> list2 = new JComboBox<TDropListItem>();
	
	private final JPanel pnlMovement = new JPanel();
	
	private final JPanel pnlCreateBall = new JPanel();
	
	private final JPanel pnlSwitch = new JPanel();
	
	private final JPanel pnlClear = new JPanel();
	
	private final JPanel pnlPaint = new JPanel();
	
	private final JPanel pnlInteract = new JPanel();
	
	private final JButton addPaintStrategy = new JButton("Add to Lists");
	
	private final JButton addInteractStrategy = new JButton("Add to Lists");
	
	private final JTextField paintStrategyText = new JTextField();
	
	private final JTextField interactStrategyText = new JTextField();

	
	/**
	 * Update the view
	 */
	public void update() {
		pnlDisplay.repaint();
	}
	
	/**
	 * Get the width of the canvas
	 * @return width
	 */
	public int getCanvasWidth() {
		return pnlDisplay.getWidth();
	}
	
	/**
	 * Get the height of the canvas
	 * @return height
	 */
	public int getCanvasHeight() {
		return pnlDisplay.getHeight();
	}
	
	/**
	 * Get the canvas
	 * @return the canvas
	 */
	public Container getCanvas() {
		return pnlDisplay;
	}
	
	/**
	 * Instantiating a ball view given a view to model adapter
	 * @param modelAdapter the adapter to the model we plan to use
	 */
	public BallView(IView2ModelAdapter<TDropListItem> modelAdapter) {
		this.modelAdapter = modelAdapter;
		paintStrategyText.setText("Ball");
		paintStrategyText.setToolTipText("Text that represents the paint strategy being entered");
		paintStrategyText.setColumns(10);
		interactStrategyText.setText("Interaction");
		interactStrategyText.setToolTipText("Text that represents the interact strategy being entered");
		interactStrategyText.setColumns(10);
		ballName.setToolTipText("Text that represents the update strategy being entered");
		pnlMovement.setBorder(new TitledBorder(null, "Update Strategy", TitledBorder.CENTER, TitledBorder.BOTTOM, null, null));
		pnlMovement.add(ballName);
		ballName.setText("Straight");
		ballName.setColumns(10);
	}
	
	/**
	 * Initializing the GUI and all the things in it (code isn't commented well because it was done in WindowBuilder)
	 */
	private void initGUI() {
		setTitle("Ball World");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 850);
		contentPane = new JPanel();
		contentPane.setToolTipText("The main BallWorld panel");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0,0));
		setContentPane(contentPane);
		pnlControl.setBackground(new Color(173, 216, 230));
		pnlControl.setToolTipText("The control panel for BallWorld");
		
		contentPane.add(pnlControl, BorderLayout.NORTH);
		
		pnlControl.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		pnlPaint.setBorder(
				new TitledBorder(null, "Paint Strategy", TitledBorder.CENTER, TitledBorder.BOTTOM, null, null));
		
		pnlInteract.setBorder(
				new TitledBorder(null, "Interact Strategy", TitledBorder.CENTER, TitledBorder.BOTTOM, null, null));
		
		pnlControl.add(pnlPaint);
		
		pnlControl.add(pnlInteract);

		
		pnlPaint.add(paintStrategyText);
		addPaintStrategy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!paintStrategyText.getText().equals("")) {
					TDropListItem newPaintStrat = modelAdapter.addPaintStrategy(paintStrategyText.getText());
					TDropListItem selected1 = (TDropListItem) list1.getSelectedItem();
					TDropListItem selected2 = (TDropListItem) list2.getSelectedItem();
					list1.addItem(newPaintStrat);
					list2.addItem(newPaintStrat);
					list1.setSelectedItem(selected1);
					list2.setSelectedItem(selected2);
				}
					
			}
		});
		
		addInteractStrategy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!interactStrategyText.getText().equals("")) {
					TDropListItem newInteractStrat = modelAdapter.addInteractStrategy(interactStrategyText.getText());
					TDropListItem selected1 = (TDropListItem) list1.getSelectedItem();
					TDropListItem selected2 = (TDropListItem) list2.getSelectedItem();
					list1.addItem(newInteractStrat);
					list2.addItem(newInteractStrat);
					list1.setSelectedItem(selected1);
					list2.setSelectedItem(selected2);
				}
					
			}
		});
		
		addPaintStrategy.setToolTipText("Add paint strategy");
		
		pnlPaint.add(addPaintStrategy);
		
		pnlInteract.add(addInteractStrategy);

		
		pnlControl.add(pnlMovement);
		
		pnlMovement.add(btnAddToLists);
		
		pnlInteract.add(interactStrategyText);

		
		btnAddToLists.setToolTipText("Click to add ball to dropdowns");
		
		btnAddToLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!ballName.getText().equals("")) {
					
					TDropListItem newStrat = modelAdapter.addUpdateStrategy(ballName.getText());
					TDropListItem selected1 = (TDropListItem) list1.getSelectedItem();
					TDropListItem selected2 = (TDropListItem) list2.getSelectedItem();
					
					list1.addItem(newStrat);
					list2.addItem(newStrat);
					
					list1.setSelectedItem(selected1);
					list2.setSelectedItem(selected2);
				}
			}
		});
		
		
		
		pnlControl.add(pnlCreateBall);
		
		pnlCreateBall.setLayout(new GridLayout(0, 1, 0, 0));
		pnlCreateBall.add(btnAddBall);
		
		btnAddBall.setToolTipText("Button to make a ball");
		btnAddBall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list1.getSelectedItem() != null)
					modelAdapter.loadBall((TDropListItem) list1.getSelectedItem());
			}
		});
		
		list1.setToolTipText("Selected ball strategy");
		pnlCreateBall.add(list1);
		list2.setToolTipText("Strategy to combine with top");
		pnlCreateBall.add(list2);
		pnlCreateBall.add(btnCombine);
		
		btnCombine.setToolTipText("Click to combine behaviours");
		btnCombine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list1.getItemCount() != 0) {
					TDropListItem combinedStrat = modelAdapter.combine(list1.getItemAt(list1.getSelectedIndex()), 
							list2.getItemAt(list2.getSelectedIndex()));
					
					list1.addItem(combinedStrat);
					list2.addItem(combinedStrat);
				}
			}
		});
		
		pnlSwitch.setBorder(
				new TitledBorder(null, "Switcher Controls", TitledBorder.CENTER, TitledBorder.BOTTOM, null, null));
		
		pnlControl.add(pnlSwitch);
		pnlSwitch.setLayout(new GridLayout(0, 1, 0, 0));
		pnlSwitch.add(btnMakeSwitcher);
		
		btnMakeSwitcher.setToolTipText("Button to make a switcher ball");
		pnlSwitch.add(btnSwitch);
		btnMakeSwitcher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list1.getSelectedItem() != null)
					modelAdapter.makeSwitcherBall((TDropListItem) list1.getSelectedItem());
			}
		});
		
		btnSwitch.setToolTipText("Switch all switcher balls");
		btnSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(list1.getItemCount() != 0)
					modelAdapter.switchSwitcher((TDropListItem) list1.getSelectedItem());
			}
		});
		
		pnlControl.add(pnlClear);
		pnlClear.setLayout(new GridLayout(0, 1, 0, 0));
		pnlClear.add(btnClearAll);
		
		btnClearAll.setToolTipText("Button to clear all balls");
		btnClearAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelAdapter.clearAll();
			}
		});
		
		pnlDisplay.setToolTipText("Main display panel for BallWorld");
		
		contentPane.add(pnlDisplay, BorderLayout.CENTER);
	}
	
	/**
	 * Initializes everything and makes the window visible
	 */
	public void start() {
		initGUI();
		this.setVisible(true);
	}
}

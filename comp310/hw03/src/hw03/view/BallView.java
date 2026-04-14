package hw03.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

/**
 * @author Christina
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
	
	/**
	 * Control panel
	 */
	private final JPanel CtrlPanel = new JPanel();
	
	/**
	 * Clear balls button
	 */
	private final JButton btnClear = new JButton("Clear Balls");
	
	/**
	 * Input for ball class name
	 */
	private final JTextField textField = new JTextField();
	
	/**
     * Create the view to model adapter.
     */
    private IModelUpdateAdapter<?> _modelUpdate = IModelUpdateAdapter.NULL_OBJECT;  

    
    /**
     * Adapter back to the model for control tasks.
     */
     private IModelControlAdapter<TDropListItem> _modelControlAdpt;   

    /**
     * The top drop list, used to select what strategy to use in a new ball and
     * to switch the switcher to.
     */
    private JComboBox<TDropListItem> _list1DL = new JComboBox<TDropListItem>(); // TODO panel add to right 

    /**
     * Bottom drop list, used for combining with the top list selection.
     */
    private JComboBox<TDropListItem> _list2DL = new JComboBox<TDropListItem>(); 
    
    /**
     * @param modelCtrlAdpt model control adapter input
     * @param modelUpdateAdpt model update adapter input
     */
    public BallView(IModelControlAdapter<TDropListItem> modelCtrlAdpt, IModelUpdateAdapter<?> modelUpdateAdpt) {
        this._modelControlAdpt = modelCtrlAdpt;
        this._modelUpdate = modelUpdateAdpt;
        leftPanel.setBackground(Color.CYAN);
        leftPanel.setToolTipText("Panel to add stuff to list");
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(textField);
        textField.setToolTipText("Type strategy");
        textField.setColumns(10);
        initGUI();
    }
    
	/**
	 * Create new ball button
	 */
	private final JButton btnAddList = new JButton("Add to List");
	
	/**
	 * Create new ball button
	 */
	private final JButton makeSelectedBtn = new JButton("Make Selected Ball");
	
	/**
	 * Create new ball button
	 */
	private final JButton combineBtn = new JButton("Combine Traits");

	
	/**
	 * Main viewing panel for the balls
	 */
	private JPanel CenterPanel = new JPanel() {
		
		/**
		 * Serial number
		 */
		private static final long serialVersionUID = 7383794964235116372L;
		
		// paint component with the graphics
        public void paintComponent(Graphics g) {
            super.paintComponent(g);  // clear the panel and redo the background
            _modelUpdate.update(g);  // call back to the model to paint the sprites
         }
		
	};
	/**
	 * center panel for balls
	 */
	private final JPanel centerPanel = new JPanel();
	/**
	 * left panel for strategy creation
	 */
	private final JPanel leftPanel = new JPanel();
	/**
	 * right panel to clear balls
	 */
	private final JPanel rightPanel = new JPanel();
	/**
	 * switch panel for switcher functionality
	 */
	private final JPanel switchPanel = new JPanel();
	/**
	 * make switch button to create switcher
	 */
	private final JButton btnMakeSwitch = new JButton("Make Switcher");
	/**
	 * button to switch switcher class
	 */
	private final JButton btnS = new JButton("Switch");


	/**
	 * Instance of the initGUI method
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		CenterPanel.setToolTipText("Panel for display");
		// Construct design panel
		contentPane.add(CenterPanel, BorderLayout.CENTER);
		CtrlPanel.setToolTipText("Control panel");
		CtrlPanel.setBackground(Color.CYAN);
		contentPane.add(CtrlPanel, BorderLayout.NORTH);
		CtrlPanel.setLayout(new BoxLayout(CtrlPanel, BoxLayout.X_AXIS));
		
		CtrlPanel.add(leftPanel);
		// Construct GUI
		textField.setColumns(10);
		centerPanel.setBackground(Color.CYAN);
		centerPanel.setToolTipText("Panel to store combine trait area");
		
		CtrlPanel.add(centerPanel);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		combineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TDropListItem o = _modelControlAdpt.combineStrategies(_list1DL.getItemAt(
						_list1DL.getSelectedIndex()), _list2DL.getItemAt(_list2DL.getSelectedIndex()));
				_list1DL.insertItemAt(o, 0);
				_list2DL.insertItemAt(o, 0);
			}
		});
		combineBtn.setToolTipText("Click to combine strategies");
		centerPanel.add(combineBtn);
        centerPanel.add(_list1DL);
        centerPanel.add(_list2DL);
        centerPanel.add(makeSelectedBtn);

        makeSelectedBtn.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
        	   _modelControlAdpt.makeBall(_list1DL.getItemAt(_list1DL.getSelectedIndex()));
           }
        });
		        
		        
		switchPanel.setBackground(Color.CYAN);
		
		CtrlPanel.add(switchPanel);
		switchPanel.setLayout(new BoxLayout(switchPanel, BoxLayout.Y_AXIS));
		btnMakeSwitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_modelControlAdpt.makeSwitcherBall("hw03.model.strategy.SwitcherStrategy");
			}
		});
		btnMakeSwitch.setToolTipText("Click to make a switcher");
		
		switchPanel.add(btnMakeSwitch);
		btnS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_modelControlAdpt.switchStrategy(_list2DL.getItemAt(_list2DL.getSelectedIndex()));
			}
		});
		btnS.setToolTipText("Click to switch switcher balls");
		
		switchPanel.add(btnS);
		btnAddList.setToolTipText("Click to add strategy to list");
		leftPanel.add(btnAddList);
		
		btnAddList.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               TDropListItem o = _modelControlAdpt.addStrategy(textField.getText());
               if (null == o) return; // just in case
		
               _list1DL.insertItemAt(o, 0);
               _list2DL.insertItemAt(o, 0);
           }
        });
			
			CtrlPanel.add(rightPanel);
			rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
			rightPanel.add(btnClear);
			btnClear.setToolTipText("Clear existing balls");
			
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				 // clear all figures on screen
				_modelControlAdpt.clearBalls();
			}
		});
	}
	
	/**
	 * Updates the view by repainting the canvas
	 */
	public void update() {
		CenterPanel.repaint();
	}
	
	/**
	 * @return The panel where balls are held
	 */
	public JPanel getCanvas() {
		return CenterPanel;
	}

	/**
	 * Start the GUI
	 */
	public void start() {
		setVisible(true);
	}


}

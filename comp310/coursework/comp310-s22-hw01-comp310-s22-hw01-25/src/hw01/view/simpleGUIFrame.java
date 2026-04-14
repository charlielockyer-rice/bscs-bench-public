package hw01.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import hw01.shape.AShape;
import hw01.shape.Circle;
import hw01.shape.CompositeShape;
import hw01.shape.Ellipse;
import hw01.shape.Rectangle;

public class simpleGUIFrame extends JFrame {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long serialVersionUID = -8280610852137860275L;

	/**
	 * The panel containing all of the content of the GUI
	 */
	private JPanel contentPane;

	/**
	 * An AShape object to be painted in the GUI. Hard coded here per the instructions of the
	 * assignment
	 */
	private AShape aShape = new Rectangle(0,30,200,20, Color.GREEN);

	/**
	 * The center panel of the GUI. Where shapes are painted
	 */
	private final JPanel centerJPanel = new JPanel() {
		/**
		 * Auto-generated serialVersionUID
		 */
		private static final long serialVersionUID = -5232928394266770286L;

		/**
		* Overridden paintComponent method to paint a shape in the panel.
		* @param g The Graphics object to paint on.
		**/
		@Override
		public void paintComponent(Graphics g) {
		    super.paintComponent(g);   // Do everything normally done first, e.g. clear the screen.
		    g.setColor(Color.RED);  // Set the color to use when drawing
		    g.fillOval(75, 100, 20, 40);  // paint a filled 20x40 red ellipse whose upper left corner is at (75, 100)
		    aShape.paint(g); // paint an AShape
		}
	};

	/**
	 * The north panel of the GUI.
	 */
	private final JPanel northJPanel = new JPanel();

	/**
	 * Label for the button in No
	 */
	private final JLabel NorthJLabel = new JLabel("Text Changing Button");

	/**
	 * Button in NorthJPanel that changes text
	 */
	private final JButton NorthJButton = new JButton("New button");

	/**
	 * Text field in NorthJPanel to input new text for the button in NorthJPanel
	 */
	private final JTextField NorthJTextField = new JTextField();

	/**
	 * Panel containing buttons to change which shape appears in centerJPanel
	 */
	private final JPanel southJpanel = new JPanel();

	/**
	 * Button to change the shape which appears in centerJPanel to a rectangle
	 */
	private final JButton rectangleButton = new JButton("Rectangle");

	/**
	 * Button to change the shape which appears in centerJPanel to an ellipse
	 */
	private final JButton ellipseButton = new JButton("Ellipse");

	/**
	 * Button to change the shape which appears in centerJPanel to a circle
	 */
	private final JButton circleButton = new JButton("Circle");

	/**
	 * Button to change the shape which appears in cetnerJPanel to a crude rendering of Grandpa
	 * Freeman's car, Dorothy
	 */
	private final JButton carButton = new JButton("Pimp my GUI");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					simpleGUIFrame frame = new simpleGUIFrame();
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public simpleGUIFrame() {
		NorthJTextField.setToolTipText("Input text to appear on the button\r\n");
		NorthJTextField.setColumns(10);
		initGUI();
	}

	/**
	 * Initialize the GUI components but do not start the frame.
	 */
	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		centerJPanel.setToolTipText("Blue center panel\r\n");
		centerJPanel.setBackground(Color.BLUE);

		contentPane.add(centerJPanel, BorderLayout.CENTER);
		northJPanel.setToolTipText("Orange north panel");
		northJPanel.setBackground(Color.ORANGE);

		contentPane.add(northJPanel, BorderLayout.NORTH);
		NorthJLabel.setToolTipText("Label in north panel");

		northJPanel.add(NorthJLabel);
		NorthJButton.setToolTipText("Click to change text on button to text in text field");
		NorthJButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NorthJButton.setText(NorthJTextField.getText());
			}
		});

		northJPanel.add(NorthJButton);

		northJPanel.add(NorthJTextField);
		southJpanel.setBackground(Color.ORANGE);

		contentPane.add(southJpanel, BorderLayout.SOUTH);
		rectangleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aShape = new Rectangle(0,30,200,20, Color.RED);
				centerJPanel.repaint();
			}
		});
		rectangleButton.setToolTipText("Button to paint a Rectangle");

		southJpanel.add(rectangleButton);
		ellipseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aShape = new Ellipse(0,30,200,20, Color.RED);
				centerJPanel.repaint();
			}
		});
		ellipseButton.setToolTipText("Button to paint an Ellipse");

		southJpanel.add(ellipseButton);
		circleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aShape = new Circle(0,30,40, Color.RED);
				centerJPanel.repaint();
			}
		});
		circleButton.setToolTipText("Button to paint a circle");

		southJpanel.add(circleButton);
		carButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle body = new Rectangle(30, 30, 100, 20, Color.RED);
				Circle frontWheel = new Circle(30, 50, 20, Color.BLACK);
				Circle backWheel = new Circle(80, 50, 20, Color.BLACK);
				aShape = new CompositeShape(new CompositeShape(body, frontWheel), backWheel);
				centerJPanel.repaint();
			}
		});
		carButton.setToolTipText("Button to draw a car");

		southJpanel.add(carButton);
	}

	/**
	 * Starts the already initialized frame, making it
	 * visible and ready to interact with the user.
	 */
	public void start(){
		setVisible(true);
	}


}

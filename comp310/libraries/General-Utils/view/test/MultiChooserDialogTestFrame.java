package provided.utils.view.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import provided.utils.view.MultiChooserDialog;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

/**
 * Example of a custom option type
 */
interface IMyOption {
	
	/**
	 * Get the option's name
	 * @return The option name
	 */
	public String getName();
	
	/**
	 * Convenience factory for IMyOption instances
	 * @param name The name of the option
	 * @return An IMyOption instance
	 */
	public static IMyOption make(String name) {
		
		return new IMyOption() {

			@Override
			public String getName() {
				return name;
			}
			

			/**
			 * Overridden to show that the toString() is used to display the option
			 */
			@Override 
			public String toString() {
				return "MyOption \""+getName()+"\"";
			}
			
		};
	}
}

/**
 * Test frame
 */
public class MultiChooserDialogTestFrame extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 795554125833111722L;
	private JPanel contentPane;
	private final JPanel pnlControl = new JPanel();
	private final JButton btnMake = new JButton("Make MultiChooserDialog");
	private final JTextPane taInstructions = new JTextPane();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MultiChooserDialogTestFrame frame = new MultiChooserDialogTestFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MultiChooserDialogTestFrame() {
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("MultiChooserDialog Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
				setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		contentPane.add(pnlControl, BorderLayout.NORTH);
		btnMake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDialog();
			}
		});
		
		pnlControl.add(btnMake);
		taInstructions.setText("To try out different options, modify MainFrame.showDialog().\r\n\r\n* Try different widths and heights (note:  zero for either value will cause the default value to be used).\r\n* Try enabling single select.\r\n* Try changing the default selected options or omitting it completely.\r\n* Try some of the simpler MultiChooserDialog constructors that require fewer parameters and are easier to use for simple configurations.\r\n     - MultiChooserDialog has many constructors, so use the one that specifies only the parameters you wish to explicitly specify. ");
		
		contentPane.add(taInstructions, BorderLayout.CENTER);
	}
	
	/**
	 * Show the MultChooserDialog 
	 */
	private void showDialog() {
		
		// The toString() of the options will be used for display in the dialog
		List<IMyOption> options = List.of(IMyOption.make("A"), IMyOption.make("B"), IMyOption.make("C"), IMyOption.make("D"), IMyOption.make("E"), IMyOption.make("F"));

		boolean isSingleSelect = false;  // Set to true for single select. Omit this parameter for default multi-select.
		Dimension dialogSize = new Dimension(0, 200); // width = 0 means use default width.  Omit this parameter completely for default width and height.
		
		// Default selected options can be specified.
		List<IMyOption> defaultSelectedOptions = List.of(options.get(1),options.get(4));
		
		
		// Make the dialog and show it, returning the selected items if any.  
		// Use a simpler MultiChooserDialog constructor if not explicitly specifying certain parameters (i.e. using defaults).
		List<IMyOption> results  = new MultiChooserDialog<IMyOption>("Select all desired options", options, "Please choose wisely!", isSingleSelect, dialogSize, defaultSelectedOptions).choose();
		
		// Quick way to print the contents of a List
		System.out.println("Results = "+Arrays.toString(results.toArray()));		
	}

}

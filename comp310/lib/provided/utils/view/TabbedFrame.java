package provided.utils.view;

import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JFrame;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;

import java.awt.BorderLayout;

/**
 * A frame to hold a tabbedPanel.   TabbedPanels are NOT required to be used in this frame!
 * This class is designed merely as an example which could be used as is or 
 * where its code can be copied or sub-classed to implement additional custom behavior.
 * NOTE: This frame defaults to disabling its close button, i.e. this frame cannot be closed.
 * This is useful for situations where the main app should not be ended when this frame is closed 
 * and the displayed components always need to be visible.
 * This behavior can be changed by calling this frame's setDefaultCloseOperation() method.
 * @author swong
 *
 */
public class TabbedFrame extends JFrame {
	/**
	 * for serialization
	 */
	private static final long serialVersionUID = -746430511867797798L;
	
	/**
	 * Tabbed panel to hold the dynamically created components
	 * No need for a title on the tabbed panel b/c the frame already has a title
	 */
	private TabbedPanel pnlTabbed;

	/**
	 * The logger in use
	 */
	private ILogger logger; 


	/**
	 * Create the frame using the given title and the shared system logger.
	 * @param title The title for the frame
	 */
	public TabbedFrame(String title) {
		this(title, ILoggerControl.getSharedLogger());
	}

	/**
	 * Create the frame using the given title and logger.
	 * @param title The title for the frame
	 * @param logger The logger to use.
	 */
	public TabbedFrame(String title, ILogger logger) {
		super(title);
		this.logger = logger;
		pnlTabbed = new TabbedPanel("", this.logger);
		initGUI();
	}	
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
//		setTitle("Configurations");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		getContentPane().add(pnlTabbed, BorderLayout.CENTER);
	}
	
	/**
	 * Returns the current value for whether or not new components are 
	 * added inside a JScrollPane by default;   
	 * This method simply delegates to the internal TabbedPanel instance.
	 * @return true if surrounding JScrollPanes are added by default, false otherwise.
	 */
	public boolean getDefaultAddScroll() {
		return this.pnlTabbed.getDefaultAddScroll();
	}
	
	/**
	 * Sets the value for whether or not new components are 
	 * added inside a JScrollPane by default;
	 * This method simply delegates to the internal TabbedPanel instance.
	 * @param addScrollDefault if true, surrounding JScrollPanes are to be added by default, 
	 * otherwise no surrounding JScrollPane will be added by default.
	 */
	public void setDefaultAddScroll(boolean addScrollDefault) {
		this.pnlTabbed.setDefaultAddScroll(addScrollDefault); 
	}
	

	/**
	 * Add a new component on its own tab using the given factory.  
	 * The component is always put inside of a JScrollPane so that the user can always access the entirety of the component.
	 * A command is returned that will remove the new tab.  
	 * This method and the returned command can be called from any thread.
	 * This method simply delegates to the internal TabbedPanel instance.
	 * @param label The label for the tab
	 * @param fac A factory that will instantiate the new component.
	 * @return A command that will remove the tab created by this method.
	 */
	public Runnable addComponentFac(String label, Supplier<JComponent> fac) {
		Runnable cmd =  pnlTabbed.addComponentFac(label, fac);
		validate();
		pack();
		return cmd;
	}
	
	/**
	 * Add a new component on its own tab using the given factory.  
	 * The component is always put inside of a JScrollPane so that the user can always access the entirety of the component.
	 * A command is returned that will remove the new tab.  
	 * This method and the returned command can be called from any thread.
	 * This method simply delegates to the internal TabbedPanel instance.
	 * @param label The label for the tab
	 * @param fac A factory that will instantiate the new component.
	 * @param addScroll If true, put the new component into a surrounding JScrollPane, 
	 * otherwise install the new component without a surrounding JScrollPane.  
	 * This value overrides but does not change the value returned by getDefaultAddScroll().
	 * @return A command that will remove the tab created by this method.
	 */
	public Runnable addComponentFac(String label, Supplier<JComponent> fac, boolean addScroll) {
		Runnable cmd =  pnlTabbed.addComponentFac(label, fac, addScroll);
		validate();
		pack();
		return cmd;
	}
	
	/**
	 * Accessor for the internal TabbedPanel.  
	 * Not used in most usage scenarios.
	 * Only used when more customized control of the tabbed panel is desired.
	 * @return The internal TabbedPanel instance.
	 */
	public TabbedPanel getTabbedPanel() {
		return pnlTabbed;
	}
	
	
	/**
	 * Start the frame
	 */
	public void start() {
		setVisible(true);
	}
}

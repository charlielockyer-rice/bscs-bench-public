package provided.utils.view;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * A panel containing multiple tabs that can hold components. 
 * A tab is added by supplying a label and a JComponent factory.
 * Note that the implementation has a lot of "safety" features to help minimize threading issues 
 * and is thus more complicated than normal.  More detailed logging output can be obtained by setting the 
 * logger's logging level to LogLevel.DEBUG.  
 * USAGE NOTE: In UI's that employ nested scroll panes, e.g. nested TabbedPanels, nested components may not display
 * as expected due the logic used by the layout managers as to which components take precedence in terms of their 
 * display sizes.   For instance, precedence may be given to the innermost component to be displayed at its 
 * full size rather than being scrolled and the outermost component, which contains the innermost component, 
 * is forced to scroll. To alleviate this issue, this class offers the option to include or not include surrounding
 * scroll bars (via a JScrollPane) as either a default setting for all newly added components or 
 * on a call-by-call basis when adding new components.
 * @author swong
 *
 */
public class TabbedPanel extends JPanel {
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 286960863341484132L;
	
	/**
	 * The titled border used by the panel
	 */
	private TitledBorder titledBorder = new TitledBorder(null, "Tabbed Panel", TitledBorder.LEADING, TitledBorder.TOP, null, null);
	
	/**
	 * The tabbed pane holding the tabs.
	 */
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * If true, default to putting new components into a JScrollPane, 
	 * otherwise install new components by default without a JScrollPane.
	 */
	boolean defaultAddScroll = true;
	
	/**
	 * The logger in use
	 */
	private ILogger logger;

	/**
	 * Create the panel with the given title and logger and the given scrolling setting.
	 * The defaultAddScroll value is used whenever an addScroll value is not explicitly specified.
	 * @param title The title on the border of this panel.  Use an empty string for no title.
	 * @param logger The logger to use
	 * @param defaultAddScroll If true, default to putting new components into a JScrollPane, 
	 * otherwise install new components by default without a JScrollPane.
	 */
	public TabbedPanel(String title, ILogger logger, boolean defaultAddScroll) {
		this.logger  = logger;
		this.setDefaultAddScroll(defaultAddScroll);
		setTitle(title);
		initGUI();		
	}
	/**
	 * Create the panel with the given title and logger where the default is to put new components 
	 * in a JScrollPane.  This constructor is equivalent to TabbedPanel(title, logger, true).
	 * @param title The title on the border of this panel.  Use an empty string for no title.
	 * @param logger The logger to use
	 */
	public TabbedPanel(String title, ILogger logger) {
		this(title, logger, true);
	}
	
	/**
	 * Create the panel with the given title.  
	 * The logger is set to ILoggerControl.getSharedLogger().
	 * @param title The title on the border of this panel.   Use an empty string for no title.
	 */
	public TabbedPanel(String title) {
		this(title, ILoggerControl.getSharedLogger());
	}
	
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setBorder(titledBorder);
		setLayout(new BorderLayout(0, 0));
		
		add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Set the title on the border of this panel.
	 * @param title The title to set.    Use an empty string for no title.
	 */
	public void setTitle(String title) {
		titledBorder.setTitle(title);
	}
	
	/**
	 * Returns the current value for whether or not new components are 
	 * added inside a JScrollPane by default;
	 * @return true if surrounding JScrollPanes are added by default, false otherwise.
	 */
	public boolean getDefaultAddScroll() {
		return this.defaultAddScroll;
	}
	
	/**
	 * Sets the value for whether or not new components are 
	 * added inside a JScrollPane by default;
	 * @param addScrollDefault if true, surrounding JScrollPanes are to be added by default, 
	 * otherwise no surrounding JScrollPane will be added by default.
	 */
	public void setDefaultAddScroll(boolean addScrollDefault) {
		this.defaultAddScroll = addScrollDefault;
	}
	
	
	
	/**
	 * Add a new component on its own tab using the given factory.  
	 * The new component is put inside of a JScrollPane 
	 * so that the user can always access the entirety of the component if 
	 * getDefaultAddScroll() returns true, otherwise no JScrollPane is added.  
	 * This method is equivalent to addComponentFac(label, fac, getDefaultAddScroll()).
	 * A command is returned that will remove the new tab.  
	 * This method and the returned command can be called from any thread.
	 * @param label The label for the tab
	 * @param fac A factory that will instantiate the new component.
	 * @return A command that will remove the tab created by this method.
	 */
	public Runnable addComponentFac(String label, Supplier<JComponent> fac) {
		return this.addComponentFac(label, fac, getDefaultAddScroll());
	}
	
	/**
	 * Add a new component on its own tab using the given factory.  
	 * If addScroll=true, the component is put inside of a JScrollPane so that the user 
	 * can always access the entirety of the component.
	 * A command is returned that will remove the new tab.  
	 * This method and the returned command can be called from any thread.
	 * @param label The label for the tab
	 * @param fac A factory that will instantiate the new component.
	 * @param addScroll If true, put the new component into a surrounding JScrollPane, 
	 * otherwise install the new component without a surrounding JScrollPane.  
	 * This value overrides but does not change the value returned by getDefaultAddScroll().
	 * @return A command that will remove the tab created by this method.
	 */
	public Runnable addComponentFac(String label, Supplier<JComponent> fac, boolean addScroll) {
		final LinkedBlockingQueue<Runnable> bq = new LinkedBlockingQueue<Runnable>(); // Enables retrieval of remove function from GUI thread.
		
		// Make a command that can be dispatched over to the GUI thread if necessary.
		Runnable cmd = ()-> {
			logger.log(LogLevel.DEBUG, "Running component factory "+ (addScroll?"with":"without") + " surrounding scroll bars...");
			JComponent newComp = fac.get();
			final JComponent installedComp = addScroll ? new JScrollPane(newComp): newComp;
			
			tabbedPane.addTab(label, null, installedComp, null);
			tabbedPane.setSelectedComponent(installedComp);
			
			if(addScroll) {
				if(newComp instanceof IScrollRequestor scrollRequestor) {  // Ugly. There's got to be a better way than this!
					logger.log(LogLevel.INFO, "Installing scroll request functionality into "+newComp);
					scrollRequestor.setScrollRequest((scrollDir)->{
						
						newComp.validate(); // Make sure the component is properly laid out first.
						
						SwingUtilities.invokeLater(()->{
							JScrollBar scBar; // The scroll bar to move
							int scrollValue; // The location to move the scroll bar to.
							
							// Get the appropriate scroll bar and extrema location as per the given scrolling direction.
							switch(scrollDir) {
								case TOP:
									scBar = ((JScrollPane) installedComp).getVerticalScrollBar();
									scrollValue = scBar.getMinimum();							
									break;
									
								case BOTTOM:
									scBar = ((JScrollPane) installedComp).getVerticalScrollBar();
									scrollValue = scBar.getMaximum();
									break;

								case LEFT:
									scBar = ((JScrollPane) installedComp).getHorizontalScrollBar();
									scrollValue = scBar.getMinimum();							
									break;
									
								case RIGHT:
									scBar = ((JScrollPane) installedComp).getHorizontalScrollBar();
									scrollValue = scBar.getMaximum();							
									break;									
									
								default:
									logger.log(LogLevel.ERROR, "Invalid scroll direction value given: "+scrollDir);
									return;
							}
							logger.log(LogLevel.DEBUG, "Auto-scrolling to the "+scrollDir+" end of the range for component: "+newComp );
							scBar.setValue(scrollValue); // Move the scroll bar to the given location.
						});
						
					});
				}
			}
			
			// The command to remove the tab is ready.  This command does not have to be run directly on the GUI thread.
			bq.offer(new Runnable() {
				
				/*
				 * A full anonymous inner class is used rather than a lambda expression for the outer Runnable 
				 * so that an internal field could be defined.
				 */
				
				/**
				 * Internal command that removes the tab the first time that it is run but gives error after that.
				 * Represents the "state" of the command, i.e. either the panel yet to be removed or has already been removed.
				 * The added component is not destroyed, only the tab is removed.
				 */
				Runnable cmd = () ->{
					// Need to remove the tab on the GUI thread.
					SwingUtilities.invokeLater(()->{
						tabbedPane.remove(installedComp);
						logger.log(LogLevel.DEBUG, "Tab, "+label+", removed.");
						// Replace cmd to give error if attempt to remove tab again.
						cmd  = () ->{
							logger.log(LogLevel.CRITICAL, "Attempted to remove already deleted tab: "+label);
						};
					});
				};
				
				@Override
				public void run() {
					cmd.run(); // remove the tab if it hasn't been removed already.
				}
			});
			 
			installedComp.validate();
			TabbedPanel.this.revalidate();  // revalidate seems to work better than validate()
			TabbedPanel.this.repaint();
			logger.log(LogLevel.DEBUG, "New component installed in new tab and removal command available.");
			
		};
		//If already on GUI thread, then just do it here.
		if (SwingUtilities.isEventDispatchThread()) {
			logger.log(LogLevel.DEBUG, "Calling thread is GUI thread. Installing new component and tab now.");
			cmd.run();
		}
		else {
			logger.log(LogLevel.DEBUG, "Calling thread is NOT GUI thread. Deferring to GUI thread to perform new component and tab installation.");
			SwingUtilities.invokeLater(cmd); 
		}
		// Return a lambda that will remove the tab holding the new component.  The component itself is not deleted.
		// Returns a command without blocking the calling thread to help avoid threading issues in the caller.
		return new Runnable() {
			/*
			 * A full anonymous inner class is used rather than a lambda expression for the outer Runnable 
			 * so that an internal field could be defined.
			 */
			
			/**
			 * Get the tab removal cmd and run it.  Replaced with the removal cmd once acquired.
			 * This command represents the "state" of the command, i.e. remove function not attained yet 
			 * or already attained.
			 */
			Runnable cmd = () -> {
				// Start a new thread so as not to block the calling thread while waiting for the removal command to be ready.
				(new Thread(()->{
					// Block until the removal cmd is ready
					try {
						logger.log(LogLevel.DEBUG, "[On new thread] Retrieving tab removal command from blocking queue...");
						Runnable removeTabCmd = bq.take();
						logger.log(LogLevel.DEBUG, "Retrieving tab removal command acquired and being run.");
						removeTabCmd.run();
						cmd = removeTabCmd; // Save the removal command to be used next time.
					} catch (Exception e) {
						logger.log(LogLevel.ERROR, "Exception while waiting for tab removal command to be ready.  No action taken. Exception: "+e);
						e.printStackTrace();
					} 
				})).start();				
			};  
			
			/**
			 * Removes the installed tab. The calling thread is never blocked.
			 */
			public void run() {
				cmd.run(); // Delegate to the "state" of the command.
			}
		};
		
	}

	/**
	 * Accessor for the internal JTabbedPane.  
	 * Not used in most usage scenarios.
	 * Only used when more customized control of the tabbed pane is desired.
	 * @return The internal JTabbedPane instance.
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}
}

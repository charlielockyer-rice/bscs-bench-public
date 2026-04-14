package provided.utils.view;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import java.awt.BorderLayout;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

/**
 * A panel that enables new components to be added as a vertical list.
 * Note: This panel does NOT include scroll bars! For scroll bars, put this 
 * panel into a JScrollPane.
 * If the parent of this component recognizes that this instance is an IScrollRequestor,
 * then the setScrollRequest() method will be called by the parent to enable automatic 
 * scrolling to the bottom of the scroll range whenever a new component is added.
 * By default, auto scrolling is enabled but can be disabled.
 * @author swong
 */
public class VerticalListPanel extends JPanel implements IScrollRequestor {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 5168191996461717724L;
	
	/**
	 * The default automatic revalidation interval
	 */
	private static final int DEFAULT_REVALIDATE_INTERVAL = 50; // millisec
	
	/**
	 * Used to automatically keep the display properly laid out as the components on it change.
	 */
	private Timer revalidateTimer;

	/**
	 * The logger to use
	 */
	private ILogger logger; 
	
	/**
	 * Strategy to request that the parent of this component scroll the display of this component to
	 * one end of its scroll range.
	 */
	private Consumer<IScrollRequestor.ScrollDir> scrollRequest = (scrollDir)->{
		logger.log(LogLevel.INFO, "No scroll request functionality installed.");
	};
	
	/**
	 * If true, will invoke the scrollRequest strategy when adding a new component.
	 */
	private boolean isAutoScrollEnabled = true;

	/**
	 * Constructor for the class with the default automatic revalidation interval 
	 * and the shared logger from ILoggerControl.
	 */
	public VerticalListPanel() {
		this(ILoggerControl.getSharedLogger(), DEFAULT_REVALIDATE_INTERVAL); 
	}
	
	/**
	 * Constructor for the class with the default automatic revalidation interval 
	 * @param logger The logger to use
	 */
	public VerticalListPanel(ILogger logger) {
		this(logger, DEFAULT_REVALIDATE_INTERVAL); 
	}
	
	/**
	 * Constructor for the class with a custom automatic revalidation interval.
	 * @param logger The logger to use
	 * @param revalidateInterval  Auto-revalidate interval in milliseconds.  revalidateInterval <= 0 means don't auto-revalidate. 
	 */
	public VerticalListPanel(ILogger logger, int revalidateInterval) {
		this.logger = logger;
		initGUI();
		if (0<revalidateInterval) {
			revalidateTimer = new Timer(50, (e) -> {
				revalidate();
			});
			revalidateTimer.start();
		}
	}
	
	/**
	 * Initialize the component's GUI.
	 */
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS)); //BoxLayout.Y_AXIS)); 
//		setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
		add(Box.createVerticalGlue());
	}
	
	/**
	 * Add the component instantiated by the given factory to the bottom of the displayed list of components.
	 * The added component will be added with the given title as a titled border of an enclosing panel.
	 * This method guarantees that the factory is run on the GUI thread.  
	 * @param title The title identifying the component to be added
	 * @param fac A factory that will instantiate the a component.
	 * @return A Runnable that will remove installed component.
	 */
	public Runnable addComponentFac(String title, Supplier<JComponent> fac){
		final BlockingQueue<Runnable> bq = new ArrayBlockingQueue<Runnable>(1); // Enables passing the removal func from the GUI thread
		
		// Make a command that can be dispatched over to the GUI thread if necessary.
		Runnable cmd = ()-> {
			
			// A containing panel helps decouple the width of the added component from the parent component.
			JPanel panel = new JPanel(new BorderLayout());  
			panel.setBorder(new TitledBorder(title));
			logger.log(LogLevel.DEBUG, "Running component factory...");
			JComponent newComponent = fac.get();
			panel.add(newComponent, BorderLayout.CENTER); // Floats the added component to the center of the containing panel.
			this.add(panel);  // Add the new panel to the bottom of the displayed list of components
			this.revalidate();  // seems to work better than validate()
			this.repaint();	
			
			if(this.isAutoScrollEnabled) {	// If enabled, request that the parent scroll to the bottom of this component
				scrollRequest.accept(IScrollRequestor.ScrollDir.BOTTOM);
			}

			
			// Make a State Design pattern command/lambda to remove the component and offer it to the blocking queue.
			bq.offer(new Runnable() {
				
				/*
				 * A full anonymous inner class is used rather than a lambda expression for the outer Runnable 
				 * so that an internal field could be defined.
				 */
				
				/**
				 * Internal command that removes the panel holding the component the first time that it is run 
				 * but gives error after that.
				 * Represents the "state" of the command, i.e. either the panel yet to be removed 
				 * or has already been removed.
				 * The component itself is not destroyed, only removed from its VerticalListPanel host.
				 */
				Runnable cmd = () ->{
					// Need to remove the component on the GUI thread.
					SwingUtilities.invokeLater(()->{
						VerticalListPanel.this.remove(panel);
						VerticalListPanel.this.revalidate();  // seems to work better than validate()
						VerticalListPanel.this.repaint();	
						logger.log(LogLevel.DEBUG, "Scrolled component, "+title+", removed.");
						// Replace cmd (i.e. change the lambda's state) to give error if attempt to remove tab again.
						cmd  = () ->{
							logger.log(LogLevel.CRITICAL, "Attempted to remove already deleted component: "+title);
						};
					});
				};
				
				@Override
				public void run() {
					cmd.run(); // remove the tab if it hasn't been removed already.
				}
			});
			 
			logger.log(LogLevel.DEBUG, "Scrolling component added.");
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
		
		// Return a State Design Pattern lambda that will remove the tab holding the new component.  
		// Returns a command without blocking the calling thread to help avoid threading issues in the caller.
		return new Runnable() {
			/*
			 * A full anonymous inner class is used rather than a lambda expression for the outer Runnable 
			 * so that an internal field could be defined.
			 */
			
			/**
			 * Get the component removal cmd and run it.  Replaced with the removal cmd once acquired.
			 * This command represents the "state" of the command, i.e. remove function not attained yet 
			 * or already attained.
			 */
			Runnable cmd = () -> {
				// Start a new thread so as not to block the calling thread while waiting for the removal command to be ready.
				(new Thread(()->{
					// Block until the removal cmd is ready
					try {
						logger.log(LogLevel.DEBUG, "[On new thread] Retrieving tab removal command from blocking queue...");
						Runnable removeCompCmd = bq.take();
						logger.log(LogLevel.DEBUG, "Retrieving component removal command acquired and being run.");
						removeCompCmd.run();
						cmd = removeCompCmd; // Change the state of this lambda so that the removal command to be used next time.
					} catch (Exception e) {
						logger.log(LogLevel.ERROR, "Exception while waiting for component removal command to be ready.  No action taken. Exception: "+e);
						e.printStackTrace();
					} 
				})).start();				
			};  
			
			/**
			 * Removes the installed tab. The calling thread is never blocked.
			 */
			public void run() {
				cmd.run();  // Delegate to the "state" of this lambda.
			}
		};		
	}
	
	/**
	 * Enable or disable the automatic scrolling request to scroll to 
	 * the bottom of the scrolling range whenever a new component is added.
	 * @param isAutoScrollEnabled  If true, auto-scrolling will be enabled.
	 */
	public void setAutoScrollEnabled(boolean isAutoScrollEnabled) {
		this.isAutoScrollEnabled = isAutoScrollEnabled;
	}
	
	/**
	 * Get the current auto-scroll requesting enabled status.
	 * @return True if auto-scroll requesting is currently enabled, false otherwise.
	 */
	public boolean getAutoScrollEnabled() {
		return this.isAutoScrollEnabled;
	}

	@Override
	public void setScrollRequest(Consumer<ScrollDir> scrollReq) {
		this.scrollRequest = scrollReq;
	}

}

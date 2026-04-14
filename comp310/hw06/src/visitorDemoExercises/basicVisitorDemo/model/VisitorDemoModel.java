package visitorDemoExercises.basicVisitorDemo.model;

import java.awt.Graphics;
import java.util.function.Consumer;

import provided.basicVisitorFW.HostA;
import provided.basicVisitorFW.HostB;
import provided.basicVisitorFW.HostC;
import provided.basicVisitorFW.IHost;
import provided.basicVisitorFW.IVisitor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoaderPath;

/**
 * The main model object for the Visitor Demo system.
 * @author swong
 *
 */
public class VisitorDemoModel {
	/**
	 * logger to use
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * The adapter out to the view
	 */
	private IViewAdapter view;
	
	/**
	 * The installed painting command instance.   Initially set to a no-op cmd.
	 */
	private Consumer<Graphics> paintCmd = (g) -> {};
	
	/**
	 * The dynamic class loader for the IVisitors.
	 */
	private IObjectLoader<IVisitor> loader = new ObjectLoaderPath<IVisitor>((params)-> {
			logger.log(LogLevel.ERROR, "Unable to load the given class: Returning an error visitor!");
			return new IVisitor() {

				@Override
				public Object caseHostA(HostA host, Object... params) {

					return "Error return for Host A";
				}

				@Override
				public Object caseHostB(HostB host, Object... params) {
					return "Error return for Host B";
				}

				@Override
				public Object caseHostC(HostC host, Object... params) {
					return "Error return for Host C";
				}
				
			};
		},
		"provided.basicVisitorFW.visitors.",  // The path to the provided visitors.  Do NOT change this.
		"visitorDemoExercises.basicVisitorDemo.model.visitors."  // THE LOCATION OF THE STUDENT-WRITTEN VISITORS!
	);
	
	/**
	 * The constructor for the class
	 * @param view the adapter to the view
	 */
	public VisitorDemoModel(IViewAdapter view) {
		this.view = view;
	}

	/**
	 * Get an array of the available host types, i.e. IHost subclasses.
	 * @return  An array of IHost objects
	 */
	public IHost[] getHosts() {
		return new IHost[]{new HostA(), new HostB(), new HostC()};
	}

	/**
	 * Return the results of having the given host object execute the visitor corresponding to the given class name.  A reference 
	 * to this model object is given as the visitor's input parameter.
	 * @param host  An IHost object
	 * @param visitorClassname The name of the visitor class, without the leading "visitors.impl."
	 * @param input_param The input parameter from a text field for the visitor to take.
	 * @return  The results of the visitor execution on the given host.
	 */
	public Object run(IHost host, String visitorClassname, String input_param) {
		return  host.execute(loadVisitor(visitorClassname), new Consumer<Consumer<Graphics>>() {

			@Override
			public void accept(Consumer<Graphics> fn) {
				paintCmd = fn;
				view.repaint();
			}
			
		}, input_param);
	}


	/**
	 * Utility method to load a visitor given its classname, without the leading "visitors.impl.".
	 * @param classname  the visitor's classname
	 * @return An IVisitor object
	 */
	private IVisitor loadVisitor(String classname){
		return loader.loadInstance(classname);
	}
	

	/**
	 * Paint on the given Graphics object using the current IPaintCmd object in the model.
	 * @param g  The Graphics context on which to draw.
	 */
	public void paint(Graphics g) {
		paintCmd.accept(g);
	}

}

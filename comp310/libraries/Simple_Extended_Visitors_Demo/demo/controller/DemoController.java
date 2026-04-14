package provided.simpleExtVisitorsDemo.demo.controller;


import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import provided.simpleExtVisitorsDemo.demo.model.DemoModel;
import provided.simpleExtVisitorsDemo.demo.model.IViewAdapter;
import provided.simpleExtVisitorsDemo.demo.view.DemoFrame;
import provided.simpleExtVisitorsDemo.demo.view.IModelAdapter;
import provided.simpleExtVisitorsDemo.extVisitor.*;

/**
 * Controller for the demo app
 * @author swong
 *
 */
public class DemoController {
	
	/**
	 * The view in use
	 */
	private DemoFrame<IHost, ExtVisitor> view;
	
	/**
	 * The model in use
	 */
	private DemoModel model;
	
	/**
	 * No parameter constructor, used when demo is run as an applet.
	 * Simply calls the other constructor with closeAction = WindowConstants.HIDE_ON_CLOSE
	 */
	public DemoController() {
		this(WindowConstants.HIDE_ON_CLOSE);
	}
	
	/**
	 * Constructor for the class
	 * @param closeAction  The action taken when the view frame is closed.  WindowConstants.HIDE_ON_CLOSE for applets and WindowConstants.EXIT_ON_CLOSE for applications. 
	 */
	public DemoController(int closeAction){
		view = new DemoFrame<IHost, ExtVisitor>(closeAction, new IModelAdapter<IHost, ExtVisitor>() {

			@Override
			public IHost makeHost(String id) {
				return model.makeHost(id);
			}

			@Override
			public Object run(IHost host, ExtVisitor visitor) {
				return model.run(host, visitor);
			}

			@Override
			public ExtVisitor makeVisitor(String name) {
				return model.makeVisitor(name);
			}

			@Override
			public void addCmd(ExtVisitor visitor, String id, String resultStr) {
				model.addCmd(visitor, id, resultStr);
			}
			
		});	
		
		model = new DemoModel(new IViewAdapter() {

			@Override
			public void appendMsg(String msg) {
				view.appendMsg(msg);
			}
			
		});
	}
	
	/**
	 * Start the app
	 */
	public void start(){
		model.start();
		view.start();	
	}
	
	/**
	* Main method to run the app
	 * @param args Not used.
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new DemoController(WindowConstants.EXIT_ON_CLOSE)).start();
			}
		});
	}

}

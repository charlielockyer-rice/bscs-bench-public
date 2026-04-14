package visitorDemoExercises.listFWVisitorExercises.controller;

import java.awt.EventQueue;

import javax.swing.WindowConstants;

import provided.listFW.IList;
import visitorDemoExercises.listFWVisitorExercises.model.*;
import visitorDemoExercises.listFWVisitorExercises.view.*;



/**
 * Controller for the ListFW demo app
 * @author swong
 *
 */
public class ListDemoApp {

	/**
	 * The view in use
	 */
	private ListDemoFrame<IList> view;
	/**
	 * The model in use
	 */
	private ListDemoModel model;

	/**
	 * No parameter constructor for use with applets.   Sets window closing behavior to WindowConstants.HIDE_ON_CLOSE
	 */
	public ListDemoApp() {
		this(WindowConstants.HIDE_ON_CLOSE);
	}

	/**
	 * Constructor for the class
	 * @param closeAction Window closing behavior
	 */
	public ListDemoApp(int closeAction) {
		view = new ListDemoFrame<IList>(new IModelAdapter<IList>() {

			@Override
			public String runAlgo(IList host, String classname, String param) {

				return model.runListAlgo(host, classname, param);
			}

			@Override
			public String runFoldR(IList host, String accClassname, String param) {
				return model.runFoldR(host, accClassname, param);   // added param for solution
			}

			@Override
			public String runFoldL(IList host, String accClassname, String param) {
				return model.runFoldL(host, accClassname, param); // add param for solution
			}
		}, closeAction);

		model = new ListDemoModel(new IViewAdapter() {

			@Override
			public void setLists(IList... lists) {
				view.setHosts(lists);
			}

		});
	}

	/**
	 * Start the application
	 */
	public void start() {
		model.start();
		view.start();
	}

	/**
	 * Launch the application.   Sets the window closing behavior to WindowConstants.EXIT_ON_CLOSE
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					(new ListDemoApp(WindowConstants.EXIT_ON_CLOSE)).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

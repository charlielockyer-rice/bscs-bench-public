package provided.rmiUtils.monitor.controller;

import java.awt.EventQueue;
import java.util.List;

import provided.rmiUtils.monitor.model.MainModel;
import provided.rmiUtils.monitor.model.IModel2ViewAdapter;
import provided.rmiUtils.monitor.view.MainView;
import provided.rmiUtils.monitor.view.IView2ModelAdapter;
import provided.utils.struct.IDyad;

/**
 * The main controller for the app
 * @author swong
 *
 */
public class App {

	/**
	 * The app's view
	 */
	private MainView view;
	
	/**
	 * The app's model
	 */
	private MainModel model;
	
	
	/**
	 * Constructor for the class
	 */
	public App() {
		view = new MainView(new IView2ModelAdapter() {

			@Override
			public void unbind(String name) {
				model.unbind(name);
				
			}});
		model = new MainModel(new IModel2ViewAdapter() {
			@Override
			public void showItems(List<IDyad<String, Object>> items) {
				view.showItems(items);
			}
		});
	}
	
	/**
	 * Start the app
	 */
	public void start() {
		view.start();
		model.start();
	}
	/**
	 * Launch the application.
	 * @param args Not used
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App app = new App();
					app.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}

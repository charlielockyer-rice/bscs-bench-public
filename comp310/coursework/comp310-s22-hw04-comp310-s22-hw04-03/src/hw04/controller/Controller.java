package hw04.controller;

import java.awt.EventQueue;
import java.awt.Graphics;

import java.awt.Container;
import hw04.view.*;
import hw04.model.*;
import provided.utils.displayModel.IDimension;

/**
 * MVC Controller for the system
 * The Controller is the class that has the main() method.  
 * The job of the main() method is to instantiate the Controller object.
 * The constructor of the controller will instantiate the model and the view and start them once they are fully constructed.
 */
public class Controller {

	// Fields for the adapters to close over:
	/**
	 * Create the ball model
	 */
	private BallModel model;  // starts off null but will be fine when the constructor is finished.
	
	/**
	 * Create the ball view
	 */
	private BallView<IBallAlgo> view;  // starts off null but will be fine when the constructor is finished.
	
	/**
	 * Controller constructor builds the system
	 */
	public Controller() {
	
		// Here the model is shown being constructed first then the view but it could easily be the other way around if needs dictated it. 
	
//		 TODO update the constructor so it works now 
		this.model = new BallModel(new IModel2ViewAdapter() {
			
			@Override
			public void update() {
				view.update();
			}
			
			@Override
			public IDimension getCanvasDim() {
				return new IDimension() {
					@Override
					public int getWidth() {
						return view.getCanvasWidth();
					}
					
					public int getHeight() {
						return view.getCanvasHeight();
					}
				};
			};
		}, new IBall2ModelAdapter() {
			
			@Override
			public Container getCanvas() {
				return view.getCanvas();
			}
		});
		
		this.view = new BallView<IBallAlgo>(new IView2ModelAdapter<IBallAlgo>() {
			public IBallAlgo addUpdateStrategy(String classname) {
				return model.makeUpdateStrategyAlgo(classname);
			}
			
			public IBallAlgo addPaintStrategy(String classname) {
				return model.makePaintStrategyFac(classname);
			}
			
			@Override
			public void loadBall(IBallAlgo algo) {
				model.loadBall(algo);
			}
			
			@Override
			public void clearAll() {
				model.clearBalls();
			}
			
			@Override
			public void paint(Graphics g) {
				model.update(g);
			}
			
			@Override
			public IBallAlgo combine(IBallAlgo algo1, IBallAlgo algo2) {
				return model.combineAlgos(algo1, algo2);
			}
			
			public void makeSwitcherBall(IBallAlgo algo) {
				model.loadBall(model.getSwitcherInstallAlgo());
			}
			
			public void switchSwitcher(IBallAlgo algo) {
				model.switchSwitcherStrategy(algo);
			}
		});

		
		
	}
	       

	
	/**
	 * Start the system. 
	 */
	public void start() {
		view.start();
		model.start();  // It is usually better to start the model first but not always.
			
	}
	
	/**
	 * Launch the application. Purpose is to instantiate controller object. It will start view and model.
	 * @param args Arguments given by the system or command line.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {   // Java specs say that the system must be constructed on the GUI event thread.
			public void run() {
				try {
					Controller controller = new Controller();   // instantiate the system
					controller.start();  // start the system
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
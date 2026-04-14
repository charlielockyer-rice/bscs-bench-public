package hw03.controller;

import java.awt.EventQueue;
import java.awt.Graphics;

import hw03.view.*;
import hw03.model.*;
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
	private BallView<IStrategyFac> view;  // starts off null but will be fine when the constructor is finished.
	
	/**
	 * Controller constructor builds the system
	 */
	public Controller() {
	
		// Here the model is shown being constructed first then the view but it could easily be the other way around if needs dictated it. 
	
//		 TODO update the constructor so it works now 
		model = new BallModel( new IViewUpdateAdapter() {

			@Override
			public void repaint() {
				view.update();	
			} 

		}, new IViewControlAdapter() {

			@Override
			public IDimension getCanvasDim() {
			    return new IDimension() {
			        public int getWidth() {
					    return view.getCanvas().getWidth();
					}
					
					public int getHeight() {
					    return view.getCanvas().getHeight();
					}
				};
			}
			
		}); 

		
		view = new BallView<IStrategyFac>(new IModelControlAdapter<IStrategyFac> () {  
	        	        
	        @Override
	        /**
	        * Returns an IStrategyFac that can instantiate the strategy specified
	        * by class name. Returns null if class name is null. The toString() of
	        * the returned factory is the class name.
	        * @param strategyName  Shortened name of desired strategy 
	        * @return A factory to make that strategy
	        */
	       public IStrategyFac addStrategy(String strategyName) {
	           return model.makeStrategyFac(strategyName);
	       }

	       @Override
	       /**
	        * Add a ball to the system with a strategy as given by the given IStrategyFac
	        * @param selectedItem The fully qualified name of the desired strategy.
	        */
	       public void makeBall(IStrategyFac selectedItem) {
	           if (null != selectedItem)
	               model.loadBall(selectedItem.make());  
	       }

	       @Override
	       /**
	        * Returns an IStrategyFac that can instantiate a MultiStrategy with the
	        * two strategies made by the two given IStrategyFac objects. Returns
	        * null if either supplied factory is null. The toString() of the
	        * returned factory is the toString()'s of the two given factories,
	        * concatenated with "-".             * 
	        * @param selectedItem1 An IStrategyFac for a strategy
	        * @param selectedItem2 An IStrategyFac for a strategy
	        * @return An IStrategyFac for the composition of the two strategies (Controller decides)
	        */
	       public IStrategyFac combineStrategies(IStrategyFac selectedItem1, IStrategyFac selectedItem2) {
	           return model.combineStrategyFacs(selectedItem1, selectedItem2);
	       }
	       
	       public void clearBalls() {
	    	   model.clearBalls();
	       }
//
		public void makeSwitcherBall(String strategy) {
			if (null != strategy) {
				model.loadBall(model.getSwitcher()); 
			}
			
		}

		public void switchStrategy(IStrategyFac factory) {
			model.setSwitcher(factory.make());
		}
	       
	   }, 			
				
	   new IModelUpdateAdapter<IStrategyFac> () {
	       /**
	       * Pass the update request to the model.
	       * @param g The Graphics object the balls use to draw themselves.
	       */
	       public void update(Graphics g) {
	           model.update(g);
	       }
	   });
	}
	       

	
	/**
	 * Start the system. 
	 */
	public void start() {
		model.start();  // It is usually better to start the model first but not always.
		view.start();	
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
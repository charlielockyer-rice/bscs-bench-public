package hw03.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.Timer;

import hw03.model.strategy.SwitcherStrategy;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;
import provided.utils.displayModel.IDimension;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoader;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * @author Christina
 * Ball model represents the back end representation for how we will observe a model.
 */
public class BallModel {
	
	/**
	 * Create a switcher strategy
	 */
	private SwitcherStrategy switcher = new SwitcherStrategy();
	
	/**
	 * Create a view update adapter
	 */
	private IViewUpdateAdapter _updateAdpt = IViewUpdateAdapter.NULL_OBJECT;
	
	/**
	 * Create a control adapter
	 */
	private IViewControlAdapter _controlAdpt = IViewControlAdapter.NULL_OBJECT;
	
	// TODO IstrategyFac
	/**
	 * Returns an IStrategyFac that can instantiate the strategy specified by
	 * classname. Returns a factory for a beeping error strategy if classname is null. 
	 * The toString() of the returned factory is the classname.
	 * 
	 * @param classname  Shortened name of desired strategy
	 * @return A factory to make that strategy
	 */
	
	/**
	 * @param updateAdpt takes a view update adapter
	 * @param controlAdpt takes a control update adapter
	 */
	public BallModel(IViewUpdateAdapter updateAdpt, IViewControlAdapter controlAdpt) {
		_updateAdpt = updateAdpt;
		_controlAdpt = controlAdpt;
	}
	
	/**
	 * timer slice so updates every 50 ms for the adapter
	 */
	private int _timeSlice = 50;  
	/**
	 * Timer object to update the canvas
	 */
	private Timer _timer = new Timer(_timeSlice,  (e) -> _updateAdpt.repaint());
	
	

	/**
	 * the Ball model start method
	 */
	public void start() {
		_timer.start();
	}
	
	/**
	 * Get the view canvas dimensions
	 */
	IDimension ballCanvasDim = _controlAdpt.getCanvasDim(); // ballCanvasDim is used by the balls to determine their bouncing behavior.

	/**
	 * Create a dispatcher to send updates to observer
	 */
	IDispatcher<Graphics> myDispatcher = new SequentialDispatcher<Graphics>();
	
	/**
	 * @param strategy is the strategy to load for the ball
	 */
	public void loadBall(IUpdateStrategy strategy) {
		//TODO: Move all the GUI-related code to the view
		IRandomizer rand = Randomizer.Singleton;
		int radius = rand.randomInt(5, 30);
		Color color = rand.randomColor();
		int xLoc = rand.randomInt(0, _controlAdpt.getCanvasDim().getWidth()); // subtract 30 so doesn't get stuck
		int yLoc = rand.randomInt(0, _controlAdpt.getCanvasDim().getHeight());
		int xVelocity = rand.randomInt(-10, 10);
		int yVelocity = rand.randomInt(-10, 10);
		
		Ball newBall = new Ball(new Point(xLoc, yLoc), new Point(xVelocity, yVelocity), color, radius, _controlAdpt.getCanvasDim(), strategy);
		myDispatcher.addObserver(newBall);
	}

	
	/**
	 * Returns an IStrategyFac that can instantiate the strategy specified by
	 * class name. Returns a factory for a beeping error strategy if class name is null. 
	 * The toString() of the returned factory is the class name.
	 * 
	 * @param classname  Shortened name of desired strategy
	 * @return A factory to make that strategy
	 */
	public IStrategyFac makeStrategyFac(final String classname) {
	    if (null == classname) return IStrategyFac.errorStrategyFac;
	    return new IStrategyFac() {
	        /**
	         * Instantiate a strategy corresponding to the given class name.
	         * @return An IUpdateStrategy instance
	         */
	        public IUpdateStrategy make() {
	            return makeStrategy("hw03.model.strategy." + classname + "Strategy");
	        }

	        /**
	         * Return the given class name string
	         */
	        public String toString() {
	            return classname;
	        }
	    };
	}
	
	/**
	 * Method to create new ball from the model side
	 * @param strategy input ball class name
	 * @return a strategy object
	 */
	public IUpdateStrategy makeStrategy(String strategy) {		
		
		IObjectLoader<IUpdateStrategy> loader = new ObjectLoader<IUpdateStrategy>(arg -> IUpdateStrategy.errorStrategy);
		IUpdateStrategy newStrategy = loader.loadInstance(strategy);
		return newStrategy;
		
	}
	
	/**
	 * Returns an IStrategyFac that can instantiate a MultiStrategy with the two
	 * strategies made by the two given IStrategyFac objects. Returns null if
	 * either supplied factory is null. The toString() of the returned factory
	 * is the toString()'s of the two given factories, concatenated with "-". 
	 * If either factory is null, then a factory for a beeping error strategy is returned.
	 * 
	 * @param stratFac1 An IStrategyFac for a strategy
	 * @param stratFac2 An IStrategyFac for a strategy
	 * @return An IStrategyFac for the composition of the two strategies
	 */
	public IStrategyFac combineStrategyFacs(final IStrategyFac stratFac1, final IStrategyFac stratFac2) {
	    if (null == stratFac1 || null == stratFac2) return IStrategyFac.errorStrategyFac;
	    return new IStrategyFac() {
			@Override
			public IUpdateStrategy make() {
				
				return new IUpdateStrategy() {
				
		    	IUpdateStrategy strategy1 = stratFac1.make();
		    	IUpdateStrategy strategy2 = stratFac2.make();
				// TODO Auto-generated method stub
		    	
		    	@Override
				public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		    		strategy1.updateState(disp, ball);
		    		strategy2.updateState(disp, ball);
		    	}};
		    	}
			
			public String toString() {
				return stratFac1.toString() + "-" + stratFac2.toString();
			}
	    };}

    /**
     * This is the method that is called by the view's adapter to the model, i.e. is called by IView2ModelAdapter.paint().
     * This method will update the balls painted locations by painting all the balls
     * onto the given Graphics object.
     * @param g The Graphics object from the view's paintComponent() call.
     */
    public void update(Graphics g) {
        myDispatcher.updateAll(g);  // The Graphics object is being given to all the balls (Observers) - used to update painted positions of balls
    }
    
    /**
     * Use the dispatcher to clear all the balls from the field. view to model
     */
    public void clearBalls() {
    	myDispatcher.removeAllObservers();
    }
    
    /**
     * @param strategy an inputed IUpdateStrategy
     */
    public void setSwitcher(IUpdateStrategy strategy) {
    	this.switcher.switchStrategy(strategy);
    }
    
    /**
     * @return new IUpdateStrategy to get the switcher
     */
    public SwitcherStrategy getSwitcher() {
    	return this.switcher;
    }

}

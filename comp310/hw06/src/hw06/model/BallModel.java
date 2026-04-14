package hw06.model;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.Timer;

import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.IBall;
import hw06.model.BallVisitors.ABallAlgoCmd;
import hw06.model.BallVisitors.AConfigBallAlgo;
import hw06.model.BallVisitors.IBallAlgo;
import hw06.model.interactStrategy.IInteractStrategy;
import hw06.model.interactStrategy.MultiInteractStrategy;
import hw06.model.paint.IPaintStrategy;
import hw06.model.paint.strategy.BallPaintStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import hw06.model.updateStrategy.SwitcherUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.dispatcher.impl.SequentialDispatcher;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoader;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * The model that runs the underlying mechanism of the ball behaviors.
 * @author Cole Rabson and Son Nguyen
 */
public class BallModel {

	/**
	 * A randomizer that help with various random initialization.
	 */
	private IRandomizer rand = Randomizer.Singleton;
	
	/**
	 * Adapter from configuration algorithms (or anything else if needed) to the model
	 */
	private IBallAlgo2ModelAdapter algo2ModelAdpt = new IBallAlgo2ModelAdapter() {
		@Override
		public void addConfigComponent(String label, Supplier<JComponent> compFac) {
			viewCtrlAdpt.addConfigComponent(label, compFac); // delegate to one of the model's adapters to the view
		}
	};
	
	/**
	 * The logger in use
	 */
	protected ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * The dispatcher to manage all the balls created.
	 */
	private IDispatcher<IBallCmd> myDispatcher = new SequentialDispatcher<IBallCmd>();
	/**
	 * the common switcher strategy given to all switcher balls.
	 */
	private SwitcherUpdateStrategy switcher = new SwitcherUpdateStrategy();

	/**
	 * Initialize as null to ensures that the view control adapter is always valid
	 */
	private IViewCtrlAdapter viewCtrlAdpt = IViewCtrlAdapter.NULL_OBJECT;

	/**
	 * Initialize as null to ensures that the view update adapter is always valid
	 */
	private IViewUpdateAdapter viewUpdateAdpt = IViewUpdateAdapter.NULL_OBJECT;

	/**
	 * update every 50 milliseconds.
	 */
	private final int timeSlice = 50;

	/**
	 * The timer to repaint every timeSlice milliseconds.
	 */
	private Timer timer = new Timer(timeSlice, (e) -> this.viewUpdateAdpt.update());

	/**
	 * 
	 */
	private String className;

	/**
	 * Constructor for the BallModel.
	 * @param viewCtrlAdpt The view control adapter for ball model.
	 * @param viewUpdateAdpt The view update adapter for ball model.
	 */
	public BallModel(IViewCtrlAdapter viewCtrlAdpt, IViewUpdateAdapter viewUpdateAdpt) {
		this.viewCtrlAdpt = viewCtrlAdpt;
		this.viewUpdateAdpt = viewUpdateAdpt;
	}

	/**
	 * Start the timer.
	 */
	public void start() {
		this.timer.start();
	}

	/**
	 * Set the sprites painting and update strategy base on the given command.
	 * onto the given Graphics object.
	 * @param cmd The command given to the ball to do update and paint.
	 */
	public void update(IBallCmd cmd) {
		myDispatcher.updateAll(cmd); // The Graphics object is being given to all the sprites (Observers)
	}

	/**
	 * Make a ball from the provided strategy
	 * @param algo The Strategy that the new ball would have.
	 * @param classname The classname of the ball to be created
	 */
	public void makeBall(IBallAlgo<Void, Void> algo, String classname) {	
		//Randomizing the initialization
		this.className = "hw06.model.BallHosts."+classname+"Ball";
		int canvas_height = viewCtrlAdpt.getCanvas().getHeight();
		int canvas_width = viewCtrlAdpt.getCanvas().getWidth();
		int radius = rand.randomInt(30, Math.min(Math.min(canvas_height / 4, canvas_width / 4),50));
		Point center = rand.randomLoc(new Rectangle(radius * 5 / 4, radius * 5 / 4, canvas_width - radius * 5 / 2,
				canvas_height - radius * 5 / 2));
		Point velocity = rand.randomVel(new Rectangle(20, 20));
		Color color = rand.randomColor();
		//Make the ball on screen by a maker Object loader.
		IObjectLoader<IObserver<IBallCmd>> maker = new ObjectLoader<IObserver<IBallCmd>>((java.util.function.Function<Object[], IObserver<IBallCmd>>) null);
		IObserver<IBallCmd> ball = maker.loadInstance(this.className, center, radius, velocity, color,
				viewCtrlAdpt.getCanvas(), algo);
		myDispatcher.addObserver(ball);
	}
	
	/**
	 * Remove all the balls off of the Center Panel
	 */
	public void clearBalls() {
		myDispatcher.removeAllObservers();
	}

	/**
	 * Returns an IBallAlgo that can instantiate the strategy specified by
	 * classname and install it into the host ball by composing it with any 
	 * existing update strategy in the ball. 
	 * Installs an error strategy if classname is null or other error occurs 
	 * during the loading process. 
	 * The toString() of the returned algorithm is the given classname.
	 * 
	 * @param classname  Shortened name of desired update strategy
	 * @return An algorithm to install the specified strategy
	 */
	public IBallAlgo<Void, Void> makeUpdateStrategyAlgo(final String classname) {
		return new AConfigBallAlgo(logger, classname, algo2ModelAdpt, new ABallAlgoCmd<Void, Void>() {
			
		    /**
			 * Auto-generate the serialVersionUID field and its unique value 
			 */
			private static final long serialVersionUID = 1L;				
			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				host.setUpdateStrategy(
						new MultiUpdateStrategy(host.getUpdateStrategy(), loadUpdateStrategy(classname)));
				return null; // Must return null
			}

		}) {
			/**
			 * Auto-generate the serialVersionUID field and its unique value 
			 */
			private static final long serialVersionUID = 1L;
			
		};
				
				
//				new IBallAlgo() {
//
//			public void caseDefault(IBall host) {
//				// Create composite with existing strategy.  
//				// A named composite class is used here but an anonymous inner class would work too.
//				// loadUpdateStrategy() expands the shortened name and uses an IObjectLoader to load it.
//
//				// Should be different to deal with composites? look at example code where it is MultiUpdateStrategy.
//				host.setUpdateStrategy(
//						new MultiUpdateStrategy(host.getUpdateStrategy(), loadUpdateStrategy(classname)));
//			}
//
//			/**
//			 * Return the given class name string
//			 */
//			public String toString() {
//				return classname;
//			}
//
//		};
	}

	/**
	 * @param classname -> the name of the strategy class to be loaded. 
	 * @return updateStrategy -> the strategy contained by the class in classname.
	 */
	private IUpdateStrategy loadUpdateStrategy(final String classname) {
		String className = "hw06.model.updateStrategy." + classname + "UpdateStrategy";
		//Make the ball on screen by a maker Object loader.
		IObjectLoader<IUpdateStrategy> maker = new ObjectLoader<IUpdateStrategy>((params) -> IUpdateStrategy.ERROR);
		IUpdateStrategy updateStrategy = maker.loadInstance(className);
		return updateStrategy;
	}

	/**
	 * Load up a paint strategy from given classname
	 * @param classname the given pain strategy
	 * @return updateStrategy the paint strategy given by classname. 
	 */
	private IPaintStrategy loadPaintStrategy(final String classname) {
		String className = "hw06.model.paint.strategy." + classname + "PaintStrategy";
		//Make the ball on screen by a maker Object loader.
		IObjectLoader<IPaintStrategy> maker = new ObjectLoader<IPaintStrategy>((params) -> IPaintStrategy.ERROR);
		IPaintStrategy paintStrategy = maker.loadInstance(className);
		return paintStrategy;
	}
	
	/**
	 * Load up a paint strategy from given classname
	 * @param classname the given pain strategy
	 * @return updateStrategy the paint strategy given by classname. 
	 */
	private IInteractStrategy loadInteractStrategy(final String classname) {
		String className = "hw06.model.interactStrategy." + classname + "InteractStrategy";
		//Make the ball on screen by a maker Object loader.
		IObjectLoader<IInteractStrategy> maker = new ObjectLoader<IInteractStrategy>((params) -> IInteractStrategy.ERROR);
		IInteractStrategy interactStrategy = maker.loadInstance(className);
		return interactStrategy;
	}

	/**
	 * Returns an IBallAlgo that can install an IPaintStrategy into its host as 
	 * specified by the given classname. 
	 * An error strategy is installed beeping error strategy if classname is null. 
	 * The toString() of the returned algo is the classname.
	 * 
	 * @param classname  Shortened name of desired strategy
	 * @return An algo to install the associated strategy
	 */
	public IBallAlgo<Void, Void> makePaintStrategyAlgo(final String classname) {

		return new AConfigBallAlgo(logger, classname, algo2ModelAdpt, new ABallAlgoCmd<>() {
			
		    // Let Eclipse Quick Fix auto-generate the serialVersionUID field and its unique value 
						
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				host.setPaintStrategy(new IPaintStrategy() {
					IPaintStrategy paintStrat1 = host.getPaintStrategy(); // Save the host's current paint strategy
					IPaintStrategy paintStrat2 = loadPaintStrategy(classname); // Load the new paint strategy and save it.

					@Override
					public void paint(Graphics g, IBall host) {
						// Delegate to each composee
						paintStrat1.paint(g, host);
						paintStrat2.paint(g, host);
					}

					@Override
					public void init(IBall host) {
						// Delegate to each composee
						paintStrat1.init(host);
						paintStrat2.init(host);

					}
				});
				return null;
			};
		}) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		};
		
//		return new IBallAlgo() {
//
//			@Override
//			public void caseDefault(IBall host) {
//				// Want generic composite paint strategy here, not MultiPaintStrategy which is specifically an Affine transform composite.
//				// An anonymous inner class is used here for the composite paint strategy but a named classed could have been used instead.
//				
//			}
//
//			/**
//			 * Return the given class name string
//			 */
//			public String toString() {
//				return classname;
//			}
//
//		};
	}
	
	/**
	 * Returns an IBallAlgo that can install an IInteractStrategy into its host as 
	 * specified by the given classname. 
	 * An error strategy is installed beeping error strategy if classname is null. 
	 * The toString() of the returned algo is the classname.
	 * 
	 * @param classname  Shortened name of desired strategy
	 * @return An algo to install the associated strategy
	 */
	public IBallAlgo<Void, Void> makeInteractStrategyAlgo(final String classname) {
		
		return new AConfigBallAlgo(logger, classname, algo2ModelAdpt, new ABallAlgoCmd<>() {					
			/**
			 * Auto-generate the serialVersionUID field and its unique value
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				host.setInteractStrategy(new MultiInteractStrategy(host.getInteractStrategy(), loadInteractStrategy(classname)));
				return null; // Must return null
			}
		}) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		};
		
		//FIXME: add multiple interaction strategy
//		return new IBallAlgo() {
//			@Override
//			public void caseDefault(IBall host) {
//				host.setInteractStrategy(new MultiInteractStrategy(host.getInteractStrategy(), loadInteractStrategy(classname)));
//			}
//
//			/**
//			 * Return the given class name string
//			 */
//			public String toString() {
//				return classname;
//			}
//
//		};
	}

	/**
	 * Returns a composite IBallAlgo that can instantiate a compois with the two
	 * strategies made by the two given IUpdateStrategyFac objects. Returns null if
	 * either supplied factory is null. The toString() of the returned factory
	 * is the toString()'s of the two given factories, concatenated with "-". 
	 * If either given algo is null, then an algo that installs a beeping error strategy is returned.
	 * 
	 * @param algo1 A ball processing algorithm
	 * @param algo2 Another ball processing algorithm
	 * @return A composition of the two algorithms
	 */
	public IBallAlgo<Void, Void> combineStrategyAlgos(final IBallAlgo<Void, Void>algo1, final IBallAlgo<Void, Void> algo2) {
		
		return new AConfigBallAlgo(logger, algo1.toString() + "-" + algo2.toString(), algo2ModelAdpt, new ABallAlgoCmd<>() {
			
		    // Let Eclipse Quick Fix auto-generate the serialVersionUID field and its unique value 
						
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				host.execute(algo1);
				host.execute(algo2);
				return null; // Must return null
			}
		}) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
		};
		
//		return new IBallAlgo() {
//			/**
//			 * Return a string that is the toString()'s of the given strategy factories concatenated with a "-"
//			 */
//			public String toString() {
//				return algo1.toString() + "-" + algo2.toString();
//			}
//
//			@Override
//			public void caseDefault(IBall host) {
//				host.execute(algo1);
//				host.execute(algo2);
//
//			}
//		};
	}

	/**
	 * An algo to reset all the strategies to null/no-op strategies
	 */
	private IBallAlgo<Void, Void> clearStrategiesAlgo =  new AConfigBallAlgo(logger,"clearStrategiesAlgo", algo2ModelAdpt, new ABallAlgoCmd<>() {
			
		    // Let Eclipse Quick Fix auto-generate the serialVersionUID field and its unique value 
						
			/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				host.setUpdateStrategy(IUpdateStrategy.NULL_OBJECT);
				host.setPaintStrategy(IPaintStrategy.NULL_OBJECT);
				host.setInteractStrategy(IInteractStrategy.NULL_OBJECT);
				return null; // Must return null
			}
	}) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	};
//	IBallAlgo() {	
//		@Override
//		public void caseDefault(IBall host) {
//			host.setUpdateStrategy(IUpdateStrategy.NULL_OBJECT);
//			host.setPaintStrategy(IPaintStrategy.NULL_OBJECT);
//			host.setInteractStrategy(IInteractStrategy.NULL_OBJECT);
//		}

//	};

	/**
	 * @return The Switcher Strategy to be loaded.
	 */
	public SwitcherUpdateStrategy getSwitcherStrategy() {
		return switcher;
	}


	/**
	 * Dummy ball that holds the decoree strategies for the switcher strategies.
	 * A null ball-to-model adapter used initially but in start(), the null adapter is 
	 * replaced with the operational adapter.     
	 */
	private IBall switcherDummyBall = new DefaultBall(null, 0, null, null, null, new AConfigBallAlgo(logger, "DummyBall", algo2ModelAdpt, new ABallAlgoCmd<>() {
		
	    // Let Eclipse Quick Fix auto-generate the serialVersionUID field and its unique value 
					
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Void apply(IBallHostID index, IBall host, Void... params) {
			host.execute(clearStrategiesAlgo); // reset all the strategies to their null objects.
			host.setPaintStrategy(new BallPaintStrategy()); // default the painting to Ball at the beginning
			return null; // Must return null
		}
	}) {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3987052509779151619L;
		
	});
//			new IBallAlgo() {
//	
//		@Override
//		public void caseDefault(IBall host) {
//			host.execute(clearStrategiesAlgo); // reset all the strategies to their null objects.
//			host.setPaintStrategy(new BallPaintStrategy()); // default the painting to Ball at the beginning
//		}
//	});

	/**
	 * The one switcher update strategy instance in the system. Allows all balls made with this strategy to be controlled at once.
	 */
	private IUpdateStrategy switcherUpdateStrategy = new IUpdateStrategy() {

		@Override
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
			// delegate to the strategy in the dummy ball
			switcherDummyBall.getUpdateStrategy().updateState(context, disp);
		}

		@Override
		public void init(IBall context) {
			switcherDummyBall.getUpdateStrategy().init(context);
		}
	};

	/**
	 * The one switcher paint strategy instance in the system. Allows all balls made with this strategy to be controlled at once.
	 */
	private IPaintStrategy switcherPaintStrategy = new IPaintStrategy() {

		@Override
		public void paint(Graphics g, IBall host) {
			// Delegate to the strategy in the dummy ball
			switcherDummyBall.getPaintStrategy().paint(g, host);
		}

		@Override
		public void init(IBall context) {
			switcherDummyBall.getPaintStrategy().init(context);
		}
	};
	
	/**
	 * The one switcher paint strategy instance in the system. Allows all balls made with this strategy to be controlled at once.
	 */
	private IInteractStrategy switcherInteractStrategy = new IInteractStrategy() {

		@Override
		public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
			return switcherDummyBall.getInteractStrategy().interactWithThen(context, other, disp);
		}

		@Override
		public void init(IBall host) {		
			switcherDummyBall.getInteractStrategy().init(host);
		}
	};

	/**
	 * The algo used to install switcher strategies into a host ball.
	 */
	private IBallAlgo<Void, Void> switcherInstallAlgo = new AConfigBallAlgo(logger, "switcherInstallAlgo", algo2ModelAdpt, new ABallAlgoCmd<>() {
		
	    // Let Eclipse Quick Fix auto-generate the serialVersionUID field and its unique value 
					
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Void apply(IBallHostID index, IBall host, Void... params) {
			// Do whatever the default case needs to do.
			host.setUpdateStrategy(switcherUpdateStrategy);
			host.setPaintStrategy(switcherPaintStrategy);
			host.setInteractStrategy(switcherInteractStrategy);
			return null; // Must return null
		}
	}) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	};
//		new IBallAlgo() {
//
//		@Override
//		public void caseDefault(IBall host) {
//			// Want to replace the existing strategies, so don't compose the switcher strategies with the existing ones.
//			// Could have used anonymous inner classes for the switcher strategies being installed here instead of using shared, pre-instantiated objects.
//			host.setUpdateStrategy(switcherUpdateStrategy);
//			host.setPaintStrategy(switcherPaintStrategy);
//			host.setInteractStrategy(switcherInteractStrategy);
//		}
//	};

	/**
	 * Getter for the algorithm to install switcher strategies into a host ball
	 * @return the switcher installation algo
	 */
	public IBallAlgo<Void, Void> getSwitcherInstallAlgo() {
		return this.switcherInstallAlgo;
	}

	/**
	 * Makes a ball with using the switcher configuration algorithm.
	 */
	public void makeSwitcherBall() {
		makeBall(getSwitcherInstallAlgo(), "Default");
	}

	/**
	 * Change the decoree strategies in the dummy ball using the given algorithm
	 * @param decoreeInstallAlgo the algorithm to install new decoree strategies into a ball
	 */
	public void switchSwitcherStrategy(IBallAlgo<Void, Void> decoreeInstallAlgo) {
		switcherDummyBall.execute(clearStrategiesAlgo); // clear the installed strategies b/c the incoming algo will compose with the existing ones.
		switcherDummyBall.execute(decoreeInstallAlgo); // Install the new decoree strategies, which will be composed with the now null/no-op existing strategies in the dummy ball.
	}

	/**
	 * Load up the configuration algo.
	 * @param text the name of the configuration that is to be loaded
	 * @return The configuration algorithm.
	 */
	public IBallAlgo<Void, Void> makeConfigAlgo(String text) {
		text = "hw06.model.BallVisitors." + text + "ConfigAlgo";
		//Make the ball on screen by a maker Object loader.
		IObjectLoader<AConfigBallAlgo> maker = new ObjectLoader<AConfigBallAlgo>((java.util.function.Function<Object[], AConfigBallAlgo>) null);
		AConfigBallAlgo configAlgo = maker.loadInstance(text,logger,algo2ModelAdpt);
		return configAlgo;
	}
}

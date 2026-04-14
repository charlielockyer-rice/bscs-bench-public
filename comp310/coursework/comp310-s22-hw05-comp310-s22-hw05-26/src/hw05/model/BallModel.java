package hw05.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;

import javax.swing.Timer;

import hw05.model.updateStrategies.IUpdateStrategy;
import hw05.model.updateStrategies.MultiStrategy;
import hw05.model.interactStrategies.IInteractStrategy;
import hw05.model.paintStrategies.BallPaintStrategy;
import hw05.model.paintStrategies.IPaintStrategy;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.impl.SequentialDispatcher;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoaderPath;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * @author charlielockyer
 * Ball model represents the back end representation for how we will observe a model.
 */
public class BallModel {
	
	/**
	 * Instantiating the view adapter
	 */
	private IModel2ViewAdapter viewAdapter = IModel2ViewAdapter.NULL;
	/**
	 * Instantiating ball adapter
	 */
	private IBall2ModelAdapter ballAdapter = IBall2ModelAdapter.NULL;
	/**
	 * IUpdateStrategy Loader
	 */
	private IObjectLoader<IUpdateStrategy> loader = new ObjectLoaderPath<IUpdateStrategy>(args -> IUpdateStrategy.NULL);
	/**
	 * IPaintStrategy Loader
	 */
	private IObjectLoader<IPaintStrategy> paintLoader = new ObjectLoaderPath<IPaintStrategy>(
			args -> IPaintStrategy.NULL);
	/**
	 * IInteractStrategy Loader
	 */
	private IObjectLoader<IInteractStrategy> interactLoader = new ObjectLoaderPath<IInteractStrategy>(
			args -> IInteractStrategy.NULL);
	/**
	 * dispatcher of IBallCmd
	 */
	private IDispatcher<IBallCmd> dispatcher = new SequentialDispatcher<IBallCmd>();
	/**
	 * The min and max radius, as well as max speed and velocity for randomly generated balls
	 * Also includes the randomizer instance and the time slice
	 */
	private int maxRadius = 20;
	private int minRadius = 5;
	private int maxSpeed  = 25;
	private Rectangle maxVelocity = new Rectangle(maxSpeed, maxSpeed, maxSpeed, maxSpeed);
	private IRandomizer rand = Randomizer.Singleton;
	private int timeSlice = 50;
	
	/**
	 * Timer action listener for updating
	 */
	private Timer timer = new Timer(timeSlice, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			viewAdapter.update();
		}
	});
	
	/**
	 * A dummy instance of a switcher ball
	 */
	private IBall switcherDummyBall;
	
	/**
	 * Instantiating the single instance of a switcher update strategy
	 */
	private IUpdateStrategy switcherUpdateStrategy = new IUpdateStrategy() {
		@Override
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
			switcherDummyBall.getUpdateStrategy().updateState(context, disp);
		}
		
		@Override
		public void init(IBall context) {
			switcherDummyBall.getUpdateStrategy().init(context);
		}
		
		
	};
	
	/**
	 * Instantiating the single instance of a switcher paint strategy
	 */
	private IPaintStrategy switcherPaintStrategy = new IPaintStrategy() {
		@Override
		public void paint(Graphics g, IBall host) {
			switcherDummyBall.getPaintStrategy().paint(g, host);
			
		}
		
		@Override
		public void init(IBall context) {
			switcherDummyBall.getPaintStrategy().init(context);
		}
	};
	
	private IInteractStrategy switcherInteractStrategy = new IInteractStrategy() {

		@Override
		public IBallCmd interactWith(IBall context, IBall target, IDispatcher disp) {
			// Delegate to the strategy in the dummy ball
			return switcherDummyBall.getInteractStrategy().interactWith(context, target, disp);
		}

		@Override
		public void init(IBall context) {
			switcherDummyBall.getInteractStrategy().init(context);
		}
	};
	
	/**
	 * Used to create the algo we pass the ball when switching
	 */
	private IBallAlgo switcherInstallAlgo = new IBallAlgo() {
		
		@Override
		public void caseDefault(IBall host) {
			host.setUpdateStrategy(switcherUpdateStrategy);
			host.setPaintStrategy(switcherPaintStrategy);
		}
	};
	
	/**
	 * Clears the update and paint strategies of all the balls
	 */
	private IBallAlgo clearStrategiesAlgo = new IBallAlgo() {
		@Override
		public void caseDefault(IBall host) {
			host.setUpdateStrategy(IUpdateStrategy.NULL);
			host.setPaintStrategy(IPaintStrategy.NULL);
			host.setInteractStrategy(IInteractStrategy.NULL);

		}
	};
	
	/**
	 * Constructor for a model that takes a ball adapter and a view adapter
	 * @param viewAdapter adapting between the model and view
	 * @param ballAdapter another segment of the adapter
	 */
	public BallModel(IModel2ViewAdapter viewAdapter, IBall2ModelAdapter ballAdapter) {
		this.viewAdapter = viewAdapter;
		this.ballAdapter = ballAdapter;
	}
	
	/**
	 * Starts everything in the model
	 */
	public void start() {
		this.timer.start();
		
		switcherDummyBall = new Ball(null, null, null, 0, viewAdapter.getCanvasDim(), ballAdapter.getCanvas(),
				new IBallAlgo() {
				
				@Override
				public void caseDefault(IBall host) {
					host.execute(clearStrategiesAlgo);
					host.setPaintStrategy(new BallPaintStrategy());
				}
			
		});
	}
	
	/**
	 * Loads a new ball with random radius and velocity
	 * @param ballAlgo the algo to pass that new ball
	 */
	public void loadBall(IBallAlgo ballAlgo) {
		Point startLocation = new Point(rand.randomInt(0, viewAdapter.getCanvasDim().getWidth()),
				rand.randomInt(0, viewAdapter.getCanvasDim().getHeight()));
		
		Integer startRadius = rand.randomInt(this.minRadius, this.maxRadius);
		
		Point startVelocity = rand.randomVel(maxVelocity);
		
		Color startColor = rand.randomColor();
		
		dispatcher.addObserver(new Ball(startLocation, startVelocity, startColor, startRadius, viewAdapter.getCanvasDim(),
				ballAdapter.getCanvas(), ballAlgo));
		
	}
	
	/**
	 * Clears all the balls on the screen
	 */
	public void clearBalls() {
		dispatcher.removeAllObservers();
	}
	
	/**
	 * Updates the graphics
	 * @param g the graphics object to update
	 */
	public void update(Graphics g) {
		this.dispatcher.updateAll(new IBallCmd() {
			
			@Override
			public void apply(IBall context, IDispatcher<IBallCmd> disp) {
				context.getUpdateStrategy().updateState(context, disp);
				context.getPaintStrategy().paint(g, context);
				context.move();
				context.bounce();
			}
		});
	}
	
	/**
	 * Makes a new update strategy given a name
	 * @param classname the name of the algorithm to make
	 * @return a new algorithm with that name
	 */
	public IBallAlgo makeUpdateStrategyFac(final String classname) {
		return new IBallAlgo() {
			
			@Override
			public void caseDefault(IBall host) {
				
				try {
					Class.forName("hw05.model.updateStrategies." + classname + "Strategy");
					host.setUpdateStrategy(new MultiStrategy(host.getUpdateStrategy(), loader.loadInstance("hw05.model.updateStrategies." + classname + "Strategy")));
					
				} catch(ClassNotFoundException e) {
					host.setUpdateStrategy(IUpdateStrategy.errorStrategy);
				}
			}
			
			public String toString() {
				return classname;
			}
		};
	}
	
	/**
	 * Makes a new update strategy given a name
	 * @param classname the name of the algorithm to make
	 * @return a new algorithm with that name
	 */
	public IBallAlgo makeInteractStrategyFac(final String classname) {
		return new IBallAlgo() {
			
			@Override
			public void caseDefault(IBall host) {
				
				try {
					Class.forName("hw05.model.interactStrategies." + classname + "Strategy");
					host.setInteractStrategy(interactLoader.loadInstance("hw05.model.interactStrategies." + classname + "Strategy"));
					
				} catch(ClassNotFoundException e) {
					host.setUpdateStrategy(IUpdateStrategy.errorStrategy);
				}
			}
			
			public String toString() {
				return classname;
			}
			
		};
	}
	
	/**
	 * Makes a new paint strategy given a name
	 * @param classname the name of the algorithm to make
	 * @return a new algorithm with that name
	 */
	public IBallAlgo makePaintStrategyFac(final String classname) {
		return new IBallAlgo() {
			@Override
			public void caseDefault(IBall host) {
				host.setPaintStrategy(new IPaintStrategy() {
					IPaintStrategy paintStrategy1 = host.getPaintStrategy();
					IPaintStrategy paintStrategy2 = IPaintStrategy.NULL;
				
					@Override
					public void paint(Graphics g, IBall host) {
						paintStrategy1.paint(g, host);
						paintStrategy2.paint(g, host);
					}
					
					@Override
					public void init(IBall host) {
						try {
							Class.forName("hw05.model.paintStrategies." + classname + "PaintStrategy");
							paintStrategy2 = paintLoader.loadInstance("hw05.model.paintStrategies." + classname + "PaintStrategy");
						} catch (ClassNotFoundException e) {
							paintStrategy2 = IPaintStrategy.NULL;
						}
						
						paintStrategy1.init(host);
						paintStrategy2.init(host);
					}
				});
			}
			
			public String toString() {
				return classname;
			}
		};
	}
	
	/**
	 * Combines two algorithms and returns a new one that has both
	 * @param algo1 the first input algorithm
	 * @param algo2 the second input algorithm
	 * @return a new algorithm that combines the two inputs
	 */
	public IBallAlgo combineAlgos(final IBallAlgo algo1, final IBallAlgo algo2) {
		if (algo1 == null || algo2 == null)
			return IBallAlgo.errorStrategy;
		
		return new IBallAlgo() {
			@Override
			public void caseDefault(IBall host) {
				host.execute(algo1);
				host.execute(algo2);
			}
			
			@Override
			public String toString() {
				return algo1.toString() + "-" + algo2.toString();
			}
		};
	}
	

	
	/**
	 * Gets the switcher algo instance that we made earlier
	 * @return the instance of the switcher install algorithm
	 */
	public IBallAlgo getSwitcherInstallAlgo() {
		return this.switcherInstallAlgo;
	}
	
	
	/**
	 * Sets the new switcher strategy
	 * @param decoreeInstallAlgo the algo to install to the switcher
	 */
	public void switchSwitcherStrategy(IBallAlgo decoreeInstallAlgo) {
		switcherDummyBall.execute(clearStrategiesAlgo);
		switcherDummyBall.execute(decoreeInstallAlgo);
		loadBall(this.getSwitcherInstallAlgo());
	}
}

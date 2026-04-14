package hw06.model.BallVisitors;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JPanel;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.FollowerBall;
import hw06.model.BallHosts.GangsterBall;
import hw06.model.BallHosts.IBall;
import hw06.model.BallHosts.LeaderBall;
import hw06.model.interactStrategy.AInteractStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;
import provided.logger.ILogger;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.view.ValuesPanel;

/**
 * Configuration algorithm to install a dynamically controllable interact strategy that makes
 * other ball follows it in different ways depending on what type the current ball is.
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class LeadConfigAlgo extends AConfigBallAlgo {
	
	/**
	 * Add generated serialVersionUID
	 */
	private static final long serialVersionUID = 7836308457605849046L;
	/**
	 * Used to uniquely identify each instance of this configuration algo
	 */
	private static int instanceCount = 0;
	
	
	/**
	 * The unique ball type ID used represent no ball type here. 
	 */
	private IBallHostID nullBallType = BallHostIDFactory.Singleton.makeID("None");
	
	/**
	 * The currently selected ball type to kill.
	 */
	private IBallHostID currentBallType = nullBallType;
	/**
	 * The ball type algo used for type-dependent interactions.
	 * ballTypeAlgo.getDefaultID() is the ball type ID that represents the default case of the algo, i.e. "All Balls".
	 * Parameter:  A Runnable command that is run for the desired ball type(s).
	 * Does a no-op for all other ball types.
  	 * Initially configured for a no-op for all ball types (isDefaultRunFunc = false), i.e. kills no one.
	 */
	private OneTypeBallAlgo<Void> ballTypeAlgo = new OneTypeBallAlgo<Void>("All Balls", false);
	/**
	 * Enable the ball to lead other balls.
	 */
	private boolean isEnabled = true;
	
	/**
	 * Max distance from the lead
	 */
	private int maxDistance = 10;
	
	/**
	 * The Speed Scale of the following balls.
	 */
	private int speedScale = 20;

	
	/**
	 * Constructs the algo with the given adapter with default, instance-specific friendly name 
	 * @param logger The logger to use
	 * @param algo2ModelAdpt The adapter to the model
	 */
	public LeadConfigAlgo(ILogger logger, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		this(logger, "Lead_"+instanceCount, algo2ModelAdpt);
		instanceCount++;
	}
    
	/**
	 * Constructs the algo with the given adapter with the given friendly name.
	 * @param logger The logger to use
	 * @param name The friendly name to display
	 * @param algo2ModelAdpt The adapter to the model
	 */
	@SuppressWarnings("unchecked")
	public LeadConfigAlgo(ILogger logger, String name, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		super(logger, name, algo2ModelAdpt);
		
		ABallAlgoCmd<Void, Void> gangsterCmd = new ABallAlgoCmd<Void, Void>() {
			/**
			 * Auto generated serial
			 */
			private static final long serialVersionUID = 5725952261180305815L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						target.execute(ballTypeAlgo, (targetHost)->{
							//target is no the same type.
							if (!isEnabled || context.execute(SameTypeBallAlgo.Singleton, target)) return null;
							target.setColor(Color.BLUE);
							target.setUpdateStrategy(new MultiUpdateStrategy( target.getUpdateStrategy(),
									new IUpdateStrategy() {									
										@Override
										public void updateState(IBall ball, IDispatcher<IBallCmd> dispball) {
											if (ball.getLocation().distance(context.getLocation()) > (ball.getRadius() + context.getRadius()) && 
													ball.getLocation().distance(context.getLocation()) < ball.getRadius() + context.getRadius() + maxDistance) {
												Point distance = new Point(context.getLocation().x- ball.getLocation().x, context.getLocation().y- ball.getLocation().y);
												double normalized_x = distance.x / distance.distance(0, 0);
												double normalized_y = distance.y / distance.distance(0, 0);
												double speedBall = context.getVelocity().distance(0, 0);
												ball.setVelocity(new Point((int)(speedScale*speedBall*normalized_x/20),(int)(speedScale*speedBall*normalized_y/20)));
											}
										}
									@Override
									public void init(IBall host) {
									}
								
							}));
							return null;
						});
					}

					@Override
					public void init(IBall host) {
					}

				});
				return null;
			}
		};
		
		ABallAlgoCmd<Void, Void> leadCmd = new ABallAlgoCmd<Void, Void>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						target.execute(ballTypeAlgo, (targetHost)->{
							//target is not lead ball and is not gangster ball.
							if (!isEnabled || context.execute(SameTypeBallAlgo.Singleton, target) || 
								target.execute(SameTypeBallAlgo.Singleton,
											new GangsterBall(new Point(), 0, new Point(), Color.BLACK, new JPanel(), new BallAlgo<Void, Void>(ABallAlgoCmd.MakeNull()))))
								return null;
							target.setUpdateStrategy(new MultiUpdateStrategy( target.getUpdateStrategy(),
									new IUpdateStrategy() {									
										@Override
										public void updateState(IBall ball, IDispatcher<IBallCmd> dispball) {
											if (ball.getLocation().distance(context.getLocation()) > (ball.getRadius() + context.getRadius()) && 
													ball.getLocation().distance(context.getLocation()) < ball.getRadius() + context.getRadius() + maxDistance) {
												Point distance = new Point(context.getLocation().x- ball.getLocation().x, context.getLocation().y- ball.getLocation().y);
												double normalized_x = distance.x / distance.distance(0, 0);
												double normalized_y = distance.y / distance.distance(0, 0);
												double speedBall = context.getVelocity().distance(0, 0);
												ball.setVelocity(new Point((int)(speedScale*speedBall*normalized_x/20),(int)(speedScale*speedBall*normalized_y/20)));
											}
										}
									@Override
									public void init(IBall host) {
									}
								
							}));
							return null;
						});
					}

					@Override
					public void init(IBall host) {
					}

				});
				return null;
			}
		};
		
		ABallAlgoCmd<Void, Void> followerCmd = new ABallAlgoCmd<Void, Void>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						target.execute(ballTypeAlgo, (targetHost)->{
							if (!isEnabled || Math.random() > .001) return null;
							target.setUpdateStrategy(new MultiUpdateStrategy( target.getUpdateStrategy(),
									new IUpdateStrategy() {						
										int clock = target.getCount();
										@Override
										public void updateState(IBall ball, IDispatcher<IBallCmd> dispball) {
											if (ball.getCount() < clock + 50 && ball.getLocation().distance(context.getLocation()) > (ball.getRadius() + context.getRadius()) && 
													ball.getLocation().distance(context.getLocation()) < ball.getRadius() + context.getRadius() + maxDistance) {
												Point distance = new Point(context.getLocation().x- ball.getLocation().x, context.getLocation().y- ball.getLocation().y);
												double normalized_x = distance.x / distance.distance(0, 0);
												double normalized_y = distance.y / distance.distance(0, 0);
												double speedBall = context.getVelocity().distance(0, 0);
												ball.setVelocity(new Point((int)(speedScale*speedBall*normalized_x/20),(int)(speedScale*speedBall*normalized_y/20)));
											}
										}
									@Override
									public void init(IBall host) {
									}
								
							}));
							return null;
						});
					}

					@Override
					public void init(IBall host) {
					}

				});
				return null;
			}
		};
		this.setDefaultCmd(ABallAlgoCmd.MakeNull());
		setCmd(LeaderBall.ID, leadCmd); // A leader ball can lead
		setCmd(GangsterBall.ID, gangsterCmd); // A gangster ball can make any one a follower.
		setCmd(FollowerBall.ID, followerCmd); // A follower ball can make some people a follower for 50 ticks
		
		// Add a ValuesPanel to the view to control the strategies made by this configuration algo.
		algo2ModelAdpt.addConfigComponent(name, ()->{
			ValuesPanel pnlValues =  new ValuesPanel( "RULE OF LEADING: The Followers follow both Leaders and "
					+ "Gangsters. Leaders will only follow Gangsters. Gangsters don't follow any one as it is lawless."
					+ "Follower might be able to leader any other ball but for a very brief period of time."
					+ "\nNOTE: Make sure to compose this with an interaction criteria, and a paint strategy. "
					+ "\nEnable or disable followers."
					+ "\nMax Distance: The max distance that the follower ball will still follow the lead ball."
					+ "\nSpeed Scale: the scaling for the speed of the following ball with respect to the lead ball (20 is the same as the lead ball)"
					+ "\nFollower type: The type of ball that the host will try to make followers "
					+ "\nThe rule above still follows,"
					+ "Gangsters never follow Leader, but you can make Gangsters stop leading Leaders.)",logger);
			pnlValues.addBooleanInput("Status","Enabled", isEnabled, (newVal)->{
				isEnabled = newVal; // No validation being done here.
				return isEnabled;  // Return the current value 		
			});
			pnlValues.addIntSliderInput("Max Distance", maxDistance, (newVal)->{
				maxDistance = newVal; 
				return maxDistance;}, 
				0, 100);
			pnlValues.addIntSliderInput("Speed Scale", speedScale, (newVal)->{
				speedScale = newVal; 
				return speedScale;  }
				, 5, 35);		
			pnlValues.addDropListInput("Follower ball type", currentBallType, (IBallHostID newVal)->{
            
				// ballTypeAlgo has no option for "no ball types", so need to check for it externally
				// as part of the input validation process.
				if(newVal.equals(nullBallType)) {
					ballTypeAlgo.reset(false);  // TurnBlack no one = reset the algo to just a no-op default case 
				}
				else {
					ballTypeAlgo.setType(newVal);  // TurnBlack only the newVal type (everyone if newVal = ballTypeAlgo.getDefaultID())
				}
				currentBallType = newVal;  // save the current kill type
				return currentBallType;   // return the current kill type to be displayed on the GUI
			}, nullBallType, ballTypeAlgo.getDefaultType(), DefaultBall.ID, LeaderBall.ID, FollowerBall.ID, GangsterBall.ID);  // Available lead options
			
			return pnlValues;
		});
	}
}
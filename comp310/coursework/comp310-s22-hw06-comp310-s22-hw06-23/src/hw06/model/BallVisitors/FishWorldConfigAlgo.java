package hw06.model.BallVisitors;

import java.awt.Color;
import java.awt.Point;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.FollowerBall;
import hw06.model.BallHosts.GangsterBall;
import hw06.model.BallHosts.IBall;
import hw06.model.BallHosts.LeaderBall;
import hw06.model.interactStrategy.CollideInteractStrategy;
import hw06.model.paint.strategy.NiceFishPaintStrategy;
import hw06.model.updateStrategy.ColorUpdateStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import hw06.model.updateStrategy.OverlapUpdateStrategy;
import hw06.model.updateStrategy.WanderUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;
import provided.logger.ILogger;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.view.ValuesPanel;

/**
 * Configuration algorithm to install a dynamically controllable interact strategy that model a Fish World.
 * More in the readme.
 * @author Charlie Lockyer and Son Nguyen
 *
 */
public class FishWorldConfigAlgo extends AConfigBallAlgo {
	
	/**
	 * Add generated serialVersionUID
	 */
	private static final long serialVersionUID = 7836308457605849046L;
	
	/**
	 * Used to uniquely identify each instance of this configuration algo
	 */
	private static int instanceCount = 0;
	
	/**
	 * The integer to scale the new fish size by
	 */
	private int scaleSize = 1;
	
	/**
	 * Set the acceleration of the ball.
	 */
	private double acc = 0;
	
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
	 * Constructs the algo with the given adapter with default, instance-specific friendly name 
	 * @param logger The logger to use
	 * @param algo2ModelAdpt The adapter to the model
	 */
	public FishWorldConfigAlgo(ILogger logger, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		this(logger, "FishWorld_"+instanceCount, algo2ModelAdpt);
		instanceCount++;
	}
    
	/**
	 * Constructs the algo with the given adapter with the given friendly name.
	 * @param logger The logger to use
	 * @param name The friendly name to display
	 * @param algo2ModelAdpt The adapter to the model
	 */
	public FishWorldConfigAlgo(ILogger logger, String name, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		super(logger, name, algo2ModelAdpt);
		//Fishes can lead, kill, and eat.
		KillConfigAlgo killConfigAlgo = new KillConfigAlgo(logger, getName() + ": Kill", algo2ModelAdpt);
		LeadConfigAlgo leadConfigAlgo = new LeadConfigAlgo(logger, getName() + ": Lead", algo2ModelAdpt);
		
		ABallAlgoCmd<Void, Void> leadCmd = new ABallAlgoCmd<Void, Void>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				installPaintStrategy(host, new NiceFishPaintStrategy());
				installUpdateStrategy(host, new MultiUpdateStrategy(new ColorUpdateStrategy(), new OverlapUpdateStrategy()));
				installInteractStrategy(host, new CollideInteractStrategy());
				installUpdateStrategy(host,  new IUpdateStrategy() {
					@Override
					public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
						setHostRandRadius(host, 60, 50);
						setHostVelocity(host);
					}
					@Override
					public void init(IBall host) {	
					}
				});
				
				host.execute(killConfigAlgo);
				host.execute(leadConfigAlgo);
				return null;
			}
		};
		ABallAlgoCmd<Void, Void> gangsterCmd = new ABallAlgoCmd<Void, Void>() {
			/**
			 * Auto generated serial
			 */
			private static final long serialVersionUID = 5725952261180305815L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				installPaintStrategy(host, new NiceFishPaintStrategy());
				host.setColor(Color.RED);
				installUpdateStrategy(host, new MultiUpdateStrategy(new WanderUpdateStrategy(), new OverlapUpdateStrategy()));
				installInteractStrategy(host, new CollideInteractStrategy());
				installUpdateStrategy(host,  new IUpdateStrategy() {
					@Override
					public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
						setHostRandRadius(host, 40, 30);
						setHostVelocity(host);
					}
					@Override
					public void init(IBall host) {	
					}
				});
				host.execute(killConfigAlgo);
				host.execute(leadConfigAlgo);
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
				installPaintStrategy(host, new NiceFishPaintStrategy());
				installUpdateStrategy(host,  new OverlapUpdateStrategy());
				installUpdateStrategy(host,  new IUpdateStrategy() {
					@Override
					public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
						setHostRandRadius(host, 90, 70);
						setHostVelocity(host);
					}
					@Override
					public void init(IBall host) {	
					}
				}
				);
				
				installInteractStrategy(host, new CollideInteractStrategy());
				host.execute(killConfigAlgo);
				host.execute(leadConfigAlgo);
				return null;
			}
		};
		
		this.setDefaultCmd(ABallAlgoCmd.MakeNull());
		setCmd(LeaderBall.ID, leadCmd); // A leader ball can lead
		setCmd(GangsterBall.ID, gangsterCmd); // A gangster ball can make any one a follower.
		setCmd(FollowerBall.ID, followerCmd); // A follower ball can make some people a follower for 50 ticks
		//FIXME: fixmeeeeee
		// Add a ValuesPanel to the view to control the strategies made by this configuration algo.
		algo2ModelAdpt.addConfigComponent(name, ()->{
			ValuesPanel pnlValues =  new ValuesPanel( "\nMax Distance = The max distance that the follower ball will still follow the lead ball."
					+ "\nSpeed Scale: the scaling for the speed of the following ball with respect to the lead ball (20 is the same as the lead ball)"
					+ "\nFollower type = The type of ball that the host will try to make followers",logger);
			
			pnlValues.addIntSliderInput("Scale Size", scaleSize, (newVal)->{
				scaleSize = newVal; 
				return scaleSize;  }
				, 1, 10);
			pnlValues.addDoubleInput("Acceleration", acc, (newVal)->{
				acc = newVal; 
				return acc;  });
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
	
	/**
	 * Set the host fish size in accordance to a divMin and divMax scale down factor
	 * @param host The host fish.
	 * @param divMin An integer for the denominator of the min size.
	 * @param divMax An integer for the denominator of the max size.
 	 */
	private void setHostRandRadius(IBall host, int divMin, int divMax) {
		//Set the sizes for the fish
		int scaledwidth = scaleSize*host.getContainer().getWidth();
		int scaledheight = scaleSize*host.getContainer().getHeight();
		int minRad = Math.max(10,Math.min(scaledwidth/divMin, scaledheight/divMin));
		int maxRad = Math.max(10,Math.min(scaledwidth/divMax, scaledheight/divMax));
		int randomRadius = host.getRandGen().randomInt(minRad, maxRad);
		host.setRadius(randomRadius);
	}
	
	/**
	 * Accelerate the ball by the acceleration
	 * @param host The host fish.
 	 */
	private void setHostVelocity(IBall host) {
		//Set the velocity
		double normalized_x = host.getVelocity().x / host.getVelocity().distance(0, 0);
		double normalized_y = host.getVelocity().y / host.getVelocity().distance(0, 0);
		host.setVelocity(new Point(host.getVelocity().x+(int)(normalized_x*acc),(host.getVelocity().y+(int)(normalized_y*acc))));
	}
}
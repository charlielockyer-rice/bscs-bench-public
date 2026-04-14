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
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;
import provided.logger.ILogger;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.view.ValuesPanel;

/**
 * Configuration algorithm to install a dynamically controllable interact strategy that has a certain 
 * probability of "killing" the target ball.   The dynamically generated control panel can
 * modify the kill probability as well as change whether it kills a specific target ball type 
 * (available types are hard-coded for simplicity) or all ball types or no ball types.
 * Individual instances of this configuration algorithm are uniquely numbered for easy 
 * identification on the GUI.
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class SuckLifeConfigAlgo extends AConfigBallAlgo {

	/**
	 *  Add generated serialVersionUID
	 */
	private static final long serialVersionUID = -2975499527049162904L;

	/**
	 * Used to uniquely identify each instance of this configuration algo
	 */
	private static int instanceCount = 0;
	
    /**
	 * The rate at which the ball is being suck its life out.
	 */
	private int suckScale = 1;
	
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
	public SuckLifeConfigAlgo(ILogger logger, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		this(logger, "SuckLife_"+instanceCount, algo2ModelAdpt);
		instanceCount++;
	}
    
	/**
	 * Constructs the algo with the given adapter with the given friendly name.
	 * @param logger The logger to use
	 * @param name The friendly name to display
	 * @param algo2ModelAdpt The adapter to the model
	 */
	@SuppressWarnings("unchecked")
	public SuckLifeConfigAlgo(ILogger logger, String name, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		super(logger, name, algo2ModelAdpt);
		ABallAlgoCmd<Void, Void> gangsterCmd = new ABallAlgoCmd<Void, Void>() {


			/**
			 *  Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 8650435217403345850L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						target.execute(ballTypeAlgo, (targetHost)->{
							if (context.execute(SameTypeBallAlgo.Singleton, target)) return null;
							target.setRadius(target.getRadius()-suckScale*2);
							context.setRadius(context.getRadius()+suckScale*2);
							if (target.getRadius() <5) disp.removeObserver(target);
							Color newColor = new Color(Math.max(target.getColor().getRed()-suckScale*2, 0),
									Math.max(target.getColor().getBlue()-suckScale*2, 0),
									Math.max(target.getColor().getGreen()-suckScale*2, 0));
							target.setColor(newColor);
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
			private static final long serialVersionUID = 8650435217403345850L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						target.execute(ballTypeAlgo, (targetHost)->{
							if (context.execute(SameTypeBallAlgo.Singleton, target)) return null;
							target.setRadius(target.getRadius()-suckScale);
							context.setRadius(context.getRadius()+suckScale);
							if (target.getRadius() <5) disp.removeObserver(target);
							Color newColor = new Color(Math.max(target.getColor().getRed()-suckScale, 0),
									Math.max(target.getColor().getBlue()-suckScale, 0),
									Math.max(target.getColor().getGreen()-suckScale, 0));
							target.setColor(newColor);
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
			 *  Add generated serialVersionUID
			 */
			private static final long serialVersionUID = -7760174101502579087L;

			@Override
			public Void apply(IBallHostID index, IBall host, Void... params) {
				
				installInteractStrategy(host, new AInteractStrategy() {
					@Override
					public void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
						if (context.execute(SameTypeBallAlgo.Singleton, target) || 
								target.execute(SameTypeBallAlgo.Singleton,
											new DefaultBall(new Point(), 0, new Point(), Color.BLACK, new JPanel(), new BallAlgo<Void, Void>(ABallAlgoCmd.MakeNull()))))
						{
							if (target.getRadius() < context.getRadius()) 
								target.execute(ballTypeAlgo, (targetHost)->{
									
									target.setRadius(target.getRadius()-suckScale*2);
									context.setRadius(context.getRadius()+suckScale*2);
									if (target.getRadius() <5) disp.removeObserver(target);
									Color newColor = new Color(Math.max(target.getColor().getRed()-suckScale*2, 0),
											Math.max(target.getColor().getBlue()-suckScale*2, 0),
											Math.max(target.getColor().getGreen()-suckScale*2, 0));
									target.setColor(newColor);
									return null;
								});
						}
					}

					@Override
					public void init(IBall host) {
					}

				});
				return null;
			}
		};
		//A default person won't be able to kill.
		this.setDefaultCmd(ABallAlgoCmd.MakeNull());
		setCmd(LeaderBall.ID, leadCmd); // A leader ball can kill.
		setCmd(GangsterBall.ID, gangsterCmd); // A gangster ball can kill with 3x the probability and make a gruelsome sound.
		setCmd(FollowerBall.ID, followerCmd); // A gangster ball can kill with 3x the probability and make a gruelsome sound.

		// Add a ValuesPanel to the view to control the strategies made by this configuration algo.
		algo2ModelAdpt.addConfigComponent(name, ()->{
			ValuesPanel pnlValues =  new ValuesPanel( "RULE OF EATING: Gangsters can suck the life out of any ball with "
					+ "twice the eat rate. Leader will suck at the given rate. Follower will only suck on default and other Followers that are "
					+ "smaller than them."
					+ "\nNOTE: Make sure to combine with a criteria update Strategy and a paint strategy for this to work. "
					+ "Eating Rate: An integer for the eat rate. "
					+ "\nuck Life Types:: The type of ball that the host try to suck life out off (still obey the rules)."
					+ "\nThe rule above still follows,"
					+ "	Followers never eat Gangsters, but you can make Gangsters stop eating Followers.)",logger);

			pnlValues.addIntegerInput("Eating Rate",suckScale, (newVal)->{
				suckScale = newVal;
				return newVal;
						
			});	
			
			pnlValues.addDropListInput("Suck Life Types:", currentBallType, (IBallHostID newVal)->{
	            
				// ballTypeAlgo has no option for "no ball types", so need to check for it externally
				// as part of the input validation process.
				if(newVal.equals(nullBallType)) {
					ballTypeAlgo.reset(false);  // Kill no one = reset the algo to just a no-op default case 
				}
				else {
					ballTypeAlgo.setType(newVal);  // Kill only the newVal type (everyone if newVal = ballTypeAlgo.getDefaultID())
				}
				currentBallType = newVal;  // save the current kill type
				return currentBallType;   // return the current kill type to be displayed on the GUI
			}, nullBallType, ballTypeAlgo.getDefaultType(), DefaultBall.ID, GangsterBall.ID, LeaderBall.ID, FollowerBall.ID);  // Available kill options
			
			return pnlValues;
		});
	}
}
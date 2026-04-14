package hw06.model.BallVisitors;

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
import provided.logger.LogLevel;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.view.ValuesPanel;

/**
 * Configuration algorithm to install a dynamically controllable interact strategy that has a certain 
 * probability of "killing" the target ball.   The dynamically generated control panel can
 * modify the kill probability as well as change whether it kills a specific target ball type 
 * (available types are hard-coded for simplicity) or all ball types or no ball types.
 * Individual instances of this configuration algorithm are uniquely numbered for easy 
 * identification on the GUI.
 * @author swong
 *
 */
public class KillConfigAlgo extends AConfigBallAlgo {
	
	/**
	 * Add generated serialVersionUID
	 */
	private static final long serialVersionUID = 2959878040378553992L;

	/**
	 * Used to uniquely identify each instance of this configuration algo
	 */
	private static int instanceCount = 0;
	
    /**
	 * The probability of killing the target if it is the right type
	 */
	private double killProb = .2;
	
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
	public KillConfigAlgo(ILogger logger, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		this(logger, "Kill_"+instanceCount, algo2ModelAdpt);
		instanceCount++;
	}
    
	/**
	 * Constructs the algo with the given adapter with the given friendly name.
	 * @param logger The logger to use
	 * @param name The friendly name to display
	 * @param algo2ModelAdpt The adapter to the model
	 */
	@SuppressWarnings("unchecked")
	public KillConfigAlgo(ILogger logger, String name, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		super(logger, name, algo2ModelAdpt);
		ABallAlgoCmd<Void, Void> leadKillCmd = new ABallAlgoCmd<Void, Void>() {
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
							if(Math.random() < killProb) {
								disp.removeObserver(target);
								logger.log(LogLevel.DEBUG, "Leader Killed target: "+target);
							}
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
		ABallAlgoCmd<Void, Void> gangsterKillCmd = new ABallAlgoCmd<Void, Void>() {
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
							if(Math.random() < killProb*3) {
								java.awt.Toolkit.getDefaultToolkit().beep();
								disp.removeObserver(target);
								logger.log(LogLevel.DEBUG, "Gangster Killed target: "+target);;
							}
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
		//A default person won't be able to kill.
		this.setDefaultCmd(ABallAlgoCmd.MakeNull());
		setCmd(LeaderBall.ID, leadKillCmd); // A leader ball can kill.
		setCmd(GangsterBall.ID, gangsterKillCmd); // A gangster ball can kill with 3x the probability and make a gruelsome sound.
		
		// Add a ValuesPanel to the view to control the strategies made by this configuration algo.
		algo2ModelAdpt.addConfigComponent(name, ()->{
			ValuesPanel pnlValues =  new ValuesPanel( "RULE OF KILLING: Gangsters can kill any ball with thrice the probability given and make a loud "
					+ "noise about it. Leader will kill with the given probability. Follower and Default can't kill."
					+ "\nNOTE: Make sure to combine with a criteria update Strategy and a paint strategy for this to work. "
					+ "Kill probability: The probability of killing the target if it is the right type.  Value must be in the range [0, 1). "
					+ "\nKill type: The type of ball that the host try to kill (still obey kill rule)."
					+ "\nThe rule above still follows,"
					+ "	Followers never kill Gangsters, but you can make Gangsters stop killing Followers.)",logger);

			pnlValues.addDoubleInput("Kill probability",killProb, (newVal)->{

				// Validate the input before setting it.
				if(0 <= newVal && newVal < 1.0) {
					killProb = newVal;
				}
				else {
					logger.log(LogLevel.ERROR, "Kill probability limit must be in the range [0, 1)!");
				}
				return killProb;
						
			});	
			
			pnlValues.addDropListInput("Kill type", currentBallType, (IBallHostID newVal)->{
            
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
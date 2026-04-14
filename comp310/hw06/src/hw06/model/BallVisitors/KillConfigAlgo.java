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
		// TODO: Implement this constructor
		// Set up ball type commands (leadKillCmd, gangsterKillCmd) for LeaderBall, GangsterBall
		// Install interact strategies that kill target balls with given probability
		// Gangsters kill with 3x probability and make a beep sound
		// Add a ValuesPanel to the view to control kill probability and kill type
	}
}
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
		// TODO: Implement this constructor
		// Set up ball type commands (gangsterCmd, leadCmd, followerCmd) for GangsterBall, LeaderBall, FollowerBall
		// Install interact strategies that make other balls follow the host based on type rules
		// Gangsters make anyone follow, Leaders make non-gangsters follow, Followers briefly lead
		// Add a ValuesPanel to the view to control enable/disable, max distance, speed scale, follower type
	}
}
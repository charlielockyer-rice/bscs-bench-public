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
import hw06.model.paint.strategy.EarthPaintStrategy;
import hw06.model.paint.strategy.MoonPaintStrategy;
import hw06.model.paint.strategy.SunPaintStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.OverlapUpdateStrategy;
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
public class SolarSystemConfigAlgo extends AConfigBallAlgo {
	
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
	public SolarSystemConfigAlgo(ILogger logger, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		this(logger, "SolorSystem_"+instanceCount, algo2ModelAdpt);
		instanceCount++;
	}
    
	/**
	 * Constructs the algo with the given adapter with the given friendly name.
	 * @param logger The logger to use
	 * @param name The friendly name to display
	 * @param algo2ModelAdpt The adapter to the model
	 */
	public SolarSystemConfigAlgo(ILogger logger, String name, IBallAlgo2ModelAdapter algo2ModelAdpt) {
		super(logger, name, algo2ModelAdpt);
		// TODO: Implement this constructor
		// Set up ball type commands (leadCmd, gangsterCmd, followerCmd) for LeaderBall, GangsterBall, FollowerBall
		// Install paint strategies (Earth, Sun, Moon), update strategies, interact strategies, and config algos
		// Add a ValuesPanel to the view to control the strategies made by this configuration algo
	}
	
	/**
	 * Set the host fish size in accordance to a divMin and divMax scale down factor
	 * @param host The host fish.
	 * @param divMin An integer for the denominator of the min size.
	 * @param divMax An integer for the denominator of the max size.
 	 */
	private void setHostRandRadius(IBall host, int divMin, int divMax) {
		// TODO: Implement this method
	}

	/**
	 * Accelerate the ball by the acceleration
	 * @param host The host fish.
 	 */
	private void setHostVelocity(IBall host) {
		// TODO: Implement this method
	}
}
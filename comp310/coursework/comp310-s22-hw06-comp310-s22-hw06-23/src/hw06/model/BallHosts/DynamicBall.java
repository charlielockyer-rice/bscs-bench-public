package hw06.model.BallHosts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import hw06.model.BallVisitors.BallAlgo;
import hw06.model.BallVisitors.IBallAlgo;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;
import provided.extvisitor.IExtVisitor;

/**
 * A ball type whose instances can appear to dynamically change their type. 
 * This type change manifests itself as a change in the case invoked on its visitors.  
 * This class implements a local host ID value that overrides the inherited host ID value from ABall.
 * The overridden host ID value, not the inherited ID value, is used when executing visitors.<br/>
 * WARNING: Because this class does not add any methods or fields to ABall, 
 * there can be problems when attempting to mimic any ball that does so!   
 * Be VERY careful when using with visitors that treat the host as anything other than an IBall!   
 * @author swong
 *
 */
public class DynamicBall extends ABall {
	/**
	 * Add generated serialVerionUID
	 */
	private static final long serialVersionUID = 6115873538313721264L;

	/**
	 *  The identifying host ID for this class.  The overridden host ID defaults to this value.
	 *  This is NOT the value used when executing visitors!
	 */
	public static final IBallHostID ID = BallHostIDFactory.Singleton.makeID(DynamicBall.class.getName());
	
	/**
	 * The overridden host ID value used when executing visitors.
	 */
	private IBallHostID hostID = ID;

	/**
	 * Constructor for the class that defaults the overridden ID to be this class's ID and sets the 
	 * inherited host ID to be that same value.  This constructor would typically be used when this 
	 * class is being instantiated directly.
	 * @param radius the initial radius of this Ball.
	 * @param velocity the initial velocity of this Ball.
	 * @param color The initial color of the ball.
	 * @param location the initial center of this Ball.
	 * @param container the adapter to the system model
	 * @param configAlgo The algo to complete the installation of strategies and any other desired operations
	 */
	public DynamicBall(Point location, int radius, Point velocity, Color color, Component container,
			IBallAlgo<Void, Void> configAlgo) {
		this(ID, location, radius, velocity, color, container, configAlgo);
	}

	/**
	 * Constructor for the class that defaults the overridden ID and inherited ID to be the given initial ID value.
	 * This constructor would typically be used by a subclass to set the inherited ID and initial overridden ID values 
	 * to be the subclass's host ID value.
	 * @param initHostID The initial host ID value to use.
	 * @param radius the initial radius of this Ball.
	 * @param velocity the initial velocity of this Ball.
	 * @param color The initial color of the ball.
	 * @param location the initial center of this Ball.
	 * @param container the adapter to the system model
	 * @param configAlgo The algo to complete the installation of strategies and any other desired operations
	 */
	public DynamicBall(IBallHostID initHostID, Point location, int radius, Point velocity, Color color,
			Component container, IBallAlgo<Void, Void> configAlgo) {
		super(initHostID, location, radius, velocity, color, container, configAlgo);
		setHostID(initHostID);  
	}
	
	/**
	 * Change the apparent type of this ball to the given type.  <br/>
	 * WARNING: Do NOT change to a host type whose implementing class 
	 * adds methods and/or fields to ABall!
	 * @param hostID The new host ID value
	 */
	public void setHostID(IBallHostID hostID) {
		this.hostID = hostID;
	}	
	
	/**
	 * {@inheritDoc}
	 * <br/>
	 * Uses the overridden host ID value held in this class instead of the inherited host ID value.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <R, P> R execute(IExtVisitor<R, IBallHostID, P, ? extends IBall> algo, P... params) {
		return ((BallAlgo<R, P>)algo).caseAt(this.hostID, this, params); // The cast is due to limitations in the Java compiler.
	}
}
package hw06.model.BallVisitors;

import java.util.function.Function;

import hw06.model.BallHosts.IBall;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;

/**
 * A ball algo that is designed to process either a single type of ball or all ball types 
 * using a function provided as the input parameter.
 * Input parameter:   Function<IBall, R> a function that takes the ball host and returns a value of type R.  Executing the visitor will 
 * return either the return value from this function or the return value of the default case command, e.g. null if 
 * the default case cmd is a no-op. 
 * This function is run for the currently set processed ball type or the default case, if set.
 * To set the default case as the processed ball type, use this instance's getDefaultType() value as the identifier
 * for the default case. 
 * 
IMPORTANT: Any commands added using setCmd() or removed using removeCmd() are NOT controlled by the methods of this 
 * subclass and are not affected by the methods in this subclass unless that specific ball type was affected by the setType() method!  
 * @author swong
 *
 * @param <R>   The return type of the input parameter function.
 */
public class OneTypeBallAlgo<R> extends BallAlgo<R, Function<IBall, R>> {

	/**
	 * Add generated serialVersionUID
	 */
	private static final long serialVersionUID = -3137191847691308721L;

	/**
	 * The no-op command used by this algo.
	 */
	private ABallAlgoCmd<R, Function<IBall, R>> defaultCmd = ABallAlgoCmd.MakeNull();
	
	/**
	 * The command that runs the given function
	 */
	private ABallAlgoCmd<R, Function<IBall, R>> runFuncCmd = new ABallAlgoCmd<>() {
		/**
		 * Add generated serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public R apply(IBallHostID index, IBall host, @SuppressWarnings("unchecked") Function<IBall, R>... fns) {
			return fns[0].apply(host);
		}
		
	};
	
	/**
	 * The IBallHostID that is used to uniquely identify the default case in the algo. 
	 */
	private IBallHostID defaultID = BallHostIDFactory.Singleton.makeID(OneTypeBallAlgo.class.getName());
	
	/**
	 * The ball type that is currently being processed.
	 */
	private IBallHostID currentID = defaultID;
	
	/**
	 * Constructor for the class where the default case is a no-op if it is not the processed ball type.
	 * @param defaultTypeName The friendly name given to the IBallHostID this instance uses to identify its default case.
	 * @param isDefaultRunFunc If true, then the default case will be set to running the given function.  
	 * Otherwise the default case will be a no-op.
	 */
	public OneTypeBallAlgo(String defaultTypeName, boolean isDefaultRunFunc) {
		this(defaultTypeName, isDefaultRunFunc, ABallAlgoCmd.MakeNull());
	}

	/**
	 * Constructor for the class where the default case is the given defaultCmd if it is not the processed ball type.
	 * @param defaultTypeName The friendly name given to the IBallHostID this instance uses to identify its default case.
	 * @param isDefaultRunFunc If true, then the default case will be set to running the given function.  
	 * Otherwise the default case will be the given defaultCmd.
	 * @param defaultCmd The command to use for the default case if it is not the processed type.
	 */
	public OneTypeBallAlgo(String defaultTypeName, boolean isDefaultRunFunc, ABallAlgoCmd<R, Function<IBall, R>> defaultCmd) {
		super(defaultCmd);
		this.defaultID = BallHostIDFactory.Singleton.makeID(defaultTypeName);  // Generate the special ID for default case
		this.defaultCmd = defaultCmd; // Save the default cmd so the default case can be reset later
		reset(isDefaultRunFunc); // Initialize to a reset state
	}
	
	/**
	 * Gets the IBallHostID this instance uses to identify its default case.
	 * This value is instance-dependent!
	 * @return This instance's default case identifier ID.
	 */
	public IBallHostID getDefaultType() {
		return this.defaultID;
	}

	/**
	 * Resets the algorithm such the current ball ID being used is for processing is the default ID, i.e. the default command.
	 * The default case processing depends on the value of isDefaultRunFunc.  If isDefaultRunFunc = false, then the default case cmd 
	 * set by the constructor will be used.  If isDefaultRunFunc = true, then the default case will process all ball types with the given 
	 * input parameter function, returning the function's return value.
	 * reset(false) can be used to disable this algo if the default case cmd is a no-op.
	 * @param isDefaultRunFunc If true, the default case is set to run the given input parameter function of the algo.  
	 * Otherwise, the default case cmd is used.
	 */
	public void reset(boolean isDefaultRunFunc) {
		setType(this.defaultID);
		if(!isDefaultRunFunc) {
			setDefaultCmd(defaultCmd);
		}
	}
	
	/**
	 * Gets the current processing state of the algo's default case.
	 * @return true, if the default case is set to run the given input parameter function of the algo.  
	 * Otherwise, the default case is a no-op. 
	 */
	public boolean isDefaultRunFunc() {
		return this.getDefaultCmd().equals(this.runFuncCmd);
	}
	
	/**
	 * Gets the current ball type that is being processed. 
	 * If the default ID is returned, then all ball types are being processed.
	 * Note: if reset(false) was called, the default case is set to be a no-op and no other ball types are being explicitly processed.
	 * The default ID value is returned in that scenario.
	 * @return The current ball type being processed.
	 */
	public IBallHostID getCurrentType() {
		return currentID;
	}
	
	/**
	 * Changes the current ball type to be processed to the given type.
	 * If the default case is desired, then use getDefaultType() as the ballTypeID value.
	 * The previous ball type will be configured to run the default case,
	 * i.e the command associated with that type is removed if it is not the default case.
	 * If the previous type was the default case, the default case will be set to be a no-op, 
	 * unless the default case is indicated by ballTypeID.
	 * @param ballTypeID The type of the ball to be set for processing.
	 */
	public void setType(IBallHostID ballTypeID) {
		// Set the previous ball type behavior to be no-op
		if(currentID.equals(defaultID)) {
			setDefaultCmd(defaultCmd);
		}
		else {
			removeCmd(currentID); // Makes the previous type revert to the default case.
		}
		
		currentID = ballTypeID; // Save the new type
		
		// Set the new type to be the processed type
		if(ballTypeID.equals(defaultID)) {
			setDefaultCmd(runFuncCmd);
		}
		else {
			setCmd(currentID, runFuncCmd);
		}
	}
}

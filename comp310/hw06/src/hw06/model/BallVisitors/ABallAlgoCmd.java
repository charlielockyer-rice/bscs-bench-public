package hw06.model.BallVisitors;

import hw06.model.BallHosts.IBall;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.ABallHostAlgoCmd;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
/**
 * An abstract command based on ABallHostAlgoCmd whose host is an IBall. 
 * This command is designed to be used in a BallAlgo instance.
 * @author swong
 *
 * @param <R> The return type of the command
 * @param <P> The input parameter type of the command 
 */ 
 public abstract class ABallAlgoCmd<R, P> extends ABallHostAlgoCmd<R, P, IBall> { 
	/**
	 * Add generated serialVersionUID 
	 */
	private static final long serialVersionUID = -7002289852615009144L;

	/**
	 * Static factory to create a null cmd that matches the generic typing of the target variable.
	 * @param <TReturn> The return type of the null cmd
	 * @param <TParam> The input parameter type of the null cmd
	 * @return Always returns null
	 */
	public static final <TReturn, TParam> ABallAlgoCmd<TReturn, TParam> MakeNull(){
		return new ABallAlgoCmd<>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 6606779758564199257L;

			@Override
			public TReturn apply(IBallHostID index, IBall host, @SuppressWarnings("unchecked") TParam... params) {
				ILoggerControl.getSharedLogger().log(LogLevel.DEBUG, "ABallAlgoCmd.NULL invoked.  Returned null.");
				return null;
			}
			
		};
	}
	
	/**
	 * Static factory to create a error cmd that matches the generic typing of the target variable.
	 * @param <TReturn> The return type of the null cmd
	 * @param <TParam> The input parameter type of the null cmd
	 * @return Always returns null
	 */
	public static final <TReturn, TParam> ABallAlgoCmd<TReturn, TParam> MakeError(){
		return new ABallAlgoCmd<>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 7054012806274057777L;

			@Override
			public TReturn apply(IBallHostID index, IBall host, @SuppressWarnings("unchecked") TParam... params) {
				ILoggerControl.getSharedLogger().log(LogLevel.ERROR, "ABallAlgoCmd.ERROR invoked.  Returned null.");
				return null;
			}
			
		};
	}    
}
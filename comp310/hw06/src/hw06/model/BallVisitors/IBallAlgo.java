package hw06.model.BallVisitors;

import hw06.model.BallHosts.IBall;
import provided.ballworld.extVisitors.IBallHostAlgo;

/**
 * An algorithm to process a host ball
 * @author Son Nguyen and Charlie Lockyer
 * @param <R> The return type of the algorithm
 * @param <P> The input parameter type of the algorithm
 */
public interface IBallAlgo<R, P> extends  IBallHostAlgo<R, P, IBall>{

}

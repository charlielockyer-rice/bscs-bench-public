package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IList;
import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Return a list with the smallest element from the list removed.   
 * Do this with a single pass through the the list!
 * The input parameter is not used ("nu").
 * @author Son Nguyen and Charlie Lockyer
 */
public class RemoveSmallestRevAlgo implements IListAlgo {
	/**
	 * Returns Min_value always. 
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return MTList.Singleton;
	}

	/**
	 * Returns the list depending if the condition has been met.
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {
		if((int)host.getFirst() < getMin(host.getRest())) {
			if (host.getRest().equals( MTList.Singleton))
				return MTList.Singleton;
			return new NEList(((NEList) (host.getRest())).getFirst() , 
					((NEList) (host.getRest())).getRest());
		}else 
			return new NEList(host.getFirst(),(IList)host.getRest().execute(this));

	}
	
	/**
	 * Helper function to get the minimum value.
	 * @param host the host list
	 * @return minimum number in the list
	 */
	public int getMin(IList host) {
		if(host == MTList.Singleton)
			return Integer.MAX_VALUE;
		if (((NEList)host).getRest() == MTList.Singleton)
			return (int) ((NEList)host).getFirst();
		else
			return Math.min((int)((NEList)host).getFirst(), getMin(((NEList)host).getRest()));
	}

}
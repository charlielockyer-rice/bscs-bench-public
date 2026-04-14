package provided.listFW.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 * Algo to calculate the product of all the elements in a list of integers.  The input parameter is not used.
 * @author swong
 *
 */
public class ProdAlgo implements IListAlgo {

	@Override
	public Object emptyCase(MTList host, Object... nu) {
		return 1;
	}


	@Override
	public Object nonEmptyCase(NEList host, Object... nu) {

		return (int) host.getFirst() * (int) host.getRest().execute(this);
	}

}

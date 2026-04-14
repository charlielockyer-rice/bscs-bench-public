package provided.rmiUtils.monitor.model;

import java.util.List;
import provided.utils.struct.IDyad;

/**
 * The adapter from the model to the view
 * @author swong
 *
 */
public interface IModel2ViewAdapter {

	/**
	 * Show the given list of dyads on the view
	 * @param items The list of bound_name-bound_object dyads to display
	 */
	void showItems(List<IDyad<String, Object>> items);

}

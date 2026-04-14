package visitorDemoExercises.listFWVisitorExercises.model;

import provided.listFW.IList;
import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;
import provided.listFW.visitors.FoldLAlgo;
import provided.listFW.visitors.FoldRAlgo;
import provided.listFW.visitors.IAccumulator;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoaderPath;

/** ListFW demo app model
 * 
 * @author swong
 *
 */
public class ListDemoModel {
	
	/**
	 * logger to use
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * Adapter to the view
	 */
	private IViewAdapter view;

	/**
	 * Instance of first available host
	 */
	private IList list0 = MTList.Singleton;

	/**
	 * Instance of second available host
	 */
	private IList list1 = new NEList(42, list0);

	/**
	 * Instance of third available host
	 */
	private IList list2 = new NEList(-99, list1);

	/**
	 * Dynamic loader for accumulator classes
	 */
	private IObjectLoader<IAccumulator> accLoader = new ObjectLoaderPath<IAccumulator>(
		(params)->{
			return new IAccumulator() {

				@Override
				public void accumulate(Object x) {
					logger.log(LogLevel.ERROR, "Error accumulator: x = "+x);
				}
				
				@Override
				public String toString() {
					return "Error Accumulator";
				}
				
			};
		},
		"provided.listFW.visitors.",
		"visitorDemoExercises.listFWVisitorExercises.model.visitors."
	);
	
	/**
	 * Dynamic loader for visitor classes
	 */
	private IObjectLoader<IListAlgo> visLoader = new ObjectLoaderPath<IListAlgo>(
		(params)->{
			return new IListAlgo() {

				@Override
				public Object emptyCase(MTList host, Object... inp) {
					return "Error algo: Empty case";
				}

				@Override
				public Object nonEmptyCase(NEList host, Object... inp) {
					return "Error algo: Non-empty case";
				}
				
			};
		},
		"provided.listFW.visitors.",
		"visitorDemoExercises.listFWVisitorExercises.model.visitors."
	);

	/**
	 * The fold right algo
	 */
	private IListAlgo foldr = new FoldRAlgo();
	/**
	 * The fold left algo
	 */
	private IListAlgo foldl = new FoldLAlgo();

	/** 
	 * Constructor for the class
	 * 
	 * @param view  The view adapter
	 */
	public ListDemoModel(IViewAdapter view) {
		this.view = view;
	}

	/** 
	 * Start the model
	 */
	public void start() {
		view.setLists(list0, list1, list2, new NEList(-123, new NEList(2012,
				list2)));

	}

	/**
	 * Returns the result of a host list executing the given visitor with the given parameter
	 * @param list The host list object
	 * @param classname  The class name of the visitor to use, without the leading "listFW.visitor."
	 * @param param  An input parameter for the visitor execution
	 * @return The result of the visitor execution by the list.
	 */
	public String runListAlgo(IList list, String classname, String param) {
		IListAlgo algo = visLoader.loadInstance(classname);
		return list + ".execute(" + classname + ", " + param + ") = "
				+ list.execute(algo, param);
	}

	/**
	 * Run the FoldRAlgo visitor on the given list using the given IAccumulator
	 * @param list The host list object
	 * @param classname The class name of the accumulator to use, without the leading "listFW.visitor."
	 * @param param A constructor parameter for an accumulator object.   Must be a String that the constructor can then convert to whatever it needs.
	 * @return The result of the foldr visitor execution by the list.
	 */
	public String runFoldR(IList list, String classname, String param) {
		Object [] params;
		if("".equals(param)) {
			params = new Object[] {};
		}
		else {
			params = new Object[] {param};
		}
		
		IAccumulator acc = accLoader.loadInstance(classname, params);
		return list + ".execute(FoldRAlgo," + classname + "(" + acc + ")) = "
				+ list.execute(foldr, acc);
	}

	/**
	 * Run the FoldLAlgo visitor on the given list using the given IAccumulator
	 * @param list The host list object
	 * @param classname The class name of the accumulator to use, without the leading "listFW.visitor."
	 * @param param  A constructor parameter for an accumulator object.   Must be a String that the constructor can then convert to whatever it needs.
	 * @return The result of the foldl visitor execution by the list.
	 */
	public String runFoldL(IList list, String classname, String param) {

		Object [] params;
		if("".equals(param)) {
			params = new Object[] {};
		}
		else {
			params = new Object[] {param};
		}
		
		IAccumulator acc = accLoader.loadInstance(classname, params);
		return list + ".execute(FoldLAlgo," + classname + "(" + acc + ")) = "
				+ list.execute(foldl, acc);
	}

}

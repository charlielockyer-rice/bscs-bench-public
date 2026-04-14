package provided.simpleExtVisitorsDemo.demo.model;

import provided.simpleExtVisitorsDemo.extVisitor.*;

/**
 * The "model" for the demo app
 * @author swong
 *
 */
public class DemoModel {

	/**
	 * The adapter to the view
	 */
	private IViewAdapter viewAdpt;

	/**
	 * Constructor for the class
	 * @param view  The adapter to the view
	 */
	public DemoModel(IViewAdapter view) {
		this.viewAdpt = view;
	}

	/**
	 * Returns a new IHost with the given id
	 * @param id  The id of the new host
	 * @return A new IHost instance
	 */
	public IHost makeHost(final String id) {
		return 	new IHost(){

			@Override
			public Object execute(IExtVisitor algo, Object... params) {
				return algo.caseAt(id, this, params);
			}

			public String toString() {
				return "Host_"+id;
			}
		};
	}

	/**
	 * Execute the given visitor on the given host, with no input parameters.
	 * @param host  The host to use
	 * @param visitor The visitor to use
	 * @return The result of host.execute(visitor)
	 */
	public Object run(IHost host, IExtVisitor visitor) {
		return host.execute(visitor);
	}

	/**
	 * Return a new visitor with the given name
	 * @param name The new visitor's toString() method returns "ExtVisitor_"+name
	 * @return A new ExtVisitor instance
	 */
	public ExtVisitor makeVisitor(String name) {
		return new ExtVisitor(name);
	}

	/**
	 * Add or replace a command in the given visitor that returns the given result string.
	 * @param visitor  The visitor to use
	 * @param id The id of the host associated with the new command
	 * @param resultStr  The string to be returned when the visitor is run on a host with the given id.
	 */
	public void addCmd(ExtVisitor visitor, final String id, final String resultStr) {
		visitor.addCmd(id, new IExtVisitorCmd(){

			@Override
			public Object apply(String id, IHost host, Object... params) {
				return "cmds["+id+"] --> "+ resultStr;
			}

		});
	}

	/**
	 * Starts the model.  
	 * No-op for now
	 */
	public void start() {
		viewAdpt.appendMsg("First, create a new host and a new visitor, then add commands to the visitor for each host.");
	}

	
}

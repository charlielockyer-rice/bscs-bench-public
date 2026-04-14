package visitorDemoExercises.basicVisitorDemo.model.visitors;

import provided.basicVisitorFW.HostA;
import provided.basicVisitorFW.HostB;
import provided.basicVisitorFW.HostC;
import provided.basicVisitorFW.IVisitor;


/**
 * A visitor that returns a String that says "This is the result from HostX,
 * where HostX is the type of the IHost object executing this visitor.
 * @author swong
 *
 */
public class Visitor3 implements IVisitor {

	@Override
	public Object caseHostA(HostA host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Object caseHostB(HostB host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Object caseHostC(HostC host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String toString() {
		return "Visitor3";
	}

}

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
		return "This is the result from HostA with the text field reading "+(String)params[1];
	}

	@Override
	public Object caseHostB(HostB host, Object... params) {
		return "This is the result from HostB with the text field reading "+(String)params[1];
	}

	@Override
	public Object caseHostC(HostC host, Object... params) {
		return "This is the result from HostC with the text field reading "+(String)params[1];
	}
	
	@Override
	public String toString() {
		return "Visitor3";
	}

}

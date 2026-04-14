package visitorDemoExercises.basicVisitorDemo.model.visitors;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Consumer;

import provided.basicVisitorFW.HostA;
import provided.basicVisitorFW.HostB;
import provided.basicVisitorFW.HostC;
import provided.basicVisitorFW.IVisitor;

// HOLD THE MOUSE OVER THE CLASS NAME BELOW TO SEE THE JAVADOCS WITHOUT THE ESCAPED CHARACTERS, E.G. &lt; and &gt;

/**
 * A visitor that sets the paint cmd in the VisitorDemoModel to a cmd that draws various shapes.
 * The params is assumed to be a single value, a reference to a function that takes a function that takes a Graphics object.
 * That is, param[0] = a Consumer&lt;Consumer&lt;Graphics&gt;&gt; object.   This function will install the given Consumer&lt;Graphics&gt;
 * function into the model.  The view calls to the model to use the installed command when painting.
 * When the host is a HostA, the installed Consumer&lt;Graphics&gt; draws a blue oval on the screen.
 * When the host is a HostB, the installed Consumer&lt;Graphics&gt; draws a red rectangle on the screen.
 * When the host is a HostC, the installed Consumer&lt;Graphics&gt; draws a green rounded rectangle on the screen.
 * Returns a string saying what it did.
 * @author swong
 *
 */
public class Visitor4 implements IVisitor {

	@SuppressWarnings("unchecked")  // Necessary because we are not using generic visitors here.
	@Override
	public Object caseHostA(HostA host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@SuppressWarnings("unchecked")   // Necessary because we are not using generic visitors here.
	@Override
	public Object caseHostB(HostB host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@SuppressWarnings("unchecked")   // Necessary because we are not using generic visitors here.
	@Override
	public Object caseHostC(HostC host, Object... params) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String toString() {
		return "Visitor2";
	}

}

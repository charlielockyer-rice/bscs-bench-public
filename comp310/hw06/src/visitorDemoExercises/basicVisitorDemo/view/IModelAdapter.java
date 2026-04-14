package visitorDemoExercises.basicVisitorDemo.view;

import java.awt.Graphics;

/**
 * The view's adapter to the model
 * @author swong
 *
 * @param <CBoxItem>  The type of objects being put in the host JComboBox
 */
public interface IModelAdapter<CBoxItem> {

	/**
	 * Get an array of CBoxItems to place in the JComboBox
	 * @return An array of CBoxItems
	 */
	CBoxItem[] getHosts();
	
	/**
	 * Ask the model to process the given host object with the given a visitor
	 * @param host  A host item
	 * @param visitorClassname  The name of the visitor to use, though the leading "visitors.impl." part is missing.
	 * @param input_param Input parameter for the visitor.
	 * @return  The results of this particular algorithm.
	 */
	String run(CBoxItem host, String visitorClassname, String input_param);
	
	/**
	 * Ask the model to paint something on this Graphics object.
	 * @param g  The Graphics object to paint on.
	 */
	void paint(Graphics g);
	
}

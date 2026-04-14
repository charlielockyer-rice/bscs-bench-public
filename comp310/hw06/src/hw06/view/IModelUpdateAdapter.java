package hw06.view;

import java.awt.Graphics;

/**
 * The Adapter Interface for updating (painting) the GUI. 
 * @author Cole Rabson, Son Nguyen
 */
public interface IModelUpdateAdapter {

	/**
	 * Paint whatever needed to be on the screen.
	 * @param g	the Graphics object to paint on
	 */
	public void paint(Graphics g);

	/**
	 * No-op singleton implementation of IModelUpdateAdapter
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IModelUpdateAdapter NULL = new IModelUpdateAdapter() {
		public void paint(Graphics g) {
		}
	};
}

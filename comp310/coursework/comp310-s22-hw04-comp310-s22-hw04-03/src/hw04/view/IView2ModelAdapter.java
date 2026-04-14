package hw04.view;

import java.awt.Graphics;

/**
 * An adapter from the view to the model
 * @author charlielockyer
 *
 * @param <TDropListItem> the items in the drop down lists
 */
public interface IView2ModelAdapter<TDropListItem> {
	
	/**
	 * Adding an update strategy to the list
	 * @param classname the name to add
	 * @return the drop list item to go in the list
	 */
	public TDropListItem addUpdateStrategy(String classname);
	
	/**
	 * Adding a paint strategy to the list
	 * @param classname the name to add
	 * @return the drop list item to go in the list
	 */
	public TDropListItem addPaintStrategy(String classname);
	
	/**
	 * Clear everything on the view
	 */
	public void clearAll();
	
	/**
	 * Loads a ball given a factory
	 * @param stratFac the factory to make the ball
	 */
	public void loadBall(TDropListItem stratFac);
	
	/**
	 * Paints on the canvas
	 * @param g the graphics object to paint on
	 */
	public void paint(Graphics g);
	
	/**
	 * The combine method to combine strategies
	 * @param strat1 first strategy
	 * @param strat2 second strategy
	 * @return the combined drop list item
	 */
	public TDropListItem combine(TDropListItem strat1, TDropListItem strat2);
	
	/**
	 * Makes a switcher ball
	 * @param algo the algorithm to give it
	 */
	public void makeSwitcherBall(TDropListItem algo);
	
	/**
	 * Does the switch of the switcher ball
	 * @param algo the algorithm
	 */
	public void switchSwitcher(TDropListItem algo);
	
	/**
	 * Instantiating the null object of the adapter
	 */
	@SuppressWarnings("rawtypes")
	public static final IView2ModelAdapter NULL = new IView2ModelAdapter() {
		public Object addUpdateStrategy(String classname) {
			return null;
		}
		
		public Object addPaintStrategy(String classname) {
			return null;
		}
		
		public void paint(Graphics g) {
			
		}
		
		public void clearAll() {
			
		}
		
		public void loadBall(Object strat) {
			
		}
		
		public Object combine(Object strat1, Object strat2) {
			return null;
		}
		
		public void makeSwitcherBall(Object algo) {
			
		}
		
		public void switchSwitcher(Object algo) {
			
		}
	};
}

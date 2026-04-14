package hw06.view;

/**
 * The Adapter Interface for all the button functionality of the BallGUI.
 * @author Cole Rabson and Son Nguyen
 * @param <TDropListItem> The type of objects put into the view's drop lists
 */
public interface IModelCtrlAdapter<TDropListItem> {

	/**
	 * Adds an Update strategy to the view's drop lists
	 * @param classname	The name of the ball class to be added
	 * @return A TDropListItem of the strategy to be added
	 */
	public TDropListItem addUpdateStrategy(String classname);
	
//	/**
//	 * Adds a Ball Type to the Ball Type 's drop lists
//	 * @param classname	The name of the ball class to be added
//	 */
//	public void addBallType(String classname);


	/**
	 * Adds an Paint strategy to the view's drop lists
	 * @param classname	The name of the ball class to be added
	 * @return A TDropListItem of the strategy to be added
	 */
	public TDropListItem addPaintStrategy(String classname);
	
	/**
	 * Adds an Interaction strategy to the view's drop lists
	 * @param classname	The name of the ball class to be added
	 * @return A TDropListItem of the strategy to be added
	 */
	public TDropListItem addInteractStrategy(String classname);

	/**
	 * Makes a switcher ball
	 */
	public void makeSwitcherBall();

	/**
	 * Clear all of the balls of the canvas
	 */
	public void clearBalls();

	/**
	 * Makes a ball from the selected TDropListItem
	 * @param selectedItem The TDropListItem selected
	 * @param classname The classname of the ball type to be made
	 */
	public void makeBall(TDropListItem selectedItem, String classname);

	/**
	 * Combines two strategies
	 * @param selectedItem1 One of the TDropListItem strategies to combine
	 * @param selectedItem2	The other TDropListItem strategy to combine
	 * @return A TDropListItem of the combined strategy
	 */
	public TDropListItem combineStrategies(TDropListItem selectedItem1, TDropListItem selectedItem2);

	/**
	 * Switches to the selected TDropListItem strategy
	 * @param selectedItem The TDropListItem strategy to switch to
	 */
	public void switchStrategy(TDropListItem selectedItem);

	/**
	 * Adds a configuration algorithm to the view's drop lists
	 * @param text	The name of the configuration to be installed
	 * @return A TDropListItem the configuration
	 */
	public TDropListItem addConfigAlgo(String text);

}

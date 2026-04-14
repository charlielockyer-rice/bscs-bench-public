package hw03.view;

/**
 * Adapter that the view uses to communicate to the model for non-repetitive control tasks such as manipulating strategies.
 * @author swong
 * @param <TDropListItem> Drop down from the view side
 *
 */
public interface IModelControlAdapter<TDropListItem> {

    /**
     * Take the given short strategy name and return a corresponding something to put onto both drop lists.
     * @param strategy type in order to create
     * @return Something to put onto both the drop lists.
     */
    public TDropListItem addStrategy(String strategy);

    /**
     * Make a ball with the selected short strategy name.
     * @param selectedItem  A shorten class name for the desired strategy
     */
    public void makeBall(TDropListItem selectedItem);
    
    /**
     * @param strategy to create switcher ball
     */
    public void makeSwitcherBall(String strategy);
    
    /**
     * @param factory method to add to switcher ball
     */
    public void switchStrategy(TDropListItem factory);
    
    //public void makeBall(String strategy);

    /**
     * Return a new object to put on both lists, given two items from the lists.
     * @param selectedItem1  An object from one drop list
     * @param selectedItem2 An object from the other drop list
     * @return An object to put back on both lists.
     */
    public TDropListItem combineStrategies(TDropListItem selectedItem1, TDropListItem selectedItem2);
	
	/**
	 * Method to clear all balls on screen.
	 */
	public void clearBalls() ; 

}


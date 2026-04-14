package mcx1_cwl6.miniMVC.view;


/**
 * View to model adapter for the mini MVC
 */
public interface IMiniView2ModelAdapter {
	
	/**
	 * Send a message
	 * @param textMsg The text to be send
	 */
	public void sendText(String textMsg);
	
	/**
	 * Send unknown
	 */
	public void sendUnknown();
	
	/**
	 * Leave a chat room
	 */
	public void leaveRoom();
	
	/**
	 * Remove room from list of rooms you are in
	 */
	public void removeRoomFromList();
	
	/**
	 * Interface for communicating for View2Model
	 * @return the adapter.
	 */
	public static IMiniView2ModelAdapter NULL_OBJECT() {
		return new IMiniView2ModelAdapter() {

			@Override
			public void sendText(String text) {
				// TODO Auto-generated method stub
			}

			@Override
			public void sendUnknown() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void leaveRoom() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeRoomFromList() {
				// TODO Auto-generated method stub
				
			}

	
		};
		
	};
	
}

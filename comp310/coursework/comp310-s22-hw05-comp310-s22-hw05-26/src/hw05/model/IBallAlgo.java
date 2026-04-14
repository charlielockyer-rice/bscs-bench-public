package hw05.model;


/**
 * An algorithm to process a host ball
 * @author akashkaranam
 *
 */
public interface IBallAlgo {
	/**
	 * The default case process
	 * @param host The host ball to process.
	 */
	public void caseDefault(IBall host);
	
	/**
	 * The error strategy for the ball that it uses by default
	 */
	public static IBallAlgo errorStrategy = new IBallAlgo() {
		private int count = 0;
		public void caseDefault(IBall host) {
			if(25 < count++){
		        java.awt.Toolkit.getDefaultToolkit().beep(); 
		        count = 0;
		    }
		}
	};
}

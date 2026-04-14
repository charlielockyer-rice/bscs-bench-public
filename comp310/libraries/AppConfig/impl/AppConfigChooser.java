package provided.config.impl;

import javax.swing.JOptionPane;

import provided.config.AppConfig;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * A class that can pop up a dialog to choose amongst an array of AppConfig-derived configurations.
 * 
 * @author swong
 *
 * @param <TAppConfig>  The specific type of AppConfig being chosen.  
 */
public class AppConfigChooser<TAppConfig extends AppConfig> {
	/**
	 * The logger to use
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();
	
	/**
	 * The array of AppConfigs to choose
	 */
	private TAppConfig[] appConfigOptions;
	
	/**
	 * The default choice index to use.  Defaults to an invalid value.
	 */
	private int defaultChoiceIndex = JOptionPane.CLOSED_OPTION;
	
	/**
	 * Construct a chooser with the given options and no default choice index defined.
	 * @param appConfigOptions A vararg of AppConfig options
	 */
	@SafeVarargs
	public AppConfigChooser(TAppConfig... appConfigOptions) {
		this.appConfigOptions = appConfigOptions;
	}
	
	/**
	 * Construct a choose with the given Options and the given default choice index.  
	 * @param defaultChoiceIndex The zero-based index of the given choices to use if the choice dialog is cancelled. 
	 * @param appConfigOptions A vararg of AppConfig options.
	 * @throws IllegalArgumentException If the given defaultChoiceIndex is not a valid index for one of the given choices.
	 */
	@SafeVarargs
	public AppConfigChooser(int defaultChoiceIndex, TAppConfig... appConfigOptions) {
		this(appConfigOptions);
		if(0>defaultChoiceIndex || defaultChoiceIndex >= appConfigOptions.length) {
			throw new IllegalArgumentException("Given defaultChoiceIndex ("+defaultChoiceIndex+") is out of range [0, "+(appConfigOptions.length-1)+"]");
		}
		else {
			this.defaultChoiceIndex = defaultChoiceIndex;
		}
	}
	
	/**
	 * Choose one of the configured AppConfig choices or, if cancelled, the choice determined by the configured default choice index.
	 * @return The chosen AppConfig
	 * @throws IllegalStateException If the choice is cancelled and no default choice index was configured.
	 */
	public TAppConfig choose() {
		int choiceIdx = JOptionPane.showOptionDialog(null, 
				"Select the desired startup configuration:", 
				"Startup Options", 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				appConfigOptions, 
				null);
		if(JOptionPane.CLOSED_OPTION == choiceIdx) {
			if(JOptionPane.CLOSED_OPTION == defaultChoiceIndex) {
				String errMsg = "AppConfig choice was canceled and no default choice index was set.";
				logger.log(LogLevel.ERROR, "Throwing IllegalStateException: "+errMsg);
				throw new IllegalStateException(errMsg); 
			}
			else {
				logger.log(LogLevel.INFO, "AppConfig choice was canceled.  Using default choice index = "+defaultChoiceIndex);
				choiceIdx = defaultChoiceIndex;
			}
		}
		return appConfigOptions[choiceIdx];
		
	}
	
	

}

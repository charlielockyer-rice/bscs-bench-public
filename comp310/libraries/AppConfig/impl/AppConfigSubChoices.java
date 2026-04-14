package provided.config.impl;

import provided.config.AppConfig;

/**
 * A convenience AppConfig used to reduce clutter in scenarios using many app config possibilities.
 * Use this class to build a hierarchy of app config choices.   This class represents the set of the 
 * sub-choices it contains.  The sub-choices are the members of that set.  
 * The choose() method uses an AppConfigChooser to pop up a dialog for the user to select one of the sub-choices.
 * In essence, AppConfigSubChoices is an AppConfigChooser that is used as an AppConfig.
 * 
 * Usage example:  Suppose top_level_chooser is an AppConfigChooser&lt;AppConfigSubChoices&lt;MyAppConfig&gt;&gt;
 * <br/>
 * MyAppConfig selectAppConfig = top_level_chooser.choose().choose();
 * 
 * @author swong
 *
 * @param <TAppConfig> The type of the app config sub-choices
 */
public class AppConfigSubChoices<TAppConfig extends AppConfig> extends AppConfig {

	/**
	 * An app chooser filled with the sub-choices to offer to the user
	 */
	private AppConfigChooser<TAppConfig> subChoicesChooser;

	/**
	 * Constructor for the class
	 * @param name The name of the set describing all of the sub-choices.
	 * @param subChoices The app config members of the described set.
	 */
	@SafeVarargs
	public AppConfigSubChoices(String name, TAppConfig... subChoices) {
		super(name);
		subChoicesChooser = new AppConfigChooser<TAppConfig>(subChoices);
	}

	/**
	 * Constructor for the class
	 * @param name The name of the set describing all of the sub-choices.
	 * @param defaultChoiceIndex The zero-based index of the given choices to use if the choice dialog is cancelled. 
	 * @param subChoices The app config members of the described set.
	 */
	@SafeVarargs
	public AppConfigSubChoices(String name, int defaultChoiceIndex, TAppConfig... subChoices) {
		super(name);
		subChoicesChooser = new AppConfigChooser<TAppConfig>(defaultChoiceIndex, subChoices);
	}
	
	
	/**
	 * Pop up a dialog for the user to choose amongst the supplied sub-choices.
	 * @return The selected sub-choice
	 */
	public TAppConfig choose() {
		return subChoicesChooser.choose();
	}

}

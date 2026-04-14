package provided.logger.util;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;


/**
 * Simple display panel for logger output.  This panel implements the IStringLogEntryProcessor
 * and hence also ILogEntryProcessor, to facilitate its use by ILoggers.
 * Note that this does not extend AStringLogEntryrocessor because of Java's multiple inheritance restrictions.
 * @author swong
 *
 */
public class LoggerPanel extends JPanel implements IStringLogEntryProcessor {
	
	/**
	 * Default maximum number of lines of text displayed 
	 */
	private static final int DEFAULT_MAX_DISPLAY_LINES = 10000;

	/**
	 * Default percentage of max_display_lines lines of text removed from the beginning of the displayed 
	 * text when max_display_lines is exceeded.
	 */
	private static final double DEFAULT_DISPLAY_LINES_RESET_PERCENT = 0.25;
	

	/**
	 * Maximum number of lines of text displayed 
	 */
	private int max_display_lines = DEFAULT_MAX_DISPLAY_LINES;
	
	/**
	 * Percentage of max_display_lines lines of text removed from the beginning of the displayed 
	 * text when max_display_lines is exceeded.
	 */
	private double display_lines_reset_percent = DEFAULT_DISPLAY_LINES_RESET_PERCENT;

	/**
	 * The title of the panel shown on its titled border
	 */
	private String title;

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -6686909804641745363L;

	/**
	 * The log entry formatter in use.  This has to be explicitly implemented here because
	 * this JPanel cannot also extend AStringLogEntryProcessor.   
	 * This could be done with as a decorated AStringLogEntryProcessor but was not deemed 
	 * worth the trouble of its benefits. 
	 */
	private ILogEntryFormatter logEntryFormatter = ILogEntryFormatter.DEFAULT;

	/**
	 * Teh scrollbars around the text area
	 */
	private final JScrollPane spnDisplay = new JScrollPane();

	/**
	 * The text display area
	 */
	private final JTextArea taDisplay = new JTextArea();

	/**
	 * Uses a default title for the panel's border.
	 * The max_display_lines and display_lines_reset_percent values default to 
	 * DEFAULT_MAX_DISPLAY_LINES and DEFAULT_DISPLAY_LINES_RESET_PERCENT respectively.
	 */
	public LoggerPanel() {
		this("Logger Output");
	}
	
	/**
	 * Create the panel with a given title
	 * The max_display_lines and display_lines_reset_percent values default to 
	 * DEFAULT_MAX_DISPLAY_LINES and DEFAULT_DISPLAY_LINES_RESET_PERCENT respectively.
	 * @param title The border title used to identify this component on the GUI
	 */
	public LoggerPanel(String title) {
		this(title, DEFAULT_MAX_DISPLAY_LINES, DEFAULT_DISPLAY_LINES_RESET_PERCENT);
	}
	
	/**
	 * Create the panel with a given title and text lines control values.
	 * @param title The border title used to identify this component on the GUI
	 * @param max_display_lines  The maximum number of lines of text displayed by this component.   
	 * Must be a positive value.
	 * @param display_lines_reset_percent The percentage of max_display_lines lines of text removed from the 
	 * beginning of the displayed text when the maximum number of lines are exceeded.  Additional number of 
	 * existing lines of text over max_display_lines are also removed.  The value must be in the range (0.0, 1.0].
	 */
	public LoggerPanel(String title, int max_display_lines, double display_lines_reset_percent) {
		Objects.requireNonNull(title, "[LoggerPanel constructor] The panel's title must be non-null.");

		if(0 >= max_display_lines) {
			throw new IllegalArgumentException("[LoggerPanel constructor] max_display_lines must be a positive value");
		}
		if(0.0 >= display_lines_reset_percent || 1.0 < display_lines_reset_percent ) {
			throw new IllegalArgumentException("[LoggerPanel constructor] display_lines_reset_percent must be in the range (0.0, 1.0].");
		}
		
		this.title = title;		
		this.max_display_lines = max_display_lines;	
		this.display_lines_reset_percent = display_lines_reset_percent;
		initGUI();		
	}



	/**
	 * Initialize the panel's GUI elements
	 */
	private void initGUI() {
		setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		add(spnDisplay, BorderLayout.CENTER);

		spnDisplay.setViewportView(taDisplay);
	}

	/**
	 * Add a log entry to the display using the current log entry formatting function
	 * @param logEntry The log entry to append to the display 
	 */
	public void accept(ILogEntry logEntry) {
		addMsg(logEntryFormatter.apply(logEntry) + "\n");
	}

	/**
	 * Add an already formatted message (linefeed NOT automatically added)
	 * @param fullMsg  The message to display, already formatted as desired, including a trailing linefeed if needed.
	 */
	public void addMsg(String fullMsg) {
		SwingUtilities.invokeLater( ()->{ 
			int lineCount = taDisplay.getLineCount();
			if(max_display_lines < lineCount) {
				String text = taDisplay.getText();
				String[] splitText = text.split("\n", (lineCount - max_display_lines)+(int)(max_display_lines*display_lines_reset_percent));
				taDisplay.setText(splitText[splitText.length-1]);
			}
			taDisplay.append(fullMsg);
		});

	}

	@Override
	public void setLogEntryFormatter(ILogEntryFormatter leFormatter) {
		Objects.requireNonNull(leFormatter,
				"[LoggerPanel.setLogEntryFormatter()] The log entry formatter must be non-null.");
		this.logEntryFormatter = leFormatter;
	}

	@Override
	public ILogEntryFormatter getLogEntryFormatter() {
		return this.logEntryFormatter;
	}

}

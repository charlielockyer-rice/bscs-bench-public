package provided.utils.view;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import javax.swing.JTextArea;
import java.awt.FlowLayout;

/**
 * Flexible convenience panel for displaying an arbitrary number of arbitrary types of 
 * value input and display components.   This class is very useful in building 
 * "configuration panel" type UIs that need various configuration option values to be 
 * input and displayed. 
 * 
 * Includes convenience methods to easily create inputs and displays for common value types.
 * @author swong
 *
 */
public class ValuesPanel extends JPanel {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 5642637475105389535L;
	
	/**
	 * Preferred size of a value's label used in the convenience methods.
	 */
	private Dimension valueLabelPrefDim = new Dimension(200, 50);
	
	/**
	 * Preferred size of a value's input textfield used in the convenience methods.
	 */
	private Dimension textFieldPrefDim = new Dimension(200, 50);
	
	/**
	 * The main scrollbars around all the components
	 */
	private final JScrollPane spnMain = new JScrollPane();
	
	/**
	 * The panel that displays all the components
	 */
	private final JPanel pnlDisplay = new JPanel();
	
	/**
	 * The text area that displays the description
	 */
	private final JTextArea taDesc = new JTextArea();
	
	/**
	 * The logger in use
	 */
	private ILogger logger;

	/**
	 * Create the panel with the given description and logger.
	 * @param desc A description of the panel's controls.
	 * @param logger The logger to use
	 */
	public ValuesPanel(String desc, ILogger logger)  {
		this.logger = logger;
		initGUI();

		taDesc.setText(desc);
	}
	/**
	 * Create the panel with the given description and the shared system logger.
	 * @param desc A description of the panel's controls.
	 */
	public ValuesPanel(String desc)  {
		this(desc, ILoggerControl.getSharedLogger());
	}
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		add(spnMain, BorderLayout.CENTER);
		
		spnMain.setViewportView(pnlDisplay);
		pnlDisplay.setLayout(new GridLayout(0, 1, 0, 0));
		
		add(taDesc, BorderLayout.NORTH);
		taDesc.setWrapStyleWord(true);
		taDesc.setLineWrap(true);
		taDesc.setEditable(false);
	}
	
	/**
	 * Add a custom input component to the ValuesPanel
	 * @param title The title displayed around the border of the custom component 
	 * @param compFac A factory that instantiates the custom component
	 */
	public void addInputComponent(String title, Supplier<JComponent> compFac) {
		// Make a command that can be deferred if necessary.
		Runnable cmd = ()->{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.setLayout(new BorderLayout(0, 0));
			panel.add(compFac.get(), BorderLayout.CENTER);
			pnlDisplay.add(panel);

		};
		if(SwingUtilities.isEventDispatchThread()) {
			// If already on GUI thread, run now.  
			// Staying on the same GUI event also helps get the proper size of the whole ValuesPanel 
			// when components are being added during its construction. 
			cmd.run();
		}
		else {
			// Defer to the GUI thread if not on the GUI thread.
			SwingUtilities.invokeLater(cmd);
		}
	}
	
	/**
	 * Convenience method to add a string value input component. 
	 * The component displays the current value, a textfield to enter a new value and an Enter button. 
	 * @param title The title shown around the border of the component.
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the value typed in, processes it and returns the new value to display.
	 */
	public void addTextInput(String title, String initValue, Function<String, String> newValFunc) {
		
		addInputComponent(title, ()->{
			JPanel panel = new JPanel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setLayout(flowLayout);
//			panel.setLayout(new BorderLayout(0, 0));
			JLabel label = new JLabel(initValue);
			label.setPreferredSize(valueLabelPrefDim);
			label.setBorder(new TitledBorder(null, "Value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.add(label);
			JTextField textField = new JTextField();
			textField.setPreferredSize(textFieldPrefDim);
			textField.setBorder(new TitledBorder(null, "Enter new value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			textField.setText(initValue);
			panel.add(textField);
			JButton btnEnter = new JButton("Enter");
			panel.add(btnEnter);
			btnEnter.addActionListener((e)->{
				String result = newValFunc.apply(textField.getText());
				if(null != result) { // Don't do anything on a null return value
					label.setText(result);
				}
			});
			
			return panel;
		});
	}
	
	/**
	 * Convenience method to add a double value input component 
	 * The component displays the current value, a textfield to enter a new value and an Enter button. 
	 * @param title The title shown around the border of the component.
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the value typed in, processes it and returns the new value to display.
	 */
	public void addDoubleInput(String title, double initValue, Function<Double, Double> newValFunc) {
		addTextInput(title, ""+initValue, (newStr)-> {
			try {
				double newDbl = Double.parseDouble(newStr);
				return ""+newValFunc.apply(newDbl); 
			}
			catch(Exception e) {
				logger.log(LogLevel.ERROR, "Exception while parsing input: "+e);
				return null; // Don't change the displayed value
			}
		});
	}
	
	/**
	 * Convenience method to add an integer value input component 
	 * The component displays the current value, a textfield to enter a new value and an Enter button. 
	 * @param title The title shown around the border of the component.
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the value typed in, processes it and returns the new value to display.
	 */
	public void addIntegerInput(String title, int initValue, Function<Integer, Integer> newValFunc) {
		addTextInput(title, ""+initValue, (newStr)-> {
			try {
				int newInt = Integer.parseInt(newStr);
				return ""+newValFunc.apply(newInt); 
			}
			catch(Exception e) {
				logger.log(LogLevel.ERROR, "Exception while parsing input: "+e);
				return null; // Don't change the displayed value
			}
		});
	}
	
	/**
	 * Convenience method to add an integer value input component 
	 * The component displays a checkbox labeled with given label.  If the check value is changed, the newValFunc will process
	 * that value and set the checkbox to the resultant value.
	 * @param title The title shown around the border of the component.
	 * @param label The label of the checkbox
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the value of the checkbox, processes it and returns the new value to display.
	 */
	public void addBooleanInput(String title, String label, boolean initValue, Function<Boolean, Boolean> newValFunc) {
		addInputComponent(title, ()->{
			
			JPanel panel = new JPanel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setLayout(flowLayout);	
			JCheckBox chkBox = new JCheckBox(label, initValue);
			chkBox.addActionListener((e)->{

				chkBox.setSelected(newValFunc.apply(chkBox.isSelected()));
			});			
			panel.add(chkBox);
			return panel;
		});
	}	
	
	
	/**
	 * Convenience method to add a droplist input that enables the user to choose from a list of objects.   
	 * @param <TDropListItem>  The type of object on the droplist.   The compiler will infer this type from the given functions.
	 * @param title The title for the titled border surrounding this component. 
	 * @param initValue The initially selected object.   Be sure that this object is included in the vararg of items below!
	 * @param newValFunc A function that takes in the selected object from the drop list and returns the new object to display.
	 * @param items A vararg of objects to populate the droplist.
	 */
	public <TDropListItem> void addDropListInput(String title, TDropListItem initValue, Function<TDropListItem, TDropListItem> newValFunc, @SuppressWarnings("unchecked") TDropListItem... items) {
		addInputComponent(title, new Supplier<JComponent> () {
			TDropListItem currentValue = initValue;
			
			public JComponent get(){
		
				JPanel panel = new JPanel();
				FlowLayout flowLayout = new FlowLayout();
				flowLayout.setAlignment(FlowLayout.LEFT);
				panel.setLayout(flowLayout);
//				panel.setLayout(new BorderLayout(0, 0));
				JLabel label = new JLabel(currentValue.toString());
				label.setPreferredSize(valueLabelPrefDim);
				label.setBorder(new TitledBorder(null, "Value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel.add(label, BorderLayout.WEST);
				JComboBox<TDropListItem> dropList = new JComboBox<TDropListItem>();
				dropList.setBorder(new TitledBorder(null, "Select Item", TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel.add(dropList, BorderLayout.CENTER);
				for(TDropListItem item: items) {
					dropList.addItem(item);
				}
				dropList.setSelectedItem(currentValue);
				
				JButton btnEnter = new JButton("Change");
				panel.add(btnEnter, BorderLayout.EAST);
				btnEnter.addActionListener((e)->{
					TDropListItem result = newValFunc.apply(dropList.getItemAt(dropList.getSelectedIndex()));
					if(null != result) { // Don't do anything on a null return value
						currentValue = result;
						label.setText(currentValue.toString());
					}
					dropList.setSelectedItem(currentValue);
					
				});
				
				return panel;
			}
			});
	}
	
	/**
	 * Convenience method to add a slider input for integer values. A new value is submitted whenever the slider is moved.
	 * @param title The title shown around the border of the component.
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the new slider value when the slider is moved, processes it and returns the new value to display.
	 * @param minVal The minimum value of the slider's range, inclusive.
	 * @param maxVal The maximum value of the slider's range, inclusive.
	 */
	public void addIntSliderInput(String title, int initValue, Function<Integer, Integer> newValFunc, int minVal, int maxVal) {
		addInputComponent(title, () ->{
			// Throw exception if value is out of slider's range.
			Consumer<Integer> rangeCheckFn = (x)->{
				if( minVal> x || maxVal < x) {
					throw new IllegalArgumentException("[ValuesPanel.addIntSliderInput()] The given value, "+x+", must be in the range ["+minVal+", "+maxVal+"]!");
				}
			};
			
			rangeCheckFn.accept(initValue); // Check the initial value
			JPanel panel = new JPanel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setLayout(flowLayout);
			JLabel label = new JLabel(""+initValue);
			label.setPreferredSize(valueLabelPrefDim);
			label.setBorder(new TitledBorder(null, "Value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.add(label);
			
			JSlider slider = new JSlider(minVal, maxVal,initValue);
			slider.setBorder(new TitledBorder(null, "Adjust value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			slider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					int newVal = newValFunc.apply(slider.getValue());
					rangeCheckFn.accept(newVal); // check the value
					label.setText(""+newVal);
					slider.setValue(newVal);
				}});
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			labelTable.put(minVal, new JLabel(""+minVal));
			labelTable.put(maxVal, new JLabel(""+maxVal));
			slider.setLabelTable(labelTable);
			slider.setPaintLabels(true);
			panel.add(slider);
			return panel;
		});
	}

	/**
	 * Convenience method to add a slider input for double precision values. A new value is submitted whenever the slider is moved.
	 * Note that due to round-off errors, displayed values may be slightly off from the expected exact values.
	 * @param title The title shown around the border of the component.
	 * @param initValue The initial value
	 * @param newValFunc A function that takes the new slider value when the slider is moved, processes it and returns the new value to display.
	 * @param minVal The minimum value of the slider's range, inclusive.
	 * @param maxVal The maximum value of the slider's range, inclusive.
	 * @param step The resolution in values generated by and recognized by the slider.   Slider values are rounded to the nearest integer number of steps from the given minValue.
	 */
	public void addDblSliderInput(String title, double initValue, Function<Double, Double> newValFunc, double minVal, double maxVal, double step) {
		addInputComponent(title, () ->{
			// Throw exception if value is out of slider's range.
			Consumer<Double> rangeCheckFn = (x)->{
				if( minVal> x || maxVal < x) {
					throw new IllegalArgumentException("[ValuesPanel.addDoubleSliderInput()] The given value, "+x+", must be in the range ["+minVal+", "+maxVal+"]!");
				}
			};
			rangeCheckFn.accept(initValue); // check the initial value
			// convert doubles into an integer range for the slider
			int minIntVal = 0;
			int maxIntVal = (int)Math.ceil((maxVal-minVal)/step);
			int initIntVal = (int) Math.round((initValue-minVal)/step);
		
			JPanel panel = new JPanel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setLayout(flowLayout);
			JLabel label = new JLabel(""+initValue);
			label.setPreferredSize(valueLabelPrefDim);
			label.setBorder(new TitledBorder(null, "Value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			panel.add(label);
			
			JSlider slider = new JSlider(minIntVal, maxIntVal,initIntVal);
			slider.setBorder(new TitledBorder(null, "Adjust value", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			slider.addChangeListener(new ChangeListener() {
	
				@Override
				public void stateChanged(ChangeEvent e) {
					
					double newVal = newValFunc.apply(minVal + slider.getValue()*step);
					rangeCheckFn.accept(newVal); // check the value
					label.setText(""+newVal);
					slider.setValue((int) Math.round((newVal-minVal)/step));
				}});
			Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
			labelTable.put(minIntVal, new JLabel(""+minVal));
			labelTable.put(maxIntVal, new JLabel(""+maxVal));
			slider.setLabelTable(labelTable);
			slider.setPaintLabels(true);
			panel.add(slider);
			return panel;		

		});
	}
}

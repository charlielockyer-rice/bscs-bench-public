package provided.utils.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *  A class that can pop up a dialog to choose amongst a selection of options.
 *  Any type of options can be used. Each option's toString() method will be used to display them in the dialog.
 *  An optional tool tip can be displayed. Optionally, only a single option at most can be selected.  
 *  @param TOption The type of options to be selected.
 */
public class MultiChooserDialog<TOption> {

	/**
	 * The title of the displayed dialog
	 */
	private String title;
	
	/**
	 * The list of options
	 */
	private List<TOption> options;
	
	/**
	 * A tool tip to be displayed
	 */
	private String toolTip;
	
	/**
	 * If true, single select is enabled.
	 */
	private boolean isSingleSelect = false;

	/**
	 * The function that will set the size of the given JDialog.  No-op by default.
	 */
	private Consumer<JDialog> setDialogSizeFn = (dialogComp)->{};

	/**
	 * List of default selected options.
	 */
	private List<TOption> defaultSelectedOptions = List.of();
	
	/**
	 * Constructor for the class.  No tool tip will be shown.
	 * Multi-select is enabled.
	 * @param title The title to display
	 * @param options A vararg of options to display
	 */
	@SafeVarargs
	public MultiChooserDialog(String title, TOption... options ) {
		this(title, List.of(options));
	}

	/**
	 * Constructor for the class.  No tool tip will be shown.
	 * Multi-select is enabled.
	 * @param title The title to display
	 * @param options A list of options to display
	 */
	public MultiChooserDialog(String title, List<TOption> options ) {
		this(title, options, "");
	}
	
	/**
	 * Constructor for the class.  Multi-select is enabled.
	 * @param title The title to display
	 * @param options A list of options to display
	 * @param toolTip The tool tip to show
	 */
	public MultiChooserDialog(String title, List<TOption> options, String toolTip ) {
		this(title, options, toolTip, false);
	}
	
	/**
	 * Constructor for the class. 
	 * @param title The title to display
	 * @param options A list of options to display
	 * @param toolTip The tool tip to show
	 * @param isSingleSelect If true, then only one item can be selected at a time, i.e. multi-select is disabled.
	 */
	public MultiChooserDialog(String title, List<TOption> options, String toolTip, boolean isSingleSelect ) {
		this.title = title;
		this.options = options;	
		this.toolTip = toolTip;
		this.isSingleSelect = isSingleSelect;
	}
	
	/**
	 * Constructor for the class. 
	 * @param title The title to display
	 * @param options A list of options to display
	 * @param toolTip The tool tip to show
	 * @param isSingleSelect If true, then only one item can be selected at a time, i.e. multi-select is disabled.
	 * @parm dialogSize The dimensions of the displayed dialog component.  See the setSize() methods documentation for more information.
	 */
	public MultiChooserDialog(String title, List<TOption> options, String toolTip, boolean isSingleSelect, Dimension dialogSize ) {
		this(title, options, toolTip, isSingleSelect);
		setSize(dialogSize);
	}
	
	
	/**
	 * Constructor for the class. 
	 * @param title The title to display
	 * @param options A list of options to display
	 * @param toolTip The tool tip to show
	 * @param isSingleSelect If true, then only one item can be selected at a time, i.e. multi-select is disabled.
	 * @parm dialogSize The dimensions of the displayed dialog component.  See the setSize() methods documentation for more information.
	 * @param defaultSelectedOptions A list of default selected options. See the setDefaultSelectedOptions() method documentation for more information.
	 */
	public MultiChooserDialog(String title, List<TOption> options, String toolTip, boolean isSingleSelect, Dimension dialogSize, List<TOption> defaultSelectedOptions ) {
		this(title, options, toolTip, isSingleSelect);
		setSize(dialogSize);
		setDefaultSelectedOptions(defaultSelectedOptions);
	}
	
	/**
	 * Display the selection dialog and return a list of the choices of the configured options.  
	 * The "Select All" button is not displayed if single selection is enabled.
	 * The list will be empty if no choices where made or if the dialog was cancelled or forcibly closed.
	 * @return  The chosen options in the same order as was given to the constructor.
	 */
	public List<TOption> choose() {
		JPanel pnlDisplay = new JPanel();
		pnlDisplay.setLayout(new BorderLayout());
			
		Box bxList = Box.createVerticalBox();
		bxList.setToolTipText(this.toolTip);
		JPanel pnlControl = new JPanel();
		Box bxControl = Box.createHorizontalBox();
		JButton btnSelectAll = new JButton("Select All");
		btnSelectAll.setToolTipText("Select all the options.");
		JButton btnClearAll = new JButton("Clear Selection"+ (isSingleSelect ? "":"s"));
		btnClearAll.setToolTipText("Unselect all the options.");
		if(!isSingleSelect) {
			bxControl.add(btnSelectAll); // No select-all button when single-select is enabled.
			bxControl.add(Box.createHorizontalStrut(5));
		}
		bxControl.add(btnClearAll);
		pnlControl.add(bxControl);
		pnlDisplay.add(pnlControl, BorderLayout.NORTH);
		
		Map<JCheckBox, TOption> itemMap = new HashMap<>();
		options.forEach((item)->{
			if(null==item) {
				System.err.println("[MultiChooserDialog.choose()] ERROR! Null option encountered and ignored.");
			}
			else {
				JCheckBox cb = new JCheckBox(item.toString());
				if(defaultSelectedOptions.contains(item)) {
					cb.setSelected(true);
				}
				
				if(isSingleSelect) {
					cb.addActionListener((evt)->{
						if(cb.isSelected()) {
							btnClearAll.doClick();
							cb.setSelected(true);
						}
							
					});
				}
	
				itemMap.put(cb, item);
				bxList.add(cb);
			}
		});
		
		pnlDisplay.add(new JScrollPane(bxList), BorderLayout.CENTER);
					
		btnSelectAll.addActionListener((evt)->{
			itemMap.keySet().forEach((cb)->{
				cb.setSelected(true);
			});
		});

		btnClearAll.addActionListener((evt)->{
			itemMap.keySet().forEach((cb)->{
				cb.setSelected(false);
			});
		});		      
		
	
		JOptionPane optionPane = new JOptionPane(pnlDisplay, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = optionPane.createDialog(title);
		setDialogSizeFn.accept(dialog); // Set the size if necessary
		dialog.setResizable(true);
		dialog.setVisible(true);
		dialog.validate();  // Double-check that everything is visible.
		
		// Check of dialog was cancelled or forcibly closed.
        if(null!= optionPane.getValue() && optionPane.getValue().equals(JOptionPane.OK_OPTION)) {
	        // Get selected options in the same order as given.
	        List<TOption> results = new ArrayList<>(options);
	        itemMap.entrySet().forEach((entry)->{
	        	if(!entry.getKey().isSelected()) {
	        		results.remove(entry.getValue());
	        	}
	        });
			
	        return results;
        }
        else {
        	return List.of(); // No results if cancelled or forcibly closed.
        }
	}
	
	/**
	 * Set the size of the displayed dialog box.  If either the given width or height is 0 or negative, 
	 * the default width or height will be used respectively.
	 * @param width The width of the displayed dialog.
	 * @param height The height of the displayed dialog.
	 */
	public void setSize(int width, int height) {
		setDialogSizeFn = (dialogComp)->{
			dialogComp.setSize(0>=width ? dialogComp.getWidth(): width, 0>=height ? dialogComp.getHeight():height);
		};
	}
	
	/**
	 * Set the size of the displayed dialog box.
	 * If either the given dimension's width or height is 0 or negative, 
	 * the default width or height will be used respectively.
	 * @param dim The dimensions of the displayed dialog.
	 */
	public void setSize(Dimension dim) {
		setSize(dim.width, dim.height);
	}
	
	/**
	 * Set the options that are selected by default.  If single select mode is enabled, only the
	 * first item of the list will be used.   Use List.of(single_option) to make a one-element list.
	 * Any included option here that is not in the supplied list of all options will be ignored. 
	 * @param defaultSelectedOptions A list of options selected by default.
	 */
	public void setDefaultSelectedOptions(List<TOption> defaultSelectedOptions) {
		if(isSingleSelect && 0<defaultSelectedOptions.size()) {
			this.defaultSelectedOptions = List.of(defaultSelectedOptions.get(0));
		}
		else {
			this.defaultSelectedOptions  = defaultSelectedOptions;
		}
	}
}

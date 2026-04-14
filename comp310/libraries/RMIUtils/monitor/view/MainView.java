package provided.rmiUtils.monitor.view;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.TitledBorder;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import provided.utils.struct.IDyad;

/**
 * The main view
 * @author swong
 *
 */
public class MainView extends JFrame {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -4506600064070453630L;

	/**
	 * The content panel of the frame
	 */
	private JPanel contentPane;
	
	/**
	 * The panel that displays the data
	 */
	private final JPanel pnlDisplay = new JPanel();
	
	/**
	 * The scrollpane for the data
	 */
	private final JScrollPane spnDisplay = new JScrollPane();
	
	/**
	 * The table model for the data
	 */
	private DefaultTableModel tblMdlData = new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Bound Name","Bound Object"
			}
		); 
	
	/**
	 * The data table
	 */
	private final JTable tblData = new JTable(tblMdlData); //new JTable(new JTableModel()); //
	
	/**
	 * Automatic column width adjuster
	 */
	private TableColumnAdjuster tblColAdjuster;

	/**
	 * The view to model adapter
	 */
	private IView2ModelAdapter v2mAdpt;
	


	/**
	 * Create the frame.
	 * @param v2mAdpt The view to model adapter
	 */
	public MainView(IView2ModelAdapter v2mAdpt) {
		this.v2mAdpt = v2mAdpt;
		initGUI();
	}
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setTitle("RMI Registry Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		pnlDisplay.setBorder(new TitledBorder(null, "Bound Items", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		contentPane.add(pnlDisplay, BorderLayout.CENTER);
		pnlDisplay.setLayout(new BorderLayout(0, 0));
		pnlDisplay.add(spnDisplay);
		spnDisplay.setViewportView(tblData);
		
		tblData.getColumn("Bound Name").setCellRenderer(new ButtonRenderer());
		tblData.getColumn("Bound Name").setCellEditor(new ButtonEditor(new JCheckBox(), (value)->{
			System.out.println("Performing action on: "+value);
			v2mAdpt.unbind(value);
		}));

		tblData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tblColAdjuster = new TableColumnAdjuster(tblData);
		tblColAdjuster.setColumnDataIncluded(true);
		tblColAdjuster.setDynamicAdjustment(true);
		tblColAdjuster.adjustColumns();
		
	}



	/**
	 * Start the view
	 */
	public void start() {
		setVisible(true);
	}
	

	/**
	 * Display the given list of bound_name-bound_object dyads on the JTable
	 * @param items The list of dyads to display
	 */
	public void showItems(List<IDyad<String, Object>> items) {
		// Clear the table
		for(int i=tblMdlData.getRowCount()-1; i>=0; i--) {
			tblMdlData.removeRow(i);
		}
		
		// Fill the table with the items
		for(IDyad<String, Object> dyad: items) {
			tblMdlData.addRow(new Object[] {dyad.getFirst(),dyad.getSecond()});
		}

	}
}
//https://stackoverflow.com/questions/13833688/adding-jbutton-to-jtable
/**
 * @author swong
 * Renderer for a button in a JTable
 */
class ButtonRenderer extends JButton implements TableCellRenderer {

    /**
	 * For serialization
	 */
	private static final long serialVersionUID = 5534130139084405664L;

	/**
	 * Constructor for the class
	 */
	public ButtonRenderer() {
        setOpaque(true);
        setToolTipText("Click to unbind this name.");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}
/**
 * Editor for a button in a JTable.  Enables click behavior on the button
 * @author swong
 *
 */
class ButtonEditor extends DefaultCellEditor {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7586081336069160125L;
	/**
	 * A button to attach an action
	 */
	protected JButton button;
	/**
	 * The button's label
	 */
    private String label;
    /**
     * True if the button was clicked
     */
    private boolean isPushed;
    
    /**
     * The action to perform when the button is clicked.
     */
	private Consumer<String> action;

	/**
	 * Constructor for the class
	 * @param checkBox A Checkbox that is needed by the superclass constructor to detect whether or not the field has been clicked.
	 * @param action The action for the button to perform.   Will be given the label of the button as input.
	 */
    public ButtonEditor(JCheckBox checkBox, Consumer<String> action) {
        super(checkBox);
        this.action = action;
        button = new JButton();
       
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int response = JOptionPane.showOptionDialog(button, "Are you sure you want to unbind '"+label+"' from the Registry?", "Unbind Name", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, 
            		new String[] {"I'm sure!", "Cancel"},"Cancel");
            if(0 == response) {
            	action.accept(label);
            }
            else {
            	System.out.println("Button action canceled.");
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}




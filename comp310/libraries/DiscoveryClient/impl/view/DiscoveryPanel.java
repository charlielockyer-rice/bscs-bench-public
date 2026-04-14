package provided.discovery.impl.view;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JCheckBox;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A convenience GUI for working with the discovery server.
 * Usage of this class is NOT required. 
 * @author Stephen Wong (c) 2018
 *
 * @param <TEndPoint>  The type of entity on the list of end points.
 */
public class DiscoveryPanel<TEndPoint> extends JPanel {
	/**
	 * For Serializable
	 */
	private static final long serialVersionUID = -8282887406382793375L;
	/**
	 * Panel to hold the connection button, etc.
	 */
	private final JPanel pnlConnect = new JPanel();
	/**
	 * Panel to hold end point info and control
	 */
	private final JPanel pnlEndPoints = new JPanel();
	/**
	 * Textfield to enter a category valaue
	 */
	private final JTextField tfCategory = new JTextField();
	/**
	 * Checkbox to indicate whether to connect as a watcher only (checked) or as a registered endpoint (unchecked)
	 */
	private final JCheckBox cbxWatchOnly = new JCheckBox("Watch Only");
	/**
	 * Button to connect to the discovery server machine
	 */
	private final JButton btnConnectDiscovery = new JButton("Connect");
	/**
	 * Button to use the selected endpoint to get a stub from a remote Registry
	 */
	private final JButton btnGetSelectedEndpoint = new JButton("Get Selected Endpoint");
	/**
	 * Scrollpane for list of endpoints
	 */
	private final JScrollPane spnEndPoints = new JScrollPane();
	/**
	 * Internal model for list of endpoints
	 */
	private DefaultListModel<TEndPoint> lstEndPointsModel = new DefaultListModel<TEndPoint>();
	/**
	 * List of endpoints
	 */
	private final JList<TEndPoint> lstEndPoints = new JList<TEndPoint>(lstEndPointsModel);
	/**
	 * The adapter from this panel back towards the model.  The adapter may connect to either the rest of the view 
	 * or the model, depending on the system.
	 */
	private IDiscoveryPanelAdapter<TEndPoint> adpt;
	
	/**
	 * If true, allows end points to be published.  If false, will check the watch only checkbox and disable it. 
	 */
	private boolean allowEndPtPublish = true;
	
	/**
	 * If true, allows connections to published endpoints.  If false, will disable the connect to endpoint button.
	 */
	private boolean allowEndPtConnect = true;

	/**
	 * Convenience constructor that creates the panel with allowEndPtPublish = true and allowEndPtConnect = true.
	 * USAGE:  For apps that are both publishing an endpoint and connecting to published endpoints.
	 * Equivalent to calling DiscoveryPanel(adpt, true, true).
	 * @param adpt An adapter to the rest of the system where the actual discovery server operations will take place.
	 * @wbp.parser.constructor
	 */
	public DiscoveryPanel(IDiscoveryPanelAdapter<TEndPoint> adpt) {
		this(adpt, true, true);
	}
	
	/**
	 * Create the panel with options to control whether or not a local endpoint is published and/or if connections to published endpoints is allowed.
	 * USAGE: For apps that restrict the ability to publish and endpoint or connect to published endpoints.
	 * <br/>Examples:  
	 * <ul>
	 * <li>Client only:  No published local endpoint but can connect to published endpoints: allowEndPtPublish = false, allowEndPtConnect = true</li>
	 * <li>Server only:  Published local endpoint but cannot connect to published endpoints: allowEndPtPublish = true, allowEndPtConnect = false</li>
	 * <li>Client + Server:  Published local endpoint and can connect to published endpoints: allowEndPtPublish = true, allowEndPtConnect = true</li>
	 * <li>Monitoring the discovery server only:  No published local endpoint and cannot connect to published endpoints: allowEndPtPublish = false, allowEndPtConnect = false</li>
	 * </ul>
	 * @param adpt An adapter to the rest of the system where the actual discovery server operations will take place.
	 * @param allowEndPtPublish If true, then force the panel and its discovery server connection to be watch only.  Used when not publishing an endpoint.
	 * @param allowEndPtConnect If true, the disable the ability to connect to an endpoint in the discovery server.   Used when only wish to publish an endpoint but not connect to one.
	 */
	public DiscoveryPanel(IDiscoveryPanelAdapter<TEndPoint> adpt, boolean allowEndPtPublish, boolean allowEndPtConnect) {
		this.adpt = adpt;
		this.allowEndPtPublish = allowEndPtPublish;
		this.allowEndPtConnect = allowEndPtConnect;
		tfCategory.setToolTipText("Enter a name for the desired group of available endpoints.");
		
		tfCategory.setEnabled(false);
		tfCategory.setBorder(new TitledBorder(null, "Category", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tfCategory.setColumns(10);

		initGUI();
	}
	
	
	/**
	 * Initialize the GUI
	 */
	private void initGUI() {
		setBorder(new TitledBorder(null, "Discovery Server", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		
		add(pnlConnect, BorderLayout.WEST);
		pnlConnect.setLayout(new GridLayout(0, 1, 0, 0));
		
		pnlConnect.add(tfCategory);
		cbxWatchOnly.setToolTipText("If checked, will not publish an endpoint to the discovery server.");
		cbxWatchOnly.setEnabled(false);
		
		
		pnlConnect.add(cbxWatchOnly);
		btnConnectDiscovery.setToolTipText("Connect to the discovery server under the given category.");
		btnConnectDiscovery.setEnabled(false);
		btnConnectDiscovery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String category = tfCategory.getText();
				if(null==category || category.isEmpty()) { // Reject blank categories
					JOptionPane.showMessageDialog(DiscoveryPanel.this, "Please enter a category!");
				}
				else {
					adpt.connectToDiscoveryServer(tfCategory.getText(), cbxWatchOnly.isSelected() ,(endPts)->{
						SwingUtilities.invokeLater(()->{
							// When all the elements of a JList are replaced, the currently selected item gets lost.
							TEndPoint selectedItem = lstEndPoints.getSelectedValue();  // save the currently selected item.
							lstEndPointsModel.clear();   // Clear the list in anticipation of the incoming list
							endPts.forEach((endPt)->{ lstEndPointsModel.addElement(endPt);});  // Add the new elements to the list.
							int selectedIndex = lstEndPointsModel.indexOf(selectedItem); // Find the original selected item in the new list
							if (0 <= selectedIndex ) {   
								lstEndPoints.setSelectedIndex(selectedIndex);  // Found it! Set that element to be selected.
							}
						});
					});
					// Disable the main connection button and checkbox
					tfCategory.setEnabled(false);
					cbxWatchOnly.setEnabled(false);
					btnConnectDiscovery.setEnabled(false);
					
					btnGetSelectedEndpoint.setEnabled(DiscoveryPanel.this.allowEndPtConnect);
				}
			}
			
		});
		
		pnlConnect.add(btnConnectDiscovery);
		
		add(pnlEndPoints, BorderLayout.CENTER);
		pnlEndPoints.setLayout(new BorderLayout(0, 0));
		btnGetSelectedEndpoint.setToolTipText("Retrieve the selected endpoint data from the discovery server and obtain the associated Remote object if so configured.");
		btnGetSelectedEndpoint.setEnabled(false);
		btnGetSelectedEndpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TEndPoint endPt = lstEndPoints.getSelectedValue();
				if(null==endPt) {
					JOptionPane.showMessageDialog(DiscoveryPanel.this, "Please select an available end point!");
				}
				else {
					adpt.connectToEndPoint(endPt);
				}
			}
		});
		
		pnlEndPoints.add(btnGetSelectedEndpoint, BorderLayout.SOUTH);
		
		pnlEndPoints.add(spnEndPoints, BorderLayout.CENTER);
		lstEndPoints.setToolTipText("The available endpoints under the specified category.");
		lstEndPoints.setVisibleRowCount(6);
		lstEndPoints.setBorder(new TitledBorder(null, "Registered EndPoints", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		spnEndPoints.setViewportView(lstEndPoints);
		
	}

	/**
	 * Starts this discovery panel with an enabled connect button and 
	 * an empty category and the watch only box unchecked. 
	 * The discovery panel should not be started until the system is ready to use it, 
	 * e.g. the RMI subsystem is already operational. 
	 */
	public void start() {
		start("", false);
	}
	
	
	/**
	 * Starts this discovery panel with an enabled connect button and 
	 * the given default values for the category and watch only checkbox.
	 * The discovery panel should not be started until the system is ready to use it, 
	 * e.g. the RMI subsystem is already operational. 
	 * @param defaultCategory   The default value for the category field
	 * @param defaultWatchOnly  The default value for the Watch Only checkbox.   Not used if panel was construct
	 */
	public void start(String defaultCategory, boolean defaultWatchOnly) {
		tfCategory.setText(defaultCategory);
		cbxWatchOnly.setSelected(defaultWatchOnly || !this.allowEndPtPublish);
		tfCategory.setEnabled(true);
		cbxWatchOnly.setEnabled(this.allowEndPtPublish);
		btnConnectDiscovery.setEnabled(true);	
		btnGetSelectedEndpoint.setEnabled(false);
	}
}

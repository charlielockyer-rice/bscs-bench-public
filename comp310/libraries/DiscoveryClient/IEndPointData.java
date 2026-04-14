package provided.discovery;

import java.io.Serializable;

/**
 * Represents the INFORMATION necessary to connect to the Registry of a particular machine and retrieve a stub with
 * a given bound name.   The machine's Registry port is assumed to be IRMI_Defs.REGISTRY_PORT.
 * IEndPoints are "naturally ordered", i.e. they are Comparable and thus can be sorted without using 
 * an external Comparator.
 * Note that even if this end point's isActive() returns true, the RMI stub to whom this end point data refers may 
 * not be available if the stub's provider had crashed within the grace period allowed by the remote 
 * discovery server machine.
 * @author Stephen Wong (c) 2018
 *
 */
public interface IEndPointData extends Comparable<IEndPointData>, Serializable {
	
	/**
	 * Get the friendly name associated with this end point.
	 * @return  a name string
	 */
	String getFriendlyName();
	
	/**
	 * Get the category associated with this end point.
	 * @return a category string
	 */
	String getCategory();
	
	/**
	 * Get the IP address associated whose Registry has the RMI stub object associated with this end point.
	 * @return  The IP address of the associated Registry
	 */
	String getAddress();
	
	/**
	 * Get the Registry bound name of the RMI stub associated with this end point.
	 * @return  The bound name of the stub in the associated Registry
	 */
	String getBoundName();
	
	/**
	 * Checks if the end point is still active.  
	 * Call this method JUST BEFORE USING the end point.   However, even then, then end point may still
	 * not be valid if end point location has crashed within the allowed grace period.   So be sure to 
	 * catch possible connection errors when using this end point data to retrieve a stub from a remote Registry.
	 * @return True if the end point is still registered on the remote discovery server machine.
	 */
	boolean isActive();
	
}

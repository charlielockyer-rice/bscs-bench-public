package provided.datapacket;

import java.util.Vector;


/**
 * Concrete IVDataPacket composite data type for use in data packets, DataPacket&lt;IVDataPacket&gt;, 
 * implemented as a Vector of ADataPackets. <br>
 * Note that Vector&lt;ADataPacket&gt; cannot be used as a data packet data type 
 * directly because type erasure prevents the distinction between 
 * Vector&lt;A&gt; and Vector&lt;B&gt; -- that is, they produce the same Class object.
 * <br>
 * Usage:<br>
 * <pre>
 *      // make the vector of data packets
 *      VDataPacket vdp = new VDataPacket();
 *      vdp.add(datapacket1);
 *      vdp.add(datapacket2);
 *      vdp.add(datapacket3);
 *      // etc
 *      // make the composite data packet
 *      ADataPacket vd = new DataPacket&lt;VDataPacket, SenderType&gt;(vdp, aSender);
 * </pre>
 * This class is a vector of the abstract data packets, ADataPacket.  If a vector of more specific 
 * types of data packets is desired, a custom class that is a sub-class of the desired
 * Vector type should be used instead of this class.   Composites made as such would be 
 * distinguishable as per their held types.
 * 
 * @author Stephen Wong (c) 2018
 *
 */
public class VDataPacket extends Vector<ADataPacket> implements IVDataPacket{

	/**
	 * Version number for serialization
	 */
	private static final long serialVersionUID = -860544422905072718L;

}

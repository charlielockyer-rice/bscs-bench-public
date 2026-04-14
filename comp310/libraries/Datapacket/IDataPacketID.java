package provided.datapacket;

import java.io.Serializable;

/**
 * A host ID value compatible with ADataPackets
 * 
 * The implementation of this interface is immaterial.   It only has to follow
 * the Java rules for object equality, i.e. the rules governing the 
 * behavior of the hashCode() and equals() methods.
 * 
 * This enables two completely different ID value implementations to be used 
 * to represent the same data type, so long as they are "equal".
 * 
 * @author Stephen Wong (c) 2018 * ----------------------------------------------
 * The type of the host ID for an ADataPacket 
 */
public interface IDataPacketID extends Serializable {

}
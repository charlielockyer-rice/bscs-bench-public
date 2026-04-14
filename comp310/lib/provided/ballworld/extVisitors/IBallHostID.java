package provided.ballworld.extVisitors;

import java.io.Serializable;

/**
 * A host ID value compatible with IBallHosts
 * 
 * The implementation of this interface is immaterial.   It only has to follow
 * the Java rules for object equality, i.e. the rules governing the 
 * behavior of the hashCode() and equals() methods.
 * 
 * This enables two completely different ID value implementations to be used 
 * to represent the same data type, so long as they are "equal".
 * 
 * @author swong (c) 2022
 */
public interface IBallHostID extends Serializable {

}

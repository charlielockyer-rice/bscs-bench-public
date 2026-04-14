package provided.utils.file;

import java.io.InputStream;
import java.io.Serializable;

/**
 * A Serializable container for the byte stream of a file.
 * Used to transmit a file's contents across the network and then 
 * be used as if the file were read from the disk in the form 
 * of an java.io.InputStream.
 * @author swong
 *
 */
public interface IFileContents extends Serializable {
	/**
	 * A name associated with the file, e.g. its filename without the rest of its path.
	 * This name is just for reference/identification purposes.
	 * @return A name to be associated with the contents.
	 */
	public String getName();

	/**
	 * Returns any additional information about the file contents.  
	 * A typical usage would for an image file, for the info to be 
	 * "jpg" or "png" to tell what kind of image data was in the contents.  
	 * @return The stored info string.
	 */
	public String getInfo();

	/**
	 * The internally stored raw byte stream
	 * @return  The byte stream of the file
	 */
	public byte[] getBytes();

	/**
	 * Create an InputStream that sources the file contents.   The use of this 
	 * stream simulates the reading of the file from the disk but without 
	 * actually involving any such storage access.   This feature would be used 
	 * by the receiver of this object to load the file contents into something that
	 * normally wants to read that data from the disk.
	 * @return An InputStream
	 */
	public InputStream getInputStream();
}

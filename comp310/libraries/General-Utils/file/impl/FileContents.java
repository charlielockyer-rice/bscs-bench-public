package provided.utils.file.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import provided.utils.file.IFileContents;

/**
 * A concrete implementation of IFileContents.  For convenience and simplicity, 
 * this class defines equality if the names are equal, not the actual contents.
 * @author swong
 */
public class FileContents implements IFileContents {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 2685311527734358523L;

	/**
	 * The name associated with the file contents
	 */
	private String name;

	/**
	 * The info associated with the file contents
	 */
	private String info = "";

	/**
	 * The contents of the file in byte array form.
	 */
	private byte[] fileContents;

	/**
	 * Construct an instance with an associated name and the full filename (i.e. includes 
	 * sufficient path information) to access the file's contents from the disk.   
	 * This filename is NOT stored and the FileContent object 
	 * is independent from where its contents were read.
	 * The stored info is set to an empty String.
	 * @param name The name to associate with the file contents, relative to the class that is invoking this constructor.
	 * @param filename The filename of the file that sources the contents.
	 */
	public FileContents(String name, String filename) {
		this(name, filename, "");
	}

	/**
	 * Construct an instance with an associated name, additional information about 
	 * the file contents and the full filename (i.e. includes 
	 * sufficient path information) to access the file's contents from the disk.   
	 * This filename is NOT stored and the FileContent object 
	 * is independent from where its contents were read.
	 * @param name The name to associate with the file contents.
	 * @param filename The filename of the file that sources the contents.
	 * @param info Additional information about the contents
	 */
	public FileContents(String name, String filename, String info) {
		this.name = name;
		this.info = info;

		try {
			InputStream inStream = this.getClass().getResourceAsStream(filename);

			fileContents = new byte[inStream.available()];
			inStream.read(fileContents);
		} catch (Exception e) {
			System.err.println(
					"[FileContents(" + name + ", " + filename + ")] Exception while reading file contents: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public byte[] getBytes() {
		return this.fileContents;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.fileContents);
	}

	/**
	 * Overridden to perform equality based on the name only
	 * @return true if the names are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof IFileContents) {
			return this.name.contentEquals(((IFileContents) other).getName());
		}
		return false;
	}

	/**
	 * Overridden to return the hashCode() of the name.
	 * @return The hashCode() of the name.
	 */
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

}

/*
 * Copyright (c) 1996, 1996, 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * Modified 10/11/17 by swong to include message display strategies
 */
package provided.rmiUtils.classServer;

import java.io.*;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMI_Defs;



/**
 * The ClassFileServer implements a ClassServer that
 * reads class files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 */
public class ClassFileServer extends ClassServer {

	
	/**
	 * path to the default package.
	 */
	private String classpath;

	/**
	 * Constructs a ClassFileServer. The shared logger from ILoggerControl is used.
	 *
	 * @param port The port to use for the server
	 * @param classpath The classpath where the server locates classes
	 * @throws IOException if cannot listen on specified port
	 */
	public ClassFileServer(int port, String classpath) throws IOException
	{
		this(port, classpath, ILoggerControl.getSharedLogger());
	}

	/**
	 * Constructs a ClassFileServer. 
	 * @param port The port to use for the server
	 * @param classpath The classpath where the server locates classes
	 * @param logger The logger to use	 
	 * @throws IOException  If there is an error creating the server.
	 */
	public ClassFileServer(int port, String classpath, ILogger logger) throws IOException
	{
		super(port, logger);
		this.classpath = classpath;
		logger.log(LogLevel.INFO, "port = "+ port+", classpath = "+classpath);
	}	

	/**
	 * Returns an array of bytes containing the bytecodes for
	 * the class represented by the argument <b>path</b>.
	 * The <b>path</b> is a dot separated class name with
	 * the ".class" extension removed.
	 *
	 * @return the bytecodes for the class
	 * @exception ClassNotFoundException if the class corresponding
	 * to <b>path</b> could not be loaded.
	 */
	public byte[] getBytes(@SuppressWarnings("exports") PathSuffix pathSuffix) throws IOException, ClassNotFoundException  {
//	public byte[] getBytes(String path) throws IOException, ClassNotFoundException  {

		logger.log(LogLevel.INFO, "Reading on path = " + pathSuffix.path+pathSuffix.suffix);
		String filename = classpath + File.separator + pathSuffix.path.replace('.', File.separatorChar) + pathSuffix.suffix;
		File f = new File(filename);
//		File f = new File(classpath + File.separator + pathSuffix.path.replace('.', File.separatorChar) + ".class");
		int length = (int)(f.length());
		if (length == 0) {
			throw new IOException("[ClassFileServer.getBytes()] File length is zero: " + filename);
		} 
		else {
			FileInputStream fin = new FileInputStream(f);
			DataInputStream in = new DataInputStream(fin);
			byte[] bytecodes = new byte[length];
			in.readFully(bytecodes);
			in.close();
			return bytecodes;
		}
	}

	/**
	 * This method is for testing purposes only.   
	 * In general, an application will instantiate its own 
	 * ClassFileServer instance.
	 * 
	 * Main method to create the class server that reads
	 * class files. This takes two command line arguments, the
	 * port on which the server accepts requests and the
	 * root of the classpath. To start up the server: <br><br>
	 *
	 * <code>   java ClassFileServer &lt;port&gt; &lt;classpath&gt;
	 * </code><br><br>
	 *
	 * The codebase of an RMI server using this webserver would
	 * simply contain a URL with the host and port of the web
	 * server (if the webserver's classpath is the same as
	 * the RMI server's classpath): <br><br>
	 *
	 * <code>   java -Djava.rmi.server.codebase=http://zaphod:2001/ RMIServer
	 * </code> <br><br>
	 *
	 * You can create your own class server inside your RMI server
	 * application instead of running one separately. In your server
	 * main simply create a ClassFileServer: <br><br>
	 *
	 * <code>   new ClassFileServer(port, classpath);
	 * </code>
	 * @param args args[0] = port number, args[1] = classpath
	 */
	public static void main(String args[]) 
	{
		ILogger logger = ILoggerControl.getSharedLogger();
		int port = IRMI_Defs.CLASS_SERVER_PORT_SERVER;
		String classpath = "";

		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}

		if (args.length >= 2) {
			classpath = args[1];
		}

		try {
			new ClassFileServer(port, classpath, logger);
		} catch (IOException e) {
			logger.log(LogLevel.ERROR, "Unable to start ClassServer: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

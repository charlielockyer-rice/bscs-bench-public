/**
 * 
 */
/**
 * The module for this assignment.   Please change the module name below to match the project name.
 * @author swong
 *
 */
module hw07 {
	requires java.desktop;
	requires transitive java.rmi;
	exports provided.remoteCompute.compute;
	exports provided.remoteCompute.client.model.taskUtils;
	
	exports provided.discovery;
	exports provided.rmiUtils;
	exports provided.logger;
}
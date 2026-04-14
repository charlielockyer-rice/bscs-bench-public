/**
 * 
 */
/**
 * The module for this assignment.   Please change the module name below to match the project name.
 * @author swong
 *
 */
module comp310_s22_chatapp_comp310_s22_chatapp_03 {
	requires java.desktop;
	requires transitive java.rmi;
	requires java.base;
	
	exports provided.discovery;
	exports provided.rmiUtils;
	exports provided.logger;
	exports provided.datapacket;
	exports provided.pubsubsync;
	exports provided.pubsubsync.impl;
	
	/**
	 *  Add exports for at least the following package and necessary sub-packages: 
	 *   - common
	 *   - student-defined message types and implementations
	 *   - any serialized support packages used by message implementations
	 */
	exports common.adapter;
	exports common.connector;
	exports common.connector.messageType;
	exports common.connector.messageType.exception;
	exports common.receiver;
	exports common.receiver.messageType;
	exports common.receiver.messageType.exception;

	
}
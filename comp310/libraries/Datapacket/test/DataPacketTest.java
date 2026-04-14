/**
 * The following is example test code to show the usage of datapackets and associated visitors and commands.
 * At the bottom of this file are examples of the types of specialized datapacket and visitor commands that can be defined 
 * by an API to insure consistency and type-safety.
 */
package provided.datapacket.test;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//
//
//import provided.datapacket.*;
//
//
//
///**
// * Data type definition for a message that holds an integer 
// * @author Stephen Wong (c) 2018
// *
// */
//interface IIntegerData extends IDataPacketData {
//	
//	/**
//	 * Retrieve the ID value directly from the interface.
//	 * getID() merely delegates to this method.
//	 * @return The host ID value associated with this data type.
//	 */
//	public static IDataPacketID GetID() {
//		return DataPacketIDFactory.Singleton.makeID(IIntegerData.class);
//	}
//	
//	@Override
//	public default IDataPacketID getID() {
//		return IIntegerData.GetID();
//	}
//	
//	/**
//	 * Returns the stored value
//	 * @return The stored value
//	 */
//	public int getValue();
//}
//
///**
// * An concrete implementation of the IIntegerData data type.
// * @author swong
// *
// */
//class IntegerData implements IIntegerData{
//	/**
//	 * For Serializable
//	 */
//	private static final long serialVersionUID = 4785602609261952514L;
//	/**
//	 * The stored value
//	 */
//	private int data;
//	
//	/**
//	 * Constructor for the class
//	 * @param data The value to store internally.
//	 */
//	public IntegerData(int data) {
//		this.data = data;
//	}
//	
//	@Override
//	public int getValue() {
//		return this.data;
//	}
//	
//	@Override
//	public String toString() {
//		return ""+data;
//	}
//}
//
//
///**
// * Data type definition for a message that holds a double 
// * @author swong
// *
// */
//interface IDoubleData extends IDataPacketData {
//	
//	/**
//	 * Retrieve the ID value directly from the interface.
//	 * getID() merely delegates to this method.
//	 * @return The host ID value associated with this data type.
//	 */
//	public static IDataPacketID GetID() {
//		return DataPacketIDFactory.Singleton.makeID(IDoubleData.class);
//	}
//	
//	@Override
//	public default IDataPacketID getID() {
//		return IDoubleData.GetID();
//	}
//	
//	/**
//	 * Returns the stored value
//	 * @return The stored value
//	 */
//	public double getValue();
//}
//
//
///**
// * Factory to hide the implementation of the IDoubleData data type
// * @author swong
// *
// */
//class DoubleDataFac {
//	/**
//	 * Singleton instance
//	 */
//	public static final DoubleDataFac Singleton = new DoubleDataFac();
//	
//	/**
//	 * Private constructor for singleton
//	 */
//	private DoubleDataFac() {}
//	
//	/**
//	 * Instantiate an instance of the IDoubleData data type 
//	 * using a hidden internal implementation.
//	 * @param data The value to hold internally
//	 * @return An IDoubleData instance.
//	 */
//	public IDoubleData make(final double data) {
//		return new IDoubleData() {
//
//			/**
//			 * For Serializable
//			 */
//			private static final long serialVersionUID = 6291034702637724683L;
//
//			@Override
//			public double getValue() {
//				return data;
//			}
//			
//			@Override
//			public String toString() {
//				return ""+data;
//			}
//		};
//	}
//}
//
///**
// * Data type definition for a message that holds a string 
// * @author swong
// *
// */
//interface IStringData extends IDataPacketData {
//	/**
//	 * Retrieve the ID value directly from the interface.
//	 * getID() merely delegates to this method.
//	 * @return The host ID value associated with this data type.
//	 */
//	public static IDataPacketID GetID() {
//		return DataPacketIDFactory.Singleton.makeID(IStringData.class);
//	}
//	
//	@Override
//	public default IDataPacketID getID() {
//		return IStringData.GetID();
//	}	
//	
//	/**
//	 * Factory method to create the data type from an input value.   
//	 * The problem with this technique is while it does hide the data type
//	 * implementation, defining it at this interface level means that 
//	 * the implementation is invariant, which is an unnecessary restriction.
//	 * @param data  The string to store in the message
//	 * @return An IStringData object
//	 */
//	static IStringData make(final String data) {
//		return new IStringData() {
//			/**
//			 * For Serializable
//			 */
//			private static final long serialVersionUID = 8312916347060215743L;
//
//			@Override
//			public String toString() {
//				return data;
//			}			
//		};
//	}
//}
//
//
/**
 * JUnit tests for the datapacket package
 * @author Stephen Wong (c) 2010
 *
 */
public class DataPacketTest  {
////	public class DataPacketTest extends TestCase {
//
//	
//	/**
//	 * Dummy sender object for the datapacket
//	 */
//	ISender sender = new ISender(){};
//	
//	/**
//	 * Tests of the datapacket
//	 * sender stub is not tested
//	 */
//	@Test
//	public void testDataPacket() {
//		ADataPacket ds = new MyDataPacket<IStringData>(IStringData.make("Stuff"), sender);
//		ADataPacket di = new MyDataPacket<IIntegerData>(new IntegerData(42), sender);
//		ADataPacket dd = new MyDataPacket<IDoubleData>(DoubleDataFac.Singleton.make(3.1415926), sender);
//
//		// make the vector of data packets
//		VDataPacket vdp = new VDataPacket();
//		vdp.add(ds);
//		vdp.add(di);
//		vdp.add(dd);
//		
//		// make the composite data packet
//		ADataPacket vd = new MyDataPacket<VDataPacket>(vdp, sender);
//		
//		System.out.println("class = "+ vd.getClass());
//		
//		final DataPacketAlgo<String, String> algo = new DataPacketAlgo<String, String>(new AMyDataPacketAlgoCmd<String, IDataPacketData, String>(){
//
//			private static final long serialVersionUID = -3838770346677745909L;
//
//			@Override
//			public String apply(IDataPacketID id, MyDataPacket<IDataPacketData> host, String... params) {
//				Object x = host.getData();
//				return "Default case called. data = "+ x;
//			}	
//
//			@Override
//			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
//				// not used.
//			}
//		});
//				
//		
//		algo.setCmd(IStringData.GetID(), new AMyDataPacketAlgoCmd<String, IStringData, String>(){
//
//			private static final long serialVersionUID = 7417327345957770087L;
//
//			@Override
//			public String apply(IDataPacketID id, MyDataPacket<IStringData> host, String... params) {
//				IStringData x = host.getData();
//				return "String case called. data = "+ x;
//			}
//
//			@Override
//			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
//				// not used.
//			}
//		});
//		
//		algo.setCmd(IIntegerData.GetID(), new AMyDataPacketAlgoCmd<String, IIntegerData, String>(){
//
//			private static final long serialVersionUID = 5948981304362218691L;
//
//			@Override
//			public String apply(IDataPacketID id, MyDataPacket<IIntegerData> host, String... params) {
//				IIntegerData x = host.getData();
//				return "Integer case called. data = "+x.getValue();
//			}
//
//			@Override
//			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
//				// not used.
//			}
//		});
//		
////		algo.setCmd(VDataPacket.class, VDataPacket.makeMapCmd(algo));
//		
//		algo.setCmd(IVDataPacket.GetID(), new AMyDataPacketAlgoCmd<String, IVDataPacket, String>(){
//
//			private static final long serialVersionUID = -5626422695894697578L;
//
//			// Note that since the visitor itself does not know the specific datapacket type the commands coerce their hosts into, the static method, VDataPacket.makeMapCmd(algo) 
//			// cannot return an AMyDataPacketAlgoCmd object.  Instead, it returns the more general ADataPacketAlgoCmd which is still compatible with the apply() method
//			// input parameters.
//			private ADataPacketAlgoCmd<List<String>, IVDataPacket, String, ICmd2ModelAdapter, DataPacket<IVDataPacket, ISender>> cmd = IVDataPacket.makeMapCmd(algo);
//			
//			
//			@Override
//			public String apply(IDataPacketID id, MyDataPacket<IVDataPacket> host, String... params) {
//				List<String> results = cmd.apply(id, host, params);
//				String result = "{\n";
//				for(String s: results ){
//					result += "  "+ s + "\n";
//				}
//				result += "}";
//				return "Composite case called. result = "+result;
//			}
//			@Override
//			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
//				// not used.
//			}
//		});
//		
//		String s = ds.execute(algo);
//		assertEquals("String case called. data = Stuff", s, "ds.execute(algo)");
//		System.out.println("ds.execute(algo) = "+s);
//		s = di.execute(algo);
//		assertEquals("Integer case called. data = 42", s, "di.execute(algo)");
//		System.out.println("di.execute(algo) = "+s);
//		s = dd.execute(algo);
//		assertEquals("Default case called. data = 3.1415926", s, "dd.execute(algo)");
//		System.out.println("dd.execute(algo) = "+s);
//		s = vd.execute(algo);
//		assertEquals("Composite case called. result = {\n  String case called. data = Stuff\n  Integer case called. data = 42\n  Default case called. data = 3.1415926\n}", s, "vd.execute(algo)");
//		System.out.println("vd.execute(algo) = "+s);
//		
//		
//	}
//
}
//
///***********************************************************************************************
// * THE FOLLOWING CLASSES WOULD TYPICALLY BE DEFINED BY THE A COMMON API
// ***********************************************************************************************/
//
///**
// * Dummy adapter to local model.   Never used explicitly above but needed for typing.
// * @author Stephen Wong
// *
// */
//interface ICmd2ModelAdapter {
//	// Normally has the methods the API specifies that the adapter to the local model must have.
//}
//
//
///**
// * Dummy sender.   Never used explicitly above but needed for typing.
// * @author Stephen Wong
// *
// */
//interface ISender {
//	// Normally has the methods that the API specifies a sender must have.
//}
//
///**
// * Type-narrowed data packet that uses ISender type senders
// * @author Stephen Wong
// *
// * @param <D>  The type of data held by the data packet
// */
//class MyDataPacket<D extends IDataPacketData> extends DataPacket<D, ISender> {
//
//	/**
//	 * For Serializable
//	 */
//	private static final long serialVersionUID = 6919860798916028881L;
//
//	/**
//	 * Constructor for the class
//	 * @param data The data held by the datapacket
//	 * @param sender The sender of the datapacket
//	 */
//	public MyDataPacket(D data, ISender sender) {
//		super(data, sender);
//	}
//
//}
//
//
///**
// * Type-narrowed data packet processing command that uses MyDataPacket&lt;D&gt; and ICmd2ModelAdapters.
// * @author Stephen Wong
// *
// * @param <R>  The return type of the command
// * @param <D>  The data in the data packet
// * @param <P>  The parameter type
// */
//abstract class AMyDataPacketAlgoCmd<R, D extends IDataPacketData, P> extends ADataPacketAlgoCmd<R, D, P, ICmd2ModelAdapter, MyDataPacket<D>> {
//
//	/**
//	 * For Serializable
//	 */
//	private static final long serialVersionUID = 3493108470390456052L;
//	
//}
//
//
//
//

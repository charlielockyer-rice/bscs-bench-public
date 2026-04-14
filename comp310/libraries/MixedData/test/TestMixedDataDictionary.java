// The following code has been disabled to release student from having to have JUnit properly installed on their systems.
// TODO: To make this test code operational:
// 1. Uncomment the imports of org.junit.xxx
// 2. Uncomment the @Test attributes
// 3. Uncomment assert statements

package provided.mixedData.test;

//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import org.junit.jupiter.api.Test;

import java.util.UUID;
import provided.mixedData.*;

/**
 * Tests for the mixed data dictionary
 * @author Stephen wong (c) 2018
 *
 */
public class TestMixedDataDictionary {

	/**
	 * mixed data dictionary tests
	 */
//	@Test
	public void testMixedDataDictionary() {
		UUID uuid = UUID.randomUUID();
		MixedDataKey<Integer> intKey1 = new MixedDataKey<Integer>(uuid, "description A", Integer.class);
		MixedDataKey<String> strKey1 = new MixedDataKey<String>(uuid, "description A", String.class);  // differs only in class from intKey1
		MixedDataKey<Integer> intKey2 = new MixedDataKey<Integer>(UUID.randomUUID(), "description A", Integer.class); // differs only in UUID from intKey1
		MixedDataKey<Integer> intKey3 = new MixedDataKey<Integer>(uuid, "description B", Integer.class); // differs only in descr from intKey1
		MixedDataKey<Integer> intKey4 = new MixedDataKey<Integer>(uuid, "description A", Integer.class); // same as intKey1
		MixedDataKey<String> strKey2 = new MixedDataKey<String>(uuid, "description C", String.class);  // differs only in desc from strKey1

		MixedDataKey<Number> numKey1 = new MixedDataKey<Number>(uuid, "A number: 42", Number.class);
//		MixedDataKey<Number> numKey2 = new MixedDataKey<Number>(uuid, "A number: 3.1415926", Number.class);
		
		IMixedDataDictionary dict = new MixedDataDictionary();
		
		// test is two keys are the same.
//		assertTrue(intKey1.equals(intKey4));
//		assertFalse(intKey1.equals(intKey2));
//		assertFalse(intKey1.equals(intKey3));
//		assertFalse(intKey1.equals(strKey1));
		
		// check for value in an empty dictionary
//		assertFalse(dict.containsKey(intKey1));

		// put int in and retrieve it
		dict.put(intKey1, 42);
		int intResults = dict.get(intKey1);
//		assertEquals( 42, intResults,"Integer key" );

		// check if a value is associated with a key that is equivalent to the key used to store the object.
//		assertTrue(dict.containsKey(intKey4));

		// use different key instance to retrieve value
		intResults = dict.get(intKey4);
//		assertEquals(42, intResults,"Integer key" );

		// put int in and retrieve it with a different key
		dict.put(intKey2, 99);
		intResults = dict.get(intKey2);
//		assertEquals(99, intResults, "Integer key 2" );
		
		// put int in and retrieve it with a different key
		dict.put(intKey3, -1);
		intResults = dict.get(intKey3);
//		assertEquals(-1, intResults, "Integer key 3" );

		// put int in and retrieve it with a different key
		dict.put(intKey4, 11223344);
		intResults = dict.get(intKey4);
//		assertEquals(11223344, intResults, "Integer key 4" );

		System.out.println("intResults = "+intResults); // This is just to get rid of the "unused value" warnings.		

		// put in a string and retrieve it
		dict.put(strKey1, "Howdy!");		
		String strResult = dict.get(strKey1);
//		assertEquals("Howdy!", strResult, "String key" );
		
		// put in a string and retrieve it
		dict.put(strKey2, "Yahoo!");		
		strResult = dict.get(strKey2);
//		assertEquals( "Yahoo!", strResult, "String key" );		

		System.out.println("strResult = "+strResult); // This is just to get rid of the "unused value" warnings.		
		
		// Test putting in subclasses of the key type
		dict.put(numKey1, 42);
		Number numResult = dict.get(numKey1);
//		assertEquals( 42, numResult, "Number key" );			

		dict.put(numKey1, 3.14);
		numResult = dict.get(numKey1);
//		assertEquals( 3.14, numResult, "Number key" );	
		
		System.out.println("numResult = "+numResult); // This is just to get rid of the "unused value" warnings.		

	}


}

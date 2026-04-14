import junit.framework.*;
import java.io.*;

public class Assign1TestPrivate extends TestCase {

  public Assign1TestPrivate (String name) {
    super(name);
  }
  
  protected void checkString(String name, String answer, String program) {
    Parser p = new Parser(new StringReader(program));
    assertEquals(name, answer, p.parse().toString());
  }

  public void testParseException() {
    try {
      String output = "doh!";
      String input = "map a, to 3";
      checkString("parseException", output, input );

         fail("parseException did not throw ParseException exception");
      } catch (ParseException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      fail("parseException threw " + e);
    }
  } //end of func
  

  public void testLet() {
    try {
      String output = "let a := 3; in (a + a)";
      String input = "let a:=3; in a + a";
      checkString("let", output, input );

    } catch (Exception e) {
      fail("let threw " + e);
    }
  } //end of func
  

  public void testMap() {
    try {
      String output = "map f to (map x to f(x(x)))(map x to f(x(x)))";
      String input = "map f to (map x to f( x( x ) ) ) (map x to f(x(x)))";
      checkString("map", output, input );

    } catch (Exception e) {
      fail("map threw " + e);
    }
  } //end of func   
}

import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

/** Testing framework for Typed Jam */
public class Assign5Test extends TestCase {

  public Assign5Test (String name) { super(name); }
 
  private void eagerCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-value " + name, answer, interp.eagerEval().toString());
  }

  private void lazyCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-name " + name, answer, interp.lazyEval().toString());
  }

  private void allCheck(String name, String answer, String program) {
    eagerCheck(name, answer, program);
    lazyCheck(name, answer, program);
  }

  public void testMathOp() {
    try {
      String output = "18";
      String input = "2 * 3 + 12";
      allCheck("mathOp", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("mathOp threw " + e);
    }
  } //end of func
  

  public void testParseException() {
    try {
      String output = "haha";
      String input = " 1 +";
      allCheck("parseException", output, input );

         fail("parseException did not throw ParseException exception");
      } catch (ParseException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("parseException threw " + e);
    }
  } //end of func
  

  public void testAppend() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input = "let append:  (list int, list int -> list int) :=       map x: list int, y: list int to         if x = empty: int then y else cons(first(x), append(rest(x), y));     s: list int := cons(1,cons(2,cons(3,empty: int))); in append(s,s)";
      allCheck("append", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("append threw " + e);
    }
  } //end of func
  

  public void testEmpty() {
    try {
      String output = "()";
      String input = "empty: list (int, bool, list ref int -> unit)";
      allCheck("empty", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("empty threw " + e);
    }
  } //end of func
  

  public void testEmptyBlock() {
    try {
      String output = "0";
      String input = "{ }";
      allCheck("emptyBlock", output, input );

         fail("emptyBlock did not throw ParseException exception");
      } catch (ParseException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("emptyBlock threw " + e);
    }
  } //end of func
  

  public void testBlock() {
    try {
      String output = "1";
      String input = "{3; 2; 1}";
      allCheck("block", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("block threw " + e);
    }
  } //end of func
  

  public void testDupVar() {
    try {
      String output = "ha!";
      String input = "let x: int :=3; x:int :=4; in x";
      allCheck("dupVar", output, input );

         fail("dupVar did not throw SyntaxException exception");
      } catch (SyntaxException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("dupVar threw " + e);
    }
  } //end of func
  

}


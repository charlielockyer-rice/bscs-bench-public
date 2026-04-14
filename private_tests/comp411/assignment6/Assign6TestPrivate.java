import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

/** testing framework for typed jam.  **/
public class Assign6TestPrivate extends TestCase {

  private void evalCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-value " + name, answer, interp.eval().toString());
  }
  private void cpsEvalCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-value " + name, answer, interp.cpsEval().toString());
  }
  private void SDEvalCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-value " + name, answer, interp.SDEval().toString());
  }
  private void CpsSDEvalCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      assertEquals("by-value-value " + name, answer, interp.SDCpsEval().toString());
  }

  private void allEvalCheck(String name, String answer, String program) {
    evalCheck(name, answer, program);
    cpsEvalCheck(name, answer, program);
    SDEvalCheck(name, answer, program);
    CpsSDEvalCheck(name, answer, program);
  }
  private void nonCpsEvalCheck(String name, String answer, String program) {
    evalCheck(name, answer, program);
    SDEvalCheck(name, answer, program);
  }
  
  private void unshadowCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));

      String result = interp.unshadow().toString();
      assertEquals("shadowCheck " + name, answer, result);
  }

  private void cpsConvert(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      String result = interp.convertToCPS().toString();
      assertEquals(name, answer, result);
  }

  private void SDCheck(String name, String answer, String program) {
      Interpreter interp = new Interpreter(new StringReader(program));
      String result = interp.convertToSD().toString();
      assertEquals(name, answer, result);
  }

  public void testBadLetrec() {
    try {
      String output = "!";
      String input = "letrec x:=4; in x";
      allEvalCheck("badLetrec", output, input );

         fail("badLetrec did not throw ParseException exception");
      } catch (ParseException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("badLetrec threw " + e);
    }
  } //end of func
  

  public void testBadLet() {
    try {
      String output = "!";
      String input = "let x:= map z to y(z);\n             y:= map z to x(z); in x(5)";
      allEvalCheck("badLet", output, input );

         fail("badLet did not throw SyntaxException exception");
      } catch (SyntaxException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("badLet threw " + e);
    }
  } //end of func
  

  public void testUuop() {
    try {
      String output = "(3 + 3)";
      String input = "3 + 3";
      unshadowCheck("Uuop", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Uuop threw " + e);
    }
  } //end of func
  

  public void testSuop() {
    try {
      String output = "(3 + 3)";
      String input = "3 + 3";
      SDCheck("Suop", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Suop threw " + e);
    }
  } //end of func
  

  public void testCuop() {
    try {
      String output = "(map x to x)((3 + 3))";
      String input = "3 + 3";
      cpsConvert("Cuop", output, input);

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cuop threw " + e);
    }
  } //end of func
  

  public void testUop() {
    try {
      String output = "6";
      String input = "3 + 3";
//      allEvalCheck("uop", output, input);
      evalCheck("uop", output, input );
    } catch (Exception e) {
      e.printStackTrace();
      fail("uop threw " + e);
    }
  } //end of func
  

  public void testUdeep() {
    try {
      String output = "let x:1 := map x:1 to letrec x:2 := map x:3 to x:3; in x:2(x:2); y:1 := let x:1 := 5; in x:1; in x:1(y:1)";
      String input = "let x:= map x to \n     letrec x:=map x to x; in x(x);\n    y:= let x:=5; in x;\n  in x(y)";
      unshadowCheck("Udeep", output, input );
    } catch (Exception e) {
      e.printStackTrace();
      fail("Udeep threw " + e);
    }
  } //end of func
  
  public void testSdeep() {
    try {
      String output = "let [*2*] map [*1*] to letrec [*1*] map [*1*] to [0,0]; in [0,0]([0,0]); let [*1*] 5; in [0,0]; in [0,0]([0,1])";
      String input = "let x:= map x to \n     letrec x:=map x to x; in x(x);\n    y:= let x:=5; in x;\n  in x(y)";
      SDCheck("Sdeep", output, input );
    } catch (Exception e) {
      e.printStackTrace();
      fail("Sdeep threw " + e);
    }
  } //end of func
  
}






import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class Assign4Test extends TestCase {
  
  public Assign4Test (String name) {
    super(name);
  }
  
  /**
   * The following 9 check methods create an interpreter object with the
   * specified String as the program, invoke the respective evaluation
   * method (valueValue, valueName, valueNeed, etc.), and check that the 
   * result matches the (given) expected output.  
   */
  
  private void valueValueCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value-value " + name, answer, interp.valueValue().toString());
  }
  
  private void valueNameCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value-name " + name, answer, interp.valueName().toString());
  }
  
  private void valueNeedCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value-need " + name, answer, interp.valueNeed().toString());
  }
  
  private void nameValueCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value " + name, answer, interp.nameValue().toString());
  }
  
  private void nameNameCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-name " + name, answer, interp.nameName().toString());
  }
  
  private void nameNeedCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-need " + name, answer, interp.nameNeed().toString());
  }
  
  private void needValueCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value " + name, answer, interp.needValue().toString());
  }
  
  private void needNameCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-name " + name, answer, interp.needName().toString());
  }
  
  private void needNeedCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-need " + name, answer, interp.needNeed().toString());
  }
  
  private void allCheck(String name, String answer, String program) {
    valueValueCheck(name, answer, program);
    valueNameCheck(name, answer, program);
    valueNeedCheck(name, answer, program);
    nameValueCheck(name, answer, program);
    nameNameCheck(name, answer, program);
    nameNeedCheck(name, answer, program);
    needValueCheck(name, answer, program);
    needNameCheck(name, answer, program);
    needNeedCheck(name, answer, program);
  }
  
  private void noNameCheck(String name, String answer, String program) {
    valueValueCheck(name, answer, program);
    valueNameCheck(name, answer, program);
    valueNeedCheck(name, answer, program);
    needValueCheck(name, answer, program);
    needNameCheck(name, answer, program);
    needNeedCheck(name, answer, program);
  }
  
  private void needCheck(String name, String answer, String program) {
    needValueCheck(name, answer, program);
    needNeedCheck(name, answer, program);
  }
   
  private void lazyCheck(String name, String answer, String program) {
    valueNameCheck(name, answer, program);
    valueNeedCheck(name, answer, program);
    nameNameCheck(name, answer, program);
    nameNeedCheck(name, answer, program);
    needNameCheck(name, answer, program);
    needNeedCheck(name, answer, program);
  }
  
  public void testBlock() {
    try {
      String output = "1";
      String input = "{3; 2; 1}";
      allCheck("block", output, input );
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("block threw " + e);
    }
  } //end of func
  
  public void testDupVar() {
    try {
      String output = "ha!";
      String input = "let x:=3; x:=4; in x";
      allCheck("dupVar", output, input );
      
      fail("dupVar did not throw SyntaxException exception");
    } catch (SyntaxException e) {   
      //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("dupVar threw " + e);
    }
  } //end of func
  
  public void testRefApp() {
    try {
      String output = "(ref 17)";
      String input = "let x := ref 10; in {x <- 17; x}";
      noNameCheck("refApp", output, input );
      nameNameCheck("refApp [name]", "(ref 10)", input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("refApp threw " + e);
    }
  } //end of func
  
  public void testBangApp() {
    try {
      String output = "10";
      String input = "let x := ref 10; in !x";
      allCheck("bangApp", output, input );
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("bangApp threw " + e);
    }
  } //end of func
  
  public void testSwap() {
    try {
      String output = "150";
//      String output = "20";
      String input = 
        "let  x := ref 20;" +
        "     y := ref 5;" +
        "     z := 10;" +
        "     swap := map x, y to " +
        "               let temp := !x; in { temp; x <- !y; y <- temp };"  +      
        "in { swap(x,y); (!y - !x) * z}"; 
      
      valueValueCheck("testSwap", output, input);
      needValueCheck("testSwap", output, input );
    } catch (VirtualMachineError|Exception e) {
      e.printStackTrace(System.err);
      fail("testSwap" + " threw " + e);
    }
  } //end of func

    public void testSwap2() {
        try {
        String output = "150";
        String input =
            "let  x := ref 20;" +
            "     y := ref 5;" +
            "     z := 10;" +
            "     swap := map x, y to " +
            "               let temp := !x; in { temp; x <- !y; y <- temp };"  +
            "in { swap(x,y); (!y - !x) * z}";

        valueValueCheck("testSwap2", output, input);
        needValueCheck("testSwap2", output, input );
        } catch (VirtualMachineError|Exception e) {
        e.printStackTrace(System.err);
        fail("testSwap2" + " threw " + e);
        }
    } //end of func

  public void testRefCreationAndDereference() {
    try {
      String output = "42";
      String input = "let x := ref 42; in !x";
      allCheck("testRefCreationAndDereference", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefCreationAndDereference threw " + e);
    }
  }

}






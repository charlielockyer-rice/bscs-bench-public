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

  public void testInvalidDereference() {
    try {
      String input = "let x := 10; in !x";
      allCheck("testInvalidDereference", "", input);
      fail("testInvalidDereference did not throw EvalException");
    } catch (EvalException e) {
      // Expected exception
    } catch (Exception e) {
      e.printStackTrace();
      fail("testInvalidDereference threw unexpected exception " + e);
    }
  }

    public void testInvalidRefUpdate() {
        try {
        String input = "let x := 10; in { x <- 20; x }";
        allCheck("testInvalidRefUpdate", "", input);
        fail("testInvalidRefUpdate did not throw EvalException");
        } catch (EvalException e) {
        // Expected exception
        } catch (Exception e) {
        e.printStackTrace();
        fail("testInvalidRefUpdate threw unexpected exception " + e);
        }
    }

  public void testSimpleRefCreation() {
    try {
      String output = "(ref 5)";
      String input = "ref 5";
      allCheck("testSimpleRefCreation", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSimpleRefCreation threw " + e);
    }
  }

  public void testDereferenceRef() {
    try {
      String output = "10";
      String input = "let x := ref 10; in !x";
      allCheck("testDereferenceRef", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDereferenceRef threw " + e);
    }
  }

  public void testMultipleLet() {
    try {
      String output = "15";
      String input = "let x := 10; y := 5; in x + y";
      allCheck("testMultipleLet", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testMultipleLet threw " + e);
    }
  }

  public void testSimpleArithmetic() {
    try {
      String output = "7";
      String input = "3 + 4";
      allCheck("testSimpleArithmetic", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSimpleArithmetic threw " + e);
    }
  }

  public void testFunctionApplication() {
    try {
      String output = "20";
      String input = "(map x to x * 2)(10)";
      allCheck("testFunctionApplication", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testFunctionApplication threw " + e);
    }
  }

  public void testConstantReference() {
    try {
      String output = "10";
      String input = "let x := 10 in x";
      allCheck("testConstantReference", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testConstantReference threw " + e);
    }
  }

  // ========== Additional Tests ==========
  // The following tests were added to improve coverage of imperative Jam features

  // ========== ref? primitive tests ==========

  public void testRefPrimitiveTrue() {
    try {
      String output = "true";
      String input = "ref?(ref 5)";
      allCheck("ref? on ref", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefPrimitiveTrue threw " + e);
    }
  }

  public void testRefPrimitiveFalseInt() {
    try {
      String output = "false";
      String input = "ref?(5)";
      allCheck("ref? on int", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefPrimitiveFalseInt threw " + e);
    }
  }

  public void testRefPrimitiveFalseList() {
    try {
      String output = "false";
      String input = "ref?(cons(1, empty))";
      allCheck("ref? on list", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefPrimitiveFalseList threw " + e);
    }
  }

  // ========== Reference equality tests ==========

  public void testRefEqualityDifferent() {
    try {
      String output = "false";
      String input = "let x := ref 10; y := ref 10; in x = y";
      allCheck("different refs not equal", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefEqualityDifferent threw " + e);
    }
  }

  public void testRefEqualitySame() {
    try {
      // Only works in value-binding modes - in name-binding, y := x means y re-evaluates x
      String output = "true";
      String input = "let x := ref 10; y := x; in x = y";
      valueValueCheck("same ref is equal", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefEqualitySame threw " + e);
    }
  }

  // ========== Nested ref tests ==========

  public void testNestedRef() {
    try {
      String output = "(ref (ref 5))";
      String input = "ref ref 5";
      allCheck("nested ref", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNestedRef threw " + e);
    }
  }

  public void testNestedDeref() {
    try {
      String output = "5";
      String input = "!(!(ref ref 5))";
      allCheck("nested deref", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNestedDeref threw " + e);
    }
  }

  // ========== Aliasing tests ==========

  public void testAliasing() {
    try {
      String output = "10";
      String input = "let x := ref 5; y := x; in { y <- 10; !x }";
      valueValueCheck("aliasing", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testAliasing threw " + e);
    }
  }

  // ========== Block tests ==========

  public void testBlockSingleExpr() {
    try {
      String output = "42";
      String input = "{ 42 }";
      allCheck("block single expr", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testBlockSingleExpr threw " + e);
    }
  }

  public void testBlockMutation() {
    try {
      String output = "3";
      String input = "let x := ref 0; in { x <- 1; x <- 2; x <- 3; !x }";
      valueValueCheck("block mutation", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testBlockMutation threw " + e);
    }
  }

  public void testEmptyBlockError() {
    try {
      String input = "{ }";
      allCheck("empty block", "", input);
      fail("empty block should throw ParseException");
    } catch (ParseException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEmptyBlockError threw unexpected " + e);
    }
  }

  // ========== Counter/closure test ==========

  public void testRefInClosure() {
    try {
      String output = "2";
      String input =
        "let counter := ref 0; " +
        "    inc := map to { counter <- !counter + 1; !counter }; " +
        "in { inc(); inc() }";
      valueValueCheck("ref in closure", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testRefInClosure threw " + e);
    }
  }

  // ========== JamUnit equality test ==========

  public void testUnitEquality() {
    try {
      String output = "true";
      String input = "let x := ref 1; y := ref 2; in (x <- 10) = (y <- 20)";
      allCheck("unit equality", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testUnitEquality threw " + e);
    }
  }

}






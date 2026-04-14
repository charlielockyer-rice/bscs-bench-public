import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class Assign2TestPrivate extends TestCase {

  public Assign2TestPrivate (String name) {
    super(name);
  }
  
  /** The following 3 check methods create an interpreter object with the
    * specified String as the program, invoke the respective evaluation
    * method (callByValue, callByName, callByNeed), and check that the 
    * result matches the (given) expected output.  If the test fails,
    * the method prints a report as to which test failed and how many
    * points should be deducted.
    */

 
  private void valueCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-value " + name, answer, interp.callByValue().toString());
  }

  private void nameCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-name " + name, answer, interp.callByName().toString());
  }
   
  private void needCheck(String name, String answer, String program) {
    Interpreter interp = new Interpreter(new StringReader(program));
    assertEquals("by-need " + name, answer, interp.callByNeed().toString());
  }

  private void allCheck(String name, String answer, String program) {
    valueCheck(name, answer, program);
    nameCheck(name, answer, program);
    needCheck(name, answer, program);
  } 

  public void testUnaryOperations() {
    try {
      // forUnOpPlus
      String outputPlus = "5";
      String inputPlus = "+5";
      allCheck("unaryPlus", outputPlus, inputPlus);

      // forUnOpMinus
      String outputMinus = "-5";
      String inputMinus = "-5";
      allCheck("unaryMinus", outputMinus, inputMinus);

      // forOpTilde
      String outputTilde = "false";
      String inputTilde = "~true";
      allCheck("unaryTilde", outputTilde, inputTilde);
    } catch (Exception e) {
      fail("Unary operations test threw " + e);
    }
  }

  public void testForIfException() {
    String output = "1";
    String input = "is true then 1 else false";
    try {
      allCheck("forIf", output, input);
      fail("Expected an EvalException to be thrown");
    } catch (Exception e) {
      // Test passes if EvalException is caught
      assertTrue("Correct EvalException caught", true);
    }
  }

  public void testForAppException() {
    String output = "nonExistentVar";
    String input = "(map x to x + 1)(nonExistentVar)";
    try {
      allCheck("forApp", output, input);
      fail("Expected an EvalException to be thrown");
    } catch (EvalException e) {
      // Test passes if EvalException is caught
      assertTrue("Correct EvalException caught", true);
    } catch (Exception e) {
      fail("An unexpected exception was thrown: " + e.getMessage());
    }
  }

  /**
   * Test for the binary operations to ensure proper coverage
   */
  public void testBoolConstantTrue() {
    String output = "true";
    String input = "true";
    allCheck("boolConstantTrue", output, input);
  }

  public void testBoolConstantFalse() {
    String output = "false";
    String input = "false";
    allCheck("boolConstantFalse", output, input);
  }

  public void testUnaryOpPlus() {
    String output = "5";
    String input = "+5";
    allCheck("unaryOpPlus", output, input);
  }

  public void testUnaryOpMinus() {
    String output = "-5";
    String input = "-5";
    allCheck("unaryOpMinus", output, input);
  }

  public void testOpTilde() {
    String output = "false";
    String input = "~true";
    allCheck("opTilde", output, input);
  }

  public void testBinaryOpDivide() {
    String output = "5";
    String input = "10 / 2";
    allCheck("binaryOpDivide", output, input);
  }

  public void testBinaryOpNotEquals() {
    String output = "true";
    String input = "5 != 4";
    allCheck("binaryOpNotEquals", output, input);
  }

  public void testBinaryOpLessThan() {
    String output = "true";
    String input = "3 < 4";
    allCheck("binaryOpLessThan", output, input);
  }

  public void testBinaryOpGreaterThan() {
    String output = "true";
    String input = "5 > 4";
    allCheck("binaryOpGreaterThan", output, input);
  }

  public void testBinaryOpLessThanEquals() {
    String output = "true";
    String input = "3 <= 3";
    allCheck("binaryOpLessThanEquals", output, input);
  }

  public void testBinaryOpGreaterThanEquals() {
    String output = "true";
    String input = "5 >= 4";
    allCheck("binaryOpGreaterThanEquals", output, input);
  }

  public void testBinaryOpAnd() {
    String output = "false";
    String input = "true & false";
    allCheck("binaryOpAnd", output, input);
  }

  public void testBinaryOpOr() {
    String output = "true";
    String input = "false | true";
    allCheck("binaryOpOr", output, input);
  }

  public void testFunctionPPrimWithClosure() {
    String output = "true";
    String input = "function?(map x to x)";
    allCheck("functionPPrimWithClosure", output, input);
  }

  public void testFunctionPPrimWithNonFunction() {
    String output = "false";
    String input = "function?(5)";
    allCheck("functionPPrimWithNonFunction", output, input);
  }

  public void testNumberPPrimWithIntConstant() {
    String output = "true";
    String input = "number?(42)";
    allCheck("numberPPrimWithIntConstant", output, input);
  }

  public void testNumberPPrimWithNonNumber() {
    String output = "false";
    String input = "number?(true)";
    allCheck("numberPPrimWithNonNumber", output, input);
  }

  public void testListPPrimWithJamList() {
    String output = "true";
    String input = "list?(cons(1, empty))";
    allCheck("listPPrimWithJamList", output, input);
  }

  public void testListPPrimWithNonList() {
    String output = "false";
    String input = "list?(42)";
    allCheck("listPPrimWithNonList", output, input);
  }

  public void testConsPPrimWithJamCons() {
    String output = "true";
    String input = "cons?(cons(1, empty))";
    allCheck("consPPrimWithJamCons", output, input);
  }

  public void testConsPPrimWithJamEmpty() {
    String output = "false";
    String input = "cons?(empty)";
    allCheck("consPPrimWithJamEmpty", output, input);
  }

  public void testArityPrimWithClosure() {
    String output = "1"; // Assuming the closure takes one argument
    String input = "arity(map x to x)";
    allCheck("arityPrimWithClosure", output, input);
  }

  public void testArityPrimWithNonFunction() {
    String input = "arity(42)";
    try {
      allCheck("arityPrimWithNonFunction", "error", input);
      fail("Expected EvalException was not thrown.");
    } catch (EvalException e) {
      // Expected exception caught, test passes
      System.out.println("Expected EvalException caught: " + e.getMessage());
    } catch (Exception e) {
      // Other unexpected exceptions fail the test
      e.printStackTrace();
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }


}

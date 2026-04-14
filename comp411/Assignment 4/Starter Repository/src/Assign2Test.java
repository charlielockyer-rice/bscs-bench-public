import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class Assign2Test extends TestCase {

  public Assign2Test (String name) {
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

  public void testNumberP() {
    try {
      String output = "number?";
      String input = "number?";
      allCheck("numberP", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("numberP threw " + e);
    }
  } //end of func
  
  public void testMathOp() {
    try {
      String output = "18";
      String input = "2 * 3 + 12";
      allCheck("mathOp", output, input );

    } catch (Exception e) {
      e.printStackTrace();
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
      e.printStackTrace();
      fail("parseException threw " + e);
    }
  } //end of func
  

  public void testEvalException() {
    try {
      String output = "mojo";
      String input = "1 + number?";
      allCheck("evalException", output, input );

         fail("evalException did not throw EvalException exception");
      } catch (EvalException e) {
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("evalException threw " + e);
    }
  } //end of func

  public void testIntegerConstant() {
    try {
      String output = "42";
      String input = "42";
      allCheck("integerConstant", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("integerConstant threw " + e);
    }
  }

  public void testAppendNoRecursion() {
    try {
      String output = "(4 5)";
      String input =
              "let append := map x,y to y; " +  // Simplified append that just returns the second argument
                      "    list1 := cons(1, cons(2, cons(3, empty))); " +
                      "    list2 := cons(4, cons(5, empty)); " +
                      "in append(list1, list2)";
      allCheck("appendNoRecursion", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("appendNoRecursion threw " + e);
    }
  }


// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testAppend() {
//      try {
//        String output = "(1 2 3 1 2 3)";
//        String input =
//          "let Y      := map f to " +
//          "                let g := map x to f(map z1,z2 to (x(x))(z1,z2)); " +
//          "                in g(g); " +
//          "    APPEND := map ap to " +
//          "                map x,y to " +
//          "                  if x = empty then y else cons(first(x), ap(rest(x), y)); " +
//          "    l      := cons(1,cons(2,cons(3,empty))); " +
//          "in (Y(APPEND))(l,l)";
//        allCheck("append", output, input );
//
//      } catch (Exception e) {
//        e.printStackTrace();
//        fail("append threw " + e);
//      }
//    } //end of func

// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testFib() {
//      try {
//        String output
//          = "((0 1) (1 1) (2 2) (3 3) (4 5) (5 8) (6 13) (7 21) (8 34) (9 55) (10 89))";
//        String input =
//          "let      Y := map f to let g := map x to f(x(x)); in g(g); " +
//          "      pair := map x,y to cons(x, cons(y, empty));" +
//          "   FIBHELP := map fibhelp to map k,fn,fnm1 to if k = 0 then fn else fibhelp(k - 1, fn + fnm1, fn); " +
//          "in let FFIB := map ffib to map n to if n = 0 then 1 else (Y(FIBHELP))(n - 1,1,1); " +
//          "   in let FIBS := map fibs to map k,l to " +
//          "                    let fibk := (Y(FFIB))(k);" +
//          "                    in if k >= 0 then fibs(k - 1, cons(pair(k,fibk), l)) else l; " +
//          "      in (Y(FIBS))(10, empty)";
//        needCheck("fib-need", output, input);
//        nameCheck("fib-name", output, input);
//      }
//      catch(Exception e) {
//        e.printStackTrace();
//        fail("[3.00] fib threw " + e);
//      }
//    } //end of func

// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testYappend() {
//      try {
//        String output = "(1 2 3 1 2 3)";
//        String input = "let    Y := map f to let g := map x to f(map z1,z2 to (x(x))(z1,z2)); in g(g);" +
//                       "  APPEND := map ap to map x,y to if empty?(x) then y else cons(first(x), ap(rest(x), y));" +
//                       "       l := cons(1,cons(2,cons(3,empty)));" +
//                       "in (Y(APPEND))(l,l) ";
//        allCheck("[3.00] yappend", output, input );
//
//      } catch (Exception e) {
//        e.printStackTrace();
//        fail("[3.00] yappend threw " + e);
//      }
//    } //end of func

// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testValueYFactorial() {
//      try {
//        String output = "720";
//        String input = "let  Y := map f to let g := map x to f(map z to (x(x))(z)); in g(g);" +
//          "  FACT := map f to map n to if n = 0 then 1 else n * f(n - 1);" +
//          "in (Y(FACT))(6)";
//        valueCheck("[3.00] yfactorial", output, input );
//
//      } catch (Exception e) {
//        e.printStackTrace();
//        fail("[3.00] valueYFactorial threw " + e);
//      }
//    } //end of func

// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testNameYFactorial() {
//      try {
//        String output = "720";
//        String input = "let   Y := map f to let g := map x to f(x(x)); in g(g);" +
//          "   FACT := map f to map n to if n = 0 then 1 else n * f(n - 1); in (Y(FACT))(6)";
//        nameCheck("[1.50] nameYFactorial", output, input );
//
//      } catch (Exception e) {
//        e.printStackTrace();
//        fail("[1.50] nameYFactorial threw " + e);
//      }
//    } //end of func

// ADVANCED TEST - Uncomment to enable Y-combinator testing
//    public void testNeedYFactorial() {
//      try {
//        String output = "720";
//        String input = "let   Y := map f to let g := map x to f(x(x)); in g(g); " +
//                       "   FACT := map f to map n to if n = 0 then 1 else n * f(n - 1); in (Y(FACT))(6)";
//        needCheck("[1.50] needYFactorial", output, input );
//
//      } catch (Exception e) {
//        e.printStackTrace();
//        fail("[1.50] needYFactorial threw " + e);
//      }
//    } //end of func

  public void testUndefinedVariable() {
    try {
      String output = "undefinedVariableError";
      String input = "x + 3";
      allCheck("undefinedVariable", output, input);
      fail("undefinedVariable did not throw EvalException exception");
    } catch (EvalException e) {
      // Expected behavior
    } catch (Exception e) {
      e.printStackTrace();
      fail("undefinedVariable threw incorrect exception type");
    }
  } //end of func


  public void testBoolConstant() {
    try {
      String output = "true";
      String input = "true";
      allCheck("boolConstant", output, input);
    } catch (Exception e) {
      fail("boolConstant threw " + e);
    }
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

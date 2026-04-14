import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class Assign3Test extends TestCase {

  public Assign3Test (String name) { super(name); }

  /** The following 9 check methods create an interpreter object with the specified String as the program, invoke the 
   * respective evaluation method (valueValue, valueName, valueNeed, etc.), and check that the result matches the
   * (given) expected output.
   * The test methods named xtest* are ignored; you should uncomment them after you implement the tested features.
   * Of course, you need to write many more tests to thorougly test your program.
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

  public void testNumberP() {
    try {
      String output = "number?";
      String input = "number?";
      allCheck("numberP", output, input );
    }
    catch (Exception e) {
//      e.printStackTrace();
      fail("numberP threw " + e);
    }
  } //end of func

  public void testMathOp() {
    try {
      String output = "18";
      String input = "2 * 3 + 12";
      valueValueCheck("mathOp", output, input);
//      allCheck("mathOp", output, input);   
    }
    catch (Exception e) {
//      e.printStackTrace();
      fail("mathOp threw " + e);
    }
  } //end of func

  public void testParseException() {
    try {
      String output = "haha";
      String input = " 1 +";
      allCheck("parseException", output, input );
      fail("parseException did not throw ParseException exception");
    }
    catch (ParseException e) {  /* Success; ParseException thrown */ }
    catch (Exception e) {
//      e.printStackTrace();
      fail("parseException threw " + e);
    }
  } //end of func

  public void testEvalException() {
    try {
      String output = "mojo";
      String input = "1 + number?";
      allCheck("evalException", output, input );
      fail("evalException did not throw EvalException exception");
    }
    catch (EvalException e) {  /* Success; EvalException thrown */ }
    catch (Exception e) {
//      e.printStackTrace();
      fail("evalException threw " + e);
    }
  } //end of func

  public void testMapEquals() {
    try {
      String output = "true";
      String input = "let id := map x to x; in id = id";
      valueValueCheck("MapEquals1", output, input);
      needValueCheck("MapEquals2", output, input);
      nameValueCheck("MapEquals3", "false", input);
    }
    catch(Exception e) {
//      e.printStackTrace();
      fail("MapEquals threw " + e);
    }
  } //end of method

  /** Test Y formulation of append using eager evaluation of cons */
  public void testAppend() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input =
              "let    Y := map f to " +
                      "              let g := map x to f(map z1,z2 to (x(x))(z1,z2)); " +
                      "              in g(g); " +
                      "  APPEND := map ap to " +
                      "              map x,y to " +
                      "                if x = empty then y else cons(first(x), ap(rest(x), y)); " +
                      "     lst := cons(1,cons(2,cons(3,empty))); " +
                      "in (Y(APPEND))(lst,lst)";
      valueValueCheck("append", output, input);
      nameValueCheck("append", output, input);
      needValueCheck("append", output, input);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("append threw " + e);
    }
  } //end of func


  public void testLetRec() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input = "let append := map x,y to if empty?(x) then y else cons(first(x), append(rest(x), y)); " +
              "         l := cons(1,cons(2,cons(3,empty))); " +
              "in append(l,l)";
      allCheck("letRec", output, input);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("letRec threw " + e);
    }
  } //end of func

  public void testLazyCons() {
    try {
      String output = "0";
      String input = "let zeroes := cons(0,zeroes); in first(rest(zeroes))";
      lazyCheck("lazyCons", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("lazyCons threw " + e);
    }
  } //end of func

  public void testLazyEquals() {
    try {
      String output = "false";
      String input1 = "let zeros := cons(0,zeros); ones := cons(1, ones); in ones = zeros";
      String input2 = "let zeros := cons(0,zeros); ones := cons(1, ones); in cons(1,ones) = cons(1,zeros)";
      lazyCheck("lazyEquals 1", output, input1);
      lazyCheck("lazyEquals 2", output, input2);
    } catch (Exception e) {
//      e.printStackTrace();
      fail("lazyCons threw " + e);
    }
  } //end of func

  public void testForwardRef() {
    try {
      String output = "Error";
      String input = "let a := a; in a";
      allCheck("[0.50] forwardRef", output, input);
      fail("[0.50] forwardRef did not throw EvalException");
    }
    catch (EvalException e) {  /* Success! badLet1 threw a ParseException;  */  }
    catch (Exception e) {
//      e.printStackTrace();
      fail("forwardRef threw Exception " + e + " rather than an EvalException");
    }
  }

  public void testBinOpAsFun() {
    try {
      String output = "7";
      String input = "let f := +; in f(3,4)";
      allCheck("+ as PrimFun", output, input);
      fail("+ accepted as PrimFun");
    }
    catch(Exception e) {  /* Success! + rejected as PrimFun  */  }
  }

  public void testBinOpAsArg() {
    try {
      String output = "3";
      String input = "let f := map x,y to x; in f(3,4)";
      allCheck("BinOp as Arg", output, input); // Correctly check if the output is "3"
    } catch(Exception e) {
      e.printStackTrace();
      fail("Unexpected exception thrown: " + e.getMessage());
    }
  }

  public void testBinOpAsName() {
    try {
      String output = "7";
      String input = "let + := 7; in 3 + 4";
      allCheck("+ as Name", output, input);
      fail("+ accepted as Name");
    }
    catch(Exception e) {  /* Success! + rejected as Name  */  }
  }

  public void testBinOpAsNeed() {
    try {
      String output = "7";
      String input = "let + := map x,y to x + y; in 3 + 4";
      allCheck("+ as Need", output, input);
      fail("+ accepted as Need");
    }
    catch(Exception e) {  /* Success! + rejected as Need  */  }
  }

  public void testBinOpAsValue() {
    try {
      String output = "7";
      String input = "let + := map x,y to x + y; in 3 + 4";
      allCheck("+ as Value", output, input);
      fail("+ accepted as Value");
    }
    catch(Exception e) {  /* Success! + rejected as Value  */  }
  }

  public void testBinOpAsLazy() {
    try {
      String output = "7";
      String input = "let + := map x,y to x + y; in 3 + 4";
      allCheck("+ as Lazy", output, input);
      fail("+ accepted as Lazy");
    }
    catch(Exception e) {  /* Success! + rejected as Lazy  */  }
  }

  public void testCallByValue() {
    String program = "let x := 5; in x + 1";
    Interpreter interpreter = new Interpreter(new StringReader(program));
    JamVal result = interpreter.callByValue();
    assertEquals("Result of callByValue", new IntConstant(6), result);
  }

  public void testCallByName() {
    String program = "let x := (map y to y * y)(3); in x + 2";
    Interpreter interpreter = new Interpreter(new StringReader(program));
    JamVal result = interpreter.callByName();
    assertEquals("Result of callByName", new IntConstant(11), result);
  }

  public void testCallByNeed() {
    String program = "let f := map x to x + 1; in f(5)";
    Interpreter interpreter = new Interpreter(new StringReader(program));
    JamVal result = interpreter.callByNeed();
    assertEquals("6", result.toString());
  }
  public void testValueBinding() {
    try {
      String output = "5";
      String input = "let x := 5; in x";
      allCheck("ValueBinding", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testValueBinding threw " + e);
    }
  }

  public void testNameBinding() {
    try {
      String output = "5";
      String input = "let x := (map y to y)(5); in x";
      allCheck("NameBinding", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNameBinding threw " + e);
    }
  }

  public void testNeedBinding() {
    try {
      String output = "5";
      String input = "let x := (map y to y)(5); in x";
      allCheck("NeedBinding", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNeedBinding threw " + e);
    }
  }

  public void testBindingToStringMethods() {
    Variable var = new Variable("x");
    JamVal val = new IntConstant(5);
    Suspension dummySuspension = () -> val;

    Interpreter.ValueBinding valueBinding = new Interpreter.ValueBinding(var, val);
    Interpreter.NameBinding nameBinding = new Interpreter.NameBinding(var, dummySuspension);
    Interpreter.NeedBinding needBinding = new Interpreter.NeedBinding(var, dummySuspension);

    String expectedValueBindingToString = "[x, 5]";
    String expectedNameBindingToString = "[x, " + dummySuspension.toString() + "]";
    String expectedNeedBindingToString = "[x, null, " + dummySuspension.toString() + "]";

    assertEquals("ValueBinding toString test failed", expectedValueBindingToString, valueBinding.toString());
    assertEquals("NameBinding toString test failed", expectedNameBindingToString, nameBinding.toString());
    assertEquals("NeedBinding toString test failed", expectedNeedBindingToString, needBinding.toString());
  }


  public void testConcreteSuspensionToString() {
    Interpreter.BindingPolicy bindingPolicy = Interpreter.CALL_BY_VALUE;
    Variable var = new Variable("x");
    Binding dummyBinding = bindingPolicy.newDummyBinding(var);
    AST dummyAst = new Variable("testVar");
    Interpreter.EvalVisitor evalVisitor = new Interpreter.EvalVisitor(bindingPolicy, Interpreter.EAGER);
    Interpreter.ConcreteSuspension concreteSuspension = new Interpreter.ConcreteSuspension(dummyAst, evalVisitor);

    String expectedPart1 = "<testVar, ";
    String expectedPart2 = "EvalVisitor";
    String actual = concreteSuspension.toString();

    assertTrue(actual.startsWith(expectedPart1) && actual.contains(expectedPart2));
  }

  public void testForBoolConstant() {
    BoolConstant trueConst = BoolConstant.TRUE;
    Interpreter.EvalVisitor visitor = new Interpreter.EvalVisitor(Interpreter.CALL_BY_VALUE, Interpreter.EAGER); // Adjust constructor as needed
    JamVal result = visitor.forBoolConstant(trueConst);
    assertEquals("Expected BoolConstant.TRUE to be returned", trueConst, result);
  }

  public void testForUnOpAppPlus() {
    UnOp plusOp = UnOpPlus.ONLY;
    IntConstant intConst = new IntConstant(5);
    UnOpApp plusApp = new UnOpApp(plusOp, intConst);

    Interpreter.EvalVisitor visitor = new Interpreter.EvalVisitor(Interpreter.CALL_BY_VALUE, Interpreter.EAGER);
    JamVal result = visitor.forUnOpApp(plusApp);
    assertEquals("Expected result of PLUS operation on 5 to be 5", intConst, result);
  }
  public void testForUnOpAppMinus() {
    UnOp minusOp = UnOpMinus.ONLY;
    IntConstant intConst = new IntConstant(5);
    UnOpApp minusApp = new UnOpApp(minusOp, intConst);

    Interpreter.EvalVisitor visitor = new Interpreter.EvalVisitor(Interpreter.CALL_BY_VALUE, Interpreter.EAGER);
    JamVal result = visitor.forUnOpApp(minusApp);
    assertEquals("Expected result of MINUS operation on 5 to be -5", new IntConstant(-5), result);
  }

  public void testForOpTilde() {
    String program = "let x := true; in ~x";
    allCheck("ForOpTilde", "false", program);
  }

  public void testForFunctionPPrim() {
    String program = "function?(map x to x)";
    allCheck("FunctionPPrimTest", "true", program);
  }

  public void testForNumberPPrim() {
    String program = "let f := number?(3); in f";
    allCheck("NumberPPrimTest", "true", program);
  }

  public void testForListPPrim() {
    String program = "let f := list?(cons(1, empty)); in f";
    allCheck("ListPPrimTest", "true", program);
  }

  public void testForConsPPrim() {
    String program = "let f := cons?(cons(1, empty)); in f";
    allCheck("ConsPPrimTest", "true", program);
  }

  public void testForArityPrim() {
    String program = "let f := map x to x; in arity(f)";
    allCheck("ArityPrimTest", "1", program);
  }

  public void testCheckBooleanWithNonBoolean() {
    String program = "let x := 1; in ~x";
    try {
      allCheck("CheckBooleanNonBoolean", "", program);
      fail("checkBoolean did not throw EvalException on non-boolean");
    } catch (EvalException e) {
      // Expected exception due to non-boolean operand for ~ operator
    }
  }

  public void testBoolConstant() {
    String program = "true";
    allCheck("BoolConstant", "true", program);
  }

  public void testForBinOpMinus() {
    String program = "let x := 5; y := 3; in x - y";
    allCheck("ForBinOpMinus", "2", program);
  }

  public void testForOpDivide() {
    String program = "let x := 10; y := 2; in x / y";
    allCheck("ForOpDivide", "5", program);
  }

  public void testForOpNotEquals() {
    String program = "let x := 5; y := 3; in x != y";
    allCheck("ForOpNotEquals", "true", program);
  }

  public void testForOpLessThan() {
    String program = "let x := 3; y := 5; in x < y";
    allCheck("ForOpLessThan", "true", program);
  }

  public void testForOpGreaterThan() {
    String program = "let x := 5; y := 3; in x > y";
    allCheck("ForOpGreaterThan", "true", program);
  }

  public void testForOpLessThanEquals() {
    String program = "let x := 3; y := 3; in x <= y";
    allCheck("ForOpLessThanEquals", "true", program);
  }

  public void testForOpGreaterThanEquals() {
    String program = "let x := 5; y := 5; in x >= y";
    allCheck("ForOpGreaterThanEquals", "true", program);
  }

  public void testForOpAnd() {
    String program = "let x := true; y := false; in x & y";
    allCheck("ForOpAnd", "false", program);
  }

  public void testForOpOr() {
    String program = "let x := true; y := false; in x | y";
    allCheck("ForOpOr", "true", program);
  }

  public void testForArityPrimError() {
    String program = "let f := map x to x; in arity(f, 2)"; // Incorrect usage of arity with two arguments
    try {
      allCheck("ForArityPrimError", "", program);
      fail("Expected an EvalException for incorrect number of arguments to arity");
    } catch (EvalException e) {
      assertTrue("Error message should mention incorrect number of arguments",
              e.getMessage().contains("arity") && e.getMessage().contains("arguments"));
    }
  }
}






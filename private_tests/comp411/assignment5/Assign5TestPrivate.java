import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

/** Testing framework for Typed Jam */
public class Assign5TestPrivate extends TestCase {

  public Assign5TestPrivate (String name) { super(name); }
 
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

  public void testRefApp() {
    try {
      String output = "(ref 17)";
      String input = "let x: ref int := ref 10; in {x <- 17; x}";
      allCheck("refApp", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("refApp threw " + e);
    }
  } //end of func
  
  public void testList() {
    try {
      String output = "(unit)";
      String input = "cons((ref 10) <- 20, empty:unit)";
      allCheck("unit", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("unit threw " + e);
    }
  } //end of func

  public void testBangApp() {
    try {
      String output = "10";
      String input = "let x: ref int := ref 10; in !x";
      allCheck("bangApp", output, input );

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("bangApp threw " + e);
    }
  } //end of func
  

  public void testAssign() {
    try {
      String output = "true";
      String input = "let x: int :=5; y: bool :=true; in x !=y";
      allCheck("assign", output, input );

         fail("assign did not throw TypeException exception");
      } catch (TypeException e) {   
         //e.printStackTrace();
      
    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("assign threw " + e);
    }
  } //end of func
  

  public void testBadAssign() {
    try {
      String output = "0";
      String input = "let x: int := 10; in x <- 5";
      allCheck("badAssign", output, input );

         fail("badAssign did not throw TypeException exception");
      } catch (TypeException e) {
         //e.printStackTrace();

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("badAssign threw " + e);
    }
  } //end of func

  // ========== Additional Tests ==========
  // The following tests were added to improve coverage of Typed Jam features

  // ========== Type error: wrong operand types ==========

  public void testTypeErrorIntPlusBool() {
    try {
      String input = "1 + true";
      allCheck("int + bool", "", input);
      fail("Expected TypeException for int + bool");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorIntPlusBool threw unexpected " + e);
    }
  }

  public void testTypeErrorBoolAndInt() {
    try {
      String input = "true & 5";
      allCheck("bool & int", "", input);
      fail("Expected TypeException for bool & int");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorBoolAndInt threw unexpected " + e);
    }
  }

  // ========== Type error: if condition not bool ==========

  public void testTypeErrorIfConditionInt() {
    try {
      String input = "if 5 then 1 else 2";
      allCheck("if int", "", input);
      fail("Expected TypeException for if with int condition");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorIfConditionInt threw unexpected " + e);
    }
  }

  // ========== Type error: if branches different types ==========

  public void testTypeErrorIfBranchesDiffer() {
    try {
      String input = "if true then 5 else false";
      allCheck("if branches differ", "", input);
      fail("Expected TypeException for if branches with different types");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorIfBranchesDiffer threw unexpected " + e);
    }
  }

  // ========== Polymorphic first/rest ==========

  public void testPolymorphicFirst() {
    try {
      String output = "1";
      String input = "first(cons(1, empty: int))";
      allCheck("polymorphic first", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPolymorphicFirst threw " + e);
    }
  }

  public void testPolymorphicRest() {
    try {
      String output = "()";
      String input = "rest(cons(1, empty: int))";
      allCheck("polymorphic rest", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testPolymorphicRest threw " + e);
    }
  }

  // ========== Function type parsing ==========

  public void testFunctionType() {
    try {
      String output = "6";
      String input = "let f: (int -> int) := map x: int to x + 1; in f(5)";
      allCheck("function type", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testFunctionType threw " + e);
    }
  }

  public void testMultiArgFunctionType() {
    try {
      String output = "8";
      String input = "let f: (int, int -> int) := map x: int, y: int to x + y; in f(3, 5)";
      allCheck("multi-arg function type", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testMultiArgFunctionType threw " + e);
    }
  }

  // ========== Wrong function argument type ==========

  public void testTypeErrorWrongArgType() {
    try {
      String input = "let f: (int -> int) := map x: int to x; in f(true)";
      allCheck("wrong arg type", "", input);
      fail("Expected TypeException for wrong argument type");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorWrongArgType threw unexpected " + e);
    }
  }

  // ========== Nested list types ==========

  public void testNestedListType() {
    try {
      String output = "((1))";
      String input = "cons(cons(1, empty: int), empty: list int)";
      allCheck("nested list type", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNestedListType threw " + e);
    }
  }

  // ========== Ref type mismatch ==========

  public void testTypeErrorRefTypeMismatch() {
    try {
      String input = "let x: ref int := ref true; in !x";
      allCheck("ref type mismatch", "", input);
      fail("Expected TypeException for ref int := ref true");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorRefTypeMismatch threw unexpected " + e);
    }
  }

  // ========== Unit type ==========

  public void testUnitType() {
    try {
      String output = "unit";
      String input = "let x: ref int := ref 5; u: unit := x <- 10; in u";
      // Note: output might be <unit> or unit depending on implementation
      eagerCheck("unit type", output, input);
    } catch (Exception e) {
      // Try alternate representation
      try {
        String output = "<unit>";
        String input = "let x: ref int := ref 5; u: unit := x <- 10; in u";
        eagerCheck("unit type", output, input);
      } catch (Exception e2) {
        e.printStackTrace();
        fail("testUnitType threw " + e);
      }
    }
  }

  // ========== cons type mismatch ==========

  public void testTypeErrorConsMismatch() {
    try {
      String input = "cons(true, empty: int)";
      allCheck("cons type mismatch", "", input);
      fail("Expected TypeException for cons(true, empty: int)");
    } catch (TypeException e) {
      // expected
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTypeErrorConsMismatch threw unexpected " + e);
    }
  }

  // ========== Basic comparisons work with same types ==========

  public void testEqualityBool() {
    try {
      String output = "true";
      String input = "true = true";
      allCheck("bool equality", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEqualityBool threw " + e);
    }
  }

  public void testEqualityInt() {
    try {
      String output = "true";
      String input = "5 = 5";
      allCheck("int equality", output, input);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testEqualityInt threw " + e);
    }
  }

}


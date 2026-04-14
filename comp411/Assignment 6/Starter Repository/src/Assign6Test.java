import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

/** testing framework for typed jam.  **/
public class Assign6Test extends TestCase {

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

  public void testUlet() {
    try {
      String output = "let id:1 := map x:1 to x:1; in id:1(10)";
      String input = "let id := map x to x; in id(10)";
      unshadowCheck("[0.50] ulet", output, input );
    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("[0.50] ulet threw " + e);
    }
  } //end of func

  public void testUmap() {
    try {
      String output = "map z:1 to z:1";
      String input = "map z to z";
      unshadowCheck("Umap", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Umap threw " + e);
    }
  } //end of func
  

  public void testSmap() {
    try {
      String output = "map [*1*] to [0,0]";
      String input = "map z to z";
      SDCheck("Smap", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Smap threw " + e);
    }
  } //end of func
  

  public void testCmap() {
    try {
      String output = "(map x to x)(map z:1,:0 to :0(z:1))";
      String input = "map z to z";
      cpsConvert("Cmap", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cmap threw " + e);
    }
  } //end of func
  

  public void testCarity() {
    try {
      String output = "(map x to x)(map x,k to k((arity(x) - 1)))";
      String input = "arity";
      cpsConvert("Carity", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Carity threw " + e);
    }
  } //end of func
  

  public void testCfirst() {
    try {
      String output = "(map x to x)(map x,k to k(first(x)))";
      String input = "first";
      cpsConvert("Cfirst", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cfirst threw " + e);
    }
  } //end of func
  

  public void testCcons() {
    try {
      String output = "(map x to x)(map x,y,k to k(cons(x, y)))";
      String input = "cons";
      cpsConvert("Ccons", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Ccons threw " + e);
    }
  } //end of func
  

  public void testClist() {
    try {
      String output = "(map x to x)(first(rest(rest(cons(1, cons(2, cons(3, empty)))))))";
      String input = "first(rest(rest(cons(1, cons(2, cons(3, empty))))))";
      cpsConvert("Clist", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Clist threw " + e);
    }
  } //end of func
  

  public void testUappend() {
    try {
      String output = "letrec append:1 := map x:2,y:2 to if (x:2 = empty) then y:2 else cons(first(x:2), append:1(rest(x:2), y:2)); in let s:2 := cons(1, cons(2, cons(3, empty))); in append:1(s:2, s:2)";
      String input = "letrec append := map x,y to\n          if x = empty then y else cons(first(x), append(rest\n(x), y));\n            in let s := cons(1,cons(2,cons(3,empty)));\n          in append(s,s)";
      unshadowCheck("Uappend", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Uappend threw " + e);
    }
  } //end of func
  
  public void testSappend() {
    try {
      String output = "letrec [*1*] map [*2*] to if ([0,0] = empty) then [0,1] else cons(first([0,0]), [1,0](rest([0,0]), [0,1])); in let [*1*] cons(1, cons(2, cons(3, empty))); in [1,0]([0,0], [0,0])";
      String input = "letrec append := map x,y to\n          if x = empty then y else cons(first(x), append(rest\n(x), y));\n            in let s := cons(1,cons(2,cons(3,empty)));\n          in append(s,s)";
      SDCheck("Sappend", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Sappend threw " + e);
    }
  } //end of func
  
  public void testCappend() {
    try {
      String output = "letrec append:1 := map x:2,y:2,:0 to if (x:2 = empty) then :0(y:2) " +
                                                           "else let :1 := first(x:2); " +
                                                                "in append:1(rest(x:2), y:2, map :2 to :0(cons(:1, :2))); " +
                      "in let s:2 := cons(1, cons(2, cons(3, empty))); in append:1(s:2, s:2, map x to x)";
      String input = "letrec append := map x,y to\n          if x = empty then y else cons(first(x), append(rest\n(x), y));\n            in let s := cons(1,cons(2,cons(3,empty)));\n          in append(s,s)";
      cpsConvert("Cappend", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cappend threw " + e);
    }
  } //end of func
  

  public void testAppend() {
    try {
      String output = "(1 2 3 1 2 3)";
      String input = "letrec append := map x,y to\n          if x = empty then y else cons(first(x), append(rest\n(x), y));\n            in let s := cons(1,cons(2,cons(3,empty)));\n          in append(s,s)";
//      allEvalCheck("append", output, input );
      evalCheck("append", output, input );
    } catch (Exception e) {
      e.printStackTrace();
      fail("append threw " + e);
    }
  } //end of func
  

  public void testUappend1() {
    try {
      String output = "letrec appendz1:1 := map xz2:2,yz2:2,z0:2 to if (xz2:2 = empty) then z0:2(yz2:2) else let z1:3 := first(xz2:2); in appendz1:1(rest(xz2:2), yz2:2, map z3:4 to z0:2(let z2:5 := z3:4; in cons(z1:3, z2:5))); in let sz2:2 := cons(1, cons(2, cons(3, empty))); in appendz1:1(sz2:2, sz2:2, map x:3 to x:3)";
      String input = "letrec appendz1 := map xz2,yz2,z0 to if (xz2 =empty) then z0(yz2) else let z1 := first(xz2); in appendz1(rest(xz2), yz2, map z3 to z0(let z2 := z3; in cons(z1, z2))); in let sz2 := cons(1, cons(2, cons(3, empty))); in appendz1(sz2, sz2, map x to x)";
      unshadowCheck("Uappend1", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Uappend1 threw " + e);
    }
  } //end of func
  

  public void testCappend1() {
    try {
      String output = "letrec appendz1:1 := map xz2:2,yz2:2,z0:2,:0 to if (xz2:2 = empty) then z0:2(yz2:2, :0) else let z1:3 := first(xz2:2); in appendz1:1(rest(xz2:2), yz2:2, map z3:4,:1 to z0:2(let z2:5 := z3:4; in cons(z1:3, z2:5), :1), :0); in let sz2:2 := cons(1, cons(2, cons(3, empty))); in appendz1:1(sz2:2, sz2:2, map x:3,:2 to :2(x:3), map x to x)";
      String input = "letrec appendz1 := map xz2,yz2,z0 to if (xz2 =empty) then z0(yz2) else let z1 := first(xz2); in appendz1(rest(xz2), yz2, map z3 to z0(let z2 := z3; in cons(z1, z2))); in let sz2 := cons(1, cons(2, cons(3, empty))); in appendz1(sz2, sz2, map x to x)";
      cpsConvert("Cappend1", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cappend1 threw " + e);
    }
  } //end of func
  

  public void testSfact() {
    try {
      String output = "let [*1*] 6; in letrec [*1*] map [*1*] to let [*1*] map [*1*] to [1,0](map [*1*] to ([1,0]([1,0]))([0,0])); in [0,0]([0,0]); in let [*1*] map [*1*] to map [*1*] to if ([0,0] = 0) then 1 else ([0,0] * [1,0](([0,0] - 1))); in ([1,0]([0,0]))([2,0])";
      String input = "let n:= 6; in\n   letrec Y := map f to let g := map x to f(map z to (x(x))(z)); in g(g);\n   in \n    let \n       FACT := map f to map n to if n = 0 then 1 else n * f(n - 1);\n      in (Y(FACT))(n)";
      SDCheck("Sfact", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Sfact threw " + e);
    }
  } //end of func
  

  public void testCfact() {
    try {
      String output = "let Y:1 := map f:1,:0 to " +
                                   "let g:2 := map x:2,:1 to f:1(map z:3,:2 to x:2(x:2, map :3 to let :4 := z:3; in :3(:4, :2)), :1); " +
                                   "in g:2(g:2, :0); " +
                      "in let FACT:1 := map f:1,:5 to :5(map n:2,:6 to if (n:2 = 0) then :6(1) else let :7 := n:2; in f:1((n:2 - 1), map :8 to :6((:7 * :8)))); " +
                         "in Y:1(FACT:1, map :9 to let :10 := 6; in :9(:10, map x to x))";
      String input = "let Y := map f to let g := map x to f(map z to (x(x))(z)); in g(g);" +
                         "FACT := map f to map n to if n = 0 then 1 else n * f(n - 1); " +
                     "in (Y(FACT))(6)";
      cpsConvert("Cfact", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cfact threw " + e);
    }
  } //end of func
  

  public void testFact() {
    try {
      String output = "720";
      String input = "let n:= 6; in\n   letrec Y := map f to let g := map x to f(map z to (x(x))(z)); in g(g);\n   in \n    let \n       FACT := map f to map n to if n = 0 then 1 else n * f(n - 1);\n      in (Y(FACT))(n)";
//      allEvalCheck("fact", output, input);
      evalCheck("fact", output, input );
    } catch (Exception e) {
      e.printStackTrace();
      fail("fact threw " + e);
    }
  } //end of func
  

  public void testUletcc() {
    try {
      String output = "letcc x:1 in if true then x:1(5) else 3";
      String input = "letcc x in  if true then  x(5)  else 3";
      unshadowCheck("Uletcc", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Uletcc threw " + e);
    }
  } //end of func


  public void testCletcc() {
    try {
      String output = "let x:1 := map :0,:1 to (map x to x)(:0); in if true then x:1(5, map x to x) else (map x to x)(3)";
      String input = "letcc x in  if true then  x(5)  else 3";
      cpsConvert("Cletcc", output, input );

    } catch (Exception e) {
      e.printStackTrace();
      fail("Cletcc threw " + e);
    }
  } //end of func

    public void testLetcc1() {
    cpsEvalCheck("<letcc1>", "1", "letcc x in 1");
  }

  public void testLetcc2() {
    cpsEvalCheck("<letcc2>", "1", "letcc x in x(1)");
  }
  public void testLetcc3() {
    cpsEvalCheck("<letcc3>", "10",
             "let prod := map s1 to " +
             "              letcc escape " +
             "              in letrec prodHelp := " +
             "                 map s to if empty?(s) then 1 " +
             "                          else if ~number?(first(s)) then escape(0) " +
             "                          else first(s) * prodHelp(rest(s)); " +
             "               in prodHelp(s1); " +
             "in prod(cons(2,cons(5,empty)))");
  }

  public void testSmallThunkSieve() {
    try {
      String output = "(3 5 7 11 13 17)";
      String input = "letrec susp? := map l to cons?(l) & function?(first(l)); " +
                         "makeSusp := map f to cons(f, empty); " +
                               "fo := map prom to if susp?(prom) then (first(prom))() else prom; " +
                        "mapStream := map f,l to let fol := fo(l);" +
                        "                        in if (fol = empty) then empty " +
                        "                           else cons(f(first(fol)), makeSusp(map  to mapStream(f, rest(fol)))); " +
                           "filter := map p,l to let fol := fo(l);" +
                           "                     in if (fol = empty) then empty " +
                           "                        else if p(first(fol)) then filter(p, rest(fol)) " +
                           "                        else cons(first(fol), makeSusp(map  to filter(p, rest(fol)))); " +
                          "divides := map a,b to (((b / a) * a) = b); " +
                          "initSeg := map l,n to if (n <= 0) then empty " +
                          "                      else let fol := fo(l); " +
                          "                           in cons(first(fol), initSeg(rest(fol), n-1)); " +
                           "primes := map l to let fol := fo(l); " +
                           "                   in cons(first(fol), makeSusp(map  to primes(filter(map x to divides(first(fol), x), rest(fol)))));  " +
                          "oddNums := map  to cons(3, makeSusp(map  to mapStream(map i to (i + 2), oddNums()))); " +
                      "in initSeg(primes(oddNums()), 6)";
      allEvalCheck("[4.00] ethunkSieve", output, input);

    } catch (Exception e) {
      e.printStackTrace(System.err);
      fail("[4.00] smallThunkSieve threw " + e);
    }
  } //end of func

  /**
   * Tests for SDEvaluator
   */

  public void testLiteralEvaluation() {
    String output = "42";
    String input = "42";
    allEvalCheck("LiteralEvaluation", output, input);
  }

  public void testSimpleArithmetic() {
    String output = "7";
    String input = "3 + 4";
    allEvalCheck("SimpleArithmetic", output, input);
  }

  public void testVariableLookup() {
    String output = "5";
    String input = "let x := 5; in x";
    allEvalCheck("VariableLookup", output, input);
  }

  public void testFunctionApplication() {
    String output = "8";
    String input = "(map x to x + 3)(5)";
    allEvalCheck("FunctionApplication", output, input);
  }

  public void testConditional() {
    String output = "true";
    String input = "if 1 < 2 then true else false";
    allEvalCheck("Conditional", output, input);
  }

  public void testSLetWithMultipleBindings() {
    String output = "6";
    String input = "let a := 2; b := 3; in a * b";
    allEvalCheck("SLetWithMultipleBindings", output, input);
  }

    public void testSLetWithMultipleBindings2() {
        String output = "6";
        String input = "let a := 2; b := 3; in a * b";
        allEvalCheck("SLetWithMultipleBindings2", output, input);
    }

  public void testSLetWithShadowing() {
      String output = "6";
      String input = "let a := 2; in let a := 3; in a * 2";
      allEvalCheck("SLetWithShadowing", output, input);
  }


//  public void testSLetRecWithMutualRecursion() {
//    String output = "true";
//    String input =
//            "letrec even := map n to if n == 0 then true else odd(n - 1);" +
//                    "       odd := map n to if n == 0 then false else even(n - 1);" +
//                    "in even(4)";
//    allEvalCheck("SLetRecWithMutualRecursion", output, input);
//  }
//
//  public void testUnboundVariableError() {
//    try {
//      String input = "x";
//      SDEvalCheck("UnboundVariableError", "!", input);
//      fail("UnboundVariableError did not throw EvalException exception");
//    } catch (EvalException e) {
//      // Expected exception.
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail("UnboundVariableError threw " + e);
//    }
//  }
//
//  public void testRecursiveFunction() {
//    try {
//      String output = "120";
//      String input = "letrec fact := map n to if n == 0 then 1 else n * fact(n - 1) in fact(5)";
//      allEvalCheck("RecursiveFunction", output, input);
//    } catch (ParseException e) {
//      fail("testRecursiveFunction threw ParseException: " + e.getMessage());
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail("testRecursiveFunction threw unexpected exception: " + e);
//    }
//  }
}






import java.util.StringTokenizer;
import junit.framework.TestCase;
import java.io.*;

public class SyntaxTestPrivate extends TestCase {
  
  public SyntaxTestPrivate(String name) {
    super(name);
  }
  
  /** The following 3 check methods create an interpreter object with the
    * specified String as the program, perform the specified checking and
    * transformation, and evaluate the transformed program using the appropriate
    * evaluator.  If the test fails, the method prints a report as to which test 
    * failed and how many points should be deducted.
    */
 
  /** The total number of points in the test suite is 60 */
  private void parseCheck(String name, String answer, String program) {
    Parser parser = new Parser(new StringReader(program));
    assertEquals("parse " + name, answer, parser.parseProg().toString());
  }
  
  private void syntaxCheck(String name, String answer, String program) {
    Parser parser = new Parser(new StringReader(program));
    assertEquals("parse and check " + name, answer, parser.checkProg().toString());
  }
  
  private void cpsCheck(String name, String answer, String program) {
    Parser parser = new Parser(new StringReader(program));
    assertEquals("parse, check, and cps " + name, answer, parser.cpsProg().toString());
  }
 
  /** Test all 10 prim functions in both modes; 0.2 pts for each test method, 2 pts total*/
  
  public void testNumberP() {
    syntaxCheck("<Number? 0.2pts>", "number?", "number?");
  }

  public void testFunctionP() {
    parseCheck("<Function? 0.2pts>", "function?", "function?");
  }

  public void testEmptyP() {
    syntaxCheck("<Empty? 0.2pts>", "empty?", "empty?");
  }

  public void testConsP() {
    syntaxCheck("<Cons? 0.2pts>", "cons?", "cons?");
  }

}

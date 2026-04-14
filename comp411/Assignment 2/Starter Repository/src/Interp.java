/** STUB FILE **/

/** Call-by-value, Call-by-name, and Call-by-need Jam interpreter */

import java.io.IOException;
import java.io.Reader;

/** Interpreter Classes */

/** A class that implements call-by-value, call-by-name, and call-by-need interpretation of Jam programs. */
class Interpreter {
  
  /** program for this interpeter, initialized in constructors. */
  AST prog;
  
  /** Constructor for a program given in a file. Not used in Assignment 2*/
  public Interpreter(String fileName) throws IOException { 
    Parser p = new Parser(fileName);
    prog = p.parse();
  }
  
  /** Constructor for a program already embedded in a Parser object. */
  public Interpreter(Parser p) { 
    prog = p.parse();
  }  
  
  /** Constructor for a program embedded in a Reader. Not used in Assignment 2. */
  public Interpreter(Reader reader) { 
    Parser p = new Parser(reader);
    prog = p.parse();
  }
  
  /** Parses and CBV interprets the input embeded in parser */
  public JamVal callByValue() { return prog.accept(valueEvalVisitor); }
  
  /** Parses and CBNm interprets the input embeded in parser */
  public JamVal callByName() { return prog.accept(nameEvalVisitor); }
  
  /** Parses and CBNd interprets the input embeded in parser */
  public JamVal callByNeed() { return prog.accept(needEvalVisitor); }
   
  /** A class representing an unevaluated expresssion (together with the corresponding evaluator). */
  private static class ConcreteSuspension implements Suspension {
    private AST exp;
    private EvalVisitor ev;  
    
    ConcreteSuspension(AST a, EvalVisitor e) { exp = a; ev = e; }
    
    AST exp() { return exp; }
    EvalVisitor ev() { return ev; }
    void putEv(EvalVisitor e) { ev = e; }
    
    /** Evaluates this suspension. Only method of Suspension interface. */
    public JamVal eval() { 
      // System.err.println("eval() called on the susp with AST = " + exp);
      return exp.accept(ev);  } 
    
    public String toString() { return "<" + exp + ", " + ev + ">"; }
  }
  
  /** Class representing a binding in CBV evaluation. */
  private static class ValueBinding extends Binding {
    ValueBinding(Variable v, JamVal jv) { super(v, jv); }
    public JamVal value() { return value; }
    public void setBinding(Suspension s) { /* ... */ }
    public String toString() { return "[" + var + ", " + value + "]"; }
  }
  
  /** Class representing a binding in CBName evaluation. The inherited value field is ignored. */
  private static class NameBinding extends Binding {
    protected Suspension susp;
    NameBinding(Variable v, Suspension s) { 
      super(v, null);
      susp = s;
    }
    public JamVal value() { /* ... */ return null; }
    public void setBinding(Suspension s) { /* ... */ }
    public String toString() { return "[" + var + ", " + susp + "]"; }
  }
  
  /** Class representing a binding in CBNeed evaluation.  The inherited value field is used to hold the value
    * first computed by need-driven evaluation. */
  private static class NeedBinding extends NameBinding {
    NeedBinding(Variable v, Suspension s) { super(v,s); }
    public JamVal value() { /* ... */ return null; }
    public String toString() { return "[" + var + ", " + value + ", " + susp + "]"; }
  }
  
  /** Visitor class implementing a lookup method on environments.
    * @return value() for variable var for both lazy and eager environments. */
  private static class LookupVisitor implements PureListVisitor<Binding,JamVal> {

    Variable var;   // the lexer guarantees that there is only one Variable for a given name
    
    LookupVisitor(Variable v) { var = v; }
    
    /* Visitor methods. */
    public JamVal forEmpty(Empty<Binding> e) { /* ... */ return null; }
    public JamVal forCons(Cons<Binding> c) { /* ... */ return null; }
  }
 
  /** The interface supporting various evaluation policies (CBV, CBNm, CBNd) for map applications and let constructions. 
    * The EvalVisitor parameter appears in each method because the variable is bound to the value of the AST which is
    * determined using the specified EvalVisitor.  Note: all of the differences among CBV, CBNm, and CBNd evaluation can 
    * be encapulated in different implementations of the Binding interface.  Perhaps this interface should be called
    * BindingPolicy.  Since this interface is not part of the Interpreter API required by Assign2Test.java, you are free
    * to rename it if you choose.
    */
  private interface EvalPolicy {
    /** Constructs the appropriate binding object for this, binding var to ast in the evaluator ev */
    Binding newBinding(Variable var, AST ast, EvalVisitor ev);
  }
  
  /** An ASTVisitor class for evaluating ASTs where the evaluation policy for function applications and other
    * binding operations such as let are determined by an embedded EvalPolicy. */
  private static class EvalVisitor implements ASTVisitor<JamVal> {
    
    /* The code in this class assumes that:
     * * OpTokens are unique; 
     * * Variable objects are unique: v1.name.equals(v.name) => v1 == v2; and
     * * The only objects used as boolean values are BoolConstant.TRUE and BoolConstant.FALSE.
     * Hence,  == can be used to compare Variable objects, OpTokens, and BoolConstants. */
    
    private PureList<Binding> env;  // the embdedded environment
    private EvalPolicy evalPolicy;  // the embedded EvalPolicy
    
    /** Constructor for recursive calls. */
    private EvalVisitor(PureList<Binding> e, EvalPolicy ep) { 
      env = e; 
      evalPolicy = ep; 
    }
    
    /** Top level constructor. */
    public EvalVisitor(EvalPolicy ep) { this(new Empty<Binding>(), ep); }
    
    /** Factory method that constructs a new visitor with environment e and same evalPolicy as this.  It is used
      * for recursive invocations of this evaluator.  Essential in some contexts because it has access to evalPolicy. */
    public EvalVisitor newVisitor(PureList<Binding> e) { /* ... */ return null; }
    
    /** Factory method that constructs a Binding of var to ast corresponding to this.evalPolicy */
    public Binding newBinding(Variable var, AST ast) { /* ... */ return null; }
    
    /** Getter for env field */
    public PureList<Binding> env() { return env; }
    
    /* ASTVisitor methods */
    public JamVal forBoolConstant(BoolConstant b) { /* ... */ return null; }
    public JamVal forIntConstant(IntConstant i) { /* ... */ return null; }
    public JamVal forEmptyConstant(EmptyConstant n) { /* ... */ return null; }
    public JamVal forVariable(Variable v) { /* ... */ return null; }
    
    public JamVal forPrimFun(PrimFun f) { /* ... */ return null; }
    
    public JamVal forUnOpApp(UnOpApp u) { /* ... */ return null; }
    
    public JamVal forBinOpApp(BinOpApp b) { /* ... */ return null; }
    
    public JamVal forApp(App a) { /* ... */ return null; }
    
    public JamVal forMap(Map m) { /* ... */ return null; }
    
    public JamVal forIf(If i) { /* ... */ return null; }
    
    /* let (non-recursive) semantics */
    public JamVal forLet(Let l) { /* ... */ return null; }
  }
  
  /** Top-level EvalVisitors implementing CBV, CBNm, and CBNd evaluation. */
  private static EvalVisitor valueEvalVisitor = new EvalVisitor(CallByValue.ONLY);
  private static EvalVisitor nameEvalVisitor = new EvalVisitor(CallByName.ONLY);
  private static EvalVisitor needEvalVisitor = new EvalVisitor(CallByNeed.ONLY);
  
  /** JamFunVisitor class that evaluates the application of a JamFun (the host) to the argument expressions given in
    * AST[] args unsing the given evalVisitor, which interprets ASTs. The evalVisitor is used to evaluate args and to 
    * generate the new visitor for evaluating the map body embedded of the JamFun if it is a JamClosure. */
  private static class StandardFunVisitor implements JamFunVisitor<JamVal> {
    
    /** Unevaluated arguments */
    AST[] args;
    
    /** Evaluation visitor */
    EvalVisitor evalVisitor;
    
    StandardFunVisitor(AST[] asts, EvalVisitor ev) {
      args = asts;
      evalVisitor = ev;
    }
    
    /** Evaluate the arguments of a PrimFun application. */
    private JamVal[] evalArgs() { /* ... */ return null; }
    
    /* Visitor methods. */
    public JamVal forJamClosure(JamClosure closure) { /* ... */ return null; }
   
    public JamVal forPrimFun(PrimFun primFun) { /* ... */ return null; }
  }
  
  private static class CallByValue implements EvalPolicy {
    
    public static final EvalPolicy ONLY = new CallByValue();
    private CallByValue() { }
    
    /** Constructs binding of var to value of arg in ev */
    public Binding newBinding(Variable var, AST arg, EvalVisitor ev) { /* ... */ return null; }
  }
    
  private static class CallByName implements EvalPolicy {
    public static final EvalPolicy ONLY = new CallByName();
    private CallByName() {}
    
    public Binding newBinding(Variable var, AST arg, EvalVisitor ev) { /* ... */ return null; }
  }
  
  private static class CallByNeed implements EvalPolicy {
    public static final EvalPolicy ONLY = new CallByNeed();
    private CallByNeed() {}
    
    public Binding newBinding(Variable var, AST arg, EvalVisitor ev) { /* ... */ return null; }
  }
  
  /* Only works for eager unary operators; need to add a newUnOpVisitor method to EvalPolicy if lazy unary operator is
   * added to Jam*/
  private static class StandardUnOpVisitor implements UnOpVisitor<JamVal> {
    private final JamVal val;
    StandardUnOpVisitor(JamVal jv) { val = jv; }
    
    /** Returns val provided it is an IntConstant; throw EvalException otherwise. */
    private IntConstant checkInteger(String op) { /* ... */ return null; }
    
    /** Returns val provided it is an BoolConstant; throw EvalException otherwise. */
    private BoolConstant checkBoolean(String op) { /* ... */ return null; }

    public JamVal forUnOpPlus() { /* ... */ return null; } 
    public JamVal forUnOpMinus() { /* ... */ return null; } 
    public JamVal forOpTilde() { /* ... */ return null; }
    
    // public JamVal forOpBang() { return ... ; }  // Supports addition of ref cells to Jam in Assignment 4
    // public JamVal forOpRef() { return ... ; }   // Supports addition of ref cells to Jam in Assignment 4
  }
  
  private static class StandardBinOpVisitor implements BinOpVisitor<JamVal> { 
    private AST arg1, arg2;
    private EvalVisitor evalVisitor;
    
    StandardBinOpVisitor(AST a1, AST a2, EvalVisitor ev) { arg1 = a1; arg2 = a2; evalVisitor = ev; }
    
    /** Evaluate the specified argument AST and confirm that it is an IntConstant. */
    private IntConstant evalIntegerArg(AST arg, String op) { /* ... */ return null; }
    
    /** Evaluate the specified argument AST and confirm that it is a BoolConstant. */
    private BoolConstant evalBooleanArg(AST arg, String op) { /* ... */ return null; }
    
    public JamVal forBinOpPlus() { /* ... */ return null; }
    public JamVal forBinOpMinus() { /* ... */ return null; }
    public JamVal forOpTimes() { /* ... */ return null; }
    public JamVal forOpDivide() { /* ... */ return null; }
    
    public JamVal forOpEquals() { /* ... */ return null; } 
    public JamVal forOpNotEquals() { /* ... */ return null; }
    public JamVal forOpLessThan() { /* ... */ return null; }
    public JamVal forOpGreaterThan() { /* ... */ return null; }
    public JamVal forOpLessThanEquals() { /* ... */ return null; }
    public JamVal forOpGreaterThanEquals() { /* ... */ return null; }
    
    public JamVal forOpAnd() { /* ... */ return null; }
    public JamVal forOpOr() { /* ... */ return null; }
    
    // public JamVal forOpGets(OpGets op) { return ... ; }  // Supports addition of ref cells to Jam in Assignment 4
  }
    
  /** Visitor that implements each primitive function.  Only supports strict functions. */
  private static class StandardPrimFunVisitor implements PrimFunVisitor<JamVal> {
    
    JamVal[] vals;  // evaluated arguments for PrimFun App
    
    StandardPrimFunVisitor(JamVal[] vls) { vals = vls; }
    
    private JamVal primFunError(String fn) {
      throw new EvalException("Primitive function `" + fn + "' applied to " + vals.length + " arguments");
    }
    
    /** Confirms that vals[0] (first evaluated argument in application of fun) is a JamCons. Assumes that any
      * such function is unary. */
    private JamCons confirmJamCons(String funName) { /* ... */ return null; }
    
    public JamVal forFunctionPPrim() { /* ... */ return null; }
    public JamVal forNumberPPrim() { /* ... */ return null; }
    public JamVal forListPPrim() { /* ... */ return null; }
    public JamVal forConsPPrim() { /* ... */ return null; }
    public JamVal forEmptyPPrim() { /* ... */ return null; }
    public JamVal forConsPrim() { /* ... */ return null; }
    public JamVal forArityPrim() { /* ... */ return null; }
    public JamVal forFirstPrim() { /* ... */ return null; }
    public JamVal forRestPrim() { /* ... */ return null; }
    
  }
}
 
class EvalException extends RuntimeException {
  EvalException(String msg) { super(msg); }
}

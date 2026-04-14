/** A visitor class for the syntax checker. Returns normally unless there is a syntax error. On a syntax error, 
  * throws a SyntaxException.
  */
class CheckVisitor implements ASTVisitor<Void> {
  
  /** Empty symbol table. */
  private static final Empty<Variable> EMPTY_VARS = new Empty<Variable>();
  
  /** Symbol table to detect free variables. */
  PureList<Variable> env;
  
  /** Root form of CheckVisitor. */
  public static final CheckVisitor INITIAL = new CheckVisitor(EMPTY_VARS);
  
  CheckVisitor(PureList<Variable> e) { env = e; }

  /* Your private methods go here. */
  
  /* ASTVisitor methods. */
  public Void forBoolConstant(BoolConstant b) {  /* ... Your code replaces */ return null; }
  public Void forIntConstant(IntConstant i) {  /* ... Your code replaces */ return null; }
  public Void forEmptyConstant(EmptyConstant n) {  /* ... Your code replaces */ return null; }
  public Void forVariable(Variable v) { /* ... Your code replaces */ return null; }
  public Void forPrimFun(PrimFun f) { /* ... Your code replaces */ return null; }
  public Void forUnOpApp(UnOpApp u) { /* ... Your code replaces */ return null; }
  public Void forBinOpApp(BinOpApp b) { /* ... Your code replaces */ return null; }
  public Void forApp(App a) { /* ... Your code replaces */ return null; }
  public Void forMap(Map m) { /* ... Your code replaces */ return null; }
  public Void forIf(If i) { /* ... Your code replaces */ return null; }
  public Void forLet(Let l) { /* ... Your code replaces */ return null; }
}

/** Singleton visitor that checks for duplicate variables in a symbol table. Returns normally unless an error is found.
  * Throws a SyntaxException on error.
  */
class AnyDuplicatesVisitor implements PureListVisitor<Variable, Void> {
  public Void forEmpty(Empty<Variable> e) { /* ... Your code replaces */ return null; }
  public Void forCons(Cons<Variable> c) { /* ... Your code replaces */ return null; }
}

/** Exception type thrown by the context-sensitive checker. */
class SyntaxException extends RuntimeException {
  SyntaxException(String s) { super(s); }
}

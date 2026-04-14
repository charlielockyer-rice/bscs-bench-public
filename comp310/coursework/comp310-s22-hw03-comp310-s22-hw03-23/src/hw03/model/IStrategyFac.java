package hw03.model;

/**
 * An interface that defines a factory that instantiates 
 * a specific IUpdateStrategy
 */
public interface IStrategyFac { 
  /**
   * Instantiate the specific IUpdateStrategy for which this factory is defined.
   * @return An IUpdateStrategy instance.
   */
  public IUpdateStrategy make();
  
  /**
   * Return a string that is the toString()'s of the given strategy factories concatenated with a "-"
   */
  public String toString();
  
  /**
 * 
 */
public static IStrategyFac errorStrategyFac = new IStrategyFac(){
      /**
       * Instantiate a strategy corresponding to the given class name.
       * @return An IUpdateStrategy instance
       */
      public IUpdateStrategy make() {
          return IUpdateStrategy.errorStrategy;
      }
   };
}


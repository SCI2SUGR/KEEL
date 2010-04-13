/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public abstract class FuzzyRegressor {
/** 
* <p> 
* It is the abstract class for the remaining basic classes related with Fuzzy Regression defined in keel.Algorithms.Symbolic_Regression.  
* 
* </p> 
*/
    //the type for fuzzy regressors based on crisp sets (singleton fuzzy sets) 
	public final static int Crisp=0;
	//the type for fuzzy regressors based on interval sets (interval fuzzy sets)
    public final static int Interval=1;
    //the type for fuzzy regressors based on fuzzy sets (triangular fuzzy sets)
    public final static int Fuzzy=2;
    //the type of constants (Crisp, Interval and Fuzzy) to manage in derived classes. 
    protected static int constType; 
    /** 
     * <p> 
     * Creates and returns a fuzzy alpha-cut with result of the run.
     * 
     * </p>
     * @return a fuzzy alpha-cut with result of the run. 
     */
    public abstract FuzzyAlphaCut output(FuzzyAlphaCut[] x);
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */
    public abstract FuzzyRegressor clone();
    /**
     * Get current debugging message setting.
     */
    public abstract void debug();
}

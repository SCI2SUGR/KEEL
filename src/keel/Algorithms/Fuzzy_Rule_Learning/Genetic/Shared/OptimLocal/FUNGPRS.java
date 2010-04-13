/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 03/03/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

// RegSymFuzzyGP Wrapper.
// Descent method optimization.

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;
import keel.Algorithms.Shared.Exceptions.*;
    
public class FUNGPRS extends FUN {
	/**
	 * <p>
	 * Class for evaluating the fitness of a rule set
	 * Need: The fuzzy system
	 * </p>
	 */
    
    RegSymFuzzyGP f;
    
    /**
     * <p> 
     * Constructor
     * </p>
     * @param vf Fuzzy system
     * @param used The set od used rules
     * @param initial The set of initial rules
     */
    public FUNGPRS(RegSymFuzzyGP vf, boolean[] used, double[]initial) { 
        super(used,initial);
        f=vf; 
    }
    
    /**
     * <p>
     * The public method evaluate the fitness of the rule set
     * </p>
     * @param x The set of rules
     * @return The result of evaluate the fitness
     */
    public double evaluate(double[] x) { 
        f.setConsts(filter(x));
        
        try {
          double result=f.fitness(); 
          f.setConsts(initial);
          return result;
        } catch(invalidFitness e) {
            System.err.println(e);   
            return 0;
        }
    }
    
}

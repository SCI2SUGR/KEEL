/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 03/03/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

// Paquete Optimizacion Local
// Local optimization package
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;

abstract public class FUN {
	/**
	 * <p>
	 * Class for duplicate the initial ana used rles and fro filter the used ones
	 * Need: The initial set of rules and the used ones
	 *</p>
	 */
    // Virtual class with a double [] to double function 
    protected boolean []used;
    protected double []initial;
    
    /**
     * <p>
     * Constructor
     * </p>
     * @param vused Mark the used rules
     * @param vinitial A set of initial rules
     */
    FUN(boolean []vused, double[]vinitial) {
        used=duplicate(vused);
        initial=duplicate(vinitial);
    }
    
    /**
     * <p>
     * The protected method duplicate a set of boolean values
     * </p>
     * @param x The set of boolean values
     * @return The duplicate set
     */
    protected boolean[] duplicate(boolean[] x) {
        boolean []result = new boolean[x.length];
        for (int i=0;i<x.length;i++) result[i]=x[i];
        return result;
    }
    
    /**
     * <p>
     * The protected method duplicate a set of double values
     * </p>
     * @param x The set of double values
     * @return The duplicate set
     */
    protected double[] duplicate(double[] x) {
        double []result = new double[x.length];
        for (int i=0;i<x.length;i++) result[i]=x[i];
        return result;
    }
    
    /**
     * <p>
     * The protected method filter the rules to select the used one
     * </p>
     * @param x The rules
     * @return The used rules
     */
    protected double[] filter(double []x) {
        double result[] = duplicate(initial);
        for (int i=0;i<x.length;i++) 
            if (used[i]) result[i]=x[i];
        return result;
    }
    
    
    /**
     * <p>
     * The abstract method evaluate the fitness of the rule set
     * </p>
     * @param x The set of rules
     * @return The result of evaluate the fitness
     */
    abstract public double evaluate(double x[]);
}



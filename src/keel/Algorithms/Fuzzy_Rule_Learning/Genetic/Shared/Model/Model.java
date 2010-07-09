/**
 * <p>
 * @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model;

import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public abstract class Model {
/**
 * <p>
 * Class for define abstract methods
 * </p>
 */
	
	/**
     * <p>
     * This abstract method return the output of the model defuzzified
     * </p>
     * @param x The output
     */	
	abstract public double output(double[] x);
    
	/**
	 * <p>
	 * This abstrac method clone a model
	 * </p>
	 * @return The model cloned
	 */
	abstract public Model clone();
	
	/**
	 * <p>
	 * This abstract method is for debug
	 * </p>
	 */
    abstract public void debug();
}

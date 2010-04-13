/**
 * <p>
 * @author Written by Luciano Sanchez (University of Oviedo) 21/01/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.00
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node;

public class IntDouble {
/**
 * <p>
 * Display the consequent and the weight of the rule as a string
 * Need: The consequent and the weight
 * </p>
 */
	public int consequent;
    public double weight;
    
    /**
     * <p>
     * This method displays the consequent and the weight of the rule as a string
     * </p>
     * @return The consequent and the weight as a string
     */
    public String aString() {
        return "{"+consequent+" "+weight+"}";
    }
}

/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

public class TTable {
    /**
     * <p>
     * This class is defined to contain a bidimensional array with the instances
     * of the dataset, the class of the instance and if it is covered by any rule
     * </p>
     */

    int num;            // Number of the register of the dataset
    float[] ejemplo;    // Example values for all of the variables
                        // Enumerated values are translated into integers
    int clase;          // Class of the example for the target var
    boolean fcubierto;  // False if not covered by any rule; true otherwise - fuzzy version
    boolean ccubierto;  // False if not covered by any rule; true otherwise - crisp version
    
    /**
     * <p>
     * Creates a new instance of TTable
     * </p>
     */
    public TTable() {
    }
    
}

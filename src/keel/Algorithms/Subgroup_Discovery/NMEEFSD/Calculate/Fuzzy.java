/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 27/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate;

public class Fuzzy {
    /**
     * <p>
     * Values for a fuzzy set definition
     * </p>
     */

    float x0,x1,x3;
    float y;  

    /**
     * <p>
     * This function fuzzy a value
     * </p>
     * @param X     Continuous value of the variable to fuzzy
     * @return      The belonging degree of the value
     */
    public float Fuzzy (float X) {
        if ((X<=x0) || (X>=x3))  // If value of X is not into range x0..x3
            return (0);          // then pert. degree = 0 
        if (X<x1)
            return ((X-x0)*(y/(x1-x0)));
        if (X>x1)
            return ((x3-X)*(y/(x3-x1)));
        return (y);
    }

    /**
     * <p>
     * Creates a new instance of Fuzzy
     * </p>
     */
    public Fuzzy() {
    }
    
}


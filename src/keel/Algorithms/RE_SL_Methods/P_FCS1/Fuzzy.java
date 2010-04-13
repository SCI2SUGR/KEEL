package keel.Algorithms.RE_SL_Methods.P_FCS1;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

public class Fuzzy {
/**	
 * <p>
 * It contains the definition for a gaussian fuzzy set
 * </p>
 */
 
    double center, width;

    /**
     * <p>       
     * Default constructor
     * </p>       
     */
    public Fuzzy() {
        center = 0.0;
        width = 0.0;
    }

    /**
     * <p>       
     * Creates a gaussian fuzzy set as a copy of another gaussian fuzzy set
     * </p>       
     * @param dif Fuzzy The gaussian fuzzy set used to create the new gaussian fuzzy set
     */
    public Fuzzy(Fuzzy dif) {
        center = dif.center;
        width = dif.width;
    }

}

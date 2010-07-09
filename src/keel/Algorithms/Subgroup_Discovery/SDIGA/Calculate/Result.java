/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.Calculate;

public class Result {

    /**
     * <p>
     * Used to store the quality measures for the generated rule
     * </p>
     */

    // Contents of Result are accessed directly, not by methods
    float perf;    // Performance (fitness)
    float fconf;   // Fuzzy Confidence
    float cconf;   // Crisp Confidence
    float comp;    // Support
    float csup;    // Support (completitud) as defined in Lavrac
    float fsup;    // Fuzzy support (divided by all examples)
    float cov;     // Coverage, measured as crisp rules?
    float sign;    // Significance
    float unus;    // Unusualness
    float accu;    // Accuracy
    
    
    /**
     * <p>
     * Creates a new instance of Result
     * </p>
     */
    public Result() {
    }
    
}

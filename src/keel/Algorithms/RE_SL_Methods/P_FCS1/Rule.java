package keel.Algorithms.RE_SL_Methods.P_FCS1;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Rule {
/**	
 * <p>
 * It contains the definition for a fuzzy rule
 * </p>
 */
 
    Fuzzy [] memfunctions;

    /**
     * <p>       
     * Creates a rule containing "tam" gaussian fuzzy sets
     * </p>       
     * @param tam int The number of gaussian fuzzy sets in the rule
     */
    public Rule(int tam) {
        memfunctions = new Fuzzy[tam];
        for(int i = 0; i < tam; i++){
            memfunctions[i] = new Fuzzy();
        }
    }


    /**
     * <p>       
     * Creates a fuzzy rule as a copy of another fuzzy rule
     * </p>       
     * @param reg Rule The fuzzy rule used to create the new fuzzy rule
     */
    public Rule(Rule reg) {
        int tam = reg.memfunctions.length;
        memfunctions = new Fuzzy[tam];
        for(int i = 0; i < tam; i++){
            memfunctions[i] = new Fuzzy(reg.memfunctions[i]);
        }
    }

}

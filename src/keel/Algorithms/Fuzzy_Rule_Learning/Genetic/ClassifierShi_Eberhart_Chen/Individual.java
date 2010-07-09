package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierShi_Eberhart_Chen;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Individual implements Comparable {
/**	
 * <p>
 * An individual of the population
 * </p>
 */
 
    int[] cromosoma;
    double fitness;
    int n_e;
    int num_feasibles;
    boolean [] feasibles;

    /**
     * <p>       
     * Creates an individual containing with a legth equal to "longitud" and having "max_n_reglas" rules
     * </p>       
     * @param max_n_reglas int The number of rules in the individual
     * @param longitud int The length of the individual
     */
    public Individual(int max_n_reglas, int longitud) {
        cromosoma = new int[longitud];
        fitness = -1.0;
        n_e = 1;
        num_feasibles = 0;
        feasibles = new boolean[max_n_reglas];
    }


    /**
     * <p>
     * Compares the fitness value of two individuals
     * </p>
     * @return int Returns -1 if the the fitness of the first individual is lesser than the fitness of the second one.
     * 1 if the the fitness of the first individual is greater than the fitness of the second one.
     * 0 if both individuals have the same fitness value
     */
    public int compareTo(Object a) {
        if (((Individual) a).fitness < this.fitness) {
            return -1;
        }
        if (((Individual) a).fitness > this.fitness) {
            return 1;
        }
        return 0;
    }

}

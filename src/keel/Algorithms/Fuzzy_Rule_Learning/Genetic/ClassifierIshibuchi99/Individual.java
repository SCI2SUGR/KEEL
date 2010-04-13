package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierIshibuchi99;

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
 
    int[] Arbol;
    int clase;
    int fitness;
    double grado_certeza;

    /**
     * <p>       
     * Creates an individual containing with "variables" input variables
     * </p>       
     * @param variables int The number of input variables
     */
    public Individual(int variables) {
        Arbol = new int[variables];
        clase = -1;
        fitness = -1;
        grado_certeza = -1;
    }

    /**
     * <p>       
     * Creates an individual as a copy of another individual
     * </p>       
     * @param indi Individual The individual used to create the new individual
     */
    public Individual(Individual indi) {
        int tam = indi.Arbol.length;
        Arbol = new int[tam];
        for(int i = 0; i < tam; i++){
            Arbol[i] = indi.Arbol[i];
        }
        clase = indi.clase;
        fitness = indi.fitness;
        grado_certeza = indi.grado_certeza;
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

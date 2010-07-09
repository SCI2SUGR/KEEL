package keel.Algorithms.RE_SL_Methods.SEFC;

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
 * It contains the definition for a individual
 * </p>
 */
 
    MemFun [] antecedente;
    double [] consecuente;
    double fitness;
    int n_e;
    int n_SistemasDifusos;

    /**
     * <p>       
     * Creates an individual containing "entradas" gaussian membership functions
     * </p>       
     * @param n_var int The number of gaussian membership functions in the individual
     */
    public Individual(int entradas) {
        antecedente = new MemFun[entradas];
        for(int i = 0; i < entradas; i++){
            antecedente[i] = new MemFun();
        }
        consecuente = new double[entradas+1];
        fitness = -1.0;
        n_e = 1;
        n_SistemasDifusos = -1;
    }

    /**
     * <p>       
     * Creates an individual as a copy of another individual
     * </p>       
     * @param indi Individual The individual used to create the new individual
     */
    public Individual(Individual indi) {
        int tam = indi.antecedente.length;
        antecedente = new MemFun[tam];
        for(int i = 0; i < tam; i++){
            antecedente[i] = new MemFun();
            antecedente[i].m = indi.antecedente[i].m;
            antecedente[i].sigma = indi.antecedente[i].sigma;
        }
        consecuente = new double[tam+1];
        for(int i = 0; i <= tam; i++){
            consecuente[i] = indi.consecuente[i];
        }

        fitness = indi.fitness;
        n_e = indi.n_e;
        n_SistemasDifusos = indi.n_SistemasDifusos;
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

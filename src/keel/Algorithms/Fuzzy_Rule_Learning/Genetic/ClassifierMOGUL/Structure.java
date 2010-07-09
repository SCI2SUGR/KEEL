package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

class Structure {
/**	
 * <p>
 * Each member of the population has this form
 * </p>
 */
 
    public double[] Gene;
    public char[] GeneSel;
    public int n_genes;
    public double Perf;
    public T_Consequent[] Consecuente;
    public int n_e;

    /**
     * <p>
     * Constructor
     * </p>
     * @param genes int The number of genes of the chromosome
     * @param num_clases int the number of clases of the problem     
     */
    public Structure(int genes, int num_clases) {
        n_genes = genes;
        Gene = new double[n_genes];
        GeneSel = new char[n_genes];

        Consecuente = new T_Consequent[num_clases];
        Consecuente = new T_Consequent[num_clases];
        for (int i = 0; i < num_clases; i++) {
            Consecuente[i] = new T_Consequent();
        }
    }
}

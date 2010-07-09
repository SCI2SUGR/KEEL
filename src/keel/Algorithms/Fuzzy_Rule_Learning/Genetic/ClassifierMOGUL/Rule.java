package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
class Rule {
/**
 * <p>
 * Each rule of the population has this form
 * </p>
 */
  
        public FuzzySet [] Ant;
        public T_Consequent [] Cons;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * @param n_ant int Number of input variables
	 * @param n_clases int Number of classes for the problem
	 */
        public Rule(int n_ant, int n_clases) {
                int i;

                Ant = new FuzzySet[n_ant];
                Cons = new T_Consequent[n_clases];

                for (i=0; i<n_ant; i++)  Ant[i] = new FuzzySet();
                for (i=0; i<n_clases; i++)  Cons[i] = new T_Consequent();
        }

}

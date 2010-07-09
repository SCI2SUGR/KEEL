package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

class T_Table {
/**	
 * <p>
 * Each instance has this form
 * </p>
 */

        public double [] ejemplo; /* data */
        public int n_variables;   /* number of variables */
        public double nivel_cubrimiento, maximo_cubrimiento; /* matching degree */
        public int cubierto;      /* it's 1 if the instance is covered */

	/**	
 	 * <p>
	 * Constructor
	 * </p>
	 * @param var int The number of variables (input + output) of the data set
	 */
        public T_Table (int var) {
                n_variables = var;
                ejemplo = new double[n_variables];

                nivel_cubrimiento = (double) 0.0;
                maximo_cubrimiento = (double) 0.0;
                cubierto = 0;
        }
}

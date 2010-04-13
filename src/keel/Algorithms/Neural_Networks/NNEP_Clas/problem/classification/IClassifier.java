package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification;


/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba)16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IClassifier {
	
	/**
	 * <p>
	 * Generic classifier.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Obtain the associated class of one observation
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return byte [] Array indicating the class of the observation
	 *                 For example, {0,0,1} indicates that observation
	 *                 is of the third class, {0,1,0} indicates that 
	 *                 observation is of the second class
	 * </p>                
	 */
    
    public byte[] classify(double []inputs);
    
	/**
	 * <p>
	 * Obtain the associated class of a set of observations, through
	 * their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return byte[] Matrix indicating the class of the observation
	 *                For example, {0,0,1} indicates that observation
	 *                is of the third class, {0,1,0} indicates that 
	 *                observation is of the second class
	 * </p>               
	 */
    
    public byte[][] classify(double [][]inputs);
    
}

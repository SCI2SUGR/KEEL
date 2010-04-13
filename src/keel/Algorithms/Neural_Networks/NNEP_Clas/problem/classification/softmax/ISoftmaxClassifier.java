package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.softmax;



import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.IClassifier;

/**
 * <p>
 * @author Written Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface ISoftmaxClassifier extends IClassifier{
	
	/**
	 * <p>
	 * Generic softmax classifier.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Obtain the normalized softmax probabilities of classes of 
	 * one observation
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double [] Array indicating the probability of each class
	 *                   of the observation
	 * </p>                   
	 */
    
    public double[] softmaxProbabilities(double []inputs);
    
	/**
	 * <p>
	 * Obtain the normalized softmax probabilities of classes 
	 * of a set of observations, through their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Matrix indicating the probability of each class
	 *                   of the observation
	 * </p>                  
	 */
    
    public double[][] softmaxProbabilities(double [][]inputs); 
	
	/**
	 * <p>
	 * Obtain the raw output of the classifier for each class
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double [] Array with the outputs of each class
	 *                   of the observation
	 * </p>                  
	 */
    
    public double[] rawOutputs(double []inputs);
    
	/**
	 * <p>
	 * Obtain the raw outputs of classes of a set of observations,
	 * through their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Matrix indicating the output of each class
	 *                   of the observation
	 * <p>                  
	 */
    
    public double[][] rawOutputs(double [][]inputs); 
    
}

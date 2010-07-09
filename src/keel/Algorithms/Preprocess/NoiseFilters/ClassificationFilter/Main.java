/**
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.ClassificationFilter;


/**
 * <p>
 * Main class of the algorithm
 * </p>
 */
public class Main {

	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It creates a new instance of Main 
	 * </p>
	 */
	public Main(){
	}
	
//*******************************************************************************************************************************
		
	/**
	 * <p>
	 * Main method
	 * </p>
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		
		Parameters.doParse(args[0]);
		
		ClassificationFilter method = new ClassificationFilter();
		method.run();		
	}

}
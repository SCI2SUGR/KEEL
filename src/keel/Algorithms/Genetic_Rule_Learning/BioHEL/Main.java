/**
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 22/04/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;


/**
 * <p>
 * Main class of BioHEL algorithm
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
	 * @param args the command line arguments with the configuration file
	 */
	public static void main(String[] args){
		
		Parameters.doParse(args[0]);
		BioHEL method = new BioHEL();
		method.run();        
	}

}
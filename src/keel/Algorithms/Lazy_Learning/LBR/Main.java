/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the classifier and executes it.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.LBR;

public class Main {

	//The classifier
	private static LBR classifier;
	
	/** 
	 * <p>
	 * The main method of the class
	 * </p> 
	 * 
	 * @param args Arguments of the program (a configuration script, generally)  
	 * 
	 */
	public static void main (String args[]) {
		
		if (args.length != 1){
			
			System.err.println("Error. A parameter is only needed.");
			
		} else {
			
			classifier = new LBR(args[0]);
			classifier.execute();
			
		}
		
	} //end-method 
  
} //end-class

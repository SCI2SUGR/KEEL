/**
 *
 * File: Main.java
 *
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the classifier and executes it.
 *
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @version 1.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.Hyperrectangles.BNGE;

public class Main {
	
	//The classifier
	private static BNGE classifier;
	
	/**
	 * The main method of the class
	 *
	 * @param args Arguments of the program (a configuration script, generally)
	 *
	 */
	public static void main (String args[]) {
		
		if (args.length != 1){
			
			System.err.println("Error. A parameter is only needed.");
			
		} else {
			
			classifier = new BNGE(args[0]);
			classifier.getRules();
			classifier.execute();
			
		}
		
	} //end-method 
  
} //end-class


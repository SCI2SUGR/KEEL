/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the classifier and executes it.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 16/11/2008 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.IDIBL;

public class Main {

	//The classifier
	private static IDIBL classifier;
	
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
			
			  classifier = new IDIBL(args[0]);
			  classifier.findProbabilities();
			  classifier.findNeigbours();
			  classifier.findParameters(classifier.getFirstTimeLimit());
			  classifier.pruneInstanceSet();
			  classifier.reTuneParameters(classifier.getSecondTimeLimit());
		      classifier.executeReference();

		}
		
	} //end-method 
  
} //end-class


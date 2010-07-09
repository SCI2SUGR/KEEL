package keel.Algorithms.Lazy_Learning.LazyDT;

/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the classifier and executes it.
 * 
 * @author Written by Victoria López Morales (University of Granada) 20/8/2009 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

public class Main {
	
	//The classifier
	private static LazyDT classifier;
	
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
			classifier = new LazyDT(args[0]);
			classifier.precompute();
			classifier.execute();
		}
	} //end-method 
  
} //end-class

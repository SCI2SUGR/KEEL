/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the algorithm and executes it.
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Instance_Selection.GGA;

public class Main {
	/** 
	 * <p>
	 * The main method of the class
	 * </p> 
	 * 
	 * @param args Arguments of the program (a configuration script, generally)  
	 * 
	 */
  public static void main (String args[]) {

    GGA gga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      gga = new GGA (args[0]);
      gga.ejecutar();
    }
	
  } //end-method 
  
}//end-class

/**
 * 
 * File: Main.java
 * 
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the algorithm and executes it.
 * 
 * @author Written by Salvador Garc�a (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Instance_Selection.SGA;

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

    SGA sga;

    if (args.length != 1)
      System.err.println("Error. A parameter is only needed.");
    else {
      sga = new SGA (args[0]);
      sga.ejecutar();
    }
	
  } //end-method 
  
}//end-class
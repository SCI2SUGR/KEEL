/**
 * <p>
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;


 
 
public class UCSControl {
/**
 * <br>
 * UCSControl
 * <br>
 * This is the main class of the package. 
 * It only contains the main procedure, which declares a new UCS object and
 * trains the system
 */
	
 public void UCSControl (){}

/**
 * It is the main procedure. 
 * A new UCS object is declared and the run method is called.
 */
  public static void main (String args[]){
    long iTime = System.currentTimeMillis();
    if (args.length == 1){
			System.out.println ("Creating UCS object");
            UCS ucs= new UCS (args[0]);
			System.out.println ("Running UCS");
            ucs.run();
    }
    else{
            System.out.println ("You have to pass the configuration file.");
    }
  }//end Main
     
} // END OF CLASS UCSControl
                                       

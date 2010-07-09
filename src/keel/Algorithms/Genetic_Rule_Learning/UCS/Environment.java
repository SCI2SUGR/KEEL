/**
 * <p>
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.io.*;
import java.util.*;



public interface Environment {
/**
 * <p>
 * This interface is the environment interface. It has to be implemented for
 * all the environment problems. 
 * <p>
 */

/**
 * The function returns the current state.
 * 
 * @return a double[] with the current state.
 */
    public double[] getCurrentState();

/**
 * The function returns the current class
 *
 * @return an integer with the current class
 */
	public int getCurrentClass ();

/**
 * Creates or selects a new example of the problem. 
 *
 * @return a double[] with the new example.
 */
    public double[] newState();

    
/**
 * It initializes the first example. It is used in the file 
 * environment to get the examples sequentially.
 */
    public void beginSequentialExamples();
    
/**
 * It returns the new Example of a single step file environment.
 *	
 * @return a double[] with the next example
 */ 
    public double[] getSequentialState();
    
/**
 * It returns the number of examples of the database. It is only
 * used in the file environments. 
 * 
 * @return an integer with the number of examples in the DB
 */    
    public int getNumberOfExamples();
    
	
} // END OF CLASS Environment






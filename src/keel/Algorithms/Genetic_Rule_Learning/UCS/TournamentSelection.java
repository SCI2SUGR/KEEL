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


public class TournamentSelection implements Selection {
/**
 * <p>
 * This class implements the tournament selection method.
 * </p>
 */

/**
 * Creates a TournamentSelection object
 */
    public  TournamentSelection() {        
    } // end TournamentSelection        



/**
 * Initializes the tournament selection. It does nothing,
 * but it is required as this class implements Selection
 * 
 * @param pop is the population.
 */
  public void init( Population correctSet ) {        
  } // end init



/**
 * Applies the tournament selection.
 *
 * @param pop is the population.
 * @return a Classifier with the selected classifier
 */
  public Classifier makeSelection( Population correctSet ){        
	return correctSet.tournamentSelection ();
  } // end makeSelection        

} // END OF CLASS TournamentSelection




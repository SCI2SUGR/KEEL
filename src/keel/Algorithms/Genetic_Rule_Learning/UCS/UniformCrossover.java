/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

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

public class UniformCrossover implements Crossover{

  ///////////////////////////////////////
  // operations


/**
 * Creates an object of the class. It does not have to initialize anything.
 */
  public  UniformCrossover() {        
  } // end UniformCrossover        

/**
 * Applies crossover according to the uniform crossover operator. It obtains two children.
 * 
 * @param parent1 is the first parent.
 * @param parent2 is the second parent
 * @param child1 is the first child
 * @param child2 is the second child.
 */
  public void makeCrossover(Classifier parent1, Classifier parent2, Classifier child1, Classifier child2) {        
    double aleat = 0;
    
    for (int i=0; i<Config.clLength; i++){
        aleat = Config.rand();
        if (aleat > 0.5){
            if ( Config.ternaryRep ){
                child1.crossAllele(i,parent1,parent2);
                child2.crossAllele(i,parent2,parent1);
            }
            else{
                child1.setAllele(i,parent2);
                child2.setAllele(i,parent1);
            }
        }
    }	
  } // end makeCrossover        

} // end UniformCrossover





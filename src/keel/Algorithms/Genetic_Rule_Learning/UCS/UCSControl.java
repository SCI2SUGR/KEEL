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
                                       


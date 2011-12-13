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

package keel.Algorithms.Decision_Trees.FunctionalTrees;

/**
 * This is the main class of the algorithm.
 * It gets the configuration script, builds the decision tree model, and
 * classifies with it.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 14/05/2009 
 * @version 0.1 
 * @since JDK1.5
 */
public class Main {
    
    /**
     * The classifier we are going to use
     */
    private static FunctionalTrees decisionTree;
    
    /** 
     * The main method of the class
     * 
     * @param args Arguments of the program (a configuration script, generally)  
     */
    public static void main (String args[]) {       
        if (args.length != 1){
            System.err.println("Error: You have to specify the parameters file");
        } else {
            decisionTree = new FunctionalTrees(args[0]);
            decisionTree.execute();
        }
    } //end-method 
  
} //end-class



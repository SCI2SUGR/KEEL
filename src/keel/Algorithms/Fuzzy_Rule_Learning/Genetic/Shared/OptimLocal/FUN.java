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
 * @author Written by Luciano Sánchez (University of Oviedo) 03/03/2004
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

// Paquete Optimizacion Local
// Local optimization package
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;

abstract public class FUN {
	/**
	 * <p>
	 * Class for duplicate the initial ana used rles and fro filter the used ones
	 * Need: The initial set of rules and the used ones
	 *</p>
	 */
    // Virtual class with a double [] to double function 
    protected boolean []used;
    protected double []initial;
    
    /**
     * <p>
     * Constructor
     * </p>
     * @param vused Mark the used rules
     * @param vinitial A set of initial rules
     */
    FUN(boolean []vused, double[]vinitial) {
        used=duplicate(vused);
        initial=duplicate(vinitial);
    }
    
    /**
     * <p>
     * The protected method duplicate a set of boolean values
     * </p>
     * @param x The set of boolean values
     * @return The duplicate set
     */
    protected boolean[] duplicate(boolean[] x) {
        boolean []result = new boolean[x.length];
        for (int i=0;i<x.length;i++) result[i]=x[i];
        return result;
    }
    
    /**
     * <p>
     * The protected method duplicate a set of double values
     * </p>
     * @param x The set of double values
     * @return The duplicate set
     */
    protected double[] duplicate(double[] x) {
        double []result = new double[x.length];
        for (int i=0;i<x.length;i++) result[i]=x[i];
        return result;
    }
    
    /**
     * <p>
     * The protected method filter the rules to select the used one
     * </p>
     * @param x The rules
     * @return The used rules
     */
    protected double[] filter(double []x) {
        double result[] = duplicate(initial);
        for (int i=0;i<x.length;i++) 
            if (used[i]) result[i]=x[i];
        return result;
    }
    
    
    /**
     * <p>
     * The abstract method evaluate the fitness of the rule set
     * </p>
     * @param x The set of rules
     * @return The result of evaluate the fitness
     */
    abstract public double evaluate(double x[]);
}




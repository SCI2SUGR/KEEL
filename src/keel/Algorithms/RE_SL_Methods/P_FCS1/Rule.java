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

package keel.Algorithms.RE_SL_Methods.P_FCS1;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Rule {
/**	
 * <p>
 * It contains the definition for a fuzzy rule
 * </p>
 */
 
    Fuzzy [] memfunctions;

    /**
     * <p>       
     * Creates a rule containing "tam" gaussian fuzzy sets
     * </p>       
     * @param tam int The number of gaussian fuzzy sets in the rule
     */
    public Rule(int tam) {
        memfunctions = new Fuzzy[tam];
        for(int i = 0; i < tam; i++){
            memfunctions[i] = new Fuzzy();
        }
    }


    /**
     * <p>       
     * Creates a fuzzy rule as a copy of another fuzzy rule
     * </p>       
     * @param reg Rule The fuzzy rule used to create the new fuzzy rule
     */
    public Rule(Rule reg) {
        int tam = reg.memfunctions.length;
        memfunctions = new Fuzzy[tam];
        for(int i = 0; i < tam; i++){
            memfunctions[i] = new Fuzzy(reg.memfunctions[i]);
        }
    }

}


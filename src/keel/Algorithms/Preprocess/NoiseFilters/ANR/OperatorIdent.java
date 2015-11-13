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



package keel.Algorithms.Preprocess.NoiseFilters.ANR;

/** 
* <p> Class with constant identifiers of operators and methods.

* @author Written by Luciano Sánchez (University of Oviedo) 26/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/
public class OperatorIdent {

     // Identifiers for crossover and mutation operators  
    
    /**
     * Indentifier for generic crossover.
     */
	public static final int GENERICROSSOVER = 1;

    /**
     * Indentifier for generic mutation.
     */
    public static final int GENERICMUTATION = 2;

    /**
     * Indentifier for GAPCROSSGA crossover.
     */
    public static final int GAPCROSSGA = 1001;

    /**
     * Indentifier for GAPCROSSGP crossover.
     */
    public static final int GAPCROSSGP = 1002;

    /**
     * Indentifier for GAPMUTAGA mutation.
     */
    public static final int GAPMUTAGA = 1003;

    /**
     * Indentifier for GAPMUTAGP mutation.
     */
    public static final int GAPMUTAGP = 1004;
	
     //Identifiers for local optimizations	

    /**
     * Identifier for local optimization (AMEBA).
     */
    	public static final int AMEBA = 2001;
	
    /**
     * Identifier for local optimization (GI_STANDARD).
     */
    public static final int GI_STANDARD = 0;

    /**
     * Identifier for local optimization (GI_CUSTOM_CESAR).
     */
    public static final int GI_CUSTOM_CESAR = 1;


}

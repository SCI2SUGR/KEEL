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
* @author Written by Luciano Sánchez (University of Oviedo) 26/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Parsing;


public class OperatorIdent {
	/**
	 * <p>
	 * Class with constant identifiers of operators and methods.
	 * </p>
	 */

     // Identifiers for crossover and mutation operators  
	public static final int GENERICROSSOVER = 1;
	public static final int GENERICMUTATION = 2;
	public static final int GAPCROSSGA = 1001;
    public static final int GAPCROSSGP = 1002;
	public static final int GAPMUTAGA = 1003;
    public static final int GAPMUTAGP = 1004;
	
     //Identifiers for local optimizations	
	public static final int AMEBA = 2001;
	
	public static final int GI_STANDARD = 0;
	public static final int GI_CUSTOM_CESAR = 1;


}


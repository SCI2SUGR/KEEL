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
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;

import java.util.*;
import java.lang.*;
import java.io.*;


public interface Attribute{
/**
 * <p>
 * This interface has to be implemented for all attributes representation. By now, there are 3 implementations. Each
 * one is a diferent attribute representation:
 *	 - Real Representation
 *	 - Integer Representation
 *	 - Ternary Representation
 * </p>
 */
	
	/** In case of being  a character attribute, the char value is set as a double in the lowerValue parameter) */
	public void setAllele(double lowerValue, double UpperValue);
	public void setAllele(Attribute at);
	
	

	/** It returns the allele as a double */
	public double getAllele();
	public Attribute getAttributeAllele();
	
	/** It returns the lower allele. If is a ternary representation, it returns the character of the position as
	a double*/
	public double getLowerAllele();
	
	/** It returns the upper allele. If is a ternary representation, it returns the character of the position as
	a double*/
	public double getUpperAllele();

	
	/** Returns the generality of the attribute. In ternary representation a 1 is returned if the symbol is a
	don't care, and in other representations the diference of the intervals is returned */	
	public double getGenerality ();

	/** Does verify that the interval construction is correct*/
	public void verifyInterval();
	
	/** It applies the specify operator. The environmental value is needed*/
	public void makeSpecify(double env);
	
	
	/** The mutation is done from the environmental state. The position i is needed for integer representation to
	know the bounds of the intervals*/
	public void mutate(double envState);
	

	/** It checks if the position of the classifier matches with the value in the environment*/
	public boolean match(double env);

	/** It checks if two attributes are equal*/
	public boolean equals(Attribute at);
	
	/** It checks if that attribute of the representation of the classifier can subsume the attribute passed as a
	parameter*/
	public boolean subsumes(Attribute at);

	/** If it's a ternary representation it returns 1 if is # and 0 otherwise. In representations with intervals
	it returns the diference between them*/
	public double isDontCareSymbol();
	
	/** Returns if the current interval is more general than the interval given as a parameter */
	public boolean isMoreGeneral(Attribute at);
	


	/** Prints the classifier representation */
	public void print();
	public void print(PrintWriter fout);
	
	public void printNotNorm(PrintWriter fout, Vector conv);
	public void printNotNorm(PrintWriter fout, int lo);
	public void printNotNorm(PrintWriter fout, double lo, double up);
	
	
	

	//Prints the character or interval as following:
	// System.out.print ("; "+mixedRep[i].getCharAllele());
	// System.out.print ("; "+mixedRep[i].getLowerAllele()+", "+mixedRep[i].getUpperAllele());
	

}


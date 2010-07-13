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
* @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public abstract class Fuzzy {
/** 
* <p> 
* It is the abstract class for the remaining basic classes related with Fuzzy Logic.. 
* 
* 
* Detailed in:
* 
* Zadeh, L. Fuzzy logic, IEEE Computer, 1:83, (1988)
* 
* </p> 
*/
	// Positive limit for the support set of a fuzzy set
	final double POSITIVEINF=1.0e10;
	// Negative limit for the support set of a fuzzy set
    final double NEGATIVEINF=-1.0e10;
    /** 
     * <p> 
     * A constructor by default. 
     * 
     * </p> 
     */
    public Fuzzy() {};
    /** 
     * <p> 
     *  Returns the membership level for the individual x.
     * 
     * </p>
     * @param x the individual which membership is to be calculated. 
     * @return the membership level for individual x. 
     */ 
    public abstract double evaluateMembership(double x);
    /** 
     * <p> 
     *  Returns the centroid of the present fuzzy number. 	
     *  
     * </p>
     * @return the centroid of the present fuzzy number. 
     */  
    public abstract double massCentre();
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */        
    public abstract Fuzzy clone();
    /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of the instance. 
     */	
    public abstract String aString();
    /** 
     * <p> 
     *  Creates and returns a FuzzyInterval with unique point of the support set.
     *  
     * </p> 
     * @return an interval with the unique point of the support set. 
     */ 
    public abstract FuzzyInterval support();
    /** 
     * <p> 
     *  Indicates whether some other object is "equal to" this one.
     * 
     * </p>
     * @param B the reference object with which to compare. 
     * @return true if this object is the same as the B argument; false otherwise. 
     */     
    public abstract boolean equals(Fuzzy b);
};


















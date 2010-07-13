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
* @author Written by Luciano Sánchez (University of Oviedo) 25/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public class FuzzyInterval extends Fuzzy {
	/** 
	* <p> 
	* Represents an interval fuzzy set. Each member of a interval fuzzy set has membership value of 1.0.
	* 
	* </p>
	* 
	*/
	//The extremes of the interval fuzzy set.
    double a,b;
    /** 
     * <p> 
     * Returns the lower extreme of the interval fuzzy set. 
     * 
     * </p> 
     * @return the lower extreme of the interval fuzzy set.
     */
    public double a() { return a; }
    /** 
     * <p> 
     * Returns the upper extreme of the interval fuzzy set. 
     * 
     * </p> 
     * @return the upper extreme of the interval fuzzy set.
     */
    public double b() { return b; } 
    /** 
     * <p> 
     * A constructor for an interval fuzzy set, given the extremes. 
     * 
     * </p> 
     * @param pa the lower extreme of the interval fuzzy set.
     * @param pb the upper extreme of the interval fuzzy set.
     */
    public FuzzyInterval(double pa, double pb) {
        a=pa; b=pb;
    }
    /** 
     * <p> 
     * A copy constructor for an interval fuzzy set, given other interval fuzzy set. 
     * 
     * </p> 
     * @param pa to be copied.    
     */
    public FuzzyInterval(FuzzyInterval pa) {
        a=pa.a; b=pa.b;
    }
    /** 
     * <p> 
     *  Indicates whether some other object is "equal to" this one.
     * 
     * </p>
     * @param B the reference object with which to compare. 
     * @return true if this object is the same as the B argument; false otherwise. 
     */     
    public boolean equals(Fuzzy B) {
        if (!(B instanceof FuzzyInterval)) return false;
        FuzzyInterval bt=(FuzzyInterval) B;
        if (a!=bt.a) return false;
        if (b!=bt.b) return false;
        return true;
    }
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */ 
    public Fuzzy clone() {
        return new FuzzyInterval(this);
    }
    /** 
     * <p> 
     * Copies the FuzzyInterval parameter over the present instance. 
     * 
     * </p> 
     * @param pa a FuzzyInterval object to be copied
     */ 
    public void Asigna(FuzzyInterval pa) {
         a=pa.a; b=pa.b;
    }
    /** 
     * <p> 
     *  Returns the membership level for the individual x.
     *  
     * 
     * </p>
     * @param x the individual which membership is to be calculated. 
     * @return the membership level for individual x. 
     */ 
    public double evaluateMembership(double x) {
        if (x>=a && x<=b) return 1;
        return 0;
    }
    /** 
     * <p> 
     *  Returns the centroid of the present interval fuzzy set. 	
     *  
     * </p>
     * @return the centroid of the present interval fuzzy set. 
     */  
    public double massCentre() {
        return (a+b)/2;
    }
    /** 
     * <p> 
     *  Creates and returns a FuzzyInterval with the extremes of the support set.
     *  
     * </p> 
     * @return an interval with the extremes of the support set. 
     */ 
    public FuzzyInterval support() {
        return new FuzzyInterval(a,b);
    }
    /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of the interval fuzzy set. 
     */	
    public String aString() {
        return "INTERVAL ["+a+", "+b+"]";
    }
    
}


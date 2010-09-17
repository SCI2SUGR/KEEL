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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyAprioriDC;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class MembershipFunction implements Comparable {
	/**
	 * <p>
	 * It depicts a Membership Function represented by a 2-tuple and indicating the center and the spread of an isosceles-triangle
	 * </p>
	 */
	
	private double c;
	private double w;
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public MembershipFunction() {
	}
	
	/**
	 * <p>
	 * It returns the center of an isosceles-triangle
	 * </p>
	 * @return A value representing the center of the isosceles-triangle
	 */
	public double getC() {
		return this.c;
	}
	
	/**
	 * <p>
	 * It sets the center of an isosceles-triangle
	 * </p>
	 * @param c A value representing the center of the isosceles-triangle
	 */
	public void setC(double c) {
		this.c = c;
	}
	
	/**
	 * <p>
	 * It returns the spread of an isosceles-triangle
	 * </p>
	 * @return A value representing the spread of the isosceles-triangle
	 */
	public double getW() {
		return this.w;
	}
	
	/**
	 * <p>
	 * It sets the spread of an isosceles-triangle
	 * </p>
	 * @param w A value representing the spread of the isosceles-triangle
	 */
	public void setW(double w) {
		this.w = w;
	}
	
	/**
	 * It computes the overlap length of a membership functions with respect to another one
	 * @param membership_function The membership function with which to compute the overlap length
	 * @return A value indicating the overlap length
	 */
	public double calculateOverlapLength(MembershipFunction membership_function) {
		double x3_mf1, x0_mf2;
		
		x3_mf1 = this.c + this.w;
		x0_mf2 = membership_function.c - membership_function.w;
		
		return (x3_mf1 - x0_mf2);
	}
	
	/**
	 * <p>
	 * It allows to clone correctly a membership function
	 * </p>
	 * @return A copy of the membership function
	 */
	public MembershipFunction clone() {
		MembershipFunction mf = new MembershipFunction();
		
		mf.c = this.c;
		mf.w = this.w;
		
		return mf;
	}
	
	/**
	 * <p>
	 * It compares a membership function with another one in order to accomplish ordering (ascending) later.
	 * The comparison is achieved by considering the values of the centers.
	 * For this reason, note that this method provides a natural ordering that is inconsistent with equals
	 * </p>
	 * @param obj The object to be compared
	 * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
	 */
	public int compareTo(Object obj) {
		double aux;
		MembershipFunction mf = (MembershipFunction)obj;
		
		if (this.c > mf.c) {
			aux = this.w;
			this.w = mf.w;
			mf.w = aux;
			
			return 1;
		}
		else if (this.c < mf.c) return -1;
		
		return 0;
	}
	
	/**
	 * <p>
	 * It returns a raw string representation of a membership function
	 * </p>
	 * @return A raw string representation of the membership function
	 */
	public String toString() {
		return ( "(c: " + this.c + ", w: " + this.w + ")" );
	}
	
}
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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyApriori;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class FuzzyRegion {
	/**
	 * <p>
	 * It depicts a Triangle Membership Function whose values are linguistic labels to be used within a fuzzy set
	 * </p>
	 */
	
	private double x0;
	private double x1;
	private double x3;
	private double y;
	private String label;
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public FuzzyRegion() {
	}
	
	/**
	 * <p>
	 * It returns the membership degree of an X value within a fuzzy region
	 * </p>
	 * @param x A value X whose membership degree has to be proved
	 * @return A membership degree of the X value within the fuzzy region
	 */
	public double getFuzzyValue(double x) {
		if (x == this.x1) return 1.0;
		
		if ( (x <= this.x0) || (x >= this.x3) ) return 0.0;
		
		if (x < this.x1) return ( (x - this.x0) * (this.y / (this.x1 - this.x0)) );
		
		if (x > this.x1) return ( (this.x3 - x) * (this.y / (this.x3 - this.x1)) );
		
		return this.y;
    }
	
	/**
	 * <p>
	 * It returns the X0 value of a fuzzy region
	 * </p>
	 * @return A value representing the X0 value of the fuzzy region
	 */
	public double getX0() {
		return this.x0;
	}

	/**
	 * <p>
	 * It sets the X0 value of a fuzzy region
	 * </p>
	 * @param x0 A value representing the X0 value of the fuzzy region
	 */
	public void setX0(double x0) {
		this.x0 = x0;
	}

	/**
	 * <p>
	 * It returns the X1 value of a fuzzy region
	 * </p>
	 * @return A value representing the X1 value of the fuzzy region
	 */
	public double getX1() {
		return this.x1;
	}

	/**
	 * <p>
	 * It sets the X1 value of a fuzzy region
	 * </p>
	 * @param x1 A value representing the X1 value of the fuzzy region
	 */
	public void setX1(double x1) {
		this.x1 = x1;
	}

	/**
	 * <p>
	 * It returns the X3 value of a fuzzy region
	 * </p>
	 * @return A value representing the X3 value of the fuzzy region
	 */
	public double getX3() {
		return this.x3;
	}

	/**
	 * <p>
	 * It sets the X3 value of a fuzzy region
	 * </p>
	 * @param x3 A value representing the X3 value of the fuzzy region
	 */
	public void setX3(double x3) {
		this.x3 = x3;
	}

	/**
	 * <p>
	 * It returns the Y value of a fuzzy region
	 * </p>
	 * @return A value representing the maximum value returned by the fuzzy region
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * <p>
	 * It sets the Y value of a fuzzy region
	 * </p>
	 * @param y A value representing the maximum value returned by the fuzzy region
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * <p>
	 * It returns the label associated with a fuzzy region
	 * </p>
	 * @return A string representing the label associated with the fuzzy region
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * <p>
	 * It sets the label associated with a fuzzy region
	 * </p>
	 * @param label A string representing the label associated with the fuzzy region
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * <p>
	 * It allows to clone correctly a fuzzy region
	 * </p>
	 * @return A copy of the fuzzy region
	 */
	public FuzzyRegion clone() {
		FuzzyRegion fr = new FuzzyRegion();
		
		fr.x0 = this.x0;
		fr.x1 = this.x1;
		fr.x3 = this.x3;
		fr.y = this.y;
		fr.label = this.label;
		
		return fr;
	}
	
	/**
	 * <p>
	 * It returns a raw string representation of a fuzzy region
	 * </p>
	 * @return A raw string representation of the fuzzy region
	 */
	public String toString() {
		return ( this.label + " (x0: " + this.x0 + ", x1: " + this.x1 + ", x3: " + this.x3 + ")" );
	}
	
}
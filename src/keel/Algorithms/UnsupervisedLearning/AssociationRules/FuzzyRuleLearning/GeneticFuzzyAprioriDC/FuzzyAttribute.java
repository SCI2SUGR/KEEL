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

public class FuzzyAttribute {
	/**
	 * <p>
	 * It represents a fuzzy attribute, that is a fuzzy set containing its fuzzy regions
	 * </p>
	 */
	
	private int idAttr;
	private FuzzyRegion[] fuzzyRegions;
	
	/**
	 * <p>
	 * It creates a new fuzzy attribute by setting up its properties
	 * </p>
	 * @param idAttr The value representing the ID of the attribute being considered
	 * @param fuzzyRegions The fuzzy regions that compose the fuzzy attribute
	 */
	public FuzzyAttribute(int idAttr, FuzzyRegion[] fuzzyRegions) {
		this.idAttr = idAttr;
		this.fuzzyRegions = fuzzyRegions;
	}
	
	/**
	 * It returns the fuzzy regions composing the fuzzy attribute
	 * @return An array of fuzzy regions composing the fuzzy attribute
	 */
	public FuzzyRegion[] getFuzzyRegions() {
		return this.fuzzyRegions;
	}
	
	/**
	 * It returns the ID of the attribute being considered
	 * @return A value representing the ID of the attribute being considered
	 */
	public int getIdAttr() {
		return this.idAttr;
	}
	
	/**
	 * It returns the number of fuzzy regions composing a fuzzy attribute
	 * @return A value indicating the number of fuzzy regions composing the fuzzy attribute
	 */
	public int getNumberOfFuzzyRegions() {
		return this.fuzzyRegions.length;
	}
	
	/**
	 * <p>
	 * It returns a raw string representation of a fuzzy attribute
	 * </p>
	 * @return A raw string representation of the fuzzy attribute
	 */
	public String toString() {
		int i;
		String str = "";
		
		for (i=0; i < this.fuzzyRegions.length - 1; i++) {
			str += this.fuzzyRegions[i] + "\n";
		}
		
		str += this.fuzzyRegions[i];
		
		return str;
	}
	
}
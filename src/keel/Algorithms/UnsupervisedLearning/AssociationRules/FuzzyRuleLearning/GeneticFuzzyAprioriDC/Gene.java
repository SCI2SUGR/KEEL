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

import java.util.Arrays;

public class Gene {
	/**
	 * <p>
	 * It is used for representing and handling a gene throughout the evolutionary learning
	 * </p>
	 */

	private MembershipFunction[] membershipFunctions;
	
	/**
	 * <p>
	 * It creates a new gene by setting up its membership functions
	 * </p>
	 * @param membership_functions The membership functions to set up for the gene
	 */
	public Gene(MembershipFunction[] membership_functions) {
		this.setMembershipFunctions(membership_functions);
	}
		
	private void setMembershipFunctions(MembershipFunction[] membership_functions) {
		this.membershipFunctions = new MembershipFunction[ membership_functions.length ];
		
		for (int i=0; i < this.membershipFunctions.length; i++)
			this.membershipFunctions[i] = membership_functions[i].clone();
	}
	
	/**
	 * <p>
	 * It returns the membership functions of a gene
	 * </p>
	 * @return An array of membership functions for the gene being considered
	 */
	public MembershipFunction[] getMembershipFunctions() {
		return this.membershipFunctions;
	}
	
	/**
	 * It computes the overlap factor for the membership functions involved in a gene
	 * @return A value indicating the overlap factor
	 */
	public double calculateOverlapFactor() {
		int i, j;
		double min_w, overlap_factor;
		
		overlap_factor = 0.0;
		
		for (i=0; i < this.membershipFunctions.length; i++) {
			for (j=i+1; j < this.membershipFunctions.length; j++) {
				min_w = Math.min(this.membershipFunctions[i].getW(), this.membershipFunctions[j].getW());
				overlap_factor += ( Math.max( (this.membershipFunctions[i].calculateOverlapLength(this.membershipFunctions[j]) / min_w), 1.0) - 1.0);
			}
		}
		
		return overlap_factor;
	}
	
	/**
	 * It computes the coverage factor for the membership functions involved in a gene
	 * @return A value indicating the coverage factor
	 * @param min_attr The minimum domain value depending on the attribute being considered
	 * @param max_attr The maximum domain value depending on the attribute being considered
	 */
	public double calculateCoverageFactor(double min_attr, double max_attr) {
		double x0, x3, min_x0, max_x3, range;
		int id_region, k, id_min_region;
		boolean change, stop;
		
		min_x0 = this.membershipFunctions[0].getC() - this.membershipFunctions[0].getW();
		max_x3 = this.membershipFunctions[0].getC() + this.membershipFunctions[0].getW();
		id_min_region = 0;
		
		for (id_region=1; id_region < this.membershipFunctions.length; id_region++) {
			x0 = this.membershipFunctions[id_region].getC() - this.membershipFunctions[id_region].getW();
			x3 = this.membershipFunctions[id_region].getC() + this.membershipFunctions[id_region].getW();
			
			if (x0 < min_x0) {
				min_x0 = x0;
				id_min_region = id_region;
			}
			
			if (x3 > max_x3) {
				max_x3 = x3;
			}
		}
		
		range = 0.0;
		
		if (min_x0 < min_attr) range -= (min_attr - min_x0);
	    if (max_x3 > max_attr) range -= (max_x3 - max_attr);
		
		while (id_min_region < this.membershipFunctions.length) {
			min_x0 = this.membershipFunctions[id_min_region].getC() - this.membershipFunctions[id_min_region].getW();
			max_x3 = this.membershipFunctions[id_min_region].getC() + this.membershipFunctions[id_min_region].getW();
			
			change = false;
			
			for (k=0; k< this.membershipFunctions.length; k++) {
				x0 = this.membershipFunctions[k].getC() - this.membershipFunctions[k].getW();
				x3 = this.membershipFunctions[k].getC() + this.membershipFunctions[k].getW();
				
				if ( (min_x0 < x0) && (x0 < max_x3) && (x3 > max_x3) ) {
					max_x3 = x0;
					id_min_region = k;
					change = true;
				}
			}
			
			range += (max_x3 - min_x0);
			
			if (! change) {
				stop = false;
				id_min_region++;
				
			    while ( (! stop) && (id_min_region < this.membershipFunctions.length) ) {
			    	x0 = this.membershipFunctions[id_min_region].getC() - this.membershipFunctions[id_min_region].getW();
					
			    	if (x0 < max_x3) id_min_region++;
					else stop = true;
				 }
			}
		}
		
		return ( 1.0 / (range / (max_attr - min_attr)) );
	}
	
	/**
	 * <p>
	 * It orders the membership functions involved in a gene
	 * </p>
	 */
	public void sortMembershipFunctions() {
		Arrays.sort(this.membershipFunctions);
	}
	
	/**
	 * <p>
	 * It allows to clone correctly a gene
	 * </p>
	 * @return A copy of the gene
	 */
	public Gene clone() {
		Gene gene = new Gene(this.membershipFunctions);
		
		return gene;
	}
	
	/**
	 * <p>
	 * It returns a raw string representation of a gene
	 * </p>
	 * @return A raw string representation of the gene
	 */
	public String toString() {
		int i;
		String str = "[";
		
		for (i=0; i < this.membershipFunctions.length - 1; i++) {
			str += this.membershipFunctions[i] + "; ";
		}
		
		str += this.membershipFunctions[i] + "]";
		
		return str;
	}
	
}
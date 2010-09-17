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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Alcalaetal;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class Gene {
	/**
	 * <p>
	 * It is used for representing and handling a gene throughout the evolutionary learning
	 * </p>
	 */

	private double[] displacements;
	
	/**
	 * <p>
	 * It creates a new gene by setting up its displacements
	 * </p>
	 * @param displacements The displacements values to set up for the gene
	 */
	public Gene(double[] displacements) {
		this.setDisplacements(displacements);
	}
		
	private void setDisplacements(double[] displacements) {
		this.displacements = new double[ displacements.length ];
		
		for (int i=0; i < this.displacements.length; i++)
			this.displacements[i] = displacements[i];
	}
	
	/**
	 * <p>
	 * It returns the displacements of a gene
	 * </p>
	 * @return An array of displacements values for the gene being considered
	 */
	public double[] getDisplacements() {
		return this.displacements;
	}
	
	/**
	 * It computes the overlap factor for the membership functions involved in a gene
	 * @return A value indicating the overlap factor
	 */
	public double calculateOverlapFactor(FuzzyAttribute uniform_fuzzy_attr) {
		int r;
		double x3_mf1, x0_mf2, x1_mf1, x0_mf1, overlap_factor;
		FuzzyRegion[] initial_fuzzy_regions;
		
		initial_fuzzy_regions = uniform_fuzzy_attr.getFuzzyRegions();
		overlap_factor = 0.0;
		
		for (r=0; r < (this.displacements.length-1); r++) {
			x3_mf1 = initial_fuzzy_regions[r].getX3() + this.displacements[r];
			x0_mf2 = initial_fuzzy_regions[r+1].getX0() + this.displacements[r+1];
			
			if (x3_mf1 > x0_mf2) {
				x0_mf1 = initial_fuzzy_regions[r].getX0() + this.displacements[r];
				x1_mf1 = initial_fuzzy_regions[r].getX1() + this.displacements[r];
				
				overlap_factor += ( Math.max((x3_mf1 - x0_mf2) / (x1_mf1 - x0_mf1), 1.0) - 1.0);
			}
		}
		
		return overlap_factor;
	}
	
	/**
	 * <p>
	 * It allows to clone correctly a gene
	 * </p>
	 * @return A copy of the gene
	 */
	public Gene clone() {
		Gene gene = new Gene(this.displacements);
		
		return gene;
	}
	
	/**
	 * <p>
	 * It indicates whether some other gene is "equal to" this one
	 * </p>
	 * @param obj The reference object with which to compare
	 * @return True if this gene is the same as the argument; False otherwise
	 */
	public boolean equals(Object obj) {
		Gene g = (Gene)obj;
		boolean ok = true;
		
		for (int i=0; i < this.displacements.length && ok; i++)
			if (g.displacements[i] != this.displacements[i]) ok = false;
		
		return ok;
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
		
		for (i=0; i < this.displacements.length - 1; i++) {
			str += this.displacements[i] + "; ";
		}
		
		str += this.displacements[i] + "]";
		
		return str;
	}
	
}
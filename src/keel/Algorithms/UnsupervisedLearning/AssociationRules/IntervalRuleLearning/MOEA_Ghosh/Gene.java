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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOEA_Ghosh;

import org.core.Randomize;

public class Gene {
	/**
	 * <p>
	 * It is used for representing and handling a Gene throughout the evolutionary learning
	 * </p>
	 */
	
	public static final int NOT_INVOLVED = 0;
	public static final int ANTECEDENT = 1;
	public static final int CONSEQUENT = 2;
	
	public static final int MINOR = 1;
	public static final int MAJOR = 2;
	
	public static final int EQUAL = 3;
	public static final int UNEQUAL = 4;

	private int attr;
	private int ac;
	private int operator;
	private double value;
	

	/**
	 * <p>
	 * It creates a new gene
	 * </p>
	 */
	public Gene() {
		
	}

	/**
	 * <p>
	 * It allows to clone correctly a gene
	 * </p>
	 * @return A copy of the gene
	 */
	public Gene copy() {
		Gene gene = new Gene();
		
		gene.attr = this.attr;
		gene.ac = this.ac;
		gene.operator = this.operator;
		gene.value = this.value;
		
		return gene;
	}
	
	/**
	 * <p>
	 * It returns whether a gene is involved in the chromosome being considered.
	 * In case it is involved, returns if it acts as antecedent or consequent
	 * </p>
	 * @return A constant value indicating the "role" played by the gene
	 */
	public int getActAs() {
		return this.ac;
	}

	/**
	 * <p>
	 * It sets whether a gene is involved in the chromosome being considered.
	 * In case it is involved, the user must specify if it acts as antecedent or consequent
	 * </p>
	 * @param ac The constant value indicating the "role" played by the gene
	 */
	public void setActAs(int ac) {
		this.ac = ac;
	}

	/*public boolean equals(Gene g) {
		if (g.attr == this.attr) {
			if (g.ac == this.ac) {
				if (g.operator == this.operator) {
					if (g.value == this.value) return true;
				}
			}
		}
		return false;
	}*/
	public boolean equals(Gene g) {
		if (g.attr == this.attr) {
			if (g.ac == this.ac) {
				if (g.operator == this.operator) {
					if (g.value == this.value){
						 return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * <p>
	 * It returns a string representation of a gene
	 * </p>
	 * @return A string representation of the gene
	 */  	
	public String toString() {
		return "Attr: " + attr + "AC: " + ac + "; Operator: " + operator + "; Value: " + value;
	}

	public int getAttr() {
		return this.attr;
	}

	public void setAttr(int var) {
		this.attr = var;
	}


    public int randAct () {
		return (Randomize.RandintClosed(Gene.NOT_INVOLVED, Gene.CONSEQUENT));
	}
    
    public int randOperatorNumeric () {
		return (Randomize.RandintClosed(Gene.MINOR, Gene.MAJOR));
	}
    
    public int randOperatorNominal () {
		return (Randomize.RandintClosed(Gene.EQUAL, Gene.UNEQUAL));
	}

    public boolean isCover (int var, double value) {
		boolean covered;

		if (this.attr != var)  return (false);

		covered = true;
				
		if  (this.operator == Gene.MAJOR) {
		  if (value < this.value)  covered = false;
	    }
		else {
			if  (this.operator == Gene.MINOR) {
				if (value > this.value)  covered = false;
			}
			else{
				if  (this.operator == Gene.EQUAL) {
					if (value != this.value)  covered = false;
				}
				else{
					if  (this.operator == Gene.UNEQUAL) {
						if (value == this.value)  covered = false;
					}
				}
			}
				
		}
		
		return (covered);
    }


	/**
	 * <p>
	 * It returns the operator used.
	 * In case it is numeric attribute, returns if it is mayor or minor than the value
	 * In case it is nominal attribute, returns if it equal or unequal respect to value
	 * </p>
	 * @return A constant value indicating the "operator" used by the gene
	 */
	public int getOperator() {
		return operator;
	}

	/**
	 * <p>
	 * It sets the type of operator used in a gene
	 * </p>
	 * @param operator The value indicating the type operator
	 */
	public void setOperator(int operator) {
		this.operator = operator;
	}
	
	/**
	 * <p>
	 * It returns the value stored in a gene
	 * </p>
	 * @return A value stored in a gene
	 */

	public double getValue() {
		return value;
	}

	/**
	 * <p>
	 * It sets the value stored in a gene
	 * </p>
	 * @param value stored in a gene
	 */
	public void setValue(double value) {
		this.value = value;
	}
}

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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MODENAR;

public class Gene {

    /**
     * Number to represent type of variable nominal.
     */
  public static final int NOMINAL = 0;

    /**
     * Number to represent type of variable integer.
     */
  public static final int INTEGER = 1;
  
      /**
     * Number to represent type of variable real or double.
     */
  public static final int REAL = 2;

	public static final int NOT_INVOLVED = 2;
	public static final int ANTECEDENT = 0;
	public static final int CONSEQUENT = 1;


	private int attr;
	private int type;
	private double l;
	private double u;
	private int ca; //represents is the gene is 0:antec, 1: consq, 2: no involve in gene


	public Gene() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Gene copy() {
		Gene gen = new Gene();

		gen.attr = this.attr;
		gen.type = this.type;
		gen.l = this.l;
		gen.u = this.u;
		gen.ca= this.ca;

		return gen;
	}

	public int getAttr() {
		return attr;
	}
	public void setAttr(int attr) {
		this.attr = attr;
	}
	public int getCa() {
		return ca;
	}
	public void setCa(int ca) {
		this.ca = ca;
	}
	public double getL() {
		return l;
	}
	public void setL(double l) {
		this.l = l;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getU() {
		return u;
	}
	public void setU(double u) {
		this.u = u;
	}
	public String toString() {
		return "A/C" + ca + "; A: " + attr + "; T: " + type + "; L: " + l + "; U: " + u;
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
		if((g.ca == 2)&&(this.ca == 2))return true;
		if (g.ca == this.ca) {
			if (g.l == this.l) {
				if (g.u == this.u) return true;
			}
		}
		return false;
	}

}
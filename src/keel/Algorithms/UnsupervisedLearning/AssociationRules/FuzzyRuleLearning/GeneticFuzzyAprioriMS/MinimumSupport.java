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


package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.GeneticFuzzyAprioriMS;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class MinimumSupport {
       private double ms;

       /**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public MinimumSupport() {
	}

	/**
	 * <p>
	 * It the minimum support value
	 * </p>
	 * @return ms A value representing the minimum support
	 */
	public double getMS() {
		return this.ms;
	}

	/**
	 * <p>
	 * It sets the minimum support value
	 * </p>
	 * @param ms A value representing the minimum support
	 */
	public void setMS(double ms) {
		this.ms = ms;
	}


	/**
	 * <p>
	 * It allows to clone correctly a minimum support
	 * </p>
	 * @return A copy of the membership function
	 */
	public MinimumSupport clone() {
		MinimumSupport minS = new MinimumSupport();

		minS.ms = this.ms;

		return minS;
	}


	/**
	 * <p>
	 * It returns a raw string representation of a minimum support
	 * </p>
	 * @return A raw string representation of the minimum support
	 */
	public String toString() {
		return ( "(minSupport: " + this.ms + ")" );
	}
}

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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 15/06/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

public class Interval {
  /**
   * <p>
   * It represents an interval
   * </p>
   */
	
  private double x0;
  private double x1;
  
  /**
   * <p>
   * Default constructor
   * </p>
   */
  public Interval() {
  }
  
  /**
   * <p>
   * It checks whether a value is covered by an interval
   * </p>
   * @param x The value to check
   * @return True if the value is covered by this interval; False otherwise
   */
  public boolean isCovered(double x) {
	if ((x >= this.x0) && (x <= this.x1)) return true;
	else return (false);
  }
  
  /**
   * <p>
   * It returns the left bound of an interval
   * </p>
   * @return A value representing the left bound of the interval
   */
  public double getLeft() {
	return this.x0;
  }
  
  /**
   * <p>
   * It returns the right bound of an interval
   * </p>
   * @return A value representing the right bound of the interval
   */
  public double getRight() {
	return this.x1;
  }
  
  /**
   * <p>
   * It sets the left bound of an interval
   * </p>
   * @param value A value representing the left bound of the interval
   */
  public void setLeft(double value) {
	this.x0 = value;
  }
  
  /**
   * <p>
   * It sets the right bound of an interval
   * </p>
   * @param value A value representing the right bound of the interval
   */
  public void setRight(double value) {
	this.x1 = value;
  }

  /**
   * <p>
   * It allows to clone correctly an interval
   * </p>
   * @return A copy of the interval
   */
  public Interval clone() {
    Interval interval = new Interval();
    
    interval.x0 = this.x0;
    interval.x1 = this.x1;
    
	return interval;
  }
  
}


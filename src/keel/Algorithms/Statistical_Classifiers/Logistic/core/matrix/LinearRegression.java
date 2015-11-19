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

/*
 * This software is a cooperative product of The MathWorks and the National
 * Institute of Standards and Technology (NIST) which has been released to the
 * public domain. Neither The MathWorks nor NIST assumes any responsibility
 * whatsoever for its use by other parties, and makes no guarantees, expressed
 * or implied, about its quality, reliability, or any other characteristic.
 */

/*
 * LinearRegression.java
 * Copyright (C) 2005 University of Waikato, Hamilton, New Zealand
 *
 */

package keel.Algorithms.Statistical_Classifiers.Logistic.core.matrix;

import keel.Algorithms.Statistical_Classifiers.Logistic.core.Utils;

/**
 * Class for performing (ridged) linear regression.
 *
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 1.1 $
 */
 
public class LinearRegression {

  /** the coefficients */
  protected double[] m_Coefficients = null;

  /**
   * Performs a (ridged) linear regression.
   *
   * @param a the matrix to perform the regression on
   * @param y the dependent variable vector
   * @param ridge the ridge parameter
   * @throws IllegalArgumentException if not successful
   */
  public LinearRegression(Matrix a, Matrix y, double ridge) {
    calculate(a, y, ridge);
  }

  /**
   * Performs a weighted (ridged) linear regression. 
   *
   * @param a the matrix to perform the regression on
   * @param y the dependent variable vector
   * @param w the array of data point weights
   * @param ridge the ridge parameter
   * @throws IllegalArgumentException if the wrong number of weights were
   * provided.
   */
  public LinearRegression(Matrix a, Matrix y, double[] w, double ridge) {

    if (w.length != a.getRowDimension())
      throw new IllegalArgumentException("Incorrect number of weights provided");
    Matrix weightedThis = new Matrix(
                              a.getRowDimension(), a.getColumnDimension());
    Matrix weightedDep = new Matrix(a.getRowDimension(), 1);
    for (int i = 0; i < w.length; i++) {
      double sqrt_weight = Math.sqrt(w[i]);
      for (int j = 0; j < a.getColumnDimension(); j++)
        weightedThis.set(i, j, a.get(i, j) * sqrt_weight);
      weightedDep.set(i, 0, y.get(i, 0) * sqrt_weight);
    }

    calculate(weightedThis, weightedDep, ridge);
  }

  /**
   * performs the actual regression.
   *
   * @param a the matrix to perform the regression on
   * @param y the dependent variable vector
   * @param ridge the ridge parameter
   * @throws IllegalArgumentException if not successful
   */
  protected void calculate(Matrix a, Matrix y, double ridge) {

    if (y.getColumnDimension() > 1)
      throw new IllegalArgumentException("Only one dependent variable allowed");

    int nc = a.getColumnDimension();
    m_Coefficients = new double[nc];
    Matrix xt = a.transpose();
    Matrix solution;

    boolean success = true;

    do {
      Matrix ss = xt.times(a);

      // Set ridge regression adjustment
      for (int i = 0; i < nc; i++)
        ss.set(i, i, ss.get(i, i) + ridge);

      // Carry out the regression
      Matrix bb = xt.times(y);
      for(int i = 0; i < nc; i++)
        m_Coefficients[i] = bb.get(i, 0);

      try {
        solution = ss.solve(new Matrix(m_Coefficients, m_Coefficients.length));
        for (int i = 0; i < nc; i++)
          m_Coefficients[i] = solution.get(i, 0);
        success = true;
      } 
      catch (Exception ex) {
        ridge *= 10;
        success = false;
      }
    } while (!success);
  }

  /**
   * returns the calculated coefficients
   *
   * @return the coefficients
   */
  public final double[] getCoefficients() {
    return m_Coefficients;
  }

  /**
   * returns the coefficients in a string representation
     * @return  the coefficients in a string representation 
   */
  public String toString() {
    return Utils.arrayToString(getCoefficients());
  }
}


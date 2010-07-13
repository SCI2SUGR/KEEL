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

package keel.Algorithms.Preprocess.Missing_Values.EM;

//GCVFCTN    Evaluate object function for generalized cross-validation.
//
//   GCVFCTN(h, d, fc2, trS0, dof0) returns the function values of the
//   generalized cross-validation object function
//
//                     trace [ S0 + F' * diag(g.^2) * F ]
//              G(h) = ---------------------------------- 
//                           ( dof0 + sum(g) )^2
//
//   where g = h^2 ./ (d + h^2) = 1 - d.^2 ./ (d + h^2). The argument h
//   of the GCV function is the regularization parameter, and d is a
//   column vector of eigenvalues (see GCVRIDGE for the meaning of the
//   other symbols above). GCVFCTN is an auxiliary routine that is
//   called by GCVRIDGE. The input arguments are defined in GCVRIDGE:
//
//        h:  regularization parameter (see below),
//        d:  column vector of eigenvalues of cov(X),
//      fc2:  row sum of squared Fourier coefficients, fc2=sum(F.^2, 2),
//     trS0:  trace(S0) = Frobenius norm of generic part of residual matrix,
//     dof0:  degrees of freedom in estimate of residual covariance
//            matrix when regularization parameter is set to zero

//	Adapted from GCVFUN in Per Christian Hansen's REGUTOOLS Toolbox.

/**
 * Implements the GCV function. In this version, the regularization parameter
 * is implemented as the lower and upper bounds of the optimization process
 * @author Julian Luengo Martin
 */
public class Gcvfctn implements  keel.Algorithms.Preprocess.Missing_Values.EM.util.UnivariateFunction{
	/** column vector of eigenvalues of cov(X) */
	double[] d;
	/** row sum of squared Fourier coefficients, fc2=sum(F.^2, 2) */
	double[] fc2;
	/** trace(S0) = Frobenius norm of generic part of residual matrix */
	double trS0;
	double xmin,xmax;
	/** degrees of freedom in estimate of residual covariance  matrix when regularization parameter is set to zero */
	int dof0;
	
	/**
	 * Copy constructor
	 * @param _d the original column vector of eigenvalues
	 * @param _fc2 the original row sum of squared Fourier coefficients
	 * @param _trS0 the new Frobenius norm of generic part of residual matrix
	 * @param _dof0 degrees of freedom in estimate of residual covariance matrix when regularization parameter is set to zero
	 */
	public Gcvfctn(double[] _d,double[] _fc2,double _trS0,int _dof0){
		d = _d;
		fc2 = _fc2;
		trS0 = _trS0;
		dof0 = _dof0;
	}
	
	/**
	 * Evaluates the GCV function
	 * @param f the value at which the function is evaluated
	 */
	public double evaluate(double f){
		double filfac[] = new double[d.length];
		double g;
		
		for(int i=0;i<filfac.length;i++){
			filfac[i] = Math.pow(f, 2) / (d[i] + Math.pow(f, 2));
		}
		
		g = Math.pow((dof0 + sum(filfac)),2);
		for(int i=0;i<filfac.length;i++){
			filfac[i] = Math.pow(filfac[i], 2) * fc2[i];
		}
		g = ( sum(filfac) + trS0 ) / g;
		
		return g;
	}
	
	/**
	 * Computes the sum of the elements of the vector
	 * @param v the reference vector
	 * @return the sum of the elements in v
	 */
	public double sum(double[] v){
		double total = 0;
		for(int i=0;i<v.length;i++)
			total += v[i];
		return total;
	}
	
	/**
	 * Gets the lower bound of the evaluation
	 */
	public double getLowerBound(){
		return xmin;
	}
	
	/**
	 * Gets the upper bound of the evaluation
	 */
	public double getUpperBound(){
		return xmax;
	}
	
	/**
	 * Sets the new bounds
	 * @param newmin new minimum bound
	 * @param newmax new maximum bound
	 */
	public void setBounds(double newmin,double newmax){
		xmin = newmin;
		xmax = newmax;
	}
}


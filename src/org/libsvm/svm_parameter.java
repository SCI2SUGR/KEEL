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

package org.libsvm;
public class svm_parameter implements Cloneable,java.io.Serializable
{
	/* svm_type */
	public static final int C_SVC = 0;
	public static final int NU_SVC = 1;
	public static final int ONE_CLASS = 2;
	public static final int EPSILON_SVR = 3;
	public static final int NU_SVR = 4;

	/* kernel_type */
	public static final int LINEAR = 0;
	public static final int POLY = 1;
	public static final int RBF = 2;
	public static final int SIGMOID = 3;
	public static final int PRECOMPUTED = 4;

	public int svm_type;
	public int kernel_type;
	public int degree;	// for poly
	public double gamma;	// for poly/rbf/sigmoid
	public double coef0;	// for poly/sigmoid

	// these are for training only
	public double cache_size; // in MB
	public double eps;	// stopping criteria
	public double C;	// for C_SVC, EPSILON_SVR and NU_SVR
	public int nr_weight;		// for C_SVC
	public int[] weight_label;	// for C_SVC
	public double[] weight;		// for C_SVC
	public double nu;	// for NU_SVC, ONE_CLASS, and NU_SVR
	public double p;	// for EPSILON_SVR
	public int shrinking;	// use the shrinking heuristics
	public int probability; // do probability estimates

	public Object clone() 
	{
		try 
		{
			return super.clone();
		} catch (CloneNotSupportedException e) 
		{
			return null;
		}
	}

}


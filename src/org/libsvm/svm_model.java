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

//
// svm_model
//
package org.libsvm;
public class svm_model implements java.io.Serializable
{
	svm_parameter param;	// parameter
	int nr_class;		// number of classes, = 2 in regression/one class svm
	int l;			// total #SV
	svm_node[][] SV;	// SVs (SV[l])
	double[][] sv_coef;	// coefficients for SVs in decision functions (sv_coef[k-1][l])
	double[] rho;		// constants in decision functions (rho[k*(k-1)/2])
	double[] probA;         // pariwise probability information
	double[] probB;

	// for classification only

	int[] label;		// label of each class (label[k])
	int[] nSV;		// number of SVs for each class (nSV[k])
				// nSV[0] + nSV[1] + ... + nSV[k-1] = l
	
	public int getnSV (int classId) {
		return nSV[classId];
	}
	
	public svm_node[][] getSV() {
		return SV;
	}	
};


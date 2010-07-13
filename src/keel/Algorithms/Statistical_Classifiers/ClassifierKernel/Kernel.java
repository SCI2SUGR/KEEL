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

/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Classifiers.ClassifierKernel;

import java.io.*;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

public class Kernel {
	/**
	* <p>
	* In this class, a kernel is defined 
	* </p>
	*/		
  static double train[][]; static int ctrain[]; // Training
  static int nclasses;                           // Number of classes
  static double s;                            // Kernel function parameter

  /* Debug */
  static double f[][];
  static int c[];
	/**
	* <p>
	* This is the constructor of the class
	* @param X Train inputs
	* @param C Train classes
	* @param vs Kernel parameter
	* @param vnclasses Number of classes
	* </p>
	*/		  
  public Kernel(double [][]X, int []C, double vs, int vnclasses) {
      train=X; ctrain=C; s=vs; nclasses=vnclasses;
  }
	/**
	* <p>
	* This methods computes kernel function value
	* @param x A vector of double with x values
	* @param x0 A vector of double with the center of the kernel
	* @return The value of the kernel function
	* </p>
	*/	
   private double K(double x[], double x0[]) {
     double y=0;
     for (int i=0;i<x.length;i++) y+=(x[i]-x0[i])*(x[i]-x0[i]);
     return Math.exp(-y*s);
   }
	/**
	* <p>
	* This methods computes the class with maximum kernel value for the input value 
	* @param x Input data
	* @return The class with maximum kernel value
	* </p>
	*/	
   public int internalClassifier(double x[]) {

     // A pattern is classified with kernel method
     double cv[]=new double[nclasses];
     for (int i=0;i<nclasses;i++) cv[i]=0;
     for (int i=0;i<train.length;i++) {
        double dx=K(x,train[i]);
        cv[ctrain[i]]+=dx;
     }

     double max=cv[0]; int imax=0; 
     for (int i=0;i<nclasses;i++) { 
        if (cv[i]>max) {
             max=cv[i]; imax=i;
        }
     }
     return imax;
   }

}


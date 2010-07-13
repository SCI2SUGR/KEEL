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
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.ClassicalOptim;

public class SquaresErrorQUAD extends FUN {
	/**
     * <p>
     * Derived class from FUN that implements the error for a perceptron trained with quadratic conjugated gradient.
     * 
     * </p>
     * 
     */
	//input examples
    double[][] input;
    //expected output
    double[][] output;
    //Neural network container. 
    public ConjGradQUAD Cua;
    /**
     * <p>
     * Constructor of an error calculator for neural network based on the quadratic conjugated gradient.
     * 
     * </p>
     * 
     * @param vCua the perceptron.
     * @param vInput input examples
     * @param vOutput expected output
     */
    public SquaresErrorQUAD(ConjGradQUAD vCua, double[][]vInput, double [][]vOutput) { 
        Cua=vCua; input=vInput; output=vOutput;
    }
        
    /**
	 * Returns the training mean square error for a perceptron with weights x
	 * 
	 * @param x the weights of a perceptron.
	 * @return the training mean square error of a perceptron with weights x.
	 */
    public double evaluate(double x[][][]) {
        // Mean Square Error
        double RMS=0;
        for (int i=0;i<input.length;i++) {
            double error[]=OPV.subtract(Cua.quadraticModelOutput(input[i],x),output[i]);
            RMS+=OPV.multiply(error,error);
        }
        
        // Mean Square Error
        return RMS/input.length;
    }
    
    
}


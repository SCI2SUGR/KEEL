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

package keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.softmax;



import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.IClassifier;

/**
 * <p>
 * @author Written Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface ISoftmaxClassifier extends IClassifier{
	
	/**
	 * <p>
	 * Generic softmax classifier.
	 * </p>
	 */
	
	/**
	 * <p>
	 * Obtain the normalized softmax probabilities of classes of 
	 * one observation
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double [] Array indicating the probability of each class
	 *                   of the observation
	 * </p>                   
	 */
    
    public double[] softmaxProbabilities(double []inputs);
    
	/**
	 * <p>
	 * Obtain the normalized softmax probabilities of classes 
	 * of a set of observations, through their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Matrix indicating the probability of each class
	 *                   of the observation
	 * </p>                  
	 */
    
    public double[][] softmaxProbabilities(double [][]inputs); 
	
	/**
	 * <p>
	 * Obtain the raw output of the classifier for each class
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return double [] Array with the outputs of each class
	 *                   of the observation
	 * </p>                  
	 */
    
    public double[] rawOutputs(double []inputs);
    
	/**
	 * <p>
	 * Obtain the raw outputs of classes of a set of observations,
	 * through their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Matrix indicating the output of each class
	 *                   of the observation
	 * <p>                  
	 */
    
    public double[][] rawOutputs(double [][]inputs); 
    
}


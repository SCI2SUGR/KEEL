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

package keel.Algorithms.Neural_Networks.NNEP_Clas.neuralnet;


import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.softmax.ISoftmaxClassifier;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.AbstractNeuralNet;


/**
 * <p> 
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * </p>
 * @version 0.1
 * @since JDK1.5
 */

public class NeuralNetClassifier extends AbstractNeuralNet implements ISoftmaxClassifier {
	
	/**
	 * <p>
	 *  Neural net used as a classifier, with the posibility
	 *  of estimating probability of each class
	 *  </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    
    public NeuralNetClassifier() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// -------------------- Implementing ISoftmaxClassifier interface
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Obtain the associated class of one observation
	 * 
	 * @param inputs Double array with all inputs of the observation
	 * 
	 * @return byte [] Array indicating the class of the observation
	 *                 For example, {0,0,1} indicates that observation
	 *                 is of the third class, {0,1,0} indicates that 
	 *                 observation is of the second class
	 * </p>
	 */
	
	public byte[] classify(double []inputs){
		
		//Obtain outputs
		double obtained[] = rawOutputs(inputs);
		
		//Classes array
		byte[] classes = new byte[outputLayer.getNofneurons()+1];
		
		//Maximum value (initially first output)
		double max = obtained[0];
		classes[0] = 1;
		
		//Index of the maximum value (initially first output)
		int index = 0;
		
		//For each output node
		for(int j=1; j<obtained.length; j++ )
			if(obtained[j]>=max){
				classes[j] = 1;
				if(obtained[j]>max)
					classes[index] = 0;
				max = obtained[j];
				index = j;
			}
		
		return classes;
	}
	
	/**
	 * <p>
	 * Obtain the associated class of a set of observations, through
	 * their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return byte[] Matrix indicating the class of the observation
	 *                For example, {0,0,1} indicates that observation
	 *                is of the third class, {0,1,0} indicates that 
	 *                observation is of the second class
	 * </p>               
	 */
	
	public byte[][] classify(double [][]inputs){
		
		//Obtain outputs
		double obtained[][] = rawOutputs(inputs);
		
		//Classes matrix
		byte[][] classes = 
			new byte[outputLayer.getNofneurons()+1][inputs[0].length];
		
		//Array of maximum values (initially first output)
		double max[] = new double[inputs[0].length];
		System.arraycopy(obtained[0],0,max,0,inputs[0].length);
		
		//Array of indexes maximum values (initially first output)
		int maxIndex[] = new int[inputs[0].length];
		for(int i=0; i<maxIndex.length; i++){
			classes[0][i] = 1;
			maxIndex[i] = 0;
		}
		
		//For each output node
		for(int j=1; j<obtained.length; j++ )
			for(int i=0; i<inputs[0].length; i++)
				if(obtained[j][i]>=max[i]){
					classes[j][i] = 1;
					if(obtained[j][i]>max[i])
						classes[maxIndex[i]][i] = 0;
					max[i] = obtained[j][i];
					maxIndex[i] = j;
				}
		
		return classes;
		
	}
	
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
	
	public double[] rawOutputs(double []inputs){
		
		// Obtained outputs
		double obtained[] =
			new double[outputLayer.getNofneurons()+1];
		
		// i-th output
		for(int i=0; i<obtained.length-1; i++)
			obtained[i] = outputLayer.getNeuron(i).operate(inputs);
		
		// 0 output
		obtained[obtained.length-1] = 0;
		
		return obtained;
	}
	
	/**
	 * <p>
	 * Obtain the raw outputs of classes of a set of observations,
	 * through their inputs values
	 * 
	 * @param inputs Double matrix with all inputs of all observations
	 * 
	 * @return double[] Matrix indicating the output of each class
	 *                   of the observation
	 * </p>                  
	 */
	
	public double[][] rawOutputs(double [][]inputs){
		
		// Obtained outputs
		double obtained[][] =
			new double[outputLayer.getNofneurons()+1][];
		
		// i-th output
		for(int i=0; i<obtained.length-1; i++)
			obtained[i] = outputLayer.getNeuron(i).operate(inputs);
		
		// 0 output
		obtained[obtained.length-1] = new double[inputs[0].length];
		for(int j=0; j<inputs[0].length; j++)
			obtained[obtained.length-1][j] = 0;
		
		return obtained;
	}
	
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
    
    public double[] softmaxProbabilities(double []inputs){
		
		// Obtain outputs
		double probabilities[] = rawOutputs(inputs);
		
		// Sum of exp(rawOutputs) values
		double expSum = 0;
		for(int i=0; i<probabilities.length; i++){
			if(i!=probabilities.length-1)
				probabilities[i] = Math.exp(probabilities[i]);
			else
				probabilities[i] = 1;
			expSum += probabilities[i];
		}
		
		// Test problems with very high outputs
		if(Double.isInfinite(expSum) || Double.isNaN(expSum)){
			// Obtain outputs
			probabilities = rawOutputs(inputs);
			
			// Sum of exp(rawOutputs) values
			expSum = 0;
			for(int i=0; i<probabilities.length; i++){
				probabilities[i] /= 50000.;
				if(i!=probabilities.length-1)
					probabilities[i] = Math.exp(probabilities[i]);
				else
					probabilities[i] = 1;
				expSum += probabilities[i];
			}
		}
		
		// Normalize outputs
		for(int i=0; i<probabilities.length; i++){
			probabilities[i] /= expSum;
		}
		
    	return probabilities;
    }
    
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
    
    public double[][] softmaxProbabilities(double [][]inputs){

		// Obtain outputs
		double probabilities[][] = rawOutputs(inputs);
		
		// Sum of exp(rawOutputs) values
		double[] expSum = new double[inputs[0].length];
		for(int i=0; i<expSum.length; i++)
			expSum[i] = 0;	
		for(int i=0; i<probabilities.length; i++){
			for(int j=0; j<inputs[0].length; j++){
				if(i!=probabilities.length-1)
					probabilities[i][j] = Math.exp(probabilities[i][j]);
				else
					probabilities[i][j] = 1;
				expSum[j] += probabilities[i][j];					
			}
		}

		for(int j=0; j<inputs[0].length; j++){
			// Test problems with very high outputs
			if(Double.isInfinite(expSum[j]) || Double.isNaN(expSum[j])){			
				// Obtain outputs
				probabilities = rawOutputs(inputs);
				for(int i=0; i<expSum.length; i++)
					expSum[i] = 0;	
				// Sum of exp(rawOutputs) values
				for(int i=0; i<probabilities.length; i++){
					for(int k=0; k<inputs[0].length; k++){
						probabilities[i][k] /= 50000.;
						if(i!=probabilities.length-1)
							probabilities[i][k] = Math.exp(probabilities[i][k]);
						else
							probabilities[i][k] = 1;
						expSum[k] += probabilities[i][k];					
					}
				}				
				j = inputs[0].length;
			}
		}

		// Normalize outputs
		for(int i=0; i<probabilities.length; i++)
			for(int j=0; j<inputs[0].length; j++)
				probabilities[i][j] /= expSum[j];
		
    	return probabilities;    	
    }
}


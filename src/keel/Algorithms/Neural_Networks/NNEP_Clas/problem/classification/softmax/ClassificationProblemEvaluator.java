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

import java.util.Comparator;
import java.util.List;

import keel.Algorithms.Neural_Networks.NNEP_Clas.problem.classification.IClassifier;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.problem.ProblemEvaluator;
import keel.Algorithms.Neural_Networks.NNEP_Common.problem.errorfunctions.IErrorFunction;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.fitness.ValueFitnessComparator;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Modified by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class ClassificationProblemEvaluator extends ProblemEvaluator<AbstractIndividual<? extends ISoftmaxClassifier>> implements IConfigure {
	
	/**
	 * <p>
	 * Regression problem evaluator
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Error Function
	/////////////////////////////////////////////////////////////////
	
	/** Error function to evaluate classificator */
	
	IErrorFunction<double[][]>  defaultErrorFunction;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Fitness comparator
	/////////////////////////////////////////////////////////////////
	
	/** Fitnesses comparator */
	
	protected Comparator<IFitness> comparator = 
		new ValueFitnessComparator(false);
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    
    public ClassificationProblemEvaluator() {
        super();
    }
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------- Classification errors
	/////////////////////////////////////////////////////////////////
	
    /**
     * <p>
     * Returns the train error value of a neural net with an specified
     * error function
     *
     * @param classifier Neural net to obtain the error
     * @param errorFunction Error function to obtain the error
     * 
     * @return double Train error value
     * </p>
     */
    
    public double getTrainClassificationError(IClassifier classifier, IErrorFunction<byte[][]> errorFunction){    	
		
    	// Dataset to be used
		DoubleTransposedDataSet dataset;
		if(dataNormalized)
			dataset = scaledTrainData;
		else
			dataset = unscaledTrainData;
		
		// Obtained outputs with this dataSet
		byte obtained[][] = 
			classifier.classify(dataset.getAllInputs());

		// Casting the outputs
		double doubleExpected[][] = dataset.getAllOutputs();
		byte expected[][] = new byte[doubleExpected.length][doubleExpected[0].length];
		for(int i=0; i<doubleExpected.length; i++)
			for(int j=0; j<doubleExpected[i].length; j++)
				expected[i][j] = (byte) doubleExpected[i][j];
        
		// Obtain error
		double error = 
			errorFunction.calculateError(obtained,expected);
		
        // Return train error value
        return error;
    }
    
    /**
     * <p>
     * Returns the test error value of a neural net with an specified
     * error function
     *
     * @param classifier Neural net to obtain the error
     * @param errorFunction Error function to obtain the error
     * 
     * @return double Test error value
     * </p>
     */
    
    public double getTestClassificationError(IClassifier classifier, IErrorFunction<byte[][]> errorFunction){
		
    	// Dataset to be used
		DoubleTransposedDataSet dataset;
		if(dataNormalized)
			dataset = scaledTestData;
		else
			dataset = unscaledTestData;
		
		// Obtained outputs with this dataSet
		byte obtained[][] = 
			classifier.classify(dataset.getAllInputs());
		
		// Casting the outputs
		double doubleExpected[][] = dataset.getAllOutputs();
		byte expected[][] = new byte[doubleExpected.length][doubleExpected[0].length];
		for(int i=0; i<doubleExpected.length; i++)
			for(int j=0; j<doubleExpected[i].length; j++)
				expected[i][j] = (byte) doubleExpected[i][j];
        
		// Obtain error
		double error = 
			errorFunction.calculateError(obtained,expected);
		
        // Return test error value
        return error;
    }
    
    /**
     * <p>
     * Returns an array with the bevavior of the classifier with
     * train dataset
     *
     * @param classifier Classifier to obtain the error
     * 
     * @return double[] Behavior array
     * </p>
     */
    
    public double[] getTrainClassificationBehaviorArray(IClassifier classifier){
    	
    	
    	// Dataset to be used
		DoubleTransposedDataSet dataset;
		if(dataNormalized)
			dataset = scaledTrainData;
		else
			dataset = unscaledTrainData;
		
		// Resulting array
		double[] result = 
    		new double[dataset.getNofobservations()];
		
		// Obtained outputs with this dataSet
		byte obtained[][] = 
			classifier.classify(dataset.getAllInputs());
		
		// Expected outputs
		double expected[][] = dataset.getAllOutputs();
		
		// Init result
		for(int j=0; j<dataset.getNofobservations(); j++)
			result[j] = 1;
				
		// Put a 0 in observations where the expected is not 
		// the same than the observed
		for(int o=0; o<obtained.length; o++) //For each output (o)
			for(int e=0; e<obtained[o].length; e++) //For each example (e)
				if(obtained[o][e] != expected[o][e])
					if(result[e] == 1)
						result[e] = 0;
		
		return result;
	}
    
    /**
     * <p>
     * Returns an array with the bevavior of the classifier with
     * test dataset
     *
     * @param classifier Classifier to obtain the error
     * 
     * @return double[] Behavior array
     * </p>
     */
    
    public double[] getTestClassificationBehaviorArray(IClassifier classifier){    	
    	
    	// Dataset to be used
		DoubleTransposedDataSet dataset;
		if(dataNormalized)
			dataset = scaledTestData;
		else
			dataset = unscaledTestData;
		
		// Resulting array
		double[] result = 
    		new double[dataset.getNofobservations()];
		
		// Obtained outputs with this dataSet
		byte obtained[][] = 
			classifier.classify(dataset.getAllInputs());
		
		// Expected outputs
		double expected[][] = dataset.getAllOutputs();
		
		// Init result
		for(int j=0; j<dataset.getNofobservations(); j++)
			result[j] = 1;
				
		// Put a 0 in observations where the expected is not 
		// the same than the observed
		for(int o=0; o<obtained.length; o++) //For each output (o)
			for(int e=0; e<obtained[o].length; e++) //For each example (e)
				if(obtained[o][e] != expected[o][e])
					if(result[e] == 1)
						result[e] = 0;
		
		return result;
	}
    
    /**
     * <p>
     * Returns a matrix with the comparative bevavior of two
     * classifiers in the train dataset
     *
     * @param classifier1 First classifier
     * @param classifier2 Second classifier
     * 
     * @return double[][] Behavior matrix
     * </p>
     */
    
    public double[][] getTrainClassificationBehaviorMatrix(IClassifier classifier1, IClassifier classifier2){    	
    			
		// Resulting matrix
		double[][] result = new double[2][2];
		
		// Behavior array
		double[] behavior1 = this.getTrainClassificationBehaviorArray(classifier1);
		double[] behavior2 = this.getTrainClassificationBehaviorArray(classifier2);
		
		// Init matrix
		for(int i=0; i<result.length; i++)
			for(int j=0; j<result[i].length; j++)
				result[i][j] = 0;
		
		for(int i=0; i<behavior1.length; i++){
			if(behavior1[i]==1 && behavior2[i]==1)
				result[0][0]++;
			else if(behavior1[i]==1 && behavior2[i]==0)
				result[0][1]++;
			else if(behavior1[i]==0 && behavior2[i]==1)
				result[1][0]++;
			else if(behavior1[i]==0 && behavior2[i]==0)
				result[1][1]++;
		}
		
		return result;
	}
    
    /**
     * <p>
     * Returns a matrix with the comparative bevavior of two
     * classifiers in the test dataset
     *
     * @param classifier1 First classifier
     * @param classifier2 Second classifier
     * 
     * @return double[][] Behavior matrix
     * </p>
     */
    
    public double[][] getTestClassificationBehaviorMatrix(IClassifier classifier1, IClassifier classifier2){
    	
		// Resulting matrix
		double[][] result = new double[2][2];
		
		// Behavior array
		double[] behavior1 = this.getTestClassificationBehaviorArray(classifier1);
		double[] behavior2 = this.getTestClassificationBehaviorArray(classifier2);
		
		// Init matrix
		for(int i=0; i<result.length; i++)
			for(int j=0; j<result[i].length; j++)
				result[i][j] = 0;
		
		for(int i=0; i<behavior1.length; i++){
			if(behavior1[i]==1 && behavior2[i]==1)
				result[0][0]++;
			else if(behavior1[i]==1 && behavior2[i]==0)
				result[0][1]++;
			else if(behavior1[i]==0 && behavior2[i]==1)
				result[1][0]++;
			else if(behavior1[i]==0 && behavior2[i]==0)
				result[1][1]++;
		}
		
		return result;
	}
    
    /**
     * <p>
     * Returns the diversity measure Q statistic of two classifiers
     * in train dataset
     *
     * @param classifier1 First classifier
     * @param classifier2 Second classifier
     * 
     * @return double Ro statistic
     * </p>
     */
    
    public double getTrainQStatistic(IClassifier classifier1, IClassifier classifier2){    	
    			
		// Resulting matrix
		double[][] matrix = 
			this.getTrainClassificationBehaviorMatrix(classifier1, classifier2);
		
		double term1 = matrix[1][1]*matrix[0][0];
		double term2 = matrix[0][1]*matrix[1][0];
		
		return (term1-term2) / (term1+term2);
	}

    
    /**
     * <p>
     * Returns the diversity measure Q statistic of two classifiers
     * in test data set
     *
     * @param classifier1 First classifier
     * @param classifier2 Second classifier
     * 
     * @return double Ro statistic
     * </p>
     */
    
    public double getTestQStatistic(IClassifier classifier1, IClassifier classifier2){    	
    			
		// Resulting matrix
		double[][] matrix = 
			this.getTestClassificationBehaviorMatrix(classifier1, classifier2);
		
		double term1 = matrix[1][1]*matrix[0][0];
		double term2 = matrix[0][1]*matrix[1][0];
		
		return (term1-term2) / (term1+term2);
	}
    
    /**
     * <p>
     * Returns the diversity measure Q statistic of a list of classifiers
     * in train dataset
     *
     * @param classifiers List of classifiers
     * 
     * @return double Ro statistic
     * </p>
     */
    
    public double getTrainQStatistic(List<IClassifier> classifiers){
    	double result=0;
    	double nOfClassifiers = classifiers.size();
    	
    	for(int i=0; i<classifiers.size()-1; i++)
    		for(int j=i+1; j<classifiers.size(); j++)
    			result += getTrainQStatistic(classifiers.get(i), classifiers.get(j));
    			
    	return (2/(nOfClassifiers*(nOfClassifiers-1))) * result;
	}

    
    /**
     * <p>
     * Returns the diversity measure Q statistic of two classifiers
     * in test data set
     *
     * @param classifiers First classifier
     * 
     * @return double Ro statistic
     * </p>
     */

    public double getTestQStatistic(List<IClassifier> classifiers){
    	double result=0;
    	double nOfClassifiers = classifiers.size();
    	
    	for(int i=0; i<classifiers.size()-1; i++)
    		for(int j=i+1; j<classifiers.size(); j++)
    			result += getTestQStatistic(classifiers.get(i), classifiers.get(j));
    	
    	return (2/(nOfClassifiers*(nOfClassifiers-1))) * result;    			
	}
}


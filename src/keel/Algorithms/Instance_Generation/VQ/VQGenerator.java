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

package keel.Algorithms.Instance_Generation.VQ;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import java.util.*;

/**
 * Class that contains Vector Quantization algorithm
 * @author diegoj
 */
public class VQGenerator extends LVQ1
{
    /** Inverse of the number of iterations which performs the algorithm. */
    protected double inverseOfNumberOfIterations = 1.0;
    
    /** Nearest-neighbors selected to assign class to each prototype of the selected data set. */
    protected int k = 1;
 
    /**
     * Constructs a new VQGenerator algorithm.
     * @param t Training data prototypes.
     * @param iterations Number of iterations that will execute the algorithm.
     * @param np Number of prototypes to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     * @param k Number of nearest-neightbors to be searched in original data set for each prototype of the extracted set.
     */    
    public VQGenerator(PrototypeSet t, int iterations, int np, double alpha_0, int k)
    {
        super(t, iterations, np, alpha_0);
        this.algorithmName="VQ";
        inverseOfNumberOfIterations = 1.0/(double)(iterations);
        this.k = k;
        KNN.setK(k); 
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, t.size()-1);        
    }
    
    /**
     * Constructs a new VQGenerator algorithm (using 1-Np rule).
     * @param t Training data prototypes.
     * @param iterations Number of iterations that will execute the algorithm.
     * @param pcNprots % of prototypes of training to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     * @param k Number of nearest-neightbors to be searched in original data set for each prototype of the extracted set.
     */    
    public VQGenerator(PrototypeSet t, int iterations, double pcNprots, double alpha_0, int k)
    {
        super(t, iterations, pcNprots, alpha_0);
        this.algorithmName="VQ";
        inverseOfNumberOfIterations = 1.0/(double)(iterations);
        this.k = k;
        KNN.setK(k);      
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, t.size()-1);        
    }
    
    /**
     * Constructs a new VQGenerator algorithm (using K-Np rule).
     * @param t Training data prototypes. 
     */
    public VQGenerator(PrototypeSet t, Parameters param)
    {
        super(t, param);
        this.algorithmName="VQ";
        inverseOfNumberOfIterations = 1.0/(double)(iterations);
        this.k = 1;
        if(param.existMore())
            this.k = param.getNextAsInt();
        KNN.setK(k);
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, t.size()-1);
    }
    
    //Reward is the same than LVQ1. Beware with the reward operation
   
    /**
     * Update alpha in stage t to the stage t+1
     * @param t Stage of the algorithm (iteration of the process).     
     */
    protected void updateAlpha(int t)
    {
        //Optimized by the use of the inverse of the number of iterations
        alpha_0 = (1.0 - (double)t * inverseOfNumberOfIterations) * alpha_0;
    }
    
   /**
    * Corrects the instance using a particular method
    * @param i is a instance of the instance set.
    * @param tData is the training data set. IS MODIFIED.
    */
    @Override
    protected void correct(Prototype i, PrototypeSet tData)
    {
        Prototype nearest = KNN._1nn(i, tData);//u
        double class_i = i.label();
        double class_nearest = nearest.label();
        if(class_i == class_nearest)
            reward(nearest,i);
    }
    
    /**
    * Execute the method and returns the output instance set
    * @return a instance set modified from the training instance set by a LVQ method
    */
    @Override
    public PrototypeSet reduceSet() 
    {
        PrototypeSet outputDataSet = initDataSet();//trainingDataSet is the X-set
        int it = 0;
        //Debug.println("Iteracion normal");
        while(it<iterations)
        {
            Prototype instance = extract(trainingDataSet);
            correct(instance, outputDataSet);
            updateAlpha(it);
            ++it;
        }
        
        //Post-supervised learning, it's not so difficult
        for(Prototype p : outputDataSet)
        {
            PrototypeSet neighbors = KNN.knn(p, trainingDataSet);
            double maxFreqClass = neighbors.mostFrequentClass();
            p.setClass(maxFreqClass);
        }
        return outputDataSet;
    }
    
     /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set
     * 2: Number of iterations of the algorithm.
     * 3: Number of prototypes of the generated set.
     * 4: Alpha0 constant of the process.
     * 5: k Number of neighbors used in the KNN function
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("VQ", "<num. iterations> <percentage of num. prototypes> <alpha_0> <k>");
        Parameters.assertBasicArgs(args);        
        
        //Debug.setStdDebugMode(false);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        VQGenerator.setSeed(seed);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        double pcNprot = Parameters.assertExtendedArgAsDouble(args,4,"percentage of prototypes", 0, 100);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0, 1);
        int k =  Parameters.assertExtendedArgAsInt(args,6,"k", 1, 5);
        
         //VQGenerator(PrototypeSet t, int iterations, int np, double alpha_0, double threshold, int k)
        VQGenerator generator = new VQGenerator(training, iter, pcNprot, alpha_0,  k);
        
    	PrototypeSet resultingSet = generator.execute();
        
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }

}


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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Instance_Generation.BTS3;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import org.core.*;
//import java.util.ArrayList;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

/**
 * Prototoype generator by the Boostrap algorithm (BST3)
 * @author diegoj
 */
public class BTS3Generator extends PrototypeGenerator
{
    /** Size of the generated set. */
    protected int numberOfPrototypesGenerated = 5;
    
    /** Number of bootstrapings of the algorithm. */
    protected int randomTrials = 10;
    
    /** Size of the merged data set. */
    protected int nearestNeighbors = 1;
    
    /**
     * Constructor
     * @param _trainingDataSet Traning data set.
     * @param parameters Parameters of the algorithm.
     */
    public BTS3Generator(PrototypeSet _trainingDataSet, Parameters parameters) {
        super(_trainingDataSet, parameters);
        algorithmName="BTS3";
        this.numberOfPrototypesGenerated = this.getSetSizeFromPercentage(parameters.getNextAsDouble());
        this.nearestNeighbors = parameters.getNextAsInt();
        this.randomTrials = parameters.getNextAsInt();
    }

    /**
     * Constructor
     * @param _trainingDataSet Traning data set.     
     * @param Np Number of prototypes to be generated.
     * @param nearestNeighbors Nearest-neighbors used to assign class to each prototype generated.
     * @param trials Random trials to be performed during execution.
     */
    public BTS3Generator(PrototypeSet _trainingDataSet,int Np, int nearestNeighbors, int trials)
    {
        super(_trainingDataSet);
        algorithmName="BTS3";        
        this.numberOfPrototypesGenerated = Np;
        this.nearestNeighbors = nearestNeighbors;
        this.randomTrials = trials;                
    }
    
    /**
     * Constructor
     * @param _trainingDataSet Traning data set.     
     * @param percSize Percentage of the size of training set that will be the reduced set.
     * @param nearestNeighbors Nearest-neighbors used to assign class to each prototype generated.
     * @param trials Random trials to be performed during execution.
     */
    public BTS3Generator(PrototypeSet _trainingDataSet, double percSize, int nearestNeighbors, int trials)
    {
        super(_trainingDataSet);
        algorithmName="BTS3";        
        this.numberOfPrototypesGenerated = this.getSetSizeFromPercentage(percSize);
        this.nearestNeighbors = nearestNeighbors;
        this.randomTrials = trials;                
    }

    /**
     * Returns a new data set with each prototype is a centroid of their nearest-neighbors with itself.
     * @param original Prototype set to be condensed.
     * @return Merged, condensed prototype set of the original set.
     */
    PrototypeSet mergePrototypesWithNNMethod(PrototypeSet original)
    {
        PrototypeSet merged = new PrototypeSet();
        for(Prototype p : original)
        {
            PrototypeSet neighborsOfP = KNN.getNearestNeighborsWithSameClassAs(p,original,nearestNeighbors);
            neighborsOfP.add(p);
            Prototype mixed = neighborsOfP.avg().formatear();//media de todos los vecinos + p
            merged.add(mixed);
        }
        return merged;
    }
    
    /**
     * Reduce the training data set by the Hamamoto et al. Bootstrap method
     * @return Reduced prototype data set that is the condensed input data set.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        boolean useApriori = true;
        PrototypeSet best = null;
        int bestAccuracy = -1;
        //for each random trial used
        for(int i=0; i<randomTrials; ++i)
        {
            //1. Select a random sample (with a-priory probability????)
            PrototypeSet selected = super.selecRandomSet(numberOfPrototypesGenerated, useApriori);
            //2. Merge each point (in selected set) with its k nearest-neighbors
            PrototypeSet merged = mergePrototypesWithNNMethod(selected);
            //3. Do randomTrials test with the 1-NN rule on the original data set
            //to see which class is the best to each prototype of the merged prototype set
            PrototypeSet finalSet = new PrototypeSet();
            for(Prototype p : merged)
            {
                Prototype nearest = KNN._1nn(p, trainingDataSet);
                finalSet.add(nearest);
            }
            int finalSetAccuracy = absoluteAccuracy(finalSet, trainingDataSet);
            //Debug.println("ANTES " + finalSetAccuracy+" vs "+bestAccuracy);
            if(finalSetAccuracy > bestAccuracy)
            {
                //Debug.println(finalSetAccuracy+" vs "+bestAccuracy);
                bestAccuracy = finalSetAccuracy;
                best = finalSet;
            }
            //Debug.println("Trial "+i);
        }
        //Debug.endsIf(best==null, "best is null");
        return best;
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich will contain the test data set
     * 2: Seed of the random generator.
     * 3: Number of prototypes to be generated.
     * 4: Nearest-Neighbors used in the internal KNN use.
     * 5: Random Trials (number of bootstrappings performed).
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("BTS3", "<seed> <percentage of prototypes generated> <Nearest-neightbors (size of the merged sets)> <random trials>");
        Parameters.assertBasicArgs(args);        
        
        //Debug.setStdDebugMode(false);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,Parameters.SEED_TXT,0,Long.MAX_VALUE);
        BTS3Generator.setSeed(seed);
        double percNprot = Parameters.assertExtendedArgAsDouble(args,3,Parameters.PERC_SIZE_TXT, 0, 100);
        int k = Parameters.assertExtendedArgAsInt(args,4,"Nearest-neighbors used in the merging process", 1, 5);        
        int randomTrials = Parameters.assertExtendedArgAsInt(args,5,"number of random trials", 1, Integer.MAX_VALUE);
    
        BTS3Generator generator = new BTS3Generator(training, percNprot, k, randomTrials);
        
    	PrototypeSet resultingSet = generator.execute();
        
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }
}


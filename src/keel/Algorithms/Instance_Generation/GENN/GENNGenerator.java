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

package keel.Algorithms.Instance_Generation.GENN;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;

/**
 * Generalized Edited Nearest Neighbor
  * @author diegoj
 */
public class GENNGenerator extends PrototypeGenerator
{
    /** Number of neighbors selected in the underlying KNN. */
    protected int k = 4;
    
    // Index of the parameter k.
    //public static final int IK = 2;
    
    /**
     * Constructor of GENNGenerator objects.
     * @param tr Training Data Set.
     * @param k Number of neighbors selected in the underlying KNN
     */
    public GENNGenerator(PrototypeSet tr, int k)
    {
        super(tr);
        algorithmName = "GENN";//Name of the algorithm
        this.k = k;
    }
    
    /**
     * Constructor of GENNGenerator objects.
     * @param tr Training Data Set.
     * @param param Parameters needed of GENNGenerator.
     */
    public GENNGenerator(PrototypeSet tr, Parameters param)
    {
        super(tr, param);
        algorithmName = "GENN";//Name of the algorithm
        this.k = param.getNextAsInt();
    }
    
    /**
     * Informs if there are a majority of prototypes whose class is the same as other prototype.
     * @param current Prototype which class is used.
     * @param protSet Set which the search will be performed.
     * @return TRUE if the number of prototypes whith current's class is equal or greather than 50%.
     */
    protected boolean majorityOfSameClass(Prototype current, PrototypeSet protSet)
    {
        double currentLabel = current.label();
        int protSet_size = protSet.size();
        int count = 0;
        for(Prototype p : protSet)
            if(currentLabel == p.label())
                ++count;
        
        return count > protSet_size/2;
    }
    
    /**
     * Reduce the set by the GENNGenerator method.
     * @return Reduced data set by the GENNGenerator method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        int _size = trainingDataSet.size(); 
        PrototypeSet result = new PrototypeSet(_size);
        result.add(trainingDataSet);        
        KNN.setK(k);
        
        //Groups of k prototypes
        int numberOfGroups = _size / k;
                
        int i = 0;
        int index = RandomGenerator.Randint(0, _size);
        while(i<numberOfGroups)//O(n_g*k*n_e)
        {
            //WARNING!
            //The current implementation depends on the extracting order 
            //of the algorithm. In other case, use trainingDataSet.get(index)
            //and KNN.knn(current, trainingDataSet)
            Prototype current = result.get(index);
            PrototypeSet neighbors = KNN.knn(current, result);
            //There are majority class. All group is classified as this frequent class
            if(majorityOfSameClass(current, neighbors))
            {
                double maxFreqClass = neighbors.mostFrequentClass();
                //Debug.errorln("MaxFreqClass " + maxFreqClass);
                //Sets each element class to the maximum frequent class
                for(Prototype p : neighbors)
                    p.setLabel(maxFreqClass);
                current.setLabel(maxFreqClass);
            }
            else
            //There are no majority class. All the group is deleted
            {
                //System.out.println("REMOVE");
                result.remove(index);
                for(Prototype p: neighbors)// O(k*n)
                    result.remove(p);//
            }
            index = RandomGenerator.Randint(0, result.size()-1);
            ++i;
         }
        return result;
    }
    
    
     /**
     * General main for GENNGenerator prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set.
     * 2: Seed used in the random generator.
     * 3: k (size of neighborhood in KNN).
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("GENN", "<seed> <k (size of neighborhood in KNN)>");        
        Parameters.assertBasicArgs(args);
                
        //System.out.println("Using k = " + k);
        //Parameters param = new Parameters(args);        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        GENNGenerator.setSeed(seed);
        
        int k = Parameters.assertExtendedArgAsInt(args, 3, "size of the neighborhood", 1, 10);
       
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);

        GENNGenerator generator = new GENNGenerator(training, k);
        //resultingSet.save(args[1]);
        
    	PrototypeSet resultingSet = generator.execute();    	
        
        //System.out.println(resultingSet.toString());
        //KNN.setK(3);
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test);
        int accuracy1NN = KNN.classficationAccuracy1NN(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }
}// end of the GENNGenerator class


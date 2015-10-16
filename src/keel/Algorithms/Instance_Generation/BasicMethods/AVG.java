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

package keel.Algorithms.Instance_Generation.BasicMethods;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;

/**
 * Implements the reduction of the prototype set, making a centroid for each class.
 * That is, it adds average prototypes of each class to the reduced set.
 * @author diegoj
 */
public class AVG extends PrototypeGenerator
{

    /**
     * Constructs the AVG
     * @param _trainingDataSet Original training prototypes set.
     */
    public AVG(PrototypeSet _trainingDataSet)
    {
        super(_trainingDataSet);
        algorithmName="AVG";
    }
    
    /**
     * Constructs the AVG
     * @param _trainingDataSet Original training prototypes set.
     * @param param Parameters of the algorithm (random seed).
     */
    public AVG(PrototypeSet _trainingDataSet, Parameters param)
    {
        super(_trainingDataSet, param);
        algorithmName="AVG";
    }
    
    /**
     * Reduces the set by adding centroid prototype of each class to reduced set.
     * @return Reduced set of centroids of classes of the original training set.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        PrototypeSet reduced = new PrototypeSet();
        ArrayList<Double> classes = trainingDataSet.nonVoidClasses();
        //For each class in the training data set, calculate the centroid of
        //its class-partition and adds it to the reduced set.
        for(double c : classes)
        {
            PrototypeSet Tc = trainingDataSet.getFromClass(c);
            //Debug.errorln("Number of ps of class " + c + ": " + Tc.size());
            Prototype centroid_c =Tc.avg();
            centroid_c.setLabel(c);
            reduced.add(centroid_c);
        }
        return reduced;
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich will contain the test data set
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("AVG", "");        
        Parameters.assertBasicArgs(args);
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        AVG generator = new AVG(training);
        
    	PrototypeSet resultingSet = generator.execute();
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);        
    }

}//end-of-class


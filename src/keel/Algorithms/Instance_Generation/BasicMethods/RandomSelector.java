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

package keel.Algorithms.Instance_Generation.BasicMethods;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;

/**
 * Implements a random selection of the training data to the edited data set.
 * @author diegoj
 */
public class RandomSelector extends PrototypeGenerator
{
    //! Number of prototypes that will contain the generated data set (prototypes extracted from the training data set).
    protected int numberOfPrototypesSelected;
    
    /**
     * Creates a new RandomSelector
     * @param t Traning data set
     * @param n Number of prototypes to be extracted.
     */
    public RandomSelector(PrototypeSet t, int n)
    {
        super(t);
        algorithmName = "RandomSelector";
        numberOfPrototypesSelected = n;
        
    }
    /**
     * Creates a new RandomSelector
     * @param t Traning data set
     * @param params External parameters
    */
    public RandomSelector(PrototypeSet t, Parameters params)
    {
        super(t, params);
        algorithmName = "RandomSelector";        
    }
    
    /**
     * Extract prototypes from the training data and returns them in a new data set.
     * @return PrototypeSet containing the extracted prototypes.
     */
    @Override
    public PrototypeSet reduceSet()
    {
       return super.selecRandomSet(numberOfPrototypesSelected, false);
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
        Parameters.setUse("RandomSelector", "<number of prototypes>");        
        Parameters.assertBasicArgs(args);
        
        RandomSelector.setSeed(System.nanoTime()*100 + System.nanoTime());
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        int n = Parameters.assertExtendedArgAsInt(args,2,"number of prototypes", 1, training.size()-1);

        RandomSelector generator = new RandomSelector(training, n);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
    }

}


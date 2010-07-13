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

import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class SAVG extends AVG
{

    private double percentSelection = 0.2;
    
    /**
     * Creates a new SAVG object.
     * @param traDataSet Training data set.
     * @param ps Percent of the initial set selected.
     */
    public SAVG(PrototypeSet traDataSet, double ps)
    {
        super(traDataSet);
        algorithmName="SAVG";
        percentSelection = ps;
        if(ps>1.0)
            percentSelection = ps/100.0;
        
    }
    
    /**
     * Creates a new SAVG object.
     * @param traDataSet Training data set.
     * @param param Parameters of the method.
     */
    public SAVG(PrototypeSet traDataSet, Parameters param)
    {
        super(traDataSet,param);
        algorithmName="SAVG";
        if(param.existMore())
            percentSelection = param.getNextAsDouble();
        if(percentSelection > 1.0)
            percentSelection /= 100.0;
    }
    
    /**
     * Reduce the set.
     * @return Reduce the set by SAVG method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        int size = (int)Math.ceil(trainingDataSet.size() * percentSelection);
        ARS ars = new ARS(trainingDataSet, size);
        PrototypeSet reduced = ars.reduceSet();
        AVG avg = new AVG(reduced);
        return avg.reduceSet();
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
        Parameters.setUse("SAVG", "<seed> [% initial selection]");        
        Parameters.assertBasicArgs(args);        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        double ps = 0.1;
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        SAVG.setSeed(seed);
        if(args.length == 4)
        {
            ps = Parameters.assertExtendedArgAsDouble(args,3,"% of prototypes selected", 0, 100);
            ps /= 100.0;
        }
        SAVG generator = new SAVG(training,ps);
        
    	PrototypeSet resultingSet = generator.execute();
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);        
    }

}


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

package keel.Algorithms.Instance_Generation.LVQ;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.utilities.Distance;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * LVQ2 algorithm
 * @author diegoj
 */
public class LVQ2 extends LVQ1
{
    /** Default value of the window width parameter */
    public final double DEFAULT_WINDOW_WIDTH = 0.2;
    
    /** Index of the window width parameter */
    //protected static final int IWINDOW = 4;
    
    /** Window width parameter */
    protected double windowWidth = DEFAULT_WINDOW_WIDTH;
    
    /** Window width lower bound */
    protected double windowLowerBound = DEFAULT_WINDOW_WIDTH;
    
    /**
     * Constructs a new LVQ2 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param nProt Number of prototypes to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     * @param windowWidth Window width parameter.
     */
    public LVQ2(PrototypeSet traDataSet, int it, int nProt, double alpha_0, double windowWidth)
    {
        super(traDataSet, it, nProt, alpha_0);
        algorithmName = "LVQ2";
        this.windowWidth = windowWidth;
        this.windowLowerBound = (1.0 - windowWidth) / (1.0 + windowWidth);
    }
    
    
    /**WITH INITIAL CODE-BOOKS
     * Constructs a new LVQ2 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param nProt Number of prototypes to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     * @param windowWidth Window width parameter.
     */
    public LVQ2(PrototypeSet InitialSet,PrototypeSet traDataSet, int it, int nProt, double alpha_0, double windowWidth)
    {
        super(InitialSet,traDataSet, it, nProt, alpha_0);
        algorithmName = "LVQ2";
        this.windowWidth = windowWidth;
        this.windowLowerBound = (1.0 - windowWidth) / (1.0 + windowWidth);
    }
    
    /**
     * Constructs a new LVQ2 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param pcNProt Number of prototypes to be returned expressed as % of training size.
     * @param alpha_0 Alpha algorithm parameter.
     * @param windowWidth Window width parameter.
     */
    public LVQ2(PrototypeSet traDataSet, int it, double pcNProt, double alpha_0, double windowWidth)
    {
        super(traDataSet, it, pcNProt, alpha_0);
        algorithmName = "LVQ2";
        this.windowWidth = windowWidth;
        this.windowLowerBound = (1.0 - windowWidth) / (1.0 + windowWidth);
    }
    
    /**
     * Constructs a new LVQ2 algorithm.
     * @param traDataSet Training data prototypes
     * @param par Parameters of the algorithm.
     */ 
    public LVQ2(PrototypeSet traDataSet, Parameters par)
    {
        super(traDataSet, par);
        algorithmName = "LVQ2";        
        this.windowWidth = par.getNextAsDouble();
        this.windowLowerBound = (1.0 - windowWidth) / (1.0 + windowWidth);
    }
    
    /**
     * Informs if a prototype an its two nearest neighbors of the same and different class are "inside" the window.
     * @param i Prototype.
     * @param sameClass_i Nearest prototype to i whit its same class.
     * @param differentClass_i Nearest prototype to i whith different class.
     * @return TRUE if is inside the window, FALSE in other chase.
     */
    boolean isInsideTheWindow(Prototype i, Prototype sameClass_i, Prototype differentClass_i)
    {
        if(i==null || sameClass_i==null || differentClass_i==null)
            return false;
        double val1 = Distance.d(i,differentClass_i) / Distance.d(i,sameClass_i);
        double val2 = 1.0/val1;
        return (Math.min(val1,val2) > windowLowerBound);
    }
    
    /**
    * Corrects the instance using a particular method
    * @param i is a instance of the instance set. IS MODIFIED.
    * @param tData is the training data set.    
    */
    @Override
    protected void correct(Prototype i, PrototypeSet tData)
    {
        double class_i = i.getOutput(0);
        PrototypeSet nearests = KNN.knn(i, tData, 2);
        Prototype nearestSameClass = KNN.getNearestWithSameClassAs(i, tData);
        double nearestClass = nearests.get(0).label();
        if(isInsideTheWindow(i,nearests.get(0),nearests.get(1)))
            if(class_i != nearestClass)
            {
                penalize(nearests.get(0),i);
                if(nearestSameClass!=null)
                    reward(nearestSameClass,i);
            }
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich will contain the test data set
     * 3: k Number of neighbors used in the KNN function
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("LVQ2", "<seed> <number of iterations> <% of prototypes> <alpha_0> <window width>");
        Parameters.assertBasicArgs(args);        
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,Parameters.SEED_TXT,0,Long.MAX_VALUE);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        double pcNprot = Parameters.assertExtendedArgAsDouble(args,4,Parameters.PERC_SIZE_TXT, 0, 100);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0, 1);
        double window_width = Parameters.assertExtendedArgAsDouble(args,6,"window width", 0, 1);
        
        LVQ2.setSeed(seed);
        LVQ2 generator = new LVQ2(training, iter, pcNprot, alpha_0,window_width);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
    }
}


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
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;

/**
 * LVQ3 algorithm for reduction prototype sets.
 * @author diegoj
 */
public class LVQ3 extends LVQ2
{
    /** Default value for the epsilon constant */
    public final double DEFAULT_EPSILON = 0.1;
    
    //protected static final int IEPSILON = 5;
    
    /** Epsilon constant (multiplier of the window width) */
    protected double epsilon = DEFAULT_EPSILON;
    
    /** Epsilon times alpha constant */
    protected double epsilonTimesAlpha_0 = DEFAULT_EPSILON;
    
    /**
     * Construct a new LVQ3 algorithm.
     * @param tDataSet Training data set.
     * @param iter Number of iteratios of the algorithm.
     * @param nProt Number of prototypes generated.
     * @param alpha_0 Alpha constant.
     * @param windowWidth Window width constant.
     * @param epsilon Epsilon constant.
     */
    public LVQ3(PrototypeSet tDataSet, int iter, int nProt, double alpha_0, double windowWidth, double epsilon)
    {
        super(tDataSet, iter, nProt, alpha_0, windowWidth);        
        algorithmName = "LVQ3";
        this.epsilon = epsilon;
        this.epsilonTimesAlpha_0 = epsilon * alpha_0;
    }
    
    /**WITH INITIAL CODE-BOOKS
     * Construct a new LVQ3 algorithm.
     * @param tDataSet Training data set.
     * @param iter Number of iteratios of the algorithm.
     * @param nProt Number of prototypes generated.
     * @param alpha_0 Alpha constant.
     * @param windowWidth Window width constant.
     * @param epsilon Epsilon constant.
     */
    public LVQ3(PrototypeSet InitialSet,PrototypeSet tDataSet, int iter, int nProt, double alpha_0, double windowWidth, double epsilon)
    {
        super(InitialSet,tDataSet, iter, nProt, alpha_0, windowWidth);        
        algorithmName = "LVQ3";
        this.epsilon = epsilon;
        this.epsilonTimesAlpha_0 = epsilon * alpha_0;
    }
    
    /**
     * Construct a new LVQ3 algorithm.
     * @param tDataSet Training data set.
     * @param iter Number of iteratios of the algorithm.
     * @param pcNprot Number of prototypes generated as percentage of training size.
     * @param alpha_0 Alpha constant.
     * @param windowWidth Window width constant.
     * @param epsilon Epsilon constant.
     */
    public LVQ3(PrototypeSet tDataSet, int iter, double pcNprot, double alpha_0, double windowWidth, double epsilon)
    {
        super(tDataSet, iter, pcNprot, alpha_0, windowWidth);        
        algorithmName = "LVQ3";
        this.epsilon = epsilon;
        this.epsilonTimesAlpha_0 = epsilon * alpha_0;
    }
    
    /**
     * Construct a new LVQ3 algorithm.
     * @param tDataSet Training data set.
     * @param par Parameters of the algorithm.
     */
    public LVQ3(PrototypeSet tDataSet, Parameters par)
    {
        super(tDataSet, par);
        algorithmName = "LVQ3";
        this.epsilon = par.getNextAsDouble();
        this.epsilonTimesAlpha_0 = epsilon * alpha_0;
    }
    
    /**
     * Applies LVQ3-reward to prototype m
     * @param m Rewarded prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    @Override
    protected void reward(Prototype m, Prototype x)
    {
        //m.set(m.addMul(x.sub(m),alpha_0*epsilon));
       //  m.set(m.add((x.sub(m)).mul(epsilonTimesAlpha_0)));
    	Prototype term = x.sub(m);
    	term = term.mul(this.alpha_0); // SINO SOLO ALPHA
    	m.set(m.add(term));    
    }
    
    /**
     * USING EPSILON parameter.
     * @param m
     * @param x
     */
    protected void reward2(Prototype m, Prototype x) 
    {
        //m.set(m.addMul(x.sub(m),alpha_0*epsilon));
       //  m.set(m.add((x.sub(m)).mul(epsilonTimesAlpha_0)));
    	Prototype term = x.sub(m);
    	term = term.mul(epsilonTimesAlpha_0);
    	m.set(m.add(term));    
    }
    
    /**
     * Applies LVQ3-penalization to prototype m
     * @param m Penalized prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    @Override
    protected void penalize(Prototype m, Prototype x)
    {
        //m.set(m.subMul(x.sub(m),alpha_0*epsilon));
        //m.set(m.sub((x.sub(m)).mul(epsilonTimesAlpha_0)));
    	Prototype term = x.sub(m);
    	term = term.mul(epsilonTimesAlpha_0);
    	m.set(m.sub(term)); 
    }
    
    /**
    * Corrects the instance using a particular method
    * @param x is a instance of the instance set. IS MODIFIED.
    * @param tData is the training data set.    
    */    
    @Override
    protected void correct(Prototype x, PrototypeSet tData)
    {
        //PrototypeSet nearests = KNN.knn(x, tData, 2);
        Prototype uno = tData.nearestTo(x);
        PrototypeSet dosTdata = tData.without(uno);
    	Prototype dos = dosTdata.nearestTo(x);
    	
    	double clase_x = x.getOutput(0);
        double clase_0 = uno.getOutput(0);
        double clase_1 = dos.getOutput(0);
        
        if(clase_x == clase_0 && clase_x == clase_1)
        {
                reward2(uno,x);
                reward2(dos,x);
        }        
        if(isInsideTheWindow(x,uno,dos))
        {
            if(clase_x == clase_0 && clase_x != clase_1)
            {
                reward(uno,x);
                penalize(dos,x);
            }
            else if(clase_x == clase_1)
            {
                reward(dos,x);
                penalize(uno,x);
            }
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
        Parameters.setUse("LVQ3", "<seed> <number of iterations> <% of prototypes> <alpha_0> <window width> <epsilon>");
        Parameters.assertBasicArgs(args);        
        
        //int k = 3;
                
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        int n_prot = Parameters.assertExtendedArgAsInt(args,4,"number of prototypes", 1, training.size()-1);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0, 1);
        double wind =  Parameters.assertExtendedArgAsDouble(args,6,"window width", 0, 1);
        double epsilon =  Parameters.assertExtendedArgAsDouble(args,7,"epsilon", 0, 1);
        
        //PrototypeSet trainingDataSet, int iterations, double alpha_0, double windowWidth, double epsilon)
        LVQ3.setSeed(seed);
        LVQ3 generator = new LVQ3(training, iter, n_prot, alpha_0, wind, epsilon);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, k, test);
    }
}


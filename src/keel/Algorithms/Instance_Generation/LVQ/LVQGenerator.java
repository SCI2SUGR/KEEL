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
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.KNN;
import keel.Dataset.*;
import java.util.*;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * <p>
 * <b> LVQGenerator </b>
 * </p>
 *
 * Abstract class parent of every LVQGenerator-type algorithm.
 *
 * @author Diego J. Romero LÃ³pez
 * @version keel0.1
 * @see Instance
 * @see InstanceSet 
 */
public abstract class LVQGenerator extends PrototypeGenerator
{
    /** Number of iterations of the algorithm.*/
    protected int iterations = 10;
    
    /** Number of prototypes of the resulting set. */
    protected int numberOfPrototypesGenerated = 10;
    
    protected boolean initialset = false;
    protected PrototypeSet initial;
    
    /**
     * Constructs a new LVQGenerator algorithm. NOTE: this class is abstract.
     * @param _trainingDataSet
     * @param iterations Iterations that will execute the algorithm.
     * @param n Number of prototypes to be returned.
     */
    public LVQGenerator(PrototypeSet _trainingDataSet, int iterations, int n)
    {
        super(_trainingDataSet);     
        algorithmName = "LVQ";//Name of the algorithm
        this.iterations = iterations;
        this.numberOfPrototypesGenerated = n;
    }
    
    /** WITH INITIAL CODE-BOOKS
     * Constructs a new LVQGenerator algorithm. NOTE: this class is abstract.
     * @param _trainingDataSet
     * @param iterations Iterations that will execute the algorithm.
     * @param n Number of prototypes to be returned.
     */
    public LVQGenerator(PrototypeSet InitialSet,PrototypeSet _trainingDataSet, int iterations, int n)
    {
        super(_trainingDataSet);     
        initialset = true;
        initial = InitialSet;
        algorithmName = "LVQ";//Name of the algorithm
        this.iterations = iterations;
        this.numberOfPrototypesGenerated = n;
    }
    
    /**
     * Constructs a new LVQGenerator algorithm. NOTE: this class is abstract.
     * @param _trainingDataSet
     * @param iterations Iterations that will execute the algorithm.
     * @param percRed Percentage of the training size that will have the reduced set.
     */
    public LVQGenerator(PrototypeSet _trainingDataSet, int iterations, double percRed)
    {
        super(_trainingDataSet);     
        algorithmName = "LVQ";//Name of the algorithm
        this.iterations = iterations;
        this.numberOfPrototypesGenerated = this.getSetSizeFromPercentage(percRed);
    }
    
    /**
     * Constructs a new LVQGenerator algorithm. NOTE: this class is abstract.
     * @param _trainingDataSet
     * @param parameters Parameters of the algorithm (number of iterations [integer], % of reduced size [double in (0-100]])
     */
    public LVQGenerator(PrototypeSet _trainingDataSet, Parameters parameters)
    {
        super(_trainingDataSet, parameters);        
        algorithmName = "LVQ";//Name of the algorithm        
        this.iterations = parameters.getNextAsInt();
        this.numberOfPrototypesGenerated = this.getSetSizeFromPercentage(parameters.getNextAsDouble());
    }
    
    /**
     * Applies a reward to prototype m
     * @param m Rewarded prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    protected abstract void reward(Prototype m, Prototype x);
    
    /**
     * Applies a penalization to prototype m
     * @param m Penalized prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    protected abstract void penalize(Prototype m, Prototype x);
    
    /**
    * Initialize the output data set    
    * @return a linked list of instances of the training instance set
    */    
    abstract PrototypeSet initDataSet();
    
    /**
    * Extracts a instance using a particular method
    * @param tData is the training data set.
    * @return a instance of the training instance set
    */    
    abstract Prototype extract(PrototypeSet tData);

    /**
    * Corrects the instance using a particular method
    * @param i is a instance of the instance set. IT IS MODIFIED.
    * @param tData is the training data set.
    */
    abstract void correct(Prototype i, PrototypeSet tData);

    /**
    * Execute the method and returns the condensed prototype set.
    * @return Prototype set modified from the training instance set by a LVQGenerator method.
    */
    @Override
    public PrototypeSet reduceSet() 
    {
        PrototypeSet outputDataSet = initDataSet();     
        
        //outputDataSet.print();
        int it=0;
        while(it<iterations)
        {
            //Debug.errorln("Iteration " + it);
            Prototype instance = extract(trainingDataSet);
            correct(instance, outputDataSet);            
            ++it;
        }
        //outputDataSet.applyThresholds();
        
		   PrototypeSet nominalPopulation = new PrototypeSet();
           nominalPopulation.formatear(outputDataSet);
		// System.err.println("\n% de acierto en training " + KNN.classficationAccuracy(nominalPopulation,trainingDataSet,1)*100./trainingDataSet.size() );
		  
        return outputDataSet;        
    }
}


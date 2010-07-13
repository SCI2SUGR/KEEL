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
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.*;
import keel.Dataset.*;
import java.util.*;
import org.core.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * <p>
 * <b> LVQ1 </b>
 * </p>
 *
 * Implements LVQ1 algorithm.
*/
public class LVQ1 extends LVQGenerator
{
    /** Default alpha_0 learning parameter of the algorithm LVQ1 */
    protected static double ALPHA_DEFAULT_VALUE = 0.01;
    
    /** Alpha parameter. Factor to the rewards and penalizations. */
    protected double alpha_0 = ALPHA_DEFAULT_VALUE;
    
    /** Index of the alpha parameter. */
    protected static final int IALPHA = 3;
    
    /** Random number index */
    protected int chosenRandomIndex = 0;
    
    /** Random number list of indexes */
    protected ArrayList<Integer> randomIndexes = null;
    
    /**
     * Constructs a new LVQ1 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param numProt Number of prototypes to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     */
    public LVQ1(PrototypeSet traDataSet, int it, int numProt, double alpha_0)
    {
        super(traDataSet, it, numProt);
        algorithmName = "LVQ1";//Name of the algorithm
        this.alpha_0 = alpha_0;
        chosenRandomIndex = 0;
        int data_size = traDataSet.size();        
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, data_size-1);
    }
    
    /** WITH INITIAL CODE-BOOKS
     * Constructs a new LVQ1 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param numProt Number of prototypes to be returned.
     * @param alpha_0 Alpha algorithm parameter.
     */
    public LVQ1(PrototypeSet InitialSet,PrototypeSet traDataSet, int it, int numProt, double alpha_0)
    {
        super(InitialSet,traDataSet, it, numProt);
        algorithmName = "LVQ1";//Name of the algorithm
        this.alpha_0 = alpha_0;
        chosenRandomIndex = 0;
        int data_size = traDataSet.size();        
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, data_size-1);
    }
    
    /**
     * Constructs a new LVQ1 algorithm.
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.
     * @param percNumProt Number of prototypes to be returned expressed as % of training size.
     * @param alpha_0 Alpha algorithm parameter.
     */
    public LVQ1(PrototypeSet traDataSet, int it, double percNumProt, double alpha_0)
    {
        super(traDataSet, it, percNumProt);
        algorithmName = "LVQ1";//Name of the algorithm
        this.alpha_0 = alpha_0;
        chosenRandomIndex = 0;
        int data_size = traDataSet.size();        
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, data_size-1);
    }


    /**
     * Constructs a new LVQ1 algorithm.
     * @param traDataSet Training data prototypes
     * @param parameters Parameters of the algorithm.
     */    
    public LVQ1(PrototypeSet traDataSet, Parameters parameters)
    {
        super(traDataSet, parameters);
        algorithmName = "LVQ1";//Name of the algorithm
        this.alpha_0 = parameters.getNextAsDouble();
        chosenRandomIndex = 0;
        int data_size = traDataSet.size();
        randomIndexes = RandomGenerator.generateDifferentRandomIntegers(0, data_size-1);
    }
    
    /**
     * Applies a reward to prototype m
     * @param m Rewarded prototype (nearest to x). IT IS MODIFIED.
     * @param x Original prototype.
     */
    protected void reward(Prototype m, Prototype x)
    {
        //m.set(m.addMul(x.sub(m), alpha_0));
        m.set(m.add((x.sub(m)).mul(alpha_0)));
    }
    
    /**
     * Applies a penalization to prototype m
     * @param m Penalized prototype (nearest to x). IT IS MODIFIED.
     * @param x Original prototype.
     */
    protected void penalize(Prototype m, Prototype x)
    {
        //m.set(m.subMul(x.sub(m), alpha_0));        
        m.set(m.sub((x.sub(m)).mul(alpha_0)));
    }
    
    /**
    * Initialize the output data set    
    * @return Initial prototypeSet
    */    
    protected PrototypeSet initDataSet()
    {
    	
    	if(!super.initialset){
	    	int numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
	        PrototypeSet initial = selecRandomSet(numberOfPrototypesGenerated, true);
	    	
	  	  // Aseguro que al menos hay un representante de cada clase.
	  	  PrototypeSet clases[] = new PrototypeSet [numberOfClass];
	  	  for(int i=0; i< numberOfClass; i++){
	  		  clases[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
	  	  }
	  	
	  	  for(int i=0; i< initial.size(); i++){
	  		  for(int j=0; j< numberOfClass; j++){
	  			  if(initial.getFromClass(j).size() ==0 && clases[j].size()!=0){
	  				  
	  				  initial.add(clases[j].getRandom());
	  			  }
	  		  }
	  	  }
    	
    	return initial;
    	
    	}else{
    		//System.out.println("Me dan un conjunto inicial");
    		PrototypeSet Initial = super.initial;
    		return Initial;
    	}
    }
    
    /**
    * Initialize the output data set ignoring the a priority probabilities
    * @return Initial prototypeSet
    */  
     protected PrototypeSet initDataSetRandomMode()
    {
        int number = 0;
        int numberOfInstances_1 = trainingDataSet.size()-1;        
        PrototypeSet edited = new PrototypeSet(numberOfPrototypesGenerated);
        HashSet<Integer>forbidden = new HashSet<Integer>();
        while(number < numberOfPrototypesGenerated)
        {
            int chosenLocal;
            do
            {
                chosenLocal = RandomGenerator.Randint(0, numberOfInstances_1);
            }
            while(forbidden.contains(chosenLocal));            
            forbidden.add(chosenLocal);
            
            edited.add(trainingDataSet.get(chosenLocal));
            ++number;
        }
        return edited;
    }
     
    /**
    * Extracts a instance using a particular method
    * @param tData is the training data set.
    * @return a instance of the training instance set
    */    
    protected Prototype extract(PrototypeSet tData)
    {
        int dataIndex = randomIndexes.get(chosenRandomIndex);
        Prototype res = tData.get(dataIndex);
        chosenRandomIndex = (chosenRandomIndex +1) % randomIndexes.size();
        return res;
    }

    /**
    * Corrects the instance using a particular method
    * @param i is a instance of the instance set.
    * @param tData is the training data set. IS MODIFIED.
    */
    protected void correct(Prototype i, PrototypeSet tData)
    {
        Prototype nearest = KNN._1nn(i, tData);
        double class_i = i.label();
        double class_nearest = nearest.label();
        if(class_i != class_nearest)
        {
            penalize(nearest,i);            
        }
        else
        {
            reward(nearest,i);
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
        Parameters.setUse("LVQ1", "<seed> <number of iterations> <% of prototypes> <alpha_0>");
        Parameters.assertBasicArgs(args);        
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);        
        //System.err.println(training.toString());
        //System.err.println("-------------------------------------------------");
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        double percNprot = Parameters.assertExtendedArgAsDouble(args,4,Parameters.PERC_SIZE_TXT, 0, 100);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0, 4);
        //Parameters param = new Parameters(args);
        
        LVQ1.setSeed(seed);
        LVQ1 generator = new LVQ1(training, iter, percNprot, alpha_0);
        //LVQ1 generator = new LVQ1(training, param);
        
    	PrototypeSet resultingSet = generator.execute();
        //String filename = args[0] +"_"+ resultingSet.size()+"_"+seedDefaultValueList+"_LVQ1.sel";
        //resultingSet.save(filename);
        
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        
    }
}


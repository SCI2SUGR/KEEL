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
import keel.Algorithms.Instance_Generation.utilities.*;
import org.core.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import java.util.ArrayList;
import java.util.*;


/**
 * Implements LVQTC algorithm
 * @author diegoj
 */        
public class LVQTC extends LVQ1
{
    /** Alpha R parameter */
    private double alpha_r = LVQTC.ALPHA_DEFAULT_VALUE;
    
    /** Alpha W parameter */
    private double alpha_w = LVQTC.ALPHA_DEFAULT_VALUE;
    
    //private double alphaReductionFactor = 0.001;
    
    /** Epoches of the algorithm */
    private int epoches = 4;
    
    //private static double DEFAULT_REDUCTION_SIZE=95.0;
    
    /** Threshold below that the prototype will be removed. */
    private int retentionThreshold = 3;
    
    private static ArrayList<Double> posibleClasses = null;
    
    /** Counter of times were class_i is the winner, one for prototype. */
    private HashMap<Prototype, HashMap<Double,Integer> > counter = null;
        
    /** Sum of all ocurrences */
    private HashMap<Prototype,Integer> sumCounter = null;
    
    private HashMap<Prototype,PrototypeSet> wrong = null;

    /**
     Constructor based on the training dataset and the parameters
     */
    public LVQTC(PrototypeSet traDataSet, Parameters parameters) {
        super(traDataSet, parameters);
        algorithmName="LVQTC";         
        this.alpha_r = this.alpha_0;
        this.alpha_w = parameters.getNextAsDouble();
        this.retentionThreshold = parameters.getNextAsInt();
        this.epoches = parameters.getNextAsInt();
        posibleClasses = traDataSet.getPosibleValuesOfOutput();
        //int numClasses = classes.size();
        counter = new HashMap<Prototype,HashMap<Double,Integer> >();
        sumCounter = new HashMap<Prototype,Integer>();
        wrong = new HashMap<Prototype,PrototypeSet>();
    }    
    
    /**
     Constructor based on the training dataset and the parameters
     * @param traDataSet Training data prototypes
     * @param it Number of iterations that will execute the algorithm.     
     * @param percProts New size of the set (% of training data set).
     * @param alpha_r Alpha algorithm parameter.
     * @param alpha_w Alpha algorithm parameter.
     * @param T Retention threshold of the algorithm. 
     */
    public LVQTC(PrototypeSet traDataSet, int it, double percProts, double alpha_r, double alpha_w, int T, int epoches) {
        super(traDataSet, it, percProts, alpha_r);
        algorithmName="LVQTC";
        this.alpha_r = alpha_r;
        this.alpha_w = alpha_w;
        this.retentionThreshold = T;
        this.epoches = epoches;
        posibleClasses = traDataSet.getPosibleValuesOfOutput();
        //int numClasses = classes.size();
        counter = new HashMap<Prototype,HashMap<Double,Integer> >();
        sumCounter = new HashMap<Prototype,Integer>();
        wrong = new HashMap<Prototype,PrototypeSet>();
        //veremos a ver quÃ© pollas hacemos
    }
    
    //Inicializa el contador para el prototypo i
    protected void initCounterOf(Prototype i)
    {
        counter.put(i, new HashMap<Double,Integer>());        
        //for each posible class set the counter = 0
        for(Double d : posibleClasses)
            counter.get(i).put(d,0);//Todos a 0
        sumCounter.put(i, -1);//Sum of all counter for i is -1
    }
    
    private void reset(PrototypeSet data)
    {
        for(Prototype p : data)
        {
            initCounterOf(p);
            wrong.put(p, new PrototypeSet());//no idea    
        }
    }
    
    private int sum(HashMap<Double,Integer> v)
    {
        ArrayList<Integer> values = new ArrayList<Integer>(v.values());
        int acc = 0;
        for(Integer i : values)
            acc += i;
        return acc;
    }
    
    private int sumOfCounterOf(Prototype p)
    {
        int value = 0;
        Debug.force(sumCounter.containsKey(p), "ERROR en sumOfCounter");
        if(sumCounter.get(p)==-1)
        {
            int _sum = sum(counter.get(p));            
            sumCounter.put(p, _sum);
            value = _sum;
        }
        else
        {
            value = sumCounter.get(p);
        }
        return value;
    }
    
    private Pair<Boolean,Double> maximumWrongClassCounter(Prototype p)
    {
        HashMap<Double,Integer> h = counter.get(p);
        ArrayList<Double> list = new ArrayList<Double>(h.keySet());
        double classWrong = p.label();
        int max = retentionThreshold;
        boolean found = false;
        for(Double klass : list)
            if(klass != p.assignedClass() && h.get(klass)>max)
            {
                classWrong = klass;
                max = h.get(klass);
                found = true;
            }        
        return new Pair<Boolean,Double>(found,classWrong);
    }
    
    /**
     * Increment the counter of a prototype for a selected class
     * @param i Prototype which class-ocurrences-counter will be modified. It should to be nearest prototype to the training prototype (in Kohonen's notation m_c).
     * @param _class Class which ocurrences will be incremented. It should to be the training prototype (in Kohonen's notation x).
     */
     private void incrementCounterOf(Prototype i, double _class)
    {
        Debug.force(counter.containsKey(i), "No contiene la clave");
         int oldValue = counter.get(i).get(_class);
        counter.get(i).put(_class, oldValue+1);
    }
    
    /*void updateCounterSum()
    {
        ArrayList<Prototype> list = new ArrayList<Prototype>(counter.keySet());
        for(Prototype p : list)
            counterSum.put(p, sum(counter.get(p)));//optimizable??
    }*/
    
    /**
     * Applies the LVQTC reward to prototype m
     * @param m Rewarded prototype (nearest to x). IT IS MODIFIED.
     * @param x Original prototype.
     */
    @Override
    protected void reward(Prototype m, Prototype x)
    {
        int q_i = sumOfCounterOf(m);
        Debug.force(q_i>0,"CERAPIO en reward");
        //System.out.println("ExisteR: " + counter.get(m));
        //System.out.println("SumR: " + q_i);
        //int q_i = 1;
        m.set(m.add((x.sub(m)).mul(alpha_r/q_i)));
    }
    
    /**
     * Applies LVQTC penalization to prototype m
     * @param m Penalized prototype (nearest to x). IT IS MODIFIED.
     * @param x Original prototype.
     */
    @Override
    protected void penalize(Prototype m, Prototype x)
    {
        int q_i = sumOfCounterOf(m);
        Debug.force(q_i>0,"CERAPIO en penalize");
        //System.out.println("ExisteP: " + counter.get(m));
        //System.out.println("SumP: " + q_i);
        //int q_i = 1;
        m.set(m.sub((x.sub(m)).mul(alpha_w/q_i)));
    }   
   
    void updateCentroidOfWrongClass(Prototype p, Prototype newWrong)
    {
        PrototypeSet oldSet = wrong.get(p);
        oldSet.add(newWrong);
        wrong.put(p, oldSet);//updates wrong centroid
    }
    
    /**
    * Corrects the instance using a particular method
    * @param i is a instance of the instance set.
    * @param tData is the training data set. IS MODIFIED.
    */
    @Override
    protected void correct(Prototype i, PrototypeSet tData)
    {
        Prototype nearest = KNN._1nn(i, tData);        
        double i_label = i.label();
        incrementCounterOf(nearest, i_label);
        /*if(nearest==null)
        {
            System.out.println("La correccion ha petao");
            System.exit(-1);
        }*/
        
        double nearest_prot_label = nearest.label();
        //Incrementa el contador del training vector (de nearest)        
        if(i_label != nearest_prot_label)
        {
            penalize(nearest,i);
            updateCentroidOfWrongClass(nearest,i);
            //wrong.put(nearest,i);//aÃ±adimos a wrong
        }
        else
        {            
            reward(nearest,i);            
        }
    }
    
    
    protected PrototypeSet neuronPruning(PrototypeSet data)
    {
        //Eliminamos las neuronas que tengan la suma de sus contadores menor que
        //el retentionThreshold
        PrototypeSet edited = new PrototypeSet();
        Prototype pMC = null;
        int mc = 0;        
        for(Prototype p : data)
        {
            int currentCounter = sum(counter.get(p));
            //Debug.println("Counter " + currentCounter);
            if(currentCounter>=retentionThreshold)
                edited.add(p);
            if(mc<currentCounter)
            {
                mc = currentCounter;
                pMC = p;
            }
        }
        //System.out.println("Data tenÃ­a " + data.size());
        //System.out.println("Edited tiene " + edited.size());
        if(edited.size() == 0)//
            edited.add(pMC);  //
        return edited;
        //return data;
    }
    
    protected PrototypeSet neuronCreation(PrototypeSet data)
    {
        PrototypeSet newPrototypes = new PrototypeSet();
        for(Prototype p : data)
        {
            Pair<Boolean, Double> isWrong = maximumWrongClassCounter(p);
            if(isWrong.first())
            {
                Prototype w = (wrong.get(p)).avg();//TO DO make wrong
                w.setLabel(isWrong.second());
                newPrototypes.add(w);
            }
        }
        
        for(Prototype newP : newPrototypes)
            data.add(newP);
        
        return data;
    }
    
    protected PrototypeSet doEpoche(PrototypeSet outputDataSet)
    {
        int it=0;
        while(it<iterations)
        {
            Prototype instance = extract(trainingDataSet);            
            correct(instance, outputDataSet);
            //Debug.println("Iteration " + it);
            ++it;
        }
        return outputDataSet;
    }
    
    /**
    * Execute the method and returns the output instance set
    * @return a instance set modified from the training instance set by a LVQ method
    */
    @Override
    public PrototypeSet reduceSet() 
    {
        PrototypeSet outputDataSet = initDataSet();
        //for(Prototype p : outputDataSet)
        //    initCounterOf(p);
        int e=0;
        while(e < epoches)
        {
            reset(outputDataSet);
            outputDataSet = doEpoche(outputDataSet);
            outputDataSet = neuronPruning(outputDataSet);//eliminamos algunas neuronas
            outputDataSet = neuronCreation(outputDataSet);//aÃ±adimos algunas neuronas
          //  Debug.println("Epoch number " + e);
            ++e;
            //reset(outputDataSet);
        }
        //outputDataSet.applyThresholds();
        return outputDataSet;
        //return initDataSet()
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
        Parameters.setUse("LVQTC", "<seed> <iterations per epoch> <% of prots> <alpha_r> <alpha_w> <retention threshold> <number of epoches>");
        Parameters.assertBasicArgs(args);        
        Debug.setStdDebugMode(false);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations per epoch", 1, Integer.MAX_VALUE);
        double pcProt = Parameters.assertExtendedArgAsDouble(args,4,"% of prototypes", 0, 100);
        double alphaR = Parameters.assertExtendedArgAsDouble(args,5,"alpha_r", 0, 1);
        double alphaW =  Parameters.assertExtendedArgAsDouble(args,6,"alpha_w", 0, 1);
        int Q =  Parameters.assertExtendedArgAsInt(args,7,"retention threshold (Q)", 1, Integer.MAX_VALUE);        
        int epoches = Parameters.assertExtendedArgAsInt(args,8,"number of epoches of the algorithm",1,Integer.MAX_VALUE);        
        

        //PrototypeSet trainingDataSet, int iterations, double alpha_0, double windowWidth, double epsilon)
        LVQTC.setSeed(seed);
        LVQTC generator = new LVQTC(training, iter, pcProt, alphaR, alphaW, Q, epoches);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, k, test);
    }

}


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
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.BasicMethods.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Dataset.*;
import java.util.*;
import org.core.*;

/**
 * Implements LVQPRU algorithm
 * @author diegoj
 */
public class LVQPRU extends PrototypeGenerator
{
    /** Minimum class part set.  */
    protected static int MINIMUM_CLASS_SET_SIZE = 2;
    
    // Minimum reduced set. 
    //protected static int MINIMUM_SET_SIZE = 2;
    
    /** Percentage of prototypes for each class in initial reduction. */
    protected double percentageOfPrototypesPerClass = 10.0;
    
    /** Percentage of prototypes. */
    protected double percentageOfPrototypes = 10.0;
    
    /** Number of prototypes that will be generated. */
    protected int numberOfPrototypes = 10;
    
    /** Number of iterations of the LVQPRU algorithm. */
    protected int numberOfIterations = 1000;
    
    /** Window width of the LVQ2.1 algorithm. */
    protected double windowWidth = 0.1;
    
    /** Number of iterations of the internal LVQ2.1 mapping. */
    protected int numberOfIterationsLVQ2_1 = 10;
    
    /** Alpha constant of the internal LVQ2.1 mapping. */
    protected double alpha_0 = 0.01;
    
    /** Size of the neighborhood used in KNN */
    protected int k = 2;
    
       /**
    * Performs a LVQ1-reduction of the set.
    * @param set Set to be reduced.
    * @return Reduced set by the LVQ1 method.
    */
    private PrototypeSet makeInitialReductionPerClass(PrototypeSet set)
    {
        if(set.size()<=2)
        {
            PrototypeSet result = new PrototypeSet();
            result.add(set.avg());
            return result;
        }
        
        int nP = getSetSizeFromPercentage(set, this.percentageOfPrototypesPerClass);
        nP = Math.max(nP, MINIMUM_CLASS_SET_SIZE);
        //Debug.errorln("Set size (en makeLVQ1Reduction) " + set.size());
        //Debug.errorln("Num prots " + nP);
        //Debug.errorln("numIter " + numberOfIterations);
        //Debug.errorln("numProt " + numberOfPrototypes);
        //Debug.errorln("alpha " + alpha_0);
        //Debug.errorln("window width " + windowWidth);
        LVQ1 lvq1 = new LVQ1(set, numberOfIterations, nP, alpha_0);
        PrototypeSet reducedByLVQ1 = lvq1.reduceSet();
        return reducedByLVQ1;
    }
    
    /**
     * Performs a LVQ2.1-reduction of the set.
     * @param set Set to be reduced.
     * @return Reduced set by the LVQ2.1 method.
     */
    private PrototypeSet makeLVQ2_1Reduction(PrototypeSet set)
    {
        if(set.size()<2)
        {
            PrototypeSet result = new PrototypeSet();
            result.add(set.avg());
            return result;
        }
            
        //Debug.errorln("Set size (en makeLVQ2_1Reduction) " + set.size());
        //Debug.errorln("numIter " + numberOfIterations);
       // Debug.errorln("numProt " + numberOfPrototypes);
        //Debug.errorln("alpha " + alpha_0);
        //Debug.errorln("window width " + windowWidth);
        int numP = Math.min(set.size(), numberOfPrototypes);
        //Debug.errorln("LVQ2.1 reduce con " + numP);
        LVQ2_1 lvq2_1 = new LVQ2_1(set, numberOfIterations, numP, alpha_0, windowWidth);
        PrototypeSet reducedByLVQ2_1 = lvq2_1.reduceSet();
        return reducedByLVQ2_1;
    }
    
    /**
     * Constructs a new LVQPRU algorithm.
     * @param _trainingDataSet
     * @param parameters
     */
    public LVQPRU(PrototypeSet _trainingDataSet, Parameters parameters)
    {
        super(_trainingDataSet, parameters);
        this.algorithmName="LVQPRU";
        numberOfIterations = parameters.getNextAsInt();
        percentageOfPrototypesPerClass = parameters.getNextAsDouble();
        percentageOfPrototypes = parameters.getNextAsDouble();
        numberOfPrototypes = this.getSetSizeFromPercentage(percentageOfPrototypes);
        numberOfIterationsLVQ2_1 = parameters.getNextAsInt();
        alpha_0 = parameters.getNextAsDouble();
        windowWidth = parameters.getNextAsDouble();
        k = parameters.getNextAsInt();
    }

    /**
     * Builds a LVQPRU algorithm.
     * @param numIter Number of iterations of the process.
     * @param pcNpc Percentage of number of prototypes per class. Used in initial reduction.
     * @param pcN Percentage of number of prototypes.
     * @param numIterLVQ2_1 Number iterations performed by the LVQ2.1 algorithm.
     * @param a Alpha parameter of the LVQ2.1 algorithm.
     * @param w Window width of the LVQ2.1 associated algorithm.          
     * 
     */
    public LVQPRU(PrototypeSet _trainingDataSet, int numIter, double pcNpc, double pcN, int numIterLVQ2_1, double a, double w, int k)
    {
        super(_trainingDataSet);
        numberOfIterations = numIter;
        //int minNpc = minimumNumberOfPrototypesPerClass();
        //numberOfInitialPrototypesPerClass = Npc;
        percentageOfPrototypesPerClass = pcNpc;
        percentageOfPrototypes = pcN;
        //if(Npc > minNpc)
        //    numberOfInitialPrototypesPerClass = minNpc;
        numberOfIterationsLVQ2_1 = numIterLVQ2_1;
        alpha_0 = a;
        windowWidth = w;        
        this.algorithmName="LVQPRU";
        this.k = k;
    }
    
    /**
     * Returns the index of the minimum element of an array.
     * @param array ArrayList.
     * @return Index of the minimum value of array.
     */
    protected static int indexOfMinElement(ArrayList<Double> array)
    {
        double min = array.get(0);
        int minIndex = 0;
        int i=0;
        for(double d : array)
        {
            if(d < min)
            {
                min = d;
                minIndex = i;
            }
            ++i;
        }
        return minIndex;
    }
    
    /*private void print(PrototypeSet R)
    {
        HashMap<Double,Integer> f = R.getFrequencyOfClasses();
        ArrayList<Double> s = new ArrayList<Double>(f.keySet());
        for(double c : s)
            Debug.errorln("De clase " + c + " hay " + f.get(c));
    
    
    }*/
    
    /**
    * Execute the method LVQPRU and returns the condensed set
    * @return Prototype set modified from the training data set by a LVQPRU method.
    */
    @Override
    public PrototypeSet reduceSet()
    {
        PrototypeSet T = trainingDataSet.copy();
        //Debug.errorln("Empezamos");
        //ArrayList<Double> classes = T.nonVoidClasses();
        ArrayList<PrototypeSet> parts = T.classPartition();
        ArrayList<PrototypeSet> reducedParts = new ArrayList<PrototypeSet>();
        for(PrototypeSet ps : parts)
        {
            PrototypeSet LVQ1reduced = makeInitialReductionPerClass(ps);
            reducedParts.add(LVQ1reduced);
            //Debug.errorln("Parte tiene " + ps.size() + " elementos");
            //Debug.errorln("Reduced tiene " + LVQ1reduced.size() + " elementos");
            //print(LVQ1reduced);
        }
        
        // Reduce the set, erasing noisy prototypes and relabeling wrong prots'
        PrototypeSet R = new PrototypeSet(reducedParts);
        //Debug.errorln("Tenemos al colega R con " + R.size() + " prototypos");
        
        PrototypeSet newR = new PrototypeSet();
        for(Prototype p : R)
        {
            PrototypeSet nn = KNN.getNearestNeighbors(p, trainingDataSet, k);            
            PrototypeSet nnWithSameClass = nn.getFromClass(p.label());
            if(nnWithSameClass.size() > 0)
                newR.add(p);
            else if(nn.size()>0)
            {
                double newClass = nn.mostFrequentClass();
                p.setClass(newClass);
                newR.add(p);
            }            
        }
        
        R = newR;
        
       // print(R);
        
        //Debug.errorln("Hemos llegao a la reducciÃ³n del LVQ2.1");
        R = makeLVQ2_1Reduction(R);        
        
        //Debug.errorln("Hemos llegau a CNN");
        //Condensing
        CNN.makeReductionOf(R);
        
        boolean end = false;
        int acc = LVQPRU.absoluteAccuracy(R, trainingDataSet);

        //Prototype chosen = null;
        ArrayList<Double> E = new ArrayList<Double>(R.size());
        int R_size = R.size();
        for (int i = 0; i < R_size; ++i)
            E.add(0.0);

        int index = 0;
        for (Prototype p : R)
            p.setIndex(index++);
        int iterations = 0;
        do
        {
            //Debug.errorln(index + " iteration");
            for(Prototype p : R)
            {
                PrototypeSet nn = KNN.getNearestNeighbors(p, R, 2);
                double l = nn.get(0).label();
                double m = nn.get(1).label();
                double n = p.label();                
                int i = nn.get(0).getIndex();
                if(n == l && n!=m)
                    E.set(i, E.get(i)+1);
                if(n!=l && n==m)
                    E.set(i,E.get(i)-1);
                ++i;
            }
            
            int iE = indexOfMinElement(E);
            Prototype erroneus = R.get(iE);
            //PrototypeSet R2 = new PrototypeSet(R);
            R.remove(iE);
            //R2 = makeLVQ2_1Reduction(R);
           
            int currAcc = LVQPRU.absoluteAccuracy(R, trainingDataSet);
            end = currAcc < acc;
            //Debug.errorln("Current Acc " + currAcc + " < " + acc + "? " + end);
            if(!end)
            {
                acc = currAcc;
                R = makeLVQ2_1Reduction(R);     
            }
            else
            {
                R.add(erroneus);
            }    
            ++iterations;            
        }
        while(!end && (iterations > numberOfIterations));
        //R.applyThresholds();
        return R;
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
        Parameters.setUse("LVQPRU", "<seed> <number of iterations> <percentage of prots. by class> <percentage of prots> <number of iterations LVQ2_1> <WindowWidth> <alpha_0> <k (of KNN)>");
        Parameters.assertBasicArgs(args);        
        //Debug.setStdDebugMode(true);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        //int numIter, int Npc, int numIterLVQ2_1, double w, double a
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        LVQPRU.setSeed(seed);
        int numIter = Parameters.assertExtendedArgAsInt(args,3,"iterations of optimal-LVQ3 reduction", 1, Integer.MAX_VALUE);
        double pcNpc = Parameters.assertExtendedArgAsDouble(args,4,"percentage of number of prototypes per class", 0, 100);
        double pcN = Parameters.assertExtendedArgAsDouble(args,5,"percentage of number of prototypes", 0, 100);
        int numIterLVQ2_1 = Parameters.assertExtendedArgAsInt(args,6,"iterations of LVQ2.1 reduction", 1, Integer.MAX_VALUE);
        double a = Parameters.assertExtendedArgAsDouble(args,7,"alpha0 parameter of the LVQ2.1 internal reduction", 0, 1);
        double w = Parameters.assertExtendedArgAsDouble(args,8,"window width parameter of the LVQ2.1 internal reduction", 0, 1);
        int k = Parameters.assertExtendedArgAsInt(args,9,"size of neighborhood of KNN", 1, 3);
        
        LVQPRU generator = new LVQPRU(training, numIter, pcNpc, pcN, numIterLVQ2_1, a, w, k);        
    	PrototypeSet resultingSet = generator.execute();
    	        
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
    }

}


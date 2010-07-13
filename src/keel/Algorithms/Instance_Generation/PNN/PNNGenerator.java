/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Instance_Generation.PNN;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.utilities.KNN.KNN;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import org.core.*;
import java.util.*;
 
/**
 * Prototypes for Nearest Neighbor Classifiers (Chang para los colegas)
 * @author diegoj
 */
public class PNNGenerator extends PrototypeGenerator
{
    /** Informs if the algorithm must generate a specified number of prototypes. */
    protected boolean useNumberOfPrototypes = false;
    
    /** Number of prototypes to be generated. */
    protected int numberOfPrototypes = 10;
    
    /**
     * Build a new algorithm PNNGenerator that will reduce a prototype set.
     * @param tSet Traning data set to be reduced.
     */
    public PNNGenerator(PrototypeSet tSet)
    {
        super(tSet);                
        this.algorithmName="PNN";
        useNumberOfPrototypes = false;        
    }
    
    /**
     * Build a new algorithm PNNGenerator that will reduce a prototype set.
     * @param tSet Traning data set to be reduced.
     * @param numberOfProts Number of prototypes to be generated.
     */
    public PNNGenerator(PrototypeSet tSet, int numberOfProts)
    {
        super(tSet);                
        useNumberOfPrototypes = true;
        Debug.force(numberOfProts < tSet.size(), "Number of prototypes desired is bigger than actual size of the training data set");
        numberOfPrototypes = numberOfProts;
        this.algorithmName="PNN";
    }
    
    /**
     * Build a new algorithm PNNGenerator that will reduce a prototype set.
     * @param tSet Traning data set to be reduced.
     * @param percentageOfPrototypes Percentage of prototypes of training to be generated.
     */
    public PNNGenerator(PrototypeSet tSet, double percentageOfPrototypes)
    {
        super(tSet);                
        useNumberOfPrototypes = true;
        numberOfPrototypes = this.getSetSizeFromPercentage(percentageOfPrototypes);
        this.algorithmName="PNN";
    }

    /**
     * Build a new algorithm PNNGenerator that will reduce a prototype set.
     * @param parameters Parameters needed for the algoritm, in this case, random seedDefaultValueList only.
     */
    public PNNGenerator(PrototypeSet _trainingDataSet, Parameters parameters)
    {
        super(_trainingDataSet, parameters);
        this.algorithmName="PNN";
        useNumberOfPrototypes=false;
        if(parameters.existMore())
        {
            useNumberOfPrototypes=true;
            numberOfPrototypes = parameters.getNextAsInt();
        }
    }
    
    /**
     * Returns the two nearest prototypes in two different sets.
     * @param A Set which first prototype belongs to.
     * @param B Set which second prototype belongs to.
     * @return A pair which elements are the nearest prototypes in A and B (first and second, resp.).
     */
    protected Pair<Prototype,Prototype> nearestPrototypesIn(PrototypeSet A, PrototypeSet B, MatrixOfDistances m)
    {
        double minimumDist = Double.MAX_VALUE;
        Pair<Prototype, Prototype> nearest = new Pair<Prototype,Prototype>(A.get(0),B.get(0));
        for(Prototype a : A)
            for(Prototype b : B)
            {
                double dist = m.get(a, b);
                if (dist < minimumDist)
                {
                    minimumDist = dist;
                    nearest = new Pair<Prototype, Prototype>(a, b);
                }                    
            }
        return nearest;
    }
    
    /**
     * Performs the maximum reduction of the training data set by the PNNGenerator (aka Chang) method.
     * @return Reduced prototype set by Chang's method.
     */
    protected PrototypeSet maximumReduction()
    {
        PrototypeSet A = new PrototypeSet();
        PrototypeSet B = trainingDataSet.copy();
        //Weight used in the centroid operation
        HashMap<Prototype,Double> W = new HashMap<Prototype,Double>();
        for(Prototype b : B)                
            W.put(b, 1.0);
        int counterOfMerges = 0;
        //int i=0, k=0;
        //int currentAccuracy = absoluteAccuracy(B, trainingDataSet);
        do
        {            
            Prototype arbitraryPoint = B.removeRandom();
            A.add(arbitraryPoint);
            //Debug.println("Iteraci√≥n " + (i++));
            counterOfMerges = 0;
            //k=0;
            MatrixOfDistances dist = new MatrixOfDistances(A,B);
            while(B.size()>0)
            {
                //Debug.println("SubIteraci√≥n " + (k++));    
                Pair<Prototype,Prototype> nearest = nearestPrototypesIn(A,B,dist);
                Prototype p = nearest.first();
                Prototype q = nearest.second();                
                if(p.label() == q.label())
                {
                    Prototype pStar = Prototype.avg(p, W.get(p), q, W.get(q));
                    //Debug.endsIf(A==null, "es null");
                    //PrototypeSet X = A.join(q);
                    //Debug.endsIf(X==null, "X es null");
                    int currentAccuracy = absoluteAccuracy(A.join(q), trainingDataSet);                
                    int newAccuracy = absoluteAccuracy(A.join(pStar), trainingDataSet);
                    //currentAccuracy = newAccuracy;
                    //Debug.errorln("Mejora? " + (newAccuracy >= currentAccuracy));
                    if(newAccuracy < currentAccuracy)
                    {
                        A.add(q);
                        B.remove(q);
                    }
                    else                    
                    {
                        W.put(pStar, W.get(p)+W.get(q));
                        A.remove(p);
                        B.remove(q);
                        dist.removeFromA(p);
                        dist.removeFromB(q);                        
                        A.add(pStar);
                        dist.addToA(pStar);
                        ++counterOfMerges;
                    }
                }
                else
                {
                    A.add(q);
                    B.remove(q);                    
                }
            }//del while(B.size()>0)
            if(counterOfMerges>0)
            {
                B = A;
                A = new PrototypeSet();
            }
        }
        while(counterOfMerges>0);
        
        return A;        
    }
    
    /**
     * Performs a reduction of the training data set by the PNNGenerator (aka Chang) method. Stopped by reaching a specified number of prototypes.
     * @return Reduced prototype set by Chang's method limited by the number of prototypes.
     */
    protected PrototypeSet controlledReduction()
    {
        PrototypeSet A = new PrototypeSet();
        PrototypeSet B = trainingDataSet.copy();
        //Weight used in the centroid operation
        HashMap<Prototype,Double> W = new HashMap<Prototype,Double>();
        for(Prototype b : B)                
            W.put(b, 1.0);
        int counterOfMerges = 0;
        //int i=0, k=0;
        //int currentAccuracy = absoluteAccuracy(B, trainingDataSet);
        boolean sizeReached = false;
        do
        {            
            Prototype arbitraryPoint = B.removeRandom();
            A.add(arbitraryPoint);
            //Debug.println("Iteraci√≥n " + (i++));
            counterOfMerges = 0;
            //k=0;
            MatrixOfDistances dist = new MatrixOfDistances(A,B);
            while(B.size()>0  &&  !sizeReached)
            {
                //Debug.println("SubIteraci√≥n " + (k++));    
                //Debug.errorln("A.size(): " + A.size() + " de " + numberOfPrototypes);
                Pair<Prototype,Prototype> nearest = nearestPrototypesIn(A,B,dist);
                Prototype p = nearest.first();
                Prototype q = nearest.second();                
                if(p.label() == q.label())
                {
                    Prototype pStar = Prototype.avg(p, W.get(p), q, W.get(q));
                    W.put(pStar, W.get(p)+W.get(q));
                    //Debug.endsIf(A==null, "es null");
                    //PrototypeSet X = A.join(q);
                    //Debug.endsIf(X==null, "X es null");
                    int currentAccuracy = absoluteAccuracy(A.join(q), trainingDataSet);                
                    int newAccuracy = absoluteAccuracy(A.join(pStar), trainingDataSet);
                    //currentAccuracy = newAccuracy;
                    //Debug.errorln("Mejora? " + (newAccuracy >= currentAccuracy));
                    if(newAccuracy < currentAccuracy)
                    {
                        A.add(q);
                        B.remove(q);
                    }
                    else                    
                    {
                        A.remove(p);
                        B.remove(q);
                        dist.removeFromA(p);
                        dist.removeFromB(q);                        
                        A.add(pStar);
                        dist.addToA(pStar);
                        ++counterOfMerges;
                    }
                }
                else
                {
                    A.add(q);
                    B.remove(q);                    
                }
                sizeReached = (A.size() == numberOfPrototypes);
            }//del while(B.size()>0  &&  !sizeReached)
            if(counterOfMerges>0  &&  !sizeReached)
            {
                B = A;
                A = new PrototypeSet();
            }
        }
        while(counterOfMerges>0   &&   !sizeReached);
        
        //Debug.errorln("Acurracy of PNNGenerator " + PNNGenerator.accuracy(A, trainingDataSet));
        return A;        
    }
    
    /**
     * Performs a reduction of the training data set by the PNNGenerator (aka Chang) method. It can use early stopping of the method.
     * @return Reduced prototype set by Chang's method limited by the number of prototypes.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        PrototypeSet reduced = null;
        if(this.useNumberOfPrototypes)
            reduced = controlledReduction();
        else
            reduced = maximumReduction();
        return reduced;
    }
    
     /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set.
     * 3: Seed of the Random Number Generator.
     * 4: number of prototypes to be generated (OPTIONAL)
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Debug.setStdDebugMode(false);
        Parameters.setUse("PNN", "<seed> [percentageOfPrototypes]");
        Parameters.assertBasicArgs(args);        
        //Debug.set(true);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        PNNGenerator.setSeed(seed);
        PNNGenerator generator = null;
        if(args.length >= 4)
        {
            double pc = Parameters.assertExtendedArgAsDouble(args,3,"percentage of prototypes",0,100);
            //Debug.errorln("Use " + num + " prototypes");
            generator = new PNNGenerator(training, pc);
        }
        else
            generator = new PNNGenerator(training);
        
    	PrototypeSet resultingSet = generator.execute();
    	
        int accuracy1NN = KNN.classficationAccuracy1NN(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }
}


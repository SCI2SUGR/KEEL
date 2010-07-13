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

package keel.Algorithms.Instance_Generation.VQ;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import java.util.*;

/**
 * AVQ prototype generator.
 * @author diegoj
 */
public class AVQGenerator extends PrototypeGenerator
{
    /** Partition of the training data set used as training. */
    protected PrototypeSet T = null;
    /** Partition of the training data set used as validation set. */
    protected PrototypeSet V = null;
    /** Reduced data set. */
    protected PrototypeSet reduced = null;
    /** Percentage of the original set used in the initial partition. */
    protected double percentageInitPartition = 80.0;
    /** Epsilon parameter of the LBG partition algorithm. */
    double epsilonLBG = 0.4;
    /** Number of iterations of the AVQGenerator. */
    protected int numberOfIterations = 1000;
    
    /**
     * Constructor of the AVQGenerator.
     * @param tDataSet Training data set.
     * @param parameters Parameters of the algorithm.
     */
    public AVQGenerator(PrototypeSet tDataSet, Parameters parameters)
    {
        super(tDataSet, parameters);
        algorithmName = "AVQ";
        this.percentageInitPartition = parameters.getNextAsDouble();
        this.numberOfIterations = parameters.getNextAsInt();
        this.epsilonLBG = parameters.getNextAsDouble();
        //Debug.errorln("% init part " +percentageInitPartition);
        //Debug.errorln("num_iter " +this.numberOfIterations);
        //Debug.errorln("epsilonLBG " +this.epsilonLBG);
    }

    /**
     * Constructor of the AVQGenerator.
     * @param tDataSet Training data set.
     * @param percentPart Percentage of the first set in the initial partition of training data set.
     * @param numIterations Number of iterations of the method.
     * @param epsilonLBG Epsilon parameter of the LBG.
     */
    public AVQGenerator(PrototypeSet tDataSet, double percentPart, int numIterations, double epsilonLBG)
    {
        super(tDataSet);
        this.algorithmName = "AVQ";
        this.percentageInitPartition = percentPart;
        this.numberOfIterations = numIterations;
        this.epsilonLBG = epsilonLBG;
    }
    
    /**
     * Count prototypes whose nearest prototype is the given.
     * @param center Given prototype.
     * @param set Set to be tested.
     * @return Number of prototypes of the set whose nearest neighbor is center.
     */
    protected static int countPrototypesWhichNearestIs(Prototype center, PrototypeSet set)
    {
        int count = 0;
        
        for(Prototype p : set)
        {
            double dCenter = Distance.d(p, center);
            Prototype nearest = set.nearestTo(p);
            double dNearest = Distance.d(p, nearest);
            if(dCenter <= dNearest)
                ++count;
        }
        return count;
    }
    
    /**
     * Generate the R-count: prototypes which its centroid is its nearest prototypes.     
     * @param c Cluster to be examinated.
     * @return Number of clusters which its centroid its the nearest prototypes.
     */
    protected int R(Cluster c)
    {
        PrototypeSet setC = c.getPrototypeSet();
        //Debug.errorln("setC");
        int count = 0;
        for(Prototype p : setC)
            if(c.isCentroidItsNearestPrototoype(p))
                ++count;
        return count;
    }
    
    /**
     * Generate the Q-count: prototypes which its nearest is the center of the cluster, and not any of the other reduced-prototypes.
     * @param reduced Reduced data set.
     * @param center Center of the prototype set.
     * @return Number of clusters which its centroid its the nearest prototypes.
     */
    protected int Q(PrototypeSet reduced, Prototype center)
    {
        int count = 0;
        //PrototypeSet Tc = T.getFromClass(center.label());
        PrototypeSet Tc = T;
        for(Prototype p : Tc)
        {
            Prototype nearest = reduced.nearestTo(p);
            double dNearest = Distance.d(p, nearest);
            double dCenter = Distance.d(p, center);
            if(dCenter < dNearest)            
                ++count;
        }
        return count;
    }
    
    /**
     * Performs Q - R for a cluster.
     * @param c Cluster to be computed Q - R.
     */
    protected int incorrectlyClassifiedSamples(Cluster c)
    {
        return R(c) - Q(reduced, c.center());
    }
    
    /**
     * Reduce the data set by the AVQGenerator method.
     * @return Reduced data set by the AVQGenerator method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        boolean forcedEnd = false;
        ArrayList<Double> classes = Prototype.possibleValuesOfOutput();
        reduced = new PrototypeSet(classes.size());//final reduced set
        
        Pair<PrototypeSet,PrototypeSet> parted = trainingDataSet.makePartition(percentageInitPartition);
        T = parted.first();
        V = parted.second();
        
        ArrayList<PrototypeSet> classPartition = T.classPartition();
        
        ArrayList<Cluster> clusters = new ArrayList<Cluster>(classPartition.size());
        for(PrototypeSet ps : classPartition)
        {
            //Debug.errorln("A√±ado al cluster: center");
            Prototype center_ps = ps.avg();
            reduced.add(center_ps);            
            clusters.add(new Cluster(center_ps, ps));
        }
        //Now each class has got a cluster with a centroid
        
        boolean end = false;
        int it=0;
        int i = 0;
        double errAnt = Double.NEGATIVE_INFINITY;
        while(!end)
        {
            int Emax = Integer.MIN_VALUE;
            Cluster Cmax = null;
            //Debug.errorln("Iteraci√≥n " + it);
            //Debug.errorln("I " + (i++));
            //Buscamos el m√°ximo Ec
            for(Cluster c : clusters)
            {
                //Debug.errorln("Cluster ");
                int Ec = incorrectlyClassifiedSamples(c);
                //Debug.errorln("Ec es " + Ec);
                if(Emax < Ec)
                {
                    Emax = Ec;
                    Cmax = c;
                }
            }
            //Debug.endsIf(Cmax == null, "Cmax es null");
            reduced.remove(Cmax.center());
            //Debug.errorln("LBG");
            //Pair<Cluster, Cluster> pair = Cmax.divideByLBG();
            int Cmax_size = Cmax.size();
            if(Cmax_size > 2)
            {
                Pair<Prototype,Prototype> newCenters = Cmax.centersOfLBGCLuster(epsilonLBG);
                Prototype newCenter1 = newCenters.first();
                Prototype newCenter2 = newCenters.second();
                //Prototype newCenter1 =Cmax.getPrototypeSet().getRandom();
                //Prototype newCenter2 =Cmax.getPrototypeSet().getRandom();            
                reduced.uniqueAdd(newCenter1);
                reduced.uniqueAdd(newCenter2);
            }
            else if(Cmax_size==2)
            {
                reduced.uniqueAdd(Prototype.avg(Cmax.get(0), Cmax.get(1)));
            }
            else
            {
                reduced.uniqueAdd(Cmax.get(0));
                forcedEnd = true;
            }
            //clusters.add(pair.first());
            //clusters.add(pair.second());
            int err = AVQGenerator.absoluteAccuracyAndError(reduced, V).second();
            //Debug.errorln(err +">=?"+errAnt+" : " + (err >= errAnt));
            if(err >= errAnt)
                it++;
            else
            {
                it=0;
                errAnt = err;
            }
            end = (it >= numberOfIterations) || forcedEnd;
        }
        
        return reduced;
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set
     * 2: Seed of the random generator.
     * 3: Number of prototypes to be generated.
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("AVQ", "<seed> <number of prototypes>");        
        Parameters.assertBasicArgs(args);
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);

        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        double percentPart = Parameters.assertExtendedArgAsDouble(args,3,"percentage of partition",0,100);
        int n = Parameters.assertExtendedArgAsInt(args,4,"number of iterations", 1, Integer.MAX_VALUE);
        double eLBG = Parameters.assertExtendedArgAsDouble(args,5,"epsilon of the LBG partition algorithm", 0, 1);
                
        AVQGenerator.setSeed(seed);
        AVQGenerator generator = new AVQGenerator(training, percentPart, n, eLBG);
        
    	PrototypeSet resultingSet = generator.execute();
        resultingSet.save("resultados_avq.txt");
        //System.err.println(resultingSet.toString());
        //System.err.println("-------------------------------------------------");
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);        
    }

}//end-of-AVQGenerator


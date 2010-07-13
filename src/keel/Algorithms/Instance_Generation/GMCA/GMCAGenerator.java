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

package keel.Algorithms.Instance_Generation.GMCA;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.MCA.*;
import keel.Algorithms.Instance_Generation.PNN.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;

/**
 * Implements GMCAGenerator algorithm.
 * @author diegoj
 */
public class GMCAGenerator extends MCAGenerator
{

    /** Clusters of the prototype set */
    ClusterSet clusters;    
    
    PrototypeSet R = null;
    /**
     * Basic constructor
     * @param _trainingDataSet Prototype training data set.
     */
    public GMCAGenerator(PrototypeSet _trainingDataSet)
    {
        super(_trainingDataSet);
        algorithmName="GMCA";    
    }

    /**
     * Constructor
     * @param _trainingDataSet Prototype training data set.
     * @param parameters Parameters of the method.
     */
    public GMCAGenerator(PrototypeSet _trainingDataSet, Parameters parameters)
    {
        super(_trainingDataSet, parameters);
        algorithmName="GMCA";        
    }
    
    /*protected void initClustersByOnePrototypeByCluster(PrototypeSet trainingSet)
    {
        clusters = new ClusterSet();
        Cluster.setClusterSet(clusters);
        int i=0;
        for(Prototype p : trainingSet)
        {
            p.setIndex(i++);
            clusters.add(new Cluster(p));
        }
            
    }*/
    
    protected void initClusters(PrototypeSet T)
    {
        R = new PrototypeSet();//representatives set
        clusters = new ClusterSet();
        //Cluster.setClusterSet(clusters);
        ArrayList<Double> classes = T.nonVoidClasses();
        for(double k : classes)
        {
            PrototypeSet Tk = T.getFromClass(k);
            Tk.randomize();
            while (Tk.size()>0)
            {
                int neighbors = 2;
                if(Tk.size()==3)
                    neighbors = 3;
                PrototypeSet clusterSet = KNN.getNearestNeighbors(Tk.get(0), Tk, neighbors);
                clusterSet.add(Tk.get(0));
                Cluster newCluster = new Cluster(clusterSet);
                clusters.add(newCluster);
                R.add(newCluster.getRepresentative());
                for(Prototype p : clusterSet)
                    Tk.remove(p);               
            }
            //Debug.endsIf(Tk.size()>0, "Tk no estÃ¡ vacÃ­o para k = " + k);
        }        
    }
    
    /**
     * Hard-checking consistency method.
     * @param modified Set to be tested its consistecy.
     * @return TRUE if the prototype si consisten, FALSE in other chase.
     */
    protected boolean isPrototypeConsistent(PrototypeSet modified)
    {
        int accuracyWithPStar = absoluteAccuracy(modified, trainingDataSet);
        //Debug.errorln(currentAccuracy + " =? " + currentAccuracy + " " + (currentAccuracy == currentAccuracy));
        //foundBetter = (accuracyWithPStar >= currentAccuracy && accuracyWithPStar >= bestAccuracy);
        boolean foundBetter = (accuracyWithPStar >= currentAccuracy);
        return foundBetter;
    }
    
    protected static double d(Prototype a, Prototype b)
    {
        return Distance.d(a,b);
    }
    
    protected boolean isConsistent(Cluster mix, PrototypeSet modified)
    {
        //Debug.errorln("Entramos en isConsistent");
        boolean merge = true;//TRUE if merge is sucess, FALSE in other chase
        ArrayList<Double> classes = modified.nonVoidClasses();
        Prototype pStar = mix.getRepresentative();
        PrototypeSet setStar = mix.getPrototypeSet();
        double kStar = pStar.label();
        double rStar = mix.getRadiusLength();
        //Debug.errorln("Clase kStar " + kStar);
        //Nearest prototypes to pStar of each present class
        HashMap<Double,Prototype> sK = new HashMap<Double,Prototype>();
        for(double k : classes)
        {
            Prototype p = modified.nearestToWithClass(pStar, k);
            sK.put(k, p);
        }
        //Debug.errorln("Cargadas las classes");
        //Maximum radius
        for(double k : classes)
            if(k != kStar)
            {
                double maxRadius = clusters.maxRadiusLengthOfClass(k);
                //Debug.errorln("Max radius of class "+ k  +" is " + maxRadius);
                //Debug.errorln("d(pStar, sK.get(k)) < (2 * Math.max(rStar, maxRadius))");
               // Debug.errorln(d(pStar, sK.get(k)) +" < "+ (2 * Math.max(rStar, maxRadius)) + "? " + (d(pStar, sK.get(k)) <= 2 * Math.max(rStar, maxRadius)));
                if( d(pStar, sK.get(k)) <= 2 * Math.max(rStar, maxRadius)  )
                {
                    PrototypeSet Pk = modified.getFromClass(k);
                    //Debug.errorln("P"+k+" tiene " + Pk.size() + " prototipos");
                    for(Prototype s : Pk)
                    {
                        Cluster clusterOfs = clusters.get(s);
                        //Debug.endsIfNull(clusterOfs, "cluster de "+s.getIndex()+" es NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
                        PrototypeSet setOfs = clusterOfs.getPrototypeSet();
                        double rs = clusterOfs.getRadiusLength();
                        //Debug.errorln("d(pStar, s) <= 2*Math.max(rStar, rs)");
                        //Debug.errorln(d(pStar, s) +"<="+ 2*Math.max(rStar, rs)+"? "+(d(pStar, s) <= 2*Math.max(rStar, rs)));
                        if( d(pStar, s) <= 2*Math.max(rStar, rs) )
                        {
                            //List of pairs: first element moves to second element cluster.
                            ArrayList<Pair<Prototype,Prototype>> setStarMoves = new ArrayList<Pair<Prototype,Prototype>>();
                            for(Prototype x : setStar)
                                if( d(pStar, x) >= d(s, x) )
                                {
                                    Prototype nx = setStar.nearestTo(x);
                                    if( d(nx,x) < d(s,x) )
                                    {
                                        //Debug.errorln("Cambia de sitio X ("+x.getIndex()+")");
                                        setStarMoves.add(new Pair<Prototype,Prototype>(x, nx));
                                        //NO USAR clusters.moveTo(x, clusters.getClusterOf(nx));
                                    }
                                    else
                                    {
                                        //Debug.errorln("No hay merge. Corta nx " + nx.getIndex());
                                        merge = false;
                                        return false;
                                    }
                                    
                                }
                            
                            ArrayList<Pair<Prototype,Prototype>> setOfsMoves = new ArrayList<Pair<Prototype,Prototype>>();
                            for(Prototype y : setOfs)
                                if( d(pStar, y) <= d(s, y))
                                {
                                    Prototype ny = Pk.nearestTo(y);
                                    //Debug.errorln(d(ny, y) +" < "+ d(pStar, y));
                                    if( d(ny, y) < d(pStar, y))
                                    {
                                        //Debug.errorln("Cambia de sitio Y ("+y.getIndex()+")");
                                        setOfsMoves.add(new Pair<Prototype,Prototype>(y, ny));
                                        //NO USAR clusters.moveTo(y, clusters.getClusterOf(ny));
                                    }
                                    else
                                    {
                                        //Debug.errorln("No hay merge. Corta ny");
                                        merge = false;
                                        return false;
                                    }
                                }
                            /* Performs movements over setStar */
                            //Debug.errorln("Tenemos que mover en setStar: " + setStarMoves.size());
                            ArrayList<Prototype> movedX = new ArrayList<Prototype>();
                            for(Pair<Prototype,Prototype> p : setStarMoves)
                            {
                                Prototype x = p.first();
                                if(!movedX.contains(x))
                                {
                                    movedX.add(x);
                                    Prototype nx = p.second();
                                    clusters.moveTo(x, clusters.getClusterOf(nx));
                                }
                            }
                            /* Performs movements over setS */
                            //Debug.errorln("Tenemos que mover en  setS: " + setOfsMoves.size());
                            ArrayList<Prototype> movedY = new ArrayList<Prototype>();
                            for(Pair<Prototype,Prototype> p : setOfsMoves)
                            {
                                Prototype y = p.first();
                                if(!movedY.contains(y))
                                {
                                    movedY.add(y);
                                    Prototype ny = p.second();
                                    clusters.moveTo(y, clusters.getClusterOf(ny));
                                }
                            }
                          }//if( d(pStar, s) <= 2*Math.max(rStar, rs) )
                        }//for(Prototype s : Pk)           
                    }//if( d(pStar, sK.get(k)) <= 2 * Math.max(rStar, maxRadius)  )
                }//if(k != kStar)
        //Debug.errorln("=======================FIN, isConsistent. Hay "+ clusters.size() + " clusters =======================");
        return true;
        //Proposition 4
        //return isPrototypeConsistent(modified);//last condition to be tested
    }
    
     //El espÃ­ritu es el mismo, eso es lo que cuenta
    @Override
    public PrototypeSet reduceSet()
    {
        int count = 0;
        int counterOfMerges = 0;
        PrototypeSet V = trainingDataSet.copy();
        int numClasses = V.nonVoidClasses().size();
        initClusters(V);
        Random r = new Random();
        r.setSeed(SEED);
        currentAccuracy = absoluteAccuracy(V, trainingDataSet);
        do
        {
            counterOfMerges = 0;
            //Pair<Prototype, Prototype> neighbors = R.nearestPair();
            ArrayList<Pair<Cluster,Cluster>> nearest = clusters.nearestClustersWithSameClass();
            int nearestSize = nearest.size();
            boolean foundBetter = false;                            
            for(int i=0; !foundBetter  &&  i<nearestSize; ++i)
            {
                clusters.test(V);
                Cluster Cp = nearest.get(i).first();
                Cluster Cq = nearest.get(i).second();
                Prototype p = Cp.getRepresentative();
                Prototype q = Cq.getRepresentative();
                //Debug.errorln("Antes " + clusters.size());
                //Debug.errorln("Merge de " + + Cp.id + "("+ p.getIndex() + ") y "+ Cq.id +"(" + q.getIndex()+")");
                Cluster mix = clusters.merge(Cp, Cq);
                //Debug.errorln("Despues " + clusters.size());
                //Debug.force(clusters.assignment.containsKey(m), "Mix no estÃ¡");
                //clusters.save();
                PrototypeSet modified = new PrototypeSet(V);                
                modified.remove(p);
                modified.remove(q);
                foundBetter = isConsistent(mix, modified);
                if (foundBetter)
                {
                    Prototype avg = mix.getRepresentative();
                    //Debug.errorln("NUEVO CLUSTER " + mix.id + " con rep = " + mix.getRepresentative().getIndex());
                    clusters.assignment.put(avg, mix);
                    count++;
                    //clusters.remove(Cp);
                    //clusters.remove(Cq);
                    //clusters.add(mix);                
                    //Debug.errorln(m.getIndex()+" tiene como cluster " + clusters.assignment.get(m).id);
                    R.remove(p);
                    R.remove(q);
                    R.add(avg);
                    //V.remove(p);
                    //V.remove(q);
                    //V.add(avg);
                    //currentAccuracy = absoluteAccuracy(V, trainingDataSet);
                    //double currentAccuracyR = absoluteAccuracy(R, trainingDataSet);
                    //Debug.errorln("SS V-> " + V.size() + " accur->" + currentAccuracy);
                    //Debug.errorln("SS R-> " + R.size() + " accur->" + currentAccuracyR);
                    ++counterOfMerges;
                    if(clusters.size() == numClasses)
                        counterOfMerges = 0;
                }
            }            
        } while (counterOfMerges > 0);
        //System.err.println(absoluteAccuracy(V, trainingDataSet) + " es la accuracy de V ("+V.size()+" prototipos)");
        //System.err.println(absoluteAccuracy(R, trainingDataSet) + " es la accuracy de R ("+R.size()+" prototipos)");
        //Debug.errorln("Hemos mejorado " + count +" veces");
        return R;        
        //return V;
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
        Debug.setStdDebugMode(false);
        Parameters.setUse("GMCA", "<seed>");
        Parameters.assertBasicArgs(args);        
        
        //Debug.set(false);
        //Debug.setErrorDebugMode(true);
        //Debug.setStdDebugMode(true);
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        GMCAGenerator.setSeed(seed);
        GMCAGenerator generator = new GMCAGenerator(training);
    	PrototypeSet resultingSet = generator.execute();
    	//resultingSet.save(args[1]);
        //System.out.println(resultingSet.toString());
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test);
        //Debug.errorln("TamaÃ±o es " + resultingSet.size());
        int accuracy1NN = KNN.classficationAccuracy1NN(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }
}


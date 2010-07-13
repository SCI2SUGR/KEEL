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

package keel.Algorithms.Instance_Generation.MCA;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.PNN.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;

/**
 * Modified Chang Algorithm.
 * @author diegoj, repared by Isaak
 */
public class MCAGenerator extends PNNGenerator
{
    
    protected double currentAccuracy = 0.0;
    
    /**
     * Build a new algorithm MCAGenerator that will reduce a prototype set.
     * @param parameters Parameters needed for the algoritm, in this case, random seedDefaultValueList only.
     */
    public MCAGenerator(PrototypeSet _trainingDataSet, Parameters parameters) {
        super(_trainingDataSet, parameters);
        algorithmName="MCA";
    }

    /**
     * Build a new algorithm PNNGenerator that will reduce a prototype set.
     */
    public MCAGenerator(PrototypeSet _trainingDataSet) {
        super(_trainingDataSet);
        algorithmName="MCA";
    }
    
    /**
     * Removes a prototype from the list of pairs of the nearest prototypes.
     * @param nearest List of nearest prototypes. IT IS MODIFIED.
     * @param p Prototype to be erased from nearest.
     * @return TRUE if elements of the list were removed, FALSE in other case.
     */
    protected static ArrayList<Pair<Prototype,Prototype>> removeFromCandidates(ArrayList<Pair<Prototype,Prototype>> nearest, Prototype p)
    {
        ArrayList<Pair<Prototype,Prototype>> modified = new ArrayList<Pair<Prototype,Prototype>> ();        
        for(Pair<Prototype,Prototype> pp : nearest)
        {
            if(pp.first()!=p && pp.second()!=p)
                modified.add(pp);
        }
        return modified;
    }
    
    /**
     * Builds a new averaged prototype.
     * @param p Prototype to be merged.
     * @param q Prototype to be merged.
     * @return Averaged prototype of p and q.
     */
    protected Prototype makeAveragePrototype(Prototype p, Prototype q)
    {
        Prototype pStar = p.avg(q);           
        return pStar;
    }
    
    /**
     * Informs if a modified prototype set is consistent (is as well as original or is better).
     * @param modified Modified prototype set.
     * @return TRUE if the modified prototype is better, FALSE in other chase.
     */
    protected boolean isConsistent(PrototypeSet modified)
    {
        //int currentAccuracy = absoluteAccuracy(V, trainingDataSet);
        double accuracyWithPStar = accuracy(modified, trainingDataSet);
        //Debug.errorln(currentAccuracy + " =? " + currentAccuracy + " " + (currentAccuracy == currentAccuracy));
        //foundBetter = (accuracyWithPStar >= currentAccuracy && accuracyWithPStar >= bestAccuracy);
        boolean foundBetter = (accuracyWithPStar >= currentAccuracy);
        return foundBetter;
    }
    
    //El espÃ­ritu es el mismo, eso es lo que cuenta
    /**
     * Reduce the set by the MCAGenerator method.
     * @return Reduced prototype set by MCAGenerator method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        int counterOfMerges = 0;
        PrototypeSet V = new PrototypeSet(trainingDataSet);
        
 	   for(int j=0;j<V.size();j++){
 	         //Now, I establish the index of each prototype.
 			  for(int i=0; i<V.size(); i++)
 				  V.get(i).setIndex(i);
 		   }
 	   
 	   
        Random r = new Random();
        r.setSeed(SEED);
        currentAccuracy = accuracy(V, trainingDataSet);
        
        //System.out.println("Initial Accuracy:"+ currentAccuracy);
        do
        {
            DistanceMatrixByClass distance = new DistanceMatrixByClass(V);
            HashMap<Double,ArrayList<Pair<Prototype,Prototype>>> nearest = distance.nearnestPrototypesForEachClass();
            counterOfMerges = 0;            
            //Debug.println("Aciertos base " + baseAccuracy);            
            ArrayList<Double> labels = new ArrayList<Double>(nearest.keySet());
            Collections.shuffle(labels,r);//shuffle the labels
            
            for(double label : labels)            
            {   
                int sizeLabel = nearest.get(label).size();
                boolean foundBetter = false;
                for(int index=0; !foundBetter && index<sizeLabel; ++index)
                {
                    Prototype p = nearest.get(label).get(index).first();
                    Prototype q = nearest.get(label).get(index).second();
                    
                    // Tngo que comprobar que p y q siguen en el conjunto...
                    
                    if( V.pertenece(p) && V.pertenece(q)){
	                    Prototype pStar = makeAveragePrototype(p,q);                             
	                    //PrototypeSet to be tested if pStar inclusion is good or not                    
	                    PrototypeSet modified = V.copy();
	                    
	                    modified.remove(p);
	                    modified.remove(q);
	                   // modified.remove(p);
	                    //modified.remove(q);
	                    modified.add(pStar);
	                    
	                    double newAcc = accuracy(modified, trainingDataSet);
	                    //foundBetter = isConsistent(modified);
	                    if(newAcc >= currentAccuracy)
	                    {
	                        V.remove(p);
	                      V.remove(q);
	                        
	                        V.add(pStar);
	                        currentAccuracy = newAcc;
	                        //Debug.errorln("Found better V-> " + V.size() + " accur->" + currentAccuracy);
	                        ++counterOfMerges;
	                        
	                        //Stablish the new indexes
	                  	   for(int j=0;j<V.size();j++){
	               	         //Now, I establish the index of each prototype.
	               			  for(int i=0; i<V.size(); ++i)
	               				  V.get(i).setIndex(i);
	               		   }
	                  	   
	                       // System.out.println("CurrentAccuracy =" + this.currentAccuracy);
	                       // System.out.println("Reduction % " + (100-(V.size()*100)/trainingDataSet.size()) );
	                    }
                    }// End if pertenece..
                }

            }//del for(double label : labels)
            //Debug.println("Counter of Merges " + counterOfMerges);       
            
            
        }while(counterOfMerges > 0);
        
        
       // V.print();
        System.out.println("Accuracy = " + currentAccuracy);
        System.out.println("Reduction % " + (100-(V.size()*100)/trainingDataSet.size()) );
        return V;
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
        Parameters.setUse("MCA", "<seed>");
        Parameters.assertBasicArgs(args);        
        
        //Debug.set(false);
        //Debug.setErrorDebugMode(true);
        //Debug.setStdDebugMode(true);
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        MCAGenerator.setSeed(seed);
        
        MCAGenerator generator = new MCAGenerator(training);
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        //System.out.println(resultingSet.toString());
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test);
        int accuracy1NN = KNN.classficationAccuracy1NN(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, KNN.k(), test);
    }

}


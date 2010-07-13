/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Instance_Generation.Chen;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.Distance;
import java.util.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import org.core.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ChenGenerator prototype reducition algorithm
 * @author diegoj
 */
public class ChenGenerator extends PrototypeGenerator
{
    /** Number of prototypes to be generated. */
    protected int numberOfPrototypes;
    
    /**
     * Build a new ChenGenerator Algorithm
     * @param t Original prototype set to be reduced.
     * @param n_d Number of prototypes to be generated.
     */
    public ChenGenerator(PrototypeSet t, int n_d)
    {
        super(t);
        algorithmName="Chen";
        numberOfPrototypes = n_d;        
    }
    
    /**
     * Build a new ChenGenerator Algorithm
     * @param t Original prototype set to be reduced.
     * @param percSet Reduction percentage of the prototype set.
     */
    public ChenGenerator(PrototypeSet t, double percSet)
    {
        super(t);
        algorithmName="Chen";
        this.numberOfPrototypes = getSetSizeFromPercentage(percSet);
    }
    
    /**
     * Build a new ChenGenerator Algorithm
     * @param t Original prototype set to be reduced.
     * @param params Parameters of the algorithm (only % of reduced set).
     */
    public ChenGenerator(PrototypeSet t, Parameters params)
    {
        super(t, params);
        algorithmName="Chen";
        numberOfPrototypes = getSetSizeFromPercentage(params.getNextAsDouble());
    }
    
    /**
     * Generate a reduced prototype set by the ChenGenerator method.
     * @return Reduced set by ChenGenerator's method.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        PrototypeSet D = trainingDataSet.clone();//note: hard-copy
        for(int i=0; i<D.size(); ++i)
            D.get(i).setIndex(i);
        int Np = numberOfPrototypes;
        
        
        ArrayList<PrototypeSet> C = new ArrayList<PrototypeSet>(Np);
        Prototype P1 = null;
        Prototype P2 = null;
        System.err.println("Le he dicho tamaño " + Np);
        for(int Nc=0; Nc<Np; Nc++)
        {
            //Obtiene los 2 prototipos más lejanos
            Pair<Prototype,Prototype> Pi = D.farthestPrototypes();
            P1 = Pi.first();
            P2 = Pi.second();
            
            //Haz partición de conjunto en función a la distancia a los prototipos
            //anteriormente calculados (los más lejanos)
            Pair<PrototypeSet,PrototypeSet> Di = D.partIntoSubsetsWhichSeedPointsAre(P1,P2);
            PrototypeSet D1 = Di.first();
            PrototypeSet D2 = Di.second(); 
            //System.err.println("Iteración " + Nc + " Tam C " + C.size());
            //System.err.println("PRototypos mas lejanos son " + P1.getIndex() + " y " + P2.getIndex());
            C.remove(D);
            C.add(D1);
            C.add(D2);            
            //Encuentra el conjunto menos homogéneo
            ArrayList<PrototypeSet> I = null;
            ArrayList<PrototypeSet> I1 = new ArrayList<PrototypeSet>();
            ArrayList<PrototypeSet> I2 = new ArrayList<PrototypeSet>();
            //System.out.println("C.size ="+ C.size());
            for(PrototypeSet pSet : C)
            {
                if(pSet.containsSeveralClasses())
                    I1.add(pSet);
                else
                    I2.add(pSet);
            }
            I=I1;
            if(I1.isEmpty())
                I = I2;
            //System.err.println("I1 " + I1.size());
            //System.err.println("I2 " + I2.size());            
            // Encuentra el conjunto en I con los 2 puntos más lejanos
            double distMax = -1.0;
            PrototypeSet Qchosen = I.get(0);
            Pair<Prototype,Prototype> diameterPoints = null;
            for(PrototypeSet q : I)
            {
                if(q.size()>1)//limit-chase. Prototype set with only 1 element
                {
                    Pair<Prototype,Prototype> farthest = q.farthestPrototypes();
                    double curDist = Distance.d(farthest.first().formatear(), farthest.second().formatear());
                    if(distMax < curDist)
                    {
                        distMax = curDist;
                        Qchosen = q;
                        diameterPoints = farthest;
                    }
                }
            }
            D = Qchosen;
            P1 = diameterPoints.first();
            P2 = diameterPoints.second();
        }//loop-for-end
        
        PrototypeSet result = new PrototypeSet(Np);
        for(int i=0; i<Np; ++i)
        {
            Prototype averaged = C.get(i).avg();
            double averagedClass = C.get(i).mostFrequentClass();
            averaged.setLabel(averagedClass);
            result.add(averaged.formatear());
            //System.out.println("Prototipo " + i + " tiene clase " + averagedClass);
        }
        //System.err.println("% de acierto en training " + ChenGenerator.accuracy(result, trainingDataSet) );
        return result;
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set.
     * 3: Seed of the random number generator.
     * 4: Number of prototypes to be generated.
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("Chen", "<seed> <percentage of prototypes>");        
        Parameters.assertBasicArgs(args);
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,Parameters.SEED_TXT,0,Long.MAX_VALUE);
        double percSize = Parameters.assertExtendedArgAsDouble(args,3, Parameters.PERC_SIZE_TXT, 0, 100);
        
        ChenGenerator.setSeed(seed);
        ChenGenerator generator = new ChenGenerator(training, percSize);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save("resultados_chen.txt");
        
        //String filename = args[0] +"_"+ resultingSet.size()+"_CHEN.sel";
        //resultingSet.save(filename);
        
        //System.out.println(resultingSet.toString());
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, k, test);
    }
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.Algorithms.Preprocess.Instance_Generation.BasicMethods;

import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Preprocess.Instance_Generation.*;
import java.util.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.KNN.*;
import org.core.*;

/**
 *
 * @author diegoj
 */
public class CNN extends PrototypeGenerator
{
    /** Neighborhood size in KNN */
    protected int k = 1;

    /**
     * Creates a CNN algorithm.
     * @param _trainingDataSet original training data.
     */
    public CNN(PrototypeSet _trainingDataSet)
    {
        super(_trainingDataSet);
        algorithmName="CNN";
    }
    
    public CNN(PrototypeSet _trainingDataSet, int k)
    {
        super(_trainingDataSet);
        algorithmName="CNN";
        this.k = k;
        
    }
    
    /**
     * Make a selection by the CNN method.
     * @param original Original data set.
     * @return Prototoypes selected by CNN method.
     */
    public static PrototypeSet makeReductionOf(PrototypeSet original)
    {
        PrototypeSet T = original.copy();
        T.randomize();
        
        PrototypeSet S = new PrototypeSet();
        ArrayList<Double> classes = T.nonVoidClasses();
        for(double c : classes)
        {
            PrototypeSet Tc = T.getFromClass(c);
            Tc.randomize();
            S.add(Tc.get(0));    
            T.remove(Tc.get(0));
        }
        
        int Tsize = T.size();
        for(int i=0; i<Tsize; ++i)
        {
            Prototype ti = T.get(i);
            Prototype s = KNN.getNearest(ti, S);
            if(s.label() != ti.label())
                S.add(ti);
        }
        return S;
    }
    
    /**
     * Make a selection by the CNN method.
     * @param original Original data set.
     * @param k K used in KNN-rule.
     * @return Prototoypes selected by CNN method.
     */
    public static PrototypeSet makeReductionOf(PrototypeSet original, int k)
    {
       PrototypeSet T = original.copy();
        T.randomize();
        
        PrototypeSet S = new PrototypeSet();
        ArrayList<Double> classes = T.nonVoidClasses();
        for(double c : classes)
        {
            PrototypeSet Tc = T.getFromClass(c);
            Tc.randomize();
            S.add(Tc.get(0));    
            T.remove(Tc.get(0));
        }
        
        int Tsize = T.size();
        for(int i=0; i<Tsize; ++i)
        {
            Prototype ti = T.get(i);
            double tiLabel = ti.label();
            PrototypeSet nearest = KNN.getNearestNeighbors(ti, S, k);            
            double nearestSetLabel = nearest.mostFrequentClass();
            if(nearestSetLabel != tiLabel && tiLabel != nearest.get(0).label())
                S.add(ti);
        }
        return S;
    }
    
    @Override
    public PrototypeSet reduceSet()
    {
        PrototypeSet reduced = null;
        if(k>1)
            reduced = makeReductionOf(trainingDataSet,k);
        else
            reduced = makeReductionOf(trainingDataSet);
        return reduced;
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
        Parameters.setUse("CNN", "<seed>");
        Parameters.assertBasicArgs(args);        
        Debug.setStdDebugMode(true);
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        CNN.setSeed(seed);
        
        CNN cnn = new CNN(training);
        PrototypeSet resultingSet = cnn.reduceSet();
        
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        cnn.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Preprocess.Instance_Generation.BasicMethods;


import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Preprocess.Instance_Generation.*;
import java.util.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.KNN.*;
import org.core.*;

/**
 * Implements a random selector but taking proportional number of prototypes in order to their occurences (that is, their a priory probabilities).
 * @author diegoj
 */
public class ARS extends RandomSelector
{
    /**
     * Creates a new AdvanceRandomSelector
     * @param t Traning data set
     * @param n Number of prototypes to be extracted.
     */
    public ARS(PrototypeSet t, int n)
    {
        super(t,n);
        // Name of the reduction tecnique        
        algorithmName = "AdvancedRandomSelector";   
    }
    
    /**
     * Creates a new AdvanceRandomSelector
     * @param t Traning data set
     * @param params External parameters
     */
    public ARS(PrototypeSet t, Parameters params)
    {
        super(t, params);
        // Name of the reduction tecnique        
        algorithmName = "AdvancedRandomSelector";           
    }
    
    
    /**
     * Extract prototypes from the training data and returns them in a new data set.
     * @return PrototypeSet containing the extracted prototypes.
     */
    @Override
    public PrototypeSet reduceSet()
    {
        return super.selecRandomSet(numberOfPrototypesSelected, true);
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich will contain the test data set
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("AdvancedRandomSelector", "<seed> <number of prototypes>");        
        Parameters.assertBasicArgs(args);
        
        ARS.setSeed(System.nanoTime()*100 + System.nanoTime());
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        //System.err.println(training.toString());
        //System.err.println("-------------------------------------------------");
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        int n = Parameters.assertExtendedArgAsInt(args,3,"number of prototypes", 1, training.size()-1);
        
        ARS.setSeed(seed);
        ARS generator = new ARS(training, n);
        
    	PrototypeSet resultingSet = generator.execute();
        //System.err.println(resultingSet.toString());
        //System.err.println("-------------------------------------------------");
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);        
//        System.err.println("-------------------------------------------------");
//        System.err.println(resultingSet.toString());
        //String filename = args[0] +"_"+ resultingSet.size()+"_"+seedDefaultValueList+"_ARS.sel";
        //resultingSet.save(filename);
        //System.err.println("-------------------------------------------------");
        //System.err.println(resultingSet.toString());
        
    	//resultingSet.save(args[1]);
//        System.err.println(Prototype.count1);
//        System.err.println(Prototype.count2);
    }
}

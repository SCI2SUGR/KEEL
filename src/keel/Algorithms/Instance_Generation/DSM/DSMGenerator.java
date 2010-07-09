package keel.Algorithms.Instance_Generation.DSM;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.LVQ.*;
import org.core.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Decision Surface Mapping algorithm (DSMGenerator) for prototype reduction.
 * @author diegoj
 */
public class DSMGenerator extends LVQ1
{
    /**
     * DSMGenerator constructor.
     * @param tDataSet Training data set to be reduced.
     * @param nIter Number of iterations to be executed.
     * @param nProt Number of prototypes to be generated.
     * @param alpha_0 Alpha0 constant parameter of the algorithm.
     */
    public DSMGenerator(PrototypeSet tDataSet, int nIter, int nProt, double alpha_0)
    {
        super(tDataSet, nIter, nProt, alpha_0);
        this.algorithmName="DSM";//Name of the algorithm DSMGenerator
    }
    
    /**
     * DSMGenerator constructor.
     * @param tDataSet Training data set to be reduced.
     * @param nIter Number of iterations to be executed.
     * @param percSize Reduced size respect training set size.
     * @param alpha_0 Alpha0 constant parameter of the algorithm.
     */
    public DSMGenerator(PrototypeSet tDataSet, int nIter, double percSize, double alpha_0)
    {
        super(tDataSet, nIter, percSize, alpha_0);
        this.algorithmName="DSM";//Name of the algorithm DSMGenerator
    }
    
    /**
     * DSMGenerator constructor.
     * @param tDataSet Training data set to be reduced.
     * @param param Parameters of the algorithm (number of iterations and prototypes, alpha0).
     */
    public DSMGenerator(PrototypeSet tDataSet, Parameters param)
    {
        super(tDataSet, param);
        this.algorithmName="DSM";//Name of the algorithm DSMGenerator
        //Debug.errorln("Number of iterations " + this.iterations);
        //Debug.errorln("Number of prototypes " + this.numberOfPrototypesGenerated);
        //Debug.errorln("Alpha0 " + this.alpha_0);
    }    
    
    /*
    * Extracts a instance using a particular method
    * @param tData is the training data set.
    * @return a instance of the training instance set
    *  
    @Override
    protected Prototype extract(PrototypeSet tData)
    {
        int _chosen = Randomize.Randint(0, tData.size()-1); 
        return tData.get(_chosen);
    }*/
    
    /**
     * Applies a DSMGenerator-reward to prototype m.
     * @param m Rewarded prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    @Override
    protected void reward(Prototype m, Prototype x)
    {
        m.set(m.add(x.subMul(m,alpha_0)));
        //m.set(  m.add( (x.sub(m)).mul(alpha_0) )  ); //It's the same
    }
    
    /**
     * Applies a DSMGenerator-reward to prototype m.
     * @param m Rewarded prototype. IT IS MODIFIED.
     * @param x Nearest prototype to m.
     */
    @Override
    protected void penalize(Prototype m, Prototype x)
    {
        
        //m.set(  m.sub( (x.sub(m)).mul(alpha_0) )  ); //It's the same
        m.set(m.sub(x.subMul(m,alpha_0)));
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
        double iLabel = i.label();
        double nearestLabel = nearest.label();
        if(iLabel != nearestLabel)
        {
            Prototype nearestSameLabel = KNN.getNearestWithSameClassAs(i, tData);
            penalize(nearest,i);
            if(nearestSameLabel != null)
                reward(nearestSameLabel, i);
        }
        //This algorithm does nothing if the assigned class
        // to prototype i is correct
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich contains the test data set.
     * 3: Number of iterations.
     * 4: Number of prototypes to be generated.
     * 5: Alpha constant parameter.
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("DSM", "<number of iterations> <seed> <number of iterations> <% of prototypes> <alpha_0>");
        Parameters.assertBasicArgs(args);        
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);        
        
        //Seed used in the creation of the initial prototype set
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        //Parameter number of iterations of the algorithm
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        //Parameter number of iterations of the algorithm
        double percNProt = Parameters.assertExtendedArgAsInt(args,4,Parameters.PERC_SIZE_TXT,1,100);
        //Parameter alpha of the algorithm
        double alpha = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0.0, 1.0);
                        
        DSMGenerator.setSeed(seed);
        DSMGenerator generator = new DSMGenerator(training, iter, percNProt, alpha);
        //resultingSet.save(args[1]);
        
    	PrototypeSet resultingSet = generator.execute();    	
        
        //System.out.println(resultingSet.toString());
        //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
        //generator.showResultsOfAccuracy(accuracyKNN, accuracy1NN, k, test);
    }
}

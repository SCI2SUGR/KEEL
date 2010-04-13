/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Preprocess.Instance_Generation.LVQ;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Preprocess.Instance_Generation.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;

import java.util.*;

/**
 * LVQ3 algorithm calling.
 * @author diegoj
 */
public class LVQ3Algorithm extends PrototypeGenerationAlgorithm<LVQ3>
{
    /**
     * Builds a new LVQ3 object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQ3 buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQ3(train, params);    
    }
    
    /**
     * Main method. Executes LVQ3 method.
     * @param args Console arguments of the method.
     */        
    public static void main(String args[])
    {
        LVQ3Algorithm algo = new LVQ3Algorithm();
        algo.execute(args);
    }
}

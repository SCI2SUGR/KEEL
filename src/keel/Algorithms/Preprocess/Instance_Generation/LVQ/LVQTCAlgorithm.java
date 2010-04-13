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
 * LVQTC algorithm calling.
 * @author diegoj
 */
public class LVQTCAlgorithm extends PrototypeGenerationAlgorithm<LVQTC>
{
    /**
     * Builds a new LVQTC object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQTC buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQTC(train, params);    
    }
    
    /**
     * Main method. Executes LVQTC method.
     * @param args Console arguments of the method.
     */        
    public static void main(String args[])
    {
        LVQTCAlgorithm algo = new LVQTCAlgorithm();
        algo.execute(args);
    }
}

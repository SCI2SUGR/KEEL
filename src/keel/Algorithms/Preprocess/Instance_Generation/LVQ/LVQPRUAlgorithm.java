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
 * LVQPRU algorithm calling.
 * @author diegoj
 */
public class LVQPRUAlgorithm extends PrototypeGenerationAlgorithm<LVQPRU>
{
    
    /**
     * Builds a new LVQPRU object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQPRU buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQPRU(train, params);    
    }
    
    /**
     * Main method. Executes LVQPRU method.
     * @param args Console arguments of the method.
     */      
    public static void main(String args[])
    {
        LVQPRUAlgorithm algo = new LVQPRUAlgorithm();
        algo.execute(args);
    }
}

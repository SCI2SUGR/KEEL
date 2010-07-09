/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.LVQ;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * LVQ2 algorithm calling.
 * @author diegoj
 */
public class LVQ2Algorithm extends PrototypeGenerationAlgorithm<LVQ2>
{
    /**
     * Builds a new LVQ2 object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQ2 buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQ2(train, params);    
    }
    
    /**
     * Main method. Executes LVQ2 method.
     * @param args Console arguments of the method.
     */        
    public static void main(String args[])
    {
        LVQ2Algorithm algo = new LVQ2Algorithm();
        algo.execute(args);
    }
}

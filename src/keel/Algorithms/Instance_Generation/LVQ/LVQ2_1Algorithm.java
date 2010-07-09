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
 * LVQ2.1 algorithm calling.
 * @author diegoj
 */
public class LVQ2_1Algorithm extends PrototypeGenerationAlgorithm<LVQ2_1>
{
    /**
     * Builds a new LVQ2.1 object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQ2_1 buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQ2_1(train, params);    
    }
    
    /**
     * Main method. Executes LVQ2.1 method.
     * @param args Console arguments of the method.
     */      
    public static void main(String args[])
    {
        LVQ2_1Algorithm algo = new LVQ2_1Algorithm();
        algo.execute(args);
    }
}

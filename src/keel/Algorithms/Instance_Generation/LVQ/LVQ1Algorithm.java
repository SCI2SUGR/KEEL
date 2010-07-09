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
 * LVQ1 algorithm calling.
 * @author diegoj
 */
public class LVQ1Algorithm extends PrototypeGenerationAlgorithm<LVQ1>
{
     /**
     * Builds a new LVQ1 object.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected LVQ1 buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new LVQ1(train, params);    
    }
    
    /**
     * Main method. Executes LVQ1 method.
     * @param args Console arguments of the method.
     */    
    public static void main(String args[])
    {
        LVQ1Algorithm algo = new LVQ1Algorithm();
        algo.execute(args);
    }
}

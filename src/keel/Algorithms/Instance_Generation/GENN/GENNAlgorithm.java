/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.GENN;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * GENN algorithm calling.
 * @author diegoj
 */
public class GENNAlgorithm extends PrototypeGenerationAlgorithm<GENNGenerator>
{
    /**
     * Builds a new GENNGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected GENNGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new GENNGenerator(train, params);    
    }
    
    /**
     * Main method. Executes GENN.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        GENNAlgorithm algo = new GENNAlgorithm();
        algo.execute(args);
    }
}

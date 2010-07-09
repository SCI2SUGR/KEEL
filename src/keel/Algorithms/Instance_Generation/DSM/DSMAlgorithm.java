/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.DSM;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * DSM algorithm calling.
 * @author diegoj
 */
public class DSMAlgorithm extends PrototypeGenerationAlgorithm<DSMGenerator>
{
    /**
     * Builds a new DSMGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected DSMGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new DSMGenerator(train, params);    
    }
    
    /**
     * Main method. Executes DSM.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        DSMAlgorithm algo = new DSMAlgorithm();
        algo.execute(args);
    }
}

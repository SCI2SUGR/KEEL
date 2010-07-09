/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.GMCA;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * GMCA algorithm calling.
 * @author diegoj
 */
public class GMCAAlgorithm extends PrototypeGenerationAlgorithm<GMCAGenerator>
{
     /**
     * Builds a new GMCAGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected GMCAGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new GMCAGenerator(train, params);    
    }

    /**
     * Main method. Executes GMCA.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        GMCAAlgorithm algo = new GMCAAlgorithm();
        algo.execute(args);
    }
}

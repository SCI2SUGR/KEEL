/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.Chen;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * Chen algorithm calling.
 * @author diegoj
 */
public class ChenAlgorithm extends PrototypeGenerationAlgorithm<ChenGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected ChenGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new ChenGenerator(train, params);    
    }
    
     /**
     * Main method. Executes Chen algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        ChenAlgorithm algo = new ChenAlgorithm();
        algo.execute(args);
    }
}

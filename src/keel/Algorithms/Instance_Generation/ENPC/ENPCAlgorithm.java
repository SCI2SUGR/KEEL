//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.ENPC;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class ENPCAlgorithm extends PrototypeGenerationAlgorithm<ENPCGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected ENPCGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new ENPCGenerator(train, params);    
    }
    
     /**
     * Main method. Executes PSO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        ENPCAlgorithm isaak = new ENPCAlgorithm();
        isaak.execute(args);
    }
}

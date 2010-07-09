//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.MSE;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class MSEAlgorithm extends PrototypeGenerationAlgorithm<MSEGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected MSEGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new MSEGenerator(train, params);    
    }
    
     /**
     * Main method. Executes PSO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        MSEAlgorithm isaak = new MSEAlgorithm();
        isaak.execute(args);
    }
}

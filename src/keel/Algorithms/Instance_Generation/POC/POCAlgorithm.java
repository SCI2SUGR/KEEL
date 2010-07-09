//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.POC;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class POCAlgorithm extends PrototypeGenerationAlgorithm<POCGenerator>
{
    /**
     * Builds a new POCGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected POCGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new POCGenerator(train, params);    
    }
    
     /**
     * Main method. Executes POC algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        POCAlgorithm isaak = new POCAlgorithm();
        isaak.execute(args);
    }
}

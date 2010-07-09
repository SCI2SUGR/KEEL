//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.RSP;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class RSPAlgorithm extends PrototypeGenerationAlgorithm<RSPGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected RSPGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new RSPGenerator(train, params);    
    }
    
     /**
     * Main method. Executes PSO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        RSPAlgorithm isaak = new RSPAlgorithm();
        isaak.execute(args);
    }
}

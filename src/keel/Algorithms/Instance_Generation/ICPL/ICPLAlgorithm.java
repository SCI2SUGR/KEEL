//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.ICPL;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class ICPLAlgorithm extends PrototypeGenerationAlgorithm<ICPLGenerator>
{
    /**
     * Builds a new ICPLGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected ICPLGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new ICPLGenerator(train, params);    
    }
    
     /**
     * Main method. Executes ICPL algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        ICPLAlgorithm isaak = new ICPLAlgorithm();
        isaak.execute(args);
    }
}

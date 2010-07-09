//
//  Main.java
//
//  Isaac Triguero
//
//

package keel.Algorithms.Instance_Generation.SGP;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class SGPAlgorithm extends PrototypeGenerationAlgorithm<SGPGenerator>
{
    /**
     * Builds a new SGPGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SGPGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new SGPGenerator(train, params);    
    }
    
     /**
     * Main method. Executes SGP algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SGPAlgorithm isaak = new SGPAlgorithm();
        isaak.execute(args);
    }
}

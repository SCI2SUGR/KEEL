package keel.Algorithms.Instance_Generation.HYB;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * HYB algorithm calling.
 * @author diegoj
 */
public class HYBAlgorithm extends PrototypeGenerationAlgorithm<HYBGenerator>
{
     /**
     * Builds a new HYBGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected HYBGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new HYBGenerator(train, params);    
    }
    
    /**
     * Main method. Executes HYB.
     * @param args Console arguments of the method.
     */    
    public static void main(String args[])
    {
        HYBAlgorithm algo = new HYBAlgorithm();
        algo.execute(args);
    }
}

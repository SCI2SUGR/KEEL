/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.MCA;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * MCA algorithm calling.
 * @author diegoj
 */
public class MCAAlgorithm extends PrototypeGenerationAlgorithm<MCAGenerator>
{
     /**
     * Builds a new MCAGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected MCAGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new MCAGenerator(train, params);    
    }
    
    /**
     * Main method. Executes MCA.
     * @param args Console arguments of the method.
     */       
    public static void main(String args[])
    {
        MCAAlgorithm algo = new MCAAlgorithm();
        algo.execute(args);
    }
}

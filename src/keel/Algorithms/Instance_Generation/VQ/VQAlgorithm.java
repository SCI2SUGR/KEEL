/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.VQ;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;


/**
 * VQ algorithm calling.
 * @author diegoj
 */
public class VQAlgorithm extends PrototypeGenerationAlgorithm<VQGenerator>
{
    /**
     * Builds a new VQGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected VQGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new VQGenerator(train, params);    
    }
    
    /**
     * Main method. Executes VQ.
     * @param args Console arguments of the method.
     */      
    public static void main(String args[])
    {
        VQAlgorithm algo = new VQAlgorithm();
        algo.execute(args);
    }
}

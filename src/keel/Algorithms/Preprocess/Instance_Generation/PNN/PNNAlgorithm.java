/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Preprocess.Instance_Generation.PNN;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Preprocess.Instance_Generation.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;

import java.util.*;


/**
 * PNN algorithm calling.
 * @author diegoj
 */
public class PNNAlgorithm extends PrototypeGenerationAlgorithm<PNNGenerator>
{
     /**
     * Builds a new PNNGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected PNNGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new PNNGenerator(train, params);    
    }
    
    /**
     * Main method. Executes PNN.
     * @param args Console arguments of the method.
     */    
    public static void main(String args[])
    {
        PNNAlgorithm algo = new PNNAlgorithm();
        algo.execute(args);
    }
}

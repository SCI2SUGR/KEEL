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
 * AVQ algorithm calling.
 * @author diegoj
 */
public class AVQAlgorithm extends PrototypeGenerationAlgorithm<AVQGenerator>
{
    /**
     * Builds a new AVQGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected AVQGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new AVQGenerator(train, params);    
    }
    
    /**
     * Main method. Executes AVQ.
     * @param args Console arguments of the method.
     */      
    public static void main(String args[])
    {
        AVQAlgorithm algo = new AVQAlgorithm();
        algo.execute(args);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Preprocess.Instance_Generation.BasicMethods;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Preprocess.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Preprocess.Instance_Generation.*;
import keel.Algorithms.Preprocess.Instance_Generation.utilities.*;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class ARSAlgorithm extends PrototypeGenerationAlgorithm<ARS>
{
    protected ARS buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new ARS(train, params);    
    }
    
    public static void main(String args[])
    {
        ARSAlgorithm algo = new ARSAlgorithm();
        algo.execute(args);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.BasicMethods;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class AVGAlgorithm extends PrototypeGenerationAlgorithm<AVG>
{
    protected AVG buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new AVG(train, params);    
    }
    
    public static void main(String args[])
    {
        AVGAlgorithm algo = new AVGAlgorithm();
        algo.execute(args);
    }
}

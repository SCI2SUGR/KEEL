/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.BTS3;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class BTS3Algorithm extends PrototypeGenerationAlgorithm<BTS3Generator>
{
    protected BTS3Generator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new BTS3Generator(train, params);    
    }
    
    public static void main(String args[])
    {
        BTS3Algorithm algo = new BTS3Algorithm();
        algo.execute(args);
    }
}

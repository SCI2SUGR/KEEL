/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.Trivial;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class TrivialAlgorithm extends PrototypeGenerationAlgorithm<PrototypeGenerator>
{
    protected PrototypeGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new PrototypeGenerator(train, params);    
    }
    
    public static void main(String args[])
    {
        TrivialAlgorithm algo = new TrivialAlgorithm();
        algo.execute(args);
    }
}

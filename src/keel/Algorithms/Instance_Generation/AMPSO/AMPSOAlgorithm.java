//
//  Main.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 10-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.AMPSO;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.*;
import keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * PSO algorithm calling.
 * @author Isaac Triguero
 */
public class AMPSOAlgorithm extends PrototypeGenerationAlgorithm<AMPSOGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected AMPSOGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new AMPSOGenerator(train, params);    
    }
    
     /**
     * Main method. Executes PSO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        AMPSOAlgorithm isaak = new AMPSOAlgorithm();
        isaak.execute(args);
    }
}

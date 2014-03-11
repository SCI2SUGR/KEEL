//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.Rel_RASCO;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * Rel_RASCO algorithm calling.
 * @author Isaac Triguero
 */
public class Rel_RASCOAlgorithm extends PrototypeGenerationAlgorithm<Rel_RASCOGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected Rel_RASCOGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new Rel_RASCOGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes Rel_RASCO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        Rel_RASCOAlgorithm isaak = new Rel_RASCOAlgorithm();
        isaak.execute(args);
    }
}

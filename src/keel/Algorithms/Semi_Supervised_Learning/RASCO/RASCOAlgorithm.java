//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.RASCO;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * RASCO algorithm calling.
 * @author Isaac Triguero
 */
public class RASCOAlgorithm extends PrototypeGenerationAlgorithm<RASCOGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected RASCOGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new RASCOGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes RASCO algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        RASCOAlgorithm isaak = new RASCOAlgorithm();
        isaak.execute(args);
    }
}

//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.CLCC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * CLCC algorithm calling.
 * @author Isaac Triguero
 */
public class CLCCAlgorithm extends PrototypeGenerationAlgorithm<CLCCGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected CLCCGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new CLCCGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes CLCC algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        CLCCAlgorithm isaak = new CLCCAlgorithm();
        isaak.execute(args);
    }
}

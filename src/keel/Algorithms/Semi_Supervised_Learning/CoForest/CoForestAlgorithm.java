//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.CoForest;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * CoForest algorithm calling.
 * @author Isaac Triguero
 */
public class CoForestAlgorithm extends PrototypeGenerationAlgorithm<CoForestGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected CoForestGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new CoForestGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes CoForest algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        CoForestAlgorithm isaak = new CoForestAlgorithm();
        isaak.execute(args);
    }
}

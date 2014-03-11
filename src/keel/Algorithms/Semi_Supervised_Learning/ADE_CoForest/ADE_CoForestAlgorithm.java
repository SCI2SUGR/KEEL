//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.ADE_CoForest;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * ADE_CoForest algorithm calling.
 * @author Isaac Triguero
 */
public class ADE_CoForestAlgorithm extends PrototypeGenerationAlgorithm<ADE_CoForestGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected ADE_CoForestGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new ADE_CoForestGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes ADE_CoForest algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        ADE_CoForestAlgorithm isaak = new ADE_CoForestAlgorithm();
        isaak.execute(args);
    }
}

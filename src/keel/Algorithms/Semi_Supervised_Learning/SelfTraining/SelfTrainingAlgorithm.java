//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.SelfTraining;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * SelfTraining algorithm calling.
 * @author Isaac Triguero
 */
public class SelfTrainingAlgorithm extends PrototypeGenerationAlgorithm<SelfTrainingGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SelfTrainingGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new SelfTrainingGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes SelfTraining algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SelfTrainingAlgorithm isaak = new SelfTrainingAlgorithm();
        isaak.execute(args);
    }
}

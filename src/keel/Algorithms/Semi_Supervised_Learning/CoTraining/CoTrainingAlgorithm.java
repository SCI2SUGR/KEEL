//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.CoTraining;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * CoTraining algorithm calling.
 * @author Isaac Triguero
 */
public class CoTrainingAlgorithm extends PrototypeGenerationAlgorithm<CoTrainingGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected CoTrainingGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new CoTrainingGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes CoTraining algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        CoTrainingAlgorithm isaak = new CoTrainingAlgorithm();
        isaak.execute(args);
    }
}

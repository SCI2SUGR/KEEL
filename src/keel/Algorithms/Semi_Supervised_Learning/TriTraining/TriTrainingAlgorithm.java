//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.TriTraining;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * TriTraining algorithm calling.
 * @author Isaac Triguero
 */
public class TriTrainingAlgorithm extends PrototypeGenerationAlgorithm<TriTrainingGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected TriTrainingGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new TriTrainingGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes TriTraining algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        TriTrainingAlgorithm isaak = new TriTrainingAlgorithm();
        isaak.execute(args);
    }
}

//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.DE_TriTraining;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * DE_TriTraining algorithm calling.
 * @author Isaac Triguero
 */
public class DE_TriTrainingAlgorithm extends PrototypeGenerationAlgorithm<DE_TriTrainingGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected DE_TriTrainingGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new DE_TriTrainingGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes DE_TriTraining algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        DE_TriTrainingAlgorithm isaak = new DE_TriTrainingAlgorithm();
        isaak.execute(args);
    }
}

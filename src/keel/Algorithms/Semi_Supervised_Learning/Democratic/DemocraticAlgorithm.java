//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.Democratic;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * Democratic algorithm calling.
 * @author Isaac Triguero
 */
public class DemocraticAlgorithm extends PrototypeGenerationAlgorithm<DemocraticGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected DemocraticGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new DemocraticGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes Democratic algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        DemocraticAlgorithm isaak = new DemocraticAlgorithm();
        isaak.execute(args);
    }
}

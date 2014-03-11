//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.C45SSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * C45SSL algorithm calling.
 * @author Isaac Triguero
 */
public class C45SSLAlgorithm extends PrototypeGenerationAlgorithm<C45SSLGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected C45SSLGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new C45SSLGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes C45SSL algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        C45SSLAlgorithm isaak = new C45SSLAlgorithm();
        isaak.execute(args);
    }
}

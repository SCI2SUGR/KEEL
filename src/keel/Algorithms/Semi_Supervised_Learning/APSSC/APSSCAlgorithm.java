//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.APSSC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * APSSC algorithm calling.
 * @author Isaac Triguero
 */
public class APSSCAlgorithm extends PrototypeGenerationAlgorithm<APSSCGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected APSSCGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new APSSCGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes APSSC algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        APSSCAlgorithm isaak = new APSSCAlgorithm();
        isaak.execute(args);
    }
}

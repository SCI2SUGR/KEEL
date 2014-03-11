//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.SETRED;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * SETRED algorithm calling.
 * @author Isaac Triguero
 */
public class SETREDAlgorithm extends PrototypeGenerationAlgorithm<SETREDGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SETREDGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new SETREDGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes SETRED algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SETREDAlgorithm isaak = new SETREDAlgorithm();
        isaak.execute(args);
    }
}

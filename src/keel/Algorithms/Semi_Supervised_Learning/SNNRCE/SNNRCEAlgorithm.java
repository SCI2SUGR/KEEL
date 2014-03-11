//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.SNNRCE;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * SNNRCE algorithm calling.
 * @author Isaac Triguero
 */
public class SNNRCEAlgorithm extends PrototypeGenerationAlgorithm<SNNRCEGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SNNRCEGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new SNNRCEGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes SNNRCE algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SNNRCEAlgorithm isaak = new SNNRCEAlgorithm();
        isaak.execute(args);
    }
}

//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.CoBC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * CoBC algorithm calling.
 * @author Isaac Triguero
 */
public class CoBCAlgorithm extends PrototypeGenerationAlgorithm<CoBCGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected CoBCGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new CoBCGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes CoBC algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        CoBCAlgorithm isaak = new CoBCAlgorithm();
        isaak.execute(args);
    }
}

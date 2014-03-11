//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.SMOSSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * SMOSSL algorithm calling.
 * @author Isaac Triguero
 */
public class SMOSSLAlgorithm extends PrototypeGenerationAlgorithm<SMOSSLGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected SMOSSLGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new SMOSSLGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes SMOSSL algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        SMOSSLAlgorithm isaak = new SMOSSLAlgorithm();
        isaak.execute(args);
    }
}

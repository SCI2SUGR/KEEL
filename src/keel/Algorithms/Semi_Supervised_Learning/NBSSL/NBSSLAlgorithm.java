//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.NBSSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * NBSSL algorithm calling.
 * @author Isaac Triguero
 */
public class NBSSLAlgorithm extends PrototypeGenerationAlgorithm<NBSSLGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected NBSSLGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new NBSSLGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes NBSSL algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        NBSSLAlgorithm isaak = new NBSSLAlgorithm();
        isaak.execute(args);
    }
}

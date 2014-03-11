//
//  Main.java
//
//  Isaak Triguero
//
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Semi_Supervised_Learning.NNSSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.*;

import java.util.*;

/**
 * NNSSL algorithm calling.
 * @author Isaac Triguero
 */
public class NNSSLAlgorithm extends PrototypeGenerationAlgorithm<NNSSLGenerator>
{
    /**
     * Builds a new ChenGenerator.
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected NNSSLGenerator buildNewPrototypeGenerator(PrototypeSet train, PrototypeSet unlabeled, PrototypeSet test, Parameters params)
    {
       return new NNSSLGenerator(train, unlabeled, test, params);    
    }
    
     /**
     * Main method. Executes NNSSL algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        NNSSLAlgorithm isaak = new NNSSLAlgorithm();
        isaak.execute(args);
    }
}

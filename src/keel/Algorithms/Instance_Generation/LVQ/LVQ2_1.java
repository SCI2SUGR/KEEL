/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Instance_Generation.LVQ;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.*;

/**
 * LVQ2.1 algorithm
 * @author diegoj
 */
public class LVQ2_1 extends LVQ2
{
    /**
     * Construct an LVQ2.1 algorithm.
     * @param T training data set.
     * @param iterations Number of iterations to be performed.
     * @param alpha_0 Alpha constant of the algorithm.
     * @param windowWidth Window width parameter.
     */
    public  LVQ2_1(PrototypeSet T, int iterations, int n_prot, double alpha_0, double windowWidth)
    {
        super(T, iterations, n_prot, alpha_0, windowWidth);    
        algorithmName = "LVQ2.1";
    }
    
        /**
     * Construct an LVQ2.1 algorithm.
     * @param T training data set.
     * @param iterations Number of iterations to be performed.
     * @param pcNprot Size of reduced set as percentage of training size.
     * @param alpha_0 Alpha constant of the algorithm.
     * @param windowWidth Window width parameter.
     */
    public  LVQ2_1(PrototypeSet T, int iterations, double pcNprot, double alpha_0, double windowWidth)
    {
        super(T, iterations, pcNprot, alpha_0, windowWidth);    
        algorithmName = "LVQ2.1";
    }
    
    /**
     * Construct an LVQ2.1 algorithm.
     * @param T training data set.
     * @param parameters Parameters of the algorithms.
     */
    public  LVQ2_1(PrototypeSet T, Parameters parameters)
    {
        super(T, parameters);    
        algorithmName = "LVQ2.1";
    }
    
    /**
     * Corrects a prototype of a set. Non-Kohonen method.
     * @param i Prototype whose nearest-neighbors of same and different class will be modified.
     * @param tData Data set in which KNN operation will be performed.
     */
    protected void hardCorrect(Prototype i, PrototypeSet tData)
    {
        //nearest-neighboor with the same class as i. Kohonen calls it m_i
        Prototype nSameClass = KNN.getNearestWithSameClassAs(i, tData);
        //nearest-neighboor with different class as i. Kohonen calls it m_j
        Prototype nDifferentClass = KNN.getNearestWithDifferentClassAs(i, tData);
        //Debug.endsIfNull(i, "i es NULL");
        //Debug.endsIfNull(nSameClass, "nSameClass es NULL");
        //Debug.endsIfNull(nDifferentClass, "nDifferentClass es NULL");
        if(isInsideTheWindow(i, nSameClass, nDifferentClass))
        {
            reward(nSameClass,i);
            penalize(nDifferentClass,i);
        }
    }
    
    /**
     * Corrects a prototype of a set.
     * @param i Prototype whose nearest-neighbors of same and different class will be modified.
     * @param tData Data set in which KNN operation will be performed.
     */
    @Override
    protected void correct(Prototype i, PrototypeSet tData)
    {
        PrototypeSet nearest = KNN.getNearestNeighbors(i, tData, 2);
        Prototype n1 = nearest.get(0);
        Prototype n2 = nearest.get(1);
        Prototype m_c = null;
        Prototype m_d = null;
        boolean passed = false;
        if((n1.label() == i.label()  &&  n2.label() != i.label() ))
        {
            passed = true;
            m_c = n1;
            m_d = n2;            
        }
        else if(n2.label() == i.label()  &&  n1.label() != i.label() )
        {
            passed = true;
            m_d = n1;            
            m_c = n2;            
        }
        if(passed && isInsideTheWindow(i, m_c, m_d))
        {
                reward(m_c,i);
                penalize(m_d,i);
        }
    }
    
    /**
     * General main for all the prototoype generators
     * Arguments:
     * 0: Filename with the training data set to be condensed.
     * 1: Filename wich will contain the test data set
     * 3: k Number of neighbors used in the KNN function
     * @param args Arguments of the main function.
     */
    public static void main(String[] args)
    {
        Parameters.setUse("LVQ2_1", "<seed> <number of iterations> <% of prototypes> <alpha_0> <window width>");
        Parameters.assertBasicArgs(args);        
        
        PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        
        long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
        int iter = Parameters.assertExtendedArgAsInt(args,3,"number of iterations", 1, Integer.MAX_VALUE);
        double pcNprot = Parameters.assertExtendedArgAsDouble(args,4,Parameters.PERC_SIZE_TXT, 0, 100);
        double alpha_0 = Parameters.assertExtendedArgAsDouble(args,5,"alpha_0", 0, 1);
        double window_width = Parameters.assertExtendedArgAsDouble(args,6,"window width", 0, 1);
        
        LVQ2_1.setSeed(seed);
        LVQ2_1 generator = new LVQ2_1(training, iter, pcNprot, alpha_0, window_width);
        
    	PrototypeSet resultingSet = generator.execute();
        
    	//resultingSet.save(args[1]);
        
        int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
        generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
    }

}


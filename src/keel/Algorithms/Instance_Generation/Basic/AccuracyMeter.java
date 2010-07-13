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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.Algorithms.Instance_Generation.Basic;

//import keel.Algorithms.Preprocess.Instance_Generation.Basic.*;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;
import keel.Dataset.*;
import java.util.*;
import org.core.*;

/**
 * Measures classification accuracy of a reduced set.
 * That is, compute the percentage of instances well-classificated using as training set
 * the reduced set.
 * @author diegoj
 */
public class AccuracyMeter
{
    /** Posible yes variants of the command line. */
    protected static final String[] yes ={ "yes", "Yes", "YES", "Y", "yeah", "oh yeah" };
    
    /**
     * Informs if a string is equal to someone in an array
     * @param s String to be tested.
     * @param posibilities Posible values of s.
     * @return TRUE if s is in posibilities, FALSE in other chase.
     */
    protected static boolean isIn(String s, String[] posibilities )
    {
        boolean value = false;
        for(int i=0; !value  &&  i<posibilities.length; ++i)
            value = value || s.equals(posibilities[i]);
        return value;
    }    
    
    /**
     * Informs if a string is yes.
     * @param s String to be tested.
     * @return TRUE if s is some kind of yes, FALSE in other chase.
     */    
    protected static boolean isYes(String s)
    {
        return isIn(s, yes);
    }
    
    /**
     * Main. Measures the accuracy of a reduced set and saves results in a file.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        //Parameters.setUse("PrototypeGenerator", "<algorithmName used> <accuracy_output_FILE> [append]");
        Parameters.assertBasicArgs(args);
        Debug.endsIf(args.length<4, "Use: <training data set> <reduced set file> <test set file> <algorithmName used in reduction> <output file> [append]");
        String algoName = args[3];
        String outputFile = args[4];
        boolean append = false;
        if(args.length==6)
            append = isYes(args[5]);
        //Debug.errorln(args[0] + " tiene el conjunto de datos original");
        //Debug.errorln(args[1] + " tiene el conjunto de datos reducidos");
        //Debug.errorln(args[2] + " tiene el conjunto de datos test");
        PrototypeSet trainingSet = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
        PrototypeSet reducedSet = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
        PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[2]);
        PrototypeGenerator gen = new PrototypeGenerator(reducedSet);
        gen.execute();
        //Debug.errorln(args[1] + " executed");
        int accuracy1NN = KNN.classficationAccuracy1NN(reducedSet, test);
        String data = gen.getResults(Parameters.getFileName(), algoName, accuracy1NN, trainingSet.size(), test);
        if(append)
            KeelFile.append(outputFile, data);
        else
            KeelFile.write(outputFile, data);
        System.out.flush();
        //PrototypeGenerator.saveResultsOfAccuracyIn(Parameters.getFileName(), algoName, reducedSet, test, outputFile, append);
    }

}


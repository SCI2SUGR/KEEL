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

/** 
* <p> 
* @author Written by Luciano Sanchez (University of Oviedo) 21/01/2004 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 
package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier;
//import keel.Algorithms.Fuzzy_Rule_Learning.Shared.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Node.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;


public class FuzzyFGPClassifier extends Classifier {
/** 
* <p> 
* FuzzyFGPClassifier is designed to allow a Fuzzy Classifier evolve by means of
* an Genetic Programming (GP). This class is a specification of the class
* {@link Classifier}.
* 
* </p> 
*/ 
	//The rule base of the classifier
    NodeRuleBase R; 
 	//The class variable or output variable partitions or classes
    static FuzzyPartition C;
    
/** 
* <p> 
* Class constructor using the following parameters:
* </p> 
* @param pR      the {@link NodeRuleBase}
* @param c       the class variable {@link Fuzzypartition} 
*/ 	
    public FuzzyFGPClassifier( NodeRuleBase pR, FuzzyPartition c) {
        R=(NodeRuleBase)pR.clone();
        C=c.clone();
    }

/** 
* <p> 
* Copy constructor of this class, which clones its components
* </p> 
* @param cb      the {@link FuzzyFGPClassifier} to copy
*/ 	
    public FuzzyFGPClassifier(FuzzyFGPClassifier cb) {
        R=(NodeRuleBase)cb.R.clone();
        C=cb.C.clone();
    }

/** 
* <p> 
* This method copies the given FuzzyFGPClassifier in the current object.
* </p> 
* @param cb   the {@link FuzzyFGPClassifier} object to be assigned to the current one 
*/ 	
    public void set(FuzzyFGPClassifier cb) {
        R=(NodeRuleBase)cb.R.clone();
        C=cb.C.clone();
    }
    
/** 
* <p> 
* This method prints information about the Rule Base useful for debugging purposes
* </p>
*/
    public void debug() {
        R.debug();
    }
    
/** 
* <p> 
* This method clones the current object.
* </p> 
* @return       a {@link Classifier} object which is a perfect copy of the current one.
*/ 	
    public Classifier clone()  {
        return new FuzzyFGPClassifier(this); 
    }

/** 
* <p> 
* This method evaluates the classifier for a given input example.
* </p> 
* @param x      array of doubles with the example to evaluate the classifier
* @return       the double values array with the membership value of the example
*               to each one of the classes.
*/ 	
   public double[] evaluate(double []x) {
		FuzzyAlphaCut xfuzzy[] = new FuzzyAlphaCut[x.length];
		for (int i=0;i<x.length;i++) xfuzzy[i]=new FuzzyAlphaCut(new FuzzyNumberTRIANG(x[i],x[i],x[i]));
        R.replaceTerminals(xfuzzy);
        IntDouble[] result=R.CrispEval();
        double [] respuesta=new double[result.length];
        for (int i=0;i<result.length;i++) {
            respuesta[i]=result[i].weight;
        }
        return respuesta;
    }
    
}



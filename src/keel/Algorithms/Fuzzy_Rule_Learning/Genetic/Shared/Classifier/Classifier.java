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


public abstract class Classifier {
/** 
* <p> 
* Classifier is the base clase for all fuzzy rule learned classifier. This class
* is inherit by {@link FuzzyClassifier} and by {@link FuzzyFGPClassifier}.
* </p> 
*/ 

/** 
* <p> 
* abstract method for evaluating the classifier for a given input example.
* </p> 
* @param x      array of doubles with the example to evaluate the classifier
* @return       the double values array with the membership value of the example
*               to each one of the classes.
*/ 	
    public abstract double[]  evaluate(double []x);
/** 
* <p> 
* public method to obtain the class which performs with maximum membership value
* for the given example .
* </p> 
* @param  x     the example to determine the class with the highest membership
*               value
* @return       the index of the class with the highest membership value.
*/ 	
    public int getMaximum(double[] x) {
        double[] tmp=evaluate(x);
        int max=0;
        for (int i=1;i<tmp.length;i++)
            if (tmp[i]>tmp[max]) max=i;
        return max;
    }
/** 
* <p> 
* abstract method to clone the current object.
* </p> 
* @return       a Classifier object which is a perfect copy of the current one.
*/ 	
    public abstract Classifier clone();
/** 
* <p> 
* abstract method to print information useful for debugging purposes
* </p>
*/ 	
    public abstract void debug();
}


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
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

public class FuzzyClassifier extends Classifier {
/** 
* <p> 
* FuzzyClassifier is designed to allow a Fuzzy Classifier evolve by means of
* an Genetic Algorithm (GA). This class is a specification of the class
* {@link Classifier}.
* 
* </p> 
*/ 
	//The rule base of the classifier
    RuleBase R;

/** 
* <p> 
* Class constructor using the following parameters:
* </p> 
* @param a      the input variables {@link FuzzyPartition} array
* @param b      the class variable {@link Fuzzypartition} 
* @param tn     the t-norm to be used
* @param ag     the aggregation operator
*/ 	
    public FuzzyClassifier(FuzzyPartition[] a,FuzzyPartition b, int tn, int ag) {
        R=new RuleBase(a,b,tn,ag);
    }

/** 
* <p> 
* Copy constructor of this class, which clones its components
* </p> 
* @param c      the {@link FuzzyClassifier} to copy
*/ 	
    public FuzzyClassifier(FuzzyClassifier c) {
        R=c.R.clone();
    }

/** 
* <p> 
* This method copies the given FuzzyClassifier in the current object.
* </p> 
* @param c   the {@link FuzzyClassifier} object to be assigned to the current one 
*/ 	
    public void set(FuzzyClassifier c) {
        R=c.R.clone();
    }
    
 /** 
* <p> 
* This method evaluates the classifier for a given input example.
* </p> 
* @param x      array of doubles with the example to evaluate the classifier
* @return       the double values array with the membership value of the example
*               to each one of the classes.
*/ 	
    public double[] evaluate(double [] x) {
        return R.output(x);
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
* @return a {@link Classifier} object which is a perfect copy of the current one.
*/ 	
    public Classifier clone()  {
        return new FuzzyClassifier(this);
    }
    
/** 
* <p> 
* This method returns the {@link RuleBase} size.
* </p> 
* @return the desired size value.
*/ 	
    public int size() {
        return R.size();
    }
    
/** 
* <p> 
* This method returns the {@link RuleBase} number of consequents.
* </p> 
* @return    the desired size value.
*/ 	
    public int getNumConsequents() {
        return R.numConsequents();
    }
    
/** 
* <p> 
* This method returns a {@link FuzzyRule} component.
* </p> 
* @return    the desired {@link FuzzyRule} component.
*/ 	
    public FuzzyRule getComponent(int n) {
        return R.getComponent(n);
    }

/** 
* <p> 
* This method sets a given {@link FuzzyRule} in the {@link RuleBas}.
* </p> 
* @param n    the index of the component to set
* @param t    the new {@link FuzzyRule} to be introduced.
*/ 	
    public void setComponent(int n, FuzzyRule r) {
        R.setComponent(n,r.clone());
    }
    
};


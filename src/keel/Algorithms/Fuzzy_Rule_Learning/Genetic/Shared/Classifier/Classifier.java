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

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

package keel.Algorithms.Complexity_Metrics;

import  keel.Dataset.*;
import java.util.*;


/**
 * This is the main class of the Statistics computation
 * 
 * <p>
 * @author Written by Nuria Macia (La Salle, Universitat Ramon Llull) 27/05/2010 and
 *         modified by Albert Orriols (La Salle, Universitat Ramon Llull) 31/05/2010.
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class Statistics {
	
/** Dataset **/
    keel.Dataset.InstanceSet dSet;
	
/** Classes */  
    private Vector classValues;

/** Instances per class */
    private int[] numInstancesPerClass;	

/** Means */  
    private double[][] mean;

/** Variances */  
    private double[][] variance;

/** Maximum */
    private double[][] maximum;

/** Minimum */
    private double[][] minimum;

/** Number of classes */
    private int numberOfClasses;

/** Number of attributes */
    private int numberOfAttributes;

Statistics ( InstanceSet _dSet, int _numberOfClasses ) {

    int i, j;
    
    dSet = _dSet;	

    numberOfAttributes = Attributes.getNumAttributes() - 1;
    numberOfClasses = _numberOfClasses;
    classValues = new Vector();
    mean = new double [ numberOfAttributes ][ numberOfClasses ];
    numInstancesPerClass = new int [ numberOfClasses ];
    variance = new double [ numberOfAttributes ][ numberOfClasses ];
    maximum = new double [ numberOfAttributes ][ numberOfClasses ];
    minimum = new double [ numberOfAttributes ][ numberOfClasses ];

    // Initialize variables
    for ( i = 0; i < numberOfAttributes; i++ ) {
        for ( j = 0; j < numberOfClasses; j++ ) {
            mean[i][j] = 0.0;
            variance[i][j] = 0.0;
            maximum[i][j] = Double.MIN_VALUE;
            minimum[i][j] = Double.MAX_VALUE;
        }
    }

    for ( i = 0; i < numberOfClasses; i++ ) {
        numInstancesPerClass[i] = 0;
    }

} // end Statistics 

/**
 * It computes the statistics for the given parameters
 * 
 * @param example Examples normalized from the KEEL Data set
 * @param classOfExample Class of each normalized example
 * @param numberOfExamples Number of examples in the data set
 * @param numberOfAttributes Number of attributes
 */
public void run ( double [][]example, int []classOfExample, int numberOfExamples, int numberOfAttributes ) {

    runClassValues( example, classOfExample, numberOfExamples, numberOfAttributes );

    runMinMax( example, classOfExample, numberOfExamples, numberOfAttributes );

    runMeanComputation( example, classOfExample, numberOfExamples, numberOfAttributes );

    runVarianceComputation( example, classOfExample, numberOfExamples, numberOfAttributes );

} // end run


private void runMeanComputation( double [][]example, int []classOfExample, int numberOfExamples, int numberOfAttributes ) {

    int i, j;
	
    for ( i = 0; i < numberOfExamples; i++ ) {
        for ( j = 0; j < numberOfAttributes; j++ ) {
            mean[j][ classOfExample[i] ] += example[i][j];
        }
    }

    for ( i = 0; i < numberOfAttributes; i++ ) {
        for ( j = 0; j < numberOfClasses; j++ ) {
            mean[i][j] /= (double) numInstancesPerClass[j]; 
        }	
    } 

} // end runMeanComputation


private void runClassValues( double [][]example, int []classOfExample, int numberOfExamples, int numberOfAttributes ) {

    int i;
    String classValue;
	
    for ( i = 0; i < dSet.getNumInstances(); i++ ) {
        classValue = dSet.getInstance(i).getOutputNominalValues(0);
        if ( !( classValues.contains( classValue ) ) ) { 
            classValues.add( classValue ); 
        }	
    }

    for ( i = 0; i < numberOfExamples; i++  ) {
        numInstancesPerClass[ classOfExample[i] ] ++;
    }
		
} // end runClassValues


private void runMinMax( double [][]example, int []classOfExample, int numberOfExamples, int numberOfAttributes ) {

    int i, j;
    int whichClass;

    for ( j = 0; j < numberOfAttributes; j++ ) {
        for ( i = 0; i < numberOfClasses; i++ ) {
            maximum[j][i] = Double.MIN_VALUE;
            minimum[j][i] = Double.MAX_VALUE;
        }
    }

    for ( i = 0; i < numberOfExamples; i++ ) {
        for ( j = 0 ; j < numberOfAttributes; j++ ) {
            maximum[j][ classOfExample[i] ] = Math.max( maximum[j][ classOfExample[i] ], example[i][j] );
            minimum[j][ classOfExample[i] ] = Math.min( minimum[j][ classOfExample[i] ], example[i][j] );
        }
    } 

} // end runMinMax


private void runVarianceComputation( double [][]example, int []classOfExample, int numberOfExamples, int numberOfAttributes ) {

    int i, j;
    double [][] sumOfSquareValues;
    double [][] sumOfValues;	

    for ( i = 0; i < numberOfExamples; i++ ) {
        for ( j = 0; j < numberOfAttributes ; j++ ) {
            variance[j][ classOfExample[i] ] += Math.pow( example[i][j] - mean[j][ classOfExample[i] ], 2 );
        }  		
    }

    for ( i = 0; i < numberOfAttributes; i++ ) {
	for ( j = 0; j < numberOfClasses; j++ ) {
	    variance[i][j] /= numInstancesPerClass[j] - 1;
        }
    }

} // end runVarianceComputation


/**
 * It returns the variance of the given attribute within the given class
 * 
 * @param whichAttribute index of the attribute
 * @param whichClass index of the class
 * @return variance of the given attribute within the given class
 */
public double getVariance ( int whichAttribute, int whichClass ) {
    return variance[ whichAttribute ][ whichClass ];
} // end getVariance


/**
 * It returns the mean of the given attribute within the given class
 * 
 * @param whichAttribute index of the attribute
 * @param whichClass index of the class
 * @return mean of the given attribute within the given class
 */
public double getMean ( int whichAttribute, int whichClass ) {
    return mean[ whichAttribute ][ whichClass ];
} // end geMean


/**
 * It returns the maximum of the given attribute within the given class
 * 
 * @param whichAttribute index of the attribute
 * @param whichClass index of the class
 * @return maximum of the given attribute within the given class
 */
public double getMax ( int whichAttribute, int whichClass ) {
    return maximum[ whichAttribute ][ whichClass ];
} // end getMax


/**
 * It returns the minimum of the given attribute within the given class
 * 
 * @param whichAttribute index of the attribute
 * @param whichClass index of the class
 * @return minimum of the given attribute within the given class
 */
public double getMin ( int whichAttribute, int whichClass ) {
    return minimum[ whichAttribute ][ whichClass ];
} // end getMin

} // end Statistics 
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

import java.util.*;
import java.io.*;


/**
 * This is the main class of the ComplexityMetrics library
 * 
 * <p>
 * @author Written by Albert Orriols and Nuria Macia (La Salle, Universitat Ramon Llull) 27/05/2010
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class ComplexityMetrics {
	
/** Dataset **/
    private keel.Dataset.InstanceSet dSet;

/** Number of examples in the data set */
    private int numberOfExamples;

/** Number of attributes in the data set */
    private int numberOfAttributes;

/** Examples normalized from the KEEL Data set*/
    private double [][] example;

/** Class of each normalized example */
    private int [] classOfExample;

/** Number of classes */
    private int numberOfClasses;

/** Number of examples per class */
    private int []numExamplesPerClass;

/** Examples organized per class (index examplesPerClass[class][instance]). */
    private double  [][][]examplesPerClass;

/** Corresponding position of the instance in the example vector (index indexExamplesPerClass[class][instance]). */
    private int [][]indexExamplesPerClass;

/** Statistics **/
    private Statistics stats;

/** Training file */
    private String datasetName;

/** Output file name */
    private String outputFileName;

/** Result of F1 */  
    private double F1;

/** Result of F2 */  
    private double F2;

/** Result of F3 */  
    private double F3;

/** Result of N1 */  
    private double N1;

/** Result of N2 */  
    private double N2;

/** Result of N3 */  
    private double N3;

/** Result of N4 */  
    private double N4;

/** Result of L1 */  
    private double L1;

/** Result of L2 */  
    private double L2;

/** Result of L3 */  
    private double L3;

/** Result of T1 */
    private double T1;

/** Result of T2 */
    private double T2;

/** Random numbers generator */
    private Random rndObject;

/** Seed of the random object */
    private double seed;

/** Indicates whether F1 has to be computed */
    private boolean computeF1;

/** Indicates whether F2 has to be computed */
    private boolean computeF2;

/** Indicates whether F3 has to be computed */
    private boolean computeF3;

/** Indicates whether N1 has to be computed */
    private boolean computeN1;

/** Indicates whether N2 has to be computed */
    private boolean computeN2;

/** Indicates whether N3 has to be computed */
    private boolean computeN3;

/** Indicates whether N4 has to be computed */
    private boolean computeN4;

/** Indicates whether L1 has to be computed */
    private boolean computeL1;

/** Indicates whether L2 has to be computed */
    private boolean computeL2;

/** Indicates whether L3 has to be computed */
    private boolean computeL3;

/** Indicates whether T1 has to be computed */
    private boolean computeT1;

/** Indicates whether T2 has to be computed */
    private boolean computeT2;

/** Parameter C for SVM training */
    final double C = 0.05;

/** Parameter TOLERANCE for SVM training */
    final double TOLERANCE = 0.001;

/** Parameter EPSILON for SVM training */
    final double EPSILON = 0.001;

ComplexityMetrics ( String configFileName ) {

    System.out.println ( " > Creating the complexity metrics object with the configuration file: " + configFileName );

    // Initialize parameters
    numExamplesPerClass = null;
    seed = 1;
    rndObject = new Random();

    computeF1 = computeF2 = computeF3 = computeN1 = computeN2 = computeN3 = computeN4 = true;
    computeL1 = computeL2 = computeL3 = computeT1 = computeT2 = true;

    F1 = F2 = F3 = N1 = N2 = N3 = N4 = L1 = L2 = L3 = T1 = T2 = -1.;

    //Initializing the names of the output file and the dataset
    outputFileName = datasetName = null;

    // Parse the configuration file
    parseConfigFile( configFileName );
    rndObject.setSeed ( (long) seed );
    
    // Create the data set
    dSet = new keel.Dataset.InstanceSet ();

    // Read the data set
    try {
        dSet.readSet ( datasetName, true );
    } catch ( Exception e ) {
        System.out.println ( "  > The data set could not be correctly loaded " );
        e.printStackTrace();
    }     

    extractDatasetInformation();

    // Make statistics
    stats = new Statistics ( dSet, numberOfClasses );
    stats.run ( example, classOfExample, numberOfExamples, numberOfAttributes );

} // end ComplexityMetrics


/**
 * Parse the configuration file
 * @attribute configFileName is the name of the configuration file
 */
private void parseConfigFile ( String configFileName ) {
    String line, varName, aux;

    try{
        System.out.println ( " > Parsing the file: " + configFileName );
        BufferedReader fin = new BufferedReader ( new FileReader(configFileName) );

        line = fin.readLine();
        while ( line != null ) {
            if ( line.length() == 0 ) {
                line = fin.readLine();
                continue;
            }

            StringTokenizer st = new StringTokenizer( line );

            varName = st.nextToken();
            st.nextToken(); // Disregarding '='

            if ( varName.equalsIgnoreCase("algorithm") ) {
            // Do not store the algorithm name

            } else if ( varName.equalsIgnoreCase( "inputdata" ) ) {
                aux = st.nextToken();
                datasetName = aux.substring ( 1, aux.length() - 1 );
                System.out.println ( "   > Input data set:  " + datasetName );
            } else if ( varName.equalsIgnoreCase ( "outputdata" ) ) {
                aux = st.nextToken();
                outputFileName = aux.substring ( 1,aux.length() - 1 );
                System.out.println ( "   > Output data set:  " + outputFileName );
            } else if ( varName.equalsIgnoreCase ( "runF1" ) ) {
                computeF1 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run F1:  " + computeF1 );
            } else if ( varName.equalsIgnoreCase ( "runF2" ) ){
                computeF2 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run F2:  " + computeF2 );
            } else if ( varName.equalsIgnoreCase ( "runF3" ) ) {
                computeF3 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run F3:  " + computeF3 );
            } else if ( varName.equalsIgnoreCase ( "runN1" ) ) {
                computeN1 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run N1:  " + computeN1 );
            } else if ( varName.equalsIgnoreCase ( "runN2" ) ) {
                computeN2 = Boolean.parseBoolean( st.nextToken() );
                System.out.println ( "   > Run N2:  " + computeN2 );
            } else if ( varName.equalsIgnoreCase ( "runN3" ) ) {
                computeN3 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run N3:  " + computeN3 );
            } else if ( varName.equalsIgnoreCase ( "runN4" ) ) {
                computeN4 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run N4:  " + computeN4 );
            } else if ( varName.equalsIgnoreCase ( "runL1" ) ) {
                computeL1 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run L1:  " + computeL1 );
            } else if ( varName.equalsIgnoreCase ( "runL2" ) ) {
                computeL2 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run L2:  " + computeL2 );
            } else if ( varName.equalsIgnoreCase ( "runL3" ) ) {
                computeL3 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run L3:  " + computeL3 );
            } else if ( varName.equalsIgnoreCase ( "runT1" ) ) {
                computeT1 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run T1:  " + computeT1 );
            } else if ( varName.equalsIgnoreCase ( "runT2" ) ) {
                computeT2 = Boolean.parseBoolean ( st.nextToken() );
                System.out.println ( "   > Run T2:  " + computeT2 );
            } else if ( varName.equalsIgnoreCase ( "seed" ) ) {
                seed = Double.parseDouble ( st.nextToken() );
                System.out.println ( "   > Seed:  " + seed );
            }

            // Read next line
            line = fin.readLine();
        }
    } catch ( Exception e ) {
        System.err.println ( "Error reading the configuration file" );
        e.printStackTrace();
    }

} // end configFileName


/**
 * Extract the information from the data set in order to quickly process 
 * the complexity measures.
 */
private void extractDatasetInformation(){

    // Getting information about attributes and examples
    numberOfExamples = dSet.getNumInstances();
    numberOfAttributes = keel.Dataset.Attributes.getInputNumAttributes();

    example = new double [ numberOfExamples ][ numberOfAttributes ];
    classOfExample = new int [ numberOfExamples ];
    numberOfClasses = 2;

    // Getting all the instances
    for ( int i = 0; i < numberOfExamples; i++) {
        example[i] = dSet.getInstance(i).getNormalizedInputValues();
        classOfExample[i] = (int) dSet.getInstance(i).getNormalizedOutputValues()[0];
        if ( numberOfClasses < classOfExample[i] + 1 ) {
            numberOfClasses = classOfExample[i] + 1;
        }
    }

    System.out.println ( " \n\n\n " );
    System.out.println ( "  > The attributes number (without counting the class attribute) is: " + numberOfAttributes );
    System.out.println ("  > The examples number is: " + numberOfExamples );
    
} // end getInstancesInformation

  
void run () {

    if ( computeF1 ) runF1();

    if ( computeF2 ) runF2();

    if ( computeF3 ) runF3();

    if ( computeN1 ) runN1();

    if ( computeN2 ) runN2();

    if ( computeN3 ) runN3();

    if ( computeN4 ) runN4();

    if ( computeL1 ) runL1();

    if ( computeL2 ) runL2();

    if ( computeL3 ) runL3();

    if ( computeT1 ) runT1();

    if ( computeT2 ) runT2();

    dumpResultsToScreen();
    writeMetricsToFile();

} // end run


void dumpResultsToScreen () {
    System.out.println ( " \n\n > Results of the complexity metrics: " );

    if ( computeF1 ) System.out.println ( " F1: " + F1 );
    if ( computeF2 ) System.out.println ( " F2: " + F2 );
    if ( computeF3 ) System.out.println ( " F3: " + F3 );
    if ( computeN1 ) System.out.println ( " N1: " + N1 );
    if ( computeN2 ) System.out.println ( " N2: " + N2 );
    if ( computeN3 ) System.out.println ( " N3: " + N3 );
    if ( computeN4 ) System.out.println ( " N4: " + N4 );
    if ( computeL1 ) System.out.println ( " L1: " + L1 );
    if ( computeL2 ) System.out.println ( " L2: " + L2 );
    if ( computeL3 ) System.out.println ( " L3: " + L3 );
    if ( computeT1 ) System.out.println ( " T1: " + T1 );
    if ( computeT2 ) System.out.println ( " T2: " + T2 );

} // end dumpResultsToScreen


void writeMetricsToFile () {

    PrintWriter fout = null;

    try {
        fout = new PrintWriter(new BufferedWriter(new FileWriter( outputFileName )));

        fout.println ( "Find below the results of the complexity metrics: \n" );

        if ( computeF1 ) fout.println ( " F1: " + F1 );
        if ( computeF2 ) fout.println ( " F2: " + F2 );
        if ( computeF3 ) fout.println ( " F3: " + F3 );
        if ( computeN1 ) fout.println ( " N1: " + N1 );
        if ( computeN2 ) fout.println ( " N2: " + N2 );
        if ( computeN3 ) fout.println ( " N3: " + N3 );
        if ( computeN4 ) fout.println ( " N4: " + N4 );
        if ( computeL1 ) fout.println ( " L1: " + L1 );
        if ( computeL2 ) fout.println ( " L2: " + L2 );
        if ( computeL3 ) fout.println ( " L3: " + L3 );
        if ( computeT1 ) fout.println ( " T1: " + T1 );
        if ( computeT2 ) fout.println ( " T2: " + T2 );

        fout.close();
    } catch ( Exception e ) {
        System.err.println ( " > [ERROR]: Printing the results to the output file: " + outputFileName );
    }

} // end writeMetricsToFile


void runF1() {

    int i;
    double fisher;

    F1 = Double.MIN_VALUE;

    if ( numberOfClasses > 2 ) {
        System.out.println ( " >> [WARNING] This metric is devised for two-class problems. Only the two first classes of the problem will be considered " );
    }

    // Compute Fisher's discriminant for each attribute
    for ( i = 0; i < numberOfAttributes; i++ ) {
        fisher =  Math.pow( stats.getMean( i, 0 ) - stats.getMean( i, 1 ), 2 ) /
                  ( stats.getVariance( i, 0 ) + stats.getVariance( i, 1 ) ) ;
		
        if ( fisher > F1 ) {
            F1 = fisher; 
        }	
    }
	
    System.out.println( " F1: " + F1 ) ;

} // end runF1


void runF2() {

    int i;

    double minmin;
    double minmax;
    double maxmin;
    double maxmax;

    if ( numberOfClasses > 2 ) {
        System.out.println ( " >> [WARNING] This metric is devised for two-class problems. Only the two first classes of the problem will be considered " );
    }

    F2 = 1.0;

    // Compute the volume overlap	
    for ( i = 0; i < numberOfAttributes; i++ ) {
        minmin = Math.min( stats.getMin( i, 0 ), stats.getMin( i, 1 ) );
        minmax = Math.min( stats.getMax( i, 0 ), stats.getMax( i, 1 ) ); 
        maxmin = Math.max( stats.getMin( i, 0 ), stats.getMin( i, 1 ) );
        maxmax = Math.max( stats.getMax( i, 0 ), stats.getMax( i, 1 ) );	
        F2 *= ( minmax - maxmin ) / ( maxmax - minmin ); 
   } 

    System.out.println( " F2: " + F2 ) ;

} // end runF2


void runF3() {

    int i, j;
    int bestAtt;
    boolean finish = false;
    double overlapMin, overlapMax;
    double[] discPower;
    double[] initialDiscPower;        // discPower and initialDiscPower variables store the discriminative power of each attribute
    double[] cumulDiscPower;          // Cumulative discriminant power (considering more discriminant variables)
    int[] order;                      // Maintains the order of the attributes
    boolean[] pointDisc;              // Indicates whether the point has been discriminated
    int numAttRemain, numExRemain;    // Number of attributes and examples that remain to process

    int mostDiscrAtt = 0;
    double discPowerOfTheBest = 0.0;

    // Metric only applicable to 2-class problems
    if ( numberOfClasses != 2 ) {
        System.out.println ( " > [WARNING in Feature Efficiency] Applying Maximum efficiency to a " + numberOfClasses + "-class data set. " );
    }

    // Organize examples per class
    organizePerClass();

    // Initialize variables
    numAttRemain     = numberOfAttributes;
    numExRemain      = numberOfExamples;
    initialDiscPower = new double  [ numberOfAttributes ];
    discPower        = new double  [ numberOfAttributes ];
    cumulDiscPower   = new double  [ numberOfAttributes ];
    order            = new int     [ numberOfAttributes ];

    for ( i = 0; i < numberOfAttributes; i++ ) {
        discPower[i]        = 0;
        initialDiscPower[i] = 0;
        cumulDiscPower[i]   = 0;
        order[i]            = i;
    }

    pointDisc = new boolean [ numberOfExamples ];

    for ( i = 0; i < numberOfExamples;  i++ ) {
        pointDisc[i] = false;
    }

    while ( !finish ) {
	
        // Get the discriminative power of each of the remaining attributes 
        finish = getDiscriminativePowerOfAttributes ( discPower, order, numAttRemain, pointDisc );

        // If we are in the first iteration, we store the initial discriminatory power of each attribute
        if ( numAttRemain == numberOfAttributes ) {
            for ( i = 0; i < numberOfAttributes; i++ ) {
                initialDiscPower[i] = discPower[i];
            }
        }

        // Order the attributes depending on their discriminatory power
        quickSort ( discPower, order, 0, numAttRemain - 1 );

        // Update the cumulative disciminatory power of the best attribute
        cumulDiscPower[ order[ numAttRemain - 1 ] ] = discPower [ numAttRemain - 1 ];

        // Store the most discriminative attribute if it is the first iteration
        if ( numAttRemain == numberOfAttributes ) {
           // The most discriminative attribute is the last in the vector.
           mostDiscrAtt = order[ numAttRemain - 1 ];
           discPowerOfTheBest = (double) discPower [ numAttRemain - 1 ] / (double) numberOfExamples;
        }

        // Use the first attribute. So, one less attribute remaining
        numAttRemain --;

        // Reset the discriminatory power of unused variables
        for ( i = 0; i < numAttRemain; i++ ) {
            discPower [i] = 0;
        }

        // Check the number of examples that we can discriminate according to the most discriminative attribute.
        bestAtt = order[ numAttRemain ];

        overlapMin = Math.max ( stats.getMin( bestAtt, 0 ), stats.getMin( bestAtt, 1 ) );
        overlapMax = Math.min ( stats.getMax( bestAtt, 0 ), stats.getMax( bestAtt, 1 ) );

        for ( i = 0; i < numberOfExamples; i++ ) {
            if ( !pointDisc[i] &&
               ( example[i][ bestAtt ] < overlapMin ||
                 example[i][ bestAtt ] > overlapMax ) ) {
                pointDisc[i] = true;
                numExRemain --;
            }
        }
        if ( numExRemain == 0 || numAttRemain == 0 ) finish = true;
    }

    F3 = discPowerOfTheBest;

    System.out.println ( " F3: " + F3 );

} // end runF3 


private boolean getDiscriminativePowerOfAttributes ( double[] discPower, int[] order, int numAttRemain, boolean[] pointDisc ) {

    int i, j, att;
    double overlapMin, overlapMax;
    boolean finish = false;

    for ( j = 0; j < numAttRemain; j++ ) {

        att = order[j];

        // Get the maximum of the minimums, and the minimum of the maximums
        overlapMin = stats.getMin( att, 0 );
        overlapMax = stats.getMax( att, 0 );

        for ( i = 1; i < 2; i++ ) {
            if ( stats.getMin( att, i ) > overlapMin ) {
                overlapMin = stats.getMin( att, i );
            }
            if ( stats.getMax( att, i ) < overlapMax ) {
                overlapMax = stats.getMax( att, i );
            }
        }

        if ( overlapMin > overlapMax ) { // The attribute completely discriminates all the examples per class.
            discPower[j] = (double) numberOfExamples;

            // Substract all the examples that have been discriminated.
            for ( i = 0; i < numberOfAttributes; i++ ) {
                if ( i != j ) {
                    discPower[j] -= discPower[i];
                }
            }
            finish = true;
        }
        else { // Count the number of examples that are truly discriminated by the attribute.

            for ( i = 0; i < numberOfExamples; i++ ) {
                if ( !pointDisc[i] &&
                   ( example[i][ att ] < overlapMin ||
                     example[i][ att ] > overlapMax ) ) {
                    discPower[j] ++;
                }
            }
        }
    }

    return finish;
   
} // end getDiscriminativePowerOfAttributes


private void runN1() {

    int different = 0;
    int[] node;
    int[][] spanTree = new int [ numberOfExamples - 1 ][2];

    for ( int i = 0; i < numberOfExamples - 1; i++ ) {
        for ( int j = 0; j < 2; j++ ) {
            spanTree[i][j] = 0;
        } 
    }

    spanTree = computePrim();
	
    node = new int [ numberOfExamples ];

    for ( int i = 0; i < numberOfExamples; i++ ) {
        node[i] = -1;
    }

    // Store the nodes of the spanning tree with different class
    for ( int i = 0; i < numberOfExamples - 1; i++ ) {
        if ( classOfExample[ spanTree[i][0] ] != classOfExample[ spanTree[i][1] ] ) {
            node[ spanTree[i][0] ] = 0;
            node[ spanTree[i][1] ] = 0;
        }
    }

    // Compute the number of nodes of the spanning tree with different class
    for ( int i = 0; i < numberOfExamples; i++ ) {
        if ( node[i] == 0 ) different ++;
    }

    N1 = (double) different / (double) numberOfExamples;

    System.out.println( " N1: " + N1 );

} // end runN1


private int selectMinNotTreated ( int[] neig, double[] edge ) {

    int i;
    int min = -1;
    double distMin = Double.MAX_VALUE;

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( ( neig[i] != -1 ) && ( edge[i] < distMin ) ) {
            distMin = edge[i];
            min = i;
        }
    }

    return min;

} // end selectMinNotTreated

private double getApproximateDistance ( int ex1, int ex2 ) {

    int att;
    double dist = 0;

    for ( att = 0; att < numberOfAttributes; att++ ) {
        dist += Math.pow ( (double)( example[ ex1 ][ att ] - example[ ex2 ][ att ]), (double)2 );
    }

    return dist;

} // end getApproximateDistance

/**
 * <p>
 * It computes the minimum spanning tree of the dataset
 * </p> 
 * 
 * @return the minimum spanning tree (pairs of connected examples)
 */
private int[][] computePrim () {

    int currentNode;
    int i, j;
    int spanTreeIndex = 0;

    // Spanning tree: pairs of examples
    int[][] spanTree = new int[ numberOfExamples - 1 ][2];

    // Structures to maintain the neighbor closer and the edge to this neighbor
    int[]   neig   = new int   [ numberOfExamples ];
    double[] edge  = new double [ numberOfExamples ];

    // Choose a vertex, which will be the seed of the spanning tree: example 0
    currentNode = 0;

    // Initialize the structures considering that we have a complete GRAPH (all nodes connected)
    neig[ currentNode ] = -1; // Indicates that the node has been processed
    edge[ currentNode ] = 0;

    for ( i = 1; i < numberOfExamples; i++ ) {
        neig[i] = currentNode;
        edge[i] = getApproximateDistance ( currentNode, i );
    }

    // Create the minimum spanning tree (MST)
    for ( i = 1; i < numberOfExamples; i++ ) {

        // Select the vertex, not treated yet, with minimum distance
        currentNode = selectMinNotTreated ( neig, edge );

        // Add this vertex to the spanning tree
        spanTree[ spanTreeIndex ][0] = currentNode;
        spanTree[ spanTreeIndex ][1] = neig[ currentNode ];
        spanTreeIndex ++;

        // Check the vertex as processed
        neig[ currentNode ] = -1;

        // Recalculate the distances of nearest neighbors.
        for ( j = 0; j < numberOfExamples; j++ ) {
            if ( neig[j] != -1 && edge[j] > getApproximateDistance ( currentNode, j ) ) {
                neig[j] = currentNode;
                edge[j] = getApproximateDistance ( currentNode, j );
            }
        }
    }

    return spanTree;

} // end computePrim


private void runN2() {

    int i, j;
    int neigIntra, neigInter; 
    double minDistIntra, minDistInter;
    double distIntraClass, distInterClass;
    double distAux;
	
    distIntraClass = 0;
    distInterClass = 0;
	
    for ( i = 0; i < numberOfExamples; i++ ) {
 
        // Get nearest neighbor intra and inter class 
        distAux = 0;
        neigInter = -1;
        neigIntra = -1;
        minDistInter = Double.MAX_VALUE;
        minDistIntra = Double.MAX_VALUE;

        for ( j = 0; j < numberOfExamples; j++ ) {
            if ( j != i ) {
                distAux = getDistance ( i, j );
                if ( classOfExample[j] == classOfExample[i] &&  distAux < minDistIntra ) {
                    neigIntra = j;
                    minDistIntra = distAux;
                }
                else if ( classOfExample[j] != classOfExample[i] && distAux < minDistInter ) {
                    neigInter = j;
                    minDistInter = distAux;
                }
           }
        }

        if ( neigInter == -1 ) {
            minDistInter = 0;
        }
        if ( neigIntra == -1 ) {
            minDistIntra = 0;
        }

        distIntraClass += minDistIntra;
        distInterClass += minDistInter;
    }
    
    if ( distInterClass != 0 ) {
        N2 = distIntraClass / distInterClass ;
        System.out.println ( " N2: " + N2 );
    }
    else {
        System.out.println ( " Error: " ) ;
    }

} // end runN2


private int getNearestNeighborOfExample ( int example, double minDist ) {

    int i;
    int neig = -1;
    minDist = Double.MAX_VALUE;

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( i != example && getDistance ( example, i ) < minDist ) {
            neig = i;
            minDist = getDistance ( example, i );
        }
    }

    return neig;

} // end getNearestNeighborOfExample


void runN3() {

    //  Run the KNN algorithm on the train set.
    System.out.println ( "\n\n  > Running N3: Testing the KNN with the train instances \n" );
    N3 = runKNN ( 1, example, classOfExample, numberOfExamples, true );

    System.out.println ( " N3: " + N3 );

} // end runN3 



double runKNN ( int k, double[][] testExamples, int[] classOfTestExamples, int numberOfTestExamples, boolean isTrain ) {

    int i, j;
    double dist;
    double minDist = 10000000;
    int minIndex = 0;
    int numCorrect = 0;
    
    for ( i = 0; i < numberOfTestExamples; i++ ) {
        minDist = 10000000;
        minIndex = 0;
        for ( j = 0; j < numberOfExamples; j++ ) {

            if ( !isTrain || i != j ) {
                
                dist = getApproximateDistance ( testExamples[i], example[j] );
                
                if ( dist < minDist ) {
                    minIndex = j;
                    minDist = dist;
                }
                
            }
        }

        if ( classOfTestExamples[i] == classOfExample[ minIndex ] ) {
            numCorrect ++;
        }

    }

    return (  1 - ( (double)numCorrect / (double)numberOfTestExamples) );

} // end runKNN


/**
 * 
 * It returns the approximate distance between 2 examples
 * 
 * @param ex1 the first example
 * @param ex2 the second example
 * @return the distance between the 2 examples
 */
double getApproximateDistance ( double[] ex1, double[] ex2 ) {

    int i;
    double dist = 0;

    for ( i = 0; i < numberOfAttributes; i++ ) {
        dist += Math.pow ( ex1[i] - ex2[i], 2.0 );
    }

    return dist;

} // end getApproximateDistance


double getDistance ( double[] ex1, double[] ex2 ) {
    return ( Math.pow ( getApproximateDistance( ex1, ex2 ), 0.5 ) );
} // end getDistance


double getDistance ( int ex1, int ex2 ){
    return ( Math.pow ( getApproximateDistance ( example[ ex1 ], example[ ex2 ] ), 0.5 ) );
} // end getDistance


void runN4() {

    double [][] testExamples;
    int [] classOfTestExamples;
    int cClass;

    int numInstToGeneratePerClass = 1000;

    // 0. Check that all classes have examples.
    organizePerClass ();

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        if ( numExamplesPerClass[cClass] < 1 ) {
            System.err.println ( "      > [ERROR in N4] Error in computing the nonlinearity of the KNN classifier. " );
            System.err.println ( "        >> Class " + cClass + " has 0 instances. " );
            N4 = -1;
            return;
        }
    }

    // 1. Create the convex hull.
    System.out.println ( "      > Generating " + numInstToGeneratePerClass + " by means of interpolation " );

    testExamples = new double [ numInstToGeneratePerClass * numberOfClasses ][];
    classOfTestExamples = new int [ numInstToGeneratePerClass * numberOfClasses ];
    createExamplesByInterpolation ( testExamples, classOfTestExamples, numInstToGeneratePerClass, false );

    // 2. Run the KNN algorithm on the interpolated data.
    System.out.println  ( "      > Testing the KNN with the test instances " );

    N4 = runKNN ( 1, testExamples, classOfTestExamples, numInstToGeneratePerClass * numberOfClasses, false );

    System.out.println ( " N4: "+N4 );

} // end runN4


void organizePerClass () {

    int i;
    int []counterInstPerClass;

    if ( numExamplesPerClass != null ) {
        System.out.println ( " Examples already organized per class ");
        return;
    }

    System.out.println ( " Organizing instances per class " );

    numExamplesPerClass = new int [ numberOfClasses ];
    counterInstPerClass = new int [ numberOfClasses ];

    for ( i = 0; i < numberOfClasses; i++ ) {
        numExamplesPerClass[i] = 0;
        counterInstPerClass [i] = 0;
    }

    for ( i = 0; i < numberOfExamples; i++ ) {
        numExamplesPerClass[ classOfExample[i] ] ++;
    }

    // Reserve memory to maintain pointers per class
    examplesPerClass = new double [ numberOfClasses ][][];

    indexExamplesPerClass = new int [ numberOfClasses ][];

    for ( i = 0; i < numberOfClasses; i++ ) {
        System.out.println ( " Number of instances of class " + i + ": " + numExamplesPerClass[i]);
        examplesPerClass[i] = new double   [ numExamplesPerClass[i] ] [];
        indexExamplesPerClass[i] = new int [ numExamplesPerClass[i] ];
    }

    int whichClass;

    // Group the instances per class
    for ( i = 0; i < numberOfExamples; i++ ) {
        whichClass = classOfExample[i];
        examplesPerClass [ whichClass ] [ counterInstPerClass[whichClass] ] = example[i];
        indexExamplesPerClass [ whichClass ] [ counterInstPerClass[whichClass] ] = i;
        counterInstPerClass [ whichClass ] ++;
    }
    
} // end organizePerClass


void createExamplesByInterpolation ( double[][] testExamples, int[] classOfTestExamples, int numExamplesTestPerClass, boolean isSMO ) {

    int i, j, cClass, inst = 0, ex1, ex2;
    double rnd;

    // 1. Organize the instances of the training data set per class
    organizePerClass ();

    // 2. Generate the test instances
    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {

        System.out.println ( " Generating instances of class: " + cClass );
       
        for ( i = 0; i < numExamplesTestPerClass; i++ ) {

            // 3.1. Allocate memory for one example
            testExamples[inst] = new double [ numberOfAttributes ];

            // 3.2. Select two examples of the class cClass
            do {
                ex1 = rndObject.nextInt ( numExamplesPerClass[ cClass ] );
                ex2 = rndObject.nextInt ( numExamplesPerClass[ cClass ] );
            }
            while ( ex1 == ex2 && numExamplesPerClass[cClass] > 1 );

            // 3.3. Get the absolute position of example 1 and 2
            ex1 = indexExamplesPerClass[ cClass ][ ex1 ];
            ex2 = indexExamplesPerClass[ cClass ][ ex2 ];

            // 3.4. Create a new instance from these two ones
            for ( j = 0; j < numberOfAttributes; j++ ) {
                rnd = rndObject.nextDouble();
                testExamples[ inst ][j] = example[ ex1 ][j] * rnd + example[ ex2 ][j] * ( 1 - rnd );
            }

            // 3.5. Set the class
            if ( isSMO && cClass == 0 ) {
                classOfTestExamples[ inst ] = -1;
            }
            else {
                classOfTestExamples[ inst ] = cClass;
            }

            // 3.6. Increment the index inst
            inst ++;
        }
    }

} // end createExamplesByInterpolation


void runT1 () {

    // 0. Declare temporal variables.
    int i;
    int [][]neigh;                     // Nearest neighbor to each example.
    double [][]distNeigh;               // Distance to the nearest neighbor.
    double globalMinDist;             // Global minimum distance between pairs of examples of the same class.
    boolean overlappedExamples = false; // Indicates whether there exist two examples of different class laying
                                     // in the exact same point of the feature space.
    double epsilon;                   // Maximum separation.

    int [][]adherenceOrder;
    int   []maxAdherenceOrder;

    // 1. Organize instances per class.
    organizePerClass();

    // 2. Initialize variables that contain the neighbors and distances to them.
    neigh             = new int    [ numberOfClasses ][];
    distNeigh         = new double [ numberOfClasses ][];
    adherenceOrder    = new int    [ numberOfClasses ][];
    maxAdherenceOrder = new int    [ numberOfClasses ];

    for ( i = 0; i < numberOfClasses; i++ ) {
        neigh[i]          = new int    [ numExamplesPerClass[i] ];
        distNeigh[i]      = new double [ numExamplesPerClass[i] ];
        adherenceOrder[i] = new int    [ numExamplesPerClass[i] ];
    }

    // 3. Search the nearest neighbors for each example.
    double []refParameter = new double [2];
    searchNearestNeighborsOfAnotherClass ( neigh, distNeigh, refParameter );
    globalMinDist = refParameter[0];
    overlappedExamples = ( refParameter[1] > 0 );

    // 4. Define the maximum separation permitted, epsilon (0.55 is defined in Tin's original paper)
    epsilon = ( 0.55 * globalMinDist );

    // 5. Search for the adherence subsets
    calculateAdherenceSubsets ( adherenceOrder, maxAdherenceOrder, distNeigh, overlappedExamples, epsilon );

    // 6. Eliminate adherence subsets strictly included in another
    eliminateAdherenceSetsIncluded ( adherenceOrder, maxAdherenceOrder, epsilon );

    // 7. Get statistics for the pretopology metric.
    double []valuesReturn = getStatisticsPretopology ( adherenceOrder, maxAdherenceOrder );

    T1 = valuesReturn[0] / numberOfExamples;

    System.out.println ( " T1: " + T1 );
   
} // end runT1 


void searchNearestNeighborsOfAnotherClass ( int[][] neigh, double[][] distNeigh, double[] refParameter ) {

    int cClass, oClass; //Current class (cClass) and opposite class (oClass).
    int i, j;
    double dist;
    double globalMinDist = refParameter[0];
    boolean overlappedExamples = false;
    

    globalMinDist = Double.MAX_VALUE;

    System.out.println ( "      > Searching the nearest neighbors of another class " );

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        // Initialize to the farther possible distance
        for ( i = 0; i < numExamplesPerClass[ cClass ]; i++ ) {
            distNeigh[ cClass ][i] = Double.MAX_VALUE;
        }

        for ( oClass = 0; oClass < numberOfClasses; oClass++ ) {
            if ( oClass != cClass ) {
                for ( i = 0; i < numExamplesPerClass[ cClass ]; i++ ) {

                    for ( j = 0; j < numExamplesPerClass[ oClass ]; j++ ) {
                        dist = getDistance ( indexExamplesPerClass[ cClass ][i], indexExamplesPerClass[ oClass ][j] );

                        if ( dist < distNeigh[ cClass ][i] ) {
                            neigh[ cClass ][i] = indexExamplesPerClass[ oClass ][j];
                            distNeigh[ cClass ][i] = dist;
                        }
                    }

                    // Update the minimum distance between pairs of examples of different classes.
                    if ( distNeigh[ cClass ][i] == 0 ) {
                        overlappedExamples = true;
                    }
                    else if ( globalMinDist > distNeigh[ cClass ][i] ) {
                        globalMinDist = distNeigh[ cClass ][i];
                    }
                }
            }
        }
    }

    refParameter[0] = globalMinDist;
    refParameter[1] = ( overlappedExamples ) ? 1 : -1;

} // end searchNearestNeighborsOfAnotherClass


void calculateAdherenceSubsets ( int[][] adherenceOrder, int[] maxAdherenceOrder, double[][] distNeigh, boolean overlappedExamples, double epsilon ) {

    int cClass, i;

    System.out.println ( "      > Calculating adherence subsets " );

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        maxAdherenceOrder[ cClass ] = 0;

        for ( i = 0; i < numExamplesPerClass[cClass]; i++ ) {
            // If we find two overlapped cases, the adherence order of the example is zero
            if ( overlappedExamples && distNeigh[ cClass ][i] == 0. ) {
                adherenceOrder[ cClass ][i] = 0;
            }
            else { // The nearest neighbor is not laying just in the same position
                adherenceOrder[ cClass ][i] = ( int ) ( distNeigh[ cClass ][i] / epsilon ) - 1;
            }

            // Compute the maximum order per class
            if ( adherenceOrder[ cClass ][i] > maxAdherenceOrder[ cClass ] ) {
                maxAdherenceOrder[ cClass ] = adherenceOrder[ cClass ][i];
            }
        }
    }

} // end calculateAdherenceSubsets


void eliminateAdherenceSetsIncluded ( int[][] adherenceOrder, int[] maxAdherenceOrder, double epsilon ) {

    int cClass, i, j;
    int maximum, nextMaximum;
    double difOfOrder, dist;

    System.out.println ( "      > Eliminating adherence subsets that are included in others " );

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        maximum = maxAdherenceOrder[ cClass ];

        // While we have a maximum adherence subset
        while ( maximum >= 0 ) {

            // Search for all the sets with adherence order = maximum, and try to
            // subsume the others to it
            for ( i = 0; i < numExamplesPerClass[ cClass ]; i++ ) {

                if ( adherenceOrder[ cClass ][i] == maximum ) { // Example really far from the boundary.

                    // Eliminate the sets that are stricly included in this set.
                    for ( j = 0; j < numExamplesPerClass[ cClass ]; j++ ) {
                        difOfOrder = ( float ) ( adherenceOrder[ cClass ][i] - adherenceOrder[ cClass ][j] ) * epsilon;
                        dist = getDistance ( indexExamplesPerClass[ cClass ][i], indexExamplesPerClass[ cClass ][j] );

                        if ( dist < difOfOrder ) { // The adherence subset j is completely included in i.
                            // So, we remove adherence set since it is included in i.
                            adherenceOrder[ cClass ][j] = -1;
                        }
                    }
                }
            }

            // Now, we search for the following maximum adherence set
            nextMaximum = -1;

            for ( i = 0; i < numExamplesPerClass[ cClass ]; i++ ) {
                if ( adherenceOrder[ cClass ][i] != -1 && adherenceOrder[ cClass ][i] < maximum
                        && adherenceOrder[ cClass ][i] > nextMaximum ) {

                    nextMaximum = adherenceOrder[ cClass ][i];
                }
            }

            // Set to maximum the next maximum adherence set order.
            maximum = nextMaximum;
        }
    }

} // end eliminateAdherenceSetsIncluded


double[] getStatisticsPretopology ( int[][] adherenceOrder, int[] maxAdherenceOrder ) {

    int cClass, i;
    float sum = 0, sumsqr = 0, numOrders = 0;
    double[] stats = new double [5];

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        for ( i = 0; i < numExamplesPerClass[ cClass ]; i++ ) {
            if ( adherenceOrder[ cClass ][i] >= 0 ) {

                sum += ( float ) ( adherenceOrder[ cClass ][i] );
                sumsqr += ( float ) ( adherenceOrder[ cClass ][i] * adherenceOrder[ cClass ][i] );
                numOrders ++;

            }
        }
    }

    // 0. Number of adherence orders
    stats[0] = numOrders;

    // 1. Mean of the order of the adherence sets
    stats[1] = sum / numOrders;

    // 2. Standard deviation of the order of the adherence sets
    stats[2] = Math.sqrt ( ( sumsqr - sum * sum / numOrders ) / ( numOrders - 1 )  );

    // 3. Maximum order of class 0
    stats[3] = maxAdherenceOrder [0];

    // 4. Maximum order of class 1
    stats[4] = maxAdherenceOrder [1];

    System.out.println ("  > Results T1: " + stats[0] + " " + stats[1] + " " + stats[2] + " " + stats[3] + " " + stats[4] );

    return stats;

} // end getStatisticsPretopology


void runT2() {

    T2 = (double) numberOfExamples / (double) numberOfAttributes ;
    System.out.println ( " T2: " + T2 );
 
} // end runT2


private void quickSort ( double[] vector, int[] order, int inf, int sup ) {

    int pivot;

    if ( inf < sup ) {
    // Divide and conquer
        pivot = partition ( vector, order, inf, sup );
        quickSort ( vector, order, inf, pivot - 1 );
        quickSort ( vector, order, pivot + 1, sup );
    }

} // end quickSort

private int partition ( double[] vector, int[] order, int inf, int sup ) {

    double tempF;
    int   tempI;
    int   pivotPosition    = inf;
    int   lastSmallerValue = inf;
    int   firstUnknown     = inf + 1;

    for ( ; firstUnknown <= sup; firstUnknown ++ ) {
        if ( vector[ firstUnknown ] < vector[ pivotPosition ] ) {
            lastSmallerValue ++;

            tempF = vector[ firstUnknown ];
            vector[ firstUnknown ] = vector[ lastSmallerValue ];
            vector[ lastSmallerValue ] = tempF;

            tempI = order[ firstUnknown ];
            order[ firstUnknown ] = order[ lastSmallerValue ];
            order[ lastSmallerValue ] = tempI;
        }
    }

    tempF = vector[ inf ];
    vector[ inf ] = vector[ lastSmallerValue ];
    vector[ lastSmallerValue ] = tempF;

    tempI = order[ inf ];
    order[ inf ] = order[ lastSmallerValue ];
    order[ lastSmallerValue ] = tempI;

    return lastSmallerValue;
    
} // end partition


void runL1 () {
    int i;
    double[] w;
    double[] B = new double [1];
    B[0] = 0;

    if ( numberOfClasses != 2 ) {
        System.out.println ( "  > [ERROR in L1] Nonlinearity of the linear classifier can be applied to only 2-class data sets " );
        L1 = -1;
        return;
    }

    // 1. Change the class 0 to -1
    System.out.println ( "      > Changing classes to -1, 1 " );

   
    // 2. Training de support vector machine
    w = trainSMO ( B );
    
    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == 0 ) {
            classOfExample[i] = -1;
        }
    }

    // 3. Get the sum of distances to the objective function for each training example
    System.out.println ( "      > Testing SVM with the train instances " );

    L1 = getDistanceObjectiveFunction ( w, B[0], example, classOfExample, numberOfExamples );

    // 4. Revert the process. Change the class -1 to 0
    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == -1 ) {
            classOfExample[i] = 0;
        }
    }

    System.out.println ( " L1: " + L1 );

} // end runL1


void runL2 ( ) {

    int i;
    double[] w;
    double[] B = new double [1];
    B[0] = 0;
    
    if ( numberOfClasses != 2 ) {
        System.out.println ( "  > [ERROR in L2] Nonlinearity of the linear classifier can be applied to only 2-class data sets " );
        L2 = -1;
        return;
    }

    // 1. Change the class 0 to -1
    System.out.println ( "      > Changing classes to -1, 1 " );

    // 2. Training de support vector machine
    w = trainSMO ( B );

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == 0 ) {
            classOfExample[i] = -1;
        }
    }
    
    // 2. Test SMO with the train instances
    System.out.println ( "      > Testing SVM with the train instances " );

    L2 = testSMO ( w, B[0], example, classOfExample, numberOfExamples );

    // 3. Revert the process. Change the class -1 to 0
    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == -1 ) {
            classOfExample[i] = 0;
        }
    }
    
    System.out.println ( " L2: " + L2 );
    
} // end runL2 


void runL3 () {

    double[][] testExamples;
    int[] classOfTestExamples;
    int i, cClass;

    double[] w;
    double[] B = new double [1];
    B[0] = 0;
    
    if ( numberOfClasses != 2 ) {
        System.err.println ( "  > [ERROR in L3] Nonlinearity of the linear classifier can be applied to only 2-class data sets. " );
        return;
    }

    // 0. Check that all classes have examples
    organizePerClass ();

    for ( cClass = 0; cClass < numberOfClasses; cClass++ ) {
        if ( numExamplesPerClass[ cClass ] < 1 ) {
            System.out.println ( "      > [ERROR in L3] Error in computing the nonlinearity of the Linear Classifier. " );
            System.out.println ( "        >> Class " + cClass + " has 0 instances. " );
            L3 = -1;
            return;
        }
    }

    // 1. Create new examples by means of interpolation
    int numInstToGeneratePerClass = 1000;
    System.out.println ( "      > Generating " + numInstToGeneratePerClass + " by means of interpolation " );

    testExamples = new double [ numInstToGeneratePerClass * numberOfClasses ][];
    classOfTestExamples = new int [ numInstToGeneratePerClass * numberOfClasses ];
    createExamplesByInterpolation ( testExamples, classOfTestExamples, numInstToGeneratePerClass, false );

    // 2. Change the class 0 to -1
    System.out.println ( "      > Changing classes to -1, 1 " );

    for ( i = 0; i < numInstToGeneratePerClass*numberOfClasses; i++ ) {
        if ( classOfTestExamples[i] == 0 ) {
            classOfTestExamples[i] = -1;
        }
    }

    // 3. Training de support vector machine
    w = trainSMO ( B );

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == 0 ) {
            classOfExample[i] = -1;
        }
    }

    // 4. Test SMO with the new interpolated examples
    System.out.println ( "      > Testing SVM with the test instances " );

    L3 = testSMO ( w, B[0], testExamples, classOfTestExamples, numInstToGeneratePerClass * numberOfClasses );
    
    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == -1 ) {
            classOfExample[i] = 0;
        }
    }

    System.out.println (" L3: " + L3 );

} // end runL3


//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCTIONS TO TRAIN A LINEAR SVM /////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

double testSMO ( double[] w, double b, double[][] testExamples, int[] classOfTestExamples, int numTestExamples ) {

    int numError = 0;

    for ( int i = 0; i < numTestExamples; i++ ) {
        if ( ( learnedFunction ( testExamples[i], w, b ) > 0) != ( classOfTestExamples[i] > 0 ) ) {
            numError++;
        }
    }

    return (double) numError / (double) numTestExamples;

} // end testSMO


double getDistanceObjectiveFunction ( double[] w, double b, double[][] testExamples, int[] classOfTestExamples, int numTestExamples ) {

    double dist = 0;

    for ( int i = 0; i < numTestExamples; i++ ) {
        dist += Math.abs ( learnedFunction ( testExamples[i], w, b ) - classOfTestExamples[i] );
    }

    return dist / ( (double)numTestExamples );

} // end getDistanceObjectiveFunction


double kernelFunction ( int i1, int i2 ) {

    double dot = 0.0;

    for ( int i = 0; i < numberOfAttributes; i++ ) {
        dot += example[ i1 ][i] * example[ i2 ][i];
    }

    return dot;

} // end kernelFunction


double learnedFunction ( int k, double[] w, double b ) {

    double s = 0.0;

    for ( int i = 0; i < numberOfAttributes; i++ ) {
        s += w[i] * example[k][i];
    }

    s -= b;

    return s;

} // end learnedFunction


double learnedFunction ( double[] testExample, double[] w, double b ) {

    double s = 0.0;

    for ( int i = 0; i < numberOfAttributes; i++ ) {
        s += w[i] * testExample[i];
    }

    s -= b;

    return s;

} // end learnedFunction


int takeStep ( int i1, int i2, double[] B, double[] alpha, double[] w, double[] errorCache ) {

    int y1, y2, s;
    double alpha1, alpha2; // Old values of alpha1 and alpha2
    double a1, a2; // New values of alpha1 and alpha2
    double E1, E2, L, H, k11, k22, k12, eta, Lobj, Hobj;
    double b = B[0];

    if ( i1 == i2 ) {
        B[0] = b;
        return 0;
    }

    // Look up alpha1, y1, E1, alpha2, y2, E2
    alpha1 = alpha[i1];

    y1 = classOfExample[i1];

    if ( alpha1 > 0 && alpha1 < C ) {
        E1 = errorCache[i1];
    }
    else {
        E1 = learnedFunction ( i1, w, b ) - y1;
    }

    alpha2 = alpha[i2];

    y2 = classOfExample[i2];

    if ( alpha2 > 0 && alpha2 < C ) {
        E2 = errorCache[i2];
    }
    else {
        E2 = learnedFunction ( i2, w, b ) - y2;
    }

    s = y1 * y2;

    // Compute L, H
    if ( y1 == y2 ) {
        double gamma = alpha1 + alpha2;

        if ( gamma > C ) {
            L = gamma - C;
            H = C;
        }
        else {
            L = 0;
            H = gamma;
        }
    }
    else { // y1 != y2
        double gamma = alpha1 - alpha2;

        if ( gamma > 0 ) {
            L = 0;
            H = C - gamma;
        }
        else {
            L = -gamma;
            H = C;
        }
    }

    if ( L == H ) {
        B[0] = b;
        return 0;
    }

    // Compute eta
    k11 = kernelFunction ( i1, i1 );
    k12 = kernelFunction ( i1, i2 );
    k22 = kernelFunction ( i2, i2 );

    eta = 2 * k12 - k11 - k22;

    if ( eta < 0 ) {
        a2 = alpha2 + y2 * ( E2 - E1 ) / eta;

        if ( a2 < L ) {
            a2 = L;
        }
        else if ( a2 > H ) {
            a2 = H;
        }
    }
    else {
        // Compute Lobj, Hobj: objective function at a2 = L, a2 = H
        double c1 = eta / 2;
        double c2 = y2 * ( E1 - E2 ) - eta * alpha2;
        Lobj = c1 * L * L + c2 * L;
        Hobj = c1 * H * H + c2 * H;

        if ( Lobj > Hobj + EPSILON ) {
            a2 = L;
        }
        else if ( Lobj < Hobj - EPSILON ) {
            a2 = H;
        }
        else {
            a2 = alpha2;
        }
    }

    if ( Math.abs ( a2 - alpha2 ) < EPSILON * ( a2 + alpha2 + EPSILON ) ) {
        B[0] = b;
        return 0;
    }

    a1 = alpha1 - s * ( a2 - alpha2 );

    if ( a1 < 0 ) {
        a2 += s * a1;
        a1 = 0;
    }
    else if ( a1 > C ) {
        double t = a1 - C;
        a2 += s * t;
        a1 = C;
    }

    // Update threshold to reflect change in Lagrange multipliers
    double delta_b, bnew;

    if ( a1 > 0 && a1 < C ) {
        bnew = b + E1 + y1 * ( a1 - alpha1 ) * k11 + y2 * ( a2 - alpha2 ) * k12;
    }
    else {
        if ( a2 > 0 && a2 < C ) {
            bnew = b + E2 + y1 * ( a1 - alpha1 ) * k12 + y2 * ( a2 - alpha2 ) * k22;
        }
        else {
            double b1, b2;
            b1 = b + E1 + y1 * ( a1 - alpha1 ) * k11 + y2 * ( a2 - alpha2 ) * k12;
            b2 = b + E2 + y1 * ( a1 - alpha1 ) * k12 + y2 * ( a2 - alpha2 ) * k22;
            bnew = ( b1 + b2 ) / 2;
        }
    }

    delta_b = bnew - b;

    b = bnew;

    // Update weight vector to reflect change in a1 and a2
    double t1 = y1 * ( a1 - alpha1 );
    double t2 = y2 * ( a2 - alpha2 );

    for ( int i = 0; i < numberOfAttributes; i++ ) {
        w[i] += example[i1][i] * t1 + example[i2][i] * t2;
    }

    // Update error cache using new Lagrange multipliers
    t1 = y1 * ( a1 - alpha1 );
    t2 = y2 * ( a2 - alpha2 );

    for ( int i = 0; i < numberOfExamples; i++ ) {
        if ( 0 < alpha[i] && alpha[i] < C ) {
            errorCache[i] += t1 * kernelFunction ( i1, i ) + t2 * kernelFunction ( i2, i ) - delta_b;
            errorCache[i1] = 0.0;
            errorCache[i2] = 0.0;
        }
    }

    alpha[i1] = a1; // Store a1 in the alpha array
    alpha[i2] = a2; // Store a2 in the alpha array

    B[0] = b;
    return 1;

} // end takeStep


int argmaxE1E2 ( int i1, double E1, double[] B, double[] alpha, double[] w, double[] errorCache ) {

    int k, i2;
    double tmax;
    double b = B[0];

    for ( i2 = -1, tmax = 0, k = 0; k < numberOfExamples; k++ ) {
        if ( alpha[k] > 0 && alpha[k] < C ) {
            double E2, temp;
            E2 = errorCache[k];
            temp = Math.abs( E1 - E2 );

            if ( temp > tmax ) {
                tmax = temp;
                i2 = k;
            }
        }
    }

    if ( i2 >= 0 ) {
        B[0] = b;
        if ( takeStep ( i1, i2, B, alpha, w, errorCache ) == 1 ) {
            return 1;
        }
    }

    B[0] = b;
    return 0;

} // end argmaxE1E2


int iterateNonBoundExamples ( int i1, double[] B, double[] alpha, double[] w, double[] errorCache ) {

    int k, k0, i2;
    double b = B[0];

    for ( k0 = ( int ) ( rndObject.nextDouble() * numberOfExamples ), k = k0; k < numberOfExamples + k0 ; k++ ) {

        i2 = k % numberOfExamples;

        if ( alpha[i2] > 0 && alpha[i2] < C ) {
            B[0] = b;
            if ( takeStep ( i1, i2, B, alpha, w, errorCache ) == 1 ) {
                b = B[0];
                return 1;
            }
            b = B[0];
        }
    }

    B[0] = b;
    return 0;

} // end iterateNonBoundExamples


int iterateEntireTrainingSet ( int i1, double[] B, double[] alpha, double[] w, double[] errorCache ) {

    int k, k0, i2;
    double b = B[0];

    for ( k0 = (int) ( rndObject.nextDouble() * numberOfExamples ), k = k0; k < numberOfExamples + k0; k++ ) {
        i2 = k % numberOfExamples;

        B[0] = b;
        if ( takeStep ( i1, i2, B, alpha, w, errorCache ) == 1) {
            return 1;
        }
        b = B[0];
    }

    B[0] = b;
    return 0;

} // end iterateEntireTrainingSet


int examineExample ( int i1, double[] B, double[] alpha, double[] w, double[] errorCache ) {

    double y1, alpha1, E1, r1;
    double b = B[0];
    
    y1 = classOfExample[i1];
    alpha1 = alpha[i1];

    if ( alpha1 > 0 && alpha1 < C ) {
        E1 = errorCache[i1];
    }
    else {
        E1 = learnedFunction ( i1, w, b ) - y1;
    }

    r1 = y1 * E1;

    // Check if example i1 violates KKT condition
    if ( ( r1 < - ( TOLERANCE ) && alpha1 < C ) || ( r1 > TOLERANCE && alpha1 > 0 ) ) {
        // The current example (i1) has violeated the KKT. So, we look for
        // the second instance to jointly optimize the two alphas

        // 1. Try argmax E1 - E2
        if ( argmaxE1E2 ( i1, E1, B, alpha, w, errorCache ) == 1 ) {
            return  1;
        }

        b = B[0];

        // 2. Try iterating through the non-bound examples
        if ( iterateNonBoundExamples ( i1, B, alpha, w, errorCache ) == 1 ) {
            return  1;
        }

        b = B[0];

        // 3. Try iterating through the entire training set
        if ( iterateEntireTrainingSet ( i1, B, alpha, w, errorCache ) == 1 ) {
            return  1;
        }

        b = B[0];
    }

    B[0] = b;
    return 0;

} // end examineExample


double[] trainSMO ( double[] B ) {

    double[] alpha;      // Lagrange multipliers
    double[] w;          // Weight vector
    double[] errorCache;

    int i;
    int numChanged = 0;
    int examineAll = 1;
    int iter = 0;
    double b = B[0];

   
    alpha = new double [ numberOfExamples ];

    errorCache = new double [ numberOfExamples ];
    w = new double [ numberOfAttributes ];

    System.out.println ( "      > Changing classes to -1, 1 " );

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == 0 ) {
            classOfExample[i] = -1;
        }
    }

    for ( i = 0; i < numberOfExamples; i++ ) {
        alpha[i] = 0;
        // The initial error is 0 - classOfExample[i] since the SVM always predicts zero (w=0 and b=0)
        errorCache[i] = 0; // - classOfExample[i];
    }

    for ( i = 0; i < numberOfAttributes; i++ ) {
        w[i] = 0.0;
    }

    System.out.println ( "      > Building the Support Vector Machine [progress line] " );

    int maxIterations = ( numberOfExamples < 25000 ) ? 100000 : 4 * numberOfExamples;

    while ( ( numChanged > 0 || examineAll == 1 ) && iter < maxIterations ) {

        System.out.print  ( "." );
        numChanged = 0;

        if ( examineAll == 1 ) {
            for ( int k = 0; k < numberOfExamples; k++ ) {
                numChanged += examineExample ( k, B, alpha, w, errorCache );
            }
        }
        else {
            for ( int k = 0; k < numberOfExamples; k++ ) {
                if ( alpha[k] > 0 && alpha[k] < C ) {
                    numChanged += examineExample ( k, B, alpha, w, errorCache );
                }
            }
        }

        if ( examineAll == 1 ) {
            examineAll = 0;
        }
        else if ( numChanged == 0 ) {
            examineAll = 1;
        }

        iter ++;
    }

   System.out.println ( "      > Changing classes to 0, 1 " );

    for ( i = 0; i < numberOfExamples; i++ ) {
        if ( classOfExample[i] == -1 ) {
            classOfExample[i] = 0;
        }
    }

    return w;

} // end trainSMO

//////////////////////////////////////////////////////////////////////////////////

/**
 * It runs the algorithm
 * 
 * @param args the command line arguments
 */
static public void main ( String [] args ) {

	System.out.println ( " > Starting running the complexity metrics " );
	System.out.println ( " > Config File: " + args[0] );
	ComplexityMetrics cm = new ComplexityMetrics ( args[0] );
	cm.run();

} // end main
   
} // end ComplexityMetrics
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
 * @author Written by Cristóbal Romero Morales (University of Oviedo) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45RulesSA;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.IOException;


/** para commons.configuration
 import org.apache.commons.configuration.*;
 */


public class C45 extends Algorithm{
/**
 * <p>
 * Class to implement the C4.5 algorithm
 * </p>
 */
	
  /** Decision tree. */
  private Tree root;

  /** Is the tree pruned or not. */
  private boolean prune = false;

  /** Confidence level. */
  private float confidence = 0.25f;

  /** Minimum number of itemsets per leaf. */
  private int minItemsets = 2;

  /** The prior probabilities of the classes. */
  private double [] priorsProbabilities;

  /** Resolution of the margin histogram. */
  private static int marginResolution = 500;

  /** Cumulative margin classification. */
  private double marginCounts [];

  /** The sum of counts for priors. */
  private double classPriorsSum;

  /** Constructor.
   *
   * @param paramFile The parameters file.
   *
   * @throws Exception If the algorithm cannot be executed.
   */
  public C45( parseParameters paramFile ) throws Exception
  {
    try
    {

      // starts the time
      long startTime = System.currentTimeMillis();

      /* Sets the options of the execution from text file*/
      //StreamTokenizer tokenizer = new StreamTokenizer( new BufferedReader( new FileReader( paramFile ) ) );
      //initTokenizer( tokenizer) ;
      //setOptions( tokenizer );

      //File Names
      modelFileName=paramFile.getTrainingInputFile();
      trainFileName=paramFile.getValidationInputFile();
      testFileName=paramFile.getTestInputFile();
      //Options
      confidence=Float.parseFloat(paramFile.getParameter(1)); //confidence level for the uniform distribution
      minItemsets = Integer.parseInt(paramFile.getParameter(2)); //itemset per Leaf
      if (confidence < 0 || confidence > 1) {
        confidence = 0.25F;
        System.err.println("Error: Confidence must be in the interval [0,1]");
        System.err.println("Using default value: 0.25");
      }
      if (minItemsets <= 0) {
        minItemsets = 2;
        System.err.println("Error: itemsetPerLeaf must be greater than 0");
        System.err.println("Using default value: 2");
      }
      prune=false;

      /* Initializes the dataset. */
      modelDataset = new MyDataset( modelFileName, true  );
      trainDataset = new MyDataset( trainFileName, false  );
      testDataset = new MyDataset( testFileName, false  );

      priorsProbabilities = new double [modelDataset.numClasses()];
      priorsProbabilities();
      marginCounts = new double [marginResolution + 1];

      // generate the tree
      generateTree( modelDataset );

    }
    catch ( Exception e )
    {
      System.err.println( e.getMessage() );
      System.exit(-1);
    }
  }


  /** Generates the tree.
   *
   * @param itemsets The dataset used to build the tree.
   *
   * @throws Exception If the tree cannot be built.
   */
  public void generateTree( MyDataset itemsets ) throws Exception
  {
    SelectCut selectCut;

    selectCut = new SelectCut( minItemsets, itemsets );
    root = new Tree( selectCut, prune, confidence );
    root.buildTree( itemsets );
  }

  /** Function to evaluate the class which the itemset must have according to the classification of the tree.
   *
   * @param itemset The itemset to evaluate.
   * @throws Exception If cannot compute the classification.
   * @return The index of the class index predicted.
   */
  public double evaluateItemset( Itemset itemset ) throws Exception
  {
    Itemset classMissing = (Itemset)itemset.copy();
    double prediction = 0;
    classMissing.setDataset( itemset.getDataset() );
    classMissing.setClassMissing();

    double [] classification = classificationForItemset( classMissing );
    prediction = maxIndex( classification );
    updateStats( classification, itemset, itemset.numClasses() );

    //itemset.setPredictedValue( prediction );

    return prediction;
  }

  /** Updates all the statistics for the current itemset.
   *
   * @param predictedClassification Distribution of class values predicted for the itemset.
   * @param itemset The itemset.
   * @param nClasses The number of classes.
   *
   */
  private void updateStats( double [] predictedClassification, Itemset itemset, int nClasses )
  {
    int actualClass = (int)itemset.getClassValue();

    if ( !itemset.classIsMissing() )
    {
      updateMargins( predictedClassification, actualClass, nClasses );

      // Determine the predicted class (doesn't detect multiple classifications)
      int predictedClass = -1;
      double bestProb = 0.0;

      for( int i = 0; i < nClasses; i++ )
      {
        if ( predictedClassification[i] > bestProb )
        {
          predictedClass = i;
          bestProb = predictedClassification[i];
        }
      }

      // Update counts when no class was predicted
      if ( predictedClass < 0 )
      {
        return;
      }

      double predictedProb = Math.max( Double.MIN_VALUE, predictedClassification[actualClass] );
      double priorProb = Math.max( Double.MIN_VALUE, priorsProbabilities[actualClass] / classPriorsSum );
    }
  }

  /** Returns class probabilities for an itemset.
   *
   * @param itemset The itemset.
   *
   * @throws Exception If cannot compute the classification.
   * @return class probabilities for an itemset.
   */
  public final double [] classificationForItemset( Itemset itemset ) throws Exception
  {
    return root.classificationForItemset( itemset );
  }

  /** Update the cumulative record of classification margins.
   *
   * @param predictedClassification Distribution of class values predicted for the itemset.
   * @param actualClass The class value.
   * @param nClasses Number of classes.
   */
  private void updateMargins( double [] predictedClassification, int actualClass, int nClasses )
  {
    double probActual = predictedClassification[actualClass];
    double probNext = 0;

    for( int i = 0; i < nClasses; i++ )
      if ( ( i != actualClass ) && ( //Comparators.isGreater( predictedClassification[i], probNext ) ) )
          predictedClassification[i] > probNext ) )
        probNext = predictedClassification[i];

    double margin = probActual - probNext;
    int bin = (int)( ( margin + 1.0 ) / 2.0 * marginResolution );
    marginCounts[bin]++;
  }

  /** Evaluates if a string is a boolean value.
   *
   * @param value The string to evaluate.
   *
   * @return True if value is a boolean value. False otherwise.
   */
  private boolean isBoolean( String value )
  {
    if ( value.equalsIgnoreCase( "TRUE") || value.equalsIgnoreCase( "FALSE" ) )
      return true;
    else
      return false;
  }

  /** Returns index of maximum element in a given array of doubles. First maximum is returned.
   *
   * @param doubles The array of elements.
   *
   * @return index of maximum element in a given array of doubles. First maximum is returned.
   */
  public static int maxIndex( double [] doubles )
  {
    double maximum = 0;
    int maxIndex = 0;

    for ( int i = 0; i < doubles.length; i++ )
    {
      if ( ( i == 0 ) || //
           doubles[i] > maximum )
      {
        maxIndex = i;
        maximum = doubles[i];
      }
    }

    return maxIndex;
  }

  /** Sets the class prior probabilities.
   *
   * @throws Exception If cannot compute the probabilities.
   */
  public void priorsProbabilities() throws Exception
  {
    for ( int i = 0; i < modelDataset.numClasses(); i++ )
      priorsProbabilities[i] = 1;

    classPriorsSum = modelDataset.numClasses();

    for (int i = 0; i < modelDataset.numItemsets(); i++)
    {
      if ( !modelDataset.itemset(i).classIsMissing() )
      {
        try
        {
          priorsProbabilities[(int)modelDataset.itemset(i).getClassValue()] += modelDataset.itemset(i).getWeight();
          classPriorsSum += modelDataset.itemset(i).getWeight();
        }
        catch ( Exception e )
        {
          System.err.println( e.getMessage() );
        }
      }
    }
  }

  /** Function to print the tree.
   *
   * @return a string representation of the C4.5 tree
   */
  public String toString()
  {
    return root.toString();
  }


  /**
   * Returns the C4.5 tree
   * @return the C4.5 tree
   */
  public Tree getTree(){return root;}

}

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
 * @author Written by Antonio Alejandro Tortosa (University of Granada) 01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.C45RulesSA;


public class Utilities {
/**
 * <p>
 * Collection of auxiliar methods.
 * </p>
 */
	
	
  /** The natural logarithm of 2. */
  private static double log2_cte = Math.log(2);

  /**
   * Mergesort algorithm for an array of long integers.
   * @param theArray long[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(long[] theArray,int nElems){
    // provides workspace
    long[] workSpace = new long[nElems];
    recMergeSort(theArray, workSpace, 0, nElems-1);
  }

  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(long[] theArray,long[] workSpace, int lowerBound,int upperBound){
    if(lowerBound == upperBound)            // if range is 1,
      return;                              // no use sorting
    else
    {                                    // find midpoint
      int mid = (lowerBound+upperBound) / 2;
      // sort low half
      recMergeSort(theArray,workSpace, lowerBound, mid);
      // sort high half
      recMergeSort(theArray,workSpace, mid+1, upperBound);
      // merge them
      merge(theArray,workSpace, lowerBound, mid+1, upperBound);
    }  // end else
  }  // end recMergeSort()
  //-----------------------------------------------------------
  static private void merge(long[] theArray,long[] workSpace, int lowPtr,int highPtr, int upperBound){
    int j = 0;                             // workspace index
    int lowerBound = lowPtr;
    int mid = highPtr-1;
    int n = upperBound-lowerBound+1;       // # of items

    while(lowPtr <= mid && highPtr <= upperBound)
      if( theArray[lowPtr] < theArray[highPtr] )
        workSpace[j++] = theArray[lowPtr++];
      else
        workSpace[j++] = theArray[highPtr++];

    while(lowPtr <= mid)
      workSpace[j++] = theArray[lowPtr++];

    while(highPtr <= upperBound)
      workSpace[j++] = theArray[highPtr++];

    for(j=0; j<n; j++)
      theArray[lowerBound+j] = workSpace[j];
  }
  /*************************END OF THE FIRST METHOD*******************************/

  /**
   * Mergesort algorithm for an array of long integers.
   * @param theArray double[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(double[] theArray,int nElems){
    // provides workspace
    double[] workSpace = new double[nElems];
    recMergeSort(theArray, workSpace, 0, nElems-1);
  }

  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(double[] theArray,double[] workSpace, int lowerBound,int upperBound){
    if(lowerBound == upperBound)            // if range is 1,
      return;                              // no use sorting
    else
    {                                    // find midpoint
      int mid = (lowerBound+upperBound) / 2;
      // sort low half
      recMergeSort(theArray,workSpace, lowerBound, mid);
      // sort high half
      recMergeSort(theArray,workSpace, mid+1, upperBound);
      // merge them
      merge(theArray,workSpace, lowerBound, mid+1, upperBound);
    }  // end else
  }  // end recMergeSort()
  //-----------------------------------------------------------
  static private void merge(double[] theArray,double[] workSpace, int lowPtr,int highPtr, int upperBound){
    int j = 0;                             // workspace index
    int lowerBound = lowPtr;
    int mid = highPtr-1;
    int n = upperBound-lowerBound+1;       // # of items

    while(lowPtr <= mid && highPtr <= upperBound)
      if( theArray[lowPtr] < theArray[highPtr] )
        workSpace[j++] = theArray[lowPtr++];
      else
        workSpace[j++] = theArray[highPtr++];

    while(lowPtr <= mid)
      workSpace[j++] = theArray[lowPtr++];

    while(highPtr <= upperBound)
      workSpace[j++] = theArray[highPtr++];

    for(j=0; j<n; j++)
      theArray[lowerBound+j] = workSpace[j];
  }
  /*************************END OF THE SECOND METHOD*******************************/

  /**
   * Returns the logarithm of a for base 2.
   *
   * @param a 	a double
   * @return	the logarithm for base 2
   */
  public static double log2(double a) {

    return Math.log(a) / log2_cte;
  }

}


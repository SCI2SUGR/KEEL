/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)  15/10/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;

import java.util.Arrays;



public class Utilities {
/**
 * Collection of auxiliar methods.
 */
	
	
  /** The natural logarithm of 2. */
  public static double log2 = Math.log(2);

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
//    recMergeSort(theArray, workSpace, 0, nElems-1);
    Arrays.sort(theArray);
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
   * Mergesort algorithm for an array of Pairs.
   * @param theArray Pair[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(Pair[] theArray,int nElems){
    // provides workspace
//    Pair[] workSpace = new Pair[nElems];
//    recMergeSort(theArray, workSpace, 0, nElems-1);
	Arrays.sort(theArray);
  }
  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(Pair[] theArray,Pair[] workSpace, int lowerBound,int upperBound){
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
  static private void merge(Pair[] theArray,Pair[] workSpace, int lowPtr,int highPtr, int upperBound){
      int j = 0;                             // workspace index
      int lowerBound = lowPtr;
      int mid = highPtr-1;
      int n = upperBound-lowerBound+1;       // # of items

      while(lowPtr <= mid && highPtr <= upperBound)
        if( theArray[lowPtr].value < theArray[highPtr].value )
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

    /*************************END OF THE THIRD METHOD*******************************/

    /**
     * Mergesort algorithm for a vector of Trio.
     * @param theArray Vector the Array to sort
     * @param nElems int size of theArray
     */
    public static void mergeSort(Trio[] theArray,int nElems){
      // provides workspace
//      Trio[] workSpace = new Trio[nElems];
//      recMergeSort(theArray, workSpace, 0, nElems-1);
    	Arrays.sort(theArray);
    }
    //------------------------------PRIVATE METHODS--------------------------------------------------/
    static private void recMergeSort(Trio[] theArray,Trio[] workSpace, int lowerBound,int upperBound){
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
    static private void merge(Trio[] theArray,Trio[] workSpace, int lowPtr,int highPtr, int upperBound){
        int j = 0;                             // workspace index
        int lowerBound = lowPtr;
        int mid = highPtr-1;
        int n = upperBound-lowerBound+1;       // # of items

        while(lowPtr <= mid && highPtr <= upperBound)
          if( theArray[lowPtr].getKey() < theArray[highPtr].getKey() )
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

      /*************************END OF THE FOURTH METHOD*******************************/


      /**
       * Returns the logarithm of a for base 2.
       *
       * @param a 	a double
       * @return	the logarithm for base 2
       */
      public static double log2(double a) {

        return Math.log(a) / log2;
      }

}

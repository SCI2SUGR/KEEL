/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Slipper;



public class Utilities {
/**
 * <p>
 * Collection of auxiliar methods.
 * </p>
 */
	
  /** The small deviation allowed in double comparisons */
  public static double SMALL = 1e-6;

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
   * Mergesort algorithm for an array of Pairs.
   * @param theArray Pair[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(Pair[] theArray,int nElems){
    // provides workspace
    Pair[] workSpace = new Pair[nElems];
    recMergeSort(theArray, workSpace, 0, nElems-1);
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

    /*************************END OF THE FIRST METHOD*******************************/

    /**
     * Mergesort algorithm for a vector of Trio.
     * @param theArray Vector the Array to sort
     * @param nElems int size of theArray
     */
    public static void mergeSort(Trio[] theArray,int nElems){
      // provides workspace
      Trio[] workSpace = new Trio[nElems];
      recMergeSort(theArray, workSpace, 0, nElems-1);
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

      /**
       * Tests if a is greater than b.
       *
       * @param a a double
       * @param b a double
       * @return if a is greater than b.
       */
      public static boolean gr(double a, double b) {

        return (a - b > SMALL);
      }

      /**
       * Tests if a is smaller or equal to b.
       *
       * @param a a double
       * @param b a double
       * @return if a is smaller or equal to b.
       */
      public static boolean smOrEq(double a, double b) {

        return (a - b < SMALL);
      }

      /**
       * Returns the logarithm of a for base 2.
       *
       * @param a a double
       * @return the logarithm of a for base 2.
       */
      public static double log2(double a) {

        return Math.log(a) / log2;
      }





}

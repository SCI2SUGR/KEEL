//
//  EncodingLength.java
//
//  Salvador García López
//
//  Created by Salvador García López 31-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Explore;

import keel.Algorithms.Preprocess.Basic.*;

public class EncodingLength {

  /*Function that return the Encoding Length value of a S set*/
  public static double evaluaEL (double datosTrain[][], double realTrain[][], int nominalTrain[][], boolean nulosTrain[][], int clasesTrain[], double conjS[][], double conjR[][], int conjN[][], boolean conjM[][], int clasesS[], int nClass, int k, int nClases, boolean distanceEu) {

    int i, x = 0;
    int claseObt;
    int m, n;

    if (nClass == 2) nClass = 3;

    /*Obtaining the number of instances misclassified with S*/
    for (i=0; i<datosTrain.length; i++) {
      claseObt = KNN.evaluacionKNN2(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
      if (claseObt != clasesTrain[i])
        x++;
    }

    m = conjS.length;
    n = datosTrain.length;

    return (F(m,n) + ((double)m)*Math.log((double)nClass)/Math.log(2) + F(x, n-m) + ((double)x)*Math.log((double)(nClass-1))*Math.log(2));
  }

  /*Function that calculates the F function of Encoding Length cost*/
  public static double F (int m, int n) {

    double suma = 0;
    int i;

    for (i=0; i<=m; i++) {
      suma += combinatoria (i, n);
    }

    if (suma == Double.POSITIVE_INFINITY) {
      i = 6;
    } else {
      for (i=0; suma > 0; i++)
        suma = Math.log(suma)/Math.log(2);
    }

    return (double)(i-1);
  }

  /*Function that calculates combinatory of two integers*/
  public static double combinatoria (int m, int n) {

    double result = 1;
    int i;

    for (i=1; i<=m; i++)
      result *= (double)(n-m+i)/(double)i;

    return result;
  }
}
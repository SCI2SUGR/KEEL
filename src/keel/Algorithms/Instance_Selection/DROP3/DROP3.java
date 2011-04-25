/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

//
//  DROP3.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.DROP3;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

public class DROP3 extends Metodo {

  /*Own parameters of the algorithm*/
  private int k;

  public DROP3 (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m, n, o;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int vecinos[][];
    Vector asociados[];
    int aciertosSin;
    int vecinosTemp[];
    double distTemp[];
    double dist, bestD;
    boolean parar;
    Referencia orden[];
    int mayoria;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the instance flagged vector of the S set*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
    }
    nSel = datosTrain.length;

    /*Do ENN before sorting*/
    for (i=0; i<datosTrain.length; i++) {
      claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
      if (claseObt != clasesTrain[i]) { //is included in the solution set if it is agree with your mayority
        marcas[i] = false;
        nSel--;
      }
    }

    /*Construction of an instance vector with distances to the nearest enemy*/
    orden = new Referencia[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      bestD = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        if (clasesTrain[i] != clasesTrain[j]) {
          dist = KNN.distancia (datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
          if (dist < bestD)
            bestD = dist;
        }
      }
      orden[i] = new Referencia (i, bestD);
    }

    /*Sorting the previous vector*/
    Arrays.sort(orden);

    /*Inicialization of the data structures of neighbors and associates*/
    distTemp = new double[k+1];
    vecinosTemp = new int[k+1];
    vecinos = new int[datosTrain.length][k+1];
    asociados = new Vector[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      asociados[i] = new Vector ();

    /*Body of the algorithm DROP3 (same as DROP2).*/
    for (i=0; i<datosTrain.length; i++) {
      /*Get the k+1 nearest neighbors of each instance*/
      if (marcas[i]) {
        KNN.evaluacionKNN2 (k+1, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos[i]);
        for (j=0; j<vecinos[i].length; j++) {
          if (vecinos[i][j] >= 0)
        	  asociados[vecinos[i][j]].addElement (new Referencia (i,0));
        }
      }
    }

    /*Check if delete or not the instances considering the WITH and WITHOUT sets*/
    for (o=0; o<datosTrain.length; o++){
      i = orden[o].entero;
      if (marcas[i]) { //only for instances haven�t noise filtered
        aciertosSin = 0;

        marcas[i] = false;
        nSel--;
        /*Construction of S set from the temporaly flags*/
        conjS = new double[nSel][datosTrain[0].length];
        conjR = new double[nSel][datosTrain[0].length];
        conjN = new int[nSel][datosTrain[0].length];
        conjM = new boolean[nSel][datosTrain[0].length];
        clasesS = new int[nSel];
        for (m=0, l=0; m<datosTrain.length; m++) {
          if (marcas[m]) { //the instance will evaluate
            for (j=0; j<datosTrain[0].length; j++) {
              conjS[l][j] = datosTrain[m][j];
              conjR[l][j] = realTrain[m][j];
              conjN[l][j] = nominalTrain[m][j];
              conjM[l][j] = nulosTrain[m][j];
            }
            clasesS[l] = clasesTrain[m];
            l++;
          }
        }

        marcas[i] = true;
        nSel++;

        /*Evaluation of associates without the instance in T*/
        for (j=0; j<k+1; j++) {
          if (vecinos[i][j] >= 0) {
        	  claseObt = KNN.evaluacionKNN2 (k, conjS, conjR, conjN, conjM, clasesS, datosTrain[vecinos[i][j]], realTrain[vecinos[i][j]], nominalTrain[vecinos[i][j]], nulosTrain[vecinos[i][j]], nClases, distanceEu);
        	  if (claseObt == clasesTrain[vecinos[i][j]])  //classify it correctly
        		  aciertosSin++;
          }
        }

        mayoria = (k+1) / 2;
        if (aciertosSin > mayoria) {
          /*Delete P from S*/
          marcas[i] = false;
          nSel--;

          /*For each associate of P, search a new nearest neighbor*/
          for (j=0; j<asociados[i].size(); j++) {
            for (l=0; l<k+1; l++) {
              vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];
              vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;
              distTemp[l] = Double.POSITIVE_INFINITY;
            }
            for (l=0; l<datosTrain.length; l++) {
              if (marcas[l]) { //it is in S
                dist = KNN.distancia (datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], realTrain[((Referencia)(asociados[i].elementAt(j))).entero], nominalTrain[((Referencia)(asociados[i].elementAt(j))).entero], nulosTrain[((Referencia)(asociados[i].elementAt(j))).entero], datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], distanceEu);
                parar = false;

                /*Calculate the nearest neighbors in this situation again*/
                for (m=0; m<(k+1) && !parar; m++) {
                  if (dist < distTemp[m]) {
                    parar = true;
                    for (n=m+1; n<k+1; n++) {
                      distTemp[n] = distTemp[n-1];
                      vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n-1];
                    }
                    distTemp[m] = dist;
                    vecinos[((Referencia)(asociados[i].elementAt(j))).entero][m] = l;
                  }
                }
              }
            }

            /*Add to the list of associates of the new neighbor this instance*/
            for (l=0; l<k+1; l++) {
              parar = false;
              for (m=0; m<asociados[vecinosTemp[l]].size() && !parar; m++) {
                if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero
                    && vecinosTemp[l] != i) {
                  asociados[vecinosTemp[l]].removeElementAt(m);
                  parar = true;
                }
              }
            }
            for (l=0; l<k+1; l++) {
              asociados[vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l]].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));
            }
          }
        }
      }
    }

    /*Construction of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (marcas[m]) { //the instance will evaluate
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[m][j];
          conjR[l][j] = realTrain[m][j];
          conjN[l][j] = nominalTrain[m][j];
          conjM[l][j] = nulosTrain[m][j];
        }
        clasesS[l] = clasesTrain[m];
        l++;
      }
    }

    System.out.println("DROP3 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, this.k);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, this.k);
	}
            
     KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);

  }

  public void leerConfiguracion (String ficheroScript) {

    String fichero, linea, token;
    StringTokenizer lineasFichero, tokens;
    byte line[];
    int i, j;

    ficheroSalida = new String[2];

    fichero = Fichero.leeFichero (ficheroScript);
    lineasFichero = new StringTokenizer (fichero,"\n\r");

    lineasFichero.nextToken();
    linea = lineasFichero.nextToken();

    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of the training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    
	for (i=j+1; line[i]!='\"'; i++);
	i++;
	for (j=i; line[j]!='\"'; j++);
	ficheroValidation = new String (line,i,j-i);
	
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Getting the path and base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);

    /*Getting the number of neighbors*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    k = Integer.parseInt(tokens.nextToken().substring(1));
    
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
}
}

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

//
//  ICF.java
//
//  Salvador García López
//
//  Created by Salvador García López 15-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ICF;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class ICF extends Metodo {

 /*Own parameters of the algorithm*/
  private int k;

  public ICF (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    double minDistEnemigo[];
    double dist;
    int reachable[];
    int coverage[];
    boolean progresa;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the flagged instances vector from the S, reachable and coverage sets*/
    marcas = new boolean[datosTrain.length];
    reachable = new int[datosTrain.length];
    coverage = new int[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
      reachable[i] = 0;
      coverage[i] = 0;
    }
    nSel = datosTrain.length;

    /*Inicialization of the matrix of minimum distences of the enemys used for see the
     adaptability of the instance*/
    minDistEnemigo = new double[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      minDistEnemigo[i] = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
        if (clasesTrain[i] != clasesTrain[j] && dist < minDistEnemigo[i])
          minDistEnemigo[i] = dist;
      }
    }

    /*Body of the ICF algorithm. First, apply the Wilson filter; then, get the reachable and coverage
     sets for each instance and compare its sizes for descarting. This process is repited until there is
     not more descarts.*/
    for (i=0; i<datosTrain.length; i++) {
      /*Apply ENN*/
      claseObt = KNN.evaluacionKNN2(k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
      if (claseObt != clasesTrain[i]) { //incorrect classification, add this instance
        marcas[i] = false;
        nSel--;
      }
    }

    do {
      /*Calculate of reachable and coverage*/
      for (i=0; i<datosTrain.length; i++) {
        if (marcas[i]) { //it is in S set
          coverage[i] = getCoverage (i, marcas, minDistEnemigo);
          reachable[i] = getReachable (i, marcas, minDistEnemigo);
        }
      }
      progresa = false;

      /*Elimination of instances*/
      for (i=0; i<datosTrain.length; i++) {
        if (marcas[i] && reachable[i] > coverage[i]) {
          marcas[i] = false;
          nSel--;
          progresa = true;
        }
      }
    } while (progresa);

    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (marcas[m]) { //the instance will be evaluated
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

    System.out.println("ICF "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /*Function that calculates teh number of elements of the coverage set for an instance*/
  private int getCoverage (int actual, boolean marcas[], double minDistEnemigo[]) {

    int i, suma = 0, adap;

    for (i=0; i<datosTrain.length; i++) {
      adap = 0;
      if (i != actual && marcas[i]) {
        adap = getAdaptable (actual, i, minDistEnemigo);
      }
      suma += adap;
    }

    return suma;
  }

  /*Function that calculates the number of elements of the reachable set for an instance*/
  private int getReachable (int actual, boolean marcas[], double minDistEnemigo[]) {

    int i, suma = 0, adap;

    for (i=0; i<datosTrain.length; i++) {
      adap = 0;
      if (i != actual && marcas[i]) {
        adap = getAdaptable (i, actual, minDistEnemigo);
      }
      suma += adap;
    }

    return suma;
  }

  /*Function that indicates if two instances are adaptables*/
  private int getAdaptable (int x, int y, double minDistEnemigo[]) {

    double dist;

    dist = KNN.distancia(datosTrain[x], realTrain[x], nominalTrain[x], nulosTrain[x], datosTrain[y], realTrain[y], nominalTrain[y], nulosTrain[y], distanceEu);
    if (dist < minDistEnemigo[x])
      return 1;
    else return 0;
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


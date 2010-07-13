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
//  ENRBF.java
//
//  Salvador García López
//
//  Created by Salvador García López 25-11-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ENRBF;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class ENRBF extends Metodo {

  /*Own parameters of the algorithm*/
  double alpha, sigma;

  public ENRBF (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, k, l;
    int nClases;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    double Gtodos[];
    double Gtotal;
    double probClass[];
    double prob;
    boolean parar;
    boolean valido;

    long tiempo = System.currentTimeMillis();

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    Gtodos = new double[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = true;
    nSel = datosTrain.length;

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    probClass = new double[nClases];

    /*Body of the algorithm. NRBF estimates probability of k-th class given vector x and a training set.
     Eliminate vector only if this probability is lower than other classes*/
    for (i=0; i<datosTrain.length; i++) {
      Gtotal = 0;
      for (j=0; j<datosTrain.length; j++) {
        if (i != j) {
          Gtodos[j] = G (datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu, sigma);
          Gtotal += Gtodos[j];
        }
      }

      Arrays.fill(probClass,0);
      for (j=0; j<nClases; j++) {
        for (k=0; k<datosTrain.length; k++) {
          if (i != k && clasesTrain[k] == j) {
            probClass[j] += Gtodos[k] / Gtotal;
          }
        }
      }

      /*Eliminate if only his probability is lower than other class*/
      parar = false;
      prob = 0;
      for (j=0; j<nClases && !parar; j++)
        if (j == clasesTrain[i]) {
          parar = true;
          prob = probClass[j];
        }
      valido = true;
      for (j=0; j<nClases && valido; j++) {
        if ((probClass[j]*alpha) > prob)
          valido = false;
      }
      if (!valido) {
        marcas[i] = false;
        nSel--;
      }
    }

    /*Building of the S set from the flags*/
    nSel = 0;
    for (i=0; i<datosTrain.length; i++)
      if (marcas[i]) nSel++;
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (marcas[i]) { //the instance will be copied to the solution
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("ENRBF "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  double G (double x[], double rx[], int nx[], boolean mx[], double xi[], double rxi[], int nxi[], boolean mxi[], boolean distanceEu, double sigma) {

    double distancia;

    distancia = KNN.distancia(x, rx, nx, mx, xi, rxi, nxi, mxi, distanceEu);
    distancia *= distancia;

    return Math.exp((-1)*distancia/sigma);
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

    /*Getting the sigma parameter*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    sigma = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the alpha parameter*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    alpha = Double.parseDouble(tokens.nextToken().substring(1));
  
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;
  }
}


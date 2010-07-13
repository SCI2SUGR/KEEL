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
//  BSE.java
//
//  Salvador García López
//
//  Created by Salvador García López 28-11-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.BSE;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class BSE extends Metodo {

 /*Own parameters of the algorithm*/
  private int k;

  public BSE (String ficheroScript) {
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
    int bestEval = 0;
    int eval;
    boolean parar = false;
    int peor;

    long tiempo = System.currentTimeMillis();

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = true;
    nSel = datosTrain.length;

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    for (i=0; i<datosTrain.length; i++) {
      claseObt = KNN.evaluacionKNN2(k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
      if (claseObt == clasesTrain[i]) {
        bestEval++;
      }
    }

    /*Body of the algorithm. BSE removes the object with the smallest contribution for the subset quality.*/
    while (!parar) {
      peor = -1;
      for (i=0; i<datosTrain.length; i++) {
        if (marcas[i]) {
          marcas[i] = false;
          nSel--;
          eval = 0;
          /*Building of the S set from the flags*/
          conjS = new double[nSel][datosTrain[0].length];
          conjR = new double[nSel][datosTrain[0].length];
          conjN = new int[nSel][datosTrain[0].length];
          conjM = new boolean[nSel][datosTrain[0].length];
          clasesS = new int[nSel];
          for (j=0, l=0; j<datosTrain.length; j++) {
            if (marcas[j]) { //the instance will be copied to the solution
              for (m=0; m<datosTrain[0].length; m++) {
                conjS[l][m] = datosTrain[j][m];
                conjR[l][m] = realTrain[j][m];
                conjN[l][m] = nominalTrain[j][m];
                conjM[l][m] = nulosTrain[j][m];
              }
              clasesS[l] = clasesTrain[j];
              l++;
            }
          }
          for (j=0; j<datosTrain.length; j++) {
              claseObt = KNN.evaluacionKNN2(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], nClases, distanceEu);
            if (claseObt == clasesTrain[j]) {
              eval++;
            }
          }
          if (eval >= bestEval) {
            peor = i;
            bestEval = eval;
          }
          marcas[i] = true;
          nSel++;
        }
      }
      if (peor >= 0) {
        marcas[peor] = false;
        nSel--;
      } else {
        parar = true;
      }
    }

    /*Building of the S set from the flags*/
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

    System.out.println("BSE "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
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

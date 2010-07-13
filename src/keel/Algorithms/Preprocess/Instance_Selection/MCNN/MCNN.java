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
//  MCNN.java
//
//  Salvador García López
//
//  Created by Salvador García López 7-4-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.MCNN;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.*;

public class MCNN extends Metodo {

  public MCNN (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    boolean St[];
    boolean Sm[];
    boolean Sr[];
    double centers[][];
    int contadores[];
    int represent[];
    double minDist, distan;
    int pos;
    boolean paso1, paso2, entra;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the flagged instaces vector for a posterior elimination*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = false;
    nSel = 0;

    St = new boolean[datosTrain.length];
    Sr = new boolean[datosTrain.length];
    Sm = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      St[i] = true;
    }

    centers = new double[nClases][datosTrain[0].length];
    contadores = new int[nClases];
    represent = new int[nClases];

    do {

      do {

        for (i = 0; i < nClases; i++) {
          Arrays.fill(centers[i], 0.0);
        }
        Arrays.fill(contadores, 0);

        /*Compute the centroids of the instances selected*/
        for (i = 0; i < datosTrain.length; i++) {
          if (St[i]) {
            for (j = 0; j < datosTrain[0].length; j++) {
              centers[clasesTrain[i]][j] += datosTrain[i][j];
            }
            contadores[clasesTrain[i]]++;
          }
        }
        for (i = 0; i < nClases; i++) {
          for (j = 0; j < datosTrain[0].length; j++) {
            centers[i][j] /= (double) contadores[i];
          }
        }

        /*Search the nearest instance to each center of its own class*/
        for (i = 0; i < nClases; i++) {
          minDist = Double.POSITIVE_INFINITY;
          pos = -1;
          for (j = 0; j < datosTrain.length; j++) {
            if (clasesTrain[j] == i) {
              distan = KNN.distancia(centers[i], datosTrain[j]);
              if (distan < minDist) {
                minDist = distan;
                pos = j;
              }
            }
          }
          represent[i] = pos;
        }

        Arrays.fill(Sr, false);
        Arrays.fill(Sm, false);

        /*Classify the instances belonging to St*/
        paso1 = false;
        for (i = 0; i < datosTrain.length; i++) {
          if (St[i]) {
            minDist = Double.POSITIVE_INFINITY;
            pos = -1;
            for (j = 0; j < nClases; j++) {
              if (represent[j] != -1) {
                distan = KNN.distancia(datosTrain[represent[j]], datosTrain[i]);
              } else {
                distan = Double.POSITIVE_INFINITY;
              }
              if (distan < minDist) {
                minDist = distan;
                pos = j;
              }
            }
            if (clasesTrain[i] == pos) {
              Sr[i] = true;
            }
            else {
              Sm[i] = true;
              paso1 = true;
            }
          }
        }

        for (i = 0; i < datosTrain.length; i++) {
          St[i] = Sr[i];
        }

      }
      while (paso1 == true);

      /*Include the most representative points to the selected subset*/
      entra = false;
      for (i = 0; i < nClases; i++) {
        if (represent[i] >= 0) {
        	if (!marcas[represent[i]]) {
        		marcas[represent[i]] = true;
        		nSel++;
        		entra = true;
        	}
        }
      }

      Arrays.fill(Sr, false);
      Arrays.fill(Sm, false);

	  if (nSel == 0) {
		  nSel = 1;
		  marcas[0] = true;
	  }

      /*Classify the instance belonging to the training data set*/
      paso2 = false;
      for (i = 0; i < datosTrain.length; i++) {
        minDist = Double.POSITIVE_INFINITY;
        pos = -1;
        for (j = 0; j < datosTrain.length; j++) {
          if (marcas[j]) {
            distan = KNN.distancia(datosTrain[i], datosTrain[j]);
            if (distan < minDist) {
              minDist = distan;
              pos = j;
            }
          }
        }
        if (pos >= 0 && clasesTrain[i] == clasesTrain[pos]) {
          Sr[i] = true;
        }
        else {
          Sm[i] = true;
          paso2 = true;
        }
      }

      for (i = 0; i < datosTrain.length; i++) {
        St[i] = Sm[i];
      }     
    } while (paso2 == true && entra == true);

    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (marcas[m]) { //the instance must be copied to the solution
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

    System.out.println("MCNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
  }
}


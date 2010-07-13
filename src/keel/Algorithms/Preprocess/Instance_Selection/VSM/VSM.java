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
//  VSM.java
//
//  Salvador García López
//
//  Created by Salvador García López 12-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.VSM;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class VSM extends Metodo {

 /*Own parameters of the algorithm*/
  private long semilla;
  private int k;

  public VSM (String ficheroScript) {
    super (ficheroScript);
  }
  
  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    Referencia votos = new Referencia();
    int baraje[];
    int pos, tmp;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    if (k > clasesTrain.length)
      k = clasesTrain.length;

    /*Shuffle the train set to random the presentation of instances*/
    Randomize.setSeed (semilla);
    baraje = new int[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      baraje[i] = i;
    for (i=0; i<datosTrain.length; i++) {
      pos = Randomize.Randint(i,datosTrain.length-1);
      tmp = baraje[i];
      baraje[i] = baraje[pos];
      baraje[pos] = tmp;
    }

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = true;
    nSel = datosTrain.length;

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

    /*Body of the algorithm. For each instance in T, search for the conformity of its mayority
     with its nearest neirborhood (60% mayority). If it is positive, the instance is selected
     for a later remove.*/
    for (i=0; i<datosTrain.length; i++) {
      /*Apply KNN to the instance*/
      claseObt = KNN.evaluacionKNN2(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[baraje[i]], realTrain[baraje[i]], nominalTrain[baraje[i]], nulosTrain[baraje[i]], nClases, distanceEu, votos);
      if (claseObt == clasesTrain[baraje[i]] && ((double)votos.entero/(double)k) >= 0.6) {
        //agree its mayority, it is removed from the solution set
        marcas[baraje[i]] = false;
        nSel--;

        /*Building of the S set from the flags*/
        conjS = new double[nSel][datosTrain[0].length];
        conjR = new double[nSel][datosTrain[0].length];
        conjN = new int[nSel][datosTrain[0].length];
        conjM = new boolean[nSel][datosTrain[0].length];
        clasesS = new int[nSel];
        for (m=0, l=0; m<datosTrain.length; m++) {
          if (marcas[m]) { //the instance will be copied to the solution
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
      }
    }

    System.out.println("VSM "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

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

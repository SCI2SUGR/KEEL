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
//  PSRCG.java
//
//  Salvador García López
//
//  Created by Salvador García López 1-6-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.PSRCG;

import keel.Algorithms.Preprocess.Basic.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import org.core.*;

public class PSRCG extends Metodo {

 /*Own parameters of the algorithm*/

    public PSRCG (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l;
    boolean grafo[][];
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int loc = 0, loc2 = 0;
    double minDist, dist;
    int nClases[], nc;
    boolean parar;
    int nombreClases[];
    double RCG1, RCG2;
    double uncer[];
    double maxUnc;
    int cont1, cont2, pos=0;

    long tiempo = System.currentTimeMillis();

    /*Getting the name of differents classes*/
    nClases = new int[clasesTrain.length];
    Arrays.fill(nClases,Integer.MIN_VALUE);
    nc = 0;
    for (i=0; i<clasesTrain.length; i++) {
      parar = false;
      for (j=0; j<nClases.length && nClases[j]!=Integer.MIN_VALUE; j++) {
        if (nClases[j] == clasesTrain[i])
          parar = true;
      }
      if (!parar) {
        nClases[nc] = clasesTrain[i];
        nc++;
      }
    }
    nombreClases = new int[nc];
    for (i=0; i<nc; i++)
      nombreClases[i] = nClases[i];

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = true;
    nSel = datosTrain.length;


    /*Inicialization of the KNN graph*/
    grafo = new boolean[datosTrain.length][datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      Arrays.fill(grafo[i], false);
      grafo[i][i] = true;
    }

    /*Get the initialy KNN graph*/
    for (i=0; i<datosTrain.length; i++) {
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        if (i != j) {
          dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
          if (dist < minDist) {
            minDist = dist;
            loc = j;
          }
        }
      }
      grafo[i][loc] = true;
      grafo[loc][i] = true;
    }

    uncer = new double[datosTrain.length];
    RCG2 = computeRCG (clasesTrain, grafo, marcas, nombreClases, nSel);
    do {
      RCG1 = RCG2;

      /*Calculate the uncertainty of each instance*/
      for (i=0; i<datosTrain.length; i++) {
        if (marcas[i]) {
          uncer[i] = Uloc (clasesTrain, marcas, nombreClases, grafo, i);
        }
      }

      /*select the instance with max uncertainty*/
      maxUnc = Double.NEGATIVE_INFINITY;
      for (i=0; i<datosTrain.length; i++) {
        if (marcas[i]) {
          if (uncer[i] > maxUnc) {
            maxUnc = uncer[i];
            pos = i;
          } else if (uncer[i] == maxUnc) {
            cont1 = cont2 = 0;
            for (j=0; j<grafo[i].length; j++)
              if (grafo[i][j] && marcas[j]) cont1++;
            for (j=0; j<grafo[pos].length; j++)
              if (grafo[pos][j] && marcas[j]) cont2++;
            if (cont1 < cont2)
              pos = i;
          }
        }
      }

      /*Remove the instance selected*/
      marcas[pos] = false;
      nSel--;

      /*Compute RCG*/
      RCG2 = computeRCG (clasesTrain, grafo, marcas, nombreClases, nSel);
    } while (RCG2 < RCG1 || !(RCG2 > 0));

    /*Inicialization of the KNN graph*/
    grafo = new boolean[datosTrain.length][datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      Arrays.fill(grafo[i], false);
      grafo[i][i] = true;
    }

    /*Get the initial KNN graph*/
    for (i=0; i<datosTrain.length; i++) {
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        if (i != j) {
            dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
          if (dist < minDist && minDist == Double.POSITIVE_INFINITY) {
            minDist = dist;
            loc = j;
          } else if (dist < minDist) {
            minDist = dist;
            loc2 = loc;
            loc = j;
          }
        }
      }
      grafo[i][loc] = true;
      grafo[i][loc2] = true;
      grafo[loc][i] = true;
      grafo[loc2][i] = true;
    }

    /*Calculate the uncertainty of each instance*/
    for (i=0; i<datosTrain.length; i++) {
      if (marcas[i]) {
        uncer[i] = Uloc (clasesTrain, marcas, nombreClases, grafo, i);
      }
    }

    /*Remove instances with uncertainty null in the neighbourhood*/
    for (i=0; i<datosTrain.length; i++) {
      if (marcas[i]) {
        if (uncer[i] == 0) {
          marcas[i] = false;
          nSel--;
        }
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

    System.out.println("PSRCG "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  private double computeRCG (int clases[], boolean grafo[][], boolean marcas[], int nombreClases[], int nSel) {

    return (U0(clases,marcas,nombreClases,nSel)-Utot(clases,marcas,nombreClases,grafo,nSel))/U0(clases,marcas,nombreClases,nSel);
  }

  private double U0 (int clases[], boolean marcas[], int nombreClases[], int nSel) {

    int i, j;
    int sumaC;
    double sumaT = 0.0;

    for (i=0; i<nombreClases.length; i++){
      sumaC = 0;
      for (j=0; j<clases.length; j++) {
        if (marcas[j]) { //is in S
          if (clases[j] == nombreClases[i]) { //it has the same label class
            sumaC++;
          }
        }
      }
      sumaT += ((double)sumaC/(double)nSel)*(1.0 - ((double)sumaC/(double)nSel));
    }

    return sumaT;
  }

  private double Uloc (int clases[], boolean marcas[], int nombreClases[], boolean grafo[][], int instance) {

    int i, j;
    int sumaC;
    double sumaT = 0.0;
    int ni=0;

    /*Get the neighbourhood cardinality of the instance*/
    for (i=0; i<grafo[instance].length; i++)
      if (grafo[instance][i] && marcas[i])
        ni++;

    for (i=0; i<nombreClases.length; i++){
      sumaC = 0;
      for (j=0; j<grafo[instance].length; j++) {
        if (grafo[instance][j] && marcas[j]) { //there is an edge and the destiny is in S
          if (clases[j] == nombreClases[i]) { //it has the same label class
            sumaC++;
          }
        }
      }
      sumaT += ((double)sumaC/(double)ni)*(1.0 - ((double)sumaC/(double)ni));
    }

    return sumaT;
  }

  private double Utot (int clases[], boolean marcas[], int nombreClases[], boolean grafo[][], int nSel) {

    int i, j;
    double sumaT = 0.0;
    int cardE = 0;
    int ni;

    /*Get the cardinality of the Edges set*/
    for (i=0; i<grafo.length; i++) {
      if (marcas[i]) {
        for (j=0; j<grafo[i].length; j++) {
          if (marcas[j]) {
            if (grafo[i][j])
              cardE++;
          }
        }
      }
    }

    for (i=0; i<grafo.length; i++) {
      if (marcas[i]) {
        ni=0;

        /*Get the neighbourhood cardinality of the instance*/
        for (j=0; j<grafo[i].length; j++)
          if (grafo[i][j] && marcas[j])
            ni++;

        sumaT += ((double)ni/(double)cardE)*Uloc(clases, marcas, nombreClases, grafo, i);
      }
    }
    return sumaT;
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

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}


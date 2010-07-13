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
//  IB3.java
//
//  Salvador García López
//
//  Created by Salvador García López 14-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.IB3;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class IB3 extends Metodo {

 /*Own parameters of the algorithm*/
  private long semilla;
  private double nAcep;
  private double nDrop;

  /*Another data*/
  int nClases;

  public IB3 (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, o;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int baraje[];
    int pos, tmp;
    int classRecord[][]; //store the number of rigths/fails
    int classFrecuency[][]; //store the class frecuency for each instance
    int CD[];
    int nCD = 0;
    double sim[];
    boolean aceptable;
    double bestsim;
    int ymax, seleccionada;

    long tiempo = System.currentTimeMillis();

    /*Do a shuffle of training instances to a random evaluate*/
    Randomize.setSeed (semilla);
    baraje = new int[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      baraje[i] = i;
    }
    for (i=0; i<datosTrain.length; i++) {
      pos = Randomize.Randint (i, datosTrain.length-1);
      tmp = baraje[i];
      baraje[i] = baraje[pos];
      baraje[pos] = tmp;
    }

    CD = new int[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      CD[i] = -1;
    sim = new double[datosTrain.length];

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the clasification register*/
    classRecord = new int[datosTrain.length][2];
    for (i=0; i<datosTrain.length; i++) {
      classRecord[i][0] = 0;
      classRecord[i][1] = 0;
    }
    classFrecuency = new int[datosTrain.length+1][nClases];
    for (i=0; i<datosTrain.length+1; i++)
      for (j=0; j<nClases; j++)
        classFrecuency[i][j] = 0;

    /*Body of the IB3 algorithm. If an instance of the train set does not agree with the nearest
     acceptable instance in S, it is added to S*/
    for (o=0; o<datosTrain.length; o++) {
      i = baraje[o]; //maintain the random order
      if (nCD == 0) {//S set empty
        CD[nCD] = i; //put an instance
        nCD++;

        /*Increment clasification*/
        classFrecuency[i][clasesTrain[i]] ++;
        classFrecuency[datosTrain.length][clasesTrain[i]] ++;

        /*update register of this instance*/
        classRecord[i][0] ++; //get right
      } else {
        /*Compute the distance from i to j, with j in CD*/
        for (j=0; j<nCD; j++) {
          sim[CD[j]] = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[CD[j]], realTrain[CD[j]], nominalTrain[CD[j]], nulosTrain[CD[j]], distanceEu);	
        }

        /*Check if there is any j that it is acceptable*/
        aceptable = false;
        bestsim = Double.POSITIVE_INFINITY;
        seleccionada = ymax = -1;
        for (j=0; j<nCD; j++) {
          if (esAceptable(CD[j], classRecord, classFrecuency, i)) {
            if (sim[CD[j]] < bestsim) {
              aceptable = true;
              ymax = CD[j];
              bestsim = sim[CD[j]];
              seleccionada = CD[j];
            }
          }
        }

        /*If there is not acceptables, take one of nCD*/
        if (!aceptable) {
          ymax = CD[Randomize.Randint(0,nCD-1)];
          seleccionada = ymax;
        }

        if (clasesTrain[i] != clasesTrain[ymax]) {
          CD[nCD] = i;
          sim[CD[nCD]] = 0;
          nCD++;
        }

        nCD = ActualizaRegistro (sim, CD, nCD, classRecord, clasesTrain[i], classFrecuency, i, seleccionada);
      }
    }

    /*Building of the S set from CD*/
    conjS = new double[nCD][datosTrain[0].length];
    conjR = new double[nCD][datosTrain[0].length];
    conjN = new int[nCD][datosTrain[0].length];
    conjM = new boolean[nCD][datosTrain[0].length];
    clasesS = new int[nCD];
    for (i=0; i<nCD; i++) {
      for (j=0; j<datosTrain[0].length; j++) {
        conjS[i][j] = datosTrain[CD[i]][j];
        conjR[i][j] = realTrain[CD[i]][j];
        conjN[i][j] = nominalTrain[CD[i]][j];
        conjM[i][j] = nulosTrain[CD[i]][j];
      }
      clasesS[i] = clasesTrain[CD[i]];
    }

    System.out.println("IB3 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /*Function that returns if an instance is acceptable or not*/
  boolean esAceptable (int muest, int clRecord[][], int clasFrec[][], int total) {

    double p, z, n;
    double aux;
    double factor;
    double denominador, numeradorMax, numeradorMin;
    double umbralMaxAc, umbralMinAc;
    double umbralMaxFr, umbralMinFr;
    int i;
    boolean acept = false;

    n = (double)(clRecord[muest][0]+clRecord[muest][1]);
    aux = (double)clRecord[muest][0];
    p = aux / n;
    z = nAcep;

    factor = ((p*(1-p))/n) + ((z*z)/(4*n*n));
    factor = z*Math.sqrt(factor);

    denominador = 1+(z*z/n);
    numeradorMax = (p + (z*z/2*n)+factor);
    numeradorMin = (p + (z*z/2*n)-factor);
    umbralMaxAc = numeradorMax / denominador;
    umbralMinAc = numeradorMin / denominador;

    umbralMinAc = p * nAcep;

    n = 0;
    for (i=0; i<nClases; i++)
      n = n + (double)clasFrec[muest][i];

    aux = (double)clasFrec[muest][clasesTrain[muest]];
    p = aux / n;
    z = nAcep;

    factor = (p*(1-p))/n + (z*z)/(4*n*n);
    factor = z*Math.sqrt(factor);

    denominador = 1+(z*z/n);
    numeradorMax = (p + z*z/2*n+factor);
    numeradorMin = (p + z*z/2*n-factor);
    umbralMaxFr = numeradorMax / denominador;
    umbralMinFr = numeradorMin / denominador;

    umbralMaxFr = p;

    if (umbralMinAc > umbralMaxFr)
      acept = true;

    return acept;
  }

  /*Function that updates the clasification registry*/
  int ActualizaRegistro (double sim[], int CD[], int nCD, int classRecord[][], int xclase, int clasFrec[][], int total, int usada) {

    int i, j;

    /*Update of the meter of classes for each instance in CD*/
    for (i=0; i<nCD; i++)
      clasFrec[CD[i]][xclase] ++;
    clasFrec[datosTrain.length][xclase] ++;

    /*Update the register of all instances*/
    for (i=0; i<nCD; i++) {
      if (sim[CD[i]] <= sim[usada]) {
        if (clasesTrain[CD[i]] == xclase)
          classRecord[CD[i]][0] ++;
        else
          classRecord[CD[i]][1] ++;

        /*Check if eliminate this instance from CD[i]*/
        if (esDropable (CD[i], classRecord, clasFrec, total)) {
          CD[i] = -1;
        }
      }
    }

    /*Eliminate them now*/
    for (i=0; i<nCD; i++) {
      if (CD[i] < 0) {
        for (j=i; j<nCD; j++)
          CD[j] = CD[j+1];
        nCD--;
        i--;
      }
    }

    return nCD;
  }

  /*Function that returns if an instance is droppable or not*/
  boolean esDropable (int muest, int clRecord[][], int clasFrec[][], int total) {

    double p, z, n;
    double aux;
    double factor;
    double denominador, numeradorMax, numeradorMin;
    double umbralMaxAc, umbralMinAc;
    double umbralMaxFr, umbralMinFr;
    int i;
    boolean acept = false;

    n = (double)(clRecord[muest][0]+clRecord[muest][1]);
    aux = (double)clRecord[muest][0];
    p = aux / n;
    z = nDrop;

    factor = (p*(1-p))/n + (z*z)/(4*n*n);
    factor = z*Math.sqrt(factor);

    denominador = 1+(z*z/n);
    numeradorMax = (p + z*z/2*n+factor);
    numeradorMin = (p + z*z/2*n-factor);
    umbralMaxAc = numeradorMax / denominador;
    umbralMinAc = numeradorMin / denominador;

    umbralMaxAc = p;

    n = 0;
    for (i=0; i<nClases; i++)
      n = n + (double)clasFrec[muest][i];

    aux = (double)clasFrec[muest][clasesTrain[muest]];
    p = aux / n;
    z = nDrop;

    factor = (p*(1-p))/n + (z*z)/(4*n*n);
    factor = z*Math.sqrt(factor);

    denominador = 1+(z*z/n);
    numeradorMax = (p + z*z/2*n+factor);
    numeradorMin = (p + z*z/2*n-factor);
    umbralMaxFr = numeradorMax / denominador;
    umbralMinFr = numeradorMin / denominador;

    umbralMinFr = p * nDrop;

    if (umbralMaxAc < umbralMinFr)
      acept = true;

    return acept;
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

    /*Getting the confianze level of acceptability and dropping*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nAcep = Double.parseDouble(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nDrop = Double.parseDouble(tokens.nextToken().substring(1));
  
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
}
}

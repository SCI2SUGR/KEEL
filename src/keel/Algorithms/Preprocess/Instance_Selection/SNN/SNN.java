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
//  SNN.java
//
//  Salvador García López
//
//  Created by Salvador García López 18-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.SNN;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;
import java.util.StringTokenizer;

public class SNN extends Metodo {

  public SNN (String ficheroScript) {
    super (ficheroScript);
  }
  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    boolean marcas[];
    int nSel;
    boolean filas[], columnas[];
    double minDist, dist;
    double distEnemCercano[];
    boolean A[][];
    boolean continuar;
    int nFilas, nColumnas;
    int cont, select;
    boolean borrar;

    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the flagged instances vector of S set*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = false;
    nSel = 0;

    /*Allocate memory for the bits matrix and vector that indicate if the rows and columns     of this matrix are valid, also the nearest enemy distance vector*/
    filas = new boolean[datosTrain.length];
    columnas = new boolean[datosTrain.length];
    distEnemCercano = new double[datosTrain.length];
    A = new boolean[datosTrain.length][datosTrain.length];
    nFilas = datosTrain.length;
    nColumnas = datosTrain.length;

    /*Inicialization of the previous vectors*/
    for (i=0; i<datosTrain.length; i++) {
      filas[i] = true;
      columnas[i] = true;
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
    	dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
        if (clasesTrain[i] != clasesTrain[j] && dist < minDist)
          minDist = dist;
      }
      distEnemCercano[i] = minDist;
    }

    /*Calculate the bits matrix. bits(i,j) are 'true0 if class(i)==class(j) and are
     *nearest than the nearest enemy of i*/
    for (i=0; i<datosTrain.length; i++)
      for (j=0; j<datosTrain.length; j++) {
      	dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
        if (clasesTrain[i] == clasesTrain[j] && dist < distEnemCercano[i])
          A[i][j] = true;
        else A[i][j] = false;
      }

    /*Body of the SNN algorithm.*/
    do {
      continuar = false;
      /*STEP 1: For the columns with an unique bit on, delete the rest of columns
       with this same bit on, the row and add the instance of the row in S.*/
      for (i=0; i<datosTrain.length; i++) {
        if (columnas[i]) { //valid column
          cont = 0;
          select = -1;
          for (j=0; j<datosTrain.length && cont < 2; j++) {
            if (filas[j] && A[j][i]) {
              cont++;
              select = j;
            }
          }
          if (cont == 1) { //this column only have a bit on
            continuar = true;
            for (j=0; j<datosTrain.length; j++) {
              if (columnas[j] && A[select][j]) {
                columnas[j] = false;
                nColumnas--;
              }
            }
            filas[select] = false;
            nFilas--;
            marcas[select] = true;
            nSel++;
          }
        }
      }

      /*STEP 2: Delete all rows with bits are not contained in other rows*/
      for (i=0; i<datosTrain.length; i++) {
        if (filas[i]) { //valid row
          borrar = false;
          for (j=0; j<datosTrain.length && !borrar; j++) {
            if (filas[j] && i != j) { //different valid row
              borrar = true;
              for (l=0; l<datosTrain.length && borrar; l++) {
                if (columnas[l]) { //valid column
                  if (A[i][l] && !(A[j][l]))
                    borrar = false;
                }
              }
              if (borrar) {
                filas[i] = false;
                nFilas--;
                continuar = true;
              }
            }
          }
        }
      }

      /*STEP 3: Delete all columns with off bits are contained in other columns*/
      for (i=0; i<datosTrain.length; i++) {
        if (columnas[i]) { //valid column
          borrar = false;
          for (j=0; j<datosTrain.length && !borrar; j++) {
            if (columnas[j] && i != j) { //different valid column
              borrar = true;
              for (l=0; l<datosTrain.length && borrar; l++) {
                if (filas[l]) { //valid row
                  if (!(A[l][i]) && A[l][j])
                    borrar = false;
                }
              }
              if (borrar) {
                columnas[i] = false;
                nColumnas--;
                continuar = true;
              }
            }
          }
        }
      }
    } while (continuar);

    /*STEP 5: Find the row j that needs less rows to be included in S*/
    /*Not included*/
    if (nColumnas > 0) {

    }

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

    System.out.println("SNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
    
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
  
}


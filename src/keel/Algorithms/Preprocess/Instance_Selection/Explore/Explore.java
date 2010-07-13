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
//  Explore.java
//
//  Salvador García López
//
//  Created by Salvador García López 1-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Explore;

import keel.Algorithms.Preprocess.Basic.*;

import java.util.StringTokenizer;
import java.util.Arrays;
import org.core.*;

public class Explore extends Metodo{

  /*Own parameters of the algorithm*/
  private long semilla;
  private int k;

  public Explore (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    int tClase[];
    int C = 0;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int baraje[];
    int pos, tmp;
    double coste_mejor, coste_actual;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Get the different classes that exist in the set*/
    tClase = new int[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      tClase[i] = Integer.MAX_VALUE;
    for (i=0; i<datosTrain.length; i++) {
      if (Arrays.binarySearch(tClase,clasesTrain[i]) < 0) { //not found
        tClase[C] = clasesTrain[i];
        C++;
        Arrays.sort(tClase);
      }
    }

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

    /*Inicialization of the flagged instaces vector to a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = false;
    if (datosTrain.length > 0) {
      marcas[baraje[0]] = true; //insert the first one always
      nSel = 1;
    } else {
      System.err.println("Input dataset is empty");
      nSel = 0;
    }

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
    coste_mejor = EncodingLength.evaluaEL (datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, conjS, conjR, conjN, conjM, clasesS, C, k, nClases, distanceEu);

    /*Body of the ELH algorithm. For each instane in T, it is included in T if improves the cost function Encoding Length*/
    for (i=1; i<datosTrain.length; i++) {
      marcas[baraje[i]] = true;
      nSel++;

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
      coste_actual = EncodingLength.evaluaEL (datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, conjS, conjR, conjN, conjM, clasesS, C, k, nClases, distanceEu);
      if (coste_actual < coste_mejor)
        coste_mejor = coste_actual;
      else {
        marcas[baraje[i]] = false;
        nSel--;
      }
    }

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

    /*Do another shuffle for now eliminate instances*/
    for (i=0; i<datosTrain.length; i++)
      baraje[i] = i;
    for (i=0; i<datosTrain.length; i++) {
      pos = Randomize.Randint(i,datosTrain.length-1);
      tmp = baraje[i];
      baraje[i] = baraje[pos];
      baraje[pos] = tmp;
    }

    /*2º part of ELGrow. Now, eliminate instances to see if the cost improves*/
    for (i=0; i<datosTrain.length; i++) {
      if (marcas[baraje[i]]) { //It is in S
        marcas[baraje[i]] = false;
        nSel--;

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
        coste_actual = EncodingLength.evaluaEL (datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, conjS, conjR, conjN, conjM, clasesS, C, k, nClases, distanceEu);
        if (coste_actual < coste_mejor)
          coste_mejor = coste_actual;
        else {
          marcas[baraje[i]] = true;
          nSel++;
        }
      }
    }

    /*3º part of Explore. Do 1000 randoms mutations and maintain the best results*/
    for (i=0; i<1000; i++) {
      pos = Randomize.Randint (0, datosTrain.length-1);
      if (marcas[pos]) {
        marcas[pos] = false;
        nSel--;
      } else {
        marcas[pos] = true;
        nSel++;
      }

      /*Building S set from the flags*/
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

      coste_actual = EncodingLength.evaluaEL (datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, conjS, conjR, conjN, conjM, clasesS, C, k, nClases, distanceEu);
      if (coste_actual < coste_mejor)
        coste_mejor = coste_actual;
      else {
        if (marcas[pos]) {
          marcas[pos] = false;
          nSel--;
        } else {
          marcas[pos] = true;
          nSel++;
        }
      }
    }

    /*Building the S set from the flags*/
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

    System.out.println("Explore "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting the names of the output files*/
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

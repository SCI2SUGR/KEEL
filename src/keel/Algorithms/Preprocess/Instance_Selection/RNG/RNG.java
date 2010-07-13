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
//  RNG.java
//
//  Salvador García López
//
//  Created by Salvador García López 22-2-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.RNG;

import keel.Algorithms.Preprocess.Basic.*;

import java.util.StringTokenizer;
import java.util.Arrays;
import org.core.*;

public class RNG extends Metodo {

 /*Own parameters of the algorithm*/
  private boolean orden;
  private boolean type;


  public RNG (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, k, l;
    boolean grafo[][];
    int nClases;
    boolean marcas[];
    int votos[], votada, votaciones;
    int nSel = 0;
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


    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = true;
    nSel = datosTrain.length;


    /*Inicialization of the graph without edges and votes container*/
    grafo = new boolean[datosTrain.length][datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      Arrays.fill(grafo[i], true);
      grafo[i][i] = false;
    }
    votos = new int[nClases];


    /*Get the proximity graph using Relative Neighbourhood Graph (RNG)*/
    for (i=0; i<datosTrain.length; i++) {
      for (j=0; j<datosTrain.length; j++) {
        for (k=0; k<datosTrain.length && grafo[i][j]; k++) {
          if (i!=k && j!= k) {
            if (KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu) > Math.max(KNN.distancia (datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[k], realTrain[k], nominalTrain[k], nulosTrain[k], distanceEu), KNN.distancia (datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], datosTrain[k], realTrain[k], nominalTrain[k], nulosTrain[k], distanceEu))) {
              grafo[i][j] = false;
            }
          }
        }
      }
    }

    /*Check the order graph*/
    if (!orden) {
      for (i=0; i<datosTrain.length; i++) {
        Arrays.fill(votos,0);
        for (j=0; j<grafo[i].length; j++) {
          if (grafo[i][j]) {
            votos[clasesTrain[j]]++;
          }
        }

        /*count of votes for this instance finalized*/
        votada = 0;
        votaciones = votos[0];
        for (j=1; j<nClases; j++) {
          if (votaciones < votos[j]) {
            votaciones = votos[j];
            votada = j;
          }
        }
        if (type) {
        	if (votada != clasesTrain[i]) {
        		marcas[i] = false;
        		nSel--;
        	}
        } else {
        	if (votada == clasesTrain[i]) {
        		marcas[i] = false;
        		nSel--;
        	}        	
        }
      }
    } else { //2nd order
      for (i=0; i<datosTrain.length; i++) {
        Arrays.fill(votos,0);
        for (j=0; j<grafo[i].length; j++) {
          if (grafo[i][j]) {
            votos[clasesTrain[j]]++;
          }
        }

        /*count of votes for this instance finalized*/
        votada = 0;
        votaciones = votos[0];
        for (j=1; j<nClases; j++) {
          if (votaciones < votos[j]) {
            votaciones = votos[j];
            votada = j;
          }
        }
        if (votada != clasesTrain[i]) {
          /*Using 2nd order graph*/
          for (j=0; j<grafo[i].length; j++) {
            if (grafo[i][j] && clasesTrain[i] == clasesTrain[j]) {
              for (k=0; k<grafo[j].length; k++) {
                if (grafo[j][k]) {
                  votos[clasesTrain[k]]++;
                }
              }
            }
          }

          /*count of votes for this instance finalized*/
          votada = 0;
          votaciones = votos[0];
          for (j=1; j<nClases; j++) {
            if (votaciones < votos[j]) {
              votaciones = votos[j];
              votada = j;
            }
          }
          if (type) {
          	if (votada != clasesTrain[i]) {
          		marcas[i] = false;
          		nSel--;
          	}
          } else {
          	if (votada == clasesTrain[i]) {
          		marcas[i] = false;
          		nSel--;
          	}        	
          }
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

    System.out.println("RNG "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting the order of the graph*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();
    token = token.substring(1);
    if (token.equalsIgnoreCase("2nd_order")) orden = true;
    else orden = false;

    /*Get the type of selection*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    type = tokens.nextToken().substring(1).equalsIgnoreCase("Edition")?true:false;    

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}


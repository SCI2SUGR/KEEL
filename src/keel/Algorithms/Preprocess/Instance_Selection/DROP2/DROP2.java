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

//  DROP2.java

//

//  Salvador García López

//

//  Created by Salvador García López 16-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.DROP2;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;
import org.core.*;


import java.util.StringTokenizer;

import java.util.Vector;

import java.util.Arrays;



public class DROP2 extends Metodo {




  /*Own parameters of the algorithm*/

  private int k;


  public DROP2 (String ficheroScript) {
    super (ficheroScript);
  }


  public void ejecutar () {



    int i, j, l, m, n, o;

    int nClases;

    int claseObt;

    boolean marcas[];

    int nSel;

    double conjS[][];

    int clasesS[];

    int vecinos[][];

    Vector asociados[];

    int aciertosSin;

    int vecinosTemp[];

    double distTemp[];

    double dist, bestD;

    boolean parar;

    Referencia orden[];

    int temp1;

    double temp2;

    int mayoria;



    long tiempo = System.currentTimeMillis();



    /*Getting the number of different classes*/

    nClases = 0;

    for (i=0; i<clasesTrain.length; i++)

      if (clasesTrain[i] > nClases)

        nClases = clasesTrain[i];

    nClases++;



    /*Building a instance vector with distances to the nearest enemy*/

    orden = new Referencia[datosTrain.length];

    for (i=0; i<datosTrain.length; i++) {

      bestD = Double.POSITIVE_INFINITY;

      for (j=0; j<datosTrain.length; j++) {

        if (clasesTrain[i] != clasesTrain[j]) {

          dist = KNN.distancia (datosTrain[i], datosTrain[j]);

          if (dist < bestD)

            bestD = dist;

        }

      }

      orden[i] = new Referencia (i, bestD);

    }



    /*Sort the previos vector*/

    Arrays.sort(orden);



    /*Inicialization of the instance flagged vector of S set*/

    marcas = new boolean[datosTrain.length];

    for (i=0; i<datosTrain.length; i++) {

      marcas[i] = true;

    }

    nSel = datosTrain.length;



    /*Inicialization of data structures of neighbors and associates*/

    distTemp = new double[k+1];

    vecinosTemp = new int[k+1];

    vecinos = new int[datosTrain.length][k+1];

    asociados = new Vector[datosTrain.length];

    for (i=0; i<datosTrain.length; i++)

      asociados[i] = new Vector ();



    /*Body of the DROP2 algorithm. It calculates, for each instance, a set of associates instances
     and look if the deletion of the main instance produces a change of accuracy in those associates*/

    for (i=0; i<datosTrain.length; i++) {

      /*Calculate the k+1 nearest neighbors of each instance*/

      KNN.evaluacionKNN2 (k+1, datosTrain, clasesTrain, datosTrain[i], nClases, vecinos[i]);

      for (j=0; j<vecinos[i].length; j++) {

        asociados[vecinos[i][j]].addElement (new Referencia (i,0));

      }

    }



    /*Check if delete or not the instancias considering the WITH and WITHOUT sets*/

    for (o=0; o<datosTrain.length; o++){

      i = orden[o].entero;

      aciertosSin = 0;



      marcas[i] = false;

      nSel--;

      /*Construction of S set from the temporal flags*/

      conjS = new double[nSel][datosTrain[0].length];

      clasesS = new int[nSel];

      for (m=0, l=0; m<datosTrain.length; m++) {

        if (marcas[m]) { //the instance will evaluate

          for (j=0; j<datosTrain[0].length; j++) {

            conjS[l][j] = datosTrain[m][j];

          }

          clasesS[l] = clasesTrain[m];

          l++;

        }

      }



      marcas[i] = true;

      nSel++;



      /*Evaluation of associates without the instance in T*/

      for (j=0; j<k+1; j++) {

        claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, datosTrain[vecinos[i][j]], nClases);

        if (claseObt == clasesTrain[vecinos[i][j]])  //it classify it correctly

          aciertosSin++;

      }



      mayoria = (k+1) / 2;

      if (aciertosSin > mayoria) {

        /*Delete P of S*/

        marcas[i] = false;

        nSel--;



        /*For each associate of P, search a new nearest neighbor*/

        for (j=0; j<asociados[i].size(); j++) {

          for (l=0; l<k+1; l++) {

            vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];

            vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;

            distTemp[l] = Double.POSITIVE_INFINITY;

          }

          for (l=0; l<datosTrain.length; l++) {

            if (marcas[l]) { //is in S

              dist = KNN.distancia (datosTrain[((Referencia)(asociados[i].elementAt(j))).entero],datosTrain[l]);

              parar = false;



              /*Get the nearest neighbors in this situation again*/

              for (m=0; m<(k+1) && !parar; m++) {

                if (dist < distTemp[m]) {

                  parar = true;

                  for (n=m+1; n<k+1; n++) {

                    distTemp[n] = distTemp[n-1];

                    vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n-1];

                  }

                  distTemp[m] = dist;

                  vecinos[((Referencia)(asociados[i].elementAt(j))).entero][m] = l;

                }

              }

            }

          }



          /*Add to the list of associates of the new neighbor this instance*/

          for (l=0; l<k+1; l++) {

            parar = false;

            for (m=0; m<asociados[vecinosTemp[l]].size() && !parar; m++) {

              if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero

                  && vecinosTemp[l] != i) {

                asociados[vecinosTemp[l]].removeElementAt(m);

                parar = true;

              }

            }

          }

          for (l=0; l<k+1; l++) {

            asociados[vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l]].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));

          }

        }

      }

    }



    /*Construction of S set from the flags*/

    conjS = new double[nSel][datosTrain[0].length];

    clasesS = new int[nSel];

    for (m=0, l=0; m<datosTrain.length; m++) {

      if (marcas[m]) { //the instance will evaluate

        for (j=0; j<datosTrain[0].length; j++) {

          conjS[l][j] = datosTrain[m][j];

        }

        clasesS[l] = clasesTrain[m];

        l++;

      }

    }



    System.out.println("DROP2 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");



    OutputIS.escribeSalida(ficheroSalida[0], conjS, clasesS, entradas, salida, nEntradas, relation);
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

  }



}


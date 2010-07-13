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

//  RENN.java

//

//  Salvador García López

//

//  Created by Salvador García López 11-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.RENN;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;
import org.core.*;
import java.util.StringTokenizer;

import java.util.Vector;



public class RENN extends Metodo {




 /*Own parameters of the algorithm*/

  private int k;


  public RENN (String ficheroScript) {
    super (ficheroScript);
  }


  public void ejecutar () {



    int i, j, l;

    int nClases;

    int claseObt;

    boolean marcas[];

    int nSel = 0;

    double conjS[][];

    int clasesS[];

    double conjS2[][];

    int clasesS2[];

    boolean fin = false;



    long tiempo = System.currentTimeMillis();



    /*Copy the original data to the S set*/

    conjS = new double[datosTrain.length][datosTrain[0].length];

    clasesS = new int[datosTrain.length];

    for (i=0; i<datosTrain.length; i++) {

        for (j=0; j<datosTrain[0].length; j++) {

          conjS[i][j] = datosTrain[i][j];

      }

      clasesS[i] = clasesTrain[i];

    }



    /*Getting the number of differents classes*/

    nClases = 0;

    for (i=0; i<clasesTrain.length; i++)

      if (clasesTrain[i] > nClases)

        nClases = clasesTrain[i];

    nClases++;



    /*Body of the RENN algorithm. Introduce an external loop considering ENN.*/

    while (!fin) {

      /*Inicialization of the flagged instances vector for a posterior copy*/

      marcas = new boolean[conjS.length];

      for (i=0; i<conjS.length; i++)

        marcas[i] = false;

      nSel = 0;



      for (i=0; i<conjS.length; i++) {

        /*Apply KNN to the instance*/

        claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, conjS[i], nClases);

        if (claseObt == clasesS[i]) { //conform with your mayority, it is included in the solution set

          marcas[i] = true;

          nSel++;

        }

      }

      if (nSel == conjS.length) { //all the instances are conform in the set

        fin = true;

      } else {//any instance must be eliminated

        /*Building of the S set from the flags*/

        conjS2 = new double[nSel][datosTrain[0].length];

        clasesS2 = new int[nSel];

        for (i=0, l=0; i<conjS.length; i++) {

          if (marcas[i]) { //the instance will be copied to the solution

            for (j=0; j<datosTrain[0].length; j++) {

              conjS2[l][j] = conjS[i][j];

            }

            clasesS2[l] = clasesS[i];

            l++;

          }

        }

        conjS = conjS2;

        clasesS = clasesS2;

      }

    }



    System.out.println("RENN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");


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


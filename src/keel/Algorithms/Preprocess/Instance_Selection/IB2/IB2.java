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

//  IB2.java

//

//  Salvador García López

//

//  Created by Salvador García López 14-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Preprocess.Instance_Selection.IB2;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;
import org.core.*;

import java.util.StringTokenizer;

import java.util.Vector;



public class IB2 extends Metodo {




 /*Own parameters of the algorithm*/

  private long semilla;

  private int k;


  public IB2 (String ficheroScript) {
    super (ficheroScript);
  }


  public void ejecutar () {



    int i, j, l, m;

    int nClases;

    int claseObt;

    boolean marcas[];

    int nSel;

    double conjS[][];

    int clasesS[];

    int baraje[];

    int pos, tmp;



    long tiempo = System.currentTimeMillis();



    /*Getting the number of differents classes*/

    nClases = 0;

    for (i=0; i<clasesTrain.length; i++)

      if (clasesTrain[i] > nClases)

        nClases = clasesTrain[i];

    nClases++;



    /*Shuffle the train set*/

    baraje = new int[datosTrain.length];

    Randomize.setSeed (semilla);

    for (i=0; i<datosTrain.length; i++)

      baraje[i] = i;

    for (i=0; i<datosTrain.length; i++) {

      pos = Randomize.Randint (i, datosTrain.length-1);

      tmp = baraje[i];

      baraje[i] = baraje[pos];

      baraje[pos] = tmp;

    }



    /*Inicialization of the flagged instaces vector for a posterior elimination*/

    marcas = new boolean[datosTrain.length];

    for (i=0; i<datosTrain.length; i++)

      marcas[i] = false;

    if (datosTrain.length > 0) {

      marcas[baraje[0]] = true; //the first instance is included always

      nSel = 1;

    } else {

      System.err.println("Input dataset is empty");

      nSel = 0;

    }



    /*Building of the S set from the flags*/

    conjS = new double[nSel][datosTrain[0].length];

    clasesS = new int[nSel];

    for (m=0, l=0; m<datosTrain.length; m++) {

      if (marcas[m]) { //the instance must be copied to the solution

        for (j=0; j<datosTrain[0].length; j++) {

          conjS[l][j] = datosTrain[m][j];

        }

        clasesS[l] = clasesTrain[m];

        l++;

      }

    }



    /*Body of the IB2 algorithm. If an instance of the train set is misclassified with
     the remainings in the S set, it is included*/

    for (i=1; i<datosTrain.length; i++) {

      /*Classify the instance eliminated in this iteration*/

      claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, datosTrain[baraje[i]], nClases);

      if (claseObt != clasesTrain[baraje[i]]) { //incorrect clasification, add this instance

        marcas[baraje[i]] = true;

        nSel++;



        /*Building of the S set from the flags*/

        conjS = new double[nSel][datosTrain[0].length];

        clasesS = new int[nSel];

        for (m=0, l=0; m<datosTrain.length; m++) {

          if (marcas[m]) { //the instance will be evaluated

            for (j=0; j<datosTrain[0].length; j++) {

              conjS[l][j] = datosTrain[m][j];

            }

            clasesS[l] = clasesTrain[m];

            l++;

          }

        }

      }

    }



    System.out.println("IB2 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");


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

  }

}


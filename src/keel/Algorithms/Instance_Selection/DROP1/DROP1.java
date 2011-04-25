/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

//  DROP1.java

//

//  Salvador Garc�a L�pez

//

//  Created by Salvador Garc�a L�pez 15-7-2004.

//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.

//



package keel.Algorithms.Instance_Selection.DROP1;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;
import org.core.*;


import java.util.StringTokenizer;

import java.util.Vector;



public class DROP1 extends Metodo {




  /*Own parameters of the algorithm*/

  private int k;


  public DROP1 (String ficheroScript) {
    super (ficheroScript);
  }


  public void ejecutar () {



    int i, j, l, m, n;

    int nClases;

    int claseObt;

    boolean marcas[];

    int nSel = 0;

    double conjS[][];

    int clasesS[];

    int vecinos[][];

    Vector asociados[];

    int aciertosCon, aciertosSin;

    int vecinosTemp[];

    double distTemp[];

    double dist;

    boolean parar;



    long tiempo = System.currentTimeMillis();



    /*Getting the number of different classes*/

    nClases = 0;

    for (i=0; i<clasesTrain.length; i++)

      if (clasesTrain[i] > nClases)

        nClases = clasesTrain[i];

    nClases++;



    /*Inicialization of the flags vector of instances of the S set*/

    marcas = new boolean[datosTrain.length];

    for (i=0; i<datosTrain.length; i++) {

      marcas[i] = true;

    }

    nSel = datosTrain.length;



    /*Inicialization of the data structures of neighbors and associates*/

    distTemp = new double[k+1];

    vecinosTemp = new int[k+1];

    vecinos = new int[datosTrain.length][k+1];

    asociados = new Vector[datosTrain.length];

    for (i=0; i<datosTrain.length; i++)

      asociados[i] = new Vector ();



    /*Body of the DROP1 algorithm. It determinates, for each instance, a set of associate instances,
     and look if the deletion of the main instance produces more accerts or fails in those associates*/

    for (i=0; i<datosTrain.length; i++) {

      /*Getting the k+1 nearest neighbors of each instance*/

      KNN.evaluacionKNN2 (k+1, datosTrain, clasesTrain, datosTrain[i], nClases, vecinos[i]);

      for (j=0; j<vecinos[i].length; j++) {

        asociados[vecinos[i][j]].addElement (new Referencia (i,0));

      }

    }



    /*Check if has to delete the instances using the WITH and WITHOUT sets*/

    for (i=0; i<datosTrain.length; i++){

      aciertosCon = 0;

      aciertosSin = 0;



      /*Construction of the S set from the flags*/

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



      /*Evaluation of associates with the instance in S*/

      for (j=0; j<asociados[i].size(); j++) {

        claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], nClases);

        if (claseObt == clasesTrain[((Referencia)(asociados[i].elementAt(j))).entero])  //lo clasifica bien, un acierto

          aciertosCon++;

      }



      marcas[i] = false;

      nSel--;

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



      /*Evaluation of associates without the instance in S*/

      for (j=0; j<asociados[i].size(); j++) {

        claseObt = KNN.evaluacionKNN2 (k, conjS, clasesS, datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], nClases);

        if (claseObt == clasesTrain[((Referencia)(asociados[i].elementAt(j))).entero])  //it is correctlty classified

          aciertosSin++;

      }



      marcas[i] = true;

      nSel++;

      if (aciertosSin >= aciertosCon) {

        /*Deleting P of S*/

        marcas[i] = false;

        nSel--;



        /*For each associate of P, look for a new near neighbor*/

        for (j=0; j<asociados[i].size(); j++) {

          for (l=0; l<k+1; l++) {

            vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];

            vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;

            distTemp[l] = Double.POSITIVE_INFINITY;

          }

          for (l=0; l<datosTrain.length; l++) {

            if (marcas[l]) { //it is from S

              dist = KNN.distancia (datosTrain[((Referencia)(asociados[i].elementAt(j))).entero],datosTrain[l]);

              parar = false;



              /*Getting the nearest neighbors in this situation again*/

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

             for (m=0; vecinosTemp[l] >= 0 && m<asociados[vecinosTemp[l]].size() && !parar; m++) {

               if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero

                   && vecinosTemp[l] != i) {

                 asociados[vecinosTemp[l]].removeElementAt(m);

                 parar = true;

               }

             }

           }

           for (l=0; l<k+1; l++) {

             int pos = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];

             if (pos >= 0)

               asociados[pos].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));

           }

         }



         /*For each neighbor of P, delete it from his list of associates*/

         for (j=0; j<k+1; j++) {

          parar = false;

          for (l=0; vecinos[i][j] >= 0 && l<asociados[vecinos[i][j]].size() && !parar; l++) {

            if (((Referencia)(asociados[vecinos[i][j]].elementAt(l))).entero == i) {

              asociados[vecinos[i][j]].removeElementAt(l);

              parar = true;

            }

          }

        }

      }

    }



    /*Construction of S set from the flags*/

    conjS = new double[nSel][datosTrain[0].length];

    clasesS = new int[nSel];

    for (m=0, l=0; m<datosTrain.length; m++) {

      if (marcas[m]) { //the instanc will evaluate

        for (j=0; j<datosTrain[0].length; j++) {

          conjS[l][j] = datosTrain[m][j];

          clasesS[l] = clasesTrain[m];

        }

        l++;

      }

    }



    System.out.println("DROP1 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");


    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, this.k);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, this.k);
	}
            
     KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);


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
	ficheroValidation = new String (line,i,j-i);
	
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


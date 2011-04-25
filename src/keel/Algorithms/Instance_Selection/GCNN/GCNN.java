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
//  GCNN.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 18-6-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.GCNN;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;

public class GCNN extends Metodo {

	/*Own parameters of the algorithm*/
	private double P;

	public GCNN (String ficheroScript) {
		super (ficheroScript);
	}

	public void ejecutar () {

		int S[];
		int i, j, l;
		int nClases;
		int pos, min;
		int baraje[];
		int tmp;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int tamS;
		int busq;
		boolean continuar;
		boolean porAnadir[];
		double deltaN;
		double dist;
		double minDistP, minDistN, minDist;
		int votes[];

		long tiempo = System.currentTimeMillis();

		porAnadir = new boolean[datosTrain.length];
		Arrays.fill(porAnadir,false);

		deltaN= Double.POSITIVE_INFINITY;
		for (i=0; i<datosTrain.length; i++) {
			for (j=i+1; j<datosTrain.length; j++) {
				if (clasesTrain[i] != clasesTrain[j]) {
					dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
					if (dist < deltaN)
						deltaN = dist;
				}
			}
		}

		/*Inicialization of the candidates set*/
		S = new int[datosTrain.length];
		for (i=0; i<S.length; i++)
			S[i] = Integer.MAX_VALUE;

		/*Getting the number of different classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		tamS = 0;

		if (nClases < 2) {
			System.err.println("Input dataset contains only one class");
			nClases = 0;
		}

		/*Inserting an element of each class, that with more votes casted by its neighbours of the same class*/
		votes = new int[datosTrain.length];
		Arrays.fill(votes, 0);
		for (i=0; i<datosTrain.length; i++) {
			minDist = Double.POSITIVE_INFINITY;
			pos = -1;
			for (j=0; j<datosTrain.length; j++) {
				if (clasesTrain[i] == clasesTrain[j] && i != j) {
					dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
					if (dist < minDist) {
						minDist = dist;
						pos = j;
					}
				}
			}
			if (pos >= 0)
				votes[pos]++;
		}
		for (i=0; i<nClases; i++) {
			min = pos = -1;
			for (j=0; j<votes.length; j++) {
				if (clasesTrain[j] == i && votes[j] > min) {
					min = votes[j];
					pos = j;
				}
			}
			if (pos >= 0) {
				S[tamS] = pos;
				tamS++;
			}
		}

		do {
			/*Inserting an element of each class of the unabsorbed samples, that with more votes casted by its neighbours of the same class*/
			Arrays.fill(votes, 0);
			for (i=0; i<datosTrain.length; i++) {
				if (porAnadir[i]) {
					minDist = Double.POSITIVE_INFINITY;
					pos = -1;
					for (j=0; j<datosTrain.length; j++) {
					if (i != j && porAnadir[j] && clasesTrain[i] == clasesTrain[j]) {
						dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
						if (dist < minDist) {
							minDist = dist;
							pos = j;
						}
					}
					if (pos >= 0)
						votes[pos]++;
					}
				} else {
					votes[i] = -1;
				}				
			}
			for (i=0; i<nClases; i++) {
				min = pos = -1;
				for (j=0; j<votes.length; j++) {
					if (porAnadir[j] && clasesTrain[j] == i && votes[j] > min) {
						min = votes[j];
						pos = j;
					}
				}
				if (pos >= 0) {
					S[tamS] = pos;
					tamS++;
				}
			}
			
			Arrays.fill(porAnadir, false);
			continuar = false;
			baraje = new int[datosTrain.length];
			for (i=0; i<datosTrain.length; i++)
				baraje[i] = i;
			for (i=0; i<datosTrain.length; i++) {
				pos = Randomize.Randint (i, clasesTrain.length-1);
				tmp = baraje[i];
				baraje[i] = baraje[pos];
				baraje[pos] = tmp;
			}

			for (i=0; i<datosTrain.length; i++) {
				/*Construction of the S set from the previous vector S*/
				conjS = new double[tamS][datosTrain[0].length];
				conjR = new double[tamS][datosTrain[0].length];
				conjN = new int[tamS][datosTrain[0].length];
				conjM = new boolean[tamS][datosTrain[0].length];
				clasesS = new int[tamS];
				for (j = 0; j < tamS; j++) {
					for (l = 0; l < datosTrain[0].length; l++) {
						conjS[j][l] = datosTrain[S[j]][l];
						conjR[j][l] = realTrain[S[j]][l];
						conjN[j][l] = nominalTrain[S[j]][l];
						conjM[j][l] = nulosTrain[S[j]][l];
					}
					clasesS[j] = clasesTrain[S[j]];
				}
				Arrays.sort(S);
				busq = Arrays.binarySearch(S, baraje[i]);
				if (busq < 0) { //this instance is not actually included in S
					minDistP = minDistN = Double.POSITIVE_INFINITY;
					for (j=0; j<datosTrain.length; j++) {
						if (baraje[i] != j) {
							dist = KNN.distancia(datosTrain[baraje[i]], realTrain[baraje[i]], nominalTrain[baraje[i]], nulosTrain[baraje[i]], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
							if (clasesTrain[baraje[i]] == clasesTrain[j]) {
								if (dist < minDistP) {
									minDistP = dist;
								}
							} else {
								if (dist < minDistN) {
									minDistN = dist;
								}
							}
						}
					}
					if ((minDistN - minDistP) <= P*deltaN) {
						continuar = true;
						porAnadir[baraje[i]] = true;
					}
				}
			}
		} while (continuar == true);

		/*Construction of the S set from the previous vector S*/
		conjS = new double[tamS][datosTrain[0].length];
		conjR = new double[tamS][datosTrain[0].length];
		conjN = new int[tamS][datosTrain[0].length];
		conjM = new boolean[tamS][datosTrain[0].length];
		clasesS = new int[tamS];
		for (j=0; j<tamS; j++) {
			for (l=0; l<datosTrain[0].length; l++) {
				conjS[j][l] = datosTrain[S[j]][l];
				conjR[j][l] = realTrain[S[j]][l];
				conjN[j][l] = nominalTrain[S[j]][l];
				conjM[j][l] = nulosTrain[S[j]][l];
			}
			clasesS[j] = clasesTrain[S[j]];
		}

		System.out.println("GCNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

        // COn conjS me vale.
        int trainRealClass[][];
        int trainPrediction[][];
                
         trainRealClass = new int[datosTrain.length][1];
		 trainPrediction = new int[datosTrain.length][1];	
                
         //Working on training
         for ( i=0; i<datosTrain.length; i++) {
              trainRealClass[i][0] = clasesTrain[i];
              trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, 1);
          }
                 
          KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
                 
                 
        //Working on test
		int realClass[][] = new int[datosTest.length][1];
		int prediction[][] = new int[datosTest.length][1];	
		
		//Check  time		
				
		for (i=0; i<realClass.length; i++) {
			realClass[i][0] = clasesTest[i];
			prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, 1);
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

     /*Getting the weight P*/
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     P = Double.parseDouble(tokens.nextToken().substring(1));
  
     /*Getting the type of distance function*/
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}


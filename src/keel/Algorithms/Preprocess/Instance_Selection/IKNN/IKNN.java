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
//  IKNN.java
//
//  Salvador García López
//
//  Created by Salvador García López 2-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.IKNN;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class IKNN extends Metodo {

	/*Own parameters of the algorithm*/
	double gammaRate;
	double xiMultiplicative;
	double xiExponential;
	
	public IKNN (String ficheroScript) {
		super (ficheroScript);
	}
  
	public void ejecutar () {

		int i, j, l, m;
		int nClases;
		boolean marcas[];
		int nSel;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int attractive[];
		double minDist, dist;
		boolean stop = false;
		Referencia order[];
		int nElem;
		int Xi_t, t;
		int numberClass[];
		int minClass;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of differents classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		
		numberClass = new int[nClases];
		Arrays.fill(numberClass, 0);
		for (i=0; i<clasesTrain.length; i++) {
			numberClass[clasesTrain[i]]++;
		}
		minClass = numberClass[0];
		for (i=1; i<numberClass.length; i++) {
			if (numberClass[i] < minClass) {
				minClass = numberClass[i];
			}
		}

		/*Inicialization of the flagged instances vector for a posterior copy*/
		marcas = new boolean[datosTrain.length];
		for (i=0; i<datosTrain.length; i++)
			marcas[i] = true;
		nSel = datosTrain.length;
		
		attractive = new int[datosTrain.length];

		/*Body of the algorithm. The attractive capacity is computed for all instances in S and those with capacities greater than gamma and
		  are among the Xi(t) portion of the highest capacities are eliminated in each iteration*/
		t=1;
		while (!stop) {
			Arrays.fill(attractive, 0);
			/*STEP 2*/
			for (i=0; i<datosTrain.length; i++) {			
				if (marcas[i]) {
					minDist = Double.POSITIVE_INFINITY;
					for (j=0; j<datosTrain.length; j++) {
						if (marcas[j] && clasesTrain[i] != clasesTrain[j]) {
							dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
							if (dist < minDist) {
								minDist = dist;
							}
						}
					}

					for (j=0; j<datosTrain.length; j++) {
						if (marcas[j] && i!=j) {
							dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
							if (dist < minDist) {
								attractive[i]++;
							}
						}
					}
				}
			}
			
			/*STEP 3*/
			stop = true;
			nElem=0;
			for (i=0; i<datosTrain.length; i++) {
				if (marcas[i]) {
					if (attractive[i] >= ((int)((double)minClass)*gammaRate)) {
						stop = false;
						nElem++;
					}
				}
			}
			
			if (!stop) {
				/*STEP 4*/	
				order = new Referencia[nElem];
				j = 0;
				for (i=0; i<datosTrain.length; i++) {
					if (marcas[i]) {
						if (attractive[i] >= ((int)((double)minClass)*gammaRate)) {
							order[j] = new Referencia(i,attractive[i]);
							j++;
						}
					}
				}
				Arrays.sort(order);
				Xi_t = (int)((xiMultiplicative * Math.pow(((double)(t+1)),xiExponential))*datosTrain.length);
				
				for (i=0; i<Xi_t && i<order.length; i++) {
					marcas[order[i].entero] = false;
					nSel--;
				}
				t++;
			}
		}
		
		/*Building of the S set from the flags*/
		conjS = new double[nSel][datosTrain[0].length];
		conjR = new double[nSel][datosTrain[0].length];
        conjN = new int[nSel][datosTrain[0].length];
        conjM = new boolean[nSel][datosTrain[0].length];
        clasesS = new int[nSel];
        for (m=0, l=0; m<datosTrain.length; m++) {
        	if (marcas[m]) { //the instance will be copied to the solution
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

        System.out.println("IKNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

  		/*Getting Gamma*/
  		linea = lineasFichero.nextToken();
  		tokens = new StringTokenizer (linea, "=");
  		tokens.nextToken();
  		gammaRate = Double.parseDouble(tokens.nextToken().substring(1));

  		/*Getting Gamma*/
  		linea = lineasFichero.nextToken();
  		tokens = new StringTokenizer (linea, "=");
  		tokens.nextToken();
  		xiMultiplicative = Double.parseDouble(tokens.nextToken().substring(1));
  		
  		/*Getting Gamma*/
  		linea = lineasFichero.nextToken();
  		tokens = new StringTokenizer (linea, "=");
  		tokens.nextToken();
  		xiExponential = Double.parseDouble(tokens.nextToken().substring(1));
  	
  	    /*Getting the type of distance function*/
  	    linea = lineasFichero.nextToken();
  	    tokens = new StringTokenizer (linea, "=");
  	    tokens.nextToken();
  	    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  	}
}

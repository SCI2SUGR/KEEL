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
//  FCNN.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 26-9-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.FCNN;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class FCNN extends Metodo {

	/*Own parameters of the algorithm*/
	private int k;

	public FCNN (String ficheroScript) {
		super (ficheroScript);
	}

	public void ejecutar () {

		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];	  
		int S[];
		int i, j, l, m;
		int nClases;
		int pos;
		int tamS;
		int nearest[][];
		Vector <Integer> deltaS = new Vector <Integer> ();
		double centroid[];
		int nCentroid;
		double dist, minDist;
		int rep[];
		boolean insert;
		int votes[];
		int max;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of different classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;

		if (nClases < 2) {
			System.err.println("Input dataset has only one class");
			nClases = 0;
		}
    
		nearest = new int[datosTrain.length][k];
		for (i=0; i<datosTrain.length; i++) {
			Arrays.fill(nearest[i],-1);
		}

		/*Inicialization of the candidates set*/
		S = new int[datosTrain.length];
		for (i=0; i<S.length; i++)
			S[i] = Integer.MAX_VALUE;
		tamS = 0;    
    
		/*Inserting an element of each class*/
		centroid = new double[datosTrain[0].length];
		for (i=0; i<nClases; i++) {
			nCentroid = 0;
			Arrays.fill(centroid, 0);
			for (j=0; j<datosTrain.length; j++) {
				if (clasesTrain[j] == i) {
					for (l=0; l<datosTrain[j].length; l++) {
						centroid[l] += datosTrain[j][l];
					}
					nCentroid++;
				}
			}
			for (j=0; j<centroid.length; j++) {
				centroid[j] /= (double)nCentroid;
			}
			pos = -1;
			minDist = Double.POSITIVE_INFINITY;
			for (j=0; j<datosTrain.length; j++) {
				if (clasesTrain[j] == i) {
					dist = KNN.distancia(centroid, datosTrain[j]);
					if (dist < minDist) {
						minDist = dist;
						pos = j;
					}
				}
			}
			if (pos >= 0)
				deltaS.add(pos);
		}

		/*Algorithm body*/
		rep = new int[datosTrain.length];
		votes = new int[nClases];
		while (deltaS.size() > 0) {
    	
			for (i=0; i<deltaS.size(); i++) {
				S[tamS] = deltaS.elementAt(i);
				tamS++;
			}
			Arrays.sort(S);
			
			Arrays.fill(rep, -1);
    	
			for (i=0; i<datosTrain.length; i++) {
				if (Arrays.binarySearch(S, i) < 0) {
					for (j=0; j<deltaS.size(); j++) {
						insert = false;
						for (l=0; l<nearest[i].length && !insert; l++) {
							if (nearest[i][l] < 0) {
								nearest[i][l] = deltaS.elementAt(j);
								insert = true;
							} else {
								if (KNN.distancia(datosTrain[nearest[i][l]], datosTrain[i]) > KNN.distancia(datosTrain[i], datosTrain[deltaS.elementAt(j)])) {
									for (m = k - 1; m >= l+1; m--) {
										nearest[i][m] = nearest[i][m-1];
									}
									nearest[i][l] = deltaS.elementAt(j);
									insert = true;
								}
							}
						} 
					}
    			
					Arrays.fill(votes, 0);
					for (j=0; j<nearest[i].length; j++) {
						if (nearest[i][j] >= 0) {
							votes[clasesTrain[nearest[i][j]]]++;
						}
					}
					max = votes[0];
					pos = 0;
					for (j=1; j<votes.length; j++) {
						if (votes[j] > max) {
							max = votes[j];
							pos = j;
						}
					}
					if (clasesTrain[i] != pos) {
						for (j=0; j<nearest[i].length; j++) {
							if (nearest[i][j] >= 0) {
								if (rep[nearest[i][j]] < 0) {
									rep[nearest[i][j]] = i;
								} else {
									if (KNN.distancia(datosTrain[nearest[i][j]], datosTrain[i]) < KNN.distancia(datosTrain[nearest[i][j]], datosTrain[rep[nearest[i][j]]])) {
										rep[nearest[i][j]] = i;    								
									}
								}    							
							}
						}
					}
				}
			}
    	
			deltaS.removeAllElements();

			for (i=0; i<tamS; i++) {
				if (rep[S[i]] >= 0 && !deltaS.contains(rep[S[i]]))
					deltaS.add(rep[S[i]]);
			}
		}

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

		System.out.println("FCNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

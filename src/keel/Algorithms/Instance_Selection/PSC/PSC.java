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
//  PSC.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 4-3-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.PSC;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

public class PSC extends Metodo {
	
	/*Parameters of the algorithm*/
	private int C;
	private long seed;

	public PSC (String ficheroScript) {
		super (ficheroScript);
	}

	public void ejecutar () {

		int i, j, k, l;
		int nClases;
		boolean marcas[];
		int nSel = 0;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int clusters[];
		double centers[][];
		double dist, minDist;
		int pos, max;
		int majorityClass[];
		int mClass;
		int Pc;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of differents classes*/
		nClases = 0;
		for (i = 0; i < clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		
		Randomize.setSeed(seed);
		
		marcas = new boolean[datosTrain.length];
		Arrays.fill(marcas, false);
		centers = new double[C][datosTrain[0].length];
		
		clusters = Cmeans(datosTrain, C, centers);
		
		majorityClass = new int[nClases];
		for (i=0; i<C; i++) {
			if (isHomogeneous(i,clusters,clasesTrain)) {				
				minDist = Double.POSITIVE_INFINITY;
				pos = 0;
				for (j=0; j<datosTrain.length; j++) {
					if (clusters[j] == i) {
						dist = KNN.distancia(datosTrain[j], centers[i]);
						if (dist < minDist) {
							minDist = dist;
							pos = j;
						}
					}
				}
				marcas[pos] = true;
				nSel++;
			} else {
				Arrays.fill(majorityClass, 0);
				for (j=0; j<datosTrain.length; j++) {
					if (clusters[j] == i) {
						majorityClass[clasesTrain[j]]++;
					}
				}
				
				max = majorityClass[0];
				pos = 0;
				for (j=1; j<nClases; j++) {
					if (majorityClass[j] > max) {
						max = majorityClass[j];
						pos = j;
					}
				}
				mClass = pos;
				
				for (j=0; j<datosTrain.length; j++) {
					if (clusters[j] == i) {
						if (clasesTrain[j] != mClass) {
							minDist = Double.POSITIVE_INFINITY;
							pos = 0;
							for (k=0; k<datosTrain.length; k++) {
								if (clusters[k] == i) {
									if (clasesTrain[k] == mClass) {
										dist = KNN.distancia(datosTrain[k], datosTrain[j]);
										if (dist < minDist) {
											minDist = dist;
											pos = k;
										}
									}
								}
							}
							if (!marcas[pos]) {
								marcas[pos] = true;
								nSel++;								
							}
							Pc = pos;

							minDist = Double.POSITIVE_INFINITY;
							pos = 0;
							for (k=0; k<datosTrain.length; k++) {
								if (clusters[k] == i) {
									if (clasesTrain[k] == clasesTrain[j]) {
										dist = KNN.distancia(datosTrain[k], datosTrain[Pc]);
										if (dist < minDist) {
											minDist = dist;
											pos = k;
										}
									}
								}
							}
							if (!marcas[pos]) {
								marcas[pos] = true;
								nSel++;								
							}
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

		System.out.println("PSC "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
	
	private int[] Cmeans (double datosTrain[][], int C, double centers[][]) {

		int clusters[];
		int tmp, pos;
		int baraje[];
		int i, j;
		double minDist, dist;
		boolean cambio = true;
		int nc[];
		
		clusters = new int[datosTrain.length];
		baraje = new int[datosTrain.length];
		
		for (i=0; i<datosTrain.length; i++) {
			baraje[i] = i;
		}

		for (i=0; i<datosTrain.length; i++) {
			pos = Randomize.Randint(i, datosTrain.length);
			tmp = baraje[i];
			baraje[i] = baraje[pos];
			baraje[pos] = tmp;			
		}
		
		for (i=0; i<C; i++) {
			for (j=0; j<datosTrain[0].length; j++) {
				centers[i][j] = datosTrain[baraje[i]][j];
			}
		}
		
		for (i=0; i<datosTrain.length; i++) {
			pos = 0;
			minDist = KNN.distancia(datosTrain[i], centers[0]);
			for (j=1; j<C; j++) {
				dist = KNN.distancia(datosTrain[i], centers[j]);
				if (dist < minDist) {
					pos = j;
					minDist = dist;
				}
			}
			clusters[i] = pos;
		}
		
		nc = new int[C];
		while (cambio) {
			cambio = false;
			
			Arrays.fill(nc, 0);
			for (i=0; i<C; i++) {
				Arrays.fill(centers[i], 0.0);
			}
			
			for (i=0; i<datosTrain.length; i++) {
				nc[clusters[i]]++;
				for (j=0; j<datosTrain[0].length; j++) {
					centers[clusters[i]][j] += datosTrain[i][j];
				}
			}
			
			for (i=0; i<C; i++) {
				for (j=0; j<datosTrain[0].length; j++) {
					centers[i][j] /= (double)nc[i];
				}
			}
			
			for (i=0; i<datosTrain.length; i++) {
				pos = 0;
				minDist = KNN.distancia(datosTrain[i], centers[0]);
				for (j=1; j<C; j++) {
					dist = KNN.distancia(datosTrain[i], centers[j]);
					if (dist < minDist) {
						pos = j;
						minDist = dist;
					}
				}
				if (clusters[i] != pos) {
					cambio = true;
					clusters[i] = pos;
				}
			}
			
		}

		return clusters;
	}
	
	boolean isHomogeneous(int pos, int clusters[], int clasesTrain[]) {
		
		int i;
		boolean stop = false;
		int classD = 0;
		
		for (i=0; i<clasesTrain.length && !stop; i++) {
			if (clusters[i] == pos) {
				classD = clasesTrain[i];
				stop = true;
			}
		}
		
		for ( ; i<clasesTrain.length; i++) {
			if (clusters[i] == pos && clasesTrain[i] != classD) {
				return false;
			}
		}
		
		return true;
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

        linea = lineasFichero.nextToken();
        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        seed = Long.parseLong(tokens.nextToken().substring(1));
        
        linea = lineasFichero.nextToken();
        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        C = Integer.parseInt(tokens.nextToken().substring(1));    
    }
}


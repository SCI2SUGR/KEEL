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
//  TRKNN.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-6-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.TRKNN;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class TRKNN extends Metodo {

	/*Own parameters of the algorithm*/
	double alpha;
	
	public TRKNN (String ficheroScript) {
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
		Vector <Vector <Referencia> > chains;
		boolean stop, odd;
		int ref;
		double dist, minDist;
		int pos;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of differents classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		
		/*Inicialization of the flagged instances vector for a posterior copy*/
		marcas = new boolean[datosTrain.length];
		Arrays.fill(marcas, true);
		nSel = datosTrain.length;
		
		chains = new Vector <Vector<Referencia>>();
		for (i=0; i<datosTrain.length; i++) {
			chains.add(new Vector<Referencia>());
		}
		
		/*Body of the algorithm. It finds the associated chain of neighbours to each training sample. Then, it marks 
		 those patterns with same class than the initial of the chain whose distance is farther than the nearest enemy.*/
		for (i=0; i<datosTrain.length; i++) {
			stop = false;
			ref = i;
			odd = false;
			while (!stop) {
				minDist = Double.POSITIVE_INFINITY;
				pos = -1;
				for (j=0; j<datosTrain.length; j++) {
					if (ref != j) {
						if (!odd) {
							if (clasesTrain[i] != clasesTrain[j]) {
								dist = KNN.distancia(datosTrain[ref], realTrain[ref], nominalTrain[ref], nulosTrain[ref], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
								if (dist < minDist) {
									minDist = dist;
									pos = j;
								}
							}
						} else {
							if (clasesTrain[i] == clasesTrain[j]) {
								dist = KNN.distancia(datosTrain[ref], realTrain[ref], nominalTrain[ref], nulosTrain[ref], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
								if (dist < minDist) {
									minDist = dist;
									pos = j;
								}
							}							
						}
					}
				}
				if (chains.elementAt(i).size() < 2) {
					chains.elementAt(i).add(new Referencia(pos,minDist));
					odd = !odd;
					ref = pos;
				} else {
					if (chains.elementAt(i).elementAt(chains.elementAt(i).size()-2).entero == pos) {
						stop = true;
					} else {
						chains.elementAt(i).add(new Referencia(pos,minDist));						
						odd = !odd;
						ref = pos;
					}
				}
			}
		}
		
		for (i=0; i<datosTrain.length; i++) {
			for (j=0; j<chains.elementAt(i).size(); j+=2) {
				if (j < chains.elementAt(i).size()-1) {
					if (j > 0) {
						if (chains.elementAt(i).elementAt(j).real > alpha * chains.elementAt(i).elementAt(j+1).real) {
							if (marcas[chains.elementAt(i).elementAt(j-1).entero]) {
								marcas[chains.elementAt(i).elementAt(j-1).entero] = false;
								nSel--;
							}
						}
					} else {
						if (chains.elementAt(i).elementAt(j).real > alpha * chains.elementAt(i).elementAt(j+1).real) {
							if (marcas[i]) {
								marcas[i] = false;
								nSel--;
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

        System.out.println("TRKNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

  		/*Getting Gamma*/
  		linea = lineasFichero.nextToken();
  		tokens = new StringTokenizer (linea, "=");
  		tokens.nextToken();
  		alpha = Double.parseDouble(tokens.nextToken().substring(1));

  	    /*Getting the type of distance function*/
  	    linea = lineasFichero.nextToken();
  	    tokens = new StringTokenizer (linea, "=");
  	    tokens.nextToken();
  	    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  	}
}

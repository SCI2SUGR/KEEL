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
//  NCNEdit.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 26-4-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.NCNEdit;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class NCNEdit extends Metodo {

 /*Own parameters of the algorithm*/
  private int k;

  public NCNEdit (String ficheroScript) {
    super (ficheroScript);
  }
  
  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel = 0;    
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nvecinos[];
    double centroide[], centroideT[];
    double dist, minDist;
    int pos;
    int votos[], votada, votaciones;

    long tiempo = System.currentTimeMillis();

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = false;

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Body of the algorithm. For each instance in T, search the correspond class agreeing its majority
     from the nearest centroid neighborhood. Is it is positive, the instance is selected.*/
    nvecinos = new int[k];
    centroide = new double[datosTrain[0].length];
    centroideT = new double[datosTrain[0].length];
    votos = new int[nClases];
    
    for (i=0; i<datosTrain.length; i++) {
    	/*Apply K-NCN to the instance*/
    	for (j=0; j<k; j++) {
    		Arrays.fill(centroide, 0.0);
    		for (l=0; l<j; l++) {
    			for (m=0; m<datosTrain[0].length; m++) {
    				if (nvecinos[l] >= 0)
    					centroide[m] += datosTrain[nvecinos[l]][m];
    			}
    		}
    		pos = -1;
    		minDist = Double.POSITIVE_INFINITY;
    		for (l=0; l<datosTrain.length; l++) {
    			if (i!=l) {
    				for (m=0; m<centroide.length; m++) {
    					centroideT[m] = centroide[m] + datosTrain[l][m];
    				}
    				for (m=0; m<centroide.length; m++) {
    					centroideT[m] /= (double)(j+1);
    				}
    				dist = KNN.distancia(datosTrain[i], centroideT);
    				if (dist < minDist) {
    					minDist = dist;
    					pos = l;
    				}
    			}
    		}
    		nvecinos[j] = pos;
    	}
    	
    	/*Obtain the voted class*/
        for (j=0; j<nClases; j++) {
            votos[j] = 0;
        }
        for (j=0; j<k; j++) {
        	if (nvecinos[j] >= 0)
        		votos[clasesTrain[nvecinos[j]]] ++;
        }               
        votada = 0;
        votaciones = votos[0];
        for (j=1; j<nClases; j++) {
        	if (votaciones < votos[j]) {
        		votaciones = votos[j];
                votada = j;
            }
        }
       
        
    	claseObt = votada;
        if (claseObt == clasesTrain[i]) { //agree with your majority, it is included in the solution set
        	marcas[i] = true;
        	nSel++;
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

    System.out.println("NCNEdit "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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


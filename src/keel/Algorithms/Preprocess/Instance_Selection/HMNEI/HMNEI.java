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
//  HMNEI.java
//
//  Salvador García López
//
//  Created by Salvador García López 7-7-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.HMNEI;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class HMNEI extends Metodo {

 /*Own parameters of the algorithm*/
  private double epsilon;

  public HMNEI (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, k, l, m;
    int nClases;
    int claseObt;
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    double conjS2[][];
    double conjR2[][];
    int conjN2[][];
    boolean conjM2[][];
    int clasesS2[];
    double dist, minDist;
    double acierto, aciertoAct = 0.0;
    int hit[], miss[];
    int pos, cont;
    double w[];
    int cc[];
    int seleccionadosAnt;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    
	/*Building of the S set from the flags*/
	conjS2 = new double[datosTrain.length][datosTrain[0].length];
	conjR2 = new double[datosTrain.length][datosTrain[0].length];
	conjN2 = new int[datosTrain.length][datosTrain[0].length];
	conjM2 = new boolean[datosTrain.length][datosTrain[0].length];
	clasesS2 = new int[datosTrain.length];
	for (m=0, l=0; m<datosTrain.length; m++) {
		for (j=0; j<datosTrain[0].length; j++) {
			conjS2[l][j] = datosTrain[m][j];
			conjR2[l][j] = realTrain[m][j];
			conjN2[l][j] = nominalTrain[m][j];
			conjM2[l][j] = nulosTrain[m][j];
		}
		clasesS2[l] = clasesTrain[m];
		l++;
	}    

    nSel = datosTrain.length;

    do {
    	acierto = aciertoAct;
    	seleccionadosAnt = nSel;
    	
    	/*Building of the S set from the flags*/
    	conjS = new double[nSel][datosTrain[0].length];
    	conjR = new double[nSel][datosTrain[0].length];
    	conjN = new int[nSel][datosTrain[0].length];
    	conjM = new boolean[nSel][datosTrain[0].length];
    	clasesS = new int[nSel];
    	for (m=0, l=0; m<nSel; m++) {
    		for (j=0; j<datosTrain[0].length; j++) {
    			conjS[l][j] = conjS2[m][j];
    			conjR[l][j] = conjR2[m][j];
    			conjN[l][j] = conjN2[m][j];
    			conjM[l][j] = conjM2[m][j];
    		}
    		clasesS[l] = clasesS2[m];
    		l++;
    	}

        /*Inicialization of the flagged instances vector from the S*/
        marcas = new boolean[nSel];
        for (i=0; i<nSel; i++) {
        	marcas[i] = true;
        }
    	
    	hit = new int[nSel];
    	miss = new int[nSel];
    	for (i=0; i<conjS.length; i++) {
    		for (j=0; j<nClases; j++) {
    			minDist = Double.POSITIVE_INFINITY;
    			pos = -1;
    			for (k=0; k<conjS.length; k++) {
    				if (i!=k && clasesS[k] == j) {
    					dist = KNN.distancia(conjS[i], conjR[i], conjN[i], conjM[i], conjS[k], conjR[k], conjN[k], conjM[k], distanceEu);
    					if (dist < minDist) {
    						minDist = dist;    						
    						pos = k;
    					}
    				}
    			}
    			if (pos >= 0) {
    				if (clasesS[i] == j) {
    					hit[pos]++;
    				} else {
    					miss[pos]++;
    				}
    			}
    		}
    	}
    	
    	w = new double[nClases];
    	cc = new int[nClases];
    	for (i=0; i<w.length; i++) {
    		cont = 0;
    		for (j=0; j<clasesS.length; j++) {
    			if (clasesS[j] == i) {
    				cont++;
    			}
    		}
    		cc[i] = cont;
    		w[i] = (double)cont / (double)nSel;
    	}
    	
    	/*RULE R1*/
    	for (i=0; i<hit.length; i++) {
    		if ((w[clasesS[i]] * (double)miss[i] + epsilon) > ((1-w[clasesS[i]]) * (double)hit[i])) {
    			marcas[i] = false;
    			nSel--;
    		}
    	}
    	
    	/*RULE R2*/
    	for (i=0; i<nClases; i++) {
    		cont = 0;
    		for (j=0; j<hit.length && cont < 4; j++) {
    			if (clasesS[j] == i && marcas[j]) {
    				cont++;
    			}
    		}
    		if (cont < 4) {
    			for (j=0; j<hit.length; j++) {
    				if (clasesS[j] == i && !marcas[j] && (hit[j]+miss[j]) > 0) {
    					marcas[j] = true;
    					nSel++;
    				}
    			}
    		}
    	}

    	/*RULE R3*/
    	if (nClases > 3) {
    		for (i=0; i<hit.length; i++) {
    			if (!marcas[i] && (miss[i]+hit[i] > 0) && miss[i] < (nClases/2)) {
    				marcas[i] = true;
    				nSel++;
    			}
    		}
    	}
    	
   	
    	/*RULE R4*/
    	for (i=0; i<hit.length; i++) {
    		if (!marcas[i] && hit[i] >= (cc[clasesS[i]] / 4)) {
    			marcas[i] = true;
    			nSel++;
    		}
    	}

    	/*Building of the S set from the flags*/
    	conjS2 = new double[nSel][datosTrain[0].length];
    	conjR2 = new double[nSel][datosTrain[0].length];
    	conjN2 = new int[nSel][datosTrain[0].length];
    	conjM2 = new boolean[nSel][datosTrain[0].length];
    	clasesS2 = new int[nSel];
    	for (m=0, l=0; m<conjS.length; m++) {
    		if (marcas[m]) { //the instance will be evaluated
    			for (j=0; j<datosTrain[0].length; j++) {
    				conjS2[l][j] = conjS[m][j];
    				conjR2[l][j] = conjR[m][j];
    				conjN2[l][j] = conjN[m][j];
    				conjM2[l][j] = conjM[m][j];
    			}
    			clasesS2[l] = clasesS[m];
    			l++;
    		}
    	}
    	
    	aciertoAct = 0;
    	for (i=0; i<datosTrain.length; i++) {
    		claseObt = KNN.evaluacionKNN2(1, conjS2, conjR2, conjN2, conjM2, clasesS2, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
    		if (claseObt == clasesTrain[i]) {
    			aciertoAct++;
    		}
    	}
    } while (aciertoAct >= acierto && nSel < seleccionadosAnt);

    System.out.println("HMNEI "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting epsilon value*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    epsilon = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }

}


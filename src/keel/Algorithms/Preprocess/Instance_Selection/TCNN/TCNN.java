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
//  TCNN.java
//
//  Salvador García López
//
//  Created by Salvador García López 23-2-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.TCNN;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;

public class TCNN extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private int k;

  public TCNN (String ficheroScript) {
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
    int baraje[];
    int tmp;
    int tamS;
    int claseObt;
    int cont;
    int busq;
    boolean continuar;
    int classAct;
    boolean setC[];
    double exTmp[];
    double exReal[];
    int exNom[];
    boolean exNul[];    
    double distX;
    boolean parar;
    int nSel = 0;
    double datosC[][];
    double realC[][];
    int nominalC[][];
    boolean nulosC[][];
    int clasesC[];

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    tamS = 0;

    if (nClases < 2) {
      System.err.println("Input dataset is empty");
      nClases = 0;
    }
    
    /*Modification of Tomek*/
    setC = new boolean[datosTrain.length];
    Arrays.fill(setC, false);
    exTmp = new double[datosTrain[0].length];
    exReal = new double[datosTrain[0].length];
    exNom = new int[datosTrain[0].length];
    exNul = new boolean[datosTrain[0].length];
    for (i=0; i<datosTrain.length; i++) {
    	classAct = clasesTrain[i];
    	for (j=i+1; j<datosTrain.length; j++) {
    		if (classAct != clasesTrain[j]) {
    			for (l=0; l<exTmp.length; l++) {
    				exTmp[l] = 0.5*(datosTrain[i][l]+datosTrain[j][l]);
    				exReal[l] = 0.5*(realTrain[i][l]+realTrain[j][l]);
    				exNom[l] = nominalTrain[i][l];
    				exNul[l] = nulosTrain[i][l] | nulosTrain[j][l];
    			}
    			distX = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], exTmp, exReal, exNom, exNul, distanceEu);
    			parar = false;
    			for (l=0; l<datosTrain.length && !parar; l++) {
    				if (l != i && l != j) {
    					if (clasesTrain[l] == classAct) {
    						if (KNN.distancia(datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], exTmp, exReal, exNom, exNul, distanceEu) <= distX) {
    							parar = true;
    						}    							
    					} else {
    						if (KNN.distancia(datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], exTmp, exReal, exNom, exNul, distanceEu) <= distX) {
    							parar = true;
    						}    						
    					}
    				}
    			}
    			if (!parar) {
    				if (!setC[i]) {
    					setC[i] = true;
    					nSel++;
    				}
    				if (!setC[j]) {
        				setC[j] = true;
        				nSel++;    					
    				}
    			}
    		}
    	}
    }
    
    /*Build the C set*/
    datosC = new double[nSel][datosTrain[0].length];
    realC = new double[nSel][datosTrain[0].length];
    nominalC = new int[nSel][datosTrain[0].length];
    nulosC = new boolean[nSel][datosTrain[0].length];
    clasesC = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (setC[m]) {
        for (j=0; j<datosTrain[0].length; j++) {
          datosC[l][j] = datosTrain[m][j];
          realC[l][j] = realTrain[m][j];
          nominalC[l][j] = nominalTrain[m][j];
          nulosC[l][j] = nulosTrain[m][j];
        }
        clasesC[l] = clasesTrain[m];
        l++;
      }
    }
    

    /*Inicialization of the candidates set*/
    if (datosC.length == 0) { // C is empty
    	datosC = new double[datosTrain.length][datosTrain[0].length];
    	realC = new double[datosTrain.length][datosTrain[0].length];
    	nominalC = new int[datosTrain.length][datosTrain[0].length];
    	nulosC = new boolean[datosTrain.length][datosTrain[0].length];
    	clasesC = new int[datosTrain.length];
        for (m=0, l=0; m<datosTrain.length; m++) {
        	for (j=0; j<datosTrain[0].length; j++) {
        		datosC[l][j] = datosTrain[m][j];
                realC[l][j] = realTrain[m][j];
                nominalC[l][j] = nominalTrain[m][j];
                nulosC[l][j] = nulosTrain[m][j];
        	}
        	clasesC[l] = clasesTrain[m];
        	l++;
        }
    } 
    S = new int[datosC.length];
    for (i=0; i<S.length; i++)
    	S[i] = Integer.MAX_VALUE;

    /*Inserting an element of each class*/
    Randomize.setSeed (semilla);
    for (i=0; i<nClases; i++) {
    	pos = Randomize.Randint (0, clasesC.length-1);
    	cont = 0;
    	while (cont < clasesC.length && clasesC[pos] != i) {
    		pos = (pos + 1) % clasesC.length;
    		cont++;
    	}
    	if (cont < clasesC.length) {
    		S[tamS] = pos;
    		tamS++;
    	}
    }

    /*Algorithm body. We resort randomly the instances of T and compare with the rest of S.
     If an instance doesn´t classified correctly, it is inserted in S*/
    do {
      continuar = false;
      baraje = new int[datosC.length];
      for (i=0; i<datosC.length; i++)
        baraje[i] = i;
      for (i=0; i<datosC.length; i++) {
        pos = Randomize.Randint (i, clasesC.length-1);
        tmp = baraje[i];
        baraje[i] = baraje[pos];
        baraje[pos] = tmp;
      }

      for (i=0; i<datosC.length; i++) {
        /*Construction of the S set from the previous vector S*/
        conjS = new double[tamS][datosC[0].length];
        conjR = new double[tamS][datosC[0].length];
        conjN = new int[tamS][datosC[0].length];
        conjM = new boolean[tamS][datosC[0].length];
        clasesS = new int[tamS];
        for (j = 0; j < tamS; j++) {
          for (l = 0; l < datosC[0].length; l++) {
            conjS[j][l] = datosC[S[j]][l];
            conjR[j][l] = realC[S[j]][l];
            conjN[j][l] = nominalC[S[j]][l];
            conjM[j][l] = nulosC[S[j]][l];
          }
          clasesS[j] = clasesC[S[j]];
        }
        Arrays.sort(S);
        busq = Arrays.binarySearch(S, baraje[i]);
        if (busq < 0) {
          /*Do KNN to the instance*/
          claseObt = KNN.evaluacionKNN(k, conjS, conjR, conjN, conjM, clasesS, datosC[baraje[i]], realC[baraje[i]], nominalC[baraje[i]], nulosC[baraje[i]], nClases, distanceEu);
          if (claseObt != clasesC[baraje[i]]) { //fail in the class, it is included in S
            continuar = true;
            S[tamS] = baraje[i];
            tamS++;
          }
        }
      }
    } while (continuar == true);

    /*Construction of the S set from the previous vector S*/
    conjS = new double[tamS][datosC[0].length];
    conjR = new double[tamS][datosC[0].length];
    conjN = new int[tamS][datosC[0].length];
    conjM = new boolean[tamS][datosC[0].length];
    clasesS = new int[tamS];
    for (j=0; j<tamS; j++) {
      for (l=0; l<datosC[0].length; l++) {
        conjS[j][l] = datosC[S[j]][l];
        conjR[j][l] = realC[S[j]][l];
        conjN[j][l] = nominalC[S[j]][l];
        conjM[j][l] = nulosC[S[j]][l];
      }
      clasesS[j] = clasesC[S[j]];
    }

    System.out.println("TCNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
  
     /*Getting the type of distance function*/
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}


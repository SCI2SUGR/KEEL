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
//  POP.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 28-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.POP;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class POP extends Metodo {

  public POP (String ficheroScript) {
    super (ficheroScript);
  }
  
  public void ejecutar () {

    int i, j, l;
    int nClases;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel;
    boolean marcas[];
    int weakness[];
    Referencia linea[];
    int minWeak;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of structures*/
    marcas = new boolean[datosTrain.length];
    weakness = new int[datosTrain.length];
    linea = new Referencia[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
      weakness[i] = 0;
    }
    nSel = datosTrain.length;

    /*Body of the POP algorithm. For each attribute, do a resort and mark the instances that are into the intervals that
     create the proyected classes in this dimension. Finally, the marked instances are eliminated.*/
    for (i=0; i<datosTrain[0].length; i++) {
    	/*Proyection to a i dimension*/
    	if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
    		for (j=0; j<datosTrain.length; j++) {
    			linea[j] = new Referencia (j,realTrain[j][i]);
    		}

    		/*Quicksort*/
    		Arrays.sort(linea);

    		/*Increment the weakness of interior instances*/
    		for (j=1; j<datosTrain.length-1; j++) {
    			if (clasesTrain[linea[j-1].entero] == clasesTrain[linea[j].entero] && clasesTrain[linea[j+1].entero] == clasesTrain[linea[j].entero])
    				weakness[linea[j].entero] ++;
    		}
    	}
    }

    for (i=0; i<datosTrain[0].length; i++) {
    	/*Proyection to a i dimension*/
    	if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
    		for (j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
    			minWeak = Integer.MAX_VALUE;
    			for (l=0; l<datosTrain.length;l++) {
    				if (nominalTrain[l][i] == j) {
    					if (weakness[l] < minWeak) {
    						minWeak = weakness[l];
    					}
    				}
    			}
    			for (l=0; l<datosTrain.length;l++) {
    				if (nominalTrain[l][i] == j) {
    					if (weakness[l] > minWeak) {
    						weakness[l]++;
    					}
    				}
    			}
    		}
    	}
    }
    
    for (i=0; i<datosTrain.length; i++)
    	if (weakness[i] == datosTrain[0].length) {
    		marcas[i] = false;
    		nSel--;
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

    System.out.println("POP "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
  }
}

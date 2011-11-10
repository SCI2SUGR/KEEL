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

/**
 * 
 * File: CNN.java
 * 
 * The CNN Instance Selection algorithm.
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Preprocess.Instance_Selection.CNN;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;

public class CNN extends Metodo {

	/*Own parameters of the algorithm*/
	private long semilla;
	private int k;

	/**
     * Default builder. Construct the algoritm by using the superclass builder.
	 *
     */
	public CNN (String ficheroScript) {
		super (ficheroScript);
		
	}//end-method 

	/**
	 * Executes the algorithm
	 */
	public void ejecutar () {

		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		  
		int S[];
		int i, j, l;
		int nClases;
		int pos;
		int baraje[];
		int tmp;
		int tamS;
		int claseObt;
		int cont;
		int busq;
		boolean continuar;

		long tiempo = System.currentTimeMillis();

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
		  System.err.println("Input dataset is empty");
		  nClases = 0;
		}

		/*Inserting an element of each class*/
		Randomize.setSeed (semilla);
		for (i=0; i<nClases; i++) {
		  pos = Randomize.Randint (0, clasesTrain.length-1);
		  cont = 0;
		  while (clasesTrain[pos] != i && cont < clasesTrain.length) {
			pos = (pos + 1) % clasesTrain.length;
			cont++;
		  }
		  if (cont < clasesTrain.length) {
			S[tamS] = pos;
			tamS++;
		  }
		}

		/*Algorithm body. We resort randomly the instances of T and compare with the rest of S.
		 If an instance doesn´t classified correctly, it is inserted in S*/
		do {
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
			if (busq < 0) {
			  /*Do KNN to the instance*/
			  claseObt = KNN.evaluacionKNN(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[baraje[i]], realTrain[baraje[i]], nominalTrain[baraje[i]], nulosTrain[baraje[i]], nClases, distanceEu);
			  if (claseObt != clasesTrain[baraje[i]]) { //fail in the class, it is included in S
				continuar = true;
				S[tamS] = baraje[i];
				tamS++;
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

		System.out.println("CNN "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

		OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
		OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
		
	}//end-method 
	
	/** 
	 * Reads configuration script, and extracts its contents.
	 * 
	 * @param ficheroScript Name of the configuration script  
	 * 
	 */	
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
		 
	}//end-method 
	
}//end-class


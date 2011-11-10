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
 * File: GGA.java
 * 
 * Population Based Incremental Learning for Instance Selection.
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Instance_Selection.PBIL;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class PBIL extends Metodo {

    /*Own parameters of the algorithm*/
    private long semilla;
    private int generations;
    private int tamPoblacion;
    private double LR;
    private double mutShift;
    private double pMutation;
    private double negativeLR;
    private double alfa;
    private double initialValuePV;
    private int kNeigh;
  
	/**
     * Default builder. Construct the algoritm by using the superclass builder.
	 *
     */
	public PBIL (String ficheroScript) {
		super (ficheroScript);
		
	}//end-method 

	/**
	 * Executes the algorithm
	 */
	public void ejecutar () {

		int i, j, l;
		int nClases;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int nSel = 0;
		Cromosoma poblacion[];
		double PV[];
		Cromosoma best;
		double mejCal, badCal;
		int mejor, peor;
		double u;
		int ev = 0;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of different classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
		  if (clasesTrain[i] > nClases)
			nClases = clasesTrain[i];
		nClases++;

		/*Random inicialization of the poblation*/
		Randomize.setSeed (semilla);
		poblacion = new Cromosoma[tamPoblacion];
		PV = new double[datosTrain.length];
		Arrays.fill(PV,initialValuePV);
		for (i=0; i<tamPoblacion; i++)
		  poblacion[i] = new Cromosoma (datosTrain.length);
		best = new Cromosoma (datosTrain.length,poblacion[0]);

		/*Initial evaluation of the poblation*/
		for (i=0; i<tamPoblacion; i++) {
		  poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
		  ev++;
		}
		mejCal = 0; mejor = 0;
		badCal = 100; peor = 0;

		/*Search for the best solution*/
		for (i=0; i<tamPoblacion; i++)
		  if (poblacion[i].getCalidad() > mejCal) {
			mejor = i;
			mejCal = poblacion[i].getCalidad();
			if (mejCal > best.getCalidad() ){
				best = new Cromosoma (datosTrain.length, poblacion[mejor]);
			}
		  } else if (poblacion[i].getCalidad() < badCal) {
			peor = i;
			badCal = poblacion[i].getCalidad();
		  }

		/*Update of the probabilities vector*/
		for (i=0; i<datosTrain.length; i++) {
		  if (poblacion[mejor].getGen(i) != poblacion[peor].getGen(i)) {
			if (poblacion[mejor].getGen(i))
			  PV[i] = PV[i]*(1-negativeLR)+ negativeLR;
			else
			  PV[i] = PV[i]*(1-negativeLR);
		  } else {
			if (poblacion[mejor].getGen(i))
			  PV[i] = PV[i]*(1-LR)+LR;
			else
			  PV[i] = PV[i]*(1-LR);
		  }

		  /*Mutation of the probabilities vector*/
		  u = Randomize.Rand();
		  if (u < pMutation) {
			PV[i]= PV[i]* (1-mutShift) + (Randomize.Randint(0,1))*mutShift;
		  }
		}

		while (ev < generations) {
		  /*Getting a new poblation*/
		  for (i=0; i<tamPoblacion; i++)
			poblacion[i] = new Cromosoma (datosTrain.length, PV);

		  /*Evaluation of the poblation*/
		  for (i=0; i<tamPoblacion; i++) {
			poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
			ev++;
		  }
		  mejCal = 0;
		  badCal = 100;

		  /*Search the best solution*/
		  for (i=0; i<tamPoblacion; i++)
			if (poblacion[i].getCalidad() > mejCal) {
			  mejor = i;
			  mejCal = poblacion[i].getCalidad();
			  if (mejCal > best.getCalidad() ){
				  best = new Cromosoma (datosTrain.length, poblacion[mejor]);
			  }
			} else if (poblacion[i].getCalidad() < badCal) {
			  peor = i;
			  badCal = poblacion[i].getCalidad();
			}

		  /*Update of the probabilities vector*/
		  for (i=0; i<datosTrain.length; i++) {
			if (poblacion[mejor].getGen(i) != poblacion[peor].getGen(i)) {
			  if (poblacion[mejor].getGen(i))
				PV[i] = PV[i]*(1-negativeLR)+ negativeLR;
			  else
				PV[i] = PV[i]*(1-negativeLR);
			} else {
			  if (poblacion[mejor].getGen(i))
				PV[i] = PV[i]*(1-LR)+LR;
			  else
				PV[i] = PV[i]*(1-LR);
			}

			/*Mutation of the probabilities vector*/
			u = Randomize.Rand();
			if (u < pMutation) {
			  PV[i]= PV[i]* (1-mutShift) + (Randomize.Randint(0,1))*mutShift;
			}
		  }
		}

		nSel = best.genesActivos();

		/*Construction of S set from the best cromosome*/
		conjS = new double[nSel][datosTrain[0].length];
		conjR = new double[nSel][datosTrain[0].length];
		conjN = new int[nSel][datosTrain[0].length];
		conjM = new boolean[nSel][datosTrain[0].length];
		clasesS = new int[nSel];
		for (i=0, l=0; i<datosTrain.length; i++) {
		  if (best.getGen(i)) { //the instance must be copied to the solution
			for (j=0; j<datosTrain[i].length; j++) {
			  conjS[l][j] = datosTrain[i][j];
			  conjR[l][j] = realTrain[i][j];
			  conjN[l][j] = nominalTrain[i][j];
			  conjM[l][j] = nulosTrain[i][j];
			}
			clasesS[l] = clasesTrain[i];
			l++;
		  }
		}

		System.out.println("PBIL "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

		/*Getting the name of training and test files*/
		line = token.getBytes();
		for (i=0; line[i]!='\"'; i++);
		i++;
		for (j=i; line[j]!='\"'; j++);
		ficheroTraining = new String (line,i,j-i);
		for (i=j+1; line[i]!='\"'; i++);
		i++;
		for (j=i; line[j]!='\"'; j++);
		ficheroTest = new String (line,i,j-i);

		/*Obtaining the path and the base name of the results files*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();

		/*Getting the name of output files*/
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

		/*Getting the number of generations*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		generations = Integer.parseInt(tokens.nextToken().substring(1));

		/*Getting the size of the poblation*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));

		/*Getting the LR tase*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		LR = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the mutation shift*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		mutShift = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the probability of mutation*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		pMutation = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the Negative LR tase*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		negativeLR = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the alpha equilibrate factor*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		alfa = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the alpha equilibrate factor*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		initialValuePV = Double.parseDouble(tokens.nextToken().substring(1));

		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		kNeigh = Integer.parseInt(tokens.nextToken().substring(1));

		/*Getting the type of distance function*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;  
		
	}//end-method 
	
}//end-class


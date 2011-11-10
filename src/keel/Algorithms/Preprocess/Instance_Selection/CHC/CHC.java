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
 * File: CHC.java
 * 
 * The CHC evolutionary model for Instance Selection.
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Preprocess.Instance_Selection.CHC;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class CHC extends Metodo {

	/*Own parameters of the algorithm*/
	private long semilla;
	private int tamPoblacion;
	private int nEval;
	private double alfa;
	private double r;
	private double prob0to1Rec;
	private double prob0to1Div;  
	private int kNeigh;

   /**
    * Default builder. Construct the algoritm by using the superclass builder.
	*
    */
	public CHC (String ficheroScript) {
		super (ficheroScript);
		
	}//end-method 

	/**
	 * Executes the algorithm
	 */
	public void ejecutar () {
	  
		int i, j, k, l;
		int nClases;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int nSel = 0;
		Cromosoma poblacion[];
		int ev = 0;
		Cromosoma C[];
		int baraje[];
		int pos, tmp;
		Cromosoma newPob[];
		int d = datosTrain.length / 4;
		int tamC;
		Cromosoma pobTemp[];
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
		baraje = new int[tamPoblacion];
		for (i=0; i<tamPoblacion; i++)
		  poblacion[i] = new Cromosoma (datosTrain.length);

		/*Initial evaluation of the poblation*/
		for (i=0; i<tamPoblacion; i++)
		  poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);

		/*Until stop condition*/
		while (ev < nEval) {
			  C = new Cromosoma[tamPoblacion];

			  /*Selection(r) of C(t) from P(t)*/
			  for (i=0; i<tamPoblacion; i++)
				baraje[i] = i;
			  for (i=0; i<tamPoblacion; i++) {
				pos = Randomize.Randint (i, tamPoblacion-1);
				tmp = baraje[i];
				baraje[i] = baraje[pos];
				baraje[pos] = tmp;
			  }
			  for (i=0; i<tamPoblacion; i++)
				C[i] = new Cromosoma (datosTrain.length, poblacion[baraje[i]]);

			  /*Structure recombination in C(t) constructing C'(t)*/
			  tamC = recombinar (C, d);
			  newPob = new Cromosoma[tamC];
			  for (i=0, l=0; i<C.length; i++) {
				if (C[i].esValido()) { //the cromosome must be copied to the new poblation C'(t)
				  newPob[l] = new Cromosoma (datosTrain.length, C[i]);
				  l++;
				}
			  }

			  /*Structure evaluation in C'(t)*/
			  for (i=0; i<newPob.length; i++) {
				newPob[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
				ev++;        
			  }

			  /*Selection(s) of P(t) from C'(t) and P(t-1)*/
			  Arrays.sort(poblacion);
			  Arrays.sort(newPob);
			  /*If the better of C' is worse than the worst of P(t-1), then there will no changes*/
			  if (tamC==0 || newPob[0].getCalidad() < poblacion[tamPoblacion-1].getCalidad()) {
				d--;
			  } else {
				pobTemp = new Cromosoma[tamPoblacion];
				for (i=0, j=0, k=0; i<tamPoblacion && k<tamC; i++) {
				  if (poblacion[j].getCalidad() > newPob[k].getCalidad()) {
					pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
					j++;
				  } else {
					pobTemp[i] = new Cromosoma (datosTrain.length, newPob[k]);
					k++;
				  }
				}
				if (k == tamC) { //there are cromosomes for copying
				  for (; i<tamPoblacion; i++) {
					pobTemp[i] = new Cromosoma (datosTrain.length, poblacion[j]);
					j++;
				  }
				}
				poblacion = pobTemp;
			  }

			  /*Last step of the algorithm*/
			  if (d < 0) {
				for (i=1; i<tamPoblacion; i++) {
				  poblacion[i].divergeCHC (r, poblacion[0], prob0to1Div);
				}
				for (i=0; i<tamPoblacion; i++)
				  if (!(poblacion[i].estaEvaluado())) {
					poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alfa, kNeigh, nClases, distanceEu);
					ev++;
				  }

				/*Reinicialization of d value*/
				d = (int)(r*(1.0-r)*(double)datosTrain.length);
			  }
		}

		Arrays.sort(poblacion);
		nSel = poblacion[0].genesActivos();

		/*Construction of S set from the best cromosome*/
		conjS = new double[nSel][datosTrain[0].length];
		conjR = new double[nSel][datosTrain[0].length];
		conjN = new int[nSel][datosTrain[0].length];
		conjM = new boolean[nSel][datosTrain[0].length];
		clasesS = new int[nSel];
		for (i=0, l=0; i<datosTrain.length; i++) {
		  if (poblacion[0].getGen(i)) { //the instance must be copied to the solution
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

		System.out.println("CHC "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

		OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
		OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
		
	}//end-method 

	/**
	 * Function that determines the cromosomes who have to be crossed and the other ones who have to be removed
	 * It returns the number of remaining cromosomes in the poblation
	 *
	 * @param C Array of chromosomes to recombine
	 * @param d Minimun distance to recombine
	 *
	 * @return Number of chromosomes combinated
	 */
	private int recombinar (Cromosoma C[], int d) {

		int i, j;
		int distHamming;
		int tamC = 0;

		for (i=0; i<C.length/2; i++) {
			  distHamming = 0;
			  for (j=0; j<datosTrain.length; j++)
				if (C[i*2].getGen(j) != C[i*2+1].getGen(j))
				  distHamming++;
			  if ((distHamming/2) > d) {
				for (j=0; j<datosTrain.length; j++) {
				  if ((C[i*2].getGen(j) != C[i*2+1].getGen(j)) && Randomize.Rand() < 0.5) {
					if (C[i*2].getGen(j)) C[i*2].setGen(j,false);
					else if (Randomize.Rand() < prob0to1Rec) C[i*2].setGen(j,true);
					if (C[i*2+1].getGen(j)) C[i*2+1].setGen(j,false);
					else if (Randomize.Rand() < prob0to1Rec) C[i*2+1].setGen(j,true);
				  }
				}
				tamC += 2;
			  } else {
				C[i*2].borrar();
				C[i*2+1].borrar();
			  }
		}

		return tamC;
		
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

		/*Obtainin the path and the base name of the results files*/
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

		/*Getting the size of the poblation and the number of evaluations*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		nEval = Integer.parseInt(tokens.nextToken().substring(1));

		/*Getting the equilibrate alfa factor and r value*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		alfa = Double.parseDouble(tokens.nextToken().substring(1));
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		r = Double.parseDouble(tokens.nextToken().substring(1));

		/*Getting the probability of change bits*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		prob0to1Rec = Double.parseDouble(tokens.nextToken().substring(1));
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		prob0to1Div = Double.parseDouble(tokens.nextToken().substring(1));
	  
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

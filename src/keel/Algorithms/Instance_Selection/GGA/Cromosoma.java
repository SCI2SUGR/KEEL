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
 * File: Cromosoma.java
 * 
 * Auxiliriary class to represent chromosomes for Instance selection methods
 * 
 * @author Written by Salvador Garcï¿½a (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Instance_Selection.GGA;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

public class Cromosoma implements Comparable {

	/*Cromosome data structure*/
	boolean cuerpo[];

	/*Useless data for cromosomes*/
	double calidad;
	boolean cruzado;
	boolean valido;
	double errorRate;

	/**
	 * Builder. Construct a random chromosome of specified size
	 *
	 * @param size Size of the chromosome
	 */
	public Cromosoma (int size) {

		double u;
		int i;

		cuerpo = new boolean[size];
		for (i=0; i<size; i++) {
		  u = Randomize.Rand();
		  if (u < 0.5) {
			cuerpo[i] = false;
		  } else {
			cuerpo[i] = true;
		  }
		}
		cruzado = true;
		valido = true;
		
	}//end-method

	/**
	 * Builder. Copies a chromosome of specified size
	 *
	 * @param size Size of the chromosome
	 * @param a Chromosome to copy
	 */
	public Cromosoma (int size, Cromosoma a) {
		
		int i;

		cuerpo = new boolean[size];
		for (i=0; i<cuerpo.length; i++)
		  cuerpo[i] = a.getGen(i);
		calidad = a.getCalidad();
		cruzado = false;
		valido = true;
		
	}//end-method

	/**
	 * Builder. Construct a chromosome from a binary array
	 *
	 * @param datos Initial data of the chromosome
	 */
	public Cromosoma (boolean datos[]) {
	
		int i;

		cuerpo = new boolean[datos.length];
		for (i=0; i<datos.length; i++)
		  cuerpo[i] = datos[i];
		cruzado = true;
		valido = true;
		
	}//end-method

	/**
	 * Get the value of a gene
	 *
	 * @param indice Index of the gene
	 *
	 * @return Value of the especified gene
	 */
	public boolean getGen (int indice) {
		return cuerpo[indice];
		
	}//end-method

	/**
	 * Get the quality of a chromosome
	 *
	 * @return Quality of the chromosome
	 */
	public double getCalidad () {
		return calidad;
		
	}//end-method
	
	/**
	 * Set the value of a gene
	 *
	 * @param indice Index of the gene
	 * @param valor Value to set
	 */
	public void setGen (int indice, boolean valor) {
		cuerpo[indice] = valor;
		
	}//end-method

	/**
	 * Evaluates a chromosome
	 *
	 * @param datos Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	 	 
	 * @param clases Output attribute of each instance
	 * @param alfa Alpha value of the fitness function
	 * @param kNeigh Number of neighbors for the KNN algorithm
	 * @param nClases Number of classes of the problem
	 * @param distanceEu True= Euclidean distance; False= HVDM
	 */
	public void evalua (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double alfa, int kNeigh, int nClases, boolean distanceEu) {

		int i, j, l, m;
		int aciertos = 0;
		double M, s;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int vecinos[];
		int claseObt;
		int vecinoCercano;
		double dist, minDist;
		
		M = (double)datos.length;
		s = (double)genesActivos();
    
		if (kNeigh > 1) {    
			vecinos = new int[kNeigh];    
			conjS = new double[(int)s][datos[0].length];
			conjR = new double[(int)s][datos[0].length];
			conjN = new int[(int)s][datos[0].length];
			conjM = new boolean[(int)s][datos[0].length];
			clasesS = new int[(int)s];
			for (j=0, l=0; j<datos.length; j++) {
				if (cuerpo[j]) { //the instance must be copied to the solution
					for (m=0; m<datos[j].length; m++) {
						conjS[l][m] = datos[j][m];
						conjR[l][m] = real[j][m];
						conjN[l][m] = nominal[j][m];
						conjM[l][m] = nulos[j][m];
					}
					clasesS[l] = clases[j];
					l++;
				}
			}    	
		
			for (i=0; i<datos.length; i++) {
				claseObt = KNN.evaluacionKNN2(kNeigh, conjS, conjR, conjN, conjM, clasesS, datos[i], real[i], nominal[i], nulos[i], nClases, distanceEu, vecinos);
				if (claseObt >= 0)
					if (clases[i] == claseObt)
						aciertos++;
			}    
		} else {
			for (i=0; i<datos.length; i++) {
				vecinoCercano = -1;
				minDist = Double.POSITIVE_INFINITY;
				for (j=0; j<datos.length; j++) {
				  if (cuerpo[j]) { //It is in S
					dist = KNN.distancia (datos[i], real[i], nominal[i], nulos[i], datos[j], real[j], nominal[j], nulos[j], distanceEu);
					if (dist < minDist && dist != 0) {
					  minDist = dist;
					  vecinoCercano = j;
					}
				  }
				}
				if (vecinoCercano >= 0)
				  if (clases[i] == clases[vecinoCercano])
					aciertos++;
			  }    	
		}

		calidad = ((double)(aciertos)/M)*alfa*100.0;
		calidad += ((1.0 - alfa) * 100.0 * (M - s) / M);
		cruzado = false;
		
	}//end-method

	/**
	 * Mutation operator
	 *
	 * @param pMutacion1to0 Probability of change 1 to 0
	 * @param pMutacion0to1 Probability of change 0 to 1	  
	 */
	public void mutacion (double pMutacion1to0, double pMutacion0to1) {

		int i;

		for (i=0; i<cuerpo.length; i++) {
		  if (cuerpo[i]) {
			if (Randomize.Rand() < pMutacion1to0) {
			  cuerpo[i] = false;
			  cruzado = true;
			}
		  } else {
			if (Randomize.Rand() < pMutacion0to1) {
			  cuerpo[i] = true;
			  cruzado = true;
			}
		  }
		}
		
	}//end-method

	/**
	 * Reinitializes the chromosome by using CHC diverge procedure
	 *
	 * @param r R factor of diverge
	 * @param mejor Best chromosome found so far
	 * @param prob Probability of setting a gen to 1
	 */
	 
	public void divergeCHC (double r, Cromosoma mejor, double prob) {
	  
		int i;

		for (i=0; i<cuerpo.length; i++) {
		  if (Randomize.Rand() < r) {
			if (Randomize.Rand() < prob) {
			  cuerpo[i] = true;
			} else {
			  cuerpo[i] = false;
			}
		  } else {
			cuerpo[i] = mejor.getGen(i);
		  }
		}
		cruzado = true;
		
	}//end-method

	/**
	 * Tests if the chromosome is already evaluated
	 *
	 * @return True if the chromosome is already evaluated. False, if not.
	 */	
	public boolean estaEvaluado () {
		return !cruzado;
		
	}//end-method

	/**
	 * Count the number of genes set to 1
	 *
	 * @return Number of genes set to 1 in the chromosome
	 */	
	public int genesActivos () {
		int i, suma = 0;

		for (i=0; i<cuerpo.length; i++) {
		  if (cuerpo[i]) suma++;
		}

		return suma;
		
	}//end-method

	/**
	 * Tests if the chromosome is valid
	 *
	 * @return True if the chromosome is valid. False, if not.
	 */	
	public boolean esValido () {
		return valido;
		
	}//end-method

	/**
	 * Marks a chromosome for deletion
	 */	
	public void borrar () {
		valido = false;
		
	}//end-method

	/**
	 * Compare to Method
	 *
	 * @param o1 Chromosome to compare
	 *
	 * @return Relative order between the chromosomes
	 */
	public int compareTo (Object o1) {
	
		if (this.calidad > ((Cromosoma)o1).calidad)
		  return -1;
		else if (this.calidad < ((Cromosoma)o1).calidad)
		  return 1;
		else return 0;
		
	}//end-method

	/**
	 * Test if two chromosome differ in only one gene
	 *
	 * @param a Chromosome to compare
	 *
	 * @return Position of the difference, if only one is found. Otherwise, -1
	 */
	public int differenceAtOne (Cromosoma a) {

    int i;
    int cont = 0, pos = -1;

		for (i=0; i<cuerpo.length && cont < 2; i++)
			if (cuerpo[i] != a.getGen(i)) {
				pos = i;
				cont++;
			}

		if (cont >= 2)
			return -1;
		else return pos;
		
	}//end-method

	/**
	 * To String Method
	 *
	 * @return String representation of the chromosome
	 */
	public String toString() {
		  
		int i;

		String temp = "[";

		for (i=0; i<cuerpo.length; i++)
		  if (cuerpo[i])
			temp += "1";
		  else
			temp += "0";
		temp += ", " + String.valueOf(calidad) + "," + String.valueOf(errorRate) + ", " + String.valueOf(genesActivos()) + "]";

		return temp;
		
	}//end-method
	
}//end-class

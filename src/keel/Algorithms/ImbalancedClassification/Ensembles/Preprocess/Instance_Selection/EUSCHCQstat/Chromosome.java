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

package keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Instance_Selection.EUSCHCQstat;

import keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Basic.KNN;
import org.core.*;
import keel.Dataset.*;

/**
 * Class to implement a chromosome for the EUS-CHC metho
 * @author Created by Salvador García López (UJA) [19-07-2004]
 * @author Modified by Mikel Galar Idoate (UPNA) [03-05-13]
 * @version 1.1 (12-05-14)
 * @since JDK 1.5
 */
public class Chromosome implements Comparable {

	/*Cromosome data structure*/
	boolean cuerpo[];

	/*Useful data for cromosomes*/
	double calidad;
	boolean cruzado;
	boolean valido;

	boolean prediction[];

	/** 
	 * Construct a random chromosome of specified size
	 * @param size 
	 */
	public Chromosome (int size) {

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
	}

	/** 
	 * It creates a copied chromosome
	 * @param size
	 * @param a Chromosome to copy
	 */
	public Chromosome (int size, Chromosome a) {

		int i;

		cuerpo = new boolean[size];
		for (i=0; i<cuerpo.length; i++)
			cuerpo[i] = a.getGen(i);
		calidad = a.getCalidad();
		cruzado = false;
		valido = true;
		prediction = a.prediction.clone();
	}

	/**
	 * It returns a given gen of the chromsome
	 * @param indice
	 * @return
	 */
	public boolean getGen (int indice) {
		return cuerpo[indice];
	}

	/**
	 * Ite returns the fitness of the chrom.
	 * @return
	 */
	public double getCalidad () {
		return calidad;
	}

	/**
	 * It sets a value for a given chrom.
	 * @param indice
	 * @param valor
	 */
	public void setGen (int indice, boolean valor) {
		cuerpo[indice] = valor;
	} 

	/**
	 * Function that evaluates a cromosome
	 */
	public void evalua (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double train[][], double trainR[][], int trainN[][], boolean trainM[][], int clasesT[], String wrapper, int K, String evMeas, boolean MS, boolean pFactor, double P, int posID, int nPos, boolean distanceEu, keel.Dataset.Attribute entradas[], boolean[][] anteriores, boolean[][] salidasAnteriores) {

		int i, j, l=0, m, h;
		int aciertosP = 0, aciertosN = 0;
		int totalP = 0, totalN = 0;
		double beta;
		double precision, recall;
		int vecinos[];
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int s, claseObt;

		prediction = new boolean[train.length];

		int negID = -1;
		for (i = 0; i < datos.length; i++)
			if(clases[i] != posID) {
				negID = clases[i];
				break;
			}
		if (MS) {
			s = genesActivos() + nPos;				
			vecinos = new int[K];	        
			conjS = new double[s][train[0].length];
			conjR = new double[s][train[0].length];
			conjN = new int[s][train[0].length];
			conjM = new boolean[s][train[0].length];
			clasesS = new int[s];
			h=0;
			for (m=0, l=0; m<cuerpo.length; m++, h++) {
				for (;h<clasesT.length && clasesT[h]==posID;h++);
				if (getGen(m)) { //the instance must be copied to the solution
					for (j=0; j<train[h].length; j++) {
						conjS[l][j] = train[h][j];
						conjR[l][j] = trainR[h][j];
						conjN[l][j] = trainN[h][j];
						conjM[l][j] = trainM[h][j];
					}
					clasesS[l] = clasesT[h];
					l++;
				}
			}
			for (m=0; m<train.length; m++) {
				if (clasesT[m] == posID) {
					for (j=0; j<train[m].length; j++) {
						conjS[l][j] = train[m][j];
						conjR[l][j] = trainR[m][j];
						conjN[l][j] = trainN[m][j];
						conjM[l][j] = trainM[m][j];
					}
					clasesS[l] = clasesT[m];
					l++;
				}
			}

			if (wrapper.equalsIgnoreCase("k-NN")) {
				for (i=0; i<datos.length; i++) {
					claseObt = KNN.evaluacionKNN2(K, conjS, conjR, conjN, conjM, clasesS, datos[i], real[i], nominal[i], nulos[i], Math.max(posID, negID) + 1, distanceEu, vecinos);
					if (claseObt >= 0)
						if (clases[i] == claseObt && clases[i] == posID) {
							aciertosP++;
							totalP++;
							prediction[i] = true;
						} else if (clases[i] != claseObt && clases[i] == posID) {
							totalP++;
							prediction[i] = false;
						} else if (clases[i] == claseObt && clases[i] != posID) {
							aciertosN++;
							totalN++;
							prediction[i] = true;
						} else if (clases[i] != claseObt && clases[i] != posID) {
							totalN++;
							prediction[i] = false;
						}
				}	    		
			}		    
		} else {
			s = genesActivos();
			vecinos = new int[K];	        
			conjS = new double[s][train[0].length];
			conjR = new double[s][train[0].length];
			conjN = new int[s][train[0].length];
			conjM = new boolean[s][train[0].length];
			clasesS = new int[s];
			for (j=0, l=0; j<train.length; j++) {
				if (cuerpo[j]) { //the instance must be copied to the solution
					for (m=0; m<train[j].length; m++) {
						conjS[l][m] = train[j][m];
						conjR[l][m] = trainR[j][m];
						conjN[l][m] = trainN[j][m];
						conjM[l][m] = trainM[j][m];
					}
					clasesS[l] = clasesT[j];
					l++;
				}
			}    	
			for (i=0; i<datos.length; i++) {
				claseObt = KNN.evaluacionKNN2(K, conjS, conjR, conjN, conjM, clasesS, datos[i], real[i], nominal[i], nulos[i], Math.max(posID, negID) + 1, distanceEu, vecinos);
				if (claseObt >= 0)
					if (clases[i] == claseObt && clases[i] == posID) {
						aciertosP++;
						totalP++;
						prediction[i] = true;
					} else if (clases[i] != claseObt && clases[i] == posID) {
						totalP++;
						prediction[i] = false;
					} else if (clases[i] == claseObt && clases[i] != posID) {
						aciertosN++;
						totalN++;
						prediction[i] = true;
					} else if (clases[i] != claseObt && clases[i] != posID) {
						totalN++;
						prediction[i] = false;
					}
			}				
		}

		if (evMeas.equalsIgnoreCase("geometric mean")) {
			calidad = Math.sqrt(((double)aciertosP/(double)totalP)*((double)aciertosN/(double)totalN));			
		} else if (evMeas.equalsIgnoreCase("auc")) {
			if (totalP < totalN)
				calidad = (((double)aciertosP / ((double)totalP)) * ((double)aciertosN / ((double)totalN))) + ((1.0 - ((double)aciertosN / ((double)totalN)))*((double)aciertosP / ((double)totalP)))/2.0 + ((1.0 - ((double)aciertosP / ((double)totalP)))*((double)aciertosN / ((double)totalN)))/2.0;
			else
				calidad = (((double)aciertosN / ((double)totalN)) * ((double)aciertosP / ((double)totalP))) + ((1.0 - ((double)aciertosP / ((double)totalP)))*((double)aciertosN / ((double)totalN)))/2.0 + ((1.0 - ((double)aciertosN / ((double)totalN)))*((double)aciertosP / ((double)totalP)))/2.0;			
		} else if (evMeas.equalsIgnoreCase(("cost-sensitive"))) {
			calidad = ((double)totalN - aciertosN) + ((double)totalP - aciertosP) * (double)totalN/(double)totalP;
			calidad /= (2*(double)totalN);
			calidad = 1 - calidad;
		} else if (evMeas.equalsIgnoreCase(("kappa"))) {
			double sumDiagonales = 0.0, sumTrTc = 0.0;
			sumDiagonales = aciertosP + aciertosN;
			sumTrTc = totalP * (totalN - aciertosN) + totalN * (totalP - aciertosP);
			calidad = (((double)datos.length * sumDiagonales - sumTrTc) / ((double)datos.length * (double)datos.length - sumTrTc));
		}

		else {
			precision = (((double)aciertosP / ((double)totalP))) / (((double)aciertosP / ((double)totalP)) + (1.0 - ((double)aciertosN / ((double)totalN))));
			recall = (((double)aciertosP / ((double)totalP))) / (((double)aciertosP / ((double)totalP)) + (1.0 - ((double)aciertosP / ((double)totalP))));
			calidad = (2 * precision * recall)/(recall + precision);
		}

		if (pFactor) {
			if (MS) {
				beta = (double)genesActivos()/(double)nPos;				
			} else {
				beta = (double)genes0Activos(clasesT)/(double)genes1Activos(clasesT);				
			}
			calidad -= Math.abs(1.0-beta)*P;
		}

		if (anteriores[0] != null) {
			/* Calcular la distancia de Hamming mÃ­nima entre el cromosoma y anteriores[][] */
			double q = -Double.MAX_VALUE;
			for (i = 0; i < anteriores.length && anteriores[i] != null; i++) {
				double qaux = Qstatistic(anteriores[i], cuerpo, clases.length);
				if (q < qaux)
					q = qaux;
			}
			double peso = (double)(anteriores.length - i) / (double) (anteriores.length);
			double IR = (double)totalN / (double)totalP * 0.1;
			calidad = calidad * (1.0 / peso) * (1.0 / IR) - q * peso;
		}
		cruzado = false;
	}


	private double Qstatistic(boolean[] v1, boolean[] v2, int n) {
		double[][] t = new double[2][2];
		double ceros = 0;
		if (v1.length < n)
			n = v1.length;
		for (int i = 0; i < n; i++) {
			if (v1[i] == v2[i] && v1[i] == true)
				t[0][0]++;
			else if (v1[i] == v2[i] && v1[i] == false)
				t[1][1]++;
			else if (v1[i] != v2[i] && v1[i] == true)
				t[1][0]++;
			else
				t[0][1]++;
			if (!v2[i])
				ceros++;
		}
		if (ceros == n)
			return 2.0;
		return (t[1][1] * t[0][0] - t[0][1] * t[1][0]) / (t[1][1] * t[0][0] + t[0][1] * t[1][0]);
	}

	/**
	 * Function that does the CHC diverge
	*/
	public void divergeCHC (double r, Chromosome mejor, double prob) {

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
	}

	public boolean estaEvaluado () {
		return !cruzado;
	}

	public int genesActivos () {

		int i, suma = 0;

		for (i=0; i<cuerpo.length; i++) {
			if (cuerpo[i]) suma++;
		}

		return suma;
	}

	public int genes0Activos (int clases[]) {

		int i, suma = 0;

		for (i=0; i<cuerpo.length; i++) {
			if (cuerpo[i] && clases[i] == 0) suma++;
		}

		return suma;
	}

	public int genes1Activos (int clases[]) {

		int i, suma = 0;

		for (i=0; i<cuerpo.length; i++) {
			if (cuerpo[i] && clases[i] == 1) suma++;
		}

		return suma;
	}

	public boolean esValido () {
		return valido;
	}

	public void borrar () {
		valido = false;
	}

	/**
	 * Function that lets compare cromosomes for an easilier sort
	*/
	public int compareTo (Object o1) {

		if (this.calidad > ((Chromosome)o1).calidad)
			return -1;
		else if (this.calidad < ((Chromosome)o1).calidad)
			return 1;
		else return 0;
	}

	/**
	 * Prints the chrosome into a string value
	 */
	public String toString() {

		int i;
		String temp = "[";

		for (i=0; i<cuerpo.length; i++)
			if (cuerpo[i])
				temp += "1";
			else
				temp += "0";
		temp += ", " + String.valueOf(calidad) + ", " + String.valueOf(genesActivos()) + "]";

		return temp;
	}
}


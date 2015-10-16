/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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
//  EUSCHC.java
//
//  Salvador García López
//
//  Created by Salvador García López 21-4-2006.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Instance_Selection.EUSCHCQstat;

import keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Basic.OutputIS;

import keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Basic.Metodo;
import keel.Algorithms.ImbalancedClassification.Ensembles.Preprocess.Basic.KNN;

import java.util.StringTokenizer;
import java.util.Arrays;
import org.core.*;
import keel.Dataset.*;
import keel.Algorithms.ImbalancedClassification.Ensembles.parseParameters;

/**
 * The auxiliary class for the Qstat computation (diversity of the chrosomomes)
 * @author Created by Mikel Galar Idoate (UPNA) [09-05-12]
 * @version 1.1
 * @since JDK 1.5
 */
public class EUSCHCQstat extends Metodo {

	/* Own parameters of the algorithm */
	private long seed;
	private int popSize;
	private int nEval;
	private double r;
	private double prob0to1Rec;
	private double prob0to1Div;
	private double P;
	private int k;
	private String evMeas;
	private boolean majSelection;
	private boolean pFactor;
	private String hybrid;
	private int kSMOTE;
	private int ASMO;
	private double smoting;
	private boolean balance;
	private String wrapper;

	private boolean anteriores[][], salidasAnteriores[][];
	private boolean[] best, bestOutputs;

	/**
	 * Builder with a script file (configuration file)
	 * @param ficheroScript
	 */
	public EUSCHCQstat(String ficheroScript) {
		super(ficheroScript);
	}

	public void setAnteriores(boolean[][] anteriores) {
		this.anteriores = anteriores;
	}

	public void setSalidasAnteriores(boolean[][] anteriores) {
		this.salidasAnteriores = anteriores;
	}

	public boolean[] getBest() {
		return best;
	}

	public boolean[] getBestOutputs() {
		return bestOutputs;
	}

	/**
	 * It runs the Qstatistic
	 */
	public void runAlgorithm() {

		int i, j, l, h;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int nSel = 0;
		Chromosome poblacion[];
		int ev = 0;
		Chromosome C[];
		int baraje[];
		int pos, tmp;
		Chromosome newPob[];
		int d;
		int tamC;
		Chromosome pobTemp[];
		int nPos = 0, nNeg = 0, posID, negID;
		double datosArt[][];
		double realArt[][];
		int nominalArt[][];
		boolean nulosArt[][];
		int clasesArt[];
		int tamS;

		long tiempo = System.currentTimeMillis();

		// Randomize.setSeed (semilla);
		posID = clasesTrain[0];
		negID = -1;
		for (i = 0; i < clasesTrain.length; i++) {
			if (clasesTrain[i] != posID) {
				negID = clasesTrain[i];
				break;
			}
		}
		/* Count of number of positive and negative examples */
		for (i = 0; i < clasesTrain.length; i++) {
			if (clasesTrain[i] == posID)
				nPos++;
			else
				nNeg++;
		}
		if (nPos > nNeg) {
			tmp = nPos;
			nPos = nNeg;
			nNeg = tmp;
			tmp = posID;
			posID = negID;
			negID = tmp;
		} else {
			/*
			 * tmp = posID; posID = negID; negID = tmp;
			 */
		}

		if (hybrid.equalsIgnoreCase("smote + eus")) {
			if (balance) {
				tamS = 2 * nNeg;
			} else {
				tamS = nNeg + nPos + (int) (nPos * smoting);
			}
			datosArt = new double[tamS][datosTrain[0].length];
			realArt = new double[tamS][datosTrain[0].length];
			nominalArt = new int[tamS][datosTrain[0].length];
			nulosArt = new boolean[tamS][datosTrain[0].length];
			clasesArt = new int[tamS];

			SMOTE(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain,
					datosArt, realArt, nominalArt, nulosArt, clasesArt, kSMOTE,
					ASMO, smoting, balance, nPos, posID, nNeg, negID,
					distanceEu);
		} else {
			datosArt = new double[datosTrain.length][datosTrain[0].length];
			realArt = new double[datosTrain.length][datosTrain[0].length];
			nominalArt = new int[datosTrain.length][datosTrain[0].length];
			nulosArt = new boolean[datosTrain.length][datosTrain[0].length];
			clasesArt = new int[clasesTrain.length];
			for (i = 0; i < datosTrain.length; i++) {
				for (j = 0; j < datosTrain[i].length; j++) {
					datosArt[i][j] = datosTrain[i][j];
					realArt[i][j] = realTrain[i][j];
					nominalArt[i][j] = nominalTrain[i][j];
					nulosArt[i][j] = nulosTrain[i][j];
				}
				clasesArt[i] = clasesTrain[i];
			}
		}

		/* Count of number of positive and negative examples */
		nPos = nNeg = 0;
		for (i = 0; i < clasesArt.length; i++) {
			if (clasesArt[i] == posID)
				nPos++;
			else
				nNeg++;
		}

		if (majSelection)
			d = nNeg / 4;
		else
			d = datosArt.length / 4;

		/* Random initialization of the population */
		poblacion = new Chromosome[popSize];
		baraje = new int[popSize];
		for (i = 0; i < popSize; i++)
			if (majSelection)
				poblacion[i] = new Chromosome(nNeg);
			else
				poblacion[i] = new Chromosome(datosArt.length);

		/* Initial evaluation of the population */
		for (i = 0; i < popSize; i++)
			poblacion[i].evalua(datosTrain, realTrain, nominalTrain,
					nulosTrain, clasesTrain, datosArt, realArt, nominalArt,
					nulosArt, clasesArt, wrapper, k, evMeas, majSelection,
					pFactor, P, posID, nPos, distanceEu, entradas, anteriores,
					salidasAnteriores);

		/* Until stop condition */
		while (ev < nEval) {
			C = new Chromosome[popSize];

			/* Selection(r) of C(t) from P(t) */
			for (i = 0; i < popSize; i++)
				baraje[i] = i;
			for (i = 0; i < popSize; i++) {
				pos = Randomize.Randint(i, popSize - 1);
				tmp = baraje[i];
				baraje[i] = baraje[pos];
				baraje[pos] = tmp;
			}
			for (i = 0; i < popSize; i++)
				if (majSelection)
					C[i] = new Chromosome(nNeg, poblacion[baraje[i]]);
				else
					C[i] = new Chromosome(datosArt.length, poblacion[baraje[i]]);

			/* Structure recombination in C(t) constructing C'(t) */
			tamC = recombinar(C, d, nNeg, nPos, majSelection);
			newPob = new Chromosome[tamC];
			for (i = 0, l = 0; i < C.length; i++) {
				if (C[i].esValido()) { // the cromosome must be copied to the
					// new poblation C'(t)
					if (majSelection)
						newPob[l] = new Chromosome(nNeg, C[i]);
					else
						newPob[l] = new Chromosome(datosArt.length, C[i]);
					l++;
				}
			}

			/* Structure evaluation in C'(t) */
			for (i = 0; i < newPob.length; i++) {
				newPob[i].evalua(datosTrain, realTrain, nominalTrain,
						nulosTrain, clasesTrain, datosArt, realArt, nominalArt,
						nulosArt, clasesArt, wrapper, k, evMeas, majSelection,
						pFactor, P, posID, nPos, distanceEu, entradas,
						anteriores, salidasAnteriores);
				ev++;
			}

			/* Selection(s) of P(t) from C'(t) and P(t-1) */
			Arrays.sort(poblacion);
			Arrays.sort(newPob);
			/*
			 * If the best of C' is worse than the worst of P(t-1), then there
			 * will no changes
			 */
			if (tamC == 0
					|| newPob[0].getCalidad() < poblacion[popSize - 1]
							.getCalidad()) {
				d--;
			} else {
				pobTemp = new Chromosome[popSize];
				for (i = 0, j = 0, l = 0; i < popSize && l < tamC; i++) {
					if (poblacion[j].getCalidad() > newPob[l].getCalidad()) {
						if (majSelection)
							pobTemp[i] = new Chromosome(nNeg, poblacion[j]);
						else
							pobTemp[i] = new Chromosome(datosArt.length,
									poblacion[j]);
						j++;
					} else {
						if (majSelection)
							pobTemp[i] = new Chromosome(nNeg, newPob[l]);
						else
							pobTemp[i] = new Chromosome(datosArt.length,
									newPob[l]);
						l++;
					}
				}
				if (l == tamC) { // there are cromosomes for copying
					for (; i < popSize; i++) {
						if (majSelection)
							pobTemp[i] = new Chromosome(nNeg, poblacion[j]);
						else
							pobTemp[i] = new Chromosome(datosArt.length,
									poblacion[j]);
						j++;
					}
				}
				poblacion = pobTemp;
			}

			/* Last step of the algorithm */
			if (d <= 0) {
				for (i = 1; i < popSize; i++) {
					poblacion[i].divergeCHC(r, poblacion[0], prob0to1Div);
				}
				for (i = 0; i < popSize; i++)
					if (!(poblacion[i].estaEvaluado())) {
						poblacion[i].evalua(datosTrain, realTrain,
								nominalTrain, nulosTrain, clasesTrain,
								datosArt, realArt, nominalArt, nulosArt,
								clasesArt, wrapper, k, evMeas, majSelection,
								pFactor, P, posID, nPos, distanceEu, entradas,
								anteriores, salidasAnteriores);
						ev++;
					}

				/* Reinicialization of d value */
				if (majSelection)
					d = (int) (r * (1.0 - r) * (double) nNeg);
				else
					d = (int) (r * (1.0 - r) * (double) datosArt.length);
			}
		}

		Arrays.sort(poblacion);

		if (majSelection) {
			nSel = poblacion[0].genesActivos() + nPos;

			/* Construction of S set from the best cromosome */
			conjS = new double[nSel][datosArt[0].length];
			conjR = new double[nSel][datosArt[0].length];
			conjN = new int[nSel][datosArt[0].length];
			conjM = new boolean[nSel][datosArt[0].length];
			clasesS = new int[nSel];
			h = 0;
			for (i = 0, l = 0; i < nNeg; i++, h++) {
				for (; clasesArt[h] == posID && h < clasesArt.length; h++)
					;
				if (poblacion[0].getGen(i)) { // the instance must be copied to
					// the solution
					for (j = 0; j < datosArt[h].length; j++) {
						conjS[l][j] = datosArt[h][j];
						conjR[l][j] = realArt[h][j];
						conjN[l][j] = nominalArt[h][j];
						conjM[l][j] = nulosArt[h][j];
					}
					clasesS[l] = clasesArt[h];
					l++;
				}
			}
			for (i = 0; i < datosArt.length; i++) {
				if (clasesArt[i] == posID) {
					for (j = 0; j < datosArt[i].length; j++) {
						conjS[l][j] = datosArt[i][j];
						conjR[l][j] = realArt[i][j];
						conjN[l][j] = nominalArt[i][j];
						conjM[l][j] = nulosArt[i][j];
					}
					clasesS[l] = clasesArt[i];
					l++;
				}
			}
		} else {
			nSel = poblacion[0].genesActivos();

			/* Construction of S set from the best cromosome */
			conjS = new double[nSel][datosArt[0].length];
			conjR = new double[nSel][datosArt[0].length];
			conjN = new int[nSel][datosArt[0].length];
			conjM = new boolean[nSel][datosArt[0].length];
			clasesS = new int[nSel];
			for (i = 0, l = 0; i < datosArt.length; i++) {
				if (poblacion[0].getGen(i)) { // the instance must be copied to
					// the solution
					for (j = 0; j < datosArt[i].length; j++) {
						conjS[l][j] = datosArt[i][j];
						conjR[l][j] = realArt[i][j];
						conjN[l][j] = nominalArt[i][j];
						conjM[l][j] = nulosArt[i][j];
					}
					clasesS[l] = clasesArt[i];
					l++;
				}
			}
		}

		if (hybrid.equalsIgnoreCase("eus + smote")) {
			nPos = nNeg = 0;
			for (i = 0; i < clasesS.length; i++) {
				if (clasesS[i] == posID)
					nPos++;
				else
					nNeg++;
			}
			if (nPos < nNeg) {
				if (balance) {
					tamS = 2 * nNeg;
				} else {
					tamS = nNeg + nPos + (int) (nPos * smoting);
				}
				datosArt = new double[tamS][datosTrain[0].length];
				realArt = new double[tamS][datosTrain[0].length];
				nominalArt = new int[tamS][datosTrain[0].length];
				nulosArt = new boolean[tamS][datosTrain[0].length];
				clasesArt = new int[tamS];

				SMOTE(conjS, conjR, conjN, conjM, clasesS, datosArt, realArt,
						nominalArt, nulosArt, clasesArt, kSMOTE, ASMO, smoting,
						balance, nPos, posID, nNeg, negID, distanceEu);

				nSel = datosArt.length;

				/* Construction of S set from the best cromosome */
				conjS = new double[nSel][datosArt[0].length];
				conjR = new double[nSel][datosArt[0].length];
				conjN = new int[nSel][datosArt[0].length];
				conjM = new boolean[nSel][datosArt[0].length];
				clasesS = new int[nSel];
				for (i = 0; i < datosArt.length; i++) {
					for (j = 0; j < datosArt[i].length; j++) {
						conjS[i][j] = datosArt[i][j];
						conjR[i][j] = realArt[i][j];
						conjN[i][j] = nominalArt[i][j];
						conjM[i][j] = nulosArt[i][j];
					}
					clasesS[i] = clasesArt[i];
				}
			}
		}

		/*
		 * for (i = 0; i < poblacion.length; i++){ for (j = 0; j <
		 * poblacion[0].cuerpo.length; j++){
		 * System.out.print((poblacion[i].cuerpo[j] ? 1 : 0)); }
		 * System.out.println(" Calidad: " + poblacion[i].calidad); }
		 */
		best = poblacion[0].cuerpo.clone();
		bestOutputs = poblacion[0].prediction.clone();
		System.out
		.println("QstatEUSCHC " + relation + " "
				+ (double) (System.currentTimeMillis() - tiempo)
				/ 1000.0 + "s");

		OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS,
				entradas, salida, nEntradas, relation);
		// OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida,
		// nEntradas, relation);
	}

	/**
	 * Function that determines the cromosomes who have to be crossed and the
	 * other ones who have to be removed It returns the number of remaining
	 * cromosomes in the poblation
	 */
	private int recombinar(Chromosome C[], int d, int nNeg, int nPos,
			boolean majSelection) {

		int i, j;
		int distHamming;
		int tamC = 0;
		int n;

		if (majSelection)
			n = nNeg;
		else
			n = nNeg + nPos;

		for (i = 0; i < C.length / 2; i++) {
			distHamming = 0;
			for (j = 0; j < n; j++)
				if (C[i * 2].getGen(j) != C[i * 2 + 1].getGen(j))
					distHamming++;
			if ((distHamming / 2) > d) {
				for (j = 0; j < n; j++) {
					if ((C[i * 2].getGen(j) != C[i * 2 + 1].getGen(j))
							&& Randomize.Rand() < 0.5) {
						if (C[i * 2].getGen(j))
							C[i * 2].setGen(j, false);
						else if (Randomize.Rand() < prob0to1Rec)
							C[i * 2].setGen(j, true);
						if (C[i * 2 + 1].getGen(j))
							C[i * 2 + 1].setGen(j, false);
						else if (Randomize.Rand() < prob0to1Rec)
							C[i * 2 + 1].setGen(j, true);
					}
				}
				tamC += 2;
			} else {
				C[i * 2].borrar();
				C[i * 2 + 1].borrar();
			}
		}

		return tamC;
	}

	/**
	 * SMOTE preprocessing procedure
	 * @param datosTrain input training dta
	 * @param realTrain actual training data
	 * @param nominalTrain nominal attribute values
	 * @param nulosTrain null values
	 * @param clasesTrain training classes
	 * @param datosArt synthetic instances
	 */
	public void SMOTE(double datosTrain[][], double realTrain[][],
			int nominalTrain[][], boolean nulosTrain[][], int clasesTrain[],
			double datosArt[][], double realArt[][], int nominalArt[][],
			boolean nulosArt[][], int clasesArt[], int kSMOTE, int ASMO,
			double smoting, boolean balance, int nPos, int posID, int nNeg,
			int negID, boolean distanceEu) {

		int i, j, l, m;
		int tmp, pos;
		int positives[];
		int neighbors[][];
		double genS[][];
		double genR[][];
		int genN[][];
		boolean genM[][];
		int clasesGen[];
		int nn;

		/* Localize the positive instances */
		positives = new int[nPos];
		for (i = 0, j = 0; i < clasesTrain.length; i++) {
			if (clasesTrain[i] == posID) {
				positives[j] = i;
				j++;
			}
		}

		/* Randomize the instance presentation */
		for (i = 0; i < positives.length; i++) {
			tmp = positives[i];
			pos = Randomize.Randint(0, positives.length - 1);
			positives[i] = positives[pos];
			positives[pos] = tmp;
		}

		/* Obtain k-nearest neighbors of each positive instance */
		neighbors = new int[positives.length][kSMOTE];
		for (i = 0; i < positives.length; i++) {
			switch (ASMO) {
			case 0:
				KNN.evaluacionKNN2(kSMOTE, datosTrain, realTrain, nominalTrain,
						nulosTrain, clasesTrain, datosTrain[positives[i]],
						realTrain[positives[i]], nominalTrain[positives[i]],
						nulosTrain[positives[i]], Math.max(posID, negID) + 1,
						distanceEu, neighbors[i]);
				break;
			case 1:
				evaluacionKNNClass(kSMOTE, datosTrain, realTrain, nominalTrain,
						nulosTrain, clasesTrain, datosTrain[positives[i]],
						realTrain[positives[i]], nominalTrain[positives[i]],
						nulosTrain[positives[i]], Math.max(posID, negID) + 1,
						distanceEu, neighbors[i], posID);
				break;
			case 2:
				evaluacionKNNClass(kSMOTE, datosTrain, realTrain, nominalTrain,
						nulosTrain, clasesTrain, datosTrain[positives[i]],
						realTrain[positives[i]], nominalTrain[positives[i]],
						nulosTrain[positives[i]], Math.max(posID, negID) + 1,
						distanceEu, neighbors[i], negID);
				break;
			}
		}

		/* Interpolation of the minority instances */
		if (balance) {
			genS = new double[nNeg - nPos][datosTrain[0].length];
			genR = new double[nNeg - nPos][datosTrain[0].length];
			genN = new int[nNeg - nPos][datosTrain[0].length];
			genM = new boolean[nNeg - nPos][datosTrain[0].length];
			clasesGen = new int[nNeg - nPos];
		} else {
			genS = new double[(int) (nPos * smoting)][datosTrain[0].length];
			genR = new double[(int) (nPos * smoting)][datosTrain[0].length];
			genN = new int[(int) (nPos * smoting)][datosTrain[0].length];
			genM = new boolean[(int) (nPos * smoting)][datosTrain[0].length];
			clasesGen = new int[(int) (nPos * smoting)];
		}
		for (i = 0; i < genS.length; i++) {
			clasesGen[i] = posID;
			nn = Randomize.Randint(0, kSMOTE - 1);
			interpola(realTrain[positives[i % positives.length]],
					realTrain[neighbors[i % positives.length][nn]],
					nominalTrain[positives[i % positives.length]],
					nominalTrain[neighbors[i % positives.length][nn]],
					nulosTrain[positives[i % positives.length]],
					nulosTrain[neighbors[i % positives.length][nn]], genS[i],
					genR[i], genN[i], genM[i]);
		}

		for (j = 0; j < datosTrain.length; j++) {
			for (l = 0; l < datosTrain[0].length; l++) {
				datosArt[j][l] = datosTrain[j][l];
				realArt[j][l] = realTrain[j][l];
				nominalArt[j][l] = nominalTrain[j][l];
				nulosArt[j][l] = nulosTrain[j][l];
			}
			clasesArt[j] = clasesTrain[j];
		}
		for (m = 0; j < datosArt.length; j++, m++) {
			for (l = 0; l < datosTrain[0].length; l++) {
				datosArt[j][l] = genS[m][l];
				realArt[j][l] = genR[m][l];
				nominalArt[j][l] = genN[m][l];
				nulosArt[j][l] = genM[m][l];
			}
			clasesArt[j] = clasesGen[m];
		}
	}

	/**
	 * Knn evaluation for classification
	 * @return
	 */
	public static int evaluacionKNNClass(int nvec, double conj[][],
			double real[][], int nominal[][], boolean nulos[][], int clases[],
			double ejemplo[], double ejReal[], int ejNominal[],
			boolean ejNulos[], int nClases, boolean distance, int vecinos[],
			int clase) {

		int i, j, l;
		boolean parar = false;
		int vecinosCercanos[];
		double minDistancias[];
		int votos[];
		double dist;
		int votada, votaciones;

		if (nvec > conj.length)
			nvec = conj.length;

		votos = new int[nClases];
		vecinosCercanos = new int[nvec];
		minDistancias = new double[nvec];
		for (i = 0; i < nvec; i++) {
			vecinosCercanos[i] = -1;
			minDistancias[i] = Double.POSITIVE_INFINITY;
		}

		for (i = 0; i < conj.length; i++) {
			dist = KNN.distancia(conj[i], real[i], nominal[i], nulos[i],
					ejemplo, ejReal, ejNominal, ejNulos, distance);
			if (dist > 0 && clases[i] == clase) {
				parar = false;
				for (j = 0; j < nvec && !parar; j++) {
					if (dist < minDistancias[j]) {
						parar = true;
						for (l = nvec - 1; l >= j + 1; l--) {
							minDistancias[l] = minDistancias[l - 1];
							vecinosCercanos[l] = vecinosCercanos[l - 1];
						}
						minDistancias[j] = dist;
						vecinosCercanos[j] = i;
					}
				}
			}
		}

		for (j = 0; j < nClases; j++) {
			votos[j] = 0;
		}

		for (j = 0; j < nvec; j++) {
			if (vecinosCercanos[j] >= 0)
				votos[clases[vecinosCercanos[j]]]++;
		}

		votada = 0;
		votaciones = votos[0];
		for (j = 1; j < nClases; j++) {
			if (votaciones < votos[j]) {
				votaciones = votos[j];
				votada = j;
			}
		}

		for (i = 0; i < vecinosCercanos.length; i++)
			vecinos[i] = vecinosCercanos[i];

		return votada;
	}

	private void interpola(double ra[], double rb[], int na[], int nb[], boolean ma[],
			boolean mb[], double resS[], double resR[], int resN[],
			boolean resM[]) {

		int i;
		double diff;
		double gap;
		int suerte;

		for (i = 0; i < ra.length; i++) {
			if (ma[i] == true && mb[i] == true) {
				resM[i] = true;
				resS[i] = 0;
			} else {
				resM[i] = false;
				if (entradas[i].getType() == Attribute.REAL) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = ra[i] + gap * diff;
					resS[i] = (ra[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = Math.round(ra[i] + gap * diff);
					resS[i] = (ra[i] + entradas[i].getMinAttribute())
							/ (entradas[i].getMaxAttribute() - entradas[i]
									.getMinAttribute());
				} else {
					suerte = Randomize.Randint(0, 2);
					if (suerte == 0) {
						resN[i] = na[i];
					} else {
						resN[i] = nb[i];
					}
					resS[i] = (double) resN[i]
							/ (double) (entradas[i].getNominalValuesList()
									.size() - 1);
				}
			}
		}
	}

	/**
	 * It reads the configuration file for performing the EUS-CHC method
	 */
	public void readConfiguration(String ficheroScript) {
		parseParameters param = new parseParameters();
		param.parseConfigurationFile(ficheroScript);
		ficheroTraining = param.getTrainingInputFile();
		ficheroTest = param.getTestInputFile();
		ficheroSalida = new String[2];
		ficheroSalida[0] = param.getTrainingOutputFile();
		ficheroSalida[1] = param.getTestOutputFile();
		int i = 0;
		seed = Long.parseLong(param.getParameter(i++));
		popSize = Integer.parseInt(param.getParameter(i++));
		nEval = Integer.parseInt(param.getParameter(i++));
		r = Double.parseDouble(param.getParameter(i++));
		prob0to1Rec = Double.parseDouble(param.getParameter(i++));
		prob0to1Div = Double.parseDouble(param.getParameter(i++));
		wrapper = param.getParameter(i++);
		k = Integer.parseInt(param.getParameter(i++));
		distanceEu = param.getParameter(i++).equalsIgnoreCase("Euclidean") ? true : false;
		evMeas = param.getParameter(i++);
		if (param.getParameter(i++).equalsIgnoreCase("majority_selection"))
			majSelection = true;
		else
			majSelection = false;
		if (param.getParameter(i++).equalsIgnoreCase("EBUS"))
			pFactor = true;
		else
			pFactor = false;
		P = Double.parseDouble(param.getParameter(i++));
		hybrid = param.getParameter(i++);
		kSMOTE = Integer.parseInt(param.getParameter(i++));
		if (param.getParameter(i).equalsIgnoreCase("both"))
			ASMO = 0;
		else if (param.getParameter(i).equalsIgnoreCase("minority"))
			ASMO = 1;
		else
			ASMO = 2;
		i++;
		if (param.getParameter(i++).equalsIgnoreCase("YES"))
			balance = true;
		else
			balance = false;
		smoting = Double.parseDouble(param.getParameter(i++));		
	}
}
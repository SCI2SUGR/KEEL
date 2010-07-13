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
//  Cromosoma.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.GA_MSE_CC_FSM;

import keel.Algorithms.Preprocess.Basic.*;
import java.util.Arrays;

import org.core.*;

public class Cromosoma implements Comparable {

  /*Cromosome data structure*/
  boolean cuerpo[];

  /*Useless data for cromosomes*/
  double calidad;
  boolean cruzado;
  boolean valido;
  double errorRate;  
  int kni[][];
  int cnk[];
  int cnk_1[];
  double Bnk[];

  /*Construct a random cromosome of specified size(OK)*/
  public Cromosoma (int size, int nClases, double datosTrain[][], double realTrain[][], int nominalTrain[][], boolean nulosTrain[][], int clasesTrain[], int kNeigh, boolean distanceEu) {

    double u;
    int i, j, l;
    int vecinos[];
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel;

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

    nSel = this.genesActivos();
    /*Building of S set from the best cromosome obtained*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (getGen(i)) { //the instance must be copied to the solution
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

    kni = new int[size][nClases];
    cnk = new int[size];
    cnk_1 = new int[size];
    Bnk = new double[size];
    for (i=0; i<kni.length; i++) {
    	Arrays.fill(kni[i], 0);
    }
    vecinos = new int[kNeigh+1];
    for (i=0; i<datosTrain.length; i++) {
    	KNN.evaluacionKNN2(kNeigh+1, conjS, conjR, conjN, conjM, clasesS, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos);
    	for (j=0; j<vecinos.length-1; j++) {
    		kni[i][clasesS[vecinos[j]]]++;
    	}
    	cnk[i] = clasesS[vecinos[kNeigh-1]];
    	cnk_1[i] = clasesS[vecinos[kNeigh]];
    	Bnk[i] = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], conjS[vecinos[kNeigh-1]], conjR[vecinos[kNeigh-1]], conjN[vecinos[kNeigh-1]], conjM[vecinos[kNeigh-1]], distanceEu);
    }    
  }

  /*Create a copied cromosome (OK)*/
  public Cromosoma (int size) {
    cuerpo = new boolean[size];
  }

  /*OK*/
  public boolean getGen (int indice) {
    return cuerpo[indice];
  }

  /*OK*/
  public double getCalidad () {
    return calidad;
  }

  /*OK*/
  public void setGen (int indice, boolean valor) {
    cuerpo[indice] = valor;
  }

  /*Construct a random cromosome of specified size(OK)*/
  public void prepare (int size, int nClases, double datosTrain[][], double realTrain[][], int nominalTrain[][], boolean nulosTrain[][], int clasesTrain[], int kNeigh, boolean distanceEu) {

    int i, j, l;
    int vecinos[];
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel;

    cruzado = true;
    valido = true;

    nSel = this.genesActivos();
    /*Building of S set from the best cromosome obtained*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (getGen(i)) { //the instance must be copied to the solution
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

    kni = new int[size][nClases];
    cnk = new int[size];
    cnk_1 = new int[size];
    Bnk = new double[size];
    for (i=0; i<kni.length; i++) {
    	Arrays.fill(kni[i], 0);
    }
    vecinos = new int[kNeigh+1];
    for (i=0; i<datosTrain.length; i++) {
    	KNN.evaluacionKNN2(kNeigh+1, conjS, conjR, conjN, conjM, clasesS, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos);
    	for (j=0; j<vecinos.length-1; j++) {
    		kni[i][clasesS[vecinos[j]]]++;
    	}
    	cnk[i] = clasesS[vecinos[kNeigh-1]];
    	cnk_1[i] = clasesS[vecinos[kNeigh]];
    	Bnk[i] = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], conjS[vecinos[kNeigh-1]], conjR[vecinos[kNeigh-1]], conjN[vecinos[kNeigh-1]], conjM[vecinos[kNeigh-1]], distanceEu);
    }    
  }

  
  /*Function that evaluates a cromosome (OK)*/
  public void evalua (int clases[], int kNeigh, int nClases) {

	int i, j;
	  
	calidad = 0;
	for (i=0; i<clases.length; i++) {
		for (j=0; j<nClases; j++) {
			if (clases[i] == j) {
				calidad += ((double)kni[i][j]/(double)kNeigh - 1)*((double)kni[i][j]/(double)kNeigh - 1);
			} else {
				calidad += ((double)kni[i][j]/(double)kNeigh)*((double)kni[i][j]/(double)kNeigh);				
			}
		}
	}
	
	calidad /= (double)(clases.length*nClases);	
    cruzado = false;
}

  /*Function that does the mutation (OK)*/
  public void mutacion (int nClus, int clusters[], int size, int nClases, double datosTrain[][], double realTrain[][], int nominalTrain[][], boolean nulosTrain[][], int clasesTrain[], int kNeigh, boolean distanceEu) {
	  
	  int i, j, k, l;
	  double mindeltaM;
	  double deltaM;
	  double dist;
	  int pos;
	  int vecinos[];
	  double conjS[][];
	  double conjR[][];
	  int conjN[][];
	  boolean conjM[][];
	  int clasesS[];
	  int nSel;
	  
	  for (i=0; i<nClus; i++) {
		  mindeltaM = Double.POSITIVE_INFINITY;
		  pos = -1;
		  for (j=0; j<size; j++) {
			  if (clusters[j] == i) {
				  deltaM = 0;
				  for (k=0; k<size; k++) {
					  dist = KNN.distancia(datosTrain[k], realTrain[k], nominalTrain[k], nulosTrain[k], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
					  if (dist < Bnk[j]) {
						  deltaM += 1 + g_mn(j,k,kNeigh,clasesTrain);
					  }
				  }
				  deltaM *= 2;
				  deltaM /= (double)(kNeigh*kNeigh*nClases*size);
				  
				  if (deltaM < mindeltaM) {
					  mindeltaM = deltaM;
					  pos = j;
				  }
			  } 
		  }
		  if (pos >= 0) {
			  cuerpo[pos] = !cuerpo[pos];
			  nSel = this.genesActivos();
			  /*Building of S set from the best cromosome obtained*/
			  conjS = new double[nSel][datosTrain[0].length];
			  conjR = new double[nSel][datosTrain[0].length];
			  conjN = new int[nSel][datosTrain[0].length];
			  conjM = new boolean[nSel][datosTrain[0].length];
			  clasesS = new int[nSel];
			  for (k=0, l=0; k<datosTrain.length; k++) {
				  if (getGen(k)) { //the instance must be copied to the solution
					  for (j=0; j<datosTrain[0].length; j++) {
						  conjS[l][j] = datosTrain[k][j];
						  conjR[l][j] = realTrain[k][j];
						  conjN[l][j] = nominalTrain[k][j];
						  conjM[l][j] = nulosTrain[k][j];
					  }
					  clasesS[l] = clasesTrain[k];
					  l++;
				  }
			  }
			  vecinos = new int[kNeigh+1];
			  if (!cuerpo[pos]) {
				  for (j=0; j<size; j++) {
					  dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], datosTrain[pos], realTrain[pos], nominalTrain[pos], nulosTrain[pos], distanceEu);
					  if (dist < Bnk[j]) {
						  kni[j][clasesTrain[pos]]--;
						  kni[j][cnk_1[j]]++;
						  KNN.evaluacionKNN2(kNeigh+1, conjS, conjR, conjN, conjM, clasesS, datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], nClases, distanceEu, vecinos);
						  cnk[j] = clasesS[vecinos[kNeigh-1]];
						  cnk_1[j] = clasesS[vecinos[kNeigh]];
						  Bnk[j] = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], conjS[vecinos[kNeigh-1]], conjR[vecinos[kNeigh-1]], conjN[vecinos[kNeigh-1]], conjM[vecinos[kNeigh-1]], distanceEu);
					  }
				  }				  
			  } else {
				  for (j=0; j<size; j++) {
					  dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], datosTrain[pos], realTrain[pos], nominalTrain[pos], nulosTrain[pos], distanceEu);
					  if (dist < Bnk[j]) {
						  kni[j][clasesTrain[pos]]++;
						  kni[j][cnk[j]]--;
						  KNN.evaluacionKNN2(kNeigh+1, conjS, conjR, conjN, conjM, clasesS, datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], nClases, distanceEu, vecinos);
						  cnk[j] = clasesS[vecinos[kNeigh-1]];
						  cnk_1[j] = clasesS[vecinos[kNeigh]];
						  Bnk[j] = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], conjS[vecinos[kNeigh-1]], conjR[vecinos[kNeigh-1]], conjN[vecinos[kNeigh-1]], conjM[vecinos[kNeigh-1]], distanceEu);
					  }
				  }
			  }
			  calidad += mindeltaM;
		  }
	  }
  }
  
  int g_mn(int m, int n, int k, int clases[]) {
	  
	  if (cuerpo[m] == false && cnk[n] == clases[m]) {
		  return -1;
	  } else if (cuerpo[m] == false && cnk[n] != clases[m]) {
		  return h0mn(n, clases[m], clases[n], k);
	  } else if (cuerpo[m] == true && cnk_1[n] == clases[m]) {
		  return -1;
	  } else {
		  return h1mn(n, clases[m], clases[n], k);
	  }
  }
  
  int h0mn(int n, int cm, int cn, int k) {
	  
	  if (cn == cnk[n]) {
		  return kni[n][cm] - kni[n][cnk[n]] + k;
	  } else if (cn == cm) {
		  return kni[n][cm] - kni[n][cnk[n]] - k;
	  } else {
		  return kni[n][cm] - kni[n][cnk[n]];
	  }
  }
  
  int h1mn(int n, int cm, int cn, int k) {

	  if (cn == cnk_1[n]) {
		  return (-1)*kni[n][cm] + kni[n][cnk_1[n]] - k;
	  } else if (cn == cm) {
		  return (-1)*kni[n][cm] + kni[n][cnk_1[n]] + k;
	  } else {
		  return (-1)*kni[n][cm] + kni[n][cnk_1[n]];
	  }

  }

  /*OK*/
  public boolean estaEvaluado () {
    return !cruzado;
  }

  /*OK*/
  public int genesActivos () {
    int i, suma = 0;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) suma++;
    }

    return suma;
  }

  /*OK*/
  public boolean esValido () {
    return valido;
  }

  /*OK*/
  public void borrar () {
    valido = false;
  }

  /*Function that lets compare cromosomes to sort easily (OK)*/
  public int compareTo (Object o1) {
    if (this.calidad > ((Cromosoma)o1).calidad)
      return -1;
    else if (this.calidad < ((Cromosoma)o1).calidad)
      return 1;
    else return 0;
  }

  /*Function that inform about if a cromosome is different only in a bit, obtain the
   position of this bit. In case of have more differences, it returns -1 (OK)*/
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
  }

  /*OK*/
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
  }
}

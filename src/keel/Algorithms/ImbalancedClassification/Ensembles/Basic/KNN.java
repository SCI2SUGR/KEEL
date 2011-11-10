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
//  KNN.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 11-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.Basic;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

public class KNN {

  public static int evaluacionKNN (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {
    return evaluacionKNN2 (nvec,conj, clases, ejemplo, nClases);
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

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
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }
    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
                minDistancias[l] = minDistancias[l - 1];
                vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }
    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    return votada;
  }


  /**
   * To implement Depur Algorithm, we need the neighboor's vector, to decide what we must make
   *
   * @param nvec
   * @param conj
   * @param clases
   * @param ejemplo
   * @param nClases
   * @return The neighboors' classes.
   * 
   */
  public static int[] evaluacionKNN3 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {

	    int i, j, l;
	    boolean parar = false;
	    int vecinosCercanos[];
	    int clasesVecinosCercanos[];
	    double minDistancias[];
	    double dist;

	    if (nvec > conj.length)
	      nvec = conj.length;

	    vecinosCercanos = new int[nvec];
	    clasesVecinosCercanos= new int[nvec];
	    minDistancias = new double[nvec];
	    for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      clasesVecinosCercanos[i]=-1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	    }
	    for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], ejemplo);
	      if (dist > 0) {
	        parar = false;
	        for (j = 0; j < nvec && !parar; j++) {
	          if (dist < minDistancias[j]) {
	            parar = true;
	            for (l = nvec - 1; l >= j+1; l--) {
	                minDistancias[l] = minDistancias[l - 1];
	                vecinosCercanos[l] = vecinosCercanos[l - 1];
	            }
	            minDistancias[j] = dist;
	            vecinosCercanos[j] = i;
	          }
	        }
	      }
	    }

	    for (j=0; j<vecinosCercanos.length; j++) {
	    	if(vecinosCercanos[j]!=-1)
	    	clasesVecinosCercanos[j] =clases[vecinosCercanos[j]];
	    }

	    return clasesVecinosCercanos;
	  }


  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, Referencia nVotos) {

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
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }

    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
                minDistancias[l] = minDistancias[l - 1];
                vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }

    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    nVotos.entero = votaciones;
    return votada;
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], int clases[], double ejemplo[], int nClases, int vecinos[]) {

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
    for (i=0; i<nvec; i++) {
      vecinosCercanos[i] = -1;
      minDistancias[i] = Double.POSITIVE_INFINITY;
    }

    for (i=0; i<conj.length; i++) {
      dist = distancia(conj[i], ejemplo);
      if (dist > 0) {
        parar = false;
        for (j = 0; j < nvec && !parar; j++) {
          if (dist < minDistancias[j]) {
            parar = true;
            for (l = nvec - 1; l >= j+1; l--) {
              minDistancias[l] = minDistancias[l - 1];
              vecinosCercanos[l] = vecinosCercanos[l - 1];
            }
            minDistancias[j] = dist;
            vecinosCercanos[j] = i;
          }
        }
      }
    }

    for (j=0; j<nClases; j++) {
      votos[j] = 0;
    }
    for (j=0; j<nvec; j++) {
      if (vecinosCercanos[j] >= 0)
        votos[clases[vecinosCercanos[j]]] ++;
    }

    votada = 0;
    votaciones = votos[0];
    for (j=1; j<nClases; j++) {
      if (votaciones < votos[j]) {
        votaciones = votos[j];
        votada = j;
      }
    }

    for (i=0; i<vecinosCercanos.length; i++)
      vecinos[i] = vecinosCercanos[i];

    return votada;
  }

  public static double distancia (double ej1[], double ej2[]) {

    int i;
    double suma = 0;

    for (i=0; i<ej1.length; i++) {
      suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
    }
    suma = Math.sqrt(suma);

    return suma;
  }

  public static double distancia2 (double ej1[], double ej2[]) {

    int i;
    double suma = 0;

    for (i=0; i<ej1.length; i++) {
      suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
    }
    return suma;
  }



  /*****************************************************************************/
  /*Adapted Methods for HVDM distance*/
  public static int evaluacionKNN (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {
	  return evaluacionKNN2 (nvec, conj, real, nominal, nulos, clases, ejemplo, ejReal, ejNominal, ejNulos, nClases, distance);
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {

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
	  for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	  }

	  for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
	      if (dist > 0) {
	    	  parar = false;
	    	  for (j = 0; j < nvec && !parar; j++) {
	    		  if (dist < minDistancias[j]) {
	    			  parar = true;
	    			  for (l = nvec - 1; l >= j+1; l--) {
	    				  minDistancias[l] = minDistancias[l - 1];
	    				  vecinosCercanos[l] = vecinosCercanos[l - 1];
	    			  }
	    			  minDistancias[j] = dist;
	    			  vecinosCercanos[j] = i;
	    		  }
	    	  }
	      }
	  }

	  for (j=0; j<nClases; j++) {
	      votos[j] = 0;
	  }
	  for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			  votos[clases[vecinosCercanos[j]]] ++;
	  }
	  votada = 0;
	  votaciones = votos[0];
	  for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			  votaciones = votos[j];
			  votada = j;
		  }
	  }

	  return votada;
  }

  public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, Referencia nVotos) {

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
	  for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	  }

	  for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
	      if (dist > 0) {
	    	  parar = false;
	    	  for (j = 0; j < nvec && !parar; j++) {
	    		  if (dist < minDistancias[j]) {
	    			  parar = true;
	    			  for (l = nvec - 1; l >= j+1; l--) {
	    				  minDistancias[l] = minDistancias[l - 1];
	    				  vecinosCercanos[l] = vecinosCercanos[l - 1];
	    			  }
	    			  minDistancias[j] = dist;
	    			  vecinosCercanos[j] = i;
	    		  }
	    	  }
	      }
	  }

	  for (j=0; j<nClases; j++) {
	      votos[j] = 0;
	  }
	  for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			  votos[clases[vecinosCercanos[j]]] ++;
	  }
	  votada = 0;
	  votaciones = votos[0];
	  for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			  votaciones = votos[j];
			  votada = j;
		  }
	  }

      nVotos.entero = votaciones;
	  return votada;
  }


  /**
   * To implement Depur Algorithm, we need the neighboor's vector, to decide what we must make
   *
   * @param nvec
   * @param conj
   * @param clases
   * @param ejemplo
   * @param nClases
   * @return The neighboors' classes.
   * Isaac Triguero.
   */

  public static int[] evaluacionKNN3 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {

	  int i, j, l;
	  boolean parar = false;
	  int vecinosCercanos[];
	  int clasesVecinosCercanos[];
	  double minDistancias[];

	  double dist;


	  if (nvec > conj.length)
		  nvec = conj.length;

	  vecinosCercanos = new int[nvec];
	  clasesVecinosCercanos= new int[nvec];
	  minDistancias = new double[nvec];
	  for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      clasesVecinosCercanos[i]=-1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	  }

	  for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
	      if (dist > 0) {
	    	  parar = false;
	    	  for (j = 0; j < nvec && !parar; j++) {
	    		  if (dist < minDistancias[j]) {
	    			  parar = true;
	    			  for (l = nvec - 1; l >= j+1; l--) {
	    				  minDistancias[l] = minDistancias[l - 1];
	    				  vecinosCercanos[l] = vecinosCercanos[l - 1];
	    			  }
	    			  minDistancias[j] = dist;
	    			  vecinosCercanos[j] = i;
	    		  }
	    	  }
	      }
	  }

	    for (j=0; j<vecinosCercanos.length; j++) {
	    	if(vecinosCercanos[j]!=-1)
	    		clasesVecinosCercanos[j] =clases[vecinosCercanos[j]];
	    }

	    return clasesVecinosCercanos;
  }


  public static int evaluacionKNN2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, int vecinos[]) {

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
	  for (i=0; i<nvec; i++) {
	      vecinosCercanos[i] = -1;
	      minDistancias[i] = Double.POSITIVE_INFINITY;
	  }

	  for (i=0; i<conj.length; i++) {
	      dist = distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
	      if (dist > 0) {
	    	  parar = false;
	    	  for (j = 0; j < nvec && !parar; j++) {
	    		  if (dist < minDistancias[j]) {
	    			  parar = true;
	    			  for (l = nvec - 1; l >= j+1; l--) {
	    				  minDistancias[l] = minDistancias[l - 1];
	    				  vecinosCercanos[l] = vecinosCercanos[l - 1];
	    			  }
	    			  minDistancias[j] = dist;
	    			  vecinosCercanos[j] = i;
	    		  }
	    	  }
	      }
	  }

	  for (j=0; j<nClases; j++) {
	      votos[j] = 0;
	  }
	  for (j=0; j<nvec; j++) {
		  if (vecinosCercanos[j] >= 0)
			  votos[clases[vecinosCercanos[j]]] ++;
	  }
	  votada = 0;
	  votaciones = votos[0];
	  for (j=1; j<nClases; j++) {
		  if (votaciones < votos[j]) {
			  votaciones = votos[j];
			  votada = j;
		  }
	  }

	  for (i=0; i<vecinosCercanos.length; i++)
		  vecinos[i] = vecinosCercanos[i];

	  return votada;
  }

  public static double distancia (double ej1[], double ej1Real[], int ej1Nom[], boolean ej1Nul[], double ej2[], double ej2Real[], int ej2Nom[], boolean ej2Nul[], boolean Euc) {

	  int i;
	  double suma = 0;

	  if (Euc == true) {
		  for (i=0; i<ej1.length; i++) {
			  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		  }
		  suma = Math.sqrt(suma);
	  } else {
		  for (i=0; i<ej1.length; i++) {
			  if (ej1Nul[i] == true || ej2Nul[i] == true) {
				  suma += 1;
			  } else if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
				  suma += Metodo.nominalDistance[i][ej1Nom[i]][ej2Nom[i]];
			  } else {
				  suma += Math.abs(ej1Real[i]-ej2Real[i]) / 4*Metodo.stdDev[i];
			  }
		  }
		  suma = Math.sqrt(suma);
	  }

	  return suma;
	  }

  public static double distancia2 (double ej1[], double ej1Real[], int ej1Nom[], boolean ej1Nul[], double ej2[], double ej2Real[], int ej2Nom[], boolean ej2Nul[], boolean Euc) {

	  int i;
	  double suma = 0;

	  if (Euc == true) {
		  for (i=0; i<ej1.length; i++) {
			  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		  }
	  } else {
		  for (i=0; i<ej1.length; i++) {
			  if (ej1Nul[i] == true || ej2Nul[i] == true) {
				  suma += 1;
			  } else if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
				  suma += Metodo.nominalDistance[i][ej1Nom[i]][ej2Nom[i]];
			  } else {
				  suma += Math.abs(ej1Real[i]-ej2Real[i]) / 4*Metodo.stdDev[i];
			  }
		  }
	  }

	  return suma;
	  }

}




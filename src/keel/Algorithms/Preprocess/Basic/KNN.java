/**
 * 
 * File: KNN.java
 * 
 * An auxiliary implementation of the KNN classifier for using in Instance Selection algorithms
 * 
 * @author Written by Salvador García (University of Granada) 20/07/2004 
 * @version 0.1 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Preprocess.Basic;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

public class KNN {

	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return Class of the new instance
	 */	
	public static int evaluacionKNN (int nvec, double conj[][], int clases[], double ejemplo[], int nClases) {
		return evaluacionKNN2 (nvec,conj, clases, ejemplo, nClases);
		  
	} //end-method
	
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return Class of the new instance
	 */	
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
		  
	} //end-method

  
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 *
	 * @return The neighboors' classes.
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
		  
	} //end-method

	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 * @param nVotos Maximun number of votes achieved
	 *
	 * @return Class of the new instance
	 */	  
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
		  
	} //end-method
	
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param nClases Number of classes of the problem
	 * @param vecinos Neighbors of the new instance
	 *
	 * @return Class of the new instance
	 */	
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
		  
	} //end-method
	
  	/** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	public static double distancia (double ej1[], double ej2[]) {

		int i;
		double suma = 0;

		for (i=0; i<ej1.length; i++) {
		  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		}
		suma = Math.sqrt(suma);

		return suma;
		  
	} //end-method
	
  	/** 
	 * Calculates the unsquared Euclidean distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej2 Second instance
	 * @return The unsquared Euclidean distance
	 * 
	 */
	public static double distancia2 (double ej1[], double ej2[]) {

		int i;
		double suma = 0;

		for (i=0; i<ej1.length; i++) {
		  suma += (ej1[i]-ej2[i])*(ej1[i]-ej2[i]);
		}
		return suma;  
		  
	} //end-method


	/*****************************************************************************/
	/*Adapted Methods for HVDM distance*/
  
  
  
	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return Class of the new instance
	 */	   
	public static int evaluacionKNN (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance) {
		  return evaluacionKNN2 (nvec, conj, real, nominal, nulos, clases, ejemplo, ejReal, ejNominal, ejNulos, nClases, distance);
		  
	} //end-method
	
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return Class of the new instance
	 */	  
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
		  
	} //end-method
	
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 * @param nVotos Maximun number of votes achieved
	 *
	 * @return Class of the new instance
	 */	 
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
		  
	} //end-method

  
 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 *
	 * @return The neighboors' classes.
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
		  
	} //end-method

 	/**
	 * Executes KNN
	 *
	 * @param nvec Number of neighbors
	 * @param conj Reference to the training set
	 * @param real  Reference to the training set (real valued)
	 * @param nominal  Reference to the training set (nominal valued)	 
	 * @param nulos  Reference to the training set (null values)	
	 * @param clases Output attribute of each instance
	 * @param ejemplo New instance to classifiy
	 * @param ejReal New instance to classifiy	 (real valued)
	 * @param ejNominal New instance to classifiy	 (nominal valued)	
	 * @param ejNulos New instance to classifiy	 (null values)	
	 * @param nClases Number of classes of the problem
	 * @param distance True= Euclidean distance; False= HVDM
	 * @param vecinos Neighbors of the new instance
	 *
	 * @return Class of the new instance
	 */	   
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
		  
	} //end-method
	
  	/** 
	 * Calculates the HVDM distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej1Real First instance (Real valued)	 
	 * @param ej1Nom First instance (Nominal valued)	
	 * @param ej1Nul First instance (Null values)		 
	 * @param ej2 Second instance
	 * @param ej2Real First instance (Real valued)	 
	 * @param ej2Nom First instance (Nominal valued)	
	 * @param ej2Nul First instance (Null values)	
	 * @param Euc Use euclidean distance instead of HVDM
	 *
	 * @return The HVDM distance 
	 */
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
		  
	} //end-method
	
  	/** 
	 * Calculates the unsquared HVDM distance between two instances
	 * 
	 * @param ej1 First instance 
	 * @param ej1Real First instance (Real valued)	 
	 * @param ej1Nom First instance (Nominal valued)	
	 * @param ej1Nul First instance (Null values)		 
	 * @param ej2 Second instance
	 * @param ej2Real First instance (Real valued)	 
	 * @param ej2Nom First instance (Nominal valued)	
	 * @param ej2Nul First instance (Null values)	
	 * @param Euc Use euclidean distance instead of HVDM
	 *
	 * @return The unsquared  HVDM distance 
	 */
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
		  
	} //end-method

} //end-class




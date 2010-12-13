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
//  Created by Salvador García López 3-10-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.SSMAPSO;

import java.util.Arrays;
import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

public class Cromosoma implements Comparable {

	/*Cromosome data structure*/
	boolean cuerpo[];

	/*Index for nearest neighbours*/
	int vecinos[][];

	/*Useful data for cromosomes*/
	double fitness;
	double fitnessAc;
	boolean evaluado;
	boolean valido;
  
	/*Construct a random cromosome of specified size (OK)*/
	public Cromosoma (int K, int size, double dMatrix[][], double datos[][], double real[][], int nominal[][], boolean nulo[][], boolean distanceEu) {

		double u;
		int i, j;

		cuerpo = new boolean[size];
		vecinos = new int[size][K];
		for (i=0; i<size; i++) {
			u = Randomize.Rand();
			if (u < 0.5) {
				cuerpo[i] = false;
			} else {
				cuerpo[i] = true;
			}
		}
		evaluado = false;
		valido = true;

		for (i=0; i<size; i++) {
			for (j=0; j<K; j++) {
				vecinos[i][j] = obtenerCercano(vecinos[i],j,dMatrix, i, datos, real, nominal, nulo, distanceEu);
			}
		}
	}
  
	/*Create a copied cromosome*/
	public Cromosoma (int K, int size, Cromosoma a) {
		
		int i, j;

	    cuerpo = new boolean[size];
	    vecinos = new int[size][K];
	    for (i=0; i<cuerpo.length; i++) {
	    	cuerpo[i] = a.getGen(i);
	    	for (j=0; j<K; j++) {
	    		vecinos[i][j] = a.getVecino(i,j);
	    	}
	    }
	    fitness = a.getFitness();
	    fitnessAc = a.getFitnessAc();
	    evaluado = true;
	    valido = true;
	}

  /*Construct a cromosome throught crossover than other two parents (OK)*/
  public Cromosoma (int K, Cromosoma a, Cromosoma b, double pCross, int size) {

    int i;

    cuerpo = new boolean[size];
    vecinos = new int[size][K];
    for (i=0; i<cuerpo.length; i++) {
      if (Randomize.Rand() < pCross) {
        cuerpo[i] = b.getGen(i);
      } else {
        cuerpo[i] = a.getGen(i);
      }
    }

    evaluado = false;
    valido = true;
  }

  public void mutation (int K, double pMut, double dMatrix[][], double datos[][], double real[][], int nominal[][], boolean nulo[][], boolean distanceEu) {

	  int i, j;

	  for (i=0; i<cuerpo.length; i++) {
		  if (Randomize.Rand() < pMut) {
			  cuerpo[i] = !cuerpo[i];
		  }
	  }
	  for (i=0; i<cuerpo.length; i++) {
		  for (j=0; j<K; j++) {
			  vecinos[i][j] = obtenerCercano(vecinos[i],j,dMatrix, i, datos, real, nominal, nulo, distanceEu);
		  }
	  }
  }

  /*Obtain the nearest neighbour given a mask (cromosome)*/
  public int obtenerCercano (int vecinos[], int J, double dMatrix[][], int index, double datos[][], double real[][], int nominal[][], boolean nulo[][], boolean distanceEu) {

    double minDist;
    int minPos, i, j;
    double dist;
    boolean perfect, cont;

    if (dMatrix == null) {
      perfect = false;
      i = 0;
      do {
        for ( ; i < cuerpo.length && !cuerpo[i]; i++);
        cont = true;
        for (j=0; j<J && cont; j++) {
          if (vecinos[j] == i) {
            cont = false;
            i++;
          }
        }
        perfect = cont;
      } while (!perfect);
      minPos = i;
      if (minPos == cuerpo.length)
        return 0;
      minDist = KNN.distancia(datos[index],real[index], nominal[index], nulo[index], datos[minPos], real[minPos], nominal[minPos], nulo[minPos], distanceEu);
      for (i=minPos+1; i<cuerpo.length; i++) {
        if (cuerpo[i]) {
          cont = true;
          for (j=0; j<J && cont; j++) {
            if (vecinos[j] == i) {
              cont = false;
            }
          }
          if (cont) {
            dist = KNN.distancia(datos[index],real[index], nominal[index], nulo[index], datos[i], real[i], nominal[i], nulo[i], distanceEu);
            if (minDist > dist) {
              minPos = i;
              minDist = dist;
            }
          }
        }
      }
    } else {
      perfect = false;
      i = 0;
      do {
        for (; i < cuerpo.length && !cuerpo[i]; i++);
        cont = true;
        for (j=0; j<J && cont; j++) {
          if (vecinos[j] == i) {
            cont = false;
            i++;
          }
        }
        perfect = cont;
      } while (!perfect);
      minPos = i;
      if (minPos == cuerpo.length)
        return 0;
      minDist = dMatrix[index][minPos];
      for (i=minPos+1; i<cuerpo.length; i++) {
        if (cuerpo[i]) {
          cont = true;
          for (j=0; j<J && cont; j++) {
            if (vecinos[j] == i) {
              cont = false;
            }
          }
          if (cont) {
            if (minDist > dMatrix[index][i]) {
              minPos = i;
              minDist = dMatrix[index][i];
            }
          }
        }
      }
    }

    return minPos;
  }

  public boolean getGen (int indice) {
    return cuerpo[indice];
  }

  public int getVecino (int indicei, int indicej) {
	    return vecinos[indicei][indicej];
  }

  public double getFitness () {
    return fitness;
  }

  public double getFitnessAc () {
    return fitnessAc;
  }

  /*Function that evaluates a cromosome completely*/
  public void evaluacionCompleta (int nClases, int K, int clases[]) {

    double contador = 0;
    int i, j;
    int votos[];
    int maxPos=0, maxValue;

    votos = new int[nClases];

    for (i=0; i<vecinos.length; i++) {
      Arrays.fill(votos,0);
      for (j=0; j<K; j++) {
        votos[clases[vecinos[i][j]]]++;
      }
      maxValue = votos[0];
      maxPos = 0;
      for (j=1; j<nClases; j++) {
        if (votos[j] > maxValue) {
          maxValue = votos[j];
          maxPos = j;
        }
      }
      if (clases[i] == maxPos)
        contador++;
    }

    fitness = contador*50.0/(double)cuerpo.length + (((double)cuerpo.length - (double)this.genesActivos())/(double)cuerpo.length)*50.0;
    fitnessAc = contador;
    evaluado = true;
  }

  public void borrar () {
    valido = false;
  }

  public boolean esValido () {
    return valido;
  }

  public void setGen (int pos, boolean valor) {
    cuerpo[pos] = valor;
  }

  public boolean estaEvaluado () {
    return evaluado;
  }

  public int genesActivos () {
    int i, suma = 0;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) suma++;
    }

    return suma;
  }

  public double optimizacionLocal (int nClases, int K, int clases[], double dMatrix[][], double umbral, double datos[][], double real[][], int nominal[][], boolean nulo[][], boolean distanceEu) {

	  int n, pos, i, j, k, tmp;
	  double evaluaciones = 0;
	  double ev;
	  int visitas[];

	  n = this.genesActivos();
	  visitas = new int[n];
	  for (j=0, k=0; j<cuerpo.length; j++) {
		  if (cuerpo[j]) {
			  visitas[k] = j;
			  k++;
		  }
	  }
	  for (j=0; j<visitas.length; j++) {
		  pos = Randomize.Randint (j, visitas.length-1);
	      tmp = visitas[j];
	      visitas[j] = visitas[pos];
	      visitas[pos] = tmp;
	  }
	  i = 0;
	  while (i < n) {
		  ev = evaluacionParcial(nClases, K, clases, visitas[i], dMatrix, umbral, datos, real, nominal, nulo, distanceEu);
	      if (ev >= 0) {
	    	  n--;
	    	  i = 0;
	    	  visitas = new int[n];
	    	  try {
	    		  for (j = 0, k = 0; j < cuerpo.length; j++) {
	    			  if (cuerpo[j]) {
	    				  visitas[k] = j;
	    				  k++;
	    			  }
	    		  }
	    	  } catch (Exception e) {
	    		  i = n;
	    	  }
	    	  for (j=0; j<visitas.length; j++) {
	    		  pos = Randomize.Randint (j, visitas.length-1);
	    		  tmp = visitas[j];
	    		  visitas[j] = visitas[pos];
	    		  visitas[pos] = tmp;
	    	  }
	      } else {
	    	  i++;
	      }
	      evaluaciones += Math.abs(ev);
	  }

	  return evaluaciones;
  }

  public double evaluacionParcial (int nClases, int K, int clases[], int ref, double dMatrix[][], double umbral, double datos[][], double real[][], int nominal[][], boolean nulo[][], boolean distanceEu) {

	  int i, j;
	  int vecinosTemp[][];
	  double ganancia = 0; //an instance just been dropped
	  int contador = 0;
	  int votos[];
	  int maxPosAnterior=0, maxPosNuevo = 0,maxValue;
	  boolean evaluar;

	  votos = new int[nClases];
	  vecinosTemp = new int[cuerpo.length][K];

	  cuerpo[ref] = false;
	  for (i=0; i<cuerpo.length; i++) {
	      evaluar = false;
	      for (j=0; j<K; j++) {
	    	  if (vecinos[i][j] == ref) {
	    		  evaluar = true;
	    		  vecinosTemp[i][j] = obtenerCercano(vecinosTemp[i],j,dMatrix, i, datos, real, nominal, nulo, distanceEu);
	    	  }
	    	  else {
	    		  vecinosTemp[i][j] = vecinos[i][j];
	    	  }
	      }
	      if (evaluar) {
	    	  contador++;
	    	  Arrays.fill(votos, 0);
	    	  for (j = 0; j < K; j++) {
	    		  votos[clases[vecinos[i][j]]]++;
	    	  }
	    	  maxValue = votos[0];
	    	  maxPosAnterior = 0;
	    	  for (j = 1; j < nClases; j++) {
	    		  if (votos[j] > maxValue) {
	    			  maxValue = votos[j];
	    			  maxPosAnterior = j;
	    		  }
	    	  }
	    	  Arrays.fill(votos, 0);
	    	  for (j = 0; j < K; j++) {
	    		  votos[clases[vecinosTemp[i][j]]]++;
	    	  }
	    	  maxValue = votos[0];
	    	  maxPosNuevo = 0;
	    	  for (j = 1; j < nClases; j++) {
	    		  if (votos[j] > maxValue) {
	    			  maxValue = votos[j];
	    			  maxPosNuevo = j;
	    		  }
	    	  }
	    	  if (clases[i] == maxPosAnterior && clases[i] != maxPosNuevo) {
	    		  ganancia--;
	    	  }
	    	  else if (clases[i] != maxPosAnterior && clases[i] == maxPosNuevo) {
	    		  ganancia++;
	    	  }
	      	}
	  }

	  if (Math.round(ganancia) >= (double)umbral) {
	      for (i=0; i<cuerpo.length; i++) {
	    	  for (j=0; j<K; j++) {
	    		  vecinos[i][j] = vecinosTemp[i][j];	    		  
	    	  }
	      }
	      fitness += (ganancia*50.0/(double)cuerpo.length + (1.0/(double)cuerpo.length)*50.0);
	      fitnessAc += ganancia;
	      return ((double)contador)/((double)cuerpo.length);
	  } else {
		  cuerpo[ref] = true;
		  return (((double)contador)/((double)cuerpo.length))*(-1);
	  }
  }
  

  /*Function that lets compare cromosomes to sort easily*/
  public int compareTo (Object o1) {
    double valor1 = this.fitness;
    double valor2 = ((Cromosoma)o1).fitness;
    if (valor1 > valor2)
      return -1;
    else if (valor1 < valor2)
      return 1;
    else return 0;
  }


  /*Function that inform about if a cromosome is different only in a bit, obtain the
   position of this bit. In case of have more differences, it returns -1*/
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



  public String toString() {

    int i;

    String temp = "[";
    for (i=0; i<cuerpo.length; i++)
      if (cuerpo[i])
        temp += "1";
      else
        temp += "0";
    temp += ", " + String.valueOf(fitness) + ", " + String.valueOf(genesActivos()) + "]";

    return temp;
  }
}


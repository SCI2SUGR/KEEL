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
* <p>
* @author Written by Salvador Garcia (University of Jaén) 12/09/2009
* @version 0.1
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Hyperrectangles.EHS_CHC;

import org.core.*;
import java.util.Arrays;
import java.util.Vector;

public class Cromosoma implements Comparable {

  /*Cromosome data structure*/
  boolean cuerpo[];

  /*Useless data for cromosomes*/
  double calidad;
  boolean cruzado;
  boolean valido;
  double errorRate;
  public double cover;

  /*Construct a random cromosome of specified size(OK)*/
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
    cover=0.0;
  }

  /*Create a copied cromosome (OK)*/
  public Cromosoma (int size, Cromosoma a) {
    int i;

    cuerpo = new boolean[size];
    for (i=0; i<cuerpo.length; i++)
      cuerpo[i] = a.getGen(i);
    calidad = a.getCalidad();
    cruzado = false;
    valido = true;
    cover=a.cover;
  }

  /*Cronstruct a cromosome from a bit array (OK)*/
  public Cromosoma (boolean datos[]) {
    int i;

    cuerpo = new boolean[datos.length];
    for (i=0; i<datos.length; i++)
      cuerpo[i] = datos[i];
    cruzado = true;
    valido = true;
  }

  /*OK*/
  public boolean getGen (int indice) {
    return cuerpo[indice];
  }

  /*OK*/
  public boolean [] getBody () {
    return cuerpo;
  }

  /*OK*/
  public double getCalidad () {
    return calidad;
  }

  /*OK*/
  public void setGen (int indice, boolean valor) {
    cuerpo[indice] = valor;
  }

  /*Function that evaluates a cromosome (OK)*/
  public void evalua (double datos[][], int nominal[][], boolean missing[][], int clases[], Hyper database[], double distans[][],double alfa, int nClases, double beta) {

    int i, j;
    int aciertos = 0;
    double M, T, s;
    int vecinoCercano;
    double dist, minDist;
    double minVolume, volume;
	int dimensions, pos;
	int cover = 0;
	Vector <Integer> cand_rules = new Vector <Integer> ();
    
    M = (double)datos.length;
    T = (double)database.length;
    s = (double)genesActivos();
    
    for (i=0; i<datos.length; i++) {
    	vecinoCercano = -1;
    	minDist = Double.POSITIVE_INFINITY;
		cand_rules.removeAllElements();
		for (j=0; j<database.length; j++) {
			if (cuerpo[j]) { //It is in S
                            if(distans[j][i]>0)
                                dist=distans[j][i];
                            else{
				dist = EHS_CHC.distancia(database[j],datos[i], nominal[i], missing[i]);
                                distans[j][i]=dist;
                            }    
				if (dist > 0) {
					if (dist < minDist) {
						minDist = dist;
						vecinoCercano = j;
					}
				} else {
					dimensions = database[j].dimensions();
					if (dimensions > 0) {
						minDist = 0;
						cand_rules.add(j);
					}
				}
			}
		}
		if (minDist > 0) {
			if (vecinoCercano >= 0) {
				if (clases[i] == database[vecinoCercano].clase)
					aciertos++;
			}			
		} else {
			minVolume = database[cand_rules.elementAt(0)].volume();
			pos = 0;
			for (j=1; j<cand_rules.size(); j++) {
				volume = database[cand_rules.elementAt(j)].volume();
				if (volume < minVolume) {
					pos = j;
					minVolume = volume;
				}
			}
			if (clases[i] == database[cand_rules.elementAt(pos)].clase)
				aciertos++;
			cover++;
		}
    }    	

    calidad = ((double)(aciertos)/M)*alfa*100.0;
    calidad += ((1.0 - alfa) * 100.0 * (T - s) / T);
    calidad = calidad*beta;
    calidad += (1.0 - beta) * 100.0 * ((double)(cover)/M);
    cruzado = false;
}

  /*Function that does the mutation (OK)*/
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
  }

  /*Function that does the CHC diverge*/
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
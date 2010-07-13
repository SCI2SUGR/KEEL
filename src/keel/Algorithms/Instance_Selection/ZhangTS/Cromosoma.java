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
//  Salvador Garcï¿½a Lï¿½pez
//
//  Created by Salvador Garcï¿½a Lï¿½pez 19-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.ZhangTS;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;
import java.util.Vector;

public class Cromosoma implements Comparable {

  /*Cromosome data structure*/
  boolean cuerpo[];

  /*Useless data for cromosomes*/
  double calidad;
  boolean cruzado;
  boolean valido;
  double errorRate;

  /*Construct a random cromosome of specified size*/
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
  }

  /*Create a copied cromosome*/
  public Cromosoma (int size, Cromosoma a) {
    int i;

    cuerpo = new boolean[size];
    for (i=0; i<cuerpo.length; i++)
      cuerpo[i] = a.getGen(i);
    calidad = a.getCalidad();
    errorRate = a.getErrorRate();
    cruzado = false;
    valido = true;
  }

  /*Construct a cromosome from a bit array*/
  public Cromosoma (boolean datos[]) {
    int i;

    cuerpo = new boolean[datos.length];
    for (i=0; i<datos.length; i++)
      cuerpo[i] = datos[i];
    cruzado = true;
    valido = true;
  }

  public boolean getGen (int indice) {
    return cuerpo[indice];
  }

  public double getCalidad () {
    return calidad;
  } 

  public double getErrorRate () {
    return errorRate;
  }

  /*Function that calculates the error threshold of a cromosome*/
  public void evaluaError (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], boolean distanceEu) {

    int i, j;
    int vecinoCercano;
    double dist, minDist;
    int fallos = 0;

    for (i=0; i<datos.length; i++) {
      vecinoCercano = -1;
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datos.length; j++) {
        if (cuerpo[j]) { //It is in S
          dist = KNN.distancia(datos[i], real[i], nominal[i], nulos[i], datos[j], real[j], nominal[j], nulos[j], distanceEu);
          if (dist < minDist && dist != 0) {
            minDist = dist;
            vecinoCercano = j;
          }
        }
      }
      if (vecinoCercano >= 0) {
        if (clases[i] != clases[vecinoCercano])
          fallos++;
      } else fallos++;
    }

    errorRate = (double)(fallos)/(double)(datos.length);
  }

  /*Funcion that calculate the error threshold of a cromosome*/
  private double evaluaError2 (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], boolean distanceEu) {

    int i, j;
    int vecinoCercano;
    double dist, minDist;
    int fallos = 0;

    for (i=0; i<datos.length; i++) {
      vecinoCercano = -1;
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datos.length; j++) {
        if (cuerpo[j]) { //Estï¿½ en S
          dist = KNN.distancia(datos[i], real[i], nominal[i], nulos[i], datos[j], real[j], nominal[j], nulos[j], distanceEu);
          if (dist < minDist && dist != 0) {
            minDist = dist;
            vecinoCercano = j;
          }
        }
      }
      if (vecinoCercano >= 0) {
        if (clases[i] != clases[vecinoCercano])
          fallos++;
      } else fallos++;
    }

    return (double)(fallos)/(double)(datos.length);
  }

  /*Function that returns the best neighbor in N+*/
  public Cromosoma getSnextNplus (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], boolean distanceEu, Vector movs) {

    int i, j, k;
    int best;
    double minError, error;
    double distOrig;
    double dist, minDist;
    Vector <Integer> misclassified = new Vector <Integer>();
    Vector <Integer> NS = new Vector <Integer> ();
    boolean add;
    int vecinoCercano;
    Cromosoma temporal;

    /*Searching for the neighboor with less error rate and distance*/
    best = -1;
    minError = Double.POSITIVE_INFINITY;
    for (i=0; i<cuerpo.length; i++) {
      if (!cuerpo[i] && !movs.contains(new Integer(i))) {
        cuerpo[i] = true;
        error = evaluaError2(datos, real, nominal, nulos, clases, distanceEu);
        if (error < minError) {
          minError = error;
          best = i;
        } else if (error == minError) {
          dist = distancia (datos, real, nominal, nulos, clases, distanceEu);
          cuerpo[i] = false;
          cuerpo[best] = true;
          distOrig = distancia (datos, real, nominal, nulos, clases, distanceEu);
          cuerpo[best] = false;
          cuerpo[i] = true;
          if (dist < distOrig) {
            best = i;
          }
        }
        cuerpo[i] = false;
      }
    }
    if (minError < errorRate) { //criterion 1
      cuerpo[best] = true;
      temporal = new Cromosoma (cuerpo.length, this);
      cuerpo[best] = false;
      return temporal;
    } else { //criterion 2
      /*Calculating the misclassified instances*/
      for (i=0; i<cuerpo.length; i++) {
        vecinoCercano = -1;
        minDist = Double.POSITIVE_INFINITY;
        for (j=0; j<datos.length; j++) {
          if (cuerpo[j]) { //It is in S
            dist = KNN.distancia(datos[i], real[i], nominal[i], nulos[i], datos[j], real[j], nominal[j], nulos[j], distanceEu);
            if (dist < minDist && dist != 0) {
              minDist = dist;
              vecinoCercano = j;
            }
          }
        }
        if (vecinoCercano >= 0)
          if (clases[i] != clases[vecinoCercano])
            misclassified.addElement(new Integer(i));
      }

      /*Storing the solutions that have classified correctly some of the previous misclassified instances*/
      for (k=0; k<datos.length; k++) {
        add = false;
        if (!cuerpo[k] && !movs.contains(new Integer(k))) {
          cuerpo[k] = true;
          for (i=0; i<misclassified.size() && !add; i++) {
            vecinoCercano = -1;
            minDist = Double.POSITIVE_INFINITY;
            for (j=0; j<datos.length; j++) {
              if (cuerpo[j]) { //It is in S
                dist = KNN.distancia(datos[((Integer)(misclassified.elementAt(i))).intValue()], real[((Integer)(misclassified.elementAt(i))).intValue()], nominal[((Integer)(misclassified.elementAt(i))).intValue()], nulos[((Integer)(misclassified.elementAt(i))).intValue()], datos[j], real[j], nominal[j], nulos[j], distanceEu);
                if (dist < minDist && dist != 0) {
                  minDist = dist;
                  vecinoCercano = j;
                }
              }
            }
            if (vecinoCercano >= 0)
              if (clases[((Integer)(misclassified.elementAt(i))).intValue()] == clases[vecinoCercano]) {
                NS.addElement(new Integer(k));
                add = true;
              }
          }
          cuerpo[k] = false;
        }
      }

      if (NS.isEmpty()) {
        if (best < 0)
          best = Randomize.Randint (0, cuerpo.length-1);
        cuerpo[best] = true;
        temporal = new Cromosoma (cuerpo.length, this);
        cuerpo[best] = false;
        return temporal;
      } else {
        /*With the instances that have classified, at least, one missclasified instance, get the
         neighboor with less error rate and distance*/
        best = -1;
        minError = Double.POSITIVE_INFINITY;
        for (i=0; i<NS.size(); i++) {
          cuerpo[((Integer)(NS.elementAt(i))).intValue()] = true;
          error = evaluaError2(datos, real, nominal, nulos, clases, distanceEu);
          if (error < minError) {
            minError = error;
            best = ((Integer)(NS.elementAt(i))).intValue();
          } else if (error == minError) {
            dist = distancia (datos, real, nominal, nulos, clases, distanceEu);
            cuerpo[((Integer)(NS.elementAt(i))).intValue()] = false;
            cuerpo[best] = true;
            distOrig = distancia (datos, real, nominal, nulos, clases, distanceEu);
            cuerpo[best] = false;
            cuerpo[((Integer)(NS.elementAt(i))).intValue()] = true;
            if (dist < distOrig) {
              best = ((Integer)(NS.elementAt(i))).intValue();
            }
          }
          cuerpo[((Integer)(NS.elementAt(i))).intValue()] = false;
        }
        cuerpo[best] = true;
        temporal =  new Cromosoma (cuerpo.length, this);
        cuerpo[best] = false;
        return temporal;
      }
    }
  }

  /*Function that return the best neighbor in N-*/
  public Cromosoma getSnextNminus (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], boolean distanceEu, Vector movs) {

    int i;
    int best;
    double minError, error;
    double distOrig;
    double dist;
    Cromosoma temporal;

    /*Searching the neighbor with less error rate and distance*/
    best = -1;
    minError = Double.POSITIVE_INFINITY;
    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) {
        cuerpo[i] = false;
        error = evaluaError2(datos, real, nominal, nulos, clases, distanceEu);
        if (error < minError) {
          minError = error;
          best = i;
        } else if (error == minError) {
          dist = distancia (datos, real, nominal, nulos, clases, distanceEu);
          cuerpo[i] = true;
          cuerpo[best] = false;
          distOrig = distancia (datos, real, nominal, nulos, clases, distanceEu);
          cuerpo[best] = true;
          cuerpo[i] = false;
          if (dist < distOrig) {
            best = i;
          }
        }
        cuerpo[i] = true;
      }
    }

    cuerpo[best] = false;
    temporal = new Cromosoma (cuerpo.length, this);
    cuerpo[best] = true;
    return temporal;
  }

  /*Function that calculates the distance between the train set and the cromosome*/
  public double distancia (double datos[][], double real[][], int nominal[][], boolean nulos[][], int clases[], boolean distanceEu) {

    int i, j;
    double dist, minDist;
    double suma = 0;

    for (i=0; i<datos.length; i++) {
      minDist = Double.POSITIVE_INFINITY;
      for (j=0; j<datos.length; j++) {
        if (cuerpo[j] && clases[j] == clases[i]) { //It is in S and it is of the same class
          dist = KNN.distancia(datos[i], real[i], nominal[i], nulos[i], datos[j], real[j], nominal[j], nulos[j], distanceEu);
          if (dist < minDist) {
            minDist = dist;
          }
        }
      }
      suma += minDist;
    }

    return suma;
  }

  public int genesActivos () {
	  
    int i, suma = 0;

    for (i=0; i<cuerpo.length; i++) {
      if (cuerpo[i]) suma++;
    }

    return suma;
  }

  /*Function that lets compare cromosomes to sort easily*/
  public int compareTo (Object o1) {
    if (this.calidad > ((Cromosoma)o1).calidad)
      return -1;
    else if (this.calidad < ((Cromosoma)o1).calidad)
      return 1;
    else return 0;
  }

  /*Function that informs about if a cromosome is different only in a bit, and obtains the
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
    temp += ", " + String.valueOf(calidad) + "," + String.valueOf(errorRate) + ", " + String.valueOf(genesActivos()) + "]";

    return temp;
  }
}

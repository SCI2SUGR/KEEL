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
//  CPruner.java
//
//  Salvador García López
//
//  Created by Salvador García López 19-8-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.CPruner;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

public class CPruner extends Metodo {

  /*Own parameters of the algorithm*/
  private int k;

  public CPruner (String ficheroScript) {
    super (ficheroScript);
  }
  
  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    Vector reachability[];
    Vector coverage[];
    int vecinos[];
    int aciertos;
    double dist, minDist;
    int posic;
    Trio orden[];
    boolean critica;
    Vector temp[];

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the instance flag vector of S set*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      marcas[i] = true;
    }
    nSel = datosTrain.length;

    /*Inicialization of data structures of reachability and coverage*/
    vecinos = new int[k];
    reachability = new Vector [datosTrain.length];
    coverage = new Vector [datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      reachability[i] = new Vector();
      coverage[i] = new Vector();
    }

    /*Getting the reachability set and coverage set of each instance*/
    for (i=0; i<datosTrain.length; i++) {
      KNN.evaluacionKNN2(k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos);
      for (j=0; j<vecinos.length && vecinos[j] >= 0; j++) {
        reachability[i].addElement(new Integer(vecinos[j]));
        if (clasesTrain[vecinos[j]] == clasesTrain[i])
          coverage[vecinos[j]].addElement(new Integer(i));
      }
    }

    /*Body of the C-Pruner algorithm. First, it does a noise filter based in comparations of size
      between the two sets, then it sorts the remaining instances considering the class of the
      neighbors and the distances to the nearest enemy. Finally, it erases the superfluous instances
     */
    for (i=0; i<datosTrain.length; i++) {
      aciertos = 0;
      for (j=0; j<reachability[i].size(); j++) {
        if (clasesTrain[i] == ((Integer)reachability[i].elementAt(j)).intValue())
          aciertos++;
      }
      if (aciertos <= (k/2) && reachability[i].size() > coverage[i].size()) {//noisy instance
        marcas[i] = false;
        nSel--;
        for (j=0; j<coverage[i].size(); j++) {
          reachability[((Integer)coverage[i].elementAt(j)).intValue()].remove(new Integer(i));
          minDist = Double.POSITIVE_INFINITY;
          posic = -1;
          for (l=0; l<datosTrain.length; l++) {
            if (marcas[l] && !(reachability[((Integer)coverage[i].elementAt(j)).intValue()].contains(new Integer(l)))) {
              dist = KNN.distancia(datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], datosTrain[((Integer)coverage[i].elementAt(j)).intValue()], realTrain[((Integer)coverage[i].elementAt(j)).intValue()], nominalTrain[((Integer)coverage[i].elementAt(j)).intValue()], nulosTrain[((Integer)coverage[i].elementAt(j)).intValue()], distanceEu);
              if (dist < minDist) {
                minDist = dist;
                posic = l;
              }
            }
          }
          if (posic >= 0) {
            reachability[((Integer)coverage[i].elementAt(j)).intValue()].addElement(new Integer(posic));
          }
        }
        for (j=0; j<reachability[i].size(); j++) {
          coverage[((Integer)reachability[i].elementAt(j)).intValue()].remove(new Integer(i));
        }
      }
    }

    /*Sorting the non-noisy instances*/
    orden = new Trio[nSel];
    m = 0;
    for (i=0; i<datosTrain.length; i++) {
      if (marcas[i]) {
        minDist = Double.POSITIVE_INFINITY;
        for (j=0; j<datosTrain.length; j++) {
          if (marcas[j] && clasesTrain[i] != clasesTrain[j]) {
            dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
            if (dist < minDist)
              minDist = dist;
          }
        }
        l=0;
        for (j=0; j<reachability[i].size(); j++) {
          if (clasesTrain[((Integer)reachability[i].elementAt(j)).intValue()] == clasesTrain[i])
            l++;
        }
        orden[m] = new Trio(i,l,minDist);
        m++;
      }
    }
    Arrays.sort(orden);

    /*Deleting noisy and suplerfluous instances*/
    for (i=0; i<orden.length; i++) {
      aciertos = 0;
      for (j=0; j<reachability[orden[i].id].size(); j++) {
        if (clasesTrain[orden[i].id] == ((Integer)reachability[orden[i].id].elementAt(j)).intValue())
          aciertos++;
      }
      if (aciertos <= (k/2) && reachability[orden[i].id].size() > coverage[orden[i].id].size()) {//noisy instance
        marcas[orden[i].id] = false;
        nSel--;
      } else if (aciertos > (k/2)) {
        critica = false;
        /*is it critical without deleting p?*/
        for (j=0; j<coverage[orden[i].id].size() && !critica; j++) {
          aciertos = 0;
          for (l=0; l<reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].size(); l++) {
            if (clasesTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()] == ((Integer)reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].elementAt(l)).intValue()) {
              aciertos++;
            }
          }
          if (aciertos <= (k/2))
            critica = true;
        }

        /*is it critical deleting p?*/
        temp = new Vector[coverage[orden[i].id].size()];
        for (j=0; j<coverage[orden[i].id].size(); j++)
          temp[j] = (Vector)reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].clone();
        
        /*simulate of an actualization of reachability in a temporal vector*/
        for (j=0; j<coverage[orden[i].id].size(); j++) {
          temp[j].remove(new Integer(orden[i].id));
          minDist = Double.POSITIVE_INFINITY;
          posic = -1;
          for (l=0; l<datosTrain.length; l++) {
            if (marcas[l] && !(temp[j].contains(new Integer(l)))) {
              dist = KNN.distancia(datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], datosTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], realTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], nominalTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], nulosTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], distanceEu);
              if (dist < minDist) {
                minDist = dist;
                posic = l;
              }
            }
          }
          if (posic >= 0) {
            temp[j].addElement(new Integer(posic));
          }
        }

        /*is it critical again?*/
        for (j=0; j<coverage[orden[i].id].size() && !critica; j++) {
          aciertos = 0;
          for (l=0; l<temp[j].size(); l++) {
            if (clasesTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()] == ((Integer)temp[j].elementAt(l)).intValue()) {
              aciertos++;
            }
          }
          if (aciertos <= (k/2))
            critica = true;

          if (!critica) {
            marcas[orden[i].id] = false;
            nSel--;
          }
        }

        if (marcas[orden[i].id] == false) { //instance erased
          for (j=0; j<coverage[orden[i].id].size(); j++) {
            reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].remove(new Integer(orden[i].id));
            minDist = Double.POSITIVE_INFINITY;
            posic = -1;
            for (l=0; l<datosTrain.length; l++) {
              if (marcas[l] && !(reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].contains(new Integer(l)))) {
                dist = KNN.distancia(datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], datosTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], realTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], nominalTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], nulosTrain[((Integer)coverage[orden[i].id].elementAt(j)).intValue()], distanceEu);
                if (dist < minDist) {
                  minDist = dist;
                  posic = l;
                }
              }
            }
            if (posic >= 0) {
              reachability[((Integer)coverage[orden[i].id].elementAt(j)).intValue()].addElement(new Integer(posic));
            }
          }
        }
      }
    }

    /*Construction of the S set from the flags vector */
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (m=0, l=0; m<datosTrain.length; m++) {
      if (marcas[m]) { //the instance will evaluate
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[m][j];
          conjR[l][j] = realTrain[m][j];
          conjN[l][j] = nominalTrain[m][j];
          conjM[l][j] = nulosTrain[m][j];
        }
        clasesS[l] = clasesTrain[m];
        l++;
      }
    }

    System.out.println("Cpruner "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

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

    /*Getting the names of the training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Getting the path and base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);
    
    /*Getting the number of neighbors*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    k = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}

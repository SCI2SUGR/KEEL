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
//  Reconsistent.java
//
//  Salvador García López
//
//  Created by Salvador García López 7-5-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Reconsistent;

import keel.Algorithms.Preprocess.Basic.*;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;
import org.core.*;

public class Reconsistent extends Metodo {

  public Reconsistent (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l;
    boolean marcas[];
    boolean marcas2[];
    boolean marcastmp[];
    boolean incorrect[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    Vector <Integer> vecinos[];
    int next;
    int maxneigh;
    int pos;
    int borrado;
    int claseObt;
    int nClases;

    long tiempo = System.currentTimeMillis();
    
    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;    

    /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    marcas2 = new boolean[datosTrain.length];
    incorrect = new boolean[datosTrain.length];
    marcastmp = new boolean[datosTrain.length];
    Arrays.fill(marcas, true);
    Arrays.fill(marcas2, true);
    Arrays.fill(incorrect, false);
    Arrays.fill(marcastmp, true);
    vecinos = new Vector [datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
        vecinos[i] = new Vector <Integer>();

    for (i=0; i<datosTrain.length; i++) {
        next = nextNeighbour (marcas,datosTrain,i,vecinos[i]);
        for (j=0; j<datosTrain.length; j++)
            marcastmp[j] = marcas[j];
        while (next >= 0 && clasesTrain[next] == clasesTrain[i]) {
        	vecinos[i].add(new Integer(next));
        	marcastmp[next] = false;
        	next = nextNeighbour(marcastmp,datosTrain,i,vecinos[i]);
        }    	
    }
    
    maxneigh = vecinos[0].size();
    pos = 0;
    for (i=1; i<datosTrain.length; i++) {
      if (vecinos[i].size() > maxneigh) {
        maxneigh = vecinos[i].size();
        pos = i;
      }
    }
    
    while (maxneigh > 0) {
      for (i=0; i<vecinos[pos].size(); i++) {
        borrado = vecinos[pos].elementAt(i).intValue();
        marcas[borrado] = false;
        for (j=0; j<datosTrain.length; j++) {
          vecinos[j].removeElement(new Integer(borrado));
        }
        vecinos[borrado].clear();
      }
      vecinos[pos].clear();

      maxneigh = vecinos[0].size();
      pos = 0;
      for (i=1; i<datosTrain.length; i++) {
        if (vecinos[i].size() > maxneigh) {
          maxneigh = vecinos[i].size();
          pos = i;
        }
      }
    }

    /*Building of the S set from the flags*/
    nSel = 0;
    for (i=0; i<datosTrain.length; i++)
      if (marcas[i]) nSel++;
    
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (marcas[i]) { //the instance will be copied to the solution
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

    for (i=0; i<datosTrain.length; i++) {
        /*Apply 1-NN to the instance*/
        claseObt = KNN.evaluacionKNN2 (1, conjS, conjR, conjN, conjM, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, true);
        if (claseObt != clasesTrain[i]) {
          incorrect[i] = true;
        }
    }
    
    for (i=0; i<datosTrain.length; i++)
        vecinos[i] = new Vector <Integer>();

    for (i=0; i<datosTrain.length; i++) {
    	if (incorrect[i]) {
    		next = nextNeighbour (marcas2,datosTrain,i,vecinos[i]);
    		for (j=0; j<datosTrain.length; j++)
    			marcastmp[j] = marcas2[j];
    		while (next >= 0 && clasesTrain[next] == clasesTrain[i]) {
    			vecinos[i].add(new Integer(next));
    			marcastmp[next] = false;
    			next = nextNeighbour(marcastmp,datosTrain,i,vecinos[i]);
    		}    	
    	}
    }
    
    maxneigh = vecinos[0].size();
    pos = 0;
    for (i=1; i<datosTrain.length; i++) {
      if (vecinos[i].size() > maxneigh) {
        maxneigh = vecinos[i].size();
        pos = i;
      }
    }
    
    while (maxneigh > 0) {
      for (i=0; i<vecinos[pos].size(); i++) {
        borrado = vecinos[pos].elementAt(i).intValue();
        marcas2[borrado] = false;
        for (j=0; j<datosTrain.length; j++) {
          vecinos[j].removeElement(new Integer(borrado));
        }
        vecinos[borrado].clear();
      }
      vecinos[pos].clear();

      maxneigh = vecinos[0].size();
      pos = 0;
      for (i=1; i<datosTrain.length; i++) {
        if (vecinos[i].size() > maxneigh) {
          maxneigh = vecinos[i].size();
          pos = i;
        }
      }
    }
    
    for (i=0; i<marcas.length; i++)
    	marcas[i] |= (marcas2[i] & incorrect[i]);
    

    /*Building of the S set from the flags*/
    nSel = 0;
    for (i=0; i<datosTrain.length; i++)
      if (marcas[i]) nSel++;
    
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (marcas[i]) { //the instance will be copied to the solution
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

    System.out.println("Reconsistent "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  int nextNeighbour (boolean marcas[], double datos[][], int ej, Vector <Integer> vecinos) {

	  int i, j, k;
	  int pos = -1;
	  double distmin = Double.POSITIVE_INFINITY;
	  double distancia;
	  double centroid[];
	  double prototipo[];

	  /*Computation of the previous centroid*/
	  centroid = new double[datos[0].length];
	  prototipo = new double[datos[0].length];
	  
      for (j=0; j<datos[0].length; j++) {
          centroid[j] = 0;
          for (k=0; k<vecinos.size(); k++) {
        	  centroid[j] += datos[vecinos.elementAt(k).intValue()][j];
          }
      }	  

	  for (i=0; i<datos.length; i++) {
	      if (marcas[i] && i != ej) {
	    	  for (j=0; j<datos[0].length; j++) {
	              prototipo[j] = centroid[j] + datos[i][j];	    		  
	              prototipo[j] /= (vecinos.size()+1);
	    	  }
	          distancia = KNN.distancia (datos[ej], prototipo);
	          if (distancia < distmin) {
	              distmin = distancia;
	              pos = i;
	          }
	      }
	  }
	    
	  return pos;
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
  }

}


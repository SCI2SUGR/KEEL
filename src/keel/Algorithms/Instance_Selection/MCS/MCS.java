/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
//  MCS.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 6-4-2007.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.MCS;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

public class MCS extends Metodo {

  public MCS (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l;
    int nClases;
    boolean marcas[];
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int NUNsample[];
    double NUNdistance[];
    int votes[];
    Vector identifiers[];
    boolean candidateVote[];
    boolean inclusion;
    boolean newcandidate[];
    int maxVotes, posMax;
    int count;
    int nSeltmp;
	boolean continuar;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i = 0; i < clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Inicialization of the flagged instances vector for a further copy*/
    marcas = new boolean[datosTrain.length];
    candidateVote = new boolean[datosTrain.length];
    newcandidate = new boolean[datosTrain.length];
    for (i = 0; i < datosTrain.length; i++) {
      newcandidate[i] = true;
    }
    nSeltmp = datosTrain.length;

    NUNsample = new int[datosTrain.length];
    NUNdistance = new double[datosTrain.length];
    votes = new int[datosTrain.length];
    identifiers = new Vector[datosTrain.length];

    /*Body of the algorithm Minimal Consistent Set*/

    do {
      for (i=0; i<datosTrain.length; i++) {
        identifiers[i] = new Vector();
      }

      for (i=0; i<datosTrain.length; i++) {
        marcas[i] = newcandidate[i];
      }
      nSel = nSeltmp;
      calcularNUN(NUNsample, NUNdistance, marcas, votes, identifiers);

      Arrays.fill(candidateVote,false);
      for (i = 0; i < datosTrain.length; i++) {
        if (!marcas[i]) {
          inclusion = true;
          for (j = 0; j < datosTrain.length && inclusion; j++) {
            if (clasesTrain[i] != clasesTrain[j]) {
              if (KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu) < NUNdistance[i]) {
                inclusion = false;
              }
            }
          }
          candidateVote[i] = inclusion;
        } else{
          candidateVote[i] = true;
        }
      }

      Arrays.fill(newcandidate, false);
      count = datosTrain.length;

	  continuar = true;
      while (count > 0 && continuar) {
        maxVotes = Integer.MIN_VALUE;
        posMax = -1;

        /*Search the most voted candidate*/
        for (i = 0; i < candidateVote.length; i++) {
          if (candidateVote[i]) {
            if (votes[i] > maxVotes) {
              maxVotes = votes[i];
              posMax = i;
            }
          }
        }

		if (posMax >= 0) {
	        /*Include the most voted candidate into the new candidate list*/
	        newcandidate[posMax] = true;
	        candidateVote[posMax] = false;
	        /*Delete the voters and update corresponding lists of voters*/
	        for (i = 0; i < identifiers[posMax].size(); i++) {
	          for (j = 0; j < datosTrain.length; j++) {
	            if (candidateVote[j]) {
	              if (identifiers[j].contains(identifiers[posMax].elementAt(i))) {
	                votes[j]--;
	                identifiers[j].removeElement(identifiers[posMax].elementAt(i));
	              }
	            }
	          }
	          count--;
	        }
		} else {
		    continuar = false;
		}
      }

      nSeltmp = 0;
      for (i = 0; i < datosTrain.length; i++) {
        if (newcandidate[i]) {
          nSeltmp++;
        }
      }
    } while (nSeltmp < nSel);

    /*Building of the S set from the flags*/
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

    System.out.println("MCS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, 1);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, 1);
	}
            
     KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);

  }

  void calcularNUN (int id[], double dist[], boolean flag[], int votos[], Vector ident[]) {

    int i, j;
    int pos;
    double minDist, distan;

    Arrays.fill(votos,0);

    for (i=0; i<datosTrain.length; i++) {
      pos = -1;
      minDist = Double.POSITIVE_INFINITY;

      /*Finding NUN instance for i instance*/
      for (j = 0; j < datosTrain.length; j++) {
        if (flag[j]) {
          distan = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
          if (i != j && distan < minDist && clasesTrain[i] != clasesTrain[j]) {
            minDist = distan;
            pos = j;
          }
        }
      }
      id[i] = pos;
      dist[i] = minDist;

      /*Finding the neighbouring instances (identifying them) closer than its NUN*/
      for (j=0; j<datosTrain.length; j++) {
        if (KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu) < dist[i] && clasesTrain[i] == clasesTrain[j]) {
          votos[j]++;
          ident[j].addElement(new Integer(i));
        }
      }
    }
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
	ficheroValidation = new String (line,i,j-i);
	
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

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;  
  }
}


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
//  Multiedit.java
//
//  Salvador García López
//
//  Created by Salvador García López 13-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.Multiedit;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;

public class Multiedit extends Metodo {

 /*Own parameters of the algorithm*/
  private long semilla;
  private int k;
  private int B;

  public Multiedit (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m;
    int nClases;
    int claseObt;
    boolean smarcas[][];
    int snSel[];
    double sconjS[][][];
    double sconjR[][][];
    int sconjN[][][];
    boolean sconjM[][][];
    int sclasesS[][];
    double sconjS2[][][];
    double sconjR2[][][];
    int sconjN2[][][];
    boolean sconjM2[][][];
    int sclasesS2[][];
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel;
    int baraje[][];
    int pos, posi=0, posj=0, tmp;
    boolean parar;
    int fin=0;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    if (k > clasesTrain.length)
      k = clasesTrain.length;

    /*Shuffle the train set and divide it into subblocks*/
    Randomize.setSeed (semilla);
    sconjS = new double[B][][];
    sconjR = new double[B][][];
    sconjN = new int[B][][];
    sconjM = new boolean[B][][];
    sclasesS = new int[B][];
    sconjS2 = new double[B][][];
    sconjR2 = new double[B][][];
    sconjN2 = new int[B][][];
    sconjM2 = new boolean[B][][];
    sclasesS2 = new int[B][];
    baraje = new int[B][];
    snSel = new int[B];
    smarcas = new boolean[B][];
    for (i=0; i<B; i++)
      if (i < datosTrain.length % B)
        baraje[i] = new int[datosTrain.length/B + 1];
      else baraje[i] = new int[datosTrain.length/B];
    for (i=0, l=0; i<B; i++)
      for (j=0; j<baraje[i].length; j++, l++)
        baraje[i][j] = l;
    for (i=0; i<B; i++) {
      for (l=0; l<baraje[i].length; l++) {
        pos = Randomize.Randint(i*baraje[i].length + l, datosTrain.length - 1);
        parar = false;
        for (j = 0; !parar; j++) {
          if (pos < baraje[j].length) {
            posi = j;
            posj = pos;
            parar = true;
          } else {
            pos -= baraje[j].length;
          }
        }
        tmp = baraje[i][l];
        baraje[i][l] = baraje[posi][posj];
        baraje[posi][posj] = tmp;
      }
    }


    /*Building of all S subsets*/
    for (i=0; i<B; i++) {
      sconjS[i] = new double[baraje[i].length][datosTrain[0].length];
      sconjR[i] = new double[baraje[i].length][datosTrain[0].length];
      sconjN[i] = new int[baraje[i].length][datosTrain[0].length];
      sconjM[i] = new boolean[baraje[i].length][datosTrain[0].length];
      sclasesS[i] = new int[baraje[i].length];
      for (m=0; m<baraje[i].length; m++) {
        for (j=0; j<datosTrain[0].length; j++) {
          sconjS[i][m][j] = datosTrain[baraje[i][m]][j];
          sconjR[i][m][j] = realTrain[baraje[i][m]][j];
          sconjN[i][m][j] = nominalTrain[baraje[i][m]][j];
          sconjM[i][m][j] = nulosTrain[baraje[i][m]][j];
        }
        sclasesS[i][m] = clasesTrain[baraje[i][m]];
      }
    }

    while (fin < B) {
      fin = 0;

      /*Inicialization of the flagged instances vector for a posterior copy*/
      for (i=0; i<B; i++) {
        smarcas[i] = new boolean[sconjS[i].length];
        for (j=0; j<sconjS[i].length; j++)
          smarcas[i][j] = false;
        snSel[i] = 0;
      }

      /*Body of the algorithm. For each instance of the i-th block, do a KNN with the i-th+1(mod B) block.
       If it is well classified, the instance is flagged for a later add. This process is repeated until
       there is no changes.*/
      for (i=0; i<B; i++) {
        /*Apply KNN to the instances of Bi*/
        for (j=0; j<sconjS[i].length; j++) {
          /*Apply KNN to the instance*/
          claseObt = KNN.evaluacionKNN2(k, sconjS[(i+1)%B], sconjR[(i+1)%B], sconjN[(i+1)%B], sconjM[(i+1)%B], sclasesS[(i+1)%B], sconjS[i][j], sconjR[i][j], sconjN[i][j], sconjM[i][j], nClases, distanceEu);
          if (claseObt == sclasesS[i][j]) { //it is well classified, add to S
            smarcas[i][j] = true;
            snSel[i]++;
          }
        }
        if (snSel[i] == sconjS[i].length)
          fin++;
        else {
          sconjS2[i] = new double[snSel[i]][datosTrain[0].length];
          sconjR2[i] = new double[snSel[i]][datosTrain[0].length];
          sconjN2[i] = new int[snSel[i]][datosTrain[0].length];
          sconjM2[i] = new boolean[snSel[i]][datosTrain[0].length];
          sclasesS2[i] = new int[snSel[i]];
          for (m=0, l=0; m<sconjS[i].length; m++) {
            if (smarcas[i][m]) {
              for (j=0; j<datosTrain[0].length; j++) {
                sconjS2[i][l][j] = sconjS[i][m][j];
                sconjR2[i][l][j] = sconjR[i][m][j];
                sconjN2[i][l][j] = sconjN[i][m][j];
                sconjM2[i][l][j] = sconjM[i][m][j];
              }
              sclasesS2[i][l] = sclasesS[i][m];
              l++;
            }
          }
          sconjS[i] = sconjS2[i];
          sconjR[i] = sconjR2[i];
          sconjN[i] = sconjN2[i];
          sconjM[i] = sconjM2[i];
          sclasesS[i] = sclasesS2[i];
        }
      }
    }

    /*Building of the final S set*/
    nSel = 0;
    for (i=0; i<B; i++)
      nSel += snSel[i];
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<B; i++) {
      for (j=0; j<sconjS[i].length; j++, l++) {
        for (m=0; m<sconjS[i][j].length; m++) {
          conjS[l][m] = sconjS[i][j][m];
          conjR[l][m] = sconjR[i][j][m];
          conjN[l][m] = sconjN[i][j][m];
          conjM[l][m] = sconjM[i][j][m];
        }
        clasesS[l] = sclasesS[i][j];
      }
    }

    System.out.println("Multiedit "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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
    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

    /*Getting the number of neighbors*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    k = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the number of subblocks*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    B = Integer.parseInt(tokens.nextToken().substring(1));
 
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}

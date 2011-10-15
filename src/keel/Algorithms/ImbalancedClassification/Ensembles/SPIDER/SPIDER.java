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
//  SPIDER.java
//
//  Mikel Galar Idoate (UPNA)
//
//  Created by Mikel Galar Idoate (UPNA) 11-5-2010.
//


package keel.Algorithms.ImbalancedClassification.Ensembles.SPIDER;

import keel.Algorithms.ImbalancedClassification.Ensembles.Basic.*;
import org.core.*;
import java.util.StringTokenizer;

import keel.Dataset.*;

public class SPIDER extends Metodo {

 /*Own parameters of the algorithm*/
  private int k;
  private String type;

  public SPIDER (String ficheroScript) {
    super (ficheroScript);
  }

  public SPIDER (InstanceSet IS, int k, String spiderType, String distance) {
     int nClases, i, j, l, m, n;
    double VDM;
    int Naxc, Nax, Nayc, Nay;
    double media, SD;

     this.type = spiderType;
     this.k = k;
     this.training = IS;
     this.test = IS;
     distanceEu = distance.equalsIgnoreCase("Euclidean")?true:false;
     ficheroSalida = new String[2];
     ficheroSalida[0] = "train.tra";
     ficheroSalida[1] = "train.tst";

       try {
         /*Normalize and check the data*/
         normalizar();
       }
       catch (Exception e) {
         System.err.println(e);
         System.exit(1);
       }

     /*Previous computation for HVDM distance*/
    if (distanceEu == false) {
      stdDev = new double[Attributes.getInputNumAttributes()];
      nominalDistance = new double[Attributes.getInputNumAttributes()][][];
      nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
      for (i = 0; i < nominalDistance.length; i++) {
        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
          nominalDistance[i] = new double[Attributes.getInputAttribute(i).
              getNumNominalValues()][Attributes.getInputAttribute(i).
              getNumNominalValues()];
          for (j = 0; j < Attributes.getInputAttribute(i).getNumNominalValues();
               j++) {
            nominalDistance[i][j][j] = 0.0;
          }
          for (j = 0; j < Attributes.getInputAttribute(i).getNumNominalValues();
               j++) {
            for (l = j + 1;
                 l < Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
              VDM = 0.0;
              Nax = Nay = 0;
              for (m = 0; m < training.getNumInstances(); m++) {
                if (nominalTrain[m][i] == j) {
                  Nax++;
                }
                if (nominalTrain[m][i] == l) {
                  Nay++;
                }
              }
              for (m = 0; m < nClases; m++) {
                Naxc = Nayc = 0;
                for (n = 0; n < training.getNumInstances(); n++) {
                  if (nominalTrain[n][i] == j && clasesTrain[n] == m) {
                    Naxc++;
                  }
                  if (nominalTrain[n][i] == l && clasesTrain[n] == m) {
                    Nayc++;
                  }
                }
                VDM +=
                    ( ( (double) Naxc / (double) Nax) - ( (double) Nayc / (double) Nay)) *
                    ( ( (double) Naxc / (double) Nax) -
                     ( (double) Nayc / (double) Nay));
              }
              nominalDistance[i][j][l] = Math.sqrt(VDM);
              nominalDistance[i][l][j] = Math.sqrt(VDM);
            }
          }
        }
        else {
          media = 0;
          SD = 0;
          for (j = 0; j < training.getNumInstances(); j++) {
            media += realTrain[j][i];
            SD += realTrain[j][i] * realTrain[j][i];
          }
          media /= (double) realTrain.length;
          stdDev[i] = Math.sqrt( (SD / ( (double) realTrain.length)) -
                                (media * media));
        }
      }
    }

  }
  
  public void ejecutar () {

    int i, j, l, t;
    int nClases;
    int claseObt;
    boolean safe[];
    int nSel = 0;
    
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];

    int nPos = 0;
    int nNeg = 0;
    int tmp;
    int posID, negID;
    int amplify[];
    int neighbours[] = null;

    long tiempo = System.currentTimeMillis();

    /*Count of number of positive and negative examples*/
    for (i=0; i<clasesTrain.length; i++) {
      if (clasesTrain[i] == 0)
        nPos++;
      else
        nNeg++;
    }
    if (nPos > nNeg) {
      tmp = nPos;
      nPos = nNeg;
      nNeg = tmp;
      posID = 1;
      negID = 0;
    } else {
      posID = 0;
      negID = 1;
    }

    /*Inicialization of the flagged instances vector for a posterior copy
      Inicialization of the amplification vector */
    safe = new boolean[datosTrain.length];
    amplify = new int[datosTrain.length]; // number of times to be amplified
    for (i=0; i<datosTrain.length; i++)
    {
      safe[i] = false;
      amplify[i] = 1; // default = 1, no amplify
    }

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Body of the algorithm. For each instance in T, search the correspond class conform his mayority
     from the nearest neighborhood. Is it is positive, the instance is selected.*/
    for (i=0; i<datosTrain.length; i++) {
      /*Apply KNN to the instance*/
      claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
      if (claseObt == clasesTrain[i]) //agree with your majority, it is included in the solution set
        safe[i] = true;
    }

    // safe[i] = false = Flagged / = true = No Flagged

    if (type.equalsIgnoreCase("weak") || type.equalsIgnoreCase("relabel"))
    {
       for (i = 0; i < datosTrain.length; i++) {
          if (clasesTrain[i] == posID && safe[i] == false) {// minority flagged as noisy
             neighbours = new int[k];
             claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, neighbours);
              // amplify as many as neighbors of the majority flagged as safe (safe = true)
             for (j = 0; j < k; j++)
                if (clasesTrain[neighbours[j]] != posID && safe[neighbours[j]] == true)
                  amplify[i]++;
          }
       }
       if (type.equalsIgnoreCase("relabel"))
       {
          for (i = 0; i < datosTrain.length; i++) {
             if (clasesTrain[i] == posID && safe[i] == false) {// minority flagged as noisy
                neighbours = new int[k];
                claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, neighbours);
                for (j = 0; j < k; j++) {
                   if (clasesTrain[neighbours[j]] != posID && safe[neighbours[j]] == false)
                   {
                      clasesTrain[neighbours[j]] = posID;
                      safe[neighbours[j]] = true;
                   }
                }
             }
          }
       }
    }
    else
    {
      for (i = 0; i < datosTrain.length; i++) {
          if (clasesTrain[i] == posID && safe[i] == true) {// minority flagged as safe
             neighbours = new int[k];
             claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, neighbours);
             for (j = 0; j < k; j++)
                if (clasesTrain[neighbours[j]] != posID && safe[neighbours[j]] == true)
                  amplify[i]++;
          }
       }
      for (i = 0; i < datosTrain.length; i++) {
          if (clasesTrain[i] == posID && safe[i] == false) {// minority flagged as noisy
             claseObt = KNN.evaluacionKNN2 (k + 2, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
             if (claseObt == clasesTrain[i])
             {
                neighbours = new int[k];
                claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, neighbours);
                 // amplify as many as neighbors of the majority flagged as safe (safe = true)
                for (j = 0; j < k; j++)
                   if (clasesTrain[neighbours[j]] != posID && safe[neighbours[j]] == true)
                     amplify[i]++;
             }
             else
             {
                neighbours = new int[k + 2];
                claseObt = KNN.evaluacionKNN2 (k + 2, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, neighbours);
                 // amplify as many as neighbors of the majority flagged as safe (safe = true)
                for (j = 0; j < k + 2; j++)
                   if (clasesTrain[neighbours[j]] != posID && safe[neighbours[j]] == true)
                     amplify[i]++;
             }
          }
       }
    }

    nSel = 0;
    for (i = 0; i < datosTrain.length; i++) {
       if ((clasesTrain[i] == posID) || (clasesTrain[i] == negID && safe[i] == true))
         nSel += amplify[i];
    }

    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if ((clasesTrain[i] == posID) || (clasesTrain[i] == negID && safe[i] == true)) { //the instance will be copied to the solution
        for (t = 0; t < amplify[i]; t++)
        {
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
    }

    System.out.println("SPIDER "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting the preprocess type */
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    type = tokens.nextToken().substring(1);
}

}

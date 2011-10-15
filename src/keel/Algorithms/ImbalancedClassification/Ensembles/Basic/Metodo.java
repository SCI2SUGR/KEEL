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
//  Metodo.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 5-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.Basic;

import keel.Dataset.*;

import java.util.StringTokenizer;

public class Metodo {

  /*Path and names of I/O files*/
  protected String ficheroTraining;
  protected String ficheroTest;
  protected String ficheroSalida[];

  /*Data Structures*/
  protected InstanceSet training;
  protected InstanceSet test;
  protected Attribute entradas[];
  protected Attribute salida;
  protected int nEntradas;
  protected String relation;

  /*Data Matrix*/
  protected double datosTrain[][];
  protected int clasesTrain[];

  /*Extra*/
  protected boolean nulosTrain[][];
  protected int nominalTrain[][];
  protected double realTrain[][];

  protected boolean distanceEu;

  static protected double nominalDistance[][][];
  static protected double stdDev[];

  public Metodo() {}

  public Metodo(String ficheroScript) {

    int nClases, i, j, l, m, n;
    double VDM;
    int Naxc, Nax, Nayc, Nay;
    double media, SD;

    distanceEu = false;

    /*Read of the script file*/
    leerConfiguracion(ficheroScript);

    /*Read of data files*/
    try {
      training = new InstanceSet();
      training.readSet(ficheroTraining, false);
      /*Normalize and check the data*/
      normalizar();
    }
    catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    }

    try {
      test = new InstanceSet();
      test.readSet(ficheroTest, false);
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

  /*This function builds the data matrix for instance selection and normalizes inputs values*/
  protected void normalizar() throws CheckException {

    int i, j, k;
    Instance temp;
    double caja[];
    StringTokenizer tokens;
    boolean nulls[];

    /*Check if dataset corresponding with a classification problem*/

    if (Attributes.getOutputNumAttributes() < 1) {
      throw new CheckException("This dataset haven�t outputs, so it not corresponding to a classification problem.");
    }
    else if (Attributes.getOutputNumAttributes() > 1) {
      throw new CheckException("This dataset have more of one output.");
    }

    if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
      throw new CheckException("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
    }

    entradas = Attributes.getInputAttributes();
    //
    salida = Attributes.getOutputAttribute(0);
    //
    nEntradas = Attributes.getInputNumAttributes();
    tokens = new StringTokenizer(training.getHeader(), " \n\r");
    tokens.nextToken();
    relation = tokens.nextToken();

    datosTrain = new double[training.getNumInstances()][Attributes.
        getInputNumAttributes()];
    clasesTrain = new int[training.getNumInstances()];
    caja = new double[1];

    nulosTrain = new boolean[training.getNumInstances()][Attributes.
        getInputNumAttributes()];
    nominalTrain = new int[training.getNumInstances()][Attributes.
        getInputNumAttributes()];
    realTrain = new double[training.getNumInstances()][Attributes.
        getInputNumAttributes()];

    double [] emin = new double[Attributes.getInputNumAttributes()];
    double [] emax = new double[Attributes.getInputNumAttributes()];
    for (i = 0; i < Attributes.getInputNumAttributes(); i++){
      emin[i] = Double.MAX_VALUE;
      emax[i] = Double.MIN_VALUE;
    }
    for (i = 0; i < training.getNumInstances(); i++) {
      temp = training.getInstance(i);
      nulls = temp.getInputMissingValues();
      datosTrain[i] = training.getInstance(i).getAllInputValues().clone();
      for (j = 0; j < nulls.length; j++) {
        if (datosTrain[i][j] < emin[j]){
          emin[j] = datosTrain[i][j];
        }
        if (datosTrain[i][j] > emax[j]){
          emax[j] = datosTrain[i][j];
        }
        if (nulls[j]) {
          datosTrain[i][j] = 0.0;
          nulosTrain[i][j] = true;
        }
      }
      caja = training.getInstance(i).getAllOutputValues().clone();
      clasesTrain[i] = (int) caja[0];
    }
    for (i = 0; i < training.getNumInstances(); i++) {
      for (k = 0; k < datosTrain[i].length; k++) {
        if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
          nominalTrain[i][k] = (int) datosTrain[i][k];
          datosTrain[i][k] /= Attributes.getInputAttribute(k).
              getNominalValuesList().size() - 1;
        }
        else {
          realTrain[i][k] = datosTrain[i][k];
          datosTrain[i][k] -= emin[k]; //Attributes.getInputAttribute(k).getMinAttribute();
          datosTrain[i][k] /= emax[k] - emin[k]; //Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();
        }
      }
    }
    for (i = 0; i < Attributes.getInputNumAttributes(); i++){
      entradas[i].setBounds(emin[i],emax[i]);
      //salida.se
    }
  }

  /*This function gets the parameters of the configuration file*/
  public void leerConfiguracion(String ficheroScript) {

  }

}

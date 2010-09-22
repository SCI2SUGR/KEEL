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
 * File: AHCClustering.java
 * </p>
 *
 * The AHCClustering algorithm is an oversampling method used to deal with the imbalanced
 * problem.
 *
 * @author Written by Salvador Garcia Lopez (University of Granada) 30/03/2006
 * @author Modified by Victoria Lopez Morales (University of Granada) 23/07/2010
 * @author Modified by Victoria Lopez Morales (University of Granada) 21/09/2010 
 * @version 0.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.ImbalancedClassification.Resampling.AHCClustering;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;

public class AHCClustering extends Metodo {
  /**
	* <p>
	* The AHCClustering algorithm is an oversampling method used to deal with the imbalanced
	* problem.
	* </p>
	*/
	
  /**
	* <p>
	* Constructor of the class. It configures the execution of the algorithm by
	* reading the configuration script that indicates the parameters that are
	* going to be used.
	* </p>
	*
	* @param ficheroScript   Name of the configuration script that indicates the
	* parameters that are going to be used during the execution of the algorithm
	*/	
  public AHCClustering (String ficheroScript) {
    super (ficheroScript);
  }

  /**
   * <p>
   * The main method of the class that includes the operations of the algorithm.
   * It includes all the operations that the algorithm has and finishes when it
   * writes the output information into files.
   * </p>
   */  
  public void run () {

    int i, j, l;
    int nClases;
    int claseObt;
    double conjS[][];
    int clasesS[];
    int nNeg = 0, nPos = 0;
    int posID, negID;
    int tmp;
    double centers[][];
    int posicTope = 0, posicAct = 0;
    boolean validosTrain[], validosExtra[];
    double minDist, distan;
    int minPosA = 0, minPosB = 0;

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

    centers = new double[nNeg-nPos][datosTrain[0].length];
    validosTrain = new boolean[datosTrain.length];
    validosExtra = new boolean[nNeg-nPos];

    /*Body of the algorithm. Consist of interpolating the examples with minimal distance and maintaining
     the new instances created. This cycle is repeated until the number of positive examples equals to the number
     of negative examples.*/

    while (posicTope < centers.length) {

      Arrays.fill(validosTrain,true);
      Arrays.fill(validosExtra,true);
      minDist = 0;

      while (minDist != Double.POSITIVE_INFINITY && posicAct < centers.length) {

        /*Search the most similar two instances*/
        minDist = Double.POSITIVE_INFINITY;
        for (i=0; i<datosTrain.length; i++) {
          if (clasesTrain[i] == posID && validosTrain[i]) {
            for (j=i+1; j<datosTrain.length; j++) {
              if (clasesTrain[j] == posID && validosTrain[j]) {
                distan = KNN.distancia(datosTrain[i], datosTrain[j]);
                if (distan < minDist) {
                  minDist = distan;
                  minPosA = i+1;
                  minPosB = j+1;
                }
              }
            }
            for (j=0; j<posicTope; j++) {
              if (validosExtra[j]) {
                distan = KNN.distancia(datosTrain[i], centers[j]);
                if (distan < minDist) {
                  minDist = distan;
                  minPosA = i+1;
                  minPosB = -j-1;
                }
              }
            }
          }
        }
        for (i=0; i<posicTope; i++) {
          if (validosExtra[i]) {
            for (j=i+1; j<posicTope; j++) {
              if (validosExtra[j]) {
                distan = KNN.distancia(centers[i], centers[j]);
                if (distan < minDist) {
                  minDist = distan;
                  minPosA = -i-1;
                  minPosB = -j-1;
                }
              }
            }
          }
        }

        /*Select the two instances and create the interpolation.*/
        if (minPosA > 0 && minPosB > 0) {
          validosTrain[minPosA-1] = false;
          validosTrain[minPosB-1] = false;
          for (i=0; i<datosTrain[0].length; i++) {
            centers[posicAct][i] = (datosTrain[minPosA-1][i] + datosTrain[minPosB-1][i]) / 2.0;
          }
        } else if (minPosA > 0 && minPosB < 0) {
          minPosB = Math.abs(minPosB);
          validosTrain[minPosA-1] = false;
          validosExtra[minPosB-1] = false;
          for (i=0; i<datosTrain[0].length; i++) {
            centers[posicAct][i] = (datosTrain[minPosA-1][i] + centers[minPosB-1][i]) / 2.0;
          }
        } else {
          minPosA = Math.abs(minPosA);
          minPosB = Math.abs(minPosB);
          validosExtra[minPosA-1] = false;
          validosExtra[minPosB-1] = false;
          for (i=0; i<datosTrain[0].length; i++) {
            centers[posicAct][i] = (centers[minPosA-1][i] + centers[minPosB-1][i]) / 2.0;
          }
        }
        posicAct++;
      }
      posicTope = posicAct;
    }

    /*Building of the S set from the flags*/
    conjS = new double[datosTrain.length+nNeg-nPos][datosTrain[0].length];
    clasesS = new int[datosTrain.length+nNeg-nPos];
    for (i=0, l=0; i<datosTrain.length; i++) {
      for (j=0; j<datosTrain[0].length; j++) {
        conjS[l][j] = datosTrain[i][j];
      }
      clasesS[l] = clasesTrain[i];
      l++;
    }

    for (i=0; i<nNeg-nPos; i++) {
      for (j=0; j<datosTrain[0].length; j++) {
        conjS[l][j] = centers[i][j];
      }
      clasesS[l] = posID;
      l++;
    }

    System.out.println("AHCClustering "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjS, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /**
   * <p>
   * Obtains the parameters used in the execution of the algorithm and stores
   * them in the private variables of the class
   * </p>
   *
   * @param ficheroScript Name of the configuration script that indicates the
   * parameters that are going to be used during the execution of the algorithm
   */  
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
  
	/** 
	 * This function builds the data matrix for reference data and normalizes inputs values
	 */	
	protected void normalizar () throws CheckException {

		int i, j, k;
		Instance temp;
		double caja[];
		StringTokenizer tokens;
		boolean nulls[];

		/*Check if dataset corresponding with a classification problem*/

		if (Attributes.getOutputNumAttributes() < 1) {
		  throw new CheckException ("This dataset haven?t outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
		  throw new CheckException ("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
		  throw new CheckException ("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
		}

		entradas = Attributes.getInputAttributes();
		salida = Attributes.getOutputAttribute(0);
		nEntradas = Attributes.getInputNumAttributes();
		tokens = new StringTokenizer (training.getHeader()," \n\r");
		tokens.nextToken();
		relation = tokens.nextToken();

		datosTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];
		clasesTrain = new int[training.getNumInstances()];
		caja = new double[1];

		nulosTrain = new boolean[training.getNumInstances()][Attributes.getInputNumAttributes()];
		nominalTrain = new int[training.getNumInstances()][Attributes.getInputNumAttributes()];
		realTrain = new double[training.getNumInstances()][Attributes.getInputNumAttributes()];

		for (i=0; i<training.getNumInstances(); i++) {
			temp = training.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTrain[i] = training.getInstance(i).getAllInputValues();
			for (j=0; j<nulls.length; j++)
				if (nulls[j]) {
					datosTrain[i][j]=0.0;
					nulosTrain[i][j] = true;
				}
			caja = training.getInstance(i).getAllOutputValues();
			clasesTrain[i] = (int) caja[0];
			for (k = 0; k < datosTrain[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					nominalTrain[i][k] = (int)datosTrain[i][k]; 
					datosTrain[i][k] /= Attributes.getInputAttribute(k).
					getNominalValuesList().size() - 1;
				} else {
					realTrain[i][k] = datosTrain[i][k];
					datosTrain[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
					datosTrain[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() -
					Attributes.getInputAttribute(k).getMinAttribute();
					if (Double.isNaN(datosTrain[i][k])){
						datosTrain[i][k] = realTrain[i][k];
			    }
				}
			}
		} 
              
            
        
      datosTest = new double[test.getNumInstances()][Attributes.getInputNumAttributes()];
		clasesTest = new int[test.getNumInstances()];
      caja = new double[1];
              
      for (i=0; i<test.getNumInstances(); i++) {
			temp = test.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTest[i] = test.getInstance(i).getAllInputValues();
			for (j=0; j<nulls.length; j++)
				if (nulls[j]) {
					datosTest[i][j]=0.0;
				}
			caja = test.getInstance(i).getAllOutputValues();
			clasesTest[i] = (int) caja[0];
		} 
              		
	} //end-method  
}

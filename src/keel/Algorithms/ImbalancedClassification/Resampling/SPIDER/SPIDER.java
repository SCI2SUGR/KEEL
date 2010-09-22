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
 * File: SPIDER.java
 * </p>
 *
 * The SPIDER algorithm is an instance selection method used to deal with
 * the imbalanced problem.
 *
 * @author Written by Mikel Galar Idoate (Public University of Navarra) 20/06/2010
 * @author Modified by Victoria Lopez Morales (University of Granada) 23/07/2010
 * @author Modified by Victoria Lopez Morales (University of Granada) 21/09/2010 
 * @version 0.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.ImbalancedClassification.Resampling.SPIDER;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;

import org.core.*;
import java.util.StringTokenizer;

public class SPIDER extends Metodo {
    /**
     * <p>
     * The SPIDER algorithm is an instance selection method used to deal with
     * the imbalanced problem.
     * </p>
     */

    /*Own parameters of the algorithm*/
    private int k;
    private String type;

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
    public SPIDER (String ficheroScript) {
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

    /* Inicialization of the flagged instances vector for a posterior copy
     * Inicialization of the amplification vector, counts the number of times
     * that an instance of the majority class is amplified */
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

    /*Getting the preprocess type WEAK / RELABEL / STRONG*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    type = tokens.nextToken().substring(1);
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

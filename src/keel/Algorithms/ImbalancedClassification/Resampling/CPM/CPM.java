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
 * File: CPM.java
 * </p>
 *
 * The CPM algorithm is an undersampling method used to deal with the imbalanced
 * problem.
 *
 * @author Written by Salvador Garcia Lopez (University of Granada) 30/03/2006
 * @author Modified by Victoria Lopez Morales (University of Granada) 23/07/2010
 * @author Modified by Victoria Lopez Morales (University of Granada) 21/09/2010 
 * @version 0.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.ImbalancedClassification.Resampling.CPM;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Preprocess.Basic.CheckException;
import keel.Dataset.*;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Vector;

public class CPM extends Metodo {
  /**
   * <p>
   * The CPM algorithm is an undersampling method used to deal with the imbalanced
   * problem.
   * </p>
   */
	
  long semilla;
  Vector centros;
  int posID;
  int negID;

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
  public CPM (String ficheroScript) {
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
    boolean marcas[];
    int nSel = 0;
    double conjS[][];
    int clasesS[];
    int nNeg = 0, nPos = 0;
    int tmp;
    int vecinosCercanos[];
    Vector cluster;
    double impureza;

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

    centros = new Vector();

    /*Initializing the main cluster and the value of impurity*/
    cluster = new Vector();
    for (i=0; i<datosTrain.length; i++)
      cluster.addElement(new Integer(i));
    impureza = (double)nPos / (double)nNeg;

    /*Body of the algorithm. This recursive function attemps to find two centers belonging to each class
     to obtain at least a cluster with lower impurity tha the original.*/

    Purity_Maximization (impureza, cluster, 0);


    /*Building of the S set from the centers vector*/
    conjS = new double[centros.size()][datosTrain[0].length];
    clasesS = new int[centros.size()];
    for (i=0; i<centros.size(); i++) {
      for (j=0; j<datosTrain[0].length; j++) {
        conjS[i][j] = datosTrain[((Integer)centros.elementAt(i)).intValue()][j];
      }
      clasesS[i] = clasesTrain[((Integer)centros.elementAt(i)).intValue()];
    }

    System.out.println("CPM "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjS, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

  /**
   * <p>
   * Recursive function that tries to find two centers belonging to each class to obtain at least
   * a cluster with lower impurity than the original
   * </p>
   *
   * @param imp Impurity of the original cluster
   * @param	parent	Cluster with the data instances
   * @param	centro	Center of the cluster we are considering
   */
  private void Purity_Maximization (double imp, Vector parent, int centro) {

    double impurity = Double.POSITIVE_INFINITY;
    Vector negatives, positives;
    Referencia pairs[];
    int i, j, k;
    int pos, tmp;
    int baraje[];
    int puntero = 0;
    int centro1 = 0, centro2 = 0;
    Vector cluster1, cluster2;
    int posit1, posit2;
    double impurity1= Double.POSITIVE_INFINITY, impurity2 = Double.POSITIVE_INFINITY;

//    System.out.println(imp + " -- "+ parent);

    cluster1 = new Vector();
    cluster2 = new Vector();

    negatives = new Vector();
    positives = new Vector();
    for (i=0; i<parent.size(); i++) {
      if (clasesTrain[((Integer)parent.elementAt(i)).intValue()] == negID) {
        negatives.add(new Integer(((Integer)parent.elementAt(i)).intValue()));
      } else {
        positives.add(new Integer(((Integer)parent.elementAt(i)).intValue()));
      }
    }

    pairs = new Referencia[negatives.size()*positives.size()];
    for (i=0, k=0; i<negatives.size(); i++) {
      for (j=0; j<positives.size(); j++, k++) {
        pairs[k] = new Referencia(((Integer)negatives.elementAt(i)).intValue(),(double)(((Integer)positives.elementAt(j)).intValue()));
      }
    }

    baraje = new int[pairs.length];
    for (i=0; i<pairs.length; i++) {
      baraje[i] = i;
    }

    for (i=0; i<pairs.length; i++) {
      pos = Randomize.Randint (i, pairs.length-1);
      tmp = baraje[i];
      baraje[i] = baraje[pos];
      baraje[pos] = tmp;
    }

    while (imp <= impurity) {
      if (puntero >= pairs.length) {
        centros.add(new Integer(centro));
        return;
      }
      centro1 = pairs[puntero].entero;
      centro2 = (int)pairs[puntero].real;
      posit1 = posit2 = 0;
	  cluster1 = new Vector();
	  cluster2 = new Vector();
      for (i=0; i<parent.size(); i++) {
        if (KNN.distancia(datosTrain[((Integer)parent.elementAt(i)).intValue()], datosTrain[centro1]) < KNN.distancia(datosTrain[((Integer)parent.elementAt(i)).intValue()], datosTrain[centro2])) {
          cluster1.add(((Integer)parent.elementAt(i)).intValue());
          if (clasesTrain[((Integer)parent.elementAt(i)).intValue()] == posID)
            posit1++;
        } else {
          cluster2.add(((Integer)parent.elementAt(i)).intValue());
          if (clasesTrain[((Integer)parent.elementAt(i)).intValue()] == posID)
            posit2++;
        }
      }
      if (cluster1.size() > 0)
        impurity1 = (double)posit1 / (double) cluster1.size();
      else {
        centros.add(new Integer(centro2));
        return;
      }
      if (cluster2.size() > 0)
        impurity2 = (double)posit2 / (double) cluster2.size();
      else {
        centros.add(new Integer(centro1));
        return;
      }
      impurity = Math.min(impurity1, impurity2);
      puntero++;
    }

    Purity_Maximization (impurity1, cluster1, centro1);
    Purity_Maximization (impurity2, cluster2, centro2);
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

    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));
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

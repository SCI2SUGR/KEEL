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
 *
 * File: KNN.java
 *
 * The KNN algorithm tries to find the K nearest instances in the
 * training data, selecting the most present class.
 *
 * Euclidean (L2), Manhattan (L1) and HVDM distances can be used as
 * distance function by the classifier.
 *
 *
 * @author Written by Salvador García López (University of Granada) 11/07/2004
  * @author Modified by Joaquin Derrac (University of Granada) 3/11/2009
 * @version 1.1
 * @since JDK1.4
 *
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySGERD;

import java.lang.String;
import java.util.StringTokenizer;
import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;
import org.core.*;

public class KNN extends Metodo {

    /*Paths and names of I/O files*/
    private String ficheroReferencia;

    /*Own parameters of the algorithm*/
    private int k;
    private int k_dif;
    private boolean distanceEu;

    /*Data structures*/
    protected InstanceSet referencia;

    /*Data matrix*/
    double datosReferencia[][];
    int clasesReferencia[];

    /*Extra*/
    boolean nulosReferencia[][];
    int nominalReferencia[][];
    double realReferencia[][];

    public KNN(InstanceSet dataset, int value_k) {
        /*Read of the script file*/
        configuracion(value_k);

        /*Read of data files*/
        try {
            training = dataset;

            /*Normalize and check the data*/
            normalizarTrain();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        try {
            referencia = dataset;

            /*Normalize the data*/
            normalizarReferencia();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    private void normalizarTrain() throws CheckException {
        int i, j, k;
        Instance temp;
        double caja[];
        StringTokenizer tokens;
        boolean nulls[];

        /*Check if dataset corresponding with a classification problem*/
        if (Attributes.getOutputNumAttributes() < 1) {
            throw new CheckException("This dataset haven´t outputs, so it not corresponding to a classification problem.");
        } else if (Attributes.getOutputNumAttributes() > 1) {
            throw new CheckException("This dataset have more of one output.");
        }

        if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
            throw new CheckException("This dataset have an input attribute with float values, so it not corresponding to a classification problem.");
        }

        entradas = Attributes.getInputAttributes();
        salida = Attributes.getOutputAttribute(0);
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

        for (i = 0; i < training.getNumInstances(); i++) {
            temp = training.getInstance(i);
            nulls = temp.getInputMissingValues();
            datosTrain[i] = training.getInstance(i).getAllInputValues();
            for (j = 0; j < nulls.length; j++) {
                if (nulls[j]) {
                    datosTrain[i][j] = 0.0;
                    nulosTrain[i][j] = true;
                }
            }
            caja = training.getInstance(i).getAllOutputValues();
            clasesTrain[i] = (int) caja[0];
            for (k = 0; k < datosTrain[i].length; k++) {
                if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
                    nominalTrain[i][k] = (int) datosTrain[i][k];
                    datosTrain[i][k] /= Attributes.getInputAttribute(k).getNominalValuesList().size() - 1;
                } else {
                    realTrain[i][k] = datosTrain[i][k];
                    datosTrain[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
                    datosTrain[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();
                }
            }
        }
    }

    /*This function builds the data matrix for classification reference and normalizes inputs values*/
    private void normalizarReferencia() throws CheckException {

        int i, j, k;
        Instance temp;
        double caja[];
        boolean nulls[];

        /*Check if dataset corresponding with a classification problem*/
        if (Attributes.getOutputNumAttributes() < 1) {
            throw new CheckException("This dataset haven´t outputs, so it not corresponding to a classification problem.");
        } else if (Attributes.getOutputNumAttributes() > 1) {
            throw new CheckException("This dataset have more of one output.");
        }

        if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
            throw new CheckException("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
        }

        datosReferencia = new double[referencia.getNumInstances()][Attributes.getInputNumAttributes()];
        clasesReferencia = new int[referencia.getNumInstances()];
        caja = new double[1];

        nulosReferencia = new boolean[referencia.getNumInstances()][Attributes.getInputNumAttributes()];
        nominalReferencia = new int[referencia.getNumInstances()][Attributes.getInputNumAttributes()];
        realReferencia = new double[referencia.getNumInstances()][Attributes.getInputNumAttributes()];

        /*Get the number of instances that have a null value*/
        for (i = 0; i < referencia.getNumInstances(); i++) {
            temp = referencia.getInstance(i);
            nulls = temp.getInputMissingValues();
            datosReferencia[i] = referencia.getInstance(i).getAllInputValues();
            for (j = 0; j < nulls.length; j++) {
                if (nulls[j]) {
                    datosReferencia[i][j] = 0.0;
                    nulosReferencia[i][j] = true;
                }
            }
            caja = referencia.getInstance(i).getAllOutputValues();
            clasesReferencia[i] = (int) caja[0];
            for (k = 0; k < datosReferencia[i].length; k++) {
                if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
                    nominalReferencia[i][k] = (int) datosReferencia[i][k];
                    datosReferencia[i][k] /= Attributes.getInputAttribute(k).getNominalValuesList().size() - 1;
                } else {
                    realReferencia[i][k] = datosReferencia[i][k];
                    datosReferencia[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
                    datosReferencia[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();
                }
            }
        }
    }


    public void ejecutar(int[] outliers, int[] ExamplesClass) {
	
        int i;
        int nClases;
        String cadena = "";

        /*Getting the number of differents classes*/
        nClases = 0;
        for (i = 0; i < clasesTrain.length; i++) {
            if (clasesTrain[i] > nClases) {
                nClases = clasesTrain[i];
            }
        }
        nClases++;

        int salidaKNN[][];
        int prediccion[][];

        /*Output of the training file*/
        for (i = 0; i < datosTrain.length; i++) {
            ExamplesClass[clasesTrain[i]]++;
            if (KNN.differentClass(k, clasesTrain[i], datosReferencia, datosTrain[i]) >= this.k_dif) {
                outliers[clasesTrain[i]]++;
            }
        }
    }


    public void configuracion(int value_k) {
        /*Getting the number of neighbors*/
        this.k = value_k;
        this.k_dif = (int) (this.k * 0.8);

    }

    /* STATIC Methods */
    public static int differentClass(int nvec, int classE,double conj[][],double ejemplo[]) {

        int i, j, l;
        boolean parar = false;

        int vecinosCercanos[];
        double minDistancias[];
        int different;
        double dist;

        if (nvec > conj.length) {
            nvec = conj.length;
        }

        vecinosCercanos = new int[nvec];
        minDistancias = new double[nvec];
		
        for (i = 0; i < nvec; i++) {
            vecinosCercanos[i] = -1;
            minDistancias[i] = Double.POSITIVE_INFINITY;
        }

        for (i = 0; i < conj.length; i++) {
            dist = distancia(conj[i], ejemplo);
			
            if (dist > 0) {
                parar = false;
                for (j = 0; j < nvec && !parar; j++) {
                    if (dist < minDistancias[j]) {
                        parar = true;
                        for (l = nvec - 1; l >= j + 1; l--) {
                            minDistancias[l] = minDistancias[l - 1];
                            vecinosCercanos[l] = vecinosCercanos[l - 1];
                        }
                        minDistancias[j] = dist;
                        vecinosCercanos[j] = i;
                    }
                }
            }
        }

        different = 0;
        for (j = 0; j < nvec; j++) {
            if (vecinosCercanos[j] != classE) {
                different++;
            }
        }

        return (different);
    }


    public static double distancia(double ej1[], double ej2[]) {
        int i;
        double suma = 0;

        for (i = 0; i < ej1.length; i++) {
            suma += (ej1[i] - ej2[i]) * (ej1[i] - ej2[i]);
        }
        suma = Math.sqrt(suma);

        return suma;
    }

}


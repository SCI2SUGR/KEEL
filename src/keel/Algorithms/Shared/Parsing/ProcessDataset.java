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
* @author Written by Luciano Sánchez (University of Oviedo) 15/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

// Wrapper for KEEL's Dataset class
package keel.Algorithms.Shared.Parsing;


import java.io.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;

import keel.Dataset.*;

public class ProcessDataset {
   /**
	 * <p> 
	 * Wrapper for KEEL's Dataset class.
	 * </p>
	 */
	 //Input examples
     private double[][] X = null;
     //Missing examples
     private boolean[][] missing = null;
     //Output results
     private double[] Y = null;
     //Classes
     private int [] C = null;
     //Maximum input value for each variable
     private double[] iMaximum;
     //Minimum input value for each variable
     private double[] iMinimum;
     //Maximum output value
     private double oMaximum;
     //Maximum output value
     private double oMinimum;

     private int nData; 		// Number of examples     
     private int nVariables; 	// Number of variables
     private int nInputs;  		// Number of inputs
     private int nClasses;    	// Number of classes

    
    final static boolean debug = false;
    
    /** 
     * <p> 
     * Returns input examples. 
     * 
     * </p> 
     * @return vector with input examples.
     */
    public double[][] getX() {
        return X;
    }

    /** 
     * <p> 
     * Returns input examples. 
     * 
     * </p> 
     * @return vector with input examples.
     */
    public double[] getY() {
        return Y;
    }
    /** 
     * <p> 
     * Returns classes for classification problems. 
     * 
     * </p> 
     * @return vector with classes.
     */
    public int[] getC() {
        return C;
    }
    /** 
     * <p> 
     * Returns maximum value for each variable. 
     * 
     * </p> 
     * @return vector maximum value for each variable.
     */
    public double[] getImaximum() {
        return iMaximum;
    }
    /** 
     * <p> 
     * Returns maximum value for each variable. 
     * 
     * </p> 
     * @return vector maximum value for each variable.
     */
    public double[] getIminimum() {
        return iMinimum;
    }
    /** 
     * <p> 
     * Returns maximum value for output. 
     * 
     * </p> 
     * @return maximum value for output.
     */
    public double getOmaximum() {
        return oMaximum;
    }
    /** 
     * <p> 
     * Returns maximum value for output. 
     * 
     * </p> 
     * @return maximum value for output.
     */
    public double getOminimum() {
        return oMinimum;
    }
    /** 
     * <p> 
     * Returns the size of input data. 
     * 
     * </p> 
     * @return the size of input data.
     */
    public int getNdata() {
        return nData;
    }
    /** 
     * <p> 
     * Returns the number of input variables plus output variables. 
     * 
     * </p> 
     * @return the number of variables.
     */
    public int getNvariables() {
        return nVariables;
    }
    /** 
     * <p> 
     * Returns the number of input variables. 
     * 
     * </p> 
     * @return the number of input variables.
     */
    public int getNinputs() {
        return nInputs;
    }
    /** 
     * <p> 
     * Returns the number of classes for classification problems. 
     * 
     * </p> 
     * @return the number of classes.
     */
    public int getNclasses() {
        return nClasses;
    }
    /** 
     * <p> 
     * Returns if an example is missing. 
     * 
     * </p> 
     * @return true is the value is missing (0 in the table); 0 otherwise;
     */
  
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }
  //Data read for Keel Format file.
    private InstanceSet IS;

    /** 
     * <p>
     * A constructor that inits a new set of instances
     * 
     * </p>
     */
    public ProcessDataset() {

        // Init a new set of instances
        IS = new InstanceSet();
    }
    /** 
     * <p>
     * Process a dataset file for a classification problem.
     * 
     * </p>
     * @param nfejemplos Name of the dataset file
     * @param train The dataset file is for training or for test
     *
     */
    public void processClassifierDataset(String nfejemplos, boolean train) throws
            IOException {

        try {

            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfejemplos, train);

            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVariables = nInputs + Attributes.getOutputNumAttributes();

            // Check that there is only one output variable and
            // it is nominal

            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
            }

            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
            }

            // Initialize and fill our own tables
            X = new double[nData][nInputs];
            missing = new boolean[nData][nInputs];
            C = new int[nData];

            // Maximum and minimum of inputs
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            // Maximum and minimum for output data
            oMaximum = 0;
            oMinimum = 0;

            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < X.length; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    C[i] = 0;
                } else {
                    C[i] = (int) IS.getOutputNumericValue(i, 0);
                }
                if (C[i] > nClasses) {
                    nClasses = C[i];
                }
            }
            nClasses++;
            System.out.println("Number of classes=" + nClasses);

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }

    }
    /** 
     * <p>
     * Process a dataset file for a modelling problem.
     * 
     * </p>
     * @param nfexamples Name of the dataset file
     * @param train The dataset file is for training or for test
     *
     */

    public void processModelDataset(String nfexamples, boolean train) throws
            IOException {

        try {

            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfexamples, train);

            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVariables = nInputs + Attributes.getOutputNumAttributes();

            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
            }

            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
            }

            // Initialize and fill our own tables
            X = new double[nData][nInputs];
            missing = new boolean[nData][nInputs];
            Y = new double[nData];

            // Maximum and minimum of inputs
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            // Maximum and minimum for output data
            oMaximum = 0;
            oMinimum = 0;

            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < X.length; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    Y[i] = 0;
                } else {
                    Y[i] = IS.getOutputNumericValue(i, 0);
                }
                if (Y[i] > oMaximum || i == 0) {
                    oMaximum = Y[i];
                }
                if (Y[i] < oMinimum || i == 0) {
                    oMinimum = Y[i];
                }
            }

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }

    }
    /** 
     * <p>
     * Process a dataset file for a clustering problem.
     * 
     * </p>
     * @param nfexamples Name of the dataset file
     * @param train The dataset file is for training or for test
     *
     */

    public void processClusterDataset(String nfexamples, boolean train) throws
            IOException {

        try {

            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfexamples, train);

            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVariables = nInputs + Attributes.getOutputNumAttributes();

            if (Attributes.getOutputNumAttributes() != 0) {
                System.out.println(
                        "This algorithm can not process datasets with outputs");
                System.out.println("All outputs will be removed");
            }

            // Initialize and fill our own tables
            X = new double[nData][nInputs];
            missing = new boolean[nData][nInputs];

            // Maximum and minimum of inputs
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            // Maximum and minimum for output data
            oMaximum = 0;
            oMinimum = 0;

            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < X.length; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }

    }

    /** 
     * <p>
     * Process a old format dataset file for a modelling problem.
     * 
     * </p>
     * @param nfejemplos Name of the dataset file
     *
     */

    public void oldClassificationProcess(String nfejemplos) {

        // Dataset reading for modelling problems
        try {

            String line;

            BufferedReader in = new BufferedReader(new FileReader(nfejemplos));
            line = in.readLine();
            nData = Integer.parseInt(line);
            line = in.readLine();
            nVariables = Integer.parseInt(line);
            nInputs = nVariables - 1;

            X = new double[nData][nInputs];
            Y = new double[nData];
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            oMaximum = 0; // Maximum and minimum for output data
            oMinimum = 0;

            for (int i = 0; i < nData; i++) {

                line = in.readLine();
                StringTokenizer tokens = new StringTokenizer(line, " ,\t");
                for (int j = 0; j < nInputs; j++) {
                    String tmp = tokens.nextToken();
                    X[i][j] = Double.parseDouble(tmp);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }
                Y[i] = Double.parseDouble(tokens.nextToken());
                if (Y[i] > oMaximum || i == 0) {
                    oMaximum = Y[i];
                }
                if (Y[i] < oMinimum || i == 0) {
                    oMinimum = Y[i];
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(e + " Fichero de ejemplos no encontrado");
        } catch (IOException e) {
            System.err.println(e + " Error lectura");
        }

    }
    /** 
     * <p>
     * Process an old format dataset file for a classification problem.
     * 
     * </p>
     * @param nfejemplos Name of the dataset file.
     *
     */
    public void oldClusteringProcess(String nfejemplos) {

        // Dataset reading for modelling problems

        try {

            String line;

            BufferedReader in = new BufferedReader(new FileReader(nfejemplos));
            line = in.readLine();
            nData = Integer.parseInt(line);
            line = in.readLine();
            nVariables = Integer.parseInt(line);
            nInputs = nVariables - 1;

            X = new double[nData][nInputs];
            C = new int[nData];
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            int cMaximum = 0; // Maximum and minimum for output data
            int cMinimum = 0;

            for (int i = 0; i < nData; i++) {

                line = in.readLine();
                StringTokenizer tokens = new StringTokenizer(line, " ,\t");
                for (int j = 0; j < nInputs; j++) {
                    String tmp = tokens.nextToken();
                    X[i][j] = Double.parseDouble(tmp);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }
                C[i] = Integer.parseInt(tokens.nextToken());
                if (C[i] > cMaximum || i == 0) {
                    cMaximum = C[i];
                }
                if (C[i] < cMinimum || i == 0) {
                    cMinimum = C[i];
                }
            }

            if (cMaximum == cMinimum) {
                throw new IOException("0 clases");
            }

            nClasses = cMaximum - cMinimum + 1;

            // It enumerates classes from 0
            for (int i = 0; i < nData; i++) {
                C[i] = (C[i] - cMinimum) / (cMaximum - cMinimum);
            }

        } catch (FileNotFoundException e) {
            System.err.println(e + " Fichero de ejemplos no encontrado");
        } catch (IOException e) {
            System.err.println(e + " Error lectura");
        }

    }
    /** 
     * <p>
     * Process an old format dataset file for a clustering problem.
     * 
     * </p>
     * @param nfejemplos Name of the dataset file.
     *
     */

    public void procesa_clustering_old(String nfejemplos) {
        // Dataset reading for clustering problems

        try {

            String line;

            BufferedReader in = new BufferedReader(new FileReader(nfejemplos));
            line = in.readLine();
            nData = Integer.parseInt(line);
            line = in.readLine();
            nVariables = Integer.parseInt(line);
            nInputs = nVariables;

            X = new double[nData][nInputs];
            iMaximum = new double[nInputs];
            iMinimum = new double[nInputs];

            for (int i = 0; i < nData; i++) {

                line = in.readLine();
                StringTokenizer tokens = new StringTokenizer(line, " ,\t");
                for (int j = 0; j < nInputs; j++) {
                    String tmp = tokens.nextToken();
                    X[i][j] = Double.parseDouble(tmp);
                    if (X[i][j] > iMaximum[j] || i == 0) {
                        iMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < iMinimum[j] || i == 0) {
                        iMinimum[j] = X[i][j];
                    }
                }

            }

        } catch (FileNotFoundException e) {
            System.err.println(e + " Fichero de ejemplos no encontrado");
        } catch (IOException e) {
            System.err.println(e + " Error lectura");
        }

    }

    /** 
     * <p>
     * prints to standard output statistics about the dataset.
     * 
     * </p>
     * 
     */
    
    public void showDatasetStatistics() {

        double sumaX[] = new double[X[0].length];
        double sumaY = 0;
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[i].length; j++) {
                sumaX[j] += X[i][j];
            }
            if (Y != null) {
                sumaY += Y[i];
            }
        }
        for (int j = 0; j < X[0].length; j++) {
            sumaX[j] /= X.length;
        }
        if (Y != null) {
            sumaY /= Y.length;
        }

        System.out.print("Mean of inputs: ");
        for (int j = 0; j < X[0].length; j++) {
            System.out.print(sumaX[j] + " ");
        }
        System.out.println();

        if (Y != null) {
            System.out.println("Mean of outputs: " + sumaY);
        }

    }

}


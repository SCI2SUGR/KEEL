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
 * File: svmClassifierCost.java
 *
 * This class is a wrapper to the LibSVM C-SVM classifier, in order to operate with KEEL 
 * data sets and parameters. The implementation has been adapted to deal with imbalanced
 * classification problems.
 *  
 * @author Written by Julian Luengo Martin 09/10/2007
 * @author Modified by Victoria Lopez Morales 01/05/2010
 * @author Modified by Victoria Lopez Morales 05/10/2010 
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.ImbalancedClassification.CSMethods.C_SVMCost;

import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;

/**
 * <p>
 * This class is a wrapper to the LibSVM C-SVM classifier, in order to operate with KEEL data sets and parameters.
 * </p>
 */
public class svmClassifierCost {
    double[] mean = null;
    double[] std_dev = null;
    double tempData = 0;

    String[][] X = null; // matrix of transformed data

    // values
    String[] mostCommon;

    int ndatos = 0;
    int nentradas = 0;
    int tipo = 0;
    int direccion = 0;
    int nvariables = 0;
    int nsalidas = 0;
    int nneigh = 1; // number of neighbours

    InstanceSet IS;
    InstanceSet ISval;
    String input_train_name = new String();
    String input_validation_name;
    String input_test_name = new String();

    String output_train_name = new String();
    String output_test_name = new String();

    String temp = new String();
    String data_out = new String("");

    String svmType;
    String kernelType;
    double C;
    double eps;
    int degree;
    double gamma;
    double coef0;
    double nu;
    double p;
    int shrinking;
    int probability = 0;
    long seed;
    int nr_weight = 0;
    
    /** Creates a new instance of svmClassifier
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public svmClassifierCost(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
        ISval = new InstanceSet();
    }

    // Write data matrix X to disk, in KEEL format
    private void write_results(String output, int positive_class) {
        // File OutputFile = new File(output_train_name.substring(1,
        // output_train_name.length()-1));
    	/*int tp = 0;
    	int tn = 0;
    	int fp = 0;
    	int fn = 0;
    	double tp_rate, fp_rate, auc;*/
    	
        try {
            FileWriter file_write = new FileWriter(output);

            file_write.write(IS.getHeader());
            
            // now, print the normalized data
            file_write.write("@data\n");
            for (int i = 0; i < ndatos; i++) {
                file_write.write(X[i][0]);
                for (int j = 1; j < 2; j++) {
                    file_write.write(" " + X[i][j]);
                }
                file_write.write("\n");
                
                /*int aux;
                if (Character.isDigit(X[i][0].charAt(0))) {
                	aux = Integer.parseInt(X[i][0]);
                }
                else {
                	if (X[i][0].contains("positive")) {
                		aux = positive_class;
                	}
                	else {
                		aux = positive_class+1;
                	}
                }
                
                if (X[i][0].equals(X[i][1])) {
                	if (aux == positive_class) {
                		tp++;
                	}
                	else {
                		tn++;
                	}
                }
                else {
                	if (aux == positive_class) {
                		fn++;
                	}
                	else {
                		fp++;
                	}
                }*/
            }
            
            /*tp_rate = (double)tp/(double)(tp+fn);
            fp_rate = (double)fp/(double)(fp+tn);
            
            auc = (1+tp_rate-fp_rate)/2;
            
            System.out.println("TP: " + tp + " TN: " + tn + " FP: " + fp + " FN: " + fn + " Area Under the ROC Curve is "+auc);*/
            
            file_write.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
            System.exit( -1);
        }
    }

    private void config_read(String fileParam) {
        parseParameters parameters;
        parameters = new parseParameters();
        parameters.parseConfigurationFile(fileParam);
        input_train_name = parameters.getTrainingInputFile();
        input_validation_name = parameters.getValidationInputFile();
        input_test_name = parameters.getTestInputFile();

        output_train_name = parameters.getTrainingOutputFile();
        output_test_name = parameters.getTestOutputFile();

        seed = Long.parseLong(parameters.getParameter(0));
        kernelType = parameters.getParameter(1);
        C = Double.parseDouble(parameters.getParameter(2));
        eps = Double.parseDouble(parameters.getParameter(3));
        degree = Integer.parseInt(parameters.getParameter(4));
        gamma = Double.parseDouble(parameters.getParameter(5));
        coef0 = Double.parseDouble(parameters.getParameter(6));
        nu = Double.parseDouble(parameters.getParameter(7));
        p = Double.parseDouble(parameters.getParameter(8));
        shrinking = Integer.parseInt(parameters.getParameter(9));

    }

    /**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
    public void process() {
        double[] outputs;
        double[] outputs2;
        Instance neighbor;
        double dist, mean;
        int actual;
        int[] N = new int[nneigh];
        double[] Ndist = new double[nneigh];
        boolean allNull;
        svm_problem SVMp = null;
        svm_parameter SVMparam = new svm_parameter();
        svm_model svr = null;
        svm_node SVMn[];
        double[] outputsCandidate = null;
        boolean same = true;
        Vector instancesSelected = new Vector();
        Vector instancesSelected2 = new Vector();
        int n_pos = 0;
        int n_neg = 0;
        int positive_class = -1;
        double positive_cost, negative_cost;

        //SVM PARAMETERS
        SVMparam.C = C;
        SVMparam.cache_size = 10; //10MB of cache
        SVMparam.degree = degree;
        SVMparam.eps = eps;
        SVMparam.gamma = gamma;
        SVMparam.nr_weight = 0;
        SVMparam.nu = nu;
        SVMparam.p = p;
        SVMparam.shrinking = shrinking;
        SVMparam.probability = 0;
        if (kernelType.compareTo("LINEAR") == 0) {
            SVMparam.kernel_type = svm_parameter.LINEAR;
        } else if (kernelType.compareTo("POLY") == 0) {
            SVMparam.kernel_type = svm_parameter.POLY;
        } else if (kernelType.compareTo("RBF") == 0) {
            SVMparam.kernel_type = svm_parameter.RBF;
        } else if (kernelType.compareTo("SIGMOID") == 0) {
            SVMparam.kernel_type = svm_parameter.SIGMOID;
        }

        //if(svmType.compareTo("C_SVC")==0){
        SVMparam.svm_type = svm_parameter.C_SVC;
        /*}else if(svmType.compareTo("NU_SVC")==0){
         SVMparam.svm_type = svm_parameter.NU_SVC;
           }*/

        try {

            // Load in memory a dataset that contains a classification problem
            IS.readSet(input_train_name, true);
            int in = 0;
            int out = 0;

            ndatos = IS.getNumInstances();
            nvariables = Attributes.getNumAttributes();
            nentradas = Attributes.getInputNumAttributes();
            nsalidas = Attributes.getOutputNumAttributes();

            X = new String[ndatos][2]; // matrix with transformed data

            mostCommon = new String[nvariables];
            SVMp = new svm_problem();
            SVMp.l = ndatos;
            SVMp.y = new double[SVMp.l];
            SVMp.x = new svm_node[SVMp.l][nentradas + 1];
            for (int l = 0; l < SVMp.l; l++) {
                for (int n = 0; n < Attributes.getInputNumAttributes() + 1; n++) {
                    SVMp.x[l][n] = new svm_node();
                }
            }

            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                
                SVMp.y[i] = inst.getAllOutputValues()[0];
                if (SVMp.y[i] == 0.0) {
                	n_pos++;
                	positive_class = 0;
                }
                else {
                	n_neg++;
                	positive_class = (int)SVMp.y[i];
                }

                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMp.x[i][n].index = n;
                    SVMp.x[i][n].value = inst.getAllInputValues()[n];
                    SVMp.y[i] = inst.getAllOutputValues()[0];
                }
                //end of instance
                SVMp.x[i][nentradas].index = -1;
            }
            if (n_pos > n_neg) {
            	int tmp = n_pos;
            	n_pos = n_neg;
            	n_neg = n_pos;
            }
            
            // Add the costs to the SVM mechanism
            positive_cost = ((double)n_neg/(double)n_pos);
            negative_cost = 1;
            
            SVMparam.nr_weight = 2;
            SVMparam.weight = new double[SVMparam.nr_weight];
            
            for (int a=0; a<SVMparam.nr_weight; a++) {
            	if (a == positive_class) {
            		SVMparam.weight[a] = positive_cost;
            	}
            	else {
            		SVMparam.weight[a] = negative_cost;
            	}
            }
            
            if (svm.svm_check_parameter(SVMp, SVMparam) != null) {
                System.err.print("SVM parameter error in training: ");
                System.err.println(svm.svm_check_parameter(SVMp, SVMparam));
                System.exit( -1);
            }
            
            //train the SVM
            if (ndatos > 0) {
                svr = svm.svm_train(SVMp, SVMparam);
            }
            ISval.readSet(input_validation_name, false);

            ndatos = ISval.getNumInstances();
            nvariables = Attributes.getNumAttributes();
            nentradas = Attributes.getInputNumAttributes();
            nsalidas = Attributes.getOutputNumAttributes();

            // We allocate again the matrix with the data to allocate the validation set (it can be larger than the original training set)
            X = new String[ndatos][2]; // matrix with transformed data
            
            for (int i = 0; i < ISval.getNumInstances(); i++) {
                Instance inst = ISval.getInstance(i);
                Attribute a = Attributes.getOutputAttribute(0);

                direccion = a.getDirectionAttribute();
                tipo = a.getType();
                if (tipo != Attribute.NOMINAL) {
                    X[i][0] = new String(""+(int) ISval.getOutputNumericValue(i, 0));
                    //new String(String.valueOf((int) inst.getAllOutputValues()[0]));
                } else {
                    X[i][0] = ISval.getOutputNominalValue(i, 0); //new String(inst.getOutputNominalValues(0));
                }

//			the values used for regression
                SVMn = new svm_node[Attributes.getInputNumAttributes() + 1];
                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMn[n] = new svm_node();
                    SVMn[n].index = n;
                    SVMn[n].value = inst.getAllInputValues()[n];
                }
                SVMn[nentradas] = new svm_node();
                SVMn[nentradas].index = -1;
                //predict the class
                if (tipo != Attribute.NOMINAL) {
                    X[i][1] = new String(String.valueOf((int) Math.round(svm.
                            svm_predict(svr, SVMn))));
                } else {
                    X[i][1] = new String(a.getNominalValue((int) Math.round(svm.
                            svm_predict(svr, SVMn))));
                }
            }
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            e.printStackTrace();
            System.exit( -1);
        }
        write_results(output_train_name, positive_class);
        /** ************************************************************************************ */
        try {

            // Load in memory a dataset that contains a classification
            // problem
            IS.readSet(input_test_name, false);
            int in = 0;
            int out = 0;

            ndatos = IS.getNumInstances();
            nvariables = Attributes.getNumAttributes();
            nentradas = Attributes.getInputNumAttributes();
            nsalidas = Attributes.getOutputNumAttributes();

            X = new String[ndatos][2]; // matrix with transformed data
            // data

            mostCommon = new String[nvariables];

            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                Attribute a = Attributes.getOutputAttribute(0);

                direccion = a.getDirectionAttribute();
                tipo = a.getType();
                if (tipo != Attribute.NOMINAL) {
                    X[i][0] = new String(""+(int) IS.getOutputNumericValue(i, 0));
                    //new String(String.valueOf((int) inst.getAllOutputValues()[0]));
                } else {
                    X[i][0] = IS.getOutputNominalValue(i, 0); //new String(inst.getOutputNominalValues(0));
                }

                SVMn = new svm_node[Attributes.getInputNumAttributes() + 1];
                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMn[n] = new svm_node();
                    SVMn[n].index = n;
                    SVMn[n].value = inst.getAllInputValues()[n];
                }
                SVMn[nentradas] = new svm_node();
                SVMn[nentradas].index = -1;
                //pedict the class
                if (tipo != Attribute.NOMINAL) {
                    X[i][1] = new String(String.valueOf((int) Math.round(svm.
                            svm_predict(svr, SVMn))));
                } else {
                    X[i][1] = new String(a.getNominalValue((int) Math.round(svm.
                            svm_predict(svr, SVMn))));
                }
            }
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            e.printStackTrace();
            System.exit( -1);
        }
        write_results(output_test_name, positive_class);
    }

}

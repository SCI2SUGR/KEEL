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
 * @author Written by Julián Luengo Martín 09/10/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.SVM.EPSILON_SVR;

import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;
import org.libsvm.*;

/**
 * <p>
 * This class is a wrapper to the LibSVM eps-SVR regression, in order to operate with KEEL data sets and parameters.
 * </p>
 */
public class svmRegression {

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

    String input_train_name = new String();
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


    /** Creates a new instance of svmRegression
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
    public svmRegression(String fileParam) {
        config_read(fileParam);
        IS = new InstanceSet();
    }

    // Write data matrix X to disk, in KEEL format
    private void write_results(String output) {
        // File OutputFile = new File(output_train_name.substring(1,
        // output_train_name.length()-1));
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
            }
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
        //input_validation_name = parameters.getValidationInputFile();
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

        SVMparam.svm_type = svm_parameter.EPSILON_SVR;

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
                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMp.x[i][n].index = n;
                    SVMp.x[i][n].value = inst.getAllInputValues()[n];
                    SVMp.y[i] = inst.getAllOutputValues()[0];
                }
                //end of instance
                SVMp.x[i][nentradas].index = -1;
            }
            if (svm.svm_check_parameter(SVMp, SVMparam) != null) {
                System.out.println("SVM parameter error in training:");
                System.out.println(svm.svm_check_parameter(SVMp, SVMparam));
                System.exit( -1);
            }
            //train the SVM
            if (ndatos > 0) {
                svr = svm.svm_train(SVMp, SVMparam);
            }
            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                X[i][0] = new String(String.valueOf(inst.getAllOutputValues()[0]));
//			the values used for regression
                SVMn = new svm_node[Attributes.getInputNumAttributes() + 1];
                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMn[n] = new svm_node();
                    SVMn[n].index = n;
                    SVMn[n].value = inst.getAllInputValues()[n];
                }
                SVMn[nentradas] = new svm_node();
                SVMn[nentradas].index = -1;
                //pedict the class
                X[i][1] = new String(String.valueOf((svm.svm_predict(svr, SVMn))));
            }
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            e.printStackTrace();
            System.exit( -1);
        }
        write_results(output_train_name);
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
                X[i][0] = new String(String.valueOf(inst.getAllOutputValues()[0]));

                SVMn = new svm_node[Attributes.getInputNumAttributes() + 1];
                for (int n = 0; n < Attributes.getInputNumAttributes(); n++) {
                    SVMn[n] = new svm_node();
                    SVMn[n].index = n;
                    SVMn[n].value = inst.getAllInputValues()[n];
                }
                SVMn[nentradas] = new svm_node();
                SVMn[nentradas].index = -1;
                //pedict the class
                X[i][1] = new String(String.valueOf(svm.svm_predict(svr, SVMn)));
            }
        } catch (Exception e) {
            System.out.println("Dataset exception = " + e);
            e.printStackTrace();
            System.exit( -1);
        }
        System.out.println("escribiendo test");
        write_results(output_test_name);
    }

}


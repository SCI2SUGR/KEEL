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
 * @author Written by Julián Luengo Martín 15/05/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.svmImpute;

import java.io.*;
import java.util.*;
import keel.Dataset.*;
import keel.Algorithms.Preprocess.Basic.*;
import org.libsvm.*;

/**
 * <p>
 * This class imputes the missing values by means of the SVM regression. It transforms the current data set, so the missing value is the output 
 * attribute to be predicted, and all other are the input values used for the regression.
 * </p>
 */
public class svmImpute {

	double[] mean = null;

	double[] std_dev = null;

	double tempData = 0;

	String[][] X = null; // matrix of transformed data

	FreqList[] timesSeen = null; // matrix with frequences of attribute

	// values
	String[] mostCommon;

	int ndatos = 0;

	int nentradas = 0;

	int tipo = 0;

	int direccion = 0;

	int nvariables = 0;

	int nsalidas = 0;

	int nneigh = 1; // number of neighbours

	InstanceSet IS,IStest;

	String input_train_name = new String();

	String input_test_name = new String();

	String output_train_name = new String();

	String output_test_name = new String();

	String temp = new String();

	String data_out = new String("");
	
	String svrType;
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
	
	int nr_weight = 0;
	

	/** Creates a new instance of svmImpute
	 * @param fileParam The path to the configuration file with all the parameters in KEEL format
	 */
	public svmImpute(String fileParam) {
		config_read(fileParam);
		IS = new InstanceSet();
		IStest = new InstanceSet();
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
				for (int j = 1; j < nvariables; j++) {
					file_write.write("," + X[i][j]);
				}
				file_write.write("\n");
			}
			file_write.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			System.exit(-1);
		}
	}

	// Read the pattern file, and parse data into strings
	private void config_read(String fileParam) {
		File inputFile = new File(fileParam);

		if (inputFile == null || !inputFile.exists()) {
			System.out.println("parameter " + fileParam
					+ " file doesn't exists!");
			System.exit(-1);
		}
		// begin the configuration read from file
		try {
			FileReader file_reader = new FileReader(inputFile);
			BufferedReader buf_reader = new BufferedReader(file_reader);
			// FileWriter file_write = new FileWriter(outputFile);

			String line;

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0); // avoid empty lines for processing
											// ->
			// produce exec failure
			String out[] = line.split("algorithm = ");
			// alg_name = new String(out[1]); //catch the algorithm name
			// input & output filenames
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("inputData = ");
			out = out[1].split("\\s\"");
			input_train_name = new String(out[0].substring(1,
					out[0].length() - 1));
			input_test_name = new String(out[1].substring(0,
					out[1].length() - 1));
			if (input_test_name.charAt(input_test_name.length() - 1) == '"')
				input_test_name = input_test_name.substring(0, input_test_name
						.length() - 1);

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("outputData = ");
			out = out[1].split("\\s\"");
			output_train_name = new String(out[0].substring(1,
					out[0].length() - 1));
			output_test_name = new String(out[1].substring(0,
					out[1].length() - 1));
			if (output_test_name.charAt(output_test_name.length() - 1) == '"')
				output_test_name = output_test_name.substring(0,
						output_test_name.length() - 1);

			// parameters
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("SVRtype = ");
			svrType = (new String(out[1])); 

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("KERNELtype = ");
			kernelType = (new String(out[1])); 
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("C = ");
			C = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("eps = ");
			eps = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("degree = ");
			degree = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("gamma = ");
			gamma = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("coef0 = ");
			coef0 = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("nu = ");
			nu = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("p = ");
			p = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("shrinking = ");
			shrinking = (new Integer(out[1])).intValue(); // parse the string into
			file_reader.close();

		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
     * <p>
     * Computes the distance between two instances (without previous normalization)
     * </p>
     * @param i First instance 
     * @param j Second instance
     * @return The Euclidean distance between i and j
     */
	private double distance(Instance i, Instance j) {
		double dist = 0;
		int in = 0;
		int out = 0;

		for (int l = 0; l < nvariables; l++) {
			Attribute a = Attributes.getAttribute(l);

			direccion = a.getDirectionAttribute();
			tipo = a.getType();

			if (direccion == Attribute.INPUT) {
				if (tipo != Attribute.NOMINAL && !i.getInputMissingValues(in)) {
					// real value, apply euclidean distance
					dist += (i.getInputRealValues(in) - j
							.getInputRealValues(in))
							* (i.getInputRealValues(in) - j
									.getInputRealValues(in));
				} else {
					if (!i.getInputMissingValues(in)
							&& i.getInputNominalValues(in) != j
									.getInputNominalValues(in))
						dist += 1;
				}
				in++;
			} else {
				if (direccion == Attribute.OUTPUT) {
					if (tipo != Attribute.NOMINAL
							&& !i.getOutputMissingValues(out)) {
						dist += (i.getOutputRealValues(out) - j
								.getOutputRealValues(out))
								* (i.getOutputRealValues(out) - j
										.getOutputRealValues(out));
					} else {
						if (!i.getOutputMissingValues(out)
								&& i.getOutputNominalValues(out) != j
										.getOutputNominalValues(out))
							dist += 1;
					}
					out++;
				}
			}
		}
		return Math.sqrt(dist);
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
		svm_problem SVMp2 = null;
		svm_parameter SVMparam= new svm_parameter();
		svm_model svr;
		svm_node SVMn[];
		double[] outputsCandidate = null;
		boolean same = true;
		Vector instancesSelected = new Vector();
		Vector instancesSelected2 = new Vector();
		Vector instancesSelected3 = new Vector();

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
		if(kernelType.compareTo("LINEAR")==0){
			SVMparam.kernel_type = svm_parameter.LINEAR;
		}else if(kernelType.compareTo("POLY")==0){
			SVMparam.kernel_type = svm_parameter.POLY;
		}else if(kernelType.compareTo("RBF")==0){
			SVMparam.kernel_type = svm_parameter.RBF;
		}else if(kernelType.compareTo("SIGMOID")==0){
			SVMparam.kernel_type = svm_parameter.SIGMOID;
		}
		
		if(svrType.compareTo("EPSILON_SVR")==0){
			SVMparam.svm_type = svm_parameter.EPSILON_SVR;
		}else if(svrType.compareTo("NU_SVR")==0){
			SVMparam.svm_type = svm_parameter.NU_SVR;
		}
		
		try {

			// Load in memory a dataset that contains a classification problem
			IS.readSet(input_train_name, true);
			int in = 0;
			int out = 0;

			ndatos = IS.getNumInstances();
			nvariables = Attributes.getNumAttributes();
			nentradas = Attributes.getInputNumAttributes();
			nsalidas = Attributes.getOutputNumAttributes();

			X = new String[ndatos][nvariables];// matrix with transformed data

			timesSeen = new FreqList[nvariables];
			mostCommon = new String[nvariables];
			
			for (int i = 0; i < ndatos; i++) {
				Instance inst = IS.getInstance(i);

				in = 0;
				out = 0;
				if (inst.existsAnyMissingValue()) {
					// search for same class instances
					// and construct a data set with the instances
					SVMp = new svm_problem();
					outputs = inst.getAllOutputValues();
					SVMp.l = 0;
					instancesSelected.clear(); // erase elements from previous
					// work
					instancesSelected3.clear();
					for (int k = 0; k < ndatos; k++) {
						// select those instances with same class and no MVs
						if (k != i && !IS.getInstance(k).existsAnyMissingValue()) {
							// compare outputs
							outputsCandidate = IS.getInstance(k).getAllOutputValues();

							same = true;
							for (int n = 0; n < Attributes.getOutputNumAttributes()	&& same; n++)
								if (outputsCandidate[n] != outputs[n])
									same = false; // not a same class instance

							if (same) { // if same class instance, get it for
								// later computations
								SVMp.l++;
								instancesSelected.addElement(IS.getInstance(k));
							}
							instancesSelected3.addElement(IS.getInstance(k));
						}
					}
				}
				for (int j = 0; j < nvariables; j++) {
					Attribute a = Attributes.getAttribute(j);

					direccion = a.getDirectionAttribute();
					tipo = a.getType();

					if (direccion == Attribute.INPUT) {
						if (tipo != Attribute.NOMINAL
								&& !inst.getInputMissingValues(in)) {
							X[i][j] = new String(String.valueOf(inst
									.getInputRealValues(in)));
						} else {
							if (!inst.getInputMissingValues(in))
								X[i][j] = inst.getInputNominalValues(in);
							else {
								if (instancesSelected.size() != 0) {
									//construct the training data for SVM
									SVMp.y = new double[SVMp.l];
									SVMp.x = new svm_node[SVMp.l][Attributes.getOutputNumAttributes()];
									for(int l=0;l<SVMp.l;l++)
										for(int n=0;n<Attributes.getOutputNumAttributes();n++)
											SVMp.x[l][n] = new svm_node();

									for (int k = 0; k < instancesSelected.size(); k++) {
										neighbor = (Instance) instancesSelected.elementAt(k);
										if (tipo != Attribute.NOMINAL) {
											SVMp.y[k] = neighbor.getInputRealValues(in);
											for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
												SVMp.x[k][n].index = n;
												SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
											}

										}else{
											SVMp.y[k] = neighbor.getInputNominalValuesInt(in);
											for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
												SVMp.x[k][n].index = n;
												SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
											}
										}
									}
									//check if parameters are OK
									if(svm.svm_check_parameter(SVMp, SVMparam)!=null){
										System.out.println("SVM parameter error:");
										System.out.println(svm.svm_check_parameter(SVMp, SVMparam));
										System.exit(-1);
									}
									//the values used for regression
									SVMn = new svm_node[Attributes.getOutputNumAttributes()];
									for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
										SVMn[n] = new svm_node();
										SVMn[n].index = n;
										SVMn[n].value = inst.getAllOutputValues()[n];
									}
									//at last, impute
									svr = svm.svm_train(SVMp, SVMparam);

									mean = svm.svm_predict(svr, SVMn);
									if (tipo != Attribute.NOMINAL) {
										if (tipo == Attribute.INTEGER)
											mean = new Double(mean + 0.5).intValue();
										X[i][j] = new String(String.valueOf(mean));
									}else{
										mean = new Double(mean + 0.5).intValue();
										X[i][j] = a.getNominalValue((int)mean);
									}
								}else if (instancesSelected3.size() != 0) { //in the case of no complete instance of same class found
									//use all complete instances in the data set...
									//construct the training data for SVM
									SVMp.l = instancesSelected3.size();
									SVMp.y = new double[SVMp.l];
									SVMp.x = new svm_node[SVMp.l][Attributes.getOutputNumAttributes()];
									for(int l=0;l<SVMp.l;l++)
										for(int n=0;n<Attributes.getOutputNumAttributes();n++)
											SVMp.x[l][n] = new svm_node();

									for (int k = 0; k < instancesSelected3.size(); k++) {
										neighbor = (Instance) instancesSelected3.elementAt(k);
										if (tipo != Attribute.NOMINAL) {
											SVMp.y[k] = neighbor.getInputRealValues(in);
											for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
												SVMp.x[k][n].index = n;
												SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
											}

										}else{
											SVMp.y[k] = neighbor.getInputNominalValuesInt(in);
											for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
												SVMp.x[k][n].index = n;
												SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
											}
										}
									}
									//check if parameters are OK
									if(svm.svm_check_parameter(SVMp, SVMparam)!=null){
										System.out.println("SVM parameter error:");
										System.out.println(svm.svm_check_parameter(SVMp, SVMparam));
										System.exit(-1);
									}
									//the values used for regression
									SVMn = new svm_node[Attributes.getOutputNumAttributes()];
									for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
										SVMn[n] = new svm_node();
										SVMn[n].index = n;
										SVMn[n].value = inst.getAllOutputValues()[n];
									}
									//at last, impute
									svr = svm.svm_train(SVMp, SVMparam);

									mean = svm.svm_predict(svr, SVMn);
									if (tipo != Attribute.NOMINAL) {
										if (tipo == Attribute.INTEGER)
											mean = new Double(mean + 0.5).intValue();
										X[i][j] = new String(String.valueOf(mean));
									}else{
										mean = new Double(mean + 0.5).intValue();
										X[i][j] = a.getNominalValue((int)mean);
									}
								}else
									X[i][j] = "<null>";
							}
						}
						in++;
					} else {
						if (direccion == Attribute.OUTPUT) {
							if (tipo != Attribute.NOMINAL
									&& !inst.getOutputMissingValues(out)) {
								X[i][j] = new String(String.valueOf(inst
										.getOutputRealValues(out)));
							} else {
								if (!inst.getOutputMissingValues(out))
									X[i][j] = inst.getOutputNominalValues(out);
								else {
									SVMp2 = new svm_problem();
									outputs = inst.getAllInputValues();
									SVMp2.l = 0;
									instancesSelected2.clear(); // erase elements from previous
									// work
									instancesSelected3.clear(); // erase elements from previous iterations
									for (int k = 0; k < ndatos; k++) {
										// select those instances with same class and no MVs
										if (k != i && !IS.getInstance(k).existsAnyMissingValue()) {
											// compare outputs
											outputsCandidate = IS.getInstance(k).getAllInputValues();

											same = true;
											for (int n = 0; n < Attributes.getInputNumAttributes()&& same; n++)
												if (outputsCandidate[n] != outputs[n])
													same = false; // not a same class instance

											if (same) { // if same class instance, get it for
												// later computations
												SVMp2.l++;
												instancesSelected2.addElement(IS.getInstance(k));
											}
											//however, store the instance if not instances of same class
											//won't be found
											instancesSelected3.addElement(IS.getInstance(k));
										}
									}
									
									if (instancesSelected2.size() != 0) {
										//construct the training data for SVM
										SVMp2.y = new double[SVMp2.l];
										SVMp2.x = new svm_node[SVMp2.l][Attributes.getInputNumAttributes()];
										for(int l=0;l<SVMp2.l;l++)
											for(int n=0;n<Attributes.getInputNumAttributes();n++)
												SVMp2.x[l][n] = new svm_node();
										
										for (int k = 0; k < instancesSelected2.size(); k++) {
											neighbor = (Instance) instancesSelected2.elementAt(k);
											if (tipo != Attribute.NOMINAL) {
												SVMp2.y[k] = neighbor.getInputRealValues(in);
												for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
													SVMp2.x[k][n].index = n;
													SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
												}

											}else{
												SVMp2.y[k] = neighbor.getInputNominalValuesInt(in);
												for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
													SVMp2.x[k][n].index = n;
													SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
												}
											}
										}
										//check if parameters are OK
										if(svm.svm_check_parameter(SVMp2, SVMparam)!=null){
											System.out.println("SVM parameter error:");
											System.out.println(svm.svm_check_parameter(SVMp2, SVMparam));
											System.exit(-1);
										}
										//the values used for regression
										SVMn = new svm_node[Attributes.getInputNumAttributes()];
										for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
											SVMn[n] = new svm_node();
											SVMn[n].index = n;
											SVMn[n].value = inst.getAllInputValues()[n];
										}
										//at last, impute
										svr = svm.svm_train(SVMp2, SVMparam);

										mean = svm.svm_predict(svr, SVMn);
										if (tipo != Attribute.NOMINAL) {
											if (tipo == Attribute.INTEGER)
												mean = new Double(mean + 0.5).intValue();
											X[i][j] = new String(String.valueOf(mean));
										}else{
											mean = new Double(mean + 0.5).intValue();
											X[i][j] = a.getNominalValue((int)mean);
										}
									} else if (instancesSelected3.size() != 0) { //impute with instances of the rest of the classes
										//construct the training data for SVM
										SVMp2.l = instancesSelected3.size();
										SVMp2.y = new double[SVMp2.l];
										SVMp2.x = new svm_node[SVMp2.l][Attributes.getInputNumAttributes()];
										for(int l=0;l<SVMp2.l;l++)
											for(int n=0;n<Attributes.getInputNumAttributes();n++)
												SVMp2.x[l][n] = new svm_node();
										
										for (int k = 0; k < instancesSelected3.size(); k++) {
											neighbor = (Instance) instancesSelected3.elementAt(k);
											if (tipo != Attribute.NOMINAL) {
												SVMp2.y[k] = neighbor.getInputRealValues(in);
												for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
													SVMp2.x[k][n].index = n;
													SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
												}

											}else{
												SVMp2.y[k] = neighbor.getInputNominalValuesInt(in);
												for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
													SVMp2.x[k][n].index = n;
													SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
												}
											}
										}
										//check if parameters are OK
										if(svm.svm_check_parameter(SVMp2, SVMparam)!=null){
											System.out.println("SVM parameter error:");
											System.out.println(svm.svm_check_parameter(SVMp2, SVMparam));
											System.exit(-1);
										}
										//the values used for regression
										SVMn = new svm_node[Attributes.getInputNumAttributes()];
										for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
											SVMn[n] = new svm_node();
											SVMn[n].index = n;
											SVMn[n].value = inst.getAllInputValues()[n];
										}
										//at last, impute
										svr = svm.svm_train(SVMp2, SVMparam);

										mean = svm.svm_predict(svr, SVMn);
										if (tipo != Attribute.NOMINAL) {
											if (tipo == Attribute.INTEGER)
												mean = new Double(mean + 0.5).intValue();
											X[i][j] = new String(String.valueOf(mean));
										}else{
											mean = new Double(mean + 0.5).intValue();
											X[i][j] = a.getNominalValue((int)mean);
										}
									} else{
										X[i][j] = "<null>";
									}
								}
							}
							out++;
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Dataset exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
		write_results(output_train_name);
		/** ************************************************************************************ */
		// does a test file associated exist?
		if (input_train_name.compareTo(input_test_name) != 0) {
			try {

				// Load in memory a dataset that contains a classification
				// problem
				IStest.readSet(input_test_name, false);
				int in = 0;
				int out = 0;

				ndatos = IStest.getNumInstances();
				nvariables = Attributes.getNumAttributes();
				nentradas = Attributes.getInputNumAttributes();
				nsalidas = Attributes.getOutputNumAttributes();

				X = new String[ndatos][nvariables];// matrix with transformed
				// data

				timesSeen = new FreqList[nvariables];
				mostCommon = new String[nvariables];

				// now, search for missed data, and replace them with
				// the most common value

				for (int i = 0; i < ndatos; i++) {
					Instance inst = IStest.getInstance(i);

					in = 0;
					out = 0;
					if (inst.existsAnyMissingValue()) {
						// search for same class instances
						// and construct a data set with the instances
						SVMp = new svm_problem();
						outputs = inst.getAllOutputValues();
						SVMp.l = 0;
						instancesSelected.clear(); // erase elements from previous
						// work
						instancesSelected3.clear();
						for (int k = 0; k < IS.getNumInstances(); k++) {
							// select those instances with same class and no MVs
							if (!IS.getInstance(k).existsAnyMissingValue()) {
								// compare outputs
								outputsCandidate = IS.getInstance(k).getAllOutputValues();

								same = true;
								for (int n = 0; n < Attributes.getOutputNumAttributes()	&& same; n++)
									if (outputsCandidate[n] != outputs[n])
										same = false; // not a same class instance

								if (same) { // if same class instance, get it for
									// later computations
									SVMp.l++;
									instancesSelected.addElement(IS.getInstance(k));
								}
								instancesSelected3.addElement(IS.getInstance(k));
							}
						}
					}
					for (int j = 0; j < nvariables; j++) {
						Attribute a = Attributes.getAttribute(j);

						direccion = a.getDirectionAttribute();
						tipo = a.getType();

						if (direccion == Attribute.INPUT) {
							if (tipo != Attribute.NOMINAL
									&& !inst.getInputMissingValues(in)) {
								X[i][j] = new String(String.valueOf(inst
										.getInputRealValues(in)));
							} else {
								if (!inst.getInputMissingValues(in))
									X[i][j] = inst.getInputNominalValues(in);
								else {
									if (instancesSelected.size() != 0) {
										//construct the training data for SVM
										SVMp.y = new double[SVMp.l];
										SVMp.x = new svm_node[SVMp.l][Attributes.getOutputNumAttributes()];
										for(int l=0;l<SVMp.l;l++)
											for(int n=0;n<Attributes.getOutputNumAttributes();n++)
												SVMp.x[l][n] = new svm_node();

										for (int k = 0; k < instancesSelected.size(); k++) {
											neighbor = (Instance) instancesSelected.elementAt(k);
											if (tipo != Attribute.NOMINAL) {
												SVMp.y[k] = neighbor.getInputRealValues(in);
												for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
													SVMp.x[k][n].index = n;
													SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
												}

											}else{
												SVMp.y[k] = neighbor.getInputNominalValuesInt(in);
												for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
													SVMp.x[k][n].index = n;
													SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
												}
											}
										}
										//check if parameters are OK
										if(svm.svm_check_parameter(SVMp, SVMparam)!=null){
											System.out.println("SVM parameter error:");
											System.out.println(svm.svm_check_parameter(SVMp, SVMparam));
											System.exit(-1);
										}
										//the values used for regression
										SVMn = new svm_node[Attributes.getOutputNumAttributes()];
										for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
											SVMn[n] = new svm_node();
											SVMn[n].index = n;
											SVMn[n].value = inst.getAllOutputValues()[n];
										}
										//at last, impute
										svr = svm.svm_train(SVMp, SVMparam);

										mean = svm.svm_predict(svr, SVMn);
										if (tipo != Attribute.NOMINAL) {
											if (tipo == Attribute.INTEGER)
												mean = new Double(mean + 0.5).intValue();
											X[i][j] = new String(String.valueOf(mean));
										}else{
											mean = new Double(mean + 0.5).intValue();
											X[i][j] = a.getNominalValue((int)mean);
										}
									}else if (instancesSelected3.size() != 0) {
										//construct the training data for SVM
										SVMp.l = instancesSelected3.size();
										SVMp.y = new double[SVMp.l];
										SVMp.x = new svm_node[SVMp.l][Attributes.getOutputNumAttributes()];
										for(int l=0;l<SVMp.l;l++)
											for(int n=0;n<Attributes.getOutputNumAttributes();n++)
												SVMp.x[l][n] = new svm_node();

										for (int k = 0; k < instancesSelected3.size(); k++) {
											neighbor = (Instance) instancesSelected3.elementAt(k);
											if (tipo != Attribute.NOMINAL) {
												SVMp.y[k] = neighbor.getInputRealValues(in);
												for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
													SVMp.x[k][n].index = n;
													SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
												}

											}else{
												SVMp.y[k] = neighbor.getInputNominalValuesInt(in);
												for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
													SVMp.x[k][n].index = n;
													SVMp.x[k][n].value = neighbor.getAllOutputValues()[n];
												}
											}
										}
										//check if parameters are OK
										if(svm.svm_check_parameter(SVMp, SVMparam)!=null){
											System.out.println("SVM parameter error:");
											System.out.println(svm.svm_check_parameter(SVMp, SVMparam));
											System.exit(-1);
										}
										//the values used for regression
										SVMn = new svm_node[Attributes.getOutputNumAttributes()];
										for (int n = 0; n < Attributes.getOutputNumAttributes(); n++){
											SVMn[n] = new svm_node();
											SVMn[n].index = n;
											SVMn[n].value = inst.getAllOutputValues()[n];
										}
										//at last, impute
										svr = svm.svm_train(SVMp, SVMparam);

										mean = svm.svm_predict(svr, SVMn);
										if (tipo != Attribute.NOMINAL) {
											if (tipo == Attribute.INTEGER)
												mean = new Double(mean + 0.5).intValue();
											X[i][j] = new String(String.valueOf(mean));
										}else{
											mean = new Double(mean + 0.5).intValue();
											X[i][j] = a.getNominalValue((int)mean);
										}
									} else
										X[i][j] = "<null>";
								}
							}
							in++;
						} else {
							if (direccion == Attribute.OUTPUT) {
								if (tipo != Attribute.NOMINAL
										&& !inst.getOutputMissingValues(out)) {
									X[i][j] = new String(String.valueOf(inst
											.getOutputRealValues(out)));
								} else {
									if (!inst.getOutputMissingValues(out))
										X[i][j] = inst
												.getOutputNominalValues(out);
									else{
										SVMp2 = new svm_problem();
										outputs = inst.getAllInputValues();
										SVMp2.l = 0;
										instancesSelected2.clear(); // erase elements from previous
										// work
										instancesSelected3.clear(); //clear previous work
										for (int k = 0; k < IS.getNumInstances(); k++) {
											// select those instances with same class and no MVs
											if (!IS.getInstance(k).existsAnyMissingValue()) {
												// compare outputs
												outputsCandidate = IS.getInstance(k).getAllInputValues();

												same = true;
												for (int n = 0; n < Attributes.getInputNumAttributes()&& same; n++)
													if (outputsCandidate[n] != outputs[n])
														same = false; // not a same class instance

												if (same) { // if same class instance, get it for
													// later computations
													SVMp2.l++;
													instancesSelected2.addElement(IS.getInstance(k));
												}
												//store all the avaliable isntances
												instancesSelected3.addElement(IS.getInstance(k));
											}
										}
										
										if (instancesSelected2.size() != 0) {
											//construct the training data for SVM
											SVMp2.l = instancesSelected3.size();
											SVMp2.y = new double[SVMp2.l];
											SVMp2.x = new svm_node[SVMp2.l][Attributes.getInputNumAttributes()];
											for(int l=0;l<SVMp2.l;l++)
												for(int n=0;n<Attributes.getInputNumAttributes();n++)
													SVMp2.x[l][n] = new svm_node();
											
											for (int k = 0; k < instancesSelected2.size(); k++) {
												neighbor = (Instance) instancesSelected2.elementAt(k);
												if (tipo != Attribute.NOMINAL) {
													SVMp2.y[k] = neighbor.getInputRealValues(in);
													for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
														SVMp2.x[k][n].index = n;
														SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
													}

												}else{
													SVMp2.y[k] = neighbor.getInputNominalValuesInt(in);
													for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
														SVMp2.x[k][n].index = n;
														SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
													}
												}
											}
											//check if parameters are OK
											if(svm.svm_check_parameter(SVMp2, SVMparam)!=null){
												System.out.println("SVM parameter error:");
												System.out.println(svm.svm_check_parameter(SVMp2, SVMparam));
												System.exit(-1);
											}
											//the values used for regression
											SVMn = new svm_node[Attributes.getInputNumAttributes()];
											for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
												SVMn[n] = new svm_node();
												SVMn[n].index = n;
												SVMn[n].value = inst.getAllInputValues()[n];
											}
											//at last, impute
											svr = svm.svm_train(SVMp2, SVMparam);

											mean = svm.svm_predict(svr, SVMn);
											if (tipo != Attribute.NOMINAL) {
												if (tipo == Attribute.INTEGER)
													mean = new Double(mean + 0.5).intValue();
												X[i][j] = new String(String.valueOf(mean));
											}else{
												mean = new Double(mean + 0.5).intValue();
												X[i][j] = a.getNominalValue((int)mean);
											}
										} else if (instancesSelected3.size() != 0) { //impute with instances of the rest of the classes
											//construct the training data for SVM
											SVMp2.y = new double[SVMp2.l];
											SVMp2.x = new svm_node[SVMp2.l][Attributes.getInputNumAttributes()];
											for(int l=0;l<SVMp2.l;l++)
												for(int n=0;n<Attributes.getInputNumAttributes();n++)
													SVMp2.x[l][n] = new svm_node();
											
											for (int k = 0; k < instancesSelected3.size(); k++) {
												neighbor = (Instance) instancesSelected3.elementAt(k);
												if (tipo != Attribute.NOMINAL) {
													SVMp2.y[k] = neighbor.getInputRealValues(in);
													for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
														SVMp2.x[k][n].index = n;
														SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
													}

												}else{
													SVMp2.y[k] = neighbor.getInputNominalValuesInt(in);
													for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
														SVMp2.x[k][n].index = n;
														SVMp2.x[k][n].value = neighbor.getAllInputValues()[n];
													}
												}
											}
											//check if parameters are OK
											if(svm.svm_check_parameter(SVMp2, SVMparam)!=null){
												System.out.println("SVM parameter error:");
												System.out.println(svm.svm_check_parameter(SVMp2, SVMparam));
												System.exit(-1);
											}
											//the values used for regression
											SVMn = new svm_node[Attributes.getInputNumAttributes()];
											for (int n = 0; n < Attributes.getInputNumAttributes(); n++){
												SVMn[n] = new svm_node();
												SVMn[n].index = n;
												SVMn[n].value = inst.getAllInputValues()[n];
											}
											//at last, impute
											svr = svm.svm_train(SVMp2, SVMparam);

											mean = svm.svm_predict(svr, SVMn);
											if (tipo != Attribute.NOMINAL) {
												if (tipo == Attribute.INTEGER)
													mean = new Double(mean + 0.5).intValue();
												X[i][j] = new String(String.valueOf(mean));
											}else{
												mean = new Double(mean + 0.5).intValue();
												X[i][j] = a.getNominalValue((int)mean);
											}
										} else{
											X[i][j] = "<null>";
										}
									}
								}
								out++;
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Dataset exception = " + e);
				e.printStackTrace();
				System.exit(-1);
			}
			write_results(output_test_name);
		}
	}

}

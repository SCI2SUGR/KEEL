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
 * @author Written by Julián Luengo Martín 10/12/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Fuzzy_Rule_Learning.Hybrid.PDFCS;

/**

 * Ref: Y. Chen, J.Z. Wang, Support Vector Learning for Fuzzy Rule-Based Classification
 * Systems. IEEE Transactions on Fuzzy Systems 11(6) 716-728.
 * @author Julián Luengo Martín
 * @date December 2008
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.core.*;

import keel.Algorithms.SVM.SMO.SMO;
import keel.Algorithms.SVM.SMO.core.FastVector;
import keel.Algorithms.SVM.SMO.core.Instance;
import keel.Algorithms.SVM.SMO.core.Instances;
import keel.Algorithms.SVM.SMO.core.Utils;
import keel.Algorithms.SVM.SMO.supportVector.Kernel;
import keel.Algorithms.SVM.SMO.supportVector.PDRFKernel;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

/**
 * <p>
 * This class implements the Positive Definite Fuzzy Classifier from Chen and Wang's paper:
 * </p>
 * <p>
 * Y. Chen, J.Z. Wang: Support Vector Learning for Fuzzy Rule-Based Classification Systems. IEEE Transactions on Fuzzy Systems, 11 (6) 2003 pp. 716-728.</ref>
 * </p>
 */
public class PDFC extends SMO{

	/** The binary classifier(s) */
	protected BinarySMO[] m_classifiers = null;
	
	/** The fuzzy rule sets  */
	protected FuzzyRuleSet m_ruleSet[] = null;
	
	/** The 'd' value for the Positive-Definite Reference Functions */
	protected double m_d = 1.0;

	/** Creates a new instance of PDFC
     * @param fileParam The path to the configuration file with all the parameters in KEEL format
     */
	public PDFC(String fileParam) {
		config_read(fileParam);
		Randomize.setSeed(m_seed);
	}

	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public PDFC(){
		super();
	}

	/**
	 * Method for building the classifier. Implements a one-against-all
	 * wrapper for multi-class problems.
	 *
	 * @param insts the set of training instances
	 * @throws Exception if the classifier can't be built successfully
	 */
	public void buildClassifier(Instances insts) throws Exception {
		Instance inst;
		
		m_classIndex = insts.classIndex();
		m_classAttribute = insts.classAttribute();
		m_KernelIsLinear = false;
		m_NumClasses = insts.numClasses();
		// Generate subsets representing each class
		Instances[] subsets = new Instances[insts.numClasses()];
		for (int i = 0; i < insts.numClasses(); i++) {
			subsets[i] = new Instances(insts, insts.numInstances());
		}
		for (int j = 0; j < insts.numInstances(); j++) {
			inst = insts.instance(j);
			subsets[(int)inst.classValue()].add(inst);
		}
		for (int i = 0; i < insts.numClasses(); i++) {
			subsets[i].compactify();
		}

		// Build the binary classifiers, using the one-versus-all scheme
		if(m_NumClasses==2){
			m_classifiers = new BinarySMO[1];
			m_ruleSet = new FuzzyRuleSet[1];
		}else{
			m_classifiers = new BinarySMO[insts.numClasses()];
			m_ruleSet = new FuzzyRuleSet[insts.numClasses()];
		}
		for (int i = 0; i < m_classifiers.length; i++) {
			m_classifiers[i] = new BinarySMO();
			m_classifiers[i].setKernel(Kernel.makeCopy(getKernel()));
			Instances data = new Instances(insts, insts.numInstances());
			for (int k = 0; k < subsets[i].numInstances(); k++) {
				//we change the original class values to artificial ones
				inst = new Instance(subsets[i].instance(k));
				inst.setDataset(insts);
				inst.setClassValue(0);
				data.add(inst);
			}
			for (int j = 0; j < insts.numClasses(); j++) {
				if(j!=i){
					for (int k = 0; k < subsets[j].numInstances(); k++) {
						//again, we change the original class values to artificial ones
						inst = new Instance(subsets[j].instance(k));
						inst.setDataset(insts);
						inst.setClassValue(1);
						data.add(inst);
					}
				}
			}
			data.compactify();
			data.randomize();
			//note that we pass to the binarySMO the artificial class values (i.e. 0 and 1)
			//that we have assigned to the instances!
			m_classifiers[i].buildClassifier(data, 1, 0, 
					m_fitLogisticModels,
					m_numFolds, m_randomSeed);
			
			//Once we have the SMO classifiers built
			//we extract the fuzzy rules of the SVMs
			m_ruleSet[i] = new FuzzyRuleSet(m_classifiers[i],data,m_kernel); 
		}
		
	}
	
	/**
	 * Estimates class probabilities for given instance.
	 * 
	 * @param inst the instance to compute the probabilities for
	 * @throws Exception in case of an error
	 */
	public double[] distributionForInstance(Instance inst) throws Exception {
		double output,max;
		int index;
		double[] result = new double[inst.numClasses()];
		
		if(inst.numClasses()==2){
			output = m_ruleSet[0].unthresholdedOutput(inst);
			if(Math.signum(output)<0)
				result[1]++;
			else
				result[0]++;
				
		}else{
			max = output = m_ruleSet[0].unthresholdedOutput(inst);
			index = 0;
			for (int j = 1; j < inst.numClasses(); j++) {
				output = m_ruleSet[j].unthresholdedOutput(inst);
				if (output > max) {
					max = output;
					index = j;
				}
			}
			result[index] += 1;
//			Utils.normalize(result);
		}
		return result;

	}

	/**
	 * Run the model once the parameters have been set by the
	 * method config_read()
	 */
	public void runModel() {
		Instances isWeka;
		Instance instWeka;
		InstanceSet IS = new InstanceSet();
		InstanceSet ISval = new InstanceSet();
		double []dist;
		String instanciasIN[];
		String instanciasOUT[];

		try{
			//*********build de SMO classifier********
			IS.readSet(input_train_name, true);
			if(m_filterType==FILTER_STANDARDIZE)
				computeStats(IS);
			isWeka = InstancesKEEL2Weka(IS,m_filterType,m_nominalToBinary);
			//			Fichero.escribeFichero("keelTemp.dat", isWeka.toString());
			buildClassifier(isWeka);

			//********validate the obtained SMO*******
			ISval.readSet(input_validation_name, false);
			isWeka = InstancesKEEL2Weka(ISval,m_filterType,m_nominalToBinary);
			//obtain the predicted class for each train instance
			Attribute a = Attributes.getOutputAttribute(0);
			int tipo = a.getType();
			instanciasIN = new String[ISval.getNumInstances()];
			instanciasOUT = new String[ISval.getNumInstances()];

			for (int i = 0; i < isWeka.numInstances();i++) {
				keel.Dataset.Instance inst = ISval.getInstance(i);
				instWeka = isWeka.instance(i);
				instWeka.setDataset(isWeka);
				dist = this.distributionForInstance(instWeka);
				int claseObt = 0;
				for(int j=1;j<m_NumClasses;j++){
					if(dist[j]>dist[claseObt])
						claseObt = j;
				}
				if(tipo!=Attribute.NOMINAL){
					instanciasIN[i] = new String(String.valueOf(inst.getOutputRealValues(0)));
					instanciasOUT[i] = new String(String.valueOf(claseObt));
				}
				else{
					instanciasIN[i] = new String(inst.getOutputNominalValues(0));
					instanciasOUT[i] = new String(a.getNominalValue(claseObt));
				}
			}
			writeOutput(output_train_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
					Attributes.getOutputAttribute(0), Attributes.getInputNumAttributes(), "relation");
		}catch(Exception ex){
			System.err.println("Fatal Error building the PDFC model!");
			ex.printStackTrace();
		};
		try{

			//******Apply the SMO to the test set ******
			IS.readSet(input_test_name, false);
			isWeka = InstancesKEEL2Weka(IS,m_filterType,m_nominalToBinary);
			//obtain the predicted class for each train instance
			Attribute a = Attributes.getOutputAttribute(0);
			int tipo = a.getType();
			instanciasIN = new String[IS.getNumInstances()];
			instanciasOUT = new String[IS.getNumInstances()];

			for (int i = 0; i < isWeka.numInstances();i++) {
				keel.Dataset.Instance inst = IS.getInstance(i);
				instWeka = isWeka.instance(i);
				instWeka.setDataset(isWeka);
				dist = this.distributionForInstance(instWeka);
				int claseObt = 0;
				for(int j=1;j<m_NumClasses;j++){
					if(dist[j]>dist[claseObt])
						claseObt = j;
				}
				if(tipo!=Attribute.NOMINAL){
					instanciasIN[i] = new String(String.valueOf(inst.getOutputRealValues(0)));
					instanciasOUT[i] = new String(String.valueOf(claseObt));
				}
				else{
					instanciasIN[i] = new String(inst.getOutputNominalValues(0));
					instanciasOUT[i] = new String(a.getNominalValue(claseObt));
				}
			}
			writeOutput(output_test_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
					Attributes.getOutputAttribute(0), Attributes.getInputNumAttributes(), "relation");
		}catch(Exception ex){
			System.err.println("Fatal Error performing test by the PDFC model!");
			ex.printStackTrace();
		};
		
		/**New, print the Rule Base and Data Base to file**/
		//TODO to print the data base
		printRuleBase();
	}
	
	/**
	 * This method prints the actual Rule Base stored in m_ruleSet to a file
	 */
	protected void printRuleBase(){
		int ruleNumber;
		//TODO - write the rules as well, not only the number of them
		ruleNumber = 0;
		for(int i=0;i<m_ruleSet.length;i++)
			ruleNumber += m_ruleSet[i].getNumRules();
		Files.writeFile(method_output, "Number of rules: "+ruleNumber+"\n");
	}


	/**
	 * Parse the parameter file created by KEEL into the needed parameters.
	 * It also configures the selected kernel passing its parameters.
	 * @param fileParam The path to the parameter file
	 */
	private void config_read(String fileParam) {
		File inputFile = new File(fileParam);

		//temporal variables, to finally configure the correspondent Kernel 
		String pdrfType;
		String preprocess;

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
			input_train_name = new String(out[0].substring(1,out[0].length() - 1));
			input_validation_name = new String(out[1].substring(0,out[1].length() - 1));
			input_test_name = new String(out[2].substring(0,out[2].length() - 1));
			if (input_validation_name.charAt(input_validation_name.length() - 1) == '"')
				input_validation_name = input_validation_name.substring(0, input_validation_name
						.length() - 1);
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
			method_output = new String(out[2].substring(0,out[2].length() - 1));
			if (method_output.charAt(method_output.length() - 1) == '"')
				method_output = method_output.substring(0,
						method_output.length() - 1);
			if (output_test_name.charAt(output_test_name.length() - 1) == '"')
				output_test_name = output_test_name.substring(0,
						output_test_name.length() - 1);

			// parameters
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("seed = ");
			m_seed = (new Integer(out[1])).intValue(); 

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("C = ");
			m_C = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("d = ");
			m_d = (new Double(out[1])).doubleValue(); // parse the string into

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("toleranceParameter = ");
			m_tol = (new Double(out[1])).doubleValue(); // parse the string into

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("epsilon = ");
			m_eps = (new Double(out[1])).doubleValue(); // parse the string into

			m_kernel = new PDRFKernel();
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("PDRFtype = ");
			pdrfType = (new String(out[1])); 
			if(pdrfType.compareTo("SymmetricTriangle")==0){
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.SymmetricTriangle);
			}else if(pdrfType.compareTo("Gaussian")==0){
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.Gaussian);
			}else if(pdrfType.compareTo("Cauchy")==0){
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.Cauchy);
			}else if(pdrfType.compareTo("Laplace")==0){
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.Laplace);	
			}else if(pdrfType.compareTo("HyperbolicSecant")==0){
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.HyperbolicSecant);	
			}else
				((PDRFKernel)m_kernel).setPDRFType(PDRFKernel.SquaredSinc);
			((PDRFKernel)m_kernel).setD(m_d);

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("ConvertNominalAttributesToBinary = ");
			m_nominalToBinary = (new String(out[1])).compareTo("True")==0;

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("PreprocessType = ");
			preprocess = new String(out[1]);
			if(preprocess.compareTo("Normalize")==0)
				m_filterType = FILTER_NORMALIZE;
			else if(preprocess.compareTo("Standardize")==0)
				m_filterType = FILTER_STANDARDIZE;
			else
				m_filterType = FILTER_NONE;
			
			
		} catch (IOException e) {
			System.out.println("Parameter reading exception: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

}


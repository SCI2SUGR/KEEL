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

package keel.Algorithms.MIL.G3PMI;

import java.io.File;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


/**
 * <p>Title: MIDD Main Program</p>
 * <p>Description: This is the main class, which is executed when we launch the program</p>
 * @version 1.0
 * @since JDK1.4
 */

public class Main
{
	/**
	 * G3P-MI Algorithm	 */

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Main method
	 * </p>
	 */
	public static void main(String[] args) {
		configureJob(args[0]);
	}	

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Configure the execution of the algorithm.
	 * 
	 * @param jobFilename Name of the KEEL file with properties of the execution
	 *  </p>                  
	 */

	private static void configureJob(String jobFilename) {

		Properties props = new Properties();

		try {
			InputStream paramsFile = new FileInputStream(jobFilename);
			props.load(paramsFile);
			paramsFile.close();			
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(0);
		}
		
		// Files training and test
		String trainFile;
		String testFile;
		StringTokenizer tokenizer = new StringTokenizer(props.getProperty("inputData"));
		tokenizer.nextToken();
		trainFile = tokenizer.nextToken();
		trainFile = trainFile.substring(1, trainFile.length()-1);
		testFile = tokenizer.nextToken();
		testFile = testFile.substring(1, testFile.length()-1);
		
		tokenizer = new StringTokenizer(props.getProperty("outputData"));
		String reportTrainFile = tokenizer.nextToken();
		reportTrainFile = reportTrainFile.substring(1, reportTrainFile.length()-1);
		String reportTestFile = tokenizer.nextToken();
		reportTestFile = reportTestFile.substring(1, reportTestFile.length()-1);	
		//System.out.println("SALIDA: " + reportTestFile);
		//String reportRulesFile = tokenizer.nextToken();
		//reportRulesFile = reportRulesFile.substring(1, reportRulesFile.length()-1);				
		
		// Algorithm auxiliar configuration
		XMLConfiguration algConf = new XMLConfiguration();
		algConf.setRootElementName("experiment");
		algConf.addProperty("process.algorithm[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMIAlgorithm");
		algConf.addProperty("process.algorithm.rand-gen-factory[@type]", "org.ayrna.jclec.util.random.RanecuFactory");
		algConf.addProperty("process.algorithm.rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));
		algConf.addProperty("process.algorithm.population-size", Integer.parseInt(props.getProperty("population-size")));
		algConf.addProperty("process.algorithm.max-of-generations", Integer.parseInt(props.getProperty("max-generations")));
		algConf.addProperty("process.algorithm.max-deriv-size", Integer.parseInt(props.getProperty("max-deriv-size")));
		algConf.addProperty("process.algorithm.species[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMISyntaxTreeSpecies");
		algConf.addProperty("process.algorithm.species.max-deriv-size", Integer.parseInt(props.getProperty("max-deriv-size")));
		algConf.addProperty("process.algorithm.species.dataset[@type]", "org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet");
		algConf.addProperty("process.algorithm.species.dataset.file-name", trainFile);
		algConf.addProperty("process.algorithm.species.rand-gen-factory[@type]", "org.ayrna.jclec.util.random.RanecuFactory");
		algConf.addProperty("process.algorithm.species.rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));
		algConf.addProperty("process.algorithm.evaluator[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMIEvaluator");
		algConf.addProperty("process.algorithm.evaluator.rand-gen-factory[@type]", "org.ayrna.jclec.util.random.RanecuFactory");
		algConf.addProperty("process.algorithm.evaluator.rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));
		algConf.addProperty("process.algorithm.evaluator.dataset[@type]", "org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet");
		algConf.addProperty("process.algorithm.evaluator.dataset.file-name", trainFile);
		algConf.addProperty("process.algorithm.evaluator.max-deriv-size", Integer.parseInt(props.getProperty("max-deriv-size")));
		algConf.addProperty("process.algorithm.provider[@type]", "org.ayrna.jclec.syntaxtree.SyntaxTreeCreator");
		algConf.addProperty("process.algorithm.parents-selector[@type]", "org.ayrna.jclec.selector.RouletteSelector");
		algConf.addProperty("process.algorithm.recombinator.decorated[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMICrossover");
		algConf.addProperty("process.algorithm.recombinator.recombination-prob", Double.parseDouble(props.getProperty("rec-prob")));
		algConf.addProperty("process.algorithm.mutator.decorated[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMIMutator");
		algConf.addProperty("process.algorithm.mutator.mutation-prob", Double.parseDouble(props.getProperty("mut-prob")));
		algConf.addProperty("process.listeners.listener[@type]", "org.ayrna.jclec.problem.classification.syntaxtree.multiinstance.G3PMIKeel.G3PMIPopulationReport"); 
		algConf.addProperty("process.listeners.listener.report-dir-name", "./");
		algConf.addProperty("process.listeners.listener.train-report-file", reportTrainFile);
		algConf.addProperty("process.listeners.listener.test-report-file", reportTestFile);
		algConf.addProperty("process.listeners.listener.global-report-name", "resumen");
		algConf.addProperty("process.listeners.listener.report-frequency", 50);
		algConf.addProperty("process.listeners.listener.test-dataset[@type]", "org.ayrna.jclec.util.dataset.KeelMultiInstanceDataSet");
		algConf.addProperty("process.listeners.listener.test-dataset.file-name", testFile);
		
		
		try {
			algConf.save(new File("configure.txt"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		org.ayrna.jclec.genlab.GenLab.main(new String [] {"configure.txt"});
	}
}
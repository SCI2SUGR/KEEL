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

package keel.Algorithms.Genetic_Rule_Learning.Tan_GP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * <p>
 * @author Written by Jose Maria Luna, Juan Luis Olmo, Alberto Cano (Universidad de Cordoba) 05/07/2010
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class Main
{
	/**
	 * <p>
	 * Falco classification algorithm
	 * </p>
	 */

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
	 * @param jobFilename Name of the KEEL file with properties of the
	 *                    execution
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
		String reportRulesFile = tokenizer.nextToken();
		reportRulesFile = reportRulesFile.substring(1, reportRulesFile.length()-1);		
		
		// Algorithm auxiliar configuration
		XMLConfiguration algConf = new XMLConfiguration();
		algConf.setRootElementName("experiment");
		algConf.addProperty("process[@algorithm-type]", "net.sourceforge.jclec.problem.classification.tan.TanAlgorithm");
		algConf.addProperty("process.rand-gen-factory[@type]", "net.sourceforge.jclec.util.random.RanecuFactory");
		algConf.addProperty("process.rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));
		algConf.addProperty("process.population-size", Integer.parseInt(props.getProperty("population-size")));
		algConf.addProperty("process.max-of-generations", Integer.parseInt(props.getProperty("max-generations")));
		algConf.addProperty("process.max-deriv-size", Integer.parseInt(props.getProperty("max-deriv-size")));
		algConf.addProperty("process.dataset[@type]", "net.sourceforge.jclec.util.dataset.KeelDataSet");
		algConf.addProperty("process.dataset.train-data.file-name", trainFile);
		algConf.addProperty("process.dataset.test-data.file-name", testFile);
		algConf.addProperty("process.species[@type]", "net.sourceforge.jclec.problem.classification.tan.TanSyntaxTreeSpecies");
		algConf.addProperty("process.evaluator[@type]", "net.sourceforge.jclec.problem.classification.tan.TanEvaluator");
		algConf.addProperty("process.evaluator.w1", Double.parseDouble(props.getProperty("w1")));
		algConf.addProperty("process.evaluator.w2", Double.parseDouble(props.getProperty("w2")));
		algConf.addProperty("process.provider[@type]", "net.sourceforge.jclec.syntaxtree.SyntaxTreeCreator");
		algConf.addProperty("process.parents-selector[@type]", "net.sourceforge.jclec.selector.RouletteSelector");
		algConf.addProperty("process.recombinator[@type]", "net.sourceforge.jclec.syntaxtree.SyntaxTreeRecombinator");
		algConf.addProperty("process.recombinator[@rec-prob]", Double.parseDouble(props.getProperty("rec-prob")));
		algConf.addProperty("process.recombinator.base-op[@type]", "net.sourceforge.jclec.problem.classification.tan.TanCrossover");
		algConf.addProperty("process.mutator[@type]", "net.sourceforge.jclec.syntaxtree.SyntaxTreeMutator");
		algConf.addProperty("process.mutator[@mut-prob]", Double.parseDouble(props.getProperty("mut-prob")));
		algConf.addProperty("process.mutator.base-op[@type]", "net.sourceforge.jclec.problem.classification.tan.TanMutator");	
		algConf.addProperty("process.copy-prob", Double.parseDouble(props.getProperty("copy-prob")));
		algConf.addProperty("process.elitist-prob", Double.parseDouble(props.getProperty("elitist-prob")));
		algConf.addProperty("process.support", Double.parseDouble(props.getProperty("support")));
		algConf.addProperty("process.listener[@type]", "net.sourceforge.jclec.problem.classification.tan.KeelTanPopulationReport");
		algConf.addProperty("process.listener.report-dir-name", "./");
		algConf.addProperty("process.listener.train-report-file", reportTrainFile);
		algConf.addProperty("process.listener.test-report-file", reportTestFile);
		algConf.addProperty("process.listener.rules-report-file", reportRulesFile);
		algConf.addProperty("process.listener.global-report-name", "resumen");
		algConf.addProperty("process.listener.report-frequency", 50);
		
		try {
			algConf.save(new File("configure.txt"));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		net.sourceforge.jclec.RunExperiment.main(new String [] {"configure.txt"});
	}
}

/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

package keel.Algorithms.Neural_Networks.NNEP_Regr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividual;
import keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividualSpecies;
import keel.Algorithms.Neural_Networks.NNEP_Common.algorithm.NeuralNetAlgorithm;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.AttributeType;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.CategoricalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DatasetException;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IMetadata;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IntegerNumericalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.KeelDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.RealNumericalAttribute;
import keel.Algorithms.Neural_Networks.NNEP_Common.problem.ProblemEvaluator;
import keel.Algorithms.Neural_Networks.NNEP_Regr.listener.NeuralNetReporterRegr;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IAlgorithmListener;
import net.sf.jclec.util.intset.Interval;

import org.apache.commons.configuration.XMLConfiguration;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class KEELWrapperRegr 
{
	/**
	 * <p>
	 * Wrapper of Neural Net Evolutionary Programming for KEEL
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Wrapped algorithm */
	protected static NeuralNetAlgorithm<NeuralNetIndividual> algorithm;

	/** Console reporter */
	protected static NeuralNetReporterRegr consoleReporter = new NeuralNetReporterRegr();

	/** Listener list */
	protected static ArrayList<IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>>> listeners 
		= new ArrayList<IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>>>();

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
		executeJob();
	}	

	/////////////////////////////////////////////////////////////////
	// ------------------------------------- Algorithm events support
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Event Algorithm started
	 * </p>
	 */
	private static final void fireAlgorithmStarted()
	{
		AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event = new AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>>(algorithm);
		for (IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>> listener : listeners) {
			listener.algorithmStarted(event);
		}
	}

	/**
	 * <p>
	 * Event Iteration completed
	 * </p>
	 */
	private static final void fireIterationCompleted()
	{
		AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event = new AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>>(algorithm);
		for (IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>> listener : listeners) {
			listener.iterationCompleted(event);
		}		
	}

	/**
	 * <p>
	 * Event Algorithm finished
	 * </p>
	 */
	private static final void fireAlgorithmFinished()
	{
		AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>> event = new AlgorithmEvent<NeuralNetAlgorithm<NeuralNetIndividual>>(algorithm);
		for (IAlgorithmListener<NeuralNetAlgorithm<NeuralNetIndividual>> listener : listeners) {
			listener.algorithmFinished(event);
		}
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Configure the execution of the algorithm.
	 * </p>
	 * @param jobFilename Name of the KEEL file with properties of the
	 *                    execution
	 */

	@SuppressWarnings("unchecked")
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
		
		// Classification or Regression ??
		byte[] schema = null;
		try {	
			schema = readSchema(trainFile);			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DatasetException e) {
			e.printStackTrace();
		}

		// Algorithm auxiliar configuration
		XMLConfiguration algConf = new XMLConfiguration();
		algConf.setRootElementName("algorithm");
		algConf.addProperty("population-size", 1000);
		algConf.addProperty("max-of-generations", Integer.parseInt(props.getProperty("Generations")));
		algConf.addProperty("creation-ratio", 10.0);
		algConf.addProperty("percentage-second-mutator", 10);
		algConf.addProperty("max-generations-without-improving-mean", 20);
		algConf.addProperty("max-generations-without-improving-best", 20);
		algConf.addProperty("fitness-difference", 0.0000001);
		algConf.addProperty("species[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetIndividualSpecies");
		algConf.addProperty("species.neural-net-type", "keel.Algorithms.Neural_Networks.NNEP_Regr.neuralnet.NeuralNetRegressor");			
		if  (props.getProperty("Transfer").equals("Product_Unit"))
		{
			algConf.addProperty("species.hidden-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ExpLayer" );
			algConf.addProperty("species.hidden-layer[@biased]", false );
			algConf.addProperty("evaluator[@log-input-data]", true );
		}
		else
		{
			algConf.addProperty("species.hidden-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.SigmLayer" );
			algConf.addProperty("species.hidden-layer[@biased]", true );
		}
		int neurons = Integer.parseInt(props.getProperty("Hidden_nodes"));
		algConf.addProperty("species.hidden-layer.minimum-number-of-neurons", (neurons / 3)!=0 ? (neurons/3) : 1);
		algConf.addProperty("species.hidden-layer.initial-maximum-number-of-neurons", (neurons / 2)!=0 ? (neurons/2) : 1);
		algConf.addProperty("species.hidden-layer.maximum-number-of-neurons", neurons);
		algConf.addProperty("species.hidden-layer.initiator-of-links", "keel.Algorithms.Neural_Networks.NNEP_Common.initiators.RandomInitiator");
		algConf.addProperty("species.hidden-layer.weight-range[@type]", "net.sf.jclec.util.range.Interval" );
		algConf.addProperty("species.hidden-layer.weight-range[@closure]", "closed-closed" );
		algConf.addProperty("species.hidden-layer.weight-range[@left]", -5.0 );
		algConf.addProperty("species.hidden-layer.weight-range[@right]", 5.0 );
		algConf.addProperty("species.output-layer[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinearLayer" );
		algConf.addProperty("species.output-layer[@biased]", true );
		algConf.addProperty("species.output-layer.initiator-of-links", "keel.Algorithms.Neural_Networks.NNEP_Common.initiators.RandomInitiator");
		algConf.addProperty("species.output-layer.weight-range[@type]", "net.sf.jclec.util.range.Interval" );
		algConf.addProperty("species.output-layer.weight-range[@closure]", "closed-closed" );
		algConf.addProperty("species.output-layer.weight-range[@left]", -5.0 );
		algConf.addProperty("species.output-layer.weight-range[@right]", 5.0 );

		algConf.addProperty("evaluator[@type]", "keel.Algorithms.Neural_Networks.NNEP_Regr.problem.regression.RegressionProblemEvaluator");
			
		algConf.addProperty("evaluator[@normalize-data]", true);

		algConf.addProperty("evaluator.error-function", "keel.Algorithms.Neural_Networks.NNEP_Regr.problem.errorfunctions.MSEErrorFunction");
		
		algConf.addProperty("evaluator.input-interval[@closure]", "closed-closed");
		if  (props.getProperty("Transfer").equals("Product_Unit"))
		{
                    algConf.addProperty("evaluator.input-interval[@left]", 1.0);
                    algConf.addProperty("evaluator.input-interval[@right]", 2.0);
		}
		else
		{
                    algConf.addProperty("evaluator.input-interval[@left]", 0.1);
                    algConf.addProperty("evaluator.input-interval[@right]", 0.9);
		}

                algConf.addProperty("evaluator.output-interval[@closure]", "closed-closed");

		algConf.addProperty("evaluator.output-interval[@left]", 1.0);
		algConf.addProperty("evaluator.output-interval[@right]", 2.0);			
		
		algConf.addProperty("provider[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.NeuralNetCreator");
		algConf.addProperty("mutator1[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.mutators.structural.StructuralMutator");
		algConf.addProperty("mutator1.temperature-exponent[@value]", 1.0);
		algConf.addProperty("mutator1.significative-weigth[@value]", 0.0000001);
		algConf.addProperty("mutator1.neuron-ranges.added[@min]", 1);
		algConf.addProperty("mutator1.neuron-ranges.added[@max]", 2);
		algConf.addProperty("mutator1.neuron-ranges.deleted[@min]", 1);
		algConf.addProperty("mutator1.neuron-ranges.deleted[@max]", 2);
		
		algConf.addProperty("mutator1.links-ranges[@relative]", false);
		algConf.addProperty("mutator1.links-ranges.added[@min]", 1);
		algConf.addProperty("mutator1.links-ranges.added[@max]", 6);
		algConf.addProperty("mutator1.links-ranges.deleted[@min]", 1);
		algConf.addProperty("mutator1.links-ranges.deleted[@max]", 6);
		algConf.addProperty("mutator2[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric.ParametricSAMutator");			

		algConf.addProperty("mutator2.temperature-exponent[@value]", 0.0);
		algConf.addProperty("mutator2.amplitude[@value]", 5.0);
		algConf.addProperty("mutator2.fitness-difference[@value]", 0.0000001);
		algConf.addProperty("mutator2.initial-alpha-values[@input]", 0.5);
		algConf.addProperty("mutator2.initial-alpha-values[@output]", 1.0);
		algConf.addProperty("rand-gen-factory[@type]", "keel.Algorithms.Neural_Networks.NNEP_Common.util.random.RanNnepFactory");
		algConf.addProperty("rand-gen-factory[@seed]", Integer.parseInt(props.getProperty("seed")));

		// Neural Net Algorithm
		algorithm = new NeuralNetAlgorithm<NeuralNetIndividual>();		
		
		algorithm.configure(algConf);
		
		// Read data
		ProblemEvaluator evaluator = (ProblemEvaluator)algorithm.getEvaluator();
		evaluator.readData(schema, new KeelDataSet(trainFile), new KeelDataSet(testFile));
		((NeuralNetIndividualSpecies)algorithm.getSpecies()).setNOfInputs(evaluator.getTrainData().getNofinputs());
		((NeuralNetIndividualSpecies)algorithm.getSpecies()).setNOfOutputs(evaluator.getTrainData().getNofoutputs());
			
		// Read output files
		tokenizer = new StringTokenizer(props.getProperty("outputData"));
		String trainResultFile = tokenizer.nextToken();
		trainResultFile = trainResultFile.substring(1, trainResultFile.length()-1);
		consoleReporter.setTrainResultFile(trainResultFile);
		String testResultFile = tokenizer.nextToken();
		testResultFile = testResultFile.substring(1, testResultFile.length()-1);
		consoleReporter.setTestResultFile(testResultFile);
		String bestModelResultFile = tokenizer.nextToken();
		bestModelResultFile = bestModelResultFile.substring(1, bestModelResultFile.length()-1);
		consoleReporter.setBestModelResultFile(bestModelResultFile);		
		
		listeners.add(consoleReporter);
	}

	/**
	 * <p>
	 * Executes the algorithm
	 * </p>
	 */

	private static void executeJob() 	{
		// Init algorithm
		algorithm.doInit();
		// Fire algorithmStarted event
		fireAlgorithmStarted();
		// Main cycle
		while ( !algorithm.isFinished() ) {
			// Do algorithm iteration
			algorithm.doIterate();
			// Fire iterationCompleted event
			fireIterationCompleted();
		}
		// Fire algorithmFinished event
		fireAlgorithmFinished();		
	}

	/**
	 * <p>
	 * Reads schema from the KEEL file
	 * </p>
	 * @param jobFilename Name of the KEEL dataset file
	 */
	
	private static byte[] readSchema(String fileName) throws IOException, DatasetException{

		KeelDataSet dataset = new KeelDataSet(fileName);
		dataset.open();		

		File file = new File(fileName);

		List<String> inputIds = new ArrayList<String>();
		List<String> outputIds = new ArrayList<String>();

		Reader reader = new BufferedReader(new FileReader(file));			
		String line = ((BufferedReader) reader).readLine();
                line = line.replace("real[","real [");
                line = line.replace("integer[","integer [");
                line = line.replace("{"," {");
		StringTokenizer elementLine = new StringTokenizer(line);
		String element = elementLine.nextToken();

		while (!element.equalsIgnoreCase("@data")){

			if(element.equalsIgnoreCase("@inputs")){
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						inputIds.add(commaTokenizer.nextToken());
				}
			}
			else if(element.equalsIgnoreCase("@outputs")){					
				while(elementLine.hasMoreTokens()){
					StringTokenizer commaTokenizer = new StringTokenizer(elementLine.nextToken(),",");
					while(commaTokenizer.hasMoreTokens())
						outputIds.add(commaTokenizer.nextToken());	
				}
			}

			// Next line of the file
			line = ((BufferedReader) reader).readLine();
			while(line.startsWith("%") || line.equalsIgnoreCase(""))
				line = ((BufferedReader) reader).readLine();
                        
                        line = line.replace("real[","real [");
                        line = line.replace("integer[","integer [");
                        line = line.replace("{"," {");

			elementLine = new StringTokenizer(line);
			element = elementLine.nextToken();
		}

		IMetadata metadata = dataset.getMetadata();
		byte[] schema = new byte[metadata.numberOfAttributes()];

		if(inputIds.isEmpty() || outputIds.isEmpty()){
			for(int i=0; i<schema.length; i++){
				if(i!=(schema.length-1))
					schema[i] = 1;
				else{
					IAttribute outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					consoleReporter.setOutputAttribute(outputAttribute);
				}
			}
		}
		else{
			for(int i=0; i<schema.length; i++){
				if(inputIds.contains(metadata.getAttribute(i).getName()))
					schema[i] = 1;
				else if(outputIds.contains(metadata.getAttribute(i).getName())){
					IAttribute outputAttribute = metadata.getAttribute(i);
					schema[i] = 2;
					consoleReporter.setOutputAttribute(outputAttribute);
				}
				else
					schema[i] = -1;
			}
		}
		

		
		StringBuffer header = new StringBuffer();
		header.append("@relation " + dataset.getName() + "\n");
		for(int i=0; i<metadata.numberOfAttributes(); i++){
			IAttribute attribute = metadata.getAttribute(i);
			header.append("@attribute " + attribute.getName() +" ");
			if(attribute.getType() == AttributeType.Categorical ){
				CategoricalAttribute catAtt = (CategoricalAttribute) attribute;
				
				Interval interval = catAtt.intervalValues();
				
				header.append("{");
				for(int j=(int)interval.getLeft(); j<=interval.size()+1; j++){
					header.append( catAtt.show(j)+ (j!=interval.size()+1?",":"}\n"));
				}
			}
			else if(attribute.getType() == AttributeType.IntegerNumerical ){
				IntegerNumericalAttribute intAtt = (IntegerNumericalAttribute) attribute;
				header.append("integer[" + (int) intAtt.intervalValues().getLeft() + "," + (int) intAtt.intervalValues().getRight() +"]\n");
			}
			else if(attribute.getType() == AttributeType.DoubleNumerical ){
				RealNumericalAttribute doubleAtt = (RealNumericalAttribute) attribute;
				header.append("real[" + doubleAtt.intervalValues().getLeft() + "," + doubleAtt.intervalValues().getRight() +"]\n");
			}
		}
		header.append("@data\n");
		consoleReporter.setHeader(header.toString());		
		
		dataset.close();
		return schema;
	}
}



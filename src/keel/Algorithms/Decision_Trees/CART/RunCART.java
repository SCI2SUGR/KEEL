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
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;

import keel.Algorithms.Decision_Trees.CART.dataset.DataSetManager;
import keel.Algorithms.Decision_Trees.CART.impurities.IImpurityFunction;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.KeelDataSet;

/**
 * Main class for CART algorithm.
 * CART: Classification And Regression Trees (Breiman and al., 1984) CART are binary trees 
 * 
 */
public abstract class RunCART 
{
	/** Algorithm */
	protected CART cartAlgorithm;

	/** The name of the file that contains the information to build the model. */
	protected static String modelFileName = "";

	/** The name of the file that contains the information to make the training. */
	protected static String trainFileName = "";
	protected DoubleTransposedDataSet trainData;

	/** The name of the file that contains the information to make the test. */
	protected static String testFileName = "";
	protected DoubleTransposedDataSet testData;

	/** The name of the train output file. */
	protected static String trainOutputFileName;

	/** The name of the test output file. */
	protected static String testOutputFileName;

	/** The name of the result file. */
	protected static String resultFileName;

	/** Number of parameters of the algorithm. */
	private int nParam = 3;

	/** The instant of starting the algorithm. */
	private long startTime;

	/** Maximum allowed depth of the tree */
	private int maxDepth;	

	/** Impurity function to use. Regression or Classification depends on this */
	protected static IImpurityFunction impurityFunction;

	/**
	 * Default constructor
	 * @param file parameter file
	 * @param regression set at true if tree is used for regression 
	 * and if false is used for classification
	 */
	public RunCART(String file, boolean regression) {
		// starts the time
		startTime = System.currentTimeMillis();

		try {
			/* Sets the options of the execution */
			StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new
					FileReader(file)));
			initTokenizer(tokenizer);
			setOptions(tokenizer);

			System.out.println("trainFileName: " +trainFileName + " testFileName: "+ testFileName);

			// open the file data

			KeelDataSet trainKeel = new KeelDataSet(trainFileName);
			trainData = new DoubleTransposedDataSet();
			trainData.read(DataSetManager.readSchema(trainFileName), trainKeel);

			KeelDataSet testKeel = new KeelDataSet(testFileName);
			testData = new DoubleTransposedDataSet();
			testData.read(DataSetManager.readSchema(testFileName), testKeel);

			// create the algorithm giving the building patterns
			cartAlgorithm = new CART(trainData);

			// configure the algorithm

			cartAlgorithm.setImpurityFunction(impurityFunction);
			cartAlgorithm.setRegression(regression);
			cartAlgorithm.setMaxDepth(maxDepth);

			// Build tree
			cartAlgorithm.build_tree();

			// Prune tree
			cartAlgorithm.prune_tree();

			System.out.println("Algorithm finished ("+(System.currentTimeMillis()-startTime)+")");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Function to initialize the stream tokenizer.
	 *
	 * @param tokenizer		The tokenizer.
	 */
	private void initTokenizer(StreamTokenizer tokenizer) {
		tokenizer.resetSyntax();
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.wordChars(' ' + 1, '\u00FF');
		tokenizer.whitespaceChars(',', ',');
		tokenizer.quoteChar('"');
		tokenizer.quoteChar('\'');
		tokenizer.ordinaryChar('=');
		tokenizer.ordinaryChar('{');
		tokenizer.ordinaryChar('}');
		tokenizer.ordinaryChar('[');
		tokenizer.ordinaryChar(']');
		tokenizer.eolIsSignificant(true);
	}


	/** Function to read the options from the execution file and assign
	 * the values to the parameters.
	 *
	 * @param options 		The StreamTokenizer that reads the parameters file.
	 *
	 * @throws Exception	If the format of the file is not correct.
	 */
	protected void setOptions(StreamTokenizer options) throws Exception {
		options.nextToken();

		/* Checks that the file starts with the token algorithm */
		if (options.sval.equalsIgnoreCase("algorithm")) {
			options.nextToken();
			options.nextToken();

			/* Check algorithm name
			if (!options.sval.equalsIgnoreCase("CART")) {
				throw new Exception("The name of the algorithm is not correct.");
			}
			 */

			options.nextToken();
			options.nextToken();

			/* Reads the names of the input files*/
			if (options.sval.equalsIgnoreCase("inputData")) {
				options.nextToken();
				options.nextToken();
				modelFileName = options.sval;

				if (options.nextToken() != StreamTokenizer.TT_EOL) {
					trainFileName = options.sval;
					options.nextToken();
					testFileName = options.sval;
					if (options.nextToken() != StreamTokenizer.TT_EOL) {
						trainFileName = modelFileName;
						options.nextToken();
					}
				}
			} else {
				throw new Exception("No file test provided.");
			}

			/* Reads the names of the output files*/
			while (true) {
				if (options.nextToken() == StreamTokenizer.TT_EOF) {
					throw new Exception("No output file provided.");
				}

				if (options.sval == null) {
					continue;
				} else if (options.sval.equalsIgnoreCase("outputData")) {
					break;
				}
			}

			options.nextToken();
			options.nextToken();
			trainOutputFileName = options.sval;
			options.nextToken();
			testOutputFileName = options.sval;
			options.nextToken();
			resultFileName = options.sval;

			if (!getNextToken(options)) {
				throw new Exception("No instances provided.");
			}

			if (options.ttype == StreamTokenizer.TT_EOF) {
				return;
			}

			for (int k = 0; k < nParam; k++) {

				/* Reads the maxDepth parameter */
				if (options.sval.equalsIgnoreCase("maxDepth")) {
					options.nextToken();
					options.nextToken();

					if (Integer.parseInt(options.sval) > 0) {
						maxDepth = Integer.parseInt(options.sval);
					}

					if (!getNextToken(options)) {
						return;
					} else {
						continue;
					}
				}
				
				/* Any other parameter should be added here */

			} // end for
		} else {
			throw new Exception("The file must start with the word " +
			"algorithm followed of the name of the algorithm.");
		}
	}


	/** Puts the tokenizer in the first token of the next line.
	 *
	 * @param tokenizer		The tokenizer which reads this function.
	 *
	 * @return				True if reaches the end of file. False otherwise.
	 *
	 * @throws Exception	If cannot read the tokenizer.
	 */
	private boolean getNextToken(StreamTokenizer tokenizer) {
		try {
			if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
				return false;
			} else {
				tokenizer.pushBack();

				while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
					;
				}

				while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
					;
				}

				if (tokenizer.sval == null) {
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());

			return false;
		}
	}


}


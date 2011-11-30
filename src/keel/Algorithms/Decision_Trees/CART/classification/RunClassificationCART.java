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

package keel.Algorithms.Decision_Trees.CART.classification;

import keel.Algorithms.Decision_Trees.CART.ResultPrinter;
import keel.Algorithms.Decision_Trees.CART.RunCART;
import keel.Algorithms.Decision_Trees.CART.dataset.DataSetManager;
import keel.Algorithms.Decision_Trees.CART.impurities.Gini;

/**
 * 
 * Class to run the CART algorithm
 *
 */
public class RunClassificationCART extends RunCART {

	/**
	 * Default constructor
	 * @param file
	 */
	public RunClassificationCART(String file) {
		super(file, false);
		
		// Get train error
		byte [][] trainResults = cartAlgorithm.getClassificationResults(trainData);
		
		// Get test error
		byte [][] testResults = cartAlgorithm.getClassificationResults(testData);
		
		// Print file of test results
		ResultPrinter printer = new ResultPrinter();
		printer.setHeader(DataSetManager.getHeader());
		printer.setOutputAttribute(DataSetManager.getOutputAttribute());
		printer.setTrainResultFile(trainOutputFileName);
		printer.setTestResultFile(testOutputFileName);
		printer.setTrainData(trainData);
		printer.setTestData(testData);
		
		printer.writeResults(trainResults, testResults);
		
		// Optionally print model
		printer.setModelResultFile(resultFileName);
		printer.printModelFile(cartAlgorithm.getTree());
		
		
		/* DEBUG Print results
		IErrorFunction ccr = new CCR();
		ccr.setRoot(cartAlgorithm.getTree().getRoot());
		System.out.println("Train CCR: "+ccr.getError(trainResults, trainData));
		System.out.println("Test CCR: "+ccr.getError(testResults, testData));
		*/
	}

	
	/**
	 * 
	 * It runs the CART method for classification
	 * 
	 * @param args the configuration file must be given in the arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("\nError: you have to specify the parameters file\n\tusage: java -jar CART.jar parameterfile.txt");
			return;
		}

		else {
			impurityFunction = new Gini();
			RunCART execution = new RunClassificationCART(args[0]);
		}

	}
	
}


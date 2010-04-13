/**
* <p>
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART.regression;

import keel.Algorithms.Decision_Trees.CART.ResultPrinter;
import keel.Algorithms.Decision_Trees.CART.RunCART;
import keel.Algorithms.Decision_Trees.CART.dataset.DataSetManager;
import keel.Algorithms.Decision_Trees.CART.impurities.LeastSquaresDeviation;

public class RunRegressionCART extends RunCART {

	/**
	 * Default constructor
	 * @param file
	 */
	public RunRegressionCART(String file) {
		super(file, true);
		
		// Get train error
		double [] trainResults = cartAlgorithm.getRegressionResults(trainData);
		
		// Get test error
		double [] testResults = cartAlgorithm.getRegressionResults(testData);
		
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
		
		/* 
		print MSE
		MSEErrorFunction error = new MSEErrorFunction();
		System.out.println("MSE train: "+error.calculateError(trainResults, trainData.getOutput(0)));
		System.out.println("MSE test: "+error.calculateError(testResults, testData.getOutput(0)));
		*/
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("\nError: you have to specify the parameters file\n\tusage: java -jar CART.jar parameterfile.txt");
			return;
		}

		else {
			impurityFunction = new LeastSquaresDeviation();
			RunCART execution = new RunRegressionCART(args[0]);
		}

	}
	
}

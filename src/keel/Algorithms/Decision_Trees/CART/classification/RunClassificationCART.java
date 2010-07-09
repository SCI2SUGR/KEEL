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
	 * @param args
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

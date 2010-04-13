/**
* <p>
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;

public class ResultPrinter {
	
	
	/////////////////////////////////////////////////////////////////////
	// ------------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////////
	
	/** KEEL headers of output files */

	private String header;

	/** Train result file */

	private String trainResultFile;

	/** Test result file */

	private String testResultFile;

	/** Best model result file */

	private String ModelResultFile;

	/** Metadata information of output attribute for generating output files */

	private IAttribute outputAttribute;
	
	/** Train data set */
	private DoubleTransposedDataSet trainData;
	
	/** Test data set */
	private DoubleTransposedDataSet testData;

	
	/////////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Getters and Setters
	/////////////////////////////////////////////////////////////////////
	

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the trainResultFile
	 */
	public String getTrainResultFile() {
		return trainResultFile;
	}

	/**
	 * @param trainResultFile the trainResultFile to set
	 */
	public void setTrainResultFile(String trainResultFile) {
		this.trainResultFile = trainResultFile;
	}

	/**
	 * @return the testResultFile
	 */
	public String getTestResultFile() {
		return testResultFile;
	}

	/**
	 * @param testResultFile the testResultFile to set
	 */
	public void setTestResultFile(String testResultFile) {
		this.testResultFile = testResultFile;
	}

	/**
	 * @return the modelResultFile
	 */
	public String getModelResultFile() {
		return ModelResultFile;
	}

	/**
	 * @param modelResultFile the modelResultFile to set
	 */
	public void setModelResultFile(String modelResultFile) {
		ModelResultFile = modelResultFile;
	}

	/**
	 * @return the outputAttribute
	 */
	public IAttribute getOutputAttribute() {
		return outputAttribute;
	}

	/**
	 * @param outputAttribute the outputAttribute to set
	 */
	public void setOutputAttribute(IAttribute outputAttribute) {
		this.outputAttribute = outputAttribute;
	}

	/**
	 * @return the trainData
	 */
	public DoubleTransposedDataSet getTrainData() {
		return trainData;
	}

	/**
	 * @param trainData the trainData to set
	 */
	public void setTrainData(DoubleTransposedDataSet trainData) {
		this.trainData = trainData;
	}

	/**
	 * @return the testData
	 */
	public DoubleTransposedDataSet getTestData() {
		return testData;
	}

	/**
	 * @param testData the testData to set
	 */
	public void setTestData(DoubleTransposedDataSet testData) {
		this.testData = testData;
	}

	/////////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Public Methods
	/////////////////////////////////////////////////////////////////////


	
	/**
	 * This method print the output file in Keel format for classification problems
	 * 
	 * @param predicted train. Output class as binary in rows, patterns in cols.
	 * @param
	 */ 

	@SuppressWarnings("unchecked")
	public void writeResults(byte[][] predictedTrain, byte[][] predictedTest) {

		try 
		{

			PrintWriter print = new PrintWriter( new FileWriter ( trainResultFile ) );
			print.write(header);

			
			double[][] observedOutputs = trainData.getAllOutputs();

			// Print train results		
			for(int i=0; i<trainData.getNofobservations(); i++){
				int observedClass = 1;
				while(observedOutputs[observedClass-1][i]!=1)
					observedClass++;
				print.write(outputAttribute.show(observedClass) + " ");

				int predictedClass = 1;
				while(predictedTrain[predictedClass-1][i]!=1)
					predictedClass++;
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();

			// Print test results		
			print = new PrintWriter( new FileWriter ( testResultFile ) );
			print.write(header);

			observedOutputs = testData.getAllOutputs();
			
			for(int i=0; i<testData.getNofobservations(); i++){
				int observedClass = 1;
				while(observedOutputs[observedClass-1][i]!=1)
					observedClass++;
				print.write(outputAttribute.show(observedClass) + " ");

				int predictedClass = 1;
				while(predictedTest[predictedClass-1][i]!=1)
					predictedClass++;
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();
		}
		catch ( IOException e )
		{
			System.err.println( "Can not open the training output file: " + e.getMessage() );
		}
	}
	
	/**
	 * TODO Must be checked
	 * 
	 * This method print the output file in Keel format for regression problems
	 * 
	 * @param predicted train. Output class as binary in rows, patterns in cols.
	 * @param
	 */ 

	@SuppressWarnings("unchecked")
	public void writeResults(double [] predictedTrain, double[] predictedTest) {

		try 
		{

			PrintWriter print = new PrintWriter( new FileWriter ( trainResultFile ) );
			print.write(header);

			
			double[] observedOutputs = trainData.getAllOutputs()[0];			                                                     ;

			// Print train results		
			for(int i=0; i<trainData.getNofobservations(); i++){
				double observedClass = observedOutputs[i];
				print.write(outputAttribute.show(observedClass) + " ");

				double predictedClass = predictedTrain[i];
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();

			// Print test results		
			print = new PrintWriter( new FileWriter ( testResultFile ) );
			print.write(header);

			observedOutputs = testData.getAllOutputs()[0];
			
			for(int i=0; i<testData.getNofobservations(); i++){
				double observedClass = observedOutputs[i];
				print.write(outputAttribute.show(observedClass) + " ");

				double predictedClass = predictedTest[i];
				print.write(outputAttribute.show(predictedClass) + "\n");
			}

			print.close();
		}
		catch ( IOException e )
		{
			System.err.println( "Can not open the training output file: " + e.getMessage() );
		}
	}
	
	/**
	 * Print result model tree 
	 * @param tree
	 */
	
	public void printModelFile(Object result) 
	{
		// Print result model
		
		PrintWriter print;
		try {
			print = new PrintWriter( new FileWriter ( ModelResultFile ) );
			// Print tree
			print.write(result.toString());
			
			// Close file
			print.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}   			
	}

	
}

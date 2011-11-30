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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IAttribute;

/**
 * 
 * Class to print the results of the CART algorithm
 *
 */
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
	 * It returns the header
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * It sets the header
	 * 
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * It returns the training results file
	 * 
	 * @return the trainResultFile
	 */
	public String getTrainResultFile() {
		return trainResultFile;
	}

	/**
	 * It sets the training results file
	 * 
	 * @param trainResultFile the trainResultFile to set
	 */
	public void setTrainResultFile(String trainResultFile) {
		this.trainResultFile = trainResultFile;
	}

	/**
	 * It gets the test result file
	 * 
	 * @return the testResultFile
	 */
	public String getTestResultFile() {
		return testResultFile;
	}

	/**
	 * It sets the test result file
	 * 
	 * @param testResultFile the testResultFile to set
	 */
	public void setTestResultFile(String testResultFile) {
		this.testResultFile = testResultFile;
	}

	/**
	 * It gets the model result file
	 * 
	 * @return the modelResultFile
	 */
	public String getModelResultFile() {
		return ModelResultFile;
	}

	
	/**
	 * It sets the model result file
	 * 
	 * @param modelResultFile the modelResultFile to set
	 */
	public void setModelResultFile(String modelResultFile) {
		ModelResultFile = modelResultFile;
	}

	/**
	 * If returns the output attribute
	 * 
	 * @return the outputAttribute
	 */
	public IAttribute getOutputAttribute() {
		return outputAttribute;
	}

	/**
	 * It sets the output attribute
	 * 
	 * @param outputAttribute the outputAttribute to set
	 */
	public void setOutputAttribute(IAttribute outputAttribute) {
		this.outputAttribute = outputAttribute;
	}

	/**
	 * Ir returns the training data
	 * 
	 * @return the trainData
	 */
	public DoubleTransposedDataSet getTrainData() {
		return trainData;
	}

	/**
	 * 
	 * It sets the training data
	 * 
	 * @param trainData the trainData to set
	 */
	public void setTrainData(DoubleTransposedDataSet trainData) {
		this.trainData = trainData;
	}

	/**
	 * It gets the test data
	 * 
	 * @return the testData
	 */
	public DoubleTransposedDataSet getTestData() {
		return testData;
	}

	/**
	 * 
	 * It sets the test data
	 * 
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
	 * @param predictedTrain Output class as binary in rows, patterns in cols.
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
	 * 
	 * This method print the output file in Keel format for regression problems
	 * 
	 * @param predictedTrain Output class as binary in rows, patterns in cols.
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
	 * @param result
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


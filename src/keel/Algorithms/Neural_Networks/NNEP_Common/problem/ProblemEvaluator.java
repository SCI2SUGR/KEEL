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

package keel.Algorithms.Neural_Networks.NNEP_Common.problem;

import java.io.IOException;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DatasetException;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.IDataset;
import keel.Algorithms.Neural_Networks.NNEP_Common.util.normalizer.Normalizer;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractEvaluator;
import net.sf.jclec.util.range.Interval;

import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class ProblemEvaluator<I extends IIndividual> extends AbstractEvaluator<I> implements IProblem {
	
	/**
	 * <p>
	 * Abstract implementation of an individuals evaluator of a dataset problem
	 * </p>
	 */
	
    /////////////////////////////////////////////////////////////////
    // --------------------------------------------------- Properties
    /////////////////////////////////////////////////////////////////
    
    /** Unscaled train DataSet with data to evaluate the individuals */
    
    protected DoubleTransposedDataSet unscaledTrainData;
    
    /** Scaled train DataSet with data to evaluate the individuals */
    
    protected DoubleTransposedDataSet scaledTrainData;
    
    /** Unscaled test DataSet with data to evaluate the individuals */
    
    protected DoubleTransposedDataSet unscaledTestData;
    
    /** Scaled test DataSet with data to evaluate the individuals */
    
    protected DoubleTransposedDataSet scaledTestData;
    
    /** normalize data ? */
    
    protected boolean dataNormalized;
    
    /** Normalizer used to normalizer the trainData */
    
    protected Normalizer normalizer;

    /** Normalization input interval */
    
    protected Interval inputInterval;

    /** Normalization input interval */
    
    protected Interval outputInterval;

    /** Logarithm transformation */

    protected boolean logTransformation;
    
    /** Auxiliary arrays */
    protected double [] unscaledMin, unscaledMax;    
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    public ProblemEvaluator() {
        super();
    }
    
    /////////////////////////////////////////////////////////////////
    // ------------------------------- Getting and setting properties
    /////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Returns the train data associated to this evaluator
     * </p>
     * @return DataSet Train data set
     */
    public DoubleTransposedDataSet getTrainData() {
        if(dataNormalized)
            return scaledTrainData;
        else
            return unscaledTrainData;
    }
    
    /**
     * <p>
     * Returns the test data associated to this evaluator
     * </p>
     * @return DataSet Test data set
     */
    public DoubleTransposedDataSet getTestData() {
        if(dataNormalized)
            return scaledTestData;
        else
            return unscaledTestData;
    }
    
    /**
     * <p>
	 * Returns a boolean value indicating if the DataSets are going to be normalized
     * </p>
	 * @return true if DataSets going to be normalized
	 */
    public boolean isDataNormalized() {
        return dataNormalized;
    }
    
    /**
     * <p>
	 * Sets a boolean value indicating if the DataSets are going to be normalized
     * </p>
	 * @param normalizeData  Boolean DataSets going to be normalized
	 */
    public void setDataNormalized(boolean normalizeData) {
        this.dataNormalized = normalizeData;
    }
    
    /**
     * <p>
	 * Returns a boolean value indicating if the DataSets are going to be log
	 * transformated
     * </p>
	 * @return true if DataSets going to be transformated
	 */
    public boolean isLogTransformation() {
        return logTransformation;
    }
    
    /**
     * <p>
	 * Sets a boolean value indicating if the DataSets are going to be log
	 * transformated
     * </p>
	 * @param logTransformation  Boolean DataSets going to be transformated
	 */
    public void setLogTransformation(boolean logTransformation) {
        this.logTransformation = logTransformation;
    }
    
    /**
     * <p>
	 * Returns the normalizer associated to the trainData DataSet
     * </p>
	 * @return Normalizer Object used to normalize trainData
	 */
    public Normalizer getNormalizer() {
        return normalizer;
    }
    
    /**
     * <p>
	 * Sets the normalizer associated to the trainData DataSet
     * </p>
	 * @param normalizer New Normalizer to be used
	 */
    public void setNormalizer(Normalizer normalizer) {
        this.normalizer = normalizer;
    }
    
    /**
     * <p>
	 * Returns the DataSet associated to the evaluator as unscaled test data
     * </p>
	 * @return DataSet DataSet used as unscaled test data
	 */
	public DoubleTransposedDataSet getUnscaledTestData() {
		return unscaledTestData;
	}
	
    /**
     * <p>
	 * Sets the DataSet associated to the evaluator as unscaled test data
     * </p>
	 * @param unscaledTestData New Dataset to be used
	 */
	public void setUnscaledTestData(DoubleTransposedDataSet unscaledTestData) {
		this.unscaledTestData = unscaledTestData;
	}
	
    /**
     * <p>
	 * Returns the DataSet associated to the evaluator as unscaled train data
     * </p>
	 * @return DataSet DataSet used as unscaled train data
	 */
	public DoubleTransposedDataSet getUnscaledTrainData() {
		return unscaledTrainData;
	}
	
    /**
     * <p>
	 * Sets the DataSet associated to the evaluator as unscaled train data
     * </p>
	 * @param unscaledTrainData New Dataset to be used
	 */
	public void setUnscaledTrainData(DoubleTransposedDataSet unscaledTrainData) {
		this.unscaledTrainData = unscaledTrainData;
	}
	
    /**
     * <p>
	 * Returns the input interval of normalized data
     * </p>
	 * @return Interval Input normalization interval
	 */
	public Interval getInputInterval() {
		return inputInterval;
	}
	
    /**
     * <p>
	 * Sets the input interval of normalized data
     * </p>
	 * @param inputInterval New input interval range
	 */
	public void setInputInterval(Interval inputInterval) {
		this.inputInterval = inputInterval;
	}

    /**
     * <p>
	 * Returns the input interval of normalized data
     * <p>
	 * @return Interval Output normalization interval
	 */
	public Interval getOutputInterval() {
		return outputInterval;
	}
	
    /**
     * <p>
	 * Sets the output range of normalized data
     * </p>
	 * @param outputRange New output normalization range
	 */
	public void setOutputInterval(Interval outputRange) {
		this.outputInterval = outputRange;
	}
	
    /**
     * <p>
	 * Returns the array of minimum values in datasets
     * </p>
	 * @return double[] Array of minimum values in datasets
	 */
	public double[] getUnscaledMin() {
		return unscaledMin;
	}
	
    /**
     * <p>
	 * Returns the array of maximum values in datasets
     * </p>
	 * @return double[] Array of maximum values in datasets
	 */
	public double[] getUnscaledMax() {
		return unscaledMax;
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IConfigure interface
	/////////////////////////////////////////////////////////////////
    
	/**
     * <p>
	 * Configuration parameters for NeuralNetEvaluator are:
	 * 
	 * <ul>
	 * <li>
	 * <code>train-data: complex</code></p> 
	 * Train data set used in individuals evaluation.
	 * <ul>
	 * 		<li>
	 * 		<code>train-data[@file-name] String </code>
	 * 		File name of train data
	 * 		</li>
	 * </ul> 
	 * </li>
	 * <li>
	 * <code>test-data: complex</code></p> 
	 * Test data set used in individuals evaluation.
	 * <ul>
	 * 		<li>
	 * 		<code>test-data[@file-name] String </code>
	 * 		File name of test data
	 * 		</li>
	 * </ul> 
	 * </li>
	 * <li>
	 * <code>[@normalize-data]: boolean (default = false)</code></p>
	 * If this parameter is set to <code>true</true> data sets values are
	 * normalizated after reading their contents
	 * </li>
	 * <li>
	 * <code>[input-interval] (complex)</code></p>
	 *  Input interval of normalization.
	 * </li>
	 * <li>
	 * <code>[output-interval] (complex)</code></p>
	 *  Output interval of normalization.
	 * </li>
	 * </ul>
     * <p>
     * @param settings Configuration object from which the properties are read
	 */
    public void configure(Configuration settings) {
    	
        // Set trainData
        unscaledTrainData = new DoubleTransposedDataSet();
        unscaledTrainData.configure(settings.subset("train-data"));
        
        // Set testData
        unscaledTestData = new DoubleTransposedDataSet();
        unscaledTestData.configure(settings.subset("test-data"));
        
        // Set normalizer
        normalizer = new Normalizer();
        
        // Set dataNormalized
        dataNormalized = settings.getBoolean("[@normalize-data]", false);
        
        // Set dataNormalized
        logTransformation = settings.getBoolean("[@log-input-data]", false);
        
        if(dataNormalized){
			// Normalization Input Interval
			Interval interval = new Interval();
			// Configure interval
			interval.configure(settings.subset("input-interval"));
			// Set interval
			setInputInterval(interval);
			// Normalization Output Interval
			interval = new Interval();
			// Configure range
			interval.configure(settings.subset("output-interval"));
			// Set interval
			setOutputInterval(interval);
        }
    }
    
    /**
     * <p> 
     * Read and normalize evaluator datasets
     * </p>
     * @throws IOException Data not correct
     * @throws NumberFormatException Format of data not correct
     */
    public void readData() throws IOException, NumberFormatException{
    	
        // Read trainData
        try{
            unscaledTrainData.read();
        }
        catch(IOException e){
            throw new IOException("trainData IOException: " + e.getLocalizedMessage());
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("trainData NumberFormatException: " + e.getLocalizedMessage());
        }
        
        // Read testData
        try{
            unscaledTestData.read();
        }
        catch(IOException e){
            throw new IOException("testData IOException: " + e.getLocalizedMessage());
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("testData NumberFormatException: " + e.getLocalizedMessage());
        }        

        normalizeData();
    }
    
    /**
     * <p>
     * Read and normalize evaluator datasets
     * </p>
     * @param schema Schema of the dataset
     * @param traindata IDataset with the training data
     * @param testdata IDataset with the test data
     */
    public void readData(byte[] schema, IDataset traindata, IDataset testdata){
    	
        // Read trainData
        try {
        	unscaledTrainData.read(schema, traindata);
		} catch (DatasetException e1) {
			e1.printStackTrace();
		}
        
        // Read testData
        try{
            unscaledTestData.read(schema, testdata);
        } catch (DatasetException e1) {
			e1.printStackTrace();
		}

        normalizeData();
    }
   
    /**
     * <p>
     * Normalize data
     * </p>
     */
    private void normalizeData(){
    	if(dataNormalized){
            // Obtain maximum and minimum values
        	unscaledMin = new double[unscaledTrainData.getNofvariables()];
        	unscaledMax = new double[unscaledTrainData.getNofvariables()];        		
        	double temp;
        	for(int i=0; i<unscaledTrainData.getNofvariables(); i++){
                unscaledMax[i] = unscaledTrainData.getMaxValueOf(i);
                temp = unscaledTestData.getMaxValueOf(i);
                if(temp>unscaledMax[i])
                	unscaledMax[i] = temp;
                unscaledMin[i] = unscaledTrainData.getMinValueOf(i);
                temp = unscaledTestData.getMinValueOf(i);
                if(temp<unscaledMin[i])
                	unscaledMin[i] = temp;
            }
            
            //Obtain maximum and minimum desired values
            double [] scaledMin = new double[unscaledTrainData.getNofvariables()];
            double [] scaledMax = new double[unscaledTrainData.getNofvariables()];
            for(int i=0; i<unscaledTrainData.getNofvariables(); i++){
            	if(i<unscaledTrainData.getNofinputs()){
            		scaledMin[i] = inputInterval.getLeft();
            		scaledMax[i] = inputInterval.getRight();
            	}
            	else{
            		scaledMin[i] = outputInterval.getLeft();
            		scaledMax[i] = outputInterval.getRight();
            	}
            }

            // Normalize trainData
            scaledTrainData = unscaledTrainData.copy();
            normalizer.scaleDS(scaledTrainData, scaledMax, scaledMin, unscaledMax, unscaledMin);
            
            // Normalize trainData
            scaledTestData = unscaledTestData.copy();
            normalizer.scaleDS(scaledTestData, scaledMax, scaledMin, unscaledMax, unscaledMin);
            
        }

        // Remove constant inputs
        int newNofinputs = 0;
        boolean[] toRemove = unscaledTrainData.obtainConstantsInputs();
        
        for(int i=0; i<toRemove.length; i++) 
        	if(!toRemove[i])
        		newNofinputs++;
        
        unscaledTrainData.removeInputs(toRemove,newNofinputs);
        unscaledTestData.removeInputs(toRemove,newNofinputs);
        
	    if(dataNormalized){
	        scaledTrainData.removeInputs(toRemove,newNofinputs);
	        scaledTestData.removeInputs(toRemove,newNofinputs);
	        
	        // Log transformation
		    if(logTransformation){
		    	double[][] inputs = scaledTrainData.getAllInputs();
		    	
		    	for(int i=0; i<inputs.length; i++)
		    		for(int j=0; j<inputs[i].length; j++)
		    			inputs[i][j] = Math.log(inputs[i][j]);
		    	
		    	inputs = scaledTestData.getAllInputs();
		    	
		    	for(int i=0; i<inputs.length; i++)
		    		for(int j=0; j<inputs[i].length; j++)
		    			inputs[i][j] = Math.log(inputs[i][j]);    	
		    		
		    }
		    	
	    }
	    
        // Log transformation
	    else if(logTransformation){
	    	double[][] inputs = unscaledTrainData.getAllInputs();
	    	
	    	for(int i=0; i<inputs.length; i++)
	    		for(int j=0; j<inputs[i].length; j++)
	    			inputs[i][j] = Math.log(inputs[i][j]);
	    	
	    	inputs = unscaledTestData.getAllInputs();
	    	
	    	for(int i=0; i<inputs.length; i++)
	    		for(int j=0; j<inputs[i].length; j++)
	    			inputs[i][j] = Math.log(inputs[i][j]);
	    }
    }
    
    /////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////
    
	@Override
	public abstract void evaluate(I ind);
	
}


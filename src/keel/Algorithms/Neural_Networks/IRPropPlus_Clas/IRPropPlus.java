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

package keel.Algorithms.Neural_Networks.IRPropPlus_Clas;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import net.sf.jclec.IConfigure;

import org.apache.commons.configuration.Configuration;

/**
 * <p> 
 * @author Juan Carlos Fernandez Caballero (University of Cordoba) 27/10/2007
 * @author Modified by Pedro Antonio Gutierrez Peña (University of Cordoba) 27/10/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class IRPropPlus implements IConfigure
{
	/**
	 * <p>
	 * Local search procedure based in a backtraking Strategy
	 * </p>
	 */
			
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////
			
	private static final long serialVersionUID = 6842976526620430756L;
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Initial Delta for RpropPlus Algorithm **/
	
	protected double initialStepSize;
	
	/** Minimum Value for Delta in RpropPlus Algorithm **/
	
	protected double minimumDelta;
		
	/** Maximum Value for Delta in RpropPlus Algorithm **/
	
	protected double maximumDelta;
	
	/** Value of positive Eta **/
	
	protected double positiveEta;
		
	/** Value of negative Eta **/
	
	protected double negativeEta;
	
	/** Value of stop condition **/
	
	protected int epochs;
		
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------ Auxiliar Properties
	/////////////////////////////////////////////////////////////////
	
	/** Input array */    
	
	protected double[][] x;

	/** Output array */    
	
	protected double[][] y;
	
	/** Output errors */
	
	protected double[] error;
	
	/** Current coefficient array */    
	
	protected double[] a;
	
	/** Reduced step size array */    
	
	protected boolean[] reducedStepSize = null;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor.
	 * </p>
	 */
	public IRPropPlus() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Get and set properties
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Returns the maximum delta value, that is, the maximum increment or
	 * step size of the corresponding coefficients
	 * 
	 * @return double Maximum delta value
	 * </p>
	 */
	
	public double getMaximumDelta() {
		return maximumDelta;
	}
	
	/**
	 * <p>
	 * Sets the maximum delta value, that is, the maximum increment or
	 * step size of the corresponding coefficients
	 * 
	 * @param maximumDelta New maximum delta value
	 * </p>
	 */
	
	public void setMaximumDelta(double maximumDelta) {
		this.maximumDelta = maximumDelta;
	}
	
	/**
	 * <p>
	 * Returns the minimum delta value, that is, the minimum increment or
	 * step size of the corresponding coefficients
	 * 
	 * @return double Maximum delta value
	 * </p>
	 */
	
	public double getMinimumDelta() {
		return minimumDelta;
	}
	
	/**
	 * <p>
	 * Sets the minimum delta value, that is, the minimum increment or
	 * step size of the corresponding coefficients
	 * 
	 * @param minimumDelta New minimum delta value
	 * </p>
	 */

	public void setMinimumDelta(double minimumDelta) {
		this.minimumDelta = minimumDelta;
	}
	
	/**
	 * <p>
	 * Returns the negative eta value, that is, the increment
	 * of the step size at each ephoc
	 * 
	 * @return double Negative eta value
	 * </p>
	 */
	
	public double getNegativeEta() {
		return negativeEta;
	}
	
	/**
	 * <p>
	 * Sets the negative eta value, that is, the increment
	 * of the step size at each epoch
	 * 
	 * @param negativeEta New negative eta value
	 * </p>
	 */
	
	public void setNegativeEta(double negativeEta) {
		this.negativeEta = negativeEta;
	}
	
	/**
	 * <p>
	 * Returns the positive eta value, that is, the increment
	 * of the step size at each epoch
	 * 
	 * @return double Positive eta value
	 * </p>
	 */
	
	public double getPositiveEta() {
		return positiveEta;
	}
	
	/**
	 * <p>
	 * Sets the positive eta value, that is, the increment
	 * of the step size at each epoch
	 * 
	 * @param positiveEta New positive eta value
	 * </p>
	 */
	
	public void setPositiveEta(double positiveEta) {
		this.positiveEta = positiveEta;
	}

	/**
	 * <p>
	 * Returns an array of booleans, indicating which coefficients
	 * have to be treated as more sensible, using reduced step size
	 * for these coefficients
	 * 
	 * @return boolean[] The reduced step size boolean array
	 * </p>
	 */
	
	public boolean[] getReducedStepSize() {
		return reducedStepSize;
	}

	/**
	 * <p>
	 * Sets an array of booleans, indicating which coefficients
	 * have to be treated as more sensible, using reduced step size
	 * for these coefficients
	 * 
	 * @param reducedStepSize New reduced step size boolean array
	 * </p>
	 */
	
	public void setReducedStepSize(boolean[] reducedStepSize) {
		this.reducedStepSize = reducedStepSize;
	}
	

	///////////////////////////////////////////////////////////////////
	// ------------------------------- Implementing IOptimizer methods
	////////////////////////////////////////////////////////////////////
	
	/**
	 * <p> 
	 * Sets training data
	 * 
	 * @param trainData DoubleTransposedDataSet to be used in training	 
	 * </p> 
	 */
	
	public void setTrainingData(DoubleTransposedDataSet trainData) {     

        //Input array and error of each observation array
        double[][] x = new double[trainData.getNofobservations()][];
	
		for( int i = 0; i < trainData.getNofobservations(); i++ )
			x[i] = trainData.getInputs(i);
		
		this.x = x;
		
		//Output array
		double[][] y = new double[trainData.getNofobservations()][];
		
		for( int i = 0; i < trainData.getNofobservations(); i++ ) 
			y[i] = trainData.getOutputs(i);
		
		this.y = y;
	}
	
    
	/**
	 * <p>
	 * One local iRprop plus search in a IOptimizableFunc.
	 *
	 * @param function IOptimizableFunc to operate.
	 * @return IOptimizableFunc Optimized function
	 * </p>
	 */
	
	public IOptimizableFunc optimize(IOptimizableFunc function) {

		//Initial values for the algorithm
		double[] initialA = function.getCoefficients();
		this.a = initialA;			

		//Apply the iRprop+
		double[] result = this.solve(function);			

		//Set the coefficients array
		function.setCoefficients(result);

		return function;
	}	
	
	
	///////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Protected methods
	///////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Apply the iRprop+ over a function that implements the IOptimizableFunc
	 * interface.
	 * 
	 * @param f function to apply the iRprop+ method
	 * @return double array with the coefficients optimized
	 * </p>
	 */

	protected double[] solve(IOptimizableFunc f) {
		
		// Stop forced
		int stop = 0;
		
		// Number of epochs without improving error
		int epochsWithoutImproving = 0;
		
		// Actual gradients
		double[] actualGradient = null;
		
		// Last gradients
		double[] lastGradient = new double[this.a.length];
		
		// Step-size
		double[] increaseStepSize = new double[this.a.length];
		
		// Increases
		double[] increaseCoefficients = new double[this.a.length];
		
		// Weights or Coefficients
		double[] coefficients = this.a;
		
		// Actual Error. One error by each input
		double actualError = 0.0;
		
		// Last Error
		double lastError = 0.0;
		
		// Initialization of arrays
		for (int i=0; i<this.a.length; i++)
		{
			lastGradient[i] = increaseCoefficients[i] = 0.0;
			increaseStepSize[i] = this.initialStepSize;
		}
		
		while (stop < this.epochs)			
		{
			// Actual gradient and error for each weight or coefficient
			actualGradient = f.gradient(this.x, this.y);
			actualError = f.getLastError();
			//System.out.println("Epoch: " + stop + " Error: " + actualError);

			// Actualization of coefficients
			// All coefficients treated equally
			if(reducedStepSize==null){
				for (int i=0; i<this.a.length; i++)
				{					
					if( lastGradient[i]*actualGradient[i] > 0)
					{
						increaseStepSize[i] = Math.min(increaseStepSize[i]*this.positiveEta, maximumDelta);
						increaseCoefficients[i] = - (Math.signum(actualGradient[i]) * increaseStepSize[i]); 
						coefficients[i] = coefficients[i] + increaseCoefficients[i];				
					}
					else if (lastGradient[i]*actualGradient[i] == 0)
					{
						increaseCoefficients[i] = - (Math.signum(actualGradient[i]) * increaseStepSize[i]);
						coefficients[i] = coefficients[i] + increaseCoefficients[i];				
					}
					else // (lastGradient[i]*actualGradient[i] < 0) OR Infinite problems
					{
						increaseStepSize[i] = Math.max(increaseStepSize[i]*this.negativeEta, minimumDelta);
						
						if ( actualError > lastError)				 
							coefficients[i] = coefficients[i] - increaseCoefficients[i];
						
						actualGradient[i] = 0.0;
					}
				}//for
			}
			// Actualization of coefficients
			// Some coefficients treated as more sensible
			else{
				for (int i=0; i<this.a.length; i++)
				{					
					// Reduced step size
					double finalPositiveEta = this.positiveEta;
					double finalNegativeEta = this.negativeEta;					
					if(reducedStepSize[i]){
						finalPositiveEta = 1 + ((finalPositiveEta-1)*0.5);
						finalNegativeEta *= 0.5;
					}
					
					if( lastGradient[i]*actualGradient[i] > 0)
					{
						increaseStepSize[i] = Math.min(increaseStepSize[i]*finalPositiveEta, maximumDelta);
						increaseCoefficients[i] = - (Math.signum(actualGradient[i]) * increaseStepSize[i]); 
						coefficients[i] = coefficients[i] + increaseCoefficients[i];				
					}
					else if (lastGradient[i]*actualGradient[i] == 0)
					{
						increaseCoefficients[i] = - (Math.signum(actualGradient[i]) * increaseStepSize[i]);
						coefficients[i] = coefficients[i] + increaseCoefficients[i];				
					}
					else // (lastGradient[i]*actualGradient[i] < 0) OR Infinite problems
					{
						increaseStepSize[i] = Math.max(increaseStepSize[i]*finalNegativeEta, minimumDelta);
						
						if ( actualError > lastError)				 
							coefficients[i] = coefficients[i] - increaseCoefficients[i];
						
						actualGradient[i] = 0.0;
					}
				}//for
			}
			
			if(lastError==actualError){
				epochsWithoutImproving++;
				if(epochsWithoutImproving>=20)
					stop = this.epochs;
			}
			else
				epochsWithoutImproving=0;
			
			// Updating gradient and error for the next step
			for(int i=0; i<this.a.length; i++)
				lastGradient[i] = actualGradient[i];
			lastError = actualError;
			
			f.setCoefficients(coefficients);
			stop++;
		}//While
						
		// Return the result
		return a;
	}
    
    
	/////////////////////////////////////////////////////////////////
	// ---------------------------- Implementing IConfigure interface
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * @param settings Settings Configuration
	 * </p>
	 */
	public void configure(Configuration settings)
	{		
		initialStepSize = settings.getDouble("initial-step-size[@value]", 0.0125);		
		
		minimumDelta = settings.getDouble("minimum-delta[@value]", 0.0);
		
		maximumDelta = settings.getDouble("maximum-delta[@value]", 50.0);
		
		positiveEta = settings.getDouble("positive-eta[@value]", 1.2);
		
		negativeEta = settings.getDouble("negative-eta[@value]", 0.2);
		
		epochs = settings.getInt("cycles[@value]", 25);
	}

}

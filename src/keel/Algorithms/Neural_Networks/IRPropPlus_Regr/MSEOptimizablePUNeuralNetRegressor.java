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

package keel.Algorithms.Neural_Networks.IRPropPlus_Regr;

import keel.Algorithms.Neural_Networks.IRPropPlus_Clas.IOptimizableFunc;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ExpLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;
import keel.Algorithms.Neural_Networks.NNEP_Regr.neuralnet.NeuralNetRegressor;

/**
 * <p>
 * @author Written by Alfonso Carlos Martinez Estudillo (University of Cordoba) 23/11/2007
 * @author Modified by Pedro Antonio Gutierrez Penia (University of Cordoba) 23/11/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class MSEOptimizablePUNeuralNetRegressor extends NeuralNetRegressor implements IOptimizableFunc{
	
	/**
	 * <p>
	 * Product Unit Neural Net with only a hidden layer and multiple outputs (regressor).
	 * Prepared for optimizing MSE.
	 * </p>
	 */
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Last Error */ 
	protected double lastError = 0;

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Returns the initial value of a[], that is, the coefficients of 
	 * the model
	 * B01 B02 ... B0(J-1) [W11 W12 ... B11 B12 B1(J-1)]* ...  
	 * 
	 * @return double array of initial coefficients values
	 * </p>
	 */

	public double[] getCoefficients() {
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();
		int hiddenNeurons = this.getNofhneurons();

		double [] a = new double[inputs*hiddenNeurons + outputs*(hiddenNeurons+1)];

		// For each neuron in hidden layer
		LinkedLayer hl = this.hiddenLayers.get(0);
		LinkedLayer ol = this.outputLayer;
		for(int i=0; i<hl.getNofneurons(); i++){
			LinkedNeuron hn = hl.getNeuron(i);
			Link links[] = hn.getLinks();
			int baseIndex = outputs+i*(inputs+outputs);

			for(int j=0; j<links.length; j++)
				if(!links[j].isBroken())
					a[baseIndex+j] = links[j].getWeight();
				else
					a[baseIndex+j] = 0;

			// For each neuron in output layer
			baseIndex += inputs;
			for(int j=0; j<ol.getNofneurons(); j++){
				LinkedNeuron on = ol.getNeuron(j);
				Link outputLinks[] = on.getLinks();
				if(!outputLinks[i].isBroken())
					a[baseIndex+j] = outputLinks[i].getWeight();
				else
					a[baseIndex+j] = 0;
			}	
		}


		// Bias weights
		if(ol.isBiased())
			for(int j=0; j<ol.getNofneurons(); j++){
				LinkedNeuron on = ol.getNeuron(j);
				Link outputLinks[] = on.getLinks();
				if(!outputLinks[hl.getMaxnofneurons()].isBroken())
					a[j] = outputLinks[hl.getMaxnofneurons()].getWeight();
				else
					a[j] = 0;            			
			}

		return a;
	}

	/**
	 * <p>
	 * Establish the final value of a[], that is, the coefficients of
	 * model
	 * B01 B02 ... B0(J-1) [W11 W12 ... B11 B12 B1(J-1)]* ...  
	 * 
	 * @param a array of final coefficients values
	 * </p>
	 */

	public void setCoefficients(double[] a) {
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();

		// For each neuron in hidden layer
		LinkedLayer hl = this.hiddenLayers.get(0);
		LinkedLayer ol = this.outputLayer;
		for(int i=0; i<hl.getNofneurons(); i++){
			LinkedNeuron hn = hl.getNeuron(i);
			Link links[] = hn.getLinks();
			int baseIndex = outputs+i*(inputs+outputs);

			for(int j=0; j<links.length; j++)
				if(a[baseIndex+j]!=0){
					links[j].setBroken(false);
					links[j].setOrigin(inputLayer.getNeuron(j));
					links[j].setTarget(hn);
					links[j].setWeight(a[baseIndex+j]);
				}

			// For each neuron in output layer
			baseIndex += inputs;
			for(int j=0; j<ol.getNofneurons(); j++){
				LinkedNeuron on = ol.getNeuron(j);
				Link outputLinks[] = on.getLinks();
				if(a[baseIndex+j]!=0){
					outputLinks[i].setBroken(false);
					outputLinks[i].setOrigin(hn);
					outputLinks[i].setTarget(on);
					outputLinks[i].setWeight(a[baseIndex+j]);
				}
			}	
		}


		// Bias weights
		if(ol.isBiased())
			for(int j=0; j<ol.getNofneurons(); j++){
				LinkedNeuron on = ol.getNeuron(j);
				Link outputLinks[] = on.getLinks();
				if(a[j]!=0){
					outputLinks[hl.getMaxnofneurons()].setBroken(false);
					outputLinks[hl.getMaxnofneurons()].setWeight(a[j]);
				}
			}
	}
	
	/** 
	 * <p>
	 * Returns the gradient vector of the derivative of MSE error function
	 * with respect to each coefficient of the model, using an input observation
	 * matrix (x[]) and an expected output matrix (y[])
	 * 
	 * @param x Array with all inputs of all observations
	 * @param y Array with all expected outputs of all observations
	 *  
	 * @return double[] Gradient vector gradient of dE/da for all coefficients
	 * </p>
	 */

	public double[] gradient(double [][] x, double [][] y){

		// Initialize variables
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();
		int params = (this.getNofhneurons()*(inputs+outputs))+outputs;
		int n_pattern = x.length;
		double sum;
		
		// Obtain the error
		lastError = 0.;

		double f[][] = new double[n_pattern][];
		double vdfda [][][] = new double[n_pattern][][];
		double coef[][] = new double[n_pattern][outputs];

		for (int i=0; i < n_pattern; i++) {
			f[i] = new double[1];
			f[i][0] = this.operate(x[i]);
			vdfda[i] = this.dfda(x[i]);
			for(int l = 0; l < outputs; l++){
				coef[i][l] = f[i][l]- y[i][l];
				lastError  += coef[i][l]*coef[i][l];
			}
		}
		//lastError /= (n_pattern*outputs);
		lastError /= 2.;
		
		//	Resulting gradient
		double[] gradient = new double [params];

		for (int j=0; j < params; j++) {
			gradient[j] = 0;
			// For each pattern
			for (int i = 0; i < n_pattern; i++ ){
				sum = 0;
				// For each output
				for(int l = 0; l < outputs; l++)
					sum += coef[i][l] * vdfda[i][j][l];
				gradient[j] = gradient[j] + sum;
			}
		}

		return gradient;
	}
	
	/**
	 * <p>
	 * Last error of the model
	 * 
	 * @return double Error of the function of the model with respect to data y[]
	 * </p>
	 */
	
	public double getLastError(){
		return lastError;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Obtain derivative of each output (f) with respect of each coefficient
	 * for an input observation pattern (x)
	 * 
	 * @param x Input observation array
	 * 
	 * @return double[][] Partial derivatives matrix
	 * </p>
	 */
	
	private double[][] dfda (double[]x) {

		// Initialize variables		
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();
		int params_node = inputs + outputs;
		int hiddenNeurons = this.getNofhneurons();
		int params = hiddenNeurons * params_node + outputs;

		double [] a = this.getCoefficients();
		double [][] vdfda = new double[params][outputs];


		// Initialize vdfda[][]
		for (int i = 0; i < params; i++)
			for (int j = 0; j < outputs; j++)
				vdfda[i][j] = 0 ;

		// Calculate partial derivatives of Beta Bias weights        
		for (int j = 0; j < outputs; j++)
			vdfda[j][j] = 1 ;

		// Calculate rest  of the rest Beta weights
		double sal;
		int baseIndex;
		for (int j = 0; j < hiddenNeurons; j++) {
			// Calculate output of hidden unit j
			baseIndex = outputs + (j * params_node)+ inputs;

			sal = this.getHlayer(0).getNeuron(j).operate(x);
			for (int i = 0; i<outputs; i++)
				vdfda[baseIndex + i][i] = sal;
		}

		boolean exponential = this.getHlayer(0) instanceof ExpLayer;
		// Calculate partial derivative of the w weights        
		for (int j = 0; j < inputs; j++){
			for (int h = 0; h < hiddenNeurons; h++ ){
				baseIndex=(outputs) + (h * params_node);
				for (int l = 0; l < outputs;  l++){
					if(exponential)
						vdfda[baseIndex + j][l] = a[baseIndex + inputs + l] * vdfda[baseIndex + inputs][0] * x[j];
					else
						vdfda[baseIndex + j][l] = a[baseIndex + inputs + l] * vdfda[baseIndex + inputs][0] * Math.log(x[j]);						
				}
			}
		}

		return vdfda;
	}
}


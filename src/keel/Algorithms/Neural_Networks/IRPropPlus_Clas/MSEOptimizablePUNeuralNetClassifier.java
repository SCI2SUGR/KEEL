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

import keel.Algorithms.Neural_Networks.NNEP_Clas.neuralnet.NeuralNetClassifier;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ExpLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;

/**
 * <p>
 * @author Written by Alfonso Carlos Martinez Estudillo (University of Cordoba) 27/10/2007
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 27/10/2007
 * @version 0.1
 * @since JDK1.5
 * <p>
 */

public class MSEOptimizablePUNeuralNetClassifier extends NeuralNetClassifier implements IOptimizableFunc{
	/**
	 * <p>
	 * Product Unit Neural Net with only a hidden layer and multiple outputs (classifier).
     * Prepared for optimizing MSE.
     * <p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Wrapped algorithm */ 
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
	 * <p>
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
	 * <p>
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
	 * <p>
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

		double g[][] = new double[n_pattern][];
		double vdgda [][][] = new double[n_pattern][][];
		double coef[][] = new double[n_pattern][outputs+1];

		for (int i=0; i < n_pattern; i++) {
			g[i] = this.softmaxProbabilities(x[i]);	
			vdgda[i] = this.dgda(x[i], g[i]);
			for(int l = 0; l <= outputs; l++){
				coef[i][l] = g[i][l]- y[i][l];
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
				for(int l = 0; l <= outputs; l++)
					sum += coef[i][l] * vdgda[i][j][l];
				gradient[j] = gradient[j] + sum;
			}
			// gradient[j] = 2*gradient[j];
			// gradient[j] = (2*gradient[j])/(n_pattern*outputs);
		}

		return gradient;
	}
	
	/**
	 * <p>
	 * Last error of the model
	 * 
	 * @return double Error of the function of the model with respect to data y[]
	 * <p>
	 */
	
	public double getLastError(){
		return lastError;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Obtain derivative of each softmax transformed output (g) 
	 * with respect of each coefficient for an input observation
	 * pattern (x)
	 * dg1dB01 dg1dB02 ... dg1dB0(J-1) [dg1dW11 dg1dW12 ... dg1dB11 dg1dB12 dg1dB1(J-1)]* ...
	 * dg2dB01 dg2dB02 ... dg2dB0(J-1) [dg2dW11 dg2dW12 ... dg2dB11 dg2dB12 dg2dB1(J-1)]* ...
	 * ....
	 * dg(j-1)dB01 dg(j-1)dB02 ... dg(j-1)dB0(J-1) [dg(j-1)dW11 dg(j-1)dW12 ... dg(j-1)dB11 dg(j-1)dB12 dg(j-1)dB1(J-1)]* ... 
	 * 
	 * @param x Input observation array
	 * 
	 * @return double[][] Partial derivatives matrix
	 * <p>
	 */

	private double [][] dgda (double[]x, double [] g){

		// Initialize variables		
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();
		int params = (this.getNofhneurons() * (inputs + outputs)) + outputs;       

		double [][] vdgda = new double[params][outputs+1];

		// Calculate outputs of network (f)
		double [] f = this.rawOutputs(x);

		// Calculate partial derivative of f respect all weights
		double [][] vdfda = this.dfda(x);
		
		// Calculate softmax probabilities if they are not previously defined
		if(g==null)
			g = this.applySoftmax(f);

		// Calculate partial derivatives of g respect all weights		
		for(int h = 0; h<params; h++) {
			for(int j = 0; j<=outputs; j++){
				double sum = 0;
				for (int l = 0; l<outputs; l++){
					if (vdfda[h][l] != 0)
						sum+=Math.exp(f[l]) * vdfda[h][l];
				}
				vdgda[h][j]=g[j] * (vdfda[h][j] - (g[j] * Math.exp(-f[j]) * sum));
			}
		}
		
		return vdgda;
	}

	/**
	 * <p>
	 * Obtain derivative of each softmax non transformed output (f) 
	 * with respect of each coefficient for an input observation
	 * pattern (x)
	 * 
	 * @param x Input observation array
	 * 
	 * @return double[][] Partial derivatives matrix
	 * <p>
	 */
	
	private double[][] dfda (double[]x) {

		// Initialize variables		
		int inputs = inputLayer.getMaxnofneurons();
		int outputs = outputLayer.getMaxnofneurons();
		int params_node = inputs + outputs;
		int hiddenNeurons = this.getNofhneurons();
		int params = hiddenNeurons * params_node + outputs;

		double [] a = this.getCoefficients();
		double [][] vdfda = new double[params][outputs+1];


		// Initialize vdfda[][]
		for (int i = 0; i < params; i++)
			for (int j = 0; j <= outputs; j++)
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
	
	/**
	 * <p>
	 * Apply softmax transformation for a set of raw outputs, controlling
	 * problems with very high values
	 * 
	 * @param rawOutputs Array with non transformed raw outputs
	 * 
	 * @return double[] Softmax transformed values
	 * <p>
	 */

	private double[] applySoftmax(double [] rawOutputs) {
		double[] exp = new double[rawOutputs.length];
		
		// Sum of exp(rawOutputs) values
		double expSum = 0;
		for(int i=0; i<rawOutputs.length; i++){
			if(i!=rawOutputs.length-1)
				exp[i] = Math.exp(rawOutputs[i]);
			else
				exp[i] = 1;
			expSum += exp[i];
		}

		// Test problems with very high outputs
		if(Double.isInfinite(expSum) || Double.isNaN(expSum)){
			// Sum of exp(rawOutputs) values
			expSum = 0;
			for(int i=0; i<rawOutputs.length; i++){
				double[] reduced = new double[rawOutputs.length];
				reduced[i] = (rawOutputs[i]/50000.);
				if(i!=rawOutputs.length-1)
					exp[i] = Math.exp(exp[i]);
				else
					exp[i] = 1;
				expSum += exp[i];
			}
		}

		// Normalize outputs
		for(int i=0; i<exp.length; i++)
			exp[i] /= expSum;
		
		return exp;
	}
}


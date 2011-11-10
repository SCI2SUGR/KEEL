/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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

/*

 * rbfn.java

/**
 * <p>
 * @author Writen by Antonio J. Rivera Rivas (University of Ja�n) 03/03/2004
 * @author Modified by V�ctor Rivas (University of Ja�n) 23/06/2006
 * @author Modified by Mar�a Dolores P�rez Godoy (University of Ja�n) 17/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
 
package keel.Algorithms.Neural_Networks.RBFN;

import org.core.*;

import java.util.*;
import java.lang.*;
import java.io.*;

import keel.Algorithms.Neural_Networks.RBFN.RBFUtils;


public class Rbfn {
/**
 * <p>
 * This class codified a Radial Basis Function Network 
 * </p>
 */

	/** Number of RBF neuronas thet net contains. */

	int nRbfs;

	/** Dimension of inputs */

	int nInptuts;

	/** Dimension of outputs (thus, number of output neuron */

	int nOutputs;

	/** Hashtable to store rbf neurons */

	Hashtable rbfn = new Hashtable();

	/**
	 * <p> 
	 * Creates a instance of rbnf of fixed structure just for test.
	 * </p> 
	 */

	public Rbfn() {

		this.nRbfs = 0;

		this.nInptuts = 1;

		this.nOutputs = 1;

		Rbf neurona = new Rbf(1, 1);

		double[] centre = new double[1];

		double[] weights = new double[1];

		double radius;

		centre[0] = 1;
		radius = 0.3;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

		centre[0] = 2;
		radius = 0.2;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

		centre[0] = 2.5;
		radius = 0.1;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

		centre[0] = 2.8;
		radius = 0.3;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

		centre[0] = 3;
		radius = 0.1;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

		centre[0] = 3.3;
		radius = 0.2;
		weights[0] = 0;

		neurona.setParam(centre, radius, weights);

		this.insertRbf((Rbf) neurona.clone());

	}

	/**
     * <p>
     * Creates a new instance of rbfn
     * </p> 
     * @param numInpt Input dimension
     * @param numOutp Ouput dimension (thus, number of ouput neurons)
     */

	public Rbfn(int numInpt, int numOutp) {

		this.nRbfs = 0;

		this.nInptuts = numInpt;

		this.nOutputs = numOutp;

	}

	/**
	 * <p>
	 * Computes the euclidean distance between 2 vectors
	 * </p>
	 * @param v1  A vector
	 * @param v2  A vector
	 * @return a double with the euclidean distance between v1 and v2
	 */

	protected double euclideanDist(double[] v1, double[] v2) {

		int i;

		double aux = 0;

		for (i = 0; i < nInptuts; i++)

			aux += (v1[i] - v2[i]) * (v1[i] - v2[i]);

		return (Math.sqrt(aux));

	}

	/**
     * <p>
     * Creates a new instance of rbfn from a matrix of instances. Sets randomly
     * the centres of the neurons and sets its radius taking intro account the
     * maximun distance between centres.
     * </p>
     * 
     * 
     * 
     * @param X  Matrix of instances
     * @param ndata  Number of instaces in X �@param nInpt Number of imputs of the net
     * @param nOutp Number of outputs of the net
     * @param nNeuro Number of hidden neurons the net will have.
     */

	public Rbfn(double[][] X, int ndata, int nInpt, int nOutp, int nNeuro) {
		// Setting instance variables
		this.nRbfs = 0;
		this.nInptuts = nInpt;
		this.nOutputs = nOutp;
		int i, ran, inicial;
		int iterations = 0;
		double E;
		double radius;
		double prevE, dist, distmin;
		double minError = 1;
		int maxIter = 1000;
		int centreOf[] = new int[ndata];
		int elemsCluster[] = new int[nNeuro];
		boolean exists, changeCentres;
		// Constructing and adding _numNeurons RBF neurons

		double[] weights = new double[nOutputs];
		for (i = 0; i < nOutputs; ++i) {
			weights[i] = 1;
		}

		double[][] centres = new double[nNeuro][nInptuts];
		double[][] prevCentres = null;
		for (i = 0; i < nNeuro; ++i) {

			ran = (int) Randomize.Randint(0, ndata - 1);
			centres[i] = X[ran];

			exists = false;
			for (int j = 0; j < i && !exists; j++) {
				exists = true;
				for (int m = 0; m < nInptuts && exists; m++) {
					if (centres[i][m] != centres[j][m])
						exists = false;
				}
			}
			if (exists) {
				inicial = ran;
				do {
					ran = (ran + 1) % ndata;
					centres[i] = X[ran];
					exists = false;
					for (int j = 0; j < i && !exists; j++) {
						exists = true;
						for (int m = 0; m < nInptuts && exists; m++) {
							if (centres[i][m] != centres[j][m])
								exists = false;
						}
					}
				} while (exists && ran != inicial);
			}

		}
		prevE = 0;
		do {
			for (int j = 0; j < nNeuro; j++) {
				elemsCluster[j] = 0;
			}
			for (i = 0; i < ndata; i++) {
				distmin = Double.MAX_VALUE;
				for (int j = 0; j < nNeuro; j++) {
					dist = this.euclideanDist(X[i], centres[j]);
					if (dist < distmin) {
						distmin = dist;
						centreOf[i] = j;
					}
				}
				elemsCluster[centreOf[i]]++;
			}
			// set new centers
			prevCentres = centres;
			centres = new double[nNeuro][nInptuts];
			for (i = 0; i < ndata; i++) {
				for (int j = 0; j < nInptuts; j++) {
					centres[centreOf[i]][j] += X[i][j];
				}
			}
			for (i = 0; i < nNeuro; i++) {
				for (int j = 0; j < nInptuts; j++) {
					if (elemsCluster[i] != 0)
						centres[i][j] /= (double) elemsCluster[i];
				}
			}
			changeCentres = false;
			for (i = 0; i < nNeuro && !changeCentres; i++) {
				for (int j = 0; j < nInptuts && !changeCentres; j++) {
					if (centres[i][j] != prevCentres[i][j]) {
						changeCentres = true;
						iterations = maxIter;
						System.out
								.println("iterations of RBFs - no change on centers");
					}
				}
			}
			// compute RMSE
			E = 0;
			for (i = 0; i < ndata; i++) {
				E += this.euclideanDist(X[i], centres[centreOf[i]]);
			}
			iterations++;
			// System.out.println(iterations+"\t"+E);
			if (Math.abs(prevE - E) == 0)
				iterations = maxIter;
			else
				prevE = E;
			System.out.println("iterations of RBFs - " + iterations);
			System.out.println("error of RBFs - " + E);
		} while (E > minError && iterations < maxIter);

		// radius=0.5*RBFUtils.maxDistance( centres );
		// radius = RBFUtils.avegDistance(centres)/2;

		for (i = 0; i < nNeuro; ++i) {
			radius = RBFUtils.geomDistance(centres[i], i, centres);
			// radius = RBFUtils.RMSDistance(centres[i], i, centres,2);
			if (radius <= 0)
				radius = RBFUtils.avegDistance(centres) / 2;
			Rbf neurona = new Rbf(nInptuts, nOutputs);
			neurona.setParam(centres[i], radius, weights, elemsCluster[i]);
			this.insertRbf((Rbf) neurona.clone());
		}
	}

	/**
     * <p>	
     * Creates a new instance of rbfn from a matrix of instances. Sets randomly
     * the centres of the neurons and sets
     * its radius taking intro account the average distance between centres.
     * </p> 
     * 
     * 
     * 
     * @param nNeuro Number of hidden neurons the net will have.
     * @param X Matrix of instances
     * @param ndata Number of instaces in X
     * �@param nInpt Number of imputs of the net
     * @param nOutpl Number of outputs of the net
     */

	public Rbfn(int nNeuro, double[][] X, int ndata, int nInpt, int nOutpl) {

		// Setting instance variables

		int ran, i, j, cont;

		int flag;

		int[] vaux = new int[ndata];

		this.nRbfs = 0;

		this.nInptuts = nInpt;

		this.nOutputs = nOutpl;

		// Constructing and adding _numNeurons RBF neurons

		double[] weights = new double[nOutputs];

		for (i = 0; i < nOutputs; ++i) {
			weights[i] = 0;
		}
		;

		double[][] centres = new double[nNeuro][nInptuts];

		for (i = 0; i < nNeuro; ++i) {

			cont = 0;

			do {

				ran = (int) Randomize.Randint(0, ndata - 1);

				flag = 0;

				j = 0;

				cont++;

				do {

					if (vaux[j++] == ran)
						flag = 1;

				} while ((j < i) && (flag == 0) && (j < ndata));

			} while ((flag == 1) && (cont < ndata));

			centres[i] = X[ran];

			if (j < ndata)
				vaux[j] = ran;

		}

		double radius = RBFUtils.avegDistance(centres) / 2;

		for (i = 0; i < nNeuro; ++i) {

			Rbf neuron = new Rbf(nInptuts, nOutputs);

			neuron.setParam(centres[i], radius, weights);

			this.insertRbf((Rbf) neuron.clone());

		}

	}

	/**
	 * <p>
	 * Deletes a neuron of the net
	 * </p> 
	 * @param idRbf Identifier of neuron to delete
	 */

	public void removeRbf(String idRbf) {

		rbfn.remove(idRbf);

		nRbfs--;

	}

	/**
	 * <p>
	 * Adds a neuron to the net
	 * </p> 
	 * @param rbf The neuron to insert
	 */

	public void insertRbf(Rbf rbf) {

		rbf.idRbf = String.valueOf(nRbfs);

		rbfn.put(rbf.idRbf, rbf);

		nRbfs++;

	}

	/**
	 * <p>
	 * Changes a neuron in the net
	 * </p>
	 * @param idRbf Identifier of neuron to delete
	 * @param rbf  Neuron to insert
	 */

	public void modifyRbf(Rbf rbf, String idRbf) {

		removeRbf(idRbf);

		rbf.idRbf = idRbf;

		rbfn.put(rbf.idRbf, rbf);

		nRbfs++;

	}

	/**
	 * <p>
	 * Returns the number of neurons in the net
	 * </p> 
	 * @return A integer that is the number of neurons of the net
	 */

	public int size() {

		return (nRbfs);

	}

	/**
	 * <p> 
	 * Gets an RBF from the net given its identifier
	 * </p>
	 * @param id  RBF's identifier
	 * @return the RBF whose identified is id
	 */

	public Rbf getRbf(String id) {

		return ((Rbf) this.rbfn.get(id));

	}

	/**
	 * <p> 
	 * Returns the list on index of the net neurons.
	 * </p>
	 * @return a string with the list on index of the net neurons
	 */

	public String[] getIndex() {

		String[] vect = new String[this.size()];

		Enumeration aEnum = this.rbfn.keys(); 

		int i = 0;

		while (aEnum.hasMoreElements()) {

			vect[i] = (String) aEnum.nextElement();

			i++;

		}

		return (vect);

	}

	/**
	 * <p>
	 * Passes an input to the net obtaining its output
	 * </p>
	 * @param _input   The sample
	 * @return The set of outputs provided by the net's output neurons.
	 */

	public double[] evaluationRbfn(double[] _input) {

		double[] aux = new double[nOutputs];

		int i;

		Enumeration it;

		Rbf rbf;

		for (i = 0; i < nOutputs; i++)

		{

			aux[i] = 0;

			it = rbfn.elements();

			while (it.hasMoreElements()) {

				rbf = (Rbf) it.nextElement();

				aux[i] += rbf.evaluationRbf(_input) * rbf.weight[i];

			}

		}

		return (aux);

	}

	/**
     * <p>
     * Computes the difference between the ouput of the net and desired outpunt
     * </p>
     * 
     * 
     * @param realOutput Desired output
     * @param netOutput Outpunt of the net
     * @return A vector of doubles with the difference between the output of the
     *         net and the desired output
     */

	public double[] errorRbfn(double[] realOutput, double[] netOutput) {

		int i;

		double[] error = new double[nOutputs];

		for (i = 0; i < nOutputs; i++)

			error[i] = realOutput[i] - netOutput[i];

		return (error);

	}

	/**
	 * <p>
	 * Returns the nearest rbf/neuron to a vector v (patron)
	 * </p>
	 * @param v vector
	 * @return the identifier of the nearest RBF to v
	 */

	public String rbfNearest(double[] v) {

		String key = "nula";

		double distmin = Double.POSITIVE_INFINITY;

		double dist;

		int i, ind;

		Rbf rbf;

		int nrbf = this.size(); 
		
		String[] vect = this.getIndex(); 
		
                for (i = 0; i < nRbfs; i++) {

			rbf = getRbf(vect[i]);

			dist = rbf.euclideaDist(v);

			if (dist < distmin) {

				distmin = dist;

				key = vect[i];

			}

		}

		return (key);

	}

	/**
     * <p>
     * Uses RAN algorithm to build a net
     * </p>
     * 
     * @param X matrix of inputs instances
     * @param Y matrix of outputs instances
     * @param ndata Number of instances
     * @param epsilon minimun error to introduce a new RBF
     * @param delta minimun distance to introduce a new RBF
     * @param alfa learning factor when a new unit is not allocated
     */

	public void RAN(double[][] X, double[][] Y, int ndata, double delta,
			double epsilon, double alfa) {

		try {

			int numVectorSeleccionado, i;

			String key;

			double[] centre = new double[nInptuts];

			double radius = 1;

			double[] weights = new double[nOutputs];

			double dist;

			double[] patternInputs = new double[nInptuts];

			double[] patternOutputs = new double[nOutputs];

			double[] outputNet = new double[nOutputs];

			double[] error = new double[nOutputs];
			String[] vect;
			double errori;
			double factor;
			int nInpt = nInptuts;
			double maxOutp = 0;
			int nOutp = nOutputs;
			int M = 60;
			double thres = 0.00001;
			Rbf rbf;

			int cont = 0;
			int[] aleat = new int[ndata];

			// Inserts first RBF

			numVectorSeleccionado = (int) Randomize.Randint(0, ndata - 1);

			patternInputs = X[numVectorSeleccionado];

			patternOutputs = Y[numVectorSeleccionado];
			/*
			 * rbf=new Rbf(nInptuts,nOutputs);
			 * 
			 * rbf.setParam(patternInputs, 1.0*delta , patternOutputs);
			 * 
			 * this.insertRbf((Rbf)rbf.clone());
			 */
			for (i = 0; i < ndata; i++) {
				aleat[i] = i;
			}
			factor = delta * Math.log10(nInptuts);
			do {// Major Loop

				// numVectorSeleccionado=(int)Randomize.Randint( 0, ndata-1 );
				int ran = (int) Randomize.Randint(0, ndata - cont - 1);
				numVectorSeleccionado = aleat[ran];
				aleat[ran] = aleat[ndata - cont - 1];
				patternInputs = X[numVectorSeleccionado];

				patternOutputs = Y[numVectorSeleccionado];

				outputNet = this.evaluationRbfn(patternInputs);

				error = this.errorRbfn(patternOutputs, outputNet);

				key = this.rbfNearest(patternInputs);

				rbf = getRbf(key);

				dist = rbf.euclideaDist(patternInputs);

				errori = 0;

				for (i = 0; i < nOutputs; i++)

					if (Math.abs(error[i]) > epsilon)
						errori = error[i];

				// if(delta < delta*nInptuts*Math.log10(delta*nRbfs/nInptuts))
				// factor = delta*nInptuts*Math.log10(delta*nRbfs/nInptuts);
				if ((Math.abs(errori) > epsilon) && (dist > delta)) { // Major
					// Condition

					// Inserts a new Rbf

					rbf = new Rbf(nInptuts, nOutputs);

					rbf.setParam(patternInputs, 1.0 * dist, error);

					this.insertRbf((Rbf) rbf.clone());
					// System.out.println(cont+" - "+dist+ " | "+errori+" -
					// "+nRbfs);

					for (i = 0; i < ndata; i++) {
						aleat[i] = (i + cont) % ndata;
					}
					cont = 0;

				}

				else {

					// Perform gradient descent on centros and weight of the Rbf
					// key

					centre = rbf.getCentre();

					weights = rbf.getWeights();

					radius = rbf.getRadius();

					for (i = 0; i < nOutputs; i++)

						weights[i] = weights[i]
								+ (alfa * errori * rbf.evaluationRbf(patternInputs));

					for (i = 0; i < nInptuts; i++)

						centre[i] = centre[i]
								+ (2 * (alfa / radius)
										* (patternInputs[i] - centre[i]) *

										rbf.evaluationRbf(patternInputs) * (errori * weights[0]));

					rbf.setCentre(centre);

					rbf.setWeight(weights);

					cont++;

				}/*
					 * //perform prunning of hidden units
					 * vect=this.getIndex(); double [] salidas = new
					 * double[nRbfs]; for (i=0;i<nRbfs;i++){
					 * //System.out.println("evaluated "+i);
					 * rbf=getRbf(vect[i]); salidas[i] =
					 * rbf.evaluationRbf(patternInputs); if(i==0 ||
					 * salidas[i]>maxOutp){ maxOutp = salidas[i]; } } //normalize
					 * outputs for (i=0;i<nRbfs;i++){ salidas[i] = salidas[i] /
					 * maxOutp; if(salidas[i] < thres){ rbf=getRbf(vect[i]);
					 * rbf.pocoRelevante++; } else{ rbf.pocoRelevante = 0; } }
					 * //delete those units which have M iterations with less
					 * than "thres" //normalized outputs (and therefore, little
					 * contribution) int nrbf=nRbfs; for (i=0;i<nrbf;i++){
					 * rbf=getRbf(vect[i]);
					 * 
					 * if(rbf.pocoRelevante >= M){ removeRbf(vect[i]);
					 * //System.out.println("RBF "+vect[i]+" deleted!"); } }
					 */

			} while (cont < (ndata));

			// System.out.println("delta rule - " +factor);
			// System.out.println("RBF initial delta rule - "
			// +delta*nInptuts*Math.log10(1.0/nInptuts));
			// System.out.println("RBF final delta rule - "
			// +delta*nInptuts*Math.log10(delta*nRbfs/nInptuts));

		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.toString());

		}

	}

	/**
     * <p>
     * Uses a decremental algorithm to buid a net. After initializing and
     * training (with LMS)
     * a net with several neurons, the algorithm in the major loop deletes the
     * neurons with the lowest weight and train the net.
     * </p>
     * 
     * @param X matrix of inputs instances
     * @param Y matrix of outputs instances
     * @param ndata Number of instances
     * @param percent Percent under the average of the weights to delete a neuron
     * @param alfa Learnig factor of LMS algorithm
     */

	public void decremental(double[][] X, double[][] Y, int ndata,
			double percent, double alfa) {

		int i, j, k, cont = 0, nrbf, flag, nrbfnini, inst, numVectorSeleccionado;
		String key;
		int[] aleat = new int[ndata];
		double dist;

		double[] centre = new double[nInptuts];

		double[] weights = new double[nOutputs];

		double[] medWeights = new double[nOutputs];

		double[] patternInputs = new double[nInptuts];

		double[] patternOutput = new double[nOutputs];

		double[] outputNet = new double[nOutputs];

		double[] error = new double[nOutputs];

		double errori;

		String[] vect;

		double radius;

		Rbf rbf;

		double weight, weightmed;

		double[][] vaux = new double[nRbfs][nOutputs];

		double epsilon = 0.1;
		double delta = 2;
		double factor = 0;
		nrbfnini = nrbf = this.size();

		vect = this.getIndex();

		this.trainLMS(X, Y, ndata, 10, alfa);

		try {

			do {
				//System.out.println("RBFS: " + nrbf);
				weightmed = 0;
				vaux = new double[nrbf][nOutputs];

				for (i = 0; i < nrbf; i++) {

					rbf = getRbf(vect[i]);

					for (j = 0; j < nOutputs; j++)

						vaux[i][j] = Math.abs(rbf.getWeight(j));

				}

				medWeights = RBFUtils.medVect(vaux);

				for (j = 0; j < nOutputs; j++) {

					if (medWeights[j] > weightmed)
						weightmed = medWeights[j];

				}

				for (i = 0; i < nrbf; i++) {

					rbf = getRbf(vect[i]);

					weight = 0;

					for (j = 0; j < nOutputs; j++) {

						if (Math.abs(rbf.getWeight(j)) > weight)
							weight = Math.abs(rbf.getWeight(j));

					}

					if (Math.abs(weight) < (percent * weightmed)) {

						removeRbf(vect[i]);

						cont = 0;

					}

				}
				vect = this.getIndex();

				nrbf = this.size();

				for (j = 0; j < nrbf; j++) {

					rbf = getRbf(vect[j]);

					weights = rbf.getWeights();

					weight = 0;

					for (k = 0; k < nOutputs; k++)

						if (weights[k] > weight)
							weight = weights[k];

					if (weight < weightmed) {

						centre = rbf.getCentre();

						radius = rbf.getRadius();

						for (k = 0; k < nInptuts; k++) {

							radius += Randomize.Randdouble(-radius * 0.05,
									radius * 0.05);

							centre[k] += Randomize.Randdouble(-radius * 0.05,
									radius * 0.05);

						}

						rbf.setRadius(radius);

						rbf.setCentre(centre);

					}

				}

				// this.trainLMS(X, Y, ndata, 5, alfa);
				inst = 0;
				for (i = 0; i < ndata; i++) {
					aleat[i] = i;
				}
				// factor = delta * Math.log10(nInptuts);
				do {// Major Loop

					// numVectorSeleccionado=(int)Randomize.Randint( 0, ndata-1
					// );
					int ran = (int) Randomize.Randint(0, ndata - inst - 1);
					numVectorSeleccionado = aleat[ran];
					aleat[ran] = aleat[ndata - inst - 1];
					patternInputs = X[numVectorSeleccionado];

					patternOutput = Y[numVectorSeleccionado];

					outputNet = this.evaluationRbfn(patternInputs);

					error = this.errorRbfn(patternOutput, outputNet);

					key = this.rbfNearest(patternInputs);

					rbf = getRbf(key);

					dist = rbf.euclideaDist(patternInputs);

					errori = 0;

					for (i = 0; i < nOutputs; i++)

						if (Math.abs(error[i]) > epsilon)
							errori = error[i];

					// Perform gradient descent on centros and weight of the Rbf
					// key

					centre = rbf.getCentre();

					weights = rbf.getWeights();

					radius = rbf.getRadius();

					for (i = 0; i < nOutputs; i++)

						weights[i] = weights[i]
								+ (alfa * errori * rbf.evaluationRbf(patternInputs));

					for (i = 0; i < nInptuts; i++)

						centre[i] = centre[i]
								+ (2 * (alfa / radius)
										* (patternInputs[i] - centre[i]) *

										rbf.evaluationRbf(patternInputs) * (errori * weights[0]));

					rbf.setCentre(centre);

					rbf.setWeight(weights);

					inst++;
				} while (inst < (ndata));

				cont++;
			} while ((cont < 15) && (nrbf > 0));
			// this.trainLMS(X,Y,ndata,1,alfa);
			System.out.println("Final num. RBFs obtained - " + nRbfs);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalError(e.toString());

		}

	}

	/**
     * <p>
     * Uses LMS to train the net.
     * </p>
     * 
     * @param X matrix of inputs instances
     * @param Y matrix of outputs instances
     * @param ndata Number of instances
     * @param iter Number of times the set of samples will be used.
     * @param alfa Learning factor.
     */

	public void trainLMS(double[][] X, double[][] Y, int ndata, int iter,
			double alfa) {
		try {
			int i, j, z, nrbf,ndataini;
			double[] error = new double[nOutputs];
			double modulo;
			double weight;
			double radius;
			double[] centre = new double[nInptuts];
			String[] vect;
			Rbf rbf;
			double[] evaluation;
			double[] inputRed;
			double[] outputReal;
			double[] outputNet;
			int[] aleat = new int[ndata];
			int nInpt = nInptuts;
			int nOutpl = nOutputs;
			double nu_c, nu_sigma, sum, dif;

			nu_c = nu_sigma = 0.0005;
			weight = sum = 0;
			ndataini = ndata;
			nrbf = this.size();
			vect = this.getIndex();
			evaluation = new double[nrbf];
			RBFUtils.verboseln("Training RBFNN using LMS");
			for (z = 0; z < iter; z++) {
				ndata = ndataini;
				RBFUtils.verboseln(" - LMS iteration num. " + (z + 1));
				RBFUtils.verboseln(" - Num. Muestras: " + ndata);
				for (i = 0; i < ndata; i++) {
					aleat[i] = i;
				}
				while (ndata > 0) {
					int ran = (int) Randomize.Randint(0, ndata - 1);
					int numVectorSeleccionado = aleat[ran];
					aleat[ran] = aleat[--ndata];
					inputRed = X[numVectorSeleccionado];
					outputReal = Y[numVectorSeleccionado];
					outputNet = this.evaluationRbfn(inputRed);

					error = new double[nOutputs];
					for (i = 0; i < nOutputs; i++) {
						error[i] = outputReal[i] - outputNet[i];
					}

					for (i = 0; i < nOutputs; i++) {
						modulo = 0.0;

						for (j = 0; j < nrbf; j++) {
							rbf = getRbf(vect[j]);
							// rbf.printRbf();
							evaluation[j] = rbf.evaluationRbf(inputRed);
							modulo = modulo + evaluation[j] * evaluation[j];
						}
						modulo = Math.sqrt(modulo);
						if(modulo == 0)
							modulo = Double.longBitsToDouble(0x0010000000000000L);

						for (j = 0; j < nrbf; j++) {
							rbf = getRbf(vect[j]);
							weight = rbf.getWeight(i);

							weight = weight + alfa
									* (error[i] * evaluation[j] / modulo);
							rbf.setWeight(i, weight);

						}
					}
					for (i = 0; i < nrbf; i++) {
						rbf = getRbf(vect[i]);
						centre = rbf.getCentre();
						radius = rbf.getRadius();
						sum = 0;
						dif = 0;
						for (int k = 0; k < nOutputs; k++) {
							weight = rbf.getWeight(k);
							sum += (outputReal[k] - outputNet[k]) * weight;
						}
						for (j = 0; j < nInptuts; j++) {
							dif += (inputRed[j] - centre[j])
							* (inputRed[j] - centre[j]);
							centre[j] = centre[j] + nu_c * sum * evaluation[i]
									* (inputRed[j] - centre[j])
									/ (radius * radius);
							
						}
						radius = radius + nu_sigma * sum * evaluation[i] * dif
								/ (radius * radius * radius);
						rbf.setRadius(radius);
						rbf.setCentre(centre);
//						if(i==0){
//							System.out.println(z+"\t centre - "+centre[0] +" || radius - "+radius+" | dif - "+dif+" || sum - "+sum+" ** weight "+weight);
//							if(new Double(centre[0]).isNaN() || dif > 1E10)
//								System.exit(-1);
//						}
					}
				}
				RBFUtils.verboseln("Error conseguido: " + error[0]);
				// System.out.println("Error conseguido: " + error[0]);
			}
		} catch (Exception e) {
			throw new InternalError(e.toString());
		}
	}

	/**
     * <p>
     * Evaluates the net for modeling problem
     * </p>
     * 
     * 
     * @param X matrix of inputs instances
     * @param ndata Number of instances
     * @param obtained Vector of results of the evaluation
     */

	public void testModeling(double[][] X, int ndata, double[] obtained) {

		int i;

		for (i = 0; i < ndata; i++) {

			obtained[i] = this.evaluationRbfn(X[i])[0];

		}

	}

	/**
     * <p> 	
     * Evaluates the net for clasification problem
     * </p>
     * 
     * 
     * @param X matrix of inputs instances
     * @param ndata Number of instances
     * @param obtained Vector of results of the evaluation
     * @param max Class maximun identifier
     * @param min Class minimun identifier
     */

	public void testClasification(double[][] X, int ndata, int[] obtained,
			int max, int min) {

		int i;

		for (i = 0; i < ndata; i++) {

			obtained[i] = (int) Math.round(this.evaluationRbfn(X[i])[0]);

			if (obtained[i] > max)
				obtained[i] = max;

			if (obtained[i] < min)
				obtained[i] = min;

		}

	}

	/**
	 * <p>
	 * Prints net on a stdout
	 * </p>
	 */

	public void printRbfn() {

		this.printRbfn("");

	}

	/**
	 * <p> 
	 * Prints net on a file.
	 * </p>
	 * @param _fileName Name of the file.
	 */

	public void printRbfn(String _fileName) {

		int i;

		String ind;

		String[] indices = new String[6];

		indices = this.getIndex();

		for (i = 0; i < indices.length; ++i) {

			ind = indices[i];

			if (_fileName != "") {

				Files.addToFile(_fileName, "Neuron: " + ind + "\n");

			} else {

				System.out.println("Neuron: " + ind);

			}

			Rbf neurona = this.getRbf(ind);

			neurona.printRbf(_fileName);

		}

	}

} /* end class */


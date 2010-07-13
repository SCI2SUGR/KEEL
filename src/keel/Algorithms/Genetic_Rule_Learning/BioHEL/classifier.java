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

package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import keel.Dataset.Instance;

public abstract class classifier {
	
	int length;		// Length of the individual in genes
	double fitness;
	double scaledFitness;
	int modif;
	int front;
	double exceptionsLength;	//For MDL fitness function
	double accuracy;
	double accuracy2;
	double coverage;
	int numAttributes;
	int numAttributesMC;
	double theoryLength;
	

	 public classifier() {
		length = 0;
		modif = 1;
	} 



	public int getLength() {
		return length;
	}

	public void fitnessComputation() {
		modif = 0;
		fitness = classifierFitness.computeClassifierFitness(this);
	}
	public void setScaledFitness(double pFitness) {
		scaledFitness = pFitness;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double pFit) {
		fitness=pFit;
	}
	public double getScaledFitness() {
		return scaledFitness;
	}
	public void setAccuracy(double acc) {
		accuracy = acc;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy2(double acc) {
		accuracy2 = acc;
	}
	public double getAccuracy2() {
		return accuracy2;
	}
	public void setCoverage(double cov) {
		coverage = cov;
	}
	public double getCoverage() {
		return coverage;
	}

	public void adjustFitness() {
		if(Parameters.useMDL) {
			fitness=exceptionsLength;
		}
	}


	public double getExceptionsLength() {
		return exceptionsLength;
	}
	public void setExceptionsLength(double excep) {
		exceptionsLength = excep;
	}
	public int isModified() {
		return modif;
	}
	public void activateModified() {
		modif = 1;
	}
	public double getTheoryLength() {
		return theoryLength;
	}
	
	abstract public int getClase();
	abstract public boolean doMatch(Instance i);
	abstract public double computeTheoryLength();
	abstract public void crossover(classifier in1, classifier ou1, classifier ou2);
	abstract public void mutation();
	abstract public int numSpecialStages();
	abstract public void doSpecialStage(int stage);
	abstract public void postprocess();
	abstract public String dumpPhenotype();
	
	public int compareToIndividual(classifier i2, int maxmin){
		if (maxmin == Parameters.MAXIMIZE) {
			if (fitness>i2.fitness)
				return +69;
			if (fitness<i2.fitness)
				return -69;
			return 0;
		}
		if (fitness<i2.fitness)
			return +69;
		if (fitness>i2.fitness)
			return -69;
		return 0;
	}

	public int compareToIndividual2(classifier i2, int maxmin) {
		if (maxmin == Parameters.MAXIMIZE) {
			if (fitness>i2.fitness)
				return -69;
			if (fitness<i2.fitness)
				return +69;
			return 0;
		}
		if (fitness<i2.fitness)
			return -69;
		if (fitness>i2.fitness)
			return +69;
		return 0;
	}

}

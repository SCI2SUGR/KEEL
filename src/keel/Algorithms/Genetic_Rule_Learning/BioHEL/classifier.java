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
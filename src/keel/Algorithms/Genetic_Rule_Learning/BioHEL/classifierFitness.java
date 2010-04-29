package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

import keel.Dataset.Instance;

public class classifierFitness {
	
	static public double computeClassifierFitness(classifier ind) {
		int i;
		int numInstances = Parameters.is.getNumInstancesOfIteration();
		int cl=ind.getClase();
		agentPerformanceTraining ap = new agentPerformanceTraining(numInstances,cl);
		Instance[] instances=Parameters.is.getInstancesOfIteration();

		for (i = 0; i < numInstances; i++) {
			if(ind.doMatch(instances[i])) {
				ap.addMatch(instances[i].getOutputNominalValuesInt(0),cl);
			} else {
				ap.addNoMatch(instances[i].getOutputNominalValuesInt(0));
			}
		}

		ind.setAccuracy(ap.getAccuracy());
		ind.setAccuracy2(ap.getAccuracy2());
		ind.setCoverage(ap.getCoverage());
		double fitness = ap.getFitness(ind);
		return fitness;
	}
	
}

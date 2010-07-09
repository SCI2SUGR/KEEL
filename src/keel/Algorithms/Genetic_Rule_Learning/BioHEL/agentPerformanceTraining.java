package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

public class agentPerformanceTraining {
	

	int ruleClass;
	int numInstancesPos;
	int numInstancesPosOK;
	int numInstancesTotal;
	int numInstancesMatched;


	public void addMatch(int realClass,int predictedClass) {
		if(realClass==ruleClass) numInstancesPos++;
		numInstancesMatched++;
		
		if (predictedClass == realClass) {
			numInstancesPosOK++;
		}
	}

	public void addNoMatch(int realClass) {
		if(realClass==ruleClass) numInstancesPos++;
	}

	public double getAccuracy() { return (double)numInstancesPosOK/(double)numInstancesTotal; }

	public double getAccuracy2() { 
		if(numInstancesMatched==0) return 0;
		return (double)numInstancesPosOK/(double)numInstancesMatched;
	}

	public double getCoverage() { return (double)numInstancesMatched/(double)numInstancesTotal;}
	public int getNumOK() { return numInstancesPosOK;}
	public int getNumPos() { return numInstancesPos;}
	public int getNumKO() { return numInstancesMatched-numInstancesPosOK;}
	public int getNumTotal() { return numInstancesTotal;}
	public double getNC(){return (double)(1-numInstancesMatched)/(double)numInstancesTotal;}
	public double getRecall(){ return (double)numInstancesPosOK/(double)numInstancesPos; } 
	public double getFMeasure() { 
		double precision=getAccuracy2();
		double recall=getRecall();
		return 2*precision*recall/(precision+recall);
	}
	


	public agentPerformanceTraining(int pNumInstances,int pRuleClass){
		ruleClass=pRuleClass;

		numInstancesTotal = pNumInstances;
		numInstancesPosOK = 0;
		numInstancesMatched = 0;
		numInstancesPos = 0;
	}

	public double getFitness(classifier ind){
		double fitness;

		if(Parameters.useMDL) {
			ind.computeTheoryLength();
			fitness = Parameters.timers.tMDL.mdlFitness(ind,this);
		} else {
			fitness=getFMeasure();
			fitness*=fitness;
		}

		return fitness;
	}


}


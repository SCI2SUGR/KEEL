package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

public abstract class timingProcess {
		
		abstract void initialize(populationWrapper pPW);
		abstract void newIteration(int iteration,int finalIteration);
		abstract void dumpStats(int iteration);	
		abstract void reinit();
}

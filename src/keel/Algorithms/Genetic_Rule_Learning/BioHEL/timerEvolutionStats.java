package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

public class timerEvolutionStats {
	
   
	static public int iterationsSinceBest;
    static public int maxMin;
    static public int globalIterationsSinceBest;

	public timerEvolutionStats(){

		iterationsSinceBest = 0;
		globalIterationsSinceBest = 0;
		maxMin = Parameters.optimizationMethod;	
	}
    
	static public int getIterationsSinceBest(){
		return iterationsSinceBest;
	}
	
	static public void resetBestStats(){
		iterationsSinceBest=0;
	}

}
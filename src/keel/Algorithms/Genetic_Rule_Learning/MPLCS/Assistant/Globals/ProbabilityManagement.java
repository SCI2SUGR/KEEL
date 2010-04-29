package keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals;

public class ProbabilityManagement {
	public final static int LINEAR = 0;
	public final static int SIGMOIDAL = 1;

	double probStart;
	double probEnd;
	double probLength;
	int evolMode;

	double currentProb;
	double sigmaYLength;
	double sigmaYBase;
	double sigmaXOffset;
	double beta;

	public ProbabilityManagement(double start,double end,int mode) {
		probStart = start;
		probEnd = end;
		evolMode = mode;

		if(mode == LINEAR) {
			probLength=end-start;
			currentProb = start;
		} else {
			sigmaYLength = end - start;
			sigmaYBase = start;
			sigmaXOffset = 0.5;
			beta = -10;
		}
	}

	public double incStep() {
		if(evolMode == LINEAR) {
			currentProb = Parameters.percentageOfLearning 
				* probLength +probStart;
		} else {
			currentProb = sigmaYLength
				/ ( 1 + Math.exp(beta 
				*(Parameters.percentageOfLearning-0.5)))
				+sigmaYBase;
		}
		return currentProb;
	}
}

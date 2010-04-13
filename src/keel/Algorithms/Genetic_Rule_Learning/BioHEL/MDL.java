package keel.Algorithms.Genetic_Rule_Learning.BioHEL;

/**
 * <p>Title: MDL</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

public class MDL {
	
	int startIteration;
	double mdlWeight;
	double mdlWeightRelaxFactor;
	double initialTheoryLenghtRatio;
	int IterationsSinceBest;

	boolean activated;

	double coverageBreakPoint;
	double coverageRatio;
	double []coverageBreaks;
	
	

	public MDL(double cbp, double cr, double itlr, double wrf){
		
		coverageBreakPoint = cbp;
		coverageRatio = cr;
		initialTheoryLenghtRatio=itlr;
		mdlWeightRelaxFactor=wrf;
		IterationsSinceBest=0;
		startIteration=0;
		activated = false;
	

		coverageBreaks = new double[BioHEL.train.getnClasses()];
		
		int examplesOfClass[]=BioHEL.train.getInstancesPerClass();
		int examplesNotRem=BioHEL.train.instancesNotRemoved();
		
		
		for(int i=0 ; i<BioHEL.train.getnClasses() ; ++i){

			coverageBreaks[i]=coverageBreakPoint/(double)examplesOfClass[i]*(double)examplesNotRem;
			
			if(coverageBreaks[i]>1)
				coverageBreaks[i]=1;
			
		}
	}

public void newIteration(int iteration, Rule bestInd, boolean newBest){
		
		
		//si es la iteracion inicial, inicializo mdlWeight
		if (iteration == startIteration) {
			activated=true;
			double error = bestInd.exceptionsLength;
			double theoryLength = bestInd.computeTheoryLength();
			
			if(error==0)
				mdlWeight=0.1;
			else
				mdlWeight=(initialTheoryLenghtRatio/(1-initialTheoryLenghtRatio))*(error/theoryLength);
		}
		
		else{			
				
			if(newBest)
				IterationsSinceBest=0;
			else
				IterationsSinceBest++;
				
				
			if (IterationsSinceBest == BioHEL.numItMDL) {
				mdlWeight *= mdlWeightRelaxFactor;
				IterationsSinceBest=0;
			}
		}
		
	}

	
	public double mdlFitness(Rule ind){
	
		ind.RuleParameters();
		
		double mdlFitness = 0;
		
		if (activated){
			mdlFitness = ind.computeTheoryLength() * mdlWeight;
		}
	
		double exceptionsLength;
		if(ind.getNumInstancesWithSameClass()==0) {
			exceptionsLength = 2;
		}
		
		else {
			double acc = 1-ind.getAccuracy2();
			int cl=ind.PredictedClass();
			double cov = ind.getRecall();
			
			if(cov<coverageBreaks[cl]/3) { 
				cov=0;
			}
			
			else {
				if(coverageBreaks[cl]<1) {
					if(cov<coverageBreaks[cl]) {
						cov=coverageRatio*(cov/coverageBreaks[cl]);
					}
					
					else {
						if(cov>coverageBreaks[cl]*5) cov=coverageBreaks[cl]*5;
						cov=coverageRatio+(1-coverageRatio)*(cov-coverageBreaks[cl])/(1-coverageBreaks[cl]);
					}
				}
			}

			cov=1-cov;
			exceptionsLength = acc+cov;
		}
	
		ind.exceptionsLength=exceptionsLength;
		mdlFitness += exceptionsLength;
				
		return mdlFitness;
	}

}
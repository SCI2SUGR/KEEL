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

public class timerMDL extends timingProcess {

	int startIteration;
	double mdlWeight;
	double mdlWeightRelaxFactor;
	double initialTheoryLenghtRatio;
	boolean fixedWeight;
	int mdlWeightRelaxStopIteration;
	double mdlWeightRelaxStopAccuracy;
	int iterationMDL;
	boolean activated;	
	boolean mdlAccuracy;	
	double coverageBreak;
	double coverageRatio;
	double[] coverageBreaks;
	double[] origCoverageBreaks;

	void initialize(populationWrapper pPW) {Parameters.pw = pPW;}
	

	public timerMDL()
	{
		coverageBreak = Parameters.coverageBreakpoint;
		if (!Parameters.useMDL) {
			mdlAccuracy = false;
			return;
		}
		mdlAccuracy = true;

		startIteration = Parameters.numIterationsMDL;
		if (startIteration != 0)
			startIteration++;

		mdlWeightRelaxFactor = Parameters.mdlWeightRelaxFactor;

		initialTheoryLenghtRatio = Parameters.initialTheoryLengthRatio;
		fixedWeight = false;
		activated = false;
		iterationMDL = 0;

		/*
		if(Parameters.MDL_WEIGHT) {
			fixedWeight=true;
			mdlWeight=cm.getParameter(MDL_WEIGHT);
		}
		*/

		int i;
		coverageBreak=Parameters.coverageBreakpoint;
		coverageRatio=Parameters.coverageRatio;
		int nc=Parameters.numClasses;
		
		coverageBreaks = new double[nc];
		origCoverageBreaks = new double[nc];
		for(i=0;i<nc;i++) {
			coverageBreaks[i]=coverageBreak/(double)Parameters.InstancesOfClass[i]*(double)Parameters.is.getNumInstances();
			if(coverageBreaks[i]>1) coverageBreaks[i]=1;
			origCoverageBreaks[i]=coverageBreaks[i];	
		}
	}

	
	public void reinit(){
		fixedWeight = false;
		activated = false;
		iterationMDL = 0;

		
		/*
		if(Parameters.MDL_WEIGHT) {
			fixedWeight=true;
			mdlWeight=cm.getParameter(MDL_WEIGHT);
		}
		*/
	}


	public void newIteration(int iteration, int lastIteration){
		if (!mdlAccuracy)
			return;
		boolean updateWeight = false;
		iterationMDL++;

		if (iteration == startIteration) {
			activated = true;
			if(!fixedWeight) {
				classifier ind1 = Parameters.pw.getBestPopulation();
				double error = ind1.getExceptionsLength();
				double theoryLength = ind1.getTheoryLength();
				
				if(error==0) {
					mdlWeight=0.1;
					fixedWeight=true;
				} else {
					mdlWeight = (initialTheoryLenghtRatio / (1 - initialTheoryLenghtRatio)) * (error / theoryLength);
				}
			}
			updateWeight = true;
		}

		if (activated && !fixedWeight) {
			if (Parameters.pw.getBestPopulation().getExceptionsLength() != 0) {
				if (timerEvolutionStats.getIterationsSinceBest() == 10) {
					mdlWeight *= mdlWeightRelaxFactor;
					updateWeight = true;
				}
			}
		}

		if (updateWeight) {
			timerEvolutionStats.resetBestStats();
			Parameters.pw.activateModifiedFlag();
		}
	}

	public double mdlFitness(classifier ind, agentPerformanceTraining ap){
		double mdlFitness = 0;
		if (activated) {
			mdlFitness = ind.getTheoryLength() * mdlWeight;
		}

		double exceptionsLength;
		if(ap.getNumPos()==0) {
			exceptionsLength = 2;
		} else {
			double acc = 1-ap.getAccuracy2();
			int cl=ind.getClase();
			double cov = ap.getRecall();
			if(cov<coverageBreaks[cl]/3) { 
				cov=0;
			} else {
				if(coverageBreaks[cl]<1) {
					if(cov<coverageBreaks[cl]) {
						cov=coverageRatio*cov/coverageBreaks[cl];
					} else {
						if(cov>coverageBreaks[cl]*5) cov=coverageBreaks[cl]*5;
						cov=coverageRatio+(1-coverageRatio)*(cov-coverageBreaks[cl])/(1-coverageBreaks[cl]);
					}
				}
			}

			cov=1-cov;

			exceptionsLength = acc+cov;
		}

		ind.setExceptionsLength(exceptionsLength);
		mdlFitness += exceptionsLength;

		return mdlFitness;
	}


	@Override
	void dumpStats(int iteration) {
		// TODO Auto-generated method stub
	}

}

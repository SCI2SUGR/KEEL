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



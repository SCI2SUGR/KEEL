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


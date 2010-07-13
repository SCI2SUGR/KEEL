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

public class windowingILAS extends windowing {
	
	Instance[] set;
	Instance[] strata;
	int[] strataSizes;
	int[] strataOffsets;
	int howMuch;
	int numStrata;
	int currentIteration;
	int instancesPerStrata;
	int thereIsStatisticalValidation;
	int stratum;
	
	public windowingILAS(){
		numStrata = Parameters.numStrataWindowing;
		strataSizes = new int[numStrata];
		strataOffsets = new int[numStrata];
		strata = null;
	}
	
	public boolean needReEval() {
		if(numStrata==1) return false;
		return true;
	}
	
	public int numVersions() {
		return numStrata;
	}
	
	public int getCurrentVersion() {
		return stratum;
	}
	
	public void setInstances(Instance[] pSet, int pHowMuch){

		if(strata != null){
			if( pHowMuch > howMuch ) {
				strata = new Instance[pHowMuch];
			}
		} else {
			strata = new Instance[pHowMuch];
		}

		set = pSet;
		howMuch = pHowMuch;
		currentIteration = 0;
		reorderInstances();
	}

	public void reorderInstances(){
		
		int i,j;
		int nc = Parameters.numClasses;

		Sampling[] samplings = new Sampling[nc];
		for( i = 0 ; i < nc ; i++)
			samplings[i] = new Sampling(numStrata);

		int tempCapacity = howMuch/numStrata+nc;
		int[] countTemp = new int[numStrata];
		Instance[][] tempStrata = new Instance[numStrata][];
		
		for( i = 0 ; i < numStrata ; i++ ){
			countTemp[i] = 0;
			tempStrata[i] = new Instance[tempCapacity];
		}

		for( i = 0 ; i < howMuch ; i++){
			int cls = set[i].getOutputNominalValuesInt(0);
			int str = samplings[cls].getSample();
			tempStrata[str][countTemp[str]++] = set[i];
		}

		int acum = 0;
		for( i = 0 ; i < numStrata ; i++ ){
			int size = countTemp[i];
			strataSizes[i] = size;
			strataOffsets[i] = acum;
			for( j = 0 ; j < size ; j++ ){
				strata[acum++]=tempStrata[i][j];
			}
		}
	}

	
	public Object[] newIteration(){
		
		Object[] res = new Object[2];
		
		stratum = currentIteration % numStrata;
		int numSelected = strataSizes[stratum];
		Instance[] selectedInstances = new Instance[numSelected];
		System.arraycopy(strata, strataOffsets[stratum], selectedInstances, 0, numSelected);
		currentIteration++;
		
		res[0] = (Integer) numSelected;
		res[1] = (Instance[]) selectedInstances;
		
		return res;		
	}


}

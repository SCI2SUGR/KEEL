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
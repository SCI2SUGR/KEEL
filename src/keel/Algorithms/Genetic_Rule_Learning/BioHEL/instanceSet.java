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

import org.core.Randomize;
import keel.Dataset.Instance;

public class instanceSet {
	
	windowing win;
	Sampling[] initSamplings;
	int numInstancesOrig;
	boolean windowingEnabled;
	int[] countsByClass;
	int[][] instByClass;
	int numClasses;
	boolean classWiseInit;

	Instance[] set;
	Instance[] origSet;

	Instance[] window;
	int windowSize;
	
	public instanceSet(int traintest){
		

		set = new Instance[Parameters.NumInstances];
		for(int i=0;i<Parameters.NumInstances;i++) {
			set[i] = new Instance(Parameters.instances[i]);
		}
		
		// ---------------
		
		int i;

		window=null;
		win=null;
	
		origSet = new Instance [Parameters.NumInstances];
		numInstancesOrig=Parameters.NumInstances;
		for(i=0;i<Parameters.NumInstances;i++) {
			origSet[i]=set[i];
		}

		initializeWindowing(traintest);

		if(traintest==Parameters.TRAIN) {
			numClasses=Parameters.numClasses;
			classWiseInit = Parameters.classWiseInit;
			initInstanceLists();
		} else {
			initSamplings=null;
			countsByClass=null;
			instByClass=null;
		}
	}

	
	public int getCurrentVersion(){
		if(Parameters.windowingMethod != Parameters.NONE) return win.getCurrentVersion();
		return 0;
	}
	
	public void restart(){
		
		initInstanceLists();
		initializeWindowing(Parameters.TRAIN);
	}
	
	public void initInstanceLists(){
		
		int i;
		int numInst=getNumInstances();

		countsByClass = new int[numClasses];
		initSamplings = new Sampling [numClasses];
		instByClass = new int [numClasses][];

		for(i=0;i<numClasses;i++) {
			countsByClass[i]=0;
		}

		for(i=0;i<numInst;i++) {
			int cl=set[i].getOutputNominalValuesInt(0);
			countsByClass[cl]++;
		}

		for(i=0;i<numClasses;i++) {
			int num=countsByClass[i];
			initSamplings[i] = new Sampling(num);
			instByClass[i] = new int[num];
			countsByClass[i]=0;
		}

		for(i=0;i<numInst;i++) {
			int cl=set[i].getOutputNominalValuesInt(0);
			instByClass[cl][countsByClass[cl]++]=i;
		}
	}

	public int getNumInstances(){return Parameters.NumInstances;}

	
	public void initializeWindowing(int traintest)
	{
		if(traintest == Parameters.TRAIN){
			if(Parameters.windowingMethod == Parameters.ILAS) {
				windowingEnabled=true;
				if(win==null) win=new windowingILAS();
			} else if(Parameters.windowingMethod == Parameters.GWS) {
				windowingEnabled=true;
				if(win==null) win=new windowingGWS();
			} else {
				windowingEnabled=false;
				window=null;
			}

			if(windowingEnabled) {
				win.setInstances(set,Parameters.NumInstances);
				Object[] reso =  win.newIteration();
				windowSize = (Integer) reso[0];
				window = (Instance[]) reso[1];
			}
		} else {
			windowingEnabled=false;
			window=null;
		}
	}
	
	public Instance getInstance(int index) {
		if(windowingEnabled) return window[index];
		return set[index];
	}
	
	public Instance[] getInstancesOfIteration() {
		if(windowingEnabled) return window;
		return set;
	}

	public Instance[] getAllInstances() {
		return set;
	}

	public Instance[] getOrigInstances() {
		return origSet;
	}
	public int getNumInstancesOrig() {
		return numInstancesOrig;
	}



	public boolean isWindowingEnabled() {return (Parameters.windowingMethod != Parameters.NONE);}

	int numVersions(){
		if(windowingEnabled) return win.numVersions();
		return 1;
	}
	
	
	public int getNumInstancesOfIteration(){
		if(windowingEnabled) return windowSize;
		return Parameters.NumInstances;
	}
	


	public boolean newIteration(boolean isLast){
		if(isLast) {
			windowingEnabled=false;
			return true;
		}

		if(Parameters.windowingMethod != Parameters.NONE) {
			Object[] reso =  win.newIteration();
			windowSize = (Integer) reso[0];
			window = (Instance[]) reso[1];
			if(win.needReEval()) return true;
			return false;
		}

		return false;
	}


	public Instance getInstanceInit(int forbiddenCL){
		
		if(classWiseInit) {
			if(forbiddenCL!=numClasses) {
				boolean allEmpty=true;
				int i;

				for(i=0;i<numClasses;i++) {
					if(i==forbiddenCL) continue;
					if(countsByClass[i]>0) {
						allEmpty=false;
						break;
					}
				}
				if(allEmpty) {
					return null;
				}
			}

			int cl;
			do {
				if(forbiddenCL!=numClasses) {
					cl=Randomize.Randint(0,numClasses-1);
					if(cl>=forbiddenCL) cl++;
				} else {
					cl=Randomize.Randint(0,numClasses);
				}
			} while(countsByClass[cl]==0);

			int pos=initSamplings[cl].getSample();
			int insIndex=instByClass[cl][pos];
			Instance ins=set[insIndex];

			return ins;
		} else {
			int nc = numClasses;
			int[] count = new int[nc];
			int total=0;
			int i;
			if(forbiddenCL != numClasses) nc--;
		
			for(i=0;i<nc;i++) {
				if(i<forbiddenCL) 
					count[i]=initSamplings[i].numSamplesLeft();
				else 
					count[i]=initSamplings[i+1].numSamplesLeft();
				total+=count[i];
			}
			
			// esto es para cuando no quedan instancias de clases que no sean por defecto
			if(total == 0) return null;
			
			int pos=Randomize.Randint(0,total);
			int acum=0;
			boolean found=false;
			for(i=0;i<nc && !found;i++) {
				acum+=count[i];
				if(pos<acum) {
					found=true;
				}
			}
			i--;
			if(i>=forbiddenCL) i++;

			pos=initSamplings[i].getSample();
			int insIndex=instByClass[i][pos];
			Instance ins = set[insIndex];
			
			return ins;
		}
	}


	public void removeInstancesAndRestart(classifier cla){
		int i;
		

		int numRemoved=0;
		int index=0;
		int[] countClassRem = new int[numClasses];

		for(i=0;i<numClasses;i++) {
			countClassRem[i]=0;
		}

		int numOrig = Parameters.NumInstances;

		while(index<Parameters.NumInstances) {
			if(cla.doMatch(set[index])) {
				set[index]=set[Parameters.NumInstances-1];
				numRemoved++;
				Parameters.NumInstances--;
			} else {
				countClassRem[set[index].getOutputNominalValuesInt(0)]++;
				index++;
			}
		}


		initInstanceLists();

		initializeWindowing(Parameters.TRAIN);
	}
}



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

import java.util.Arrays;
import org.core.Randomize;
import keel.Dataset.Instance;

public class windowingGWS extends windowing{
	
	Instance[] set;
	Instance[][] instancesOfClass;
	Instance[] sample;
	int sampleSize;
	double[] classQuota;
	int[] classSizes;
	int howMuch;
	int numStrata;
	int numClasses;
	int stratum;
	int currentIteration;


	public boolean needReEval() {
		return true;
	}

	public int numVersions() {
		return numStrata;
	}

	public int getCurrentVersion() {
		return stratum;
	}

	
	public void setInstances(Instance[] pSet, int pHowMuch){
		int i;

		set=pSet;
		howMuch=pHowMuch;
		
		int[] numInstC = computeNumInstC();

		numClasses = Parameters.numClasses;
		numStrata = Parameters.numStrataWindowing;
		instancesOfClass= new Instance [numClasses][];
		classSizes = new int[numClasses];
		classQuota = new double[numClasses];

		int capacity=0;
		for(i=0;i<numClasses;i++)  {
			int num = numInstC[i];
			instancesOfClass[i] = new Instance[num];
			classSizes[i]=0;
			classQuota[i]=(double)num/(double)numStrata;
			capacity+=(int)Math.ceil(classQuota[i]);
		}
		
		sample = new Instance[capacity];

		for(i=0;i<howMuch;i++) {
			int cls=set[i].getOutputNominalValuesInt(0);
			System.out.println("caca de vaca = " + classSizes[cls]);
			instancesOfClass[cls][classSizes[cls]++]=set[i];
		}

		currentIteration=0;
	}

	
	public Object[] newIteration(){
		int i,j;

		stratum=currentIteration%numStrata;
		currentIteration++;

		sampleSize=0;
		for(i=0;i<numClasses;i++) {
			int fixQ=(int)classQuota[i];
			for(j=0;j<fixQ;j++) {
				int pos=Randomize.Randint(0,classSizes[i]);
				sample[sampleSize++]=instancesOfClass[i][pos];
			}
			double prob=classQuota[i]-fixQ;
			if(Randomize.Rand()<prob) {
				int pos=Randomize.Randint(0,classSizes[i]);
				sample[sampleSize++]=instancesOfClass[i][pos];
			}
		}

		Object[] res = new Object[2];
		res[0] = (Integer) sampleSize;
		res[1] = (Instance[]) sample;
		
		return res;	
	}
	
	public int[] computeNumInstC(){
				
		int[] countClass = new int[Parameters.numClasses];
		Arrays.fill(countClass, 0);
		
		for(int i = 0 ; i < howMuch ; ++i)
			countClass[set[i].getOutputNominalValuesInt(0)]++;
		
		return countClass;
	}

}


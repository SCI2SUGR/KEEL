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

public class agentPerformance {
	
	int numClassifiers;
	int numClasses;

	double numInstancesOK;
	double numInstancesKO;
	double numInstancesNC;
	double numInstancesTotal;
	int[][] statisticsForEachClass;
	int[][] statisticsConfusionMatrix;
	int[] classifierActivated;
	int[] classifierCorrect;
	int[] classifierWrong;
	int aliveClassifiers;
	
	public double getAccuracy() { return numInstancesOK/numInstancesTotal; }
	public double getError(){return numInstancesKO/numInstancesTotal;}
	public double getNC(){return numInstancesNC/numInstancesTotal;}
	public int getNumError(){return (int)numInstancesKO;}
	public int getNumNC(){return (int)numInstancesNC;}
	
	public int getActivationsOfClassifier(int classifier) { 
		return classifierActivated[classifier]; 
	}
	
	int getCorrectPredictionsOfClassifier(int classifier) {
		return classifierCorrect[classifier];
	}
	
	double getAccOfClassifier(int classifier) {
		return (double)classifierCorrect[classifier]/(double)classifierActivated[classifier];
	}
	
	public int getAliveClassifiers(){return aliveClassifiers;}
	
	void disableClassifier(int classifier) {
		classifierActivated[classifier]=0;
	}

	double getLSacc(int classifier) {
		if(classifierActivated[classifier]==0) return 0;
		double acc=getAccOfClassifier(classifier);
		double laplaceAcc=(classifierCorrect[classifier]+1.0)
			/(classifierActivated[classifier]+numClasses);
		return (acc<laplaceAcc?acc:laplaceAcc);
	}

	int isClassifierWrong(int classifier) {
		return classifierWrong[classifier];
	}

	public agentPerformance(int pNumClassifiers, int pNumClasses){
		
		numClasses = pNumClasses;
		numClassifiers = pNumClassifiers;

		numInstancesOK = 0;
		numInstancesKO = 0;
		numInstancesNC = 0;
		numInstancesTotal = 0;
		aliveClassifiers = 0;

		int i, j;
		statisticsForEachClass = new int [numClasses][];
		statisticsConfusionMatrix = new int [numClasses][];
		for (i = 0; i < numClasses; i++) {
			statisticsConfusionMatrix[i] = new int[numClasses];
			statisticsForEachClass[i] = new int[3];
		}

		classifierActivated = new int[numClassifiers];
		classifierCorrect = new int[numClassifiers];
		classifierWrong = new int[numClassifiers];
		for (i = 0; i < numClassifiers; i++) {
			classifierActivated[i] = 0;
			classifierCorrect[i] = 0;
			classifierWrong[i] = 0;
		}

		for (i = 0; i < numClasses; i++) {
			for (j = 0; j < numClasses; j++)
				statisticsConfusionMatrix[i][j] = 0;
			for (j = 0; j < 3; j++)
				statisticsForEachClass[i][j] = 0;
		}
	}

	public void addPrediction(int realClass,int predictedClass,int usedClassifier){
		numInstancesTotal++;
		if(usedClassifier!=-1) {
			if(classifierActivated[usedClassifier] == 0) {
				aliveClassifiers++;
			}
			classifierActivated[usedClassifier]++;
			statisticsConfusionMatrix[realClass][predictedClass]++;
			if (predictedClass == realClass) {
				numInstancesOK++;
				statisticsForEachClass[realClass][0]++;
				classifierCorrect[usedClassifier]++;
			} else {
				classifierWrong[usedClassifier]++;
				numInstancesKO++;
				statisticsForEachClass[realClass][1]++;
			}
		} else {
			numInstancesNC++;
			statisticsForEachClass[realClass][2]++;
		}
	}

	public double getAverageActivation(){
			double actDR=0;
		if(Parameters.defaultClassOption!=Parameters.DISABLED) 
			actDR=classifierActivated[numClassifiers-1];
		return (numInstancesTotal-numInstancesNC-actDR)/numInstancesTotal;
	}

}

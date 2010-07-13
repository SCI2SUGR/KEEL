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
import java.util.Vector;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;


public class classifier_aggregated {
	
	Vector<classifier> classifiers;
	double accuracy;


	public classifier_aggregated() {
		
		classifiers = new Vector<classifier>();
		
		switch(Parameters.defaultClassOption) {
		
		case Parameters.MAJOR:	
			Parameters.defaultClassInteger = getMostFrequentClass();
			break;
			
		case Parameters.MINOR:
			Parameters.defaultClassInteger = getLeastFrequentClass();
			break;
			
		case Parameters.DISABLED:
		default:
			Parameters.defaultClassInteger = -1;
		break;
		
		}
		
	}

	public int getClase(int cla) {
		if(Parameters.defaultClassInteger!=-1 && cla==classifiers.size()) 
			return Parameters.defaultClassInteger;
		return classifiers.get(cla).getClase();
	}

	public int getNumClassifiers() {
		int numCL=classifiers.size();
		if(Parameters.defaultClassInteger!=-1) numCL++;
		return numCL;
	}

	public void setDefaultRule(instanceSet is) {

		int i;

		if(Parameters.defaultClassOption!=Parameters.DISABLED) return;

		int nc=Parameters.numClasses;
		int[] classCounts = new int[nc];
		for(i=0;i<nc;i++) classCounts[i]=0;

		int numInst=Parameters.is.getNumInstances();
		Instance[] instances=Parameters.is.getAllInstances();
		for(i=0;i<numInst;i++) {
			classCounts[instances[i].getOutputNominalValuesInt(0)]++;
		}

		int max=classCounts[0];
		int posMax=0;
		for(i=1;i<nc;i++) {
			if(classCounts[i]>max) {
				posMax=i;
				max=classCounts[i];
			}
		}

		Parameters.defaultClassInteger=posMax;
	}

	
	public int classify(Instance ins) {
		int i;

		int size=classifiers.size();
		for(i=0;i<size;i++) {
			if(classifiers.get(i).doMatch(ins)) return i;
		}
		if(Parameters.defaultClassInteger!=-1) return size;
		return -1;
	}

	public double getAccuracy() {return accuracy;}
	public void setAccuracy(double acc) {accuracy=acc;}

	public String dumpPhenotype() {
		int i;
		String temp = "";
		
		int size=classifiers.size();
		String string = "";
		for(i=0;i<size;i++) {
			temp = i +":";
			string += temp;
			temp = classifiers.get(i).dumpPhenotype();
			
			string += temp;
		}
		
		if(Parameters.defaultClassInteger!=-1) {
			temp = i +":Default rule -> " + Attributes.getOutputAttribute(0).getNominalValue(Parameters.defaultClassInteger) +"\n";
			string += temp;
		}
		string += "\n";
		return string;
	}


	public void addClassifier(classifier cl) {
		classifiers.addElement(cl);
	}
	
	public int getMostFrequentClass(){
		
		int[] countClass = new int[Parameters.numClasses];
		Arrays.fill(countClass, 0);
		
		for(int i = 0 ; i < Parameters.NumInstances ; ++i)
			countClass[Parameters.instances[i].getOutputNominalValuesInt(0)]++;
		
		int pos = 0;
		int max = countClass[0];
		
		for(int i = 1 ; i < Parameters.numClasses ; ++i)
			if(countClass[i] > max)
				pos = i;
		
		return pos;
	}
	
	public int getLeastFrequentClass(){
		
		int[] countClass = new int[Parameters.numClasses];
		Arrays.fill(countClass, 0);
		
		for(int i = 0 ; i < Parameters.NumInstances ; ++i)
			countClass[Parameters.instances[i].getOutputNominalValuesInt(0)]++;
		
		int pos = 0;
		int min = countClass[0];
		
		for(int i = 1 ; i < Parameters.numClasses ; ++i)
			if(countClass[i] < min)
				pos = i;
		
		return pos;
	}

}

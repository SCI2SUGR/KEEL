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

/** 
* <p> 
* @author Written by Luciano Sanchez (University of Oviedo) 21/07/2008 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzyLogitBoost;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Shared.Exceptions.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

import org.core.*;


public class ClassifierFuzzyLogitBoost {
/** 
* <p> 
* ClassifierFuzzyLogitBoost generates a Fuzzy Rule Based System classifier using 
* the Logit Boosting algorithm. This class acts as an interface for the FB (Fuzzy 
* Boosting) class with the KEEL environment. 
*
* Detailed in 
* M.J. del Jesus, F. Hoffmann, L. Junco, L. Sánchez. Induction of Fuzzy-Rule-
* Based Classifiers With Evolutionary Boosting Algorithms. IEEE Transactions on 
* Fuzzy Systems 12:3 (2004) 296-308.
* 
* </p> 
*/ 
	
	//The Randomize object used in this class
	static Randomize rand;
	//The maximum number of Fuzzy Rules to be learned.
	final static int MAXFUZZYRULES=1000;
	
/** 
* <p> 
* This private static method extract the dataset and the method's parameters  
* from the KEEL environment, learn the FRBS classifier using the chosen boosting 
* algorithm and print out the results with the validation dataset. 
* </p> 
* @param opt  integer with the chosen boosting learning method: 
*                    0 fuzzy adaboost algorithm
*                    1 fuzzy logit bit boosting algorithm
*                    2 fuzzy adaboost algorithm with max-min norms
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	private static void fuzzyBoosting(int opt,boolean tty, ProcessConfig pc) {
		
		
		try {
			
			int defaultNumberInputPartitions=0;
			int numberOfCrossovers=0;
			
			
			
			ProcessDataset pd=new ProcessDataset();
			
			String readALine;
			readALine=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
			
			if (pc.parNewFormat) pd.processClassifierDataset(readALine,true);
			else pd.oldClusteringProcess(readALine);
			
			int nData=pd.getNdata();           // Number of examples
			int nVariables=pd.getNvariables();   // Number of variables
			int nInputs=pd.getNinputs();     // Number of inputs
			int nClasses=pd.getNclasses();
			
			
			double[][] X = pd.getX();            // Input data
			int[] C = pd.getC();                 // Output data
			int [] Ct=new int[C.length];
			pd.showDatasetStatistics();
			
			double[] inputMaximum = pd.getImaximum();   // Maximum and minimum for input data
			double[] inputMinimum = pd.getIminimum();
			
			double outputMaximum = pd.getOmaximum();     // Maximum and minimum for output data
			double outputMinimum = pd.getOminimum();
			
			int[] nInputPartitions=new int[nInputs]; // Terminos en cada particion linguistica
			int nOutputPartitions;
			
			// "R" module compatibility
			double ytrain[][]=new double[X.length][nClasses];
			for (int i=0;i<ytrain.length;i++) ytrain[i][C[i]]=1;
			
			int p=0;
			double lintrain[]=new double[nData*nInputs];
			for (int i=0;i<nData;i++)
				for (int j=0;j<nInputs;j++) lintrain[p++]=X[i][j];
			
			p=0;
			double linytrain[]=new double[nData*nClasses];
			for (int j=0;j<nClasses;j++)
				for (int i=0;i<nData;i++) linytrain[p++]=ytrain[i][j];
			
			FB fb=new FB();
			
			
			int nRules; 
			nRules=pc.parRuleNumber;
			
			
			int nlabels; 
			nlabels=pc.parPartitionLabelNum;
			
			
			// Antes:
			// double []ruleBase=new double[2000];
			// Ahora:
			int ruleBaseSize=(nlabels+1)*nInputs+nRules+
				nRules*(nInputs+nClasses);
			double []ruleBase=new double[ruleBaseSize]; //2000];
			
			
			double numFails=0;
			fb.fuzzycreavacio(nInputs,nClasses,nlabels,lintrain,linytrain,ruleBase,rand);
			
			int limit=0; if (nClasses==2) limit=1; else limit=nClasses;
			double fit[]=new double[1];
			
			for (int r=0;r<nRules;r++) {
				numFails=0;
				switch (opt) {
					case 1: fb.fadaboostinc(nInputs,nClasses,lintrain,linytrain,ruleBase); break;
					case 2: fb.flogitboostinc(nInputs,nClasses,lintrain,linytrain,ruleBase,false); break;
					case 3: fb.fadaboostincmaxmin(nInputs,nClasses,lintrain,linytrain,ruleBase,fit); break;
				}
				for (int i=0;i<X.length;i++) {
					
					double [] segs;
					if (opt==1 || opt==2)
						segs=fb.fuzzyclasifica(X[i],nClasses,ruleBase);
					else segs=fb.fuzzyclasificamaxmin(X[i],nClasses,ruleBase);
					
					int ac=fb.argmax(segs);
					if (ac!=(int)C[i]) {
						numFails++;
					} else {
					}
					Ct[i]=ac;
				}
			}
			System.out.println("Train: ="+numFails/X.length);
			pc.trainingResults(C,Ct);
			
			// Error test
			ProcessDataset pdt = new ProcessDataset();
			int nTest,nTestInputs,nTestVariables;
			readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
			
			if (pc.parNewFormat) pdt.processClassifierDataset(readALine,false);
			else pdt.oldClusteringProcess(readALine);
			
			nTest = pdt.getNdata();
			nTestVariables = pdt.getNvariables();
			nTestInputs = pdt.getNinputs();
			pdt.showDatasetStatistics();
			
			if (nTestInputs!=nInputs) throw new IOException("Test file IOERR");
			
			X=pdt.getX(); C=pdt.getC(); int[] Co=new int[C.length];
			nData=X.length;
			
			// R module compatibility			  
			ytrain=new double[X.length][nClasses];        
			for (int i=0;i<ytrain.length;i++) ytrain[i][C[i]]=1;
			
			p=0;
			lintrain=new double[nData*nInputs];
			for (int i=0;i<nData;i++)
				for (int j=0;j<nInputs;j++) lintrain[p++]=X[i][j];
			
			p=0;
			linytrain=new double[nData*nClasses];
			for (int j=0;j<nClasses;j++)
				for (int i=0;i<nData;i++) linytrain[p++]=ytrain[i][j];
			
			numFails=0;
			for (int i=0;i<X.length;i++) {
				double [] segs;
				if (opt==1 || opt==2)
					segs=fb.fuzzyclasifica(X[i],nClasses,ruleBase);
				else segs=fb.fuzzyclasificamaxmin(X[i],nClasses,ruleBase);
				int ac=fb.argmax(segs);
				Co[i]=ac;
				if (ac!=(int)C[i]) {
					numFails++;
				} else {
				}
			}
			
			System.out.println("Test: ="+numFails/X.length);
			pc.results(C,Co);
			
			
		} catch(FileNotFoundException e) {
			System.err.println(e+" Input file not found");
		} catch(IOException e) {
			System.err.println(e+" Read Error");
		}
		
		
	}    
	
/** 
* <p> 
* This public static method runs the algorithm that this class concerns with. 
* </p> 
* @param args  Array of strings to sent parameters to the main program. The 
*              path of the algorithm's parameters file must be given.
*/ 	
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algo=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		ClassifierFuzzyLogitBoost pi=new ClassifierFuzzyLogitBoost();
		pi.fuzzyBoosting(2,tty,pc);
		
	}
	
	
}



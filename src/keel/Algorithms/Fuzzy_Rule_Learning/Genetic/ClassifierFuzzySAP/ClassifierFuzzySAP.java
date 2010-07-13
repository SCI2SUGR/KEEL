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
* @author Written by Luciano Sanchez (University of Oviedo) 21/07/2005 
* @author Modified by J.R. Villar (University of Oviedo) 19/12/2008
* @version 1.0 
* @since JDK1.4 
* </p> 
*/ 


package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzySAP;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;


import org.core.*;


public class ClassifierFuzzySAP {
/** 
* <p> 
* ClassifierFuzzySAP is intended to generate a Fuzzy Rule Based System
* (FRBS) classifier using an Simulated Annealing algorithm and Programming (SAP). 
* 
* This class makes used of the following classes:
*      {@link FuzzySAPClassifier}: the classifier to be learned
*      {@link SimulatedAnnealing}: to optimize following the SAP rules.
*
* Detailed in:
*
* L. Sánchez, I. Couso, J.A. Corrales. Combining GP Operators With SA Search To 
* Evolve Fuzzy Rule Based Classifiers. Information Sciences 136:1-4 (2001) 
* 175-192.
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
* from the KEEL environment, carries out with the partitioning of the
* input and output spaces, learn the FRBS classifier --which is a 
* FuzzySAPClassifier instance-- using the  {@link SimulatedAnnealing} algorithm,
* and prints out the results with the validation dataset. 
*
* </p> 
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	private static void fuzzySapClassifier(boolean tty, ProcessConfig pc) {
        
				
	try {
		String readALine=new String();
		
		int defaultNumberInputPartitions=0;
		int numberOfCrossovers=0;
		
		ProcessDataset pd=new ProcessDataset();
		
		readALine=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
		
		if (pc.parNewFormat) pd.processClassifierDataset(readALine,true);
		else pd.oldClusteringProcess(readALine);
		
		int nData=pd.getNdata();           // Number of examples
		int nVariables=pd.getNvariables();   // Number of variables
		int nInputs=pd.getNinputs();     // Number of inputs
		
		System.out.println("Number of examples="+nData);
		System.out.println("Number of inputs="+nInputs);
		
		double[][] X = pd.getX();             // Input data
		int[] C = pd.getC();                  // Output data
		int nClasses = pd.getNclasses();        // Number of classes
		
		double[] inputMaximum = pd.getImaximum();   // Maximum and minimum for input data
		double[] inputMinimum = pd.getIminimum();
		int[] nInputPartitions=new int[nInputs];
		pd.showDatasetStatistics();
		
		
		// Partitions definition
		FuzzyPartition[] inputPartitions=new FuzzyPartition[nInputs];
		
		for (int i=0;i<nInputs;i++) {
			nInputPartitions[i]=pc.parPartitionLabelNum;
			inputPartitions[i]=new FuzzyPartition(inputMinimum[i],inputMaximum[i],nInputPartitions[i]);
		}
		
		FuzzyPartition outputPartitions=new FuzzyPartition(nClasses);
		
		int localHeight;
		localHeight=pc.parMaxHeigth;
		
		// Simulated Annealing optimization
		FuzzySAPClassifier p = new FuzzySAPClassifier(inputPartitions,outputPartitions,localHeight,pc.parFitnessType,rand);
		
		p.setExamples(X,C);
		
		int nIterations;
		nIterations=pc.parIterNumber;
		
		int gaMutationID=OperatorIdent.GAPMUTAGA;
		int gpMutationID=OperatorIdent.GAPMUTAGP;
		
		double df;
		df=pc.parDeltaFit;
		
		GeneticAlgorithm AG=new SimulatedAnnealing(
												   p,	
												   pc.parCrGAProb,        // GA Mutation probability
												   df,                  // Expected fitness increment for a crossover 
												   pc.parP0,            // Probability for accepting -deltafit in 0 iteration
												   pc.parP1,            // Probability for accepting -deltafit in final iteration
												   pc.parMutAmpl,      // Mutation amplitude
												   pc.parNSUB,          // Number of iterations for each temperature
												   rand,
												   pc.parMutaId2,
												   pc.parMutaId3,
												   pc.parLoIterNumber,
												   pc.parLoId,
												   pc.parLoProb);
		
		
		
		p=(FuzzySAPClassifier)AG.evolve(nIterations);
		
		// Result is printed
		p.debug();
		System.out.println("Classification error of the rule base: "+p.fitness());
		pc.trainingResults(C,p.getCo());
		
		ProcessDataset pdt = new ProcessDataset();
		int nTest,nTestInputs,nTestVariables;
		readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
		
		if (pc.parNewFormat) pdt.processClassifierDataset(readALine,false);
		else pdt.oldClusteringProcess(readALine);
		
		nTest = pdt.getNdata();
		nTestVariables = pdt.getNvariables();
		nTestInputs = pdt.getNinputs();
		pdt.showDatasetStatistics();
		
		if (nTestInputs!=nInputs) throw new IOException("IOError in test file");
		
		double[][] Xp=pdt.getX(); int [] Cp=pdt.getC();
		
		p.setExamples(Xp,Cp);
		System.out.println("Test error: "+p.fitness());
		pc.results(Cp,p.getCo());
		
		
	} catch(FileNotFoundException e) {
		System.err.println(e+" Train file not found");
	} catch(IOException e) {
		System.err.println(e+" Read error");
	} catch(invalidFitness e) {
		System.err.println(e);
	} catch(invalidCrossover e) {
		System.err.println(e);
	} catch(invalidMutation e) {
		System.err.println(e);
	} catch(invalidOptim e) {
		System.err.println(e);
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
	ClassifierFuzzySAP pi=new ClassifierFuzzySAP();
	pi.fuzzySapClassifier(tty,pc);
	
}


}


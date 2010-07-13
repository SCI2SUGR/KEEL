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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierFuzzyGAP;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Classifier.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Shared.Exceptions.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;


import org.core.*;


public class ClassifierFuzzyGAP {
/** 
* <p> 
* ClassifierFuzzyGAP is intended to generate a Fuzzy Rule Based System
* (FRBS) classifier using an Genetic Algorithm and Programming (GAP). 
* 
* This class makes used of the following classes:
*      {@link FuzzyGAPClassifier}: the classifier to be learned
*      {@link GeneticAlgorithm}: to optimize following the GAP rules, The concrete
*                        algorithm used depends on the Steady and Niches
*                        parameters, varying between the {@link AlgorithmGAPGen}, the
*                        {@link AlgorithmGAPNiches} and the {@link AlgorithmGAPSteady}.
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
* FuzzyGAPClassifier instance-- using the GAP algorithm --which is an instance 
* of the GeneticAlgorithm class-- and print out the results with the validation 
* dataset. 
*
* If the parameter Steady is not fixed then the GAP used is the AlgorithmGAPGen. 
* If that parameter is fixed, if the parameter Niches is fixed then the GAP
* used is the AlgorithmGAPNiches. If the latter parameter is not fixed then the
* GAP used is the AlgorithmGAPSteady.
* 
* </p> 
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	private static void fuzzyGAPClassifier(boolean tty, ProcessConfig pc) {
        
		
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
			int [] Ct=new int[C.length];
            
            double[] inputMaximum = pd.getImaximum();    // Maximum and Minimum for input data
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
            
            int localnPopulations=pc.parIslandNumber;
			int lPopulation=pc.parPopSize;
            
            System.out.println("Pop. size="+lPopulation);
            System.out.println("Islands="+localnPopulations);
            
            int lOption=0;
            
			boolean STEADY=pc.parSteady;
            
            int localHeight=10;  //the desired maximum height of the tree
            localHeight=pc.parMaxHeigth;
            
            int nIterations=0; //the number if iterations
            nIterations=pc.parIterNumber;
            //the geneticalgorithm and programming parameters
            int lTournament=4; 
            double lmutationProb=0.05; 
            double lmutationAmpl=0.1;
            double migrationProb=0.001;
            double localOptProb=0.0;
            int localOptIterations=0;
            double gaCrossoverProb=0.5;
            double gaMutationProb=0.5;
            
                lTournament=pc.parTourSize;
                lmutationProb=pc.parMutProb;
                lmutationAmpl=pc.parMutAmpl;
                migrationProb=pc.parMigProb;
                localOptProb=pc.parLoProb;
                localOptIterations=pc.parLoIterNumber;
                gaCrossoverProb=pc.parCrGAProb;
                gaMutationProb=pc.parMuGAProb;
            
            boolean lNumNiches=false;
            if (STEADY) {
                    lNumNiches=pc.parNiche;
            }
            
            // Genetic Algorithm optimization
            FuzzyGAPClassifier p = new FuzzyGAPClassifier(inputPartitions,outputPartitions,localHeight,pc.parFitnessType,rand);
            
            p.setExamples(X,C);
            
            GeneticAlgorithm AG;
            int gaCrossoverID=OperatorIdent.GAPCROSSGA; int gpCrossoverID=OperatorIdent.GAPCROSSGP;
            int gaMutationID=OperatorIdent.GAPMUTAGA; int gpMutationID=OperatorIdent.GAPMUTAGP;
            
            if (STEADY) {
                if (lNumNiches)  AG=new AlgorithmGAPNiches(p,lPopulation,lTournament,lmutationProb,lmutationAmpl,
                                                       migrationProb,gaCrossoverProb,gaMutationProb,localnPopulations,localOptProb,localOptIterations,OperatorIdent.AMEBA,
                                                       rand,gaCrossoverID,gpCrossoverID,gaMutationID,gpMutationID);
                else AG=new AlgorithmGAPSteady(p,lPopulation,lTournament,lmutationProb,lmutationAmpl,migrationProb,
                                               gaCrossoverProb,gaMutationProb,localnPopulations,localOptProb,localOptIterations,OperatorIdent.AMEBA,
                                               rand,gaCrossoverID,gpCrossoverID,gaMutationID,gpMutationID);
            }
            else AG=new AlgorithmGAPGen(p,lPopulation,localnPopulations,lmutationProb,lmutationAmpl,migrationProb,
                                        gaCrossoverProb,gaMutationProb,localOptProb,localOptIterations,OperatorIdent.AMEBA,rand,
                                        gaCrossoverID,gpCrossoverID,gaMutationID,gpMutationID);
            
            p=(FuzzyGAPClassifier)AG.evolve(nIterations);
            
            // Result is printed
            p.debug();
            System.out.println("Train error= "+p.fitness());
			pc.trainingResults(C,p.getCo());
            
            ProcessDataset pdt = new ProcessDataset();
            int nTest,nTestInputs,nTestVariables;
			readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processClassifierDataset(readALine,false);
            else pdt.oldClusteringProcess(readALine);
            
            nTest = pdt.getNdata();
            nTestVariables = pdt.getNvariables();
            nTestInputs = pdt.getNinputs();
            
            if (nTestInputs!=nInputs) throw new IOException("IOError in test file");
            
            double[][] Xp=pdt.getX(); int [] Cp=pdt.getC();
            pdt.showDatasetStatistics();
            
            p.setExamples(Xp,Cp);
            System.out.println("Test error "+p.fitness());
            pc.results(Cp,p.getCo());
			
        } catch(FileNotFoundException e) {
            System.err.println(e+" Train file not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
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
		ClassifierFuzzyGAP pi=new ClassifierFuzzyGAP();
		pi.fuzzyGAPClassifier(tty,pc);
		
	}
	
	
}


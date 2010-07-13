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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ModelFuzzyGAP;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;


import org.core.*;


public class ModelFuzzyGAP {
	/** 
	* <p> 
	* ModelFuzzyGAP is intended to generate a Fuzzy Rule Based System
	* (FRBS) model using an Genetic Algorithm and Programming (GAP). 
	* 
	* This class makes used of the following classes:
	*      {@link FuzzyGAPModelIndividual}: the individual to be learned
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
	* input and output spaces, learn the FRBS model --which is a 
	* {@link FuzzyGAPModelIndividual} instance-- using the GAP algorithm --which is an instance 
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
    private static void fuzzyGAPmodelling(boolean tty, ProcessConfig pc) {
        
        try {
            
            String lines=new String();
            int option=0;
            
            int defaultNumberInputPartition=0;
            int nCrosses=0;
            
            ProcessDataset pd=new ProcessDataset();
            
            lines=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(lines,true);
            else pd.oldClassificationProcess(lines);
            
            
            int ndata=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            
            double[][] X = pd.getX();             // Input data
            double[] Y = pd.getY();               // Output data
            pd.showDatasetStatistics();
            
            double[] inputMaximum = pd.getImaximum();   // Maximum and minimum for input data
            double[] inputMinimum = pd.getIminimum();
            
            double outputMaximum = pd.getOmaximum();     // Maximum and minimum for output data
            double outputMinimum = pd.getOminimum();
            
            int[] numberOfInputPartitions=new int[nInputs]; // Linguistic partition terms
            int numberOfOutputPartitions;
            
            // Partitions definition
            FuzzyPartition[] inputPartitions=new FuzzyPartition[nInputs];
            for (int i=0;i<nInputs;i++) {
                numberOfInputPartitions[i]=pc.parPartitionLabelNum;
                inputPartitions[i]=new FuzzyPartition(inputMinimum[i],inputMaximum[i],numberOfInputPartitions[i]);
			}
            numberOfOutputPartitions=pc.parPartitionLabelNum;
            FuzzyPartition outputPartitions=new FuzzyPartition(outputMinimum,outputMaximum,numberOfOutputPartitions);
            System.out.println(outputPartitions.aString());

			int lPopulation=pc.parPopSize;
			int numberOfIslands=pc.parIslandNumber;
            
            System.out.println("Population="+lPopulation);
            System.out.println("Islands="+numberOfIslands);
            
            
            boolean STEADY=pc.parSteady;
            
            boolean NICHES=pc.parNiche;
            
            int defuzzificationType=RuleBase.DEFUZCDM;
            
            int theHeight=10;
			theHeight=pc.parMaxHeigth;
            
            // Genetic Algorithm Optimization
            FuzzyGAPModelIndividual p = new FuzzyGAPModelIndividual(inputPartitions,outputPartitions,theHeight,pc.parFitnessType,rand,defuzzificationType);
            
            p.setExamples(X,Y);
            
            int nIterations=pc.parIterNumber;
            
            GeneticAlgorithm AG;
            int crossoverID=OperatorIdent.GAPCROSSGA; int gpCrossoverID=OperatorIdent.GAPCROSSGP;
            int mutationID=OperatorIdent.GAPMUTAGA; int gpMutationID=OperatorIdent.GAPMUTAGP;
            int tournament=4;
            double mutationProb=0.05;
            double mutationAmpl=0.1;
            double migrationProb=0.001;
            double gaCrossoverProb=0.5;
            double gaMutationProb=0.5;
            double localOptProb=0.0;
            int localOptIter=0;
                tournament=pc.parTourSize;
                mutationProb=pc.parMutProb;
                mutationAmpl=pc.parMutAmpl;
                migrationProb=pc.parMigProb;
                localOptProb=pc.parLoProb;
                localOptIter=pc.parLoIterNumber;
                gaCrossoverProb=pc.parCrGAProb;
                gaMutationProb=pc.parMuGAProb;
            
            
            if (STEADY) {
                if (NICHES) AG=new AlgorithmGAPNiches(p,lPopulation,tournament,mutationProb,
                                                      mutationAmpl,migrationProb,gaCrossoverProb,gaMutationProb,numberOfIslands,localOptProb,localOptIter,
                                                      OperatorIdent.AMEBA,rand,crossoverID,gpCrossoverID,mutationID,gpMutationID);
                else AG=new AlgorithmGAPSteady(p,lPopulation,tournament,mutationProb,mutationAmpl,
                                               migrationProb,gaCrossoverProb,mutationProb,numberOfIslands,localOptProb,localOptIter,
                                               OperatorIdent.AMEBA,rand,crossoverID,gpCrossoverID,mutationID,gpMutationID);
            }
            else AG=new AlgorithmGAPGen(p,lPopulation,numberOfIslands,mutationProb,mutationAmpl,migrationProb,
                                        gaCrossoverProb,gaMutationProb,localOptProb,localOptIter,
                                        OperatorIdent.AMEBA,rand,crossoverID,gpCrossoverID,mutationID,gpMutationID);
            
            
            p=(FuzzyGAPModelIndividual)AG.evolve(nIterations);
            
            
            // Result is printed
            p.debug();
			pc.trainingResults(Y,p.getYo());
            System.out.println("RMS train "+p.fitness());
            
            ProcessDataset pdt = new ProcessDataset();
            int nTest,nTestInputs,nTestVariables;
			lines=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(lines,false);
            else pdt.oldClassificationProcess(lines);
            
            nTest = pdt.getNdata();
            nTestVariables = pdt.getNvariables();
            nTestInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (nTestInputs!=nInputs) throw new IOException("IOERR in test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY();
            
            p.setExamples(Xp,Yp);
            System.out.println("RMS test = "+p.fitness());
            pc.results(Yp,p.getYo()); 
            
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Input file not found");
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
		ModelFuzzyGAP pi=new ModelFuzzyGAP();
		pi.fuzzyGAPmodelling(tty,pc);
		
	}
	
	
}


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



package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ModelFuzzyPittsBurgh;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Individual.*;
import keel.Algorithms.Shared.Exceptions.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;


import org.core.*;


public class ModelFuzzyPittsBurgh {
/** 
* <p> 
* ModelFuzzyPittsBurgh is intended to generate a Fuzzy Rule Based System
* (FRBS) classifier using the Pittsburgh genetic algorihm Approach. 
* 
* This class makes used of the following classes:
*      {@link PittsburghModel}: the regression model to be learned
*      {@link GeneticAlgorithm}: to optimize following the genetic rules.
*                       The concrete algorithm used depends on the Steady parameter
*                       varying between the {@link GeneticAlgorithmSteady} if set,
*                       otherwise {@link GeneticAlgorithmGenerational}.
*
* Detailed in:
*
* De Jong, K. A., Learning With Genetic Algorithm: An Overview, Machine Learning, 
* VOL. 3, 1988, pp121-138.
*
* Michalewicz, Z., Genetic Algorithms + Data Structures = Evolution Programs, Springer, 
* 1995
* 
* </p> 
*/ 
	
	//The Randomize object used in this class
	static Randomize rand;
	//The maximum number of Fuzzy Rules to be learned.
	final static int MAXFUZZYRULES=1000;
	
/** 
* <p> 
* <pre>
* This private static method extract the dataset and the method's parameters  
* from the KEEL environment, carries out with the partitioning of the
* input and output spaces, learn the FRBS regression model --which is a 
* {@link PittsburghClassifier} instance-- using the   GP algorithm --which is an instance 
* of the GeneticAlgorithm class-- and prints out the results with the validation 
* dataset. 
*
* If the parameter Steady is not fixed then the genetic algorithm used is the 
* {@link GeneticAlgorithmGenerational}. If that parameter is fixed then the GP 
* used is the {@link GeneticAlgorithmSteady}.
* </pre>
* </p> 
* @param tty  unused boolean parameter, kept for compatibility
* @param pc   ProcessConfig object to obtain the train and test datasets
*             and the method's parameters.
*/ 	
	   private static void fuzzyPittsburghModelling(boolean tty, ProcessConfig pc) {
		   
		   try {
			   
			   String readALine=new String();
			   int lOption=0;
			   
			   int defaultNumberInputPartitions=0;
			   int numberOfCrossovers=0;
			   
			   ProcessDataset pd=new ProcessDataset();
			   
			   readALine=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
			   
			   
			   if (pc.parNewFormat) pd.processModelDataset(readALine,true);
			   else pd.oldClassificationProcess(readALine);
			   
			   
			   int nData=pd.getNdata();           // Number of examples
			   int nVariables=pd.getNvariables();   // Number of variables
			   int nInputs=pd.getNinputs();     // Number of inputs
			   
			   double[][] X = pd.getX();             // Input data
			   double[] Y = pd.getY();               // Output data
			   double[] Yt = new double[Y.length];
			   pd.showDatasetStatistics();
			   
			   double[] inputMaximum = pd.getImaximum();   // Maximum and Minimum for input data
			   double[] inputMinimum = pd.getIminimum();
			   
			   double outputMaximum = pd.getOmaximum();     // Maximum and Minimum for output data
			   double outputMinimum = pd.getOminimum();
			   
			   int[] nInputPartitions=new int[nInputs]; // Linguistic partition terms
			   int nOutputPartitions;
			   
			   // Partitions definition
			   // Check the number of rules
			   int nrules=1;
			   FuzzyPartition[] inputPartitions=new FuzzyPartition[nInputs];
			   for (int i=0;i<nInputs;i++) {
				   nInputPartitions[i]=pc.parPartitionLabelNum;
				   inputPartitions[i]=new FuzzyPartition(inputMinimum[i],inputMaximum[i],nInputPartitions[i]);
				   nrules*=nInputPartitions[i];
				   if (nrules>MAXFUZZYRULES) break;
			   }
			   nOutputPartitions=pc.parPartitionLabelNum;
			   FuzzyPartition outputPartitions=new FuzzyPartition(outputMinimum,outputMaximum,nOutputPartitions);
			   System.out.println("Number of rules = "+nrules);
			   
			   if (nrules<MAXFUZZYRULES) {
					   int lPopulation=pc.parPopSize;
					   int localnPopulations=pc.parIslandNumber;
				   
				   
				   boolean STEADY=pc.parSteady;
				   
				   
				   
				   int defuzzificationType=RuleBase.DEFUZCDM;
				   
				   // Rule base
				   FuzzyModel sistema=
					   new FuzzyModel(inputPartitions,outputPartitions,
									  RuleBase.product,
									  RuleBase.sum,
									  defuzzificationType);
				   
				   // Genetic Algorithm Optimization
				   PittsburghModel p = new PittsburghModel(sistema,pc.parFitnessType,rand);
				   
				   p.setExamples(X,Y);
				   
				   int nIterations=pc.parIterNumber;
				   
				   GeneticAlgorithm AG;
				   int crossoverID=OperatorIdent.GENERICROSSOVER; int mutationID=OperatorIdent.GENERICMUTATION;
				   
				   int lTournament=4;
				   double mutacion=0.05;
				   double lmutationAmpl=0.1;
				   double migrationProb=0.001;
				   double localOptProb=0.0;
				   int localOptIterations=0;
					   lTournament=pc.parTourSize;
					   mutacion=pc.parMutProb;
					   lmutationAmpl=pc.parMutAmpl;
					   migrationProb=pc.parMigProb;
					   localOptProb=pc.parLoProb;
					   localOptIterations=pc.parLoIterNumber;
				   
				   if (STEADY) AG=new GeneticAlgorithmSteady(p,lPopulation,localnPopulations,lTournament,
															 mutacion,lmutationAmpl,migrationProb,localOptProb,localOptIterations,
															 OperatorIdent.AMEBA,rand,crossoverID,mutationID);
				   else AG=new GeneticAlgorithmGenerational(p,lPopulation,localnPopulations,mutacion,lmutationAmpl,
															migrationProb,localOptProb,localOptIterations,
															OperatorIdent.AMEBA,rand,crossoverID,mutationID);
				   
				   
				   p=(PittsburghModel)AG.evolve(nIterations);
				   
				   // Result is printed
				   p.debug();
				   pc.trainingResults(Y,p.getYo()); 
				   System.out.println("RMS Train = "+p.fitness());
				   
				   ProcessDataset pdt = new ProcessDataset();
				   int nTest,nTestInputs,nTestVariables;
				   readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
				   
				   if (pc.parNewFormat) pdt.processModelDataset(readALine,false);
				   else pdt.oldClassificationProcess(readALine);
				   
				   nTest = pdt.getNdata();
				   nTestVariables = pdt.getNvariables();
				   nTestInputs = pdt.getNinputs();
				   pdt.showDatasetStatistics();
				   
				   if (nTestInputs!=nInputs) throw new IOException("IOERR Test file");
				   
				   double[][] Xp=pdt.getX(); double [] Yp=pdt.getY(); 
				   
				   
				   p.setExamples(Xp,Yp);
				   System.out.println("RMS test = "+p.fitness());
				   pc.results(Yp,p.getYo()); 
				   
			   } else {
				   
				   pc.trainingResults(Y,Yt);
				   
				   ProcessDataset pdt = new ProcessDataset();
				   int nTest,nTestInputs,nTestVariables;
				   readALine=(String)pc.parInputData.get(ProcessConfig.IndexTest);
				   
				   if (pc.parNewFormat) pdt.processModelDataset(readALine,false);
				   else pdt.oldClassificationProcess(readALine);
				   
				   nTest = pdt.getNdata();
				   nTestVariables = pdt.getNvariables();
				   nTestInputs = pdt.getNinputs();
				   pdt.showDatasetStatistics();
				   
				   if (nTestInputs!=nInputs) throw new IOException("IOERR test file");
				   
				   double[][] Xp=pdt.getX(); double [] Yp=pdt.getY(); 
				   
				   
				   double [] Yo = new double[Yp.length];
				   System.out.println("Generating constant output (0)");
				   
				   // Yo = 0
				   
				   pc.results(Yp,Yo); 
				   
			   }
			   
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
		   ModelFuzzyPittsBurgh pi=new ModelFuzzyPittsBurgh();
		   pi.fuzzyPittsburghModelling(tty,pc);
		   
	   }
	   
	   
}

	   
	   

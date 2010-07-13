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
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2007
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Symbolic_Regression.Shared;
import keel.Algorithms.Shared.Exceptions.*;
import keel.Algorithms.Shared.Parsing.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Algorithms.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Model.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.*;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;

import org.core.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

public class ParseFileRegSym {
	/**
	 * <p>
	 *   This class obtains a symbolic model of training input data using algorithms:
	 *   -GAP (Genetic Algorithm Programming). method symbolicRegressionFuzzyGAP.
	 *   -SAP (Simulated Anneling Programming). method symbolicRegressionFuzzySAP.
     * </p>  
     */
    /** 
     * <p> 
     * This method learns a symbolic model for training input data given in pc using the Genetic Algorithm Programming (GAP) paradigm. 
     * The method enables selecting the type of constants used in learning algorithm (Crisp or Fuzzy).
     * Finally, it prints out the statistical results for train and test datasets.  
	 * </p>
	 * @param constType type of variables (FuzzyRegressor.Crisp or FuzzyRegressor.Fuzzy) 
	 * @param tty  unused boolean parameter, kept for compatibility
	 * @param pc   ProcessConfig object to obtain the train and test datasets
	 *             and the method's parameters.
	 * @param rand Random number generator
	 */
	
	
	public static void symbolicRegressionFuzzyGAP(int constType,boolean tty, ProcessConfig pc, Randomize rand) {
        
        try {
            
            String line = new String();
            boolean STEADY=pc.parSteady;
            
            int option=0;
            boolean niche=false;
            if (STEADY) {
                    niche=pc.parNiche;
            }
                        
			int populationSize=pc.parPopSize;
			int numIslands=pc.parIslandNumber;
            System.out.println("Pop. Size="+populationSize);
            System.out.println("Islands="+numIslands);
            
            int default_neparticion=0;
            int ncruces=0;
            
            ProcessDataset pd=new ProcessDataset();
            
			line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(line,true);
            else pd.oldClassificationProcess(line);
            
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            pd.showDatasetStatistics();
            
            System.out.println("Number of examples="+nData);
            System.out.println("Number of inputs="+nInputs);
            
            double[][] X = pd.getX();             // Input data
            double[] Y = pd.getY();               // Output data
            
            double[] eMaximum = pd.getImaximum();   // Maximum and minimum for input data
            double[] eMinimum = pd.getIminimum();
            
            double sMaximum = pd.getOmaximum();     // Maximum and minimum for output data
            double sMinimum = pd.getOminimum();
            
            int[] nEpartition=new int[nInputs]; // Linguistic partition terms
            int nSpartition;
            
            int numCtes=pc.parGALen;
            
            int depth=10;
			depth=pc.parMaxHeigth;
            
            // Genetic Algorithm Optimization
            RegSymFuzzyGP p = new RegSymFuzzyGP(-1,1,nInputs,
												constType,numCtes,depth,pc.parFitnessType,rand);
            
            p.asignaejemplos(X,Y,pc.fuzzyTolerance);
            
            int nIter=pc.parIterNumber;
            
            GeneticAlgorithm AG;
            int IDCROSSGA=OperatorIdent.GAPCROSSGA; 
            int IDMUTAGA=OperatorIdent.GAPMUTAGA;
            int IDCROSSGP=OperatorIdent.GAPCROSSGP; 
            int IDMUTAGP=OperatorIdent.GAPMUTAGP;
            int tournamentSize=4;
            double mutationProb=0.05;
            double mutationAmpl=0.1;
            double migrationProb=0.001;
            double crossGAProb=0.5;
            double mutaGAProb=0.5;
            double localOptimProb=0.0;
            int localOptimNum=0;
                tournamentSize=pc.parTourSize;
                mutationProb=pc.parMutProb;
                mutationAmpl=pc.parMutAmpl;
                migrationProb=pc.parMigProb;
                localOptimProb=pc.parLoProb;
                localOptimNum=pc.parLoIterNumber;
                crossGAProb=pc.parCrGAProb;
                mutaGAProb=pc.parMuGAProb;
            
            if (STEADY) {
                if (niche) AG=new AlgorithmGAPNiches(p,populationSize,tournamentSize,mutationProb,
                                                      mutationAmpl,migrationProb,crossGAProb,mutaGAProb,numIslands,localOptimProb,localOptimNum,
                                                      OperatorIdent.AMEBA,rand,IDCROSSGA,IDCROSSGP,IDMUTAGA,IDMUTAGP);
                else AG=new AlgorithmGAPSteady(p,populationSize,tournamentSize,mutationProb,mutationAmpl,migrationProb,
                                               crossGAProb,mutaGAProb,numIslands,localOptimProb,localOptimNum,
                                               OperatorIdent.AMEBA,rand,IDCROSSGA,IDCROSSGP,IDMUTAGA,IDMUTAGP);
            }
            
            else AG=new AlgorithmGAPGen(p,populationSize,numIslands,mutationProb,mutationAmpl,migrationProb,
                                        crossGAProb,mutaGAProb,localOptimProb,localOptimNum,
                                        OperatorIdent.AMEBA,rand,IDCROSSGA,IDCROSSGP,IDMUTAGA,IDMUTAGP);
            
            try {
                p=(RegSymFuzzyGP)AG.evolve(nIter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            // Result is printed
            p.debug();
			pc.trainingResults(Y,p.getYo());
            System.out.println("Fitness (train) = "+p.fitness());
            p.debug_fitness();
            
            ProcessDataset pdt = new ProcessDataset();
            int nTests,npInputs,npVariables;
			line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(line,false);
            else pdt.oldClassificationProcess(line);
            
            nTests = pdt.getNdata();
            npVariables = pdt.getNvariables();
            npInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (npInputs!=nInputs) throw new IOException("Wrong test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY();
            
            p.asignaejemplos(Xp,Yp,pc.fuzzyTolerance);
            System.out.println("Fitness (test) "+p.fitness());
            p.debug_fitness();
            
            pc.results(Yp,p.getYo()); 
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Examples file not found");
        } catch(IOException e) {
            System.err.println(e+" Read Error");
        } catch(invalidFitness e) {
            System.err.println(e);
        }
        
    }
    /** 
     * <p> 
     * This method learns a symbolic model for training input data given in pc using the Simulated Anneling Programming (SAP) paradigm. 
     * The method enables selecting the type of constants used in learning algorithm (Crisp or Fuzzy).
     * Finally, it prints out the statistical results for train and test datasets.  
	 * </p>
	 * @param constType type of variables (FuzzyRegressor.Crisp or FuzzyRegressor.Fuzzy) 
	 * @param tty  unused boolean parameter, kept for compatibility
	 * @param pc   ProcessConfig object to obtain the train and test datasets
	 *             and the method's parameters.
	 * @param rand Random number generator
	 */
    public static void symbolicRegressionFuzzySAP(int constType,boolean tty, ProcessConfig pc,Randomize rand) {
        
        try {
            
            String line = new String();
            int option;
            
            
            int defaultNepartition=0;
            int nCrosses=0;
            
            ProcessDataset pd=new ProcessDataset();
            
			line=(String)pc.parInputData.get(ProcessConfig.IndexTrain);
            
            if (pc.parNewFormat) pd.processModelDataset(line,true);
            else pd.oldClassificationProcess(line);
            
            
            int nData=pd.getNdata();           // Number of examples
            int nVariables=pd.getNvariables();   // Number of variables
            int nInputs=pd.getNinputs();     // Number of inputs
            pd.showDatasetStatistics();
            
            System.out.println("Number of examples="+nData);
            System.out.println("Number of inputs="+nInputs);
            
            double[][] X = pd.getX();             // Input data
            double[] Y = pd.getY();               // Output data
            
            double[] eMaximum = pd.getImaximum();   // Maximum and Minimum for input data
            double[] eMinimum = pd.getIminimum();
            
            double sMaximum = pd.getOmaximum();     // Maximum and Minimum for output data
            double sMinimum = pd.getOminimum();
            
            int[] nEparticion=new int[nInputs]; // Linguistic partition terms
            int nSparticion;
            
            int numCtes=pc.parGALen;
            
            int depth=10;
			depth=pc.parMaxHeigth;
            
            // Genetic Algorithm Optimization
            RegSymFuzzyGP p = 
                new RegSymFuzzyGP(-1,1,nInputs,constType,numCtes,
								  depth,pc.parFitnessType,rand);
            
            p.asignaejemplos(X,Y,pc.fuzzyTolerance);
            
            int nIter=pc.parIterNumber;
            int IDMUTAGA=OperatorIdent.GAPMUTAGA;
            int IDMUTAGP=OperatorIdent.GAPMUTAGP;
            
            double df=pc.parDeltaFit;
            
            GeneticAlgorithm AG=new SimulatedAnnealing(
													   p,	
													   pc.parCrGAProb,               // GA Mutation probability
													   df,                         // Waited fitness increment for a crossover
													   pc.parP0,                   // Probability for accepting -deltafit in 0 iteration
													   pc.parP1,                   // Probability for accepting -deltafit in final iteration
													   pc.parMutAmpl,             // Mutation amplitude
													   pc.parNSUB,                 // Number of iterations for each temperature
													   rand,
													   pc.parMutaId2,
													   pc.parMutaId3,
													   pc.parLoIterNumber,
													   pc.parLoId,
													   pc.parLoProb);
            
            try {
                p=(RegSymFuzzyGP)AG.evolve(nIter);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            
            // Result is printed
            p.debug();
			pc.trainingResults(Y,p.getYo());
            System.out.println("Fitness (train) "+p.fitness());
            p.debug_fitness();
            
            ProcessDataset pdt = new ProcessDataset();
            int nTest,ntInputs,ntVariables;
			line=(String)pc.parInputData.get(ProcessConfig.IndexTest);
            
            if (pc.parNewFormat) pdt.processModelDataset(line,false);
            else pdt.oldClassificationProcess(line);
            
            nTest = pdt.getNdata();
            ntVariables = pdt.getNvariables();
            ntInputs = pdt.getNinputs();
            pdt.showDatasetStatistics();
            
            if (ntInputs!=nInputs) throw new IOException("Wrong test file");
            
            double[][] Xp=pdt.getX(); double [] Yp=pdt.getY();
            
            p.asignaejemplos(Xp,Yp,pc.fuzzyTolerance);
            System.out.println("Fitness (test) = "+p.fitness());
            p.debug_fitness();
            
            pc.results(Yp,p.getYo()); 
            
        } catch(FileNotFoundException e) {
            System.err.println(e+" Example file not found");
        } catch(IOException e) {
            System.err.println(e+" Read error");
        } catch(invalidFitness e) {
            System.err.println(e);
        }
        
    }
	
	
}

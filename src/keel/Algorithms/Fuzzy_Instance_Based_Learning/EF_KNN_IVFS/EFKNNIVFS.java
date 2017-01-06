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
 *
 * File: EFKNNIVFS.java
 *
 * The EFKNNIVFS algorithm.
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import org.core.Files;
import org.core.Randomize;
import java.util.StringTokenizer;

class EFKNNIVFS extends FuzzyIBLAlgorithm {

	private int K;
    private int pop_size;
    private int evaluations_limit;
    private IVFSKNN classifier;

	/** 
	 * Reads the parameters of the algorithm. 
	 * 
	 * @param script Configuration script
	 * 
	 */
	@Override
	protected void readParameters(String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

        //Getting the seed
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        seed = Long.parseLong(tokens.nextToken().substring(1));
	    
	    //Getting the K parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));

        //Getting the population size
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        pop_size = Integer.parseInt(tokens.nextToken().substring(1));

        //Getting the limit of evaluations
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        evaluations_limit = Integer.parseInt(tokens.nextToken().substring(1));


	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public EFKNNIVFS(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="EFKNNIVFS";

        //Initialization of random generator

        Randomize.setSeed(seed);

        //Initialization of the IVFS-KNN classifier
        IVFSKNN.configureClass(K,trainData,trainOutput,nClasses);

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method

    /**
     * Initialize the search of solutions for EF-KNN-IVFS
     */
    public void init_search(){

		Timer.resetTime();
        CHC.initialize(pop_size);

    }

    /**
     * Perform the search of EF-KNN-IVFS solutions.
     *
     * This is done by running multiple generations of the
     * CHC algorithm until the available evaluations are exhausted
     */
    public void search_solution(){

        int evaluations = Fitness.getEvaluations();

        while(evaluations < evaluations_limit){
            evaluations = CHC.doGeneration();
        }

        Chromosome solution = CHC.getSolution();

        classifier = new IVFSKNN(solution);

		Timer.setModelTime();
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
    }

    /**
     * Classifies the training set (leave-one-out)
     */
    public void classifyTrain(){

        //Start of training time
        Timer.resetTime();

        classifyTrainSet();

        //End of training time
        Timer.setTrainingTime();

        //Showing results
        System.out.println(name+" "+ relation + " Training " + Timer.getTrainingTime() + "s");

    } //end-method

    /**
     * Classifies the test set
     */
    public void classifyTest(){

        //Start of training time
        Timer.resetTime();

        classifyTestSet();

        //End of test time
        Timer.setTestTime();

        //Showing results
        System.out.println(name+" "+ relation + " Test " + Timer.getTestTime() + "s");

    } //end-method

    /**
     * Classifies the training set
     */
    public void classifyTrainSet(){

        for(int i=0;i<trainData.length;i++){
            trainPrediction[i] = classifier.classifyInstance(referenceData[i]);
        }

    } //end-method

    /**
     * Classifies the test set
     */
    public void classifyTestSet(){

        for(int i=0;i<testData.length;i++){
            testPrediction[i] = classifier.classifyInstance(testData[i]);
        }

    } //end-method

    /**
     * Reports the results obtained
     */
    public void printReport(){

        writeOutput(outFile[0], trainOutput, trainPrediction);
        writeOutput(outFile[1], testOutput, testPrediction);

        ReportTool.setResults(trainOutput,trainPrediction,testOutput,testPrediction,nClasses);

        ReportTool.printReport();

    } //end-method

} //end-class


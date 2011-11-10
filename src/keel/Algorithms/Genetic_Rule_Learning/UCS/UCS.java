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
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;

 
public class UCS {
/**
 * <p>
 * UCS
 * </p>
 *
 * This class controls the UCS run. The core of UCS is a population of classifiers (pop). 
 * Moreover, it has a set of objects to implement the different operators and mechanisms:
 * environments, GA, prediction array, etc.
 */
  
/**
 * It contains all the classifiers in the population
 */
    private Population pop; 

    
/**
 * It is a prediction array
 */
    private PredictionArray predArray; 
    
    
/**
 * It is a reference to the train environment.
 */
    private Environment env; 
    
    
/**
 * It is a reference to the test environment.
 */
    private Environment testEnv; 
    
    
/**
 * It is an object to make statistics of the current run.
 */
    private Statistic statistics; 
    


/**
 * <p>
 * It is a reference to the class that controls the time 
 * run.
 *
 */
    private TimeControl tControl = null;


/**
 * <p>
 * Initializes an UCS object. 
 * It configures the UCS run reading the configuration file.
 * </p>
 * @param configFile is the name of the config file.
 */
  public UCS (String configFile){
	System.out.println ("  > Creating UCS object ");
    try{
		System.out.println ("    > Parsing the configuration file");
        Config.doParse( configFile );
    }catch(Exception e){
        System.out.println ("Exception while parsing the configuration file");
        System.exit(0);	
    }
    
    if (Config.UCSRun.toLowerCase().equals("train")){
        if (Config.typeOfProblem.toLowerCase().equals("mux")){
            env = new MPEnvironment();
        }else if (Config.typeOfProblem.toLowerCase().equals("fp")){
            System.out.println ("    > Loading the train set:"+Config.trainFile);
            env = new SSFileEnvironment(Config.trainFile, true);
			
			Config.r_0 = ((double)Config.clLength / (double)env.getNumberOfExamples()) * 100;
			System.out.println ("    > R_0 has automatically been updated to: " + Config.r_0 );
        }	
        else {
            System.out.println ("There is any problem identified as '"+Config.typeOfProblem+"'");
        }	
    }
    else{
        System.out.println ("Type of run not known: "+Config.UCSRun);
       	System.exit(0);
    }

    if ( Config.doTest ){
        
		if ( Config.typeOfProblem.toLowerCase().equals("mux") ){
			testEnv = new MPEnvironment();
		}
		else{
			System.out.println ("\n    > Loading the test set: "+Config.testFile);
        	if (Config.UCSRun.equalsIgnoreCase("train")) 
        	        testEnv = new SSFileEnvironment( Config.testFile, false );
        	else    testEnv = new SSFileEnvironment( Config.testFile, true );
        	Config.numberOfTestExamples = testEnv.getNumberOfExamples();
		}
    }


    if (Config.doStatistics){
        System.out.println ("\n    > Intializing the object for statistics.");
        statistics = new Statistic( );	
    }

    Config.setSeed((long) Config.seed);

    tControl = new TimeControl();
    pop = null;

  } // end UCS

    
   

/**
 * It runs the system. 
 */
  public void run (){ 	
	long iTime = System.currentTimeMillis();

	startTrainUCS();
	finishUCS(iTime);	
  }//end run 


  
/**
 * Runs one UCS train experiment. It creates a new population and 
 * calls the function doOneTrainExperiment. It makes explore and exploit
 * iterations. It also updates time statistics.
 */
  
  public void startTrainUCS(){
    int [] globalResults = createAndInitArray(3);

    // The initial time of the system is get to make statistics
    long iTrainTime = System.currentTimeMillis();
    System.out.println ("\n\n================== STARTING THE TRAIN ==================");
    System.out.println("\n ------------ Some Output Statistics ------------");
    System.out.println("Iteration - %Correct - Error - MicroCl - MacroCl");

    // Does create a population of size Config.popSize + numberOfActions
    pop  = new Population( Config.numberOfActions + Config.popSize );

    // The function that makes one train experiment is called.	
    doOneTrainExperiment( globalResults );

    // Testing the data model with the train set
    doTestTrain( true );

    // The time wasted in training is updated	
    tControl.updateTrainTime( iTrainTime );

    // Testing the data model with the test set
    doTest ( true );

	// Printing the population
    pop.sortPopulation( 0, pop.getMacroClSum()-1, 0 );
	statistics.printPopulation(pop);
    
  }  //end startTrainUCS




/**
 * Runs one train UCS experiment. Now, in the train run, one exploit is done between
 * N explores (where N is a parameter defined by the user). So, the operators of covering, 
 * the GA and the update parameters routines are called in the explore runs. 
 * 
 * @param globalResults contains the addition of all classified, not classified
 * and correct and wrong classified examples of the exploit executions in the train.
 */
  public void doOneTrainExperiment (int [] globalResults){
    int tStamp=0,exploitNum=0; // Is the time of the UCS
    int [] windowExecutionResults = createAndInitArray(3);

    // We create a variable to store the error of the system in each exploit prediction.

    while ( tStamp < Config.numberOfExplores ){
        double [] envState = env.newState();
        int classOfExample = env.getCurrentClass();

        // A single step explore iteration is made.
        doOneSingleStepExplore(envState, classOfExample, tStamp);

        // We look if a exploit has to be made. 
        if (tStamp % Config.exploresBetweenExploits == 0 && tStamp != 0){
            envState = env.newState();
            classOfExample = env.getCurrentClass();
            doOneSingleStepExploit( envState, classOfExample, tStamp, windowExecutionResults, false,0, false );
            exploitNum++;
            // The screen statistics are made if it's necessary
            doScreenStatistics( tStamp, exploitNum, windowExecutionResults, globalResults );
        }

        // The time of the system is updated.
        tStamp++;
    }

    // We write the last iteration results.
    doScreenStatistics(tStamp, exploitNum, windowExecutionResults, globalResults);

    // The last statistics are made.
    for ( int i=0; i<windowExecutionResults.length; i++ ){
            globalResults[i] += windowExecutionResults[i];
            windowExecutionResults[i] = 0;
    }
    // Enable if you want to sort the population with the classifiers numerosity
    pop.sortPopulation(0,pop.getMacroClSum() -1, 0);

    // The train statistics are printed
    //statistics.makeTrainStatistics(pop, Config.numberOfExplores, globalResults);

  } // end doOneTrainExperiment




/**
 * <p>
 * Performs a test experiment. It does not apply the covering operator, the GA 
 * and the update parameters routines. It only creates the match set and the
 * prediction array, choosing the best action. If there are more than
 * one action with the same probability, it chooses one of them randomly.
 * For the moment, the examples are chosen sequentially from the environment if it's
 * a file single step environment, to be sure that all the examples will participate 
 * in the test run.
 * </p>
 * @param tEnv is the environment to do the test. It can be the same or different from 
 * the train environment. 
 * @param typeOfTest is a parameter that indicates the kind of test: testing with the test
 * set (0) or testing with the train set (1)
 * @param globalResults contains the addition of all classified, not classified and correct 
 * and wrong classified examples of the exploit executions in the train.
 * </p>
 */

  public void doOneTestExperiment( Environment tEnv, int typeOfTest, int [] globalResults, boolean writeExpOut ){
    int tStamp=0;
	double [] envState = null;
    int [] windowExecutionResults = createAndInitArray(3);

    if (Config.sequentialTest) tEnv.beginSequentialExamples();

    while ( tStamp<Config.numberOfTestExamples ){

        // A new enviromental state is got.
        if ( Config.sequentialTest ) 	envState = tEnv.getSequentialState();
        else 						envState = tEnv.newState();
        int classOfExample = tEnv.getCurrentClass();

        // An exploit is made with the new environmental state
        doOneSingleStepExploit( envState, classOfExample, tStamp, windowExecutionResults, true, typeOfTest, writeExpOut);

        // The statistics are written to file. Enable if you want partial results of test
        // doScreenStatistics(tStamp, tStamp, windowExecutionResults, globalResults);

        // The time of the system is incremented.
        tStamp++;
    }
    // We add the partial results to the global ones. 
    for (int i=0; i<windowExecutionResults.length; i++){
        globalResults[i] += windowExecutionResults[i];
        windowExecutionResults[i] = 0;
    }

    // Screen statistics are made.
    doTestScreenStatistics( tStamp, globalResults, Config.numberOfTestExamples );


	statistics.makeTestStatistics(pop, Config.numberOfTestExamples, globalResults, typeOfTest);

  } // end doOneTestExperiment





  		
/**
 * <p>
 * Performs one explore iteration. It applies the covering operator, the 
 * GA and the update parameters routines if it is necessary. 
 * </p>
 * @param envState is the new example that has to be classified. 
 * @param tStamp is the current time stamp of the system. It is used to decide 
 * if the GA has to be applied, and to create new classifiers. 
 */		
  
  public void doOneSingleStepExplore( double [] envState, int classOfExample, int tStamp){
    
    // Creating the match set
    Population matchSet = new Population( pop, envState );	

    // Creating the correct set
    Population correctSet = new Population( matchSet, envState, classOfExample, tStamp );

	// Updating parameters of classifiers in [M]
	matchSet.updateParametersSet( correctSet.getMicroClSum(), envState, classOfExample, tStamp );
	
    // At the end, we run the genetic algorithm.
    correctSet.runGA( tStamp, envState, classOfExample );

  } // end doOneSingleStepExplore
  


/**
 * <p>
 * Performs one single step exploit. It only chooses the best prediction 
 * from the prediction array (it is not stochastic)
 * </p>
 * @param envState is the new example that has to be classified. 
 * @param tStamp is the current time stamp of the system. It's used to 
 * decide if the GA has to be applied, and to create new classifiers. 
 * @param windowExecutionResults is an array where the results are set 
 * down to make statistics. 
 * @param typeOfTest indicates the test set kind (if is the test set (0) or the train
 * set (1) ) of the run.
 * @param writeExpOut determines is an output file with expected-real 
 * output has to be writen.
 */

  public void doOneSingleStepExploit( double [] envState, int classOfExample, int tStamp, int []windowExecutionResults, 
  										boolean isTest, int typeOfTest, boolean writeExpOut){

    // We create the match set
    Population matchSet = new Population( pop, envState );	

    // With the match set the prediction array is created
    predArray = new PredictionArray( matchSet );

    // We choose the best action in the prediction array, and get the reward of doing that.
    int action = predArray.getBestAction();
    
    
    // We make the statistics
    if ( matchSet.getMacroClSum() > 0  &&  predArray.howManyBestActions() == 1){ 
            statistics.printStateAndClass(envState,action);
            
            if ( action == classOfExample  )	windowExecutionResults[0] ++;
            else								windowExecutionResults[1] ++;
    }
    else{	
			// We guess for the majority class
			if ( Config.defaultRule ){
				if ( Config.majorityClass == classOfExample )	windowExecutionResults[0] ++;
				else											windowExecutionResults[1] ++;
			}
			else{
            	statistics.printStateAndClassNoCov(envState,-1);
            	windowExecutionResults[2]++;
			}
    }
    
    // Writting the expected ouptut and the real output for a test instance
    if ( isTest && typeOfTest == 0 && writeExpOut ){ 
		if ( matchSet.getMacroClSum() > 0 )  	statistics.writeExpectedTestOut( classOfExample, action );
		else							   		statistics.writeExpectedTestOut( classOfExample, Config.majorityClass );
	}
	// Writting the expected output and the real output for a train instance
    else if (isTest && typeOfTest == 1 && writeExpOut){ 
		if ( matchSet.getMacroClSum() > 0 )  	statistics.writeExpectedTrainOut( classOfExample, action );
		else							   		statistics.writeExpectedTrainOut( classOfExample, Config.majorityClass );
	}


  } // end doOneSingleStepExploit




/**
 * It makes a test between or at the end of the traininig 
 *
 * @param writeExpOut indicates if the correct class and the predicted class have to
 * be printed for the test set according to the keel format 
 */

  private void doTest( boolean writeExpOut ){
    long iTestTime = System.currentTimeMillis();	
    System.out.println ("\n============= MAKING TEST BETWEEN A TRAIN =============");
    // A vector is initialited to make statistics
    int [] statisticsVector = createAndInitArray(3);
    doOneTestExperiment( testEnv, 0, statisticsVector, writeExpOut );
    System.out.println ("=======================================================\n");
    tControl.updateTestTime(iTestTime);
  }  // end doTest



/**
 * It makes a test with the train set.
 *
 * @param writeExpOut indicates if the correct class and the predicted class have to
 * be printed for the test set according to the keel format 
 */
  private void doTestTrain(boolean writeExpOut){   
    long iTestTime = System.currentTimeMillis();	
    System.out.println ("\n============= MAKING TEST WITH THE TRAIN SET =============");
    // A vector is initialited to make statistics
    int [] statisticsVector = createAndInitArray(3);
    Config.numberOfTestExamples = env.getNumberOfExamples();
    doOneTestExperiment( env, 1, statisticsVector, writeExpOut);
    Config.numberOfTestExamples = testEnv.getNumberOfExamples();
    System.out.println ("=======================================================\n");
    tControl.updateTestTime(iTestTime);
  }//end doTestTrain

   




/**
 * <p>
 * It computes the statistics
 * </p>
 * @param tStamp is the time of the UCS.
 * @param exploitNum is the number of exploits that UCS has made.
 * @param statisticVector is the vector that contains all the statistics 
 * @param globalResults is a vector with the global results statistics.
 */

  public void doScreenStatistics (int tStamp, int exploitNum, int [] statisticVector, int [] globalResults){
    if (exploitNum %Config.statisticWindowSize == 0 && exploitNum != 0){
            System.out.print (tStamp+"  "+((double)statisticVector[0]/ ((double)Config.statisticWindowSize)) );
			System.out.println ("  "+pop.getMacroClSum()+"  "+pop.getMicroClSum());

            statistics.makeIncStatistics( pop, tStamp, statisticVector );

            for (int i=0; i<statisticVector.length; i++){
                    globalResults[i] += statisticVector[i];
                    statisticVector[i] = 0;
            }
    }	   
  }//doScreenStatistics



/**
 * <p>
 * It does test screen statistics
 * </p>
 * @param tStamp is the system time.
 * @param globalResults is the sum of all statistics done in the system.
 * @param numProblems is the number of examples done in the run.
 */

  public void doTestScreenStatistics(int tStamp, int []globalResults, int numProblems){
	System.out.println ("\nGlobal results of test execution: ");
	System.out.println ("Correct: "+globalResults[0]+" Incorrect: "+globalResults[1]+" not Covered: "+globalResults[2]);
	System.out.println (tStamp+"  "+((double)globalResults[0]/(double)numProblems)+"  "+"  "+pop.getMacroClSum()+"  "+pop.getMicroClSum()+" ");
  }//end doTestScreenStatistics



/**
 * Creates and initializes an integer vector of size N
 * @param N is the size of the vector that has to be created
 */
  public int [] createAndInitArray(int N){
    int [] vect = new int[N];
    for (int i=0; i<N; i++) vect[i] = 0;
    return vect;
  }//end createAndInitArray


/**
 * <p>
 * It makes the last things to do after closing the program. It upgrades the times, print the last
 * statistics and close all opened files.
 * </p>
 * @param iTime is the actual time.
 */
  public void finishUCS(long iTime) {            	
    tControl.updateTotalTime (System.currentTimeMillis() - iTime);
    tControl.printTimes();
    if (statistics != null){
        statistics.makeTimeStatistics(tControl);
        statistics.closeFiles();
    }
  } // end finishUCS        



/**
 * Does print the population and stops the execution.
 * @param r is the bufferedReader where to read.
 */
   public void print ( BufferedReader r ) {
    try{
        pop.print();	
        System.out.println ("Print something to return to the menu");
        r.readLine();
    }catch (Exception e){
        e.printStackTrace();
    }
   } //end print
     
   
} // end UCS
                                       


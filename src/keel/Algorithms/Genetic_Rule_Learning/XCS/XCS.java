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
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Parser;
//import  keel.Algorithms.Genetic_Rule_Learning.XCS.TimeControl.TimeControl;
import java.util.*;
import java.lang.*;
import java.io.*;

public class XCS {
/**
 * <p>
 * This is the main class of the XCS. 
 *
 * The input parameters to run the aplication are:
 *
 * IN THE TRAIN MODE:
 *         java XCS  train  type_of_problem  configuration_file [descriptorFile]*  [examplesFile]*
 *
 * 		.The configuration_file is the file that contains all the initial values for XCS parameters, 
 * 		and it has to be parsed by the application.
 *
 * 		.The type_of_problem parameter indicates the problem to be executed. For each type 
 *		of problem, an environment has to be implemented. 
 * 
 * 		.The []* parameters are only necessary in single step file problems. In the descriptor file, 
 * 		the type of attributes are specified, while all the examples are in the examples file.
 *
 * IN THE TEST MODE:
 *         java XCS  test input_file type_of_problem  configuration_file  [descriptorFile]*  [examplesFile]*          
 * 		
 *		. The input_file is the file where the classifiers of a train execution have been stored. So, first of
 *		all, the aplication will read all the classifiers in the file, and then will start the test execution.		
 *
 *
 * IN THE REDUCTION MODE:
 *	   java XCS reduction input_rules_file configuration_file
 *
 *		In case of being a reduction, the rules are read from a file, and the reduction chosen in the 
 * 		configuration file is made.
 *
 * 		. The input_rules_file is the file that contains the input rules that have to be reduced.
 *
 *		. The configuration_file is the file that contains all the inital values for XCS parameters, 
 *		and it has to be parsed by the application.
 *
 * </p>
 * <p>
 * This class contains the XCS itself. It has the population, the prediction array, and the test, train 
 * and reduction environments, and objects of statistics and reduction classes. It is highly configurable, 
 * so, using a configuration file, usally with the extension ".kcf" (keel configuration), almost any possible
 * configuration can be defined.
 * </p>
 */
	
	
  
/**
 * <p>
 * It contains all the classifiers in the population
 * </p>
 */
    private Population pop; 

    
/**
 * <p>
 * It is a prediction array
 * </p>
 */
    private PredictionArray predArray; 
    
    
/**
 * <p>
 * It is a reference to the environment problem.
 * </p>
 */
    private Environment env; 
    
    
/**
 * <p>
 * It is a reference to the environment for the test.
 * </p>
 */
    private Environment testEnv; 
    
    
/**
 * <p>
 * It is a reference to the environment for the reduction.
 * </p>
 */
    private Environment reductionEnv;
    
    
/**
 * <p>
 * It is an object to make statistics of the current run.
 * </p>
 */
    private Statistic statistics; 
    

/**
 * <p>
 * It is the optimal population
 * </p>
 */
    private Population optimalPopulation = null; 


/**
 * <p>
 * It is a reference to a Reduction Operator
 * </p>
 */
    private Reduction reduction = null;


/**
 * <p>
 * It is a reference to the class that controls the time 
 * run.
 *
 */
    private TimeControl tControl = null;

/**
 * <p>
 * It is the type of run (1 train, 2 test, 3 reduction)
 * </p>
 */
    private String typeOfRun = null;


  public void run (){ 	
    long iTime = System.currentTimeMillis();
    if (typeOfRun.equals("train")){ // Train run
        startTrainXCS();
    }else if (typeOfRun.equals("test")){ //Test run
        startTestXCS();
    }else{ //Reduction run
        startReductXCS();
    }
    finishXCS(iTime);	
  }//end run 


/**
 * <p>
 * Initializes an XCS object. It configures the XCS run reading the configuration file.
 * </p>
 * @param configFile is the name of the config file.
 */
  public XCS (String configFile){
    try{
        Parser.doParse(configFile);
    }catch(Exception e){
        System.out.println ("Exception while parsing the configuration file");
        System.exit(0);	
    }
    
    System.out.println ("Config.doTest = "+Config.doTest);
    
    if (Config.XCSRun.toLowerCase().equals("train")){
        typeOfRun = "train";	
        if (Config.typeOfProblem.toLowerCase().equals("mux"))
            env = new MPEnvironment();
        else if (Config.typeOfProblem.toLowerCase().equals("par"))
            env = new PAREnvironment();
        else if (Config.typeOfProblem.toLowerCase().equals("pos")){
            env = new POSEnvironment();
        }
        else if (Config.typeOfProblem.toLowerCase().equals("dec")){
            env = new DECEnvironment();
        }
        else if (Config.typeOfProblem.toLowerCase().equals("rmp")){
            env = new RMPEnvironment();
        }
        else if (Config.typeOfProblem.toLowerCase().equals("fp")){
            System.out.println ("LOADING THE TRAIN SET:"+Config.trainFile);
            env = new SSFileEnvironment(Config.trainFile, true);
        }	
        else {
            System.out.println ("There is any problem identified as '"+Config.typeOfProblem+"'");
        }	
    }
    else if (Config.XCSRun.toLowerCase().equals("test")){
        typeOfRun = "test";	
    }
    else{
        typeOfRun = "reduct";
    }

    if (Config.doTest){
        System.out.println ("\nLOADING THE TEST SET "+Config.testFile);
        if (Config.XCSRun.equalsIgnoreCase("train")) 
                testEnv = new SSFileEnvironment(Config.testFile, false);
        else    testEnv = new SSFileEnvironment(Config.testFile, true);
        Config.numberOfTestExamples = testEnv.getNumberOfExamples();
    }

    if (Config.doReduction){
        System.out.println ("\nLOADING THE REDUCTION ENVIRONMENT");
        // The reduction is made with the train file.
        reductionEnv = new SSFileEnvironment(Config.trainFile, true);
        if (Config.typeOfReduction.toUpperCase().equals("SD") || Config.typeOfReduction.toUpperCase().equals("WD"))	
                reduction = new DixonReduction();
        if (Config.typeOfReduction.toUpperCase().equals("EW") || Config.typeOfReduction.toUpperCase().equals("NW"))
                reduction = new WilsonReduction();
        // The number of reduct iterations is get from the environment.				
        Config.numberOfReductExamples = reductionEnv.getNumberOfExamples();
    }	

    if (Config.doStatistics){
        statistics = new Statistic(typeOfRun,Config.statisticFileOutName);	
    }

    if (Config.getOptimalPopulation){
        optimalPopulation = new Population(Config.popSize);
        optimalPopulation.readPopulationFromFile(Config.optimalPopulationFile);
        System.out.println ("\nLOADING OPTIMAL POPULATION");
        optimalPopulation.print();
    }

    Config.setSeed((long) Config.seed);

    tControl = new TimeControl();
    pop = null;

    // Enable if you want see the parameters read from the configuration file
    //System.out.println ("------------------- the parser parameters are ------------------------- ");
    //Config.printContents();
    //System.out.println ("----------------------------------------------------------------------- ");    	
  } // end XCS

    
   

  
/**
 * <p>
 * Runs one XCS train experiment. It creates a new population and 
 * calls the function doOneTrainExperiment. It makes explore and exploit
 * iterations. It also updates time statistics.
 * </p>
 */
  
  public void startTrainXCS(){
    int [] globalResults = createAndInitArray(3);

    // The initial time of the system is get to make statistics
    long iTrainTime = System.currentTimeMillis();
    System.out.println ("\n\n================== THE TRAIN STARTS  ==================");
    System.out.println("\n ------------ Some Output Statistics ------------");
    System.out.println("Iteration - %Correct - Error - MicroCl - MacroCl");

    // Does create a population of size Config.popSize + numberOfActions
    pop  = new Population(Config.numberOfActions + Config.popSize);

    // The function that makes one train experiment is called.	
    doOneTrainExperiment(globalResults);

    // After all, the train set is tested over the final population
    doTestTrain(true);

    // The time wasted in training is updated	
    tControl.updateTrainTime(iTrainTime);

    // A test is done if it has been configurated
    //if (Config.numberOfExplores % Config.testWindow == 0 && Config.doTest){
    //    doTest(true);
    //}
    //if (Config.doTest)
        doTest(true);

    // A reduction iteration is made, if the user has configured that!!
    if (Config.doReduction && ( ( (Config.numberOfExplores > Config.initReductionIteration) && ( (Config.numberOfExplores-Config.initReductionIteration)%Config.reductWindow == 0)) || Config.initReductionIteration == Config.numberOfExplores) ){
        statistics.printPopulation(pop);
        doReduction();
        // The reducted population is writed to file
        try{	PrintWriter fo = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName+".LastReduct.plt")));
            pop.printPopulationToFile(fo);
            fo.close();}catch(Exception e){e.printStackTrace();
        }
    }	  
    else{
        // Enable if you want to sort the population (to read it better)
        //pop.sortPopulation(0,pop.getMacroClSum()-1,0);
        statistics.printPopulation(pop);
    }
  }  //end startTrainXCS



/**
 * <p>
 * Runs one test XCS experiment. The diference between test and train
 * is that in the test experiment only exploit trials are made, and the
 * parameters of the classifiers are not updated. In addition, the covering operator
 * is not applied, so, we can have some unclassified examples. 
 * </p>
 * 
 */

  public void startTestXCS(){
    int [] globalResults = createAndInitArray(3);

    // We get the initial time
    long iTestTime = System.currentTimeMillis();

    // A new population is created an read from file
    pop  = new Population(Config.numberOfActions + Config.popSize);
    System.out.println ("The population is read from file: "+ Config.populationFile);
    pop.readPopulationFromFile(Config.populationFile);

    // Finally, one test experiment is done		
    doOneTestExperiment(testEnv,0,globalResults, false);

    // The time statistics are updated. 
    tControl.updateTestTime(iTestTime);
	
  } // end startTestExperiment





/**
 * <p>
 * Runs one train XCS experiment. Now, in the train run, one exploit is done between
 * N explores (where N is a parameter defined by the user). So, the operators of covering, 
 * the GA and the update parameters routines are called in the explore runs. 
 * </p>
 * @param globalResults contains the addition of all classified, not classified
 * and correct and wrong classified examples of the exploit executions in the train.
 */
  public void doOneTrainExperiment (int [] globalResults){
    int tStamp=0,exploitNum=0; // Is the time of the XCS
    int [] windowExecutionResults = createAndInitArray(3);

    // We create a variable to store the error of the system in each exploit prediction.
    double [] sysError = new double[1];
    sysError[0] = 0.;

    while (tStamp < Config.numberOfExplores){
        double [] envState = env.newState();
        // A single step explore iteration is made.
        doOneSingleStepExplore(envState, tStamp);

        // We look if a exploit has to be made. 
        if (tStamp % Config.exploresBetweenExploits == 0 && tStamp != 0){
            envState = env.newState();
            doOneSingleStepExploit(env,envState, tStamp, windowExecutionResults, sysError, false,0, false);
            exploitNum++;
            // The screen statistics are made if it's necessary
            doScreenStatistics(tStamp, exploitNum, sysError, windowExecutionResults,globalResults);
        }


        // We perform a test example if the user has configured that!
        //if (Config.doTest && (tStamp+1)%Config.testWindow == 0 && tStamp != 0){
        //    doTest(false);	
        //}

        // A reduction iteration is made, if the user has configured that
        if ( Config.doReduction && ( ( (tStamp > Config.initReductionIteration) && ( (tStamp-Config.initReductionIteration)%Config.reductWindow == 0)) || Config.initReductionIteration == tStamp) && tStamp != 0 ){
            doReduction();	
        }


        //If a reduction has been made, we call for another test to evaluate the reduction
        //if (Config.doTest && Config.doReduction && tStamp%Config.reductWindow == 0 & tStamp != 0){
        //        doTest(false);	
        //}

        // The time of the system is updated.
        tStamp++;
    }

    // We write the last iteration results.
    doScreenStatistics(tStamp, exploitNum, sysError, windowExecutionResults, globalResults);

    // The last statistics are made.
    for (int i=0; i<windowExecutionResults.length; i++){
            globalResults[i] += windowExecutionResults[i];
            windowExecutionResults[i] = 0;
    }
    // Enable if you want to sort the population with the classifiers numerosity
    pop.sortPopulation(0,pop.getMacroClSum() -1, 0);

    // The train statistics are printed
    statistics.makeTrainStatistics(pop, optimalPopulation, Config.numberOfExplores, globalResults);

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

  public void doOneTestExperiment(Environment tEnv, int typeOfTest,int [] globalResults, boolean writeExpOut){
    int tStamp=0;
    int [] windowExecutionResults = createAndInitArray(3);

    // A variable that counts the error of the system prediction is initialized.
    double [] sysError = new double[1];
    sysError[0] = 0.;

    if (Config.sequentialTest) tEnv.beginSequentialExamples();

    while (tStamp<Config.numberOfTestExamples){
        // A new enviromental state is get.
        double [] envState = null;
        if (Config.sequentialTest) 	envState = tEnv.getSequentialState();
        else 				envState = tEnv.newState();

        // An exploit is made with the new environmental state
        doOneSingleStepExploit(tEnv,envState, tStamp, windowExecutionResults, sysError, true, typeOfTest, writeExpOut);

        // The statistics are written to file. Enable if you want partial results of test
        // doScreenStatistics(tStamp, tStamp, sysError, windowExecutionResults, globalResults);

        // The time of the system is incremented.
        tStamp++;
    }
    // We add the partial results to the global ones. 
    for (int i=0; i<windowExecutionResults.length; i++){
        globalResults[i] += windowExecutionResults[i];
        windowExecutionResults[i] = 0;
    }

    // Screen statistics are made.
    doTestScreenStatistics(tStamp, globalResults, sysError, Config.numberOfTestExamples);


    if (typeOfTest == 0 && (typeOfRun.equals("train") || typeOfRun.equals("test"))) 
        statistics.makeTestStatistics(pop, optimalPopulation, Config.numberOfTestExamples, globalResults);

  } // end doOneTestExperiment





  		
/**
 * <p>
 * Performs one explore iteration. It applies the covering operator, the 
 * GA and the update parameters routines if it is necessary. 
 * </p>
 * @param envState is the new example that has to be classified. 
 * @param acTime is the current time stamp of the system. It is used to decide 
 * if the GA has to be applied, and to create new classifiers. 
 */		
  
  public void doOneSingleStepExplore(double [] envState,int acTime){
    // First, the match set has to be created with the current state of the environment
    Population matchSet = new Population(envState,pop,acTime,true);	

    // With the match set the prediction array is created
    predArray = new PredictionArray(matchSet);

    // The action is chosen randomly
    int actionChoosen = predArray.chooseExploreAction();

    // Then, the action set has to be created
    Population actionSet = new Population(matchSet,actionChoosen);

    // We choose the best action int he prediction array, and get the reward of doing that.
    double reward = env.makeAction(actionChoosen);

    // The parametres of the classifiers in the action set have to be updated.
    actionSet.updateParametersSet(reward,0.0,envState, acTime);

    // At the end, we run the genetic algorithm.
    actionSet.runGA(acTime,envState);


    // The "new reduction algorithm" is called
    //if (Config.typeOfReduction != null && Config.typeOfReduction.toUpperCase().equals("NA"))
    //	actionSet.runReduction(acTime);	
	
  } // end doOneSingleStepExplore
  


/**
 * <p>
 * Performs one single step exploit. It only chooses the best prediction 
 * from the prediction array (it is not stochastic)
 * </p>
 * @param envState is the new example that has to be classified. 
 * @param acTime is the current time stamp of the system. It's used to 
 * decide if the GA has to be applied, and to create new classifiers. 
 * @param windowExecutionResults is an array where the results are set 
 * down to make statistics. 
 * @param sysError is an array where the errors in the predictions are 
 * set down to make posterior statistics. 
 * @param typeOfTest indicates the test set kind (if is the test set (0) or the train
 * set (1) ) of the run.
 * @param writeExpOut determines is an output file with expected-real 
 * output has to be writen.
 */

  public void doOneSingleStepExploit(Environment tEnv,double [] envState,int acTime,int []windowExecutionResults,double []sysError, boolean isTest, int typeOfTest, boolean writeExpOut){

    // We create the match set
    Population matchSet = new Population(envState,pop,acTime,false);	

    // With the match set the prediction array is created
    predArray = new PredictionArray(matchSet);

    // We choose the best action in the prediction array, and get the reward of doing that.
    int action = predArray.getBestAction();
    double reward = 0.0;

    // If there are two actions with the same prediction, reward = 0  	
    if (predArray.howManyBestActions() == 1){ 
            reward = tEnv.makeAction(action);		
    }

    // We make the statistics
    if (matchSet.getMacroClSum() > 0  && predArray.howManyBestActions() == 1){ // We look that, at least, the [M] has one classifier. 
            if (statistics != null) statistics.printStateAndClass(envState,action);
            if (tEnv.wasCorrect())	windowExecutionResults[0] ++;
            else			windowExecutionResults[1] ++;
    }
    else{	
            if (statistics != null) statistics.printStateAndClassNoCov(envState,-1);
            windowExecutionResults[2]++;
    }
    if (isTest && typeOfTest == 0 && writeExpOut){ 
		if (matchSet.getMacroClSum() > 0)  statistics.writeExpectedTestOut(tEnv.getEnvironmentClass(), action);
		else							   statistics.writeExpectedTestOut(tEnv.getEnvironmentClass(), -1);
	}
    else if (isTest && typeOfTest == 1 && writeExpOut){ 
		if (matchSet.getMacroClSum() > 0)  statistics.writeExpectedTrainOut(tEnv.getEnvironmentClass(), action);
		else							   statistics.writeExpectedTrainOut(tEnv.getEnvironmentClass(), -1);
	}

    sysError[0] += Math.abs (reward - predArray.getBestValue()); 

  } // end doOneSingleStepExploit




/**
 * It makes a test between or at the end of the traininig 
 */

  private void doTest(boolean writeExpOut){
    long iTestTime = System.currentTimeMillis();	
    System.out.println ("\n============= MAKING TEST BETWEEN A TRAIN =============");
    // A vector is initialited to make statistics
    int [] statisticsVector = createAndInitArray(3);
    doOneTestExperiment(testEnv,0,statisticsVector,writeExpOut);
    System.out.println ("=======================================================\n");
    tControl.updateTestTime(iTestTime);
  }  // end doTest



/**
 * It makes a test with the train set.
 */
  private void doTestTrain(boolean writeExpOut){   
    long iTestTime = System.currentTimeMillis();	
    System.out.println ("\n============= MAKING TEST WITH THE TRAIN SET =============");
    // A vector is initialited to make statistics
    int [] statisticsVector = createAndInitArray(3);
    Config.numberOfTestExamples = env.getNumberOfExamples();
    doOneTestExperiment(env,1,statisticsVector, writeExpOut);
    Config.numberOfTestExamples = testEnv.getNumberOfExamples();
    System.out.println ("=======================================================\n");
    tControl.updateTestTime(iTestTime);
  }//end doTestTrain

   

/**
 * It makes a reduction between or at the end of the traininig 
 */
  private void doReduction(){
    long iReductionTime = System.currentTimeMillis();

    System.out.println ("\n=========== MAKING REDUCTION BETWEEN A TRAIN ==========");
    pop = doOneReductionExecution (reductionEnv);
    System.out.println ("The reducted population has: "+pop.getMacroClSum()+" classifiers.");
    System.out.println ("=======================================================\n");

    tControl.updateReductionTime(iReductionTime);
  }//end doReduction





/**
 * <p>
 * It computes the statistics
 * </p>
 * @param tStamp is the time of the XCS.
 * @param exploitNum is the number of exploits that XCS has made.
 * @param sysError is the error sum of XCS.
 * @param statisticVector is the vector that contains all the statistics 
 * @param globalResults is a vector with the global results statistics.
 */

  public void doScreenStatistics (int tStamp, int exploitNum, double [] sysError, int [] statisticVector, int [] globalResults){
    if (exploitNum %Config.statisticWindowSize == 0 && exploitNum != 0){
            String sErr = new Double(sysError[0]/((double)Config.statisticWindowSize)).toString();
            sErr = sErr.substring(0,Math.min(8,sErr.length()-1));
            System.out.println (tStamp+"  "+((double)statisticVector[0]/ ((double)Config.statisticWindowSize))+"  "+sErr+"  "+pop.getMacroClSum()+"  "+pop.getMicroClSum());
            Population.numAplicacions = 0;

            statistics.makeIncStatistics(pop,optimalPopulation,tStamp,statisticVector, (sysError[0]/((double)Config.statisticWindowSize)));

            for (int i=0; i<statisticVector.length; i++){
                    globalResults[i] += statisticVector[i];
                    statisticVector[i] = 0;
            }
            sysError[0] = 0.;
    }	   
  }//doScreenStatistics



/**
 * <p>
 * It does test screen statistics
 * </p>
 * @param tStamp is the system time.
 * @param globalResults is the sum of all statistics done in the system.
 * @param sysError is the error sum of XCS.
 * @param numProblems is the number of examples done in the run.
 */

    public void doTestScreenStatistics(int tStamp, int []globalResults, double []sysError, int numProblems){
	System.out.println ("\nGlobal results of test execution: ");
	System.out.println ("Correct: "+globalResults[0]+" Incorrect: "+globalResults[1]+" not Covered: "+globalResults[2]);
	System.out.println (tStamp+"  "+((double)globalResults[0]/(double)numProblems)+"  "+((double)sysError[0]/(double)numProblems)+"  "+pop.getMacroClSum()+"  "+pop.getMicroClSum()+" ");
    }


/**
 * <p>
 * Applies the reduction choosen by the user. First of all, the population
 * is read from a file. Then, it distinguishes between the Wilson reduction 
 * and the two versions of Dixon's alternative reduction algorithm.
 * </p>
 */

  public void startReductXCS (){
    long iReductionTime = System.currentTimeMillis();
    System.out.println ("\n================== A REDUCTION RUN STARTS ==================");
    System.out.println ("A population of: "+(Config.numberOfActions + Config.popSize)+" macro classifiers is created.");
    pop  = new Population(Config.numberOfActions + Config.popSize);
    System.out.println ("We read the file: "+Config.populationFile);
    pop.readPopulationFromFile(Config.populationFile);

    if (Config.doReduction){
        Population Mcomp = reduction.makeReduction(pop,reductionEnv);

        System.out.println ("The reducted population is set down to the file: "+ Config.reductedRulesFile);
        Mcomp.printPopulationToFile(Config.reductedRulesFile+".plt");

        //statistics.printPopulation(pop);
        try{
            PrintWriter fPDraw = new PrintWriter(new BufferedWriter(new FileWriter(Config.reductedRulesFile+".drw2")));
            Mcomp.drawPopulationToFile(fPDraw);
            fPDraw.close();
        }catch (Exception ioe){ioe.printStackTrace();}
    }

    tControl.updateReductionTime(iReductionTime);

    try{
        PrintWriter fTime = new PrintWriter(new BufferedWriter(new FileWriter(Config.reductedRulesFile+".tme")));
        tControl.printTimes(fTime);	
        fTime.close();
    }catch (Exception fEx){
            fEx.printStackTrace();
    }
  } // end startReductXCS


  

/**
 * <p>
 * Runs a reduction of the population and returns the reduced population.
 * </p>
 * @param reductionEnv is the environment that has to be used to do the reduction
 * @return a Population with the reduced population.
 */
   
  public Population doOneReductionExecution (Environment reductionEnv){
    return reduction.makeReduction(pop,reductionEnv);	
  }// end doOneReductionExecution



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
  public void finishXCS(long iTime) {            	
    tControl.updateTotalTime (System.currentTimeMillis() - iTime);
    tControl.printTimes();
    if (statistics != null){
        statistics.makeTimeStatistics(tControl);
        statistics.closeFiles();
    }
  } // end finishXCS        



/**
 * Does print the population and stops the execution.
 * @param r is the bufferedReader where to read.
 */
   public void print (BufferedReader r) {
    try{
        pop.print();	
        System.out.println ("Print something to return to the menu");
        r.readLine();
    }catch (Exception e){
        e.printStackTrace();
    }
   } //end print
     
   
} // end XCS
                                       


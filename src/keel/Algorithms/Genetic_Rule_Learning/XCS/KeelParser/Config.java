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

package keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser;
import java.lang.*;
import java.util.*;
import java.io.*;

/**
 * <p>
 * This class contains all the configuration parameters for the XCS.
 * </p>
 
 */
public class Config {

  ///////////////////////////////////////
  // attributes


/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// PROBLEM PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/**
 * <p>
 * It represents the type of problem to be executed. It can be: FP, MP, PAR, POS, DEC
 * </p>
 */
    public static String typeOfProblem = "fp";

/**
 * <p>
 * It indicates if a train has to be made
 * </p>
 */
    public static boolean doTrain = false;

/**
 * <p>
 * Represents the number of experiments that have to be made.
 * </p>
 */
    public static int numberOfExplores = 100000; 

/**
 * <p>
 * Indicates the number of explore experiments that have to be 
 * made before doing an exploit experiment
 * </p>
 */
    public static int exploresBetweenExploits = 10;

/**
 * <p>
 * It's the seed of the run
 * </p>
 */
    public static double seed = 1.0;

/**
 * <p>
 * It represents the number of explores iterations that have to be 
 * made to do a exploit
 *</p>
 */ 
    public static String XCSRun = "train"; 


/**
 * <p>
 * It the BD of train examples
 * </p>
 */
    public static String trainFile = null;

/**
 * <p>
 * It the BD of test examples
 * </p>
 */
    public static String testFile = null;


/**
 * <p>
 * If not null, it represents the name of the file where the 
 * population has been writen
 * </p>
 */
    public static String populationFile = null;



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// XCS PARAMETERS FILE /////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


/**
 * <p>
 * Represents the maximum number of microclassifiers in the population.
 * </p>
 */
    public static int popSize = 1000; 

/**
 * <p>
 * Parameter of acurcy function (the fall of rate in the fitness
 * evaluation).
 * </p>
 * 
 */
    public static double alpha = 0.1; 

/**
 * <p>
 * Learning rate for prediction, prediction error, fitness, and action set
 * estimation updates
 * </p>
 * 
 */
    public static double beta = 0.2; 

/**
 * <p>
 * Discount factor (used in multiple step problems)
 * </p>
 * 
 */
    public static double gamma = 0.71; 

/**
 * <p>
 * Value of the fraction used in the second deletion method.
 * </p>
 * 
 */
    public static double delta = 0.1; 

/**
 * <p>
 * Exponent in the power function  for the fitness evaluation
 * </p>
 * 
 */
    public static double nu = 5.0; 

/**
 * <p>
 * Number of classifiers that has to be covered when creating the
 * prediction array.
 * </p>
 * 
 */
    public static int theta_mna; 


/**
 * <p>
 * Is the deletion threshold. If the experience of a classifier is greather
 * than this parameter, its fitness may be considered in its probability of
 * deletion.
 * </p>
 * 
 */
    public static double theta_del = 20.0; 


/**
  * <p>
  * Is the subsumption threshold. If the experience of a classifier is less 
  * than that parameter, the classifier is not expert enough to be a subusmer.
  * </p>
  */
    public static double theta_sub = 20.0;




/**
 * <p>
 * Parameter of the accuracy function (Is the error threshold under which
 * the accuracy of the classifier is set to 1.
 * </p>
 * 
 */
    public static double epsilon_0 = 10.0; 



/**
 * <p>
 * It indicates if the subsumption in the action set is required.
 * </p>
 */
    public static boolean doASSubsumption = false; 


/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// REDUCTION IN INITIALIZATIONS /////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * <p>
 * The factor by which the prediction error is reduced when a new
 * classifier is generated in the AG.
 * </p>
 */
    public static double predictionErrorReduction = 0.25; 

/**
 * <p>
 * The factor by which the fitness is reduced when a new classifier is
 * generated in the AG.
 * </p>
 */
    public static double fitReduction = 0.1; 

/**
 * <p>
 * Is the initial prediction for the new classifiers.
 * </p>
 */
    public static double initialPrediction = 10.0; 

/**
 * <p>
 * It is the inital fitness for the new classifiers.
 * </p>
 */
    public static double initialFitness = 0.01; 

/**
 * <p>
 * It represents the initial system error for the new classifiers.
 * </p>
 */
    public static double initialPError = 0.0; 



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// GENETIC PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


/**
 * <p>
 * Probability of applying crossover in the GA.
 * </p>
 * 
 */
    public static double pX = 0.8; 

/**
 * <p>
 * Probability of mutate an allele in the offspring.
 * </p>
 * 
 */
    public static double pM = 0.04; 

/**
 * <p>
 * It represents the time from last GA aplication for aplicate again the GA
 * in the action set.
 * </p>
 */
    public static double  theta_GA = 50; 

/**
 * <p>
 * It indicates if the GA subsumption is required.
 * </p>
 */
    public static boolean doGASubsumption = true; 


/**
 * <p>
 * It represents the percentage of population that has to be selected 
 * to make tournament. Its value is in the (0..1] range.
 * selection.
 * </p>
 */
    public static double tournamentSize = 0.4; 



/**
 * <p>
 * Represents the type of mutation. It can take two values: 
 * 1 --> niched mutation, or 2 --> free mutation.
 * </p>
 */
    public static String typeOfMutation="niched"; 
    
/**
 * <p>
 * Represents the type of selection. It can take two values: 
 * 1 --> roulette wheel selection, or 2 --> tournament selection.
 * </p>
 */
    public static String typeOfSelection="RWS"; 

/**
 * <p>
 * Represents the type of crossover. It can take two values: 
 * 1 --> two point crossover, or 2 --> uniform crossover.
 * </p>
 */
    public static String typeOfCrossover="2PT"; 

/**
 * <p>
 * It indicates if the within crossover is permitted in case
 * of using a real representation.
 * </p>
 */
   public static boolean permitWithinCrossover = true;


/**
 * <p>
 * It indicates the range of the uniform distribution to use in the mutation of a real allele.
 * </p>
 */
    public static double m_0 = 0.1; 



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// REPRESENTATION PARAMETERS ///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * <p>
 * Is the probability of using # in one attribute when covering.
 * </p>
 * 
 */
    public static double pDontCare = 0.33; 


/**
 * <p>
 * Represents the don't care symbol for problem that use a 
 * character representation or a mixed representation.
 * </p>
 * 
 */
    public static char dontCareSymbol = '#'; 


/**
 * <p>
 * Represents the type of the representation. If it's true the problems
 * uses a ternary representation, and if it's false the problem uses a
 * mixed or a real representation.
 * </p>
 */
    public static boolean ternaryRep = true; 



/**
 * <p>
 * Represents the type of each attribute of a classifier. It is used by the
 * mixed representation, because it has to know the type of each attribute.
 * It can take the "real" or "character" values. 
 * </p>
 */
    public static String[] typeOfAttributes; 

/**
 * <p>
 * Represents the lenght of the classifier.
 * </p>
 */
    public static int clLength; 




/**
 * <p>
 * Represents the number of different characters that can take a character representation.
 * </p>
 */
    public static int numberOfCharacters = 3; 
    
    
/**
 * <p>
 * It contains all the characters that can take a problem with character representation.
 * </p>
 */
    public static char [] charVector; 
    

/**
 * <p>
 * Represents the number of actions that a classifier can take. 
 * </p>
 */
    public static int numberOfActions = 2; 

/**
  * <p>
  * Represents the interval of random numbers that will be 
  * generated to be added in the real mutation.
  * </p>
  */

    public static double r_0 = 0.5;    
    
    

/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// SPECIFY PARAMETERS ///////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
  * <p>
  * It indicates if the specify operator has to be applied.
  * </p>
  */
    public static boolean doSpecify = false;
    
/**
  * <p>
  * Is the experience average of classifiers in the action set to
  * apply the specify operator.
  * </p>
  */
    public static double Nspecify = 10.0;
    
/**
  * <p>
  * Is the probability of don't care allele to be changed for the
  * environmental value.
  * </p>
  */    
    public static double Pspecify = 0.5;

/**
  * <p>
  * Represents the interval of random numbers that will be 
  * generated to be added in the specify operator.
  * </p>
  */
    public static double l_0 = 0.1;


/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// STATISTICS PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * <p>
 * It indicates if the user wants to get some statistics in a file. 
 * </p>
 */
    public static boolean doStatistics = true;

/**
 * <p>
 * It the size of the window for the incremental statistics. It means that,
 * every statisticWindowSize, a statistic of the population will be set down
 * in the incremental statistic file. 
 * </p>
 */
    public static int statisticWindowSize = 100;

/**
 * <p>
 * It is the name of the file where the statistics will be written in.
 * </p>
 */
    public static String statisticFileOutName = "outFile.txt";
    
    
/**
 * <p>
 * It's set to true if the optimal population has to be read from a file.
 * </p>
 */    
    public static boolean getOptimalPopulation = false;
    
   
/**
 * <p>
 * It's the file name that contains the optimal population.
 * </p>
 */    
    public static String optimalPopulationFile = null;
   

/**
 * <p>
 * It represents the precision wanted by the user to draw an interval.
 * So, the interval will be split in "realDrawnPrecision" pieces to 
 * be drawn in a file.
 * </p>
 */
   
   public static int realDrawnPrecision = 10;



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// TEST PARAMETERS /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * <p>
 * This parameter indicates if test has to be made between 
 * the train execution.
 * </p>
 */
   public static boolean doTest = true;
   

/**
 * <p>
 * Indicates the number of train executions that has to be made
 * to do a test execution.
 * </p>
 */
   public static int testWindow = 1000000;


/**
 * This parameter indicates if the test execution must be a 
 * sequential execution (if the enviornment is a file environment),
 * or it mustn't. A sequential execution is that all the examples
 * are got sequentially begining at the first example.
 */
   public static boolean sequentialTest = true;
  
     

/**
 * <p>
 * Indicates the number of iterations that has to be made in a
 * test execution.
 * </p>
 */
   public static int numberOfTestExamples = 100;



/////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////// REDUCT PARAMETERS /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

   
/**
 * <p>
 * Indicates if reduction  has to be made
 * </p>
 */
   public static boolean doReduction = false;

/**
 * <p>
 * Indicates the type of reduction to be made. It can take the 
 * following values: SD (Strong Dixon), WD (Weak Dixon), nW (Wilson
 * with numerosity) and eW (Wilson with experience)
 * </p>
 */
   public static String typeOfReduction = "SD";
   
   
   
/**
 * <p>
 * It indicates the iteration where the reduction specified has
 * to be applied for the first time. 
 * </p>
 */   
   public static int initReductionIteration = 0;
   
/**
 * <p>
 * It indicates the interval where the execution window will
 * be applied. 
 * </p>
 */
   public static int reductWindow = 1;   
   
/**
 * <p>
 * Is the number of reduct iterations that have to be made in a 
 * reduction. In case of using a file environment, the number of
 * iterations will be set at the number of problems of the environment.
 * </p>
 */
   public static int numberOfReductExamples = 1000;
   
      
/**
 * <p>
 * It's the name where the reductes rules have to be written.  
 * </p>
 */
   public static String reductedRulesFile = "";
   
   
   
/**
 * <p>
 * It's the reduction applied to the max reward to decide if a
 * classifier is accurate enough to participate in the reduction.
 * </p>
 */
   public static double Preduct = 0.999;


/**
  * <p>
  * Is the reduction threshold. If the experience of a classifier is less 
  * than that parameter, the classifier is not expert enough to be a in the
  * reduction set.
  * </p>
  */
    public static double theta_reduct = 10.0;


/**
 * <p>
 * Parameter of the Dixon Reduction. It is the error threshold necessary by
 * a classifier to be accurated enough to be in the reduction set. 
 * </p>
 */
    public static double epsilon_reduct = 0.1; 




 
/**
 * <p>
 * It stores the under and upper bounds of each parameter
 * in the integer representation of an attribute.
 * </p>
 */
    //public static int []intVector;
 

/**
 * It indicates the level of debug (the quantity of message that will appear in the estandard output
*/
    public static int debugLevel = 0;


    public static double [][]attBounds;
    public static Vector []enumConv;
    public static Vector classConv;

/** 
 * Variables to store extra information about attributes
 */
   public static String relationName = null;
   public static Vector attNames;
   public static String className;
   public static String inputsLine;
   public static String outputsLine;
 
/**
 * The following parameters are to save the BD examples maximum and minimum values. It has been introduced
 * before the second phase. They store the maximum and the minimum values of the attributes.
 */
   public static double minBDAttributeValue[];
   public static double maxBDAttributeValue[];
 

/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////// OUTPUT FILES ///////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

// Be careful in maintaining the order of the files in the configuration file.
   public static String fOTrainFileName;
   public static String fOTestFileName;
  
   public static String fPopFileName;
   public static String fPopNormFileName;
   
   public static String fTrainFileName;
   public static String fTestFileName;
   
   public static String fIncFileName;
   public static String fDrawFileName;
   public static String fTimeFileName;
   
   

/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// XCSI PARAMETERS //////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

    private static Random aleat = null;

  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It's de defalut constructor of the class.
 * </p>
 */
    public  Config() {        
        aleat = new Random();
    } // end Config        


   public static void setSeed(long seed){
	if (aleat == null) aleat = new Random();
	aleat.setSeed(seed);
   }


/**
 * Returns a number between [0, 1), so, 0 inclusive and 1 exclusive.
 * @return a double with the random double number.
 */
   public static double rand(){
    	return aleat.nextDouble();	
   }


/**
 * It does print all the parameters value to check their correct initialization
 */
    public static void printContents(){
    	
    	
    	System.out.println ("\n--> PROBLEM PARAMETERS");
    	
    	System.out.println("typeOfProblem = "+typeOfProblem);
    	System.out.println("numberOfExplores = "+numberOfExplores);
	System.out.println("seed = "+seed);
	System.out.println(" exploresBetweenExploits = "+exploresBetweenExploits);
	System.out.println(" XCSRun = "+ XCSRun);
	System.out.println(" trainFile = "+trainFile);
	System.out.println(" testFile = "+testFile);
	System.out.println(" populationFile = "+populationFile);
	
    	
    	System.out.println ("\n--> XCS PARAMETERS CONFIGURATION");
    	
    	System.out.println("popSize = "+popSize);	
    	System.out.println("alpha = "+alpha);
    	System.out.println("beta = "+beta);
    	System.out.println("gamma = "+gamma);
    	System.out.println("delta = "+delta);
    	System.out.println("nu = "+nu);
    	System.out.println("theta_mna = "+theta_mna);
    	System.out.println("theta_del = "+theta_del);
    	System.out.println("theta_sub = "+theta_sub);
    	System.out.println("epsilon_0 = "+epsilon_0);
    	System.out.println("doASSubsumption = "+doASSubsumption);
    	
   
   	System.out.println ("\n--> REDUCTION IN INITIALIZATION");

	System.out.println("predictionErrorReduction = "+predictionErrorReduction);
	System.out.println("fitReduction = "+fitReduction);
	System.out.println("initialPrediction = "+initialPrediction);
	System.out.println("initialFitness = "+initialFitness);
	System.out.println("initialPError = "+initialPError);
    	

	System.out.println ("\n--> AG PARAMETERS");
    	
    	System.out.println("pX = "+pX);
    	System.out.println("pM = "+pM);
    	System.out.println("doGASubsumption = "+doGASubsumption);
    	System.out.println("tournamentSize = "+tournamentSize);
    	System.out.println("typeOfSelection = "+typeOfSelection);
	System.out.println("typeOfCrossover = "+typeOfCrossover);    
	System.out.println("typeOfMutation = "+typeOfMutation);
    	System.out.println("m_0 = "+m_0);
	System.out.println("permitWithinCrossover = "+permitWithinCrossover);
   

    	
    	System.out.println ("\n--> REPRESENTATION PARAMETERS");
    	
    	System.out.println("pDontCare = "+pDontCare);	
    	System.out.println("dontCareSymbol = "+dontCareSymbol);
    	System.out.println("ternaryRep = "+ternaryRep);
    	if (typeOfAttributes != null){
    		for (int i=0; i<typeOfAttributes.length; i++){
		    	System.out.println("typeOfAttributes["+i+"] = "+typeOfAttributes[i]);
		}
    	}
    	System.out.println("clLength = "+clLength);
    	System.out.println("numberOfCharacters = "+numberOfCharacters);
    	if (charVector != null){
   		for (int i=0; i<charVector.length; i++){
   			System.out.println("charVector["+i+"] = "+charVector[i]);
   		}
    	}
    	System.out.println ("NumberOfActions = "+numberOfActions);
    
    	System.out.println ("\nVector dels limits integer: 2");
    	/*if (intVector != null){
   		for (int i=0; i<intVector.length; i++){
   			System.out.println("intVector["+i+"] = "+intVector[i]);
   		}
    	}*/
    	System.out.println ("r_0 = "+r_0);
   
 
 	System.out.println ("\n--> TEST PARAMETERS");
 	System.out.println ("doTest = "+doTest);
 	System.out.println ("testWindow = "+testWindow);
 	System.out.println ("sequentialTest = "+sequentialTest);
 	System.out.println ("numberOfTestExamples = "+numberOfTestExamples);
 
    	System.out.println ("\n--> SPECIFY PARAMETERS");
   	
   	System.out.println ("doSpecify = "+doSpecify); 
   	System.out.println ("Nspecify = "+Nspecify); 
   	System.out.println ("Pspecify = "+Pspecify); 
   	System.out.println ("l_0 (specify desviation)= "+l_0); 
    
    
    	System.out.println ("\n--> STATISTICS PARAMETERS");
    	
    	System.out.println ("doStatistics = "+doStatistics);
    	System.out.println ("statisticFileOutName = "+statisticFileOutName);
    	System.out.println ("statisticWindowSize = "+statisticWindowSize);
   	System.out.println ("getOptimalPopulation = "+getOptimalPopulation);
   	System.out.println ("optimalPopulationFile = "+optimalPopulationFile);
    	System.out.println ("doTest = "+doTest);
    	System.out.println ("testWindow = "+testWindow);
    	System.out.println ("numberOfTestExamples = "+numberOfTestExamples);
    	System.out.println ("testFile = "+testFile);
    
    
    	System.out.println ("\n--> REDUCTION PARAMETERS");
    	
    	System.out.println ("doReduction = "+doReduction);
    	System.out.println ("typeOfReduction = "+typeOfReduction);
	System.out.println ("reductedRulesFile = "+reductedRulesFile);
	System.out.println ("epsilon_reduct = "+epsilon_reduct);
	System.out.println ("theta_reduct = "+theta_reduct);
	System.out.println ("Preduct = "+Preduct);
    }
    
    
    public static void write (String s){
    	if (debugLevel == 10){
    		System.out.print(s);
    	}
    	
    }
    

} // end ConfigParameters





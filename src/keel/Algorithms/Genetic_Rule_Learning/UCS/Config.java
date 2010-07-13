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
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.util.*;
import java.io.*;


public class Config {
/**
 * <p>
 * This class contains all the configuration parameters for the XCS.
 * </p>
 */

/**
 * It represents the unkown value in a training instance
 */
	public final static double unknownValue = -1;

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// PROBLEM PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/**
 * It represents the type of problem to be executed. It can be: FP, MP, PAR, POS, DEC
 */
    public static String typeOfProblem = "fp";

/**
 * It indicates if a train has to be made
 */
    public static boolean doTrain = false;

/**
 * Represents the number of experiments that have to be made.
 */
    public static int numberOfExplores = 100000; 

/**
 * Indicates the number of explore experiments that have to be 
 * made before doing an exploit experiment
 */
    public static int exploresBetweenExploits = 10;

/**
 * It's the seed of the run
 */
    public static double seed = 1.0;

/**
 * It represents the number of explores iterations that have to be 
 * made to do a exploit
 */ 
    public static String UCSRun = "train"; 


/**
 * It the BD of train examples
 */
    public static String trainFile = null;

/**
 * It the BD of test examples
 */
    public static String testFile = null;


/**
 * If not null, it represents the name of the file where the 
 * population has been writen
 */
    public static String populationFile = null;



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// XCS PARAMETERS FILE /////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


/**
 * Represents the maximum number of microclassifiers in the population.
 */
    public static int popSize = 1000; 

/**
 * Parameter of accuracy function (the fall of rate in the fitness evaluation).
 */
    public static double alpha = 0.1; 

/**
 * Learning rate for prediction, prediction error, fitness, and action set
 * estimation updates
 * 
 */
    public static double beta = 0.2; 

/**
 * Value of the fraction used in the second deletion method.
 * 
 */
    public static double delta = 0.1; 

/**
 * Exponent in the power function  for the fitness evaluation
 */
    public static double nu = 10.0; 


/**
 * Is the deletion threshold. If the experience of a classifier is greather
 * than this parameter, its fitness may be considered in its probability of
 * deletion.
 */
    public static double theta_del = 20.0; 

/**
  * Is the subsumption threshold. If the experience of a classifier is less 
  * than that parameter, the classifier is not expert enough to be a subusmer.
  */
    public static double theta_sub = 20.0;


/**
 * Parameter of the accuracy function (Is the accuracy threshold beyond which
 * the accuracy of the classifier is set to 1.
 */
    public static double acc_0 = 0.99; 



/////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// REDUCTION IN INITIALIZATIONS /////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


/**
 * The factor by which the fitness is reduced when a new classifier is
 * generated in the AG.
 */
    public static double fitReduction = 0.1; 

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// GENETIC PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


/**
 * Probability of applying crossover in the GA.
 */
    public static double pX = 0.8; 

/**
 * Probability of mutate an allele in the offspring.
 */
    public static double pM = 0.04; 

/**
 * It represents the time from last GA aplication for aplicate again the GA
 * in the action set.
 */
    public static double  theta_GA = 50; 

/**
 * It indicates if the GA subsumption is required.
 */
    public static boolean doGASubsumption = true; 


/**
 * It represents the percentage of population that has to be selected 
 * to make tournament. Its value is in the (0..1] range.
 * selection.
 */
    public static double tournamentSize = 0.4; 



/**
 * Represents the type of mutation. It can take two values: 
 * 1 --> niched mutation, or 2 --> free mutation.
 */
    public static String typeOfMutation="niched"; 
    
/**
 * Represents the type of selection. It can take two values: 
 * 1 --> roulette wheel selection, or 2 --> tournament selection.
 */
    public static String typeOfSelection="RWS"; 

/**
 * Represents the type of crossover. It can take two values: 
 * 1 --> two point crossover, or 2 --> uniform crossover.
 */
    public static String typeOfCrossover="2PT"; 


/**
 * It indicates the range of the uniform distribution to use in the mutation of a real allele.
 */
    public static double m_0 = 0.1; 



/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// REPRESENTATION PARAMETERS ///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * Is the probability of using # in one attribute when covering.
 */
    public static double pDontCare = 0.33; 


/**
 * Represents the don't care symbol for problem that use a 
 * character representation or a mixed representation.
 */
    public static char dontCareSymbol = '#'; 


/**
 * Represents the type of the representation. If it's true the problems
 * uses a ternary representation, and if it's false the problem uses a
 * mixed or a real representation.
 */
    public static boolean ternaryRep = true; 



/**
 * Represents the type of each attribute of a classifier. It is used by the
 * mixed representation, because it has to know the type of each attribute.
 * It can take the "real" or "character" values. 
 */
    public static String[] typeOfAttributes; 

/**
 * Represents the lenght of the classifier.
 */
    public static int clLength = 37; 




/**
 * Represents the number of different characters that can take a character representation.
 */
    public static int numberOfCharacters = 3; 
    
    
/**
 * It contains all the characters that can take a problem with character representation.
 */
    public static char [] charVector; 
    

/**
 * Represents the number of actions that a classifier can take. 
 */
    public static int numberOfActions = 2; 

/**
  * Represents the interval of random numbers that will be 
  * generated to be added in the real mutation.
  */

    public static double r_0 = 0.5;    
    
    


/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////// STATISTICS PARAMETERS //////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * It indicates if the user wants to get some statistics in a file. 
 */
    public static boolean doStatistics = true;

/**
 * It the size of the window for the incremental statistics. It means that,
 * every statisticWindowSize, a statistic of the population will be set down
 * in the incremental statistic file. 
 */
    public static int statisticWindowSize = 1000;

/**
 * It is the name of the file where the statistics will be written in.
 */
    public static String statisticFileOutName1 = "outFile1.txt";
    public static String statisticFileOutName2 = "outFile2.txt";
    public static String statisticFileOutName3 = "outFile3.txt";
    


/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// TEST PARAMETERS /////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

/**
 * This parameter indicates if test has to be made between 
 * the train execution.
 */
   public static boolean doTest = true;
   

/**
 * Indicates the number of train executions that has to be made
 * to do a test execution.
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
 * Indicates the number of iterations that has to be made in a
 * test execution.
 */
   public static int numberOfTestExamples = 100;




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
   public static int majorityClass = 0;
   public static boolean defaultRule = true;
 
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
   
   


    private static Random aleat = null;



/**
 * It's de defalut constructor of the class.
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
	System.out.println(" UCSRun = "+ UCSRun);
	System.out.println(" trainFile = "+trainFile);
	System.out.println(" testFile = "+testFile);
	System.out.println(" populationFile = "+populationFile);
	
    	
	System.out.println ("\n--> XCS PARAMETERS CONFIGURATION");
	
	System.out.println("popSize = "+popSize);	
	System.out.println("alpha = "+alpha);
	System.out.println("beta = "+beta);
	System.out.println("delta = "+delta);
	System.out.println("nu = "+nu);
	System.out.println("theta_del = "+theta_del);
	System.out.println("theta_sub = "+theta_sub);
	System.out.println("acc_0 = "+acc_0);
	
   
   	System.out.println ("\n--> REDUCTION IN INITIALIZATION");

	System.out.println("fitReduction = "+fitReduction);
		

	System.out.println ("\n--> AG PARAMETERS");
    	
    System.out.println("pX = "+pX);
    System.out.println("pM = "+pM);
    System.out.println("doGASubsumption = "+doGASubsumption);
    System.out.println("tournamentSize = "+tournamentSize);
    System.out.println("typeOfSelection = "+typeOfSelection);
	System.out.println("typeOfCrossover = "+typeOfCrossover);    
	System.out.println("typeOfMutation = "+typeOfMutation);
    System.out.println("m_0 = "+m_0);
	
	

    	
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
    System.out.println ("r_0 = "+r_0);
   
 
 	System.out.println ("\n--> TEST PARAMETERS");
 	System.out.println ("doTest = "+doTest);
 	System.out.println ("testWindow = "+testWindow);
 	System.out.println ("sequentialTest = "+sequentialTest);
 	System.out.println ("numberOfTestExamples = "+numberOfTestExamples);
 
    System.out.println ("\n--> SPECIFY PARAMETERS");
   	
    
    System.out.println ("\n--> STATISTICS PARAMETERS");
    	
    System.out.println ("doStatistics = "+doStatistics);
    System.out.println ("statisticFileOutName1 = "+statisticFileOutName1);
    System.out.println ("statisticFileOutName2 = "+statisticFileOutName2);
    System.out.println ("statisticFileOutName3 = "+statisticFileOutName3);
    System.out.println ("statisticWindowSize = "+statisticWindowSize);
   	System.out.println ("doTest = "+doTest);
    System.out.println ("testWindow = "+testWindow);
    System.out.println ("numberOfTestExamples = "+numberOfTestExamples);
    System.out.println ("testFile = "+testFile);
  }//end printContents
    
    
  public static void write (String s){
    	if (debugLevel == 10){
    		System.out.print(s);
    	}  	
  }//end write
    



  public static void doParse ( String configFile ){
  	String line = null;
  	String varName;
  	String aux;
  	try{
  		BufferedReader fin = new BufferedReader ( new FileReader(configFile) );
  		line = fin.readLine();
  		while ( line != null ){
			if ( line.length() == 0) line = fin.readLine();
  			StringTokenizer st = new StringTokenizer ( line );
  			varName = st.nextToken();
  			st.nextToken(); //Disregarding '='
  			if ( varName.equalsIgnoreCase("algorithm") ){
  					
  			}else if ( varName.equalsIgnoreCase("inputdata") ){
  				st.nextToken(); //Disregarding the first file
  				
  				aux = st.nextToken();
  				Config.trainFile = aux.substring( 1,aux.length() - 1 );
  				
  				aux = st.nextToken();
  				Config.testFile  = aux.substring( 1,aux.length() - 1 );
  				
  			}else if ( varName.equalsIgnoreCase("outputData") ){
				int i=0;
				while ( st.hasMoreTokens() ){
					aux = st.nextToken();
					setOutputFile ( i, aux.substring (1, aux.length() - 1) );
					i++;
				}
				
  			}else if ( varName.equalsIgnoreCase("seed") ){
  				Config.seed = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("numberOfExplores") ){
  				Config.numberOfExplores = Integer.parseInt ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("popSize") ){
  				Config.popSize = Integer.parseInt ( st.nextToken() ); 
  			}else if ( varName.equalsIgnoreCase("alpha") ){
  				Config.alpha = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("beta") ){
  				Config.beta = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("delta") ){
  				Config.delta = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("nu") ){
  				Config.nu = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("theta_del") ){
  				Config.theta_del = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("theta_sub") ){
  				Config.theta_sub = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("acc_0") ){
  				Config.acc_0 = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("fitReduction") ){
  				Config.fitReduction = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("pX") ){
  				Config.pX = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("pM") ){
  				Config.pM = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("theta_GA") ){
  				Config.theta_GA = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("doGASubsumption") ){
  				Config.doGASubsumption = Boolean.parseBoolean( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("typeOfSelection") ){
  				Config.typeOfSelection = st.nextToken();
  			}else if ( varName.equalsIgnoreCase("tournamentSize") ){
  				Config.tournamentSize = Double.parseDouble ( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("typeOfCrossover") ){
				Config.typeOfCrossover = st.nextToken();
  			}else if ( varName.equalsIgnoreCase("pDontCare") ){
  				Config.pDontCare = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("r_0") ){
  				Config.r_0 = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("m_0") ){
  				Config.m_0 = Double.parseDouble( st.nextToken() );
  			}else if ( varName.equalsIgnoreCase("typeOfMutation") ){
  				Config.typeOfMutation = st.nextToken();
  			}else{
  				System.out.println ("CONFIGURATION PARAMETER OMITTED since not recognized: "+line);
  			}  			

			line = fin.readLine();
  		}
	}catch( Exception e ){
		System.err.println ("Error reading configuration parameters.");
		System.err.println ("Parameter not recognized: "+line);
		e.printStackTrace();	
		System.exit(-1);
	}
  	
  }//end doParse
  
  static void setOutputFile (int num, String fName){
	switch (num){
		case 0:
				System.out.println ("Config.statisticFileOutName1 = "+fName);
				Config.statisticFileOutName1 = fName;
				Config.fOTrainFileName = fName;
				break;
		case 1:
				System.out.println ("Config.statisticFileOutName2 = "+fName);
				Config.statisticFileOutName2 = fName;
				Config.fOTestFileName = fName;
				break;
		case 2:
				System.out.println ("Config.statisticFileOutName3 = "+fName);
				Config.statisticFileOutName3 = fName;
				Config.fTimeFileName = fName;
				break;

		case 3:
				Config.fPopFileName = fName;
				break;
		case 4:
				Config.fPopNormFileName = fName;
				break;			
		case 5:
				Config.fTrainFileName = fName;
				break;
		case 6:
				Config.fTestFileName = fName;
				break;		
		case 7:
				Config.fIncFileName = fName;
				break;
		case 8:
				Config.fDrawFileName = fName;
				break;
		
	}
  }//end setOutputFile

} // end ConfigParameters





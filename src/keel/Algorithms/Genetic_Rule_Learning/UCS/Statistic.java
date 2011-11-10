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
 * @version 1.3
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.util.*;
import java.lang.*;
import java.io.*;


public class Statistic {
/**
 * <p>
 * This class is used to show and configurate all the possible statistics.
 * In the normal mode, it will generate four statistic output files: The
 * INC file that contains the incremental results of the execution. The TRN
 * and the TST file, that have the final result statistic of the a train o
 * a test execution. And finally, it generates the PLT (population) file,
 * that contains all the resulting classifiers of the execution. The
 * statistics that will be written on that files are: iterations, number of
 * good classifications, number of bad classifications, number of not
 * covered classifications, total number of examples, percentage of correct
 * classification of the covered examples, percentage of correct
 * classifications of the whole number of examples, number of macro
 * classifiers of the population, percentage of the optimal population that
 * has been reached already, the percentage of generalitzation and the
 * number of micro classifiers of the population.
 * </p>
 */


/**
 * Represents the output file where the incremental statistics are written.
 */
    private PrintWriter fInc = null; 

/**
 * Represents the output file where the train statistics are written.
 */
    private PrintWriter fTrain = null; 

/**
 * Represents the output file where the test statistics are written.
 */
    private PrintWriter fTest = null; 

/**
 * Represents the output file where the population will be written 
 * at the end of the execution.
 */
    private PrintWriter fPop = null; 
 
/**
 * Represents the output file where the population will be drawn
 * at the end of the execution.
 */
    private PrintWriter fPDraw = null; 
    
/**
 * Represents the output file where the time cost of every type
 * (reductions, train and test) will be written.
 */
    private PrintWriter fTime = null; 

/**
 * Output file where expected vs real output statistics are written in the train
 */ 
	private PrintWriter fOTrain = null;

/**
 * Output file where expected vs real output statistics are written in the test
 */ 
	private PrintWriter fOTest = null;



/**
 * Creates an Statistic object. It takes the name of the ouput files
 * from the configuration file and opens them. If this parameter does not 
 * exist in the configuration file, it is not possible to perform statistics.
 */
  public  Statistic( ) {
	//Adding the seed number to the statistic file name
	//Config.statisticFileOutName3 = Config.statisticFileOutName3 + ".S-" + (new Integer( (int)Config.seed )).toString();
    try{
		
		//System.out.println ("      > Opening file for incremental training statistics: "+(Config.statisticFileOutName3+".inc"));
		//	fInc   = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3+".inc")));
		
		//System.out.println ("      > Opening file for population: "+(Config.statisticFileOutName3+".plt"));
		//	fPop   = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3+".plt")));
		System.out.println ("      > Opening file for population: "+Config.statisticFileOutName3);
			fPop   = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3)));
	
		if ( Config.typeOfProblem.toLowerCase().equals("fp") ){	
			System.out.println ("      > Opening file for correct class vs. predicted class for each train instances: "+Config.statisticFileOutName1);
				fOTrain = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName1)));

			System.out.println ("      > Opening file for correct class vs. predicted class for each test instance: "+Config.statisticFileOutName2);
				fOTest = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName2)));
        }

		//System.out.println ("      > Opening file for the train error instance:"+Config.statisticFileOutName3+".tra");
		//	fTrain = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3+".tra")));

		//System.out.println ("      > Opening file for the test error instance:"+Config.statisticFileOutName3+".tst");
		//	fTest = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3+".tst")));

		//In all cases a Time Statistics file will be opened.
		//System.out.println ("      > Opening file for time statistics: "+Config.statisticFileOutName3+".tme");
        //	fTime = new PrintWriter(new BufferedWriter(new FileWriter(Config.statisticFileOutName3+".tme")));

    }catch (Exception e){
        System.err.println ("ERROR IN THE STATISTICS. One or more files of the output statistics cannot be opened.");
        e.printStackTrace();
        System.exit(0);
    }

    if (fInc != null)   fInc.println   ("Iter - #correct - #wrong - #notCovered - #total - correct/covered - correct/total - #macroCl - % of [O] reached - %generalization - # of microcl - System Error");
    if (fTrain != null) fTrain.println ("Iter - #correct - #wrong - #notCovered - #total - correct/covered - correct/total - #macroCl - % of [O] reached - %generalization - # of microcl");
    if (fTest != null)  fTest.println  ("Iter - #correct - #wrong - #notCovered - #total - correct/covered - correct/total - #macroCl - % of [O] reached - %generalization - # of microcl");
    //if (fTrainTest != null) fTrainTest.println  ("Iter - #correct - #wrong - #notCovered - #total - correct/covered - correct/total - #macroCl - % of [O] reached - %generalization - # of microcl");
    if (fOTrain != null)initOutputFile ( fOTrain );
    if (fOTest != null) initOutputFile ( fOTest );
  } // end Statistic        
  
  

/**
 * <p>
 * It makes the incremental statistics. It will write incrementally 
 * the statistics mentioned above.
 * </p>
 * @param pop is the current population.
 * @param iteration is the current iteration of the problem.
 * @param executionResults is a vector that contains the results of
 * the window execution. In the first position it contains the number
 * of examples that have been correctly classified. In the second it
 * contains the number of failed classifications, and in the third one
 * the number of not covered classifications.
 */
  public void makeIncStatistics( Population pop, int iteration, int []executionResults ) {        
    if (Config.doStatistics && fInc != null){
        fInc.print (iteration);
        fInc.print ("\t "+executionResults[0]);
        fInc.print ("\t "+executionResults[1]);
        fInc.print ("\t "+executionResults[2]);
        int total = executionResults[0] + executionResults[1] + executionResults[2];
        fInc.print ("\t "+total);
        fInc.print ("\t "+ (float)((double)executionResults[0] / (double) (executionResults[0]+executionResults[1])));
        fInc.print ("\t "+ (float)((double)executionResults[0] / (double) total));
        fInc.print ("\t "+ pop.getMacroClSum()); 
        fInc.print ("\t "+ pop.getGeneralityAverage());
        fInc.print ("\t "+ pop.getMicroClSum());
		fInc.print ("\n");
    }
  } // end makeIncStatistics        



/**
 * <p>
 * It makes the train statistics. It will write on a train statistics file the
 * statistics mentioned above.
 * </p>
 * @param pop is the current population.
 * @param iteration is the current iteration of the problem 
 * @param executionResults is a vector that contains the results of
 * the train execution. In the first position it contains the number
 * of examples that have been correctly classified. In the second it
 * contains the number of failed classifications, and in the third one
 * the number of not covered classifications.
 */
  public void makeTrainStatistics(Population pop, int iteration, int []executionResults) {        
    if (Config.doStatistics && fTrain != null){
        fTrain.print (iteration);
        fTrain.print ("\t "+executionResults[0]);
        fTrain.print ("\t "+executionResults[1]);
        fTrain.print ("\t "+executionResults[2]);
        int total = executionResults[0] + executionResults[1] + executionResults[2];
        fTrain.print ("\t "+total);
        fTrain.print ("\t "+ (float)((double)executionResults[0] / (double) (executionResults[0]+executionResults[1])));
        fTrain.print ("\t "+ (float)((double)executionResults[0] / (double) total));
        fTrain.print ("\t "+ pop.getMacroClSum());  
        fTrain.print ("\t "+ pop.getGeneralityAverage());
        fTrain.println ("\t "+ pop.getMicroClSum());
		fTrain.print ("\n");
    }
  } // end makeTrainStatistics        



/**
 * <p>
 * It makes the test statistics. 
 * </p>
 * @param pop is the current population.
 * @param iteration is the current iteration of the problem 
 * @param executionResults is a vector that contains the results of
 * the test execution. In the first position it contains the number
 * of examples that have been correctly classified. In the second it
 * contains the number of failed classifications, and in the third one
 * the number of not covered classifications.
  */
  public void makeTestStatistics(Population pop, int iteration, int []executionResults, int typeOfTest) {        
    if (Config.doStatistics && typeOfTest == 0 && fTest != null ){
        makeTrainOrTestStatistics( fTest, pop, iteration, executionResults );
    }
	else if ( Config.doStatistics && typeOfTest == 1 && fTrain != null ){
		makeTrainOrTestStatistics( fTrain, pop, iteration, executionResults );
	}
  } // end makeTestStatistics        



/**
 * <p>
 * It makes the train statistics. 
 * </p>
 * @param pop is the current population.
 * @param iteration is the current iteration of the problem 
 * @param executionResults is a vector that contains the results of
 * the test execution. In the first position it contains the number
 * of examples that have been correctly classified. In the second it
 * contains the number of failed classifications, and in the third one
 * the number of not covered classifications.
 * @param f is a PrintWriter of the file where the results have to be
 * writen. 
  */
  public void makeTrainOrTestStatistics(PrintWriter f, Population pop, int iteration, int []executionResults) {        
    f.print (iteration);
    f.print ("\t "+executionResults[0]);
    f.print ("\t "+executionResults[1]);
    f.print ("\t "+executionResults[2]);
    int total = executionResults[0] + executionResults[1] + executionResults[2];
    f.print ("\t "+total);
    f.print ("\t "+ (float)((double)executionResults[0] / (double) (executionResults[0]+executionResults[1])));
    f.print ("\t "+ (float)((double)executionResults[0] / (double) total));
    f.print ("\t "+ pop.getMacroClSum());  
    f.print ("\t "+ pop.getGeneralityAverage());
    f.println ("\t "+ pop.getMicroClSum());
  } // end makeTestStatistics    



/**
 * <p>
 * It makes the time statistics. It will write into a file the time
 * wasted in training and testing the system, and the reduction 
 * algorithms time.
 * </p>
 * <p>
 * @param tControl is a TimeControl object that contains the times
 * wasted in each category
 * </p>
 */
  public void makeTimeStatistics(TimeControl tControl) {        
    if (Config.doStatistics && fTime != null){
        tControl.printTimes(fTime);
    }
  } // end makeTimeStatistics     




/**
 * It writes the population to a file. 
 * @param pop is the current population.
 */
  public void printPopulation(Population pop) {        
    if (Config.doStatistics && fPop != null){
        pop.printPopulationToFile(fPop);
    }
  } //printPopulation




/**
 * It prints the environmental state and the correct associated action.
 * It is only used in test experiments.
 * 
 * @param envState is the environmental state. 
 * @param action is the correct action associated to that state. 
*/

  public void printStateAndClass(double []envState, int action){
  } //end printStateAndClass


/**
 * It prints the environmental state and for those examples with no action.
 * It's used only in test experiments.
 * 
 * @param envState is the environmental state. 
 * @param action is the correct action associated to that state. 
*/

  public void printStateAndClassNoCov(double []envState, int action){
  } //end printStateAndClassNoCov


/**
 * It initializes the output file 
 */
  private void initOutputFile (PrintWriter f){
    String line;
    
    //Printing the relation name
    f.println ("@relation "+Config.relationName);
    
    //Printing the input attribute
    keel.Dataset.Attribute []attrs = keel.Dataset.Attributes.getInputAttributes();
    for (int i=0; i<attrs.length; i++){
        f.println ( attrs[i].toString() );
    }
    
    //Printing the output attribute
    attrs = keel.Dataset.Attributes.getOutputAttributes();
    f.println (attrs[0].toString());
    
  }//end initOutputFile
  
 
/**
 * <p>
 * It writes to the train statistics file the expected out compared with the
 * output predicted by the system.
 * </p>
 * @param expected the expected ouptut
 * @param current the current output (the real one)
 */  
  public void writeExpectedTrainOut(int expected, int current){
    if ( fOTrain != null ){
		if (current == -1){
			fOTrain.print((String)Config.classConv.elementAt(expected)+"  ");
			fOTrain.println("uncovered");
		} else if (Config.classConv != null){
    	    fOTrain.print((String)Config.classConv.elementAt(expected)+"  ");	
    	    fOTrain.println((String)Config.classConv.elementAt(current)+"  ");	
    	}
    	else{
    	    fOTrain.print(expected+"  ");	
    	    fOTrain.println(current+"  ");	
    	}
	}
  }//end writeExpectedTrainOut
  

/**
 * <p>
 * It writes to the test statistics file the expected out compared with the
 * output predicted by the system.
 * </p>
 * @param expected the expected ouptut
 * @param current the current output (the real one)
 */
  public void writeExpectedTestOut(int expected, int current){
	
	if ( fOTest != null ){
		if (current == -1){
			fOTest.print((String)Config.classConv.elementAt(expected)+"  ");
			fOTest.println("uncovered");
		} else if (Config.classConv != null){
    	    fOTest.print((String)Config.classConv.elementAt(expected)+"  ");	
    	    fOTest.println((String)Config.classConv.elementAt(current)+"  ");	
    	}
    	else{
    	    fOTest.print(expected+"  ");	
    	    fOTest.println(current+"  ");	
    	}
	}
  }//end writeExpectedTestOut

  
/**
 * <p>
 * Closes all the opened files. 
 * </p>
 */
  public void closeFiles(){
    System.out.println ("We close all opened files");
    if (Config.doStatistics){
        if (fInc != null) 	fInc.close();
        if (fTrain != null)	fTrain.close();
        if (fTest != null) 	fTest.close();
        if (fPop != null)	fPop.close();
        if (fTime != null)	fTime.close();
        if (fOTrain != null) fOTrain.close();
        if (fOTest != null) fOTest.close();
    }
  } // end closeFiles


} // end Statistic





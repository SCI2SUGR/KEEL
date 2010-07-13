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

import java.util.*;
import java.lang.*;
import java.io.*;


public class Parameters {
/**
 * <p>
 * In this class there are all the classifier parameters of UCS.
 * </p>
 */
  ///////////////////////////////////////
  // attributes


/**
 * Represents the accuracy of the classifier.
 */
    private double accuracy; 
    
/**
 * Reckons the number of correct classifications 
 */
 	private double numCorrectPred;


/**
 * Represents the fitness of the classifier. It contains the total fitness 
 * (of all the microclassifiers).
 */
    private double fitness; 

/**
 * Represents the experience of the classifier
 */
    private int experience; 

/**
 * Represents the action set size estimate.
 */
    private double correctSetSize; 

/**
 * Represents the time of the classifier.
 * 
 */
    private int timeOfCl; 

/**
 * Represents the number of microclassifiers this macroclassifier contains.
 */
    private int numerosity; 

/**
 * Represents the generality of the classifier. 
 */
    private double generality;





/**
 * Constructs a Parameters Object.
 */
    public  Parameters() {        
    } // end Parameters        

  
/**
 * <p>
 * It creates an instance of Parameters. It is used in covering
 * </p>
 * @param tStamp is the current time of the system.
 * @param size is the action set size
 * @param gen is the generality of the classifier.
 */
    
  public Parameters(int tStamp, double size, double gen){
    accuracy = 1;
    fitness = 0.01;
    timeOfCl = tStamp;
    correctSetSize = size;
    numCorrectPred = 1;
    experience = 1;
    numerosity = 1;
    generality = gen;
  }//end Parameters
    
    
/**
 * <p>
 * It creates a new parameter object making a crossover between the 
 * parents parametres. 
 * </p>
 * @param p1 is the object that contain the parameters for the first parent.
 * @param p2 is the object that contain the parameters for the second parent.
 * @param tStamp is the current time stamp.
 */
  public Parameters (Parameters p1, Parameters p2, int tStamp){
    accuracy = (p1.accuracy + p2.accuracy)/2;
    fitness = Config.fitReduction * ( p1.getMicroClFitness() + p2.getMicroClFitness() )/2.;
    timeOfCl = tStamp;
    correctSetSize = p1.correctSetSize;
    numCorrectPred = 1;
    experience = 1;
    numerosity = 1;
    generality = p1.generality;
  }//end Classifier



/**
 * <p>
 * It creates a new parameter object copying the parameters from a parent
 * </p>
 * @param p is the origin of the copy
 * @param tStamp is the current time stamp
 */
  public Parameters (Parameters p, int tStamp){
	accuracy = p.accuracy;
    fitness = Config.fitReduction * (p.fitness / (double)p.numerosity);
    timeOfCl = tStamp;
    correctSetSize = p.correctSetSize;
    numCorrectPred = 1;
    experience = 1;
    numerosity = 1;
    generality = p.generality;
  } // end Parameters



/**
 * <p>
 * Updates the parameters of a classifier (reinforcement component)
 * </p>
 * @param microClInC is the size of the correct set activated in this iteration
 * @param classOfRule is the class predicted by the rule
 * @param classOfExample is the class of the input example 
 */
  public void updateParameters( double microClInC, double classOfRule, double classOfExample ) {        
	experience ++;
    
    //If the prediction is correct, update the number of correct predictions    
	if ( classOfRule == classOfExample ) numCorrectPred ++;  	
	
	//Update parameters
	accuracy = numCorrectPred / ((double)experience);
	correctSetSize = (correctSetSize * ((double)(experience - 2)) + microClInC )/ ((double)(experience-1.));    

  } // end updateParameters        


/**
 * <p>
 * Updates the fitness of a classifier
 * </p>
 * @param kSum is the sum of all accuracies.
 * @param k is the accuracy of the classifier.
 */
  public void updateFitness(double kSum, double k) {        
  	fitness += Config.beta * ((k * numerosity) / kSum - fitness);      
  } // end updateFitness        



/**
 * <p>
 * Returns the accuracy of the classifier.
 * </p>
 * @return a double with the accuracy
 */
  public double getAccuracy() {        
  	return accuracy;
  } // end getAccuracy     


/**
 * <p>
 * Returns the estimated correct set size.
 * </p>
 * @return an integer with the estimated action set size.
 */
  public double getCSize() {        
	return correctSetSize;
  } // end getCSize        


/**
 * <p>
 * Returns the experience of the classifier.
 * </p>
 * @return an integer with the experience of the classifier.
 */
  public int getExperience() {        
	return experience;
  } // end getExperience    





/**
 * <p>
 * Returns the numerosity of the classifier.
 * </p>
 * @return an integer with the numerosity
 */
  public int getNumerosity() {            
	return numerosity;
  } // end getNumerosity



/**
 * <p>
 * Returns the fitness of the current macro-classifier.
 * </p>
 * @return a double with the fitness of the classifier.
 */
  public double getMacroClFitness() {        
	return fitness;
  } // end getFitness        


/**
 * <p>
 * Returns the fitness of the current micro-classifier
 */
  public double getMicroClFitness() {        
	return fitness/((double)numerosity);
  } // end getMicroClFitness        



/**
 * <p>
 * Returns the time of the classifier.
 * </p>
 * @return an integer with the time stamp of the classifier.
 */
  public int getTime() {        
	return timeOfCl;
  } // end getTime    



/**
 * <p>
 * Gets the generality of the classifier.
 * </p>
 * @return a double with the generality of the classifier. 
 */
  public double getGenerality() {        
	return generality;
  } // end getGenerality



/**
 * <p>
 * Sets the time stamp for this classifier.
 * </p>
 * @param sTime  is the time stamp
 */
    public void setTime(int sTime) {        
        timeOfCl = sTime;
    } // end setTime        



/**
 * <p>
 * Sets the experience of the classifier.
 * </p>
 * @param expCl is the experience of the classifier.
 */
    public void setExperience(int expCl) {        
	experience = expCl;
    } // end setExperience        

/**
 * <p>
 * Sets the numerosity of the classifier
 * </p>
 * @param num  is the numerosity of the classifier.
 */
    public void setNumerosity(int num) {        
	numerosity = num;
    } // end setNumerosity        


/**
 * Sets the generality of the classifier.
 *
 * @param general is the generality that has to be set to the classifier. 
 */
    public void setGenerality(double general) {        
        generality = general;
    } // end getGenerality



/**
 * Increases the numerosity of the classifier.
 */
    public void increaseNumerosity(int num) {        
        numerosity += num;
    } // end increaseNumerosity


/**
 * Returns the probability of a classifier to be deleted.
 * 
 * @param avFitness is the average fitness of the set.
 * @return a double with the probability of being deleted.
 */
  public double deletionVote(double avFitness) {        
	double vote = (double) correctSetSize * (double) numerosity; 
	
	if (experience >= Config.theta_del && getMicroClFitness() < Config.delta * avFitness){
        	vote *= avFitness / getMicroClFitness() ;
	}
	return vote;      
  } // end deletionVote 



/**
 * <p>
 * Indicates if the classifier can subsume. The 
 * classifier has to be sufficiently accurate and 
 * sufficiently experienced to subsume another classifier.
 * </p>
 * @return a boolean indicating if the classifier can subsume.
 */

  public boolean couldSubsume(){
	if ( experience > Config.theta_sub && accuracy > Config.acc_0 ) return true;
	return false;
  }//end couldSubsume



/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the file output where the parameters have to be printed.
*/

  public void print (PrintWriter fout){
	
	fout.print ("\t"+accuracy);
   	fout.print ("\t"+fitness);
   	fout.print ("\t"+numerosity);
   	fout.print ("\t"+experience);
   	fout.print ("  "+correctSetSize);
   	fout.print ("  "+generality);
   	fout.print ("  "+timeOfCl);
   }//end print



  public void print (){
    System.out.print ("\t Acc: "+accuracy);
    String fit = new Double(fitness).toString();
    if (fit.length() > 4) fit = fit.substring(0,4);
    System.out.print ("\t Fit: "+fit);
    System.out.print ("\t Num: "+numerosity);
    System.out.print ("\t Exp: "+experience);
    System.out.print ("\t ASize: "+correctSetSize);
    System.out.print ("\t Generality : "+generality);
    System.out.print ("\t time: "+timeOfCl);  	
  }//end print

} // end Parameters





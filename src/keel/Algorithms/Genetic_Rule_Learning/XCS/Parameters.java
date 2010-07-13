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
import java.util.*;
import java.lang.*;
import java.io.*;


public class Parameters {
/**
 * <p>
 * In this class there are all the classifier parameters of XCS.
 * </p>
 * <p>
 */
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents the predition of the classifier.
 * </p>
 */
    private double prediction; 

/**
 * <p>
 * Prediction error
 * </p>
 */
    private double predError; 

/**
 * <p>
 * Represents the fitness of the classifier. It contains the total fitness 
 * (of all the microclassifiers).
 * </p>
 */
    private double fitness; 

/**
 * <p>
 * Represents the experience of the classifier
 * </p>
 */
    private int experience = 0; 

/**
 * <p>
 * Represents the action set size estimate.
 * </p>
 */
    private double actionSetSize; 

/**
 * <p>
 * Represents the time of the classifier.
 * </p>
 * 
 */
    private int timeOfCl = 0; 

/**
 * <p>
 * Represents the number of microclassifiers this macroclassifier contains.
 * </p>
 */
    private int numerosity = 1; 

/**
 * <p>
 * Represents the generality of the classifier. A classifier
 * with a high number of don't care symbols (in the ternary 
 * representation) or with widder intervals (in the real 
 * representation), is more general.
 * It takes an absolute value. It should be divided by the number
 * of genes of the classifier. 
 */
    private double generality = 0.;

/**
 * <p>
 * Represents the number of matches of the classifier. 
 * </p>
 */
    private int numberOfMatches = 0;


/**
 * <p>
 * Parameter "useful" of the classifier (used in
 * Dixon's reduction algorithm).
 * </p>
 */
    private boolean useful = false;



/**
 * Is stores the number of times that the classifier has been useful
 * during training.
 */
   private int usefulTimes = 0;


/**
 * Is the time of the classifier used in the "new reduction" algorithm. Its udpate and use is independent
 * of the "timeOfCl" parameter. 
 */
   private int tComp = 0;







  ///////////////////////////////////////
  // operations




/**
 * <p>
 * Constructs a Parameters Object.
 * </p><p>
 * </p>
 */
    public  Parameters() {        
    } // end Parameters        

/**
 * <p>
 * Creates a Parameter object initializing its attributes.
 * </p>
 * @param pred is the prediction of the classifier.
 * @param predErr is the prediction error of the classifier.
 * @param fit is the fitness
 * @param tStamp is the time stamp
 * @param size is the size of the set where the classifier belongs to.
 * @param exp is the experience of the classifier
 * @param num is the numerosity.
 */
 /*   public  Parameters(double pred, double predErr, double fit, int tStamp, double size, int exp, int num,double general) {        
        prediction = pred;
        predError = predErr;
        fitness = fit;
        timeOfCl = tStamp;
	tComp = tStamp;
        actionSetSize = size;
        experience = exp;
        numerosity = num;
   	generality = general;
    	numberOfMatches = 0;
    } // end Parameters        
*/
  
/**
 * <p>
 * It creates an instance of Parameters. It is used in covering
 * </p>
 * @param tStamp is the current time of the system.
 * @param size is the action set size
 * @param gen is the generality of the classifier.
 */
    
  public Parameters(int tStamp, double size, double gen){
    prediction = Config.initialPrediction;
    predError = Config.initialPError;
    fitness = Config.initialFitness;
    timeOfCl = tStamp;
    tComp = tStamp;
    actionSetSize = size;
    experience = 0;
    numerosity = 1;
    generality = gen;
    numberOfMatches = 0;
  }//end Parameters
    
    
/**
 * <p>
 * It creates a new parameter object making a crossover between the 
 * parents parametres. 
 * </p>
 * @param p1 is the object that contain the parameters for the 
 * first parent.
 * @param p2 is the object that contain the parameters for the
 * second parent.
 * @param tStamp is the current time stamp.
 */
  public Parameters (Parameters p1, Parameters p2, int tStamp){
    prediction = (p1.prediction + p2.prediction)/2;
    predError = Config.predictionErrorReduction * (p1.predError + p2.predError)/2.;
    fitness = Config.fitReduction * ((p1.fitness / (double)p1.numerosity) + (p2.fitness/ (double) p2.numerosity))/2.;
    timeOfCl = tStamp;
    tComp = p1.tComp;
    actionSetSize = p1.actionSetSize;
    experience = 0;
    numerosity = 1;
    generality = p1.generality;
    numberOfMatches = 0;
    useful = false;
    usefulTimes = 0;

  }//end Classifier


/**
 * <p>
 * Copy constructor
 * Constructs a Parameters Object initializing its attributes from another Parameters object.
 * </p>
 * @param p is the origin of the copy.
 */
    public Parameters (Parameters p, int tStamp){
	prediction = p.prediction;
        predError = p.predError;
        fitness = p.fitness/(double)p.numerosity;
        timeOfCl = tStamp;
	tComp = p.tComp;
        actionSetSize = p.actionSetSize;
        experience = 0;
        numerosity = 1;
	generality = p.generality;
    	numberOfMatches = 0;
    } // end Parameters


/**
 * <p>
 * Constructs a Parameters Object initializing its attributes
 * </p>
 * @param t contains the information to initialize the parameters object.
 */
    public Parameters (StringTokenizer t){
	prediction 	= new Double (t.nextToken()).doubleValue();
        predError 	= new Double (t.nextToken()).doubleValue();
        fitness 	= new Double (t.nextToken()).doubleValue();
        numerosity 	= new Integer(t.nextToken()).intValue();
        experience 	= new Integer(t.nextToken()).intValue();
        actionSetSize 	= new Double (t.nextToken()).doubleValue();
        generality 	= new Double (t.nextToken()).doubleValue();
        timeOfCl 	= 0; //new Integer(t.nextToken()).intValue();
	tComp = 0;
    	numberOfMatches = 0;
    }



/**
 * <p>
 * Updates the parameters of a classifier (reinforcement component)
 * </p>
 * @param P is the payoff of the current iteration (P = reward + gamma * maxPrediction)
 * @param microClSum is the numerosity of the action set (all the action set.). 
 */
    public void updateParameters(double P, double microClSum) {        
        experience ++;
        
        //Updates prediction and prediction error
        //It converges faster to the mean value. 
        //(used in initial iterations, when experience is low).
        if ((double) experience< 1.0/Config.beta){ 

            //Updates made as Butz's algorithmic description
        	predError += (Math.abs(P - prediction) - predError) / (double) experience;  
        	prediction += (P - prediction) / (double)experience;  
        	actionSetSize += (microClSum - actionSetSize) / (double) experience; 
       
        }
        else{  //It converges slower
        	predError += Config.beta * (Math.abs(P - prediction) - predError);  
        	prediction += Config.beta * (P - prediction);  
        	actionSetSize += Config.beta * (microClSum - actionSetSize); 
        }
        
       
        
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
    
	if(predError <= Config.epsilon_0){
	    return 1.0;
	}else{
	    return Config.alpha * Math.pow( predError / Config.epsilon_0 , -Config.nu);
	}
    } // end getAccuracy     


/**
 * <p>
 * Returns the estimated action set size.
 * </p>
 * @return an integer with the estimated action set size.
 */
    public double getASize() {        
        return actionSetSize;
    } // end getASize        

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
 * Returns the fitness of the current classifier.
 * </p>
 * @return a double with the fitness of the classifier.
 */
    public double getFitness() {        
        return fitness;
    } // end getFitness        


/**
 * <p>
 * Returns the prediction of the classifier.
 * </p>
 * @return a double with the prediction of the classifier.
 */
    public double getPrediction() {        
        return prediction;
    } // end getPrediction    




/**
 * <p>
 * Returns the prediction error of the classifier.
 * </p>
 * @return a double with the prediction error.
 */
    public double getPredError() {        
        return predError;
    } // end getPredError        

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
 * Returns the time of the classifier. The difference between timeOfCl and tComp is that the last one
 * is used to the "new reduction" algorithm, while "timeOfCl" is the usual time stamp of XCS
 * </p>
 * @return an integer with the time stamp of the classifier.
 */
    public int getCompTime() {        
        return tComp;
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
 * Gets the number of matches of the classifier. 
 * </p>
 * @return an integer with the number of matches of the classifier. 
 */
    public int getNumberMatches() {        
        return numberOfMatches;
    } // end getNumberMatches


/**
 * <p>
 * Gets the useful parameter of the classifier.
 * </p>
 * @return a boolean with the useful parameter value of the classifier. 
 */
    public boolean getUseful() {        
	return useful;
    } // end getUseful    



/**
 * <p>
 * Returns the useful time parameter of the classifier
 * </p>
 * @return an integer with the times that the classifier has been useful.
 */
    public int getUsefulTimes(){
	return usefulTimes;
    }


/**
 * <p>
 * Sets the fitness of the classifier.
 * </p>
 * @param fit is the fitness that has to be set.
 */
    public void setFitness(double fit) {        
        fitness = fit;
    } // end setFitness     



/**
 * <p>
 * Sets the prediction value.
 * </p>
 * @param pred is the prediction to be set
 */
    public void setPrediction(double pred) {        
	prediction = pred;
    } // end setPrediction        

/**
 * <p>
 * Sets the prediction error
 * </p>
 * @param predErr is the prediction error value.
 */
    public void setPredError(double predErr) {        
	predError = predErr;
    } // end setPredError        

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
 * Sets the time stamp for this classifier.
 * </p>
 * @param sTime  is the time stamp
 */
    public void setCompTime(int sTime) {        
        tComp = sTime;
    } // end setTime        




/**
 * <p>
 * Sets the classifier action set size estimate
 * </p>
 * @param size is the estimate of the action set size
 */
    public void setASize(double size) {        
	actionSetSize = size;
    } // end setASize        

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
 * <p>
 * Sets the generality of the classifier.
 * </p>
 * <p>
 * @param general is the generality that has to be set to the classifier. 
 * </p>
 */
    public void setGenerality(double general) {        
        generality = general;
    } // end getGenerality


/**
 * <p>
 * Sets the number of matches
 * </p>
 * @param num is the number of matches to set.
 */
    public void setNumberMatches(int num) {        
	numberOfMatches = num;
    } // end setNumberMatches        


/**
 * <p>
 * Sets the useful parameter
 * </p>
 * @param value is the value that the useful param has to be set.
 */
    public void setUseful(boolean value) {        
	useful = value;
    } // end setUseful    


/**
 * <p>
 * Sets the usefulTimes parameter
 * </p>
 * @param value is the value that the usefulTimes param has to be set.
 */
    public void setUsefulTimes(int value) {        
	usefulTimes = value;
    } // end setUseful    




/**
 * <p>
 * Increases the number of matches
 * </p>
 * @param num is the number of matches to increase.
 */
    public void increaseNumberMatches(int num) {        
	numberOfMatches += num;
    } // end increaseNumberMatches 




/**
 * <p>
 * Increases the numerosity of the classifier.
 * </p>
 */
    public void increaseNumerosity(int num) {        
        numerosity += num;
    } // end increaseNumerosity


/**
 * <p>
 * Returns the probability of a classifier to be deleted.
 * </p>
 * @param avFitness is the average fitness of the set.
 * @return a double with the probability of being deleted.
 */
    public double deletionVote(double avFitness) {        
        double vote = (double) actionSetSize * (double) numerosity; 
        // Now, it looks if the classifier has enough experience, 
        // and if the fitness of the microclassifier is less than 
        // the averageFitness * delta, (where delta < 1).
        if (experience >= Config.theta_del && fitness / (double)numerosity < Config.delta * avFitness){
        	// If it satisfies the condition, the vote increases in function of 
                // the difference between the average fitness and 
        	// the real fitness of the classifier. 
                // If the fitness is very low, the vote will be high.
        	vote *= avFitness / ( fitness / (double)numerosity);
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
    	if (experience > Config.theta_sub && predError < Config.epsilon_0) return true;
       	
    	return false;
    }



/**
 * <p>
 * Indicates if the classifier is experienced, 
 * and accurate enough to be in the reduction set. 
 * Otherwise, the classifier will not be included 
 * in the reduction
 * </p>
 * @param maxReward is the maximum reward of the environment. 
 * @return a boolean indicating if the classifier can be in the reduction set.
 */

    public boolean couldReduce(double maxReward){
    	if (experience > Config.theta_reduct && predError <= Config.epsilon_reduct && prediction >= maxReward*Config.Preduct) 
    		return true;
    	
    return false;
    }


/**
 * Indicates if the classifier is accurate and experienced enough
 * to not be removed from the population
 */
   public boolean couldComp(){
	if (experience > Config.theta_reduct && predError > Config.epsilon_reduct) return false;

	return true;
   }



/**
 * Adds the given value to the usefulTimes parameter of
 * the classifier
 * @param num is the value that has to be added
 *
*/
    public void addUsefulTimes(int num){
	usefulTimes += num;
   } 







/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the file output where the parameters have to be printed.
*/

    public void print (PrintWriter fout){
	
	fout.print ("\t"+prediction);
    	fout.print ("\t"+predError);
    	//String fit = new Double(fitness).toString();
    	//if (fit.length() > 4) fit = fit.substring(0,4);
    	fout.print ("\t"+fitness);
    	fout.print ("\t"+numerosity);
    	fout.print ("\t"+experience);
    	fout.print ("  "+actionSetSize);
    	fout.print ("  "+generality);
    	fout.print ("  "+timeOfCl);
    }



    public void print (){
    	System.out.print ("\t Pred: "+prediction);
    	System.out.print ("\t PredErr: "+predError);
    	String fit = new Double(fitness).toString();
    	if (fit.length() > 4) fit = fit.substring(0,4);
    	System.out.print ("\t Fit: "+fit);
    	System.out.print ("\t Num: "+numerosity);
    	System.out.print ("\t Exp: "+experience);
    	System.out.print ("\t ASize: "+actionSetSize);
    	System.out.print ("\t Generality : "+generality);
    	System.out.print ("\t time: "+timeOfCl);
    	System.out.print ("\t numOfMatches = "+numberOfMatches);
    	
     }

} // end Parameters





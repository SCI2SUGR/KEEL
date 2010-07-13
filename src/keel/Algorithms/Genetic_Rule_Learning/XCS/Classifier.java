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


public class Classifier {
/**
 * <p>
 * The classifier contains two classes, the representation and the
 * parameters. So, the metods of the class are: get and set
 * fitness, action, etc.
 * </p>
 */

	///////////////////////////////////////
   // associations

/**
 * <p>
 * Instance of a representation
 * </p>
 */
    private Representation rep; 
/**
 * <p>
 * Instance of a parameter
 * </p>
 */
    private Parameters parameters; 
/**
 * <p>
 * 
 * </p>
 */


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It constructs a classifier with the condition and the action specified.
 * It's used by the covering operator.
 * </p>
 * <p>
 * @param condition is the environtment state
 * @param action is the action chosen
 * @param size is the size of the set.
 * @param tStamp is the time
 * </p>

 */
  public  Classifier(double[] condition, int action, int size, int tStamp) {           
    rep = new Representation(condition,action);
    parameters = new Parameters(tStamp, size, rep.getGenerality());
  } // end Classifier        

	
/**
 * <p>
 * It creates an empty classifier with the parameters passed. It is used in the genetic
 * algorithm in crossover.
 * </p>
 * <p>
 * @param initPred is the prediction of the classifier.
 * @param initPError is the prediction error of the classifier.
 * @param initFit is the fitness
 * @param tStamp is the time stamp
 * @param size is the size of the set where the classifier belongs to.
 * @param exp is the experience of the classifier
 * @param num is the numerosity.
 * </p>
 */
	
/*  public Classifier(Classifier cl,double initPred, double initPError, double initFit, int tStamp, double size, int exp, int num){
    rep = new Representation(cl.rep);
    parameters = new Parameters(initPred,initPError,initFit,tStamp,size,exp,num,rep.getGenerality());
  } // end Classifier
*/

/**
 * <p>
 * It creates a new classifier copying the representation and the parameters of the 
 * classifier passed as a parameter.
 * </p>
 * @param cl is the classifier to be copied to the new classifier.
 */

  public Classifier (Classifier cl, int tStamp){
    rep = new Representation(cl.rep);
    parameters = new Parameters(cl.parameters, tStamp);
  }//end Classifier

/**
 * <p>
 * It creates a new classifier making a crossover between the parents parametres. Any
 * of the attributes representation are copied because it will be initialized by the GA.
 * </p>
 * @param parent1 is the first parent.
 * @param parent2 is the second parent.
 * @param tStamp is the current time stamp.
 */
  public Classifier (Classifier parent1, Classifier parent2, int tStamp){
    rep = new Representation(parent1.rep);
    parameters = new Parameters(parent1.parameters, parent2.parameters, tStamp);
  }//end Classifier


/**
 * <p>
 * It creates a new classifier with the parameters contained in the String. 
 * It is used when reading the classifiers from a file.
 * </p>
 */

  public Classifier(StringTokenizer t){
    int i=0;

    rep = new Representation(t);  	
    if (t.hasMoreTokens())	parameters = new Parameters(t);
    else 			parameters = null;
  } // end Classifier




/**
 * <p>
 * Mutates the classifier. It mutates the action and the condition.
 * </p>
 * @param envState is the current environtment state. It is necessary for the niched mutation.
 * @return a boolean indicating if the action has been mutated.
 */
    public boolean mutate(double[] envState) {        
        return rep.mutate(envState);
    } // end mutate


/**
 * <p>
 * Updates the parameters of a classifier (reinforcement component)
 * </p>
 * @param P is the payoff of the current iteration (P = reward + gamma * maxPrediction)
 * @param microClSum is the numerosity of the action set (all the action set.). 
 */
  public void updateParameters(double P,double microClSum) {        
    parameters.updateParameters(P,microClSum);
  } // end updateParameters        


/**
 * <p>
 * Updates the fitness of a classifier
 * </p>
 * @param kSum is the sum of all accuracies.
 * @param k is the accuracy of the classifier.
 */
 
  public void updateFitness(double kSum, double k) {        
    parameters.updateFitness(kSum,k);
  } // end updateFitness       





/**
 * <p>
 * Indicates if the classifier matches with the environmental state
 * </p>
 * @param envState is the environment state
 * @return a boolean indicating if the classifier matches.
 */
  public boolean match(double []envState) {        
    return rep.match(envState);
  } // end match




/**
 * <p>
 * Returns if the classifier of the class is equal to the classifier
 * given as a parameter.
 * </p>
 * @param cl is a classifier.
 * @return a boolean indicating if they are equals.
 */
  public boolean equals(Classifier cl) {        
    try{
    if (rep.getAction() != cl.rep.getAction()) return false;
    return rep.equals (cl.rep);	

    }catch (Exception e){
            return false;
    }
  } // end equals        




/**
 * <p>
 * Returns if the classifier of the class subsumes the classifier 
 * passed as a parameter. 
 * </p>
 * @param cl is the subsumed classifier.
 * @return a boolean indicating if it subsumes
 */
  
  public boolean doesSubsume(Classifier cl){

    if (rep.getAction() != cl.rep.getAction()) return false;

    if (parameters.couldSubsume()){
        if (rep.isMoreGeneral(cl.rep)){
            return true;
        }
    }
    return false;
  }//end doesSubsume



/**
 * <p>
 * Returns the probability of a classifier to be deleted.
 * </p>
 * @return a double with the probability of being deleted.
 * @param avFitness is the average fitness of the set.
 */
    public double deletionVote(double avFitness) {        
        return parameters.deletionVote(avFitness);
    } // end deletionVote   



/**
 * <p>
 * Returns the representation of the classifier.
 *</p>
 * @return a Representation with the condition of the classifier.
 */
   public Representation getRep(){
   	return rep;	
   }//end getRep


/**
 * <p>
 * It crosses a real allele within two parents. If the representation is 
 * a ternary representation, a crossover within intervals is not possible
 * because there is only one gene in each position. So, in this case, the gene 
 * of the second parent will be copied. 
 * In case of being a real representation, a random number is generated to 
 * decide where to cross the interval. It it's crossed within the inteval, 
 * the crossAllele method will do it. 
 * </p>
 * @param parent1 is the first parent classifier. 
 * @param parent2 is the second parent classifier. 
 */

  public void crossAllele(int i, Classifier parent1, Classifier parent2){
    rep.crossAllele(i,parent1.rep,parent2.rep);
  }//end crossAllele




/**
 * <p>
 * Sets the allele value
 * </p>
 * @param i is the position.
 * @param lowerValue is the lower value that has to be set.
 * @param upperValue is the upper value that has to be set.
 */
  public void setAllele(int i, double lowerValue, double upperValue) {        
    rep.setAllele(i,lowerValue,upperValue);
  } // end setAllele        
 

/**
 * <p>
 * sets the allele value
 * </p>
 * @param i is the position.
 * @param cl is the classifier that has to be copied.
 */
  public void setAllele(int i, Classifier cl) {        
    rep.setAllele(i,cl.rep);
  } // end setAllele    






/**
 * <p>
 * Returns the action of the classifier
 * </p>
 * @return an integer with the action of the classifier
 */
  public int getAction() {        
    return rep.getAction();
  } // end getAction        


/**
 * <p>
 * Returns the accuracy of the classifier.
 * </p>
 * @return a double with the accuracy
 */
  public double getAccuracy() {        
    return parameters.getAccuracy();
  } // end getAccuracy     


/**
 * <p>
 * Returns the prediction of the classifier.
 * </p>
 * @return a double with the prediction of the classifier.
 */
    public double getPrediction() {        
        return parameters.getPrediction();
    } // end getPrediction    



/**
 * <p>
 * Returns the prediction error of the classifier.
 * </p>
 * @return a double with the prediction error.
 */
    public double getPredError() {        
        return parameters.getPredError();
    } // end getPredError        


/**
 * <p>
 * Returns the fitness of the classifier.
 * </p>
 * @return a double with the fitness
 */
    public double getFitness() {        
        return parameters.getFitness();
    } // end getFitness        


/**
 * <p>
 * Returns the numerosity of the classifier.
 * </p>
 * @return an integer with the numerosity
 */
    public int getNumerosity() {        
        return parameters.getNumerosity();
    } // end getNumerosity



/**
 * <p>
 * Returns the time stamp of the classifier.
 * </p>
 * @return an integer with the time stamp of the classifier.
 */
    public int getTime() {        
        return parameters.getTime();
    } // end getTime        


/**
 * <p>
 * Returns the time of the classifier. The difference between timeOfCl and tComp is that the last one
 * is used to the "new reduction" algorithm, while "timeOfCl" is the usual time stamp of XCS
 * </p>
 * @return an integer with the time stamp of the classifier.
 */
    public int getCompTime() {        
        return parameters.getCompTime();
    } // end getTime        




/**
 * <p>
 * Returns the estimated action set size.
 * </p>
 * @return an integer with the estimated action set size.
 */
    public double getASize() {        
        return parameters.getASize();
    } // end getASize        

/**
 * <p>
 * Returns the experience of the classifier.
 * </p>
 * @return an integer with the experience of the classifier.
 */
    public int getExperience() {        
        return parameters.getExperience();
    } // end getExperience      


/**
 * <p>
 * Gets the generality of the classifier.
 * </p>
 * @return a double with the generality of the classifier. 
 */
    public double getGenerality() {        
        return parameters.getGenerality();
    } // end getGenerality


/**
 * <p>
 * Gets the number of matches of the classifier. 
 * </p>
 * @return an integer with the number of matches of the classifier. 
 */
    public int getNumberMatches() {        
        return parameters.getNumberMatches();
    } // end getNumberMatches


/**
 * <p>
 * Gets the useful parameter of the classifier.
 * </p>
 * @return a boolean with the useful parameter  value of the classifier. 
 */
    public boolean getUseful() {        
	return parameters.getUseful();
    } // end getUseful    



/**
 * <p>
 * Gets the useful times parameter of the classifier.
 * </p>
 * @return a boolean with the useful parameter  value of the classifier. 
 */
    public int getUsefulTimes() {        
	return parameters.getUsefulTimes();
    } // end getUseful    


/**
 * <p>
 * Sets the action passed to the classifier
 * </p>
 * @param act is the action to be set.
 */
    public void setAction(int act) {        
        rep.setAction(act);
    } // end setAction        


/**
 * <p>
 * Sets the time stamp for this classifier.
 * </p>
 * @param tStamp is the time stamp
 */
    public void setTime(int tStamp) {        
        parameters.setTime(tStamp);
    } // end setTime        



/**
 * <p>
 * Sets the compactation time stamp for this classifier.
 * </p>
 * @param tStamp is the time stamp
 */
    public void setCompTime(int tStamp) {        
        parameters.setCompTime(tStamp);
    } // end setTime        




/**
 * <p>
 * Sets the fitness of the classifier at the value specified
 * </p>
 * @param fit is the fitness that has to take the classifier.
 */
    public void setFitness(double fit) {        
        parameters.setFitness(fit);
    } // end setFitness        



/**
 * <p>
 * Sets the numerosity of the classifier.
 * </p>
 */
    public void setNumerosity(int num) {        
        parameters.setNumerosity(num);
    } // end setNumerosity

/**
 * <p>
 * Sets the prediction value.
 * </p>
 * @param pred is the prediction to be set
 */
    public void setPrediction(double pred) {        
	parameters.setPrediction(pred);
    } // end setPrediction        

/**
 * <p>
 * Sets the prediction error
 * </p>
 * @param predErr is the prediction error value.
 */
    public void setPredError(double predErr) {        
	parameters.setPredError(predErr);
    } // end setPredError        


/**
 * <p>
 * Sets the number of matches
 * </p>
 * @param num is the number of matches to be set.
 */
    public void setNumberMatches(int num) {        
	parameters.setNumberMatches(num);
    } // end setNumberMatches        


/**
 * <p>
 * Sets the useful parameter
 * </p>
 * @param value is the value that the useful param has to be set.
 */
    public void setUseful(boolean value) {        
	parameters.setUseful(value);
    } // end setUseful    



/**
 * <p>
 * Sets the usefulTimes parameter
 * </p>
 * @param value is the value that the useful param has to be set.
 */
    public void setUseful(int value) {        
	parameters.setUsefulTimes(value);
    } // end setUseful    


/**
 * <p>
 * Increases the number of matches
 * </p>
 * @param num is the number of matches to be increased.
 */
    public void increaseNumberMatches(int num) {        
	parameters.increaseNumberMatches(num);
    } // end increaseNumberMatches 


/**
 * <p>
 * Computes the generality of the classifier. Its result
 * is stored in the generality parameter. 
 * </p>
 */
    public void calculateGenerality() {        
        parameters.setGenerality(rep.getGenerality());
    } // end calculateGenerality




/**
 * <p>
 * Increases the numerosity of the classifier.
 * </p>
 * @param num is the number to increase the numerosity of the classifier.
 */
    public void increaseNumerosity(int num) {        
        parameters.increaseNumerosity(num);
    } // end increaseNumerosity


/**
 * <p>
 * Changes all the don't care symbols by the state in 
 * the environment, with Pspecify probability
 * </p>
 * @param env is the environment.
 */
    public void makeSpecify (double []env){
    	rep.makeSpecify(env);
    }


/**
 * <p>
 * Returns the number of don't care symbols in the 
 * classifier. It is used by the action set subsumption
 * </p>
 * @return a double with the number of don't care symbols.
 */
    public double numberOfDontCareSymbols(){
   	return rep.numberOfDontCareSymbols();	 
    }


/**
 * <p>
 * Indicates if the classifier is more general than 
 * the classifier passed as a parameter. 
 * </p>
 * @param cl is the classifier to which the current classifier is compared. 
 * @return a boolean indicating if the current classifier is more general
 */
    public boolean isMoreGeneral(Classifier cl){
    	return rep.isMoreGeneral(cl.rep);
    }




/**
 * <p>
 * Returns if the classifier can subsume. The 
 * classifier has to be sufficiently accurate and 
 * sufficiently experienced to subsume another classifier.
 * </p>
 * @return a boolean indicating if the classifier can subsume.
 */

    public boolean couldSubsume(){
    	return parameters.couldSubsume();
    }



/**
 * <p>
 * Returns if the classifier is experienced, 
 * and accurate enough to be in the reduction set. 
 * Otherwise, the classifier will not be included 
 * in the reduction
 * </p>
 * @param maxReward is the maximum reward of the environment. 
 * @return a boolean indicating if the classifier can be in the reduction set.
 */

    public boolean couldReduce(double maxReward){
    	return parameters.couldReduce(maxReward);
    }



/**
 * Adds the value passed to the usefulTimes parameter of
 * the classifier
 * @param num is the value that has to be added
 *
*/
    public void addUsefulTimes(int num){
	parameters.addUsefulTimes(num);
   } 



   public boolean couldComp(){
	return parameters.couldComp();
   }


/**
 * <p>
 * Prints the classifier.
 * </p>
*/
   public void print(){	
	rep.print();
	if (parameters!=null) parameters.print();
   }

/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the file output where the classifier has to be printed.
*/
   public void print(PrintWriter fout){	
	rep.print(fout);
	if (parameters!=null) parameters.print(fout);
   }

/**
 * <p>
 * Prints the desnormalized classifier to the specified file.
 * </p>
 * @param fout is the file output where the classifier has to be printed.
*/
   public void printNotNorm(PrintWriter fout){	
	rep.printNotNorm(fout);
	if (parameters!=null) parameters.print(fout);
   }

	

/**
 * <p>
 * It draws the population to a file. A character allele is
 * drawn as 1 o 0. Otherwise, a real allele is drawn in ten 
 * points, which represent the interval [0..1] divided in ten
 * fragments. In each fragment, there can be three types of symbols:
 *	. --> The fragment is not covered by the classifier.
 *	o --> The fragment is partially covered by the classifier. 
 *	O --> The fragment is totally covered by the classifier.
 * 
 * This notation has been got from Wilson2000 XCSR
 * </p>
 * @param fout is the file where the population has to be drawn.
 */
    public void draw(PrintWriter fout) {        
	rep.draw(fout);
    } //end draw


   

} // end Classifier





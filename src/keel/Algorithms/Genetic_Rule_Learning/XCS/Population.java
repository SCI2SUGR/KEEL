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


public class Population {
/**
 * <p>
 * This class is a classifier set. It is used to create the folowing sets:
 * match set [P], action set [A] and the population P, where all the classifiers belong to.
 *
 * There are three different constructors, one for each set. 
 * There are a lot of access methods to the population parameters, such as the
 * numerosities sums, get and set classifiers, etc. 
 * Moreover, there are specific methods used by XCS, like methods that
 * insert, insert subsuming, insert deleting, deletes, subsumes new classifiers 
 * in the population.
 * </p>
 */
  ///////////////////////////////////////
  // attributes

	static public int numAplicacions = 0;
/**
 * <p>
 * It is a reference to the parent population. It is needed, for example, to
 * delete a classifier from the action set, because it has to be deleted
 * from the population too.
 * </p>
 * 
 */
    private Population parentRef; 
   
/**
 * <p>
 * Number of macroClassifiers in the set.
 * </p>
 * 
 */
    private int macroClSum = 0; 
    

/**
 * <p>
 * Number of microClassifiers in the set (the sum of all classifiers numerosity)
 * </p>
 * 
 */
    private int microClSum = 0; 


   ///////////////////////////////////////
   // associations

/**
 * <p>
 * Reference to a genetic algorithm object.
 * </p>
 */
    private GA ga; 
/**
 * <p>
 * A reference to a specify object
 * </p>
 */
    private Specify specify; 
/**
 * <p>
 * The population (a classifier array)
 * </p>
 */
    private Classifier [] set;


  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It creates a new empty population (it's used in the beginning of each
 * experiment to initialize the population).
 * </p>
 * @param numCl is the number of classifiers in the population. It is defined in the configuration parameters.
 */
  public  Population(int numCl) {        
   macroClSum = 0;
   microClSum = 0;
   parentRef = null;
   set = new Classifier[numCl]; //It creates a new classifier set of numCl size. 
   specify = new Specify();
   
  } // end Population        

/**
 * <p>
 * This constructor creates the match set of the population. It has to
 * cover the uncovered actions while, at least, the theta_mna actions aren't
 * covered. It uses the actionCovered variable to do this.
 * </p>
 * @param envState is the state of the input (it has to take the classifiers that match with it).
 * @param pop is the population of the system (it contains all the classifiers).
 * @param tStamp it's the actual time. It's needed to create the new  classifiers).
 * @param isExploreExecution indicates if the current step is an explore or an exploit trail, 
 * because the covering operator will be applied or not.
 */
  public  Population(double[] envState, Population pop,int tStamp,boolean isExploreExecution) {        
	int pos = 0;
	// A population of size parent.numerosity + numberOfActions is needed, 
	// because in the worst case, it will have to cover all the actions.
	set = new Classifier [pop.macroClSum + Config.numberOfActions];
	microClSum = 0;
	macroClSum = 0;
	parentRef = pop;
	specify = new Specify();
	
	boolean[] actionCovered = new boolean [Config.numberOfActions];
	for (pos=0; pos<actionCovered.length; pos++){
		actionCovered[pos] = false;
	}
	
	for (pos=0; pos<pop.getMacroClSum(); pos++){
		if (pop.set[pos].match(envState)){
			addClassifier(pop.set[pos]);
			actionCovered[pop.set[pos].getAction()] = true;
		}
	}
	
	if (isExploreExecution){
		Covering cov = new Covering ();
		cov.coverActions(pop,this,envState,tStamp,actionCovered);
	}
  } // end Population        
   	
   	
   	
    

/**
 * <p>
 * It creates the action set using the match set and the chosen action.
 * </p>
 * @param matchSet is the match set of the population
 * @param action is the action chosen.
 */
  public  Population(Population matchSet, int action) {        
    macroClSum = 0;
    microClSum = 0;
    parentRef = matchSet;
    specify = new Specify();
    
    set = new Classifier [matchSet.macroClSum+2];  //We add two for the children if the GA is applied.
    for (int i=0; i<matchSet.macroClSum; i++){
    	if (matchSet.set[i].getAction() == action){
    		if (Config.debugLevel > 0) matchSet.set[i].print();
    		addClassifier(matchSet.set[i]);
    	}
    }
    ga = new GA(); 	        	
  } // end Population        





/**
 * <p>
 * Updates the XCS parameters of the set. It updates
 * all prediction, prediction error, fitness and action set
 * size parameters. After that, it applies action set 
 * subsumption if it's required.
 * </p>
 * @param reward is the reward given by the environment.
 * @param maxPA is the maximum prediction obtained in the prediction array.
 */
  public void updateParametersSet(double reward,double maxPA,double [] envState, int tStamp) {        
	int i = 0;
	double maxPrediction = - 1.0;
	int maxPredPos = 0;
	double P = reward + maxPA * Config.gamma;
	       
	for (i=0; i<macroClSum; i++){
		set[i].updateParameters(P,microClSum);	
		if (set[i].getPrediction() * (double)set[i].getNumerosity() > maxPrediction){
			maxPrediction = set[i].getPrediction() * (double)set[i].getNumerosity();
			maxPredPos = i;
		}
	}
	
	if (reward == 1000){
		set[maxPredPos].addUsefulTimes(1);
	}
	
	// Now it has to update the fitness of all the classifiers in set.
	double kSum = 0;
	double [] kVector = new double[macroClSum];
	
	for (i=0; i<macroClSum; i++){
		kVector[i] = set[i].getAccuracy();
		kSum += kVector[i] * set[i].getNumerosity();
	}
	
	for (i=0; i<macroClSum; i++){
		set[i].updateFitness(kSum,kVector[i]);
	}
	
	//Finally, it has to do actionSetSubsumption if it's needed.
	if (Config.doASSubsumption)
		doActionSetSubsumption();
	
	if (Config.doSpecify){
		Population pop = parentRef;
		while (pop.parentRef != null) pop = pop.parentRef;
		specify.makeSpecify(pop,this,envState,tStamp);	
	}
  } // end updateParametersSet        


  
/**
 * <p>
 * This method applies the action set subsumption
 * </p>
 */
  public void doActionSetSubsumption() {        
    int i,pos=0;
    Classifier cl = null;

	for (i=0; i<macroClSum; i++){
		if (set[i].couldSubsume()){
			if (cl == null || set[i].numberOfDontCareSymbols() > cl.numberOfDontCareSymbols() || (set[i].numberOfDontCareSymbols() == cl.numberOfDontCareSymbols() && Config.rand() < 0.5)){
				cl = set[i];
				pos = i;
			}
		}
	}
	if (cl != null){
    	for (i=0; i<macroClSum; i++){
			if (cl != set[i] && cl.isMoreGeneral(set[i])){
				cl.increaseNumerosity(set[i].getNumerosity());
				//Now, the classifier has to be removed from the actionSet and the population.
				//It's deleted from the action set.
				Classifier clDeleted = set[i];
				deleteClassifier(i);
				
				//And now, it's deleted from the population
				Population p = parentRef;
				while (p.parentRef != null){ 
					p = p.parentRef;
				}
					
				pos = p.isThereClassifier(clDeleted);  //The classifier is searched in the initial population.
				
				if (pos >=0)  p.deleteClassifier(pos);			
			}
    	}
	}	
  } // end doActionSetSubsumption        


/**
 * <p>
 * This method means that the classifer has to be definetely removed from 
 * the population because its numerosity has decreased to 0.
 * </p>
 * @param pos is the position of the population from where the classifier has to be deleted. 
 */

  public void deleteClassifier (int pos){
	if (pos < macroClSum){
		macroClSum --;
		set[pos] = set[macroClSum];
		set[macroClSum] = null;
		}
  }//end deleteClassifier



/**
 * <p>
 * Adds a classifier in the population.
 * </p>
 * @param cl is the new classifier to be added.
 */
  public void addClassifier(Classifier cl) {        
    try {
    	set[macroClSum] = cl;
    	microClSum += cl.getNumerosity();
    	macroClSum++;
	}catch (Exception e){
            System.out.println ("Exception in the insertion of a new classifier. The macroClSum is : "+macroClSum+" and the microClsum: "+microClSum);
            System.out.println ("And the maximum number of classifiers in the population is: "+Config.popSize);
            e.printStackTrace();
    }
  } // end addClassifier        



/**
 * <p>
 * Inserts the classifier in the population. Before that, it looks if there
 * is a classifier in the population with the same action and condition (in
 * this case, increments its numerosity). After, it checks that the number
 * of micro classifiers is less than the maximum population size. If it
 * isn't, it deletes one classifier from the population calling the
 * deleteClassifier function.
 * It inserts the classifier in the population and in the action set if it's
 * not null.
 * </p>
 * @param cl  is the classifier that has to be inserted in the population.
 */
  public void insertInPopulation(Classifier cl,Population ASet) {        
    boolean found = false;
    int i=0;

    while (i<macroClSum && !found){
        if (set[i].equals(cl)){
            set[i].increaseNumerosity(cl.getNumerosity());
            microClSum += cl.getNumerosity();
            if (ASet != null){
                if (ASet.isThereClassifier(set[i]) >= 0) ASet.microClSum += cl.getNumerosity();
            }
            found = true;
        }
        i++;
    }
    if (!found){ 
            addClassifier(cl);  
    }

    // Here, the classifier has been added to the population
    if (microClSum > Config.popSize){ //If we have inserted to many classifiers, we have to delete one.
            deleteClFromPopulation(ASet);
    }
  } // end insertInPopulation        




/**
 * <p>
 * Inserts the classifier into the population. Before, it looks if there
 * is a classifier in the population that can subsume the new one (in
 * this case, increments its numerosity). After, it checks that the number
 * of micro classifiers is less than the maximum population size. If it
 * isn't, it deletes one classifier of the population calling the
 * deleteClassifier function.
 * It inserts the classifier in the population and in the action set if it's
 * not null.
 * </p>
 * @param cl  is the classifier that has to be inserted in the population.
 */
  public void insertInPSubsumingCl(Classifier cl,Population ASet) {        
    int i=0;
    Classifier bestSubsumer = null;
    Classifier equalClassifier = null;

    //We look for the best subsumer or for an equal classifier. 
    while (i<macroClSum){
        if (set[i].couldSubsume() && set[i].isMoreGeneral(cl)){
            if (bestSubsumer == null) bestSubsumer = set[i];
            else if (set[i].isMoreGeneral(bestSubsumer)) bestSubsumer = set[i];
        }
        if (set[i].equals(cl)) equalClassifier = set[i];
        i++;
    }

    //If there is a subsumer, its numerosity is increased.
    if (bestSubsumer != null){
        bestSubsumer.increaseNumerosity(cl.getNumerosity());
        microClSum += cl.getNumerosity();
    }
    else if (equalClassifier != null){
        equalClassifier.increaseNumerosity(cl.getNumerosity());
        microClSum += cl.getNumerosity();
    }
    else{
        addClassifier(cl);
    }
    //There's no classifier deletion, independent of if the maximum size
    //has been overcomen
  } // end insertInPSubsumingCl  



  
/**
  * <p>
  * Deletes one classifier from the population. After that, if
  * the population passed as a parameter is not null, it looks for
  * the deleted classifier. If it is in the second population, it
  * will delete it too.
  * </p>
  * @param aSet is the population where the deleted classifier has to be searched. 
  * @return a Classifier that contains the deleted classifier.
  */
  public Classifier deleteClFromPopulation (Population aSet){
    //A classifier has been deleted from the population
    Classifier clDeleted = deleteClassifier(); 

    if (aSet != null){ //Now, this classifier has to be deleted from the action set (if it exists in).
        int pos = aSet.isThereClassifier(clDeleted); // It is searched in the action set. 
        if (pos >=0){ // It has to be deleted from the action set too.
            aSet.microClSum --;

            // If the classifier has 0 numerosity, we remove it from the population.
            if (clDeleted.getNumerosity() == 0){ //It has to be completely deleted from action set.
                aSet.macroClSum --;  // Decrements the number of macroclassifiers
                aSet.set[pos] = aSet.set[aSet.macroClSum];  // Moves the last classifier to the deleted one
                aSet.set[aSet.macroClSum] = null; // Puts the last classifier to null.
            }
        }
    }	
    return clDeleted;
  }//end deleteClFromPopulation


/**
 * <p>
 * Deletes a classifier from this population. It chooses the classifier to be deleted.
 * </p>
 * @return the deleted classifier.
 */
  public Classifier deleteClassifier() {        
    double avFitness = 0;
    int i=0;
    for (i=0; i<macroClSum; i++){
            avFitness += set[i].getFitness();
    }
    avFitness /= (double)microClSum;


    double voteSum = 0;
    Roulette rul = new Roulette(macroClSum);

    for (i=0; i<macroClSum; i++){
            rul.add(set[i].deletionVote(avFitness));	
    }

    i = rul.selectRoulette();

    // Now we have to remove the selected classifier.
    microClSum--;
    set[i].increaseNumerosity(-1);
    Classifier deleted = set[i];
    if (set[i].getNumerosity() == 0){
        macroClSum--;
        set[i] = set[macroClSum];
        set[macroClSum]=null;
    }

    return deleted;
  } // end deleteClassifier   

  
  

/**
 * <p>
 * It is used by specify. Returns the prediction error average.
 * </p>
 * @return a double with the prediction error average.
 */
  public double getPredErrorAverage() {        
   	double pErrorAv = 0;
   	for (int i=0; i<macroClSum; i++){
		pErrorAv += set[i].getPredError();
	}
    return pErrorAv/microClSum;
  } // end getPredErrorAverage        
    


/**
 * <p>
 * Returns the experience average of the population.
 * It is used by specify.
 * </p>
 * @return a double with the experience average.
 */
  public double getExperienceAverage() {        
   	double expAv = 0;
   	
   	for (int i=0; i<macroClSum; i++){
   		expAv += (double) set[i].getExperience()* set[i].getNumerosity();
   	}
    return expAv/microClSum;
  } // end getExperienceAverage        




/**
 * <p>
 * Returns the number of macro classifiers in the set.
 * </p>
 * @return a double with the number of macro classifiers.
 */
  public int getMacroClSum() {        
    return macroClSum;
  } // end getNumerosity 


/**
 * <p>
 * Returns the number of micro classifiers in the set.
 * </p>
 * @return a double with the number of micro classifiers.
 */
  public int getMicroClSum() {        
    return microClSum;
  } // end getMicroClSum


/**
 * <p>
 * Returns the generalization average of the classifiers
 * in the population.
 * </p>
 * @return a double with the generalization average. 
 */
  public double getGeneralityAverage() {        
    double value=0.;
    
    for (int i=0; i<macroClSum; i++){
    	value += set[i].getGenerality() * set[i].getNumerosity(); 
    }
    value /= (double)Config.clLength;
    
    return value / (double)microClSum;
  } // end getMicroClSum



/**
 * <p>
 * Returns a reference of the parent set.
 * </p>
 * @return a Population with the parent set.
 */
    public Population getParentRef() {        
        return parentRef;
    } // end getMicroClSum


/**
 * <p>
 * Sets the number of macro classifiers in the set.
 * </p>
 * @param mCl is the number of macro classifiers.
 */
    public void setMacroClSum(int mCl) {        
        macroClSum = mCl;
    } // end setNumerosity 

/**
 * <p>
 * Sets the number of micro classifiers in the set.
 * </p>
 * @param mCl is the number of micro classifiers.
 */
    public void setMicroClSum(int mCl) {        
        microClSum = mCl;
    } // end setMicroClSum

/**
 * <p>
 * Increases the number of micro classifiers in the set.
 * </p>
 * @param mCl is the number that the microClSum has to be increased.
 */
    public void increaseMicroClSum(int mCl) {        
        microClSum += mCl;
    } // end increaseMicroClSum





/**
 * <p>
 * Returns the position of the classifier in the set. If it is not in
 * the set, it returns -1.
 * </p>
 * @param cl is the classifier to be searched in the set
 * @return an integer with the position of the classifier in the set
 */
    public int isThereClassifier(Classifier cl) {        

        for (int i=0; i<macroClSum; i++){
        	if (set[i] == cl) return i;
        }
        
        return -1;
    } // end isThereClassifier     



/**
 * <p>
 * Returns the classifier in the given position.
 * </p>
 * @param i is the position of the classifier.
 * @return the classifier in the given position.
 */
    public Classifier getClassifier(int i) {        
        if (i<macroClSum) return set[i];
        
        return null;
    } // end getClassifier  






/**
 * <p>
 * Runs the GA if the time since the last application of the GA is greater than
 * the threshold.
 * </p>
 * @param tStamp is the actual time
 * @param envState is the environment state.
 */
    public void runGA(int tStamp, double[] envState) {        
        if ((double)tStamp - getAverageClTime() >= Config.theta_GA && macroClSum > 0){ 
        	setTimeOfClassifiers(tStamp);
        	ga.runGA(envState,this,tStamp);
    	}
    } // end runGA    




/**
 * <p>
 * Runs the compactation algorithm if the time since the last application of
 * the algorithm is greater than the threshold.
 * </p>
 * @param tStamp is the actual time
 */
    public void runReduction(int tStamp) {        
        if ((double)tStamp - getAverageCompTime() >= Config.reductWindow && macroClSum > 0){ 
        	setTCompOfClassifiers(tStamp);
        	reductInTrain();
    	}
    } // end runGA    






/**
 * <p>
 * Initializes the classifiers' time stamp to the tStamp value
 * </p>
 */
    public void setTimeOfClassifiers (int tStamp){
    	
    	for (int i=0; i<macroClSum; i++){
    		set[i].setTime(tStamp);
    	}
    
    }



/**
 * <p>
 * Initializes the classifiers' reduction time stamp to the tStamp value
 * </p>
 */
    public void setTCompOfClassifiers (int tStamp){
    	
    	for (int i=0; i<macroClSum; i++){
    		set[i].setCompTime(tStamp);
    	}
    
    }



/**
 * <p>
 * Returns the average time of classifiers in the population.
 * </p>
 * @return an integer with the average time.
 */
    public double getAverageClTime() {        
        int averageTime=0;
        for (int i=0; i<macroClSum; i++){
        	averageTime += (double)set[i].getTime() * (double)set[i].getNumerosity();
        }
        return averageTime/(double)microClSum;
    } // end getAverageClTime  




/**
 * <p>
 * Returns the average reduction time of classifiers in the population.
 * </p>
 * @return an integer with the average time.
 */
    public double getAverageCompTime() {        
        int averageTime=0;
        for (int i=0; i<macroClSum; i++){
        	averageTime += (double)set[i].getCompTime() * (double)set[i].getNumerosity();
        }
        return averageTime/(double)microClSum;
    } // end getAverageCompTime  





/////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////// FUNCTIONS FOR THE REDUCTION ALGORITHMS //////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////


/**
  * <p> 
  * It creates the D population defined by Wilson 2002. It creates
  * a population with the minimum number of classifiers that cover
  * all the input examples.
  * </p>
  */
    public Population createMCompPopulation (Environment env){
    	int moreMatches=0, maxMatched=0;

    	//We create a Config.popSize population instead of a macroClSum classifier, because, if it's
    	//used in a training execution, this population can increase (new classifiers can be added).
    	Population Mcomp = new Population (Config.popSize);
    
	while (env.getNumberOfExamples() > 0 && macroClSum > 0){
	    	moreMatches = 0;
	    	maxMatched = set[0].getNumberMatches();
	    	for (int i=1; i<macroClSum; i++){
	    		if (set[i].getNumberMatches() > maxMatched){
	    			maxMatched = set[i].getNumberMatches();
	    			moreMatches = i;
	    		}
	    	}
	    	
	    	Mcomp.addClassifier(set[moreMatches]);
		env.deleteMatchedExamples(set[moreMatches]);
		deleteClassifier(moreMatches);
	}
	return Mcomp;
    }  // end createMcompPopulation




/**
  * <p>
  * Sorts the population with a quicksort algorithm. Its criteria is
  * to sort the population by the numerosity or the experience.
  * </p>
  * @param i is the position where the sort algorithm has to start. 
  * @param j is the position where the sort algorithm has to finish. 
  * @param typeOfSort is to indicate if the parameter to sort the population is the numerosity (0 value) or the experience (1 value)
  */
  
    public void sortPopulation(int i, int j, int typeOfSort){
    	int s=0,t=0,aux=0;
    	int [] st = new int [2]; 
    	
    	if (i<j){ 
    		st[0]=i;
    		st[1]=j;
    		partition(i,j,st,typeOfSort);
    		sortPopulation(i,st[1],typeOfSort);
    		sortPopulation(st[0],j,typeOfSort);
    	}
    	
    }  // end orderPopulation



/**
  * <p>
  * It is an auxiliar function for the quicksort algorithm. It is 
  * used to make the partitions of the population that has to be
  * ordered. 
  * </p>
  * @param i is the position where the sort algorithm has to start. 
  * @param j is the position where the sort algorithm has to finish. 
  * @param st is the two positions of the population that have to be sorted. 
  * @param typeOfSort indicates if the parameter to sort the population is the numerosity (0 value) or the experience (1 value)
  */

    private void partition(int i, int j, int []st,int typeOfSort){
    	int middle=0, pivot=0;
    	Classifier tmp = null;
    	
    		st[0] = i;
    		st[1] = j;
    		middle = (i+j)/2;
    		if (typeOfSort == 0) 	pivot = set[middle].getNumerosity();
    		else 			pivot = set[middle].getExperience();
    		
    		while (st[0]<=st[1]){
    			if(typeOfSort == 0){
    				while (set[st[0]].getNumerosity() > pivot) st[0]++;
    				while (set[st[1]].getNumerosity() < pivot) st[1]--;
    			}
    			else{
    				while (set[st[0]].getExperience() > pivot) st[0]++;
    				while (set[st[1]].getExperience() < pivot) st[1]--;
    			}
    			if (st[0] < st[1]){
    				tmp = set[st[0]];
    				set[st[0]] = set[st[1]];
    				set[st[1]] = tmp;
    				st[0]++;
    				st[1]--;
    			}
    			else{
    				if (st[0] == st[1]){
    					st[0]++;
    					st[1]--;
    				}
    			}
    		}
    
    }


/**
 * <p>
 * It creates a new Population with only the sufficiently experienced
 * classifiers.
 * </p>
 * @param maxReward is the maximum reward of the  environment. 
 * @return a Population with the experienced population
*/

   public Population deleteNotExpClassifiers(double maxReward){
	//We create a Config.popSize population instead of a macroClSum classifier, because, if it's
    	//used in a training execution, this population can increase (new classifiers can be added).
	Population pExp = new Population(Config.popSize);
	for (int i=0; i<macroClSum; i++){
		if (set[i].couldReduce(maxReward)){
			pExp.addClassifier(set[i]);
		}
	}   	
 	return pExp;
   } // end deleteNotExpClassifiers


/**
 * <p>
 * It initialitzes all the useful params of the population.
 * </p>
 */

   public void setUseful (boolean value){
   
   	for (int i=0; i<macroClSum; i++){
   		set[i].setUseful(value);
   	}	
   } // end setUseful
   

/**
 * It sets the useful parameter to those classifiers
 * int the population having the best value of numerosity *
 * prediction. It's is used by the Strong version of the
 * Dixon's reduction algorithm.
 */
    void setUsefulAccurateClassifier (boolean value){
 	double max = (double)set[0].getNumerosity() * set[0].getPrediction();
 	Vector bestCls = new Vector();
 	bestCls.add((Classifier)set[0]);	
    	
    	for (int i=1; i<macroClSum; i++){
    		
    		if ((double)set[i].getNumerosity() * set[i].getPrediction() > max){
    			max = (double)set[i].getNumerosity() * set[i].getPrediction();
    			bestCls.removeAllElements();
    			bestCls.add(set[i]);
    		}	
    		else if ((double)set[i].getNumerosity() * set[i].getPrediction() == max){
    			bestCls.add(set[i]);	
    		}
    		
    	}
    
    	for (int i=0; i<bestCls.size(); i++){
    			((Classifier)bestCls.get(i)).setUseful(value);
    	}
    } //end setUsefulAccurateClassifier



/**
 * <p>
 * Returns the position in the population of a  classifier. 
 *^If this classifier does not exist, it returns -1.
 * </p>
 * @param cl is the classifier that has to be searched  in the population.
 * @return a int indicating the postion of the classifier. If it doesn't exists, a -1 is returned.
 */

   private int getPosition(Classifier cl){   	
   	for (int i=0; i<macroClSum; i++){
   		if (set[i] == cl) return i;	
   	}	
   	return -1;
   }


/**
 * <p>
 * Returns the number of non useful classifiers in the population.
 * </p>
 * @return a int with the number of non useful classifiers.
 */
   public int numberOfNotUseful(){
   int num =0;
   	for (int i=0; i<macroClSum; i++){
   		if (!set[i].getUseful())
   			num++;	
   	}
   	return num;
   }

/**
 * <p>
 * It removes all classifiers in population that are
 * not useful (the useful parameter is set to false)
 * <p>
 */
 
   public void removeNonUsefulClassifiers(){
   	for (int i=0; i<macroClSum; i++){
   		if (!set[i].getUseful()){
			deleteClassifier(i);   				
   			i--;
   		}
   	}	
  }









///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// OUR REDUCTION ALGORITHM ////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * It's the new reduction algorithm. (Experimental phase)
 */
    private void reductInTrain(){
	double averageUseful = 0.0, stdUseful = 0.0;
	int i=0;
	int numSum = 0;
	// A reference to the population is gotten.
	Population pop = this.getParentRef().getParentRef();
	
	while (i<macroClSum){
		if (!set[i].couldComp()){
			numSum += set[i].getNumerosity();
			set[i].setNumerosity(0);
			// The classifier is removed from the [A]
			set[i] = set[macroClSum-1];
			macroClSum --;
			/////
			numAplicacions ++;
		}
		else{ 
			averageUseful += set[i].getUsefulTimes();
			stdUseful += (set[i].getUsefulTimes() * set[i].getUsefulTimes());
			i++;
		}
	}	

	if (macroClSum >0){
		if (macroClSum > 1) stdUseful = Math.sqrt ( ( stdUseful -((averageUseful*averageUseful)/macroClSum)) / (macroClSum -1));
		averageUseful = averageUseful / (double)macroClSum;

		// With the "thres" parameter you can control the compactation pressure (add or substract the stdUseful)
		int thres = (int) (averageUseful - stdUseful);
		i=0;
		averageUseful = 0;
		while (i<macroClSum){
			if (set[i].getUsefulTimes() < thres && set[i].getPrediction() > Config.Preduct){
				numSum += set[i].getNumerosity();
				set[i].setNumerosity(0);
				set[i] = set[macroClSum-1];
				macroClSum --;
				/////
				numAplicacions++;
			}
			else{
				// We add the contribuion of each classifier to distribute the numerosity at the end
				averageUseful += set[i].getUsefulTimes();	
				i++;
			}
		}
	
		// The numerosity of classifiers deleted are set to other classifiers in the population.
		int addNum = 0;
		int discount = 0;
		for (i=0; i<macroClSum-1; i++){
			addNum = (int) ( ((double)set[i].getUsefulTimes() / averageUseful) * (double)numSum);
			set[i].increaseNumerosity(addNum);
			discount += addNum;
		}

		if (macroClSum > 0) set[macroClSum -1].increaseNumerosity(numSum - discount);

	}
	else{
		microClSum -= numSum;
		pop.microClSum -= numSum;
	}

	pop.deleteClWithZeroNum();
    }



/**
 * <p>
 * It deletes the classifiers with numerosity set to zero.
  </p>
 */
   private void deleteClWithZeroNum(){
	int i=0;

	while (i<macroClSum){
		if (set[i].getNumerosity() == 0){
			set[i] = set[macroClSum-1];
			macroClSum--;
		}
		else{
			i++;
		}

	}
	
   }


///////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// FUNCTIONS FOR THE STATISTICS //////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * <p>
 * Looks for the percentage of optimal classifiers present in the
 * current population.
 * </p>
 * @param optimalPop is the optimal population.
 * @return a double with the optimal population percentage in the current population.
 */
 
    public double optimalPopulationPercentage (Population optimalPop){
    	
    	double num=0.;
    	if (optimalPop != null){
       		for (int i=0; i<optimalPop.macroClSum; i++){
    			for (int j=0; j<macroClSum; j++){
    				if (optimalPop.set[i].equals(set[j])){
    					num++;
    					break;
    				}		
    			}	
    		}
    		num /= (double) optimalPop.macroClSum;  
    	}
    	return num;
    } // end optimalPopulationPercentage





/**
 * <p>
 * Prints the population into the specified file. 
 * </p>
 * @param fileName is the file name where the classifiers have to be printed. 
 */
    public void printPopulationToFile(String fileName) {        
	
	try{
		PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		fout.println (macroClSum);  // We write the number of macro classifiers.	
		fout.println (microClSum);  // We write the number of micro classifiers.
		fout.println ("Cond - Action  - prediction - predError - fitness - Numerosity - Experience - aSetSize - Generality - timeStamp");
		for (int i=0; i<macroClSum; i++){
			set[i].print(fout);
			fout.println("");
		}
		fout.close();	
    	}catch (IOException eio){
    		System.out.println ("ERROR when writing the population to a out file. ");
    	}
    	
    	
    } // end printPopulationToFile




/**
 * <p>
 * It draws the population to a file. A character allele is
 * drawn as 1 o 0. Otherwise, a real allele is drawn in ten 
 * points, which represent the interval [0..1] divided in ten
 * fragments. In each fragment, it can be three types of symbols:
 *	. --> The fragment is not covered by the classifier.
 *	o --> The fragment is partially covered by the classifier. 
 *	O --> The fragment is totally covered by the classifier.
 * 
 * This notation is obtained from Wilson-2000 XCSR
 * </p>
 * @param fout is the file where the population has to be drawn.
 */
    public void drawPopulationToFile(PrintWriter fout) {        
	fout.println (macroClSum);  // We write the number of macro classifiers.	
	fout.println (microClSum);  // We write the number of micro classifiers.
	for (int i=0; i<macroClSum; i++){
			set[i].draw(fout);
			fout.println("");
		}
    } //end drawPopulationToFile




/**
 * <p>
 * Prints the population into the file specified. 
 * </p>
 * @param fout is the file where the population has to be printed.
 */
    public void printPopulationToFile(PrintWriter fout) {        
	
		fout.println (macroClSum);  // We write the number of macro classifiers.	
		fout.println (microClSum);  // We write the number of micro classifiers.
		fout.println ("Cond - Action     - prediction - predError - fitness - Numerosity - Experience - aSetSize - Generality - timeStamp");
		for (int i=0; i<macroClSum; i++){
			set[i].print(fout);
			fout.println("");
		}
    } // end printPopulationToFile



/**
 * <p>
 * Prints the desnormalized population into the specified file. 
 * </p>
 * @param fout is the file where the population has to be printed.
 */
    public void printNotNormPopToFile(PrintWriter fout) {        
		fout.println (macroClSum);  // We write the number of macro classifiers.	
		fout.println (microClSum);  // We write the number of micro classifiers.
		fout.println ("Cond - Action     - prediction - predError - fitness - Numerosity - Experience - aSetSize - Generality - timeStamp");
		for (int i=0; i<macroClSum; i++){
			set[i].printNotNorm(fout);
			fout.println("");
		}
	}



/**
 * <p>
 * Reads the population from a file. 
 * </p>
 * @param fileName is the file name where the classifiers have to be read. 
 */
    public void readPopulationFromFile(String fileName) {        
	String line = null;
	try{
		System.out.print ("\n LOADING POPULATION FROM FILE");
		BufferedReader fin = new BufferedReader(new FileReader(fileName));
		macroClSum = new Integer(fin.readLine().trim()).intValue();  // We write the number of macro classifiers.	
		microClSum = new Integer(fin.readLine().trim()).intValue();  // We write the number of micro classifiers.
		fin.readLine(); //This line is only information to interpret the file.  
		for (int i=0; i<macroClSum; i++){
			line = fin.readLine();
			set[i] = new Classifier(new StringTokenizer(line));
		}
		fin.close();	
		System.out.println (": done!");
    	}catch (IOException eio){
    		System.out.println ("ERROR when reading the population from the file: "+fileName+": "+eio);
    	}
    	
    	
    } // end readPopulationFromFile



    public void print(){
	System.out.println ("POPULATION: ");
	System.out.println ("\tmacroClassifierSum = "+macroClSum);	
	System.out.println ("\tmicroClassifierSum = "+microClSum+"\n");
	for (int i=0; i<macroClSum; i++){
		System.out.print ("\n Cl_"+i);
		set[i].print();
	}
	System.out.println ("");
    }


} // end Population











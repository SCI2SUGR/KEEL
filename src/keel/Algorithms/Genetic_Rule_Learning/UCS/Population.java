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


public class Population {
/**
 * <p>
 * This class contains a rule-set. It is used to create the folowing sets:
 * population [P], match set [M], and correct set [C].
 *
 * There are three different constructors, one for each set. 
 * There are a lot of access methods to the population parameters, such as the
 * numerosities sums, get and set classifiers, etc. 
 * Moreover, there are specific methods used by UCS, like methods that
 * insert, insert subsuming, insert deleting, deletes, subsumes new classifiers 
 * in the population.
 * </p>
 */
	
	
/**
 * The population 
 */
    private Classifier []set;
   
/**
 * Number of macroClassifiers in the set.
 */
    private int macroClSum = 0; 
    

/**
 * Number of microClassifiers in the set (the sum of all classifiers numerosity)
 */
    private int microClSum = 0; 


/**
 * Reference to the parent population.
 */
    private Population parentRef; 
    

/**
 * Reference to a genetic algorithm object.
 */
    private GA ga; 





/**
 * <p>
 * Population constructor: it creates an empty population.
 * </p>
 * @param numCl is the number of classifiers in the population. 
 * It is defined in the configuration parameters.
 */
  public  Population( int numCl ) {        
   macroClSum = 0;
   microClSum = 0;
   parentRef = null;
   set = new Classifier[numCl]; //It creates a new classifier set of numCl size. 
   
  }// end Population        




/**
 * <p>
 * Math set [M] constructor. It selects all the matching classifiers from [P] to create [M].
 * </p>
 * @param pop is the population [P].
 * @param envState is the input example.
 */
  public  Population( Population pop, double []envState ) {        
	int pos;
	boolean coveringApplied = false;
	
	// We resize the population to make room for new classifiers
	set = new Classifier [ pop.macroClSum + Config.numberOfActions];
	microClSum = 0;
	macroClSum = 0;
	parentRef = pop;
	
	for ( pos=0; pos<pop.getMacroClSum(); pos++ ){
		if ( pop.set[pos].match( envState ) ){
			addClassifier( pop.set[pos] );
		}
	}
  } // end Population        
   	
   	
   	
    

/**
 * <p>
 * Correct set [C] constructor. It selects all the classifiers from [M] that advocate the class 
 * of the example.
 * </p>
 *
 * @param matchSet is the match set of the population
 * @param envState is the input example.
 * @param classOfExample is the class of the input example.
 */
  public Population( Population matchSet, double []envState, int classOfExample, int tStamp ) {        
	int pos;
	macroClSum = 0;
    microClSum = 0;
    parentRef = matchSet;
    
    set = new Classifier [ matchSet.macroClSum + 2 ]; 
    for ( int i=0; i<matchSet.macroClSum; i++ ){
    	if ( matchSet.set[i].getAction() == classOfExample ){
    		if (Config.debugLevel > 0) matchSet.set[i].print();
    		addClassifier(matchSet.set[i]);
    	}
    }
    ga = new GA(); 	    
    Population initialPop = matchSet.parentRef;

	// Covering if there is not any classifier in [C]
    boolean coveringApplied = false;
	while (macroClSum == 0){	
		coveringApplied = true;
	
		// We have to cover the action if there isn't any classifier in [C].
		Classifier cl = new Classifier ( envState, classOfExample, microClSum+1, tStamp );			
		
		
		if ( initialPop.microClSum + 1 >= Config.popSize ){ // The maximum population size has been overcomen
			
			// Deleting one classifier from [C]
			Classifier clDeleted = initialPop.deleteClassifier(); 
			
			// [M] is searched for the deleted classifier. If it exists, it is removed. 
			pos = matchSet.isThereClassifier(clDeleted); 
			if (pos >=0){ 
				matchSet.microClSum --; 
				if ( clDeleted.getNumerosity() == 0 ){ 
					matchSet.deleteClassifier(pos);	
				}		
			}
			
			// [C] is searched for the deleted classifier. If it exists, it is removed
			pos = isThereClassifier(clDeleted); 
			if (pos >=0){ 
				microClSum --; 
				if ( clDeleted.getNumerosity() == 0 ){ 
					deleteClassifier(pos);	
				}		
			}
		}
		
		//The new classifier is added to [C], [M] and [P]
		addClassifier(cl); 	
		matchSet.addClassifier(cl);
		initialPop.addClassifier(cl); 
  	}    	
  } // end Population        




/**
 * <p>
 * Updates  UCS' parameters. 
 * </p>
 * 
 * @param microClInC is the number of microclassifiers in [C]
 * @param envState is the input example
 * @param classOfExample is the class of the input example
 * @param tStamp is the current time stamp
 */
  public void updateParametersSet( double microClInC, double []envState, int classOfExample, int tStamp) {        
	int i;
	
	for ( i=0; i<macroClSum; i++ ){
		set[i].updateParameters( microClInC, classOfExample );	
	}
	
	double kSum = 0;
	double []kVector = new double [ macroClSum ];
	
	for ( i=0; i<macroClSum; i++ ){
		if ( set[i].getAction() != classOfExample ){
			kVector[i] = 0;
		}	
		else{
			kVector[i] = set[i].getAccuracy();
			if ( kVector[i] < Config.acc_0 ){
				kVector[i] = Config.alpha * Math.pow( Config.acc_0 / kVector[i] , -Config.nu);	
			}
			else{
				kVector[i] = 1;
			}
		}
		kSum += kVector[i] * set[i].getNumerosity();
	}
	
	for ( i=0; i<macroClSum; i++ ){
		set[i].updateFitness( kSum, kVector[i] );
	}
	
  } // end updateParametersSet        


       


/**
 * <p>
 * This method means that the classifer has to be definetely removed from 
 * the population because its numerosity has decreased to 0.
 * </p>
 * @param pos is the position of the population from where the classifier has to be deleted. 
 */

  public void deleteClassifier ( int pos ){
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
            System.out.println ("Exception in the insertion of a new classifier. The macroClSum is : "+macroClSum);
            System.out.println (" and the microClsum: "+microClSum);
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
  public void insertInPopulation( Classifier cl, Population ASet ) {        
    boolean found = false;
    int i=0;

    while ( i<macroClSum && !found ){
        if ( set[i].equals(cl) ){
            set[i].increaseNumerosity( cl.getNumerosity() );
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
    if ( microClSum > Config.popSize ){ //If we have inserted to many classifiers, we have to delete one.
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
  public void insertInPSubsumingCl( Classifier cl ) {        
    int i=0;
    Classifier bestSubsumer = null;
    Classifier equalClassifier = null;

    //We look for the best subsumer or for an equal classifier. 
    while ( i<macroClSum ){
        if ( set[i].doesSubsume( cl ) ){
            if ( bestSubsumer == null ) bestSubsumer = set[i];
            else if ( set[i].doesSubsume( bestSubsumer ) ) bestSubsumer = set[i];
        }
        if ( set[i].equals(cl) ) equalClassifier = set[i];
        i++;
    }

    //If there is a subsumer, its numerosity is increased.
    if ( bestSubsumer != null ){
        bestSubsumer.increaseNumerosity( cl.getNumerosity() );
        microClSum += cl.getNumerosity();
    }
    else if (equalClassifier != null){
        equalClassifier.increaseNumerosity( cl.getNumerosity() );
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
            avFitness += set[i].getMacroClFitness();
    }
    avFitness /= (double)microClSum;


    double voteSum = 0;
    Roulette rul = new Roulette( macroClSum );

    for (i=0; i<macroClSum; i++){
            rul.add( set[i].deletionVote(avFitness) );	
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
    public void runGA(int tStamp, double[] envState, int classOfExample ) {        
        if ((double)tStamp - getAverageClTime() >= Config.theta_GA && macroClSum > 0){ 
        	setTimeOfClassifiers(tStamp);
        	ga.runGA( this, envState, classOfExample, tStamp);
    	}
    } // end runGA    



/**
 * It implements tournament selection. 
 * This method is called from TournamentSelection.makeSelection.
 * The tournament Selection has been implemented as defined by Butz.
 * It possible to choose the same classifier in [A] to be
 * the parent of both tournaments
 */

  public Classifier tournamentSelection(){
    int i,j;
    double maxFitness = -1.;
    Classifier cl_t = null;
    while ( cl_t == null ){
        for ( i=0; i<macroClSum; i++ ){
            if(set[i].getMicroClFitness() > maxFitness){
                for (j=0; j<set[i].getNumerosity(); j++){
                    if (Config.rand() < Config.tournamentSize){
                        cl_t = set[i];
                        maxFitness = set[i].getMicroClFitness();
                        break;
                    }
                }
            }
        }
  	}  
    return cl_t;
  }//end tournamentSelection

 


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



///////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// FUNCTIONS FOR THE STATISTICS //////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////



/**
 * <p>
 * Prints the population into the specified file. 
 * </p>
 * @param fileName is the file name where the classifiers have to be printed. 
 */
  public void printPopulationToFile(String fileName) {        
	
	try{
		PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
		printPopulationToFile ( fout );
	}catch (IOException eio){
		System.out.println ("ERROR when writing the population to a out file. ");
		eio.printStackTrace();
	}	
  } // end printPopulationToFile



/**
 * <p>
 * Prints the population into the file specified. 
 * </p>
 * @param fout is the file where the population has to be printed.
 */
    public void printPopulationToFile(PrintWriter fout) {        
	
		fout.println (macroClSum);  // We write the number of macro classifiers.	
		fout.println (microClSum);  // We write the number of micro classifiers.
		fout.println ("Cond - Action  - accuracy -  fitness - Numerosity - Experience - [C]SetSize - Generality - timeStamp");
		for (int i=0; i<macroClSum; i++){
			set[i].print(fout);
			fout.println("");
		}
    } // end printPopulationToFile



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











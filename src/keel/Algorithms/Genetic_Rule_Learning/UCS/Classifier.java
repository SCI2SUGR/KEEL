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


public class Classifier {
/**
 * <p>
 * The classifier contains two classes, the representation and the
 * parameters. So, the metods of the class are: get and set
 * fitness, action, etc.
 * </p>
 */
 
/**
 * Represents the action of the classifier. 
 */
    private int action; 


/**
 * It is an array of references to attributes
 */
    private Attribute rep[]; 

    
/**
 * Instance of a parameter
 */
    private Parameters parameters; 



/**
 * <p>
 * It constructs a classifier with the condition and the action specified.
 * It's used by the covering operator.
 * </p>
 * <p>
 * @param size is the size of the set.
 * @param tStamp is the time
 * </p>

 */
  public  Classifier( double[] envState, int classOfExample, int size, int tStamp ) {           
    int i = 0;
    rep = new Attribute [ Config.clLength ];

    if (Config.ternaryRep){  
        for (i=0; i<rep.length; i++)	rep[i] = new TernaryRep( envState[i] );
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("ternary"))		
                rep[i] = new TernaryRep( envState[i] );
            else{
                if ( envState[i] == Config.unknownValue )
                	rep[i] = new RealRep( Config.rand() );
                else
                	rep[i] = new RealRep( envState[i] );
        	}
        }	
    }
    action = classOfExample;
    
    parameters = new Parameters( tStamp, size, getGenerality() );
  } // end Classifier        

	

/**
 * <p>
 * It creates a new classifier copying the representation and the parameters of the 
 * classifier passed as a parameter.
 * </p>
 * @param cl is the classifier to be copied to the new classifier.
 */

  public Classifier ( Classifier cl, int tStamp ){
    
    copyRepresentation ( cl );
    parameters = new Parameters( cl.parameters, tStamp);
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
  public Classifier ( Classifier parent1, Classifier parent2, int tStamp){
    copyRepresentation ( parent1 );
    parameters = new Parameters(parent1.parameters, parent2.parameters, tStamp);
  }//end Classifier


/**
 * <p>
 * It copies the representation of a classifier to the new classifiers, reserving new memory for the new one. 
 */
  private void copyRepresentation ( Classifier cl ){
  	int i = 0;
    rep = new Attribute[ Config.clLength ];
    
    if (Config.ternaryRep){  // It's a ternary representation. We have to create an array.
        for (i=0; i<rep.length; i++) rep[i] = new TernaryRep( (TernaryRep)cl.rep[i] );
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("ternary")){
                    rep[i] = new TernaryRep( (TernaryRep)cl.rep[i] );
            }
            else{
                    rep[i] = new RealRep( (RealRep)cl.rep[i] );
            }
        }
    }
    action = cl.action;
  }//end copyRepresentation
  
  




/**
 * <p>
 * Mutates the classifier. It mutates the action and the condition.
 * </p>
 * @param envState is the current environtment state. It is necessary for the niched mutation.
 * @return a boolean indicating if the action has been mutated.
 */
  public boolean mutate(double[] envState) {        
  	int i;
  	
  	// First, the condition is mutated
    for (i=0; i<rep.length; i++){
		rep[i].mutate( envState[i] );
    }
  	     
  	// Second, the action is mutated
  	int act = 0;
    if (Config.rand() < Config.pM){
		do{
			act = (int)(Config.rand() * (double)Config.numberOfActions);
		}while (action == act);
		action = act;
    	return true;
    }
    return false;
  } // end mutate



/**
 * <p>
 * Updates the parameters of a classifier.
 * </p>
 * @param microClInC is the numerosity of the action set (all the action set.). 
 * @param classOfExample is the class of the example.
 */
  public void updateParameters(double microClInC, int classOfExample) {        
    parameters.updateParameters( microClInC, action, classOfExample );
  } // end updateParameters        


/**
 * <p>
 * Updates the fitness of a classifier
 * </p>
 * @param kSum is the sum of all accuracies.
 * @param k is the accuracy of the classifier.
 */
 
  public void updateFitness(double kSum, double k) {        
    parameters.updateFitness( kSum, k );
  } // end updateFitness       





/**
 * <p>
 * Indicates if the classifier matches with the environmental state
 * </p>
 * @param envState is the environment state
 * @return a boolean indicating if the classifier matches.
 */
  public boolean match(double []envState) {        
    for (int i=0; i<rep.length; i++){
    	if ( envState[i] != Config.unknownValue ){
			if ( !rep[i].match(envState[i]) ) return false;
		}
	}
	return true;
  } // end match




/**
 * <p>
 * Returns if the classifier of the class is equal to the classifier
 * given as a parameter.
 * </p>
 * @param cl is a classifier.
 * @return a boolean indicating if they are equals.
 */
  public boolean equals( Classifier cl ) {        
    int i;
    
    try{
    	//Checking the action
    	if ( action != cl.getAction() ) return false;
    	
    	//Checking the condition
    	for (i=0; i<rep.length; i++){
			if (! rep[i].equals ( cl.rep[i] ) ) return false;
		}
		return true;

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
  
  public boolean doesSubsume( Classifier cl ){
	int i;
	
	// First, check if the condition is the same
    if ( action != cl.getAction() ) return false;

	// Then, check that is more general
    if ( parameters.couldSubsume() ){
        for (i=0; i<rep.length;i++){
			if ( !rep[i].isMoreGeneral(cl.rep[i]) ) return false;
		}
		return true;
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
    if (Config.ternaryRep){
   		rep[i].setAllele( parent2.rep[i] );
   	}
   	else{
   		if ( Config.typeOfAttributes[i].equals("ternary") ){
   			rep[i].setAllele(parent2.rep[i]);
   		}
   		else{
   			if (Config.rand() < 0.5)
   				rep[i].setAllele(parent2.rep[i]);
   			else{ //The alleles has to be crossed
				rep[i].setAllele(parent1.rep[i].getLowerAllele(), parent2.rep[i].getUpperAllele());
				rep[i].verifyInterval();
			} 		  	

		}
	}
  }//end crossAllele



/**
 * <p>
 * sets the allele value
 * </p>
 * @param i is the position.
 * @param cl is the classifier that has to be copied.
 */
  public void setAllele(int i, Classifier cl) {        
    rep[i].setAllele( cl.rep[i] );
  } // end setAllele    



/**
 * <p>
 * Returns the action of the classifier
 * </p>
 * @return an integer with the action of the classifier
 */
  public int getAction() {        
    return action;
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
 * Returns the fitness of the "macro-classifier"
 * </p>
 * @return a double with the macro-classifier fitness
 */
  public double getMacroClFitness() {        
 	return parameters.getMacroClFitness();
  } // end getMacroClFitness        


/**
 * <p>
 * Returns the fitness of the "micro-classifier"
 * </p>
 * @return a double with the micro-classifier fitness
 */
  public double getMicroClFitness() {        
 	return parameters.getMicroClFitness();
  } // end getMicroClFitness        



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
 * Returns the estimated action set size.
 * </p>
 * @return an integer with the estimated action set size.
 */
    public double getCSize() {        
        return parameters.getCSize();
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
    double genSum = 0.;

    for (int i=0; i<rep.length; i++){
            genSum += rep[i].getGenerality();
    }
    return genSum;
  } // end getGenerality
 



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
 * Sets the numerosity of the classifier.
 * </p>
 */
    public void setNumerosity(int num) {        
        parameters.setNumerosity(num);
    } // end setNumerosity



/**
 * <p>
 * Computes the generality of the classifier and stores it
 * in it corresponding parameter in the class Parameters.
 * </p>
 */
  public void calculateGenerality() {        
	parameters.setGenerality( getGenerality() );
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
 * Returns if the classifier can subsume. The 
 * classifier has to be sufficiently accurate and 
 * sufficiently experienced to subsume another classifier.
 * </p>
 * @return a boolean indicating if the classifier can subsume.
 */

  public boolean couldSubsume(){
	return parameters.couldSubsume();
  }//end couldSubsume



/**
 * Prints the classifier.
 */
  public void print(){	
	System.out.print ("  Act: " + action + "   Conf:  ");
	for (int i=0; i<rep.length; i++){
		rep[i].print();
	}
	if ( parameters!=null ) parameters.print();
  }//end print


/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the file output where the classifier has to be printed.
 */
  public void print( PrintWriter fout ){	
	fout.print(" ");
    for (int i=0; i<rep.length; i++){
        rep[i].print( fout );
    }
	fout.print("  " + action + "  ");
	if ( parameters!=null ) parameters.print(fout);
  }

/**
 * <p>
 * Prints the desnormalized classifier to the specified file.
 * </p>
 * @param fout is the file output where the classifier has to be printed.
*/
  public void printNotNorm( PrintWriter fout ){	
	int i=0;
    fout.print(" ");
    try{
    for (i=0; i<rep.length; i++){
		if (Config.typeOfAttributes[i].equals("ternary")){
            rep[i].print ( fout );
        }else if (Config.typeOfAttributes[i].equals("integer")){
            rep[i].printNotNorm ( fout, Config.attBounds[i][0] );
        }else if (Config.typeOfAttributes[i].equals("real")){
            rep[i].printNotNorm ( fout, Config.attBounds[i][0], Config.attBounds[i][1] );
        }
    }
    }catch(Exception e){
        System.out.println ("Exception when printing the attribute: "+i);
        e.printStackTrace();
    }
    fout.print ("\t "+(String)Config.classConv.elementAt(action));

	if (parameters!=null) parameters.print(fout);
  }

   

} // END OF CLASS Classifier





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




public class Representation {
/**
 * <p>
 * A classifer can contain three types of representations: ternary
 * representatiton (each alelle can take 3 possible values, 0, 1 or don't
 * care), real representation, where each alelle can take any real value,
 * and the mixed one, that can take character or real representation for
 * each alelle. In fact, the real representation is the mixed representation
 * with all the alelles being reals.
 * </p>
 */
  ///////////////////////////////////////
  // attributes

/**
 * <p>
 * Represents the action of the classifier. 
 * </p>
 */
    private int action; 


   ///////////////////////////////////////
   // associations

/**
 * <p>
 * It is an array of references to attributes
 * </p>
 */
    private Attribute rep[]; 



/**
 * <p>
 * It creates a new representation with the specified condition and a
 * random action.
 * </p>
 * @param envState is the environtment state.
 */
  public  Representation(double[] envState) {        
    int i = 0;
    rep = new Attribute [Config.clLength];

    if (Config.ternaryRep){  
        for (i=0; i<rep.length; i++)	rep[i] = new TernaryRep(envState[i]);
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("character"))		
                rep[i] = new TernaryRep(envState[i]);
            else if (Config.typeOfAttributes[i].equals("integer"))	
                rep[i] = new IntegerRep(envState[i],i);
            else
                rep[i] = new RealRep(envState[i]);
        }	
    }
    action = (int) (Config.rand() * (double) Config.numberOfActions);
    if (action == Config.numberOfActions) action --;
  } // end Representation        



/**
 * <p>
 * It creates a new representation with the specified condition and action.
 * </p>
 * <p>
 * @param envState is the environment state
 * @param act is the action that the classifier has to take. 
 * </p>
 */
  public  Representation(double[] envState, int act) {        
    int i = 0;
    rep = new Attribute [Config.clLength];

    if (Config.ternaryRep){  // It's a ternary representation. We have to create an array.
        for (i=0; i<rep.length; i++){
                rep[i] = new TernaryRep(envState[i]);
        }
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("character"))		
                    rep[i] = new TernaryRep(envState[i]);
            else if (Config.typeOfAttributes[i].equals("integer"))	
                    rep[i] = new IntegerRep(envState[i],i);
            else    rep[i] = new RealRep(envState[i]);	
        }	
    }
    action = act;
  } // end Representation        




/**
 * <p>
 * It creates a new representation that is a clone of the representation
 * passed. 
 * </p>
 * @param r is the representation that has to be cloned.
 */
  public  Representation(Representation r) {      
    int i = 0;

    rep = new Attribute[Config.clLength];
    if (Config.ternaryRep){  // It's a ternary representation. We have to create an array.
        for (i=0; i<rep.length; i++){
                rep[i] = new TernaryRep((TernaryRep)r.rep[i]);
        }
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("character")){
                    rep[i] = new TernaryRep((TernaryRep)r.rep[i]);
            }else if (Config.typeOfAttributes[i].equals("integer")){
                    rep[i] = new IntegerRep((IntegerRep)r.rep[i]);
            }else{
                    rep[i] = new RealRep((RealRep)r.rep[i]);
            }
        }
    }
    action = r.action;
  } //end Representation





/**
 * <p>
 * It creates a new representation that is a clone of the representation
 * passed. 
 * </p>
 * @param t is an String that contains the representation that has to be cloned.
 */
  public  Representation(StringTokenizer t) {      
    int i = 0;
    rep = new Attribute[Config.clLength];

    if (Config.ternaryRep){  // It's a ternary representation. We have to create an array.
        for (i=0; i<rep.length; i++){
            rep[i] = new TernaryRep( t.nextToken().charAt(0));
        }
    }
    else{ 
        for (i=0; i<rep.length; i++){
            if (Config.typeOfAttributes[i].equals("character")){
                rep[i] = new TernaryRep (t.nextToken().charAt(0));	
            }
            else if (Config.typeOfAttributes[i].equals("integer")){
                rep[i] = new IntegerRep ( (int) (new Double(t.nextToken())).doubleValue(), (int) (new Double(t.nextToken())).doubleValue(), i );
            }
            else{
                rep[i] = new RealRep ( (new Double(t.nextToken())).doubleValue(), (new Double(t.nextToken())).doubleValue());
            }	
        }	
    }
    action = new Integer(t.nextToken()).intValue();
  } // end Representation




/**
 * <p>
 * Sets the action passed as a parameter.
 * </p>
 * @param act is the action to be set.
 */
  public void setAction(int act) {        
    action = act;
  } // end setAction        




/**
 * <p>
 * It returns the action of the current classifier.
 * </p>
 * @return a int with the action
 */
  public int getAction() {        
    return action;
  } // end getAction        



/**
 * <p>
 * It returns the generality of the classifier.
 * </p>
 * @return a double with the generality of this classifier representation. 
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
 * Mutates the classifier. It mutates the action and the condition,
 * according to the probability of mutation.
 * </p>
 * return a boolean indicating if the action has been mutated.
 */
  public boolean mutate(double[] currentState) {        
    int i=0;

    for (i=0; i<rep.length; i++){
            rep[i].mutate(currentState[i]);
    }
    
    //Now, the action has to be mutated. 
    return mutateAction();
  } // end mutate    




/**
 * <p>
 * Mutates the action of the classifier.
 * </p>
 * return a boolean indicating if the action has been mutated.
 */
  public boolean mutateAction() {    
    int act = 0;
    if (Config.rand() < Config.pM){
            do{
                    act = (int)(Config.rand() * (double)Config.numberOfActions);
            }while (action == act);
            action = act;
    return true;
    }
    return false;
  } // end mutateAction    



/**
 * <p>
 * Returns the value of the alelle
 * </p>
 * @param i is the position.
 * @return a real with the value of the lower allele
 */
    public double getLowerAllele(int i) {        
        return rep[i].getLowerAllele();
    } // end getLowerAllele        

/**
 * <p>
 * Returns the value of the alelle
 * </p>
 * @param i is the position.
 * @return a real with the value of the lower allele
 */
    public double getUpperAllele(int i) {        
        return rep[i].getUpperAllele();
    } // end getUpperAllele        




/**
 * <p>
 * Sets the allele value
 * </p>
 * @param i is the position.
 * @param lowerValue is the lower value that has to be set.
 * @param upperValue is the upper value that has to be set.
 */
    public void setAllele(int i, double lowerValue, double upperValue) {        
	rep[i].setAllele(lowerValue,upperValue);	
    } // end setAllele        


/**
 * <p>
 * Sets the allele value
 * </p>
 * @param i is the position.
 * @param r is the representation of the classifier that has to be copied.
 */
  public void setAllele(int i, Representation r) {        
    rep[i].setAllele(r.rep[i]);  
  } // end setAllele    



/**
 * <p>
 * Sets the allele value
 * </p>
 * @param i is the position.
 * @param val is the character value.
 */
    public void setAllele(int i, char val){
    	rep[i].setAllele((double)val, (double) val);
    }


/**
 * <p>
 * It crosses a real allele within two parents. If the representation is 
 * a ternary representation, a crossover within intervals is not possible
 * because there is only one gene in each position. So, in this case, the gene
 * of the second parent will be copied. 
 * In case of being a real representation, a random number is generated to 
 * decide where to cross the interval. It it is crossed within the interval, 
 * the crossAllele method will make it. 
 * </p>
 * @param i is the allele that has to be crossed.
 * @param parent1 is the attribute object of the first parent. 
 * @param parent2 is the attribute object of the second parent. 
 */

   public void crossAllele(int i, Representation parent1, Representation parent2){
   	if (Config.ternaryRep){
   		rep[i].setAllele(parent2.rep[i]);
   	}
   	else{
   		if (Config.typeOfAttributes[i].equals("character")){
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
   	
   }


/**
 * <p>
 * Changes all the don't care symbols by the state in the environment, with Pspecify probability
 * </p>
 * @param env is the environment.
 */
    public void makeSpecify (double []env){
	int i = 0;
    
	for (i=0; i<rep.length; i++){
  		rep[i].makeSpecify(env[i]);
    	}
    }



/**
 * <p>
 * Returns true if the allele matches with the environment
 * </p>
 * @param env is the value of the environment.
 * @return a boolean indicating if the allele matches with the environmental value.
 */
   public boolean match (double[] env){

   	for (int i=0; i<rep.length; i++){
		if (!rep[i].match(env[i])) return false;
	}
		return true;

   	
  } // end match






/**
 * <p>
 * Indicates if the classifier of the class is equal to the classifier
 * passed.
 * </p>
 * @return a boolean indicating if they are equals.
 * @param clRep is the representation of the classifier.
 */
    public boolean equals(Representation clRep) {        
        try{
        	
        	for (int i=0; i<rep.length; i++){
        		if (! rep[i].equals (clRep.rep[i]) ) return false;
        	}
        	return true;
        
	}catch (Exception e){
		return false;
	}
    } // end equals




/**
 * <p>
 * Returns true if the current representation subsumes the representation passed as a parameter
 * </p>
 * @param r is the representation of the subsumer.
 * @return a boolean indicating if the classifier subsumes.
 */


   public boolean subsumes (Representation r){
   	if (action != r.action) return false;
   	
	for (int i =0; i < rep.length; i++){
   		if (! rep[i].subsumes(r.rep[i]) ) return false;		
   	}
   	return true;
  } // end subusmes




/**
 * <p>
 * Returns the number of don't care symbols in the 
 * classifier. It is used by action set subsumption
 * </p>
 * @return a double with the number of don't care symbols.
 */
    public double numberOfDontCareSymbols(){
   	double num = 0.;
   	
	for (int i=0; i<rep.length; i++){
   		num += rep[i].isDontCareSymbol(); 
   	}
    	
	return num;
    } // end numberOfDontCareSymbols




/**
 * <p>
 * Indicate if the current representation is more general than 
 * the representation passed as a parameter. 
 * </p>
 * @param r is the representation to which the current representation
 * is compared.
 * @return a boolean indicating if the current representation is more general.
 */
    public boolean isMoreGeneral(Representation r){
    	boolean ret = false;
    	// We need to check the condition for the "insertInPSubsumingCl	
	if (action != r.action) return false;
    	    	   	
    	if (numberOfDontCareSymbols() >= r.numberOfDontCareSymbols()){
		for (int i=0; i<rep.length;i++){
			if (!rep[i].isMoreGeneral(r.rep[i])) return false;
		}
		return true;
    	}
    	
 	return false;
    }





   public void print (){
   	System.out.print ("\t Act: "+action);
   	System.out.print ("\t Cond: ");
   	
   	System.out.println (" ");
   	for (int i=0; i<rep.length; i++){
   		rep[i].print();
	}
   
    }//end print 

 

/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the output file.
*/

   public void printNotNorm (PrintWriter fout){
  	int i=0;
	fout.print(" ");	
	try{
	for (i=0; i<rep.length; i++){
		if (Config.typeOfAttributes[i].equals("enumerate")){
			rep[i].printNotNorm(fout, Config.enumConv[i]);
		}else if (Config.typeOfAttributes[i].equals("integer")){
			if (Config.enumConv[i].size() > 0)
				rep[i].printNotNorm(fout, Config.enumConv[i]);
			else
				rep[i].printNotNorm(fout, (int) Config.attBounds[i][0]);
		}else if (Config.typeOfAttributes[i].equals("real")){
			rep[i].printNotNorm(fout, Config.attBounds[i][0], Config.attBounds[i][1]);
		}
	
	}
	}catch(Exception e){
		System.out.println ("Exception when printing the attribute: "+i);
		e.printStackTrace();	
	}
	fout.print ("\t "+(String)Config.classConv.elementAt(action));
    }//end print 



/**
 * <p>
 * Prints the classifier to the specified file.
 * </p>
 * @param fout is the output file.
*/

   public void print (PrintWriter fout){
  	
	fout.print(" ");	
	for (int i=0; i<rep.length; i++){
		rep[i].print(fout);
	}
	
	fout.print ("\t "+action);
    }//end print 



/**
 * <p>
 * It draws the population to a file. A character allele is
 * drawn as 1 o 0. Otherwise, a real allele is drawn in ten 
 * points, that represent the interval [0..1] divided in ten
 * fragments. In each fragment, three types of symbol are possible:
 *	. --> The fragment is not covered by the classifier.
 *	o --> The fragment is partially covered by the classifier. 
 *	O --> The fragment is totally covered by the classifier.
 * 
 * This notation has been obtained from Wilson2000 XCSR
 * </p>
 * @param fout is the file where the population has to be drawn.
 */
    public void draw(PrintWriter fout) {        
   	double lower=0,upper=0;
   	if (Config.ternaryRep){
   		fout.print (" "+ ((char)rep[0].getAllele())+" ");
   		for (int i=1; i<rep.length; i++){
   			fout.print ("| "+((char)rep[i].getAllele())+" ");
   		}
   	}
   	else{
   		if (Config.typeOfAttributes[0].equals("character")){
   			fout.print (" "+((char)rep[0].getAllele())+" ");
   		}
   		else{
   			printInterval(fout,rep[0].getLowerAllele(),rep[0].getUpperAllele());
   			
   		}
   		for (int i=1; i<rep.length; i++){
   			if (Config.typeOfAttributes[i].equals("character")){
   				fout.print ("| "+((char)rep[i].getAllele())+" ");
   			}
   			else if (Config.typeOfAttributes[i].equals("integer")){
   				printInterval(i,fout,((int) rep[i].getLowerAllele()), ((int)rep[i].getUpperAllele()));	
   			}
   			else{
   				printInterval(fout,rep[i].getLowerAllele(),rep[i].getUpperAllele());
   			}
		}
	}
	fout.print ("\t "+action);
    } //end draw



/**
 * It draws a real interval
 * @param fout is a reference to the file where the
 * classifier has to be drawn.
 * @param lower is the lower value of the interval
 * @param upper is the upper value of the interval.
 */
    private void printInterval(PrintWriter fout,double lower, double upper){
	double aux = 0.0;
	int points = Config.realDrawnPrecision;
	double inc = 1. / (double)points;
   	for (double i=0.; i<(double)points; i++){
   		if ( i*inc < lower && (i*inc + inc) <= lower){ 
   			fout.print (".");	
   		}
   		else if(i*inc < lower && (i*inc + inc) > lower){
   			fout.print ("o");	
   		} // In the next if, the value is bigger than the lower
   		else if(i*inc < upper && (i*inc + inc) <= upper){
   			fout.print ("O");
   		}
   		else if(i*inc < upper && (i*inc + inc) > upper){
   			fout.print("o");
   		}
   		else if (i*inc >=lower){
   			fout.print(".");
   		}
   		else{
   			fout.print ("ERR. Case not covered!!");	
   		}
   				
   	}
    	fout.print (" | ");
    }


/**
 * It draws an integer interval
 * @param fout is a reference to the file where the
 * classifier has to be drawn.
 * @param numInterval is the position of the interval in the 
 * classifier.
 * @param lower is the lower value of the interval
 * @param upper is the upper value of the interval.
 */
    private void printInterval(int numInterval,PrintWriter fout,int lower, int upper){
	double aux = 0.0;
	int points = Config.realDrawnPrecision;
	int inc = ((int)Config.attBounds[numInterval][1] - (int)Config.attBounds[numInterval][0] + 1) / points;
   	for (int i=0; i<points; i++){
   		if (i*inc >=lower && i*inc <=upper){
   			fout.print ("O");
   		}
   		else{
   			fout.print(".");
   		}
   		
   				
   	}
    	fout.print (" | ");
    }



} // end Representation





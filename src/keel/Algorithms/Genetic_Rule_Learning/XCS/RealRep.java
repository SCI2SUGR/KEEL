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



public class RealRep implements Attribute{
/**
 * <p>
 * It's the real lower-upper bound representation. It's not unordered, so, when changing interval values 
 * (mutation, crossover, etc., the consistence of interval has to be verified (lowerBound <= upperBound)
 * </p>
 */
	
/**
 * <p>
 * Represents the lower bound of the representation.
 * </p>
 */
    private double lowerBound; 

/**
 * <p>
 * Represents the upper bound of the representation.
 * </p>
 */
    private double upperBound; 

/**
  * <p>
  * It is a constant that contains the lower limit of the real
  * representation.
  * </p>
  */
    private final int minimumValue = 0;
   
   
   /**
  * <p>
  * It is a constant that contains the upper limit of the real
  * representation.
  * </p>
  */ 
    private final int maximumValue = 1;


/**
 * <p>
 * A reference to the mutation operator.
 * </p>
 */
    private RealMutation realMutation; 



/**
 * <p>
 * It's the default constructor of the class. 
 * </p>
 */
    public  RealRep() {        
    	if (Config.typeOfMutation.toLowerCase().equals("niched")){
		realMutation = new RNichedMutation();
	}        	
	else{
		realMutation = new RFreeMutation();
	}
    } // end RealRep      



/**
 * <p>
 * Does construct a real representation allele from the environmental state
 * </p>
 * @param env is the environmental attribute state 
 */
    public  RealRep(double env) {        
   
	if (env == -1.){
		lowerBound = 0.5 - Config.rand() * Config.r_0;
		upperBound = 0.5 + Config.rand() * Config.r_0;
	}
	else{
		lowerBound = env - Config.rand() * Config.r_0;
		upperBound = env + Config.rand() * Config.r_0;
  	}
  	
  	
  	if (lowerBound < minimumValue) lowerBound = minimumValue;
	if (upperBound > maximumValue) upperBound = maximumValue;
	
   
   	// A new mutation operator is declared
    	if (Config.typeOfMutation.toLowerCase().equals("niched")){
		realMutation = new RNichedMutation();
	}        	
	else{
		realMutation = new RFreeMutation();
	}
    } // end RealRep      






/**
 * <p>
 * It is the constructor of the class. It initializes the lower and upper bound values. 
 * </p>
 * @param lValue is the lower bound value.
 * @param rValue is the upper bound value.
 */
    public  RealRep(double lValue, double rValue) {        

        if (lValue >= minimumValue) lowerBound = lValue;
        else 				lowerBound = minimumValue;        
        
        if (rValue <= maximumValue) upperBound = rValue;
        else 				upperBound = maximumValue;

	if (Config.typeOfMutation.toLowerCase().equals("niched")){
		realMutation = new RNichedMutation();
	}        	
	else{
		realMutation = new RFreeMutation();
	}
        
    } // end RealRep      
    

/**
 * <p>
 * It's a constructor for the class. It clones the realRep 
 * object passed as a parameter.
 * </p>
 * @param r is the real representation that has to be copied in the constructor.
 */
    public  RealRep(Attribute r) {        
        lowerBound = ((RealRep)r).lowerBound;
        upperBound = ((RealRep)r).upperBound;
        
        if (Config.typeOfMutation.toLowerCase().equals("niched")){
		realMutation = new RNichedMutation();
	}        	
	else{
		realMutation = new RFreeMutation();
	}
    } // end RealRep      

    
    
/**
 * <p>
 * Sets the lower and upper values.
 * </p>
 * @param lValue is the  lower bound value.
 * @param rValue is the upper bound value.
 */
    public  void setAllele(double lValue, double rValue) {        
        if (lValue >= minimumValue) lowerBound = lValue;
        else 				lowerBound = minimumValue;        
        
        if (rValue <= maximumValue) upperBound = rValue;
        else 				upperBound = maximumValue;
    } // end setAllele   
    

   
/**
 * <p>
 * Sets the lower and upper values.
 * @param r is the realRep of the classifier that has to be copied.
 */
    public  void setAllele(Attribute r) {        
        lowerBound = ((RealRep)r).lowerBound;
        upperBound = ((RealRep)r).upperBound;
    } // end setAllele   
    


/**
 * <p>
 * Returns the value of the alelle
 * </p>
 * @return a 2RealRep with the two reals numbers of the representation.
 */
    public Attribute getAttributeAllele() { 
        return this;
    } // end getAllele        

  

/**
 * <p>
 * Returns the lower real value of the representation
 * </p>
 * @return a double with the lower value.
 */
    public double getLowerAllele() {        
        return lowerBound;
    } // end getLowerAllele     
    
    
    
/**
 * <p>
 * Returns the upper real value of the representation
 * </p>
 * @return a double with the upper value.
 */
    public double getUpperAllele() {        
       return upperBound;
    } // end getUpperAllele     


/**
 * <p>
 * It returns the generality of the allele.
 * </p>
 * @return a double with the generality of this allele. 
 */
    public double getGenerality() {        
        return upperBound - lowerBound;
    } // end getGenerality


/**
 * <p>
 * Verifies if the interval is correct (if the upper value
 * is greater or equal than the lower value). If it is not, it
 * interchanges its values. 
 * </p>
 */
    public void verifyInterval(){
    	if (lowerBound > upperBound){
    		double aux = upperBound;
    		upperBound = lowerBound;
    		lowerBound = aux;
    	}	
    }


/**
 * <p>
 * If the random number generated is less than the specify probability 
 * (parameter in the Config), the interval is specified with
 * an uniform distribution [0..l_0]
 * </p>
 * @param env is the environment.
 */
    public void makeSpecify (double env){
    	
	if (Config.rand() <= Config.Pspecify ){
		lowerBound += Config.rand() * Config.l_0; 
		if (lowerBound > env && env != -1.) lowerBound = env;
	}
	if (Config.rand() <= Config.Pspecify ){
		upperBound -= Config.rand() * Config.l_0;
		if (upperBound < env && env != -1.) upperBound = env; 
	}
    
    }



/**
 * <p>
 * Mutates the 2 reals contained in the representation.
 * </p>
 */
    public void mutate(double currentState) {        
        double tmp = 0.0;
        
	lowerBound = realMutation.mutateLower(lowerBound,upperBound,currentState);
	if (lowerBound < minimumValue) lowerBound = minimumValue;
	if (lowerBound > upperBound) swapAlleles();
	if (upperBound > maximumValue) upperBound = maximumValue;
	
	upperBound = realMutation.mutateUpper(lowerBound,upperBound,currentState);
    	if (upperBound > maximumValue) upperBound = maximumValue;
    	if (upperBound < lowerBound) swapAlleles();
    	if (lowerBound < minimumValue) lowerBound = minimumValue;
    	
    
    } // end mutate  



/**
 * <p>
 * Swaps the allele values.
 * </p>
 */
    private void swapAlleles(){
    	double tmp = upperBound;
        upperBound = lowerBound;
        lowerBound = tmp;
    
    }


/**
 * <p>
 * Returns true if the allele matches with the environment
 * </p>
 * @param env is the value of the environment.
 * @return a boolean indicating if the allele matches with the environmental value.
 */


   public boolean match (double env){
   	if (env == -1.) return true;
   	if (lowerBound > env || upperBound <env) return false;
   	return true;
  } // end match



/**
 * <p>
 * Returns true if the current representation allele subsumes the representation allele passed as a parameter
 * </p>
 * @param r is the real representation of the subsumer classifier.
 * @return a boolean indicating if the allele is subsumed.
 */


   public boolean subsumes (Attribute r){
   	if (lowerBound <= ((RealRep)r).lowerBound && upperBound >= ((RealRep)r).upperBound) return true;
   	return false;
  } // end subumes


/**
 * <p>
 * Returns true if the allele matches with the environment
 * </p>
 * @param r is the real representation of the other classifier.
 * @return a boolean indicating if the allele matches with the environmental value.
 */
  public boolean equals (Attribute r){
    if (lowerBound == ((RealRep)r).lowerBound && upperBound == ((RealRep)r).upperBound) return true;
    return false;
  } // end equals


  
/**
 * <p>
 * Returns if the real representation is a don't care symbol. It's not necessary in real representation, but it has to
 * be codified because is defined in the interface. So, it will return the generality of the interval.
 * </p>
 * @return a double that indicates if the character of the representation is a don't care symbol.
 */
    public double isDontCareSymbol(){
    	return (upperBound - lowerBound);	
    } // end isDontCareSymbol


/**
 * <p>
 * Returns if the current interval is more general than the interval given as a parameter
 * </p>
 * @param r is the real representation of the classifier. 
 * @return a boolean indicating if it's more general.
 */
    public boolean isMoreGeneral(Attribute r){
    	if (lowerBound <= ((RealRep)r).lowerBound && upperBound >= ((RealRep)r).upperBound) return true;
    	
    	return false;
    }



   public void print(){
   	String s1 = new Double(lowerBound).toString();
   	if (s1.length() > 4) s1 = s1.substring(0,4);
   	String s2 = new Double(upperBound).toString();
   	if (s2.length() > 4) s2 = s2.substring(0,4);
	System.out.print ("; "+s1+", "+ s2);
   }


   public void print (PrintWriter out){
	out.print (" "+lowerBound+" "+upperBound+" ");
   }


   
   public void printNotNorm(PrintWriter fout, double lo, double up){
   	double lb = lowerBound * (up-lo) + lo;
   	double ub = upperBound * (up-lo) + lo;
   	fout.print (" "+lb+" "+ub+" ");	
   }
	


// These methods have to be implemented because they are declared in the interface
   public double getAllele(){return 0.0;}
   public void printNotNorm(PrintWriter fout, int lo){}
   public void printNotNorm(PrintWriter fout, Vector conv){}
   
} // end 2RealRep





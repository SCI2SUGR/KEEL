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


public class IntegerRep implements Attribute{
/**
 * <p>
 * This class represents the integer representation of a gene. It works as described
 * in the XCSI article (see Wilson 2000)
 * </p>
 */
	
/**
 * <p>
 * Represents the lower bound of the representation.
 * </p>
 */
    private int lowerBound; 

/**
 * <p>
 * Represents the upper bound of the representation.
 * </p>
 */
    private int upperBound; 

/**
  * <p>
  * It is a constant that contains the lower limit of the integer
  * representation (minimum value of the attribute).
  * </p>
  */
    private int minimumValue = 0;
   
   
   /**
  * <p>
  * It is a constant that contains the upper limit of the integer
  * representation (maximum value of the attribute).
  * </p>
  */ 
    private int maximumValue = 9;

   ///////////////////////////////////////
   // associations

/**
 */
    private IntegerMutation integerMutation; 


  ///////////////////////////////////////
  // operations




/**
 * <p>
 * Does create a Integer representation for de environmental value given as a parameter
 * </p>
 * @param env is the environmental attribute state 
 */
 
  public IntegerRep (double env, int i){
    minimumValue = (int)Config.attBounds[i][0];
    maximumValue = (int)Config.attBounds[i][1];
    double scale = maximumValue - minimumValue + 1;
    if (env == -1.){
        double interval = Config.attBounds[i][0] + 
                            (Config.attBounds[i][1] - Config.attBounds[i][0])/2;
        lowerBound = (int) Math.round ((float) (interval - Config.rand() * Config.r_0) * scale);
        upperBound = (int) Math.round ((float) (interval + Config.rand() * Config.r_0) * scale);
    }
    else{
        lowerBound = (int) Math.round ((float) (env - Config.rand() * Config.r_0 * scale));
        upperBound = ((int) Math.round ((float) (env + Config.rand() * Config.r_0 * scale)));
    }


    if (lowerBound < minimumValue) lowerBound = minimumValue;
    if (upperBound > maximumValue) upperBound = maximumValue;


    // A new mutation operator is declared
    if (Config.typeOfMutation.toLowerCase().equals("niched")){
        integerMutation = new INichedMutation();
    }        	
    else{
        integerMutation = new IFreeMutation();
    }
  }//end IntegerRep



/**
 * <p>
 * It is the default constructor of the class. 
 * </p>
 * @param lValue is the under value to be set.
 * @param rValue is the upper value to be set.
 * @param i is the position of the attribute.
 */

  public  IntegerRep(int lValue, int rValue, int i) {
    minimumValue = (int)Config.attBounds[i][0];
    maximumValue = (int)Config.attBounds[i][1];
    if (lValue >= minimumValue)	lowerBound = lValue;
    else 			lowerBound = minimumValue;        

    if (rValue <= maximumValue) upperBound = rValue;
    else 			upperBound = maximumValue;

    if (Config.typeOfMutation.toLowerCase().equals("niched")){
        integerMutation = new INichedMutation();
    }        	
    else{
        integerMutation = new IFreeMutation();
    }
  } // end IntegerRep      



/**
 * <p>
 * It is the constructor of the class. It initializes the lower and upper bound values. 
 * </p>
 * @param lValue is the lower bound value.
 * @param rValue is the upper bound value.
 */
  public  IntegerRep(int lValue, int rValue,int udLimit, int upLimit){        
    minimumValue = udLimit;
    maximumValue = upLimit;

    if (lValue >= minimumValue) 	lowerBound = lValue;
    else 				lowerBound = minimumValue;        

    if (rValue <= maximumValue) 	upperBound = rValue;
    else 				upperBound = maximumValue;

    if (Config.typeOfMutation.toLowerCase().equals("niched")){
        integerMutation = new INichedMutation();
    }        	
    else{
        integerMutation = new IFreeMutation();
    }
  } // end IntegerRep      
    

/**
 * <p>
 * It is a constructor for the class. It clones the realRep 
 * object passed as a parameter.
 * @param r is the real representation that has to be copied in the constructor.
 */
  public  IntegerRep(Attribute r) {        
    lowerBound = ((IntegerRep)r).lowerBound;
    upperBound = ((IntegerRep)r).upperBound;
    minimumValue = ((IntegerRep)r).minimumValue;
    maximumValue = ((IntegerRep)r).maximumValue;

    if (Config.typeOfMutation.toLowerCase().equals("niched")){
        integerMutation = new INichedMutation();
    }        	
    else{
        integerMutation = new IFreeMutation();
    }
  } // end IntegerRep      

    
    
/**
 * <p>
 * Sets the lower and upper values.
 * </p>
 * @param lValue is the lower bound value.
 * @param rValue is the upper bound value.
 */
  public  void setAllele(double lValue, double rValue) {        
    if ((int)lValue >= minimumValue) 	lowerBound = (int)lValue;
    else 				lowerBound = minimumValue;        

    if ((int)rValue <= maximumValue) 	upperBound = (int)rValue;
    else 				upperBound = maximumValue;
  } // end setAllele   
    
    
   
   
/**
 * <p>
 * Sets the lower and upper values.
 * </p>
 * @param r is the realRep of the classifier that has to be copied.
 */
  public  void setAllele(Attribute r) {        
    lowerBound = ((IntegerRep)r).lowerBound;
    upperBound = ((IntegerRep)r).upperBound;
    minimumValue = ((IntegerRep)r).minimumValue;
    maximumValue = ((IntegerRep)r).maximumValue;
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
 * @return a int with the lower value.
 */
  public double getLowerAllele() {        
    return (double)lowerBound;
  } // end getLowerAllele     

    
    
/**
 * <p>
 * Returns the upper real value of the representation
 * </p>
 * @return a int with the upper value.
 */
  public double getUpperAllele() {        
   return (double)upperBound;
  } // end getUpperAllele     

  

/**
 * <p>
 * It returns the generality of the allele.
 * </p>
 * @return a double with the generality of this allele. 
 */
  public double  getGenerality() {        
    return ((double)(upperBound - lowerBound + 1)) / ((double)(maximumValue - minimumValue + 1));
  } // end getGenerality



/**
 * <p>
 * Verifies if the interval is correct (if the upper value
 * is greather or equal than the lower value). If it isn't, it
 * interchanges its values. 
 * </p>
 */
  public void verifyInterval(){
    if (lowerBound > upperBound){
            int aux = upperBound;
            upperBound = lowerBound;
            lowerBound = aux;
    }	
  }//end verifyInterval


/**
 * <p>
 * If the random number that is generated is less than the specify probability 
 * (parameter in the Config), the interval is specified with
 * an uniform distribution [0..l_0]
 * </p>
 * @param env is the environment.
 */
  public void makeSpecify (double env){
    double scale = maximumValue - minimumValue + 1;
    if (Config.rand() <= Config.Pspecify ){
            lowerBound += (int)((double)Config.rand() * Config.l_0 * scale); 
            if (lowerBound > (int)env && env != -1.) lowerBound = (int)env;
    }
    if (Config.rand() <= Config.Pspecify ){
            upperBound -= (int)((double)Config.rand() * Config.l_0 * scale);
            if (upperBound < (int)env && env != -1.) upperBound = (int)env; 
    }
  }//end makeSpecify



/**
 * <p>
 * Muates the 2 reals contained in the representation.
 * </p>
 */
  public void mutate(double currentState) {        
    lowerBound = integerMutation.mutateLower(lowerBound,upperBound, (int)currentState, (maximumValue-minimumValue+1));
    upperBound = integerMutation.mutateUpper(lowerBound,upperBound,(int)currentState, (maximumValue-minimumValue+1));
    if (lowerBound < minimumValue) lowerBound = minimumValue;
    else if (lowerBound > maximumValue) lowerBound = maximumValue;
    if (upperBound > maximumValue) upperBound = maximumValue;
    else if (upperBound < minimumValue) upperBound = minimumValue;
    if (lowerBound > upperBound) swapAlleles();
  } // end mutate  



/**
 * <p>
 * Swaps the allele values.
 * </p>
 */
  private void swapAlleles(){
    int tmp = upperBound;
    upperBound = lowerBound;
    lowerBound = tmp;
  }//end swapAlleles


/**
 * <p>
 * Returns true if the allele matches with the environment
 * </p>
 * @param env is the value of the environment.
 * @return a boolean indicating if the allele matches with the environmental value.
 */
  public boolean match (double env){
    if (env == -1.) return true;
    if (lowerBound > (int)env || upperBound < (int)env) return false;
    return true;
  } // end match


  
/**
 * <p>
 * Returns true if the current representation allele subsumes the representation allele passed as a parameter
 * </p>
 * @param r is the integer representation of the subsumer classifier.
 * @return a boolean indicating if the allele is subsumed.
 */
  public boolean subsumes (Attribute r){
    if (lowerBound <= ((IntegerRep)r).lowerBound && upperBound >= ((IntegerRep)r).upperBound) return true;
    return false;
  } // end subsumes


  
/**
 * <p>
 * Return true if the allele matches with the environment
 * </p>
 * @param r is the integer representation of the other classifier.
 * @return a boolean indicating if the allele matches with the environmental value.
 */
  public boolean equals (Attribute r){
    if (lowerBound == ((IntegerRep)r).lowerBound && upperBound == ((IntegerRep)r).upperBound) return true;
    return false;
  } // end equals


/**
 * <p>
 * Return if the integer of the representation. It's not necessary in integer representation, but it has to
 * be codified because is defined in the interface. So, it will return the generality of the interval.
 * is a don't care symbol
 * </p>
 * @return an integer that indicates if the character of the representation is a don't care symbol.
 */
  public double isDontCareSymbol(){
    return (double)(upperBound - lowerBound);
  } // end isDontCareSymbol


/**
 * <p>
 * Returns if the current interval is more general than the interval given as a parameter
 * </p>
 * @param r is the real representation of the classifier.
 * @return a boolean indicating if the current interval is more general.
 */
  public boolean isMoreGeneral(Attribute r){
    if (lowerBound <= ((IntegerRep)r).lowerBound && upperBound >= ((IntegerRep)r).upperBound) return true;
    return false;
  }//end isMoreGeneral

/**
 * <p>
 * This methods prints the attribute bounds
 * </p>
 */
  public void print(){
    System.out.print ("; "+lowerBound+", "+ upperBound);
  }//end print


/**
 * <p>
 * This method prints to the attribute bounds to the specified PrintWriter
 * </p>
 * @param out is the PrintWriter where to print the bounds
 */
   public void print (PrintWriter out){
	out.print (" "+lowerBound+" "+upperBound+" ");
   }//end out


   public double getAllele(){
	return 0.0;
   }

   public void printNotNorm(PrintWriter fout, Vector conv){
   	fout.println("  "+((String)conv.elementAt(lowerBound))+"  "+((String)conv.elementAt(lowerBound))+"  ");	
   }
   
   public void printNotNorm(PrintWriter fout, int lo){
   	fout.print (" "+(lowerBound+lo)+" "+(upperBound+lo)+" ");
   }
   
   
   public void printNotNorm(PrintWriter fout, double lo, double up){}
	


} // end IntegerRep





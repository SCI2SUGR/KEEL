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
* @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public class FuzzyRule {
	/** 
	* <p> 
	* Represents a fuzzy rule.  
	* 
	* </p> 
	*/
   // The consequent is codified in rule position in the BaseRule 
   public  int consequent;
   public  double weight;
   /** 
    * <p> 
    * Constructor by default. It creates a fuzzy rule with consequent 0 and weight 0. 
    * 
    * </p> 
    */
   public FuzzyRule() {
       consequent=0; weight=0;
   }
   /** 
    * <p> 
    * A constructor for a fuzzy rule, given its consequent and weight in the base rules. 
    * 
    * </p> 
    * @param c the single point of the fuzzy set.
    */
   public FuzzyRule(int a, double b) {
       consequent=a; weight=b;
   }
   /** 
    * <p> 
    * A copy constructor for fuzzy rule, given other fuzzy rule. 
    * 
    * </p> 
    * @param b to be copied.    
    */
   public FuzzyRule(FuzzyRule r) {
       consequent=r.consequent;
       weight=r.weight;
   }
   /** 
    * <p> 
    * Copies the FuzzyRule parameter over the present instance. 
    * 
    * </p> 
    * @param r a FuzzyRule object to be copied
    */
   public void set(FuzzyRule r) {
       consequent=r.consequent;
       weight=r.weight;
   }
   /** 
    * <p> 
    * Creates and returns a copy of this object.
    * 
    * </p>
    * @return a clone of this instance. 
    */    
   public FuzzyRule clone() {
       return new FuzzyRule(this);
   }
        
}


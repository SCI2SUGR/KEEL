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

package keel.Algorithms.Genetic_Rule_Learning.OIGA;


/**
 * <p>
 * Represents one rule as specified by the OIGA Algorithm
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class Rule {
	int numAttributes;
	boolean active[];
	double limits[][];
	int _class;
	
	/**
	 * <p>
	 * Default constructor. Does not allocate any memory!
	 * </p>
	 */
	public Rule(){
		numAttributes = 0;
		active = null;
		limits = null;
		_class = -1;
	}
	
	/**
	 * <p>
	 * Constructor with  the number of attributes specified
	 * </p>
	 * @param _nAtt number of attributes included in this rule
	 */
	public Rule(int _nAtt){
		numAttributes = _nAtt;
		active = new boolean[numAttributes];
		limits = new double[numAttributes][2];
		_class = -1;
		for(int i=0;i<numAttributes;i++)
			active[i] = false;
	}
	
	/**
	 * <p>
	 * Deep-copy constructor
	 * </p>
	 * @param rule original rule
	 */
	public Rule(Rule rule){
		numAttributes = rule.numAttributes;
		active = new boolean[numAttributes];
		limits = new double[numAttributes][2];
		for(int i=0;i<numAttributes;i++){
			active[i] = rule.active[i];
			limits[i][0] = rule.limits[i][0];
			limits[i][1] = rule.limits[i][1];
		}
		_class = rule._class;
	}
	
	/**
	 * Obtains the activation state of the attribute
	 * @param att selected attribute from the rule 
	 * @return True if it is active in the rule, False otherwise
	 */
	public boolean getActivation(int att){
		return active[att];
	}
	
	/**
	 * Returns the bounds of the attribute in the rule
	 * @param att selected attribute from the rule 
	 * @return both low and high limits of the attribute in the rule
	 */
	public double[] getLimits(int att){
		return limits[att];
	}
	
	
	/**
	 * <p>
	 * Returns the class associated to this rule
	 * </p>
	 * @return the consequent class of the rule
	 */
	public int getClas(){
		return _class;
	}
	
	/**
	 * Sets the activation of the attribute
	 * @param att attribute selected from the rule
	 * @param act if this attribute will be used or not
	 */
	public void setActivation(int att,boolean act){
		active[att] = act;
	}
	
	/**
	 * Sets the new limits of the attribute in the rule
	 * @param att attribute selected in the rule
	 * @param min new min limit for the attribute
	 * @param max new max limit for the attribute
	 */
	public void setLimits(int att,double min, double max){
		limits[att][0] = min;
		limits[att][1] = max;
	}
	
	/**
	 * Sets the consequent of the rule
	 * @param _c new class consequent of the rule
	 */
	public void setClass(int _c){
		_class = _c;
	}
	
	/**
	 * Allocates new memory for the number of attributes specified (delete
	 * previous memory). The rules will be reset after this!
	 * @param numAtt new number of attributes of the rule
	 */
	public void setLength(int numAtt){
		numAttributes = numAtt;
		active = new boolean[numAttributes];
		limits = new double[numAttributes][2];
		for(int i=0;i<numAttributes;i++)
			active[i] = false;
	}
	
	/**
	 * Evaluates the inputs and output the class if covered, -1 if not
	 * @param inputs input data of the data set to be classified
	 * @return the class obtained if input data is covered by the rule, -1 in other case
	 */
	public int evaluate(double inputs[]){
		boolean supported = true; //if the inputs are covered by the rule
		boolean contributingRule = false; //at least one attribute must be valid to cast a vote
		for(int i=0;i<numAttributes && supported;i++){
			if(active[i] && limits[i][0]<=limits[i][1]){//use this attribute?
				contributingRule = true;
				if(limits[i][0]>inputs[Oiga.attributeOrder[i]] || inputs[Oiga.attributeOrder[i]]>limits[i][1])
					supported = false;
			}
		}
		if(supported && contributingRule)
			return _class;
		else
			return -1;
	}
	
	/**
	 * Adds all the antecedents attributes of the rule s to this rule (to perform the IGA)
	 * @param s all the attributes of this rule will be appended to the present one
	 */
	public void append(Rule s){
		boolean prevActive[] = active;
		double prevlimits[][] = limits;
		        
		active = new boolean[numAttributes+s.numAttributes];
		limits = new double[numAttributes+s.numAttributes][2];
		for(int i=0;i<numAttributes;i++){
			active[i] = prevActive[i];
			limits[i][0] = prevlimits[i][0];
			limits[i][1] = prevlimits[i][1];
		}
		for(int i=numAttributes,j=0;i<numAttributes+s.numAttributes;i++,j++){
			active[i] = s.active[j];
			limits[i][0] = s.limits[j][0];
			limits[i][1] = s.limits[j][1];
		}
		this.numAttributes += s.numAttributes;
	}
	
}


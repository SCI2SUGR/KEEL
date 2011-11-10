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
 *
 * File: Rule.java
 *
 * Auxiliary class to repressent rules for the INNER algorithm
 *
 * @author Written by Joaquin Derrac (University of Granada) 17/10/2009
 * @version 1.1
 * @since JDK1.5
 *
 */
package keel.Algorithms.Hyperrectangles.INNER;

import java.util.Arrays;
import java.util.Comparator;

import keel.Dataset.Attribute;

public class Rule implements Comparator{
	
	private static int problemSize;   //attributes of the problem
	private static int nValues[];   //cardinality of each attribute (real==-1)
	private static int nClasses;   //number of classes
	private static boolean isNominal[]; //for nominal attributes
	private static double defaultImpurityLevelL[]; //left boundary of the impurity level
	private static double defaultImpurityLevelR[]; //right boundary of the impurity level
	
	private static double trainingData[][]; //copy of the training data
	private static int trainingOutput[];    //copy of the training output
	private static int nInstances[];        //number of instances in each class
	
	private double valueMin[];  //for numeric attributes
	private double valueMax[];  //for numeric attributes
	private double differenceTable[][];  //for nominal attributes
	private boolean dontCare[]; // conditions pruned
	
	private int size;   //size of the rule
	private int output; //output attribute
	
	private double impurity; // level of impurity
	
  /**
     * Sets the size of the problem
     *
     * @param value Number of attributes of the rule
     *
     */
	public static void setSize(int value){
		
		problemSize=value;
		
		isNominal=new boolean[problemSize];
		nValues=new int[problemSize];
		
	}//end-method

    /**
     * Test which attributes are nominal
     *
     * @param inputs Attributes' descriptions
     *
     */
	public static void setAttributes(Attribute[] inputs){
		
		for(int i=0;i<problemSize;i++){
			if(inputs[i].getType()==Attribute.NOMINAL){
				isNominal[i]=true;
			}
			else{
				isNominal[i]=false;				
			}
		}
		
	}//end-method

	/**
     * Sets the number of different values for an attribute
     *
     * @param value Number of values
     * @param pos Index of the attribute
     *
     */
	public static void setNumValue(int value,int pos){
		
		nValues[pos]=value;
		
	}//end-method
	
	/**
     * Sets the number of classes of the problem
     *
     * @param value Number of classes
     *
     */	
	public static void setNClasses(int value){
		
		nClasses=value;
		
	}//end-method

    /**
     * Returns the output class of the rule
     *
     * @return Output class of the rule
     */
	public int getOutput(){

		return output;

	}//end-method
	
	/**
     * Copies the training data iniside the class
     *
     * @param trainData training data Training data
     * @param trainOutput training output Output values
     * 
     */		
	public static void copyData(double trainData [][], int trainOutput []){
		
		trainingData=trainData;
		trainingOutput=trainOutput;
	}//end-method
	
	/**
     * Sets the array of instances of each class
     *
     * @param vector Array of instances of each class
     *
     */		
	public static void setNInstances(int vector []){
		
		nInstances=vector;
		
	}//end-method

	/**
	* Computes the initial confidence intervals of the impurity levels of each class 
	*/
	public static void computeDefaultImpurityLevels(){
		
		double mean, rest, div;
		double p;
		double n;
		double z;
		double up, low;
		
		defaultImpurityLevelL= new double[nClasses];
		defaultImpurityLevelR= new double[nClasses];
		z=1.96; //alpha=0.95 in a normal table   
		
		n=(double)trainingData.length;
		
		div=1.0+(z*z/n);
		
		for(int i=0;i<nClasses;i++){
			
			p=((double)nInstances[i]/n);
			
			mean=p+(z*z/(2*n));
			
			rest=z*Math.sqrt(((p*(1.0-p))/n)+((z*z)/(4*n*n)));
			
			low=mean-rest;
			up=mean+rest;
			defaultImpurityLevelL[i]=low/div;
			defaultImpurityLevelR[i]=up/div;
		}
	}//end-method
	
	/**
     * Default builder. Generates a void rule
     */
	public Rule(){
		
		valueMin=new double[problemSize];
		valueMax=new double[problemSize];
		dontCare=new boolean[problemSize];
		
		differenceTable=new double [problemSize][];
		
		for(int i=0;i<problemSize;i++){
			differenceTable[i]=new double[nValues[i]];
		}
		
		size=problemSize;
		output=-1;
		
		impurity=-1.0;

	}//end-method

    /**
     * Builder. Generates a rule covering only a point
     *
     * @param instance Basic instance
     * @param out Ouput of the instance
     */
	public Rule(double instance[],int out){
		
		valueMin=new double[problemSize];
		valueMax=new double[problemSize];
		dontCare=new boolean[problemSize];
		
		Arrays.fill(dontCare, false);
		
		differenceTable=new double [problemSize][];
		
		for(int i=0;i<problemSize;i++){
			differenceTable[i]=new double[nValues[i]];
		}
		
		for(int i=0;i<instance.length;i++){
			
			if(isNominal[i]){
				Arrays.fill(differenceTable[i], 1);
				differenceTable[i][(int)instance[i]]=0;			
			}else{
				valueMin[i]=instance[i];
				valueMax[i]=instance[i];			
			}
			
		}
		
		size=problemSize;
		output=out;
		
		computeInitialImpurity();

	}//end-method

    /**
	* Computes the initial impurity level of a rule
	*/
	private void computeInitialImpurity(){
			
		double mean, rest, div;
		double p;
		double n;
		double z;
		double up, low;
		
		z=1.96; //alpha=0.95 in a normal table   
		
		n=1.0;
		
		div=1.0+(z*z/n);
		
		p=1.0;
			
		mean=p+(z*z/(2*n));
			
		rest=z*Math.sqrt(((p*(1.0-p))/n)+((z*z)/(4*n*n)));
			
		low=mean-rest;
		up=mean+rest;
		low=low/div;
		up=up/div;
		
		impurity= 100.0 * ((defaultImpurityLevelR[output]-low)/(up-low));
		
	}//end-method

	/**
	* Computes the distance between two rules
	*
	* @param another Second rule
	*
	* @return Distance between the two rules
	*/
	public double ruleDistance (Rule another){
		
		double dist=0.0;
		double value;
		
		for(int i=0;i<problemSize;i++){
			
			if(!dontCare[i]){
				if(isNominal[i]){
					value=ruleNominalDistance(differenceTable[i],another.differenceTable[i]);
				}
				else{
					value=ruleRealDistance(valueMin[i],valueMax[i],another.valueMin[i],another.valueMax[i]);
				}
				
				value=value*value;
				dist+=value;
			}
		}
		
		dist=Math.sqrt(dist);

		return dist;
		
	}//end-method
	
	/**
	* Computes the distance between a real attribute of two rules
	*
	* @param aMin Lower bound of the first rule
	* @param aMax Upper bound of the first rule
	* @param bMin Lower bound of the second rule
	* @param bMax Upper bound of the second rule
	*
	* @return Distance between the two attributes
	*/	
	private double ruleRealDistance(double aMin,double aMax,double bMin,double bMax){
		
		double dist=0.0;
		
		if(aMin<bMin){
			if(bMin>aMax){
				dist=bMin-aMax;
			}
		}
		
		if(aMin>bMin){
			if(bMax<aMin){
				dist=aMin-bMax;
			}
		}
		
		return dist;
		
	}//end-method
	
	/**
	* Computes the distance between a nominal attribute of two rules
	*
	* @param tableA Difference table of the first rule
	* @param tableB Difference table of the secoind rule
	*
	* @return Distance between the two attributes
	*/
	private double ruleNominalDistance(double tableA[],double tableB[]){
		
		double dist=0.0;
		double max=-1.0;
		
		for(int i=0;i<tableA.length;i++){
			
			if(tableB[i]!=1){
				if(tableA[i]>max){
					max=tableA[i];
				}
			}
		}

		if(max==-1.0){
			dist=Double.MAX_VALUE;
		}
		else{
			dist=max;
		}
		
		return dist;
		
	}//end-method
	
	/**
	* Computes the distance between a rule and an instance
	*
	* @param example The instance
	*
	* @return Distance between the rule and the instance
	*/	
	public double distance (double example[]){
		
		double dist=0.0;
		double value;
		
		for(int i=0;i<example.length;i++){
			
			if(!dontCare[i]){
				if(isNominal[i]){
					value=nominalDistance(i,example[i]);
				}
				else{
					value=realDistance(i,example[i]);
				}
				
				value=value*value;
				dist+=value;
			}
		}
		
		dist=Math.sqrt(dist);

		return dist;
		
	}//end-method
	
	/**
	* Computes the distance between a rule and an instance in a real valued attribute
	*
	* @param att Attribute index
	* @param value Value of the instance's attribute
	*
	* @return Distance between the rule and the instance
	*/	
	private double realDistance(int att, double value){
			
		if(value<valueMin[att]){
			return valueMin[att]-value;
		}
		if(value>valueMax[att]){
			return value-valueMax[att];
		}
		
		return 0.0;	
		
	}//end-method
	
	/**
	* Computes the distance between a rule and an instance in a nominal valued attribute
	*
	* @param att Attribute index
	* @param value Value of the instance's attribute
	*
	* @return Distance between the rule and the instance
	*/		
	private double nominalDistance(int att, double value){
			
		
		if(differenceTable[att][(int)value]==0.0){
			return 0.0;
		}
		
		double val=Double.MAX_VALUE;
		int lower=-1;
		
		for(int i=0;i<differenceTable[att].length;i++){
			if(differenceTable[att][i]<val){
				lower=i;
			}
		}
		
		if(lower==(int)value){
			return 0.0;
		}
		
		return 1.0;
		
	}//end-method
	
	/**
	* Generalizes a continuous attribute
	*
	* @param att Attribute index
	* @param value Value to obtain
	* @param percentage Percentage of generalization allowed
	* @param out Output class of the example to be covered
	*
	*/		
	public void generalizeContinuous(int att,double value, double percentage, int out){

		if(out==output){

			if(value<valueMin[att]){		
				valueMin[att]-=(valueMin[att]-value)*percentage;
				valueMin[att]=Math.max(0.0, valueMin[att]);
				
			}
			if(value>valueMax[att]){
				valueMax[att]+=(value-valueMax[att])*percentage;
				valueMax[att]=Math.min(1.0, valueMax[att]);
			}
		}
		else{

			if(value<valueMin[att]){		
				valueMin[att]+=(valueMin[att]-value)*percentage;
				valueMin[att]=Math.min(valueMax[att], valueMin[att]);
			}
			if(value>valueMax[att]){
				valueMax[att]-=(value-valueMax[att])*percentage;
				valueMin[att]=Math.max(valueMax[att], valueMin[att]);
			}
		}
	
	}//end-method
	
	/**
	* Generalizes a nominal attribute
	*
	* @param att Attribute index
	* @param value Value to obtain
	* @param percentage Percentage of generalization allowed
	* @param out Output class of the example to be covered
	*
	*/	
	public void generalizeNominal(int att,double value, double percentage, int out){
		
		double tValue = differenceTable[att][(int)value];
		double movement = percentage*(tValue+1.0);

		if(out==output){

			tValue=tValue-movement;
			tValue=Math.max(0.0,tValue);
			
		}
		else{

			tValue=tValue+movement;
			tValue=Math.min(1.0,tValue);			
					
		}
	
	}//end-method
	
	/**
	* Computes the impurity level of a rule
	*/
	public void computeImpurityLevel(){
		
		double mean, rest, div;
		double p;
		double hits;
		double n;
		double z;
		double up, low;
		
		z=1.96; //alpha=0.95 in a normal table   
		
		n=0.0;	
		hits=0.0;
		
		for(int i=0;i<trainingOutput.length;i++){
			
			if(inside(trainingData[i])){
				if(trainingOutput[i]==output){
					hits+=1.0;
				}
				n+=1.0;
			}
		}
		
		div=1.0+(z*z/n);
		if(n==0){
			impurity=Double.MAX_VALUE;
		}else{
			p=(hits/n);
				
			mean=p+(z*z/(2*n));
				
			rest=z*Math.sqrt(((p*(1.0-p))/n)+((z*z)/(4*n*n)));
				
			low=mean-rest;
			up=mean+rest;
			low=low/div;
			up=up/div;
			
			impurity= 100.0 * ((defaultImpurityLevelR[output]-low)/(up-low));
		}
		
	}//end-method
	
	/**
	* Computes the impurity level of a rule, by considering only those instances which are not already covered by other rules
	*
	* @return Impurity level
	*/	
	public double getSpecialImpurityLevel(Rule ruleset[]){
		
		double mean, rest, div;
		double p;
		double hits;
		double n;
		double z;
		double up, low;
		double newImpurity;
		boolean consider;
		
		z=1.96; //alpha=0.95 in a normal table   
		
		n=0.0;	
		hits=0.0;
		
		for(int i=0;i<trainingOutput.length;i++){
			
			if(inside(trainingData[i])){
				//special consideration
				consider=true;
				for(int j=0;j<ruleset.length&&consider;j++){
					if(ruleset[j].impurity<impurity){
						if(ruleset[j].inside(trainingData[i])){
							consider=false;
						}
					}
				}
				if(consider){
					if(trainingOutput[i]==output){
						hits+=1.0;
					}
					n+=1.0;
				}
			}
		}
		
		div=1.0+(z*z/n);
		if(n==0){
			newImpurity=Double.MAX_VALUE;
		}else{
			p=(hits/n);
				
			mean=p+(z*z/(2*n));
				
			rest=z*Math.sqrt(((p*(1.0-p))/n)+((z*z)/(4*n*n)));
				
			low=mean-rest;
			up=mean+rest;
			low=low/div;
			up=up/div;
			
			newImpurity= 100.0 * ((defaultImpurityLevelR[output]-low)/(up-low));
		}
		
		return newImpurity;
	}//end-method
	
    /**
     * Tests if an instance is covered by the rule
     *
     * @param example Instance to be tested
     * @return True if it is covered. False, if not.
     */
	public boolean inside(double example []){
		
		boolean isInside=true;
		
		for(int i=0;i<size && isInside;i++){
			if(!dontCare[i]){
				if(isNominal[i]){
					if(differenceTable[i][(int)example[i]]== 0.0){
						isInside=false;
					}
				}
				else{
					if(example[i]<valueMin[i]){
						isInside=false;
					}
					if(example[i]>valueMax[i]){
						isInside=false;
					}
				}
			}
		}
		
		return isInside;
	}//end-method

	/**
	* Returns the impurity level of a rule
	*/
	public double getImpurityLevel(){
		
		return impurity;
	}//end-method


    /**
     * Clone method
     *
     * @return A intialized copy of the rule
     */
	@Override
	public Rule clone(){

		Rule clon=new Rule();	
		
		clon.size=size;
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				System.arraycopy(differenceTable[i], 0, clon.differenceTable[i], 0, differenceTable[i].length);
			}
			else{
				clon.valueMax[i]=valueMax[i];
				clon.valueMin[i]=valueMin[i];				
			}

		}
		
		clon.output=output;
		clon.impurity=impurity;
		System.arraycopy(dontCare, 0, clon.dontCare, 0, dontCare.length);
		
		return clon;

	}//end-method

	/**
	* Prune redundant conditions of a rule, if it decreases its impurity level
	*/
	public void pruneConditions(){
		
		boolean delete=true;
		double before,after;
		
		while(delete){
			
			delete=false;
			
			for(int i=0;i<problemSize&&!delete;i++){
				
				if(!dontCare[i]){
					before=impurity;
					dontCare[i]=true;
					computeImpurityLevel();
					after=impurity;
					
					if(after<before){
						delete=true;

					}
					else{
						dontCare[i]=false;
					}
				}
			}

		}
		computeImpurityLevel();
		
	}//end-method
	
	/**
	* Test if two rules intersects
	*
	* @param another Second rule.
	*
	* @return True if the rules intersect. False, if not.
	*/
	public boolean intersect(Rule another){
		
		boolean intersect=true;

		for(int i=0;i<problemSize&&intersect;i++){
			if((!dontCare[i])&&(!another.dontCare[i])){

				if(isNominal[i]){
					for(int j=0;j<differenceTable[i].length&&intersect;j++){
						if(differenceTable[i][j]!=another.differenceTable[i][j]){
							intersect=false;
						}
					}
				}else{		
					if((valueMin[i]<another.valueMin[i])||(valueMin[i]>another.valueMax[i])){
						if((valueMax[i]<another.valueMin[i])||(valueMax[i]>another.valueMax[i])){
							intersect=false;
						}
					}
				}
			}
		}
		
		return intersect;
	}//end-method
	
	/**
	* Test the rule contains a second rule
	*
	* @param another Second rule.
	*
	* @return True if the second rule is contained. False, if not.
	*/	
	public boolean contains(Rule another){
		
		boolean inside=true;
		if(another.output!=output){
			inside=false;
		}
		
		for(int i=0;i<problemSize&&inside;i++){
			if(!dontCare[i]){
				if(another.dontCare[i]){
					inside=false;
				}
				if(isNominal[i]){
					for(int j=0;j<differenceTable[i].length&&inside;j++){
						if(differenceTable[i][j]<another.differenceTable[i][j]){
							inside=false;
						}
					}
				}else{
					
					if(another.valueMin[i]<valueMin[i]){
						inside=false;
	
					}
					if(another.valueMax[i]>valueMax[i]){
						inside=false;
					}
				}
			}
		}
		
		return inside;
	}//end-method

	/**
	* Computes the inclussion degree of two rules in a given attribute
	*
	* @param another Second rule.
	* @param att Attribute selected
	*
	* @return Degree of inclussion
	*/		
	public double inclusionDegree(Rule another, int att){
		
		double degree=0.0;
		double num, den;
		
		if(dontCare[att]||another.dontCare[att]){
			return 0.0;
		}
		if(isNominal[att]){
			
			num=0.0;
			den=0.0;
			for(int i=0;i<differenceTable[att].length;i++){
				
				num+=((1.0-differenceTable[att][i])*(1.0-another.differenceTable[att][i]));
				den+=(1.0-differenceTable[att][i]);
			}
			
			degree=num/den;
			
		}
		else{
			
			if(valueMin[att]<another.valueMin[att]){
				if(another.valueMin[att]>valueMax[att]){
					degree=(Math.min(valueMax[att], another.valueMax[att])-Math.max(valueMin[att], another.valueMin[att]))/(valueMax[att]-another.valueMin[att]);
				}
			}
			
			if(valueMin[att]>another.valueMin[att]){
				if(another.valueMax[att]<valueMin[att]){
					degree=(Math.min(valueMax[att], another.valueMax[att])-Math.max(valueMin[att], another.valueMin[att]))/(valueMax[att]-another.valueMin[att]);
				}
			}
			
			
		}
		
		degree=Math.abs(degree);
		
		return degree;
	}//end-method
	
	/**
	* Performs an extension of a rule to another
	*
	* @param another Second rule.
	*
	* @return Final Rule
	*/
	public Rule testExtension(Rule another){
		
		Rule extended;
		int selected=-1;
		
		computeImpurityLevel();
				
		extended=clone();
		
		for(int i=0;i<problemSize&&selected<0;i++){
			if(inclusionDegree(another,i)>0.0){
				selected=i;
			}
		}
		
		if(selected>=0){
			if(!isNominal[selected]){
				extended.valueMin[selected]=Math.min(extended.valueMin[selected], another.valueMin[selected]);
				extended.valueMax[selected]=Math.max(extended.valueMax[selected], another.valueMax[selected]);
			}
			else{
				for(int j=0;j<differenceTable[selected].length;j++){
					extended.differenceTable[selected][j]=Math.max(extended.differenceTable[selected][j], another.differenceTable[selected][j]);
				}
			}
		}
		else{
			for(int i=0;i<problemSize;i++){
				selected=i;
				if(!isNominal[selected]){
					extended.valueMin[selected]=Math.min(extended.valueMin[selected], another.valueMin[selected]);
					extended.valueMax[selected]=Math.max(extended.valueMax[selected], another.valueMax[selected]);
				}
				else{
					for(int j=0;j<differenceTable[selected].length;j++){
						extended.differenceTable[selected][j]=Math.max(extended.differenceTable[selected][j], another.differenceTable[selected][j]);
					}
				}
			}
		}
	
		extended.computeImpurityLevel();
		
		if(extended.impurity<=impurity){
			return extended;
		}
		
		return null;
		
	}//end-method
	
    /**
     * Equals method
     *
     * @param rul Another rule
     * @return True of both rules are equal. False, if not
     */
	@Override
	public boolean equals(Object rul) {
		
		Rule another=(Rule)rul;
		
		boolean isEqual=true;
		
		if(output!=another.output){
			isEqual=false;
		}
			
		for(int i=0;i<size && isEqual;i++){
			if(isNominal[i]){
				if(dontCare[i]!=another.dontCare[i]){
					isEqual=false;
				}
				else{
					for(int j=0;j<differenceTable[i].length&& isEqual;j++){
						if(differenceTable[i][j]!=another.differenceTable[i][j]){
							isEqual=false;
						}
					}
				}
			}
			else{
				if(valueMin[i]!=another.valueMin[i]){
					isEqual=false;
				}
				if(valueMax[i]!=another.valueMax[i]){
					isEqual=false;
				}
			}
		}
		
		return isEqual;

	}//end-method*/

    /**
     * To String method
     *
     * @return A text string representing the contents of the rule
     */
	@Override
	public String toString() {

		String text="";
		
		for(int i=0;i<size;i++){
			text+="Att"+i+": ";
			if(isNominal[i]){
				if(dontCare[i]){
					text+="TRUE ";
				}
				else{
					for(int j=0;j<differenceTable[i].length;j++){
						text+=differenceTable[i][j]+"-";
					}
				}
			}
			else{
				if(dontCare[i]){
					text+="TRUE ";
				}
				else{
					text+=valueMin[i]+"-";
					text+=valueMax[i]+" ";		
				}
			}
		}
		
		text+="Class = "+output;
		text+=" Impurity= "+impurity;
		
		return text;

	}//end-method*/
	
	/**
	* Compare two rules, regarding its impurity level
	*
	* @param o First rule.
	* @param o2 Second rule.
	*
	* @return Order of the rules
	*/
	public int compare(Object o,Object o2) {
        Rule dir = (Rule)o;
        Rule dir2 = (Rule)o2;
        if(dir.impurity > dir2.impurity)
            return -1;
        else if(dir.impurity == dir2.impurity)
            return 0;
        else
            return 1;
			
	}//end-method
	
}//end-class


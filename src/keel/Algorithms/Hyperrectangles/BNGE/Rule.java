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
 * Auxiliary class to repressent rules for the BNGE algorithm
 *
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @version 1.1
 * @since JDK1.5
 *
 */
package keel.Algorithms.Hyperrectangles.BNGE;

import keel.Dataset.Attribute;

public class Rule {
	
	private static int size;            //attributes of the rule
	private static boolean isNominal[]; //nominal attributes
	private static int nValues[];       //different values in nominal attributes
	
	private double valueMin[];          //for numeric attributes
	private double valueMax[];          //for numeric attributes
	private boolean valueNom[][];       //for nominal attributes
	
	private double area;                //area of the rule
	
	private int output;                 //output attribute
	
	/**
     * Sets the size of the rule
     *
     * @param value Number of attributes of the rule
     *
     */
	public static void setSize(int value){
		
		size=value;
		nValues=new int[size];
        
	}//end-method

    /**
     * Test which attributes are nominal
     *
     * @param inputs Attributes' descriptions
     *
     */
	public static void setAttributes(Attribute[] inputs){
		
		isNominal=new boolean[size];
		
		for(int i=0;i<size;i++){
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
     * Default builder. Generates a void rule
     */
	public Rule(){
		
		valueMin=new double[size];
		valueMax=new double[size];
		valueNom=new boolean[size][];	
		
		for(int i=0;i<size;i++){
			valueNom[i]=new boolean [nValues[i]];
			for(int j=0;j<nValues[i];j++){
				valueNom[i][j]=false;
			}
		}
			
		output=-1;
		area=0;

	}//end-method

    /**
     * Builder. Generates a rule covering only a point
     *
     * @param instance Basic instance
     * @param out Ouput of the instance
     */
	public Rule(double instance[],int out){
		
		int nomRep;
		
		valueMin=new double[size];
		valueMax=new double[size];
		valueNom=new boolean[size][];	
		
		for(int i=0;i<size;i++){
			valueNom[i]=new boolean [nValues[i]];
			for(int j=0;j<nValues[i];j++){
				valueNom[i][j]=false;
			}
		}

		for(int i=0;i<size;i++){
			if(isNominal[i]){
				nomRep=(int)(instance[i]*(nValues[i]-1));
				valueNom[i][nomRep]=true;
			}
			else{
				valueMax[i]=instance[i];
				valueMin[i]=instance[i];				
			}
		}
		
		output=out;
		
		computeArea();

	}//end-method

    /**
     * Reinitialices a rule, loading it with the contents of a single instance
     *
     * @param instance Basic instance
     * @param out Ouput of the instance
     */
	public void loadRule(double instance[],int out){
		
		int nomRep;
	
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				nomRep=(int)(instance[i]*(nValues[i]-1));
				valueNom[i][nomRep]=true;
			}
			else{
				valueMax[i]=instance[i];
				valueMin[i]=instance[i];				
			}
		}
		
		output=out;
		
		computeArea();

	}//end-method

    /**
     * Computes the area of the rule
     */
	private void computeArea(){
		
		int count;
		
		area=0.0;
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				count=0;
				for(int j=0;j<valueNom[i].length;j++){
					if(valueNom[i][j]){
						count++;
					}
				}
				area+=(double)((double)count/(double)valueNom[i].length);
			}
			else{
				area+=valueMax[i]-valueMin[i];				
			}			
		}
		
	}//end-method

    /**
     * Clone method
     *
     * @return A intialized copy of the rule
     */
	@Override
	public Rule clone(){
		
		Rule clon=new Rule();	
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				System.arraycopy(valueNom[i], 0, clon.valueNom[i], 0, nValues[i]);
			}
			else{
				clon.valueMax[i]=valueMax[i];
				clon.valueMin[i]=valueMin[i];				
			}
		}
		
		clon.output=output;
		clon.area=area;
		
		return clon;

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
		
		if(area!=another.area){
			isEqual=false;
		}
			
		for(int i=0;i<size && isEqual;i++){
			if(isNominal[i]){
				for(int j=0;j<nValues[i]&& isEqual;j++){
					if(valueNom[i][j]!=another.valueNom[i][j]){
						isEqual=false;
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

	}//end-method

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
				for(int j=0;j<nValues[i];j++){
					text+=j+",";
				}
			}
			else{
				text+=valueMin[i]+"-";
				text+=valueMax[i]+" ";				
			}
		}
		
		text+="Output= "+output;
		text+=" Area: "+area;
		
		return text;

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
     * Returns the area of the rule
     *
     * @return Area of the rule
     */
	public double getArea(){
		
		return area;

	}//end-method
	
    /**
     * Computes the distance between a given instance and the rule.
     *
     * @param instance Instance to be tested
     *
     * @return Distance computed
     */
	public double distance(double instance[]){
		
		double dist=0.0;
		double inc;
		int nomRep;
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				nomRep=(int)(instance[i]*(nValues[i]-1));
				if(valueNom[i][nomRep]==false){
					dist+=1.0;
				}
			}
			else{
				if(instance[i]<valueMin[i]){
					inc=(valueMin[i]-instance[i]);
					dist+=(inc*inc);
				}
				if(instance[i]>valueMax[i]){
					inc=(instance[i]-valueMax[i]);
					dist+=(inc*inc);
				}
			}

		}	
		
		return dist;

	}//end-method

    /**
     * Computes the distance between two rules.
     *
     * @param another Second rule to be tested
     *
     * @return Distance computed
     */
	public double distanceRule(Rule another){
		
		double dist=0.0;
		int count;
		double inc;
		double a,b;
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){ //compute the proportion of examples which differs
				count=0;
				for(int j=0;j<nValues[i];j++){
					if(valueNom[i][j]!=another.valueNom[i][j]){
						count++;
					}
				}
				inc=(double)((double)count/(double)nValues[i]);
			}
			else{ //compute distance between the centroids

				a=(valueMax[i]-valueMin[i])/2.0;
				b=(another.valueMax[i]-another.valueMin[i])/2.0;
				
				if(a>b){
					inc=a-b;
				}
				else{
					inc=b-a;
				}
			}
			dist+=inc*inc;
		}	
		
		return dist;

	}//end-method

    /**
     * Test if two rules are overlapped
     *
     * @param another Second rule to test
     *
     * @return True if the rules are overlapped. False, if not.
     */
	public boolean overlap(Rule another){
		
		boolean over=true;
		boolean test;
		
		for(int i=0;i<size && over;i++){
			if(isNominal[i]){
				test=false;
				for(int j=0;j<nValues[i]&&!test;j++){
					if((valueNom[i][j]==true)&&(another.valueNom[i][j]==true)){
						test=true;
					}
				}
				if(!test){
					over=false;
				}
			}
			else{
				test=false;
				//left overlap
				if((another.valueMax[i]>=valueMin[i])&&(another.valueMax[i]<=valueMax[i])){
					test=true;
				}
				else{
					//right overlap
					if((valueMax[i]>=another.valueMin[i])&&(valueMax[i]<=another.valueMax[i])){
						test=true;
					}							
				}
				if(!test){
					over=false;
				}	
			}
		}

		return over;

	}//end-method

    /**
     * Merge two rules
     *
     * @param another Second rule to merge
     */
	public void merge(Rule another){

		for(int i=0;i<size;i++){
			if(isNominal[i]){
				for(int j=0;j<nValues[i];j++){
					if(another.valueNom[i][j]==true){
						valueNom[i][j]=true;
					}
				}
			}
			else{
				if(another.valueMin[i]<valueMin[i]){
					valueMin[i]=another.valueMin[i];	
				}
				if(another.valueMax[i]>valueMax[i]){
					valueMax[i]=another.valueMax[i];	
				}
			}
		}

		computeArea();

	}//end-method

}//end-class



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
 * Auxiliary class to repressent rules for the RISE algorithm
 *
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @version 1.1
 * @since JDK1.5
 *
 */
package keel.Algorithms.Hyperrectangles.RISE;

import keel.Dataset.Attribute;

public class Rule {
	
	private static int size;   //attributes of the rule
	private static boolean isNominal[]; //for nominal attributes
	
	private double valueMin[];  //for numeric attributes
	private double valueMax[];  //for numeric attributes
	private double valueNom[];  //for nominal attributes

	private boolean dontCare[]; //for nominal attributes
	
	private int output;         //output attribute
	
	private double laplaceAcc;  //accuracy
	private static int S;		//SVDM parameter

	private static int ACV [][][];  //SVDM
	private static int AC [][];     //SVDM
	private static int nClasses;    //SVDM
	private static int nValues[];   //SVDM
	private static int maxValue;       //SVDM
	private static int Q;           //distance parameter
		

    /**
     * Sets the size of the rule
     *
     * @param value Number of attributes of the rule
     *
     */
	public static void setSize(int value){
		
		size=value;
		
	}//end-method

    /**
     * Sets the Q parameter
     *
     * @param value Value of the Q parameter
     *
     */
	public static void setQ(int value){
		
		Q=value;
		
	}//end-method

    /**
     * Sets the S parameter
     *
     * @param value Value of the S parameter
     *
     */
	public static void setS(int value){
		
		S=value;
		
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
     * Sets the number of classes
     *
     * @param value Number of classes
     *
     */
	public static void setNClasses(int value){
		
		nClasses=value;
		nValues=new int[size];
		maxValue=-1;

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
		
		if(maxValue<value){
			maxValue=value;
		}
		
	}//end-method

   /**
     * Sets the Laplace accuracy of the rule
     *
     * @param acc Accuracy of the rule
     */
	public void setLaplaceAcc(double acc){

		laplaceAcc=acc;

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
     * Returns the Laplace accuracy of the rule
     *
     * @return The accuracy of the rule
     */
	public double getLaplaceAcc(){

		return laplaceAcc;

	}//end-method

    /**
     * Computes the SVDM distance matrix
     *
     * @param instances Instances of the data set
     * @param outputs Output attribute of the instances
     */
	public static void loadSVDMmatrix(double instances[][],int outputs[]){
				

		ACV=new int [size][nClasses][maxValue];
		AC=new int [size][nClasses];
		int intValue;
		
		for(int i=0;i<size;i++){
			for(int j=0;j<nClasses;j++){
				for(int k=0;k<maxValue;k++){
					ACV[i][j][k]=0;
				}
				AC[i][j]=0;
			}		
		}
		for(int i=0;i<instances.length;i++){
			for(int j=0;j<instances[i].length;j++){
				
				if(isNominal[j]==true){
					intValue=(int)(instances[i][j]*(nValues[j]-1));
					
					ACV[j][outputs[i]][intValue]++;
					AC[j][outputs[i]]++;
				}
			}
		}

	}//end-method

	/**
     * Default builder. Generates a void rule
     */
	public Rule(){
		
		valueMin=new double[size];
		valueMax=new double[size];
		valueNom=new double[size];	
		
		dontCare=new boolean[size];	
		
		output=-1;
		
		laplaceAcc=-1.0;

	}//end-method

    /**
     * Builder. Generates a rule covering only a point
     *
     * @param instance Basic instance
     * @param out Ouput of the instance
     */
	public Rule(double instance[],int out){
		
		valueMin=new double[size];
		valueMax=new double[size];
		valueNom=new double[size];	
		
		dontCare=new boolean[size];		
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				valueNom[i]=instance[i];
			}
			else{
				valueMax[i]=instance[i];
				valueMin[i]=instance[i];				
			}
			dontCare[i]=false;
		}
		
		output=out;

	}//end-method

    /**
     * Reinitialices a rule, loading it with the contents of a single instance
     *
     * @param instance Basic instance
     * @param out Ouput of the instance
     */
	public void loadRule(double instance[],int out){
	
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				valueNom[i]=instance[i];
			}
			else{
				valueMax[i]=instance[i];
				valueMin[i]=instance[i];				
			}
			dontCare[i]=false;
		}
		
		output=out;

	}//end-method

    /**
     * Tests if an instance is covered by the rule
     *
     * @param instance Instance to be tested
     * @return True if it is covered. False, if not.
     */
	public boolean inside(double instance[]){
		
		boolean isInside=true;
		
		for(int i=0;i<size && isInside;i++){
			if(isNominal[i]){
				if((dontCare[i]==false)&&(instance[i]!=valueNom[i])){
					isInside=false;
				}
			}
			else{
				if(instance[i]<valueMin[i]){
					isInside=false;
				}
				if(instance[i]>valueMax[i]){
					isInside=false;
				}
			}
		}
		
		return isInside;

	}//end-method

    /**
     * Performs the most specific possible generalization over the rule to cover
     * a new instance
     *
     * @param instance Instance to be covered
     */
	public void mostSpecificGeneralization(double instance[]){
		
		for(int i=0;i<size;i++){
			if(isNominal[i]){
				if((dontCare[i]==false)&&(instance[i]!=valueNom[i])){
					dontCare[i]=true;
				}
			}
			else{
				if(instance[i]<valueMin[i]){
					valueMin[i]=instance[i];
				}
				if(instance[i]>valueMax[i]){
					valueMax[i]=instance[i];
				}
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
				clon.valueNom[i]=valueNom[i];
			}
			else{
				clon.valueMax[i]=valueMax[i];
				clon.valueMin[i]=valueMin[i];				
			}
			clon.dontCare[i]=dontCare[i];
		}
		
		clon.output=output;
		clon.laplaceAcc=laplaceAcc;
		
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
			
		for(int i=0;i<size && isEqual;i++){
			if(isNominal[i]){
				if(dontCare[i]!=another.dontCare[i]){
					isEqual=false;
				}
				else{
					if((dontCare[i]==false)&&(valueNom[i]!=another.valueNom[i])){
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
				if(dontCare[i]){
					text+="TRUE ";
				}
				else{
					text+=valueNom+" ";
				}
			}
			else{
				text+=valueMin[i]+"-";
				text+=valueMax[i]+" ";				
			}
		}
		
		text+="Class = "+output;
		text+=" Acc= "+laplaceAcc;
		
		return text;

	}//end-method

    /**
     * Computes the distance between a given instance and the rule. Employs SVDM
     * distance for nominal attributes and Euclidean distance for numerical
     * attributes.
     *
     * @param instance Instance to be tested
     *
     * @return Distance computed
     */
	public double distance(double instance[]){
		
		double dist=0.0;
		double inc;
		
		for(int i=0;i<size;i++){
			if(dontCare[i]==false){
				if(isNominal[i]){
					inc=SVDM(valueNom[i],instance[i],i);
					dist+=Math.pow(inc, S);
				}
				else{
					if(instance[i]<valueMin[i]){
						inc=(valueMin[i]-instance[i]);
						dist+=Math.pow(inc, S);
					}
					if(instance[i]>valueMax[i]){
						inc=(instance[i]-valueMax[i]);
						dist+=Math.pow(inc, S);
					}
				}
			}
		}	
		
		return dist;

	}//end-method

    /**
     * Gets the SVDM distance between two nominal values
     *
     * @param value1 First value
     * @param value2 Second value
     * @param att Attribute of the values
     * @return The SVDM distance
     */
	private double SVDM(double value1, double value2,int att){
		
		double dist=0.0;
		double aux;
		int count1,count2;
		int max,min;
		int denom1,denom2;
		
		denom1=(int)(value1*(nValues[att]-1));
		denom2=(int)(value2*(nValues[att]-1));
		
		for(int i=0;i<nClasses;i++){
			
			count1=ACV[att][i][denom1];
			count2=ACV[att][i][denom2];
			
			if(count1!=count2){
				
				max=min=0;
				if(count1>count2){
					max=count1;
					min=count2;
				}
				if(count2>count1){
					max=count2;
					min=count1;
				}
				
				aux=((double)(max-min)/AC[att][i]);
				
				dist+=Math.pow(aux,Q);
				
			}			
		}

		return dist;

	}//end-method
	
}//end-class


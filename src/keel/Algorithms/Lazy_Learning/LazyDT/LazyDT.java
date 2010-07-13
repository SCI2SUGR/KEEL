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

package keel.Algorithms.Lazy_Learning.LazyDT;

import java.util.Arrays;
import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

/**
 * 
 * File: LazyDT.java
 * 
 * The LazyDT algorithm doesn't build a decision tree model in a training phase and
 * uses the model when we start classifying. In spite of that behaviour, it precomputes
 * some of the operations that should be done, and only with some information advances
 * to the classification step.
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 24/08/2009 
 * @version 0.1 
 * @since JDK1.5
 */

public class LazyDT extends LazyAlgorithm {

	//Parameters
	
	//Constants
	/**
	 * Range of the information measure that has to be different to follow one path
	 */
	private final static double RANGE = 1.01;
	/**
	 * Minimum number of attributes that have to support a path
	 */
	private final static int MIN_DATA = 5;
	
	//Additional structures
	/**
	 * Maximum number of values that a categorical attribute can have
	 */
	int maxNumValues;
	/**
	 * Number of values that each attribute can have (all attributes must be categorical)
	 */
	int numValues[];
	/**
	 * Original attribute values of the dataset
	 */
	double originalData[][];

	
	/** 
     * Creates a LazyDT instance by reading the script file that contains all the information needed
     * for running the algorithm
     *
     * @param script    The configuration script which contains the parameters of the algorithm
     */   
	public LazyDT (String script) {
	  
		readDataFiles(script);
		
		// Naming the algorithm
		name="LazyDT";

		// Initialization of auxiliary structures
		numValues=new int [inputAtt];
		
		// Initialization stuff ends here. So, we can start time-counting
		setInitialTime();
		
	} //end-method 
	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {

	}//end-method

	/**
     * Classifies a given item with the information stored using the LazyDT algorithm
     * 
     * @param item  Data attribute values for the item we are classifying
     * @param atts  Attributes in the data set that are used for building the tree and describing the 
     * instance given
     * @return the class asigned to the item given
     */
	protected int evaluate (double example[]) {
		int test;
		MyPair result;
		
		// First, we denormalize the example
		denormalize(example);
		
		// Find a path with the example data
		result=explore(originalData, trainOutput, example,numValues);
		
		// Obtaining the relevant result
		test= result.result();
		
		return test;
	}

	/**
	 * Creates the path for an example following the LazyDT algorithm, looking at the attribute values
	 * and trying to find a class according to the values given
	 * 
	 * @param data Original values of the attribute values
	 * @param output   Original output class for all the instances
	 * @param example  Data attribute values for the item we are trying to create a path
	 * @param attValues    Maximum number of possible values for all the attributes
	 * @return a pair with the predicted class
	 */
	private MyPair explore(double [][] data,int [] output,double example[], int [] attValues){
		MyPair result=new MyPair();
		double minEnt;
		double ent[];
		boolean done=false;
		boolean options [];
		MyPair partial;
		int maxSupport;
		int res;
		
		// Check if all the possibilites are the same
		if(equalVector(output)){
			result=new MyPair(output[0],output.length);
			done=true;
		}
		
		// Check if all the available data is the same
		if(equalData(data)){
			result=majority(output);
			done=true;
		}
		
		// Check if data has more than one attribute
		if(data[0].length==1){
			result=majority2(example[0],data,output);
			done=true;
		}
		
		// Check if the data available is at least the minimum data necessary
		if(data.length <MIN_DATA){
			result=majority(output);
			done=true;
		}
		
		if(!done){
			
			minEnt=Double.MAX_VALUE;
			ent=new double[data[0].length];
			
			//select attribute to prune
			for(int i=0;i<data[0].length;i++){
				ent[i]=entrophy(data, output,i, attValues[i]);
				if(minEnt>ent[i]){
					minEnt=ent[i];
				}
			}
				int contador=0;
			//get the options
			options= new boolean[data[0].length];
			for(int i=0;i<data[0].length;i++){
				if(ent[i]<=(RANGE*minEnt)){
					options[i]=true;
					contador++;
				}
				else{
					options[i]=false;
				}
			}
			
			//try options
			res=-1;
			maxSupport=0;
			for(int i=0;i<data[0].length;i++){
				
				if(options[i]){
					partial=tryNewExplore(data,output,example,attValues,i);			
					if(partial.instances()>maxSupport){
						maxSupport=partial.instances();
						res=partial.result();
					}
				}
			}
			
			//get the best tree found
			result=new MyPair(res,maxSupport);
				
		}
		
		return result;
	}
	
	/**
     * Creates an alternate new path (other than the first one chosen) for an example following the LazyDT
     * algorithm, looking at the attribute values and trying to find a class according to the values given
     * 
     * @param data Original values of the attribute values
     * @param output   Original output class for all the instances
     * @param example  Data attribute values for the item we are trying to create a path
     * @param attValues    Maximum number of possible values for all the attributes
     * @param selected  Attribute chosen for the last followed path
     * @return a pair with the predicted class
     */
    private MyPair tryNewExplore(double [][] data,int [] output,double example[], int [] attValues,int selected){
		
		double [][] newData;
		int [] newOutput;
		double newExample[];
		int [] newAttValues;
		boolean mask[];
		int remaining;
		int counter;
		int counter2;
		MyPair result;
		
		//prune data
		
		mask=new boolean [data.length];
		
		Arrays.fill(mask, false);
		remaining=0;
		for(int i=0;i<data.length;i++){
			if(example[selected]==data[i][selected]){
				mask[i]=true;
				remaining++;
			}
		}

		if (remaining==0){
			return majority(output);
		}
		
		newData= new double[remaining][data[0].length-1];
		newOutput= new int[remaining];
		newExample= new double[data[0].length-1];
		newAttValues=new int[data[0].length-1];
		
		counter=0;
		for(int i=0;i<data.length;i++){
			if(mask[i]){
				counter2=0;
				for(int j=0;j<data[0].length;j++){
					if(j!=selected){
						newData[counter][counter2]=data[i][j];
						counter2++;
					}
				}		
				newOutput[counter]=output[i];
				counter++;
			}
		}
		
		counter2=0;
		for(int j=0;j<data[0].length;j++){
			if(j!=selected){
				newExample[counter2]=example[j];
				newAttValues[counter2]=attValues[j];
				counter2++;
			}
		}	

		result=explore(newData,newOutput,newExample,newAttValues);
		
		return result;
	}
	
    /**
     * Checks if a given array has the same value for all of its elements
     * 
     * @param vector    Array that is going to be checked
     * @return true, if all the elements in the array are the same; false, otherwise
     */
	private boolean equalVector(int vector[]){
		
		int value=vector[0];
		
		if(vector.length<2){
			return true;
		}
		for(int i=0;i<vector.length;i++){
			if(vector[i]!=value){
				return false;
			}
		}
		
		return true;	
	}
	
	/**
	 * Gets the most frequent value stored in the array
	 * 
	 * @param vector   Array whose most frequent value is going to be found
	 * @return a pair with the most frequent value and its value   
	 */
	private MyPair majority(int vector[]){
		
		int values[]=new int [nClasses];
		int max;
		int selected;
		MyPair result;
		
		Arrays.fill(values, 0);
		
		for(int i=0;i<vector.length;i++){
			values[vector[i]]++;
		}
		
		selected=-1;
		max=-1;

		for(int i=0;i<values.length;i++){
			if(max<values[i]){
				max=values[i];
				selected=i;
			}
		}
		
		result= new MyPair(selected,max);
		
		return result;	
	}
	
	/**
	 * Gets the most frequent value stored in the output array from the correct instances
	 * 
	 * @param value   Value to find
	 * @param data   Data matrix
	 * @param vector   Array whose most frequent value is going to be found
	 * @return a pair with the most frequent value and its value   
	 */	
	private MyPair majority2(double value,double data [][], int vector[]){

		boolean mask[];
		
		//prune data
		
		mask=new boolean [data.length];
		
		Arrays.fill(mask, false);

		for(int i=0;i<data.length;i++){
			if(value==data[i][0]){
				mask[i]=true;
			}
		}

		int values[]=new int [nClasses];
		int max;
		int selected;
		MyPair result;
		
		Arrays.fill(values, 0);
		
		for(int i=0;i<vector.length;i++){
			if(mask[i]){
				values[vector[i]]++;
			}
		}
		
		selected=-1;
		max=-1;

		for(int i=0;i<values.length;i++){
			if(max<values[i]){
				max=values[i];
				selected=i;
			}
		}
		
		result= new MyPair(selected,max);
		
		return result;	
	}

	/**
     * Checks if a given matrix has the same value looking at the columns
     * 
     * @param data    Matrix that is going to be checked
     * @return true, if all the elements in the same column are the same; false, otherwise
     */
	private boolean equalData(double data[][]){
		
		double value;
		
		if(data.length<2){
			return true;
		}
		
		for(int j=0;j<data[0].length;j++){
			value=data[0][j];
			for(int i=1;i<data.length;i++){
				
				if(data[i][j]!=value){
					return false;
				}
			}
		}
		
		return true;	
	}
	
	/**
	 * Calculates the entrophy for a possible split in an attribute
     * 
     * @param data Original values of the attribute values
     * @param output   Original output class for all the instances
     * @param att  Attribute that is going to be split
     * @param valuesAtt    Maximum number of possible values for all the attributes
     * @return the entrophy for the data split for that attribute
	 */
	private double entrophy(double [][] data,int [] output, int att, int valuesAtt){
		
		double value=0.0;
		double entr;
		double fraction;
		
		int dataCount [][]= new int [nClasses][valuesAtt];
		int classCount[]= new int [nClasses];
		
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(dataCount[i],0);
			classCount[i]=0;
		}
		
		for(int i=0;i<data.length;i++){
			dataCount[output[i]][(int)data[i][att]]++;
			classCount[output[i]]++;
		}
		
		for(int i=0;i<nClasses;i++){
			
			entr=0.0;
			for(int j=0;j<valuesAtt;j++){
				
				if(dataCount[i][j]!=0){
					fraction=(double)dataCount[i][j]/(double)data.length;
					
					entr+=fraction * (Math.log(fraction)/Math.log(2.0));
				}
			}
			
			//weighting can be applied here
			//value-=((double)classCount[i]/(double)data.length)*entr;
			value-=entr;		
		}

		return value;
	}
	
	/**
	 * Does some previous computations to the beginning of the algorithm, this means, getting the number
	 * of different values of the categorical attributes and the denormalization of the dataset
	 */
	public void precompute(){
		
		maxNumValues=0;
		
		for(int i=0;i<inputAtt;i++){
			numValues[i]=train.getAttributeDefinitions().getInputAttribute(i).getNominalValuesList().size();
			if(numValues[i]>maxNumValues){
				maxNumValues=numValues[i];
			}			
		}
		
		originalData=new double[trainData.length][trainData[0].length];
		
		for(int i=0;i<trainData.length;i++){		
			System.arraycopy(trainData[i], 0, originalData[i], 0, trainData[i].length);
		}
		//denormalize dataset
		for(int i=0;i<originalData.length;i++){
			for(int j=0;j<originalData[0].length;j++){
				originalData[i][j] *= numValues[j]-1;
			}
		}
		
	}
	
	/**
	 * Denormalize an example given
	 * 
	 * @param example  Array with all the values that is going to be denormalized
	 */
	private void denormalize(double [] example){
		for(int j=0;j<trainData[0].length;j++){
			example[j] *= numValues[j]-1;
		}
		
	}
	
	/**
	 * Prints a matrix in the standard output. Usually used to check when developing the algorithm.
	 * 
	 * @param m    Matrix that is going to be printed
	 */
	private void printM(double[][] m){
		
		String text;
		
		for(int i=0;i<m.length;i++){
			
			text="";
			for(int j=0;j<m[0].length;j++){		
				text+=m[i][j]+" ";
			}
			System.out.println(text);
		}
		System.out.println("******");
	}
	
	/**
	 * 
	 * Small nested class that is used in the LazyDT algorithm like a data structure that stores
	 * an output class and the number of instances that support that class
	 * 
	 * @author Written by Victoria Lopez Morales (University of Granada) 26/08/2009 
	 * @version 0.1 
	 * @since JDK1.5
	 */
	private class MyPair{
		/**
		 * Output class for the algorithm
		 */
		private int result;
		/**
		 * Number of instances that support the class decided
		 */
		private int instances;

		/** 
	     * Creates a pair with empty values that we can identify
	     */
		public MyPair(){
			result=-1;
			instances=0;
		}
		
		/** 
	     * Creates a pair with the output class assigned to it and the number of instances supporting it
	     *
	     * @param res  The output class assigned to this pair
	     * @param ins  The number of instances supporting the output class
	     */  
		public MyPair(int res, int ins){
			result=res;
			instances=ins;
		}
		
		/**
		 * Gets the output class stored in this pair
		 * 
		 * @return the output class stored in this pair
		 */
		public int result(){
			return result;
		}
		
		/**
		 * Gets the number of instances that supports the output class stored
		 * 
		 * @return the number of instances that supports the output class stored
		 */
		public int instances(){
			return instances;
		}
	}
	
} //end-class 


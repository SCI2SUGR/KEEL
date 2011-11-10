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
 * File: Cluster.java
 * 
 * A class modelling a cluster for NSC algorithm
 * It provides basic functionality such insert and drop
 * elements, calculates inner and outer border, variance, and more.  
 * 
 * @author Written by Joaquín Derrac (University of Granada) 15/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.NSC;

public class Cluster{

	private int content [];
	private int size;
	private int numAtt;
	private int q;    //elements of inner border
	private int k;    //elements of outer border
	
	private double centroid [];
	private boolean validProperties;
	private double variance;
	private double stdDev;
	private int innerBorder[];
	private int outerBorder[];	
	
	private static final int INCREMENT = 100;
	
	/** 
	 * Buider.Builts a empty cluster, setting its parameters
	 * 
	 * @param attributes Dimensionality of instances
	 * @param qValue Value of q
	 * @param kValue Value of k
	 * 
	 */
	public Cluster (int attributes,int qValue,int kValue) {

		size=0;
		content=new int[INCREMENT];
		validProperties=false;
		numAtt=attributes;
		q=qValue;
		k=kValue;
		
		innerBorder=new int[q];	
		outerBorder=new int[k];	
		centroid=new double[numAtt];
	
	} //end-method 
	
	/** 
	 * Clone method.
	 * 
	 */
	public Cluster clone(){
		
		Cluster copy;
		
		copy=new Cluster(numAtt,q,k);
		
		copy.setAll(content);
		
		return copy;
	}//end-method
	
	/** 
	 * See size.
	 * 
	 * @return Size
	 */
	public int getSize(){
	
		return size;
	
	}//end-method
	
	/** 
	 * See if the cluster is empty
	 * 
	 * @return True if the cluster is empty
	 */
	public boolean isEmpty(){
		
		if(size==0){
			
			return true;
		}
		
		return false;
		
	}//end-method
	
	/** 
	 * Get an element from the cluster
	 * 
	 * @param index Index of the element
	 * @return The element
	 * 
	 */
	public int get(int index){
		
		if(index<size){
			return content[index];
		}
		
		return -1;
		
	}//end-method
	
	/** 
	 * Set an element into the cluster
	 * 
	 * @param ins Index of the instance in the train data
	 * @param index Index of the element
	 * 
	 */	
	public void set(int ins,int index){
		
		if(index<size){
			content[index]=ins;
			instanceChanged();
		}
		
	}//end-method
	
	/** 
	 * Get all elements from the cluster
	 * 
	 * @return All the elements on the cluster
	 * 
	 */
	public int[] getAll(){
		
		int instances[]=new int[size];
		
		System.arraycopy(content, 0, instances, 0, size);
		
		return instances;
		
	}//end-method
	
	/** 
	 * Set all elements into the cluster
	 * 
	 * @param ins Indexes of the instances in the train data
	 * 
	 */	
	public void setAll(int [] ins){
		
		size=ins.length;
		content=new int [size];
		System.arraycopy(ins, 0, content, 0, size);
		
		instanceChanged();

	}//end-method
	
	/** 
	 * Add an element into the cluster
	 * 
	 * @param ins Index of the instance in the train data
	 * 
	 */		
	public void add(int ins){

		if(size==content.length){

			int aux[]=new int [size];
			
			System.arraycopy(content, 0, aux, 0, size);
			content=new int [size+INCREMENT];
			System.arraycopy(aux, 0, content, 0, size);

		}
		
		content[size]=ins;
		size++;	
		
		instanceChanged();
		
	}//end-method	
	
	/** 
	 * Drop an element from the cluster
	 * 
	 * @param index Index of the instance in the cluster
	 * @return Index of the instance dropt
	 * 
	 */	
	public int drop(int index){

		int output;
		
		if(index<size){
			
			output=content[index];
			
			for(int i=index;i<(size-1);i++){
			
				content[i]=content[i+1];
			}

			size--;

			instanceChanged();

			return output;
		}		
		return -1;
		
	}//end-method
	
	/** 
	 * Drop an element from the cluster
	 * 
	 * @param number Index of the instance in train data
	 * 
	 */	
	public void dropByContent(int number){
		
		boolean found=false;
		int i;
		
		for(i=0;(i<size)&&!found;i++){		
			if(content[i]==number){
				found=true;
			}
		}
		for(int j=i;j<size-1;j++){
			content[j]=content[j+1];
		}
		
		size--;
		
		instanceChanged();	
		
	}//end-method
	
	/** 
	 * Get the centroid of the cluster
	 * 
	 * @return The centroid
	 * 
	 */		
	public double[] getCentroid(){
		
			return centroid;
			
	}//end-method

	/** 
	 * Set the centroid of the cluster
	 * 
	 * @param values Values of the centroid
	 * 
	 */
	public void setCentroid(double [] values){
		
		System.arraycopy(values, 0, centroid, 0, values.length);
		
	}//end-method
	
	/** 
	 * Set the inner border of the cluster
	 * 
	 * @param value Values of the inner border
     *
	 */
	public void setInnerBorder(int [] value){
		
		System.arraycopy(value, 0, innerBorder, 0, value.length);
		
	}//end-method
	
	/** 
	 * Get the inner border of the cluster
	 *
	 * @return The inner border
	 * 
	 */
	public int[] getInnerBorder(){

		return innerBorder;
		
	}//end-method
	
	/** 
	 * Set the outer border of the cluster
	 * 
	 * @param value Values of the outer border
	 * 
	 */
	public void setOuterBorder(int [] value){
		
		System.arraycopy(value, 0, outerBorder, 0, value.length);
	
	}//end-method
	
	/** 
	 * Get the outer border of the cluster
	 * 
	 * @return The outer border
	 * 
	 */
	public int[] getOuterBorder(){
		
		return outerBorder;

	}//end-method
	
	/** 
	 * Get variance of the cluster
	 * 
	 * @return The varience
	 * 
	 */
	public double getVariance(){
		
		if(validProperties==true){
			return variance;
		}
		else{
			return -1.0;
		}
		
	}//end-method
	
	/** 
	 * Get stdDev of the cluster
	 * 
	 * @return The stdDev
	 * 
	 */	
	public double getStdDev(){
		
		if(validProperties==true){
			return stdDev;
		}
		else{
			return -1.0;
		}
		
	}//end-method
	
	/** 
	 * Get variance and stdDev of the cluster
	 * 
	 * @param value Value of the variance
	 * 
	 */
	public void setVariance(double value){
		
		variance=value;
		stdDev=Math.sqrt(value);

	}//end-method
	
	/** 
	 * Nulls the values calculated for the cluster
	 * 
	 */	
	private void instanceChanged(){
		
		validProperties=false;

	}//end-method
	
	/** 
	 * Validate the values calculated for the cluster
	 * 
	 */		
	public void setProperties(){
		
		validProperties=true;

	}//end-method
	
	/** 
	 * See if values calculated for the cluster are valid
	 * 
	 * @return True if values are valid
	 * 
	 */		
	public boolean getProperties(){
		
		return validProperties;

	}//end-method

} //end-class 


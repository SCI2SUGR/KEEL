/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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
 * File: ChromosomeIS.java
 * 
 * A chromosome implementation for IS population
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.CIW_NN;

import org.core.Randomize;

class ChromosomeIS implements Comparable{

	private static int size;
	private static double prob0to1;
	private static double prob1;
	private static int diff[];
	
	private int body[];
	private double fitness;
	
	/**
	 * Clone method
	 */
	@Override
	public ChromosomeIS clone(){
		
		ChromosomeIS clon=new ChromosomeIS();
		
		clon.fitness=fitness;
		clon.body=new int[size];
		System.arraycopy(body, 0, clon.body, 0, size);
		
		return clon;
	}

	/**
	 * Builder
	 */
	public ChromosomeIS(){
		
		body=new int[size];
		
		for(int i=0;i<size;i++){
			body[i]= i;
		}
		
		shuffleBody();
		
		for(int i=0;i<size;i++){
			if(body[i]<=(int)(size*prob1)){
				body[i]=1;
			}
			else{
				body[i]=0;
			}
		}		
		
		fitness=-1.0;
	}
	
	/**
	 * Sets prob1 value
	 * 
	 * @param value Value to set
	 */
	public static void setprob1(double value){
		
		prob1=value;
	}
	
	/**
	 * Shuffles body of the chromosome
	 */
	private void shuffleBody(){
		
		int pos,tmp;
		
	    for (int i=0; i<size; i++) {
	    	
	    	pos = Randomize.Randint (0, size);
	    	tmp = body[i];
	    	body[i] = body[pos];
	    	body[pos] = tmp;
	    }
	}
    
	/**
	 * Sets prob0to1 value
	 * 
	 * @param value Value to set
	 */
	public static void setProb(double value){
		
		prob0to1=value;
	}
	
	/**
	 * Sets the size of the chromosomes
	 * 
	 * @param value Size of the chromosomes
	 */
	public static void setSize(int value){	
		size=value;
	}
	
	/**
	 * Gets the size of the chromosomes
	 * 
	 * @return Size of the chromosomes
	 */
	public static int getSize(){	
		return size;
	}
	
	/**
	 * Tests if the chromosome is evaluated
	 * 
	 * @return True if the chromosome is evaluated, false if not
	 */
	public boolean isEvaluated(){
		
		if(fitness==-1.0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the fitness value
	 * 
	 * @return Fitness value
	 */
	public double getFitness(){
		
		return fitness;
	}
	
	/**
	 * Sets the fitness value
	 * 
	 * @param value Fitness value
	 */
	public void setFitness(double value){
		
		fitness=value;
	}
	
	/**
	 * Gets a gene
	 * 
	 * @param pos Gene selected
	 * 
	 * @return Gene value
	 */
	public int get(int pos){
		
		return body[pos];
	}
	
	/**
	 * Get the size of the chromosomes
	 * 
	 * @return Size of the chromosomes
	 */
	public void set(int pos,int value){
		
		body[pos]=value;
	}
	
	/**
	 * Gets the body of a chromosome
	 * 
	 * @return Body of a chromosome
	 */
	public int [] getAll(){
		
		return body;
	}
	
	/**
	 * Computes reduction value
	 * 
	 * @return Reduction rate
	 */
	public double computeRed(){
		
		double count=0;
		double result;
		
		for(int i=0;i<size;i++){
			if(body[i]==1){
				count+=1.0;
			}
		}
		
		result=(double)(count/(double)size);
		result=1.0-result;
		
		if(result==1.0){
			return -10000.0;
		}
		
		return result;
	}
	
	/**
	 * Classic HUX cross operator
	 * 
	 * @param second Second chromosome to cross
	 * 
	 * @return Offspring
	 */
	public ChromosomeIS HUX(ChromosomeIS second){
		
		int index=0;
		int aux;
		diff=new int [size];
		
		for(int i=0;i<size;i++){
			
			if(body[i]!=second.body[i]){
				diff[index]=i;
				index++;
			}
		}
		
		shuffleDiff(index);
		
		index=index/2;
	
		for(int i=0;i<index;i++){
			if(Randomize.Randdouble(0.0, 1.0)<prob0to1){
				aux=body[diff[i]];
				body[diff[i]]=second.body[diff[i]];
				second.body[diff[i]]=aux;
			}
			else{
				body[diff[i]]=0;
				second.body[diff[i]]=0;
			}
		}
		fitness=-1.0;
		second.fitness=-1.0;
		
		return second;
	}

	/**
	 * Compare to method
	 */
	@Override
	public int compareTo(Object o) {
		
		ChromosomeIS other= (ChromosomeIS)o;
		
		if(this.fitness>other.fitness){
			return -1;
		}
		
		if(this.fitness<other.fitness){
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * Shuffles the vector of different alleles
	 * 
	 * @param size Size of the vector
	 */
	private void shuffleDiff(int size){
		
		int pos,tmp;
		
	    for (int i=0; i<size; i++) {
	    	
	    	pos = Randomize.Randint (0, size);
	    	tmp = diff[i];
	    	diff[i] = diff[pos];
	    	diff[pos] = tmp;
	    }
	}
	
	/**
	 * To string method
	 */
	@Override
	public String toString() {
		
		String text="";
		
		for(int i=0;i<body.length;i++){
			text+=body[i];
		}
		text+=" Fitness: "+fitness;
		
		return text;
	}
	
	
	
} //end-class 


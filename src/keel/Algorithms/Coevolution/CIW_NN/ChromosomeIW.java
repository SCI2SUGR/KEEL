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
 * File: ChromosomeIW.java
 * 
 * A chromosome implementation for IW population
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.CIW_NN;

import org.core.Randomize;

class ChromosomeIW implements Comparable{

	private static int size;
	private double body[];
	private double fitness;

	/**
	 * Clone method
	 */
	@Override
	public ChromosomeIW clone(){
		
		ChromosomeIW clon=new ChromosomeIW();
		
		clon.fitness=fitness;
		clon.body=new double[size];
		System.arraycopy(body, 0, clon.body, 0, size);
		
		return clon;
	}
	
	/**
	 * Builder
	 */
	public ChromosomeIW(){
		
		body=new double[size];
		
		for(int i=0;i<size;i++){
			body[i]= Randomize.RanddoubleClosed(0.0, 1.0);
		}
		
		fitness=-1.0;
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
	public double get(int pos){
		
		return body[pos];
	}
	
	/**
	 * Sets a gene
	 * 
	 * @param pos Position of the gene
	 * @param value Gene value
	 */
	public void set(int pos,double value){
		
		body[pos]=value;
	}
	
	/**
	 * Get the size of the chromosomes
	 * 
	 * @return Size of the chromosomes
	 */
	public static int getSize(){	
		return size;
	}
	
	/**
	 * Gets the body of a chromosome
	 * 
	 * @return Body of a chromosome
	 */
	public double [] getAll(){
		
		return body;
	}
	
	/**
	 * Classic BLX cross operator
	 * 
	 * @param second Second chromosome to cross
	 * @param alpha Alpha value
	 * 
	 * @return Offspring
	 */
	public ChromosomeIW BLX(ChromosomeIW second, double alpha){
		
		double value1,value2,min,max,interval;
		
		for(int i=0;i<body.length;i++){
			
			min=Math.min(body[i], second.body[i]);
			max=Math.max(body[i], second.body[i]);
			interval=max-min;
			
			min= min-(interval*alpha);
			min=Math.max(min,0.0);
			max= max+(interval*alpha);
			max=Math.min(max,1.0);
			
			value1=Randomize.RanddoubleClosed(min, max);
			value2=Randomize.RanddoubleClosed(min, max);
			
			body[i]=value1;
			second.body[i]=value2;
		}

		fitness=-1.0;
		second.fitness=-1.0;
		
		return second;
	}
	
	/**
	 * Performs a non uniform mutation
	 * 
	 * @param time Ratio of evaluations spent so far
	 */
	public void notUniformMutation(double time){

		
		for(int i=0;i<body.length;i++){
			
			if(Randomize.Rand()<0.01){
			
				if(Randomize.Rand()<0.5){				
					body[i]+=delta(time,1.0-body[i]);
				}else{
					body[i]-=delta(time,body[i]);				
				}	
			}
		}
		
	}
	
	/**
	 * Delta function for non uniform mutation
	 * 
	 * @param time Ratio of evaluations spent so far
	 * @param range Range of mutation
	 * 
	 * @return Final value for the gene
	 */
	private double delta(double time, double range){
		
		double value;
		double b=5.0;
		
		value=range*(1.0 - Math.pow(Randomize.Rand(),Math.pow((1.0-time),b)));
		
		return value;
	}
	
	/**
	 * Compare to method
	 */
	@Override
	public int compareTo(Object o) {
		
		ChromosomeIW other= (ChromosomeIW)o;
		
		if(this.fitness>other.fitness){
			return -1;
		}
		
		if(this.fitness<other.fitness){
			return 1;
		}
		
		return 0;
	}
	
	/**
	 * To string method
	 */
	@Override
	public String toString() {
		
		String text="";
		
		for(int i=0;i<body.length;i++){
			text+=body[i]+"-";
		}
		text+=" Fitness: "+fitness;
		
		return text;
	}
	
} //end-class 


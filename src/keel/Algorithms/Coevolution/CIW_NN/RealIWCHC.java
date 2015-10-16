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
 * File: RealIWCHC.java
 * 
 * A real-coded implementation of the CHC algorithm, for the IW population
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.CIW_NN;

import java.util.Arrays;

import org.core.Randomize;


class RealIWCHC {

	private static int evals;
	private static int MAX_EVALS;
	private static double mutProb;
	
	/**
	 * Sets maximum evaluations for this epoch
	 * 
	 * @param value Maximum evaluations
	 */
	public static void setMAX_EVALS(int value){
		
		MAX_EVALS=value;
	}

	/**
	 * Sets mutation probability
	 * 
	 * @param mut Mutation probability
	 */
	public static void setMutation(double mut){
		
		mutProb=mut;
	}
	
	/**
	 * Performs a generation of the SSGA
	 * 
	 * @param population IW population
	 * 
	 * @return Amount of evaluations spent
	 */
	public static int generation(ChromosomeIW [] population){
		
		int candidate1,candidate2;
		int selected1, selected2;
		ChromosomeIW one,two,three,four,elite;
		double value,time;
		
		evals=0;
		
		while (evals < MAX_EVALS) {

			elite=population[0].clone();
			
	        //Binary tournament selection: First candidate
			
			candidate1 = Randomize.Randint(0,population.length);
	        do {
	        	candidate2 = Randomize.Randint(0,population.length);
	        	
	        } while (candidate2 == candidate1);
	        
	        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
	        	selected1=candidate1;
	        }
	        else{
	        	selected1=candidate2;
	        }

	        //Binary tournament selection: First candidate
			
			candidate1 = Randomize.Randint(0,population.length);
	        do {
	        	candidate2 = Randomize.Randint(0,population.length);
	        } while (candidate2 == candidate1);
	        
	        if (population[candidate1].getFitness() > population[candidate2].getFitness()){
	        	selected2=candidate1;
	        }
	        else{
	        	selected2=candidate2;
	        }

	        //Cross operator

	        one=population[selected1].clone();
	        two=population[selected2].clone();

	        //BLX - 0.3
	        two=one.BLX(two,0.3);

	        //mutation
	        //Mutation operator
	        if(Randomize.Rand()<0.05){
		        time=(double)evals/(double)MAX_EVALS;
		        
		        one.notUniformMutation(time);
		        two.notUniformMutation(time);
	        }
			value=evaluateFitness(two);
			two.setFitness(value);
			value=evaluateFitness(one);
			one.setFitness(value);

	        three=population[selected1].clone();
	        four=population[selected2].clone();
	        
	        //BLX - 0.5 (1)
	        four=three.BLX(two,0.5);

	        //mutation
	        //Mutation operator
	        if(Randomize.Rand()<mutProb){
		        time=(double)evals/(double)MAX_EVALS;
		        
		        three.notUniformMutation(time);
		        four.notUniformMutation(time);
	        }
	        
			value=evaluateFitness(three);
			three.setFitness(value);
			value=evaluateFitness(four);
			four.setFitness(value);
			
	        //get best
	        if(three.getFitness()>one.getFitness()){
	        	one=three.clone();
	        }
	        
	        if(four.getFitness()>two.getFitness()){
	        	two=four.clone();
	        }
	        
	        three=population[selected1].clone();
	        four=population[selected2].clone();
	        
	        //BLX - 0.5 (2)
	        four=three.BLX(two,0.5);

	        //mutation
	        //Mutation operator
	        if(Randomize.Rand()<0.05){
		        time=(double)evals/(double)MAX_EVALS;
		        
		        three.notUniformMutation(time);
		        four.notUniformMutation(time);
	        }
	        
			value=evaluateFitness(three);
			three.setFitness(value);
			value=evaluateFitness(four);
			four.setFitness(value);
			
	        //get best
	        if(three.getFitness()>one.getFitness()){
	        	one=three.clone();
	        }
	        
	        if(four.getFitness()>two.getFitness()){
	        	two=four.clone();
	        }

	        three=population[selected1].clone();
	        four=population[selected2].clone();
	        
	        //BLX -0.7
	        four=three.BLX(two,0.7);

	        //mutation
	        //Mutation operator
	        if(Randomize.Rand()<0.05){
		        time=(double)evals/(double)MAX_EVALS;
		        
		        three.notUniformMutation(time);
		        four.notUniformMutation(time);
	        }
	        
	        value=evaluateFitness(three);
			three.setFitness(value);
			value=evaluateFitness(four);
			four.setFitness(value);
			
	        //get best
	        if(three.getFitness()>one.getFitness()){
	        	one=three.clone();
	        }
	        
	        if(four.getFitness()>two.getFitness()){
	        	two=four.clone();
	        }

	        /*Replace the two worst*/
	        Arrays.sort(population);

	        population[population.length-1] = one.clone();
	        population[population.length-2] = two.clone();
     
	        Arrays.sort(population);
	        
	        if(population[0].getFitness()<elite.getFitness()){
	        	 population[0] = elite.clone();
	        }
	        
	    }
		
		return evals;
	}
	
	/**
	 * Fitness function
	 * 
	 * @param a Chromosome to evaluate
	 * 
	 * @return Fitness value
	 */
	private static double evaluateFitness(ChromosomeIW a){
		
		double acc;
		
		WKNN.setInstanceWeights(a.getAll());
		acc=WKNN.accuracy();
		evals++;

		return acc;
		
	}	
		
} //end-class 


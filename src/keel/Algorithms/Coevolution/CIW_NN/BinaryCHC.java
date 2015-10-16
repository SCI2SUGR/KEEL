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
 * File: BinaryCHC.java
 * 
 * A binary implementation of the CHC algorithm, for the IS population
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.CIW_NN;

import java.util.Arrays;

import org.core.Randomize;


class BinaryCHC {

	private static int evals;
	private static int sizePop;
	private static int threshold;
	private static int initialThreshold;
	private static double alpha;
	private static double prob0to1R;
	
	/**
	 * Set threshold for the CHC restart
	 * 
	 * @param value Threshold
	 */
	public static void setThreshold(int value){
		
		initialThreshold=value;
		threshold=value;
	}

	/**
	 * Sets alpha value
	 * 
	 * @param value Alpha value
	 */
	public static void setAlpha(double value){
		
		alpha=value;
	}
	
	/**
	 * Sets prob0to1 probability on restart
	 * 
	 * @param value Probability to set
	 */
	public static void setprob0to1R(double value){
		
		prob0to1R=value;
	}
	
	/**
	 * Performs a generation of the CHC algorithm
	 * 
	 * @param population IS population
	 * 
	 * @return Evaluations spent
	 */
	public static int generation(ChromosomeIS [] population){
		
		ChromosomeIS newPopulation [];
		boolean nuevos=false;
		double value;

		evals=0;
		sizePop=population.length;
		
		//baraje de la poblacion
		newPopulation=shuffle(population);
		
		//Cruce 
		
		for(int i=0;i<sizePop;i+=2){
		
			if(hamming(newPopulation[i].getAll(),newPopulation[i+1].getAll())/2>threshold){

				//cruce
				HUXCross(newPopulation,i,i+1);
				
				//evaluacion
				value=evaluateFitness(newPopulation[i]);
				newPopulation[i].setFitness(value);
				
				value=evaluateFitness(newPopulation[i+1]);
				newPopulation[i+1].setFitness(value);
				
				nuevos=true;
			}
		}
		
		//Seleccion selectiva
		if(nuevos){
			nuevos=mergePop(population,newPopulation);
		}
		
		//reinicicializacion
		if(nuevos==false){
			
			threshold--;
			
			if(threshold==0){

				//generar
				for(int i=1; i<sizePop;i++){
					population[i]=population[0].clone();
				}
				
				//mutar

				for(int i=1; i<sizePop;i++){
					for(int j=0;j<ChromosomeIS.getSize();j++){
						if(Randomize.Rand()<0.35){
							if(Randomize.Rand()<prob0to1R){
								population[i].set(j,1);
							}
							else{
								population[i].set(j,0);
							}
						}
					}
					population[i].setFitness(-1.0);
				}
				
				//evaluar
				for(int i=1; i<sizePop;i++){
					value=evaluateFitness(population[i]);
					population[i].setFitness(value);
				}
				
				//ordenar
				Arrays.sort(population);
				        
				threshold=initialThreshold;
			}
		}

		return evals;
	}
	
	/**
	 * Merges two population
	 * 
	 * @param population Old population
	 * @param newPopulation New population
	 * 
	 * @return True if any new chromosome has been acepted. False, if not
	 */
	private static boolean mergePop(ChromosomeIS [] population,ChromosomeIS [] newPopulation){
		
		int index=0;
		int taken=0;
		boolean used=false;
		double bestFitness;
		int bestPosition;
		
		ChromosomeIS [] finalPop=new ChromosomeIS [sizePop]; 
		
		bestFitness=-1;
		bestPosition=-1;
		
		for(int i=0;i<newPopulation.length;i++){
			if(newPopulation[i].getFitness()>bestFitness){
				bestFitness=newPopulation[i].getFitness();
				bestPosition=i;
			}
		}
		
		while(taken<sizePop){
			
			if(population[index].getFitness()>bestFitness){
				finalPop[taken]=population[index].clone();
				index++;
			}
			else{
				finalPop[taken]=newPopulation[bestPosition].clone();			
				newPopulation[bestPosition].setFitness(-1.0);
				
				bestFitness=-1;
				bestPosition=-1;
				
				for(int i=0;i<newPopulation.length;i++){
					if(newPopulation[i].getFitness()>bestFitness){
						bestFitness=newPopulation[i].getFitness();
						bestPosition=i;
					}
				}
				used=true;
			}
			taken++;
		}
		
		System.arraycopy(finalPop, 0, population, 0, sizePop);
		
		return used;
	}
	
	/**
	 * Computes hamming distance
	 * 
	 * @param a First array
	 * @param b Second array
	 * 
	 * @return Hamming distance
	 */
	private static int hamming (int a[],int b[]){
		
		int dist=0;
		
		for(int i=0;i<a.length;i++){

			if(a[i]!=b[i]){
				dist++;
			}
		}
		
		return dist;		
	}
	
	/**
	 * Shuffles the population
	 * 
	 * @param population IS population
	 * 
	 * @return Shuffled population
	 */
	private static ChromosomeIS [] shuffle(ChromosomeIS [] population){
		
		int index[]=new int [sizePop];
		int pos,tmp;
		ChromosomeIS clon []= new ChromosomeIS [sizePop];
		
	    for (int i=0; i<sizePop; i++){
	    	index[i] = i;
	    }

	    for (int i=0; i<sizePop; i++) {    	
	    	pos = Randomize.Randint (0, sizePop);
	    	tmp = index[i];
		    index[i] = index[pos];
		    index[pos] = tmp;
	    }
	 
	    for (int i=0; i<sizePop; i++) {
	    	clon[i]=population[index[i]].clone();
	    }
		
	    return clon;
	}
	
	/**
	 * Fitnes function
	 * 
	 * @param a Chromosome to evaluate
	 * 
	 * @return Fitness value
	 */
	private static double evaluateFitness(ChromosomeIS a){
		
		double value,acc,red;
		
		WKNN.setInstances(a.getAll());
		acc=WKNN.accuracy();
		evals++;
		red=a.computeRed();
		
		value=(alpha*acc)+((1.0-alpha)*red);
		
		return value;
		
	}
	
	/**
	 * HUX crossing operator
	 * 
	 * @param population Is population
	 * @param a First chromosome
	 * @param b Second chromosome
	 */
	private static void HUXCross(ChromosomeIS population [],int a,int b){

		population[b]=population[a].HUX(population[b]);
	}	
		
} //end-class 


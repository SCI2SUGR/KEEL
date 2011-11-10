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
 * File: Subpopulation.java
 * 
 * This class manage the subpopulations of selectors of the CoCoIS model
 * 
 * @author Written by Joaquin Derrac (University of Granada) 3/3/2010
 * @version 1.1
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Instance_Selection.CoCoIS;

import java.util.Arrays;

import org.core.Randomize;

public class Subpopulation{

	private int ID; //identifier of the population
	
	private double trainData [][]; //subset of training data assigned to the population
	private int trainOutput[]; //training data output
	private int nClasses; //number of classes
	
	private int IDs[]; //identifier for each member of the population
	private int population[][]; //population chromosomes
	private double fitness[]; //fitness value of the population individuals
	private int newPopulation[][]; //copy of the population for each new generation
	                       
	private double cache[][]; //distances cache
	
	private static int K; // K parameter for K-NN
	private static int size; //size of the population
	
	private static double WError; //Weight for Error in fitness function
	private static double WReduction; //Weight for Reduction in fitness function
	private static double WDifference; //Weight for Difference in fitness function
	private static double Elitism; //Percentage of member affected by elitism
	private static double PRnn; //Probability of application of RNN mutation
	private static double PRandom; //Probability of application of random mutation
	private static double PBit; //Probability of bit change in random mutation
	
	private int ISSelection[]; //Current instance selection vector for K-NN
	private double minDist[]; //auxiliary vector for K-NN classifier
	private int nearestN[];//auxiliary vector for K-NN classifier
	private int selectedClasses[];//auxiliary vector for K-NN classifier
	
	/**
     * Sets the K parameter
     *
     * @param value Value for the K parameter
     *
     */
	public static void setK(int value){		
		
		K=value;
		
	}//end-method
	
	/**
     * Sets the size of the population
     *
     * @param value Size of the population
     *
     */	
	public static void setSize(int value){		
		size=value;
	}//end-method	
	
	/**
     * Sets the WError parameter
     *
     * @param value Value for the WError parameter
     *
     */
	public static void setWError(double value){		
		WError=value;
	}//end-method
	
	/**
     * Sets the WReduction parameter
     *
     * @param value Value for the WReduction parameter
     *
     */	
	public static void setWReduction(double value){		
		WReduction=value;
	}//end-method	
	
	/**
     * Sets the WDifference parameter
     *
     * @param value Value for the WDifference parameter
     *
     */	
	public static void setWDifference(double value){		
		WDifference=value;
	}//end-method	

	/**
     * Sets the Elitism percentage
     *
     * @param value Value for the Elitism percentage
     *
     */	
	public static void setElitism(double value){		
		Elitism=value;
	}//end-method	

	/**
     * Sets the RNN mutation probability
     *
     * @param value Value for the RNN mutation probability
     *
     */	
	public static void setPRnn(double value){		
		PRnn=value;
	}//end-method		

	/**
     * Sets the random mutation probability
     *
     * @param value Value for the random mutation probability
     *
     */	
	public static void setPRandom(double value){		
		PRandom=value;
	}//end-method	

	/**
     * Sets the bit flip probability
     *
     * @param value Value for the bit flip probability
     *
     */	
	public static void setPBit(double value){		
		PBit=value;
	}//end-method	
	
	 /**
     * Builder. Generates a new subpopulation from a subset of the entire training set
     *
     * @param id Identifier of the population
     * @param train Subset of training data
     * @param out Output attribute of the subset of training data
     */
	public Subpopulation(int id, double train[][],int out[]){
		
		//identify it
		ID=id;
		
		IDs=new int[size];
		
		for(int i=0;i<size;i++){
			IDs[i]=i;
		}
		
		//set data
		trainData=new double [train.length][train[0].length];
		trainOutput=new int [out.length];
		
		for(int i=0;i< trainData.length;i++){
			
			for(int j=0;j<trainData[0].length;j++){
				trainData[i][j]=train[i][j];
			}
			trainOutput[i]=out[i];
		}
		
		//Getting the number of different classes
	    nClasses = 0;
	    for (int i=0; i<trainOutput.length; i++){
	      if (trainOutput[i] > nClasses){
	    	  nClasses = trainOutput[i];
	      }
	    }
	    nClasses++;
		
		//create population
		population= new int [size][trainData.length];
		
		for(int i=0;i<size;i++){
			for(int j=0;j<trainData.length;j++){
				if(Randomize.Rand()<0.5){
					population[i][j]=1;
				}
				else{
					population[i][j]=0;
				}
				
			}
		}
		
		fitness= new double [size];
		Arrays.fill(fitness, -1.0);
		
		//initialize cache of distances
		generateCache();
		
		//initialize structures
		ISSelection=new int [trainData.length];
		selectedClasses= new int[nClasses];
		nearestN = new int[K];
		minDist = new double[K];

	}//end-method
	
	 /**
     * Performs a new generation of the subpopulation
     * 
     */	
	public void doGeneration(){
		
		int notSave;
		int father, mother;
			
		//evaluate population
		for(int i=0;i<size;i++){
			
			fitness[i]=evaluateFitness(i);		
		}
		
		sortPopulation();
		
		//apply elitism
		notSave=size-((int)((double)size*Elitism));
		
		newPopulation=new int [size][trainData.length];
		
		//generate new population
		for(int i=0;i<notSave;i+=2){
			
			//selection of parents
    		
    		father = Randomize.RandintClosed(0, size-notSave-1);
    	    do {
	    		mother = Randomize.RandintClosed(0, size-notSave-1);
    	    } while (mother == father);
    	    
    	    //crossover
    	    HUX(father,mother,i);
		}
		
		//merge new population
		int basis=size-notSave;
		for(int i=0;i<notSave;i++){
			
			for(int j=0; j<population[i].length;j++){
				population[basis+i][j]=newPopulation[i][j];
			}
			fitness[basis+i]=-1.0;
			CoCoIS.RequestReevaluation(ID, getKey(basis+i));
		}
		
		//apply random mutation
		for(int i=0; i<size;i++){
			
			if(Randomize.Rand()<PRandom){
				
				for(int j=0; j<population[i].length;j++){
					
					if(Randomize.Rand()<PBit){
						population[i][j]=(population[i][j]+1)%2;
					}
				}
				fitness[i]=-1.0;
				CoCoIS.RequestReevaluation(ID, getKey(i));
			}
		}
		
		//apply Rnn mutation
		for(int i=0; i<size;i++){
			
			if(Randomize.Rand()<PRnn){

				rnnMutation(i);

				fitness[i]=-1.0;
				CoCoIS.RequestReevaluation(ID, getKey(i));
			}
			
		}
		
	}//end-method
	
	 /**
     * Sorts population by descending fitness value
     * 
     */	
	private void sortPopulation(){
		
		for(int i=0;i<population.length;i++){
			for(int j=i+1;j<population.length;j++){			
				if(fitness[j]>fitness[i]){
					swapSelectors(i,j);
				}
			}
		}

	}//end-method
	
	 /**
     * Swaps two selectors of the subpopulation
     * 
     * @param a First selector
     * @param B Second selector
     */	
	private void swapSelectors(int a,int b){
		
		double aux;
		int auxN;

		//swap data
		for(int i=0;i<population[0].length;i++){
			auxN=population[a][i];
			population[a][i]=population[b][i];
			population[b][i]=auxN;
		}
		
		//swap fitness
		aux=fitness[a];
		fitness[a]=fitness[b];
		fitness[b]=aux;
		
		//swap IDs
		auxN=IDs[a];
		IDs[a]=IDs[b];
		IDs[b]=auxN;
		
	}//end-method

	/**
     * Crosses two selectors of the subpopulation and generates two new childs
     * 
     * @param a First selector
     * @param b Second selector
     * @param newIndex Base index for the offspring
     */	
	public void HUX(int a, int b,int newIndex){
		
		int index=0;
		int aux;
		int diff []=new int [population[a].length];
		
		//copy parents
		for(int i=0;i<population[a].length;i++){
			
			newPopulation[newIndex][i]=population[a][i];
			newPopulation[newIndex+1][i]=population[b][i];
		}
		
		//mark non matching alleles
		for(int i=0;i<population[a].length;i++){
			
			if(population[a][i]!=population[b][i]){
				diff[index]=i;
				index++;
			}
		}
		
		//shuffle differences
		shuffleDiff(diff,index);
		
		index=index/2;

		//Exchange half of the differences randomly
		for(int i=0;i<index;i++){			
			aux=newPopulation[newIndex][diff[i]];
			newPopulation[newIndex][diff[i]]=newPopulation[newIndex+1][diff[i]];
			newPopulation[newIndex+1][diff[i]]=aux;	
		}

	}//end-method
	
	/**
     * Shuffles a vector of differences
     * 
     * @param diff Vector of differences
     * @param index Final position of the vector
     */	
	private void shuffleDiff(int diff [], int index){
		
		int pos,tmp;
		
	    for (int i=0; i<index; i++) {
	    	
	    	pos = Randomize.Randint (0, index);
	    	tmp = diff[i];
	    	diff[i] = diff[pos];
	    	diff[pos] = tmp;
	    }
	    
	}//end-method
	
	/**
     * Performs a Rnn mutation on the selected chromosome
     * 
     * @param individual Chromosome selected
     */		
	private void rnnMutation(int individual){
		
		int initialAcc, actualAcc;
		
		//compute initial accuracy
		initialAcc=computeHits(individual);

		for(int i=0;i<trainData.length;i++){
				
			//test instance
			if(population[individual][i]==1){ 
				
				//remove instance
				population[individual][i]=0;
				
				//compute accuracy
				actualAcc=computeHits(individual);
				
				//decide if removing the instance definitively
				if(initialAcc>actualAcc){
					population[individual][i]=1;
				}
				else{
					initialAcc=actualAcc;
				}
			}
		}
		
	}//end-method
	
	/**
     * Fitness function of the subpopulations
     * 
     * @param index Individual to evaluate
     */	
	private double evaluateFitness(int index){
		
		double fitness;
		double acc, red, dif;
		int hits;
		
		//compute reduction rate
		red=computeRed(index);
		acc=0.0;
		dif=0.0;
		
		//a void chromosome gets a low fitness
		if(red==1.0){
			fitness=WReduction;
		}
		else{	
			//compute difference rate
			dif=CoCoIS.getContribution(ID,index);
			
			//compute accuracy
			hits=computeHits(index);
			acc=(double)((double)hits/(double)trainData.length);
			
			fitness=(WError*acc)+(WReduction*red)+(WDifference*dif);
			
		}
		
		return fitness;
		
	}//end-method

	/**
     * Compute reduction rate of an individual
     * 
     * @param index Individual to evaluate
     */	
	private double computeRed(int index){
		
		double red;
		int count;

		//count number of instances selected
		count=0;
		for(int i=0;i<population[index].length;i++){
			if(population[index][i]==1){
				count++;
			}
		}
		
		red= 1.0-((double)count/(double)population[index].length);
	
		return red;
		
	}//end-method
	
	/**
     * Compute number of hits in K-NN classification of an individual
     * 
     * @param individual Individual to evaluate
     */		
	private int computeHits(int individual){
		
		int hits;
		int test;
		int old;
		
		hits=0;
		
		//copy member to the K-NN classifier
		for(int i=0;i<trainData.length;i++){
			ISSelection[i]=population[individual][i];
		}
		
		//perform classification
		for (int i=0; i<trainData.length; i++) {
					
			//leave-one-out
			old=ISSelection[i];
			ISSelection[i]=0;
			
			test=knnClassify(i);
			
			if(test==trainOutput[i]){
				hits++;
			}
			ISSelection[i]=old;
		}
		
		return hits;
		
	}//end-method

	/**
     * K-NN classifier
     * 
     * @param index Training instance to classify
     * @return Class predicted
     */	
	private int knnClassify(int index){
		
		
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;
	
		Arrays.fill(minDist, Double.MAX_VALUE);
	    
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(ISSelection[i]==1){
				
		    	dist = distance(index,i);

				//see if it's nearer than our previous selected neigbours
				stop=false;
				
				for(int j=0;j<K && !stop;j++){
				
					if (dist < minDist[j]) {
					    
						for (int l = K - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//we have check all the instances... see what is the most present class
		
		if(K==1){
			return trainOutput[nearestN[0]];
		}
		
		Arrays.fill(selectedClasses, 0);
		
		for (int i=0; i<K; i++) {
			selectedClasses[trainOutput[nearestN[i]]]++;
		}
		
		prediction=-1;
		predictionValue=0;
		
		for (int i=0; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;

	}//end-method
	
	/**
     * Generates a cache of distances to speed up the method
     * 
     */
	private void generateCache(){
		
		cache= new double [trainData.length][trainData.length];
		
		for(int i=0;i<trainData.length;i++){
			Arrays.fill(cache[i], -1.0);
			cache[i][i]=0.0;
		}
		
	}//end-method
	
	/**
	 * Distance between two training instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * @return Euclidean distance
	 */
	private double distance(int a, int b){
		
		double dist;
		
		//use cache
		if(cache[a][b]!=-1.0){
			dist=cache[a][b];
		}
		else{
			//compute distance and store it in the cache
			dist= euclideanDistance(a,b);
			cache[a][b]=dist;
			cache[b][a]=dist;
		}
		
		return dist;

	}//end-method
	
	/**
	 * Euclidean distance between two training instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * @return Euclidean distance
	 */
	private double euclideanDistance(int a, int b){
		
		double dist=0.0;
		double aux;
		
		for(int i=0;i<trainData[0].length;i++){
			aux=trainData[a][i]-trainData[b][i];
			aux=aux*aux;
			dist+=aux;
		}
		
		//sqrt avoided to speed up the algorithm
		
		return dist;
		
	}//end-method
	
	/**
	 * Returns the body of an individual, given its ID
	 * 
	 * @param key ID of the individual
	 * @return body of the individual
	 */
	public int [] getBody(int key){
		
		int body [];
		int index;
		
		//search real index
		index=searchKey(key);
		
		body=new int[trainData.length];
		
		for(int i=0;i<trainData.length;i++){
			body[i]=population[index][i];
		}
		
		return body;
		
	}//end-method
	
	/**
	 * Search the real index of an individual, given its ID
	 * 
	 * @param value ID of the individual
	 * @return index of the individual
	 */
	private int searchKey(int value){
		
		boolean found=false;
		int index=0;
		
		for(int i=0; i<size && !found;i++){
			
			if(IDs[i]==value){
				found=true;
				index=i;
			}
		}
		
		return index;
		
	}//end-method
	
	/**
	 * Returns the ID assigned to an individual
	 * @param value Index of the individual
	 * @return ID
	 */
	private int getKey(int value){
			
		return IDs[value];
		
	}//end-method
	
	/**
	 * Performs a roulette selection process 
	 * @return Individual selected
	 */
	public int rouletteSelection(){
	    
		int selected;
	    double uniform;
	    double sum[];
	    
	    if(fitness[0]==-1.0){
	    	return Randomize.RandintClosed(0, size-1);
	    }
	    
	    sum=new double[size];
	    
	    sum[0]=fitness[0];
	    
	    for(int i=1;i<size;i++){
	    	sum[i]=sum[i-1]+fitness[i];
	    }

	    uniform = Randomize.Randdouble(0.0, sum[size-1]);
	    selected = 0;
	    
	    while (uniform > sum[selected]){
	    	selected++;
	    }

	    //selected is the method. We must return its ID
	    
	    return getKey(selected);
	}

	/**
	 * Prints the population
	 * @return String with the contents of the population
	 */
	public String print(){
		
		String text="";

		for(int i=0;i<size;i++){
			for(int j=0;j<trainData.length;j++){
				text+=population[i][j];
			}
			text+="\n";
		}
		
		return text;
		
	}//end-method
	
	/**
	 * Prints an individual of the population
	 * 
	 * @param val index of the individual
	 * @return String with the contents of the individual
	 */	
	public String printIndividual(int val){
		
		String text="";

		for(int j=0;j<trainData.length;j++){
			text+=population[val][j];
		}
		text+="\n";

		return text;
		
	}//end-method
	
}//end-class


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
 * File: CoCoIS.java
 * 
 * This class implements the Cooperative Coevolutionary Instance Selection model
 * (CoCoIS)
 * 
 * @author Written by Joaquin Derrac (University of Granada) 3/3/2010
 * @version 1.1
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Preprocess.Instance_Selection.CoCoIS;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class CoCoIS extends Metodo{

	//Parameters 
	
	private static long seed; //seed of the random geenrator
	private static int K; //K value of the K-NN classifier
	private static int individuals; //size of the combinators population
	private static int nSubpopulations; //number of subpopulations
	private static int MAX_GENERATIONS; //Maximum number of generations
	private static int M; //Generations per epoch of selector populations
	private static int N; //Generations per epoch of combinators population
	private static double W; //Weight of combinators fitness function
	private static double mutationProb; //Mutation probability
	private static int subpopSize; //size of the subpopulations
	private static double WError; //Weight for Error in fitness function
	private static double WReduction; //Weight for Reduction in fitness function
	private static double WDifference; //Weight for Difference in fitness function
	private static double Elitism; //Percentage of member affected by elitism
	private static double PRnn; //Probability of application of RNN mutation
	private static double PRandom; //Probability of application of random mutation
	private static double PBit; //Probability of bit change in random mutation
	
	//Other data structures
	private static double dataTrain [][]; //training data
	private static int outputTrain [];  //training output
	private int assignation [];	// assignation of instances to strata
	private int strataSize []; // size of each strata
	
	private static int combinators [][]; // population of combinators
	private static double cFitness[]; //fitness value of the population of combinators
	private static double cAcc[]; // accuracy rate of the population of combinators
	
	private static Subpopulation selectors []; //selector subpopulations
	
	private static int ISSelection[]; //Current instance selection vector for K-NN
	private static double minDist[]; //auxiliary vector for K-NN classifier
	private static int nearestN[];//auxiliary vector for K-NN classifier
	private static int selectedClasses[];//auxiliary vector for K-NN classifier
		
	private static int generations; //current number of generations
	private static int nClasses; //number of classes in the training set

	/**
     * Default builder. Process the configuration file
     *
     * @param ficheroScript Configuration file
     *
     */
	public CoCoIS (String ficheroScript) {
		  super (ficheroScript);
	}//end-method
	
	/**
	 * Computes strata distribution for the subpopulations
	 */
	private void computeStrata(){
		
		int counter;
		int lastClass;
		
		//assign strata
		assignation=new int[dataTrain.length];
		strataSize=new int [nSubpopulations];
			
		Arrays.fill(strataSize, 0);
		
		//sort instances by class
		
		for(int i=0;i<dataTrain.length;i++){
			for(int j=i+1;j<dataTrain.length;j++){			
				if(outputTrain[j]<outputTrain[i]){
					swapInstances(i,j);
				}
			}
		}
		
		//assing instances mantaining class distribution
		counter=0;
		lastClass=outputTrain[0];
		for(int i=0;i<dataTrain.length;i++){
			
			if(lastClass==outputTrain[i]){
				
				assignation[i]=counter;
				strataSize[counter]++;
				counter=(counter+1)%nSubpopulations;

			}
			else{
				counter=0;
				assignation[i]=counter;
				strataSize[counter]++;
				counter=(counter+1)%nSubpopulations;
				lastClass=outputTrain[i];
			}
		}
		
		//sort instances by asignation
		for(int i=0;i<dataTrain.length;i++){
			for(int j=i+1;j<dataTrain.length;j++){			
				if(assignation[j]<assignation[i]){
					swapInstances(i,j);
				}
			}
		}
		
	}//end-method
	
	/**
	 * Swaps two instances
	 * @param a First instance
	 * @param b Second instance
	 */
	private void swapInstances(int a,int b){
		
		int aux;
		double auxD, auxR;
		int auxN,auxA;
		boolean auxM;
    
		//swap data
		for(int i=0;i<dataTrain[0].length;i++){

	          auxD=dataTrain[a][i];
	          auxR=realTrain[a][i];
	          auxN=nominalTrain[a][i];
	          auxM=nulosTrain[a][i];
	                             
	          dataTrain[a][i]=dataTrain[b][i];
	          realTrain[a][i]=realTrain[b][i];
	          nominalTrain[a][i]=nominalTrain[b][i];
	          nulosTrain[a][i]=nulosTrain[b][i];
	          
	          dataTrain[b][i]=auxD;
	          realTrain[b][i]=auxR;
	          nominalTrain[b][i]=auxN;
	          nulosTrain[b][i]=auxM;

		}
		
		//swap class attribute
		aux=outputTrain[a];
		outputTrain[a]=outputTrain[b];
		outputTrain[b]=aux;
		
		//swap assignation
		auxA=assignation[a];
		assignation[a]=assignation[b];
		assignation[b]=auxA;
		
	}//end-method
	
	/**
	 * Executes CCIS
	 */
	public void ejecutar () {

		//data formatting
	    double conjS[][]; //selected data
	    double conjR[][]; //selected data (real values)
	    int conjN[][]; //selected data (nominal values)
	    boolean conjM[][]; //selected data (missing values)
	    int clasesS[]; //selected data (output values)
	    
	    double strataData [][]; //strata distribution
	    int strataOutput []; //strata output values
	    
	    int father, mother; //parents for the crossover operator
	    int up, down; //points of the crossover operator
	    int childA [], childB []; //offspring
	    
	    int aux; //auxiliary variable
	    int count; //count variable
	    
	    Randomize.setSeed (seed);
	    
	    outputTrain=new int[clasesTrain.length];
	    dataTrain=new double[datosTrain.length][datosTrain[0].length];
	                
	    for(int i=0;i<datosTrain.length;i++){
	    
	    	for(int j=0;j<datosTrain[0].length;j++){
	    		dataTrain[i][j]=datosTrain[i][j];
	    	}
	    	
	    	outputTrain[i]=clasesTrain[i];
	    }
	    
	    ISSelection=new int [dataTrain.length];
		nearestN = new int[K];
		minDist = new double[K];
		
	    computeStrata();
	    
	    /*Getting the number of different classes*/
	    nClasses = 0;
	    for (int i=0; i<outputTrain.length; i++){
	      if (outputTrain[i] > nClasses){
	    	  nClasses = outputTrain[i];
	      }
	    }
	    nClasses++;
	    
	    selectedClasses= new int[nClasses];
	    
	    //initialize population of combinators
	    combinators=new int [individuals][nSubpopulations];
	    cFitness=new double [individuals];
	    cAcc=new double [individuals];
	    
	    for(int i=0;i<individuals;i++){

	    	for(int j=0; j<nSubpopulations; j++){
	    		combinators[i][j]= Randomize.RandintClosed(0, subpopSize-1);
	    	}
	    	
	    	cFitness[i]=-1.0;
	    	cAcc[i]=-1.0;
	    }
	    
	    //initialize populations of selectors
	    Subpopulation.setK(K);
	    Subpopulation.setSize(subpopSize);
	    
	    Subpopulation.setWError(WError);
	    Subpopulation.setWReduction(WReduction);
	    Subpopulation.setWDifference(WDifference);
	    
	    Subpopulation.setElitism(Elitism); 
	    Subpopulation.setPRnn(PRnn);
	    Subpopulation.setPRandom(PRandom);
	    Subpopulation.setPBit(PBit);

	    selectors=new Subpopulation[nSubpopulations];
	    
	    for(int strata=0;strata<nSubpopulations;strata++){
	    
	    	//obtain Strata
	    	strataData=new double [strataSize[strata]][dataTrain[0].length];
	    	strataOutput=new int [strataSize[strata]];
	    	count=0;
	    	
	    	for(int i=0;i<dataTrain.length;i++){
	    		if(assignation[i]==strata){
	    			
	    			for(int j=0;j<dataTrain[0].length;j++){
	    				strataData[count][j]=dataTrain[i][j];
	    			}
	    			strataOutput[count]=outputTrain[i];		
	    			count++;
	    		}
	    		
	    	}
	    	
	    	selectors[strata]=new Subpopulation(strata,strataData,strataOutput);
	    	
	    }
	    
	    long tiempo = System.currentTimeMillis();
	    
	    /*******************************
	     * 
	     * CCIS MAIN PROCEDURE
	     * 
	     *******************************/

	    generations=0;
	    
	    while(generations<MAX_GENERATIONS){
	    	
	    	
	    	 /*******************************
		     * 
		     * Evolve combinators
		     * 
		     *******************************/
	    	for(int evolve=0;evolve<N; evolve++){
	    	
	    	
	    		//evaluate
	    		for(int i=0;i<individuals;i++){
	    		
	    			if(cFitness[i]==-1.0){
	    				cFitness[i]=fitnessFunction(i);
	    			}
	    		}
	    		
	    		//sort individuals by fitness value
	    		sortPop();

	    		//selection of parents
	    		
	    		father = rouletteSelection(cFitness);
	    	    do {
		    		mother = rouletteSelection(cFitness);
	    	    } while (mother == father);
	    	    
	    		//crossover
	    	    
	    	    childA=new int[nSubpopulations];
	    	    childB=new int[nSubpopulations];
	    	    
	    	    //select two points
	    	    up = Randomize.RandintClosed(0, nSubpopulations-1);
	    	    do {   		
		    		down = Randomize.RandintClosed(0, nSubpopulations-1);
	    	    } while (up == down);
	    	    
	    	    if(up<down){
	    	    	aux=up;
	    	    	up=down;
	    	    	down=aux;
	    	    }
	    	    
	    	    //crossover
	    	    for(int i=0;i<down;i++){
	    	    	childA[i]=combinators[father][i];
	    	    	childB[i]=combinators[mother][i];
	    	    }
	    	    
	    	    for(int i=down;i<up;i++){
	    	    	childA[i]=combinators[mother][i];
	    	    	childB[i]=combinators[father][i]; 	    	
	    	    }
	    	    for(int i=up;i<nSubpopulations;i++){
	    	    	childA[i]=combinators[father][i];
	    	    	childB[i]=combinators[mother][i];
	    	    }	    	    
	    	        	    
	    		//replacement
	    	    for(int i=0;i<nSubpopulations;i++){
	    	    	combinators[individuals-2][i]=childA[i];
	    	    	combinators[individuals-1][i]=childB[i];
	    	    }
	    	    cFitness[individuals-2]=-1.0;
	    	    cFitness[individuals-1]=-1.0;
	    	    
	    		//mutation
	    	    for(int i=0;i<individuals;i++){
	    	    	if(Randomize.Rand()<mutationProb){   		
	    	    		aux=Randomize.RandintClosed(0, nSubpopulations-1);
	    	    		combinators[i][aux]=selectors[aux].rouletteSelection();
	    	    		cFitness[i]=-1.0;
	    	    	}
	    	    }
	    		
	    		
	    	}//end for-combinators
	    	
	    	//last evaluation to asses combinators for evaluation of selectors
    		for(int i=0;i<individuals;i++){
    		
    			if(cFitness[i]==-1.0){
    				cFitness[i]=fitnessFunction(i);
    			}
    		}
    		
    		//sort individuals by fitness value
    		sortPop();
    		
	    	 /*******************************
		     * 
		     * Evolve selectors
		     * 
		     *******************************/	    	
	    	
    		for(int evolve=0;evolve<M; evolve++){
    			
    			for(int pop=0;pop<nSubpopulations;pop++){
    				
    				selectors[pop].doGeneration();
    				
    			}
    			
    			//evaluate population of combinators
        		for(int i=0;i<individuals;i++){
        		
        			if(cFitness[i]==-1.0){
        				cFitness[i]=fitnessFunction(i);
        			}
        		}
        		
        		//sort individuals by fitness value
        		sortPop();
        		
    		}

	    	generations++;
	    	
	    }//end-while main loop
	    
	  //last evaluation to asses combinators to obtain the final reduced subset
		for(int i=0;i<individuals;i++){

			if(cFitness[i]==-1.0){
				cFitness[i]=fitnessFunction(i);
			}
		}
		

		//sort individuals by fitness value
		sortPop();
	    
	    
	    /*******************************
	     * 
	     * Obtention of final reduced subset
	     * 
	     *******************************/
	    
		//build individual
		
		int body []=buildIndividual(0);

		int nSel=0;
		for(int i=0;i<body.length;i++){
			if(body[i]==1){
				nSel++;
			}
		}

	    /*Building of S set from the best cromosome obtained*/
	    
	    conjS = new double[nSel][dataTrain[0].length];
	    conjR = new double[nSel][dataTrain[0].length];
	    conjN = new int[nSel][dataTrain[0].length];
	    conjM = new boolean[nSel][dataTrain[0].length];
	    clasesS = new int[nSel];
	    for (int i=0, l=0; i<dataTrain.length; i++) {
	      if (body[i]==1) { //the instance must be copied to the solution
	        for (int j=0; j<dataTrain[0].length; j++) {
	          conjS[l][j] = dataTrain[i][j];
	          conjS[l][j] = dataTrain[i][j];
	          conjR[l][j] = realTrain[i][j];
	          conjN[l][j] = nominalTrain[i][j];
	          conjM[l][j] = nulosTrain[i][j];
	        }
	        clasesS[l] = outputTrain[i];
	        l++;
	      }
	    }

	    System.out.println("CCIS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
	    
	    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
	    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	   
	}//end-method
	
	/**
	 * Deletes the fitness value of every member which contains the
	 * given selector in the subpopulation selected
	 * 
	 * @param pop Subpopulation selected
	 * @param selector Selector given
	 */
	public static void RequestReevaluation(int pop, int selector){
		
		for(int i=0;i<individuals;i++){
			
			if(combinators[i][pop]==selector){
				cFitness[i]=-1.0;
			}
		}
	}//end-method
	
	/**
	 * Builds an individual form the selectors of the populations
	 * @param index Individual to build
	 * @return Complete individual
	 */
	private static int [] buildIndividual(int index){
		
		int item;
		int fraction[];
		int pointer;
		int body[];
		
		//build individual
		
		body=new int [dataTrain.length];
		pointer=0;
		
		for(int i=0;i<nSubpopulations;i++){
			
			item=combinators[index][i];
			fraction=selectors[i].getBody(item);
			
			for(int j=0;j<fraction.length;j++){
				body[pointer]=fraction[j];
				pointer++;
			}
		}

		return body;
		
	}//end-method 
	
	/**
	 * Builds an individual from the selectors of the populations,
	 * excepting one population
	 * @param index Individual to build
	 * @param delete Population discarded
	 * @return Complete individual
	 */
	private static int [] buildIndividualWithout(int index, int delete){
		
		int item;
		int fraction[];
		int pointer;
		int body[];
		
		//build individual
		
		body=new int [dataTrain.length];
		pointer=0;
		
		for(int i=0;i<nSubpopulations;i++){
			
			item=combinators[index][i];
			fraction=selectors[i].getBody(item);
			
			if(i==delete){
				Arrays.fill(fraction,0);
			}
			
			for(int j=0;j<fraction.length;j++){
				body[pointer]=fraction[j];
				pointer++;
			}
		}

		return body;
		
	} //end-method
	
	/**
	 * Fitness function for the combinators
	 * 
	 * @param index Individual to be evaluated
	 * @return Fitness value
	 */
	private static double fitnessFunction(int index){
		
		double fitness;
		double acc;
		double reduction;
		
		int body[];

		int count;
		
		body=buildIndividual(index);
		
		//compute reduction
		count=0;
		for(int i=0;i<body.length;i++){
			if(body[i]==1){
				count++;
			}
		}
		
		reduction= 1.0-((double)count/(double)body.length);
		
		if(reduction==1.0){
			cAcc[index]=0.0;
			return 0.0;
		}
		else{
			//compute accuracy
			acc=computeAccuracy(body);
			cAcc[index]=acc;
		}
		
		fitness= (acc*W)+(reduction*(1-W));
		
		return fitness;
		
	}//end-method
	
	/**
	 * Computes accuracy of an individual
	 * @param selection Body of the individual selected
	 * @return
	 */
	private static double computeAccuracy(int selection[]){
		
		int hits;
		int test;
		double acc;
		int old;
		
		hits=0;
		
		//copy member to the K-NN classifier
		for(int i=0;i<dataTrain.length;i++){
			ISSelection[i]=selection[i];
		}
		
		//perform classification
		for (int i=0; i<dataTrain.length; i++) {
			
			//leave-one-out
			old=ISSelection[i];
			ISSelection[i]=0;
			test=knnClassify(i);
			
			if(test==outputTrain[i]){
				hits++;
			}
			ISSelection[i]=old;
		}
		
		acc=(double)((double)hits/(double)dataTrain.length);
		
		return acc;

	}//end-method
	
	/**
	 * 
     * K-NN classifier
     * 
     * @param index Training instance to classify
     * @return Class predicted
	 */
	private static int knnClassify(int index){
		
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;
		
		Arrays.fill(minDist,Double.MAX_VALUE);
	   
	    //KNN Method starts here
	    
		for (int i=0; i<dataTrain.length; i++) {
		
			if(ISSelection[i]==1){
				
		    	dist = euclideanDistance(index,i);

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
			return outputTrain[nearestN[0]];
		}
		
		Arrays.fill(selectedClasses, 0);
	
		for (int i=0; i<K; i++) {
			selectedClasses[outputTrain[nearestN[i]]]+=1;
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
	 * Sorts the population of combinators
	 */
	private void sortPop(){
		
		for(int i=0;i<combinators.length;i++){
			for(int j=i+1;j<combinators.length;j++){			
				if(cFitness[j]>cFitness[i]){
					swapCombinators(i,j);
				}
			}
		}
	}//end-method
	
	/**
	 * Swaps two combinators
	 * @param a First combinator
	 * @param b Second combinator
	 */
	private void swapCombinators(int a,int b){
		
		double aux;
		int auxN;

		//swap data
		for(int i=0;i<combinators[0].length;i++){
			auxN=combinators[a][i];
			combinators[a][i]=combinators[b][i];
			combinators[b][i]=auxN;
		}
		
		//swap fitness
		aux=cFitness[a];
		cFitness[a]=cFitness[b];
		cFitness[b]=aux;
		
	}//end-method
	
	/**
	 * Roulette selection method
	 * @param fitness Fitness array of the individuals
	 * @return Individual selected
	 */
	private int rouletteSelection(double fitness[]){
	    
		int selected;
	    double uniform;
	    double sum[];
	    
	    //two worst individuals are not considered
	    sum=new double[fitness.length-2];
	    
	    sum[0]=fitness[0];
	    
	    for(int i=1;i<fitness.length-2;i++){
	    	sum[i]=sum[i-1]+fitness[i];
	    }

	    uniform = Randomize.Randdouble(0.0, sum[fitness.length-3]);
	    selected = 0;
	    while (uniform > sum[selected]){
	    	selected++;
	    }

	    return selected;
	    
	}//end-method
	
	/**
	 * Computes the contribution of a given selector
	 * @param pop Population of the selector
	 * @param selector Selector tested
	 * @return Contribution
	 */
	public static double getContribution(int pop, int selector){
		
		double contrib=0.0;
		boolean present[];
		int howMany=0;
		double accWithout[];
		int newSelection[]=new int [dataTrain.length];
		
		present=new boolean[individuals];
		Arrays.fill(present, false);
		
		accWithout=new double[individuals];
		Arrays.fill(accWithout, 0.0);
		
		//mark combinations where it is present
		for(int i=0;i<individuals;i++){
			
			if(combinators[i][pop]==selector){
				present[i]=true;
			}
		}
		
		if(howMany<1){
			return 0.0;
		}
		
		//compute old performance
		for(int i=0;i<individuals;i++){
			
			if((present[i])&&(cFitness[i]==-1.0)){
				fitnessFunction(i);
			}
		}
		
		//compute new performance
		for(int i=0;i<individuals;i++){
			
			if(present[i]){
	
				newSelection=buildIndividualWithout(i,pop);	
				accWithout[i]=computeAccuracy(newSelection);
			}
		}
		
		for(int i=0;i<individuals;i++){

			if(present[i]){
				contrib+=(cAcc[i]-accWithout[i]);
			}
			
		}
		
		contrib/=(double)howMany;
		
		return contrib;
		
	}//end-method

	/**
	 * Euclidean distance between two instances
	 * @param a First instance
	 * @param b Second instance
	 * @return Distance computed
	 */
	private static double euclideanDistance(int a, int b){
		
		double dist=0.0;
		double aux;
		
		for(int i=0;i<dataTrain[0].length;i++){
			aux=dataTrain[a][i]-dataTrain[b][i];
			aux=aux*aux;
			dist+=aux;
		}
		
		//sqrt avoided to speed up the algorithm
		
		return dist;
		
	}//end-method
	
	/**
	 * Process the configuration file
	 * 
	 * @param ficheroScript Configuration file
	 */
	public void leerConfiguracion (String ficheroScript) {

		    String fichero, linea, token;
		    StringTokenizer lineasFichero, tokens;
		    byte line[];
		    int i, j;

		    ficheroSalida = new String[2];

		    fichero = Fichero.leeFichero (ficheroScript);
		    lineasFichero = new StringTokenizer (fichero,"\n\r");

		    lineasFichero.nextToken();
		    linea = lineasFichero.nextToken();

		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();

		    /*Getting the name of the training and test files*/
		    line = token.getBytes();
		    for (i=0; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroTraining = new String (line,i,j-i);
		    for (i=j+1; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroTest = new String (line,i,j-i);

		    /*Getting the path and base name of the results files*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();

		    /*Getting the names of the output files*/
		    line = token.getBytes();
		    for (i=0; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroSalida[0] = new String (line,i,j-i);
		    for (i=j+1; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroSalida[1] = new String (line,i,j-i);

		    /*Getting the seed*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    seed = Long.parseLong(tokens.nextToken().substring(1));

		    /*Getting the K parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    K = Integer.parseInt(tokens.nextToken().substring(1));
		    
		    /*Getting the number of individuals of the population of combinators*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    individuals = Integer.parseInt(tokens.nextToken().substring(1));
		    
		    /*Getting the number of subpopulations*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    nSubpopulations = Integer.parseInt(tokens.nextToken().substring(1));

		    /*Getting the number of max generations*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    MAX_GENERATIONS = Integer.parseInt(tokens.nextToken().substring(1));
		    
		    /*Getting the M parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    M = Integer.parseInt(tokens.nextToken().substring(1));		    

		    /*Getting the N parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    N = Integer.parseInt(tokens.nextToken().substring(1));
		    
		    /*Getting the W weight parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    W = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the mutation probability*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    mutationProb = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the subpopulation size*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    subpopSize = Integer.parseInt(tokens.nextToken().substring(1));
		    
		    /*Getting the WError weight parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    WError = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the WReduction weight parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    WReduction = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the WDifference weight parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    WDifference = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the Elitism parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    Elitism = Double.parseDouble(tokens.nextToken().substring(1));

		    /*Getting the PRnn parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    PRnn = Double.parseDouble(tokens.nextToken().substring(1));
		    
		    /*Getting the PRandom parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    PRandom = Double.parseDouble(tokens.nextToken().substring(1));		    

		    /*Getting the PBit parameter*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    PBit = Double.parseDouble(tokens.nextToken().substring(1));	
	 
	}//end-method
	
}//end-class


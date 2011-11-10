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
 * File: IDIBL.java
 * 
 * The Integrated Decremental Instance Based Learning algorithm.
 * A complete IBL classifier, wich uses IVDM distance, Drop4 prunning
 * algorithm, and a tunning algorithm to optimize the parameters used in
 * the final K-nn classification process 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 16/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.IDIBL;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.*;

public class IDIBL extends LazyAlgorithm{
	
	//Parameters
	
	int MAXK;
	int firstTimeLimit;
	int secondTimeLimit;
	
	int K;
	int kernel;
	boolean avgK;
	double wK;
	
	//Constants

	private static final int MAJORITY = 0;
	private static final int LINEAR = 1;
	private static final int GAUSSIAN = 2;
	private static final int EXPONENTIAL = 3;
	
	private static final int TUNE_K = 0;
	private static final int TUNE_KERNEL = 1;
	private static final int TUNE_AVGK = 2;
	private static final int TUNE_WK = 3;
	
	//Adictional structures
	
	double probabilities [][][];
	int countInstances [][];
	int countClassInstances [][][];
	int maxIntervals;
	double intervalsLenght[];
	
	double minValues[];
	double maxValues[];
	
	int indexTrainData[];
	
	NQueue neighbourhood[];

	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public IDIBL (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="IDIBL";    
		
		//Inicialization of auxiliar structures
	    
		maxIntervals= Math.max(nClasses,5);
	    
		probabilities= new double[inputAtt][maxIntervals][nClasses];
		countInstances=new int[inputAtt][maxIntervals];
		countClassInstances=new int[inputAtt][maxIntervals][nClasses];
		
		for(int i=0;i<inputAtt;i++){
			for(int j=0;j<maxIntervals;j++){
				countInstances[i][j]=0;
				for(int k=0;k<nClasses;k++){
					countClassInstances[i][j][k]=0;
				}	
			}			
		}
		
		intervalsLenght=new double [inputAtt];		
		minValues=new double [inputAtt];
		maxValues=new double [inputAtt];
		
		for(int i=0;i<inputAtt;i++){
			minValues[i]=0.0;
			maxValues[i]=1.0;		
			intervalsLenght[i]=(double)(maxValues[i]-minValues[i])/(double)maxIntervals;
		}
		
		indexTrainData=new int [trainData.length];
		
		for(int i=0;i<trainData.length;i++){
			indexTrainData[i]=i;
		}
	    
	    //Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime(); 
		
	} //end-method     

	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    
	    //Getting the seed
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    seed = Long.parseLong(tokens.nextToken().substring(1));
	    
	    //Getting the MAXK parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    MAXK = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the MAXK parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    firstTimeLimit = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the MAXK parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    secondTimeLimit = Integer.parseInt(tokens.nextToken().substring(1));

	} //end-method

	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class selected
	 * 
	 */
	protected int evaluate(double example[]){
		
		int size;
		NQueue neigbours;
		double distance;
		double votes[];
		double vote;
		double maxVotes;
		int classSelected;
		int selected;

		NQueue.setMAX_SIZE(K);	
		
		votes=new double[nClasses];
		
		for(int i=0;i<votes.length;i++){	
			votes[i]=0.0;
		}
		
		//Get K-Nearest neighbors 

		size=trainData.length;
		neigbours=new NQueue();
		
		for(int i=0;i<size;i++){
			neigbours.insert(new Neighbour(i,IVDM(trainData[i],example),i));
		}

		for(int i=0;i<K;i++){

			distance=neigbours.get(i).getDistance();
			
			//voting process
			vote=calculateVote(neigbours,distance,K,kernel,wK,avgK);
			
			classSelected=trainOutput[neigbours.get(i).getTrainInstance()];
			votes[classSelected]+=vote;	

		}

		//select output
		selected=-1;
		maxVotes=-1;
			
		for(int i=0;i<votes.length;i++){
			if(maxVotes<votes[i]){			
				maxVotes=votes[i];
				selected=i;
			}
		}
		
		return selected;
	} //end-method   
	
	/** 
	 * Calculates IVDM distance between two instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * @return Distance calculated
	 * 
	 */
	private double IVDM(double a[],double b[]){
		
		double sum;
		double value;
		double aux;
		
		sum=0.0;
		
		for(int i=0;i<inputAtt;i++){
			
			value=0.0;
			for(int j=0;j<nClasses;j++){

				aux=probabilities[i][discretize(a[i],i)][j];
				aux-=probabilities[i][discretize(b[i],i)][j];
				
				value+=aux*aux;
			}
			
			sum+=value*value;
		}
		
		return sum;
		
	} //end-method   
	
	/** 
	 * Evaluates a instance to predict its class.Use only instances not
	 * rejected by drop 4 algorithm
	 * 
	 * @param instance Instance evaluated 
	 * @param acepted Instances acepted by drop 4
	 * @param position Index of instance
	 * @return Class selected
	 * 
	 */
	 private int evaluateDrop4(double instance[],boolean acepted[],int position){

		double votes[];
		double dist;
		double vote;
		double maxVotes;
		int classSelected;
		int selected;
		int used;
		
		votes=new double[nClasses];
		
		for(int i=0;i<votes.length;i++){	
			votes[i]=0.0;
		}

		used=0;		
		for(int i=0;used<K&&i<neighbourhood[position].getSize();i++){
			
			//calcula una distancia
			dist=neighbourhood[position].get(i).getDistance();
					
			//es ponderada
			vote=calculateVote(neighbourhood[position],dist,K,kernel,wK,avgK);

			//emite su voto (peso, clase)
					
			classSelected=trainOutput[neighbourhood[position].get(i).getTrainInstance()];
			votes[classSelected]+=vote;	
			used++;
		}
		
		selected=-1;
		maxVotes=-1;
			
		for(int i=0;i<votes.length;i++){
			if(maxVotes<votes[i]){			
				maxVotes=votes[i];
				selected=i;
			}
		}
			
		return selected;
		
	} //end-method  
	
	/** 
	 * Evaluates a instance to predict its class.Use only instances not
	 * rejected by drop 4 algorithm, and does not use the instance
	 * selected actually in removal process
	 * 
	 * @param instance Instance evaluated 
	 * @param acepted Instances acepted by drop 4
	 * @param position Index of instance
	 * @return Class selected
	 * 
	 */
	 private int evaluateDrop4WithElimination(double instance[],boolean acepted[],int position){

		double votes[];
		double dist;
		double vote;
		double maxVotes;
		int classSelected;
		int selected;
		int used;
		
		votes=new double[nClasses];
		
		for(int i=0;i<votes.length;i++){	
			votes[i]=0.0;
		}

		used=0;		
		for(int i=0;used<K&&i<neighbourhood[position].getSize();i++){

			if(neighbourhood[position].get(i).getTrainInstance()!=position){
				
				//calcula una distancia
				dist=neighbourhood[position].get(i).getDistance();
					
				//es ponderada
				vote=calculateVote(neighbourhood[position],dist,K,kernel,wK,avgK);

				//emite su voto (peso, clase)
					
				classSelected=trainOutput[neighbourhood[position].get(i).getTrainInstance()];
				votes[classSelected]+=vote;	
				used++;
			}	
		}
		
		selected=-1;
		maxVotes=-1;
			
		for(int i=0;i<votes.length;i++){
			if(maxVotes<votes[i]){			
				maxVotes=votes[i];
				selected=i;
			}
		}
			
		return selected;
		
	} //end-method  
	
	/** 
	 * Find probabilities to calculate IVDM distances
	 *
	 */
	public void findProbabilities(){
		
		int clas;
		int value;
		
		for(int att=0;att<inputAtt;att++){
			
			for(int i=0;i<trainData.length;i++){
			
				clas=trainOutput[i];
				value=discretize(trainData[i][att],att);
				
				countClassInstances[att][value][clas]++;
				countInstances[att][value]++;				
			}

			for(int i=0;i<maxIntervals;i++){

				for(int j=0;j<nClasses;j++){
					if(countClassInstances[att][i][j]==0){
						probabilities[att][i][j]=0.0;
					}
					else{
						probabilities[att][i][j]=
							(double)countClassInstances[att][i][j]/countInstances[att][i];					
					}
				}
			}
			
		}
		
	} //end-method   

	/** 
	 * Discretize a value
	 * 
	 * @param value Value to discretize
	 * @param att Index of the attribute used
	 * @return Result of the discretization
	 *
	 */
	private int discretize(double value,int att){
		
		int result;
		
		if(value>=maxValues[att]){
			result=maxIntervals-1;
		}else{
			if(value<=minValues[att]){
				result=0;
			}
			else{
				result=((int)Math.floor((value-minValues[att])/intervalsLenght[att]));				
			}
		}
		return result;
	}//end-method   
	
	/** 
	 * Finds MAXK Nearest Neighbors for every reference instance
	 * 
	 */
	public void findNeigbours(){
		
		int size;

		NQueue.setMAX_SIZE(MAXK+1);
		
		size=referenceData.length;
		neighbourhood=new NQueue[size];
		
		for(int i=0;i<size;i++){
			neighbourhood[i]=new NQueue();
		}
		for(int i=0;i<size;i++){
			for(int j=0;j<trainData.length;j++){
				if(same(referenceData[i],trainData[j])==false){
					neighbourhood[i].insert(new Neighbour(indexTrainData[j],IVDM(referenceData[i],trainData[j]),j));
				}
			}
		}
				
	}//end-method   

	/** 
	 * Finds best parameters for drop 4 algorithm
	 * 
	 * @param timeLimit Limit of iterations
	 * 
	 */
	public void findParameters(int timeLimit){
		
		double bestCVC;
		double currentCVC;
		double newCVC;
		int bestKernel;
		int bestK;
		double bestWK;
		double stepWK;
		int time;
		int newK;
		int newKernel=LINEAR;
		boolean newAvgK=false;
		double newWK=0.5;	
		int tune;
		int newTune;
		boolean firstIteration;
		
		//initialize
		
		bestCVC=0;
		time=0;
		
		//initialize parameters
		
		K=3;
		kernel=LINEAR;
		avgK=false;
		wK=0.5;
		
		newK=3;
		newKernel=LINEAR;
		newAvgK=false;
		newWK=0.5;
		
		tune=-1;
		
		firstIteration=true;
		
		bestCVC=calcCVC(newK,newKernel,newWK,newAvgK);
	
		while(time<timeLimit){
			
			//random select tune
			
			do{
				newTune=Randomize.Randint(0,4);
				
			}while(newTune==tune);

			if(firstIteration){
				tune=TUNE_K;
				firstIteration=false;
			}
			else{
				tune=newTune;
			}
			
			currentCVC=0.0;
						
			switch (tune){
			
				case TUNE_K:  //K Neighbors
					
						currentCVC=0.0;
						bestK=K;

						for(int i=2;i<=MAXK;i++){
							
							if(i!=K){
								
								newK=i;
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);

								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestK=i;
								}
								
							}							
							
						}

						newK=bestK;
					break;
					
				case TUNE_KERNEL:  //Weighting kernel
					
						currentCVC=0.0;
						bestKernel=kernel;
						
						for(int i=1;i<=3;i++){
							
							if(i!=kernel){
								
								newKernel=i;
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
								
								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestKernel=i;
								}
								
							}
						}
						
						newKernel=bestKernel;
					
					break;
				case TUNE_AVGK:  //Use average of K
						
						if(avgK==true){
							newAvgK=false;
						}
						else{
							newAvgK=true;
						}
						
						currentCVC=calcCVC(newK,newKernel,newWK,newAvgK);
						
					
					break;
					
				case TUNE_WK: //Value of Wk
					
						currentCVC=0.0;
						bestWK=wK;

						for(int i=0;i<=10;i++){
							
							newWK=i*0.1;
							newCVC=calcCVC(newK,newKernel,newWK,newAvgK);

							if(newCVC>currentCVC){
								currentCVC=newCVC;
								bestWK=newWK;
							}
						}

						stepWK=bestWK;

						for(int i=-9;i<=9;i++){
							
							newWK=(i*0.01)+stepWK;
							if(newWK>=0.0 && newWK<=1.0){
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
								
								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestWK=newWK;
								}

							}
							
						}

						stepWK=bestWK;
						
						for(int i=-9;i<=9;i++){
							
							newWK=(i*0.001)+stepWK;
							
							if(newWK>=0.0 && newWK<=1.0){
							
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
							
								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestWK=newWK;
								}

							}
						}						
						
						newWK=bestWK;

					break;
			}
			
			if(currentCVC>bestCVC){
				
				bestCVC=currentCVC;
				
				K=newK;
				kernel=newKernel;
				avgK=newAvgK;
				wK=newWK;

				time=0;
			}
			else{
				newK=K;
				newKernel=kernel;
				newAvgK=avgK;
				newWK=wK;				
				time++;
			}
			
		}
		
		newKernel=MAJORITY;		
		currentCVC=0.0;
		
		bestK=K;
		
		for(int i=1;i<=30;i++){
	
			newK=i;
			newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
			
			if(newCVC>currentCVC){
				currentCVC=newCVC;
				bestK=i;
			}
		}
		
		newK=bestK;
		
		if(currentCVC>bestCVC){
			K=newK;
			kernel=newKernel;
			avgK=newAvgK;
			wK=newWK;
		}

	} //end-method   
	
	/** 
	 * Calculate Cross-Validation Confidence for actual settings
	 * 
	 * @param newK New value of K
	 * @param newKernel New value of Kernel 
	 * @param newWK New value of Wk
	 * @param newAvgK New value of avgK
	 * @return Confidence value
	 * 
	 */

	private double calcCVC(int newK,int newKernel,double newWK,boolean newAvgK){
		
		double result;
		int correct;
		int realClass;
		double votes [];
		int votesCount [];
		double maxVotes;
		int selected;
		double sumVotes;
		double sumCorrectVotes;
		double dist;
		double vote;
		int classSelected;
		
		result=0.0;
		correct=0;
		sumVotes=0.0;
		sumCorrectVotes=0.0;
		
		votes=new double[nClasses];
		votesCount=new int[nClasses];
		
		for(int i=0;i<referenceData.length;i++){
			
			realClass=referenceOutput[i];
			
			for(int j=0;j<votes.length;j++){
				
				votes[j]=0.0;
				votesCount[j]=0;
			
			}
	
			for(int j=0;j<newK;j++){
							
				//calculate distance
				dist=neighbourhood[i].get(j).getDistance();
				
				//voting process
				vote=calculateVote(neighbourhood[i],dist,newK,newKernel,newWK,newAvgK);

				classSelected=referenceOutput[neighbourhood[i].get(j).getInstance()];
				votes[classSelected]+=vote;
				votesCount[classSelected]++;
				
			}
			
			selected=-1;
			maxVotes=-1;

			for(int j=0;j<votes.length;j++){

				if(maxVotes<votes[j]){			
					maxVotes=votes[j];
					selected=j;
				}
				
				sumVotes+=votes[j];
			}
			
			
			if(selected==realClass){
				correct++;
			}
			sumCorrectVotes+=votes[selected];

		}

		//calculate CVC
		result=(correct+(sumCorrectVotes/sumVotes))/(referenceData.length+1);
		
		return result;
	} //end-method   
	
	/** 
	 * Calculate vote value
	 * 
	 * @param instance which votes 
	 * @param dist Distance used 
	 * @param newK New value of K
	 * @param newKernel New value of Kernel 
	 * @param newWK New value of Wk
	 * @param newAvgK New value of avgK
	 * @return Vote value
	 * 
	 */
	private double calculateVote(NQueue neig,double dist,int newK, int newKernel,double newWK,boolean newAvgK){
		
		double result;
		double Dk;
		
		result=0.0;
		
		//get Dk

		if(newAvgK==true){
			
			Dk=0.0;
			
			for(int i=0;i<newK;i++){
				Dk+=neig.get(i).getDistance();
			}
			Dk+=Dk;
			Dk/=newK+1;

		}
		else{
			Dk=neig.get(newK-1).getDistance();
			
		}

		//All neigbours are at the same IVDM distance
		if(Dk==0){

			result=1.0;
			
			return result;
		}
		
		switch (newKernel){
		
			case MAJORITY: 	result=1.0;
						break;
						
			case LINEAR:	result=(1.0-newWK)*(Dk-dist);
							result/=Dk;
							result+=newWK;
						break;
			case GAUSSIAN:
							result=Math.pow(newWK, (dist*dist)/(Dk*Dk));
						break;
						
			case EXPONENTIAL:
							result=Math.pow(newWK, dist/Dk);
						break;
		}
		
		return result;
		
	} //end-method   

	/** 
	 * Prune instance set using drop 4 algorithm
	 * 
	 */
	public void pruneInstanceSet(){
		
		boolean acepted[];
		int selected;
		double enemy[];
		int realClass;
		double distance;
		int further;
		double maxDistance;
		double editedData[][];
		int editedOutput[];
		int pointer;
		int position;
		
		acepted=new boolean [trainData.length];
		
		for(int i=0;i<trainData.length;i++){

			acepted[i]=true;		
		}

		//Initial filter
		
		//Drops an instance if it is misclassified and not helps.
		
		for(int i=0;i<trainData.length;i++){
		
			selected=evaluateDrop4(trainData[i],acepted,indexTrainData[i]);

			if(selected!=trainOutput[i]){
				acepted[i]=removeIfNotHelping(i,acepted);
			}
		}
		
		//get the new train data
		
		editedData=new double [trainData.length][inputAtt];
		editedOutput=new int [trainData.length];	
		indexTrainData=new int [trainData.length];
		
		pointer=0;
		
		for(int i=0;i<trainData.length;i++){
			
			if(acepted[i]){
				System.arraycopy(trainData[i], 0, editedData[pointer], 0, inputAtt);
				editedOutput[pointer]=trainOutput[i];
				indexTrainData[pointer]=i;
				pointer++;
			}		
		}
		
		trainData=new double [pointer][inputAtt];
		trainOutput=new int [pointer];
		
		for(int i=0;i<trainData.length;i++){
			
			System.arraycopy(editedData[i], 0, trainData[i], 0, inputAtt);
			trainOutput[i]=editedOutput[i];
		}
		
		acepted=new boolean [trainData.length];
		
		for(int i=0;i<trainData.length;i++){

			acepted[i]=true;		
		}
		
		//build new queues
		neighbourhood=new NQueue[trainData.length];
		findNeigbours();
		
		//Second filter

		//Sort instances by nearest enemy, and drop if not helps.
		
		enemy=new double[trainData.length];
		
		for(int i=0;i<trainData.length;i++){
			
			realClass=trainOutput[i];
			distance=0.0;
			position=indexTrainData[i];
			
			for(int j=0;j<neighbourhood[position].getSize()&&distance<=0.0;j++){
				
				if(referenceOutput[neighbourhood[position].get(j).getInstance()]!=realClass){
					distance=neighbourhood[position].get(j).getDistance();
				}
			}
			enemy[i]=distance;	
		}
		
		for(int i=0;i<trainData.length;i++){
			
			further=-1;
			maxDistance=-1.0;
			
			for(int j=0;j<enemy.length;j++){
				
				if(enemy[j]>maxDistance){
					maxDistance=enemy[j];
					further=j;
				}
			}
			
			if(further!=-1){
				enemy[further]=-1.0;
				acepted[further]=removeIfNotHelping(further,acepted);
			}
			else{
				i=trainData.length;
			}
		}
		
		//get the new train data
		
		editedData=new double [trainData.length][inputAtt];
		editedOutput=new int [trainData.length];	
		pointer=0;
		
		for(int i=0;i<trainData.length;i++){
			
			if(acepted[i]){
				System.arraycopy(trainData[i], 0, editedData[pointer], 0, inputAtt);
				editedOutput[pointer]=trainOutput[i];
				indexTrainData[pointer]=indexTrainData[i];
				pointer++;
			}
			
		}
		
		trainData=new double [pointer][inputAtt];
		trainOutput=new int [pointer];
		
		for(int i=0;i<trainData.length;i++){
			
			System.arraycopy(editedData[i], 0, trainData[i], 0, inputAtt);
			trainOutput[i]=editedOutput[i];

		}
		
		//build new queues
		
		if(trainData.length<K-1){
			K=trainData.length-1;
		}
		MAXK=K;
		
		neighbourhood=new NQueue[trainData.length];
		findNeigbours();
		
	} //end-method   
	
	/** 
	 * Removes a instance if it is not helping in the classification
	 * 
	 * @param position Index in train data
	 * @param acepted Train data instances accepted by drop 4
	 * @return True if the instance can be removed
	 * 
	 */
	private boolean removeIfNotHelping(int position,boolean acepted[]){
		
		int correctWith;
		int correctWithOut;
		
		correctWith=0;
		correctWithOut=0;

		for(int i=0;i<referenceData.length;i++){

			if(evaluateDrop4(referenceData[i],acepted,i)==referenceOutput[i]){
				correctWith++;		
			}
			
		}
		
		acepted[position]=false;

		for(int i=0;i<referenceData.length;i++){

			if(evaluateDrop4WithElimination(referenceData[i],acepted,position)==referenceOutput[i]){
					correctWithOut++;		
			}
			
		}

		if(correctWith>correctWithOut){
			return true;
		}
		else{
			return false;
		}

	} //end-method   
	
	
	/** 
	 * Finds best parameters for classification
	 * 
	 * @param timeLimit Limit of iterations
	 * 
	 */
	public void reTuneParameters(int timeLimit){
		
		double bestCVC;
		double currentCVC;
		double newCVC;
		int bestKernel;
		int bestK;
		double bestWK;
		double stepWK;
		int time;
		int newK;
		int newKernel;
		boolean newAvgK;
		double newWK;	
		int tune;
		int newTune;
		boolean firstIteration;
		
		
		//initialize
		
		bestCVC=0;
		time=0;
		
		//initialize parameters
		
		newK=K;
		newKernel=kernel;
		newAvgK=avgK;
		newWK=wK;
		
		tune=-1;
		
		firstIteration=true;

		bestCVC=calcCVC(newK,newKernel,newWK,newAvgK);

		while(time<timeLimit){

			//random select tune
			
			do{
				newTune=Randomize.Randint(0,4);
				
			}while(newTune==tune);

			if(firstIteration){
				tune=TUNE_K;
				firstIteration=false;
			}
			else{
				tune=newTune;
			}
			
			currentCVC=0.0;
						
			switch (tune){
			
				case TUNE_K: // K Neighbors
					
						currentCVC=0.0;
						bestK=K;

						for(int i=2;i<=MAXK;i++){
							
							if(i!=K){
								
								newK=i;
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);

								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestK=i;
								}
								
							}							
							
						}

						newK=bestK;
					break;
					
				case TUNE_KERNEL: //Weighting kernel
					
						currentCVC=0.0;
						bestKernel=kernel;

						for(int i=1;i<=3;i++){
							
							if(i!=kernel){
								
								newKernel=i;
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);

								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestKernel=i;
								}
								
							}
						}
						
						newKernel=bestKernel;
					
					break;
				case TUNE_AVGK: // Use average of K
						
						if(avgK==true){
							newAvgK=false;
						}
						else{
							newAvgK=true;
						}
						
						currentCVC=calcCVC(newK,newKernel,newWK,newAvgK);
						
					break;
					
				case TUNE_WK: // Wk value
					
						currentCVC=0.0;
						bestWK=wK;

						for(int i=0;i<=10;i++){
							
							newWK=i*0.1;
							newCVC=calcCVC(newK,newKernel,newWK,newAvgK);

							if(newCVC>currentCVC){
								currentCVC=newCVC;
								bestWK=newWK;
							}
						}

						stepWK=bestWK;

						for(int i=-9;i<=9;i++){
							
							newWK=(i*0.01)+stepWK;
							if(newWK>=0.0 && newWK<=1.0){
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
								
								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestWK=newWK;
								}

							}
							
						}

						stepWK=bestWK;
						
						for(int i=-9;i<=9;i++){
							
							newWK=(i*0.001)+stepWK;
							
							if(newWK>=0.0 && newWK<=1.0){
							
								newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
							
								if(newCVC>currentCVC){
									currentCVC=newCVC;
									bestWK=newWK;
								}

							}
						}						
						
						newWK=bestWK;

					break;
			}
			
			if(currentCVC>bestCVC){
				
				bestCVC=currentCVC;
				
				K=newK;
				kernel=newKernel;
				avgK=newAvgK;
				wK=newWK;

				time=0;
			}
			else{
				newK=K;
				newKernel=kernel;
				newAvgK=avgK;
				newWK=wK;				
				time++;
			}
			
		}

		newKernel=MAJORITY;		
		currentCVC=0.0;
		
		bestK=K;
		
		for(int i=1;i<=MAXK;i++){
	
			newK=i;
			newCVC=calcCVC(newK,newKernel,newWK,newAvgK);
			
			if(newCVC>currentCVC){
				currentCVC=newCVC;
				bestK=i;
			}
		}
		
		newK=bestK;
		
		if(currentCVC>bestCVC){
			K=newK;
			kernel=newKernel;
			avgK=newAvgK;
			wK=newWK;
		}

	} //end-method   

	/** 
	 * Get first tunning algorithm time limit
	 * 
	 * @return Time limit
	 * 
	 */
	
	public int getFirstTimeLimit(){
		
		return firstTimeLimit;
		
	} //end-method   
	
	/** 
	 * Get second tunning algorithm time limit
	 * 
	 * @return Time limit
	 * 
	 */
	public int getSecondTimeLimit(){
		
		return secondTimeLimit;
		
	} //end-method   

} //end-class 


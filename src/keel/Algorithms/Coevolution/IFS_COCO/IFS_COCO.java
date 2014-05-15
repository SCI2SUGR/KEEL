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
 * File: IFS_COCO.java
 * 
 * The IFS_COCO Algorithm.
 *
 * It makes use of three different preprocessing techniques in order to 
 * improve the KNN classification. Instance Selection, Feature Selection
 * and Dual Selection are considered whitin the evolutionary framework
 * 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Coevolution.IFS_COCO;

import java.util.Arrays;
import java.util.StringTokenizer;

import keel.Algorithms.Coevolution.CoevolutionAlgorithm;
import keel.Dataset.Attribute;

import org.core.Files;
import org.core.Randomize;

public class IFS_COCO extends CoevolutionAlgorithm{

	private String txtFitness,txtFitness1,txtFitness2,txtFitness3;
	private String redFitness,redFitness1,redFitness2,redFitness3;
	
	private int K;
	private int sizePop;
	private int MAX_EVALUATIONS;
	private int evaluations;
	
	private int sizeCrom;
	private int [] d;
	private double r;
	
	private int diff[];
	
	private int [] CInstances;
	private int [] CFeatures;
	
	private int [] SIInstances;
	
	private int [] SCFeatures;
	
	private int [] SCIInstances;
	private int [] SCIFeatures;
	
	private int [][][] population;
	private double [][] popFitness;
	
	private int [][] newPop;
	private double [] newPopFitness;
	
	private int [] bestSIOutput;
	private int [] bestSCOutput;
	private int [] bestSCIOutput;
	
	private int MODE;
	
	private static final int ALL=0;
	private static final int SI=1;
	private static final int SC=2;
	private static final int SCI=3;
	
	private static final int NO_EVAL=-2;
	
	private double points[];
	private double newPoints[];
	
	private int bestClassifier;
	private double MAX_POINTS;
	
	private double bestFitness;
	private int bestFitnessPop;
	
	private int [] bestSIArray;
	private int [] bestSCArray;
	private int [] bestSCIArray;
	private int [] bestSCCArray;
	
	private int [] lastSIArray;
	private int [] lastSCArray;
	private int [] lastSCIArray;
	private int [] lastSCCArray;
	
	private double alpha;
	private double beta;
	private double prob0to1;
	
	private int actualPop;
	
	private int trainRealClass[][];
	private int trainPrediction[][];
	private int testRealClass[][];
	private int testPrediction[][];	
	private int testUnclassified;	
	private int trainUnclassified;	
	private int testConfMatrix[][];
	private int trainConfMatrix[][];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public IFS_COCO (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="IFS_COCO";
		
		sizeCrom=trainData.length+inputAtt;
		r=0.35;
		evaluations=0;
		
		bestFitness=-1;
		
		//Inicialization of auxiliar structures
		CInstances=new int [trainData.length];
		CFeatures=new int [inputAtt];
		
		SIInstances=new int [trainData.length];
		SCIInstances=new int [trainData.length];
		
		SCFeatures=new int [inputAtt];
		SCIFeatures=new int [inputAtt];
		
		bestSIOutput=new int [trainData.length];
		bestSCOutput=new int [trainData.length];
		bestSCIOutput=new int [trainData.length];
		
		for(int i=0;i<CInstances.length;i++){
			CInstances[i]=1;
		}
		
		for(int i=0;i<CFeatures.length;i++){
			CFeatures[i]=1;
		}
		
		population=new int [3][sizePop][sizeCrom];
		popFitness=new double [3][sizePop];
		
		for(int i=0;i<popFitness[0].length;i++){
			popFitness[0][i]=-1;
			popFitness[1][i]=-1;
			popFitness[2][i]=-1;
		}
		
		d=new int[6];
		
		d[0]=trainData.length/4;
		d[1]=inputAtt/4;
		d[2]=sizeCrom/4;
		
		d[3]=(int) (r*(1.0-r)*(d[0]*4));
		d[4]=(int) (r*(1.0-r)*(d[1]*4));
		d[5]=(int) (r*(1.0-r)*(d[2]*4));
		
		points=new double[3];
		newPoints=new double[3];
		
		points[0]=0.0;
		points[1]=0.0;
		points[2]=0.0;
		
		bestSIArray= new int [trainData.length];
		bestSCArray= new int [inputAtt];
		bestSCIArray= new int [trainData.length];
		bestSCCArray= new int [inputAtt];
		
		lastSIArray= new int [trainData.length];
		lastSCArray= new int [inputAtt];
		lastSCIArray= new int [trainData.length];
		lastSCCArray= new int [inputAtt];
		
		//Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
	    txtFitness=txtFitness1=txtFitness2=txtFitness3="";
	    redFitness=redFitness1=redFitness2=redFitness3="";
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
		String mode;
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

	    //Getting the population size parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    sizePop = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the K parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    K = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the MAX EVALUATIONS parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    MAX_EVALUATIONS = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the MODE
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    mode = tokens.nextToken().substring(1);
	    
	    MODE=ALL;

	    if(mode.equalsIgnoreCase("SI")){
	    	MODE=SI;
	    }
	    if(mode.equalsIgnoreCase("SC")){
	    	MODE=SC;
	    }
	    if(mode.equalsIgnoreCase("SCI")){
	    	MODE=SCI;
	    }
	    
	    //Getting the alpha parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    alpha = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    beta = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the prob reduce parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    prob0to1 = Double.parseDouble(tokens.nextToken().substring(1));

	}//end-method

	/** 
	 * Performs the co-evolutionary process
	 * 
	 */	
	public void coevolution(){

		//initial population
		randomGenerate();
		
		if(MODE==ALL){
			evaluateInitialSI();
			evaluateInitialSC();
			evaluateInitialSCI();
			
			//search best of each one
			sortPop(0);
			sortPop(1);
			sortPop(2);
		
			System.arraycopy(population[0][0], 0, SIInstances, 0, trainData.length);
			System.arraycopy(population[1][0], trainData.length, SCFeatures, 0, inputAtt);
			System.arraycopy(population[2][0], 0, SCIInstances, 0, trainData.length);
			System.arraycopy(population[2][0], trainData.length, SCIFeatures, 0, inputAtt);
			
			//Obtain output of the best chromosome
			
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			bestSIOutput=evaluateTrain();
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			bestSCOutput=evaluateTrain();
			
			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			bestSCIOutput=evaluateTrain();
			
			//evaluate populations
			evaluatePop(0);
			evaluatePop(1);
			evaluatePop(2);
			
			//sort
			sortPop(0);
			sortPop(1);
			sortPop(2);
		
		}
		
		if(MODE==SI){
			
			evaluateInitialSI();
			
			//best element
			sortPop(0);
		
			System.arraycopy(population[0][0], 0, SIInstances, 0, trainData.length);
			
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			bestSIOutput=evaluateTrain();
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				bestSCOutput[i]=-1;
			}
			
			//SIC Classify
			for(int i=0;i<trainData.length;i++){
				bestSCIOutput[i]=-1;
			}
			
			//evaluate population
			evaluatePop(0);
			
			//sort
			sortPop(0);
		
		}		
		
		if(MODE==SC){

			evaluateInitialSC();
			
			//best element
			sortPop(1);
		
			System.arraycopy(population[1][0], trainData.length, SCFeatures, 0, inputAtt);
			
			//SI Classify
			for(int i=0;i<trainData.length;i++){
				bestSIOutput[i]=-1;
			}
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			bestSCOutput=evaluateTrain();
			
			//SIC Classify
			for(int i=0;i<trainData.length;i++){
				bestSCIOutput[i]=-1;
			}
			
			//evaluate population
			evaluatePop(1);
			
			//sort
			sortPop(1);
		
		}
		
		if(MODE==SCI){

			evaluateInitialSCI();
			
			//best element
			sortPop(2);
		
			System.arraycopy(population[2][0], 0, SCIInstances, 0, trainData.length);
			System.arraycopy(population[2][0], trainData.length, SCIFeatures, 0, inputAtt);
			
			//SI Classify
			for(int i=0;i<trainData.length;i++){
				bestSIOutput[i]=-1;
			}
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				bestSCOutput[i]=-1;
			}
			
			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			bestSCIOutput=evaluateTrain();
			
			//evaluate population
			evaluatePop(2);
			
			//sort
			sortPop(2);
		
		}
		

		// CHC Process Init

		evaluations=0;
		
		while(evaluations<MAX_EVALUATIONS){
			
			newPoints[0]=0;
			newPoints[1]=0;
			newPoints[2]=0;
			
			bestClassifier=0;
			MAX_POINTS=points[0];
			
			if(MAX_POINTS<points[1]){
				bestClassifier=1;
				MAX_POINTS=points[1];
			}

			if(MAX_POINTS<points[2]){
				bestClassifier=2;
				MAX_POINTS=points[2];
			}

			bestFitnessPop=-1;
						
			if(MODE==ALL){

				//SI Classify
				System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
				for(int i=0;i<inputAtt;i++){
					CFeatures[i]=1;
				}		
				
				bestSIOutput=evaluateTrain();
				
				//SC Classify
				
				for(int i=0;i<trainData.length;i++){
					CInstances[i]=1;
				}
				System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
				
				bestSCOutput=evaluateTrain();
				
				//SIC Classify
				System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
				System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
				
				bestSCIOutput=evaluateTrain();
				
				System.arraycopy(SIInstances, 0, lastSIArray, 0, trainData.length);
				System.arraycopy(SCFeatures, 0, lastSCArray, 0, inputAtt);
				System.arraycopy(SCIInstances, 0, lastSCIArray, 0, trainData.length);
				System.arraycopy(SCIFeatures, 0, lastSCCArray, 0, inputAtt);
				
				actualPop=0;
				CHCEpoch(0);
				actualPop=1;
				CHCEpoch(1);
				actualPop=2;
				CHCEpoch(2);
				
				if(bestFitnessPop>=0){
					
					switch(bestFitnessPop){
					
						case 0: System.arraycopy(population[0][0], 0, bestSIArray, 0, trainData.length);
								System.arraycopy(lastSCArray, 0, bestSCArray, 0, inputAtt);
								System.arraycopy(lastSCIArray, 0, bestSCIArray, 0, trainData.length);
								System.arraycopy(lastSCCArray, 0, bestSCCArray, 0, inputAtt);
							break;
					
						case 1: System.arraycopy(lastSIArray, 0, bestSIArray, 0, trainData.length); 
								System.arraycopy(population[1][0], trainData.length, bestSCArray, 0, inputAtt);
								System.arraycopy(lastSCIArray, 0, bestSCIArray, 0, trainData.length);
								System.arraycopy(lastSCCArray, 0, bestSCCArray, 0, inputAtt);
							
							break;
							
						case 2: System.arraycopy(lastSIArray, 0, bestSIArray, 0, trainData.length); 
								System.arraycopy(lastSCArray, 0, bestSCArray, 0, inputAtt);
								System.arraycopy(population[2][0], 0, bestSCIArray, 0, trainData.length);
								System.arraycopy(population[2][0], trainData.length, bestSCCArray, 0, inputAtt);
							
							break;							
					};

				}
								
				System.arraycopy(population[0][0], 0, SIInstances, 0, trainData.length);
				System.arraycopy(population[1][0], trainData.length, SCFeatures, 0, inputAtt);
				System.arraycopy(population[2][0], 0, SCIInstances, 0, trainData.length);
				System.arraycopy(population[2][0], trainData.length, SCIFeatures, 0, inputAtt);
			
				txtFitness1+=popFitness[0][0]+"\n";
				txtFitness2+=popFitness[1][0]+"\n";
				txtFitness3+=popFitness[2][0]+"\n";
				txtFitness+=popFitness[0][0]+"\n";
				txtFitness+=popFitness[1][0]+"\n";
				txtFitness+=popFitness[2][0]+"\n";
			}
			
			if(MODE==SI){

				//SI Classify
				System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
				for(int i=0;i<inputAtt;i++){
					CFeatures[i]=1;
				}		
				
				bestSIOutput=evaluateTrain();
				
				actualPop=0;
				CHCEpoch(0);
				
				System.arraycopy(population[0][0], 0, SIInstances, 0, trainData.length);

			}
			
			if(MODE==SC){
				
				//SC Classify
				
				for(int i=0;i<trainData.length;i++){
					CInstances[i]=1;
				}
				System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
				
				bestSCOutput=evaluateTrain();
				
				actualPop=1;
				CHCEpoch(1);
				
				System.arraycopy(population[1][0], trainData.length, SCFeatures, 0, inputAtt);
			}
			
			if(MODE==SCI){
			
				//SIC Classify
				System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
				System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
				
				bestSCIOutput=evaluateTrain();
				
				actualPop=2;
				CHCEpoch(2);
				
				System.arraycopy(population[2][0], 0, SCIInstances, 0, trainData.length);
				System.arraycopy(population[2][0], trainData.length, SCIFeatures, 0, inputAtt);
			}
			
			points[0]=newPoints[0];
			points[1]=newPoints[1];
			points[2]=newPoints[2];
			
			newPoints[0]=0;
			newPoints[1]=0;
			newPoints[2]=0;

		}
		
		// CHC Process End

		if(MODE==ALL){

			System.arraycopy(bestSIArray, 0, SIInstances, 0, trainData.length);
			System.arraycopy(bestSCArray, 0, SCFeatures, 0, inputAtt);
			System.arraycopy(bestSCIArray, 0, SCIInstances, 0, trainData.length);
			System.arraycopy(bestSCCArray, 0, SCIFeatures, 0, inputAtt);
			
		}
		
		System.out.println(bestFitness);
		System.out.println(alpha);
		System.out.println(beta);
		
		//Writing results
		System.out.println(name+" "+ relation + " Evolve " + (double)(System.currentTimeMillis()-initialTime)/1000.0 + "s");
		
	}
	
	/**
	 * Performs a generation in a population
	 *
	 * @param pop Index of the population
	 *
	 */
	private void CHCEpoch(int pop){
	
		int size=0;
		boolean nuevos=false;
		
		switch(pop){
			case 0: size= trainData.length;
				break;
			case 1: size= inputAtt;
				break;
			case 2: size= sizeCrom;
				break;		
		};
		
		newPop= new int [sizePop][size];
		newPopFitness= new double [sizePop];
		

		createNewPop(pop,size);
		
		//crossover
		diff=new int[size];
		
		for(int i=0;i<newPop.length;i+=2){
		
			if(hamming(i,i+1)/2>d[pop]){
				
				HUXCross(i,i+1);
				
				newPopFitness[i]=fitness(pop,i);
				newPopFitness[i+1]=fitness(pop,i+1);
				
				nuevos=true;
			}
			else{
				newPopFitness[i]=NO_EVAL;
				newPopFitness[i+1]=NO_EVAL;
			}
		}
		
		if(nuevos){
			nuevos=mergePop(pop);
		}

		if(nuevos==false){

			d[pop]--;

			if(d[pop]<0){

				for(int i=1; i<sizePop;i++){
					System.arraycopy(population[pop][0], 0, population[pop][i], 0, sizeCrom);
				}
				
				if(actualPop==0){
					for(int i=1; i<sizePop;i++){
						for(int j=0;j<sizeCrom;j++){
							if(Randomize.Rand()<r){
								if(Randomize.Rand()<0.25){
									population[pop][i][j]=1;
								}
								else{
									population[pop][i][j]=0;
								}
							}
						}
					}
				}
				else{
					for(int i=1; i<sizePop;i++){
						for(int j=0;j<sizeCrom;j++){
							if(Randomize.Rand()<r){
								population[pop][i][j]=(population[pop][i][j]+1)%2;
							}
						}
					}
				}

				evaluatePop(pop);
				
				sortPop(pop);
				        
				d[pop]=d[pop+3];
			}
		}

	}
	
	/**
	 * Updates the population specified
	 *
	 * @param pop Index of the population
	 *
	 */
	private boolean mergePop(int pop){
	
		int index=0;
		int taken=0;
		boolean used=false;
		double bestFitness;
		int bestPosition;
		
		int [][] finalPop=new int[sizePop][sizeCrom]; 
		double [] finalPopFitness=new double[sizePop];
		
		bestFitness=-1;
		bestPosition=-1;
		for(int i=0;i<newPop.length;i++){
			if(newPopFitness[i]>bestFitness){
				bestFitness=newPopFitness[i];
				bestPosition=i;
			}
		}

		
		while(taken<sizePop){
			
			if(popFitness[pop][index]>bestFitness){
				System.arraycopy(population[pop][index], 0, finalPop[taken], 0, sizeCrom);
				finalPopFitness[taken]=popFitness[pop][index];
				index++;
			}
			else{
				if(pop==1){
					System.arraycopy(newPop[bestPosition], 0, finalPop[taken], trainData.length ,newPop[bestPosition].length);
				}
				else{
					System.arraycopy(newPop[bestPosition], 0, finalPop[taken], 0,newPop[bestPosition].length );
				}
				finalPopFitness[taken]=newPopFitness[bestPosition];
				
				newPopFitness[bestPosition]=-1;
				bestFitness=-1;
				bestPosition=-1;
				for(int i=0;i<newPop.length;i++){
					if(newPopFitness[i]>bestFitness){
						bestFitness=newPopFitness[i];
						bestPosition=i;
					}
				}
				used=true;
			}
			taken++;
		}
		
		for(int i=0;i<finalPop.length;i++){
			System.arraycopy(finalPop[i], 0, population[pop][i], 0, sizeCrom);
		}
		
		System.arraycopy(finalPopFitness, 0, popFitness[pop], 0, sizePop);
		
		return used;
	}
	
	/**
	 * Evaluates a population
	 *
	 * @param pop Index of the population
	 *
	 */
	private void evaluatePop(int pop){
		
		for(int i=0;i<sizePop;i++){
			popFitness[pop][i]=oldFitness(pop,i);
		}
	}
	
	/**
	 * Computes the fitness of a chromosome (without updating)
	 *
	 * @param pop Index of the population
	 * @param crom Index of the chromosome
	 *
	 */
	private double oldFitness(int pop, int crom){
		
		int [] output=new int [trainData.length];
		double fitness;
		double reductionI=0.0;
		double reductionF=0.0;
		int SIcount, SCcount,SCIcount,SCCcount;
		
		switch(pop){
		
			case 0: System.arraycopy(population[pop][crom], 0, SIInstances, 0, SIInstances.length);
			
					//SI Classify
					System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
					for(int i=0;i<inputAtt;i++){
						CFeatures[i]=1;
					}
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(output[i],bestSCOutput[i],bestSCIOutput[i]);
					}
					
					//reduction
					SIcount=0;
					
					for(int i=0;i<trainData.length;i++){
						SIcount+=SIInstances[i];
					}
					
					reductionI=1.0-((double)SIcount)/((double)trainData.length);
					
				break;
				
			case 1: System.arraycopy(population[pop][crom], 0, SCFeatures, 0, SCFeatures.length);
			
					//SC Classify
					
					for(int i=0;i<trainData.length;i++){
						CInstances[i]=1;
					}
					System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(bestSIOutput[i],output[i],bestSCIOutput[i]);
					}
					
					//reduction
					SCcount=0;
					
					for(int i=0;i<inputAtt;i++){
						SCcount+=SCFeatures[i];
					}
					
					reductionF=1.0-((double)SCcount)/((double)inputAtt);

				break;
				
			case 2: System.arraycopy(population[pop][crom], 0, SCIInstances, 0, SCIInstances.length);
					System.arraycopy(population[pop][crom], SCIInstances.length, SCIFeatures, 0, SCIFeatures.length);
				
					//SIC Classify
					System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
					System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(bestSIOutput[i],bestSCOutput[i],output[i]);
					}
					
					//reduction
					SCIcount=0;
					SCCcount=0;
					
					for(int i=0;i<trainData.length;i++){
						SCIcount+=SCIInstances[i];
					}
					for(int i=0;i<inputAtt;i++){
						SCCcount+=SCIFeatures[i];
					}
					
					reductionI=1.0-((double)SCIcount)/((double)trainData.length);
					reductionF=1.0-((double)SCCcount)/((double)inputAtt);
					
					break;
		};
		
		fitness=0.0;
		
		for(int i=0;i<trainData.length;i++){
			
			if(output[i]==trainOutput[i]){
				fitness+=1.0;
			}
		}
		
		fitness/=(double)trainData.length;
		
		switch(pop){
		
			case 0: fitness= alpha*fitness +((1.0-alpha)*reductionI);
				break;
	
			case 1: fitness= beta*fitness +((1.0-beta)*reductionF);
				break;
		
			case 2: fitness= alpha*beta*fitness +((1.0-alpha)*reductionI)+((1.0-beta)*reductionF);
				break;
		}
		
		return fitness;

	}

	/**
	 * Computes the fitness of a chromosome 
	 *
	 * @param pop Index of the population
	 * @param crom Index of the chromosome
	 *
	 */
	private double fitness(int pop, int crom){
		
		int [] output=new int [trainData.length];
		double fitness;
		double reductionI=0.0;
		double reductionF=0.0;
		int SIcount, SCcount,SCIcount,SCCcount;
		
		switch(pop){
		
			case 0: System.arraycopy(newPop[crom], 0, SIInstances, 0, SIInstances.length);
			
					//SI Classify
					System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
					for(int i=0;i<inputAtt;i++){
						CFeatures[i]=1;
					}
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						
						if(output[i]==trainOutput[i]){
							newPoints[pop]+=1;
						}
					}
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(output[i],bestSCOutput[i],bestSCIOutput[i]);
					}
					
					//reduction
					SIcount=0;
					
					for(int i=0;i<trainData.length;i++){
						SIcount+=SIInstances[i];
					}
					
					reductionI=1.0-((double)SIcount)/((double)trainData.length);

				break;
				
			case 1: System.arraycopy(newPop[crom], 0, SCFeatures, 0, SCFeatures.length);
			
					//SC Classify
					
					for(int i=0;i<trainData.length;i++){
						CInstances[i]=1;
					}
					System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						
						if(output[i]==trainOutput[i]){
							newPoints[pop]+=1;
						}
					}
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(bestSIOutput[i],output[i],bestSCIOutput[i]);
					}
					
					//reduction
					SCcount=0;
					
					for(int i=0;i<inputAtt;i++){
						SCcount+=SCFeatures[i];
					}
					
					reductionF=1.0-((double)SCcount)/((double)inputAtt);

				break;
				
			case 2: System.arraycopy(newPop[crom], 0, SCIInstances, 0, SCIInstances.length);
					System.arraycopy(newPop[crom], SCIInstances.length, SCIFeatures, 0, SCIFeatures.length);
				
					//SIC Classify
					System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
					System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
					
					output=evaluateTrain();
					
					for(int i=0;i<trainData.length;i++){
						
						if(output[i]==trainOutput[i]){
							newPoints[pop]+=1;
						}
					}
					
					for(int i=0;i<trainData.length;i++){
						output[i]=judgeClass(bestSIOutput[i],bestSCOutput[i],output[i]);
					}
					
					//reduction
					SCIcount=0;
					SCCcount=0;
					
					for(int i=0;i<trainData.length;i++){
						SCIcount+=SCIInstances[i];
					}
					for(int i=0;i<inputAtt;i++){
						SCCcount+=SCIFeatures[i];
					}
					
					reductionI=1.0-((double)SCIcount)/((double)trainData.length);
					reductionF=1.0-((double)SCCcount)/((double)inputAtt);
				
					break;
		};
		
		fitness=0.0;
		
		for(int i=0;i<trainData.length;i++){
			
			if(output[i]==trainOutput[i]){
				fitness+=1.0;
			}
		}
		
		fitness/=(double)trainData.length;
		
		switch(pop){
		
			case 0: fitness= alpha*fitness +((1.0-alpha)*reductionI);
				break;

			case 1: fitness= beta*fitness +((1.0-beta)*reductionF);
				break;
		
			case 2: fitness= alpha*beta*fitness +((1.0-alpha)*reductionI)+((1.0-beta)*reductionF);
				break;
		}
		
		evaluations++;
		
		if(bestFitness<=fitness){
			
			bestFitness=fitness;
			bestFitnessPop=pop;
		}
		
		return fitness;

	}

	/**
	 * HUX crossover operatior 
	 *
	 * @param a Index of the first chromosome
	 * @param b Index of the second chromosome
	 *
	 */	
	private void HUXCross(int a,int b){
		
		int index=0;
		int aux;
		
		for(int i=0;i<newPop[a].length;i++){
		
			if(newPop[a][i]!=newPop[b][i]){
				diff[index]=i;
				index++;
			}
		}
		
		shuffleDiff(index);
		
		index=index/2;
		
		switch(actualPop){
		
			case 0:
				for(int i=0;i<index;i++){
					if(Randomize.Rand() > prob0to1){
						newPop[a][diff[i]]=0;
						newPop[b][diff[i]]=0;
					}
					else{
						aux=newPop[a][diff[i]];
						newPop[a][diff[i]]=newPop[b][diff[i]];
						newPop[b][diff[i]]=aux;
					} 
				}
				break;
				
			case 1:
				
				for(int i=0;i<index;i++){
				
					aux=newPop[a][diff[i]];
					newPop[a][diff[i]]=newPop[b][diff[i]];
					newPop[b][diff[i]]=aux;
				}
				
				break;
				
			case 2:
				
				for(int i=0;i<index;i++){
					if( i<trainData.length && Randomize.Rand() > prob0to1){
						newPop[a][diff[i]]=0;
						newPop[b][diff[i]]=0;
					}
					else{
						aux=newPop[a][diff[i]];
						newPop[a][diff[i]]=newPop[b][diff[i]];
						newPop[b][diff[i]]=aux;
					} 
				}
				break;
		
		}
		
	}

	/**
	 * Shuffles an array of differences 
	 *
	 * @param size Size of the array
	 *
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
	 * Hamming distance between two chromosomes
	 *
	 * @param a Index of the first chromosome
	 * @param b Index of the second chromosome
	 *
	 */		
	private int hamming(int a, int b){
		
		int dist=0;
		
		for(int i=0;i<newPop[a].length;i++){

			if(newPop[a][i]!=newPop[b][i]){
				dist++;
			}
		}
		
		return dist;
		
	}
	
	/**
	 * Initializes a population 
	 *
	 * @param pop Index of the population
	 * @param size Size of the population
	 *
	 */	
	private void createNewPop(int pop, int size){
		
		int index[]=new int [sizePop];
		int pos,tmp,start;
		
	    for (int i=0; i<sizePop; i++){
	    	index[i] = i;
	    }

	    for (int i=0; i<sizePop; i++) {
	    	
	    	pos = Randomize.Randint (0, sizePop);
	    	tmp = index[i];
		    index[i] = index[pos];
		    index[pos] = tmp;
	    }
	    
	    start=0;
	    if(pop==1){
	    	start=trainData.length;
	    }
	
	    for (int i=0; i<sizePop; i++) {
	    	System.arraycopy(population[pop][index[i]], start, newPop[i], 0, size);
	    }
	}
	
	/**
	 * Performs a fully random creation of populations 
	 *
	 */	
	private void randomGenerate(){
		
		for(int i=0;i<sizePop;i++){
			for(int j=0;j<sizeCrom;j++){
				population[0][i][j]=Randomize.RandintClosed(0,1);
				population[1][i][j]=Randomize.RandintClosed(0,1);
				population[2][i][j]=Randomize.RandintClosed(0,1);
			}
		}

	}

	/**
	 * Swaps two chromosomes of a population
	 *
	 * @param pop Index of the population
	 * @param a Index of the first chromosome
	 * @param b Index of the second chromosome
	 *
	 */		
	private void swap(int pop,int a, int b){
		
		int [] aux= new int [sizeCrom];
		double au;
		
		System.arraycopy(population[pop][a], 0, aux, 0, aux.length);
		System.arraycopy(population[pop][b], 0, population[pop][a], 0, aux.length);
		System.arraycopy(aux, 0, population[pop][b], 0, aux.length);
		
		au=popFitness[pop][a];
		popFitness[pop][a]=popFitness[pop][b];
		popFitness[pop][b]=au;
	}
	
	/**
	 * Sorts a population
	 *
	 * @param pop Index of the population
	 *
	 */		
	private void sortPop(int pop){
		
		for(int i=0;i<population[pop].length-1;i++){
			for(int j=0;j<population[pop].length;j++){
				if(popFitness[pop][i]>popFitness[pop][j]){
					swap(pop,i,j);
				}
			}
		}
	}

	/**
	 * Evaluates initial SI population
	 *
	 */		
	private void evaluateInitialSI(){
		
		int [] SIOutput;
		int value;

		for(int j=0;j<inputAtt;j++){
			CFeatures[j]=1;
		}
		
		for(int i=0;i<population[0].length;i++){
		
			System.arraycopy(population[0][i], 0, CInstances, 0, trainData.length);

			SIOutput=evaluateTrain();
			
			value=0;
			
			for(int j=0;j<trainData.length;j++){
				
				if(SIOutput[j]==trainOutput[j]){
					value++;
				}
			}

			popFitness[0][i]=value;
		}
		
	}

	/**
	 * Evaluates initial SC population
	 *
	 */			
	private void evaluateInitialSC(){
		
		int [] SCOutput;
		int value;

		for(int j=0;j<trainData.length;j++){
			CInstances[j]=1;
		}
		
		for(int i=0;i<population[1].length;i++){
		
			System.arraycopy(population[1][i], trainData.length, CFeatures, 0, inputAtt);

			SCOutput=evaluateTrain();
			
			value=0;
			
			for(int j=0;j<trainData.length;j++){
				
				if(SCOutput[j]==trainOutput[j]){
					value++;
				}
			}

			popFitness[1][i]=value;
		}
		
	}
	
	/**
	 * Evaluates initial SCI population
	 *
	 */		
	private void evaluateInitialSCI(){
		
		int [] SCIOutput;
		int value;
		
		for(int i=0;i<population[1].length;i++){
		
			System.arraycopy(population[2][i], trainData.length, CFeatures, 0, inputAtt);
			System.arraycopy(population[2][i], 0, CInstances, 0, trainData.length);
			
			SCIOutput=evaluateTrain();
			
			value=0;
			
			for(int j=0;j<trainData.length;j++){
				
				if(SCIOutput[j]==trainOutput[j]){
					value++;
				}
			}

			popFitness[2][i]=value;
		}
		
	}
	
	/** 
	 * Performs the classification of train dataset
	 * 
	 */	
	public void classifyTrain(){
		
		modelTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		System.out.println(name+" "+ relation + " Model " + modelTime + "s");
		
		//Check  time		
		setInitialTime();
		
		int [] clasResult;
		
		trainRealClass = new int[trainData.length][1];
		trainPrediction = new int[trainData.length][1];			
		    
		clasResult=coEvaluateTrain();
		    
		for (int i=0; i<trainRealClass.length; i++) {
			trainRealClass[i][0]= trainOutput[i];
			trainPrediction[i][0]= clasResult[i];
		}
			
		trainingTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
		//Writing results
		writeOutput(outFile[0], trainRealClass, trainPrediction);
		System.out.println(name+" "+ relation + " Training " + trainingTime + "s");
		
	}//end-method 
	
	/** 
	 * Performs the classification of test dataset
	 * 
	 */	
	public void classifyTest(){
		
		//Check  time		
		setInitialTime();
		
		int [] clasResult;
		
		testRealClass = new int[testData.length][1];
		testPrediction = new int[testData.length][1];			
		    
		clasResult=coEvaluateTest();
		    
		for (int i=0; i<testRealClass.length; i++) {
			testRealClass[i][0]= testOutput[i];
			testPrediction[i][0]= clasResult[i];
		}
			
		testTime=((double)System.currentTimeMillis()-initialTime)/1000.0;
		
		//Writing results
		writeOutput(outFile[1], testRealClass, testPrediction);
		System.out.println(name+" "+ relation + " Test " + testTime + "s");
		
	}//end-method 
	
	/** 
	 * Performs the classification of train dataset
	 * 
	 */		
	private int [] coEvaluateTrain(){

		int [] SIOutput;
		int [] SCOutput;
		int [] SICOutput;
		int [] result;
		
		result=new int [trainData.length];
		
		if(MODE==ALL){
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			SIOutput=evaluateTrain();
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			SCOutput=evaluateTrain();
			
			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			SICOutput=evaluateTrain();
			
			for(int i=0;i<trainData.length;i++){
				result[i]=judgeClass(SIOutput[i],SCOutput[i],SICOutput[i]);
			}
		}
		
		if(MODE==SI){
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			result=evaluateTrain();
			
		}
		
		if(MODE==SC){
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			result=evaluateTrain();

		}
		
		if(MODE==SCI){

			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			result=evaluateTrain();

		}

		return result;

	}

	/** 
	 * Performs the classification of test dataset
	 * 
	 */		
	private int [] coEvaluateTest(){

		int [] SIOutput;
		int [] SCOutput;
		int [] SICOutput;
		int [] result;
		
		result=new int [trainData.length];
		
		if(MODE==ALL){
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			SIOutput=evaluateTest();
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			SCOutput=evaluateTest();
			
			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			SICOutput=evaluateTest();
			
			for(int i=0;i<testData.length;i++){
				result[i]=judgeClass(SIOutput[i],SCOutput[i],SICOutput[i]);
			}
		}
		
		if(MODE==SI){
			//SI Classify
			System.arraycopy(SIInstances, 0, CInstances, 0, trainData.length);
			for(int i=0;i<inputAtt;i++){
				CFeatures[i]=1;
			}
			
			result=evaluateTest();
			
		}
		
		if(MODE==SC){
			
			//SC Classify
			
			for(int i=0;i<trainData.length;i++){
				CInstances[i]=1;
			}
			System.arraycopy(SCFeatures, 0, CFeatures, 0, inputAtt);
			
			result=evaluateTest();

		}
		
		if(MODE==SCI){

			//SIC Classify
			System.arraycopy(SCIInstances, 0, CInstances, 0, trainData.length);
			System.arraycopy(SCIFeatures, 0, CFeatures, 0, inputAtt);
			
			result=evaluateTest();

		}

		return result;
	}

	/**
	 * Selects the combined output
	 *
	 * @param a First output
	 * @param b Second output
	 * @param c Third output
	 *
	 */		
	private int judgeClass(int a, int b, int c){
		
		if(a==b){
			if(a!=-1){
				return a;
			}else{
				return c;
			}
		}
		
		if(b==c){
			if(b!=-1){
				return b;
			}else{
				return a;
			}
		}
		
		if(a==c){
			if(a!=-1){
				return a;
			}else{
				return b;
			}
		}
		
		switch (bestClassifier){
		
			case 0: return a;

			case 1: return b;

			case 2: return c;

		};
		
		return -1;
		
	}

	/** 
	 * Evaluates the test dataset
	 * 
	 */		
	private int [] evaluateTest(){
		
		int [] result=new int [testData.length];
				
		for(int i=0;i<testData.length;i++){
			
			result[i]=knnClassifier(testData[i]);

		}
		
		return result;
	}

	/** 
	 * Evaluates the train dataset
	 * 
	 */		
	private int [] evaluateTrain(){
		
		int [] result=new int [trainData.length];
		int old;
				
		for(int i=0;i<trainData.length;i++){
			old=CInstances[i];
			CInstances[i]=0;
			result[i]=knnClassifier(trainData[i]);
			CInstances[i]=old;
		}
		
		return result;
	}

	/** 
	 * Wrapper k-NN classifier
	 * 
	 * @param example Instance to classify
	 */	
	private int knnClassifier(double [] example){
		
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;
		
		nearestN = new int[K];
		minDist = new double[K];
	
	    for (int i=0; i<K; i++) {
			nearestN[i] = -1;
			minDist[i] = Double.MAX_VALUE;
		}
	    
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
			if(CInstances[i]==1){
				
		    	dist = prunedEuclideanDistance(example,i);

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
		selectedClasses= new int[nClasses];
	
		for (int i=0; i<nClasses; i++) {
			selectedClasses[i] = 0;
		}	
		
		for (int i=0; i<K && nearestN[i]!=-1; i++) {
			selectedClasses[trainOutput[nearestN[i]]]+=1;
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

	}

	/** 
	 * Distance function
	 * 
	 * @param example Instance to classify
	 * @param index Training reference instance
	 */		
	private double prunedEuclideanDistance(double [] example,int index){
		
		double length=0.0;
		double value;

		for (int i=0; i<example.length; i++) {
			
			value = example[i]-trainData[index][i];
			length += (double)CFeatures[i]*value*value;
		}
			
		length = Math.sqrt(length); 
				
		return length;
	}
	
	/**
	 * Prints the additional output file
	 */
	public void printExitValues(){
		
		double redIS;
		double redFS;
		double redIFSi;
		double redIFSf;
		
		int SIcount=0;
		int SCcount=0;
		int SCIcount=0;
		int SCCcount=0;

		String text="";		
		
		computeConfussionMatrixes();
		
		//Accuracy
		text+="Accuracy: "+getAccuracy()+"\n";
		text+="Accuracy (Training): "+getTrainAccuracy()+"\n";
		
		//Kappa
		text+="Kappa: "+getKappa()+"\n";
		text+="Kappa (Training): "+getTrainKappa()+"\n";
		
		//Unclassified
		text+="Unclassified instances: "+testUnclassified+"\n";
		text+="Unclassified instances (Training): "+trainUnclassified+"\n";	
		
		//Reduction
		
		for(int i=0;i<trainData.length;i++){
			SIcount+=SIInstances[i];
		}
		for(int i=0;i<inputAtt;i++){
			SCcount+=SCFeatures[i];
		}
		for(int i=0;i<trainData.length;i++){
			SCIcount+=SCIInstances[i];
		}
		for(int i=0;i<inputAtt;i++){
			SCCcount+=SCIFeatures[i];
		}
		
		redIS=1.0-((double) SIcount / (double) trainData.length);
		redFS=1.0-((double) SCcount / (double) inputAtt);
		
		redIFSi=1.0-((double) SCIcount / (double) trainData.length);
		redIFSf=1.0-((double) SCCcount / (double) inputAtt);		
		
	
		//Reduction IS	
		text+= "Reduction (IS): " +redIS+ "\n";
		
		//Reduction FS
		text+= "Reduction (FS): "+redFS+"\n";
		
		//Reduction IFS (instances)
		text+= "Reduction (IFS,I): "+redIFSi+"\n";
		
		//Reduction IFS (features)
		text+= "Reduction (IFS,F): "+redIFSf+"\n";
		
		//Model time
		text+= "Model time: "+modelTime+" s\n";
		
		//Training time
		text+= "Training time: "+trainingTime+" s\n";
		
		//Test time
		text+= "Test time: "+testTime+" s\n";
		
		//Print final chromosomes
		text+="Final solution:\n";
		text+=printSIbest()+"\n";
		text+=printSCbest()+"\n";
		text+=printSCIIbest()+"\n";
		text+=printSCICbest()+"\n";
		
		//Confusion matrix
		text+="Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=testConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";
		
		text+="Training Confussion Matrix:\n";
		for(int i=0;i<nClasses;i++){
			
			for(int j=0;j<nClasses;j++){
				text+=trainConfMatrix[i][j]+"\t";
			}
			text+="\n";
		}
		text+="\n";

		text+=txtFitness;
		
		text+="*************************\n";
		
text+=txtFitness1;
		
		text+="*************************\n";
		
text+=txtFitness2;
		
		text+="*************************\n";
		
text+=txtFitness3;
		
		text+="*************************\n";
		//Finish additional output file
		Files.writeFile (outFile[2], text);
		
	}//end-method 

	/** 
	 * Prints best SI solution
	 * 
	 */	
	private String printSIbest(){
		
		String text="";
		
		for(int i=0;i<SIInstances.length;i++) {
			if(SIInstances[i]==1){
				text+="1";
			}
			else{
				text+="0";
			}

		}
		
		return text;
	}

	/** 
	 * Prints best SC solution
	 * 
	 */		
	private String printSCbest(){
		
		String text="";
		
		for(int i=0;i<SCFeatures.length;i++) {
			if(SCFeatures[i]==1){
				text+="1";
			}
			else{
				text+="0";
			}

		}
		
		return text;
	}

	/** 
	 * Prints best SCI solution (instances)
	 * 
	 */		
	private String printSCIIbest(){
		
		String text="";
		
		for(int i=0;i<SCIInstances.length;i++) {
			if(SCIInstances[i]==1){
				text+="1";
			}
			else{
				text+="0";
			}

		}
		
		return text;
	}

	/** 
	 * Prints best SI solution (features)
	 * 
	 */		
	private String printSCICbest(){
		
		String text="";
		
		for(int i=0;i<SCIFeatures.length;i++) {
			if(SCIFeatures[i]==1){
				text+="1";
			}
			else{
				text+="0";
			}

		}
		
		return text;
	}
	
	/**
	 * Computes the confusion matrixes
	 * 
	 */
	private void computeConfussionMatrixes(){
		
		testConfMatrix= new int [nClasses][nClasses];
		trainConfMatrix= new int [nClasses][nClasses];
		
		testUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(testConfMatrix[i], 0);
		}
		
		for(int i=0;i<testPrediction.length;i++){
			if(testPrediction[i][0]==-1){
				testUnclassified++;
			}else{
				testConfMatrix[testPrediction[i][0]][testRealClass[i][0]]++;
			}
		}
		
		trainUnclassified=0;
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(trainConfMatrix[i], 0);
		}
		
		for(int i=0;i<trainPrediction.length;i++){
			if(trainPrediction[i][0]==-1){
				trainUnclassified++;
			}else{
				trainConfMatrix[trainPrediction[i][0]][trainRealClass[i][0]]++;
			}
		}
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on test set
	 * 
	 * @return Accuracy on test set
	 */
	private double getAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		acc=((double)count/(double)test.getNumInstances());
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the accuracy obtained on the training set
	 * 
	 * @return Accuracy on test set
	 */
	private double getTrainAccuracy(){
		
		double acc;
		int count=0;
		
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		acc=((double)count/(double)train.getNumInstances());
		
		return acc;
		
	}//end-method 
	
	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private double getKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=testConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)test.getNumInstances());
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=testConfMatrix[i][j];
				count2+=testConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)test.getNumInstances());
			prob2=((double)count2/(double)test.getNumInstances());
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 

	/**
	 * Computes the Kappa obtained on test set
	 * 
	 * @return Kappa on test set
	 */	
	private double getTrainKappa(){
		
		double kappa;
		double agreement,expected;
		int count,count2;
		double prob1,prob2;
		
		count=0;
		for(int i=0;i<nClasses;i++){			
			count+=trainConfMatrix[i][i];
		}
		
		agreement=((double)count/(double)train.getNumInstances());
		
		expected=0.0;
		
		for(int i=0;i<nClasses;i++){			
			
			count=0;
			count2=0;
			
			for(int j=0;j<nClasses;j++){
				count+=trainConfMatrix[i][j];
				count2+=trainConfMatrix[j][i];
			}
			
			prob1=((double)count/(double)train.getNumInstances());
			prob2=((double)count2/(double)train.getNumInstances());
			
			expected+=(prob1*prob2);
		}

		kappa=(agreement-expected)/(1.0-expected);
		
		return kappa;
		
	}//end-method 
	
	/**
	 * Prints output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	private void writeOutput(String filename, int [][] realClass, int [][] prediction) {
	
		String text = "";
		
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
		
		if (output.getType() == Attribute.INTEGER) {
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + realClass[i][j] + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + prediction[i][j] + " ";
			      }
			      text += "\n";			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      }     
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}
		}
		else{
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      for (int j=0; j<realClass[0].length; j++){
			    	  text += "" + (String)output.getNominalValuesList().elementAt(realClass[i][j]) + " ";
			      }
			      for (int j=0; j<realClass[0].length; j++){
			    	  if(prediction[i][j]>-1){
			    		  text += "" + (String)output.getNominalValuesList().elementAt(prediction[i][j]) + " ";
			    	  }
			    	  else{
			    		  text += "" + "Unclassified" + " ";
			    	  }
			      }
			      text += "\n";
			      
			      if((i%10)==9){
			    	  Files.addToFile(filename, text);
			    	  text = "";
			      } 
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}		
		}
		
	}//end-method 

} //end-class 

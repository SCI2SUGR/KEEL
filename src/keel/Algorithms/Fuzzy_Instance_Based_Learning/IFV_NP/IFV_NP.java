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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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



package keel.Algorithms.Fuzzy_Instance_Based_Learning.IFV_NP;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.core.Files;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.FuzzyIBLAlgorithm;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.ReportTool;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Timer;
import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;

/**
 * 
 * File: IFV_NP.java
 * 
 * The IFV_NP algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class IFV_NP extends FuzzyIBLAlgorithm {

	private int K;
	private double meanInstances [][];
	
	private double prototypes [][];
	
	private double membership [];
	private double nonmembership [];
	
	private double correlationMatrix [][];
	private double stDevMatrix[][];
	
	private double threshold;
	
	private final int MAX_ITERATIONS = 100;
	private int iterations;

	/** 
	 * Reads the parameters of the algorithm. 
	 * 
	 * @param script Configuration script
	 * 
	 */
	@Override
	protected void readParameters(String script) {
		
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

	    //Getting the threshold
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    threshold = Double.parseDouble(tokens.nextToken().substring(1));
	    
	} //end-method	

	/**
	 * Main builder. Initializes the methods' structures
	 * 
	 * @param script Configuration script
	 */
	public IFV_NP(String script){
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="IFV_NP";

	    //Initialization of Reporting tool
	    ReportTool.setOutputFile(outFile[2]);
	    
	} //end-method	

	/**
	 * Generates the model of the algorithm
	 */
	public void generateModel (){
		
		//Start of model time
		Timer.resetTime();	
		
		computeMeanInstances();
		computeCorrelations();
		computeStDev();

		iterations=0;
		
		//End of model time
		Timer.setModelTime();
	
		//Showing results
		System.out.println(name+" "+ relation + " Model " + Timer.getModelTime() + "s");
		
	}
	
	private void computeStDev(){
	
		double means[][];
		double quad[][];
		int count[][];
		
		stDevMatrix=new double [nClasses][inputAtt];
		means=new double [nClasses][inputAtt];
		quad=new double [nClasses][inputAtt];
		count=new int [nClasses][inputAtt];
		
		for(int i=0;i<nClasses;i++){		
			Arrays.fill(means[i], 0.0);
			Arrays.fill(quad[i], 0.0);
			Arrays.fill(count[i], 0);
		}
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<inputAtt;j++){
				means[trainOutput[i]][j]+=trainData[i][j];
				quad[trainOutput[i]][j]+=trainData[i][j]*trainData[i][j];
				count[trainOutput[i]][j]++;
			}
		}
		
		for(int i=0;i<nClasses;i++){	
		
			if(nInstances[i]<0){
				Arrays.fill(stDevMatrix[i], 0.0);
			}
			else{
				for(int j=0;j<inputAtt;j++){
					means[i][j]/=(double)count[i][j];
					stDevMatrix[i][j]=(quad[i][j]/(double)count[i][j])-(means[i][j]*means[i][j]);
					stDevMatrix[i][j]=Math.sqrt(stDevMatrix[i][j]);
				}
			}
		}
	}
	
	private void computeCorrelations(){
		
		double mean [] = new double [inputAtt];
		double numerator,denominator,denominator1,denominator2;
		
		Arrays.fill(mean, 0.0);
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<inputAtt;j++){
				mean[j]+=trainData[i][j];
			}
		}
		
		for(int j=0;j<inputAtt;j++){
			mean[j]/=(double)trainData.length;
		}
		
		correlationMatrix= new double [inputAtt][inputAtt];
		
		for(int i=0;i<inputAtt;i++){
			correlationMatrix[i][i]=1.0;
			for(int j=i+1;j<inputAtt;j++){
				numerator=0.0;
				denominator1=0.0;
				denominator2=0.0;
				
				for(int instance=0;instance<trainData.length;instance++){
					numerator+= (trainData[instance][i]-mean[i])*(trainData[instance][j]-mean[j]);
					denominator1+= (trainData[instance][i]-mean[i])*(trainData[instance][i]-mean[i]);
					denominator2+= (trainData[instance][j]-mean[j])*(trainData[instance][j]-mean[j]);
				}
				denominator=Math.sqrt(denominator1*denominator2);
				
				correlationMatrix[i][j]=numerator/denominator;
				correlationMatrix[j][i]=correlationMatrix[i][j];
			}

		}
	}
	
	
	private void computeMeanInstances(){
		
		meanInstances = new double [nClasses][inputAtt];
		
		for(int i=0;i<nClasses;i++){
			Arrays.fill(meanInstances[i],0.0);
		}
		
		for(int i=0;i<trainData.length;i++){
			for(int j=0;j<trainData[0].length;j++){
				meanInstances[trainOutput[i]][j]+=trainData[i][j];
			}
		}
		
		for(int i=0;i<nClasses;i++){
			for(int j=0;j<trainData[0].length;j++){
				if(nInstances[i]>0){
					meanInstances[i][j]/=(double)nInstances[i];
				}
				else{
					//very far
					meanInstances[i][j]=-100000;
				}
			}
		}
	
	}
	
	/**
	 * Classifies the training set (leave-one-out)
	 */
	public void classifyTrain(){
	    
		//Start of training time
		Timer.resetTime();
		
		classifyTrainSet();
		
		//End of training time
		Timer.setTrainingTime();
		
		//Showing results
		System.out.println(name+" "+ relation + " Training " + Timer.getTrainingTime() + "s");
		
	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTest(){
		    
		//Start of training time
		Timer.resetTime();
		
		classifyTestSet();
		
		//End of test time
		Timer.setTestTime();
		
		//Showing results
		System.out.println(name+" "+ relation + " Test " + Timer.getTestTime() + "s");	
		
	} //end-method	
	
	/**
	 * Classifies the training set
	 */
	public void classifyTrainSet(){
				
		for(int i=0;i<trainData.length;i++){
			
			trainPrediction[i]=classifyTrainInstance(i,trainData[i]);
			
		}

	} //end-method	
	
	/**
	 * Classifies the test set
	 */
	public void classifyTestSet(){

		for(int i=0;i<testData.length;i++){
			
			testPrediction[i]=classifyTestInstance(i,testData[i]);
			
		}

	} //end-method	
	
	/** 
	 * Classifies an instance of the training set
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * @return class computed
	 */
	private int classifyTrainInstance(int index, double example[]) {
	
		int result;
		double aux;
		int auxI;
		double distances [];
		int classes[];
		
		int nearest;
		double maxMembership;

		distances = new double [nClasses];
		classes = new int [nClasses];
		
		for(int i=0;i<nClasses;i++){
			
			distances[i]=Util.euclideanDistance(example, meanInstances[i]);
			classes[i]=i;

		}
		
		//sort distances
		for(int i=0;i<nClasses;i++){
			for(int j=i;j<nClasses;j++){
			
				if(distances[i]>distances[j]){
					aux=distances[i];
					distances[i]=distances[j];
					distances[j]=aux;
					
					auxI=classes[i];
					classes[i]=classes[j];
					classes[j]=auxI;
				}
			}
		}
		
		membership=new double [nClasses];
		nonmembership=new double [nClasses];
		
		membership[classes[0]]=Math.exp(-1.0*distances[0]);
		nonmembership[classes[0]]=Math.exp(-1.0*distances[1]);
		
		for(int i=1;i<distances.length;i++){
			
			membership[classes[i]]=Math.exp(-1.0*distances[i]);
			nonmembership[classes[i]]=Math.exp(-1.0*distances[i-1]);
		}
		
		//equalize & identify the nearest class
		
		maxMembership=membership[0];
		nearest=0;
		for(int i=1;i<nClasses;i++){
			nonmembership[i]=Math.min(nonmembership[i],(1.0-membership[i]));	
			
			if(maxMembership<membership[i]){
				
				maxMembership=membership[i];
				nearest=i;
			}
		}
		
		//proper neighbor found
		if(maxMembership> threshold){
			 return nearest;
		}
		
		//moving procedure
		iterations=0;
		while(iterations<MAX_ITERATIONS){
			
			iterations++;

			//generate prototypes
			if(iterations==1){
				prototypes = new double [nClasses][inputAtt];
			
				for(int i=0;i<nClasses;i++){
					System.arraycopy(example, 0, prototypes[i], 0, prototypes[i].length);
				}
				
			}else{
				for(int i=0;i<nClasses;i++){
					if(i!=nearest){

						System.arraycopy(prototypes[nearest], 0, prototypes[i], 0, prototypes[i].length);
					}
				}
			}
			
			double xOld,xNew;
			double alpha,rjk;
			double tangent,lij;
			int nearestAtt;
			double minDistAtt;

			for(int i=0;i<nClasses;i++){
				
				//find nearestAtt
				nearestAtt=0;
				minDistAtt=Math.abs(prototypes[i][0]-meanInstances[i][0]);
				
				for(int j=1;j<inputAtt;j++){
					
					aux=Math.abs(prototypes[i][j]-meanInstances[i][j]);
					
					if(minDistAtt>aux){
						minDistAtt=aux;
						nearestAtt=j;
					}
				}
		
				for(int j=0;j<inputAtt;j++){
					
					xOld=prototypes[i][j];
					
					rjk=correlationMatrix[j][nearestAtt];
					alpha= 1.0/(1.0+Math.exp(-1.0*rjk));
					
					lij=(meanInstances[i][j]-prototypes[i][j])/stDevMatrix[i][j];
					
					tangent= Math.exp(lij)-Math.exp(-1.0*lij);

					tangent/= Math.exp(lij)+Math.exp(-1.0*lij);

					xNew=xOld+(alpha*tangent*xOld);
					
					prototypes[i][j]=xNew;
				}
			}
			
			//evaluate new prototypes
			
			distances = new double [nClasses];
			classes = new int [nClasses];
			
			for(int i=0;i<nClasses;i++){
				
				distances[i]=Util.euclideanDistance(example, prototypes[i]);
				classes[i]=i;

			}
			
			//sort distances
			for(int i=0;i<nClasses;i++){
				for(int j=i;j<nClasses;j++){
				
					if(distances[i]>distances[j]){
						aux=distances[i];
						distances[i]=distances[j];
						distances[j]=aux;
						
						auxI=classes[i];
						classes[i]=classes[j];
						classes[j]=auxI;
					}
				}
			}
			
			membership=new double [nClasses];
			nonmembership=new double [nClasses];
			
			membership[classes[0]]=Math.exp(-1.0*distances[0]);
			nonmembership[classes[0]]=Math.exp(-1.0*distances[1]);
			
			for(int i=1;i<distances.length;i++){
				
				membership[classes[i]]=Math.exp(-1.0*distances[i]);
				nonmembership[classes[i]]=Math.exp(-1.0*distances[i-1]);
			}
			
			maxMembership=membership[0];
			nearest=0;
			nonmembership[0]=Math.min(nonmembership[0],(1.0-membership[0]));	
			for(int i=1;i<nClasses;i++){
				nonmembership[i]=Math.min(nonmembership[i],(1.0-membership[i]));	
				
				if(maxMembership<membership[i]){
					
					maxMembership=membership[i];
					nearest=i;
				}
			}
			
			//proper neighbor found
			if(maxMembership> threshold){

				 return nearest;
			}

		}
				
		return nearest;
		
	} //end-method	
	
	/** 
	 * Classifies an instance of the test set
	 * 
	 * @param index Index of the instance in the test set
	 * @param example Instance evaluated 
	 * @return class computed
	 */
	private int classifyTestInstance(int index, double example[]) {
	

		int result;
		double aux;
		int auxI;
		double distances [];
		int classes[];
		
		int nearest;
		double maxMembership;

		distances = new double [nClasses];
		classes = new int [nClasses];
		
		for(int i=0;i<nClasses;i++){
			
			distances[i]=Util.euclideanDistance(example, meanInstances[i]);
			classes[i]=i;

		}
		
		//sort distances
		for(int i=0;i<nClasses;i++){
			for(int j=i;j<nClasses;j++){
			
				if(distances[i]>distances[j]){
					aux=distances[i];
					distances[i]=distances[j];
					distances[j]=aux;
					
					auxI=classes[i];
					classes[i]=classes[j];
					classes[j]=auxI;
				}
			}
		}
		
		membership=new double [nClasses];
		nonmembership=new double [nClasses];
		
		membership[classes[0]]=Math.exp(-1.0*distances[0]);
		nonmembership[classes[0]]=Math.exp(-1.0*distances[1]);
		
		for(int i=1;i<distances.length;i++){
			
			membership[classes[i]]=Math.exp(-1.0*distances[i]);
			nonmembership[classes[i]]=Math.exp(-1.0*distances[i-1]);
		}
		
		//equalize & identify the nearest class
		
		nonmembership[0]=Math.min(nonmembership[0],(1.0-membership[0]));	
		maxMembership=membership[0];
		nearest=0;
		for(int i=1;i<nClasses;i++){
			nonmembership[i]=Math.min(nonmembership[i],(1.0-membership[i]));	
			
			if(maxMembership<membership[i]){
				
				maxMembership=membership[i];
				nearest=i;
			}
		}
		
		//proper neighbor found
		if(maxMembership> threshold){
			 return nearest;
		}
		
		//moving procedure
		iterations=0;
		while(iterations<MAX_ITERATIONS){
			
			iterations++;
			
			//generate prototypes
			if(iterations==1){
				prototypes = new double [nClasses][inputAtt];
			
				for(int i=0;i<nClasses;i++){
					System.arraycopy(example, 0, prototypes[i], 0, prototypes[i].length);
				}
				
			}else{
				for(int i=0;i<nClasses;i++){
					if(i!=nearest){
						System.arraycopy(prototypes[nearest], 0, prototypes[i], 0, prototypes[i].length);
					}
				}
			}
			
			double xOld,xNew;
			double alpha,rjk;
			double tangent,lij;
			int nearestAtt;
			double minDistAtt;

			for(int i=0;i<nClasses;i++){
				
				//find nearestAtt
				nearestAtt=0;
				minDistAtt=Math.abs(prototypes[i][0]-meanInstances[i][0]);
				
				for(int j=1;j<inputAtt;j++){
					
					aux=Math.abs(prototypes[i][j]-meanInstances[i][j]);
					
					if(minDistAtt>aux){
						minDistAtt=aux;
						nearestAtt=j;
					}
				}
				
		
				for(int j=0;j<inputAtt;j++){
					
					xOld=prototypes[i][j];
					
					rjk=correlationMatrix[j][nearestAtt];
					alpha= 1.0/(1.0+Math.exp(-1.0*rjk));
					
					lij=(meanInstances[i][j]-prototypes[i][j])/stDevMatrix[i][j];
					
					tangent= Math.exp(lij)-Math.exp(-1.0*lij);

					tangent/= Math.exp(lij)+Math.exp(-1.0*lij);

					xNew=xOld+(alpha*tangent*xOld);
					
					prototypes[i][j]=xNew;
				}
			}
			
			//evaluate new prototypes
			
			distances = new double [nClasses];
			classes = new int [nClasses];
			
			for(int i=0;i<nClasses;i++){
				
				distances[i]=Util.euclideanDistance(example, prototypes[i]);
				classes[i]=i;

			}
			
			//sort distances
			for(int i=0;i<nClasses;i++){
				for(int j=i;j<nClasses;j++){
				
					if(distances[i]>distances[j]){
						aux=distances[i];
						distances[i]=distances[j];
						distances[j]=aux;
						
						auxI=classes[i];
						classes[i]=classes[j];
						classes[j]=auxI;
					}
				}
			}
			
			membership=new double [nClasses];
			nonmembership=new double [nClasses];
			
			membership[classes[0]]=Math.exp(-1.0*distances[0]);
			nonmembership[classes[0]]=Math.exp(-1.0*distances[1]);
			
			for(int i=1;i<distances.length;i++){
				
				membership[classes[i]]=Math.exp(-1.0*distances[i]);
				nonmembership[classes[i]]=Math.exp(-1.0*distances[i-1]);
			}
			
			nonmembership[0]=Math.min(nonmembership[0],(1.0-membership[0]));	
			maxMembership=membership[0];
			nearest=0;
			for(int i=1;i<nClasses;i++){
				nonmembership[i]=Math.min(nonmembership[i],(1.0-membership[i]));	
				
				if(maxMembership<membership[i]){
					
					maxMembership=membership[i];
					nearest=i;
				}
			}
			
			//proper neighbor found
			if(maxMembership> threshold){

				 return nearest;
			}

		}
				
		return nearest;
		
	} //end-method	

	
	/**
	 * Reports the results obtained
	 */
	public void printReport(){
		
		writeOutput(outFile[0], trainOutput, trainPrediction);
		writeOutput(outFile[1], testOutput, testPrediction);
		
		ReportTool.setResults(trainOutput,trainPrediction,testOutput,testPrediction,nClasses);
		
		ReportTool.printReport();
		
	} //end-method	
    
} //end-class 
